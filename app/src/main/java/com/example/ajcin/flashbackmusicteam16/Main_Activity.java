package com.example.ajcin.flashbackmusicteam16;

import android.Manifest;
import android.app.Activity;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** Main_Activity moderator of everything.
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
    public String user;
    public List<String> contactList;
    PlayListBuilder playListBuilder;

    // for getting last known lcation when the app starts
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int PEOPLE_RESULT_CODE = 100;
    private static final int CONTACT_RESULT_CODE = 200;

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
                        //queryplays launches new mode as well
                        playListBuilder = new VibePlayListBuilder(populateMusic,contactList);
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
        Query queryRef = myRef.child("Plays").child(getCompleteAddressString(lat,lon).replaceAll("\\s+","")).orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.w("where am i", snapshot.getKey());
                if (snapshot == null || snapshot.getValue() == null){
                    // TODO no song has been played in current location, what do?
                }
                else {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Play play = child.getValue(Play.class);
                        ((VibePlayListBuilder)playListBuilder).addPlay(play);
                    }
                }

                // change mode once all songs are queried
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
            return setUpFlashBackMode();
        }
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.GONE);
            Bundle song_bundle = new Bundle();
            String song_name = result;
            song_bundle.putString("song", song_name);
            NowPlayingFragment npFragment = new NowPlayingFragment();
            npFragment.setArguments(song_bundle);
            transaction.replace(R.id.musicItems, npFragment).commit();
            Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
        }

    }
    /*
     * Set up vibe mode. Get playlist and start song.
     * returns the song name of the first song of the playlist.
     */
    private String setUpFlashBackMode(){

        playListBuilder.readyScores();
        queuedSongs= playListBuilder.build();
        if(queuedSongs.isEmpty()){
            return "";
        }
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
        editor.putString("time", queuedSongs.get(0).get_last_time_string());
        editor.apply();

        sharedPreferences = getSharedPreferences("flash_back_mode", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("isFlashBackMode", "true");
        editor.apply();

        mediaPlayer.reset();
        Song toBePlayed = queuedSongs.get(0);
        loadMedia(queuedSongs.get(0));
        queuedSongs.remove(0);
        return toBePlayed.get_title();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PEOPLE_RESULT_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    user = data.getStringExtra("user_name");
                    contactList = data.getStringArrayListExtra("contact_names");
                    Log.d("Main_Activity", "User name returned successfully");
                    Log.d("Main_Activity", "Contact list returned successfully");
                }
                else{
                    user = null;
                    contactList = null;
                    Log.d("Main_Activity", "User name returned null");
                    Log.d("Main_Activity", "Contact list returned null");
                }
                break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, GooglePeopleActivity.class);
        startActivityForResult(intent,PEOPLE_RESULT_CODE);

        setContentView(R.layout.activity_album_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        populateMusic = new PopulateMusic(this);
       // intervalStart=LocalTime.parse("11:00:00");
        //intervalEnd= LocalTime.parse("16:00:00");

        LocalDateTime dummyTime = LocalDateTime.of(2017, 2, 7, 12, 0);
        TimeMachine.useFixedClockAt(dummyTime);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // TODO remove this after testing
        //context = getApplicationContext();
        //DownloadHandler handler = new DownloadHandler(context);
        //handler.download_file( context, "https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/AndroidDownloadManager.mp3");
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
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                playListBuilder = new VibePlayListBuilder(populateMusic,contactList);
                                queryPlays(transaction);
                                navigation.getMenu().getItem(2).setChecked(true);
                                Toast.makeText(getApplicationContext(), "Flashback mode engaged", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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
            s[0].addDateTime(TimeMachine.now());
            Double lat = s[0].getListOfLocations().get(0).getLatitude();
            Double lon = s[0].getListOfLocations().get(0).getLongitude();
            String address = getCompleteAddressString(lat,lon);
            s[0].set_last_played_address(address);
            s[0].set_user_name(user);

            Play play = new Play();
            play.setLatitude(lat).setLongitude(lon).setAddress(s[0].get_last_played_address())
                    .setSongName(s[0].get_title()).setUser(user).setTime(TimeMachine.now().toString());
            //remove all spaces and new lines
            myRef.child("Plays").child(s[0].get_last_played_address().replaceAll("\\s+","")).push().setValue(play);

            myRef.child("Songs").child(s[0].get_title()).child("last_played_date_time").setValue(s[0].get_last_time_string());
            myRef.child("Songs").child(s[0].get_title()).child("last_played_user").setValue(user);
            myRef.child("Songs").child(s[0].get_title()).child("last_played_address").setValue(address);
            myRef.child("Songs").child(s[0].get_title()).child("name").setValue(s[0].get_title());
            myRef.child("Songs").child(s[0].get_title()).child("artist").setValue(s[0].get_artist());
            myRef.child("Songs").child(s[0].get_title()).child("album").setValue(s[0].get_album());


            Log.d("long + lat", Double.valueOf(currentLocation.getLatitude()).toString() + " "+  Double.valueOf(currentLocation.getLongitude()).toString());

            if(isFlashbackMode) {
                if (!queuedSongs.isEmpty()){
                    mediaPlayer.reset();
                loadMedia(queuedSongs.get(0));
                SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("song_name", queuedSongs.get(0).get_title());
                editor.putString("artist_name", queuedSongs.get(0).get_artist());
                editor.putString("album_name", queuedSongs.get(0).get_album());
                editor.putString("address", queuedSongs.get(0).get_last_played_address());
                editor.putString("time", queuedSongs.get(0).get_last_time_string());
                editor.apply();
                queuedSongs.remove(0);
            }
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
            editor.putString("time", curr_song.get_last_time_string());
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
