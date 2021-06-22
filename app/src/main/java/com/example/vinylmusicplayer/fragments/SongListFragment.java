package com.example.vinylmusicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
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
    public interface OnSongItemClicked {
        void onClick(String id);
    }
    public SongListFragment(ListViewModel model, OnSongItemClicked songItemClicked){
        this.model=model;
        this.songItemClicked=songItemClicked;
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
    public void onEvent(AlbumListFragment.CoverAlbumChanged event){
        List<String> allRowToChange= model.getSongKey(model.getSongsAlbum(),event.id);
        for(String id: allRowToChange ){
            songsListAdapter.updateCoverImage(model.getSongPositionById(id),event.drawable);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);


            songsListAdapter = new SongsListAdapter(getActivity(),
                    rowPos -> {
                        this.songItemClicked.onClick(model.getSongs().getValue().get(rowPos).getId());
                    }, new SongsListAdapter.OnOptionMenuListener() {
                @Override
                public void onItemClick(int position, int itemClicked) {
                    switch (itemClicked) {
                        case R.id.shareSong:
                            try {
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
