package com.example.vinylmusicplayer.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.Album;
import com.example.vinylmusicplayer.classes.OnRVItemListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder> {
    private List<Album> album = new ArrayList<>();
    private OnRVItemListener onRVItemListener;
    public AlbumListAdapter(OnRVItemListener onRVItemListener){
        this.onRVItemListener=onRVItemListener;
    }



    @NonNull
    @Override
    public AlbumListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.songs_line, viewGroup, false);
        return new AlbumListViewHolder(layoutView,onRVItemListener);
    }

    public void setData(List<Album> albumList) {

        this.album = new ArrayList<>(albumList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListViewHolder holder, int i) {
        if (!album.isEmpty()) {
            String albumName = album.get(i).getTitle();
            holder.textView.setText(albumName);
            holder.cover.setImageDrawable(album.get(i).getCoverImage());
        }
    }

    @Override
    public int getItemCount() {
        return album.size();
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        private ImageView cover;
        private OnRVItemListener onRVItemListener;

        public AlbumListViewHolder(@NonNull View itemView, OnRVItemListener onRVItemListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.nameSong);
            cover = itemView.findViewById(R.id.coverAlbum);

            itemView.setOnClickListener(this);
            this.onRVItemListener=onRVItemListener;
        }

        @Override
        public void onClick(View v) {
            onRVItemListener.onItemClick(getAdapterPosition());
        }
    }
}
