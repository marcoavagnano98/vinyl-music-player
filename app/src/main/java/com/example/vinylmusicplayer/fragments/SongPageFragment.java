package com.example.vinylmusicplayer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;

import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.FilesManager;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SongListFragment extends Fragment {

    SongsListAdapter songsListAdapter;
    ListViewModel model;
    OnSongItemClicked songItemClicked;
    RecyclerView recyclerView;
    private boolean isToRemoveMusicBar;

    public interface OnSongItemClicked {
        void onClick(String id);
    }

    public SongListFragment(ListViewModel model, OnSongItemClicked songItemClicked) {
        this.model = model;
        this.songItemClicked = songItemClicked;
        this.isToRemoveMusicBar = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeFragment.CoverAlbumChanged event) {
        List<String> allRowToChange = model.getSongKey(model.getSongsAlbum(), event.id);

        for (String id : allRowToChange) {
            int rowId=model.getSongPositionById(id);
            songsListAdapter.updateCoverImage(rowId, event.drawable);
            songsListAdapter.updateArtistName(rowId,event.artistName);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerView);
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

        songsListAdapter = new SongsListAdapter(getActivity(),
                rowPos -> {
                    this.songItemClicked.onClick(model.getSongs().getValue().get(rowPos).getId());
                }, new SongsListAdapter.OnOptionMenuListener() {
            @Override
            public void onItemClick(int position, int itemClicked) {
                switch (itemClicked) {
                    case R.id.shareSong:
                        try {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.setType("audio/*");
                            model.getSongs().observe(getViewLifecycleOwner(), sharingSong -> {
                                sendIntent.putExtra(Intent.EXTRA_STREAM, sharingSong.get(position).getUri());
                                String title = "Condividi: ".concat(sharingSong.get(position).getTitle());
                                Intent shareIntent = Intent.createChooser(sendIntent, title);
                                getActivity().startActivity(shareIntent);
                            });


                        } catch (NoSuchMethodError | IllegalArgumentException | NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.deleteSong:
                        try {
                            FilesManager.deleteFile(model.getSongs().getValue().get(position).getUri(), getContext());
                            model.removeSong(position);
                            songsListAdapter.notifyItemRemoved(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
        recyclerView.setAdapter(songsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateSongsList();

    }

    private void updateSongsList() {
        model.getSongs().observe(getViewLifecycleOwner(), songList -> {
            songsListAdapter.setData(songList);
        });
    }
}
