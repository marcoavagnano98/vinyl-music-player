package com.example.vinylmusicplayer.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.PlaylistAdapter;
import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.helpers.SwipeHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistFragment extends Fragment {
    private AlertDialog addPlaylistDialog;
    private AlertDialog confirmDeletingPlaylist;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    List<Playlist> allPlaylists;
    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;
    FloatingActionButton addPlaylistButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.playlistView);
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, 100) {
            @Override
            public void instantiateSwipedRegion(RecyclerView.ViewHolder viewHolder, List<SwipedRegion> buffer) {
                SwipedRegionListener removeListener = pos -> {
                    final View deletingPopUp = getLayoutInflater().inflate(R.layout.alert_deleting, null);
                    TextView header=deletingPopUp.findViewById(R.id.header);
                    TextView message=deletingPopUp.findViewById(R.id.message);
                    header.setText(R.string.delete_playlist);
                    message.setText("Vuoi veramente eliminare la playlist \"" + allPlaylists.get(0).getName() + "\"");
                    materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    materialAlertDialogBuilder.setView(deletingPopUp);
                    confirmDeletingPlaylist = materialAlertDialogBuilder.create();
                    confirmDeletingPlaylist.setCancelable(false);
                    confirmDeletingPlaylist.show();
                    deletingPopUp.findViewById(R.id.cancelDeleting).setOnClickListener(l -> {
                        confirmDeletingPlaylist.hide();
                        playlistAdapter.notifyDataSetChanged();
                    });
                    deletingPopUp.findViewById(R.id.deleteButton).setOnClickListener(l -> {
                        playlistAdapter.removePlaylist(pos);
                        playlistAdapter.setData(allPlaylists);
                        confirmDeletingPlaylist.hide();
                    });
                    setSwipePosition(endSwipe);
                };
                SwipedRegionListener editLineListener = new SwipedRegionListener() {
                    @Override
                    public void onClick(int pos) {

                        setSwipePosition(endSwipe);
                    }

                };
                buffer.add(new SwipedRegion(getContext(), "Delete", R.drawable.ic_baseline_delete_24, 45, Color.parseColor("#8e99f3"), removeListener));
                buffer.add(new SwipedRegion(getContext(), "Edit line", R.drawable.ic_baseline_edit_24, 45, Color.parseColor("#8e99f3"), editLineListener));
            }
        };
        addPlaylistButton = view.findViewById(R.id.fabAdd);
        allPlaylists = new ArrayList<>();

        playlistAdapter = new PlaylistAdapter(getContext());
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View playlistPopUp = getLayoutInflater().inflate(R.layout.add_playlist, null);
                recyclerView.setAdapter(playlistAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                materialAlertDialogBuilder.setView(playlistPopUp);
                addPlaylistDialog = materialAlertDialogBuilder.create();
                addPlaylistDialog.show();
                TextInputEditText namePlaylistText = playlistPopUp.findViewById(R.id.textInputPlaylist);
                playlistPopUp.findViewById(R.id.cancelInsert).setOnClickListener(l -> addPlaylistDialog.hide());
                playlistPopUp.findViewById(R.id.savePlaylist).setOnClickListener(l -> {
                    allPlaylists.add(new Playlist(namePlaylistText.getText().toString()));
                    playlistAdapter.setData(allPlaylists);
                    addPlaylistDialog.hide();
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_view, container, false);
    }
}
