package com.example.ajcin.flashbackmusicteam16;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.LinkedList;
import static android.content.Context.MODE_PRIVATE;

/** songListFragment class to store resource id of file, along with various information about the Song.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 7, 2018
 */
public class songListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private String[] song_list_string;

    /** Required empty constructor */
    public songListFragment() {
    }

    /** Create the songListFragment */
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

    /** onListItemClick
      * Create media player and begin playing song.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(((AlbumView)getActivity()).mediaPlayer == null){
            ((AlbumView)getActivity()).createMediaPlayer();
        }
        ((AlbumView)getActivity()).mediaPlayer.reset();
        Song selected_song = ((AlbumView)getActivity()).populateMusic.getSong(song_list_string[position]);
        ((AlbumView)getActivity()).loadMedia(selected_song);
        ((AlbumView)getActivity()).album_playlist = new LinkedList<>();
        changeToNowPlaying(selected_song);
        //((AlbumView)getActivity()).mediaPlayer.start();

        //Store song name and album
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name", selected_song.get_title());
        editor.putString("artist_name", selected_song.get_artist());
        editor.putString("album_name", selected_song.get_album());
        editor.putString("address", selected_song.get_last_played_address());
        editor.putString("time", selected_song.get_last_time());
        editor.apply();
    }

    /** changeToNowPlaying
      * Change the view to Now Playing with the selected song's information.
      * @param song current Song being played
     */
    public void changeToNowPlaying(Song song) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle song_bundle = new Bundle();
        String[] song_name = new String[1];
        song_name[0] = song.get_title();
        song_bundle.putStringArray("song", song_name);
        NowPlayingFragment npFragment = new NowPlayingFragment();
        npFragment.setArguments(song_bundle);
        ((AlbumView)getActivity()).navigation.getMenu().getItem(2).setChecked(true);
        transaction.replace(R.id.musicItems, npFragment).commit();
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
