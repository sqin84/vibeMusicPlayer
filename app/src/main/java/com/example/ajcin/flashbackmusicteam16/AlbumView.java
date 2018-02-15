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

    private TextView mTextMessage;
    private ArrayList<Song> songList;
    private ArrayList<Album> albumList;
    private ListView songView;
    private MediaPlayer mediaPlayer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_albums:
                    transaction.replace(R.id.musicItems,new AlbumFragment()).commit();
                    return true;
                case R.id.navigation_songs:
                    transaction.replace(R.id.musicItems,new songListFragment()).commit();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();
        albumList = new ArrayList<Album>();
        populateMusicList();

//        LogCat to view albums/artists/songs
//        Iterator<Album> iter = albumList.iterator();
//        while(iter.hasNext()){
//            Album curr = iter.next();
//            Log.d("Albums", curr.get_name());
//            Log.d("Albums", curr.get_artist());
//            Iterator<Song> s_iter = curr.get_album_songs().iterator();
//            while(s_iter.hasNext()){
//                Song curr_s = s_iter.next();
//                Log.d("Albums", curr_s.get_title());
//            }
//        }

        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
    }

    public void populateMusicList() {
        Field[] fields=R.raw.class.getFields();
        int[] resArray = new int[fields.length];
        for(int f=0; f < fields.length; f++){
            try {
                int res_id = fields[f].getInt(null);
                AssetFileDescriptor assetFileDescriptor =
                        this.getResources().openRawResourceFd(res_id);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                String song_album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String song_artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                String song_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                Song curr_song = new Song(song_name, song_artist, song_album, res_id);
                songList.add(curr_song);
                Iterator<Album> itr = albumList.iterator();
                boolean indexed = false;
                while(itr.hasNext()){
                   Album curr_album = itr.next();
                   if(song_album.equals(curr_album.get_name())){
                        curr_album.add_song(curr_song);
                        indexed = true;
                   }
                }
                if(!indexed){
                    albumList.add(new Album(song_album, song_artist));
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


};
