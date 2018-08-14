package com.moselo.HomingPigeon.Data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<MessageEntity>> mAllMessage;

    public MessageRepository(Application application) {
        MessageDatabase db = MessageDatabase.getDatabase(application);
        messageDao = db.messageDao();
        mAllMessage = messageDao.getAllMessage();
    }

    public LiveData<List<MessageEntity>> getmAllMessage() {
        return mAllMessage;
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
