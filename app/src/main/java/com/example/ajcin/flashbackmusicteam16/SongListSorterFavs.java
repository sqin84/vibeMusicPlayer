package com.example.ajcin.flashbackmusicteam16;

import java.util.Comparator;
import java.util.List;


/**
 * Comparator for sorting a list of songs in favorties order
 */

public class SongListSorterFavs extends SongListSorter {

    SongListSorterFavs(List<Song> s){
        this.s = s;
    }
    @Override
    public Comparator<Song> getComparator() {
        return new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                if(song1.get_is_favorited() && song2.get_is_favorited()){
                    return song1.get_title().compareToIgnoreCase(song2.get_title());
                }
                if(!song1.get_is_favorited() && !song2.get_is_favorited()){
                    return song1.get_title().compareToIgnoreCase(song2.get_title());
                }
                if(song1.get_is_favorited() && !song2.get_is_favorited()){
                    return -1;
                }
                else{
                    return 1;
                }
            }
        };
    }
}
