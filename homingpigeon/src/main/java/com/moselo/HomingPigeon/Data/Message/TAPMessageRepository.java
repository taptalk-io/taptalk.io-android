package com.moselo.HomingPigeon.Data.Message;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.moselo.HomingPigeon.Data.TapTalkDatabase;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;
import com.moselo.HomingPigeon.Manager.TAPChatManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TAPMessageRepository {

    private TAPMessageDao messageDao;
    private LiveData<List<TAPMessageEntity>> allMessages;
    private List<TAPMessageEntity> allMessageList = new ArrayList<>();

    public TAPMessageRepository(Application application) {
        TapTalkDatabase db = TapTalkDatabase.getDatabase(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessage();
    }

    public void delete(List<TAPMessageEntity> messageEntities, TAPDatabaseListener listener) {
        new Thread(() -> {
            messageDao.delete(messageEntities);
            listener.onDeleteFinished();
        }).start();
    }

    public void insert(TAPMessageEntity message) {
        new InsertAsyncTask(messageDao).execute(message);
    }

    private static class InsertAsyncTask extends AsyncTask<TAPMessageEntity, Void, Void> {
        private TAPMessageDao asyncTaskDao;

        InsertAsyncTask(TAPMessageDao chatDao) {
            asyncTaskDao = chatDao;
        }

        @Override
        protected Void doInBackground(TAPMessageEntity... messages) {
            asyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages) {
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < TAPChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                TAPChatManager.getInstance().clearSaveMessages();

        }).start();
    }

    public void insert(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages, TAPDatabaseListener listener) {
        new Thread(() -> {
            messageDao.insert(messageEntities);

            if (0 < TAPChatManager.getInstance().getSaveMessages().size() && isClearSaveMessages)
                TAPChatManager.getInstance().clearSaveMessages();

            listener.onInsertFinished();

        }).start();
    }

    public LiveData<List<TAPMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void getMessageListDesc(final String roomID, final TAPDatabaseListener listener) {
        new Thread(() -> {
            allMessageList = messageDao.getAllMessageListDesc(roomID);
            listener.onSelectFinished(allMessageList);
        }).start();
    }

    public void getMessageListDesc(final String roomID, final TAPDatabaseListener listener, final long lastTimestamp) {
        new Thread(() -> {
            List<TAPMessageEntity> entities = messageDao.getAllMessageTimeStamp(lastTimestamp, roomID);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getMessageListAsc(final String roomID, final TAPDatabaseListener listener) {
        new Thread(() -> {
            allMessageList = messageDao.getAllMessageListAsc(roomID);
            listener.onSelectFinished(allMessageList);
        }).start();
    }

    public void searchAllMessages(String keyword, final TAPDatabaseListener listener) {
        new Thread(() -> {
            String queryKeyword = "%" + keyword + '%';
            List<TAPMessageEntity> entities = messageDao.searchAllMessages(queryKeyword);
            listener.onSelectFinished(entities);
        }).start();
    }

    public void getRoomList(String myID, List<TAPMessageEntity> saveMessages, boolean isCheckUnreadFirst, final TAPDatabaseListener listener) {
        new Thread(() -> {
            if (0 < saveMessages.size()) {
                messageDao.insert(saveMessages);
                TAPChatManager.getInstance().clearSaveMessages();
            }
            List<TAPMessageEntity> entities = messageDao.getAllRoomList();

            if (isCheckUnreadFirst && entities.size() > 0) {
                Map<String, Integer> unreadMap = new LinkedHashMap<>();
                for (TAPMessageEntity entity : entities)
                    unreadMap.put(entity.getRoomID(), messageDao.getUnreadCount(myID, entity.getRoomID()));
                listener.onSelectedRoomList(entities, unreadMap);
            } else listener.onSelectFinished(entities);
        }).start();
    }

    public void searchAllChatRooms(String myID, String keyword, final TAPDatabaseListener listener) {
        new Thread(() -> {
            String queryKeyword = "%" + keyword + '%';
            List<TAPMessageEntity> entities = messageDao.searchAllChatRooms(queryKeyword);
            Map<String, Integer> unreadMap = new LinkedHashMap<>();
            for (TAPMessageEntity entity : entities)
                unreadMap.put(entity.getRoomID(), messageDao.getUnreadCount(myID, entity.getRoomID()));
            listener.onSelectedRoomList(entities, unreadMap);
        }).start();
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, final TAPDatabaseListener listener) {
        new Thread(() -> {
            List<TAPMessageEntity> entities = messageDao.getAllRoomList();

            if (isCheckUnreadFirst && entities.size() > 0) {
                Map<String, Integer> unreadMap = new LinkedHashMap<>();
                for (TAPMessageEntity entity : entities)
                    unreadMap.put(entity.getRoomID(), messageDao.getUnreadCount(myID, entity.getRoomID()));
                listener.onSelectedRoomList(entities, unreadMap);
            } else{
                listener.onSelectFinished(entities);
            }
        }).start();
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final TAPDatabaseListener listener) {
        new Thread(() -> {
            int unreadCount = messageDao.getUnreadCount(myID, roomID);
            listener.onCountedUnreadCount(roomID, unreadCount);
        }).start();
    }

    public void delete(final String localID) {
        new Thread(() -> messageDao.delete(localID)).start();
    }

    public void deleteAllMessage() {
        new Thread(() -> messageDao.deleteAllMessage()).start();
    }

    public void updatePendingStatus() {
        new Thread(() -> messageDao.updatePendingStatus()).start();
    }

    public void updatePendingStatus(final String localID) {
        new Thread(() -> messageDao.updatePendingStatus(localID)).start();
    }
}
