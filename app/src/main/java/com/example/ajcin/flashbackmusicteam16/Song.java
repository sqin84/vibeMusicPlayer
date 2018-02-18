package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by luke on 2/7/2018.
 */

public class Song {

    private final String song_title;
    private final String song_artist;
    private final String song_album;
    private final int song_id;

    private String last_location;
    private String last_day;
    private String last_time;
    private boolean is_disliked;
    private boolean is_favorited;

    private int score;
    private List<Location> locations;
    private List<LocalDateTime> dateTimes;
    public LinkedList<Location> getListOfLocations()
    {
        return (LinkedList)this.locations;
    }
    public LinkedList<LocalDateTime> getListOfLocalDateTimes()
    {
        return (LinkedList)this.dateTimes;
    }
    public Song(String title,String artist, String album, int id){
        song_title = title;
        song_album = album;
        song_artist = artist;
        song_id = id;
        score = 0;
        locations=new LinkedList<Location>();
        dateTimes=new LinkedList<LocalDateTime>();
    }

    public int getScore()
    {
        return this.score;
    }
    public void setScore(int score)
    {
        this.score=score;
    }

    public void favorite_song() {
        is_favorited = (is_favorited) ? false : true;
        if(is_favorited)    is_disliked = false;
    }

    public void dislike_song() {
        is_disliked = (get_is_disliked()) ? false : true;
        if(is_disliked) is_favorited = false;
    }
    public void addLocation(Location e){
        locations.add(e);
    }
    public void addDateTime(LocalDateTime e)
    {dateTimes.add(e);}

    public String get_title() {  return song_title;}
    public String get_album() {  return song_album;}
    public String get_artist() {    return song_artist;}
    public int get_id() {   return song_id;}
    public String get_last_location() { return last_location;}
    public String get_last_day() {   return last_day;}
    public String get_last_time() { return last_time;}
    public Boolean get_is_disliked() {  return is_disliked;}
    public Boolean get_is_favorited() { return is_favorited;}
    public void update_last_location(String location)   {last_location = location;}
    public void update_last_day(String day) {last_day = day;}
    public void update_last_time(String time)   {last_time = time;}

}
