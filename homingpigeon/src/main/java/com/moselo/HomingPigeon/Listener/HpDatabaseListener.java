package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Interface.HomingPigeonDatabaseInterface;

import java.util.List;
import java.util.Map;

public abstract class HpDatabaseListener implements HomingPigeonDatabaseInterface {
    @Override public void onSelectFinished(List<HpMessageEntity> entities) {}
    @Override public void onInsertFinished() {}
    @Override public void onSelectedRoomList(List<HpMessageEntity> entities, Map<String, Integer> unreadMap) {}
    @Override public void onCountedUnreadCount(String roomID, int unreadCount) {}
}
