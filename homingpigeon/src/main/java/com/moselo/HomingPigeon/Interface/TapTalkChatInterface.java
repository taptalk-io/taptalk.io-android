package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.HpMessageModel;

public interface TapTalkChatInterface {

    void onReceiveMessageInActiveRoom(HpMessageModel message);
    void onUpdateMessageInActiveRoom(HpMessageModel message);
    void onDeleteMessageInActiveRoom(HpMessageModel message);
    void onReceiveMessageInOtherRoom(HpMessageModel message);
    void onUpdateMessageInOtherRoom(HpMessageModel message);
    void onDeleteMessageInOtherRoom(HpMessageModel message);
    void onSendTextMessage(HpMessageModel message);
    void onSendImageMessage(HpMessageModel message);
    void onReplyMessage(HpMessageModel message);
    void onRetrySendMessage(HpMessageModel message);
    void onSendFailed(HpMessageModel message);
    void onMessageRead(HpMessageModel message);
    void onLayoutLoaded(HpMessageModel message);
    void onBubbleExpanded();
    void onOutsideClicked();
    void onUserOnline();
    void onUserOffline(Long lastActivity);
}
