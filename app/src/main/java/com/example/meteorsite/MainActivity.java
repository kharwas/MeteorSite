package com.example.meteorsite;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    Button sub;

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
}
