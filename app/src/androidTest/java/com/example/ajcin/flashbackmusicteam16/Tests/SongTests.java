package com.example.ajcin.flashbackmusicteam16.Tests;

import com.example.ajcin.flashbackmusicteam16.ResourceSong;
import com.example.ajcin.flashbackmusicteam16.Song;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/** SongTests to test non-trivial functionality in Song class.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 18, 2018
 */
public class SongTests {

    @Test
    public void testConstructorAndGetters() {
        Song song = new ResourceSong("Sample Song", "Test Artist", "Best of Song Tests", 5);
        assertEquals("Sample Song", song.get_title());
        assertEquals("Test Artist", song.get_artist());
        assertEquals("Best of Song Tests", song.get_album());
        assertEquals(0, song.get_score());
    }

    @Test
    public void testUpdateScore() {
        Song song = new ResourceSong("Sample Song", "Test Artist", "Best of Song Tests", 5);
        song.set_score(12);
        assertEquals(12, song.get_score());
    }

    @Test
    public void testFavoriteSong() {
        Song song = new ResourceSong("Sample Song", "Test Artist", "Best of Song Tests", 5);
        song.favorite_song();
        assertEquals(true, (boolean) song.get_is_favorited());
        song.favorite_song();
        assertEquals(false, (boolean) song.get_is_favorited());
    }

    @Test
    public void testDislikeSong() {
        Song song = new ResourceSong("Sample Song", "Test Artist", "Best of Song Tests", 5);
        song.dislike_song();
        assertEquals(true, (boolean) song.get_is_disliked());
        song.dislike_song();
        assertEquals(false, (boolean) song.get_is_disliked());
    }

    @Test
    public void testToggleToNeutral() {
        Song song = new ResourceSong("Sample Song", "Test Artist", "Best of Song Tests", 5);
        song.dislike_song();
        assertEquals(true, (boolean) song.get_is_disliked());
        assertEquals(false, (boolean) song.get_is_favorited());

        song.favorite_song();
        assertEquals(false, (boolean) song.get_is_disliked());
        assertEquals(true, (boolean) song.get_is_favorited());
    }
}
