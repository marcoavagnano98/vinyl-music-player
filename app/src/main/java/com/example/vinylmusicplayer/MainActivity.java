package com.example.vinylmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.SharedPreferences;
import android.icu.number.IntegerWidth;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.vinylmusicplayer.classes.RandomString;
import com.example.vinylmusicplayer.fragments.HomeFragment;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.media.MediaCodec.MetricsConstants.MODE;

public class MainActivity extends AppCompatActivity {
    final String TAG="HomeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        prefsEditor.clear();
//        prefsEditor.apply();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String folder=getIntent().getStringExtra("folder");
        Utils.insertFragment(this,new HomeFragment(folder),TAG);
    }

}