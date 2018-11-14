package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackActivity;
import io.taptalk.TapTalk.Helper.TAPUtils;

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
