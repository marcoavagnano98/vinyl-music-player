package com.example.vinylmusicplayer.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vinylmusicplayer.backend.SpotifyDataRetriever;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.FilesManager;
import com.example.vinylmusicplayer.classes.RandomString;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.classes.TaskRunner;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Artist>> artists;
    private MutableLiveData<List<Album>> album = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Song>> currentPlayQueue =new MutableLiveData<>(new ArrayList<>());
    private Map<String, String> songsAlbum = new HashMap<>();
    private Map<String, String> songsArtist = new HashMap<>();
    private TaskRunner taskRunner;
    private MutableLiveData<List<String[]>> spotifyData;
    private MutableLiveData<List<String[]>> artistSpotifyData;
    private FilesManager filesManager;

    private RandomString rndString = new RandomString();

    public Map<String, String> getSongsAlbum() {
        return songsAlbum;
    }

    public Map<String, String> getSongsArtist() {
        return songsArtist;
    }

    public ListViewModel(@NonNull Application application) {
        super(application);
        filesManager = new FilesManager();
        taskRunner = new TaskRunner();

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

    public MutableLiveData<List<String[]>> getArtistSpotifyData() {
        return artistSpotifyData;
    }
    public MutableLiveData<List<Song>> getCurrentPlayQueue(){
        return currentPlayQueue;
    }
    public void setCurrentPlayQueue(List<Song> queue){
        currentPlayQueue.setValue(queue);
    }
    public void setPostCurrentPlayQueue(List<Song> queue){
        currentPlayQueue.postValue(queue);
    }

    public boolean retrieveAllSongsInFolder(String folder, Activity activity) {
        Set<String> filesKeySet = filesManager.allSongsInFolder(folder); //assume absolute path /storage/emulated/0
        List<Song> list = new ArrayList<>();
        boolean songsFound = false;
        for (String s : filesKeySet) {
            File f = filesManager.getAudioFIleById(s); //audio file must only retrive by id
            list.add(new Song(s, filesManager.truncateExtension(f.getName()), Uri.fromFile(f)));
            songsFound = true;
        }
        songs.setValue(list);
        loadSpotifyData(activity);
        return songsFound;
    }

    public void loadArtists(Activity activity) {
        List<Artist> temp = new ArrayList<>();
        if (artistSpotifyData == null) {
            artistSpotifyData = new MutableLiveData<>();
        }
        for (String[] dataLine : spotifyData.getValue()) {
            Artist artist = new Artist(rndString.nextString(), dataLine[0]);
            int occOf = temp.lastIndexOf(artist);
            if (occOf != -1) {
                temp.get(occOf).incNumSong();
                linkSongArtist(dataLine[3], temp.get(occOf).getId());
            } else {
                artist.incNumSong();
                linkSongArtist(dataLine[3], artist.getId());
                temp.add(artist);
            }
        }
        taskRunner.executeAsync(new SpotifyDataRetriever(activity, getArtistQueryData(temp)), (data) -> {
            artistSpotifyData.setValue(data);
            for (Artist artist : temp) {
                for (String[] line : artistSpotifyData.getValue()) {
                    if (line[1].equals(artist.getId())) {
                        artist.setImageUrl(line[0]);
                        break;
                    }
                }
            }
            //call spotify data for artist cover
            artists.setValue(temp);
        });

    }

    public void loadAlbum() {
        List<Album> temp = new ArrayList<>();
        for (String[] dataLine : spotifyData.getValue()) {
            String songId = dataLine[3];
            int currentSongDuration = getSongById(songId).getDuration();
            Album album = new Album(rndString.nextString(), dataLine[1], dataLine[2]);
            int occOf = temp.lastIndexOf(album);
            if (occOf != -1) {
                temp.get(occOf).incNumSong();
                temp.get(occOf).setTotalDuration(currentSongDuration);
                linkSongAlbum(songId, temp.get(occOf).getId());
            } else {
                album.setTotalDuration(currentSongDuration);
                album.incNumSong();
                linkSongAlbum(songId, album.getId());
                temp.add(album);
            }
        }
        album.setValue(temp);
    }

    public MutableLiveData<List<String[]>> getSpotifyData() {
        if (spotifyData == null) {
            spotifyData = new MutableLiveData<>();
        }
        return spotifyData;
    }

    private void loadSpotifyData(Activity activity) {

        taskRunner.executeAsync(new SpotifyDataRetriever(activity, getSongQueryData()), (data) -> {
            spotifyData.setValue(data);
        });
    }

    public Map<String, String> getSongQueryData() {
        Map<String, String> queryData = new HashMap<>();
        List<Song> songs = getSongs().getValue();
        for (Song song : songs) {
            try {
                queryData.put(song.getId(), URLEncoder.encode(song.getTitle(), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException err) {
                err.printStackTrace();
            }
        }
        return queryData;
    }

    public Map<String, String> getArtistQueryData(List<Artist> temp) {
        Map<String, String> queryData = new HashMap<>();
        for (Artist artist : temp) {
            queryData.put(artist.getId(), artist.getName());
        }
        return queryData;
    }


    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public Song getSongById(String id) {
        return songs.getValue().stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public Album getAlbumById(String id) {
        return album.getValue().stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList()).get(0);
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

    public List<Song> getSongsByListUri(List<String> uri) {

        List<Song> songList = new ArrayList<>();
        for (String u : uri) {
            for (Song song : songs.getValue()) {
                if (u.compareTo(song.getUri().toString()) == 0) {
                    songList.add(song);
                    break;
                }

            }
        }
        return songList;
    }

    public void setArtistsValue(List<Artist> listArtist) {
        this.artists.postValue(listArtist);
    }

    public void setAlbumValue(List<Album> album) {
        this.album.postValue(album);
    }

    public LiveData<List<Artist>> getArtists() {
        //  return new String[]{artistName, albumName, imageUrl, songId};
        if (artists == null) {
            artists = new MutableLiveData<>();
        }
        return artists;
    }

    public LiveData<List<Album>> getAlbum() {
        if (album == null) {
            album = new MutableLiveData<>();
        }
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

    public Artist getArtistFromSongId(String id) {
        return getArtistFromId(songsArtist.get(id));
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
            List<Song> array=new ArrayList<>(songs.getValue());
            array.remove(index);
            String songId = songs.getValue().get(index).getId();
            Artist artist = getArtistFromSongId(songId);
            int aIndex = artists.getValue().lastIndexOf(artist);
            int numSong = artists.getValue().get(aIndex).decNumSong();
            if (numSong <= 0) {
                artists.getValue().remove(aIndex);
            }
            Album alb = getAlbumById(getSongsAlbum().get(songId));
            int albIndex = album.getValue().lastIndexOf(alb);
            int numAlbumSong = album.getValue().get(albIndex).decNumSong();
            if (numAlbumSong <= 0) {
                album.getValue().remove(albIndex);
            }
            songs.setValue(array);
        }
    }
}
