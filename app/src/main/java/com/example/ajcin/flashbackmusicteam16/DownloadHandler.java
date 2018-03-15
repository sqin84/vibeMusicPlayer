package com.example.ajcin.flashbackmusicteam16;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public long download_file(Context context, String download_url) {
        try {
            URL url = new URL(download_url);
            Uri song_uri = Uri.parse(download_url);
            DownloadManager manager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(song_uri);

            //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            //mmr.setDataSource(download_url);
            //String song_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            //Log.d("DownloadHandler", "song name is: " + song_name);

            request.setTitle("Test Song");
            request.setDescription("Url is " + download_url);

            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            String download_path = music.getAbsolutePath();
            //request.setDestinationInExternalFilesDir(context, download_path, song_name);
            request.setDestinationInExternalFilesDir(context, download_path, "Test.mp3");
            long download_ref = manager.enqueue(request);

            //TODO check if download is album

            return download_ref;
        } catch(MalformedURLException e) {
            Toast.makeText(context, "Error downloading from specified URL.", Toast.LENGTH_SHORT);
            e.printStackTrace();
            return -1;
        }
    }

    public boolean unpack_zip(String path, String zipname) {
        InputStream instream;
        ZipInputStream zip_instream;

        try {
            String file_name;
            instream = new FileInputStream(path + zipname);
            zip_instream = new ZipInputStream(new BufferedInputStream(instream));
            ZipEntry zip_entry;
            byte[] buffer = new byte[1024];
            int count;

            while((zip_entry = zip_instream.getNextEntry()) != null) {
                file_name = zip_entry.getName();

                if(zip_entry.isDirectory()) {
                    File fmd = new File(path + file_name);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + file_name);

                while((count = zip_instream.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zip_instream.closeEntry();
            }

            zip_instream.close();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void check_status(long download_ref) {
        DownloadManager.Query download_query = new DownloadManager.Query();
        download_query.setFilterById(download_ref);

        Cursor cursor = manager.query(download_query);
        if(cursor.moveToFirst()) {
            download_status(cursor, download_ref);
        }
    }

    private void download_status(Cursor cursor, long download_ref){

        //column for download  status
        int column_index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(column_index);
        //column for reason code if the download failed or paused
        int column_reason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(column_reason);
        //get the download filename
        int filename_index = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String file_name = cursor.getString(filename_index);

        String status_text = "";
        String reason_text = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                status_text = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reason_text = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reason_text = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reason_text = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reason_text = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reason_text = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reason_text = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reason_text = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reason_text = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reason_text = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                status_text = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reason_text = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reason_text = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reason_text = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reason_text = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                status_text = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                status_text = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                status_text = "STATUS_SUCCESSFUL";
                reason_text = "Filename:\n" + file_name;
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

    /* NO LONGER A REQUIREMENT ****/
    /*public boolean check_available_space(String url) {
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

            return (file_size < bytes_available);
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    } */
}
