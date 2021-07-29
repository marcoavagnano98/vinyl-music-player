package com.example.vinylmusicplayer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.classes.Playlist;
import com.example.vinylmusicplayer.classes.Song;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{
    List<Playlist> playListName=new ArrayList<>();
    private Context context;
    private OnRVItemListener onRVItemListener;

    public PlaylistAdapter(Context context, OnRVItemListener onRVItemListener){
        this.context=context;
        this.onRVItemListener=onRVItemListener;
    }
    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_cell, parent, false);
        return new PlaylistViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistViewHolder holder, int position) {
            holder.textView.setText(playListName.get(position).getName().toUpperCase());
            String playlistSubtitle=String.valueOf(playListName.get(position).getNumSong()).concat(" canzoni");
            holder.textView2.setText(playlistSubtitle);
            holder.imageView.setImageResource(R.drawable.unknown_album);
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
    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        TextView textView2;



        public PlaylistViewHolder(@NonNull View itemView) {
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
