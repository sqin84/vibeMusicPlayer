package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;

/**
 * Created by ramanrpd on 17/02/18.
 */

public class LocationInfo {
    private double currentLatitude;
    private double currentLongitude;

    public void setLocation(Location curr)
    {
        currentLatitude=curr.getLatitude();
        currentLongitude=curr.getLongitude();
    }
    public double getCurrentLatitude(){
        return currentLatitude;
    }
    public double getCurrentLongitude(){
        return currentLongitude;
    }
}
