package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.Handler;

import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;
import com.moselo.HomingPigeon.Manager.TAPDataManager;
import com.moselo.HomingPigeon.Manager.TAPChatManager;
import com.moselo.HomingPigeon.Model.TAPMessageModel;
import com.moselo.HomingPigeon.Model.TAPRoomModel;
import com.moselo.HomingPigeon.Model.TAPUserModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpChatViewModel extends AndroidViewModel {

    private LiveData<List<TAPMessageEntity>> allMessages;
    private Map<String, TAPMessageModel> messagePointer, unreadMessages;
    private List<TAPMessageModel> messageModels;
    private TAPUserModel myUserModel;
    private TAPRoomModel room;
    private TAPMessageModel replyTo, pendingCustomKeyboardMessage;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String otherUserID = "0";
    private long lastTimestamp = 0;
    private long lastActivity;
    private int numUsers;
    private boolean isOnBottom, /*isTyping,*/ isInitialAPICallFinished, isContainerAnimating;

    public HpChatViewModel(Application application) {
        super(application);
        allMessages = TAPDataManager.getInstance().getMessagesLiveData();
    }

    public LiveData<List<TAPMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void delete(String messageLocalID) {
        TAPDataManager.getInstance().deleteFromDatabase(messageLocalID);
    }

    public Map<String, TAPMessageModel> getMessagePointer() {
        return messagePointer == null ? messagePointer = new LinkedHashMap<>() : messagePointer;
    }

    public void setMessagePointer(Map<String, TAPMessageModel> messagePointer) {
        this.messagePointer = messagePointer;
    }

    public void addMessagePointer(TAPMessageModel pendingMessage) {
        getMessagePointer().put(pendingMessage.getLocalID(), pendingMessage);
    }

    public void removeMessagePointer(String localID) {
        getMessagePointer().remove(localID);
    }

    public void updateMessagePointer(TAPMessageModel newMessage) {
        getMessagePointer().get(newMessage.getLocalID()).updateValue(newMessage);
    }

    public Map<String, TAPMessageModel> getUnreadMessages() {
        return unreadMessages == null ? unreadMessages = new LinkedHashMap<>() : unreadMessages;
    }

    public void setUnreadMessages(Map<String, TAPMessageModel> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public int getUnreadCount() {
        return getUnreadMessages().size();
    }

    public void addUnreadMessage(TAPMessageModel unreadMessage) {
        getUnreadMessages().put(unreadMessage.getLocalID(), unreadMessage);
    }

    public void removeUnreadMessage(String localID) {
        getUnreadMessages().remove(localID);
    }

    public void clearUnreadMessages() {
        if (getUnreadCount() == 0) return;

        for (Map.Entry<String, TAPMessageModel> unreadMessage : getUnreadMessages().entrySet()) {
            unreadMessage.getValue().setIsRead(true);
        }
        getUnreadMessages().clear();
    }

    public void getMessageEntities(String roomID, TAPDatabaseListener listener) {
        TAPDataManager.getInstance().getMessagesFromDatabaseDesc(roomID, listener);
    }

    public void getMessageByTimestamp(String roomID, TAPDatabaseListener listener, long lastTimestamp) {
        TAPDataManager.getInstance().getMessagesFromDatabaseDesc(roomID, listener, lastTimestamp);
    }

    public List<TAPMessageModel> getMessageModels() {
        return messageModels == null ? messageModels = new ArrayList<>() : messageModels;
    }

    public void setMessageModels(List<TAPMessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public void addMessageModels(List<TAPMessageModel> messageModels) {
        getMessageModels().addAll(messageModels);
    }

    public TAPUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
        TAPChatManager.getInstance().setActiveRoom(room);
    }

    public TAPMessageModel getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(TAPMessageModel replyTo) {
        this.replyTo = replyTo;
    }

    public TAPMessageModel getPendingCustomKeyboardMessage() {
        return pendingCustomKeyboardMessage;
    }

    public void setPendingCustomKeyboardMessage(TAPMessageModel pendingCustomKeyboardMessage) {
        this.pendingCustomKeyboardMessage = pendingCustomKeyboardMessage;
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

    public boolean isContainerAnimating() {
        return isContainerAnimating;
    }

    public void setContainerAnimating(boolean containerAnimating) {
        isContainerAnimating = containerAnimating;
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
