package com.example.vinylmusicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.ArtistListAdapter;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.List;

public class ArtistListFragment extends Fragment {
    private final ListViewModel model;
    private SongListFragment.OnSongItemClicked songItemClicked;

    public ArtistListFragment(ListViewModel model, SongListFragment.OnSongItemClicked songItemClicked) {
        this.model = model;
        this.songItemClicked = songItemClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ArtistListAdapter adapter = new ArtistListAdapter(new OnRVItemListener() {
            @Override
            public void onItemClick(int position) {
                Fragment fragment = new ArtistFragment(model, songItemClicked);
                Bundle args = new Bundle();
                args.putInt("position", position);
                fragment.setArguments(args);
                Utils.insertFragment((AppCompatActivity) getActivity(), fragment, "ArtistFragment");
            }
        });
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView1, int newState) {
                super.onScrollStateChanged(recyclerView1, newState);

                if (!recyclerView1.canScrollVertically(1) && recyclerView1.canScrollVertically(-1)) {
                    if (sharedPref.getBoolean("musicBarActive", false)) {
                        HomeFragment.musicBarLayout.setVisibility(View.GONE);
                    }

                } else {

                    if (sharedPref.getBoolean("musicBarActive", false)) {
                        HomeFragment.musicBarLayout.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setData(model.getArtists().getValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
