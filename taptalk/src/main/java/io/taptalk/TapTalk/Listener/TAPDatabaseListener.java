package io.taptalk.TapTalk.Listener;

import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Interface.TapTalkDatabaseInterface;

public abstract class TAPDatabaseListener<T> implements TapTalkDatabaseInterface<T> {
    @Override public void onSelectFinished(List<T> entities) {}
    @Override public void onSelectFinished(T entity) {}
    @Override public void onInsertFinished() {}
    @Override public void onDeleteFinished() {}
    @Override public void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap) {}
    @Override public void onCountedUnreadCount(String roomID, int unreadCount) {}
    @Override public void onCountedUnreadCount(int unreadCount) {}
    @Override public void onContactCheckFinished(int isContact) {}
    @Override public void onSelectFailed(String errorMessage) {}
    @Override public void onInsertFailed(String errorMessage) {}
}
