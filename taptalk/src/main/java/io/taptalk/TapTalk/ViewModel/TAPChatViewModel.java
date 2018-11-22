package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.Handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOrderModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPChatViewModel extends AndroidViewModel {

    private LiveData<List<TAPMessageEntity>> allMessages;
    private Map<String, TAPMessageModel> messagePointer, unreadMessages, ongoingOrders;
    private List<TAPMessageModel> messageModels, pendingRecyclerMessages;
    private TAPUserModel myUserModel;
    private TAPRoomModel room;
    private TAPMessageModel replyTo;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String otherUserID = "0";
    private long lastTimestamp = 0;
    private long lastActivity;
    private int numUsers, containerAnimationState;
    private boolean isOnBottom, /*isTyping,*/ isInitialAPICallFinished;

    public final int IDLE = 0;
    public final int ANIMATING = 1;
    public final int PROCESSING = 2;

    public TAPChatViewModel(Application application) {
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
//        getMessagePointer().get(newMessage.getLocalID()).updateValue(newMessage);
        // TODO: 19 November 2018 FIX NULL POINTER ON MESSAGE POINTER
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateValue(newMessage);
        }
    }

    public void updateMessagePointerRead(TAPMessageModel newMessage) {
//        getMessagePointer().get(newMessage.getLocalID()).updateReadMessage();
        // TODO: 19 November 2018 FIX NULL POINTER ON MESSAGE POINTER
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateReadMessage();
        }
    }

    public void updateMessagePointerDelivered(TAPMessageModel newMessage) {
        // TODO: 19 November 2018 FIX NULL POINTER ON MESSAGE POINTER
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateDeliveredMessage();
        }
    }

    public Map<String, TAPMessageModel> getUnreadMessages() {
        return unreadMessages == null ? unreadMessages = new LinkedHashMap<>() : unreadMessages;
    }

    public void setUnreadMessages(Map<String, TAPMessageModel> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Map<String, TAPMessageModel> getOngoingOrders() {
        return ongoingOrders == null ? ongoingOrders = new LinkedHashMap<>() : ongoingOrders;
    }

    public void setOngoingOrders(Map<String, TAPMessageModel> ongoingOrders) {
        this.ongoingOrders = ongoingOrders;
    }

    public TAPOrderModel getOrderModel(TAPMessageModel message) {
        return TAPUtils.getInstance().fromJSON(new TypeReference<TAPOrderModel>() {}, message.getBody());
    }

    public TAPMessageModel getPreviousOrderWithSameID(TAPMessageModel message) {
        String orderID = getOrderModel(message).getOrderID();
        if (getOngoingOrders().containsKey(orderID)) {
            return getOngoingOrders().get(orderID);
        } else {
            return null;
        }
    }

    public void addOngoingOrderCard(TAPMessageModel message) {
        getOngoingOrders().put(getOrderModel(message).getOrderID(), message);
    }

    public void removeOngoingOrderCard(TAPMessageModel message) {
        getOngoingOrders().remove(getOrderModel(message).getOrderID());
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

    public List<TAPMessageModel> getPendingRecyclerMessages() {
        return null == pendingRecyclerMessages ? pendingRecyclerMessages = new ArrayList<>() : pendingRecyclerMessages;
    }

    public void setPendingRecyclerMessages(List<TAPMessageModel> pendingRecyclerMessages) {
        this.pendingRecyclerMessages = pendingRecyclerMessages;
    }

    public void addPendingRecyclerMessage(TAPMessageModel message) {
        getPendingRecyclerMessages().add(message);
    }

    public void removePendingRecyclerMessage(TAPMessageModel message) {
        getPendingRecyclerMessages().remove(message);
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

    public int getContainerAnimationState() {
        return containerAnimationState;
    }

    public void setContainerAnimationState(int containerAnimationState) {
        this.containerAnimationState = containerAnimationState;
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
