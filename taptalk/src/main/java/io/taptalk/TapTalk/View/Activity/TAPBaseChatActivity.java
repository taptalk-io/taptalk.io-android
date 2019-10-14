package io.taptalk.TapTalk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackActivity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPChatManager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;

public abstract class TAPBaseChatActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        TAPUtils.getInstance().dismissKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(RELOAD_ROOM_LIST);
        intent.putExtra(ROOM_ID, TAPChatManager.getInstance().getOpenRoom());
        LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);
    }
}
