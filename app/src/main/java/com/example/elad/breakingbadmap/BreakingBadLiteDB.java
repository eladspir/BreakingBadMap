package com.example.elad.breakingbadmap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Elad on 03/04/2016.
 */
public class BreakingBadLiteDB extends SQLiteOpenHelper {

    private static final String TAG = BreakingBadLiteDB.class.getSimpleName();


    //Database Name
    private static final String DATABASE_NAME = "BreakingBad";

    //Creating Tables Strings
    private static final String TABLE_LOCATIONS = "locations";
    private static final String KEY_ID = "location_id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_NAME = "name";


    // Commands
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    private static final String CREATE_LOCATIONS_TABLE = CREATE_TABLE + TABLE_LOCATIONS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_LAT + " double precision,"
            + KEY_LON + " double precision" + ");";

    //Database Version
    private static final int DATABASE_VERSION = 10;

    private SQLiteDatabase mDB;



    public BreakingBadLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = this.getWritableDatabase(); // Initialize db
    }

    public List<BreakingBadLocations> getLocations(){

        List<BreakingBadLocations> points = new ArrayList<BreakingBadLocations>();


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM locations", null);
        if(c.getCount()==0)
        {
            Log.d(TAG, "Error: No Recored found");
            return null;
        }
        LatLng point = null;
        while(c.moveToNext()) {
            Log.d(TAG, c.getString(0));
            Log.d(TAG, c.getString(1));
            Log.d(TAG, c.getString(2));
            Log.d(TAG, c.getString(3));
            int id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
            double lat = c.getDouble(c.getColumnIndexOrThrow(KEY_LAT));
            double lon = c.getDouble(c.getColumnIndexOrThrow(KEY_LON));
            String name = c.getString(c.getColumnIndexOrThrow(KEY_NAME));
            Log.d(TAG, "Lon: " + lon);
            Log.d(TAG, "Lat: " + lat);

            points.add(new BreakingBadLocations(name, lat, lon ));
        }

        return points;
    }

    public LatLng getDummy()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM locations", null);
        if(c.getCount()==0)
        {
            Log.d(TAG, "Error: No Recored found");
            return null;
        }
        LatLng point = null;
        while(c.moveToNext()) {
            Log.d(TAG, c.getString(0));
            Log.d(TAG, c.getString(1));
            Log.d(TAG, c.getString(2));
            Log.d(TAG, c.getString(3));
            int id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
            double lat = c.getDouble(c.getColumnIndexOrThrow(KEY_LAT));
            double lon = c.getDouble(c.getColumnIndexOrThrow(KEY_LON));
            String name = c.getString(c.getColumnIndexOrThrow(KEY_NAME));
            Log.d(TAG, "Lon: " + lon);
            Log.d(TAG, "Lat: " + lat);

            point = new LatLng(lon, lat);
        }

        return point;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating database and table
        createTable(db);
        insert(db, "Walters House", -106.536758, 35.126037);
        insert(db, "Jesses House", -106.665627, 35.087559);

    }

    private void insert(SQLiteDatabase db, String name, double lat, double lon) {
        db.execSQL("INSERT INTO locations "
                + "(" + KEY_NAME + ", " + KEY_LAT + ", " + KEY_LON + ") "
                + " VALUES ("
                + toSQL(name) +  ", "
                + lat + ", "
                + lon + ");");
    }

    private String toSQL(String txt){
        return "'" + txt + "'";
    }

    private void createTable(SQLiteDatabase db) {
        Log.d(TAG, "Creating table 0");
        db.execSQL(CREATE_LOCATIONS_TABLE);
        Log.d(TAG, "Creating table 1");
    }

    private void addRow() {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL(DROP_TABLE + " " + TABLE_LOCATIONS);

        // Create Tables again;
        onCreate(db);
    }
}
