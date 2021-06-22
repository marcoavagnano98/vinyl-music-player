package com.example.vinylmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.vinylmusicplayer.classes.RandomString;
import com.example.vinylmusicplayer.fragments.HomeFragment;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.lang.reflect.Method;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final String TAG="HomeFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= 24) {
//            try {
//                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
//                m.invoke(null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        RandomString rnd=new RandomString();
        String a=rnd.nextString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Utils.insertFragment(this,new HomeFragment(),TAG);
    }
}