package com.moselo.HomingPigeon.Data.Message;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;

import java.util.ArrayList;
import java.util.List;

public class HpMessageRepository {

    private HpMessageDao messageDao;
    private LiveData<List<HpMessageEntity>> allMessages;
    private List<HpMessageEntity> allMessageList = new ArrayList<>();

    public HpMessageRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessage();
    }

    public void insert(HpMessageEntity message) {
        new InsertAsyncTask(messageDao).execute(message);
    }

    private static class InsertAsyncTask extends AsyncTask<HpMessageEntity, Void, Void> {
        private HpMessageDao asyncTaskDao;

        InsertAsyncTask (HpMessageDao chatDao) {
            asyncTaskDao = chatDao;
        }

        @Override
        protected Void doInBackground(HpMessageEntity... messages) {
            asyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages){
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < HpChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                HpChatManager.getInstance().clearSaveMessages();

        }).start();
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages, HpDatabaseListener listener){
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < HpChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                HpChatManager.getInstance().clearSaveMessages();

            listener.onInsertFinished();

        }).start();
    }

    public LiveData<List<HpMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void getMessageList(final String roomID, final HpDatabaseListener listener) {
        new Thread(() -> {
            allMessageList = messageDao.getAllMessageList(roomID);
            listener.onSelectFinished(allMessageList);
        }).start();
    }

    public void getMessageList(final String roomID, final HpDatabaseListener listener, final long lastTimestamp){
        new Thread(() -> {
            List<HpMessageEntity> entities = messageDao.getAllMessageTimeStamp(lastTimestamp, roomID);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(List<HpMessageEntity> saveMessages, final HpDatabaseListener listener) {
        new Thread(() -> {
            if (0 < saveMessages.size()){
                messageDao.insert(saveMessages);
                HpChatManager.getInstance().clearSaveMessages();
            }

            List<HpMessageEntity> entities = messageDao.getAllRoomList();
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(final HpDatabaseListener listener) {
        new Thread(() -> {
            List<HpMessageEntity> entities = messageDao.getAllRoomList();
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getUnreadCountPerRoom(String roomID) {

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
