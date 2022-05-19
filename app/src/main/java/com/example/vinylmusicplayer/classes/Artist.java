package com.example.vinylmusicplayer.classes;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Artist {
    private String id;
    private String name;
    private String imageUrl;

    private Drawable coverImage;
    private int numSong;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;

    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setCoverImage(Drawable coverImage) {
        this.coverImage = coverImage;
    }

    public void incNumSong() {
        this.numSong++;
    }

    public int decNumSong() {
        this.numSong--;
        return numSong;
    }

    public int getNumSong() {
        return numSong;
    }

    public Drawable getCoverImage() {
        return coverImage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coverImage, numSong);
    }
}
