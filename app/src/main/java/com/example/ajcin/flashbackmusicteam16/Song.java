package com.example.ajcin.flashbackmusicteam16;

/**
  * Song class to store resource id of file, along with various information about the Song.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 7, 2018
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

    public Song(String title,String artist, String album, int id){
        song_title = title;
        song_album = album;
        song_artist = artist;
        song_id = id;
    }

    public void favorite_song() {
        if(get_is_favorited()) {
            is_favorited = false;
        }
        else {
            is_favorited = true;
            is_disliked = false;
        }
    }

    public void dislike_song() {
        if(get_is_disliked()) {
            is_disliked = false;
        }
        else {
            is_disliked = true;
            is_favorited = false;
        }
    }

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
