package com.example.meteorsite;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    Button sub;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    MapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        sub = (Button) findViewById(R.id.subbtn);


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

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }


    public void insertVal(View v){
        ContentValues mNewValues = new ContentValues();

        mNewValues.put(MeteorContentProvider.COLUMN_NAME,"something");
        mNewValues.put(MeteorContentProvider.COLUMN_METEOR_ID, 123);
        mNewValues.put(MeteorContentProvider.COLUMN_NAMETYPE,"Valid");
        mNewValues.put(MeteorContentProvider.COLUMN_RECCLASS, "H4");
        mNewValues.put(MeteorContentProvider.COLUMN_MASS, 12.4);
        mNewValues.put(MeteorContentProvider.COLUMN_FALL, "Fall");
        mNewValues.put(MeteorContentProvider.COLUMN_YEAR,"1998");
        mNewValues.put(MeteorContentProvider.COLUMN_RECLAT, 0.2);
        mNewValues.put(MeteorContentProvider.COLUMN_RECLONG, -1.4);
        mNewValues.put(MeteorContentProvider.COLUMN_GEO_LOCATION, "(0.0,0.0)");

        getContentResolver().insert(MeteorContentProvider.CONTENT_URI, mNewValues);
    }

    public void search(View v){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                googleMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title("NewMarker"));

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(new LatLng(place.getLatLng().latitude + 5, place.getLatLng().longitude))
                        .include(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude + 5))
                        .include(new LatLng(place.getLatLng().latitude - 5, place.getLatLng().longitude))
                        .include(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude - 5))
                        .build();

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                Log.i("test", "Place: " + place.getName());
                Log.i("test", "LatLong: " + place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("test", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
