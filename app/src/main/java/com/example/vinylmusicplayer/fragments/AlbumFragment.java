package com.example.vinylmusicplayer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.helpers.DateHelper;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListViewModel model;
    private SongPageFragment.OnSongItemClicked onSongItemClicked;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    AppCompatImageView coverImageView;

    public AlbumFragment(ListViewModel model, SongPageFragment.OnSongItemClicked onSongItemClicked) {
        this.model = model;
        this.onSongItemClicked = onSongItemClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.appBarLayout = view.findViewById(R.id.app_bar);
        this.collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        this.coverImageView = view.findViewById(R.id.coverImageViewId);
        AppCompatTextView numSongText = view.findViewById(R.id.albumInfo);
        AppCompatTextView authorText = view.findViewById(R.id.author);
        AppCompatTextView durationAlbumText = view.findViewById(R.id.totalAlbumTime);
        Bundle args = getArguments();
        String id = args.getString("albumId");
        Album album = model.getAlbumById(id);
        coverImageView.setImageDrawable(album.getCoverImage());
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        List<String> list = model.getSongKey(model.getSongsAlbum(), id);
        Artist artist = model.getArtistFromSongId(list.get(0));
        numSongText.setText(String.valueOf(album.getNumSong()).concat(" brani"));
        authorText.setText(artist.getName());
        durationAlbumText.setText(DateHelper.longToFormatSongTime(album.getTotalDuration()));
        toolbar.setNavigationOnClickListener(l -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        collapsingToolbarLayout.setTitle(album.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbar.setTitle(album.getTitle());
//        String iii = album.getTitle();
//        toolbar.setTitleTextColor(Color.WHITE);
        SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), position -> {
            this.onSongItemClicked.onClick(list.get(position),null);
        }, null);
        recyclerView.setAdapter(songsListAdapter);
        songsListAdapter.setData(model.getSongsByListId(list),true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
