package com.example.vinylmusicplayer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
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
import com.example.vinylmusicplayer.helpers.NetworkHelper;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    SpotifyDataRetriever spotifyDataRetriever;
    ListViewModel model;
    private final int TAB_NUMBER = 4;
    Context context;
    private String musicFolder;
    private Toolbar musicBar;
    public static LinearLayout musicBarLayout;
    private TextView musicBarText;
    private ImageButton musicBarIcon;
    private MusicManager musicManager;
    private boolean musicBarActive = false;
    private RandomString rndString = new RandomString();
    private int currentPage;
    SongPageFragment.OnSongItemClicked songItemClicked;
    ViewPagerAdapter viewPagerAdapter;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    MaterialToolbar toolbar;
    SearchView searchView;
    String queryText="";


    public HomeFragment(String folder) {

        musicFolder = folder;
    }

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
        if (sharedPref.getBoolean("musicBarActive", false)) {
            musicBarLayout.setVisibility(View.VISIBLE);
            String title = model.getSongById(id).getTitle();
            musicBarText.setText(title);
            if (!musicManager.isPlaying() && !itemClicked) {
                musicBarIcon.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            }
        } else {
            musicBarLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("StateFragment", "ONDestroy");
        if (musicManager.isCurrentlyActive()) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null) {
            searchView.setQuery("", false);
        }
        toolbar.getMenu().getItem(0).collapseActionView();
        Log.d("StateFragment", "OnPause");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToStorageLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE); //request storage permission
        accessToStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); //request storage permission
        accessToStorageLauncher.launch(Manifest.permission.WAKE_LOCK);
        Log.d("StateFragment", "OnCreate");
    }

    public static class CoverAlbumChanged {
        public String id;
        public Drawable drawable;
        public String artistName;

        public CoverAlbumChanged(String id, Drawable drawable) {
            this.id = id;
            this.drawable = drawable;
        }
    }

    public static class QuerySearch {
        public String query;
        public int page;

        public QuerySearch(String query, int page) {
            this.query = query;
            this.page = page;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StateFragment", "OnViewCreated");
        final Activity activity = getActivity();
        context = getContext();
        if (activity != null) {
            toolbar = view.findViewById(R.id.topAppBar);
            toolbar.inflateMenu(R.menu.top_app_bar);

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.search) {
                        searchView = (SearchView)item.getActionView();
                        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                        editText.setTextColor(Color.WHITE);
                        editText.setHintTextColor(Color.GRAY);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                EventBus bus = EventBus.getDefault();
                                queryText=query;
                                bus.post(new QuerySearch(queryText, currentPage));
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                EventBus bus = EventBus.getDefault();
                                queryText=newText;
                                bus.post(new QuerySearch(queryText, currentPage));
                                return true;
                            }
                        });
                    }
                    return false;
                }
            });
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            final View loadingView = getLayoutInflater().inflate(R.layout.loading_view, null);
            editor.putBoolean("musicBarActive", false);
            editor.apply();
            model = new ViewModelProvider(getActivity()).get(ListViewModel.class);

            ViewPager2 viewPager2 = activity.findViewById(R.id.tabPager);

            musicBar = getView().findViewById(R.id.musicBar);
            musicBarLayout = getView().findViewById(R.id.musicBarLayout);
            musicBarText = getView().findViewById(R.id.musicBarText);
            musicBarIcon = getView().findViewById(R.id.musicBarIcon);
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
            materialAlertDialogBuilder.setView(loadingView);
            AlertDialog loadingPopUp = materialAlertDialogBuilder
                    .create();
            loadingPopUp.setCancelable(false);
            songItemClicked = (id, playlist) -> {
                int index;
                if (playlist != null) {
                    index = playlist.indexOf(model.getSongById(id));
                    musicManager.create(playlist, index);
                } else {
                    index = model.getSongPositionById(id);
                    musicManager.create(model.getSongs().getValue(), index);
                }
                String artistId = model.getSongsArtist().get(id);
                editor.putString("currSongId", id);
                editor.putString("currArtistId", artistId);
                editor.putBoolean("musicBarActive", true);
                editor.apply();
                setStatusBar(id, true);
                musicBarIcon.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                // musicManager.stop();
                musicManager.play();
                musicManager.setCurrentActive(true);
            };
            viewPagerAdapter = new ViewPagerAdapter(this, model, songItemClicked);
            viewPager2.setAdapter(viewPagerAdapter);

            if (model.getSongs().getValue().isEmpty()) {
                if (model.retrieveAllSongsInFolder(musicFolder, getActivity())) {
                    loadingPopUp.show(); //show retriving data
                    model.getSpotifyData().observe(getActivity(), (data) -> {
                        //when data is setted
                        model.loadArtists(getActivity());
                    });
                    model.getArtists().observe(getActivity(), (list) -> {
                        Drawable cover = VectorDrawableCompat.create(getResources(), R.drawable.unknown_album, getContext().getTheme());
                        for (Artist artist : list) {
                            try {
                                cover = NetworkHelper.drawableFromUrl(artist.getImageUrl());
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                            artist.setCoverImage(cover);
                        }
                        model.loadAlbum();
                    });
                    model.getAlbum().observe(getActivity(), (list) -> {
                        Drawable cover = VectorDrawableCompat.create(getResources(), R.drawable.unknown_album, getContext().getTheme());
                        for (Album album : list) {
                            try {
                                cover = NetworkHelper.drawableFromUrl(album.getUrlImage());
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                            album.setCoverImage(cover);
                            EventBus bus = EventBus.getDefault();
                            bus.post(new CoverAlbumChanged(album.getId(), cover));
                        }
                        viewPagerAdapter.setDynamicData();
                        if (list.size() > 0) {
                            loadingPopUp.dismiss();
                        }
                    });
                    model.getSongs().observe(getActivity(), (list) -> {
                        for (Song song : list) {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(context, song.getUri());
                            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            int ssDuration = Integer.parseInt(duration);
                            song.setDuration(ssDuration);
                        }
                    });

                }
            }
            //  songs = model.getSongs().getValue();
            musicManager = MusicManager.getInstance();
            musicManager.setContext(context);
            musicManager.setSongCompletionListener(state -> {
                musicBarText.setText(musicManager.getCurrentSong().getTitle());
            });
            if (musicManager.isCurrentlyActive()) {  //check if one song has been played
                editor.putBoolean("musicBarActive", true);
                editor.apply();
                setStatusBar(musicManager.getCurrentSong().getId(), false);
            }
            //  }
            musicBar.setOnClickListener(v -> {

                Utils.slideUpFragment((AppCompatActivity) activity, new PlayFragment(activity, model, songItemClicked), "PlayFragment");
            });

            musicBarIcon.setOnClickListener(v -> {
                if (musicManager.isPlaying()) {
                    musicManager.pause();
                    v.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    musicManager.play();
                    v.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                }
            });

            TabLayout tabLayout = getView().findViewById(R.id.tabLayoutHome);
            new TabLayoutMediator(tabLayout, viewPager2, this::manageTabSelection).attach();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    currentPage = tab.getPosition();
//                    EventBus bus = EventBus.getDefault();
//
//                    bus.post(new QuerySearch(queryText, currentPage));
                    if (tab.getPosition() == 3) {
                        musicBarLayout.setVisibility(View.GONE);
                    } else {
                        if (sharedPref.getBoolean("musicBarActive", false)) {
                            musicBarLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (musicManager.isCurrentlyActive()) {  //check if one song has been played
            editor.putBoolean("musicBarActive", true);
            editor.apply();
            setStatusBar(musicManager.getCurrentSong().getId(), false);
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
                break;
            case ALBUM:
                textToSet = getActivity().getString(R.string.tab3_title);
                break;
            case PLAYLIST:
                textToSet = getActivity().getString(R.string.tab4_title);
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