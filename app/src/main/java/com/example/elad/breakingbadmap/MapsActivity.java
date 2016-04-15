package com.example.elad.breakingbadmap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;



public class MapsActivity extends AppCompatActivity

    implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = MapsActivity.class.getSimpleName();

    protected BreakingBadLiteDB mDB;
    protected GoogleApiClient mGoogleApiClient;
    private Map mMap;

    SQLiteDatabase db2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "on Create 0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        buildGoogleApiClient();
        mDB = new BreakingBadLiteDB(this);
        mMap = new Map(this, mGoogleApiClient);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        updateValuesFromBundle(savedInstanceState);


        Log.d(TAG, "on Create 1");
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap.onMapReady(googleMap);


        LatLng point = mDB.getDummy();
        if (point != null) {
            mMap.addMarker(point);
            mMap.moveCamera(point);
        }


    }


    public void updateUI() {
        mMap.updateMap();
    }



    @Override
    public void onConnected(Bundle bundle) {
        mMap.onConnected(bundle);
    }

    @Override
    public void onConnectionSuspended(int cause) {

        mMap.onConnectionSuspended(cause);
    }



    @Override
    // Location listener
    public void onLocationChanged(Location location) {
        mMap.onLocationChanged(location);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        mMap.onSaveInstanceState(savedInstanceState);
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        mMap.updateValuesFromBundle(savedInstanceState);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
