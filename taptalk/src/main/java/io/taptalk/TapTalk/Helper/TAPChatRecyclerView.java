package io.taptalk.TapTalk.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_SCROLL;
import static android.view.MotionEvent.ACTION_UP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;

public class TAPChatRecyclerView extends RecyclerView {

    private ItemTouchHelper swipeHelper;
    private TAPSwipeHelper swipeCallback;
//    private TAPSwipeHelper.SwipeControllerActions activitySwipeReplyInterface;
    private int oldHeight;
    private int latestMotionEventAction = 1;

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
        if (null != swipeHelper && latestMotionEventAction == ACTION_MOVE) {
            if (!swipeCallback.currentViewHolderSwiped()) {
                Log.e("TAPChatRecyclerView", "onDraw: ");
                swipeHelper.onDraw(c, this, null);
            } else {
                getParent().requestDisallowInterceptTouchEvent(true);
                return;
            }
        }
        super.onDraw(c);
    }

    public void setupSwipeHelper(Context context, TAPSwipeHelper.SwipeControllerActions swipeReplyInterface) {
//        activitySwipeReplyInterface = swipeReplyInterface;
        swipeCallback = new TAPSwipeHelper(context, swipeReplyInterface);
        swipeHelper = new ItemTouchHelper(swipeCallback);
        swipeHelper.attachToRecyclerView(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.e("TAPChatRecyclerView", "onTouchEvent: " + e.getAction());
        if (null != getParent()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (e.getAction() == ACTION_UP && null != swipeHelper) {
            post(() -> {
                Log.e("TAPChatRecyclerView", "onTouchEvent: detach");
                swipeHelper.attachToRecyclerView(new RecyclerView(getContext()));
                post(() -> {
                    Log.e("TAPChatRecyclerView", "onTouchEvent: re-attach");
                    swipeHelper.attachToRecyclerView(this);
                });
            });
        }
        this.latestMotionEventAction = e.getAction();
        return super.onTouchEvent(e);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (null != getParent()) {
            Log.e("TAPChatRecyclerView", "onScrolled: ");
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        super.onScrolled(dx, dy);
    }
}
