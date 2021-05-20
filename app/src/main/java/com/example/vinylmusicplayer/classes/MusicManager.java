package com.example.vinylmusicplayer.classes;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public  class MusicManager {
    private  List<Song> playList;
    private  Context context;
    private  int index;
    private  MediaPlayer mediaPlayer;
    private boolean isPlaying;

    private  void create() {
        mediaPlayer = MediaPlayer.create(context, playList.get(index).getUri());
    }

    public MusicManager(Context context) {
        index = 0;
        this.context = context;
        this.isPlaying = false;

    }

    public  void attachList(List<Song> playList) {
        this.playList = playList;
        create();
    }

    public void play() {
        mediaPlayer.start();
        isPlaying = true;
    }

    public void stop() {
        mediaPlayer.stop();
        this.isPlaying = false;
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
    }

}
