package io.taptalk.TapTalk.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.ACTION_UP;

public class TAPChatRecyclerView extends RecyclerView {

    private ItemTouchHelper swipeHelper;
    private int oldHeight;

    public TAPChatRecyclerView(Context context) {
        super(context);
    }

    public TAPChatRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TAPChatRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int delta = b - t - this.oldHeight;
        this.oldHeight = b - t;
        if (delta < 0) {
            this.scrollBy(0, -delta);
        }
    }

    @Override
    public void onDraw(Canvas c) {
        if (null != swipeHelper) {
            swipeHelper.onDraw(c, this, null);
        }
        super.onDraw(c);
    }

    public void setupSwipeHelper(Context context, TAPSwipeReplyCallback.SwipeReplyInterface swipeReplyInterface) {
        swipeHelper = new ItemTouchHelper( new TAPSwipeReplyCallback(context, swipeReplyInterface));
        swipeHelper.attachToRecyclerView(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.e("TAPChatRecyclerView", "onTouchEvent: " + e.getAction());
        if (e.getAction() == ACTION_UP && null != swipeHelper) {
            // Re-attach swipe helper on when user lifts touch
            post(() -> {
                Log.e("TAPChatRecyclerView", "onTouchEvent: detach");
                swipeHelper.attachToRecyclerView(new RecyclerView(getContext()));
                post(() -> {
                    Log.e("TAPChatRecyclerView", "onTouchEvent: re-attach");
                    swipeHelper.attachToRecyclerView(this);
                });
            });
        }
        return super.onTouchEvent(e);
    }
}
