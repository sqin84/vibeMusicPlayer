package com.example.ajcin.flashbackmusicteam16;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.ajcin.flashbackmusicteam16.Album;
import com.example.ajcin.flashbackmusicteam16.AlbumView;
import com.example.ajcin.flashbackmusicteam16.R;
import com.example.ajcin.flashbackmusicteam16.Song;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by luke on 2/15/2018.
 */

public class PopulateMusic {

    private Context context;
    private ArrayList<Song> song_list;
    private ArrayList<Album> album_list;


    public PopulateMusic(Context current){
        this.context = current;
        song_list = new ArrayList<Song>();
        album_list = new ArrayList<Album>();
        populateMusicList();
    }

    public ArrayList<Song> getSongList(){return song_list;}

    public String[] getSongListString(){
        String[] song_list_string = new String[song_list.size()];
        Iterator<Song> iter = song_list.iterator();
        int index = 0;
        while(iter.hasNext()){
            Song curr_song = iter.next();
            song_list_string[index] = curr_song.get_title();
            index ++;
        }
        return song_list_string;
    }

    public Album getAlbum(String album){
        Album selected_album = new Album("NA","NA");
        Iterator<Album> iter = album_list.iterator();
        while(iter.hasNext()){
            Album curr_album = iter.next();
            if(curr_album.get_title().equals(album)){
                selected_album = curr_album;
            }
        }
        return selected_album;
    }
    public ArrayList<Album> getAlbumList(){return album_list;}

    public String[] getAlbumListString(){
        String[] album_list_string = new String[album_list.size()];
        Iterator<Album> iter = album_list.iterator();
        int index = 0;
        while(iter.hasNext()){
            Album curr_album = iter.next();
            album_list_string[index] = curr_album.get_title();
            index ++;
        }
        return album_list_string;
    }

    public ArrayList<Song> getSongListInAlbum(Album album){
        ArrayList<Song> album_songs = new ArrayList<Song>();
        int album_index = album_list.indexOf(album);
        if(album_index != -1){
            Album selected_album = album_list.get(album_index);
            album_songs = selected_album.get_album_songs();
        }
        return album_songs;
    }

    public String[] getSongListInAlbumString(Album album){
        ArrayList<Song> album_songs = getSongListInAlbum(album);
        String[] album_song_list_string = new String[album_songs.size()];
        Iterator<Song> iter = album_songs.iterator();
        int index = 0;
        while(iter.hasNext()){
            Song curr_song = iter.next();
            album_song_list_string[index] = curr_song.get_title();
            index ++;
        }
        return album_song_list_string;
    }

    public void populateMusicList() {
        Field[] fields = R.raw.class.getFields();
        int[] resArray = new int[fields.length];
        for(int f=0; f < fields.length; f++){
            try {
                int res_id = fields[f].getInt(null);
                AssetFileDescriptor assetFileDescriptor =
                        context.getResources().openRawResourceFd(res_id);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                String song_album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String song_artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                String song_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                Song curr_song = new Song(song_name, song_artist, song_album, res_id);
                song_list.add(curr_song);
                Iterator<Album> itr = album_list.iterator();
                boolean indexed = false;
                while(itr.hasNext()){
                    Album curr_album = itr.next();
                    if(song_album.equals(curr_album.get_title())){
                        curr_album.add_song(curr_song);
                        indexed = true;
                    }
                }
                if(!indexed){
                    album_list.add(new Album(song_album, song_artist));
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

