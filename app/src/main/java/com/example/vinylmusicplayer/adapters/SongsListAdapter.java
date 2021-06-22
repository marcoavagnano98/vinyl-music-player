package com.example.vinylmusicplayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    OnOptionMenuListener onOptionMenuListener;

    public SongsListAdapter(Activity activity, OnRVItemListener onRVItemListener, OnOptionMenuListener onOptionMenuListener) {
        this.onRVItemListener = onRVItemListener;
        this.onOptionMenuListener = onOptionMenuListener;
        // this.songList = songList;
        this.activity = activity;
        songList = new ArrayList<>();

    }

    public void setData(List<Song> songList) {

        this.songList = new ArrayList<>(songList);
        this.notifyDataSetChanged();
        //this.songList=new ArrayList<>(songList);
    }

    public interface OnRVItemListener {
        void onItemClick(int position);
    }

    public interface OnOptionMenuListener {
        void onItemClick(int position, int itemClicked);
    }


    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_line, parent, false);
        return new SongsViewHolder(layoutView, onRVItemListener, onOptionMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        holder.textView.setText(songList.get(position).getTitle());
        if (songList.get(position).getCoverImage() == null) {
            holder.imageView.setImageResource(R.drawable.unknown_album);
        } else {
            holder.imageView.setImageDrawable(songList.get(position).getCoverImage());
        }

    }

    public void updateCoverImage(int position,Drawable drawable) {
        songList.get(position).setCoverImage(drawable);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;
        OnRVItemListener onRVItemListener;
        ImageView optionMenu;

        public SongsViewHolder(@NonNull View itemView, OnRVItemListener onRVItemListener, OnOptionMenuListener onOptionMenuListener) {
            super(itemView);

            textView = itemView.findViewById(R.id.nameSong);
            imageView = itemView.findViewById(R.id.coverAlbum);
            optionMenu = itemView.findViewById(R.id.menuDropper);
            optionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(optionMenu.getContext(), itemView);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            onOptionMenuListener.onItemClick(getAdapterPosition(), item.getItemId());
                            return true;
                        }
                    });
                    popup.inflate(R.menu.option_item);
                    popup.setGravity(Gravity.RIGHT);
                    popup.show();

                }
            });
            this.onRVItemListener = onRVItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRVItemListener.onItemClick(getAdapterPosition());
        }
    }
}
