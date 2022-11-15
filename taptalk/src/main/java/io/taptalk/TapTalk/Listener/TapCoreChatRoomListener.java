package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapCoreChatRoomInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TapCoreChatRoomListener implements TapCoreChatRoomInterface {
    
    @Override
    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {

    }

    @Override
    public void onReceiveStartTyping(String roomID, TAPUserModel user) {

    }

    @Override
    public void onReceiveStopTyping(String roomID, TAPUserModel user) {

    }

    @Override
    public void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive) {

    }
}
