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
    private MessageRepository messageRepo;

    public static DatabaseManager getInstance(){
        if (null == instance)
            instance = new DatabaseManager();

        return instance;
    }

    public void setRepository(String databaseType, Application application){
        switch (databaseType){
            case MESSAGE_DB:
                messageRepo = new MessageRepository(application);
                break;
        }
    }

    public LiveData<List<MessageEntity>> getMessagesLiveData(){
        if (null != messageRepo)
            return messageRepo.getAllMessages();
        else
            throw new IllegalStateException("Message Repository was not initialized");
    }

    public void insert (MessageEntity messageEntity){
        if (null != messageRepo)
            messageRepo.insert(messageEntity);
        else
            throw new IllegalStateException("Message Repository was not initialized");
    }

    public void insert (List<MessageEntity> messageEntities){
        if (null != messageRepo)
            messageRepo.insert(messageEntities);
        else
            throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getMessages (HomingPigeonGetChatListener listener){
        if (null != messageRepo)
            messageRepo.getMessageList(listener);
        else
            throw new IllegalStateException("Message Repository was not initialized");
    }

    public void getMessages(HomingPigeonGetChatListener listener, long lastTimestamp){
        if (null != messageRepo)
            messageRepo.getMessageList(listener, lastTimestamp);
        else
            throw new IllegalStateException("Message Repository was not initialized");
    }

}
