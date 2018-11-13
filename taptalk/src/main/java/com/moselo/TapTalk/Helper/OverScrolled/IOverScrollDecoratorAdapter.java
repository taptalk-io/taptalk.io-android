package com.moselo.TapTalk.Helper.OverScrolled;

import android.view.View;

public interface IOverScrollDecoratorAdapter {
    View getView();

    boolean isInAbsoluteStart();

    boolean isInAbsoluteEnd();
}
