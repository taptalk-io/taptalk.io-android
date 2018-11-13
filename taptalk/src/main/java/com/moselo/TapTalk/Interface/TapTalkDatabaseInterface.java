package com.moselo.TapTalk.Interface;

import java.util.List;
import java.util.Map;

public interface TapTalkDatabaseInterface<T> {
    void onSelectFinished(List<T> entities);
    void onInsertFinished();
    void onDeleteFinished();
    void onCountedUnreadCount(String roomID, int unreadCount);
    void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap);
    void onContactCheckFinished(int isContact);
}
