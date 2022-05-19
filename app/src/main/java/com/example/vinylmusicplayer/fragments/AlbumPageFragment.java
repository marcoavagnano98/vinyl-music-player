package com.example.vinylmusicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.AlbumGridAdapter;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AlbumPageFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListViewModel model;
    SongPageFragment.OnSongItemClicked onSongItemClicked;
    AlbumGridAdapter albumGridAdapter;
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
    public void onEvent(HomeFragment.QuerySearch event) {
        Log.d("NUMBER", event.page + "");
        if (event.page == 2) {
            albumGridAdapter.filter(model.getAlbum().getValue(), event.query);
        }
    }

    public AlbumPageFragment(ListViewModel model, SongPageFragment.OnSongItemClicked onSongItemClicked) {
        this.model = model;
        this.onSongItemClicked=onSongItemClicked;
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(SongPageFragment.SongRemoved event) {
//        albumGridAdapter.notifyDataSetChanged();
//    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        if(model.getSongs().getValue().isEmpty()){
            recyclerView.setVisibility(View.GONE);
        }
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
         albumGridAdapter = new AlbumGridAdapter(position->{
            AlbumFragment albumFragment=new AlbumFragment(model,onSongItemClicked);
            Bundle bundle=new Bundle();
            bundle.putString("albumId",albumGridAdapter.getAlbum().get(position).getId());
            albumFragment.setArguments(bundle);
            Utils.insertFragment((AppCompatActivity) getActivity(), albumFragment, "AlbumFragment");
        });
        recyclerView.setAdapter(albumGridAdapter);
        model.getAlbum().observe(getViewLifecycleOwner(),(list)->{
            albumGridAdapter.setData(list);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

}
