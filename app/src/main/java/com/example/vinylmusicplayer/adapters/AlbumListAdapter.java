package com.example.vinylmusicplayer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.Album;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder> {
    private List<Album> album = new ArrayList<>();
    OnCoverChanged onCoverChanged;

    public AlbumListAdapter(OnCoverChanged onCoverChanged){
        this.onCoverChanged=onCoverChanged;
    }

    @NonNull
    @Override
    public AlbumListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_cell, viewGroup, false);
        return new AlbumListViewHolder(layoutView);
    }
    public interface OnCoverChanged{
        void onChanged(String id,Drawable drawable);
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

            try {
                Drawable cover=drawableFromUrl(album.get(i).getUrlImage());
                album.get(i).setCoverImage(cover);
                holder.imageButt.setImageDrawable(cover);
                onCoverChanged.onChanged(album.get(i).getId(),cover);

            } catch (IOException e) {
                e.printStackTrace();
                holder.imageButt.setImageResource(R.drawable.unknown_album);
            }
        }
    }
    public Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }
    @Override
    public int getItemCount() {
        return album.size();
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageButton imageButt;

        public AlbumListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.albumText);
            imageButt = itemView.findViewById(R.id.cover);
        }
    }
}
