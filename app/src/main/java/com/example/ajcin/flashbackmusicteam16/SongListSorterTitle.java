package com.example.ajcin.flashbackmusicteam16;

import java.util.Comparator;
import java.util.List;

/**
 * Compartor for sorting a song list according to song titles
 */

public class SongListSorterTitle extends SongListSorter{

    SongListSorterTitle(List<Song> s){
        this.s = s;
    }
    @Override
    public Comparator<Song> getComparator() {
        return new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.get_title().compareToIgnoreCase(song2.get_title());
            }
        };
    }
}
