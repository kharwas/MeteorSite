package com.example.meteorsite;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    public final static String PREFS_NAME = "meteor_preferences";
    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    MapFragment mapFragment;
    private GoogleMap googleMap;
    private MyDatabase db;
    private Cursor meteorites;
    private SharedPreferences sharedpreferences;
    private int spinnerPosition;
    private int searchRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open shared preferences which is currently only used to store the range and the
        // position it is located at in the spinner
        sharedpreferences = getSharedPreferences(PREFS_NAME, 0);

        // get values from shared preferences
        searchRange = sharedpreferences.getInt("searchRange", 300);
        spinnerPosition = sharedpreferences.getInt("spinnerPosition", 7);

        // initialize database
        db = new MyDatabase(this);

        // load map fragment
        mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MeteorMapFragment.FRAGMENT_TAG);
        if(mapFragment == null)
            mapFragment = new MeteorMapFragment();
        mapFragment.getMapAsync(this);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapview, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(true)
                .setMessage("There seems to be a connection issue. Please check your internet connection " +
                        "and try again.")
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    // searches for locations using google's place autocomplete api
    public void search(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setCancelable(true)
                    .setMessage("MeteorSite seems to be experiencing connection issues, please try again later.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } catch (GooglePlayServicesNotAvailableException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setCancelable(true)
                    .setMessage("Meteorsite seems to be experiencing connection issues, please try again later.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    // places the location of meteors onto the map
    public void findMeteors(Location location) {
        Bitmap newIcon;
        Double meteorMass;
        double lat;
        double lng;

        // need to clear the map each time so markers from previous searches are removed
        googleMap.clear();

        // convert range in miles to range in kilos then to degrees
        double range = (double) searchRange;
        double rangeLong = (1 / (111.32 * Math.cos(location.getLatitude())) * (range/2 * .621371));
        double rangeLat = (1 / 111.32) * (range/2 * .621371);

        // create location search box
        double upperLong = 0.0;
        double lowerLong = 0.0;
        double upperLat = 0.0;
        double lowerLat = 0.0;
        if (location.getLongitude() > 0) {
            upperLong = location.getLongitude() + rangeLong;
            lowerLong = location.getLongitude() - rangeLong;
        } else {
            upperLong = location.getLongitude() - rangeLong;
            lowerLong = location.getLongitude() + rangeLong;
        }
        if (location.getLatitude() > 0) {
            upperLat = location.getLatitude() + rangeLat;
            lowerLat = location.getLatitude() - rangeLat;
        } else {
            upperLat = location.getLatitude() - rangeLat;
            lowerLat = location.getLatitude() + rangeLat;
        }

        // query the database
        meteorites = db.getMeteorites(upperLong, lowerLong, upperLat, lowerLat);

        if (meteorites != null && meteorites.moveToFirst()) {
            do {    // iterate through each meteorite found
                for (int i = 0; i < meteorites.getColumnCount(); i++) {

                    // need latitude and longitude to create a location for the google map markers
                    try {
                        lat = Double.parseDouble(meteorites.getString(meteorites.getColumnIndex("reclat")));
                        lng = Double.parseDouble(meteorites.getString(meteorites.getColumnIndex("reclong")));
                    }
                    catch(Exception ex){
                        // if the latitude and longitude can't be retrieved then there's nothing to
                        // put on the map so just skip it
                        continue;
                    }

                    // get meteor mass
                    try {
                        meteorMass = Double.parseDouble(meteorites.getString(meteorites.getColumnIndex("mass")));
                    }
                    catch (Exception ex){
                        meteorMass = 0.0;
                    }

                    // assign marker to be used based on meteor mass
                    if(meteorMass < 50){
                        newIcon = BitmapFactory.decodeResource(getResources(), R.drawable.small_marker);
                    }
                    else if(meteorMass < 1000)
                        newIcon = BitmapFactory.decodeResource(getResources(), R.drawable.normal_marker);
                    else
                        newIcon = BitmapFactory.decodeResource(getResources(), R.drawable.large_marker);

                    String meteorInfo = "Mass: " + meteorMass + "kg";

                    // add marker to the map
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(meteorites.getString(meteorites.getColumnIndex("name")))
                            .snippet(meteorInfo)
                            .icon(BitmapDescriptorFactory.fromBitmap(newIcon)));
                }
            }while (meteorites.moveToNext());
            meteorites.close();
        }
    }

    // called when the auto complete returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                // create bounds which are used to move the camera
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(new LatLng(place.getLatLng().latitude + 5, place.getLatLng().longitude))
                        .include(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude + 5))
                        .include(new LatLng(place.getLatLng().latitude - 5, place.getLatLng().longitude))
                        .include(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude - 5))
                        .build();

                // create a location from the place result
                Location location = new Location("provider");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                findMeteors(location);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("PlaceAutocomplete", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_search:
                search();
                break;
            case R.id.menu_range:
                // create alert dialog prompter user to enter a range and store it in shared preferences
                LayoutInflater inlfator = LayoutInflater.from(this);
                View rangeView = inlfator.inflate(R.layout.dialog_range, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setView(rangeView);
                final Spinner spinner = (Spinner) rangeView.findViewById(R.id.spinner);
                spinner.setSelection(spinnerPosition);

                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                searchRange = Integer.parseInt(spinner.getSelectedItem().toString());
                                                spinnerPosition = spinner.getSelectedItemPosition();
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putInt("searchRange", searchRange);
                                                editor.putInt("spinnerPosition", spinnerPosition);
                                                editor.commit();
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close(); // close database connection
    }
}
