package com.example.elad.breakingbadmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Elad on 16/04/2016.
 */
public class BreakingBadLocations {

    public LatLng mLatLng;
    public String mName;

    BreakingBadLocations(String name, double lon, double lat){
        mLatLng = new LatLng(lat, lon);
        mName = name;
    }
}
