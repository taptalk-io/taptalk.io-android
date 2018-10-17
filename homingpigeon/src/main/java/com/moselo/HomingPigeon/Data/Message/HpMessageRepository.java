package com.moselo.HomingPigeon.Data.Message;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        InsertAsyncTask(HpMessageDao chatDao) {
            asyncTaskDao = chatDao;
        }

        @Override
        protected Void doInBackground(HpMessageEntity... messages) {
            asyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages) {
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < HpChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                HpChatManager.getInstance().clearSaveMessages();

        }).start();
    }

    public void insert(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages, HpDatabaseListener listener) {
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

    public void getMessageList(final String roomID, final HpDatabaseListener listener, final long lastTimestamp) {
        new Thread(() -> {
            List<HpMessageEntity> entities = messageDao.getAllMessageTimeStamp(lastTimestamp, roomID);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void searchAllMessages(String keyword, final HpDatabaseListener listener) {
        new Thread(() -> {
            String queryKeyword = "%" + keyword + '%';
            List<HpMessageEntity> entities = messageDao.searchAllMessages(queryKeyword);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(String myID, List<HpMessageEntity> saveMessages, boolean isCheckUnreadFirst, final HpDatabaseListener listener) {
        new Thread(() -> {
            if (0 < saveMessages.size()) {
                messageDao.insert(saveMessages);
                HpChatManager.getInstance().clearSaveMessages();
            }
            List<HpMessageEntity> entities = messageDao.getAllRoomList();

            if (isCheckUnreadFirst && entities.size() > 0) {
                Map<String, Integer> unreadMap = new LinkedHashMap<>();
                for (HpMessageEntity entity : entities)
                    unreadMap.put(entity.getRoomID(), messageDao.getUnreadCount(myID, entity.getRoomID()));
                listener.onSelectedRoomList(entities, unreadMap);
            } else listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, final HpDatabaseListener listener) {
        new Thread(() -> {
            List<HpMessageEntity> entities = messageDao.getAllRoomList();

            if (isCheckUnreadFirst && entities.size() > 0) {
                Map<String, Integer> unreadMap = new LinkedHashMap<>();
                for (HpMessageEntity entity : entities)
                    unreadMap.put(entity.getRoomID(), messageDao.getUnreadCount(myID, entity.getRoomID()));
                listener.onSelectedRoomList(entities, unreadMap);
            } else{
                listener.onSelectFinished(entities);
            }
        }).start();
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final HpDatabaseListener listener) {
        new Thread(() -> {
            int unreadCount = messageDao.getUnreadCount(myID, roomID);
            listener.onCountedUnreadCount(roomID, unreadCount);
        }).start();
    }

    public void delete(final String localID) {
        new Thread(() -> messageDao.delete(localID)).start();
    }

    public void deleteAll() {
        new Thread(() -> messageDao.deleteAll()).start();
    }

    public void updatePendingStatus() {
        new Thread(() -> messageDao.updatePendingStatus()).start();
    }

    public void updatePendingStatus(final String localID) {
        new Thread(() -> messageDao.updatePendingStatus(localID)).start();
    }
}
