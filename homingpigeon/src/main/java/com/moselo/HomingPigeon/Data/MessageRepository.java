package com.moselo.HomingPigeon.Data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<MessageEntity>> allMessages;
    private List<MessageEntity> allMessageList = new ArrayList<>();

    public MessageRepository(Application application) {
        MessageDatabase db = MessageDatabase.getDatabase(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessage();
    }

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    public List<MessageEntity> getAllMessageList(final HomingPigeonGetChatListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allMessageList = messageDao.getAllMessageList();
                listener.onGetMessages(allMessageList);
            }
        }).start();
        return allMessageList;
    }

    public void insert (MessageEntity message) {
        new InsertAsyncTask(messageDao).execute(message);
    }

    private static class InsertAsyncTask extends AsyncTask<MessageEntity, Void, Void> {
        private MessageDao asyncTaskDao;

        InsertAsyncTask (MessageDao chatDao) {
            asyncTaskDao = chatDao;
        }

        @Override
        protected Void doInBackground(MessageEntity... messages) {
            asyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    public void insert(List<MessageEntity> messageEntities){
        new insertAsync(messageDao).execute(messageEntities);
    }

    private static class insertAsync extends AsyncTask<List<MessageEntity>, Void, Void> {
        private MessageDao mAsyncTaskDao;

        public insertAsync(MessageDao mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(List<MessageEntity>... lists) {
            if (lists.length > 0){
                mAsyncTaskDao.insert(lists[0]);
            }
            return null;
        }
    }
}
