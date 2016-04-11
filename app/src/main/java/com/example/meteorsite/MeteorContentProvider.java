package com.example.meteorsite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MeteorContentProvider extends ContentProvider {
    public final static String DBNAME = "MeteorDatabase";
    public final static String METEOR_TABLE = "meteor";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_METEOR_ID = "meteor_id";
    public final static String COLUMN_NAMETYPE = "nametype";
    public final static String COLUMN_RECCLASS = "recclass";
    public final static String COLUMN_MASS = "mass";
    public final static String COLUMN_FALL = "fall";
    public final static String COLUMN_YEAR = "year";
    public final static String COLUMN_RECLAT = "reclat";
    public final static String COLUMN_RECLONG = "reclong";
    public final static String COLUMN_GEO_LOCATION = "geo_location";

    public static final String AUTHORITY = "com.example.meteorsite.provider";
    public static final Uri CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + METEOR_TABLE);

    private DatabaseHelper dbHelper;

    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            METEOR_TABLE +  // Table's name
            "(_ID INTEGER PRIMARY KEY, " + COLUMN_NAME + " TEXT NOT NULL," +
            COLUMN_METEOR_ID + " INTEGER NOT NULL," + COLUMN_NAMETYPE + " TEXT NOT NULL," +
            COLUMN_RECCLASS + " TEXT NOT NULL," + COLUMN_MASS + " REAL NOT NULL," +
            COLUMN_FALL + " TEXT NOT NULL," + COLUMN_YEAR + " TEXT NOT NULL" +
            COLUMN_RECLAT + " REAL NOT NULL," + COLUMN_RECLONG + " REAL NOT NULL," +
            COLUMN_GEO_LOCATION + " TEXT NOT NULL)";


    public MeteorContentProvider() {
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = dbHelper.getWritableDatabase().insert(METEOR_TABLE, null, values);
        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor c = dbHelper.getReadableDatabase().query(METEOR_TABLE, projection, selection, selectionArgs,
                null, null, sortOrder);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected static final class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_MAIN);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + METEOR_TABLE);
            onCreate(db);
        }
    }
}
