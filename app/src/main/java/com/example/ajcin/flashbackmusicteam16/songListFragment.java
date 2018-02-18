package com.example.ajcin.flashbackmusicteam16;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class songListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private String[] song_list_string;

    public songListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        song_list_string = bundle.getStringArray("songs");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, song_list_string);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        view.findViewById(android.R.id.list).setScrollContainer(true);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(((AlbumView)getActivity()).mediaPlayer == null){
            ((AlbumView)getActivity()).createMediaPlayer();
        }
        ((AlbumView)getActivity()).mediaPlayer.reset();
        Song selected_song = ((AlbumView)getActivity()).populateMusic.getSong(song_list_string[position]);
        ((AlbumView)getActivity()).loadMedia(selected_song);
        ((AlbumView)getActivity()).album_playlist = new ArrayList<Song>(0);
        changeToNowPlaying(selected_song);

        //Store song name and album
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",selected_song.get_title());
        editor.putString("song_album",selected_song.get_album());
        editor.apply();
    }

    public void changeToNowPlaying(Song song) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle song_bundle = new Bundle();
        String[] song_name = new String[1];
        song_name[0] = song.get_title();
        song_bundle.putStringArray("song", song_name);
        NowPlayingFragment npFragment = new NowPlayingFragment();
        npFragment.setArguments(song_bundle);
        transaction.replace(R.id.musicItems, npFragment).commit();
    }



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
              //      + " must implement OnFragmentInteractionListener");
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
