package com.example.vinylmusicplayer.classes;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public  class MusicManager {
    private  List<Song> playList;
    private  Context context;
    private  int index;
    private  MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int songDuration;
    private boolean isCurrentActive=false;

    private  void create() {
        mediaPlayer = MediaPlayer.create(context, playList.get(index).getUri());
        songDuration=mediaPlayer.getDuration();
    }

    public MusicManager(Context context) {
        index = 0;
        this.context = context;
        this.isPlaying = false;
        this.playList=new ArrayList<>();

    }

    public  void attachList(List<Song> playList) {
        this.playList = playList;
        create();
    }
    public void attachSong(Song song){

    }

    public boolean isCurrentlyActive() {
        return isCurrentActive;
    }

    public void setCurrentActive(boolean status){
        this.isCurrentActive=status;
    }
    public void play() {
        mediaPlayer.start();
        isPlaying = true;

    }
    public int getDuration(){
       return songDuration;
    }

    public void stop() {
        mediaPlayer.stop();

    }

    public void next() {
        if (index < playList.size() - 1) {
            index++;
        }
        mediaPlayer.reset();
        create();
        play();
    }

    public void next(int index) {
        if (index <= playList.size() - 1) {
            this.index = index;
        }
        mediaPlayer.reset();
        create();
        play();
    }

    public void prev() {
        if (index > 0) {
            index--;
        }
        mediaPlayer.reset();
        create();
        play();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    public void pause(){
        mediaPlayer.pause();
        this.isPlaying = false;
    }

}
