package com.example.vinylmusicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.PlayActivity;
import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.ArtistListAdapter;
import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.ArrayList;

public  class DemoObjectFragment extends Fragment {
    public static ArrayList<Song> list1=new ArrayList<>();
    public static ArrayList<Artist> list2=new ArrayList<>();
    ListViewModel model;
    public DemoObjectFragment(ListViewModel model){
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
        Bundle args = getArguments();
        int position = args.getInt("position");
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if(position==0){
            SongsListAdapter adapter=new SongsListAdapter(getActivity(), position1 -> {
                Intent i = new Intent(getActivity().getApplicationContext(), PlayActivity.class);
                i.putExtra("position", position1);
                getActivity().startActivity(i);
            });
            recyclerView.setAdapter(adapter);
            model.getSongs().observe(getActivity(), songList -> {
                list1=new ArrayList<>(songList);
                adapter.setData(songList);
            });
        }else if(position==1){
            ArtistListAdapter adapter=new ArtistListAdapter(new ArtistListAdapter.OnRVItemListener2() {
                @Override
                public void onItemClick(int position) {
                    Fragment fragment = new ArtistFragment(model);
                    Bundle args = new Bundle();
                    args.putInt("position", position);
                    fragment.setArguments(args);
                    Utils.insertFragment((AppCompatActivity)getActivity(),fragment,"ArtistFragment");
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.setData(model.getArtists().getValue());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}