package com.example.ajcin.flashbackmusicteam16;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.LinkedList;
import static android.content.Context.MODE_PRIVATE;

/** songListFragment class to store resource id of file, along with various information about the Song.
  * Author: CSE 110 - Team 16, Winter 2018
  * Date: February 7, 2018
 */
public class songListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Song> songs;
    ArrayAdapter<Song> adapter;

    /** Required empty constructor */
    public songListFragment() {
    }

    /** Create the songListFragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        songs = ((Main_Activity)getActivity()).populateMusic.getSongList();
        SongListSorter sorter = new SongListSorterRecent(songs);
        sorter.sort();

        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, songs);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        view.findViewById(android.R.id.list).setScrollContainer(true);

        Button sb = view.findViewById(R.id.button4);
        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortButton(view);
            }
        });
        return view;
    }

    /** onListItemClick
      * Create media player and begin playing song.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(((Main_Activity)getActivity()).mediaPlayer == null){
            ((Main_Activity)getActivity()).createMediaPlayer();
        }
        ((Main_Activity)getActivity()).mediaPlayer.reset();
        Song selected_song = songs.get(position);
        ((Main_Activity)getActivity()).loadMedia(selected_song);
        ((Main_Activity)getActivity()).album_playlist = new LinkedList<>();
        changeToNowPlaying(selected_song);
        //((Main_Activity)getActivity()).mediaPlayer.start();

        //Store song name and album
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name", selected_song.get_title());
        editor.putString("artist_name", selected_song.get_artist());
        editor.putString("album_name", selected_song.get_album());
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

    public void sortButton(View view){
        //Show pop up menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_sort, popup.getMenu());

        //registering popup_album with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SongListSorter sorter;
               switch(item.getItemId()){
                   case R.id.one:
                       sorter = new SongListSorterTitle(songs);
                       break;
                   case R.id.two:
                       sorter = new SongListSorterArtist(songs);
                       break;
                   case R.id.three:
                       sorter = new SongListSorterAlbum(songs);
                       break;
                   case R.id.four:
                       sorter = new SongListSorterFavs(songs);
                       break;
                   default:
                       sorter = new SongListSorterRecent(songs);
                }
                sorter.sort();
                adapter.notifyDataSetChanged();
                //adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, songs);
                //setListAdapter(adapter);
                return true;
            }
        });

        popup.show();//showing popup_album menu
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
