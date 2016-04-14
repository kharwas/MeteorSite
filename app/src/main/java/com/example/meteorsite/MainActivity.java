package com.example.meteorsite;

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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button sub;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sub = (Button) findViewById(R.id.subbtn);
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
                Log.i("test", "Place: " + place.getName());
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
