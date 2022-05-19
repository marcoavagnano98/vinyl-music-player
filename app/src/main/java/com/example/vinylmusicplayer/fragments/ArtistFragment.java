package com.example.vinylmusicplayer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.Utils;
import com.example.vinylmusicplayer.adapters.AlbumListAdapter;
import com.example.vinylmusicplayer.adapters.SongsListAdapter;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArtistFragment extends Fragment {
    ListViewModel model;
    RecyclerView recyclerView;
    RecyclerView recyclerViewSlided;
    SongPageFragment.OnSongItemClicked songItemClicked;
    List<Song> artistSongs;
    ImageView imageView;
    TextView numArtistSongsText;
    TextView numAlbumSongsText;
    ConstraintLayout songLayout;
    ConstraintLayout albumLayout;
    View albumSlidingView;
    boolean isFaded;
    int startPointToSlide;

    public ArtistFragment(ListViewModel model, SongPageFragment.OnSongItemClicked songItemClicked) {
        this.model = model;
        this.songItemClicked = songItemClicked;
        this.artistSongs = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        startPointToSlide = 0;
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewSlided = view.findViewById(R.id.albumRvSlided);
        imageView = view.findViewById(R.id.coverArtist);
        numArtistSongsText = view.findViewById(R.id.numSongs);
        numAlbumSongsText = view.findViewById(R.id.numAlbum);
        songLayout = view.findViewById(R.id.songHeaderLayout);
        albumLayout = view.findViewById(R.id.showAlbumLayout);


        setLayoutMetrics();
        String artistId = args.getString("artistId");
        Artist a = model.getArtistFromId(artistId);
        imageView.setImageDrawable(a.getCoverImage());
        List<String> songsIds = model.getSongKey(model.getSongsArtist(), a.getId());
        this.artistSongs = model.getSongsByListId(songsIds);
        List<Album> listAlbumFromArtist = getAlbum(songsIds);
        int numAlbum = listAlbumFromArtist.size();
        String numSongTextString = String.valueOf(artistSongs.size()).concat(" canzoni");
        String numAlbumTextString = String.valueOf(numAlbum).concat(" album");
        numArtistSongsText.setText(numSongTextString);
        numAlbumSongsText.setText(numAlbumTextString);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.setTitle(a.getName());
        toolbar.setTitleTextColor(Color.WHITE);

        SongsListAdapter adapter = new SongsListAdapter(getActivity(), position -> {

            this.songItemClicked.onClick(artistSongs.get(position).getId(), null);
        }, null);
        recyclerView.setAdapter(adapter);
        adapter.setData(artistSongs, true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AlbumListAdapter adapterSlided = new AlbumListAdapter(position -> {
            Bundle albumArgs = new Bundle();
            albumArgs.putString("albumId", listAlbumFromArtist.get(position).getId());
            AlbumFragment albumFragment = new AlbumFragment(model, this.songItemClicked);
            albumFragment.setArguments(albumArgs);
            Utils.insertFragment((AppCompatActivity) getActivity(), albumFragment, "AlbumFragment");
        });
        adapterSlided.setData(listAlbumFromArtist);
        recyclerViewSlided.setAdapter(adapterSlided);
        recyclerViewSlided.setLayoutManager(new LinearLayoutManager(getContext()));

        //   view.setSupportActionBar(toolbar);

        // add back arrow to toolbar
//        if (getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//        final Activity activity = getActivity();

    }

    private List<Album> getAlbum(List<String> ids) {
        List<Album> sortedList = new LinkedList<>();
        for (String key : ids) {
            String albumId = model.getSongsAlbum().get(key);
            if (sortedList.stream().noneMatch(a -> a.getId().equals(albumId))) {
                sortedList.add(model.getAlbumById(albumId));
            }
        }
        return sortedList;
    }

    private void setLayoutMetrics() {
        float[] metrics = Utils.getDpiScreenSize(getActivity());
        int heightDpi = (int) metrics[1];
        float pixels = getContext().getResources().getDisplayMetrics().density;
        int scrollLayoutHeight = (heightDpi - (int) (250 * pixels)) / 2;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) songLayout.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        layoutParams.matchConstraintMaxHeight = scrollLayoutHeight;
        songLayout.setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) albumLayout.getLayoutParams();
        layoutParams2.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        layoutParams2.matchConstraintMaxHeight = scrollLayoutHeight;
        albumLayout.setLayoutParams(layoutParams2);
    }
}
