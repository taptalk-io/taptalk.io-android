package com.moselo.TapTalk.Listener;

import com.moselo.HomingPigeon.Interface.TapTalkDatabaseInterface;

import java.util.List;
import java.util.Map;

public abstract class TAPDatabaseListener<T> implements TapTalkDatabaseInterface<T> {
    @Override public void onSelectFinished(List<T> entities) {}
    @Override public void onInsertFinished() {}
    @Override public void onDeleteFinished() {}
    @Override public void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap) {}
    @Override public void onCountedUnreadCount(String roomID, int unreadCount) {}
    @Override public void onContactCheckFinished(int isContact) {}
}
