package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.MessageModel;

public interface HomingPigeonChatListener {

    void onReceiveMessageInActiveRoom(MessageModel message);

    void onUpdateMessageInActiveRoom(MessageModel message);

    void onDeleteMessageInActiveRoom(MessageModel message);

    void onReceiveMessageInOtherRoom(MessageModel message);

    void onUpdateMessageInOtherRoom(MessageModel message);

    void onDeleteMessageInOtherRoom(MessageModel message);

    void onSendTextMessage(MessageModel message);

    void onRetrySendMessage(MessageModel message);

    void onSendFailed(MessageModel message);

    void onMessageClicked(MessageModel message, boolean isExpanded);
}
