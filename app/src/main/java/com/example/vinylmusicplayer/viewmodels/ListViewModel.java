package com.example.vinylmusicplayer.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.FilesManager;
import com.example.vinylmusicplayer.classes.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Artist>> artists = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Album>> album = new MutableLiveData<>(new ArrayList<>());
    private Map<String, String> songsAlbum = new HashMap<>();
    private Map<String, String> songsArtist = new HashMap<>();

    private FilesManager filesManager;

    public Map<String, String> getSongsAlbum() {
        return songsAlbum;
    }

    public Map<String, String> getSongsArtist() {
        return songsArtist;
    }

    public ListViewModel(@NonNull Application application) {
        super(application);
        filesManager = new FilesManager();
    }

    public void linkSongAlbum(String songId, String albumId) { //ogni canzone ha il suo album
        songsAlbum.put(songId, albumId);
    }

    public void linkSongArtist(String songId, String artistId) {
        songsArtist.put(songId, artistId);
    }

    public <K, V> List<K> getSongKey(Map<K, V> map, V value) //get all songs from album
    {
        List<K> list = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public LiveData<List<Song>> retrieveAllSongsInFolder(String folder) {
        Set<String> filesKeySet = filesManager.allSongsInFolder(folder); //assume absolute path /storage/emulated/0
        List<Song> list = new ArrayList<>();
        for (String s : filesKeySet) {
            File f = filesManager.getAudioFIleById(s); //audio file must only retrive by id
            list.add(new Song(s, filesManager.truncateExtension(f.getName()), Uri.fromFile(f)));
        }
        songs.setValue(list);
        return songs;
    }

    public LiveData<List<Song>> getSongs() {

            return songs;

    }

    public Song getSongById(String id) {
        return songs.getValue().stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public List<Song> getSongsByListId(List<String> ids) {
        List<Song> songList = new ArrayList<>();
        for (String id : ids) {
            for (Song song : songs.getValue()) {
                if (id.compareTo(song.getId()) == 0) {
                    songList.add(song);
                    break;
                }

            }
        }
        return songList;


}

    public List<String> allSongsName() {
        List<Song> songs2 = retrieveAllSongsInFolder("Music").getValue();
        return songs2.stream().map(Song::getTitle).collect(Collectors.toList());
    }

    public void setArtistsValue(List<Artist> listArtist) {
        this.artists.postValue(listArtist);
    }

    public void setAlbumValue(List<Album> album) {
        this.album.postValue(album);
    }

    public LiveData<List<Artist>> getArtists() {
        return artists;
    }

    public LiveData<List<Album>> getAlbum() {
        return album;
    }

    public Artist getArtistByPosition(int position) {
        return artists.getValue().get(position);
    }

    public LiveData<List<Song>> getPlaylist(List<String> ids) {
        List<Song> playlist = new ArrayList<>();
        for (Song song : songs.getValue()) {
            for (String id : ids) {
                if (song.getId().equals(id)) {
                    playlist.add(song);
                }
            }
        }
        return new MutableLiveData<>(playlist);

    }

    public List<Song> allSongsFromArtist(Artist a) {
        List<String> songsId = a.getSongsId();
        return songsId.stream().map(this::getSongById).collect(Collectors.toList());
    }

    public Artist getArtistFromId(String id) {
        return artists.getValue().stream().filter(a -> a.getId() == id).collect(Collectors.toList()).get(0);
    }

    public int getSongPositionById(String id) {
        int position = 0;
        for (Song song : songs.getValue()) {
            if (song.getId().compareTo(id) == 0) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public void removeSong(int index) {
        if (songs != null && songs.getValue().size() > 0) {
            songs.getValue().remove(index);
        }
    }
}
