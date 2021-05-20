package com.example.vinylmusicplayer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vinylmusicplayer.backend.SpotifyDataRetriever;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.fragments.DemoObjectFragment;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewPagerAdapter extends FragmentStateAdapter {
    ArrayList<Song> list1;
    ArrayList<Artist> list2;
    Map<Artist,List<Song>> map;
    ListViewModel model;

    public ViewPagerAdapter(Fragment fragment,ListViewModel model) {
        super(fragment);
      //  this.list1 = list1;
        this.list2=new ArrayList<>();
        map=new HashMap<>();
        this.model=model;
    }

    public void setDynamicData(List<Artist> artist, int type) {
        model.setArtistsValue(artist);
        createFragment(type);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new DemoObjectFragment(model);
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


// Instances of this class are fragments representing a single
// object in our collection.
