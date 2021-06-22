package com.example.vinylmusicplayer.classes;

import android.graphics.drawable.Drawable;

import java.util.List;

public class Album {
    private String id;
    private String title;
    private int numSong;
    private String urlImage;
    private List<String> songsId;
    private String artistId;
    private Drawable coverImage;

    public Album(String id, String title, String urlImage) {
        this.id = id;
        this.title = title;
        this.urlImage = urlImage;
    }
    public String getId() {
        return id;
    }

    public Drawable getCoverImage() {
        return coverImage;
    }
    public void setCoverImage(Drawable coverImage) {
        this.coverImage = coverImage;
    }


    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public List<String> getSongsId() {
        return songsId;
    }

    public void setSongsId(List<String> songsId) {
        this.songsId = songsId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public int getNumSong() {
        return numSong;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
