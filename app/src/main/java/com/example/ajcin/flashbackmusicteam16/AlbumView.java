package com.example.ajcin.flashbackmusicteam16;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** AlbumView class to handle logic associated with playing Songs from an album.
 * Author: CSE 110 - Team 16, Winter 2018
 * Date: February 7, 2018
 */
public class AlbumView extends AppCompatActivity {

    public PopulateMusic populateMusic;
    public MediaPlayer mediaPlayer;
    public LinkedList<Song> album_playlist;
    public Song currentlyPlaying;
    public BottomNavigationView navigation;
    public ProgressBar progressBar;
    private Location currentLocation;
    private int currentResource;
    private LocalTime intervalStart, intervalEnd;
    private TimeMachine timeMachine;
    ArrayList<Song> queuedSongs;
    public Context context;
    //private static final int MEDIA_RES_ID = R.raw.after_the_storm;

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
        FlashbackMode flashbackMode = new FlashbackMode(currentLocation, TimeMachine.now(),populateMusic);
        queuedSongs= flashbackMode.initiate();
        isFlashbackMode = true;
       // Toast.makeText(this, Integer.valueOf(queuedSongs.get(0).getScore()).toString(), Toast.LENGTH_SHORT).show();
        if(mediaPlayer == null){
            createMediaPlayer();
        }

        //Store song name and album
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",queuedSongs.get(0).get_title());
        editor.putString("album_name",queuedSongs.get(0).get_album());
        editor.putString("artist_name",queuedSongs.get(0).get_artist());
        editor.putString("address", queuedSongs.get(0).get_last_played_address());
        editor.putString("time", queuedSongs.get(0).get_last_time());
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
       // intervalStart=LocalTime.parse("11:00:00");
        //intervalEnd= LocalTime.parse("16:00:00");

        LocalDateTime dummyTime = LocalDateTime.of(2017, 2, 7, 12, 00);
        TimeMachine.useFixedClockAt(dummyTime);

        // TODO remove this after testing
        //context = getApplicationContext();
        //DownloadHandler handler = new DownloadHandler(context);
        //handler.downloadSong( context, "https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/AndroidDownloadManager.mp3");
        // TODO remove this after testing

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // updating current location
        LocationListener locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = location;
                //Toast.makeText(getApplicationContext(), new Double(currentLocation.getLatitude()).toString(),Toast.LENGTH_SHORT).show();
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
        if(flashback_mode.equals("")) {
            editor.putString("isFlashBackMode", "false");
            editor.apply();

            String[] album_list_string = populateMusic.getAlbumListString();
            Bundle album_bundle = new Bundle();
            album_bundle.putStringArray("albums", album_list_string);

            // launch new fragment
            AlbumFragment album_fragment = new AlbumFragment();
            album_fragment.setArguments(album_bundle);
            transaction.replace(R.id.musicItems, album_fragment).commit();
        }
        else if(flashback_mode.equals("true")) {
            isFlashbackMode = true;
            setUpFlashBackMode();
            transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
            navigation.getMenu().getItem(2).setChecked(true);
            Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
        } 
        else if(flashback_mode.equals("false")) {
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

    public void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //float speed=3f;
       // mediaPlayer.getPlaybackParams().setSpeed(speed);
    }

    public void loadMedia(final Song selected_song) {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(getApplicationContext(), "song completed!", Toast.LENGTH_SHORT).show();
                Location currLocation = currentLocation;
                selected_song.addLocation(currentLocation);
                selected_song.addDateTime(TimeMachine.now());

                Log.d("long + lat", Double.valueOf(currentLocation.getLatitude()).toString() + " "+  Double.valueOf(currentLocation.getLongitude()).toString());

                /* doesn't work, attempt to get address
                new AsyncTaskAddress().execute(currentLocation.getLatitude(),currentLocation.getLongitude());
                selected_song.set_last_played_address(mAddressOutput);
                */

                mediaPlayer.start();

                if(isFlashbackMode){
                    Toast.makeText(getApplicationContext(), "song completed!", Toast.LENGTH_SHORT).show();
                    mediaPlayer.reset();
                    loadMedia(queuedSongs.get(0));
                    SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("song_name",queuedSongs.get(0).get_title());
                    editor.putString("artist_name", queuedSongs.get(0).get_artist());
                    editor.putString("album_name",queuedSongs.get(0).get_album());
                    editor.putString("address", queuedSongs.get(0).get_last_played_address());
                    editor.putString("time", queuedSongs.get(0).get_last_time());
                    editor.apply();
                    queuedSongs.remove(0);
                }
                else {
                    nextAlbumTrack();
                }
            }
        });
        currentlyPlaying = selected_song;
        selected_song.startPlayingSong(this, mediaPlayer, this);
    }

    //Test method
    public void nextAlbumTrack() {
        if(album_playlist != null && album_playlist.size()>0) {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            Song curr_song = album_playlist.removeFirst();
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user_name", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("song_name",curr_song.get_title());
            editor.putString("album_name",curr_song.get_album());
            editor.putString("artist_name",curr_song.get_artist());
            editor.putString("time", curr_song.get_last_time());
            editor.putString("address", curr_song.get_last_played_address());
            editor.apply();
            loadMedia(curr_song);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null)
        mediaPlayer.release();
    }

    // so that back button doesn't kill app
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {

           Log.w("exception: ", e.getClass().getName());
           /* e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
            */
        }
        return strAdd;
    }
    public PopulateMusic getPopulateMusic() { return populateMusic;}


};
