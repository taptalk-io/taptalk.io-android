package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;

import java.util.List;

public interface HomingPigeonGetChatListener {
    void onGetMessages(List<MessageEntity> entities);
}
