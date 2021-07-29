package com.example.vinylmusicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.AlbumListAdapter;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AlbumListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListViewModel model;
    SongListFragment.OnSongItemClicked onSongItemClicked;


    public AlbumListFragment(ListViewModel model, SongListFragment.OnSongItemClicked onSongItemClicked) {
        this.model = model;
        this.onSongItemClicked=onSongItemClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        List<Album> listAlbum=model.getAlbum().getValue();
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
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(position->{
            AlbumFragment albumFragment=new AlbumFragment(model,onSongItemClicked);
            Bundle bundle=new Bundle();
            bundle.putString("albumId",listAlbum.get(position).getId());
            albumFragment.setArguments(bundle);
            Utils.insertFragment((AppCompatActivity) getActivity(), albumFragment, "AlbumFragment");
        });
        recyclerView.setAdapter(albumListAdapter);
        albumListAdapter.setData(model.getAlbum().getValue());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

}
