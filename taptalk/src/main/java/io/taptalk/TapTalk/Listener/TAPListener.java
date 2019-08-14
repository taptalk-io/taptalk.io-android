package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TAPListener implements TapTalkInterface {
    @Override
    public void onRefreshAuthTicket() {
    }

    @Override
    public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {
    }

    @Override
    public void onNotificationReceived(TAPMessageModel messageModel) {
    }
}
