package io.taptalk.TapTalk.Listener;

import io.taptalk.TapTalk.Interface.TapTalkChatInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;

public abstract class TAPChatListener implements TapTalkChatInterface {
    @Override public void onReceiveMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onDeleteMessageInActiveRoom(TAPMessageModel message) {}
    @Override public void onReceiveMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onUpdateMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onDeleteMessageInOtherRoom(TAPMessageModel message) {}
    @Override public void onSendMessage(TAPMessageModel message) {}
    @Override public void onReplyMessage(TAPMessageModel message) {}
    @Override public void onRetrySendMessage(TAPMessageModel message) {}
    @Override public void onSendFailed(TAPMessageModel message) {}
    @Override public void onMessageRead(TAPMessageModel message) {}
    @Override public void onLayoutLoaded(TAPMessageModel message) {}
    @Override public void onReceiveStartTyping(TAPTypingModel typingModel) {}
    @Override public void onReceiveStopTyping(TAPTypingModel typingModel) {}
    @Override public void onMessageQuoteClicked(TAPMessageModel message) {}
    @Override public void onOutsideClicked() {}
    @Override public void onBubbleExpanded() {}
    @Override public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {}
    @Override public void onReadMessage(String roomID) {}
    @Override public void onUnreadIdentifierShown() {}
}
