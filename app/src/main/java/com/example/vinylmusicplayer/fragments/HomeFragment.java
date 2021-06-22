package com.example.vinylmusicplayer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.ViewPagerAdapter;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.MusicManager;
import com.example.vinylmusicplayer.classes.RandomString;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.backend.SpotifyDataRetriever;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ArrayList<Song> allSongs;

    List<File> fileListSongs;
    SpotifyDataRetriever spotifyDataRetriever;
    ListViewModel model;
    private final int TAB_NUMBER = 4;
    Context context;
    public static String musicFolder = "Music";
    private Toolbar musicBar;
    private LinearLayout musicBarLayout;
    private TextView musicBarText;
    private ImageButton musicBarIcon;
    private static MusicManager musicManager;
    private boolean musicBarActive = false;
    private RandomString rndString=new RandomString();


    enum TAB_SELECTION {
        SONGS(0), ARTIST(1), ALBUM(2), PLAYLIST(3), NOTDEFINED(-1);

        private int numVal;

        TAB_SELECTION(int numVal) {
            this.numVal = numVal;
        }

        public static TAB_SELECTION getType(int value) {
            TAB_SELECTION selection = TAB_SELECTION.NOTDEFINED;
            for (TAB_SELECTION line : TAB_SELECTION.values()) {
                if (value == line.numVal) {
                    selection = line;
                }
            }
            return selection;
        }
    }


    private ActivityResultLauncher<String> accessToStorageLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {

                    }
                }
            });

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void setStatusBar(String id, boolean itemClicked) {
        musicBarLayout.setVisibility(View.VISIBLE);
        String title = model.getSongById(id).getTitle();
        musicBarText.setText(title);
        musicBarActive = true;
        if(!musicManager.isPlaying() && !itemClicked){
            musicBarIcon.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = getActivity();
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        context = getContext();
        if (activity != null) {
            model = new ViewModelProvider(getActivity()).get(ListViewModel.class);
            ViewPager2 viewPager2 = activity.findViewById(R.id.tabPager);
            accessToStorageLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE); //request storage permission
            accessToStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); //request storage permission
            musicBar = getView().findViewById(R.id.musicBar);
            musicBarLayout = getView().findViewById(R.id.musicBarLayout);
            musicBarText = getView().findViewById(R.id.musicBarText);
            musicBarIcon = getView().findViewById(R.id.musicBarIcon);
            if (musicManager == null  ) {
                musicManager = new MusicManager(getContext());
                musicManager.attachList(model.retrieveAllSongsInFolder(HomeFragment.musicFolder).getValue());
                List<Song> ll=model.getSongs().getValue();
                Log.d("Songf", ll.toString());
            } else {
                if(musicManager.isCurrentlyActive()) {  //check if one song has been played
                    setStatusBar(sharedPref.getString("currSongId", ""), false);
                }
            }
            musicBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.insertFragment((AppCompatActivity) activity, new PlayFragment(activity), "PlayFragment");
                }
            });
           SongListFragment.OnSongItemClicked songItemClicked = id -> {              // manage the fragments open when song clicked
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("currSongId", id);
                editor.apply();
                setStatusBar(id,true);
                Toast.makeText(getContext(),((float)musicManager.getDuration()/60000) + "", Toast.LENGTH_SHORT).show();
                musicManager.next(model.getSongPositionById(id));
                musicManager.setCurrentActive(true);
            };
            musicBarIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(musicManager.isPlaying()) {
                        musicManager.pause();
                        ((ImageButton) v).setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    }else{
                        musicManager.play();
                        ((ImageButton) v).setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                }
            });

            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, model, songItemClicked);
            viewPager2.setAdapter(viewPagerAdapter);

            TabLayout tabLayout = getView().findViewById(R.id.tabLayoutHome);


            //  model.retrieveAllSongsInFolder(musicFolder); //set songs in viewModel
            List<Artist> allArtists = new ArrayList<>();
            List<Album> allAlbum=new ArrayList<>();
            spotifyDataRetriever = new SpotifyDataRetriever(activity, (data, index) -> {
                Artist artist = new Artist(rndString.nextString(), data[0]);
                Album album=new Album(rndString.nextString(),data[1],data[2]);
                List<Song> allSongs = model.getSongs().getValue();
                Song songSelected=model.getSongById(data[3]);
                model.linkSongAlbum(allSongs.get(index).getId(),album.getId()); //link album song
                boolean artistFound = false;
                synchronized (allArtists) {
                    if (!allArtists.isEmpty()) {
                        for (int i = 0; i < allArtists.size(); i++) {
                            if (artist.getName().contains(allArtists.get(i).getName())) {
                                artistFound = true;
                                model.linkSongArtist(songSelected.getId(),allArtists.get(i).getId()); //link song with his artist
                            }
                        }
                    }

                    if (!artistFound) {
                        model.linkSongArtist(songSelected.getId(),artist.getId());
                        allArtists.add(artist);
                    }
                }
                boolean albumFound=false;
                synchronized (allAlbum){
                    if(!allAlbum.isEmpty()){
                        for(Album item : allAlbum){
                            if(album.getTitle().contains(item.getTitle())){
                                albumFound=true;
                                break;
                            }
                        }

                    }
                }
                if(!albumFound){
                    allAlbum.add(album);
                }
                viewPagerAdapter.setDynamicData(allArtists,allAlbum);

            });
            new TabLayoutMediator(tabLayout, viewPager2, this::manageTabSelection).attach();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //List<RecyclerView> recyclerView = getView().findViewById(R.id.recycler_view);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

//    private void buildSongsList(FilesManager filesManager) {
//        fileListSongs = new ArrayList<>();
//        allSongs = new ArrayList<>();
//        Set<String> filesKeySet = filesManager.allSongsInFolder("Music"); //assume absolute path /storage/emulated/0
//        for (String s : filesKeySet) {
//            File f = filesManager.getAudioFIleById(s); //audio file must only retrive by id
//            allSongs.add(new Song(s, filesManager.truncateExtension(f.getName()), Uri.fromFile(f)));
//        }
//    }

    private void manageTabSelection(TabLayout.Tab tab, int position) {
        TAB_SELECTION selection = TAB_SELECTION.getType(position);
        String textToSet = "";
        switch (selection) {

            case SONGS:
                //TODO: set recyclerview to retrieve songs
                textToSet = getActivity().getString(R.string.tab1_title);

                break;
            case ARTIST:
                textToSet = getActivity().getString(R.string.tab2_title);
                try {
                    if (model.getArtists().getValue().isEmpty()) {
                        int index = 0;
                        for (Song song : model.getSongs().getValue()) {
                            try {
                                String encodedTitle = URLEncoder.encode(song.getTitle(), String.valueOf(StandardCharsets.UTF_8));
                                spotifyDataRetriever.query(song.getId(),encodedTitle, index++);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case ALBUM:
                textToSet = getActivity().getString(R.string.tab3_title);
                //TODO: set gridLayout for album view
                break;
            case PLAYLIST:
                textToSet = getActivity().getString(R.string.tab4_title);
                //TODO: set CardLayout for playlist
                break;
        }
        if (tab != null) {
            tab.setText(textToSet);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false);
    }
}