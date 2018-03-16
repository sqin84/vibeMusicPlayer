package com.example.ajcin.flashbackmusicteam16;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.mortbay.jetty.Main;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

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
    Button upcoming_btn;
    TextView song_name;
    TextView artist_name;
    TextView album_name;
    TextView time_textview;
    TextView location_textview;
    TextView user_name_textview;
    EditText timeInput;
    String songName;
    ArrayList<Song> upcoming_songs_list;
    View rootView;

    String artist = "";
    String album = "";
    String time = "";
    String address = "";
    String user_name = "";

    /** Required empty contructor */
    public NowPlayingFragment() {}

    /** onCreateView
      * Get and display information of current song. Handles button clicks within Now Playing.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        play_btn = (Button) rootView.findViewById(R.id.btn_play);
        pause_btn = (Button) rootView.findViewById(R.id.btn_pause);
        reset_btn = (Button) rootView.findViewById(R.id.btn_reset);
        favorite_btn = (Button) rootView.findViewById(R.id.btn_favorite);
        dislike_btn = (Button) rootView.findViewById(R.id.btn_dislike);
        setTime_btn = (Button) rootView.findViewById(R.id.setTime);
        upcoming_btn = (Button) rootView.findViewById(R.id.upcoming_btn);

        song_name = (TextView) rootView.findViewById(R.id.songName);
        artist_name = (TextView) rootView.findViewById(R.id.artistName);
        album_name = (TextView) rootView.findViewById(R.id.albumName);
        timeInput = rootView.findViewById(R.id.editText);
        time_textview = rootView.findViewById(R.id.time);
        location_textview = rootView.findViewById(R.id.location);
        user_name_textview = (TextView) rootView.findViewById(R.id.last_user);

        if(getArguments() != null){
            songName = getArguments().getString("song");
            upcoming_songs_list = (ArrayList<Song>)getArguments().getSerializable("song_list");
            if(upcoming_songs_list == null){
                upcoming_btn.setVisibility(View.INVISIBLE);
            }
        }
        else{
            upcoming_btn.setVisibility(View.INVISIBLE);
        }

        //Display song name and album in NowPlaying
        if(((Main_Activity)getActivity()).mediaPlayer != null && getArguments() != null ) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", MODE_PRIVATE);
            //SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_name", Context.MODE_PRIVATE);



            // first, grab local data, mainly to display local songs that is played for the firstime/not in firebase
            song_name.setText(sharedPreferences.getString("song_name",""));
            artist_name.setText(sharedPreferences.getString("artist_name", ""));
            album_name.setText(sharedPreferences.getString("album_name", ""));

            // the two can be deleted ?!
//            time_textview.setText(sharedPreferences.getString("time", ""));
//            location_textview.setText(sharedPreferences.getString("address", ""));

            // update with firebase data
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
                if(values.length == 5) {
                    int year = Integer.parseInt(values[0]);
                    int month = Integer.parseInt(values[1]);
                    int day = Integer.parseInt(values[2]);
                    int hour = Integer.parseInt(values[3]);
                    int minute = Integer.parseInt(values[4]);

                    LocalDateTime dummyTime = LocalDateTime.of(year, month, day, hour, minute);
                    TimeMachine.useFixedClockAt(dummyTime);
                    Toast.makeText(getContext(),"Time set!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"Incorrect number of entries!",Toast.LENGTH_SHORT).show();
                    timeInput.getText().clear();
                }
            }
        });
        upcoming_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ArrayList<String> upcoming_song_name_list = new ArrayList<String>();
                for(Song s : upcoming_songs_list){
                    upcoming_song_name_list.add(s.get_title());
                }
                LayoutInflater popoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = popoutInflater.inflate(R.layout.upcoming_tracks_popup_window,null);
                ListView upcoming_track_list_view = (ListView) v.findViewById(R.id.upcoming_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, upcoming_song_name_list);
                upcoming_track_list_view.setAdapter(adapter);
                int popupWidth = 500;
                int popupHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
                PopupWindow pw = new PopupWindow(v);
                pw.setFocusable(true);
                pw.setWidth(popupWidth);
                pw.setHeight(popupHeight);
                pw.showAsDropDown(view, -5, 0);
                pw.setBackgroundDrawable(new ColorDrawable());
            }
        });

        return rootView;
    }

    private String assignName(String user_name){
        String[] proxyNames = new String[]{"alligator", "anteater", "armadillo", "auroch", "axolotl", "badger", "bat",
                "beaver", "buffalo", "camel", "chameleon", "cheetah", "chipmunk", "chinchilla", "chupacabra", "cormorant",
                "coyote", "crow", "dingo", "dinosaur", "dolphin", "duck", "elephant", "ferret", "fox", "frog", "giraffe", "gopher",
                "grizzly", "hedgehog", "hippo", "hyena", "jackal", "ibex", "ifrit", "iguana", "koala", "kraken", "lemur", "leopard",
                "liger", "llama", "manatee", "mink", "monkey", "narwhal", "nyan cat", "orangutan", "otter", "panda", "penguin",
                "platypus", "python", "pumpkin", "quagga", "rabbit", "raccoon", "rhino", "sheep", "shrew", "skunk", "slow loris",
                "squirrel", "turtle", "walrus", "wolf", "wolverine", "wombat"};
        String proxyName;
        Random randomgenerator = new Random();
        SharedPreferences sp= getContext().getSharedPreferences("proxy_contacts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set used_names = (HashSet<String>)sp.getStringSet("used_names",null);
        String pn = proxyNames[randomgenerator.nextInt(proxyNames.length)];
        if(used_names == null){
            used_names = new HashSet<String>();
            used_names.add(pn);
        }
        else{
            while(used_names.contains(pn)){
                pn = proxyNames[randomgenerator.nextInt(proxyNames.length)];
            }
            used_names.add(pn);
        }
        editor.putStringSet("used_names",used_names);
        editor.apply();
        return pn;
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
                    user_name = (String)snapshot.child("last_played_user").getValue();
                    if(((Main_Activity)getActivity()).contactList.contains(user_name)){
                        user_name_textview.setText(user_name);
                    }
                    else{
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("proxy_contacts", MODE_PRIVATE);
                        String proxy_name = sharedPreferences.getString(user_name,null);
                        if(proxy_name == null){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            proxy_name = assignName(user_name);
                            editor.putString(user_name,proxy_name);
                            editor.apply();
                        }
                        user_name_textview.setText("anonymous " + proxy_name);
                    }

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
=======
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
