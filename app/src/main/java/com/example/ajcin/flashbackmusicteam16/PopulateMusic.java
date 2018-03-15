package com.example.ajcin.flashbackmusicteam16;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/** PopulateMusic class to get albums and songs to be played.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 7, 2018
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

    /** getSong
      * Find song with specified name.
      * @param song name of the song to search for
      * @return song object with matching name
     */
    public Song getSong(String song){
        Song selected_song;
        Iterator<Song> iter = song_list.iterator();
        while(iter.hasNext()){
            Song curr_song = iter.next();
            if(curr_song.get_title().equals(song)){
                selected_song = curr_song;
                return selected_song;
            }
        }
        return null;
    }

    public ArrayList<Song> getSongList() {  return song_list; }

    /** getSongListString
      * Compile list of all local song's names.
      * @return array of song names
     */
    public String[] getSongListString() {
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

    /** getAlbum
      * Find album with specified name
      * @param album name of the album to search for
      * @return album object with matching name
     */
    public Album getAlbum(String album){
        Album selected_album = new Album("NA","NA");
        Iterator<Album> iter = album_list.iterator();
        while(iter.hasNext()){
            Album curr_album = iter.next();
            if(curr_album.get_title().equals(album)){
                selected_album = curr_album;
                break;
            }
        }

        return selected_album;
    }

    public ArrayList<Album> getAlbumList() {    return album_list; }

    /** getAlbumListString
      * Compile list of all local album's names.
      * @return array of album names
     */
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

    /** getSongListInAlbum
      * Get all songs that belong to the specified album.
      * @param album album to find songs within
      * @return list of songs in album
     */
    public ArrayList<Song> getSongListInAlbum(Album album){
        ArrayList<Song> album_songs = new ArrayList<Song>();
        int album_index = album_list.indexOf(album);
        if(album_index != -1){
            Album selected_album = album_list.get(album_index);
            album_songs = selected_album.get_album_songs();
        }
        return album_songs;
    }

    /** getSongListInAlbumString
      * Get the names of all songs that belong to the specified album.
      * @param album album to find songs within
      * @return array of song names within album
     */
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

    /** populateMusicList
      * Populate the list of songs and albums.
     */
    public void populateMusicList() {
        /************************** AARON ******************************************/
        //File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        /*File[] files = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);

        Log.d("PopulateMusic", "got list of files");
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for(int i = 0; i < files.length; i++) {
            try {
                mmr.setDataSource(files[i].getAbsolutePath());
                Log.d("PopulateMusic", "set up mmr data source");
                String song_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String song_artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                String song_album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                //String url = mmr.extractMetadata(MediaMetadataRetriever.METADATA_URL);
                Song curr_song = new UrlSong(song_name, song_artist, song_album, 1, "test");
                song_list.add(curr_song);
                Log.d("PopulateMusic", "extracted metadata and added to song_list");

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
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }*/
        /************************** AARON ******************************************/

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
                ResourceSong curr_song = new ResourceSong(song_name, song_artist, song_album, res_id);
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

