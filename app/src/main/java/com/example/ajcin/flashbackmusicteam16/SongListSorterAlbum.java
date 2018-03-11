package com.example.ajcin.flashbackmusicteam16;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting list according to album names
 */

public class SongListSorterAlbum extends SongListSorter {

    SongListSorterAlbum(List<Song> s){
        this.s = s;
    }
    @Override
    public Comparator<Song> getComparator() {
        return new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.get_album().compareToIgnoreCase(song2.get_album());
            }
        };
    }
}
