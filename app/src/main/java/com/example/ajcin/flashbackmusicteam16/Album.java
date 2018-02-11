package com.example.ajcin.flashbackmusicteam16;

import java.util.ArrayList;

/**
 * Created by luke on 2/7/2018.
 */

public class Album {

    private final String album_name;
    private final String album_artist;
    private ArrayList<Song> album_songs;

    private String last_location;
    private String last_day;
    private String last_time;

    public Album(String name,String artist, String album){
        album_name = name;
        album_artist = album;
    }
    public String get_name(){
        return album_name;
    }
    public String get_artist(){
        return album_artist;
    }
    public String get_last_location(){
        return last_location;
    }
    public String get_last_day(){
        return last_day;
    }
    public String get_last_time(){
        return last_time;
    }
    public ArrayList<Song> get_album_songs() {return album_songs;}
    public void update_last_location(String location){
        last_location = location;
    }
    public void update_last_day(String day){
        last_day = day;
    }
    public void update_last_time(String time){
        last_time = time;
    }
}
