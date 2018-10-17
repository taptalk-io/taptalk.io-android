package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.HomingPigeonDatabaseInterface;

import java.util.List;
import java.util.Map;

public abstract class HpDatabaseListener<T> implements HomingPigeonDatabaseInterface<T> {
    @Override public void onSelectFinished(List<T> entities) {}
    @Override public void onInsertFinished() {}
    @Override public void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap) {}
    @Override public void onCountedUnreadCount(String roomID, int unreadCount) {}
}
