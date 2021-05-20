package com.example.vinylmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.vinylmusicplayer.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {
    final String TAG="HomeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.insertFragment(this,new HomeFragment(),TAG);
    }
}