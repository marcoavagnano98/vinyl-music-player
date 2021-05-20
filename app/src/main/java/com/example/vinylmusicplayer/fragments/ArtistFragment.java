package com.example.vinylmusicplayer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.PlayActivity;
import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {
    ListViewModel model;

    public ArtistFragment(ListViewModel model) {
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        super.onViewCreated(view, savedInstanceState);
        int index = args.getInt("position");
        Artist a = model.getArtistByPosition(index);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.setTitle(a.getName());
        toolbar.setTitleTextColor(Color.WHITE);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);

        SongsListAdapter adapter=new SongsListAdapter(getActivity(), new SongsListAdapter.OnRVItemListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(getActivity().getApplicationContext(), PlayActivity.class);
                i.putExtra("artistId", a.getId());
                i.putExtra("aIndex",position);
                getActivity().startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
        model.getPlaylist(a.getSongsId()).observe(getActivity(),songList -> {
            adapter.setData(songList);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //   view.setSupportActionBar(toolbar);

        // add back arrow to toolbar
//        if (getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//        final Activity activity = getActivity();

    }

}
