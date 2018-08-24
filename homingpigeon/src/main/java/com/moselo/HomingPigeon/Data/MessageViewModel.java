package com.moselo.HomingPigeon.Data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private MessageRepository repository;
    private LiveData<List<MessageEntity>> allMessages;
    private UserModel myUserModel;
    private String roomId;
    private int numUsers;
    private int unreadCount = 0;
    private boolean isConnected = false;
    private boolean isTyping = false;
    private boolean isOnBottom;
    private List<MessageModel> messageModels = new ArrayList<>();
    private List<MessageEntity> messageEntities = new ArrayList<>();

    public List<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public MessageViewModel(Application application) {
        super(application);
        repository = new MessageRepository(application);
        allMessages = repository.getAllMessages();
    }

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void insert (MessageEntity messageEntity){
        repository.insert(messageEntity);
    }

    public void getMessageEntities(HomingPigeonGetChatListener listener) {
        messageEntities = repository.getAllMessageList(listener);
    }

    public void insert (List<MessageEntity> messageEntities){

        repository.insert(messageEntities);
    }

    public UserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(UserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
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
