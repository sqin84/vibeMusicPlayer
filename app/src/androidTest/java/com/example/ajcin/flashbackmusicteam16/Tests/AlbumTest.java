package com.example.ajcin.flashbackmusicteam16.Tests;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.example.ajcin.flashbackmusicteam16.Album;
import com.example.ajcin.flashbackmusicteam16.PopulateMusic;
import com.example.ajcin.flashbackmusicteam16.R;
import com.example.ajcin.flashbackmusicteam16.Song;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by shuo on 2/18/2018.
 */
public class AlbumTest {
    @Test
    public void add_song() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getTargetContext());
        Uri mediaPath = Uri.parse("android.resource://com.example.ajcin.flashbackmusicteam16/"+ R.raw.after_the_storm);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(InstrumentationRegistry.getTargetContext(),mediaPath);
        String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        Song song = populateMusic.getSong(name);
        Song song1 = populateMusic.getSong("America Religious");
        assertEquals("After The Storm",song.get_title());
        ArrayList<Song> test = new ArrayList<>();
        test.add(song);
        test.add(song1);
        Album album = new Album("Fantasy","Jay");
        album.add_song(song);
        album.add_song(song1);
        assertEquals(test,album.get_album_songs());
    }

    @Test
    public void get_title() throws Exception {
        Album album = new Album("Fantasy","Jay");
        String title = album.get_title();
        assertEquals("Fantasy",title);
    }

    @Test
    public void get_artist() throws Exception {
        Album album = new Album("Fantasy","Jay");
        String artiist = album.get_artist();
        assertEquals("Jay",artiist);
    }

    @Test
    public void get_last_location() throws Exception {

    }

    @Test
    public void get_last_day() throws Exception {
    }

    @Test
    public void get_last_time() throws Exception {
    }

    @Test
    public void get_album_songs() throws Exception {
        PopulateMusic populateMusic = new PopulateMusic(InstrumentationRegistry.getContext());
        Song song = populateMusic.getSong("after_the_storm");
        Song song1 = populateMusic.getSong("america_religious");
        ArrayList<Song> test = new ArrayList<>();
        test.add(song);
        test.add(song1);
        Album album = new Album("Fantasy","Jay");
        album.add_song(song);
        album.add_song(song1);
        assertEquals(test,album.get_album_songs());
    }

    @Test
    public void update_last_location() throws Exception {
    }

    @Test
    public void update_last_day() throws Exception {
    }

    @Test
    public void update_last_time() throws Exception {
    }

}