package com.example.vinylmusicplayer.classes;

import com.example.vinylmusicplayer.helpers.DateHelper;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Playlist {
    String id;
    String name;
    int numSong;
    Date date;
    @Expose(serialize = false)
    private ArrayList<Song> playlistSong;

    public Playlist(String id, String name) {
        this.id = id;
        this.name = name;
        this.numSong = 0;
        playlistSong = new ArrayList<>();
        date = DateHelper.getCurrentDateTime();
    }

    public Date getDate() {
        return date;
    }

    public void set(List<Song> playlist) {
        this.numSong = playlist.size();
        playlistSong = new ArrayList<>(playlist);
    }


    public String getName() {
        return name;
    }

    public int getNumSong() {
        return numSong;
    }

    public ArrayList<Song> getPlaylistSong() {
        return playlistSong;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalPlaylistDuration() {
        int duration=0;
        for(Song song : playlistSong){
            duration+=song.getDuration();
        }
        return duration;
    }

    public String getId() {
        return id;
    }
}
