package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;

import java.util.List;
import java.util.Map;

public interface HomingPigeonDatabaseInterface {
    void onSelectFinished(List<HpMessageEntity> entities);
    void onInsertFinished();
    void onCountedUnreadCount(String roomID, int unreadCount);
    void onSelectedRoomList(List<HpMessageEntity> entities, Map<String, Integer> unreadMap);
}
