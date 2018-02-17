package com.example.ajcin.flashbackmusicteam16;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NowPlayingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NowPlayingFragment#newInstance} factory method to
 * create an instance of this fragment.
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

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        /*BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_nowPlaying);*/

        play_btn = (Button) rootView.findViewById(R.id.btn_play);
        pause_btn = (Button) rootView.findViewById(R.id.btn_pause);
        reset_btn = (Button) rootView.findViewById(R.id.btn_reset);
        favorite_btn = (Button) rootView.findViewById(R.id.btn_favorite);
        dislike_btn = (Button) rootView.findViewById(R.id.btn_dislike);

        song_name = (TextView) rootView.findViewById(R.id.songName);
        artist_name = (TextView) rootView.findViewById(R.id.artistName);
        album_name = (TextView) rootView.findViewById(R.id.albumName);

        //Display song name and album in NowPlaying

        if(((AlbumView)getActivity()).mediaPlayer != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", Context.MODE_PRIVATE);
            String name = sharedPreferences.getString("song_name", "");
            String artist = sharedPreferences.getString("artist_name", "");
            String album = sharedPreferences.getString("album_name", "");
            song_name.setText(name);
            artist_name.setText(artist);
            album_name.setText(album);
        }else{
            //if there is nothing playing at the momenr
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
                //((AlbumView)getActivity()).currentlyPlaying.favorite_song();
            }
        });
        dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((AlbumView)getActivity()).currentlyPlaying.dislike_song();
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v){

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NowPlayingFragment.
     */
    // TODO: Rename and change types and number of parameters
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
