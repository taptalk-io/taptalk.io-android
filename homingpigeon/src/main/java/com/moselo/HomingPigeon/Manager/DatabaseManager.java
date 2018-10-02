package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Data.Message.MessageRepository;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchRepository;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.MESSAGE_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.SEARCH_DB;

public class DatabaseManager {
    private static DatabaseManager instance;

    //for message Table
    private MessageRepository messageRepository;

    //for Recent Search Table
    private RecentSearchRepository searchRepository;

    public static DatabaseManager getInstance() {
        return (null == instance) ? (instance = new DatabaseManager()) : instance;
    }

    public void setRepository(String databaseType, Application application) {
        switch (databaseType) {
            case MESSAGE_DB:
                messageRepository = new MessageRepository(application);
                break;
            case SEARCH_DB:
                searchRepository = new RecentSearchRepository(application);
                break;
        }
    }

    public void insert(MessageEntity messageEntity) {
        if (null != messageRepository)
            messageRepository.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<MessageEntity> messageEntities,  boolean isClearSaveMessages) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<MessageEntity> messageEntities,  boolean isClearSaveMessages, HomingPigeonDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(RecentSearchEntity recentSearchEntity) {
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

    public void delete(RecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.delete(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(List<RecentSearchEntity> recentSearchEntities) {
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

    public LiveData<List<MessageEntity>> getMessagesLiveData() {
        if (null != messageRepository)
            return messageRepository.getAllMessages();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(String roomID, HomingPigeonDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getMessageList(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(String roomID, HomingPigeonDatabaseListener listener, long lastTimestamp) {
        if (null != messageRepository)
            messageRepository.getMessageList(roomID, listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(List<MessageEntity> saveMessages, HomingPigeonDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(saveMessages, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(HomingPigeonDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public LiveData<List<RecentSearchEntity>> getRecentSearchLive() {
        if (null != searchRepository)
            return searchRepository.getAllRecentSearch();
        else throw new IllegalStateException("Recent Search Repository was not initialized");

    }

}
