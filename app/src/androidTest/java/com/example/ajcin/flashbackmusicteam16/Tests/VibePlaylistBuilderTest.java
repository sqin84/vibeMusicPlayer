package com.example.ajcin.flashbackmusicteam16.Tests;

import android.support.test.rule.ActivityTestRule;

import com.example.ajcin.flashbackmusicteam16.Main_Activity;
import com.example.ajcin.flashbackmusicteam16.Play;
import com.example.ajcin.flashbackmusicteam16.PopulateMusic;
import com.example.ajcin.flashbackmusicteam16.Song;
import com.example.ajcin.flashbackmusicteam16.TimeMachine;
import com.example.ajcin.flashbackmusicteam16.VibePlayListBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by luke on 3/15/2018.
 */

public class VibePlaylistBuilderTest {
    VibePlayListBuilder builder;
    PopulateMusic p;
    List<String> contacts;
    LinkedList<Song> songs;
    ArrayList<Play> plays;
    Play play_one;
    Play play_two;
    Play play_three;

//    @Rule
//    public ActivityTestRule<Main_Activity> mainActivity = new ActivityTestRule<Main_Activity>(Main_Activity.class);

    @Before
    public void setUp(){
//        p = new PopulateMusic(mainActivity.getActivity());
        contacts = new ArrayList<String>();
        play_one = new Play();
        play_two = new Play();
        play_three = new Play();
        contacts.add("Juke Lervis");
        play_one.setAddress("test location");
        play_one.setSongName("test_song_one");
        play_one.setLatitude(1.0);
        play_one.setLongitude(2.0);
        play_one.setTime("2017-04-22T12:30");
        play_one.setUser(contacts.get(0));
        play_two.setAddress("test location");
        play_two.setSongName("test_song_two");
        play_two.setLatitude(1.0);
        play_two.setLongitude(2.0);
        play_two.setTime("2017-04-22T12:30");
        play_two.setUser("Caron Aintron");
        play_three.setAddress("test location");
        play_three.setSongName("test_song_three");
        play_three.setLatitude(1.0);
        play_three.setLongitude(2.0);
        play_three.setTime("2017-04-01T12:30");
        play_three.setUser("Qida Sin");
        LocalDateTime dummyTime = LocalDateTime.of(2017, 04, 20, 12, 30);
        TimeMachine.useFixedClockAt(dummyTime);
        builder = new VibePlayListBuilder(null,contacts);
        builder.addPlay(play_three);
        builder.addPlay(play_two);
        builder.addPlay(play_one);
    }
    @Test
    public void readyScores(){
        builder.readyScores();
        assertTrue(play_one.score[0]==1);
        assertTrue(play_one.score[1]==1);
        assertTrue(play_one.score[2]==1);
        assertTrue(play_two.score[0]==1);
        assertTrue(play_two.score[1]==1);
        assertTrue(play_two.score[2]==0);
        assertTrue(play_three.score[0]==1);
        assertTrue(play_three.score[1]==0);
        assertTrue(play_three.score[2]==0);

        plays = builder.getPlay();
        Play play_test_one = plays.get(0);
        Play play_test_two = plays.get(1);
        Play play_test_three = plays.get(2);
        assertTrue(play_test_one.getUser().equals(play_one.getUser()));
        assertTrue(play_test_two.getUser().equals(play_two.getUser()));
        assertTrue(play_test_three.getUser().equals(play_three.getUser()));
    }


}
