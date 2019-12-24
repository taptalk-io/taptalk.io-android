package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapCoreChatRoomInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TapCoreChatRoomListener implements TapCoreChatRoomInterface {

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
