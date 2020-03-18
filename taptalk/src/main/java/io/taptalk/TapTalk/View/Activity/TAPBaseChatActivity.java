package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackActivity;
import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;

public abstract class TAPBaseChatActivity extends SwipeBackActivity {

    String instanceKey = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instanceKey = getIntent().getStringExtra(INSTANCE_KEY);

//        switch (TapTalk.getTapTalkScreenOrientation()) {
//            case TapTalkOrientationPortrait:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case TapTalkOrientationLandscape:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                break;
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.dismissKeyboard(this);
    }
}
