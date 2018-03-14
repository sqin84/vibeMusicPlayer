package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ajcin on 3/7/2018.
 */

public class ResourceSong extends Song {
    protected int song_id;

    public ResourceSong(String title, String artist, String album, int id) {
        super(title, artist, album);
        this.song_id = id;
    }

    public int get_id() {   return song_id; }

    @Override
    public void startPlayingSong(Activity activity, MediaPlayer mediaPlayer, Main_Activity mainActivity) {
        int resourceId = get_id();

        AssetFileDescriptor assetFileDescriptor = activity.getResources().openRawResourceFd(resourceId);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
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
