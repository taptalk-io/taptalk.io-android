package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.MessageRepository;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.DatabaseType.MESSAGE_DB;

public class DatabaseManager {
    private static DatabaseManager instance;

    //for message Table
    private MessageRepository messageRepository;

    public static DatabaseManager getInstance(){
        return (null == instance) ? (instance = new DatabaseManager()) : instance;
    }

    public void setRepository(String databaseType, Application application){
        switch (databaseType){
            case MESSAGE_DB:
                messageRepository = new MessageRepository(application);
                break;
        }
    }

    public void insert(MessageEntity messageEntity){
        if (null != messageRepository)
            messageRepository.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void insert(List<MessageEntity> messageEntities) {
        if (null != messageRepository)
            messageRepository.insert(messageEntities);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void delete(String localID) {
        if (null != messageRepository)
            messageRepository.delete(localID);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
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

    public LiveData<List<MessageEntity>> getMessagesLiveData(){
        if (null != messageRepository)
            return messageRepository.getAllMessages();
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(HomingPigeonGetChatListener listener){
        if (null != messageRepository)
            messageRepository.getMessageList(listener);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

    public void getMessages(HomingPigeonGetChatListener listener, long lastTimestamp){
        if (null != messageRepository)
            messageRepository.getMessageList(listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized.");
    }

}
