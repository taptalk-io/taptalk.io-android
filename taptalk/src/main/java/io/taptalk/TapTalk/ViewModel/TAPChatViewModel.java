package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

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
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPOrderModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;

public class TAPChatViewModel extends AndroidViewModel {

    private static final String TAG = TAPChatViewModel.class.getSimpleName();
    private LiveData<List<TAPMessageEntity>> allMessages;
    private Map<String, TAPMessageModel> messagePointer, unreadMessages, ongoingOrders;
    private LinkedHashMap<String, TAPUserModel> groupTyping;
    private List<TAPMessageModel> messageModels, pendingRecyclerMessages;
    private List<TAPCustomKeyboardItemModel> customKeyboardItems;
    private TAPUserModel myUserModel, otherUserModel;
    private TAPRoomModel room;
    private TAPMessageModel quotedMessage, pendingDownloadMessage, openedFileMessage, unreadIndicator, loadingIndicator;
    private TAPOnlineStatusModel onlineStatus;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String tappedMessageLocalID;
    private Integer quoteAction;
    private String lastUnreadMessageLocalID;
    private long lastTimestamp = 0;
    private int initialUnreadCount, numUsers, previousEditTextSelectionIndex, containerAnimationState;
    private boolean isOnBottom, isActiveUserTyping, isOtherUserTyping, isCustomKeyboardEnabled,
            isInitialAPICallFinished, isUnreadButtonShown, isNeedToShowLoading, isScrollFromKeyboard;

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

    public void removeFromUploadingList(String messageLocalID) {
        TAPChatManager.getInstance().removeUploadingMessageFromHashMap(messageLocalID);
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
        // TODO: 19 November 2018 FIX NULL POINTER ON MESSAGE POINTER
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateValue(newMessage);
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
        return TAPUtils.getInstance().fromJSON(new TypeReference<TAPOrderModel>() {
        }, message.getBody());
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

        getUnreadMessages().clear();
    }

    public void getMessageEntities(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
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

    public List<TAPCustomKeyboardItemModel> getCustomKeyboardItems() {
        return null == customKeyboardItems ? customKeyboardItems = new ArrayList<>() : customKeyboardItems;
    }

    public void setCustomKeyboardItems(List<TAPCustomKeyboardItemModel> customKeyboardItems) {
        this.customKeyboardItems = customKeyboardItems;
    }

    public TAPUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(TAPUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }

    public TAPUserModel getOtherUserModel() {
        return otherUserModel;
    }

    public void setOtherUserModel(TAPUserModel otherUserModel) {
        this.otherUserModel = otherUserModel;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
        TAPChatManager.getInstance().setActiveRoom(room);
    }

    public TAPMessageModel getQuotedMessage() {
        return null == quotedMessage ? TAPChatManager.getInstance().getQuotedMessage() : quotedMessage;
    }

    public int getInitialUnreadCount() {
        return initialUnreadCount;
    }

    public void setInitialUnreadCount(int initialUnreadCount) {
        this.initialUnreadCount = initialUnreadCount;
    }

    public Integer getQuoteAction() {
        return null == quoteAction ? null == TAPChatManager.getInstance().getQuoteAction() ? -1 : TAPChatManager.getInstance().getQuoteAction() : quoteAction;
    }

    public void setQuotedMessage(TAPMessageModel quotedMessage, int quoteAction) {
        this.quotedMessage = quotedMessage;
        this.quoteAction = quoteAction;
        TAPChatManager.getInstance().setQuotedMessage(quotedMessage, quoteAction);
    }

    public String getTappedMessageLocalID() {
        return tappedMessageLocalID;
    }

    public void setTappedMessageLocalID(String tappedMessageLocalID) {
        this.tappedMessageLocalID = tappedMessageLocalID;
    }

    /**
     * Used to save pending download message when requesting storage permission
     */
    public TAPMessageModel getPendingDownloadMessage() {
        return pendingDownloadMessage;
    }

    public void setPendingDownloadMessage(TAPMessageModel pendingDownloadMessage) {
        this.pendingDownloadMessage = pendingDownloadMessage;
    }

    /**
     * Used to save file-type message when user opens file from chat bubble
     */
    public TAPMessageModel getOpenedFileMessage() {
        return openedFileMessage;
    }

    public void setOpenedFileMessage(TAPMessageModel openedFileMessage) {
        this.openedFileMessage = openedFileMessage;
    }

    public TAPMessageModel getUnreadIndicator() {
        return unreadIndicator;
    }

    public void setUnreadIndicator(TAPMessageModel unreadIndicator) {
        this.unreadIndicator = unreadIndicator;
    }

    public TAPMessageModel getLoadingIndicator(boolean updateCreated) {
        if (null == loadingIndicator) {
            loadingIndicator = new TAPMessageModel();
            loadingIndicator.setType(TYPE_LOADING_MESSAGE_IDENTIFIER);
            loadingIndicator.setLocalID(LOADING_INDICATOR_LOCAL_ID);
            loadingIndicator.setUser(TAPChatManager.getInstance().getActiveUser());
        }
        if (updateCreated) {
            // Update created time for loading indicator to array's last message created time
            loadingIndicator.setCreated(getMessageModels().get(getMessageModels().size() - 1).getCreated());
        }
        return loadingIndicator;
    }

    public TAPOnlineStatusModel getOnlineStatus() {
        return null == onlineStatus ? onlineStatus = new TAPOnlineStatusModel(false, 0L) : onlineStatus;
    }

    public void setOnlineStatus(TAPOnlineStatusModel onlineStatus) {
        this.onlineStatus = onlineStatus;
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

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getPreviousEditTextSelectionIndex() {
        return previousEditTextSelectionIndex;
    }

    public void setPreviousEditTextSelectionIndex(int previousEditTextSelectionIndex) {
        this.previousEditTextSelectionIndex = previousEditTextSelectionIndex;
    }

    public int getContainerAnimationState() {
        return containerAnimationState;
    }

    public void setContainerAnimationState(int containerAnimationState) {
        this.containerAnimationState = containerAnimationState;
    }

    public boolean isActiveUserTyping() {
        return isActiveUserTyping;
    }

    public void setActiveUserTyping(boolean activeUserTyping) {
        isActiveUserTyping = activeUserTyping;
    }

    public LinkedHashMap<String, TAPUserModel> getGroupTyping() {
        return null == groupTyping? groupTyping = new LinkedHashMap<>() : groupTyping;
    }

    public void setGroupTyping(LinkedHashMap<String, TAPUserModel> groupTyping) {
        this.groupTyping = groupTyping;
    }

    public void removeGroupTyping(String userID) {
        getGroupTyping().remove(userID);
    }

    public boolean removeAndCheckIfNeedToDismissTyping(String userID) {
        if (null == userID || getGroupTyping().containsKey(userID)) return false;

        removeGroupTyping(userID);

        return 0 == getGroupTyping().size();
    }

    public void addGroupTyping(TAPUserModel typingUsers) {
        if (null != typingUsers) {
            getGroupTyping().put(typingUsers.getUserID(), typingUsers);
        }
    }

    public int getGroupTypingSize() {
        return getGroupTyping().size();
    }

    public String getFirstTypingUserName() {
        if (0 >= getGroupTypingSize()) return "";

        TAPUserModel firstTypingUser = getGroupTyping().entrySet().iterator().next().getValue();
        return firstTypingUser.getName().split(" ")[0];
    }

    public boolean isCustomKeyboardEnabled() {
        return isCustomKeyboardEnabled;
    }

    public void setCustomKeyboardEnabled(boolean customKeyboardEnabled) {
        isCustomKeyboardEnabled = customKeyboardEnabled;
    }

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

    /**
     * Unread button will only show once
     */
    public boolean isUnreadButtonShown() {
        return isUnreadButtonShown;
    }

    public void setUnreadButtonShown(boolean unreadButtonShown) {
        isUnreadButtonShown = unreadButtonShown;
    }

    /**
     * Show loading when fetching older messages
     */
    public boolean isNeedToShowLoading() {
        return isNeedToShowLoading;
    }

    public void setNeedToShowLoading(boolean needToShowLoading) {
        isNeedToShowLoading = needToShowLoading;
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
            return tempUserID[0].equals(myUserModel.getUserID()) ? tempUserID[1] : tempUserID[0];
        } catch (Exception e) {
            Log.e(TAG, "getOtherUserID: ", e);
            return "0";
        }
    }

    public String getLastUnreadMessageLocalID() {
        return null == lastUnreadMessageLocalID ? "" : lastUnreadMessageLocalID;
    }

    public void setLastUnreadMessageLocalID(String lastUnreadMessageLocalID) {
        this.lastUnreadMessageLocalID = lastUnreadMessageLocalID;
    }

    public boolean isScrollFromKeyboard() {
        return isScrollFromKeyboard;
    }

    public void setScrollFromKeyboard(boolean scrollFromKeyboard) {
        isScrollFromKeyboard = scrollFromKeyboard;
    }
}
