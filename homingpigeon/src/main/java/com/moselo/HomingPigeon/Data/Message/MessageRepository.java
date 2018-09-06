package com.moselo.HomingPigeon.Data.Message;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private MessageDao messageDao;
    private LiveData<List<MessageEntity>> allMessages;
    private List<MessageEntity> allMessageList = new ArrayList<>();

    public MessageRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessage();
    }

    public void insert(MessageEntity message) {
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void getMessageList(final String roomID, final HomingPigeonGetChatListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allMessageList = messageDao.getAllMessageList(roomID);
                listener.onGetMessages(allMessageList);
            }
        }).start();
    }

    public void getMessageList(final String roomID, final HomingPigeonGetChatListener listener, final long lastTimestamp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MessageEntity> entities = messageDao.getAllMessageTimeStamp(lastTimestamp, roomID);
                listener.onGetMessages(entities);
            }
        }).start();
    }

    public void delete(final String localID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageDao.delete(localID);
            }
        }).start();
    }

    public void updatePendingStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageDao.updatePendingStatus();
            }
        }).start();
    }

    public void updatePendingStatus(final String localID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageDao.updatePendingStatus(localID);
            }
        }).start();
    }
}
