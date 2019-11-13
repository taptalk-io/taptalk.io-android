package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public interface TapTalkInterface {
    void onTapTalkRefreshTokenExpired();

    void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount);

    void onNotificationReceived(TAPMessageModel message);

    void onUserLogout();

    void onTaskRootChatRoomClosed(Activity activity);
}
