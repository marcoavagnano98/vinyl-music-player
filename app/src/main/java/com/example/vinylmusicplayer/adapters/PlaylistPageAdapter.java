package com.example.vinylmusicplayer.adapters;

import android.content.Context;
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
import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.fragments.SongPageFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistPageAdapter extends RecyclerView.Adapter<PlaylistPageAdapter.PlaylistPageViewHolder>{
    List<Playlist> playListName=new ArrayList<>();
    private Context context;
    private OnRVItemListener onRVItemListener;
    private int [] playlistCoverResource={R.drawable.playlist,R.drawable.blue_playlist,
            R.drawable.bordeaux_playlist,R.drawable.brown_playlist,
            R.drawable.gray_playlist,R.drawable.green_playlist,R.drawable.purple_playlist,
            R.drawable.light_blue_playlist};


    public PlaylistPageAdapter(Context context, OnRVItemListener onRVItemListener){
        this.context=context;
        this.onRVItemListener=onRVItemListener;

    }
    @NonNull
    @Override
    public PlaylistPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_cell, parent, false);
        return new PlaylistPageViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistPageViewHolder holder, int position) {
        Random rnd=new Random();
            holder.textView.setText(playListName.get(position).getName().toUpperCase());
            String playlistSubtitle=String.valueOf(playListName.get(position).getNumSong()).concat(" canzoni");
            holder.textView2.setText(playlistSubtitle);
            if(holder.imageView.getDrawable() == null) {
                holder.imageView.setImageResource(playlistCoverResource[rnd.nextInt(7)]);
            }
    }
    public void filter(List<Playlist> searchableList, String query) {
        List<Playlist> list = new ArrayList<>();
        if (!query.equals("")) {
            for (Playlist playlist : searchableList) {
                if (playlist.getName().toLowerCase().contains(query.toLowerCase())) {
                    list.add(playlist);
                }
            }
            playListName = list;
        } else {
            playListName = searchableList;
        }
        notifyDataSetChanged();
    }

    public List<Playlist> getPlayListName() {
        return playListName;
    }

    @Override
    public int getItemCount() {
        return playListName.size();
    }
    public void setData(List<Playlist> playListName){
        this.playListName=playListName;
        this.notifyDataSetChanged();
    }

    public void removePlaylist(int position){
        playListName.remove(position);
        this.notifyDataSetChanged();
    }
    public class PlaylistPageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        TextView textView2;



        public PlaylistPageViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.playlistText);
            imageView=itemView.findViewById(R.id.cover);
            textView2=itemView.findViewById(R.id.numSongText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           onRVItemListener.onItemClick(getAdapterPosition());
        }
    }
}
