package com.example.vinylmusicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.classes.OnRVItemListener;
import com.example.vinylmusicplayer.classes.Song;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.AddToPlaylistViewHolder> {
    private List<Song> songList = new ArrayList<>();
    private List<Boolean> checkedList = new ArrayList<>();
    private OnRVItemListener onRVItemListener;
    private boolean isCheckedLine;

    public AddToPlaylistAdapter(OnRVItemListener onRVItemListener) {
        this.onRVItemListener = onRVItemListener;
    }


    @NonNull
    @Override
    public AddToPlaylistAdapter.AddToPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_line_checkable, viewGroup, false);
        return new AddToPlaylistAdapter.AddToPlaylistViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistViewHolder holder, int position) {
        if (songList.get(position).getArtistName() != null) {
            holder.artistNameText.setText(songList.get(position).getArtistName());
        }
        holder.songTitleText.setText(songList.get(position).getTitle());
        if (songList.get(position).getCoverImage() == null) {
            holder.imageView.setImageResource(R.drawable.unknown_album);
        } else {
            holder.imageView.setImageDrawable(songList.get(position).getCoverImage());
        }
        holder.checkBox.setChecked(checkedList.get(position));
    }

    public void setData(List<Song> songs, List<Boolean> checkedList) {
        this.songList = new ArrayList<>(songs);
        this.checkedList = new ArrayList<>(checkedList);
        this.notifyDataSetChanged();
    }

    public void check(int position) {
        if (!checkedList.get(position)) {
            checkedList.set(position, true);
        } else {
            checkedList.set(position, false);
        }
        this.notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class AddToPlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songTitleText;
        TextView artistNameText;
        ImageView imageView;
        CheckBox checkBox;

        public AddToPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitleText = itemView.findViewById(R.id.nameSong);
            artistNameText = itemView.findViewById(R.id.nameArtist);
            imageView = itemView.findViewById(R.id.coverAlbum);
            checkBox = itemView.findViewById(R.id.checkBoxSong);
            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRVItemListener.onItemClick(getAdapterPosition());
            check(getAdapterPosition());
        }
    }
}
