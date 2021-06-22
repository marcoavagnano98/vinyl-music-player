package com.example.vinylmusicplayer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.MusicManager;
import com.google.android.material.appbar.AppBarLayout;

public class PlayFragment extends Fragment {
    MusicManager musicManager;
    Activity activity;
    AppBarLayout appBarLayout;
    public PlayFragment(Activity activity){
        this.musicManager=musicManager;
        this.activity=activity;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appBarLayout=view.findViewById(R.id.app_bar);
        int width=appBarLayout.getWidth();
        if (appBarLayout != null) {
            ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
            params.height = appBarLayout.getWidth();
            appBarLayout.setLayoutParams(params);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.play_view, container, false);
    }
}
