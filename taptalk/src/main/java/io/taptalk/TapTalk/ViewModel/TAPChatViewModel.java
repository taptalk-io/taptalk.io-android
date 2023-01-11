package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;

public class TAPChatViewModel extends AndroidViewModel {

    private static final String TAG = TAPChatViewModel.class.getSimpleName();
    private String instanceKey = "";
    private LiveData<List<TAPMessageEntity>> allMessages;
    private Map<String, TAPMessageModel> messagePointer, unreadMessages, unreadMentions;
    private Map<String, TAPUserModel> roomParticipantsByUsername;
    private Map<String, List<Integer>> messageMentionIndexes;
    private LinkedHashMap<String, TAPUserModel> groupTyping;
    private LinkedHashMap<String, TAPMessageModel> dateSeparators;
    private LinkedHashMap<String, Integer> dateSeparatorIndexes;
    private LinkedHashMap<String, Integer> messageReadCountMap;
    private List<TAPMessageModel> messageModels, pendingRecyclerMessages, pinnedMessages;
    private List<TAPCustomKeyboardItemModel> customKeyboardItems;
    private ArrayList<String> starredMessageIds;
    private ArrayList<String> pinnedMessageIds;
    private ArrayList<TAPMessageModel> selectedMessages, forwardedMessages;
    private TAPUserModel myUserModel, otherUserModel;
    private TAPRoomModel room;
    private TAPMessageModel quotedMessage, pendingDownloadMessage, openedFileMessage, unreadIndicator, loadingIndicator;
    private TAPOnlineStatusModel onlineStatus;
    private Uri cameraImageUri;
    private Handler lastActivityHandler;
    private String tappedMessageLocalID;
    private String lastUnreadMessageLocalID;
    private Integer quoteAction;
    private TAPGetMessageListByRoomResponse pendingAfterResponse;
    private long lastTimestamp = 0L;
    private long lastBeforeTimestamp = 0L;
    private int initialUnreadCount, numUsers, containerAnimationState, firstVisibleItemIndex;
    private boolean isOnBottom, isActiveUserTyping, isOtherUserTyping, isCustomKeyboardEnabled,
            isInitialAPICallFinished, isUnreadButtonShown, isNeedToShowLoading,
            isScrollFromKeyboard, isAllUnreadMessagesHidden, deleteGroup;
    private boolean isHasMoreData = true;
    private boolean isAllMessagesHidden = true;
    public final int IDLE = 0;
    public final int ANIMATING = 1;
    public final int PROCESSING = 2;
    private File audioFile;
    private boolean isMediaPlaying, isSeeking;
    private Uri voiceUri;
    private MediaPlayer mediaPlayer;
    private Timer durationTimer;
    private int duration, pausedPosition, pinnedMessageIndex;
    private boolean isSelectState;
    private HashMap<String, String> linkHashMap;

    public static class TAPChatViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String instanceKey;

        public TAPChatViewModelFactory(Application application, String instanceKey) {
            this.application = application;
            this.instanceKey = instanceKey;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TAPChatViewModel(application, instanceKey);
        }
    }

    public TAPChatViewModel(Application application, String instanceKey) {
        super(application);
        this.instanceKey = instanceKey;
        allMessages = TAPDataManager.getInstance(instanceKey).getMessagesLiveData();
        setOnBottom(true);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public LiveData<List<TAPMessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void delete(String messageLocalID) {
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(messageLocalID);
    }

    public void removeFromUploadingList(String messageLocalID) {
        TAPChatManager.getInstance(instanceKey).removeUploadingMessageFromHashMap(messageLocalID);
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
        TAPMessageModel message = getMessagePointer().get(newMessage.getLocalID());
        if (null != message) {
            message.updateValue(newMessage);
        }
    }

    public Map<String, TAPMessageModel> getUnreadMessages() {
        return unreadMessages == null ? unreadMessages = new LinkedHashMap<>() : unreadMessages;
    }

    public int getUnreadCount() {
        return getUnreadMessages().size();
    }

    public void addUnreadMessage(TAPMessageModel unreadMessage) {
        getUnreadMessages().put(unreadMessage.getLocalID(), unreadMessage);
        if (TAPUtils.isActiveUserMentioned(unreadMessage, myUserModel)) {
            addUnreadMention(unreadMessage);
        }
    }

    public void removeUnreadMessage(String localID) {
        getUnreadMessages().remove(localID);
        if (TAPUtils.isActiveUserMentioned(getMessagePointer().get(localID), myUserModel)) {
            removeUnreadMention(localID);
        }
    }

    public void clearUnreadMessages() {
        getUnreadMessages().clear();
    }

    public Map<String, TAPUserModel> getRoomParticipantsByUsername() {
        return roomParticipantsByUsername == null ? roomParticipantsByUsername = new LinkedHashMap<>() : roomParticipantsByUsername;
    }

    public void addRoomParticipantByUsername(TAPUserModel user) {
        if (null == user.getUsername() || user.getUsername().isEmpty()) {
            return;
        }
        getRoomParticipantsByUsername().put(user.getUsername(), user);
    }

    public Map<String, List<Integer>> getMessageMentionIndexes() {
        return messageMentionIndexes == null ? messageMentionIndexes = new LinkedHashMap<>() : messageMentionIndexes;
    }

    public void addMessageMentionIndexes(String localID, List<Integer> indexes) {
        getMessageMentionIndexes().put(localID, indexes);
    }

    public Map<String, TAPMessageModel> getUnreadMentions() {
        return unreadMentions == null ? unreadMentions = new LinkedHashMap<>() : unreadMentions;
    }

    public int getUnreadMentionCount() {
        return getUnreadMentions().size();
    }

    public void addUnreadMention(TAPMessageModel unreadMessage) {
        getUnreadMentions().put(unreadMessage.getLocalID(), unreadMessage);
    }

    public void removeUnreadMention(String localID) {
        getUnreadMentions().remove(localID);
    }

    public void clearUnreadMentions() {
        getUnreadMentions().clear();
    }

//    public void getMessageEntities(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
//        TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, listener);
//    }
//
//    public void getMessageByTimestamp(String roomID, TAPDatabaseListener<TAPMessageEntity> listener, long lastTimestamp) {
//        TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, listener, lastTimestamp);
//    }

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
        TAPChatManager.getInstance(instanceKey).setActiveRoom(room);
    }

    public TAPMessageModel getQuotedMessage() {
        return null == quotedMessage ? TAPChatManager.getInstance(instanceKey).getQuotedMessage(room.getRoomID()) : quotedMessage;
    }

    public int getInitialUnreadCount() {
        return initialUnreadCount;
    }

    public void setInitialUnreadCount(int initialUnreadCount) {
        this.initialUnreadCount = initialUnreadCount;
    }

    // TODO: 24/05/22 set quote action MU
    public Integer getQuoteAction() {
        return null == quoteAction ? null == TAPChatManager.getInstance(instanceKey).getQuoteAction(room.getRoomID()) ? -1 : TAPChatManager.getInstance(instanceKey).getQuoteAction(room.getRoomID()) : quoteAction;
    }

    public void setQuotedMessage(TAPMessageModel quotedMessage, int quoteAction) {
        this.quotedMessage = quotedMessage;
        this.quoteAction = quoteAction;
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, quoteAction);
    }

    public TAPGetMessageListByRoomResponse getPendingAfterResponse() {
        return pendingAfterResponse;
    }

    public void setPendingAfterResponse(TAPGetMessageListByRoomResponse pendingAfterResponse) {
        this.pendingAfterResponse = pendingAfterResponse;
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
            loadingIndicator.setUser(TAPChatManager.getInstance(instanceKey).getActiveUser());
        }
        if (updateCreated) {
            // Update created time for loading indicator to array's last message created time
            if (getMessageModels().isEmpty()) {
                loadingIndicator.setCreated(0L);
            } else {
                loadingIndicator.setCreated(getMessageModels().get(getMessageModels().size() - 1).getCreated() - 1L);
            }
        }
        return loadingIndicator;
    }

    public LinkedHashMap<String, TAPMessageModel> getDateSeparators() {
        return null == dateSeparators ? dateSeparators = new LinkedHashMap<>() : dateSeparators;
    }

    public LinkedHashMap<String, Integer> getDateSeparatorIndexes() {
        return null == dateSeparatorIndexes ? dateSeparatorIndexes = new LinkedHashMap<>() : dateSeparatorIndexes;
    }

    public LinkedHashMap<String, Integer> getMessageReadCountMap() {
        return null == messageReadCountMap ? messageReadCountMap = new LinkedHashMap<>() : messageReadCountMap;
    }

    public TAPMessageModel generateDateSeparator(Context context, TAPMessageModel message) {
        return TAPMessageModel.Builder(
                TAPTimeFormatter.dateStampString(context, message.getCreated()),
                getRoom(),
                TYPE_DATE_SEPARATOR,
                message.getCreated() - 1,
                getMyUserModel(),
                "",
                null);
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

    public long getLastBeforeTimestamp() {
        return lastBeforeTimestamp;
    }

    public void setLastBeforeTimestamp(long lastBeforeTimestamp) {
        this.lastBeforeTimestamp = lastBeforeTimestamp;
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

    public int getFirstVisibleItemIndex() {
        return firstVisibleItemIndex;
    }

    public void setFirstVisibleItemIndex(int firstVisibleItemIndex) {
        this.firstVisibleItemIndex = firstVisibleItemIndex;
    }

    public boolean isActiveUserTyping() {
        return isActiveUserTyping;
    }

    public void setActiveUserTyping(boolean activeUserTyping) {
        isActiveUserTyping = activeUserTyping;
    }

    public LinkedHashMap<String, TAPUserModel> getGroupTyping() {
        return null == groupTyping ? groupTyping = new LinkedHashMap<>() : groupTyping;
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
        return firstTypingUser.getFullname().split(" ")[0];
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

    public String getOtherUserID() {
        try {
            String[] tempUserID = room.getRoomID().split("-");
            return tempUserID[0].equals(myUserModel.getUserID()) ? tempUserID[1] : tempUserID[0];
        } catch (Exception e) {
//            Log.e(TAG, "getOtherUserID: ", e);
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

    public boolean isAllUnreadMessagesHidden() {
        return isAllUnreadMessagesHidden;
    }

    public void setAllUnreadMessagesHidden(boolean allUnreadMessagesHidden) {
//        Log.e(TAG, "setAllUnreadMessagesHidden: " + allUnreadMessagesHidden);
        isAllUnreadMessagesHidden = allUnreadMessagesHidden;
    }

    public boolean isDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(boolean deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public ArrayList<String> getStarredMessageIds() {
        return null == starredMessageIds? new ArrayList<>() : starredMessageIds;
    }

    public void setStarredMessageIds(ArrayList<String> starredMessageIds) {
        this.starredMessageIds = starredMessageIds;
    }

    public void addStarredMessageId(String messageId) {
        if (starredMessageIds == null) {
            starredMessageIds = new ArrayList<>();
        }
        this.starredMessageIds.add(messageId);
    }

    public void removeStarredMessageId(String messageId) {
        if (starredMessageIds == null) {
            starredMessageIds = new ArrayList<>();
        }
        this.starredMessageIds.remove(messageId);
    }

    public ArrayList<String> getPinnedMessageIds() {
        return null == pinnedMessageIds? new ArrayList<>() : pinnedMessageIds;
    }

    public void setPinnedMessageIds(ArrayList<String> pinnedMessageIds) {
        this.pinnedMessageIds = new ArrayList<>();
        this.pinnedMessageIds.addAll(pinnedMessageIds);
    }

    public void addPinnedMessageId(String messageId) {
        getPinnedMessageIds().add(messageId);
    }

    public void addPinnedMessageId(int index, String messageId) {
        getPinnedMessageIds().add(index, messageId);
    }

    public void removePinnedMessageId(String messageId) {
        getPinnedMessageIds().remove(messageId);
    }

    public boolean isHasMoreData() {
        return isHasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData) {
        isHasMoreData = hasMoreData;
    }

    public boolean isAllMessagesHidden() {
        return isAllMessagesHidden;
    }

    public void setAllMessagesHidden(boolean allMessagesHidden) {
        isAllMessagesHidden = allMessagesHidden;
    }

    public File getAudioFile() {
        return audioFile == null? new File("") : audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public boolean isMediaPlaying() {
        return isMediaPlaying;
    }

    public void setMediaPlaying(boolean mediaPlaying) {
        isMediaPlaying = mediaPlaying;
    }

    public boolean isSeeking() {
        return isSeeking;
    }

    public void setSeeking(boolean seeking) {
        isSeeking = seeking;
    }

    public Uri getVoiceUri() {
        return voiceUri;
    }

    public void setVoiceUri(Uri voiceUri) {
        this.voiceUri = voiceUri;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Timer getDurationTimer() {
        return durationTimer;
    }

    public void setDurationTimer(Timer durationTimer) {
        this.durationTimer = durationTimer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPausedPosition() {
        return pausedPosition;
    }

    public void setPausedPosition(int pausedPosition) {
        this.pausedPosition = pausedPosition;
    }

    public boolean isSelectState() {
        return isSelectState;
    }

    public void setSelectState(boolean selectState) {
        isSelectState = selectState;
    }

    public ArrayList<TAPMessageModel> getSelectedMessages() {
        return null == selectedMessages ? selectedMessages = new ArrayList<>() : selectedMessages;
    }

    public void setSelectedMessages(ArrayList<TAPMessageModel> selectedMessages) {
        this.selectedMessages = selectedMessages;
    }

    public void removeSelectedMessage(TAPMessageModel message) {
        getSelectedMessages().remove(message);
    }

    public void addSelectedMessage(TAPMessageModel message) {
        getSelectedMessages().add(message);
    }

    public void clearSelectedMessages() {
        getSelectedMessages().clear();
    }

    public List<TAPMessageModel> getPinnedMessages() {
        return null == pinnedMessages ? pinnedMessages = new ArrayList<>() : pinnedMessages;
    }

    public void setPinnedMessages(List<TAPMessageModel> pinnedMessages) {
        this.pinnedMessages = new ArrayList<>();
        this.pinnedMessages.addAll(pinnedMessages);
    }

    public void removePinnedMessage(TAPMessageModel message) {
        getPinnedMessages().remove(message);
    }

    public void addPinnedMessage(TAPMessageModel message) {
        getPinnedMessages().add(message);
    }

    public void addPinnedMessage(int index, TAPMessageModel message) {
        getPinnedMessages().add(index, message);
    }

    public void replacePinnedMessage(int index, TAPMessageModel message) {
        getPinnedMessages().set(index, message);
    }

    public void addPinnedMessages(List<TAPMessageModel> pinnedMessages) {
        getPinnedMessages().addAll(pinnedMessages);
    }

    public void clearPinnedMessages() {
        getPinnedMessages().clear();
    }

    public int getPinnedMessageIndex() {
        return pinnedMessageIndex;
    }

    public void setPinnedMessageIndex(int index) {
        this.pinnedMessageIndex = index;
    }

    public void increasePinnedMessageIndex(int increment) {
        int increasedIndex;
        if (getPinnedMessageIndex() + increment >= getPinnedMessageIds().size()) {
            increasedIndex = 0;
        } else {
            increasedIndex = getPinnedMessageIndex() + increment;
        }
        setPinnedMessageIndex(increasedIndex);
    }


    public ArrayList<TAPMessageModel> getForwardedMessages() {
        return null == forwardedMessages? TAPChatManager.getInstance(instanceKey).getForwardedMessages(room.getRoomID()) : forwardedMessages;
    }

    public void setForwardedMessages(ArrayList<TAPMessageModel> forwardedMessages, int quoteAction) {
        this.forwardedMessages = forwardedMessages;
        this.quoteAction = quoteAction;
        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), forwardedMessages, quoteAction);
    }

    public HashMap<String, String> getLinkHashMap() {
        return linkHashMap == null? linkHashMap = new HashMap<>() : linkHashMap;
    }

    public void setLinkHashMap(HashMap<String, String> linkHashMap) {
        this.linkHashMap = linkHashMap;
    }

    public void clearLinkHashMap() {
        getLinkHashMap().clear();
    }
}
