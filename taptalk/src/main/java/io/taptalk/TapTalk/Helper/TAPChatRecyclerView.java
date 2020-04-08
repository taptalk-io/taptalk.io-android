package io.taptalk.TapTalk.Helper;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;

public class TAPChatRecyclerView extends RecyclerView {

    private ItemTouchHelper swipeHelper;
//    private TAPSwipeHelper.SwipeControllerActions activitySwipeReplyInterface;
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
            Log.e("TAPChatRecyclerView", "onDraw: ");
            swipeHelper.onDraw(c, this, null);
        }
        super.onDraw(c);
    }

    public void setupSwipeHelper(Context context, TAPSwipeHelper.SwipeControllerActions swipeReplyInterface) {
//        activitySwipeReplyInterface = swipeReplyInterface;
        swipeHelper = new ItemTouchHelper(new TAPSwipeHelper(context, swipeReplyInterface));
        swipeHelper.attachToRecyclerView(this);
    }
}
