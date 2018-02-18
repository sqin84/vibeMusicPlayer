package com.example.ajcin.flashbackmusicteam16;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

public class AlbumView extends AppCompatActivity {

    public PopulateMusic populateMusic;
    public MediaPlayer mediaPlayer;
    private static final int MEDIA_RES_ID = R.raw.after_the_storm;
    public BottomNavigationView navigation;
    public ProgressBar progressBar;

    private LocationInfo currentLocationInfo;
    private int currentResource;
    private LocalTime intervalStart, intervalEnd;
    private TimeMachine timeMachine;

    ArrayList<Song> queuedSongs;

    public static boolean isFlashbackMode = false;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_albums:
                    if (!isFlashbackMode) {
                        String[] album_list_string = populateMusic.getAlbumListString();
                        Bundle album_bundle = new Bundle();
                        album_bundle.putStringArray("albums", album_list_string);

                        // launch new fragment
                        AlbumFragment album_fragment = new AlbumFragment();
                        album_fragment.setArguments(album_bundle);
                        transaction.replace(R.id.musicItems, album_fragment).commit();
                        return true;
                    }
                    Toast.makeText(getApplicationContext(), "Not available in Flashback Mode", Toast.LENGTH_SHORT).show();
                    return false;
                case R.id.navigation_songs:
                    if (!isFlashbackMode) {
                        String[] song_list_string = populateMusic.getSongListString();
                        Bundle song_bundle = new Bundle();
                        song_bundle.putStringArray("songs", song_list_string);

                        // launch new fragment
                        songListFragment song_fragment = new songListFragment();
                        song_fragment.setArguments(song_bundle);
                        transaction.replace(R.id.musicItems, song_fragment).commit();
                        return true;
                    }
                    Toast.makeText(getApplicationContext(), "Not available in Flashback Mode", Toast.LENGTH_SHORT).show();
                    return false;

                case R.id.navigation_nowPlaying:
                    if (!isFlashbackMode){
                        transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
                    return true;
                    }
                    Toast.makeText(getApplicationContext(), "Not available in Flashback Mode", Toast.LENGTH_SHORT).show();
                    return false;
                case R.id.navigation_flashbackMode:
                    if(!isFlashbackMode) {
                        setUpFlashBackMode();
                        transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
                        Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
                        return true;
                    }else{
                        Toast.makeText(getApplicationContext(), "Standard mode", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("flash_back_mode", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("isFlashBackMode", "false");
                        editor.apply();
                        transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
                        navigation.getMenu().getItem(2).setChecked(true);
                        isFlashbackMode = false;
                    }
            }
            return false;
        }

    };

    private void setUpFlashBackMode(){
        FlashbackMode flashbackMode = new FlashbackMode(currentLocationInfo.getLocation(), TimeMachine.now(),populateMusic);
        queuedSongs= flashbackMode.initiate();
        isFlashbackMode = true;
        if(mediaPlayer == null){
            createMediaPlayer();
        }

        //Store song name and album
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",queuedSongs.get(0).get_title());
        editor.putString("song_album",queuedSongs.get(0).get_album());
        editor.apply();

        sharedPreferences = getSharedPreferences("flash_back_mode", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("isFlashBackMode", "true");
        editor.apply();

        mediaPlayer.reset();
        loadMedia(queuedSongs.get(0));

        queuedSongs.remove(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        populateMusic = new PopulateMusic(this);
        intervalStart=LocalTime.parse("11:00:00");
        intervalEnd= LocalTime.parse("16:00:00");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // updating current location
        currentLocationInfo=new LocationInfo();
        LocationListener locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocationInfo.setLocation(location);
                //Toast.makeText(getApplicationContext(), new Double(currentLocationInfo.getCurrentLatitude()).toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        // requesting location access
        LocationManager locationManger=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider=LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            return;
        }
        locationManger.requestLocationUpdates(locationProvider,3000,0,locationListener);

        SharedPreferences sharedPreferences = getSharedPreferences("flash_back_mode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String flashback_mode = sharedPreferences.getString("isFlashBackMode", "");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(flashback_mode.equals("")){
            editor.putString("isFlashBackMode", "false");
            editor.apply();

            String[] album_list_string = populateMusic.getAlbumListString();
            Bundle album_bundle = new Bundle();
            album_bundle.putStringArray("albums", album_list_string);

            // launch new fragment
            AlbumFragment album_fragment = new AlbumFragment();
            album_fragment.setArguments(album_bundle);
            transaction.replace(R.id.musicItems, album_fragment).commit();
        }else if(flashback_mode.equals("true")){
            isFlashbackMode = true;
            setUpFlashBackMode();
            transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
            navigation.getMenu().getItem(2).setChecked(true);
            Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
        }else if(flashback_mode.equals("false")){

            String[] album_list_string = populateMusic.getAlbumListString();
            Bundle album_bundle = new Bundle();
            album_bundle.putStringArray("albums", album_list_string);

            // launch new fragment
            AlbumFragment album_fragment = new AlbumFragment();
            album_fragment.setArguments(album_bundle);
            transaction.replace(R.id.musicItems, album_fragment).commit();

            isFlashbackMode = false;
            Toast.makeText(getApplicationContext(), "Standard Mode", Toast.LENGTH_SHORT).show();
        }


    }

    public void createMediaPlayer(){mediaPlayer = new MediaPlayer();}

    public void loadAlbumMedia(Album selected_album){

    }

    public void loadMedia(final Song selected_song){

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(getApplicationContext(), "song completed!", Toast.LENGTH_SHORT).show();
               Location currentLocation = currentLocationInfo.getLocation();
               selected_song.addLocation(currentLocation);
               selected_song.addDateTime(TimeMachine.now());
               mediaPlayer.start();


               if(isFlashbackMode){
                   Toast.makeText(getApplicationContext(), "song completed!", Toast.LENGTH_SHORT).show();
                    mediaPlayer.reset();
                    loadMedia(queuedSongs.get(0));
                    queuedSongs.remove(0);
               }


            }
        });

        int resourceId = selected_song.get_id();

        AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(resourceId);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null)
        mediaPlayer.release();
    }

    // so that back button doesn't kill app
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public PopulateMusic getPopulateMusic(){return populateMusic;}
};
