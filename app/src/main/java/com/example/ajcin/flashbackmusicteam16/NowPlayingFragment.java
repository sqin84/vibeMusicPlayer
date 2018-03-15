package com.example.ajcin.flashbackmusicteam16;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/** NowPlayingFragment class to handle actions within the Now Playing tab.
 * Author: CSE 110 - Team 16, Winter 2018
 * Date: February 7, 2018
 */
public class NowPlayingFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    Button play_btn;
    Button pause_btn;
    Button reset_btn;
    Button favorite_btn;
    Button dislike_btn;
    Button setTime_btn;
    TextView song_name;
    TextView artist_name;
    TextView album_name;
    TextView time_textview;
    TextView location_textview;
    EditText timeInput;
    String songName;

    String artist = "";
    String album = "";
    String time = "";
    String address = "";



    /** Required empty contructor */
    public NowPlayingFragment() {}

    /** onCreateView
      * Get and display information of current song. Handles button clicks within Now Playing.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        play_btn = (Button) rootView.findViewById(R.id.btn_play);
        pause_btn = (Button) rootView.findViewById(R.id.btn_pause);
        reset_btn = (Button) rootView.findViewById(R.id.btn_reset);
        favorite_btn = (Button) rootView.findViewById(R.id.btn_favorite);
        dislike_btn = (Button) rootView.findViewById(R.id.btn_dislike);
        setTime_btn = (Button) rootView.findViewById(R.id.setTime);

        song_name = (TextView) rootView.findViewById(R.id.songName);
        artist_name = (TextView) rootView.findViewById(R.id.artistName);
        album_name = (TextView) rootView.findViewById(R.id.albumName);
        timeInput = rootView.findViewById(R.id.editText);
        time_textview = rootView.findViewById(R.id.time);
        location_textview = rootView.findViewById(R.id.location);





        //Display song name and album in NowPlaying
        if(((Main_Activity)getActivity()).mediaPlayer != null && getArguments() != null ) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", Context.MODE_PRIVATE);


            // first, grab local data, mainly to display local songs that is played for the firstime/not in firebase
            song_name.setText(sharedPreferences.getString("song_name",""));
            artist_name.setText(sharedPreferences.getString("artist_name", ""));
            album_name.setText(sharedPreferences.getString("album_name", ""));

            // the two can be deleted ?!
            time_textview.setText(sharedPreferences.getString("time", ""));
            location_textview.setText(sharedPreferences.getString("address", ""));

            // update with firebase data
            songName = getArguments().getString("song");
            querySong();


        }else{
            //if there is nothing playing at the moment
            song_name.setText("");
            artist_name.setText("No Song Playing");
            album_name.setText("");
        }

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Main_Activity)getActivity()).mediaPlayer != null){
                    ((Main_Activity)getActivity()).mediaPlayer.start();
                }
            }
        });
        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Main_Activity)getActivity()).mediaPlayer != null) {
                    if (((Main_Activity) getActivity()).mediaPlayer.isPlaying()) {
                        ((Main_Activity) getActivity()).mediaPlayer.pause();
                    }
                }
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Main_Activity)getActivity()).mediaPlayer != null) {
                    ((Main_Activity) getActivity()).mediaPlayer.seekTo(0);
                    ((Main_Activity) getActivity()).mediaPlayer.start();
                }
            }
        });
        favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Main_Activity)getActivity()).mediaPlayer != null) {
                    Song currSong = ((Main_Activity) getActivity()).currentlyPlaying;
                    currSong.favorite_song();

                    if (currSong.get_is_favorited()) {
                        favorite_btn.setBackgroundColor(Color.RED);
                        dislike_btn.setBackgroundColor(Color.LTGRAY);
                    } else {
                        favorite_btn.setBackgroundColor(Color.LTGRAY);
                    }
                }

            }
        });
        dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Main_Activity)getActivity()).mediaPlayer != null) {
                    Song currSong = ((Main_Activity) getActivity()).currentlyPlaying;
                    currSong.dislike_song();

                    if (currSong.get_is_disliked()) {
                        dislike_btn.setBackgroundColor(Color.RED);
                        favorite_btn.setBackgroundColor(Color.LTGRAY);
                    } else {
                        dislike_btn.setBackgroundColor(Color.LTGRAY);
                    }
                }
            }
        });
        setTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = timeInput.getText().toString();
                String[] values = input.split(",");
                int year = Integer.parseInt(values[0]);
                int month = Integer.parseInt(values[1]);
                int day = Integer.parseInt(values[2]);
                int hour = Integer.parseInt(values[3]);
                int minute = Integer.parseInt(values[4]);

                LocalDateTime dummyTime = LocalDateTime.of(year, month, day, hour, minute);
                TimeMachine.useFixedClockAt(dummyTime);
            }
        });

        return rootView;
    }

    private void querySong(){
        ((Main_Activity)getActivity()).progressBar.setVisibility(View.VISIBLE);
        Log.w("song name", songName);
        Query queryRef = ((Main_Activity)getActivity()).myRef.child("Songs").child(songName).orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null){
                    Log.w("query failed","failed");
                }
                else {
                    Log.w("query success","success");
                    //TODO grab user name the same way here!
                    time = (String) snapshot.child("last_played_date_time").getValue();
                    artist = (String) snapshot.child("artist").getValue();
                    album = (String) snapshot.child ("album").getValue();
                    address = (String)snapshot.child("last_played_address").getValue();

                    song_name.setText(songName);
                    artist_name.setText(artist);
                    album_name.setText(album);
                    time_textview.setText(time);
                    location_textview.setText(address);
                }
                ((Main_Activity)getActivity()).progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }
    @Override
    public void onClick(View v){

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static NowPlayingFragment newInstance(String param1, String param2) {
        NowPlayingFragment fragment = new NowPlayingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
