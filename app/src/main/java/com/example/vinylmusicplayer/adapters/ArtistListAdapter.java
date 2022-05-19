package com.example.vinylmusicplayer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.Artist;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.classes.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistListViewHolder> {
    private List<Artist> artists;

    OnRVItemListener onRVItemListener;


    public ArtistListAdapter(OnRVItemListener onRVItemListener) {
        this.onRVItemListener = onRVItemListener;
    }

    public void setData(List<Artist> artistList) {
        this.artists = new ArrayList<>(artistList);
        this.notifyDataSetChanged();
        //this.songList=new ArrayList<>(songList);
    }
    public void filter(List<Artist> searchableList, String query) {
        List<Artist> list = new ArrayList<>();
        if (!query.equals("")) {
            for (Artist artist : searchableList) {
                if (artist.getName().toLowerCase().contains(query.toLowerCase())) {
                    list.add(artist);
                }
            }
            artists = list;
        } else {
            artists = searchableList;
        }
        notifyDataSetChanged();
    }
    public List<Artist> getArtists(){
        return artists;
    }

    @NonNull
    @Override
    public ArtistListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_line, parent, false);
        return new ArtistListAdapter.ArtistListViewHolder(layoutView, onRVItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.ArtistListViewHolder holder, int position) {
        String name = artists.get(position).getName();
        holder.textView.setText(name);
        if (artists.get(position).getCoverImage() == null) {
            holder.imageView.setImageResource(R.drawable.unknown_album);
        } else {
            holder.imageView.setImageDrawable(artists.get(position).getCoverImage());
        }
        holder.optionMenu.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;
        OnRVItemListener onRVItemListener2;
        ImageView optionMenu;

        public ArtistListViewHolder(@NonNull View itemView, OnRVItemListener onRVItemListener2) {
            super(itemView);
            textView = itemView.findViewById(R.id.nameSong);
            imageView = itemView.findViewById(R.id.coverAlbum);
            this.onRVItemListener2 = onRVItemListener2;
            optionMenu = itemView.findViewById(R.id.menuDropper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRVItemListener2.onItemClick(getAdapterPosition());
        }
    }

}
