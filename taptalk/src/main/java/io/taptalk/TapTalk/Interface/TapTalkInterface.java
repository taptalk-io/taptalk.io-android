package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapTalkInterface {
    void onRefreshAuthTicket();
    void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount);
    void onNotificationReceived(TAPMessageModel messageModel);
}
