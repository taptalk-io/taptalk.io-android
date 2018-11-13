package com.moselo.TapTalk.Listener;

import com.moselo.HomingPigeon.Interface.TapTalkChatInterface;
import com.moselo.HomingPigeon.Model.TAPMessageModel;

public abstract class TAPChatListener implements TapTalkChatInterface {
    @Override public void onReceiveMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onDeleteMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onReceiveMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onDeleteMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onSendTextMessage(TAPMessageModel message) {}
    @Override public void onSendImageMessage(TAPMessageModel message) {}
    @Override public void onReplyMessage(TAPMessageModel message) {}
    @Override public void onRetrySendMessage(TAPMessageModel message) {}
    @Override public void onSendFailed(TAPMessageModel message) {}
    @Override public void onMessageRead(TAPMessageModel message) {}
    @Override public void onLayoutLoaded(TAPMessageModel message) {}
    @Override public void onBubbleExpanded() {}
    @Override public void onOutsideClicked() {}
    @Override public void onUserOnline() {}
    @Override public void onUserOffline(Long lastActivity) {}
}
