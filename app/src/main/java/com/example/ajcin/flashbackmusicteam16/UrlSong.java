package com.example.ajcin.flashbackmusicteam16;

import android.app.Activity;
import android.app.DownloadManager;
import static android.content.Context.DOWNLOAD_SERVICE;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ajcin on 3/2/2018.
 */
public class UrlSong extends Song {

    private final String url;

    public UrlSong(String title, String artist, String album, int id, String url) {
        super(title, artist, album, id);
        this.url = url;
    }

    public String get_url() {
        return url;
    }

    public long downloadSong(Activity activity, Context context) {
        String downloadUrl = get_url();

        if(!checkAvailableSpace(downloadUrl)) {
            // need to delete songs
        }

        Uri song_uri = Uri.parse(downloadUrl);
        DownloadManager manager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(song_uri);
        request.setTitle("Downloading Song");
        request.setDescription("Url is " + downloadUrl);
        request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS,
                get_title() + ".mp3");
        long downloadRef = manager.enqueue(request);
        return downloadRef;
    }

    public boolean checkAvailableSpace(String url) {
        URL song_url = null;
        URLConnection connection = null;
        try {
            song_url = new URL(url);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            connection = song_url.openConnection();
            connection.connect();
        } catch(IOException e) {
            e.printStackTrace();
        }

        long file_size = connection.getContentLengthLong();

        //if storage has more space than file size, return true
        return false;
    }
}
