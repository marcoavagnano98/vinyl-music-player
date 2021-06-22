package com.example.vinylmusicplayer.classes;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song {
    private final String id;
    private int idArtist;
    private int idAlbum;
    private final Uri uri;
    private String type;
    private final String title;
    private Drawable coverImage;

    public Song(String id, String title, Uri uri) {
        this.id = id;
        this.title = title;
        this.uri = uri;
    }

    public void setCoverImage(Drawable coverImage) {
        this.coverImage = coverImage;
    }

    public Drawable getCoverImage() {
        return coverImage;
    }

    public Uri getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }



}
