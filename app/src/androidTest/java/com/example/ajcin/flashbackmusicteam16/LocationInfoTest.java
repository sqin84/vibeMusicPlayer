package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shuo on 2/18/2018.
 */
public class LocationInfoTest {
    private static final double DELTA = 1e-15;
    @Test
    public void setLocation() throws Exception {
        LocationInfo locationInfo = new LocationInfo();
        Location location = new Location("");
        location.setLatitude(12.0);
        location.setLongitude(13.0);
        locationInfo.setLocation(location);
        assertEquals(12.0,locationInfo.getCurrentLatitude(),DELTA);
        assertEquals(13.0,locationInfo.getCurrentLongitude(),DELTA);
    }

    @Test
    public void getCurrentLatitude() throws Exception {
        LocationInfo locationInfo = new LocationInfo();
        Location location = new Location("");
        location.setLatitude(12.0);
        location.setLongitude(13.0);
        locationInfo.setLocation(location);
        assertEquals(12.0,locationInfo.getCurrentLatitude(),DELTA);
    }

    @Test
    public void getCurrentLongitude() throws Exception {
        LocationInfo locationInfo = new LocationInfo();
        Location location = new Location("");
        location.setLatitude(12.0);
        location.setLongitude(13.0);
        locationInfo.setLocation(location);
        assertEquals(13.0,locationInfo.getCurrentLongitude(),DELTA);
    }

    @Test
    public void getLocation() throws Exception {
        LocationInfo locationInfo = new LocationInfo();
        Location location = new Location("");
        location.setLatitude(12.0);
        location.setLongitude(13.0);
        locationInfo.setLocation(location);
        assertEquals(location,locationInfo.getLocation());
    }

    @Test
    public void getDistance() throws Exception {
        LocationInfo locationInfo = new LocationInfo();
        Location location1 = new Location("");
        Location location2 = new Location("");
        location1.setLatitude(0.0);
        location1.setLongitude(1.0);
        location2.setLatitude(0.0);
        location2.setLongitude(2.0);
        int i = locationInfo.getDistance(location1,location2);
        int j =Math.abs(Math.round(location1.distanceTo(location2)));
        assertEquals(j,i);

    }

}