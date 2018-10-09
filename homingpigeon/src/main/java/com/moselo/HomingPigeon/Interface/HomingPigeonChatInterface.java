package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.HpMessageModel;

public interface HomingPigeonChatInterface {

    void onReceiveMessageInActiveRoom(HpMessageModel message);

    void onUpdateMessageInActiveRoom(HpMessageModel message);

    void onDeleteMessageInActiveRoom(HpMessageModel message);

    void onReceiveMessageInOtherRoom(HpMessageModel message);

    void onUpdateMessageInOtherRoom(HpMessageModel message);

    void onDeleteMessageInOtherRoom(HpMessageModel message);

    void onSendTextMessage(HpMessageModel message);

    void onRetrySendMessage(HpMessageModel message);

    void onSendFailed(HpMessageModel message);

    void onMessageClicked(HpMessageModel message, boolean isExpanded);
}
