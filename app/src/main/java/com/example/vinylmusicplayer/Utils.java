package com.example.vinylmusicplayer;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vinylmusicplayer.fragments.ArtistFragment;
import com.example.vinylmusicplayer.fragments.HomeFragment;
import com.example.vinylmusicplayer.fragments.StartFragment;

public class Utils {
    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment, tag);
        if (!(fragment instanceof HomeFragment) && !(fragment instanceof StartFragment)) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public static void showFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(fragment)
                .commit();
    }

    public static void slideUpFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment, tag);
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static float[] getDpiScreenSize(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        return new float[]{widthPixels,heightPixels};
    }
}
