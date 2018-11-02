package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.Handler;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpUserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpChatViewModel extends AndroidViewModel {

    private LiveData<List<HpMessageEntity>> allMessages;
    private Map<String, HpMessageModel> messagePointer, unreadMessages;
    private List<HpMessageModel> messageModels;
    private HpUserModel myUserModel;
    private HpRoomModel room;
    private HpMessageModel replyTo;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String otherUserID = "0";
    private long lastTimestamp = 0;
    private long lastActivity;
    private int numUsers;
    private boolean isOnBottom, /*isTyping,*/ isInitialAPICallFinished;

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

    public Map<String, HpMessageModel> getMessagePointer() {
        return messagePointer == null ? messagePointer = new LinkedHashMap<>() : messagePointer;
    }

    public void setMessagePointer(Map<String, HpMessageModel> messagePointer) {
        this.messagePointer = messagePointer;
    }

    public void addMessagePointer(HpMessageModel pendingMessage) {
        getMessagePointer().put(pendingMessage.getLocalID(), pendingMessage);
    }

    public void removeMessagePointer(String localID) {
        getMessagePointer().remove(localID);
    }

    public void updateMessagePointer(HpMessageModel newMessage) {
        getMessagePointer().get(newMessage.getLocalID()).updateValue(newMessage);
    }

    public Map<String, HpMessageModel> getUnreadMessages() {
        return unreadMessages == null ? unreadMessages = new LinkedHashMap<>() : unreadMessages;
    }

    public void setUnreadMessages(Map<String, HpMessageModel> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public int getUnreadCount() {
        return getUnreadMessages().size();
    }

    public void addUnreadMessage(HpMessageModel unreadMessage) {
        getUnreadMessages().put(unreadMessage.getLocalID(), unreadMessage);
    }

    public void removeUnreadMessage(String localID) {
        getUnreadMessages().remove(localID);
    }

    public void clearUnreadMessages() {
        if (getUnreadCount() == 0) return;

        for (Map.Entry<String, HpMessageModel> unreadMessage : getUnreadMessages().entrySet()) {
            unreadMessage.getValue().setIsRead(true);
        }
        getUnreadMessages().clear();
    }

    public void getMessageEntities(String roomID, HpDatabaseListener listener) {
        HpDataManager.getInstance().getMessagesFromDatabase(roomID, listener);
    }

    public void getMessageByTimestamp(String roomID, HpDatabaseListener listener, long lastTimestamp) {
        HpDataManager.getInstance().getMessagesFromDatabase(roomID, listener, lastTimestamp);
    }

    public List<HpMessageModel> getMessageModels() {
        return messageModels == null ? messageModels = new ArrayList<>() : messageModels;
    }

    public void setMessageModels(List<HpMessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public void addMessageModels(List<HpMessageModel> messageModels) {
        getMessageModels().addAll(messageModels);
    }

    public HpUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(HpUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public HpRoomModel getRoom() {
        return room;
    }

    public void setRoom(HpRoomModel room) {
        this.room = room;
        HpChatManager.getInstance().setActiveRoom(room);
    }

    public HpMessageModel getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(HpMessageModel replyTo) {
        this.replyTo = replyTo;
    }

    public Uri getCameraImageUri() {
        return cameraImageUri;
    }

    public void setCameraImageUri(Uri cameraImageUri) {
        this.cameraImageUri = cameraImageUri;
    }

    public Handler getLastActivityHandler() {
        return null == lastActivityHandler ? lastActivityHandler = new Handler() : lastActivityHandler;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

//    public boolean isTyping() {
//        return isTyping;
//    }
//
//    public void setTyping(boolean typing) {
//        isTyping = typing;
//    }

    public boolean isInitialAPICallFinished() {
        return isInitialAPICallFinished;
    }

    public void setInitialAPICallFinished(boolean initialAPICallFinished) {
        isInitialAPICallFinished = initialAPICallFinished;
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
