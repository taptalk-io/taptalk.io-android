package com.moselo.TapTalk.Interface;

import com.moselo.HomingPigeon.Model.TAPMessageModel;

public interface TapTalkChatInterface {

    void onReceiveMessageInActiveRoom(TAPMessageModel message);
    void onUpdateMessageInActiveRoom(TAPMessageModel message);
    void onDeleteMessageInActiveRoom(TAPMessageModel message);
    void onReceiveMessageInOtherRoom(TAPMessageModel message);
    void onUpdateMessageInOtherRoom(TAPMessageModel message);
    void onDeleteMessageInOtherRoom(TAPMessageModel message);
    void onSendTextMessage(TAPMessageModel message);
    void onSendImageMessage(TAPMessageModel message);
    void onReplyMessage(TAPMessageModel message);
    void onRetrySendMessage(TAPMessageModel message);
    void onSendFailed(TAPMessageModel message);
    void onMessageRead(TAPMessageModel message);
    void onLayoutLoaded(TAPMessageModel message);
    void onBubbleExpanded();
    void onOutsideClicked();
    void onUserOnline();
    void onUserOffline(Long lastActivity);
}
