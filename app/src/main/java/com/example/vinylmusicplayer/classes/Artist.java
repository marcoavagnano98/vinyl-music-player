package com.example.vinylmusicplayer.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class  Artist {
    private String id;
    private String name;
    private List<String> songsId;

    public Artist(String id, String name) {
        this.id=id;
        this.name=name;
        songsId=new ArrayList<>();
    }
    public void addSongId(String id){
        songsId.add(id);
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public List<String> getSongsId(){
        return songsId;
    }

}
