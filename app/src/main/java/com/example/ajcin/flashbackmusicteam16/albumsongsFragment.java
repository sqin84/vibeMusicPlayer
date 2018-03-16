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

import java.util.LinkedList;
import static android.content.Context.MODE_PRIVATE;

/** albumsongsFragment class to handle playing songs from an album.
 * Author: CSE 110 - Team 16, Winter 2018
 * Date: February 7, 2018
 */
public class albumsongsFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    String[] album_song_list_string;

    /** Required empty constructor */
    public albumsongsFragment() {}

    /** Create the albumsongsFragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        album_song_list_string = bundle.getStringArray("album_songs");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, album_song_list_string);
        setListAdapter(adapter);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        view.findViewById(android.R.id.list).setScrollContainer(true);
        return view;
    }

    /** Play the song that is clicked. */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(((Main_Activity)getActivity()).mediaPlayer == null){
            ((Main_Activity)getActivity()).createMediaPlayer();
        }
        ((Main_Activity)getActivity()).mediaPlayer.reset();
        Song selected_song = ((Main_Activity)getActivity()).populateMusic.getSong(album_song_list_string[position]);
        ((Main_Activity)getActivity()).loadMedia(selected_song);
        ((Main_Activity)getActivity()).album_playlist = new LinkedList<>();
        changeToNowPlaying(selected_song);

        //Store song name and album
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",selected_song.get_title());
        editor.putString("artist_name", selected_song.get_artist());
        editor.putString("album_name",selected_song.get_album());
        editor.putString("address", selected_song.get_last_played_address());
        editor.putString("time", selected_song.get_last_time_string());
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
        //String[] song_name = new String[1];
        String song_name = song.get_title();
        song_bundle.putString("song", song_name);
        NowPlayingFragment npFragment = new NowPlayingFragment();
        npFragment.setArguments(song_bundle);
        ((Main_Activity)getActivity()).navigation.getMenu().getItem(2).setChecked(true);
        transaction.replace(R.id.musicItems, npFragment).commit();
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
             //       + " must implement OnFragmentInteractionListener");
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
