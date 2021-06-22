package com.example.vinylmusicplayer.fragments;

import android.os.Bundle;
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
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.List;

public class ArtistListFragment extends Fragment {
    private final ListViewModel model;

    public ArtistListFragment(ListViewModel model){
        this.model = model;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ArtistListAdapter adapter = new ArtistListAdapter(new ArtistListAdapter.OnRVItemListener2() {
            @Override
            public void onItemClick(int position) {
                Fragment fragment = new ArtistFragment(model);
                Bundle args = new Bundle();
                args.putInt("position", position);
                fragment.setArguments(args);
                Utils.insertFragment((AppCompatActivity) getActivity(), fragment, "ArtistFragment");
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setData(model.getArtists().getValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
