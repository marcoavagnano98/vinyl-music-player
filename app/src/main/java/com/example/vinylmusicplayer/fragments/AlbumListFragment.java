package com.example.vinylmusicplayer.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.AlbumListAdapter;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import org.greenrobot.eventbus.EventBus;

public class AlbumListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListViewModel model;
    public AlbumListFragment(ListViewModel model){
        this.model=model;
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
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(new AlbumListAdapter.OnCoverChanged() {
            @Override
            public void onChanged(String id, Drawable drawable) {
                EventBus bus = EventBus.getDefault();
                bus.post(new CoverAlbumChanged(id,drawable));
            }
        });
        recyclerView.setAdapter(albumListAdapter);
        albumListAdapter.setData(model.getAlbum().getValue());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }
    public static class CoverAlbumChanged {
        public String id;
        public Drawable drawable;
        public CoverAlbumChanged(String id,Drawable drawable) {
            this.id = id;
            this.drawable=drawable;
        }
    }
}
