package com.example.ajcin.flashbackmusicteam16;

import java.util.LinkedList;

/**
 * Created by sqin8 on 3/8/2018.
 */

public interface PlayListBuilder {
    void readyScores();
    LinkedList<Song> build();
}
