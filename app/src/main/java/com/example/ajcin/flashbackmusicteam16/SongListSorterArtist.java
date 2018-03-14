package com.example.ajcin.flashbackmusicteam16;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting a list of songs by artist names
 */

public class SongListSorterArtist extends SongListSorter {

    SongListSorterArtist(List<Song> s){
        this.s = s;
    }

    @Override
    public Comparator<Song> getComparator() {
        return new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.get_artist().compareToIgnoreCase(song2.get_artist());
            }
        };
    }
}
