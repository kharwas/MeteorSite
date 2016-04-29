package com.example.meteorsite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class MyDatabase extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "meteorite.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getMeteorites(Double ulng, Double llng, Double ulat, Double llat) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id", "name", "mass", "reclat", "reclong"};
        String lowerLat = "reclat>" + String.valueOf(llat) + " AND ";
        String upperLat = "reclat<" + String.valueOf(ulat) + " AND ";
        String lowerLong = "reclong<" + String.valueOf(llng) + " AND ";
        String upperLong = "reclong>" + String.valueOf(ulng);
        String sqlWhere = lowerLat + upperLat + lowerLong + upperLong;

        String sqlTables = "meteorite";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, sqlWhere, null, "mass", null, null, "10");

        c.moveToFirst();
        return c;
    }
}
