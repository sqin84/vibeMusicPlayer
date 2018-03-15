package com.example.ajcin.flashbackmusicteam16;

import java.util.Comparator;
import java.util.List;

/**
 * Created by sqin8 on 3/10/2018.
 */

public class SongListSorterRecent extends SongListSorter {

    SongListSorterRecent(List<Song> s){
        this.s = s;
    }
    @Override
    public Comparator<Song> getComparator() {
        return new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                if (!song1.getListOfLocalDateTimes().isEmpty() && !song2.getListOfLocalDateTimes().isEmpty()){
                    return Math.abs(song1.getListOfLocalDateTimes().get(0).compareTo(TimeMachine.now()))
                            - Math.abs(song2.getListOfLocalDateTimes().get(0).compareTo(TimeMachine.now()));
                }else if(song1.getListOfLocalDateTimes().isEmpty() && !song2.getListOfLocalDateTimes().isEmpty()){
                    return 1;
                }else if(!song1.getListOfLocalDateTimes().isEmpty() && song2.getListOfLocalDateTimes().isEmpty()){
                    return -1;
                }else{
                    return song1.get_title().compareToIgnoreCase(song2.get_title());
                }
            }
        };
    }
}
