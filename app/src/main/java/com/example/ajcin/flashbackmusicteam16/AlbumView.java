package com.example.ajcin.flashbackmusicteam16;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class AlbumView extends AppCompatActivity {


    static PopulateMusic populateMusic;
    static MediaPlayer mediaPlayer;
    private static final int MEDIA_RES_ID = R.raw.after_the_storm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_albums:
                    String[] album_list_string = populateMusic.getAlbumListString();
                    Bundle album_bundle = new Bundle();
                    album_bundle.putStringArray("albums",album_list_string);
                    AlbumFragment album_fragment = new AlbumFragment();
                    album_fragment.setArguments(album_bundle);
                    transaction.replace(R.id.musicItems,album_fragment).commit();
                    return true;
                case R.id.navigation_songs:
                    String[] song_list_string = populateMusic.getSongListString();
                    Bundle song_bundle = new Bundle();
                    song_bundle.putStringArray("songs",song_list_string);
                    songListFragment song_fragment = new songListFragment();
                    song_fragment.setArguments(song_bundle);
                    transaction.replace(R.id.musicItems,song_fragment).commit();
                    return true;
                case R.id.navigation_nowPlaying:
                    transaction.replace(R.id.musicItems,new NowPlayingFragment()).commit();

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        populateMusic = new PopulateMusic(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void createMediaPlayer(){mediaPlayer = new MediaPlayer();}

    public void loadAlbumMedia(Album selected_album){

    }

    public void loadMedia(Song selected_song){

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        int resourceId = selected_song.get_id();

        AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(resourceId);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }

    public PopulateMusic getPopulateMusic(){return populateMusic;}
};
