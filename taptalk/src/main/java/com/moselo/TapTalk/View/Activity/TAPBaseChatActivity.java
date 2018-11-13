package com.moselo.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Helper.SwipeBackLayout.SwipeBackActivity;

public abstract class TAPBaseChatActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.getInstance().dismissKeyboard(this);
    }

    protected abstract void initView();
}
