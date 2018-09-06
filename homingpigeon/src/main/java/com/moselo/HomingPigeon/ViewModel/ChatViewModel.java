package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatViewModel extends AndroidViewModel {

    private LiveData<List<MessageEntity>> allMessages;
    private Map<String, MessageModel> pendingMessages;
    private List<MessageModel> messageModels;
    private UserModel myUserModel;
    private String roomID;
    private long lastTimestamp = 0;
    private int numUsers;
    private int unreadCount = 0;
    private boolean isTyping = false;
    private boolean isOnBottom;

    public ChatViewModel(Application application) {
        super(application);
//        repository = new MessageRepository(application);
        allMessages = DataManager.getInstance().getMessagesLiveData();
        pendingMessages = new LinkedHashMap<>();
        messageModels = new ArrayList<>();
    }

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

//    public void insert(MessageEntity messageEntity) {
//        DataManager.getInstance().insertToDatabase(messageEntity);
//    }
//
//    public void insert(List<MessageEntity> messageEntities) {
//        DataManager.getInstance().insertToDatabase(messageEntities);
//    }

    public void delete(String messageLocalID) {
        DataManager.getInstance().deleteFromDatabase(messageLocalID);
    }

    public Map<String, MessageModel> getPendingMessages() {
        return pendingMessages;
    }

    public void setPendingMessages(Map<String, MessageModel> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    public void addPendingMessage(MessageModel pendingMessage) {
        pendingMessages.put(pendingMessage.getLocalID(), pendingMessage);
    }

    public void removePendingMessage(String localID) {
        pendingMessages.remove(localID);
    }

    public void updatePendingMessage(MessageModel newMessage) {
        pendingMessages.get(newMessage.getLocalID()).updateValue(newMessage);
    }

    public void getMessageEntities(String roomID, HomingPigeonGetChatListener listener) {
        DataManager.getInstance().getMessagesFromDatabase(roomID, listener);
    }

    public void getMessageByTimestamp(String roomID, HomingPigeonGetChatListener listener, long lastTimestamp) {
        DataManager.getInstance().getMessagesFromDatabase(roomID, listener, lastTimestamp);
    }

    public List<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public UserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(UserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
        ChatManager.getInstance().setActiveRoom(roomID);
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public boolean isOnBottom() {
        return isOnBottom;
    }

    public void setOnBottom(boolean onBottom) {
        isOnBottom = onBottom;
    }

    public int getMessageSize() {
        if (null != allMessages.getValue()) {
            return allMessages.getValue().size();
        }
        return 0;
    }
}
