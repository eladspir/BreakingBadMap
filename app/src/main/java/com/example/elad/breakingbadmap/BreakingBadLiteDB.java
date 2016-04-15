package com.example.elad.breakingbadmap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Elad on 03/04/2016.
 */
public class BreakingBadLiteDB extends SQLiteOpenHelper {

    private static final String TAG = BreakingBadLiteDB.class.getSimpleName();


    //Database Name
    private static final String DATABASE_NAME = "BreakingBad";

    //Database Version
    private static final int DATABASE_VERSION = 2;

    public BreakingBadLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public LatLng getDummy()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM locations", null);
        if(c.getCount()==0)
        {
            Log.d(TAG, "Error: No Recored found");
            return null;
        }
        LatLng point = null;
        while(c.moveToNext()) {
            Log.d(TAG, c.getString(0));
            int id = c.getInt(c.getColumnIndexOrThrow("ID"));
            double lon = c.getDouble(2);
            Log.d(TAG, "Lon: " + lon);
            double lat = c.getDouble(3);
            Log.d(TAG, "Lat: " + lat);

            point = new LatLng(lon, lat);
        }

        return point;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating database and table
       // db=openOrCreateDatabase("BreakingBadDB", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS locations;");
        db.execSQL("CREATE TABLE IF NOT EXISTS locations(name VARCHAR, ID int, Lat double precision, Lon double precision);");
        db.execSQL("INSERT INTO locations VALUES('Elad', 1, 35.126037, -106.536758);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
