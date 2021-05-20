package com.example.vinylmusicplayer.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.FilesManager;
import com.example.vinylmusicplayer.classes.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private MutableLiveData<List<Artist>> artists = new MutableLiveData<>(new ArrayList<>());
    private FilesManager filesManager;

    public ListViewModel(@NonNull Application application) {
        super(application);
        filesManager = new FilesManager();
    }

    public LiveData<List<Song>> retrieveAllSongsInFolder(String folder) {
        Set<String> filesKeySet = filesManager.allSongsInFolder(folder); //assume absolute path /storage/emulated/0
        List<Song> list = new ArrayList<>();
        for (String s : filesKeySet) {
            File f = filesManager.getAudioFIleById(s); //audio file must only retrive by id
            list.add(new Song(s, filesManager.truncateExtension(f.getName()), Uri.fromFile(f)));
        }
        songs.setValue(list);
        return getSongs();
    }

    public LiveData<List<Song>> getSongs() {
        return songs;
    }
    public Song getSongById(String id){
        return songs.getValue().stream().filter(a->a.getId().equals(id)).collect(Collectors.toList()).get(0);
    }
    public List<String> allSongsName() {
        List<Song> songs2 = retrieveAllSongsInFolder("Music").getValue();
        return songs2.stream().map(Song::getTitle).collect(Collectors.toList());
    }

    public void setArtistsValue(List<Artist> listArtist) {
        this.artists.postValue(listArtist);
    }

    public LiveData<List<Artist>> getArtists() {
        return artists;
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
    public List<Song> allSongsFromArtist(Artist a){
        List<String> songsId=a.getSongsId();
        return songsId.stream().map(this::getSongById).collect(Collectors.toList());
    }
    public Artist getArtistFromId(int id){
        return artists.getValue().stream().filter(a->a.getId() == id).collect(Collectors.toList()).get(0);
    }
}
