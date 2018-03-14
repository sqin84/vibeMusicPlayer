package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Created by ajcin on 3/2/2018.
 */
public class UrlSong extends Song {

    private final String url;

    public UrlSong(String title, String artist, String album, int id, String url) {
        super(title, artist, album);
        this.url = url;
    }

    public String get_url() {
        return url;
    }

    @Override
    public void startPlayingSong(Activity activity, MediaPlayer mediaPlayer, Main_Activity mainActivity) {

    }
}
