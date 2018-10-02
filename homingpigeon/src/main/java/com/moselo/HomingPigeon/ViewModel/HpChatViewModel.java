package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpChatViewModel extends AndroidViewModel {

    private LiveData<List<HpMessageEntity>> allMessages;
    private Map<String, MessageModel> messagePointer;
    private List<MessageModel> messageModels;
    private UserModel myUserModel;
    private RoomModel room;
    private String otherUserID = "0";
    private long lastTimestamp = 0;
    private int numUsers;
    private int unreadCount = 0;
    private boolean isOnBottom, isTyping;

    public HpChatViewModel(Application application) {
        super(application);
        allMessages = HpDataManager.getInstance().getMessagesLiveData();
    }

    public LiveData<List<HpMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void delete(String messageLocalID) {
        HpDataManager.getInstance().deleteFromDatabase(messageLocalID);
    }

    public Map<String, MessageModel> getMessagePointer() {
        return messagePointer == null ? messagePointer = new LinkedHashMap<>() : messagePointer;
    }

    public void setMessagePointer(Map<String, MessageModel> messagePointer) {
        this.messagePointer = messagePointer;
    }

    public void addMessagePointer(MessageModel pendingMessage) {
        getMessagePointer().put(pendingMessage.getLocalID(), pendingMessage);
    }

    public void removeMessagePointer(String localID) {
        getMessagePointer().remove(localID);
    }

    public void updateMessagePointer(MessageModel newMessage) {
        getMessagePointer().get(newMessage.getLocalID()).updateValue(newMessage);
    }

    public void getMessageEntities(String roomID, HomingPigeonDatabaseListener listener) {
        HpDataManager.getInstance().getMessagesFromDatabase(roomID, listener);
    }

    public void getMessageByTimestamp(String roomID, HomingPigeonDatabaseListener listener, long lastTimestamp) {
        HpDataManager.getInstance().getMessagesFromDatabase(roomID, listener, lastTimestamp);
    }

    public List<MessageModel> getMessageModels() {
        return messageModels == null ? messageModels = new ArrayList<>() : messageModels;
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

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
        HpChatManager.getInstance().setActiveRoom(room);
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

    // TODO: 14/09/18 ini harus di ganti untuk flow Chat Group (ini cuma bisa chat 1v1)
    public String getOtherUserID() {
        try {
            String[] tempUserID = room.getRoomID().split("-");
            return otherUserID = tempUserID[0].equals(myUserModel.getUserID()) ? tempUserID[1] : tempUserID[0];
        }catch (Exception e){
            return "0";
        }
    }
}
