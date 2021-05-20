package com.example.vinylmusicplayer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.ViewPagerAdapter;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.FilesManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {
    ArrayList<Song> allSongs;

    List<File> fileListSongs;
    SpotifyDataRetriever spotifyDataRetriever;
    ListViewModel model;
    private final int TAB_NUMBER = 4;
    Context context;


    //Client ID spotify: 117f89e1bef040d5922b254fe6101ce1
//Client secret: 83adf88d52954ae4a98844c0c3e5e03f
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = getActivity();

        context = getContext();
        if (activity != null) {
            model = new ViewModelProvider((ViewModelStoreOwner) activity).get(ListViewModel.class);
            ViewPager2 viewPager2 = activity.findViewById(R.id.tabPager);

            accessToStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); //request storage permission
            //   buildSongsList(filesManager);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,model);
            viewPager2.setAdapter(viewPagerAdapter);
            TabLayout tabLayout = getView().findViewById(R.id.tabLayoutHome);
            model.retrieveAllSongsInFolder("Music"); //set songs in viewModel
            List<Artist> allArtists=new ArrayList<>();
            spotifyDataRetriever = new SpotifyDataRetriever(activity, (data, index) -> {
                Artist artist = new Artist(index, data[0]);
                List<Song> allSongs = model.getSongs().getValue();
                boolean artistFound = false;
                synchronized (allArtists) {
                    if (!allArtists.isEmpty()) {
                        for (int i=0;i<allArtists.size();i++) {
                            if (artist.getName().contains(allArtists.get(i).getName())) {
                                artistFound = true;
                                allArtists.get(i).addSongId(allSongs.get(index).getId());
                            }
                        }
                    }

                    if (!artistFound) {
                        artist.addSongId(allSongs.get(index).getId());  //assign to all artists his song
                        allArtists.add(artist);
                    }
                }
                viewPagerAdapter.setDynamicData(allArtists, 1);
            });
            new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {


                manageTabSelection(tab, position);

            }).attach();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
//                    int position = tab.getPosition();
//                    manageTabSelection(null, position);

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

    private void buildSongsList(FilesManager filesManager) {
        fileListSongs = new ArrayList<>();
        allSongs = new ArrayList<>();
        Set<String> filesKeySet = filesManager.allSongsInFolder("Music"); //assume absolute path /storage/emulated/0
        for (String s : filesKeySet) {
            File f = filesManager.getAudioFIleById(s); //audio file must only retrive by id
            allSongs.add(new Song(s, filesManager.truncateExtension(f.getName()), Uri.fromFile(f)));
        }
    }

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
                        for (String songName : model.allSongsName()) {
                            try {
                                String encodedTitle = URLEncoder.encode(songName, String.valueOf(StandardCharsets.UTF_8));
                                spotifyDataRetriever.query(encodedTitle, index++);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }catch (Exception e){
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