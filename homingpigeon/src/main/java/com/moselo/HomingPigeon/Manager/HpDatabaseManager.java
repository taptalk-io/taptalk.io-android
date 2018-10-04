package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.Message.HpMessageRepository;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchRepository;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.MESSAGE_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.SEARCH_DB;

public class HpDatabaseManager {
    private static HpDatabaseManager instance;

    //for message Table
    private HpMessageRepository messageRepository;

    //for Recent Search Table
    private HpRecentSearchRepository searchRepository;

    public static HpDatabaseManager getInstance() {
        return (null == instance) ? (instance = new HpDatabaseManager()) : instance;
    }

    public void setRepository(String databaseType, Application application) {
        switch (databaseType) {
            case MESSAGE_DB:
                messageRepository = new HpMessageRepository(application);
                break;
            case SEARCH_DB:
                searchRepository = new HpRecentSearchRepository(application);
                break;
        }
    }

    public void insert(HpMessageEntity messageEntity) {
        if (null != messageRepository)
            messageRepository.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages, HpDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(HpRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.insert(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(String localID) {
        if (null != messageRepository)
            messageRepository.delete(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void delete(HpRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.delete(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(List<HpRecentSearchEntity> recentSearchEntities) {
        if (null != searchRepository)
            searchRepository.delete(recentSearchEntities);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void updatePendingStatus() {
        if (null != messageRepository)
            messageRepository.updatePendingStatus();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updatePendingStatus(String localID) {
        if (null != messageRepository)
            messageRepository.updatePendingStatus(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public LiveData<List<HpMessageEntity>> getMessagesLiveData() {
        if (null != messageRepository)
            return messageRepository.getAllMessages();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(String roomID, HpDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getMessageList(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(String roomID, HpDatabaseListener listener, long lastTimestamp) {
        if (null != messageRepository)
            messageRepository.getMessageList(roomID, listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(String myID, List<HpMessageEntity> saveMessages, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, saveMessages, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final HpDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getUnreadCountPerRoom(myID, roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public LiveData<List<HpRecentSearchEntity>> getRecentSearchLive() {
        if (null != searchRepository)
            return searchRepository.getAllRecentSearch();
        else throw new IllegalStateException("Recent Search Repository was not initialized");

    }

}
