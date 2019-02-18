package io.taptalk.TapTalk.Interface;

import java.util.List;
import java.util.Map;

public interface TapTalkDatabaseInterface<T> {
    void onSelectFinished(List<T> entities);
    void onSelectFinished(T entity);
    void onInsertFinished();
    void onDeleteFinished();
    void onCountedUnreadCount(String roomID, int unreadCount);
    void onCountedUnreadCount(int unreadCount);
    void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap);
    void onContactCheckFinished(int isContact);
    void onSelectFailed(String errorMessage);
    void onInsertFailed(String errorMessage);
}
