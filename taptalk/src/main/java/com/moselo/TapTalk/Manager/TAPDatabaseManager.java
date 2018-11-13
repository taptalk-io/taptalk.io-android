package com.moselo.TapTalk.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.Contact.TAPMyContactRepository;
import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Data.Message.TAPMessageRepository;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchRepository;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;
import com.moselo.HomingPigeon.Model.TAPUserModel;

import java.util.List;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;

public class TAPDatabaseManager {
    private static TAPDatabaseManager instance;

    //for message Table
    private TAPMessageRepository messageRepository;

    //for Recent Search Table
    private TAPRecentSearchRepository searchRepository;

    //for MyContact Table
    private TAPMyContactRepository myContactRepository;

    public static TAPDatabaseManager getInstance() {
        return (null == instance) ? (instance = new TAPDatabaseManager()) : instance;
    }

    public void setRepository(String databaseType, Application application) {
        switch (databaseType) {
            case MESSAGE_DB:
                messageRepository = new TAPMessageRepository(application);
                break;
            case SEARCH_DB:
                searchRepository = new TAPRecentSearchRepository(application);
                break;
            case MY_CONTACT_DB:
                myContactRepository = new TAPMyContactRepository(application);
        }
    }

    /**
     * ==============================================================
     * Message Table
     * ==============================================================
     */

    public void deleteMessage(List<TAPMessageEntity> messageEntities, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.delete(messageEntities, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(TAPMessageEntity messageEntity) {
        if (null != messageRepository)
            messageRepository.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities, isClearSaveMessages, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void delete(String localID) {
        if (null != messageRepository)
            messageRepository.delete(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void deleteAllMessage() {
        if (null != messageRepository)
            messageRepository.deleteAllMessage();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void updatePendingStatus(String localID) {
        if (null != messageRepository)
            messageRepository.updatePendingStatus(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public LiveData<List<TAPMessageEntity>> getMessagesLiveData() {
        if (null != messageRepository)
            return messageRepository.getAllMessages();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesDesc(String roomID, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getMessageListDesc(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesDesc(String roomID, TAPDatabaseListener listener, long lastTimestamp) {
        if (null != messageRepository)
            messageRepository.getMessageListDesc(roomID, listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessagesAsc(String roomID, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getMessageListAsc(roomID, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void searchAllMessages(String keyword, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.searchAllMessages(keyword, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(String myID, List<TAPMessageEntity> saveMessages, boolean isCheckUnreadFirst, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, saveMessages, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getRoomList(myID, isCheckUnreadFirst, listener);
        else throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void searchAllRooms(String myID, String keyword, TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.searchAllChatRooms(myID, keyword, listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final TAPDatabaseListener listener) {
        if (null != messageRepository)
            messageRepository.getUnreadCountPerRoom(myID, roomID, listener);
        else throw new IllegalStateException("Message Repository was not initialized");
    }

    public void updatePendingStatus() {
        if (null != messageRepository)
            messageRepository.updatePendingStatus();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    /**
     * ==============================================================
     * Recent Search Table
     * ==============================================================
     */

    public void insert(TAPRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.insert(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(TAPRecentSearchEntity recentSearchEntity) {
        if (null != recentSearchEntity)
            searchRepository.delete(recentSearchEntity);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void delete(List<TAPRecentSearchEntity> recentSearchEntities) {
        if (null != searchRepository)
            searchRepository.delete(recentSearchEntities);
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public void deleteAllRecentSearch() {
        if (null != searchRepository)
            searchRepository.deleteAllRecentSearch();
        else
            throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchLive() {
        if (null != searchRepository)
            return searchRepository.getAllRecentSearch();
        else throw new IllegalStateException("Recent Search Repository was not initialized");
    }

    /**
     * ==============================================================
     * My Contact Table
     * ==============================================================
     */

    public void getMyContactList(TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.getAllMyContactList(listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public LiveData<List<TAPUserModel>> getMyContactList() {
        if (null != myContactRepository)
            return myContactRepository.getMyContactListLive();
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void searchAllMyContacts(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.searchAllMyContacts(keyword, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(TAPDatabaseListener<TAPUserModel> listener, TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(listener, userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertMyContact(List<TAPUserModel> userModels) {
        if (null != myContactRepository)
            myContactRepository.insert(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void insertAndGetMyContact(List<TAPUserModel> userModels, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.insertAndGetContact(userModels, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteMyContact(TAPUserModel... userModels) {
        if (null != myContactRepository)
            myContactRepository.delete(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteMyContact(List<TAPUserModel> userModels) {
        if (null != myContactRepository)
            myContactRepository.delete(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void deleteAllContact() {
        if (null != myContactRepository)
            myContactRepository.deleteAllContact();
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void updateMyContact(TAPUserModel userModels) {
        if (null != myContactRepository)
            myContactRepository.update(userModels);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }

    public void checkUserInMyContacts(String userID, TAPDatabaseListener<TAPUserModel> listener) {
        if (null != myContactRepository)
            myContactRepository.checkUserInMyContacts(userID, listener);
        else throw new IllegalStateException("My Contact Repository was not initialized");
    }
}
