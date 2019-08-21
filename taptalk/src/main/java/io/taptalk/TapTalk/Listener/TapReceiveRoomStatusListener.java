package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapReceiveRoomStatusInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TapReceiveRoomStatusListener implements TapReceiveRoomStatusInterface {

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
