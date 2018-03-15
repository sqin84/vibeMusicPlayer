
package com.example.ajcin.flashbackmusicteam16.Tests;

import com.example.ajcin.flashbackmusicteam16.Main_Activity;
import com.example.ajcin.flashbackmusicteam16.Play;
import com.example.ajcin.flashbackmusicteam16.ResourceSong;
import com.example.ajcin.flashbackmusicteam16.Song;
import com.google.firebase.database.DatabaseReference;

import org.junit.Rule;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *  Tests for firebase upload and downloads of play objects
 */

public class PlayFirebaseTest {
    DatabaseReference ref;
    @Rule
    public ActivityTestRule<Main_Activity> mainActivity = new ActivityTestRule<Main_Activity>(Main_Activity.class);

    @Before
    public void setUp(){
       ref =  mainActivity.getActivity().myRef;
       ref.child("Testing").setValue(null);
    }
    public void pushPlay(DatabaseReference myRef, Song s){
        Play play = new Play();
        play.setAddress(s.get_last_played_address()).setSongName(s.get_title()).setUser(s.get_user_name());

        //remove all spaces and new lines
        myRef.child("Testing").child(s.get_last_played_address().replaceAll("\\s+","")).push().setValue(play);
    }

    @Test
    public void test1(){
        Song s1 = new ResourceSong("Fireworks","Katy", "A",0);
        Song s2 = new ResourceSong("Hello", "Adele", "B", 1);
        final String[] names = new String[2];
        names[0] = "Fireworks";
        names[1] = "Hello";
        s1.set_last_played_address("123 UCSD");
        s2.set_last_played_address("123 UCSD");
        s1.set_user_name("Juke Lervis");
        s2.set_user_name("Juke Lervis");


        pushPlay(mainActivity.getActivity().myRef,s1);
        pushPlay(mainActivity.getActivity().myRef,s2);

        // How to Query, will call vibe mode functions in onDataChange
        Query queryRef = ref.child("Testing").child("123UCSD").orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null){
                    Log.w("where am i",snapshot.getKey());
                }
                else {
                    int i = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.w("child where am i",child.getKey());
                        Play play = child.getValue(Play.class);
                        assertTrue(play.getAddress().equals("123 UCSD"));
                        assertTrue(play.getSongName().equals(names[i]));
                        assertTrue(play.getUser().equals("Juke Lervis"));
                        i++;
                    }
                    Log.w("what is i", String.valueOf(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });

    }

}