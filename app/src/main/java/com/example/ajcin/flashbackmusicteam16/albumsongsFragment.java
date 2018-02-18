package com.example.ajcin.flashbackmusicteam16;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.content.Context.MODE_PRIVATE;


public class albumsongsFragment extends ListFragment {


    private OnFragmentInteractionListener mListener;
    String[] album_song_list_string;


    public albumsongsFragment() {
        // Required empty public constructor
    }

    private String[] songs_in_an_album = {"song1", "song2", "heuheue"};

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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(((AlbumView)getActivity()).mediaPlayer == null){
            ((AlbumView)getActivity()).createMediaPlayer();
        }
        ((AlbumView)getActivity()).mediaPlayer.reset();
        Song selected_song = ((AlbumView)getActivity()).populateMusic.getSong(album_song_list_string[position]);
        ((AlbumView)getActivity()).loadMedia(selected_song);
        changeToNowPlaying(selected_song);
        ((AlbumView)getActivity()).mediaPlayer.start();

        //Store the track information to historicalTrack arrayList
        ((AlbumView)getActivity()).HistoricalTrack.add(selected_song.get_title()+" "+"("+selected_song.get_album()+")");

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
           // throw new RuntimeException(context.toString()
             //       + " must implement OnFragmentInteractionListener");
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
