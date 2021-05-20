package com.example.vinylmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import com.example.vinylmusicplayer.classes.MusicManager;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlayActivity extends AppCompatActivity {
    MusicManager musicManager;
    List<Song> playlist;
    int startIndex;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        FloatingActionButton prevSong = findViewById(R.id.buttonPrev);
        FloatingActionButton nextSong = findViewById(R.id.buttonNext);

        Intent i = getIntent();
        startIndex = i.getIntExtra("position", -1);
        if(startIndex==-1){ //play from artists songs
            startIndex=i.getIntExtra("aIndex",-1);
            String artist=
        }
        if(i.getIntArrayExtra("playlist") == null){
            ListViewModel lvM=new ViewModelProvider((ViewModelStoreOwner) this).get(ListViewModel.class);
            playlist=lvM.retrieveAllSongsInFolder("Music").getValue();
        }
       // playlist = i.getParcelableArrayListExtra("playlist");
        musicManager = new MusicManager(this);
        musicManager.attachList(playlist);
        prevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.prev();
            }
        });
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.next();
            }
        });
        playSong();

    }

    private void playSong() {
        if (startIndex > -1) {
            musicManager.next(startIndex);
        }
    }

    private void checkCurrentTrack() {
        if (musicManager.isPlaying()) {
            musicManager.stop();
        }
    }

    @Override
    public void onBackPressed() {
          super.onBackPressed();
        musicManager.pause();
        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();

    }
}