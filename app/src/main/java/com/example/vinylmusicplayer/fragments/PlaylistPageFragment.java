package com.example.vinylmusicplayer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.PlaylistPageAdapter;
import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.classes.RandomString;
import com.example.vinylmusicplayer.helpers.SaveGsonData;
import com.example.vinylmusicplayer.helpers.SwipeHelper;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlaylistPageFragment extends Fragment {
    private AlertDialog addPlaylistDialog;
    private AlertDialog confirmDeletingPlaylist;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    List<Playlist> allPlaylists;
    RecyclerView recyclerView;
    PlaylistPageAdapter playlistPageAdapter;
    FloatingActionButton addPlaylistButton;
    ListViewModel model;
    private SongPageFragment.OnSongItemClicked songItemClicked;

    public PlaylistPageFragment(ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {
        this.model = model;
        this.songItemClicked = songItemClicked;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeFragment.QuerySearch event) {
        Log.d("NUMBER", event.page + "");
        if (event.page == 3) {
            playlistPageAdapter.filter(allPlaylists, event.query);
        }
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Map<Playlist, List<String>> allPlaylistData = SaveGsonData.retrieve(getActivity());
        allPlaylists = new ArrayList<>();
        for (Playlist playlist : allPlaylistData.keySet()) {
            playlist.set(model.getSongsByListUri(allPlaylistData.get(playlist)));
            allPlaylists.add(playlist);
        }
        recyclerView = view.findViewById(R.id.playlistView);
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView, 150) {
            @Override
            public void instantiateSwipedRegion(RecyclerView.ViewHolder viewHolder, List<SwipedRegion> buffer) {
                SwipedRegionListener removeListener = pos -> {
                    final View deletingPopUp = getLayoutInflater().inflate(R.layout.alert_deleting, null);
                    TextView header = deletingPopUp.findViewById(R.id.header);
                    TextView message = deletingPopUp.findViewById(R.id.message);
                    header.setText(R.string.delete_playlist);
                    message.setText("Vuoi veramente eliminare la playlist \"" + allPlaylists.get(pos).getName() + "\"");
                    materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    materialAlertDialogBuilder.setView(deletingPopUp);
                    confirmDeletingPlaylist = materialAlertDialogBuilder.create();
                    confirmDeletingPlaylist.setCancelable(false);
                    confirmDeletingPlaylist.show();
                    deletingPopUp.findViewById(R.id.cancelDeleting).setOnClickListener(l -> {
                        confirmDeletingPlaylist.dismiss();
                        playlistPageAdapter.notifyDataSetChanged();
                    });
                    deletingPopUp.findViewById(R.id.deleteButton).setOnClickListener(l -> {
                        String pId = allPlaylists.get(pos).getId();
                        allPlaylists.remove(pos);
                        SaveGsonData.save(getActivity(), allPlaylists);
                        SaveGsonData.removeItem(getActivity(), pId);
                        // playlistPageAdapter.removePlaylist(pos);
                        playlistPageAdapter.setData(allPlaylists);
                        confirmDeletingPlaylist.dismiss();
                    });
                    setSwipePosition(endSwipe);
                };
                SwipedRegionListener editLineListener = new SwipedRegionListener() {
                    @Override
                    public void onClick(int pos) {
                        final View editView = getLayoutInflater().inflate(R.layout.add_playlist, null);
                        TextView textView = editView.findViewById(R.id.coverPlaylist);
                        TextInputEditText textInputEditText = editView.findViewById(R.id.textInputPlaylist);
                        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                        materialAlertDialogBuilder.setView(editView);
                        MaterialButton editButton=editView.findViewById(R.id.savePlaylist);
                        AlertDialog editPopup = materialAlertDialogBuilder.create();
                        editPopup.setCancelable(false);
                        editPopup.show();
                        textView.setText("Modifica nome");
                        editButton.setText("Salva");
                        editButton.setOnClickListener(v -> {
                            allPlaylists.get(pos).setName(textInputEditText.getText().toString());
                            SaveGsonData.save(getActivity(), allPlaylists);
                            editPopup.dismiss();
                            setRecyclerView();
                        });
                        editView.findViewById(R.id.cancelInsert).setOnClickListener(l -> editPopup.dismiss());
                        setSwipePosition(endSwipe);
                    }

                };
                buffer.add(new SwipedRegion(getContext(), "Delete", R.drawable.ic_baseline_delete_24, 45, Color.parseColor("#3256bd"), removeListener));
                buffer.add(new SwipedRegion(getContext(), "Edit line", R.drawable.ic_baseline_edit_24, 45, Color.parseColor("#3256bd"), editLineListener));
            }
        };
        addPlaylistButton = view.findViewById(R.id.fabAdd);


        playlistPageAdapter = new PlaylistPageAdapter(getContext(), pos -> {
            PlaylistFragment playlistFragment = new PlaylistFragment(playlistPageAdapter.getPlayListName().get(pos), model, songItemClicked);
            Utils.insertFragment((AppCompatActivity) getActivity(), playlistFragment, "PlaylistFragment");
        });
        setRecyclerView();
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View playlistPopUp = getLayoutInflater().inflate(R.layout.add_playlist, null);
                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                materialAlertDialogBuilder.setView(playlistPopUp);
                addPlaylistDialog = materialAlertDialogBuilder.create();
                addPlaylistDialog.show();
                TextInputEditText namePlaylistText = playlistPopUp.findViewById(R.id.textInputPlaylist);
//                addPlaylistDialog.findViewById(R.id.chooseCover).setOnClickListener(l->{
//
//                });
                playlistPopUp.findViewById(R.id.cancelInsert).setOnClickListener(l -> addPlaylistDialog.dismiss());
                playlistPopUp.findViewById(R.id.savePlaylist).setOnClickListener(l -> {
                    RandomString rnd = new RandomString(10);
                    allPlaylists.add(new Playlist(rnd.nextString(), namePlaylistText.getText().toString()));
                    SaveGsonData.save(getActivity(), allPlaylists);
                    setRecyclerView();
                    addPlaylistDialog.dismiss();
                });
            }
        });
    }

    public void setRecyclerView() {
        recyclerView.setAdapter(playlistPageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allPlaylists.sort((p1, p2) -> p1.getDate().compareTo(p2.getDate()));
        playlistPageAdapter.setData(allPlaylists);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_view, container, false);
    }
}
