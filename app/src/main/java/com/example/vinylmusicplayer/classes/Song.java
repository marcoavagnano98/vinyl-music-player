package com.example.vinylmusicplayer.classes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Parcelable  {
    private String id;
    private int idArtist;
    private int idAlbum;
    private Uri uri;
    private String type;
    private String title;
      public Song(String id,  String title, Uri uri){
          this.id = id;
          this.title = title;
          this.uri=uri;
      }

    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        uri =Uri.parse(in.readString());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.uri.toString());
    }

}
