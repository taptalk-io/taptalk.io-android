package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.MessageModel;

public interface HomingPigeonChatListener {

    void onReceiveTextMessageInActiveRoom(MessageModel message);

    void onReceiveTextMessageInOtherRoom(MessageModel message);

    void onSendTextMessage(MessageModel message);

    void onRetrySendMessage(MessageModel message);
}
