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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;

import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.FilesManager;
import com.example.vinylmusicplayer.classes.MusicManager;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.helpers.DateHelper;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SongPageFragment extends Fragment {

    SongsListAdapter songsListAdapter;
    ListViewModel model;
    OnSongItemClicked songItemClicked;
    RecyclerView recyclerView;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    AlertDialog popupAddToPlaylist;
    private boolean isToRemoveMusicBar;
    private MusicManager musicManager;

    public interface OnSongItemClicked {
        void onClick(String id, List<Song> playlist);
    }

    public SongPageFragment(ListViewModel model, OnSongItemClicked songItemClicked) {
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
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeFragment.CoverAlbumChanged event) {
        List<String> allRowToChange = model.getSongKey(model.getSongsAlbum(), event.id);

        for (String id : allRowToChange) {
            int rowId = model.getSongPositionById(id);
            songsListAdapter.updateCoverImage(rowId, event.drawable);
            songsListAdapter.updateArtistName(rowId, model.getArtistFromId(model.getSongsArtist().get(id)).getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeFragment.QuerySearch event) {
        Log.d("NUMBER", event.page + "ss");
        if (event.page == 0) {
            songsListAdapter.filter(model.getSongs().getValue(), event.query);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerView);
        musicManager = MusicManager.getInstance();
        if (model.getSongs().getValue().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        }
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
                    this.songItemClicked.onClick(songsListAdapter.getCurrentSongs().get(rowPos).getId(), null);
                }, (position, itemClicked) -> {
            Song song = model.getSongs().getValue().get(position);
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

                        if (musicManager.getCurrentSong().getId().equals(song.getId())) {
                            Toast.makeText(getContext(), "La canzone è in riproduzione", Toast.LENGTH_SHORT).show();
                        } else {
                            FilesManager.deleteFile(song.getUri(), getContext());

                            model.removeSong(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.detailsSong:
                    showSongDetails(song);
                    break;

            }
        });
        recyclerView.setAdapter(songsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateSongsList();

    }
    public void showSongDetails(Song song) {
        final View detailsView = getLayoutInflater().inflate(R.layout.details, null);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setView(detailsView);
        AlertDialog popupDetails = materialAlertDialogBuilder
                .create();
        popupDetails.setCancelable(false);
        popupDetails.show();
        TextView textView = detailsView.findViewById(R.id.detailsText);
        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Nome: ").append(song.getTitle()).append("\n")
                .append("Percorso: ").append(song.getUri().getPath()).append("\n")
                .append("Tempo riproduzione: ").append(DateHelper.longToFormatSongTime(song.getDuration()));
        textView.setText(detailsText.toString());
        detailsView.findViewById(R.id.closePopup).setOnClickListener(l -> popupDetails.dismiss());
    }
    private void updateSongsList() {
        model.getSongs().observe(getViewLifecycleOwner(), songList -> {
            songsListAdapter.setData(songList, true);
        });
    }

}
