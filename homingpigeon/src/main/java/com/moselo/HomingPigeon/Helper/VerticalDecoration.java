package com.moselo.HomingPigeon.Helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalDecoration extends RecyclerView.ItemDecoration {

    private int top, bottom, count;

    public VerticalDecoration(int top, int bottom, int count) {
        this.top = top;
        this.bottom = bottom;
        this.count = count;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) outRect.top = top;
        else outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
        if (parent.getChildLayoutPosition(view) == count - 1) outRect.bottom = bottom;
        else outRect.bottom = 0;
    }
}