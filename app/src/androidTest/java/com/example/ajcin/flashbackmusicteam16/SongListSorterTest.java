package com.example.ajcin.flashbackmusicteam16;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Created by sqin8 on 3/10/2018.
 */

public class SongListSorterTest {
    ArrayList<Song> songs;
    Song song1;
    Song song2;
    Song song3;
    Song song4;

    @Before
    public void setUp(){
        songs = new ArrayList<>();
        song1 = new ResourceSong("Apple","Apple", "Apple",0 );
        song2 = new ResourceSong("Banana", "Banana", "Banana", 0);
        song4 = new ResourceSong("Orange", "Orange", "Orange",0);
        song3 = new ResourceSong("Fruit", "Fruit", "Fruit",0);

        song2.favorite_song();
        song3.favorite_song();

        TimeMachine.useFixedClockAt(LocalDateTime.of(2017, 2, 8, 12, 00));
        song2.addDateTime(LocalDateTime.of(2017, 2, 7, 12, 00));
        song3.addDateTime(LocalDateTime.of(2017, 2, 8, 12, 00));

        songs.add(song1);
        songs.add(song2);
        songs.add(song3);
        songs.add(song4);

    }

    @Test
    public void testTitle(){
        SongListSorter sorter = new SongListSorterTitle(songs);
        sorter.sort();

        assertTrue(songs.get(0) == song1);
        assertTrue(songs.get(1) == song2);
        assertTrue(songs.get(2) == song3);
        assertTrue(songs.get(3) == song4);

    }

    @Test
    public void testAlbum(){
        SongListSorter sorter = new SongListSorterAlbum(songs);
        sorter.sort();

        assertTrue(songs.get(0) == song1);
        assertTrue(songs.get(1) == song2);
        assertTrue(songs.get(2) == song3);
        assertTrue(songs.get(3) == song4);
    }

    @Test
    public void testArtist(){
        SongListSorter sorter = new SongListSorterArtist(songs);
        sorter.sort();

        assertTrue(songs.get(0) == song1);
        assertTrue(songs.get(1) == song2);
        assertTrue(songs.get(2) == song3);
        assertTrue(songs.get(3) == song4);
    }

    @Test
    public void testFavs(){
        SongListSorter sorter = new SongListSorterFavs(songs);
        sorter.sort();
        assertTrue(songs.get(0) == song2);
        assertTrue(songs.get(1) == song3);
        assertTrue(songs.get(2) == song1);
        assertTrue(songs.get(3) == song4);
    }

    @Test
    public void testRecent(){
        SongListSorter sorter = new SongListSorterRecent(songs);
        sorter.sort();

        assertTrue(songs.get(0) == song2);
        assertTrue(songs.get(1) == song3);
        assertTrue(songs.get(2) == song1);
        assertTrue(songs.get(3) == song4);
    }
}
