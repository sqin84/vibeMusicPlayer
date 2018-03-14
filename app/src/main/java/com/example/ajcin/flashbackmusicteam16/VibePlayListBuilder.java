package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sqin8 on 3/8/2018.
 */

public class VibePlayListBuilder implements PlayListBuilder {
    LinkedList<Play> plays;
    Location currentLocation;
    LocalDateTime localDateTime;
    List<Song> songs;
    PopulateMusic populateMusic;
    public void readyScores() {

        // Priority is given to a track based on first (a) whether it was played
        // near the user's present location, second (b) whether it was played in the last week,
        // and third (c) whether it was played by a friend.  When multiple of these factors are present,
        // each is given equal weight in producing the ordering of tracks.
        // Ties are broken according to the (a)-to-(c) ordering of the preceding criteria.

        // so, each play/song will have a score out of 3. a,b,c will each contribute one point.
        // when there is a tied score between two songs, then we break ties accordingly. i.e ab outweighs bc.
        // if a track was played in the last week and played by a friend (3), then we choose the most recently played.



        // criteria a is already taken care, so all songs queried from firebase will have score of 1 already
        // updateLocationBasedScore();
        sortByScores();
        updateTimeBasedScore();
        sortByScores();
        updateFriendPlayBasedScore();
        sortByScores();


    }
    private void sortByScores()
    {
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.get_score()-t1.get_score();
            }
        });
    }

    private void updateLocationBasedScore()
    {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation=location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        for(int i=0;i<songs.size();i++){
            LinkedList<Location> locations=songs.get(i).getListOfLocations();
            for(int j=0;j<locations.size();j++){
                double startLat=locations.get(j).getLatitude();
                if(Math.abs(locations.get(i).distanceTo(currentLocation))<=50)
                {
                    songs.get(i).set_score(songs.get(i).get_score()+1);
                    break;
                }
            }
        }
    }
    private void updateTimeBasedScore(){
        LocalDate currentDate = LocalDate.now();

        for(int i=0;i<songs.size();i++){
            LinkedList<LocalDateTime> localDateTimes=songs.get(i).getListOfLocalDateTimes();
            for(int j=0;j<localDateTimes.size();j++){
                if(Math.abs(localDateTimes.get(j).toLocalDate().compareTo(currentDate))<=7)
                {
                    songs.get(i).set_score(songs.get(i).get_score()+1);
                    break;
                }
            }
        }

    }

    private void updateFriendPlayBasedScore(){


    }


    private void isPlayedLastWeek(){};
    private void isPlayedByFriend(){};

    public void addSong(Play s){
        plays.add(s);
    }
    public LinkedList<Song> build() {
        // we will need to remove the duplicates to produce song list with unique elements
        songs = new LinkedList<>(populateMusic.getSongList());
        this.readyScores();
        this.removeDuplicates();
        this.removeDislikedSongs();
        return (LinkedList)this.songs;
    }
    private void removeDislikedSongs(){
        for(int i=0;i<songs.size();i++)
        {
            if(songs.get(i).get_is_disliked())
            {
                songs.remove(i);
            }
        }
    }
    private void removeDuplicates()
    {
        for(int i=0;i<songs.size();i++)
        {
            for(int j=i+1;j<songs.size();j++)
            {
                if(songs.get(i).equals(songs.get(j)))
                {
                    songs.remove(j);
                }
            }
        }
    }
}
