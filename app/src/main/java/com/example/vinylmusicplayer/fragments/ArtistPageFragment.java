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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.ArtistListAdapter;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ArtistPageFragment extends Fragment {
    private final ListViewModel model;
    private SongPageFragment.OnSongItemClicked songItemClicked;
    ArtistListAdapter adapter;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//
//    }
    public ArtistPageFragment(ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {
        this.model = model;
        this.songItemClicked = songItemClicked;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeFragment.QuerySearch event) {
        Log.d("NUMBER", event.page + "");
        if (event.page == 1) {
            adapter.filter(model.getArtists().getValue(), event.query);
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(SongPageFragment.SongRemoved event) {
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if(model.getSongs().getValue().isEmpty()){
            recyclerView.setVisibility(View.GONE);
        }
         adapter = new ArtistListAdapter(new OnRVItemListener() {
            @Override
            public void onItemClick(int position) {
                Artist artist=adapter.getArtists().get(position);
                Fragment fragment = new ArtistFragment(model, songItemClicked);
                Bundle args = new Bundle();
                args.putString("artistId", artist.getId());
                fragment.setArguments(args);
                Utils.insertFragment((AppCompatActivity) getActivity(), fragment, "ArtistFragment");
            }
        });
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
        recyclerView.setAdapter(adapter);
        model.getArtists().observe(getViewLifecycleOwner(),(list)->{
            adapter.setData(list);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
