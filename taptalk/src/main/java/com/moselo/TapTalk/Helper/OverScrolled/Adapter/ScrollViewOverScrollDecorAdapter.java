package com.moselo.TapTalk.Helper.OverScrolled.Adapter;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.moselo.HomingPigeon.Helper.OverScrolled.IOverScrollDecoratorAdapter;

public class ScrollViewOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final NestedScrollView mView;

    public ScrollViewOverScrollDecorAdapter(NestedScrollView view) {
        mView = view;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return !mView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return !mView.canScrollVertically(1);
    }
}
