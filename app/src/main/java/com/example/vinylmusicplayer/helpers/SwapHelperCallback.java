package com.example.vinylmusicplayer.helpers;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinylmusicplayer.classes.Song;
import com.example.vinylmusicplayer.viewmodels.ListViewModel;

import java.util.Collections;
import java.util.List;

public class SwapHelperCallback extends ItemTouchHelper.Callback {
    ItemTouchHelperAdapter adapter;
    List<Song> playQueue;
    ListViewModel model;
    RecyclerView recyclerView;
    boolean isDraggable;

    public SwapHelperCallback(RecyclerView recyclerView, ListViewModel model, ItemTouchHelperAdapter adapter) {
        this.recyclerView = recyclerView;
        this.model = model;
        this.adapter = adapter;
        isDraggable = false;
        this.playQueue = model.getCurrentPlayQueue().getValue();
        attachSwap();

    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getX() > 800) {
                isDraggable = true;
                Log.d("Location", event.getX() + "X");
            } else {
                isDraggable = false;
            }
            return true;
        }
    };

    public void attachSwap() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public List<Song> getPlayQueue() {
        return playQueue;
    }

    public void swapElements(int source, int dest) {
        if (source < dest) {
            for (int i = source; i < dest; i++) {
                Collections.swap(playQueue, i, i + 1);
            }
        } else {
            for (int i = source; i > dest; i--) {
                Collections.swap(playQueue, i, i - 1);
            }
        }
    }

//    @Override
//    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
//        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
//    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int source = viewHolder.getAdapterPosition();
        int dest = target.getAdapterPosition();
        swapElements(source, dest);
        adapter.onItemMove(source, dest);
        model.setPostCurrentPlayQueue(playQueue);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        playQueue.remove(position);
        adapter.onItemDismiss(position);
        model.setPostCurrentPlayQueue(playQueue);
    }

}
