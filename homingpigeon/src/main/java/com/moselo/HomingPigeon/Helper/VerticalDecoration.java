package com.moselo.HomingPigeon.Helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalDecoration extends RecyclerView.ItemDecoration {

    private int top, bottom, index;

    public VerticalDecoration(int top, int bottom, int index) {
        this.top = top;
        this.bottom = bottom;
        this.index = index;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 0;
        outRect.right = 0;
        if (parent.getChildLayoutPosition(view) == index){
            outRect.top = top;
            outRect.bottom = bottom;
        } else {
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}