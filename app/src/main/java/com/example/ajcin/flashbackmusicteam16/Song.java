package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.location.Location;
import android.media.MediaPlayer;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ajcin on 2/23/2018.
 */

abstract public class Song {
    protected String song_title;
    protected String song_artist;
    protected String song_album;
    protected String last_played_address;
    protected int score;
    protected List<Location> locations;
    protected List<LocalDateTime> dateTimes;
    private String last_location;
    private String last_day;
    private String last_time;
    private boolean is_disliked;
    private boolean is_favorited;
    private String user;

    public Song(String title, String artist, String album) {
        locations=new LinkedList<Location>();
        score = Integer.MAX_VALUE;
        song_title = title;
        last_played_address = "";
        dateTimes=new LinkedList<LocalDateTime>();
        song_artist = artist;
        song_album = album;
    }

    public String get_last_played_address() {
        return last_played_address;
    }
    public void set_last_played_address(String l){
        last_played_address = l;
    }


    public int get_score(){ return this.score; }

    public String get_title() {  return song_title;}

    public String get_album() {  return song_album;}

    public String get_artist() {    return song_artist;}

    public String get_last_day() {
        if(!dateTimes.isEmpty())return dateTimes.get(0).getMonth() +" " + dateTimes.get(0).getDayOfMonth();
        else return "";
    }
    public String get_last_time(){
        if(!dateTimes.isEmpty()) {
            String str = Integer.valueOf(dateTimes.get(0).getHour()).toString() + ":";
            if (dateTimes.get(0).getMinute() > 10) {
                str = str + Integer.valueOf(dateTimes.get(0).getMinute()).toString();
            } else {
                str = str + "0" + Integer.valueOf(dateTimes.get(0).getMinute()).toString();
            }
            return str;
        }else{
            return "";
        }
    }

    public String get_last_time_string() {
       return "Last Played: " + get_last_day()+"\n" +get_last_time();
    }

    public Boolean get_is_disliked() {  return is_disliked;}

    public Boolean get_is_favorited() { return is_favorited;}

    public LinkedList<Location> getListOfLocations() {
        return (LinkedList)this.locations;
    }

    public LinkedList<LocalDateTime> getListOfLocalDateTimes() {
        return (LinkedList)this.dateTimes;
    }



    /** set_score
      * Update song's score with specified value.
      * @param score new score to assign to the song
     */
    public void set_score(int score) {  this.score=score; }

    public void set_user_name(String user){ this.user=user;}
    public String get_user_name(){return user;}

    public void set_last_day(String day) {  last_day = day; }

    public void set_last_time(String time){ last_time = time; }

    /** favorite_song
      * Toggle whether the song is favorited or not.
     */
    public void favorite_song() {
        if(get_is_favorited()) {
            is_favorited = false;
        }
        else {
            is_favorited = true;
            is_disliked = false;
        }
    }

    /** dislike_song
      * Toggle whether the song is disliked or not.
     */
    public void dislike_song() {
        if(get_is_disliked()) {
            is_disliked = false;
        }
        else {
            is_disliked = true;
            is_favorited = false;
        }
    }

    /** addLocation
      * Add location to the list of locations where the song was played.
      * @param e location to add
     */
    public void addLocation(Location e){
        if(locations.size() < 10) {
            ((LinkedList) locations).addFirst(e);
        }else{
            ((LinkedList) locations).addFirst(e);
            ((LinkedList) locations).removeLast();
        }
    }

    /** addDateTime
      * Add dateTime to the list of dateTimes where the song was played.
      * @param e dateTime to add
     */
    public void addDateTime(LocalDateTime e)
    {
        if( dateTimes.size() < 10) {
        ((LinkedList) dateTimes).addFirst(e);
    }else{
        ((LinkedList) dateTimes).addFirst(e);
        ((LinkedList) dateTimes).removeLast();
    }}

    public String toString(){
        return get_title() + " by " + get_artist();
    }

    abstract void startPlayingSong(Activity activity, MediaPlayer mediaPlayer, Main_Activity mainActivity);
}
