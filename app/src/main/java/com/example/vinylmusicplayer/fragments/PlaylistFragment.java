package com.example.vinylmusicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.AddToPlaylistAdapter;
import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.helpers.DateHelper;
import com.example.vinylmusicplayer.helpers.SaveGsonData;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.util.ErrorDialogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlaylistFragment extends Fragment {
    RecyclerView recyclerView;
    Playlist playlist;
    MaterialCardView materialCardView;
    MaterialToolbar materialToolbar;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    AlertDialog popupAddToPlaylist;
    ListViewModel model;
    FloatingActionButton playButton;


    private SongPageFragment.OnSongItemClicked songItemClicked;
//    Toolbar musicBar;
//    LinearLayout musicBarLayout;

    public PlaylistFragment(Playlist playlist, ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {
        this.playlist = playlist;
        this.model = model;
        this.songItemClicked = songItemClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist, container, false);
    }


//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.playlist_menu, menu);
//    }


    public void addToPlaylist() {
        final View view = getLayoutInflater().inflate(R.layout.add_to_playlist, null);
        RecyclerView recyclerView = view.findViewById(R.id.atpRecyclerView);
        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setView(view);
        popupAddToPlaylist = materialAlertDialogBuilder.create();
        popupAddToPlaylist.setCancelable(true);
        popupAddToPlaylist.show();

        List<Song> addableSong = new ArrayList<>(model.getSongs().getValue());
        List<Boolean> checkedList = new ArrayList<>(findSongInPlaylist(addableSong, playlist.getPlaylistSong()));

        AddToPlaylistAdapter addToPlaylistAdapter = new AddToPlaylistAdapter(pos -> {
            checkedList.set(pos, !checkedList.get(pos));
        });
        addButton.setOnClickListener(v -> {
            List<Song> app = new ArrayList<>();
            for (int i = 0; i < checkedList.size(); i++) {
                if (checkedList.get(i)) {
                    app.add(addableSong.get(i));
                }
            }
            playlist.set(app);
            SaveGsonData.savePlaylistItems(getActivity(), playlistSetUri(playlist), playlist.getId());
            setRecyclerView();
            popupAddToPlaylist.hide();

        });
        addToPlaylistAdapter.setData(addableSong, checkedList);
        recyclerView.setAdapter(addToPlaylistAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private Set<String> playlistSetUri(Playlist playlist) {
        return playlist.getPlaylistSong().stream().map(u -> u.getUri().toString()).collect(Collectors.toSet());
    }


    public List<Boolean> findSongInPlaylist(List<Song> addableList, List<Song> filterList) {
        List<Boolean> returnList = new ArrayList<>(Arrays.asList(new Boolean[addableList.size()]));
        Collections.fill(returnList, Boolean.FALSE);
        if (filterList.isEmpty()) {
            return returnList;
        } else {
            for (int i = 0; i < returnList.size(); i++) {
                if (filterList.contains(addableList.get(i))) {
                    returnList.set(i, true);
                }
            }
        }
        return returnList;
    }

    public void randomPlayPlaylist() {
        if (playlist.getPlaylistSong().size() > 0) {
            List<Song> list = new ArrayList<>(playlist.getPlaylistSong());

            Collections.shuffle(list); //randomize playlist
            songItemClicked.onClick(list.get(0).getId(), list);
            Utils.insertFragment((AppCompatActivity) getActivity(), new PlayFragment(getActivity(), model, songItemClicked), "PlayFragment");
        } else {
            Toast.makeText(getContext(), "Aggiungi almeno una canzone", Toast.LENGTH_LONG).show();
        }
    }

    public void showPlaylistDetails() {
        final View detailsView = getLayoutInflater().inflate(R.layout.details, null);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setView(detailsView);
        AlertDialog popupDetails = materialAlertDialogBuilder
                .create();
        popupDetails.setCancelable(false);
        popupDetails.show();
        TextView textView = detailsView.findViewById(R.id.detailsText);
        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Nome: ").append(playlist.getName()).append("\n")
                .append("Data di creazione: ").append(playlist.getDate()).append("\n")
                .append("Tempo totale di riproduzione: ").append(DateHelper.longToFormatSongTime(playlist.getTotalPlaylistDuration()));
        textView.setText(detailsText.toString());
        detailsView.findViewById(R.id.closePopup).setOnClickListener(l -> popupDetails.dismiss());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        playButton = view.findViewById(R.id.playButton);
        playButton.setScaleType(ImageView.ScaleType.CENTER);
        recyclerView = view.findViewById(R.id.recyclerViewPlaylist);
        TextView playlistTitleText = view.findViewById(R.id.titlePlaylist);
        materialToolbar = view.findViewById(R.id.toolbarTitle);

        playlistTitleText.setText(playlist.getName());
//        musicBar=view.findViewById(R.id.musicBar);
//        musicBarLayout = view.findViewById(R.id.musicBarLayout);
//        if (sharedPref.getBoolean("musicBarActive", false)) {
//            musicBarLayout.setVisibility(View.VISIBLE);
//        }
//        musicBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {


//            }
//        });
        if (!playlist.getPlaylistSong().isEmpty()) {
            setRecyclerView();
        }
        materialToolbar.inflateMenu(R.menu.playlist_menu);
        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.addToPlaylist) {
                addToPlaylist();
                return true;
            }
            if (item.getItemId() == R.id.randomPlay) {
                randomPlayPlaylist();
                return true;
            }
            if (item.getItemId() == R.id.details) {
                showPlaylistDetails();
                return true;
            }
            return false;
        });
//        materialToolbar.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                addToPlaylist();
//                return true;
//            }
//        });
//        materialToolbar.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                randomPlayPlaylist();
//                return true;
//            }
//        });
//        materialToolbar.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                showPlaylistDetails();
//                return true;
//            }
//        });
//
//                .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        playButton.setOnClickListener(v -> {
            if (playlist.getPlaylistSong().size() > 0) {
                songItemClicked.onClick(playlist.getPlaylistSong().get(0).getId(), playlist.getPlaylistSong());
                Utils.insertFragment((AppCompatActivity) getActivity(), new PlayFragment(getActivity(), model, songItemClicked), "PlayFragment");
            }else{
                Toast.makeText(getContext(),"Aggiungere almeno una canzone!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setRecyclerView() {
        SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), pos -> {
            songItemClicked.onClick(playlist.getPlaylistSong().get(pos).getId(), playlist.getPlaylistSong());
            Utils.insertFragment((AppCompatActivity) getActivity(), new PlayFragment(getActivity(), model, songItemClicked), "PlayFragment");
        }, null);
        songsListAdapter.setData(playlist.getPlaylistSong(), false);
        recyclerView.setAdapter(songsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
