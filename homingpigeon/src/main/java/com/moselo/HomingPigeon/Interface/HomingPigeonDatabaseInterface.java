package com.moselo.HomingPigeon.Interface;

import java.util.List;
import java.util.Map;

public interface HomingPigeonDatabaseInterface<T> {
    void onSelectFinished(List<T> entities);
    void onInsertFinished();
    void onCountedUnreadCount(String roomID, int unreadCount);
    void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap);
    void onContactCheckFinished(int isContact);
}
