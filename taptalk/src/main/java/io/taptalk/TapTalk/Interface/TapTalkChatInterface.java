package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;

public interface TapTalkChatInterface {
    void onReceiveMessageInActiveRoom(TAPMessageModel message);
    void onUpdateMessageInActiveRoom(TAPMessageModel message);
    void onDeleteMessageInActiveRoom(TAPMessageModel message);
    void onReceiveMessageInOtherRoom(TAPMessageModel message);
    void onUpdateMessageInOtherRoom(TAPMessageModel message);
    void onDeleteMessageInOtherRoom(TAPMessageModel message);
    void onSendMessage(TAPMessageModel message);
    void onReplyMessage(TAPMessageModel message);
    void onRetrySendMessage(TAPMessageModel message);
    void onSendFailed(TAPMessageModel message);
    void onMessageRead(TAPMessageModel message);
    void onLayoutLoaded(TAPMessageModel message);
    void onReceiveStartTyping(TAPTypingModel typingModel);
    void onReceiveStopTyping(TAPTypingModel typingModel);
    void onMessageQuoteClicked(TAPMessageModel message);
    void onGroupMemberAvatarClicked(TAPMessageModel message);
    void onOutsideClicked();
    void onBubbleExpanded();
    void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus);
    void onReadMessage(String roomID);
}
