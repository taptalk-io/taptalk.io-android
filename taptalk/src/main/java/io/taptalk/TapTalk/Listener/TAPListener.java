package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkInterface;

public abstract class TAPListener implements TapTalkInterface {
    @Override
    public void onRefreshAuthTicket() {
    }

    @Override
    public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {
    }
}
