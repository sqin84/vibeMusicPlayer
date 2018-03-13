package com.example.ajcin.flashbackmusicteam16;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** Main_Activity class to handle logic associated with playing Songs from an album.
 * Author: CSE 110 - Team 16, Winter 2018
 * Date: February 7, 2018
 */
public class Main_Activity extends AppCompatActivity {

    public PopulateMusic populateMusic;
    public MediaPlayer mediaPlayer;
    public LinkedList<Song> album_playlist;
    public Song currentlyPlaying;
    public BottomNavigationView navigation;
    public ProgressBar progressBar;
    private Location currentLocation;
    LinkedList<Song> queuedSongs;
    public Context context;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference();
    //private static final int MEDIA_RES_ID = R.raw.after_the_storm;

    public static boolean isFlashbackMode = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
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
                        //Bundle song_bundle = new Bundle();
                      //  song_bundle.putStringArray("songs", song_list_string);

                        // launch new fragment
                        songListFragment song_fragment = new songListFragment();
                       // song_fragment.setArguments(song_bundle);
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
                        queryPlays(transaction);
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

    private void queryPlays(final FragmentTransaction transaction){
        progressBar.setVisibility(View.VISIBLE);
        Double lat = currentLocation.getLatitude();
        Double lon = currentLocation.getLongitude();
        Query queryRef = myRef.child("Plays").orderByKey().equalTo(getCompleteAddressString(lat,lon));
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null){
                    // TODO no song has been played in current location, what do?
                }
                else {
                    int i = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Play play = child.getValue(Play.class);
                            /*TODO this is where you launch vibe mode, where you generate a playlist
                             * TODO by sorting the plays wit scoring algorithm and generating a list of
                             *  Todo songs to play/download
                             */
                    }
                }
                new modeChangeTask(transaction).execute(currentLocation);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }
    private class modeChangeTask extends AsyncTask<Location, String, String>{
        FragmentTransaction transaction;
        modeChangeTask(FragmentTransaction transaction){
            this.transaction = transaction;
        }
        protected String doInBackground(Location... l){
            setUpFlashBackMode();
            return "";
        }
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.GONE);
            transaction.replace(R.id.musicItems, new NowPlayingFragment()).commit();
            Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
        }

    }
    private void setUpFlashBackMode(){
        FlashbackPlayListBuilder flashbackPlayListBuilder = new FlashbackPlayListBuilder(currentLocation, TimeMachine.now(),populateMusic);
        queuedSongs= flashbackPlayListBuilder.build();
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

        Intent intent = new Intent(this, GooglePeopleActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_album_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        populateMusic = new PopulateMusic(this);
       // intervalStart=LocalTime.parse("11:00:00");
        //intervalEnd= LocalTime.parse("16:00:00");

        LocalDateTime dummyTime = LocalDateTime.of(2017, 2, 7, 12, 0);
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
       // float speed=2.5f;
        // mediaPlayer.getPlaybackParams().setSpeed(speed);

    }

    // this is called in onCompletion of media player, anything that uses the current song's address field
    // needs to be done in this asyncTask
    private class SongOnCompletionTask extends AsyncTask<Song, String, String>{
        protected String doInBackground(Song... s){
            s[0].addLocation(new Location(currentLocation));
            Double lat = s[0].getListOfLocations().get(0).getLatitude();
            Double lon = s[0].getListOfLocations().get(0).getLongitude();
            s[0].set_last_played_address(getCompleteAddressString(lat,lon));

            Play play = new Play();
            play.setLatitude(lat).setLongitude(lon).setAddress(s[0].get_last_played_address())
                    .setSongName(s[0].get_title()).setUser(null);
            //remove all spaces and new lines
            myRef.child("Plays").child(s[0].get_last_played_address().replaceAll("\\s+","")).push().setValue(play);

            s[0].addDateTime(TimeMachine.now());

            Log.d("long + lat", Double.valueOf(currentLocation.getLatitude()).toString() + " "+  Double.valueOf(currentLocation.getLongitude()).toString());

            if(isFlashbackMode){
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
            return "";
        }
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(), "song completed!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            Log.w("address:",  result);
        }

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
                // anything that has to do after onComplete must be done in this AsyncTask
                new SongOnCompletionTask().execute(selected_song);
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

        Geocoder geocoder;
        //List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        /*addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*/
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                String knownName = addresses.get(0).getFeatureName();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                if( knownName != null){
                    Log.w("current feature name", knownName);
                }else{
                    Log.w("now known feature name","sad");
                }
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {

           Log.w("exception: ", e.getClass().getName());
           /* e.printStackTrace();*/
           Log.w("My Current loction address", "Canont get Address!");

        }
        return strAdd;
    }
    public PopulateMusic getPopulateMusic() { return populateMusic;}


};
