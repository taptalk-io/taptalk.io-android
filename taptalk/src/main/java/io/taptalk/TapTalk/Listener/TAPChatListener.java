package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkChatInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;

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
    @Override public void onUserOnline(TAPOnlineStatusModel onlineStatus) {}
    @Override public void onUserOffline(Long lastActivity) {}
    @Override public void onReadMessage(String roomID) {}
}
