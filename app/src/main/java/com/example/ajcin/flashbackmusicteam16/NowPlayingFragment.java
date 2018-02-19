package com.example.ajcin.flashbackmusicteam16;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    TextView song_name;
    TextView artist_name;
    TextView album_name;
    TextView time_textview;
    TextView location_textview;

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

        song_name = (TextView) rootView.findViewById(R.id.songName);
        artist_name = (TextView) rootView.findViewById(R.id.artistName);
        album_name = (TextView) rootView.findViewById(R.id.albumName);
        time_textview = rootView.findViewById(R.id.time);
        location_textview = rootView.findViewById(R.id.time);

        //Display song name and album in NowPlaying
        if(((AlbumView)getActivity()).mediaPlayer != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", Context.MODE_PRIVATE);
            String name = sharedPreferences.getString("song_name", "");
            String artist = sharedPreferences.getString("artist_name", "");
            String album = sharedPreferences.getString("album_name", "");
            String time = sharedPreferences.getString("time", "");
            String location = sharedPreferences.getString("address", "");

            song_name.setText(name);
            artist_name.setText(artist);
            album_name.setText(album);
            time_textview.setText(time);
            location_textview.setText(location);

        }else{
            //if there is nothing playing at the moment
            song_name.setText("");
            artist_name.setText("No Song Playing");
            album_name.setText("");
        }

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((AlbumView)getActivity()).mediaPlayer != null){
                    ((AlbumView)getActivity()).mediaPlayer.start();
                }
            }
        });
        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((AlbumView)getActivity()).mediaPlayer != null) {
                    if (((AlbumView) getActivity()).mediaPlayer.isPlaying()) {
                        ((AlbumView) getActivity()).mediaPlayer.pause();
                    }
                }
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((AlbumView)getActivity()).mediaPlayer != null) {
                    ((AlbumView) getActivity()).mediaPlayer.seekTo(0);
                    ((AlbumView) getActivity()).mediaPlayer.start();
                }
            }
        });
        favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((AlbumView)getActivity()).mediaPlayer != null) {
                    Song currSong = ((AlbumView) getActivity()).currentlyPlaying;
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
                if(((AlbumView)getActivity()).mediaPlayer != null) {
                    Song currSong = ((AlbumView) getActivity()).currentlyPlaying;
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

        return rootView;
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
