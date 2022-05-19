package com.example.vinylmusicplayer.helpers;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    private final RecyclerView recyclerView;
    private final int regionWidth;
    private final GestureDetector gestureDetector;
    private List<SwipedRegion> regionList;
    private int swipePosition;
    private float swipeThreshold = 0.3f;
    private final Map<Integer, List<SwipedRegion>> regionBuffer;
    private final Queue<Integer> removerQueue;
    private final int maxIconSize = 160;
    private final float percentIconResize = 0.8f; //set icon size as 80% of rect height
    public final int endSwipe = -1;

    public SwipeHelper(Context context, RecyclerView recyclerView, int regionWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.swipePosition = endSwipe;
        this.regionWidth = regionWidth;
        this.regionList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.regionBuffer = new HashMap<>();
        removerQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer element) {
                if (contains(element)) {
                    return false;
                } else {
                    return super.add(element);
                }
            }
        };
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * when a region is clicked buffer list is checked to find out which one contains point clicked
     */
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (SwipedRegion region : regionList) {
                if (region.clickManager(e.getX(), e.getY(), PressType.ONECLICK)) {
                    break;
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            for (SwipedRegion region : regionList) {
                if (region.clickManager(e.getX(), e.getY(), PressType.LONGPRESS)) {
                    break;
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (swipePosition < 0) {
                return false;
            } else if (recyclerView.findViewHolderForAdapterPosition(swipePosition) == null) {
                return false;
            }
            Point point = new Point((int) event.getRawX(), (int) event.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);
            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y && point.x > (rect.right - (regionWidth * regionList.size()))) { //gesture must handle only in region
                    gestureDetector.onTouchEvent(event);
                } else {
                    removerQueue.add(swipePosition);
                    recoverySwipedItem();
                    swipePosition = endSwipe;
                }
            }
            return false;
        }
    };

    private void recoverySwipedItem() {
        while (!removerQueue.isEmpty()) {
            int pos = removerQueue.poll(); //remove head elements and return it
            if (pos > endSwipe) {
                Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (swipePosition != pos) {
            removerQueue.add(swipePosition);
        }
        swipePosition = pos;
        if (regionBuffer.containsKey(swipePosition)) {
            regionList = regionBuffer.get(swipePosition);
        } else {
            regionList.clear();
        }
        regionBuffer.clear();
        swipeThreshold = 0.1f * regionList.size() * regionWidth;
        recoverySwipedItem();
    }

    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 3.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0) {
            swipePosition = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<SwipedRegion> buffer = new ArrayList<>();
                if (!regionBuffer.containsKey(pos)) {
                    instantiateSwipedRegion(viewHolder, buffer);
                    regionBuffer.put(pos, buffer);
                } else {
                    buffer = regionBuffer.get(pos);
                }
                translationX = dX * buffer.size() * regionWidth / itemView.getWidth();
                drawSwipedRegion(c, itemView, buffer, pos, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawSwipedRegion(Canvas c, View itemView, List<SwipedRegion> buffer, int pos, float translationX) {
        float right = itemView.getRight();
        float dRegionWidth = -1 * translationX / buffer.size();
        for (SwipedRegion region : buffer) {
            float left = right - dRegionWidth;
            region.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public void setSwipePosition(int swipePosition) {
        this.swipePosition = swipePosition;
    }

    public abstract void instantiateSwipedRegion(RecyclerView.ViewHolder viewHolder, List<SwipedRegion> buffer);

    public interface SwipedRegionListener {
        void onClick(int pos);

    }

    private enum PressType {
        ONECLICK,
        LONGPRESS
    }

    public class SwipedRegion {
        private final String text;
        private final int imageId;
        private final int textSize;
        private final int color;
        private int pos;
        private RectF clickRegion;
        private final Context context;
        private final SwipedRegionListener listener;


        public SwipedRegion(Context context, String text, int imageId, int textSize, int color, SwipedRegionListener listener) {
            this.text = text;
            this.imageId = imageId;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            this.context = context;

        }

        public boolean clickManager(float x, float y, PressType pressType) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                if (pressType == PressType.ONECLICK) {
                    listener.onClick(pos);
                }
//                if (pressType == PressType.LONGPRESS) {
//                    listener.onLongPress(pos);
//                }
                return true;
            }
            return false;
        }


        public void onDraw(Canvas c, RectF rectF, int pos) {
            Paint p = new Paint();
            p.setColor(color);
            c.drawRect(rectF, p);
            p.setColor(Color.WHITE);
            p.setTextSize(textSize);
            Rect r = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x = 0;
            float y = 0;
            if (imageId == 0) {   //display only text in swiped region
                x = cWidth / 2f - r.width() / 2f - r.left;
                y = cHeight / 2f + r.height() / 2f - r.bottom;
                c.drawText(text, rectF.left + x, rectF.top + y, p);
            } else {
                Drawable d = ContextCompat.getDrawable(context, imageId);
                Bitmap bitmap = drawableToBitmap(d);
//                int iconSize = (int) (cHeight * percentIconResize); //set icon size
                int iconSize = 60;
//                if (iconSize > maxIconSize) {
//                    iconSize = maxIconSize;
//                }
                //   Log.d("SizeIcon", cHeight + " - " + cWidth + " " + iconSize);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true); //resized icon based on rect sizes
                float bw = resized.getWidth() / 2f;
                float bh = resized.getHeight() / 2f;
                c.drawBitmap(resized, ((rectF.left + rectF.right) / 2) - bw, ((rectF.top + rectF.bottom) / 2 - bh), p);
            }
            clickRegion = rectF;
            this.pos = pos;

        }

        private Bitmap drawableToBitmap(Drawable d) {
            if (d instanceof BitmapDrawable) {
                return ((BitmapDrawable) d).getBitmap();
            }
            Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
            return bitmap;
        }
    }
}
