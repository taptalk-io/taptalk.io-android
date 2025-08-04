package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CHARACTER_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_CAPTION_EXCEEDS_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_EDIT_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_EXCEEDED_MAX_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_URI_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_EDIT_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_EXCEEDED_MAX_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kEventOpenRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketAuthentication;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketBlockUser;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketClearChat;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketCloseRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketDeleteMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketMarkAsReadRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketMarkAsUnreadRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketMuteRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketNewMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketOpenMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketPinRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketScheduleMessageRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketStartTyping;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketStopTyping;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUnblockUser;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUnmuteRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUnpinRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUpdateMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUserOnlineStatus;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.AUDIO_MP3;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.VIDEO_MP4;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DESCRIPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TITLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URLS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.DISCONNECTED;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPSocketMessageListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapCreateScheduledMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPDataFileModel;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPDataLocationModel;
import io.taptalk.TapTalk.Model.TAPEmitModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;

public class TAPChatManager {

    private final String TAG = TAPChatManager.class.getSimpleName();
    private static HashMap<String, TAPChatManager> instances;
    private String instanceKey = "";
    private Map<String, TAPMessageModel> pendingMessages, waitingUploadProgress, waitingResponses, incomingMessages, quotedMessages;
    private Map<String, TapScheduledMessageModel> pendingScheduledMessages;
    public Map<String, TapScheduledMessageModel> uploadingScheduledMessages;
    private Map<String, ArrayList<TAPMessageModel>> forwardedMessages;
    private Map<String, Integer> quotedActions;
    private Map<String, String> messageDrafts, pendingMessageActions;
    private HashMap<String, HashMap<String, Object>> userInfo;
    private HashMap<String, TapSendMessageInterface> sendMessageListeners;
    private List<TAPChatListener> chatListeners;
    private List<TAPMessageEntity> saveMessages; // Message to be saved
    private List<String> replyMessageLocalIDs;
    private TAPUserModel activeUser;
    private ScheduledExecutorService scheduler;
    private TAPRoomModel activeRoom; // Will be removed when activity is paused
    private String openRoom; // (Room ID) Will be kept until activity is destroyed
    private boolean isCheckPendingArraySequenceActive = false;
    private boolean isPendingMessageExist;
    private boolean isFileUploadExist;
    private boolean isFinishChatFlow;
    private boolean isNeedToCalledUpdateRoomStatusAPI = true;
    private boolean isSendMessageDisabled = false; // TODO TEMPORARY FLAG FOR SEND MESSAGE API
    private int pendingRetryAttempt = 0;
    private int maxRetryAttempt = 10;
    private int pendingRetryInterval = 60 * 1000;
    private final int maxImageSize = 2000;

    public static TAPChatManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPChatManager instance = new TAPChatManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPChatManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPChatManager(String instanceKey) {
        this.instanceKey = instanceKey;
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
        TAPConnectionManager.getInstance(instanceKey).setSocketMessageListener(socketMessageListener);
        setActiveUser(TAPDataManager.getInstance(instanceKey).getActiveUser());
        chatListeners = new ArrayList<>();
        sendMessageListeners = new HashMap<>();
        saveMessages = new ArrayList<>();
        pendingMessages = new LinkedHashMap<>();
        pendingScheduledMessages = new LinkedHashMap<>();
        uploadingScheduledMessages = new LinkedHashMap<>();
        pendingMessageActions = new LinkedHashMap<>();
        waitingResponses = new LinkedHashMap<>();
        incomingMessages = new LinkedHashMap<>();
        waitingUploadProgress = new LinkedHashMap<>();
        messageDrafts = new HashMap<>();
    }

    private TapTalkSocketInterface socketListener = new TapTalkSocketInterface() {
        @Override
        public void onSocketConnected() {

            checkAndSendPendingMessages();
            checkAndSendPendingScheduledMessages();
            isFinishChatFlow = false;
        }

        @Override
        public void onSocketDisconnected() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (TapTalk.isForeground &&
                        TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext) &&
                        DISCONNECTED == TAPConnectionManager.getInstance(instanceKey).getConnectionStatus())
                    TAPConnectionManager.getInstance(instanceKey).reconnect();
            } else {
                if (TapTalk.isForeground &&
                        TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext) &&
                        DISCONNECTED == TAPConnectionManager.getInstance(instanceKey).getConnectionStatus())
                    TAPConnectionManager.getInstance(instanceKey).reconnect();
            }
        }

        @Override
        public void onSocketConnecting() {

        }

        @Override
        public void onSocketError() {
            TAPConnectionManager.getInstance(instanceKey).reconnect();
        }
    };

    private TAPSocketMessageListener socketMessageListener = new TAPSocketMessageListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {
            List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
            switch (eventName) {
                case kEventOpenRoom:
                    break;
                case kSocketCloseRoom:
                    break;
                case kSocketNewMessage:
                case kSocketUpdateMessage:
                case kSocketDeleteMessage:
                    receiveMessageFromSocket(TAPUtils.fromJSON(new TypeReference<TAPEmitModel<HashMap<String, Object>>>() {}, emitData).getData(), eventName);
                    break;
                case kSocketOpenMessage:
                    break;
                case kSocketStartTyping:
                    TAPEmitModel<TAPTypingModel> startTypingEmit = TAPUtils
                            .fromJSON(new TypeReference<TAPEmitModel<TAPTypingModel>>() {
                            }, emitData);
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onReceiveStartTyping(startTypingEmit.getData());
                    }
                    break;
                case kSocketStopTyping:
                    TAPEmitModel<TAPTypingModel> stopTypingEmit = TAPUtils
                            .fromJSON(new TypeReference<TAPEmitModel<TAPTypingModel>>() {
                            }, emitData);
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onReceiveStopTyping(stopTypingEmit.getData());
                    }
                    break;
                case kSocketAuthentication:
                    break;
                case kSocketUserOnlineStatus:
                    TAPEmitModel<TAPOnlineStatusModel> onlineStatusEmit = TAPUtils
                            .fromJSON(new TypeReference<TAPEmitModel<TAPOnlineStatusModel>>() {
                            }, emitData);
                    TAPOnlineStatusModel onlineStatus = onlineStatusEmit.getData();
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onUserOnlineStatusUpdate(onlineStatus);
                    }
                    break;
                case kSocketClearChat:
                    TAPEmitModel<TAPUpdateRoomResponse> clearChatEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse clearChatData = clearChatEmit.getData();
                    String roomId = clearChatData.getRoom().getRoomID();
                    TAPDataManager.getInstance(instanceKey).saveLastRoomMessageDeleteTime();
                    TAPDataManager.getInstance(instanceKey).removePinnedRoomID(roomId);
                    TAPDataManager.getInstance(instanceKey).removeStarredMessageIds(roomId);
                    TAPDataManager.getInstance(instanceKey).removePinnedMessageIds(roomId);
                    TAPDataManager.getInstance(instanceKey).removeUnreadRoomID(roomId);
                    TapCoreChatRoomManager.getInstance(instanceKey).deleteLocalGroupChatRoom(roomId, new TapCommonListener() {
                        @Override
                        public void onSuccess(String successMessage) {
                            super.onSuccess(successMessage);
                            for (TAPChatListener listener : chatListenersCopy) {
                                listener.onChatCleared(clearChatData.getRoom());
                            }
                        }
                    });
                    break;
                case kSocketMuteRoom:
                case kSocketUnmuteRoom:
                    TAPEmitModel<TAPUpdateRoomResponse> muteRoomEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse muteRoomData = muteRoomEmit.getData();
                    String mutedRoomId = muteRoomData.getRoom().getRoomID();
                    Long expiredAt = muteRoomData.getExpiredAt();
                    HashMap<String, Long> mutedRooms = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs();
                    if (expiredAt != null) {
                        // mute room
                        mutedRooms.put(mutedRoomId, expiredAt);
                    } else {
                        // unmute room
                        mutedRooms.remove(mutedRoomId);
                    }
                    TAPDataManager.getInstance(instanceKey).saveMutedRoomIDs(mutedRooms);
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onMuteOrUnmuteRoom(muteRoomData.getRoom(), expiredAt);
                    }
                    break;
                case kSocketPinRoom:
                    TAPEmitModel<TAPUpdateRoomResponse> pinRoomEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse pinRoomData = pinRoomEmit.getData();
                    String pinnedRoomId = pinRoomData.getRoom().getRoomID();
                    ArrayList<String> pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs();
                    if (!pinnedRooms.contains(pinnedRoomId)) {
                        pinnedRooms.add(0, pinnedRoomId);
                    }
                    TAPDataManager.getInstance(instanceKey).savePinnedRoomIDs(pinnedRooms);
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onPinRoom(pinRoomData.getRoom());
                    }
                    break;
                case kSocketUnpinRoom:
                    TAPEmitModel<TAPUpdateRoomResponse> unpinRoomEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse unpinRoomData = unpinRoomEmit.getData();
                    String unpinnedRoomId = unpinRoomData.getRoom().getRoomID();
                    TAPDataManager.getInstance(instanceKey).removePinnedRoomID(unpinnedRoomId);
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onUnpinRoom(unpinRoomData.getRoom());
                    }
                    break;
                case kSocketMarkAsReadRoom:
                    TAPEmitModel<TAPUpdateRoomResponse> markAsReadRoomEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse markAsReadRoomData = markAsReadRoomEmit.getData();
                    String readRoomId = markAsReadRoomData.getRoom().getRoomID();
                    TAPDataManager.getInstance(instanceKey).removeUnreadRoomID(readRoomId);
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onMarkRoomAsRead(markAsReadRoomData.getRoom().getRoomID());
                    }
                    break;
                case kSocketMarkAsUnreadRoom:
                    TAPEmitModel<TAPUpdateRoomResponse> markAsUnreadRoomEmit = TAPUtils.fromJSON(new TypeReference<>() {}, emitData);
                    TAPUpdateRoomResponse markAsUnreadRoomData = markAsUnreadRoomEmit.getData();
                    String unreadRoomId = markAsUnreadRoomData.getRoom().getRoomID();
                    ArrayList<String> unreadRooms = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs();
                    if (!unreadRooms.contains(unreadRoomId)) {
                        unreadRooms.add(unreadRoomId);
                    }
                    TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(unreadRooms);
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onMarkRoomAsUnread(markAsUnreadRoomData.getRoom().getRoomID());
                    }
                    break;
                case kSocketScheduleMessageRoom:
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        chatListener.onGetScheduledMessageList();
                    }
                    HashMap<String, Object> scheduledMessageMap = TAPUtils.fromJSON(new TypeReference<TAPEmitModel<HashMap<String, Object>>>() {}, emitData).getData();
                    TAPMessageModel scheduledMessage = TAPEncryptorManager.getInstance().decryptMessage(scheduledMessageMap);
                    if (scheduledMessage == null) {
                        return;
                    }
                    TAPDataManager.getInstance(instanceKey).insertToDatabase(TAPMessageEntity.fromMessageModel(scheduledMessage));
                    break;
                case kSocketBlockUser:
                    TAPEmitModel<TAPOnlineStatusModel> blockUserEmit = TAPUtils
                            .fromJSON(new TypeReference<>() {
                            }, emitData);
                    TAPOnlineStatusModel blockUserEmitData = blockUserEmit.getData();
                    TAPUserModel blockedUser = blockUserEmitData.getUser();
                    ArrayList<String> blockedUsers = TAPDataManager.getInstance(instanceKey).getBlockedUserIds();
                    if (!blockedUsers.contains(blockedUser.getUserID())) {
                        blockedUsers.add(blockedUser.getUserID());
                    }
                    TAPDataManager.getInstance(instanceKey).saveBlockedUserIds(blockedUsers);
                    TAPContactManager.getInstance(instanceKey).updateUserData(blockedUser);
                    TapCoreContactManager.getInstance(instanceKey).triggerContactBlocked(blockedUser);
                    break;
                case kSocketUnblockUser:
                    TAPEmitModel<TAPOnlineStatusModel> unblockUserEmit = TAPUtils
                            .fromJSON(new TypeReference<>() {
                            }, emitData);
                    TAPOnlineStatusModel unblockUserEmitData = unblockUserEmit.getData();
                    TAPUserModel unblockedUser = unblockUserEmitData.getUser();
                    ArrayList<String> blockedUserList = TAPDataManager.getInstance(instanceKey).getBlockedUserIds();
                    blockedUserList.remove(unblockedUser.getUserID());
                    TAPDataManager.getInstance(instanceKey).saveBlockedUserIds(blockedUserList);
                    TAPContactManager.getInstance(instanceKey).updateUserData(unblockedUser);
                    TapCoreContactManager.getInstance(instanceKey).triggerContactUnblocked(unblockedUser);
                    break;
            }
        }
    };

    public void addChatListener(TAPChatListener chatListener) {
        chatListeners.remove(chatListener);
        chatListeners.add(chatListener);
    }

    public void removeChatListener(TAPChatListener chatListener) {
        chatListeners.remove(chatListener);
    }

    public void removeChatListenerAt(int index) {
        chatListeners.remove(index);
    }

    public void clearChatListener() {
        chatListeners.clear();
    }

    public TAPRoomModel getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(TAPRoomModel room) {
        this.activeRoom = room;
        if (null != room) {
            setOpenRoom(room.getRoomID());
        }
    }

    public String getOpenRoom() {
        return openRoom;
    }

    public void setOpenRoom(String openRoom) {
        this.openRoom = openRoom;
    }

    public TAPUserModel getActiveUser() {
        return activeUser == null ? TAPDataManager.getInstance(instanceKey).getActiveUser() : activeUser;
    }

    public void setActiveUser(TAPUserModel user) {
        this.activeUser = user;
    }

//    public Map<String, TAPMessageModel> getMessageQueueInActiveRoom() {
//        Map<String, TAPMessageModel> roomQueue = new LinkedHashMap<>();
//        for (Map.Entry<String, TAPMessageModel> entry : pendingMessages.entrySet()) {
//            if (entry.getValue().getRoom().getRoomID().equals(activeRoom)) {
//                roomQueue.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return roomQueue;
//    }

    public boolean hasPendingMessages() {
        return !waitingResponses.isEmpty() || !pendingMessages.isEmpty();
    }

    public HashMap<String, TapSendMessageInterface> getSendMessageListeners() {
        return null == sendMessageListeners ? sendMessageListeners = new HashMap<>() : sendMessageListeners;
    }

    /**
     * generate room ID
     */
    public String arrangeRoomId(String userId, String friendId) {
        int myId = (null != userId && !"null".equals(userId)) ? Integer.parseInt(userId) : 0;
        int fId = (null != friendId && !"null".equals(friendId)) ? Integer.parseInt(friendId) : 0;
        return myId < fId ? myId + "-" + fId : fId + "-" + myId;
    }

    /**
     * get other user ID from the currently active room
     */
    public String getOtherUserIdFromRoom(String roomID) {
        try {
            String[] splitRoomID = roomID.split("-");
            return !splitRoomID[0].equals(getActiveUser().getUserID()) ? splitRoomID[0] : splitRoomID[1];
        } catch (Exception e) {
            return "0";
        }
    }

    public void sendMessage(TAPMessageModel message, TapSendMessageInterface listener) {
        if (null != listener) {
            sendMessageListeners.put(message.getLocalID(), listener);
            listener.onStart(message);
        }
        triggerListenerAndSendMessage(message, true);
    }

    public void sendTextMessage(String textMessage) {
        if (null == activeRoom) {
            return;
        }
        sendTextMessage(textMessage, 0L);
    }

    public void sendTextMessage(String textMessage, Long scheduledTime) {
        if (null == activeRoom) {
            return;
        }
        if (scheduledTime < System.currentTimeMillis()) {
            sendTextMessageWithRoomModel(textMessage, activeRoom);
        } else {
            sendTextMessageWithRoomModel(textMessage, activeRoom, scheduledTime);
        }
    }


    public void sendLinkMessage(String textMessage, HashMap<String, Object> data) {
        sendLinkMessage(textMessage, data, 0L);
    }

    public void sendLinkMessage(String textMessage, HashMap<String, Object> data, Long scheduledTime) {
        if (null == activeRoom) {
            return;
        }
        if (scheduledTime < System.currentTimeMillis()) {
            sendLinkMessageWithRoomModel(textMessage, activeRoom, data);
        } else {
            sendLinkMessageWithRoomModel(textMessage, activeRoom, data, scheduledTime);
        }
    }

    public void sendImageMessageToServer(TAPMessageModel messageModel) {
        sendImageMessageToServer(messageModel, 0L);
    }

    public void sendImageMessageToServer(TAPMessageModel messageModel, Long scheduledTime) {
        removeUploadingMessageFromHashMap(messageModel.getLocalID());
        uploadingScheduledMessages.remove(messageModel.getLocalID());
        if (scheduledTime < System.currentTimeMillis()) {
            triggerListenerAndSendMessage(messageModel, false);
        } else {
            triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, false);
        }
    }

    public void sendFileMessageToServer(TAPMessageModel messageModel) {
        sendFileMessageToServer(messageModel, 0L);
    }

    public void sendFileMessageToServer(TAPMessageModel messageModel, Long scheduledTime) {
        removeUploadingMessageFromHashMap(messageModel.getLocalID());
        uploadingScheduledMessages.remove(messageModel.getLocalID());
        if (scheduledTime < System.currentTimeMillis()) {
            triggerListenerAndSendMessage(messageModel, false);
        } else {
            triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, false);
        }
    }

    public void sendProductMessageToServer(HashMap<String, Object> productList, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        TAPMessageModel productMessage = createProductMessageModel(productList, roomModel);
        if (null != listener) {
            sendMessageListeners.put(productMessage.getLocalID(), listener);
            listener.onStart(productMessage);
        }
        triggerListenerAndSendMessage(productMessage, true);
    }

    public void sendLocationMessage(TAPRoomModel room, String address, Double latitude, Double longitude) {
        sendLocationMessage(room, address, latitude, longitude, 0L);
    }

    public void sendLocationMessage(TAPRoomModel room, String address, Double latitude, Double longitude, Long scheduledTime) {
        if (scheduledTime < System.currentTimeMillis()) {
            checkAndSendForwardedMessage(room);
            triggerListenerAndSendMessage(createLocationMessageModel(address, latitude, longitude, room), true);
        } else {
            triggerListenerAndSendScheduledMessage(createLocationMessageModel(address, latitude, longitude, room), scheduledTime, true);
        }
    }

    public void sendLocationMessage(String address, Double latitude, Double longitude, TAPRoomModel room, TapSendMessageInterface listener) {
        checkAndSendForwardedMessage(room);
        TAPMessageModel messageModel = createLocationMessageModel(address, latitude, longitude, room);
        if (null != listener) {
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
        }
        triggerListenerAndSendMessage(messageModel, true);
    }

    public void sendTextMessageWithRoomModel(String textMessage, TAPRoomModel roomModel) {
        sendTextMessageWithRoomModel(textMessage, roomModel, 0L);
    }

    public void sendTextMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, Long scheduledTime) {

        Integer startIndex;

        if (scheduledTime < System.currentTimeMillis()) {
            checkAndSendForwardedMessage(roomModel);
        }

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel;
                List<String> urls = TAPUtils.getUrlsFromString(substr);
                if (urls.isEmpty()) {
                    messageModel = createTextMessage(substr, roomModel, getActiveUser());
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    data.put(URLS, urls);
                    messageModel = createLinkMessage(substr, roomModel, getActiveUser(), data);
                }
                // Add entity to list
                messageEntities.add(TAPMessageEntity.fromMessageModel(messageModel));

                // Send truncated message
                if (scheduledTime < System.currentTimeMillis()) {
                    triggerListenerAndSendMessage(messageModel, true);
                } else {
                    triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, true);
                }
            }
        } else {

                TAPMessageModel messageModel;
                List<String> urls = TAPUtils.getUrlsFromString(textMessage);
                if (urls.isEmpty()) {
                    messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    data.put(URLS, urls);
                    messageModel = createLinkMessage(textMessage, roomModel, getActiveUser(), data);
                }
            // Send message
            if (scheduledTime < System.currentTimeMillis()) {
                triggerListenerAndSendMessage(messageModel, true);
            } else {
                triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, true);
            }
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    public void sendTextMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        Integer startIndex;

        checkAndSendForwardedMessage(roomModel);

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            //List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel = createTextMessage(substr, roomModel, getActiveUser());

                if (null != listener) {
                    sendMessageListeners.put(messageModel.getLocalID(), listener);
                    listener.onStart(messageModel);
                }

                // Add entity to list
                //messageEntities.add(TAPMessageEntity.fromMessageModel()(messageModel));

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
                TAPMessageModel messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
            if (null != listener) {
                sendMessageListeners.put(messageModel.getLocalID(), listener);
                listener.onStart(messageModel);
            }
            // Send message
            triggerListenerAndSendMessage(messageModel, true);
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    public void sendLinkMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, HashMap<String, Object> data) {
        sendLinkMessageWithRoomModel(textMessage, roomModel, data, 0L);
    }

    public void sendLinkMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, HashMap<String, Object> data, Long scheduledTime) {

        Integer startIndex;

        if (scheduledTime < System.currentTimeMillis()) {
            checkAndSendForwardedMessage(roomModel);
        }

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel;
                List<String> urls = TAPUtils.getUrlsFromString(substr);
                if (urls.isEmpty()) {
                    messageModel = createTextMessage(substr, roomModel, getActiveUser());
                } else {
                    HashMap<String, Object> newData = new HashMap<>(data);
                    String url = TAPUtils.setUrlWithProtocol(urls.get(0));
                    if (!url.equalsIgnoreCase((String) data.get(URL))) {
                        newData = new HashMap<>();
                        newData.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    }
                    newData.put(URLS, urls);
                    messageModel = createLinkMessage(substr, roomModel, getActiveUser(), newData);
                }
                // Add entity to list
                messageEntities.add(TAPMessageEntity.fromMessageModel(messageModel));

                // Send truncated message
                if (scheduledTime < System.currentTimeMillis()) {
                    triggerListenerAndSendMessage(messageModel, true);
                } else {
                    triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, true);
                }
            }
        } else {
            TAPMessageModel messageModel;
            List<String> urls = TAPUtils.getUrlsFromString(textMessage);
            if (urls.isEmpty()) {
                messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
            } else {
                HashMap<String, Object> newData = new HashMap<>(data);
                String url = TAPUtils.setUrlWithProtocol(urls.get(0));
                if (!url.equalsIgnoreCase((String) data.get(URL))) {
                    newData = new HashMap<>();
                    newData.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                }
                newData.put(URLS, urls);
                messageModel = createLinkMessage(textMessage, roomModel, getActiveUser(), newData);
            }
            // Send message
            if (scheduledTime < System.currentTimeMillis()) {
                triggerListenerAndSendMessage(messageModel, true);
            } else {
                triggerListenerAndSendScheduledMessage(messageModel, scheduledTime, true);
            }
        }
    }

    public void sendLinkMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, HashMap<String, Object> data, TapSendMessageInterface listener) {
        Integer startIndex;

        checkAndSendForwardedMessage(roomModel);

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                List<String> urls = TAPUtils.getUrlsFromString(substr);
                TAPMessageModel messageModel;
                HashMap<String, Object> newData = new HashMap<>(data);
                if (urls.isEmpty()) {
                    newData = new HashMap<>();
                } else {
                    if (!TAPUtils.setUrlWithProtocol(urls.get(0)).equalsIgnoreCase((String) data.get(URL))) {
                        newData = new HashMap<>();
                        newData.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    }
                    newData.put(URLS, urls);
                }
                messageModel = createLinkMessage(substr, roomModel, getActiveUser(), newData);

                if (null != listener) {
                    sendMessageListeners.put(messageModel.getLocalID(), listener);
                    listener.onStart(messageModel);
                }

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
            TAPMessageModel messageModel = createLinkMessage(textMessage, roomModel, getActiveUser(), data);
            if (null != listener) {
                sendMessageListeners.put(messageModel.getLocalID(), listener);
                listener.onStart(messageModel);
            }
            // Send message
            triggerListenerAndSendMessage(messageModel, true);
        }
    }

    public void sendDirectReplyTextMessage(String textMessage, TAPRoomModel roomModel) {
        if (!TapTalk.isForeground)
            TAPConnectionManager.getInstance(instanceKey).connect();

        Integer startIndex;
        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel;
                List<String> urls = TAPUtils.getUrlsFromString(substr);
                if (urls.isEmpty()) {
                    messageModel = createTextMessage(substr, roomModel, getActiveUser());
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    data.put(URLS, urls);
                    messageModel = createLinkMessage(substr, roomModel, getActiveUser(), data);
                }
                // Add entity to list
                messageEntities.add(TAPMessageEntity.fromMessageModel(messageModel));

                // save LocalID to list of Reply Local IDs
                // gunanya adalah untuk ngecek kapan semua reply message itu udah kekirim atau belom
                // ini kalau misalnya message yang di kirim > character limit
                addReplyMessageLocalID(messageModel.getLocalID());

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
                TAPMessageModel messageModel;
                List<String> urls = TAPUtils.getUrlsFromString(textMessage);
                if (urls.isEmpty()) {
                    messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
                    data.put(URLS, urls);
                    messageModel = createLinkMessage(textMessage, roomModel, getActiveUser(), data);
                }

            // save LocalID to list of Reply Local IDs
            // gunanya adalah untuk ngecek kapan semua reply message itu udah kekirim atau belom
            // ini kalau misalnya message yang di kirim < character limit (1x dikirim aja)
            addReplyMessageLocalID(messageModel.getLocalID());

            // Send message
            triggerListenerAndSendMessage(messageModel, true);
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    private TAPMessageModel createTextMessage(String message, TAPRoomModel room, TAPUserModel user) {
        // Create new TAPMessageModel based on text
        if (null == getQuotedMessages().get(room.getRoomID())) {
            return TAPMessageModel.Builder(
                    message,
                    room,
                    TYPE_TEXT,
                    System.currentTimeMillis(),
                    user, TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    null
            );
        } else {
            HashMap<String, Object> data = new HashMap<>();
            if (null != getUserInfo(room.getRoomID())) {
                data.put(USER_INFO, getUserInfo(room.getRoomID()));
            }
            TAPMessageModel messageWithQuote = TAPMessageModel.BuilderWithQuotedMessage(
                    message,
                    room,
                    TYPE_TEXT,
                    System.currentTimeMillis(),
                    user, TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data,
                    getQuotedMessages().get(room.getRoomID()),
                    instanceKey
            );
            setQuotedMessage(room.getRoomID(), null, 0);
            return messageWithQuote;
        }
    }

    /**
     * Scheduled Message Functions
     */

    public void editScheduledMessage(int scheduledMessageID, TAPMessageModel message, String updatedText, TapCommonListener listener, boolean isTypeChangeEnabled) {
        if (message.getType() == TYPE_TEXT || message.getType() == TYPE_LINK) {
            if (updatedText.length() > CHARACTER_LIMIT) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, CHARACTER_LIMIT));
                }
                return;
            }
            message.setBody(updatedText);
            List<String> urls = TAPUtils.getUrlsFromString(updatedText);
            if (isTypeChangeEnabled) {
                if (urls.isEmpty()) {
                    HashMap<String, Object> data = message.getData();
                    if (data != null) {
                        data.put(URL, null);
                        data.put(URLS, null);
                        data.put(TITLE, null);
                        data.put(DESCRIPTION, null);
                        data.put(IMAGE, null);
                        data.put(TYPE, null);
                        message.setData(data);
                    }
                    message.setType(TYPE_TEXT);
                } else {
                    HashMap<String, Object> data = message.getData() == null ? new HashMap<>() : message.getData();
                    data.put(URLS, urls);
                    message.setData(data);
                    message.setType(TYPE_LINK);
                }
            }
        }
        else if (message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) {
            if (updatedText.length() > TapTalk.getMaxCaptionLength(instanceKey)) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, TapTalk.getMaxCaptionLength(instanceKey)));
                }
                return;
            }
            HashMap<String, Object> data = message.getData();
            if (data != null) {
                data.put(CAPTION, updatedText);
                message.setData(data);
                if (message.getType() == TYPE_IMAGE) {
                    message.setBody(TAPChatManager.getInstance(instanceKey).generateImageCaption(updatedText));
                }
                else {
                    message.setBody(TAPChatManager.getInstance(instanceKey).generateVideoCaption(updatedText));
                }
            }
        }
        else {
            if (null != listener) {
                listener.onError(ERROR_CODE_EDIT_INVALID_MESSAGE_TYPE, ERROR_MESSAGE_EDIT_INVALID_MESSAGE_TYPE);
            }
            return;
        }
        message.setIsMessageEdited(false);
        TAPDataManager.getInstance(instanceKey).editScheduledMessageContent(scheduledMessageID, message, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void editScheduledMessage(int scheduledMessageID, TAPMessageModel updatedMessage, TapCommonListener listener) {
        updatedMessage.setIsMessageEdited(false);
        TAPDataManager.getInstance(instanceKey).editScheduledMessageContent(scheduledMessageID, updatedMessage, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                super.onSuccess(response);
                listener.onSuccess(response.getMessage());
            }

            @Override
            public void onError(TAPErrorModel error) {
                super.onError(error);
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                super.onError(errorMessage);
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }

    private void triggerListenerAndSendScheduledMessage(TAPMessageModel message, Long scheduledTime, boolean isNotifyChatListener) {
        // update status text
        message.setMessageStatusText(TAPTimeFormatter.formatClock(scheduledTime));
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty() && isNotifyChatListener) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                if (null != chatListener) {
                    chatListener.onCreateScheduledMessage(new TapScheduledMessageModel(scheduledTime, message));
                }
            }
        }
        runScheduledMessageSequence(message, scheduledTime);
    }

    private void runScheduledMessageSequence(TAPMessageModel message, Long scheduledTime) {
        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
            // Send message if socket is connected
            List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
            TAPDataManager.getInstance(instanceKey).createScheduledMessage(message, scheduledTime, new TAPDefaultDataView<>() {
                @Override
                public void onSuccess(TapCreateScheduledMessageResponse response) {
                    super.onSuccess(response);
                    pendingScheduledMessages.remove(message.getLocalID());
                    checkAndSendPendingScheduledMessages();
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        if (null != chatListener) {
                            chatListener.onScheduledMessageSent(new TapScheduledMessageModel(scheduledTime, message));
                        }
                    }
                }

                @Override
                public void onError(TAPErrorModel error) {
                    super.onError(error);
                    checkAndSendPendingScheduledMessages();
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        if (null != chatListener) {
                            chatListener.onScheduledSendFailed(new TapScheduledMessageModel(scheduledTime, message));
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    super.onError(errorMessage);
                    checkAndSendPendingScheduledMessages();
                    for (TAPChatListener chatListener : chatListenersCopy) {
                        if (null != chatListener) {
                            chatListener.onScheduledSendFailed(new TapScheduledMessageModel(scheduledTime, message));
                        }
                    }
                }
            });
        } else {
            // Add message to queue if socket is not connected
            pendingScheduledMessages.put(message.getLocalID(), new TapScheduledMessageModel(scheduledTime, message));
        }
    }

    /**
     * Construct Link Message Model
     */

    private TAPMessageModel createLinkMessage(String message, TAPRoomModel room, TAPUserModel user, HashMap<String, Object> data) {
        // Create new TAPMessageModel for link type
        if (null == getQuotedMessages().get(room.getRoomID())) {
            return TAPMessageModel.Builder(
                    message,
                    room,
                    TYPE_LINK,
                    System.currentTimeMillis(),
                    user, TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data
            );
        } else {
            if (null != getUserInfo(room.getRoomID())) {
                data.put(USER_INFO, getUserInfo(room.getRoomID()));
            }
            TAPMessageModel messageWithQuote = TAPMessageModel.BuilderWithQuotedMessage(
                    message,
                    room,
                    TYPE_LINK,
                    System.currentTimeMillis(),
                    user, TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data,
                    getQuotedMessages().get(room.getRoomID()),
                    instanceKey
            );
            setQuotedMessage(room.getRoomID(), null, 0);
            return messageWithQuote;
        }
    }

    /**
     * Construct Product Message Model
     */
    private TAPMessageModel createProductMessageModel(HashMap<String, Object> product, TAPRoomModel roomModel) {
        return TAPMessageModel.Builder(
                "Product List",
                roomModel,
                TYPE_PRODUCT,
                System.currentTimeMillis(),
                activeUser,
                TYPE_PERSONAL == roomModel.getType() ?
                        getOtherUserIdFromRoom(roomModel.getRoomID()) :
                        "0",
                product
        );
    }

    /**
     * Construct Location Message Model
     */

    private TAPMessageModel createLocationMessageModel(String address, Double latitude, Double longitude, TAPRoomModel roomModel) {
        if (null == getQuotedMessage(roomModel.getRoomID())) {
            return TAPMessageModel.Builder(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    roomModel,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataLocationModel(address, latitude, longitude).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataLocationModel(address, latitude, longitude).toHashMap();
            if (null != getUserInfo(roomModel.getRoomID())) {
                data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
            }
            TAPMessageModel messageWithQuote = TAPMessageModel.BuilderWithQuotedMessage(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    roomModel,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage(roomModel.getRoomID()),
                    instanceKey);
            setQuotedMessage(roomModel.getRoomID(), null, 0);
            return messageWithQuote;
        }

    }

    /**
     * Construct File Message Model
     */

    private TAPMessageModel createFileMessageModel(Context context, Uri uri, TAPRoomModel roomModel, String caption, TapSendMessageInterface listener) {

        String filePath = TAPFileUtils.getFilePath(context, uri);

        if (null == filePath || filePath.isEmpty()) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve file path from provided Uri.");
            }
            return null;
        }

        File file = new File(filePath);
        return createFileMessageModel(context, file, roomModel, caption, listener);
    }

    private TAPMessageModel createFileMessageModel(Context context, File file, TAPRoomModel roomModel, String caption, TapSendMessageInterface listener) {
        try {
            String fileName = file.getName();
            Number fileSize = file.length();
            String fileMimeType = TAPUtils.getFileMimeType(file);
            if (fileMimeType == null || fileMimeType.isEmpty()) {
                fileMimeType = "application/octet-stream";
            }

            // Build message model
            TAPMessageModel messageModel;
            Uri fileUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);
            HashMap<String, Object> data = new TAPDataFileModel(fileName, fileMimeType, fileSize).toHashMap();
            data.put(FILE_URI, fileUri.toString());
            if (caption != null && !caption.isEmpty()) {
                data.put(CAPTION, caption);
            }
            if (null == getQuotedMessage(roomModel.getRoomID())) {
                messageModel = TAPMessageModel.Builder(
                        generateFileMessageBody(fileName),
                        roomModel,
                        TYPE_FILE,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                        data);
            } else {
                if (null != getUserInfo(roomModel.getRoomID())) {
                    data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
                }
                messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                        generateFileMessageBody(fileName),
                        roomModel,
                        TYPE_FILE,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                        data,
                        getQuotedMessage(roomModel.getRoomID()),
                        instanceKey);
                setQuotedMessage(roomModel.getRoomID(), null, 0);
            }
            // Save file Uri
//        TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), fileUri);
            TAPFileDownloadManager.getInstance(instanceKey).addFileProviderPath(fileUri, file.getAbsolutePath());
            return messageModel;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve file data from provided Uri.");
            }
            return null;
        }
    }

    // Create file message with remote url
    public void createFileMessageModel(String fileUrl, TAPRoomModel roomModel, String caption, String fileName, String mimeType, TapSendMessageInterface listener) {
        new Thread(() -> {
            try {
                String dataFileName;
                if (fileName != null && !fileName.isEmpty()) {
                    dataFileName = fileName;
                }
                else {
                    dataFileName = TAPFileUtils.getFileNameFromURL(fileUrl);
                    if (dataFileName == null || dataFileName.isEmpty()) {
                        dataFileName = TAPTimeFormatter.formatTime(System.currentTimeMillis(), "yyyyMMdd_HHmmssSSS");
                    }
                }

                String fileMimeType;
                if (mimeType != null && !mimeType.isEmpty()) {
                    fileMimeType = mimeType;
                }
                else {
                    fileMimeType = TAPUtils.getMimeTypeFromUrl(fileUrl);
                    if (fileMimeType == null || fileMimeType.isEmpty()) {
                        fileMimeType = "application/octet-stream";
                    }
                }

                URL url = new URL(fileUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                Number size = connection.getContentLength();

                // Build message model
                TAPMessageModel messageModel;
                HashMap<String, Object> data = new TAPDataFileModel(dataFileName, fileMimeType, size).toHashMap();
                data.put(FILE_URL, fileUrl);
                if (caption != null && !caption.isEmpty()) {
                    data.put(CAPTION, caption);
                }
                data.remove(FILE_URI);
                if (null == getQuotedMessage(roomModel.getRoomID())) {
                    messageModel = TAPMessageModel.Builder(
                            generateFileMessageBody(dataFileName),
                            roomModel,
                            TYPE_FILE,
                            System.currentTimeMillis(),
                            activeUser,
                            TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                            data);
                }
                else {
                    if (null != getUserInfo(roomModel.getRoomID())) {
                        data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
                    }
                    messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                            generateFileMessageBody(dataFileName),
                            roomModel,
                            TYPE_FILE,
                            System.currentTimeMillis(),
                            activeUser,
                            TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                            data,
                            getQuotedMessage(roomModel.getRoomID()),
                            instanceKey);
                    setQuotedMessage(roomModel.getRoomID(), null, 0);
                }
                // Save file Uri
                if (null != listener) {
                    listener.onStart(messageModel);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                if (null != listener) {
                    listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve file data.");
                }
            }
        }).start();
    }

    private void addFileMessageToUploadQueue(Context context, TAPMessageModel messageModel, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        addFileMessageToUploadQueue(context, messageModel, roomModel, listener, 0L);
    }


    private void addFileMessageToUploadQueue(Context context, TAPMessageModel messageModel, TAPRoomModel roomModel, TapSendMessageInterface listener, Long scheduledTime) {
        // Check if file size exceeds limit
        long maxFileUploadSize = TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize();
        if (null != messageModel.getData() &&
                null != messageModel.getData().get(SIZE) &&
                ((Number) messageModel.getData().get(SIZE)).longValue() > maxFileUploadSize
        ) {
            if (null != listener) {
                listener.onError(
                        messageModel,
                        ERROR_CODE_EXCEEDED_MAX_SIZE,
                        String.format(
                                Locale.getDefault(),
                                ERROR_MESSAGE_EXCEEDED_MAX_SIZE,
                                TAPUtils.getStringSizeLengthFile(maxFileUploadSize)
                        )
                );
            }
            return;
        }

        if (null != listener) {
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
        }

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        if (scheduledTime < System.currentTimeMillis()) {
            triggerSendMessageListener(messageModel);
        } else {
            triggerSendMessageListener(messageModel, scheduledTime);
        }

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, roomModel.getRoomID(), messageModel, listener);
    }

    /**
     * Create file message model and call upload api
     */
    private void createFileMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file, Long scheduledTime) {
        createFileMessageModelAndAddToUploadQueue(context, roomModel, file, null, scheduledTime);
    }

    private void createFileMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener, Long scheduledTime) {
        createFileMessageModelAndAddToUploadQueue(context, roomModel, file, "", listener, scheduledTime);
    }

    private void createFileMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file, String caption, TapSendMessageInterface listener, Long scheduledTime) {
        checkAndSendForwardedMessage(roomModel);

        TAPMessageModel messageModel = createFileMessageModel(context, file, roomModel, caption, listener);

        if (null == messageModel) {
            return;
        }
        if (scheduledTime > System.currentTimeMillis()) {
            uploadingScheduledMessages.put(messageModel.getLocalID(), new TapScheduledMessageModel(scheduledTime, messageModel));
        }

        addFileMessageToUploadQueue(context, messageModel, roomModel, listener, scheduledTime);
    }

    private void createFileMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri uri, String caption, TapSendMessageInterface listener) {
        checkAndSendForwardedMessage(roomModel);

        TAPMessageModel messageModel = createFileMessageModel(context, uri, roomModel, caption, listener);

        if (null == messageModel) {
            return;
        }

        addFileMessageToUploadQueue(context, messageModel, roomModel, listener);
    }

    public TAPMessageModel createTemporaryMediaMessageWithUrl(int type, String url, String caption, TAPRoomModel room, TAPMessageModel quotedMessage) {
        return createTemporaryMediaMessageWithUrl(type, url, caption, "", "", room, quotedMessage);
    }

    public TAPMessageModel createTemporaryMediaMessageWithUrl(int type, String url, String caption, String fileName, String mimeType, TAPRoomModel room, TAPMessageModel quotedMessage) {
        String body;
        if (type == TYPE_IMAGE) {
            body = generateImageCaption(caption);
        }
        else if (type == TYPE_VIDEO) {
            body = generateVideoCaption(caption);
        }
        else if (type == TYPE_FILE) {
            body = generateFileMessageBody(caption);
        }
        else if (type == TYPE_VOICE) {
            body = generateVoiceNoteMessageBody(caption);
        }
        else {
            body = "";
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put(URL, url);
        if (caption != null && !caption.isEmpty()) {
            data.put(CAPTION, caption);
        }
        if (fileName != null && !fileName.isEmpty()) {
            data.put(FILE_NAME, fileName);
        }
        if (mimeType != null && !mimeType.isEmpty()) {
            data.put(MEDIA_TYPE, mimeType);
        }
        else {
            String mediaType = TAPUtils.getMimeTypeFromUrl(url);
            if (mediaType == null || mediaType.isEmpty()) {
                if (type == TYPE_IMAGE) {
                    mediaType = IMAGE_JPEG;
                }
                else if (type == TYPE_VIDEO) {
                    mediaType = VIDEO_MP4;
                }
                else if (type == TYPE_VOICE) {
                    mediaType = AUDIO_MP3;
                }
                else {
                    mediaType = "application/octet-stream";
                }
            }
            data.put(MEDIA_TYPE, mediaType);
        }
        if (null != getUserInfo(room.getRoomID())) {
            data.put(USER_INFO, getUserInfo(room.getRoomID()));
        }
        if (quotedMessage != null) {
            return TAPMessageModel.BuilderWithQuotedMessage(
                body,
                room,
                type,
                System.currentTimeMillis(),
                getActiveUser(),
                TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                data,
                quotedMessage,
                instanceKey
            );
        }
        else {
            return TAPMessageModel.Builder(
                body,
                room,
                type,
                System.currentTimeMillis(),
                getActiveUser(),
                TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                data
            );
        }
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, Uri uri, TapSendMessageInterface listener) {
        /*new Thread(() -> */createFileMessageModelAndAddToUploadQueue(context, roomModel, uri, "", listener)/*).start()*/;
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, Uri uri, String caption, TapSendMessageInterface listener) {
        /*new Thread(() -> */createFileMessageModelAndAddToUploadQueue(context, roomModel, uri, caption, listener)/*).start()*/;
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener) {
        /*new Thread(() -> */createFileMessageModelAndAddToUploadQueue(context, roomModel, file, listener, 0L)/*).start()*/;
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, File file, String caption, TapSendMessageInterface listener) {
        /*new Thread(() -> */createFileMessageModelAndAddToUploadQueue(context, roomModel, file, caption, listener, 0L)/*).start()*/;
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, File file) {
        sendFileMessage(context, roomModel, file, 0L);
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, File file, Long scheduledTime) {
        /*new Thread(() -> */createFileMessageModelAndAddToUploadQueue(context, roomModel, file, scheduledTime)/*).start()*/;
    }

    public void sendFileMessage(Context context, TAPMessageModel fileModel) {
        //new Thread(() -> {
            addUploadingMessageToHashMap(fileModel);
            TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, fileModel.getRoom().getRoomID(), fileModel);
        //}).start();
    }

    private String generateFileMessageBody(String fileName) {
        if (fileName == null) {
            fileName = "";
        }
        return TapTalk.appContext.getString(R.string.tap_emoji_file) + " " + (fileName.isEmpty() ? TapTalk.appContext.getString(R.string.tap_file) : fileName);
    }


    /**
     * Construct Audio Message Model
     */

    private TAPMessageModel createVoiceNoteMessageModel(Context context, Uri uri, TAPRoomModel roomModel, TapSendMessageInterface listener) {

        String path = TAPFileUtils.getFilePath(context, uri);

        if (null == path || path.isEmpty()) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve file path from provided Uri.");
            }
            return null;
        }

        File file = new File(path);
        return createVoiceNoteMessageModel(context, file, roomModel, listener);
    }

    private TAPMessageModel createVoiceNoteMessageModel(Context context, File file, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        try {
            String fileName = file.getName();
            Number fileSize = file.length();
            String fileMimeType = TAPUtils.getFileMimeType(file);
            if (fileMimeType == null || fileMimeType.isEmpty()) {
                fileMimeType = AUDIO_MP3;
            }

            // Build message model
            TAPMessageModel messageModel;
            Uri fileUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);

            // Get audio data
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, fileUri);

            int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            HashMap<String, Object> data = new TAPDataFileModel(fileName, fileMimeType, fileSize).toHashMap();
            data.put(DURATION, duration);
            data.put(FILE_URI, fileUri.toString());
            if (null == getQuotedMessage(roomModel.getRoomID())) {
                messageModel = TAPMessageModel.Builder(
                        generateVoiceNoteMessageBody(),
                        roomModel,
                        TYPE_VOICE,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                        data);
            } else {
                if (null != getUserInfo(roomModel.getRoomID())) {
                    data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
                }
                messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                        generateVoiceNoteMessageBody(),
                        roomModel,
                        TYPE_VOICE,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                        data,
                        getQuotedMessage(roomModel.getRoomID()),
                        instanceKey);
                setQuotedMessage(roomModel.getRoomID(), null, 0);
            }
            // Save file Uri
//        TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), fileUri);
            TAPFileDownloadManager.getInstance(instanceKey).addFileProviderPath(fileUri, file.getAbsolutePath());
            return messageModel;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve file data from provided Uri.");
            }
            return null;
        }
    }

    private void addVoiceNoteMessageToUploadQueue(Context context, TAPMessageModel messageModel, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        // Check if file size exceeds limit
        long maxFileUploadSize = TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize();
        if (null != messageModel.getData() &&
                null != messageModel.getData().get(SIZE) &&
                ((Number) messageModel.getData().get(SIZE)).longValue() > maxFileUploadSize
        ) {
            if (null != listener) {
                listener.onError(
                        messageModel,
                        ERROR_CODE_EXCEEDED_MAX_SIZE,
                        String.format(
                                Locale.getDefault(),
                                ERROR_MESSAGE_EXCEEDED_MAX_SIZE,
                                TAPUtils.getStringSizeLengthFile(maxFileUploadSize)
                        )
                );
            }
            return;
        }

        if (null != listener) {
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
        }

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, roomModel.getRoomID(), messageModel, listener);
    }

    /**
     * Create voice message model and call upload api
     */
    private void createVoiceNoteMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file) {
        createVoiceNoteMessageModelAndAddToUploadQueue(context, roomModel, file, null);
    }

    private void createVoiceNoteMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener) {
        checkAndSendForwardedMessage(roomModel);

        TAPMessageModel messageModel = createVoiceNoteMessageModel(context, file, roomModel, listener);

        if (null == messageModel) {
            return;
        }

        addVoiceNoteMessageToUploadQueue(context, messageModel, roomModel, listener);
    }

    private void createVoiceNoteMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri uri, TapSendMessageInterface listener) {
        checkAndSendForwardedMessage(roomModel);

        TAPMessageModel messageModel = createVoiceNoteMessageModel(context, uri, roomModel, listener);

        if (null == messageModel) {
            return;
        }

        addVoiceNoteMessageToUploadQueue(context, messageModel, roomModel, listener);
    }

    public void sendVoiceNoteMessage(Context context, TAPRoomModel roomModel, Uri uri, TapSendMessageInterface listener) {
        /*new Thread(() -> */createVoiceNoteMessageModelAndAddToUploadQueue(context, roomModel, uri, listener);/*).start()*/;
    }

    public void sendVoiceNoteMessage(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener) {
        /*new Thread(() -> */createVoiceNoteMessageModelAndAddToUploadQueue(context, roomModel, file, listener)/*).start()*/;
    }

    public void sendVoiceNoteMessage(Context context, TAPRoomModel roomModel, File file) {
        /*new Thread(() -> */createVoiceNoteMessageModelAndAddToUploadQueue(context, roomModel, file)/*).start()*/;
    }

    private String generateVoiceNoteMessageBody() {
        return generateVoiceNoteMessageBody("");
    }

    private String generateVoiceNoteMessageBody(String caption) {
        if (caption == null) {
            caption = "";
        }
        return TapTalk.appContext.getString(R.string.tap_emoji_voice_note) + " " + (caption.isEmpty() ? TapTalk.appContext.getString(R.string.tap_voice) : caption);
    }

    /**
     * Construct Image Message Model
     */

    private TAPMessageModel createImageMessageModel(Context context, Uri fileUri, String caption, TAPRoomModel roomModel) {
        String imageUri = fileUri.toString();
        String imagePath = TAPFileUtils.getFilePath(context, fileUri);
        long size = null == imagePath ? 0L : new File(imagePath).length();

        // Get image width and height
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int imageWidth;
        int imageHeight;
        int orientation = TAPFileUtils.getImageOrientation(imagePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            imageWidth = options.outHeight;
            imageHeight = options.outWidth;
        } else {
            imageWidth = options.outWidth;
            imageHeight = options.outHeight;
        }

        // Build message model
        TAPMessageModel messageModel;
        if (null == getQuotedMessage(roomModel.getRoomID())) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap();
            if (null != getUserInfo(roomModel.getRoomID())) {
                data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage(roomModel.getRoomID()),
                    instanceKey);
            setQuotedMessage(roomModel.getRoomID(), null, 0);
        }
        return messageModel;
    }

    public TAPMessageModel createImageMessageModel(Bitmap bitmap, String caption, TAPRoomModel roomModel) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        long size = bitmap.getByteCount();

        // Build message model
        TAPMessageModel messageModel;
        if (null == getQuotedMessage(roomModel.getRoomID())) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap();
            if (null != getUserInfo(roomModel.getRoomID())) {
                data.put(USER_INFO, getUserInfo(roomModel.getRoomID()));
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage(roomModel.getRoomID()),
                    instanceKey);
            setQuotedMessage(roomModel.getRoomID(), null, 0);
        }
        return messageModel;
    }

    public String generateImageCaption(String caption) {
        if (caption == null) {
            caption = "";
        }
        return TapTalk.appContext.getString(R.string.tap_emoji_photo) + " " + (caption.isEmpty() ? TapTalk.appContext.getString(R.string.tap_photo) : caption);
    }

    private TAPMessageModel createVideoMessageModel(Context context, Uri fileUri, String caption, TAPRoomModel room, TapSendMessageInterface listener) {
        try {
            String videoPath = TAPFileUtils.getFilePath(context, fileUri);

            // Get video data
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            Uri parsedUri = TAPFileUtils.parseFileUri(fileUri);
//            retriever.setDataSource(context, parsedUri);
            retriever.setDataSource(context, fileUri);
            String rotation = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            }
            int width, height;
            if (null != rotation && (rotation.equals("90") || rotation.equals("270"))) {
                // Swap width and height when video is rotated
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            }
            else {
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }
            int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Bitmap thumbnail = retriever.getFrameAtTime();
            String thumbBase64 = "";
            if (thumbnail != null) {
                thumbBase64 = TAPFileUtils.encodeToBase64(TAPFileUploadManager.getInstance(instanceKey).resizeBitmap(thumbnail, THUMB_MAX_DIMENSION));
            }
            retriever.release();
            long size = null == videoPath ? 0L : new File(videoPath).length();

            // Build message model
            TAPMessageModel messageModel;
            HashMap<String, Object> data = new TAPDataImageModel(width, height, size, caption, null, videoPath).toHashMap();
            data.put(DURATION, duration);
            if (thumbBase64 != null && !thumbBase64.isEmpty()) {
                data.put(THUMBNAIL, thumbBase64);
            }
            if (null == getQuotedMessage(room.getRoomID())) {
                messageModel = TAPMessageModel.Builder(
                        generateVideoCaption(caption),
                        room,
                        TYPE_VIDEO,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                        data);
            }
            else {
                if (null != getUserInfo(room.getRoomID())) {
                    data.put(USER_INFO, getUserInfo(room.getRoomID()));
                }
                messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                        generateVideoCaption(caption),
                        room,
                        TYPE_VIDEO,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                        data,
                        getQuotedMessage(room.getRoomID()),
                        instanceKey);
                setQuotedMessage(room.getRoomID(), null, 0);
            }
//        TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), videoPath);
            return messageModel;
        }
        catch (Exception e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve video data from provided Uri.");
            }
            return null;
        }
    }

    // Create video message with remote url
    public void createVideoMessageModel(Context context, String fileUrl, String caption, TAPRoomModel room, TapSendMessageInterface listener) {
        new Thread(() -> {
            try {
                // Get video data
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Uri savedUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(TAPUtils.getUriKeyFromUrl(fileUrl));
                if (savedUri != null) {
                    retriever.setDataSource(context, savedUri);
                }
                else {
                    retriever.setDataSource(fileUrl);
                }
                String rotation = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                }
                int width, height;
                if (null != rotation && (rotation.equals("90") || rotation.equals("270"))) {
                    // Swap width and height when video is rotated
                    width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                }
                else {
                    width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                }
                int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                String mediaType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                Bitmap thumbnail = retriever.getFrameAtTime();
                String thumbBase64 = "";
                if (thumbnail != null) {
                    thumbBase64 = TAPFileUtils.encodeToBase64(TAPFileUploadManager.getInstance(instanceKey).resizeBitmap(thumbnail, THUMB_MAX_DIMENSION));
                }
                retriever.release();

                URL url = new URL(fileUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                long size = connection.getContentLength();

                // Build message model
                TAPMessageModel messageModel;
                HashMap<String, Object> data = new TAPDataImageModel(width, height, size, caption, null, "").toHashMap();
                data.put(FILE_URL, fileUrl);
                data.put(DURATION, duration);
                data.put(MEDIA_TYPE, mediaType);
                if (thumbBase64 != null && !thumbBase64.isEmpty()) {
                    data.put(THUMBNAIL, thumbBase64);
                }
                data.remove(FILE_URI);
                if (null == getQuotedMessage(room.getRoomID())) {
                    messageModel = TAPMessageModel.Builder(
                        generateVideoCaption(caption),
                        room,
                        TYPE_VIDEO,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                        data
                    );
                }
                else {
                    if (null != getUserInfo(room.getRoomID())) {
                        data.put(USER_INFO, getUserInfo(room.getRoomID()));
                    }
                    messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                        generateVideoCaption(caption),
                        room,
                        TYPE_VIDEO,
                        System.currentTimeMillis(),
                        activeUser,
                        TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                        data,
                        getQuotedMessage(room.getRoomID()),
                        instanceKey
                    );
                    setQuotedMessage(room.getRoomID(), null, 0);
                }
                if (null != listener) {
                    listener.onStart(messageModel);
                }
            }
            catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "createVideoMessageModel exception: " + e.getMessage());
                }
                e.printStackTrace();
                if (null != listener) {
                    listener.onError(null, ERROR_CODE_URI_NOT_FOUND, "Unable to retrieve video data.");
                }
            }
        }).start();
    }

    public String generateVideoCaption(String caption) {
        if (caption == null) {
            caption = "";
        }
        return TapTalk.appContext.getString(R.string.tap_emoji_video) + " " + (caption.isEmpty() ? TapTalk.appContext.getString(R.string.tap_video) : caption);
    }

    /**
     * Create image message model and call upload api
     */
    public void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel room, Uri fileUri, String caption) {
        createImageMessageModelAndAddToUploadQueue(context, room, fileUri, caption, 0L);
    }

    public void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel room, Uri fileUri, String caption, Long scheduledTime) {
        TAPMessageModel messageModel = createImageMessageModel(context, fileUri, caption, room);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel, scheduledTime);
        if (scheduledTime > System.currentTimeMillis()) {
            uploadingScheduledMessages.put(messageModel.getLocalID(), new TapScheduledMessageModel(scheduledTime, messageModel));
        }

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, room.getRoomID(), messageModel);
    }

    private void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri fileUri, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createImageMessageModel(context, fileUri, caption, roomModel);

        // Check if caption length exceeds limit
        if (caption.length() > TapTalk.getMaxCaptionLength(instanceKey)) {
            listener.onError(messageModel, ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, TapTalk.getMaxCaptionLength(instanceKey)));
            return;
        }

        if (null != listener) {
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
        }

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel);

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, listener);
    }

    private void createVideoMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri fileUri, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createVideoMessageModel(context, fileUri, caption, roomModel, listener);

        // Return if generated message is null due to exception
        if (null == messageModel) {
            return;
        }

        // Check if caption length exceeds limit
        if (caption.length() > TapTalk.getMaxCaptionLength(instanceKey)) {
            listener.onError(messageModel, ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, TapTalk.getMaxCaptionLength(instanceKey)));
            return;
        }
        // Check if file size exceeds limit
        long maxFileUploadSize = TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize();
        if (null != messageModel.getData() && null != messageModel.getData().get(SIZE) &&
                ((Number) messageModel.getData().get(SIZE)).longValue() > maxFileUploadSize) {
            if (null != listener) {
                listener.onError(
                        messageModel,
                        ERROR_CODE_EXCEEDED_MAX_SIZE,
                        String.format(
                                Locale.getDefault(),
                                ERROR_MESSAGE_EXCEEDED_MAX_SIZE,
                                TAPUtils.getStringSizeLengthFile(maxFileUploadSize)
                        )
                );
            }
            return;
        }

        if (null != listener) {
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
        }

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, listener);
    }

    /**
     * Create Image Message with Bitmap Model and Call Upload API
     *
     * @param context
     * @param room
     * @param bitmap
     * @param caption
     */
    private void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel room, Bitmap bitmap, String caption) {
        TAPMessageModel messageModel = createImageMessageModel(bitmap, caption, room);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, room.getRoomID(), messageModel, bitmap);
    }

    private void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Bitmap bitmap, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createImageMessageModel(bitmap, caption, roomModel);
        sendMessageListeners.put(messageModel.getLocalID(), listener);
        listener.onStart(messageModel);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel, bitmap);

        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, bitmap, listener);
    }

    /**
     * Returns Message Model with original image width & height data
     */
    // Previously showDummyImageMessage
    private TAPMessageModel fixOrientationAndShowImagePreviewBubble(TAPMessageModel imageMessage) {
        return fixOrientationAndShowImagePreviewBubble(imageMessage, 0L);
    }

    private TAPMessageModel fixOrientationAndShowImagePreviewBubble(TAPMessageModel imageMessage, Long scheduledTime) {
        if (null == imageMessage.getData()) {
            return imageMessage;
        }
        TAPDataImageModel imageData = new TAPDataImageModel(imageMessage.getData());
        Uri imageUri = Uri.parse(imageData.getFileUri());

        // Get image width and height
        String pathName = TAPFileUtils.getFilePath(TapTalk.appContext, imageUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        int orientation = TAPFileUtils.getImageOrientation(pathName);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            imageData.setWidth(options.outHeight);
            imageData.setHeight(options.outWidth);
        } else {
            imageData.setWidth(options.outWidth);
            imageData.setHeight(options.outHeight);
        }
        imageMessage.putData(TAPUtils.toHashMap(imageData));

        // Trigger listener to show image preview in activity
        if (scheduledTime < System.currentTimeMillis()) {
            triggerSendMessageListener(imageMessage);
        } else {
            triggerSendMessageListener(imageMessage, scheduledTime);
        }

        return imageMessage;
    }

    private TAPMessageModel fixOrientationAndShowImagePreviewBubble(TAPMessageModel imageMessage, Bitmap bitmap) {
        if (null == imageMessage.getData()) {
            return imageMessage;
        }
        TAPDataImageModel imageData = new TAPDataImageModel(imageMessage.getData());
        imageData.setWidth(bitmap.getWidth());
        imageData.setHeight(bitmap.getHeight());
        imageMessage.putData(TAPUtils.toHashMap(imageData));

        // Trigger listener to show image preview in activity
        triggerSendMessageListener(imageMessage);

        return imageMessage;
    }

    public void createVideoMessageModelAndAddToUploadQueue(Context context, TAPRoomModel room, Uri fileUri, String caption) {
        createVideoMessageModelAndAddToUploadQueue(context, room, fileUri, caption, 0L);
    }

    public void createVideoMessageModelAndAddToUploadQueue(Context context, TAPRoomModel room, Uri fileUri, String caption, Long scheduledTime) {
        TAPMessageModel messageModel = createVideoMessageModel(context, fileUri, caption, room, new TapCoreSendMessageListener() {
        });

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        if (scheduledTime < System.currentTimeMillis()) {
            triggerSendMessageListener(messageModel);
        } else {
            triggerSendMessageListener(messageModel, scheduledTime);
        }

        if (scheduledTime > System.currentTimeMillis()) {
            uploadingScheduledMessages.put(messageModel.getLocalID(), new TapScheduledMessageModel(scheduledTime, messageModel));
        }
        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, room.getRoomID(), messageModel);
    }

    @Deprecated
    public void sendImageOrVideoMessage(Context context, TAPRoomModel room, ArrayList<TAPMediaPreviewModel> medias) {
        sendImageOrVideoMessage(context, room, medias, 0L);
    }

    @Deprecated
    public void sendImageOrVideoMessage(Context context, TAPRoomModel room, ArrayList<TAPMediaPreviewModel> medias, Long scheduledTime) {
        new Thread(() -> {
            if (scheduledTime < System.currentTimeMillis()) {
                checkAndSendForwardedMessage(room);
            }
            for (TAPMediaPreviewModel media : medias) {
                if (media.getType() == TYPE_IMAGE) {
                    createImageMessageModelAndAddToUploadQueue(context, room, media.getUri(), media.getCaption(), scheduledTime);
                } else if (media.getType() == TYPE_VIDEO) {
                    createVideoMessageModelAndAddToUploadQueue(context, room, media.getUri(), media.getCaption(), scheduledTime);
                }
            }
        }).start();
    }

    public void sendImageMessage(Context context, TAPRoomModel room, Uri imageUri, String caption) {
        /*new Thread(() -> */createImageMessageModelAndAddToUploadQueue(context, room, imageUri, caption)/*).start()*/;
    }

    public void sendImageMessage(Context context, TAPRoomModel room, Bitmap bitmap, String caption) {
        /*new Thread(() -> */createImageMessageModelAndAddToUploadQueue(context, room, bitmap, caption)/*).start()*/;
    }

    public void sendImageMessage(Context context, TAPRoomModel roomModel, Uri imageUri, String caption, TapSendMessageInterface listener) {
        /*new Thread(() -> */createImageMessageModelAndAddToUploadQueue(context, roomModel, imageUri, caption, listener)/*).start()*/;
    }

    public void sendImageMessage(Context context, TAPRoomModel roomModel, Bitmap bitmap, String caption, TapSendMessageInterface listener) {
        /*new Thread(() -> */createImageMessageModelAndAddToUploadQueue(context, roomModel, bitmap, caption, listener)/*).start()*/;
    }

    public void sendVideoMessage(Context context, TAPRoomModel roomModel, Uri videoUri, String caption, TapSendMessageInterface listener) {
        /*new Thread(() -> */createVideoMessageModelAndAddToUploadQueue(context, roomModel, videoUri, caption, listener)/*).start()*/;
    }

    public void resendMessage(TAPMessageModel failedMessageModel) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        triggerListenerAndSendMessage(messageToResend, true);
    }

    public void resendMessage(TAPMessageModel failedMessageModel, TapSendMessageInterface listener) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        if (null != listener) {
            sendMessageListeners.put(messageToResend.getLocalID(), listener);
            listener.onStart(messageToResend);
        }
        triggerListenerAndSendMessage(messageToResend, true);
    }

    public void retryUpload(Context context, TAPMessageModel failedMessageModel) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        // Set Start Point for Progress
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(messageToResend.getLocalID(), 0, 0);
        addUploadingMessageToHashMap(messageToResend);
        TAPFileUploadManager.getInstance(instanceKey).addUploadQueue(context, messageToResend.getRoom().getRoomID(), messageToResend);
        triggerSendMessageListener(messageToResend);
    }

    /**
     * @return true if forwarded message is sent
     * false if forwarded message does not exist
     */
    public boolean checkAndSendForwardedMessage(TAPRoomModel roomModel) {
        String roomID = roomModel.getRoomID();
        if (null != getQuoteActions().get(roomID) && getQuoteActions().get(roomID) == FORWARD) {
            // Send forwarded message
            ArrayList<TAPMessageModel> forwardedMessages = getForwardedMessages().get(roomID);
            if (forwardedMessages != null) {
                for (TAPMessageModel message : forwardedMessages) {
                    TAPMessageModel messageModel = buildForwardedMessage(message, roomModel);
                    triggerListenerAndSendMessage(messageModel, true);
                }
                setForwardedMessages(roomID, null, 0);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean checkAndSendForwardedMessage(TAPRoomModel roomModel, TapSendMessageInterface listener) {
        String roomID = roomModel.getRoomID();
        if (null != getQuoteActions().get(roomID) && getQuoteActions().get(roomID) == FORWARD) {
            // Send forwarded message
            ArrayList<TAPMessageModel> forwardedMessages = getForwardedMessages().get(roomID);
            if (forwardedMessages != null) {
                for (TAPMessageModel message : forwardedMessages) {
                    TAPMessageModel messageModel = buildForwardedMessage(message, roomModel);
                    if (null != listener) {
                        sendMessageListeners.put(messageModel.getLocalID(), listener);
                        listener.onStart(messageModel);
                    }
                    triggerListenerAndSendMessage(messageModel, true);
                }
                setForwardedMessages(roomID, null, 0);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private TAPMessageModel buildForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room) {
        if (null == messageToForward) {
            return null;
        }
        // File message Uri no longer room ID restricted
//        if ((messageToForward.getType() == TYPE_VIDEO || messageToForward.getType() == TYPE_FILE || messageToForward.getType() == TYPE_VOICE) && null != messageToForward.getData()) {
//            // Copy file message Uri to destination room
//            String key = TAPUtils.getUriKeyFromMessage(messageToForward);
//            Uri uri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(messageToForward);
//            TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(room.getRoomID(), key, uri);
//        }
        return TAPMessageModel.BuilderForwardedMessage(
                messageToForward,
                room,
                System.currentTimeMillis(),
                getActiveUser(),
                TYPE_PERSONAL == room.getType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0");
    }

    private void triggerSendMessageListener(TAPMessageModel messageModel) {
        triggerSendMessageListener(messageModel, 0L);
    }

    private void triggerSendMessageListener(TAPMessageModel messageModel, Long scheduledTime) {
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty()) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                if (null != chatListener) {
                    TAPMessageModel tempNewMessage = messageModel.copyMessageModel();
                    if (scheduledTime < System.currentTimeMillis()) {
                        chatListener.onSendMessage(tempNewMessage);
                    } else {
                        chatListener.onCreateScheduledMessage(new TapScheduledMessageModel(scheduledTime, messageModel));
                    }
                }
            }
        }
    }

    // Previously sendMessage
    private void triggerListenerAndSendMessage(TAPMessageModel messageModel, boolean isNotifyChatListener) {
        // Call listener
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty() && isNotifyChatListener) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                if (null != chatListener) {
                    TAPMessageModel tempNewMessage = messageModel.copyMessageModel();
                    chatListener.onSendMessage(tempNewMessage);
                }
            }
        }
        runSendMessageSequence(messageModel, kSocketNewMessage);
    }

    /**
     * Message draft
     */

    public void saveMessageToDraft(String roomID, String message) {
        messageDrafts.put(roomID, message);
    }

    public String getMessageFromDraft(String roomID) {
        return messageDrafts.get(roomID);
    }

    public void removeDraft(String roomID) {
        messageDrafts.remove(roomID);
    }

    /**
     * User Info
     */
    private HashMap<String, HashMap<String, Object>> getUserInfoMap() {
        return userInfo == null ? userInfo = new HashMap<>() : userInfo;
    }

    public void saveUserInfo(String roomID, HashMap<String, Object> info) {
        getUserInfoMap().put(roomID, info);
    }

    public HashMap<String, Object> getUserInfo(String roomID) {
        return getUserInfoMap().get(roomID);
    }

    public void removeUserInfo(String roomID) {
        getUserInfoMap().remove(roomID);
    }

    /**
     * Quoted message
     */
    private Map<String, TAPMessageModel> getQuotedMessages() {
        return null == quotedMessages ? quotedMessages = new LinkedHashMap<>() : quotedMessages;
    }

    private Map<String, Integer> getQuoteActions() {
        return null == quotedActions ? quotedActions = new LinkedHashMap<>() : quotedActions;
    }

    public void setQuotedMessage(String roomID, @Nullable TAPMessageModel message, int quoteAction) {
        if (null == message) {
            // Delete quoted message and user info
            getQuotedMessages().remove(roomID);
            getQuoteActions().remove(roomID);
            getForwardedMessages().remove(roomID);
            removeUserInfo(roomID);
        } else {
            getQuotedMessages().put(roomID, message);
            getQuoteActions().put(roomID, quoteAction);
        }
    }

    public void setQuotedMessage(String roomID, String quoteTitle, @Nullable String quoteContent, @Nullable String quoteImageURL) {
        // FIXME: 29 January 2019 CURRENTLY USING DUMMY MESSAGE MODEL TO SAVE QUOTED MESSAGE
        if (null == quoteTitle) {
            return;
        }
        TAPUserModel dummyUserWithName = new TAPUserModel();
        dummyUserWithName.setFullname(quoteTitle);
        HashMap<String, Object> quoteData = new HashMap<>();
        quoteData.put(IMAGE_URL, null == quoteImageURL ? "" : quoteImageURL);
        // Dummy message model for quote
        TAPMessageModel dummyMessage = TAPMessageModel.Builder(
                null == quoteContent ? "" : quoteContent, new TAPRoomModel(),
                -1, 0L, dummyUserWithName, "", quoteData);
        getQuotedMessages().put(roomID, dummyMessage);
        getQuoteActions().put(roomID, REPLY);
    }

    public TAPMessageModel getQuotedMessage(String roomID) {
        return getQuotedMessages().get(roomID);
    }

    public Integer getQuoteAction(String roomID) {
        if (null == getQuoteActions().get(roomID)) {
            return -1;
        }
        return getQuoteActions().get(roomID);
    }

    /**
     * Forwarded message
     */
    private Map<String, ArrayList<TAPMessageModel>> getForwardedMessages() {
        return null == forwardedMessages ? forwardedMessages = new LinkedHashMap<>() : forwardedMessages;
    }

    public void setForwardedMessages(String roomID, @Nullable ArrayList<TAPMessageModel> messages, int quoteAction) {
        if (null == messages) {
            // Delete quoted message and user info
            getQuotedMessages().remove(roomID);
            getQuoteActions().remove(roomID);
            getForwardedMessages().remove(roomID);
            removeUserInfo(roomID);
        } else {
            getForwardedMessages().put(roomID, messages);
            getQuoteActions().put(roomID, quoteAction);
        }
    }

    public ArrayList<TAPMessageModel> getForwardedMessages(String roomID) {
        return getForwardedMessages().get(roomID);
    }

    /**
     * send pending messages from queue
     */
    public void checkAndSendPendingMessages() {
        if (!pendingMessages.isEmpty()) {
            TAPMessageModel message = pendingMessages.entrySet().iterator().next().getValue();
            String eventAction = pendingMessageActions.get(message.getLocalID());
            runSendMessageSequence(message, eventAction);
            pendingMessages.remove(message.getLocalID());
            pendingMessageActions.remove(message.getLocalID());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    checkAndSendPendingMessages();
                }
            }, 50);
        }
    }

    public void checkAndSendPendingScheduledMessages() {
        if (!pendingScheduledMessages.isEmpty()) {
            TapScheduledMessageModel scheduledMessage = pendingScheduledMessages.entrySet().iterator().next().getValue();
            if (scheduledMessage.getScheduledTime() > System.currentTimeMillis()) {
                runScheduledMessageSequence(scheduledMessage.getMessage(), scheduledMessage.getScheduledTime());
            } else {
                runSendMessageSequence(scheduledMessage.getMessage(), kSocketNewMessage);
            }
        }
    }

    /**
     * Edit message
     */

    public void editMessage(TAPMessageModel message, String updatedText, TapSendMessageInterface listener, boolean isTypeChangeEnabled) {
        if (message.getType() == TYPE_TEXT || message.getType() == TYPE_LINK) {
            if (updatedText.length() > CHARACTER_LIMIT) {
                if (null != listener) {
                    listener.onError(message, ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, CHARACTER_LIMIT));
                }
                return;
            }
            message.setBody(updatedText);
            List<String> urls = TAPUtils.getUrlsFromString(updatedText);
            if (isTypeChangeEnabled) {
                if (urls.isEmpty()) {
                    HashMap<String, Object> data = message.getData();
                    if (data != null) {
                        data.put(URL, null);
                        data.put(URLS, null);
                        data.put(TITLE, null);
                        data.put(DESCRIPTION, null);
                        data.put(IMAGE, null);
                        data.put(TYPE, null);
                        message.setData(data);
                    }
                    message.setType(TYPE_TEXT);
                } else {
                    HashMap<String, Object> data = message.getData() == null ? new HashMap<>() : message.getData();
                    data.put(URLS, urls);
                    message.setData(data);
                    message.setType(TYPE_LINK);
                }
            }
        }
        else if (message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) {
            if (updatedText.length() > TapTalk.getMaxCaptionLength(instanceKey)) {
                if (null != listener) {
                    listener.onError(message, ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, TapTalk.getMaxCaptionLength(instanceKey)));
                }
                return;
            }
            HashMap<String, Object> data = message.getData();
            if (data != null) {
                data.put(CAPTION, updatedText);
                message.setData(data);
                if (message.getType() == TYPE_IMAGE) {
                    message.setBody(TAPChatManager.getInstance(instanceKey).generateImageCaption(updatedText));
                }
                else {
                    message.setBody(TAPChatManager.getInstance(instanceKey).generateVideoCaption(updatedText));
                }
            }
        }
        else {
            if (null != listener) {
                listener.onError(message, ERROR_CODE_EDIT_INVALID_MESSAGE_TYPE, ERROR_MESSAGE_EDIT_INVALID_MESSAGE_TYPE);
            }
            return;
        }
        if (null != listener) {
            sendMessageListeners.put(message.getLocalID(), listener);
            listener.onStart(message);
        }
        // Edit message
        runSendMessageSequence(message, kSocketUpdateMessage);
    }

    public void editMessage(TAPMessageModel newMessage, TapSendMessageInterface listener) {
        if (null != listener) {
            sendMessageListeners.put(newMessage.getLocalID(), listener);
            listener.onStart(newMessage);
        }
        // Edit message
        runSendMessageSequence(newMessage, kSocketUpdateMessage);
    }

    /**
     * Send message to server
     */
    private void runSendMessageSequence(TAPMessageModel messageModel, String connectionEvent) {

        // TODO TEMPORARY FLAG FOR SEND MESSAGE API
        if (isSendMessageDisabled) {
            List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
            if (!chatListenersCopy.isEmpty()) {
                for (TAPChatListener chatListener : chatListenersCopy) {
                    if (null != chatListener) {
                        TAPMessageModel tempNewMessage = messageModel.copyMessageModel();
                        chatListener.onSendMessagePending(tempNewMessage);
                    }
                }
            }
            return;
        }

        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
            // Send message if socket is connected
            waitingResponses.put(messageModel.getLocalID(), messageModel);
            sendEmit(connectionEvent, messageModel);
        } else {
            // Add message to queue if socket is not connected
            pendingMessages.put(messageModel.getLocalID(), messageModel);
            pendingMessageActions.put(messageModel.getLocalID(), connectionEvent);
        }
    }

    // TODO TEMPORARY METHOD FOR SEND MESSAGE API
    public void putWaitingForResponseMessage(TAPMessageModel message) {
        waitingResponses.put(message.getLocalID(), message);
    }

    public void putPendingMessage(TAPMessageModel message) {
        pendingMessages.put(message.getLocalID(), message);
    }

    public void sendMessage(TAPMessageModel message) {
        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
            waitingResponses.put(message.getLocalID(), message);
            sendEmit(kSocketNewMessage, message);
        } else {
            pendingMessages.put(message.getLocalID(), message);
        }
    }

    /**
     * Send Start Typing to Server
     */
    public void sendStartTypingEmit(String roomID) {
        TAPTypingModel typingModel = new TAPTypingModel(roomID);
        sendEmit(kSocketStartTyping, typingModel);
    }

    /**
     * Send Stop Typing to Server
     */
    public void sendStopTypingEmit(String roomID) {
        TAPTypingModel typingModel = new TAPTypingModel(roomID);
        sendEmit(kSocketStopTyping, typingModel);
    }

    /**
     * Send emit to server (Message)
     */
    private void sendEmit(String eventName, TAPMessageModel messageModel) {
        try {
            TAPEmitModel<HashMap<String, Object>> encryptedMessageMap;
            encryptedMessageMap = new TAPEmitModel<>(eventName, TAPEncryptorManager.getInstance().encryptMessage(messageModel));
            TAPConnectionManager.getInstance(instanceKey).send(TAPUtils.toJsonString(encryptedMessageMap));
            Log.d(TAG, "sendEmit: " + TAPUtils.toJsonString(messageModel));
            if (sendMessageListeners.containsKey(messageModel.getLocalID())) {
                sendMessageListeners.get(messageModel.getLocalID()).onSuccess(messageModel);
                sendMessageListeners.remove(messageModel.getLocalID());
            }
        } catch (Exception e) {
            if (sendMessageListeners.containsKey(messageModel.getLocalID())) {
                sendMessageListeners.get(messageModel.getLocalID()).onError(messageModel, ERROR_CODE_OTHERS, e.getMessage());
                sendMessageListeners.remove(messageModel.getLocalID());
            }
        }
    }

    /**
     * Send emit to server (Typing)
     */
    private void sendEmit(String eventName, TAPTypingModel typingModel) {
        TAPEmitModel<TAPTypingModel> TAPEmitModel;
        TAPEmitModel = new TAPEmitModel<>(eventName, typingModel);
        TAPConnectionManager.getInstance(instanceKey).send(TAPUtils.toJsonString(TAPEmitModel));
    }

    /**
     * update pending status when app enters background and close socket
     */
    public void updateMessageWhenEnterBackground() {
        //saveWaitingMessageToList();
        setPendingRetryAttempt(0);
        isCheckPendingArraySequenceActive = false;
        if (null != scheduler && !scheduler.isShutdown())
            scheduler.shutdown();
        saveUnsentMessage();
        checkPendingBackgroundTask();
    }

    private void addUploadingMessageToHashMap(TAPMessageModel messageModel) {
        waitingUploadProgress.put(messageModel.getLocalID(), messageModel);
    }

    public void removeUploadingMessageFromHashMap(String localID) {
        waitingUploadProgress.remove(localID);
    }

    public boolean checkMessageIsUploading(String localID) {
        if (waitingUploadProgress.containsKey(localID) && null != waitingUploadProgress.get(localID)) {
            return true;
        }

        return false;
    }

    private void insertToList(Map<String, TAPMessageModel> hashMap) {
        try {
            List<TAPMessageEntity> messagesToInsert = new ArrayList<>();
            for (Map.Entry<String, TAPMessageModel> message : hashMap.entrySet()) {
                messagesToInsert.add(TAPMessageEntity.fromMessageModel(message.getValue()));
            }
            saveMessages.addAll(messagesToInsert);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    private void checkPendingBackgroundTask() {
        // TODO: 05/09/18 nnti cek file manager upload queue juga
        isPendingMessageExist = false;
        isFileUploadExist = false;

        if (0 < pendingMessages.size())
            isPendingMessageExist = true;
        if (0 < waitingUploadProgress.size())
            isFileUploadExist = true;

        if (isCheckPendingArraySequenceActive)
            return;

        // TODO: 13/09/18 check file progress upload

        if ((isPendingMessageExist || isFileUploadExist) && maxRetryAttempt > pendingRetryAttempt) {
            isCheckPendingArraySequenceActive = true;
            pendingRetryAttempt++;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    checkPendingBackgroundTask();
                }
            }, pendingRetryInterval);
            isCheckPendingArraySequenceActive = false;
        } else saveIncomingMessageAndDisconnect();
    }

    public void saveIncomingMessageAndDisconnect() {
        if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) != TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON) {
            TAPConnectionManager.getInstance(instanceKey).close();
        }
        saveUnsentMessage();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        isFinishChatFlow = true;
    }

    public void disconnectAfterRefreshTokenExpired() {
        TAPConnectionManager.getInstance(instanceKey).close();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        isFinishChatFlow = true;
    }

    public void deleteActiveRoom() {
        activeRoom = null;
    }

    private void receiveMessageFromSocket(HashMap<String, Object> newMessageMap, String eventName) {
        TAPMessageModel newMessage = TAPEncryptorManager.getInstance().decryptMessage(newMessageMap);
        if (newMessage == null) {
            return;
        }

        // TODO: 28 Jan 2020 TEMPORARY SOCKET MESSAGE LOG
        if (TapTalk.isLoggingEnabled) {
            Log.d(TAG, "receiveMessageFromSocket: " + TAPUtils.toJsonString(newMessage));
        }

        // Remove from waiting response hashmap
        if (kSocketNewMessage.equals(eventName)) {
            waitingResponses.remove(newMessage.getLocalID());
        }

        if ((LEAVE_ROOM.equals(newMessage.getAction()) || DELETE_ROOM.equals(newMessage.getAction()))
                && getActiveUser().getUserID().equals(newMessage.getUser().getUserID())) {
            // Remove all room messages if user leaves/deletes a chat room
            new Thread(() -> {
                for (Map.Entry<String, TAPMessageModel> entry : incomingMessages.entrySet()) {
                    if (entry.getValue().getRoom().getRoomID().equals(newMessage.getRoom().getRoomID())) {
                        incomingMessages.remove(entry.getKey());
                    }
                }
                for (TAPMessageEntity message : saveMessages) {
                    if (message.getRoomID().equals(newMessage.getRoom().getRoomID())) {
                        saveMessages.remove(message);
                    }
                }
            }).start();
        } else {
            // Insert decrypted message to database
//            incomingMessages.put(newMessage.getLocalID(), newMessage);
            TAPDataManager.getInstance(instanceKey).insertToDatabase(TAPMessageEntity.fromMessageModel(newMessage));
        }

        TAPUtils.handleReceivedSystemMessage(instanceKey, newMessage);

        // Query Unread Message
        //TAPNotificationManager.getInstance(instanceKey).updateUnreadCount();

        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);

        // Receive message in active room
        if (!chatListenersCopy.isEmpty() &&
                ((null != activeRoom && newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))
                        || (newMessage.getRoom().getRoomID().equals(openRoom)))
        ) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                if (null != chatListener) {
                    TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                    if (kSocketNewMessage.equals(eventName))
                        chatListener.onReceiveMessageInActiveRoom(tempNewMessage);
                    else if (kSocketUpdateMessage.equals(eventName))
                        chatListener.onUpdateMessageInActiveRoom(tempNewMessage);
                    else if (kSocketDeleteMessage.equals(eventName))
                        chatListener.onDeleteMessageInActiveRoom(tempNewMessage);
                }
            }
        }
        // Receive message outside active room (not in room list)
        else if (!TAPNotificationManager.getInstance(instanceKey).isRoomListAppear() &&
                (null == activeRoom || !newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))
        ) {
            if (kSocketNewMessage.equals(eventName) &&
                    !newMessage.getUser().getUserID().equals(activeUser.getUserID()) &&
                    null != newMessage.getIsHidden() &&
                    !newMessage.getIsHidden() &&
                    null != newMessage.getIsDeleted() &&
                    !newMessage.getIsDeleted() &&
                    !TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(newMessage.getRoom().getRoomID())
            ) {
                // TODO: CHECK IF NEED TO SHOW IN-APP NOTIFICATION
                // Show notification for new messages from other users
                TAPNotificationManager.getInstance(instanceKey).createAndShowInAppNotification(TapTalk.appContext, newMessage);
            }
            if (!chatListenersCopy.isEmpty()) {
                for (TAPChatListener chatListener : chatListenersCopy) {
                    if (null != chatListener) {
                        TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                        if (kSocketNewMessage.equals(eventName))
                            chatListener.onReceiveMessageInOtherRoom(tempNewMessage);
                        else if (kSocketUpdateMessage.equals(eventName))
                            chatListener.onUpdateMessageInOtherRoom(tempNewMessage);
                        else if (kSocketDeleteMessage.equals(eventName))
                            chatListener.onDeleteMessageInOtherRoom(tempNewMessage);
                    }
                }
            }
        }
        // Receive message outside active room (in room list)
        else if (null == activeRoom || !newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID())) {
            if (kSocketNewMessage.equals(eventName) &&
                    !newMessage.getUser().getUserID().equals(activeUser.getUserID()) &&
                    null != newMessage.getIsHidden() &&
                    !newMessage.getIsHidden() &&
                    null != newMessage.getIsDeleted()
                    && !newMessage.getIsDeleted() &&
                    !TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(newMessage.getRoom().getRoomID())
            ) {
                // TODO: CHECK IF NEED TO SHOW IN-APP NOTIFICATION
                // Show notification for new messages from other users
                TAPNotificationManager.getInstance(instanceKey).createAndShowInAppNotification(TapTalk.appContext, newMessage);
            }
            if (!chatListenersCopy.isEmpty()) {
                for (TAPChatListener chatListener : chatListenersCopy) {
                    if (null != chatListener) {
                        TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                        if (kSocketNewMessage.equals(eventName))
                            chatListener.onReceiveMessageInOtherRoom(tempNewMessage);
                        else if (kSocketUpdateMessage.equals(eventName))
                            chatListener.onUpdateMessageInOtherRoom(tempNewMessage);
                        else if (kSocketDeleteMessage.equals(eventName))
                            chatListener.onDeleteMessageInOtherRoom(tempNewMessage);
                    }
                }
            }
        }

        // Add to list delivered message
        if (kSocketNewMessage.equals(eventName) && !newMessage.getUser().getUserID().equals(activeUser.getUserID())
                && null != newMessage.getIsSending() && !newMessage.getIsSending()
                && null != newMessage.getIsDelivered() && !newMessage.getIsDelivered()
                && null != newMessage.getIsRead() && !newMessage.getIsRead()) {
            TAPMessageStatusManager.getInstance(instanceKey).addDeliveredMessageQueue(newMessage);
        }

        // Check the message is from our direct reply or not (in background)
        if (!isReplyMessageLocalIDsEmpty()) {
            removeReplyMessageLocalID(newMessage.getLocalID());
            if (isReplyMessageLocalIDsEmpty()) {
                checkPendingBackgroundTask();
                Toast.makeText(TapTalk.appContext, "Reply Success", Toast.LENGTH_SHORT).show();
            }
        }

        // Save user data to contact manager
        if (newMessage.getUser() != activeUser ||
                UPDATE_USER.equals(newMessage.getAction())) {
            TAPContactManager.getInstance(instanceKey).updateUserData(newMessage.getUser());
        }
    }

    public void deleteMessageFromIncomingMessages(String localID) {
        incomingMessages.remove(localID);
    }

    public void saveNewMessageToList() {
        if (0 == incomingMessages.size())
            return;

        insertToList(new LinkedHashMap<>(incomingMessages));
        saveMessageToDatabase();
        incomingMessages.clear();
    }

    public void savePendingMessageToList() {
        if (0 < pendingMessages.size()) {
            insertToList(pendingMessages);
        }
    }

    public void saveWaitingMessageToList() {
        if (0 == waitingResponses.size())
            return;

        insertToList(new LinkedHashMap<>(waitingResponses));
        waitingResponses.clear();
    }

    public void saveUploadingMessageToList() {
        if (0 == waitingUploadProgress.size())
            return;

        insertToList(new LinkedHashMap<>(waitingUploadProgress));
    }

    public void triggerSaveNewMessage() {
        if (null == scheduler || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        } else {
            scheduler.shutdown();
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        scheduler.scheduleAtFixedRate(() -> {
            saveNewMessageToList();
            TAPMessageStatusManager.getInstance(instanceKey).triggerCallMessageStatusApi();
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void saveUnsentMessage() {
        saveNewMessageToList();
        savePendingMessageToList();
        saveWaitingMessageToList();
        saveUploadingMessageToList();
        saveMessageToDatabase();
    }

    public void putUnsentMessageToList() {
        saveNewMessageToList();
        savePendingMessageToList();
        saveWaitingMessageToList();
        saveUploadingMessageToList();
    }

    public void saveMessageToDatabase() {
        if (0 == saveMessages.size()) return;

        TAPDataManager.getInstance(instanceKey).insertToDatabase(new ArrayList<>(saveMessages), true);
    }

    public List<TAPMessageEntity> getSaveMessages() {
        return saveMessages;
    }

    public List<TapScheduledMessageModel> getPendingScheduledMessages() {
        List<TapScheduledMessageModel> scheduledMessages = new ArrayList<>();
        try {
            for (Map.Entry<String, TapScheduledMessageModel> message : pendingScheduledMessages.entrySet()) {
                scheduledMessages.add(message.getValue());
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        return scheduledMessages;
    }

    public void clearSaveMessages() {
        saveMessages.clear();
    }

    private void setPendingRetryAttempt(int counter) {
        pendingRetryAttempt = counter;
    }

    boolean isFinishChatFlow() {
        return isFinishChatFlow;
    }

    public void setFinishChatFlow(boolean finishChatFlow) {
        isFinishChatFlow = finishChatFlow;
    }

    public boolean isNeedToCalledUpdateRoomStatusAPI() {
        return isNeedToCalledUpdateRoomStatusAPI;
    }

    public void setNeedToCallUpdateRoomStatusAPI(boolean needToCalledUpdateRoomStatusAPI) {
        isNeedToCalledUpdateRoomStatusAPI = needToCalledUpdateRoomStatusAPI;
    }

    public boolean isSendMessageDisabled() {
        return isSendMessageDisabled;
    }

    public void setSendMessageDisabled(boolean sendMessageDisabled) {
        isSendMessageDisabled = sendMessageDisabled;
    }

    private List<String> getReplyMessageLocalIDs() {
        return null == replyMessageLocalIDs ? replyMessageLocalIDs = new ArrayList<>() : replyMessageLocalIDs;
    }

    private void addReplyMessageLocalID(String localID) {
        //masukin local ID ke dalem list kalau misalnya appsnya lagi ga di foreground aja,
        //karena kalau di foreground kita ga boleh matiin socketnya cman krna reply
        if (!TapTalk.isForeground)
            getReplyMessageLocalIDs().add(localID);
    }

    private void removeReplyMessageLocalID(String localID) {
        getReplyMessageLocalIDs().remove(localID);
    }

    private boolean isReplyMessageLocalIDsEmpty() {
        return getReplyMessageLocalIDs().isEmpty();
    }

    public void updateUnreadCountInRoomList(String roomID) {
        new Thread(() -> {
            List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
            for (TAPChatListener chatListener : chatListenersCopy) {
                if (null != chatListener) {
                    chatListener.onReadMessage(roomID);
                }
            }
        }).start();
    }

    public void resetChatManager() {
        clearSaveMessages();
        pendingMessages.clear();
        pendingScheduledMessages.clear();
        pendingMessageActions.clear();
        waitingUploadProgress.clear();
        waitingResponses.clear();
        incomingMessages.clear();
        getQuotedMessages().clear();
        getReplyMessageLocalIDs().clear();
        getQuoteActions().clear();
        setActiveUser(null);
    }

    public String formattingSystemMessage(TAPMessageModel message) {
        String systemMessageBody;

        // TODO: 26 Feb 2020 REPLACE 'YOU' ACCORDING TO LANGUAGE
        if (null == message.getTarget()) {
            systemMessageBody = message.getBody()
                    .replace("{", "")
                    .replace("}", "")
                    .replaceFirst("sender",
                            message.getUser().getUserID().equals(getActiveUser().getUserID()) ?
                                    "You" : message.getUser().getFullname());
        } else {
            systemMessageBody = message.getBody()
                    .replace("{", "")
                    .replace("}", "")
                    .replaceFirst("sender", message.getUser().getUserID().equals(getActiveUser().getUserID()) ?
                            "You" : message.getUser().getFullname())
                    .replaceFirst("target", message.getTarget().getTargetID() != null ?
                            message.getTarget().getTargetID().equals(getActiveUser().getUserID()) ?
                                    "you" : message.getTarget().getTargetName() == null ? "" : message.getTarget().getTargetName() : "");
        }

        return systemMessageBody;
    }

    /**
     * =============================================================================================
     * TAP UI
     * ============================================================================================
     */

    public void triggerSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        TapUI.getInstance(instanceKey).triggerSearchChatBarTapped(activity, mainRoomListFragment);
    }

    public void triggerCloseRoomListButtonTapped(Activity activity) {
        TapUI.getInstance(instanceKey).triggerCloseRoomListTapped(activity);
    }

    public void triggerTapTalkAccountButtonTapped(Activity activity) {
        TapUI.getInstance(instanceKey).triggerTapTalkAccountButtonTapped(activity);
    }

    public void triggerNewChatButtonTapped(Activity activity) {
        TapUI.getInstance(instanceKey).triggerNewChatButtonTapped(activity);
    }

    public void triggerChatRoomOpened(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {
        TapUI.getInstance(instanceKey).triggerChatRoomOpened(activity, room, otherUser);
    }

    public void triggerChatRoomClosed(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {
        TapUI.getInstance(instanceKey).triggerChatRoomClosed(activity, room, otherUser);
    }

    public void triggerActiveUserSendMessage(Activity activity, TAPMessageModel messageModel, TAPRoomModel room) {
        TapUI.getInstance(instanceKey).triggerActiveUserSendMessage(activity, messageModel, room);
    }

    public void triggerChatRoomProfileButtonTapped(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
        TapUI.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(activity, room, user);
    }

    public void triggerUserMentionTapped(Activity activity, TAPMessageModel message, TAPUserModel user, boolean isRoomParticipant) {
        TapUI.getInstance(instanceKey).triggerUserMentionTapped(activity, message, user, isRoomParticipant);
    }

    public void triggerMessageQuoteTapped(Activity activity, TAPMessageModel messageModel) {
        TapUI.getInstance(instanceKey).triggerMessageQuoteTapped(activity, messageModel);
    }

    public void triggerChatProfileReportUserButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel user) {
        TapUI.getInstance(instanceKey).triggerChatProfileReportUserButtonTapped(activity, room, user);
    }

    public void triggerChatProfileReportGroupButtonTapped(Activity activity, TAPRoomModel room) {
        TapUI.getInstance(instanceKey).triggerChatProfileReportGroupButtonTapped(activity, room);
    }

    public void triggerChatProfileGroupsInCommonButtonTapped(Activity activity, TAPRoomModel room) {
        TapUI.getInstance(instanceKey).triggerChatProfileGroupsInCommonButtonTapped(activity, room);
    }

    public void triggerChatProfileStarredMessageButtonTapped(Activity activity, TAPRoomModel room) {
        TapUI.getInstance(instanceKey).triggerChatProfileStarredMessageButtonTapped(activity, room);
    }

    public List<TAPCustomKeyboardItemModel> getCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, TAPUserModel recipientUser) {
        return TapUI.getInstance(instanceKey).getCustomKeyboardItems(room, activeUser, recipientUser);
    }

    public void triggerCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPRoomModel room, TAPUserModel activeUser, TAPUserModel otherUser) {
        TapUI.getInstance(instanceKey).triggerCustomKeyboardItemTapped(activity, customKeyboardItemModel, room, activeUser, otherUser);
    }

    public TapBaseChatRoomCustomNavigationBarFragment getChatRoomCustomNavigationBar(Activity activity, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
        return TapUI.getInstance(instanceKey).getChatRoomCustomNavigationBar(activity, room, activeUser, recipientUser);
    }

    public void triggerProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        TapUI.getInstance(instanceKey).triggerProductListBubbleLeftOrSingleButtonTapped(activity, product, room, recipient, isSingleOption);
    }

    public void triggerProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        TapUI.getInstance(instanceKey).triggerProductListBubbleRightButtonTapped(activity, product, room, recipient, isSingleOption);
    }

    public void triggerSavedMessageBubbleArrowTapped(TAPMessageModel message) {
        TapUI.getInstance(instanceKey).triggerSavedMessageBubbleArrowTapped(message);
    }

    public void triggerPinnedMessageTapped(TAPMessageModel message) {
        TapUI.getInstance(instanceKey).triggerPinnedMessageTapped(message);
    }

    public void triggerReportMessageButtonTapped(Activity activity, TAPMessageModel message) {
        TapUI.getInstance(instanceKey).triggerReportMessageButtonTapped(activity, message);
    }

    public String getRoomListTitleText(TAPRoomListModel roomList, int position, Context context) {
        return TapUI.getInstance(instanceKey).getRoomListTitleText(roomList, position, context);
    }

    public String getDefaultRoomListTitleText(TAPRoomListModel roomList, int position, Context context) {
        TAPRoomModel room = roomList.getLastMessage().getRoom();
        TAPUserModel user = null;
        TAPRoomModel group = null;

        if (room.getType() == TYPE_PERSONAL && getActiveUser() != null) {
            user = TAPContactManager.getInstance(instanceKey).getUserData(getOtherUserIdFromRoom(room.getRoomID()));
        } else if (room.getType() == TYPE_GROUP || room.getType() == TYPE_TRANSACTION) {
            group = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(room.getRoomID());
        }

        // Set room name
        if (null != user && (null == user.getDeleted() || user.getDeleted() <= 0L) &&
                null != user.getFullname() && !user.getFullname().isEmpty()) {
            return user.getFullname();
        } else if (null != group && !group.isDeleted() && null != group.getName() &&
                !group.getName().isEmpty()) {
            return group.getName();
        } else {
            return room.getName();
        }
    }

    public String getRoomListContentText(TAPRoomListModel roomList, int position, Context context) {
        return TapUI.getInstance(instanceKey).getRoomListContentText(roomList, position, context);
    }

    public String getDefaultRoomListContentText(TAPRoomListModel roomList, int position, Context context) {
        if (TYPE_SYSTEM_MESSAGE == roomList.getLastMessage().getType()) {
            // Show system message
            return TAPChatManager.getInstance(instanceKey).formattingSystemMessage(roomList.getLastMessage());
        } else if (roomList.getLastMessage().getRoom().getType() != TYPE_PERSONAL) {
            // Show group/channel room with last message
            String sender;
            if (getActiveUser() != null && getActiveUser().getUserID().equals(roomList.getLastMessage().getUser().getUserID())) {
                sender = context.getString(R.string.tap_you);
            }
            else {
                sender = TAPUtils.getFirstWordOfString(roomList.getLastMessage().getUser().getFullname());
            }
            return String.format("%s: %s", sender, roomList.getLastMessage().getBody());
        } else {
            // Show personal room with last message
            return roomList.getLastMessage().getBody();
        }
    }

    public List<TapLongPressMenuItem> getMessageLongPressMenuItems(Context context, TAPMessageModel message) {
        return TapUI.getInstance(instanceKey).getMessageLongPressMenuItems(context, message);
    }

    public List<TapLongPressMenuItem> getScheduledMessageLongPressMenuItems(Context context, TAPMessageModel message) {
        return TapUI.getInstance(instanceKey).getScheduledMessageLongPressMenuItems(context, message);
    }

    public List<TapLongPressMenuItem> getLinkLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String url) {
        return TapUI.getInstance(instanceKey).getLinkLongPressMenuItems(context, message, url);
    }

    public List<TapLongPressMenuItem> getEmailLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String emailAddress) {
        return TapUI.getInstance(instanceKey).getEmailLongPressMenuItems(context, message, emailAddress);
    }

    public List<TapLongPressMenuItem> getPhoneLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String phoneNumber) {
        return TapUI.getInstance(instanceKey).getPhoneLongPressMenuItems(context, message, phoneNumber);
    }

    public List<TapLongPressMenuItem> getMentionLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String username) {
        return TapUI.getInstance(instanceKey).getMentionLongPressMenuItems(context, message, username);
    }

    public void triggerLongPressMenuItemSelected(Activity activity, TapLongPressMenuItem longPressMenuItem, @Nullable TAPMessageModel message) {
        TapUI.getInstance(instanceKey).triggerLongPressMenuItemSelected(activity, longPressMenuItem, message);
    }

    /**
     * =============================================================================================
     * TAP CORE
     * ============================================================================================
     */

    public void triggerRequestMessageFileUpload(TAPMessageModel messageModel, Uri fileUri) {
        TapCoreMessageManager.getInstance(instanceKey).triggerRequestMessageFileUpload(messageModel, fileUri);
    }

    public void triggerUpdatedChatRoomDataReceived(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {
        for (TAPChatListener listener : chatListeners) {
            listener.onReceiveUpdatedChatRoomData(room, recipientUser);
        }
    }
}
