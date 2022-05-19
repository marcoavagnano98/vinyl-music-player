package com.example.vinylmusicplayer.classes;

import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Objects;

public class Album {
    private String id;
    private String title;
    private int numSong;
    private int totalDuration;
    private String urlImage;
    private List<String> songsId;
    private String artistId;
    private Drawable coverImage;

    public Album(String id, String title, String urlImage) {
        this.id = id;
        this.title = title;
        this.urlImage = urlImage;
        //  this.coverImage=coverImage;
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

    public void setTotalDuration(int totalDuration) {
        this.totalDuration += totalDuration;
    }

    public int getTotalDuration() {
        return totalDuration;
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

    public void incNumSong() {
        this.numSong++;
    }
    public int decNumSong(){
        this.numSong --;
        return numSong;
    }

    public String getUrlImage() {
        return urlImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(title, album.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, numSong, totalDuration, urlImage, songsId, artistId, coverImage);
    }
}
