package com.example.vinylmusicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vinylmusicplayer.fragments.AlbumPageFragment;
import com.example.vinylmusicplayer.fragments.ArtistPageFragment;
import com.example.vinylmusicplayer.fragments.PlaylistPageFragment;
import com.example.vinylmusicplayer.fragments.SongPageFragment;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;


public class ViewPagerAdapter extends FragmentStateAdapter {
    ListViewModel model;
    SongPageFragment.OnSongItemClicked songItemClicked;


    public ViewPagerAdapter(Fragment fragment, ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {
        super(fragment);
        this.model = model;
        this.songItemClicked = songItemClicked;

    }

    public void setDynamicData() {
        createFragment(1);
        createFragment(2);
    }

    public void setSearchFragment(int position) {
        createFragment(position);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new SongPageFragment(model, songItemClicked);
                break;
            case 1:
                fragment = new ArtistPageFragment(model, songItemClicked);
                break;
            case 2:
                fragment = new AlbumPageFragment(model, songItemClicked);
                break;
            case 3:
                fragment = new PlaylistPageFragment(model, songItemClicked);
                break;
            default:
                fragment = new Fragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


// Instances of this class are fragments representing a single
// object in our collection.
