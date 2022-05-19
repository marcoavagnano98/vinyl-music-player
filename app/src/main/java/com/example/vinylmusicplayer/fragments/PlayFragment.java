package com.example.vinylmusicplayer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.PlayQueueAdapter;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.MusicManager;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.helpers.DateHelper;
import com.example.vinylmusicplayer.helpers.SwapHelperCallback;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayFragment extends Fragment {

    private final int updateUnit = 1000;
    Activity activity;
    AppBarLayout appBarLayout;
    SharedPreferences sharedPref;
    AppCompatImageView coverImageView;
    AppCompatTextView songTitleTextView;
    AppCompatTextView artistNameTextView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ConstraintLayout playBody;
    SeekBar progressBar;
    private Timer updateProgressBarTimer;
    private int percent = updateUnit;
    private MusicManager musicManager;
    Chronometer chronometerTimeElapsed;
    TextView totalSongTime;
    FloatingActionButton buttonPlay;
    MaterialButton nextSong;
    MaterialButton prevSong;
    TextView movingTextView;
    ListViewModel model;
    private int duration;
    int currentTimeElapsed;
    RecyclerView otherFromArtistRV;
    AppCompatTextView otherFromArtist;
    PlayQueueAdapter songsListAdapter;
    SongPageFragment.OnSongItemClicked songItemClicked;
    List<Song> suggestSongs;
    Window window;
    SharedPreferences.Editor editor;
    Toolbar toolbarMusic;
    // boolean isPlaylist;
    boolean isPlayingBeforeSeek;
    Palette.Swatch darkVibrantSwatch;
    Palette.Swatch lightMutedSwatch;
    Artist artist;
    AppbarState appbarState;
    AudioManager audioManager;
    int[] marginsBackup;
    ItemTouchHelper.Callback callback;

    enum AppbarState {
        COLLAPSED, EXPANDED
    }

    public PlayFragment(Activity activity, ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {

        this.activity = activity;
        this.model = model;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        updateProgressBarTimer = new Timer();
        this.songItemClicked = songItemClicked;
        suggestSongs = new ArrayList<>();
        //     this.isPlaylist = false;
    }

//    public PlayFragment(Activity activity, ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked, boolean isPlaylist) {
//        this(activity, model, songItemClicked);
//        this.isPlaylist = isPlaylist;
//    }

    @Override
    public void onResume() {
        musicManager.setSongCompletionListener(state -> {
            if (getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    resetProgressBar();
                    buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                    setupUi();
                    startPlayView();
                });
            }
        });
        if(darkVibrantSwatch != null) {
            window.setStatusBarColor(darkVibrantSwatch.getRgb());
        }
        String prevId = sharedPref.getString("prevSongId", "");
        if (!prevId.equals(musicManager.getCurrentSong().getId()) && !prevId.equals("")) {
            setupUi(); //ui changed
        }
        duration = musicManager.getCurrentSong().getDuration();
        currentTimeElapsed = musicManager.getCurrentTimeElapsed();
        chronometerTimeElapsed.setBase(SystemClock.elapsedRealtime() - musicManager.getCurrentTimeElapsed());
        if (currentTimeElapsed >= updateUnit) {
            percent = currentTimeElapsed;
        }
        if (musicManager.isPlaying()) {
            startPlayView();
        } else {  // quando entro nel fragment con il player stoppato
            setProgressBar();
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        musicManager = MusicManager.getInstance();
        otherFromArtistRV = view.findViewById(R.id.recyclerViewOtherFromArtist);

        Log.d("PlayFragment", "OnViewCreated");

        otherFromArtistRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView1, int newState) {
                super.onScrollStateChanged(recyclerView1, newState);
                if (!recyclerView1.canScrollVertically(1) && !recyclerView1.canScrollVertically(-1)) {
                    recyclerView1.setNestedScrollingEnabled(false);
                } else if (appbarState == AppbarState.COLLAPSED) {
                    recyclerView1.setNestedScrollingEnabled(true);

                }
            }
        });

        otherFromArtist = view.findViewById(R.id.otherFromArtist);
        toolbarMusic = view.findViewById(R.id.toolbarMusic);
        toolbarMusic.setTitleTextColor(Color.WHITE);
        movingTextView = view.findViewById(R.id.movingTitle);
        movingTextView.setSelected(true);
        movingTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        playBody = view.findViewById(R.id.playBody);
        appbarState = AppbarState.EXPANDED;

        collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbarMusic.setNavigationOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();

        });
        songsListAdapter = new PlayQueueAdapter(activity, position -> {
            String idSong;

            idSong = suggestSongs.get(position).getId();
            editor.putString("currSongId", idSong);
            editor.apply();
            resetProgressBar();
            songItemClicked.onClick(idSong, suggestSongs);
            buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24);
            setupUi();
            startPlayView();
        });


        otherFromArtistRV.setLayoutManager(new LinearLayoutManager(getContext()));
        buttonPlay = view.findViewById(R.id.buttonPlay);
        nextSong = view.findViewById(R.id.buttonNext);
        prevSong = view.findViewById(R.id.buttonPrev);
        prevSong.setOnClickListener(v -> {
            prevSongPlay();
        });
        nextSong.setOnClickListener(v -> {
            musicManager.next();
            nextSongPlay();
        });
        setButtonPlayResource();
        buttonPlay.setOnClickListener(v -> {
            if (musicManager.isPlaying()) {
                musicManager.pause();
                stopProgressBar();
            } else {
                musicManager.play();
                chronometerTimeElapsed.setBase(SystemClock.elapsedRealtime() - musicManager.getCurrentTimeElapsed());
                startProgressBar();
            }
            setButtonPlayResource();

        });
        window = activity.getWindow();
        chronometerTimeElapsed = view.findViewById(R.id.elapsedTimeChronometer);
        totalSongTime = view.findViewById(R.id.totalTimeText);
        appBarLayout = view.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (appbarState == AppbarState.EXPANDED) {
                    appbarState = AppbarState.COLLAPSED;
                    movingTextView.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
                    layoutParams.topMargin = 0;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    progressBar.requestLayout();
                    progressBar.setEnabled(false);
                    progressBar.setPadding(0, 0, 0, 0);
                    chronometerTimeElapsed.setVisibility(View.INVISIBLE);
                    totalSongTime.setVisibility(View.INVISIBLE);
                    progressBar.setThumb(null);
                    artistNameTextView.setVisibility(View.GONE);
                    songTitleTextView.setVisibility(View.GONE);
                }
            } else {
                if (appbarState == AppbarState.COLLAPSED) {
                    artistNameTextView.setVisibility(View.VISIBLE);
                    movingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setEnabled(true);

                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
                    layoutParams.leftMargin = marginsBackup[0];
                    layoutParams.topMargin = marginsBackup[1];
                    layoutParams.rightMargin = marginsBackup[2];
                    layoutParams.bottomMargin = marginsBackup[3];
                    chronometerTimeElapsed.setVisibility(View.VISIBLE);
                    totalSongTime.setVisibility(View.VISIBLE);
                    progressBar.requestLayout();
                    progressBar.setPadding(marginsBackup[4], 0, marginsBackup[5], 0);
                    songTitleTextView.setVisibility(View.VISIBLE);
                    progressBar.setThumb(ContextCompat.getDrawable(getContext(), R.drawable.thumb_image));
                    appbarState = AppbarState.EXPANDED;
                }
            }
        });
        coverImageView = view.findViewById(R.id.coverImageViewId);
        songTitleTextView = view.findViewById(R.id.songTitle);
        progressBar = view.findViewById(R.id.progressBar);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
        marginsBackup = new int[]{layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin,
                layoutParams.bottomMargin, progressBar.getPaddingLeft(), progressBar.getPaddingRight()};
        artistNameTextView = view.findViewById(R.id.artistName);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    percent = progress;
                    chronometerTimeElapsed.setBase(SystemClock.elapsedRealtime() - progress);
                    musicManager.setCurrentPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (musicManager.isPlaying()) {
                    isPlayingBeforeSeek = true;
                    musicManager.pause();
                } else {
                    isPlayingBeforeSeek = false;
                }
                updateProgressBarTimer.cancel();
                chronometerTimeElapsed.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPlayingBeforeSeek) {
                    musicManager.play();
                    startProgressBar();
                }
            }
        });
        //set progressbar label align with start
        int width = progressBar.getWidth()
                - progressBar.getPaddingLeft()
                - progressBar.getPaddingRight();
        int thumbPos = progressBar.getPaddingLeft()
                + width
                * progressBar.getProgress()
                / progressBar.getMax();
        chronometerTimeElapsed.setTranslationX(thumbPos);
        totalSongTime.setTranslationX(-thumbPos);

        setupUi();
        super.onViewCreated(view, savedInstanceState);
    }

    public void setupUi() {
        if (appBarLayout != null) {

            duration = musicManager.getDuration();
            Resources metrics = getResources();
            int widthPx = metrics.getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
            params.height = widthPx;
            appBarLayout.setLayoutParams(params);
            coverImageView.setImageDrawable(musicManager.getCurrentSong().getCoverImage());
            Bitmap bitmap = ((BitmapDrawable) coverImageView.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    darkVibrantSwatch = palette.getDarkVibrantSwatch();
                    lightMutedSwatch = palette.getLightMutedSwatch();
                    if (darkVibrantSwatch != null) {
                        ColorDrawable colorDrawable = new ColorDrawable(darkVibrantSwatch.getRgb());
                        window.setStatusBarColor(darkVibrantSwatch.getRgb());
                        buttonPlay.setBackgroundTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));
                        collapsingToolbarLayout.setContentScrim(colorDrawable);
                        progressBar.setProgressTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));
                        progressBar.setThumbTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));


                    } else {
                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.purpleDark));
                        collapsingToolbarLayout.setContentScrim(colorDrawable);
                        buttonPlay.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.purpleDark));
                        progressBar.setProgressTintList(AppCompatResources.getColorStateList(getContext(), R.color.purpleDark));
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purpleDark));

                        progressBar.setThumbTintList(AppCompatResources.getColorStateList(getContext(), R.color.purpleDark));
                    }
//                    if (lightMutedSwatch != null) {
//
//                        // progressBar.setThumbTintList(ColorStateList.valueOf(darkMuted.getRgb()));
////                        prevSong.setBackgroundTintList(ColorStateList.valueOf(lightMutedSwatch.getRgb()));
////                        nextSong.setBackgroundTintList(ColorStateList.valueOf(lightMutedSwatch.getRgb()));
//
//                    }

                }
            });

            songTitleTextView.setText(musicManager.getCurrentSong().getTitle());
            artist = model.getArtistFromSongId(musicManager.getCurrentSong().getId());
            otherFromArtistRV.setAdapter(songsListAdapter);

            // if (isPlaylist) {
            suggestSongs = musicManager.getPlayList();
            model.setCurrentPlayQueue(suggestSongs);
            if (callback == null) { //prevent glitch
                callback = new SwapHelperCallback(otherFromArtistRV, model, songsListAdapter);
            }
            otherFromArtist.setText("Coda di riproduzione");
            model.getCurrentPlayQueue().observe(getActivity(), songList -> {
                musicManager.changeQueue(songList);
            });
            songsListAdapter.setData(suggestSongs);
            artistNameTextView.setText(artist.getName());
            artistNameTextView.setOnClickListener(l -> {
                ArtistFragment artistFragment = new ArtistFragment(model, songItemClicked);
                Bundle args = new Bundle();
                args.putString("artistId", artist.getId());
                artistFragment.setArguments(args);
                Utils.insertFragment((AppCompatActivity) getActivity(), artistFragment, "ArtistFragment");
            });
            progressBar.setMax(duration);
            totalSongTime.setText(DateHelper.longToFormatSongTime(duration));
            movingTextView.setText(musicManager.getCurrentSong().getTitle() + " - " + artist.getName());

        }
    }


    private void setButtonPlayResource() {
        if (musicManager.isPlaying()) {
            buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            buttonPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void startPlayView() {
        progressBar.setMax(musicManager.getDuration());
        chronometerTimeElapsed.setBase(SystemClock.elapsedRealtime() - musicManager.getCurrentTimeElapsed());
        editor.putString("currSongId", musicManager.getCurrentSong().getId());
        editor.apply();
        startProgressBar();
    }

    private void prevSongPlay() {
        resetProgressBar();
        musicManager.prev();
        buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        setupUi();
        startPlayView();
    }

    private void nextSongPlay() {
        resetProgressBar();
        buttonPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        setupUi();
        startPlayView();
    }

    private void setUpdateProgressBarTimer() {
        updateProgressBarTimer = new Timer();
        updateProgressBarTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setProgressBar();
            }
        }, 0, updateUnit);
    }

    private void startProgressBar() {
        chronometerTimeElapsed.start();
        setUpdateProgressBarTimer();
    }

    private void stopProgressBar() {
        chronometerTimeElapsed.stop();
        updateProgressBarTimer.cancel();
    }

    private void resetProgressBar() {
        stopProgressBar();
        progressBar.setProgress(0);
        percent = 0;
    }

    public void setProgressBar() {
        if (progressBar.getMax() <= percent) {
            stopProgressBar();
            //  nextSongPlay();
        } else {
            progressBar.setProgress(percent);
            percent += updateUnit;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.play_view, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PlayFragment", "OnPause");
        stopProgressBar();
        if (window != null) {
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purpleDark));
        }
        //in onResume we know if song changed
        editor.putString("prevSongId", musicManager.getCurrentSong().getId());
        editor.apply();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PlayFragment", "OnCreate");


//        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//        audioManager.requestAudioFocus(focusChange -> {
//            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//                if (getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
//                    stopProgressBar();
//                    setButtonPlayResource();
//                }
//                musicManager.pause();
//
//            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                musicManager.play();
//                if (getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
//                    chronometerTimeElapsed.setBase(SystemClock.elapsedRealtime() - musicManager.getCurrentTimeElapsed());
//                    startProgressBar();
//                }
//            }
//            setButtonPlayResource();
//        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


    }
}
