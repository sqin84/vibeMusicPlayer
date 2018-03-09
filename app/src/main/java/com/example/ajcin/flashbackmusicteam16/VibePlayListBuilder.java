package com.example.ajcin.flashbackmusicteam16;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sqin8 on 3/8/2018.
 */

public class VibePlayListBuilder implements PlayListBuilder {
    LinkedList<Song> playlist;
    public void readyScores() {

        // Priority is given to a track based on first (a) whether it was played
        // near the user's present location, second (b) whether it was played in the last week,
        // and third (c) whether it was played by a friend.  When multiple of these factors are present,
        // each is given equal weight in producing the ordering of tracks.
        // Ties are broken according to the (a)-to-(c) ordering of the preceding criteria.

        // so, each play/song will have a score out of 3. a,b,c will each contribute one point.
        // when there is a tied score between two songs, then we break ties according

        // criteria a is already taken care, so all songs queried from firebase will have score of 1 already
    }

    private void isPlayedLastWeek(){};
    private void isPlayedByFriend(){};

    public void addSong(Song s){
        playlist.add(s);
    }
    public LinkedList<Song> build() {
        return null;
    }
}
