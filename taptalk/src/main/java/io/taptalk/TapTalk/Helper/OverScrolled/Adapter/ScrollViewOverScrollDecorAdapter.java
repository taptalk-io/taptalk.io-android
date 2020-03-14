package io.taptalk.TapTalk.Helper.OverScrolled.Adapter;

import androidx.core.widget.NestedScrollView;
import android.view.View;

import io.taptalk.TapTalk.Helper.OverScrolled.IOverScrollDecoratorAdapter;

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
