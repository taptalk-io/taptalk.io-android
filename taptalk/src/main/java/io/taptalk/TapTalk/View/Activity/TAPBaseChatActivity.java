package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackActivity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TapUI;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;

@Deprecated
public abstract class TAPBaseChatActivity extends SwipeBackActivity {

    public String instanceKey = "";

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

        // Show/hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            Boolean allActionBarEnabled = TapUI.getInstance(instanceKey).isAllTapTalkActivityActionBarEnabled();
            Boolean chatRoomActionBarEnabled = TapUI.getInstance(instanceKey).isTapTalkChatActivityActionBarEnabled();
            Boolean actionBarEnabled = null;
            if (null != allActionBarEnabled && null != chatRoomActionBarEnabled) {
                actionBarEnabled = allActionBarEnabled || chatRoomActionBarEnabled;
            } else if (null != allActionBarEnabled) {
                actionBarEnabled = allActionBarEnabled;
            } else if (null != chatRoomActionBarEnabled) {
                actionBarEnabled = chatRoomActionBarEnabled;
            }
            if (null != actionBarEnabled) {
                if (actionBarEnabled) {
                    actionBar.show();
                } else {
                    actionBar.hide();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.dismissKeyboard(this);
    }
}
