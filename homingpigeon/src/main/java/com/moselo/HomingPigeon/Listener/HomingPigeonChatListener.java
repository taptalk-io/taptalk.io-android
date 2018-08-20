package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.MessageModel;

public interface HomingPigeonChatListener {

    void onNewTextMessage(MessageModel message);
}
