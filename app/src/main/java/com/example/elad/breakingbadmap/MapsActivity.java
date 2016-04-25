package com.example.elad.breakingbadmap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MapsActivity extends AppCompatActivity

    implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = MapsActivity.class.getSimpleName();

    protected BreakingBadLiteDB mDB;
    protected GoogleApiClient mGoogleApiClient;
    private Map mMap;


    RecyclerView recyclerView;
    boolean showFAB = true;

    SQLiteDatabase db2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "on Create 0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setTitle("Breaking Bad Map"); // TODO: create string list in xml

        buildGoogleApiClient();
        mDB = new BreakingBadLiteDB(this);
        mMap = new Map(this, mGoogleApiClient);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        updateValuesFromBundle(savedInstanceState);

        Button btnZoomAll = (Button)findViewById(R.id.btnZoomAll);
        btnZoomAll.setOnClickListener(btnListener);



       /* recyclerView = (RecyclerView) findViewById(R.id.bb_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());*/


        /**
         * Bottom Sheet
         */

        // To handle FAB animation upon entrance and exit
        final Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        final Animation shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.bb_fab);

        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(growAnimation);


        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.bb_coordinator);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bb_bottom_sheet);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB)
                            fab.startAnimation(shrinkAnimation);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showFAB = true;
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(growAnimation);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        showFAB = false;
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        showFAB = false;
                        break;


                }

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
        Log.d(TAG, "on Create 1");
    }

    private View.OnClickListener btnListener = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            mMap.zoomToAllLocations();
        }

    };

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

        List<BreakingBadLocations> points = mDB.getLocations();
        mMap.addMarkerPoints(points);

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
