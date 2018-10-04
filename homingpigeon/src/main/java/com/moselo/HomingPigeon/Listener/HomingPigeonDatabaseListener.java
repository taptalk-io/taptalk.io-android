package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;

import java.util.List;
import java.util.Map;

public interface HomingPigeonDatabaseListener {
    void onSelectFinished(List<HpMessageEntity> entities);
    void onInsertFinished();
    void onSelectedRoomList(List<HpMessageEntity> entities, Map<String, Integer> unreadMap);
}
