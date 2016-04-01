package com.example.elad.breakingbadmap;

import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;



public class MapsActivity extends AppCompatActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION = 2;

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_update_key";
    private static final String LOCATION_KEY = "location_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "on Create 0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.d(TAG, "on Create 1");
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


}
