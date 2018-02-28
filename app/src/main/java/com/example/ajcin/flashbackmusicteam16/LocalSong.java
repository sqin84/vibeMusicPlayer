package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.MediaPlayer;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/** Song class to store resource id of file, along with various information about the Song.
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
    private String last_played_address;

    private boolean is_disliked;
    private boolean is_favorited;
    private int score;
    private List<Location> locations;
    private List<LocalDateTime> dateTimes;

    /** Song constructor
      * @param title title of the song
      * @param artist name of song's artist
      * @param album name of album the song belongs to
      * @param id resource id of the song
     */
    public Song(String title,String artist, String album, int id){
        song_title = title;
        song_album = album;
        song_artist = artist;
        song_id = id;
        score = Integer.MAX_VALUE;
        last_played_address = "";
        locations=new LinkedList<Location>();
        dateTimes=new LinkedList<LocalDateTime>();
    }

    public String get_last_played_address() {
        if(!locations.isEmpty()) {
            return ("Latitude: "+locations.get(0).getLatitude()+"\nLongitude: "+locations.get(0).getLongitude());
        }
        else {
            return "";
        }
    }

    public void set_last_played_address(String l){
        last_played_address = l;
    }
    public int get_score()
    {
        return this.score;
    }

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
    public void set_score(int score) {
        this.score=score;
    }

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
       // if(!locations.isEmpty()&&(e.distanceTo(locations.get(0)) < 100))
         //   return;
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

    public String get_title() {  return song_title;}
    public String get_album() {  return song_album;}
    public String get_artist() {    return song_artist;}
    public int get_id() {   return song_id;}
    public String get_last_day() {   return last_day;}
    public String get_last_time() {
        if(!dateTimes.isEmpty()){
            String str="Last Played: "+Integer.valueOf(dateTimes.get(0).getHour()).toString() + ":";
            if(dateTimes.get(0).getMinute()>10)
            {
                str=str+Integer.valueOf(dateTimes.get(0).getMinute()).toString();
            }
            else
            {
                str=str+"0"+Integer.valueOf(dateTimes.get(0).getMinute()).toString();
            }
            str=str+"\n Day of the week: "+dateTimes.get(0).getDayOfWeek();
            return str;
        }
        return "";
    }
    public Boolean get_is_disliked() {  return is_disliked;}
    public Boolean get_is_favorited() { return is_favorited;}
    public void update_last_day(String day) {last_day = day;}
    public void update_last_time(String time)   {last_time = time;}

    void startPlayingSong(Activity activity, MediaPlayer mediaPlayer, AlbumView albumView) {
        int resourceId = get_id();

        AssetFileDescriptor assetFileDescriptor = activity.getResources().openRawResourceFd(resourceId);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            albumView.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            albumView.mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.d("MediaPlayer", "did not prepare correctly");
            System.out.println(e.toString());
        }
    }
}
