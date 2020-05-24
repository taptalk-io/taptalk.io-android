package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapListener implements TapTalkInterface {

    private String instanceKey = "";

    public TapListener() {
    }

    public TapListener(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onTapTalkRefreshTokenExpired() {
    }

    @Override
    public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {
    }

    @Override
    public void onNotificationReceived(TAPMessageModel message) {
        TapTalk.showTapTalkNotification(instanceKey, message);
    }

    @Override
    public void onUserLogout() {
    }

    @Override
    public void onTaskRootChatRoomClosed(Activity activity) {

    }
}
