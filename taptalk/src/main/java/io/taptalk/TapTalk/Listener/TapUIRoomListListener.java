package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Keep;

import java.lang.ref.WeakReference;

import io.taptalk.TapTalk.Interface.TapUIRoomListInterface;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.R;

@Keep
public abstract class TapUIRoomListListener implements TapUIRoomListInterface {

    private String instanceKey = "";

    public TapUIRoomListListener() {
    }

    public TapUIRoomListListener(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        if (null != mainRoomListFragment) {
            mainRoomListFragment.showSearchChat();
        }
    }

    @Override
    public void onCloseRoomListTapped(Activity activity) {
        if (null != activity) {
            activity.finish();
        }
    }

    @Override
    public void onTapTalkAccountButtonTapped(Activity activity) {
        TAPMyAccountActivity.Companion.start(activity, instanceKey);
    }

    @Override
    public void onNewChatButtonTapped(Activity activity) {
        TAPNewChatActivity.start(activity, instanceKey);
    }
}
