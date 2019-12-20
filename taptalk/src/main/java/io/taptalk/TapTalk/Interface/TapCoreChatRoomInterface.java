package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapCoreChatRoomInterface {
    void onReceiveStartTyping(String roomID, TAPUserModel user);

    void onReceiveStopTyping(String roomID, TAPUserModel user);

    void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive);
}
