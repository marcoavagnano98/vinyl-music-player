package com.example.vinylmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vinylmusicplayer.fragments.ArtistFragment;
import com.example.vinylmusicplayer.fragments.HomeFragment;

public class Utils {
    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment, tag);
        if (!(fragment instanceof HomeFragment)){
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }
}
