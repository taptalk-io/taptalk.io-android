package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;

import java.util.List;

public interface HomingPigeonDatabaseListener {
    void onSelectFinished(List<MessageEntity> entities);
}
