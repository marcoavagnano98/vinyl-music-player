package com.example.vinylmusicplayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.SongsViewHolder> {
    private List<Song> songList;
    private List<String> artistNames = new ArrayList<>();
    Activity activity;
    OnRVItemListener onRVItemListener;

    public SongsListAdapter(Activity activity, OnRVItemListener onRVItemListener) {
        this.onRVItemListener = onRVItemListener;
       // this.songList = songList;
        this.activity = activity;
        songList=new ArrayList<>();

    }
    public void setData(List<Song> songList){

        this.songList=new ArrayList<>(songList);
        this.notifyDataSetChanged();
        //this.songList=new ArrayList<>(songList);
    }

    public interface OnRVItemListener {
        void onItemClick(int position);
    }


    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_line, parent, false);
        return new SongsViewHolder(layoutView, onRVItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {

        holder.textView.setText(songList.get(position).getTitle());
        holder.imageView.setImageResource(R.drawable.unknown_album);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        ImageView imageView;
        OnRVItemListener onRVItemListener;
        public SongsViewHolder(@NonNull View itemView, OnRVItemListener onRVItemListener) {
            super(itemView);

            textView=itemView.findViewById(R.id.nameSong);
            imageView=itemView.findViewById(R.id.coverAlbum);
            this.onRVItemListener=onRVItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRVItemListener.onItemClick(getAdapterPosition());
        }
    }
}
