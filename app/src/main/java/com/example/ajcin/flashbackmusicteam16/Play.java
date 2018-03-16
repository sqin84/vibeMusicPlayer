package com.example.ajcin.flashbackmusicteam16;

import com.google.firebase.database.Exclude;

/**
 * Created by sqin8 on 3/4/2018.
 */

public class Play {
    /*Types Play can have
        String
            Long
    Double
            Boolean
    Map<String, Object>
    List<Object>
    */

    String songName;
    double latitude;
    double longitude;
    String address;
    String time;
    String user;
    String url;

    @Exclude
    public int[] score = new int[3];

    public Play setAddress(String a){address = a; return this;}
    public String getAddress(){return address;}
    public Play setSongName(String s){songName = s; return this;}
    public String getSongName(){ return songName;}
    public Play setLatitude(double l){latitude = l; return this;}
    public double getLatitude(){ return latitude;}
    public Play setLongitude(double l){longitude = l; return this;}
    public double getLongitude(){return longitude;}
    public Play setTime(String t){time = t; return this;}
    public String getTime(){return time;}
    public Play setUser(String u){user = u; return this;}
    public String getUser(){return user;}
    public Play setUrl(String u){url = u; return this;}
    public String getUrl(){return url;}

    public Play(){}
}
