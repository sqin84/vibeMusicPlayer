package com.example.ajcin.flashbackmusicteam16;

import android.util.Log;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sqin8 on 3/8/2018.
 */

public class VibePlayListBuilder implements PlayListBuilder {
    ArrayList<Play> plays = new ArrayList<>();
    List<String> contacts;
    LinkedList<Song> songs;
    PopulateMusic p;

    public VibePlayListBuilder(PopulateMusic p, List<String> c){
        this.p = p;
        this.contacts = c;
    }
    public void readyScores() {


        // Priority is given to a track based on first (a) whether it was played
        // near the user's present location, second (b) whether it was played in the last week,
        // and third (c) whether it was played by a friend.  When multiple of these factors are present,
        // each is given equal weight in producing the ordering of tracks.
        // Ties are broken according to the (a)-to-(c) ordering of the preceding criteria.

        // so, each play/song will have a score out of 3. a,b,c will each contribute one point.
        // when there is a tied score between two songs, then we break ties accordingly. i.e ab outweighs bc.
        // if a track was played in the last week and played by a friend (3), then we choose the most recently played.

        for(Play play:plays){
            play.score[0] = 1;
            LocalDateTime playedTime = LocalDateTime.parse(play.getTime());
            if(Math.abs(TimeMachine.now().toLocalDate().compareTo(playedTime.toLocalDate()))<= 7){
                play.score[1] = 1;
            }else{
                play.score[1] = 0;
            }
            if(contacts.contains(play.getUser())){
                play.score[2] = 1;
            }else{
                play.score[2] = 0;
            }
        }
        Collections.sort(plays, new Comparator<Play>() {
            @Override
            public int compare(Play play1, Play play2) {
                if(play1.score[0] + play1.score[1] + play1.score[2] <  play2.score[0] + play2.score[1] + play2.score[2] ){
                    return 1;
                }else if(play1.score[0] + play1.score[1] + play1.score[2] >  play2.score[0] + play2.score[1] + play2.score[2]){
                    return -1;
                }else{
                    if(play1.score[1] == 1 && play2.score[1] == 0){
                        return -1;
                    }else if(play1.score[1] == 0 && play2.score[1] == 1){
                        return 1;
                    }
                    else {
                        return Math.abs(LocalDateTime.parse(play1.getTime()).compareTo(TimeMachine.now()))
                                - Math.abs(LocalDateTime.parse(play2.getTime()).compareTo(TimeMachine.now()));
                    }

                }
            }
        });
        // criteria a is already taken care, so all songs queried from firebase will have score of 1 already

    }

    private void isPlayedLastWeek(){};
    private void isPlayedByFriend(){};

    public void addPlay(Play s){
        plays.add(s);
    }
    public void settContacts(List<String> c){ contacts = c;}
    public LinkedList<Song> build() {
        // we will need to remove the duplicates to produce song list with unique elements
        for(int i =0; i < plays.size(); i++){
            for(int j = i + 1; j < plays.size()-1; j++){
                if(plays.get(i).getSongName() .equals(plays.get(j).getSongName())){
                    plays.remove(j);
                    j--;
                }
            }
        }

        songs = new LinkedList<>();
        // TODO apply download logic
        for(Play play:plays){

            Song s = p.getSong(play.getSongName());
            if(s != null){
                songs.add(s);
            }
        }
        return songs;
    }
}
