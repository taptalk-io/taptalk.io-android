package com.moselo.HomingPigeon.Helper.OverScrolled;

import android.view.View;

public interface IOverScrollDecor {
    View getView();

    void setOverScrollStateListener(IOverScrollStateListener listener);
    void setOverScrollUpdateListener(IOverScrollUpdateListener listener);

    int getCurrentState();

    void detach();
}
