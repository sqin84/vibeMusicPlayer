package com.example.ajcin.flashbackmusicteam16;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by ajcin on 3/7/2018.
 */

public class DownloadHandler {
    Context context;
    DownloadManager manager;

    public DownloadHandler(Context c) {
        context = c;
        manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    public long downloadSong(Context context, String downloadUrl) {
        //if(!checkAvailableSpace(downloadUrl)) {
        //    Toast.makeText(context, "Not enough space available", Toast.LENGTH_SHORT);
         //   return 0;
        //}

        Uri song_uri = Uri.parse(downloadUrl);
        DownloadManager manager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(song_uri);
        request.setTitle("Song Download");
        request.setDescription("Url is " + downloadUrl);

        File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        String downloadPath = music.getAbsolutePath();
        Log.d("DownloadHandler", "target dir: " + downloadPath);
        Log.d("DownloadHandler", "context: " + context);
        request.setDestinationInExternalFilesDir(context, downloadPath, "Test.mp3");
        long downloadRef = manager.enqueue(request);

        return downloadRef;
    }

    public void downloadSongStatus(long downloadRef) {
        DownloadManager.Query songDownloadQuery = new DownloadManager.Query();
        songDownloadQuery.setFilterById(downloadRef);

        Cursor cursor = manager.query(songDownloadQuery);
        if(cursor.moveToFirst()) {
            downloadStatus(cursor, downloadRef);
        }
    }

    private void downloadStatus(Cursor cursor, long downloadRef){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 3000);
    }

    public boolean checkAvailableSpace(String url) {
        URL song_url;
        URLConnection connection;

        try {
            song_url = new URL(url);
            connection = song_url.openConnection();
            connection.connect();
            long file_size = connection.getContentLengthLong();

            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            StatFs stat = new StatFs(music.getPath());
            long  bytes_available = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

            return (file_size < bytes_available) ? true : false;
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
