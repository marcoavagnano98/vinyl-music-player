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
import com.example.vinylmusicplayer.classes.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistListAdapter  extends RecyclerView.Adapter<ArtistListAdapter.ArtistListViewHolder>{
    private List<Artist> artists;
    OnRVItemListener2 onRVItemListener2;
    public interface OnRVItemListener2 {
        void onItemClick(int position);
    }


    public ArtistListAdapter(OnRVItemListener2 onRVItemListener2) {
        this.artists = artists;
        this.onRVItemListener2=onRVItemListener2;
    }
    public void setData(List<Artist> artistList){
        this.artists=new ArrayList<>(artistList);
        this.notifyDataSetChanged();
        //this.songList=new ArrayList<>(songList);
    }


    @NonNull
    @Override
    public ArtistListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_line, parent, false);
        return new ArtistListAdapter.ArtistListViewHolder(layoutView,onRVItemListener2);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.ArtistListViewHolder holder, int position) {
        String name=artists.get(position).getName();
        List<String> ids=artists.get(position).getSongsId();
        holder.textView.setText(name);
        Log.d("InfoAbout",name + " " + ids.toString());

    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        ImageView imageView;
        OnRVItemListener2 onRVItemListener2;
        public ArtistListViewHolder(@NonNull View itemView,OnRVItemListener2 onRVItemListener2) {
            super(itemView);
            textView=itemView.findViewById(R.id.nameSong);
            imageView=itemView.findViewById(R.id.coverAlbum);
            this.onRVItemListener2=onRVItemListener2;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRVItemListener2.onItemClick(getAdapterPosition());
        }
    }

}
