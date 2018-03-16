package com.example.ajcin.flashbackmusicteam16.Tests;

import android.support.test.InstrumentationRegistry;

import com.example.ajcin.flashbackmusicteam16.Album;
import com.example.ajcin.flashbackmusicteam16.PopulateMusic;
import com.example.ajcin.flashbackmusicteam16.R;
import com.example.ajcin.flashbackmusicteam16.ResourceSong;
import com.example.ajcin.flashbackmusicteam16.Song;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shuo on 2/18/2018.
 */
public class PopulateMusicTest {
    @Test
    public void getSong() throws Exception {
        int res = R.raw.after_the_storm;
        PopulateMusic populateMusic =new PopulateMusic(InstrumentationRegistry.getTargetContext());
        ResourceSong getSong = (ResourceSong) populateMusic.getSong("After The Storm");
        assertEquals(res,getSong.get_id());
    }

    @Test
    public void getSongList() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        Song testSong=populateMusic.getSongList().get(0);
        Song testSong2 = populateMusic.getSongList().get(2);
        assertEquals("After The Storm",testSong.get_title());
        assertEquals("At Midnight",testSong2.get_title());
    }

    @Test
    public void getSongListString() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        String[] songStringTest = populateMusic.getSongListString();
        assertEquals("After The Storm",songStringTest[0]);
        assertEquals("At Midnight",songStringTest[2]);
    }

    @Test
    public void getAlbum() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        Album testAlbum= populateMusic.getAlbum("Origins - The Best of Terry Oldfield");
        assertEquals("Origins - The Best of Terry Oldfield",testAlbum.get_title());

    }

    @Test
    public void getAlbumList() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        String s = populateMusic.getAlbumList().get(0).get_title();
        assertEquals("Origins - The Best of Terry Oldfield",s);
    }

    @Test
    public void getAlbumListString() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        String[] s = populateMusic.getAlbumListString();
        assertEquals("Origins - The Best of Terry Oldfield",s[0]);
    }

    @Test
    public void getSongListInAlbum() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        Song s = populateMusic.getSongListInAlbum(populateMusic.getAlbumList().get(0)).get(0);
        assertEquals("Flight of the Eagle",s.get_title());

    }

    @Test
    public void getSongListInAlbumString() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        String s =populateMusic.getSongListInAlbumString(populateMusic.getAlbumList().get(0))[0];
        assertEquals("Flight of the Eagle",s);
    }

    @Test
    public void populateMusicList() throws Exception {

    }

}