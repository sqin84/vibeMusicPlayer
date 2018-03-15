package com.example.ajcin.flashbackmusicteam16;

import android.location.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/** FlashbackPlayListBuilder class to perform scoring calculations.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 17, 2018
  */
public class FlashbackPlayListBuilder implements PlayListBuilder{
    Location currentLocation;
    LocalDateTime localDateTime;
    List<Song> songs;
    PopulateMusic populateMusic;

    public FlashbackPlayListBuilder(Location currentLocation, LocalDateTime localDateTime, PopulateMusic populateMusic) {
        this.populateMusic=populateMusic;
        this.currentLocation=currentLocation;
        this.localDateTime=localDateTime;
    }

    /** build
      * Gets a list of relevant songs to play in Flashback Mode.
      * @return list of songs to play
     */
    public LinkedList<Song> build() {
        songs = new LinkedList<>(populateMusic.getSongList());
        this.readyScores();
        return (LinkedList)this.songs;
    }

    /** readyScores
      * Calculate the score of each song.
     */
    public void readyScores() {
        for(Song s: songs) {
            int dateTimeScore = Integer.MAX_VALUE;
            int locationScore = Integer.MAX_VALUE;
            for (LocalDateTime p : s.getListOfLocalDateTimes()) {
                int tmp = Math.abs(p.getHour() - localDateTime.getHour());
                if (tmp<dateTimeScore) {
                    dateTimeScore = tmp;
                }
            }
            for (Location p : s.getListOfLocations()) {
                int tmp = (Math.abs(Math.round(p.distanceTo(currentLocation))));
                if (tmp<locationScore) {
                    locationScore = tmp;
                }
            }

            s.set_score((int) (locationScore / 100) + (int) (dateTimeScore * 2));
        }

        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.get_score()-t1.get_score();
            }
        });
    }
}