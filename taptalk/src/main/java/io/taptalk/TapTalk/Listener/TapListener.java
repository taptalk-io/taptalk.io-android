package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapListener implements TapTalkInterface {
    @Override
    public void onTapTalkRefreshTokenExpired() {
    }

    @Override
    public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {
    }

    @Override
    public void onNotificationReceived(TAPMessageModel message) {
    }

    @Override
    public void onUserLogout() {
    }
}
