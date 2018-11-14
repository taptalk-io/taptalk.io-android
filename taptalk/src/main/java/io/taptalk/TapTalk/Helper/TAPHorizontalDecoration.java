package io.taptalk.TapTalk.Helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TAPHorizontalDecoration extends RecyclerView.ItemDecoration {

    private int top, bottom, left, right, first, last, count;

    public TAPHorizontalDecoration(int left, int right, int first, int last, int count, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.first = first;
        this.last = last;
        this.count = count;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) outRect.left = first;
        else outRect.left = left;

        outRect.top = top;
        outRect.bottom = bottom;

        if (parent.getChildLayoutPosition(view) == count - 1) outRect.right = last;
        else outRect.right = right;
    }
}
