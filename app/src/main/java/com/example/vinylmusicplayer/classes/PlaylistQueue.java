package com.example.vinylmusicplayer.classes;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class PlaylistQueue {
    List<Song> playQueue;
    public PlaylistQueue(List<Song> queue){
        this.playQueue=queue;
    }

    public List<Song> getPlayQueue() {
        return playQueue;
    }
    public void swapElements(int source, int dest){
        Collections.swap(playQueue,source,dest);
    }
}
