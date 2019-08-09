package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkChatInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

public abstract class TapReceiveMessageListener implements TapTalkChatInterface {
    @Override public void onReceiveMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onReceiveMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInOtherRoom(TAPMessageModel message) {}
}
