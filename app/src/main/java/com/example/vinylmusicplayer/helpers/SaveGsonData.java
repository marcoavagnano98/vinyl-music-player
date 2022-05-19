package com.example.vinylmusicplayer.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import androidx.annotation.NonNull;

import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.classes.Song;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

public class SaveGsonData {

    public static void save(Activity activity, List<Playlist> playlist) {
        SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.deserialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .create();
        Set<String> set = new ArraySet<>();
        for (Playlist p : playlist) {
            String json = gson.toJson(p);
            set.add(json);
        }
        prefsEditor.putStringSet("allPlaylists", set);
        prefsEditor.apply();
    }
    public static void savePlaylistItems(Activity activity,Set<String> set, String pId){
        SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putStringSet(pId,set);
       // prefsEditor.putInt(pId,set.size());
        prefsEditor.apply();
    }
    public static Set<String> retrievePlaylistItems(Activity activity,String pName){
        SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
        Set<String> set = mPrefs.getStringSet(pName, new ArraySet<>());
        return set;
    }
    public static void removeItem(Activity activity, String pId){
        SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove(pId);
        prefsEditor.apply();
    }

    public static Map<Playlist,List<String>> retrieve(Activity activity) {
        SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        Map<Playlist,List<String>> allPlaylistData=new HashMap<>();
        List<Playlist> playlists = new ArrayList<>();
        Set<String> set = mPrefs.getStringSet("allPlaylists", new ArraySet<>());
        Iterator<String> iterator=set.iterator();
        for (int i=0;i<set.size(); i++) {
            Playlist playlist=gson.fromJson(iterator.next(), Playlist.class);
            allPlaylistData.put(playlist,new ArrayList<>(retrievePlaylistItems(activity,playlist.getId())));

        }
        return allPlaylistData;
    }


}
