package io.taptalk.TapTalk.View.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackActivity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;

public abstract class TAPBaseChatActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (TapTalk.getTapTalkScreenOrientation()) {
            case TapTalkOrientationPortrait:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case TapTalkOrientationLandscape:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.getInstance().dismissKeyboard(this);
    }
}
