package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;

import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Interface.TapTalkDatabaseInterface;

@Keep
public abstract class TAPDatabaseListener<T> implements TapTalkDatabaseInterface<T> {
    @Override
    public void onSelectFinished(List<T> entities) {
    }

    @Override
    public void onSelectFinished(T entity) {
    }

    @Override
    public void onInsertFinished() {
    }

    @Override
    public void onDeleteFinished() {
    }

    @Override
    public void onSelectedRoomList(List<T> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
    }

    @Override
    public void onCountedUnreadCount(String roomID, int unreadCount, int mentionCount) {
    }

    @Override
    public void onCountedUnreadCount(int unreadCount) {
    }

    @Override
    public void onContactCheckFinished(int isContact) {
    }

    @Override
    public void onSelectFailed(String errorMessage) {
    }

    @Override
    public void onInsertFailed(String errorMessage) {
    }

    @Override
    public void onSelectFinishedWithUnreadCount(List<T> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {

    }
}
