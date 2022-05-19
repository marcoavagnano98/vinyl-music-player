package com.example.vinylmusicplayer.classes;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.SystemClock;

import java.util.List;

public class MusicManager implements MediaPlayer.OnCompletionListener {
    private static MusicManager instance;
    private List<Song> playList;
    private Context context;
    private int index;
    private MediaPlayer mediaPlayer;
    private int songDuration;
    private boolean isCurrentActive = false;
    private Song currentSong;
    private long mLastStopTime;
    private OnSongCompletion onSongCompletion;
    private boolean isCompleted;


    public static MusicManager getInstance() { //singleton pattern
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCurrentPosition(int progress){
        mediaPlayer.seekTo(progress);
    }
    public List<Song> getPlayList() {
        return playList;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
        this.onSongCompletion.isComplete(true);
    }

    public interface OnSongCompletion {
        void isComplete(boolean state);
    }

    public void setSongCompletionListener(OnSongCompletion onSongCompletion) {
        this.onSongCompletion = onSongCompletion;
    }

//    private void create() {
//        currentSong = playList.get(index);
//        mediaPlayer = MediaPlayer.create(context, currentSong.getUri());
//        this.isCompleted = false;
//        mediaPlayer.setOnCompletionListener(this);
//        songDuration = mediaPlayer.getDuration();
//    }

    private MusicManager() {
    }

    public void create(List<Song> playList, int index) {
        this.index = index;
        this.playList = playList;
        setMediaPlayer();
    }
    public void changeQueue(List<Song> queue){
        this.playList=queue;
        this.index=queue.lastIndexOf(currentSong);
    }

    private void setMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        currentSong = playList.get(index);
        
        mediaPlayer = MediaPlayer.create(this.context, currentSong.getUri());
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        this.isCompleted = false;
        mediaPlayer.setOnCompletionListener(this);
        songDuration = mediaPlayer.getDuration();
    }


    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isCurrentlyActive() {
        return isCurrentActive;
    }

    public void setCurrentActive(boolean status) {
        this.isCurrentActive = status;
    }

    public void play() {
        mediaPlayer.start();
    }

    public int getDuration() {
        return songDuration;
    }

    public int getCurrentTimeElapsed() {
        return mediaPlayer.getCurrentPosition();
    }


    public void next() {
        if (index < playList.size() - 1) {
            index++;
        }
        setMediaPlayer();
        play();
    }

    public void prev() {
        if (index > 0) {
            index--;
        }
        setMediaPlayer();
        play();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        mediaPlayer.pause();

        mLastStopTime = SystemClock.elapsedRealtime();

    }


}
