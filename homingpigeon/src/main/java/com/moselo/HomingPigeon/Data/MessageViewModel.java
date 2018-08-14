package com.moselo.HomingPigeon.Data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private MessageRepository mRepository;
    private LiveData<List<MessageEntity>> mAllMessage;

    public MessageViewModel(Application application) {
        super(application);
        mRepository = new MessageRepository(application);
        mAllMessage = mRepository.getmAllMessage();
    }

    public LiveData<List<MessageEntity>> getmAllMessage() {
        return mAllMessage;
    }

    public void insert (List<MessageEntity> messageEntities){
        mRepository.insert(messageEntities);
    }
}
