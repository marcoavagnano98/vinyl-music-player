package com.example.vinylmusicplayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.helpers.ItemTouchHelperAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayQueueAdapter extends RecyclerView.Adapter<PlayQueueAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    OnRVItemListener onRVItemListener;
    Activity activity;
    List<Song> queue;
    public PlayQueueAdapter(Activity activity, OnRVItemListener onRVItemListener){
        this.activity=activity;
        this.onRVItemListener=onRVItemListener;
    }
    public void setData(List<Song> songList) {

        this.queue = new ArrayList<>(songList);
        this.notifyDataSetChanged();

        //this.songList=new ArrayList<>(songList);
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_line, parent, false);
        return new ItemViewHolder(layoutView,onRVItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
//        holder.cardLine.setCardBackgroundColor(
//                ContextCompat.getColor(activity.getApplicationContext(),R.color.darkGray));
        if(queue.get(position).getArtistName() != null){
            holder.artistNameText.setText(queue.get(position).getArtistName());
        }
        holder.songTitleText.setText(queue.get(position).getTitle());
        if (queue.get(position).getCoverImage() == null) {
            holder.cover.setImageResource(R.drawable.unknown_album);
        } else {
            holder.cover.setImageDrawable(queue.get(position).getCoverImage());
        }
        holder.swapImage.setImageResource(R.drawable.ic_baseline_swap_vert_24);
    }


    @Override
    public int getItemCount() {
        return queue.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(queue,fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        queue.remove(position);
        notifyItemRemoved(position);
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialCardView cardLine;
        TextView songTitleText;
        TextView artistNameText;
        ImageView cover;
        ImageView swapImage;
        OnRVItemListener onRVItemListener;


        public ItemViewHolder(@NonNull View itemView,OnRVItemListener onRVItemListener) {
            super(itemView);
            cardLine=itemView.findViewById(R.id.single_card);
            songTitleText = itemView.findViewById(R.id.nameSong);
            artistNameText=itemView.findViewById(R.id.nameArtist);
            cover = itemView.findViewById(R.id.coverAlbum);
            swapImage=itemView.findViewById(R.id.menuDropper);
            this.onRVItemListener=onRVItemListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onRVItemListener.onItemClick(getAdapterPosition());
        }
    }
}
