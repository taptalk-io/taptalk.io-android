package com.moselo.HomingPigeon.Data.Message;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Manager.ChatManager;

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

    public void insert(List<MessageEntity> messageEntities, boolean isClearSaveMessages){
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < ChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                ChatManager.getInstance().clearSaveMessages();

        }).start();
    }

    public void insert(List<MessageEntity> messageEntities, boolean isClearSaveMessages, HomingPigeonDatabaseListener listener){
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < ChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                ChatManager.getInstance().clearSaveMessages();

            listener.onSelectFinished(messageEntities);

        }).start();
    }

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void getMessageList(final String roomID, final HomingPigeonDatabaseListener listener) {
        new Thread(() -> {
            allMessageList = messageDao.getAllMessageList(roomID);
            listener.onSelectFinished(allMessageList);
        }).start();
    }

    public void getMessageList(final String roomID, final HomingPigeonDatabaseListener listener, final long lastTimestamp){
        new Thread(() -> {
            List<MessageEntity> entities = messageDao.getAllMessageTimeStamp(lastTimestamp, roomID);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(List<MessageEntity> saveMessages, final HomingPigeonDatabaseListener listener) {
        new Thread(() -> {
            if (0 < saveMessages.size()){
                messageDao.insert(saveMessages);
                ChatManager.getInstance().clearSaveMessages();
            }

            List<MessageEntity> entities = messageDao.getAllRoomList();
            listener.onSelectFinished(entities);
        }).start();
    }

    public void delete(final String localID) {
        new Thread(() -> messageDao.delete(localID)).start();
    }

    public void updatePendingStatus() {
        new Thread(() -> messageDao.updatePendingStatus()).start();
    }

    public void updatePendingStatus(final String localID) {
        new Thread(() -> messageDao.updatePendingStatus(localID)).start();
    }
}
