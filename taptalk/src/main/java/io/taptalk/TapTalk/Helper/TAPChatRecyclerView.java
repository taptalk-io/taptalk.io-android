package io.taptalk.TapTalk.Helper;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class TAPChatRecyclerView extends RecyclerView {
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
}