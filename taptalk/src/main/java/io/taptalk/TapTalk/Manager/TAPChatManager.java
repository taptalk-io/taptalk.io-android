package io.taptalk.TapTalk.Manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
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

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPSocketMessageListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPDataFileModel;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
import io.taptalk.TapTalk.Model.TAPDataLocationModel;
import io.taptalk.TapTalk.Model.TAPEmitModel;
import io.taptalk.TapTalk.Model.TAPForwardFromModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPQuoteModel;
import io.taptalk.TapTalk.Model.TAPReplyToModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TAPUserRoleModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_CAPTION_EXCEEDS_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_EXCEEDED_MAX_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_EXCEEDED_MAX_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kEventOpenRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketAuthentication;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketCloseRoom;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketDeleteMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketNewMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketOpenMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketStartTyping;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketStopTyping;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUpdateMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ConnectionEvent.kSocketUserOnlineStatus;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_CAPTION_LENGTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.DISCONNECTED;

public class TAPChatManager {

    private final String TAG = TAPChatManager.class.getSimpleName();
    private static TAPChatManager instance;
    private Map<String, TAPMessageModel> pendingMessages, waitingUploadProgress, waitingResponses, incomingMessages, quotedMessages;
    private Map<String, Integer> quotedActions;
    private Map<String, String> messageDrafts;
    private HashMap<String, HashMap<String, Object>> userInfo;
    private HashMap<String, TapSendMessageInterface> sendMessageListeners;
    private List<TAPChatListener> chatListeners;
    private List<TAPMessageEntity> saveMessages; //message to be saved
    private List<String> replyMessageLocalIDs;
    private TAPRoomModel activeRoom;
    private TAPUserModel activeUser;
    private ScheduledExecutorService scheduler;
    private String openRoom;
    private boolean isCheckPendingArraySequenceActive = false;
    private boolean isPendingMessageExist;
    private boolean isFileUploadExist;
    private boolean isFinishChatFlow;
    private boolean isNeedToCalledUpdateRoomStatusAPI = true;
    private int pendingRetryAttempt = 0;
    private int maxRetryAttempt = 10;
    private int pendingRetryInterval = 60 * 1000;
    private final int maxImageSize = 2000;
    private final Integer CHARACTER_LIMIT = 1000;

    private TapTalkSocketInterface socketListener = new TapTalkSocketInterface() {
        @Override
        public void onSocketConnected() {

            checkAndSendPendingMessages();
            isFinishChatFlow = false;
        }

        @Override
        public void onSocketDisconnected() {
            if (TapTalk.isForeground &&
                    TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext) &&
                    DISCONNECTED == TAPConnectionManager.getInstance().getConnectionStatus())
                TAPConnectionManager.getInstance().reconnect();
        }

        @Override
        public void onSocketConnecting() {

        }

        @Override
        public void onSocketError() {
            TAPConnectionManager.getInstance().reconnect();
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
                    receiveMessageFromSocket(TAPUtils.getInstance().fromJSON(
                            new TypeReference<TAPEmitModel<HashMap<String, Object>>>() {
                            },
                            emitData).getData(),
                            eventName);
                    break;
                case kSocketOpenMessage:
                    break;
                case kSocketStartTyping:
                    TAPEmitModel<TAPTypingModel> startTypingEmit = TAPUtils.getInstance()
                            .fromJSON(new TypeReference<TAPEmitModel<TAPTypingModel>>() {
                            }, emitData);
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onReceiveStartTyping(startTypingEmit.getData());
                    }
                    break;
                case kSocketStopTyping:
                    TAPEmitModel<TAPTypingModel> stopTypingEmit = TAPUtils.getInstance()
                            .fromJSON(new TypeReference<TAPEmitModel<TAPTypingModel>>() {
                            }, emitData);
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onReceiveStopTyping(stopTypingEmit.getData());
                    }
                    break;
                case kSocketAuthentication:
                    break;
                case kSocketUserOnlineStatus:
                    TAPEmitModel<TAPOnlineStatusModel> onlineStatusEmit = TAPUtils.getInstance()
                            .fromJSON(new TypeReference<TAPEmitModel<TAPOnlineStatusModel>>() {
                            }, emitData);
                    TAPOnlineStatusModel onlineStatus = onlineStatusEmit.getData();
                    for (TAPChatListener listener : chatListenersCopy) {
                        listener.onUserOnlineStatusUpdate(onlineStatus);
                    }
                    break;
            }
        }
    };

    public static TAPChatManager getInstance() {
        return instance == null ? (instance = new TAPChatManager()) : instance;
    }

    public TAPChatManager() {
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
        TAPConnectionManager.getInstance().setSocketMessageListener(socketMessageListener);
        setActiveUser(TAPDataManager.getInstance().getActiveUser());
        chatListeners = new ArrayList<>();
        sendMessageListeners = new HashMap<>();
        saveMessages = new ArrayList<>();
        pendingMessages = new LinkedHashMap<>();
        waitingResponses = new LinkedHashMap<>();
        incomingMessages = new LinkedHashMap<>();
        waitingUploadProgress = new LinkedHashMap<>();
        messageDrafts = new HashMap<>();
    }

    public void addChatListener(TAPChatListener chatListener) {
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
        //ini kenapa di taro disini karena setiap ada active room otomatis open room nya juga akan diubah
        //kecuali kalau misalnya active roomnya itu diubah jadi null
        if (null != room) setOpenRoom(room.getRoomID());
    }

    //Open room itu buat nyimpen roomID apa yang dibuka
    // Apa bedanya sama activeRoom? bedanya adalah klo active room itu di pause lgsg jd null
    // kalau open room slama dy belom ke destroy activitynya ga akan jadi null
    //kegunaannya buat di pake pas reply yang ada di background
    //ngecekin karena dy trigger onNewMessageFromOtherRoom, jadi kalau roomnya lagi ga di buka jangan di push messagenya ke UI
    public String getOpenRoom() {
        return openRoom;
    }

    public void setOpenRoom(String openRoom) {
        this.openRoom = openRoom;
    }

    public TAPUserModel getActiveUser() {
        return activeUser == null ? TAPDataManager.getInstance().getActiveUser() : activeUser;
    }

    public void setActiveUser(TAPUserModel user) {
        this.activeUser = user;
    }

    public void saveActiveUser(TAPUserModel user) {
        this.activeUser = user;
        TAPDataManager.getInstance().saveActiveUser(user);
    }

    public Map<String, TAPMessageModel> getMessageQueueInActiveRoom() {
        Map<String, TAPMessageModel> roomQueue = new LinkedHashMap<>();
        for (Map.Entry<String, TAPMessageModel> entry : pendingMessages.entrySet()) {
            if (entry.getValue().getRoom().getRoomID().equals(activeRoom)) {
                roomQueue.put(entry.getKey(), entry.getValue());
            }
        }
        return roomQueue;
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
        String[] splitRoomID = roomID.split("-");
        return !splitRoomID[0].equals(getActiveUser().getUserID()) ? splitRoomID[0] : splitRoomID[1];
    }

    /**
     * convert TAPMessageEntity to TAPMessageModel
     */
    public TAPMessageModel convertToModel(TAPMessageEntity entity) {
        return new TAPMessageModel(
                entity.getMessageID(),
                entity.getLocalID(),
                entity.getFilterID(),
                entity.getBody(),
                new TAPRoomModel(entity.getRoomID(), entity.getRoomName(), entity.getRoomType(),
                        // TODO: 18 October 2018 REMOVE CHECK
                        /* TEMPORARY CHECK FOR NULL IMAGE */null != entity.getRoomImage() ?
                        TAPUtils.getInstance().fromJSON(new TypeReference<TAPImageURL>() {
                        }, entity.getRoomImage())
                        /* TEMPORARY CHECK FOR NULL IMAGE */ : null
                        , entity.getRoomColor()),
                entity.getType(),
                entity.getCreated(),
                new TAPUserModel(entity.getUserID(), entity.getXcUserID(), entity.getUserFullName(),
                        TAPUtils.getInstance().fromJSON(new TypeReference<TAPImageURL>() {
                        }, entity.getUserImage()),
                        entity.getUsername(), entity.getUserEmail(), entity.getUserPhone(),
                        TAPUtils.getInstance().fromJSON(new TypeReference<TAPUserRoleModel>() {
                        }, entity.getUserRole()),
                        entity.getLastLogin(), entity.getLastActivity(), entity.getRequireChangePassword(), entity.getUserCreated(),
                        entity.getUserUpdated(), entity.getUserDeleted()),
                entity.getRecipientID(),
                null == entity.getData() ? null : TAPUtils.getInstance().toHashMap(entity.getData()),
                null == entity.getQuote() ? null : TAPUtils.getInstance().fromJSON(new TypeReference<TAPQuoteModel>() {
                }, entity.getQuote()),
                null == entity.getReplyTo() ? null : TAPUtils.getInstance().fromJSON(new TypeReference<TAPReplyToModel>() {
                }, entity.getReplyTo()),
                null == entity.getForwardFrom() ? null : TAPUtils.getInstance().fromJSON(new TypeReference<TAPForwardFromModel>() {
                }, entity.getForwardFrom()),
                entity.getIsDeleted(),
                entity.getSending(),
                entity.getFailedSend(),
                entity.getDelivered(),
                entity.getIsRead(),
                entity.getHidden(),
                entity.getUpdated(),
                entity.getUserDeleted(),
                entity.getAction(), entity.getTarget());
    }

    /**
     * convert TAPMessageModel to TAPMessageEntity
     */
    public TAPMessageEntity convertToEntity(TAPMessageModel model) {
        return new TAPMessageEntity(
                model.getMessageID(), model.getLocalID(), model.getFilterID(), model.getBody(),
                model.getRecipientID(), model.getType(), model.getCreated(),
                null == model.getData() ? null : TAPUtils.getInstance().toJsonString(model.getData()),
                null == model.getQuote() ? null : TAPUtils.getInstance().toJsonString(model.getQuote()),
                null == model.getReplyTo() ? null : TAPUtils.getInstance().toJsonString(model.getReplyTo()),
                null == model.getForwardFrom() ? null : TAPUtils.getInstance().toJsonString(model.getForwardFrom()),
                model.getUpdated(), model.getDeleted(),
                model.getIsRead(), model.getDelivered(), model.getHidden(), model.getIsDeleted(),
                model.getSending(), model.getFailedSend(), model.getRoom().getRoomID(),
                model.getRoom().getRoomName(), model.getRoom().getRoomColor(),
                model.getRoom().getRoomType(),
                TAPUtils.getInstance().toJsonString(model.getRoom().getRoomImage()),
                model.getUser().getUserID(), model.getUser().getXcUserID(),
                model.getUser().getName(), model.getUser().getUsername(),
                TAPUtils.getInstance().toJsonString(model.getUser().getAvatarURL()),
                model.getUser().getEmail(), model.getUser().getPhoneNumber(),
                TAPUtils.getInstance().toJsonString(model.getUser().getUserRole()),
                model.getUser().getLastLogin(), model.getUser().getLastActivity(),
                model.getUser().getRequireChangePassword(),
                model.getUser().getCreated(), model.getUser().getUpdated(), model.getUser().getDeleted(),
                model.getAction(), model.getTarget()
        );
    }

    public void sendMessage(TAPMessageModel message, TapSendMessageInterface tapSendMessageInterface) {
        sendMessageListeners.put(message.getLocalID(), tapSendMessageInterface);
        tapSendMessageInterface.onStart(message);
        triggerListenerAndSendMessage(message, true);
    }

    public void sendTextMessage(String textMessage) {
        sendTextMessageWithRoomModel(textMessage, activeRoom);
    }

    public void sendImageMessageToServer(TAPMessageModel messageModel) {
        removeUploadingMessageFromHashMap(messageModel.getLocalID());
        triggerListenerAndSendMessage(messageModel, false);
    }

    public void sendFileMessageToServer(TAPMessageModel messageModel) {
        removeUploadingMessageFromHashMap(messageModel.getLocalID());
        triggerListenerAndSendMessage(messageModel, false);
    }

    public void sendProductMessageToServer(HashMap<String, Object> productList, TAPRoomModel roomModel, TapSendMessageInterface sendMessageInterface) {
        TAPMessageModel productMessage = createProductMessageModel(productList, roomModel);
        sendMessageListeners.put(productMessage.getLocalID(), sendMessageInterface);
        sendMessageInterface.onStart(productMessage);
        triggerListenerAndSendMessage(productMessage, true);
    }

    public void sendLocationMessage(String address, Double latitude, Double longitude) {
        triggerListenerAndSendMessage(createLocationMessageModel(address, latitude, longitude), true);
    }

    public void sendLocationMessage(String address, Double latitude, Double longitude, TAPRoomModel room, TapSendMessageInterface tapsendMessageInterface) {
        TAPMessageModel messageModel = createLocationMessageModel(address, latitude, longitude, room, tapsendMessageInterface);
        sendMessageListeners.put(messageModel.getLocalID(), tapsendMessageInterface);
        tapsendMessageInterface.onStart(messageModel);
        triggerListenerAndSendMessage(messageModel, true);
    }

    public void sendTextMessageWithRoomModel(String textMessage, TAPRoomModel roomModel) {
        Integer startIndex;

        checkAndSendForwardedMessage(roomModel);

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.getInstance().mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel = createTextMessage(substr, roomModel, getActiveUser());
                // Add entity to list
                messageEntities.add(convertToEntity(messageModel));

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
            TAPMessageModel messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
            // Send message
            triggerListenerAndSendMessage(messageModel, true);
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    public void sendTextMessageWithRoomModel(String textMessage, TAPRoomModel roomModel, TapSendMessageInterface tapSendMessageInterface) {
        Integer startIndex;

        checkAndSendForwardedMessage(roomModel);

        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            //List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.getInstance().mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel = createTextMessage(substr, roomModel, getActiveUser());

                sendMessageListeners.put(messageModel.getLocalID(), tapSendMessageInterface);
                tapSendMessageInterface.onStart(messageModel);

                // Add entity to list
                //messageEntities.add(convertToEntity(messageModel));

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
            TAPMessageModel messageModel = createTextMessage(textMessage, roomModel, getActiveUser());
            sendMessageListeners.put(messageModel.getLocalID(), tapSendMessageInterface);
            tapSendMessageInterface.onStart(messageModel);
            // Send message
            triggerListenerAndSendMessage(messageModel, true);
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    public void sendDirectReplyTextMessage(String textMessage, TAPRoomModel roomModel) {
        if (!TapTalk.isForeground)
            TAPConnectionManager.getInstance().connect();

        Integer startIndex;
        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = TAPUtils.getInstance().mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                TAPMessageModel messageModel = createTextMessage(substr, roomModel, getActiveUser());
                // Add entity to list
                messageEntities.add(convertToEntity(messageModel));

                // save LocalID to list of Reply Local IDs
                // gunanya adalah untuk ngecek kapan semua reply message itu udah kekirim atau belom
                // ini kalau misalnya message yang di kirim > character limit
                addReplyMessageLocalID(messageModel.getLocalID());

                // Send truncated message
                triggerListenerAndSendMessage(messageModel, true);
            }
        } else {
            TAPMessageModel messageModel = createTextMessage(textMessage, roomModel, getActiveUser());

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
                    user, TYPE_PERSONAL == room.getRoomType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    null
            );
        } else {
            HashMap<String, Object> data = new HashMap<>();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            return TAPMessageModel.BuilderWithQuotedMessage(
                    message,
                    room,
                    TYPE_TEXT,
                    System.currentTimeMillis(),
                    user, TYPE_PERSONAL == room.getRoomType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data,
                    getQuotedMessages().get(room.getRoomID())
            );
        }
    }

    /**
     * Construct Product Message Model
     */
    private TAPMessageModel createProductMessageModel(HashMap<String, Object> product,
                                                      TAPRoomModel roomModel) {
        return TAPMessageModel.Builder(
                "Product List",
                roomModel,
                TYPE_PRODUCT,
                System.currentTimeMillis(),
                activeUser,
                TYPE_PERSONAL == activeRoom.getRoomType() ?
                        getOtherUserIdFromRoom(roomModel.getRoomID()) :
                        "0",
                product
        );
    }

    /**
     * Construct Location Message Model
     */
    private TAPMessageModel createLocationMessageModel(String address, Double latitude, Double longitude) {
        if (null == getQuotedMessage()) {
            return TAPMessageModel.Builder(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    activeRoom,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    new TAPDataLocationModel(address, latitude, longitude).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataLocationModel(address, latitude, longitude).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            return TAPMessageModel.BuilderWithQuotedMessage(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    activeRoom,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
    }

    private TAPMessageModel createLocationMessageModel(String address, Double latitude, Double longitude, TAPRoomModel roomModel, TapSendMessageInterface listener) {
        if (null == getQuotedMessage()) {
            return TAPMessageModel.Builder(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    roomModel,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataLocationModel(address, latitude, longitude).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataLocationModel(address, latitude, longitude).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            return TAPMessageModel.BuilderWithQuotedMessage(
                    TapTalk.appContext.getString(R.string.tap_location_body),
                    roomModel,
                    TYPE_LOCATION,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }

    }

    /**
     * Construct File Message Model
     */
    private TAPMessageModel createFileMessageModel(Context context, File file) {
        String fileName = file.getName();
        Number fileSize = file.length();
        String fileMimeType = null != TAPUtils.getInstance().getFileMimeType(file) ?
                TAPUtils.getInstance().getFileMimeType(file) : "application/octet-stream";

        // Build message model
        TAPMessageModel messageModel;
        Uri fileUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);
        HashMap<String, Object> data = new TAPDataFileModel(fileName, fileMimeType, fileSize).toHashMap();
        data.put(FILE_URI, fileUri.toString());
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateFileMessageBody(fileName),
                    activeRoom,
                    TYPE_FILE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data);
        } else {
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateFileMessageBody(fileName),
                    activeRoom,
                    TYPE_FILE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        // Save file Uri
//        TAPFileDownloadManager.getInstance().saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), fileUri);
        TAPFileDownloadManager.getInstance().addFileProviderPath(fileUri, file.getAbsolutePath());
        return messageModel;
    }

    private TAPMessageModel createFileMessageModel(Context context, File file, TAPRoomModel roomModel) {
        String fileName = file.getName();
        Number fileSize = file.length();
        String fileMimeType = null != TAPUtils.getInstance().getFileMimeType(file) ?
                TAPUtils.getInstance().getFileMimeType(file) : "application/octet-stream";

        // Build message model
        TAPMessageModel messageModel;
        Uri fileUri = FileProvider.getUriForFile(context, FILEPROVIDER_AUTHORITY, file);
        HashMap<String, Object> data = new TAPDataFileModel(fileName, fileMimeType, fileSize).toHashMap();
        data.put(FILE_URI, fileUri.toString());
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateFileMessageBody(fileName),
                    roomModel,
                    TYPE_FILE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data);
        } else {
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateFileMessageBody(fileName),
                    roomModel,
                    TYPE_FILE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        // Save file Uri
//        TAPFileDownloadManager.getInstance().saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), fileUri);
        TAPFileDownloadManager.getInstance().addFileProviderPath(fileUri, file.getAbsolutePath());
        return messageModel;
    }

    /**
     * Create file message model and call upload api
     */
    private void createFileMessageModelAndAddToUploadQueue(Context context, String roomID, File file) {
        TAPMessageModel messageModel = createFileMessageModel(context, file);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, roomID, messageModel);
    }

    private void createFileMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createFileMessageModel(context, file, roomModel);

        // Check if file size exceeds limit
        if (null != messageModel.getData() && null != messageModel.getData().get(SIZE) &&
                ((Number) messageModel.getData().get(SIZE)).longValue() > TAPFileUploadManager.getInstance().getMaxFileUploadSize()) {
            listener.onError(ERROR_CODE_EXCEEDED_MAX_SIZE, ERROR_MESSAGE_EXCEEDED_MAX_SIZE);
            return;
        }

        sendMessageListeners.put(messageModel.getLocalID(), listener);
        listener.onStart(messageModel);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, roomModel.getRoomID(), messageModel, listener);
    }

    public void sendFileMessage(Context context, String roomID, File file) {
        new Thread(() -> createFileMessageModelAndAddToUploadQueue(context, roomID, file)).start();
    }

    public void sendFileMessage(Context context, TAPRoomModel roomModel, File file, TapSendMessageInterface listener) {
        new Thread(() -> createFileMessageModelAndAddToUploadQueue(context, roomModel, file, listener)).start();
    }

    public void sendFileMessage(Context context, File file) {
        new Thread(() -> createFileMessageModelAndAddToUploadQueue(context, getOpenRoom(), file)).start();
    }

    public void sendFileMessage(Context context, TAPMessageModel fileModel) {
        new Thread(() -> {
            addUploadingMessageToHashMap(fileModel);
            TAPFileUploadManager.getInstance().addUploadQueue(context, fileModel.getRoom().getRoomID(), fileModel);
        }).start();
    }

    private String generateFileMessageBody(String fileName) {
        return TapTalk.appContext.getString(R.string.tap_emoji_file) + " " +
                (fileName.isEmpty() ? TapTalk.appContext.getString(R.string.tap_file) : fileName);
    }

    /**
     * Construct Image Message Model
     */
    private TAPMessageModel createImageMessageModel(Context context, Uri fileUri, String caption) {
        String imageUri = fileUri.toString();
        String imagePath = TAPFileUtils.getInstance().getFilePath(context, fileUri);
        long size = null == imagePath ? 0L : new File(imagePath).length();

        // Get image width and height
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int imageWidth;
        int imageHeight;
        int orientation = TAPFileUtils.getInstance().getImageOrientation(imagePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            imageWidth = options.outHeight;
            imageHeight = options.outWidth;
        } else {
            imageWidth = options.outWidth;
            imageHeight = options.outHeight;
        }

        // Build message model
        TAPMessageModel messageModel;
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    activeRoom,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    activeRoom,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        return messageModel;
    }

    private TAPMessageModel createImageMessageModel(Context context, Uri fileUri, String caption, TAPRoomModel roomModel) {
        String imageUri = fileUri.toString();
        String imagePath = TAPFileUtils.getInstance().getFilePath(context, fileUri);
        long size = null == imagePath ? 0L : new File(imagePath).length();

        // Get image width and height
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int imageWidth;
        int imageHeight;
        int orientation = TAPFileUtils.getInstance().getImageOrientation(imagePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            imageWidth = options.outHeight;
            imageHeight = options.outWidth;
        } else {
            imageWidth = options.outWidth;
            imageHeight = options.outHeight;
        }

        // Build message model
        TAPMessageModel messageModel;
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, imageUri).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        return messageModel;
    }

    private TAPMessageModel createImageMessageModel(Bitmap bitmap, String caption) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        long size = bitmap.getByteCount();

        // Build message model
        TAPMessageModel messageModel;
        Log.e(TAG, "createImageMessageModel: " + activeRoom.getRoomType());
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    activeRoom,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    activeRoom,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        return messageModel;
    }

    private TAPMessageModel createImageMessageModel(Bitmap bitmap, String caption, TAPRoomModel roomModel) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        long size = bitmap.getByteCount();

        // Build message model
        TAPMessageModel messageModel;
        Log.e(TAG, "createImageMessageModel: " + roomModel.getRoomType());
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap());
        } else {
            HashMap<String, Object> data = new TAPDataImageModel(imageWidth, imageHeight, size, caption, null, null).toHashMap();
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateImageCaption(caption),
                    roomModel,
                    TYPE_IMAGE,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == roomModel.getRoomType() ? getOtherUserIdFromRoom(roomModel.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
        return messageModel;
    }

    private String generateImageCaption(String caption) {
        return TapTalk.appContext.getString(R.string.tap_emoji_photo) + " " + (caption.isEmpty() ? TapTalk.appContext.getString(R.string.tap_photo) : caption);
    }

    private TAPMessageModel createVideoMessageModel(Context context, Uri fileUri, String caption) {
        String videoPath = TAPFileUtils.getInstance().getFilePath(context, fileUri);

        // Get video data
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, fileUri);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int width, height;
        if (rotation.equals("90") || rotation.equals("270")) {
            // Swap width and height when video is rotated
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        } else {
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        }
        int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        String thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(TAPFileUploadManager.getInstance().resizeBitmap(retriever.getFrameAtTime(), THUMB_MAX_DIMENSION));
        retriever.release();
        long size = null == videoPath ? 0L : new File(videoPath).length();

        // Build message model
        TAPMessageModel messageModel;
        HashMap<String, Object> data = new TAPDataImageModel(width, height, size, caption, null, videoPath).toHashMap();
        data.put(DURATION, duration);
        data.put(THUMBNAIL, thumbBase64);
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateVideoCaption(caption),
                    activeRoom,
                    TYPE_VIDEO,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data);
        } else {
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateVideoCaption(caption),
                    activeRoom,
                    TYPE_VIDEO,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
//        TAPFileDownloadManager.getInstance().saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), videoPath);
        return messageModel;
    }

    private TAPMessageModel createVideoMessageModel(Context context, Uri fileUri, String caption, TAPRoomModel room) {
        String videoPath = TAPFileUtils.getInstance().getFilePath(context, fileUri);

        // Get video data
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, fileUri);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int width, height;
        if (rotation.equals("90") || rotation.equals("270")) {
            // Swap width and height when video is rotated
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        } else {
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        }
        int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        String thumbBase64 = TAPFileUtils.getInstance().encodeToBase64(TAPFileUploadManager.getInstance().resizeBitmap(retriever.getFrameAtTime(), THUMB_MAX_DIMENSION));
        retriever.release();
        long size = null == videoPath ? 0L : new File(videoPath).length();

        // Build message model
        TAPMessageModel messageModel;
        HashMap<String, Object> data = new TAPDataImageModel(width, height, size, caption, null, videoPath).toHashMap();
        data.put(DURATION, duration);
        data.put(THUMBNAIL, thumbBase64);
        if (null == getQuotedMessage()) {
            messageModel = TAPMessageModel.Builder(
                    generateVideoCaption(caption),
                    room,
                    TYPE_VIDEO,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == room.getRoomType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data);
        } else {
            if (null != getUserInfo()) {
                data.put(USER_INFO, getUserInfo());
            }
            messageModel = TAPMessageModel.BuilderWithQuotedMessage(
                    generateVideoCaption(caption),
                    room,
                    TYPE_VIDEO,
                    System.currentTimeMillis(),
                    activeUser,
                    TYPE_PERSONAL == room.getRoomType() ? getOtherUserIdFromRoom(room.getRoomID()) : "0",
                    data,
                    getQuotedMessage());
        }
//        TAPFileDownloadManager.getInstance().saveFileMessageUri(messageModel.getRoom().getRoomID(), messageModel.getLocalID(), videoPath);
        return messageModel;
    }

    private String generateVideoCaption(String caption) {
        return TapTalk.appContext.getString(R.string.tap_emoji_video) + " " + (caption.isEmpty() ? TapTalk.appContext.getString(R.string.tap_video) : caption);
    }

    /**
     * Create image message model and call upload api
     */
    private void createImageMessageModelAndAddToUploadQueue(Context context, String roomID, Uri fileUri, String caption) {
        TAPMessageModel messageModel = createImageMessageModel(context, fileUri, caption);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, roomID, messageModel);
    }

    private void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri fileUri, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createImageMessageModel(context, fileUri, caption, roomModel);

        // Check if caption length exceeds limit
        if (caption.length() > MAX_CAPTION_LENGTH) {
            listener.onError(ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, MAX_CAPTION_LENGTH));
            return;
        }

        sendMessageListeners.put(messageModel.getLocalID(), listener);
        listener.onStart(messageModel);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, listener);
    }

    private void createVideoMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Uri fileUri, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createVideoMessageModel(context, fileUri, caption, roomModel);

        // Check if caption length exceeds limit
        if (caption.length() > MAX_CAPTION_LENGTH) {
            listener.onError(ERROR_CODE_CAPTION_EXCEEDS_LIMIT, String.format(Locale.getDefault(), ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT, MAX_CAPTION_LENGTH));
            return;
        }
        // Check if file size exceeds limit
        if (null != messageModel.getData() && null != messageModel.getData().get(SIZE) &&
                ((Number) messageModel.getData().get(SIZE)).longValue() > TAPFileUploadManager.getInstance().getMaxFileUploadSize()) {
            listener.onError(ERROR_CODE_EXCEEDED_MAX_SIZE, ERROR_MESSAGE_EXCEEDED_MAX_SIZE);
            return;
        }
        sendMessageListeners.put(messageModel.getLocalID(), listener);
        listener.onStart(messageModel);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, listener);
    }

    /**
     * Create Image Message with Bitmap Model and Call Upload API
     *
     * @param context
     * @param roomID
     * @param bitmap
     * @param caption
     */
    private void createImageMessageModelAndAddToUploadQueue(Context context, String roomID, Bitmap bitmap, String caption) {
        TAPMessageModel messageModel = createImageMessageModel(bitmap, caption);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, roomID, messageModel, bitmap);
    }

    private void createImageMessageModelAndAddToUploadQueue(Context context, TAPRoomModel roomModel, Bitmap bitmap, String caption, TapSendMessageInterface listener) {
        TAPMessageModel messageModel = createImageMessageModel(bitmap, caption, roomModel);
        sendMessageListeners.put(messageModel.getLocalID(), listener);
        listener.onStart(messageModel);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        messageModel = fixOrientationAndShowImagePreviewBubble(messageModel, bitmap);

        TAPFileUploadManager.getInstance().addUploadQueue(context, messageModel.getRoom().getRoomID(), messageModel, bitmap, listener);
    }

    /**
     * Returns Message Model with original image width & height data
     */
    // Previously showDummyImageMessage
    private TAPMessageModel fixOrientationAndShowImagePreviewBubble(TAPMessageModel imageMessage) {
        if (null == imageMessage.getData()) {
            return imageMessage;
        }
        TAPDataImageModel imageData = new TAPDataImageModel(imageMessage.getData());
        Uri imageUri = Uri.parse(imageData.getFileUri());

        // Get image width and height
        String pathName = TAPFileUtils.getInstance().getFilePath(TapTalk.appContext, imageUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        int orientation = TAPFileUtils.getInstance().getImageOrientation(pathName);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            imageData.setWidth(options.outHeight);
            imageData.setHeight(options.outWidth);
        } else {
            imageData.setWidth(options.outWidth);
            imageData.setHeight(options.outHeight);
        }
        imageMessage.putData(TAPUtils.getInstance().toHashMap(imageData));

        // Trigger listener to show image preview in activity
        triggerSendMessageListener(imageMessage);

        return imageMessage;
    }

    private TAPMessageModel fixOrientationAndShowImagePreviewBubble(TAPMessageModel imageMessage, Bitmap bitmap) {
        if (null == imageMessage.getData()) {
            return imageMessage;
        }
        Log.e(TAG, "Height: " + bitmap.getHeight());
        Log.e(TAG, "Width: " + bitmap.getWidth());
        TAPDataImageModel imageData = new TAPDataImageModel(imageMessage.getData());
        imageData.setWidth(bitmap.getWidth());
        imageData.setHeight(bitmap.getHeight());
        imageMessage.putData(TAPUtils.getInstance().toHashMap(imageData));

        // Trigger listener to show image preview in activity
        triggerSendMessageListener(imageMessage);

        return imageMessage;
    }

    private void createVideoMessageModelAndAddToUploadQueue(Context context, String roomID, Uri fileUri, String caption) {
        TAPMessageModel messageModel = createVideoMessageModel(context, fileUri, caption);

        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageModel.getLocalID(), 0, 0);

        addUploadingMessageToHashMap(messageModel);
        triggerSendMessageListener(messageModel);

        TAPFileUploadManager.getInstance().addUploadQueue(context, roomID, messageModel);
    }

    public void sendImageOrVideoMessage(Context context, TAPRoomModel room, ArrayList<TAPMediaPreviewModel> medias) {
        new Thread(() -> {
            checkAndSendForwardedMessage(room);
            for (TAPMediaPreviewModel media : medias) {
                if (media.getType() == TYPE_IMAGE) {
                    createImageMessageModelAndAddToUploadQueue(context, room.getRoomID(), media.getUri(), media.getCaption());
                } else if (media.getType() == TYPE_VIDEO) {
                    createVideoMessageModelAndAddToUploadQueue(context, room.getRoomID(), media.getUri(), media.getCaption());
                }
            }
        }).start();
    }

    public void sendImageMessage(Context context, String roomID, Uri imageUri, String caption) {
        new Thread(() -> createImageMessageModelAndAddToUploadQueue(context, roomID, imageUri, caption)).start();
    }

    public void sendImageMessage(Context context, String roomID, Bitmap bitmap, String caption) {
        new Thread(() -> createImageMessageModelAndAddToUploadQueue(context, roomID, bitmap, caption)).start();
    }

    public void sendImageMessage(Context context, TAPRoomModel roomModel, Uri imageUri, String caption, TapSendMessageInterface listener) {
        new Thread(() -> createImageMessageModelAndAddToUploadQueue(context, roomModel, imageUri, caption, listener)).start();
    }

    public void sendImageMessage(Context context, TAPRoomModel roomModel, Bitmap bitmap, String caption, TapSendMessageInterface listener) {
        new Thread(() -> createImageMessageModelAndAddToUploadQueue(context, roomModel, bitmap, caption, listener)).start();
    }

    public void sendVideoMessage(Context context, TAPRoomModel roomModel, Uri videoUri, String caption, TapSendMessageInterface listener) {
        new Thread(() -> createVideoMessageModelAndAddToUploadQueue(context, roomModel, videoUri, caption, listener)).start();
    }

    public void resendMessage(TAPMessageModel failedMessageModel) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        triggerListenerAndSendMessage(messageToResend, true);
    }

    public void resendMessage(TAPMessageModel failedMessageModel, TapSendMessageInterface listener) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        sendMessageListeners.put(messageToResend.getLocalID(), listener);
        listener.onStart(messageToResend);
        triggerListenerAndSendMessage(messageToResend, true);
    }

    public void retryUpload(Context context, TAPMessageModel failedMessageModel) {
        TAPMessageModel messageToResend = TAPMessageModel.BuilderResendMessage(failedMessageModel, System.currentTimeMillis());
        // Set Start Point for Progress
        TAPFileUploadManager.getInstance().addUploadProgressMap(messageToResend.getLocalID(), 0, 0);
        addUploadingMessageToHashMap(messageToResend);
        TAPFileUploadManager.getInstance().addUploadQueue(context, messageToResend.getRoom().getRoomID(), messageToResend);
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
            TAPMessageModel messageModel = buildForwardedMessage(getQuotedMessages().get(roomID), roomModel);
            triggerListenerAndSendMessage(messageModel, true);
            setQuotedMessage(roomID, null, 0);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAndSendForwardedMessage(TAPRoomModel roomModel, TapSendMessageInterface listener) {
        String roomID = roomModel.getRoomID();
        if (null != getQuoteActions().get(roomID) && getQuoteActions().get(roomID) == FORWARD) {
            // Send forwarded message
            TAPMessageModel messageModel = buildForwardedMessage(getQuotedMessages().get(roomID), roomModel);
            sendMessageListeners.put(messageModel.getLocalID(), listener);
            listener.onStart(messageModel);
            triggerListenerAndSendMessage(messageModel, true);
            setQuotedMessage(roomID, null, 0);
            return true;
        } else {
            return false;
        }
    }

    private TAPMessageModel buildForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room) {
        if (null == getQuotedMessages().get(room.getRoomID())) {
            return null;
        }
        if ((messageToForward.getType() == TYPE_VIDEO || messageToForward.getType() == TYPE_FILE) && null != messageToForward.getData()) {
            // Copy file message Uri to destination room
            String fileID = (String) messageToForward.getData().get(FILE_ID);
            TAPFileDownloadManager.getInstance().saveFileMessageUri(room.getRoomID(), fileID, TAPFileDownloadManager.getInstance().getFileMessageUri(messageToForward.getRoom().getRoomID(), fileID));
        }
        return TAPMessageModel.BuilderForwardedMessage(
                messageToForward,
                room,
                System.currentTimeMillis(),
                getActiveUser(),
                TYPE_PERSONAL == activeRoom.getRoomType() ? getOtherUserIdFromRoom(activeRoom.getRoomID()) : "0");
    }

    private void triggerSendMessageListener(TAPMessageModel messageModel) {
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty()) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                TAPMessageModel tempNewMessage = messageModel.copyMessageModel();
                chatListener.onSendMessage(tempNewMessage);
            }
        }
    }

    // Previously sendMessage
    private void triggerListenerAndSendMessage(TAPMessageModel messageModel, boolean isNotifyChatListener) {
        // Call listener
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty() && isNotifyChatListener) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                TAPMessageModel tempNewMessage = messageModel.copyMessageModel();
                chatListener.onSendMessage(tempNewMessage);
            }
        }
        runSendMessageSequence(messageModel);
    }

    /**
     * Message draft
     */
    public void saveMessageToDraft(String message) {
        messageDrafts.put(getActiveRoom().getRoomID(), message);
    }

    public void saveMessageToDraft(String roomID, String message) {
        messageDrafts.put(roomID, message);
    }

    public String getMessageFromDraft() {
        return messageDrafts.get(getActiveRoom().getRoomID());
    }

    public void removeDraft() {
        messageDrafts.remove(getActiveRoom().getRoomID());
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

    public HashMap<String, Object> getUserInfo() {
        if (null == activeRoom) {
            return null;
        }
        return getUserInfoMap().get(activeRoom.getRoomID());
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

    public void setQuotedMessage(@Nullable TAPMessageModel message, int quoteAction) {
        if (null == activeRoom) {
            return;
        }
        String roomID = activeRoom.getRoomID();
        if (null == message) {
            // Delete quoted message and user info in active room
            getQuotedMessages().remove(roomID);
            getQuoteActions().remove(roomID);
            removeUserInfo(roomID);
        } else {
            getQuotedMessages().put(roomID, message);
            getQuoteActions().put(roomID, quoteAction);
        }
    }

    public void setQuotedMessage(String roomID, @Nullable TAPMessageModel message, int quoteAction) {
        if (null == message) {
            // Delete quoted message and user info
            getQuotedMessages().remove(roomID);
            getQuoteActions().remove(roomID);
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
        dummyUserWithName.setName(quoteTitle);
        HashMap<String, Object> quoteData = new HashMap<>();
        quoteData.put("imageURL", null == quoteImageURL ? "" : quoteImageURL);
        // Dummy message model for quote
        TAPMessageModel dummyMessage = TAPMessageModel.Builder(
                null == quoteContent ? "" : quoteContent, new TAPRoomModel(),
                -1, 0L, dummyUserWithName, "", quoteData);
        getQuotedMessages().put(roomID, dummyMessage);
        getQuoteActions().put(roomID, REPLY);
    }

    public TAPMessageModel getQuotedMessage() {
        if (null == activeRoom) {
            return null;
        }
        return getQuotedMessages().get(activeRoom.getRoomID());
    }

    public Integer getQuoteAction() {
        if (null == activeRoom) {
            return -1;
        }
        return getQuoteActions().get(activeRoom.getRoomID());
    }

    /**
     * send pending messages from queue
     */
    public void checkAndSendPendingMessages() {
        if (!pendingMessages.isEmpty()) {
            TAPMessageModel message = pendingMessages.entrySet().iterator().next().getValue();
            runSendMessageSequence(message);
            pendingMessages.remove(message.getLocalID());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    checkAndSendPendingMessages();
                }
            }, 50);
        }
    }

    /**
     * Send message to server
     */
    private void runSendMessageSequence(TAPMessageModel messageModel) {
        if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
            waitingResponses.put(messageModel.getLocalID(), messageModel);

            // Send message if socket is connected
            sendEmit(kSocketNewMessage, messageModel);
        } else {
            // Add message to queue if socket is not connected
            pendingMessages.put(messageModel.getLocalID(), messageModel);
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
            TAPEmitModel<HashMap<String, Object>> TAPEmitModel;
            TAPEmitModel = new TAPEmitModel<>(eventName, TAPEncryptorManager.getInstance().encryptMessage(messageModel));
            TAPConnectionManager.getInstance().send(TAPUtils.getInstance().toJsonString(TAPEmitModel));
            Log.d(TAG, "sendEmit: " + TAPUtils.getInstance().toJsonString(messageModel));
            if (sendMessageListeners.containsKey(messageModel.getLocalID())) {
                sendMessageListeners.get(messageModel.getLocalID()).onSuccess(messageModel);
                sendMessageListeners.remove(messageModel.getLocalID());
            }
        } catch (Exception e) {
            if (sendMessageListeners.containsKey(messageModel.getLocalID())) {
                sendMessageListeners.get(messageModel.getLocalID()).onError(ERROR_CODE_OTHERS, e.getMessage());
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
        TAPConnectionManager.getInstance().send(TAPUtils.getInstance().toJsonString(TAPEmitModel));
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
        for (Map.Entry<String, TAPMessageModel> message : hashMap.entrySet()) {
            saveMessages.add(convertToEntity(message.getValue()));
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
        if (TapTalk.isAutoConnectEnabled()) {
            TAPConnectionManager.getInstance().close();
        }
        saveUnsentMessage();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        isFinishChatFlow = true;
    }

    public void disconnectAfterRefreshTokenExpired() {
        TAPConnectionManager.getInstance().close();
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

        // Remove from waiting response hashmap
        if (kSocketNewMessage.equals(eventName))
            waitingResponses.remove(newMessage.getLocalID());

        // Insert decrypted message to database
        incomingMessages.put(newMessage.getLocalID(), newMessage);

        // Query Unread Message
        //TAPNotificationManager.getInstance().updateUnreadCount();

        // Receive message in active room
        List<TAPChatListener> chatListenersCopy = new ArrayList<>(chatListeners);
        if (!chatListenersCopy.isEmpty() &&
                ((null != activeRoom && newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))
                        || (newMessage.getRoom().getRoomID().equals(openRoom)))) {
            for (TAPChatListener chatListener : chatListenersCopy) {
                TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                if (kSocketNewMessage.equals(eventName))
                    chatListener.onReceiveMessageInActiveRoom(tempNewMessage);
                else if (kSocketUpdateMessage.equals(eventName))
                    chatListener.onUpdateMessageInActiveRoom(tempNewMessage);
                else if (kSocketDeleteMessage.equals(eventName))
                    chatListener.onDeleteMessageInActiveRoom(tempNewMessage);
            }
        }
        // Receive message outside active room (not in room List)
        else if (!TAPNotificationManager.getInstance().isRoomListAppear() && !chatListenersCopy.isEmpty() && (null == activeRoom || !newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))) {
            if (kSocketNewMessage.equals(eventName) && !newMessage.getUser().getUserID().equals(activeUser.getUserID()) &&
                    null != newMessage.getHidden() && !newMessage.getHidden() && null != newMessage.getIsDeleted()
                    && !newMessage.getIsDeleted()) {
                // Show notification for new messages from other users
                TAPNotificationManager.getInstance().createAndShowInAppNotification(TapTalk.appContext, newMessage);
            }
            for (TAPChatListener chatListener : chatListenersCopy) {
                TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                if (kSocketNewMessage.equals(eventName))
                    chatListener.onReceiveMessageInOtherRoom(tempNewMessage);
                else if (kSocketUpdateMessage.equals(eventName))
                    chatListener.onUpdateMessageInOtherRoom(tempNewMessage);
                else if (kSocketDeleteMessage.equals(eventName))
                    chatListener.onDeleteMessageInOtherRoom(tempNewMessage);
            }
        }
        // Receive message outside active room (in room List)
        else if (!chatListenersCopy.isEmpty() && (null == activeRoom || !newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))) {
            if (kSocketNewMessage.equals(eventName) && !newMessage.getUser().getUserID().equals(activeUser.getUserID()) &&
                    null != newMessage.getHidden() && !newMessage.getHidden() && null != newMessage.getIsDeleted()
                    && !newMessage.getIsDeleted()) {
                // Show notification for new messages from other users
                TAPNotificationManager.getInstance().createAndShowInAppNotification(TapTalk.appContext, newMessage);
            }
            for (TAPChatListener chatListener : chatListenersCopy) {
                TAPMessageModel tempNewMessage = newMessage.copyMessageModel();
                if (kSocketNewMessage.equals(eventName))
                    chatListener.onReceiveMessageInOtherRoom(tempNewMessage);
                else if (kSocketUpdateMessage.equals(eventName))
                    chatListener.onUpdateMessageInOtherRoom(tempNewMessage);
                else if (kSocketDeleteMessage.equals(eventName))
                    chatListener.onDeleteMessageInOtherRoom(tempNewMessage);
            }
        }

        // Add to list delivered message
        if (kSocketNewMessage.equals(eventName) && !newMessage.getUser().getUserID().equals(activeUser.getUserID())
                && null != newMessage.getSending() && !newMessage.getSending()
                && null != newMessage.getDelivered() && !newMessage.getDelivered()
                && null != newMessage.getIsRead() && !newMessage.getIsRead()) {
            TAPMessageStatusManager.getInstance().addDeliveredMessageQueue(newMessage);
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
        TAPContactManager.getInstance().updateUserData(newMessage.getUser());
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
            TAPMessageStatusManager.getInstance().triggerCallMessageStatusApi();
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

        TAPDataManager.getInstance().insertToDatabase(new ArrayList<>(saveMessages), true);
    }

    public List<TAPMessageEntity> getSaveMessages() {
        return saveMessages;
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

    public void setNeedToCalledUpdateRoomStatusAPI(boolean needToCalledUpdateRoomStatusAPI) {
        isNeedToCalledUpdateRoomStatusAPI = needToCalledUpdateRoomStatusAPI;
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
                chatListener.onReadMessage(roomID);
            }
        }).start();
    }

    public void resetChatManager() {
        clearSaveMessages();
        pendingMessages.clear();
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

        if (null == message.getTarget()) {
            systemMessageBody = message.getBody()
                    .replace("{", "")
                    .replace("}", "")
                    .replaceFirst("sender",
                            message.getUser().getUserID().equals(getActiveUser().getUserID()) ?
                                    "You" : message.getUser().getName());
        } else {
            systemMessageBody = message.getBody()
                    .replace("{", "")
                    .replace("}", "")
                    .replaceFirst("sender", message.getUser().getUserID().equals(getActiveUser().getUserID()) ?
                            "You" : message.getUser().getName())
                    .replaceFirst("target", message.getTarget().getTargetID() != null ?
                            message.getTarget().getTargetID().equals(getActiveUser().getUserID()) ?
                                    "you" : message.getTarget().getTargetName() == null ? "" : message.getTarget().getTargetName() : "");
        }

        return systemMessageBody;
    }

    /**
     * =============================================================================================
     *  TAP UI
     *  ============================================================================================
     */

    public void triggerTapTalkAccountButtonTapped(Activity activity) {
        TapUI.getInstance().triggerTapTalkAccountButtonTapped(activity);
    }


    public void triggerChatRoomProfileButtonTapped(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
        TapUI.getInstance().triggerChatRoomProfileButtonTapped(activity, room, user);
    }

    public void triggerMessageQuoteTapped(Activity activity, TAPMessageModel messageModel) {
        TapUI.getInstance().triggerMessageQuoteTapped(activity, messageModel);
    }

    public List<TAPCustomKeyboardItemModel> getCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, TAPUserModel recipientUser) {
        return TapUI.getInstance().getCustomKeyboardItems(room, activeUser, recipientUser);
    }

    public void triggerCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPRoomModel room, TAPUserModel activeUser, TAPUserModel otherUser) {
        TapUI.getInstance().triggerCustomKeyboardItemTapped(activity, customKeyboardItemModel, room, activeUser, otherUser);
    }

    public void triggerProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        TapUI.getInstance().triggerProductListBubbleLeftOrSingleButtonTapped(activity, product, room, recipient, isSingleOption);
    }

    public void triggerProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        TapUI.getInstance().triggerProductListBubbleRightButtonTapped(activity, product, room, recipient, isSingleOption);
    }
}
