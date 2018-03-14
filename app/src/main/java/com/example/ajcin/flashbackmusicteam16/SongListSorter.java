package com.example.ajcin.flashbackmusicteam16;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sqin8 on 3/10/2018.
 */

public abstract class SongListSorter {
    List<Song> s;
    public void sort(){
        Collections.sort(s, getComparator());
    }

    public abstract Comparator<Song> getComparator();
}
