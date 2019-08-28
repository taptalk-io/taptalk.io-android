package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

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
}
