package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;

/**
 * Created by ramanrpd on 17/02/18.
 */

public class LocationInfo {
    private double currentLatitude;
    private double currentLongitude;
    private Location location;

    public void setLocation(Location curr)
    {
        currentLatitude=curr.getLatitude();
        currentLongitude=curr.getLongitude();
        location = curr;
    }
    public double getCurrentLatitude(){
        return currentLatitude;
    }
    public double getCurrentLongitude(){
        return currentLongitude;
    }
    public Location getLocation(){return location;}
    public static int getDistance(Location location1,Location location2){
        return Math.abs(Math.round(location1.distanceTo(location2)));
    }

}
