package com.example.vinylmusicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.fragments.AlbumListFragment;
import com.example.vinylmusicplayer.fragments.ArtistListFragment;
import com.example.vinylmusicplayer.fragments.SongListFragment;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.List;


public class ViewPagerAdapter extends FragmentStateAdapter {
    ListViewModel model;
    SongListFragment.OnSongItemClicked songItemClicked;



    public ViewPagerAdapter(Fragment fragment, ListViewModel model, SongListFragment.OnSongItemClicked songItemClicked) {
        super(fragment);
        this.model = model;
        this.songItemClicked = songItemClicked;

    }

    public void setDynamicData(List<Artist> artists, List<Album> albums) {
        model.setAlbumValue(albums);
        model.setArtistsValue(artists);
        createFragment(1);
        createFragment(2);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new SongListFragment(model, songItemClicked);
                break;
            case 1:
                fragment = new ArtistListFragment(model);
                break;
            case 2:
                fragment = new AlbumListFragment(model);
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
