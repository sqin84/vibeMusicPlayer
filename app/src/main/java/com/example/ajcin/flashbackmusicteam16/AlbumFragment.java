package com.example.ajcin.flashbackmusicteam16;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


public class AlbumFragment extends ListFragment {


    private OnFragmentInteractionListener mListener;
    String[] album_list_string;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        album_list_string = bundle.getStringArray("albums");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, album_list_string);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_album, container, false);
         view.findViewById(android.R.id.list).setScrollContainer(true);
         return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        Album selected_album = ((AlbumView)getActivity()).populateMusic.getAlbum(album_list_string[position]);
        String[] album_song_list = ((AlbumView)getActivity()).populateMusic.getSongListInAlbumString(selected_album);
        final Bundle album_song_bundle = new Bundle();
        album_song_bundle.putStringArray("album_songs",album_song_list);
        final albumsongsFragment album_songs_fragment = new albumsongsFragment();
        album_songs_fragment.setArguments(album_song_bundle);


        //Show pop up menu
        PopupMenu popup = new PopupMenu(getContext(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.one){
                transaction.replace(R.id.musicItems,album_songs_fragment).commit();
                }
                if(item.getItemId()==R.id.two){
                    /*Bundle bundle = getArguments();
                    String[] album_song_list_string;
                    album_song_list_string = bundle.getStringArray("album_songs");
                    if(((AlbumView)getActivity()).mediaPlayer == null){
                        ((AlbumView)getActivity()).createMediaPlayer();
                    }
                    ((AlbumView)getActivity()).mediaPlayer.reset();
                    Song selected_song = ((AlbumView)getActivity()).populateMusic.getSong(album_song_list_string[]);
                    ((AlbumView)getActivity()).loadMedia(selected_song);
                    ((AlbumView)getActivity()).mediaPlayer.start();*/

                }
                return true;
            }
        });

        popup.show();//showing popup menu
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
            //throw new RuntimeException(context.toString()
                   // + " must implement OnFragmentInteractionListener");
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
