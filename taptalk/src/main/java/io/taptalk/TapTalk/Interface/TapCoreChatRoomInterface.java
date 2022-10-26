package io.taptalk.TapTalk.Interface;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapCoreChatRoomInterface {
    void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser);

    void onReceiveStartTyping(String roomID, TAPUserModel user);

    void onReceiveStopTyping(String roomID, TAPUserModel user);

    void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive);
}
