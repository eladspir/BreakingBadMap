package com.example.elad.breakingbadmap;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Elad on 01/04/2016.
 */
public class Map implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final String TAG = Map.class.getSimpleName();
    private static final int REQUEST_LOCATION = 1;

    protected GoogleApiClient mGoogleApiClient;

    public Location mLastLocation;
    LocationRequest mLocationRequest;
    public boolean mRequestingLocationUpdates = true;

    public String mLastUpdateTime;
    private GoogleMap mMap;

    public static final double DEFAULT_LAT = 32.1162781;
    public static final double DEFAULT_LON = 34.8252417;
    public Location mCurrentLocation;

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_update_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time_key";

    private List<Marker> mMarkers = new ArrayList<Marker>();
    Activity mActivity;

    Map(Activity iMainActiviy, GoogleApiClient iGoogleApiClient){
        mGoogleApiClient = iGoogleApiClient;
        mActivity = iMainActiviy;


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    mActivity,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        GetLastLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged 0");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        Log.d(TAG, "onLocationChanged 1");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng start = new LatLng(DEFAULT_LAT, DEFAULT_LON);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));


        getMap().getUiSettings().setZoomControlsEnabled(true);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker m) {
                Log.d(TAG, "onMarkerClick 0");
                m.showInfoWindow();
                Log.d(TAG, "onMarkerClick 1");
                return true;
            }
        });

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker m) {

                // Getting view from the layout file info_window_layout
                View v = mActivity.getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                LatLng latLng = m.getPosition();

                TextView txtInfoContentName = (TextView) v.findViewById(R.id.infocontent_name);

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                txtInfoContentName.setText(m.getTitle());

                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);

                // Setting the longitude
                tvLng.setText("Longitude:"+ latLng.longitude);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
    }

    public Marker addMarker(LatLng point){

        return getMap().addMarker(new MarkerOptions()
                .position(point)
                .title("Dummy Point"));
    }

    public Marker addMarker(MarkerOptions options){

        return getMap().addMarker(options);

    }



    public void zoomToAllLocations(){

        if (mMarkers.size() == 0){
            return;
        }


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers) {
            builder.include(marker.getPosition());
        }


        LatLngBounds bounds = builder.build();
        Log.d(TAG, bounds.toString());

        // begin new code:
        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        int height = mActivity.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.moveCamera(cu);
    }

    public void addMarkerPoints(List<BreakingBadLocations> points){

        Log.d(TAG, "addMarkerPoints 0");
        Log.d(TAG, "Points size: " + points.size());


        mMarkers.clear();
        if (points != null){
            Iterator<BreakingBadLocations> pointsIter = points.iterator();
            while (pointsIter.hasNext()) {
                BreakingBadLocations currLocation =  pointsIter.next();
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(currLocation.mLatLng)
                        .title(currLocation.mName));
                mMarkers.add(m);
            }
        }

        zoomToAllLocations();

        Log.d(TAG, "addMarkerPoints 1");

    }







    private void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Permission not Granted");


            requestPermission();

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            Log.d(TAG, "Permission Granted");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            getMap().setMyLocationEnabled(true);
        }


        if (mLastLocation == null) {
            Log.d(TAG, "Using default location");
            mLastLocation = new Location("dummylocation");
            mLastLocation.setLatitude(DEFAULT_LAT);
            mLastLocation.setLongitude(DEFAULT_LON);

        }


        Log.d(TAG, "mLastLocation.lat: " + mLastLocation.getLatitude());
        Log.d(TAG, "mLastLocation.lon: " + mLastLocation.getLongitude());

    }

    public void updateMap() {

        initCamera(mCurrentLocation);
    }

    public void moveCamera(LatLng point){
        CameraPosition position = CameraPosition.builder()
                .target(point)
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        getMap().animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);



    }

    private void initCamera(Location location) {
        Log.d(TAG, "initCamera 0");

        if (location == null){
            Log.d(TAG, "location is null. Aborting");
            Log.d(TAG, "initCamera exit0");
            return;
        }
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        getMap().animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        getMap().setTrafficEnabled(false);


        getMap().getUiSettings().setZoomControlsEnabled(true);
        Log.d(TAG, "initCamera 1");
    }


    GoogleMap getMap(){
        return mMap;
    }

    // TODO: add as broadcast?
    public void onResume()
    {
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void onPause() {
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    protected void startLocationUpdates() {

        Log.d(TAG, "startLocationUpdates 0");

        createLocationRequest();

        // Check for Existing Permissions
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        Log.d(TAG, "startLocationUpdates 1");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected boolean requestPermission() {

        Log.d(TAG, "Requesting permission 0");

        // EXPLAIN WHY NEEDED
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {



        } else {

        ActivityCompat.requestPermissions(
                mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);

            // TODO: implement handler
        }

        return true;
    }



    protected void createLocationRequest() {

        Log.d(TAG, "createLocationRequest 0 ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d(TAG, "createLocationRequest 1 ");

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
    }

    public void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }

            updateMap();
        }

    }
}
