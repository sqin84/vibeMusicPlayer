package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

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
        String song_url = get_url();

        try {
            mediaPlayer.setDataSource(song_url);
            mainActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mainActivity.mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.d("MediaPlayer", "did not prepare correctly");
            System.out.println(e.toString());
        }
    }
}
