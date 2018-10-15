package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Interface.HomingPigeonSocketInterface;
import com.moselo.HomingPigeon.Listener.HpChatListener;
import com.moselo.HomingPigeon.Model.HpEmitModel;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.HpUserRoleModel;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kEventOpenRoom;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketAuthentication;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketCloseRoom;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketDeleteMessage;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketNewMessage;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketOpenMessage;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketStartTyping;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketStopTyping;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketUpdateMessage;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketUserOffline;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.ConnectionEvent.kSocketUserOnline;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpChatManager {

    private final String TAG = HpChatManager.class.getSimpleName();
    private static HpChatManager instance;
    private List<HpChatListener> chatListeners;
    List<HpMessageEntity> saveMessages; //message to be save
    private Map<String, HpMessageModel> pendingMessages, waitingResponses, incomingMessages;
    private Map<String, String> messageDrafts;
    private HpRoomModel activeRoom;
    private HpUserModel activeUser;
    private boolean isCheckPendingArraySequenceActive = false;
    private boolean isPendingMessageExist = false;
    private boolean isFileUploadExist = false;
    private boolean isFinishChatFlow = false;
    private final Integer CHARACTER_LIMIT = 1000;
    private int pendingRetryAttempt = 0;
    private int maxRetryAttempt = 10;
    private int pendingRetryInterval = 60 * 1000;
    private ScheduledExecutorService scheduler;

    private HomingPigeonSocketInterface socketListener = new HomingPigeonSocketInterface() {
        @Override
        public void onSocketConnected() {
            Log.e(TAG, "onSocketConnected: ");
            checkAndSendPendingMessages();
            isFinishChatFlow = false;
        }

        @Override
        public void onSocketDisconnected() {
            if (HomingPigeon.isForeground && HpNetworkStateManager.getInstance().hasNetworkConnection(HomingPigeon.appContext)
                    && HpConnectionManager.ConnectionStatus.DISCONNECTED == HpConnectionManager.getInstance().getConnectionStatus())
                HpConnectionManager.getInstance().reconnect();
        }

        @Override
        public void onSocketConnecting() {

        }

        @Override
        public void onSocketError() {
            HpConnectionManager.getInstance().reconnect();
        }

        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {
            switch (eventName) {
                case kEventOpenRoom:
                    break;
                case kSocketCloseRoom:
                    break;
                case kSocketNewMessage:
                    HpEmitModel<HpMessageModel> messageEmit = HpUtils.getInstance()
                            .fromJSON(new TypeReference<HpEmitModel<HpMessageModel>>() {
                            }, emitData);
                    try {
                        Log.e(TAG, "onReceiveNewEmit: " + HpUtils.getInstance().toJsonString(messageEmit));
                        receiveMessageFromSocket(HpMessageModel.BuilderDecrypt(messageEmit.getData()), eventName);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case kSocketUpdateMessage:
                    HpEmitModel<HpMessageModel> messageUpdateEmit = HpUtils.getInstance()
                            .fromJSON(new TypeReference<HpEmitModel<HpMessageModel>>() {
                            }, emitData);
                    try {
                        receiveMessageFromSocket(HpMessageModel.BuilderDecrypt(messageUpdateEmit.getData()), eventName);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case kSocketDeleteMessage:
                    HpEmitModel<HpMessageModel> messageDeleteEmit = HpUtils.getInstance()
                            .fromJSON(new TypeReference<HpEmitModel<HpMessageModel>>() {
                            }, emitData);
                    try {
                        receiveMessageFromSocket(HpMessageModel.BuilderDecrypt(messageDeleteEmit.getData()), eventName);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case kSocketOpenMessage:
                    break;
                case kSocketStartTyping:
                    break;
                case kSocketStopTyping:
                    break;
                case kSocketAuthentication:
                    break;
                case kSocketUserOnline:
                    break;
                case kSocketUserOffline:
                    break;
            }
        }
    };

    public static HpChatManager getInstance() {
        return instance == null ? (instance = new HpChatManager()) : instance;
    }

    public HpChatManager() {
        HpConnectionManager.getInstance().addSocketListener(socketListener);
        setActiveUser(HpDataManager.getInstance().getActiveUser());
        chatListeners = new ArrayList<>();
        saveMessages = new ArrayList<>();
        pendingMessages = new LinkedHashMap<>();
        waitingResponses = new LinkedHashMap<>();
        incomingMessages = new LinkedHashMap<>();
        messageDrafts = new HashMap<>();
    }

    public void addChatListener(HpChatListener chatListener) {
        chatListeners.add(chatListener);
    }

    public void removeChatListener(HpChatListener chatListener) {
        chatListeners.remove(chatListener);
    }

    public void removeChatListenerAt(int index) {
        chatListeners.remove(index);
    }

    public void clearChatListener() {
        chatListeners.clear();
    }

    public HpRoomModel getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(HpRoomModel roomId) {
        this.activeRoom = roomId;
    }

    public HpUserModel getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(HpUserModel user) {
        this.activeUser = user;
    }

    public void saveActiveUser(Context context, HpUserModel user) {
        this.activeUser = user;
        HpDataManager.getInstance().saveActiveUser(user);
    }

    public Map<String, HpMessageModel> getMessageQueueInActiveRoom() {
        Map<String, HpMessageModel> roomQueue = new LinkedHashMap<>();
        for (Map.Entry<String, HpMessageModel> entry : pendingMessages.entrySet()) {
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
     * convert HpMessageEntity to HpMessageModel
     */
    public HpMessageModel convertToModel(HpMessageEntity entity) {
        return new HpMessageModel(
                entity.getMessageID(),
                entity.getLocalID(),
                entity.getBody(),
                new HpRoomModel(entity.getRoomID(), entity.getRoomName(), entity.getRoomType()),
                entity.getType(),
                entity.getCreated(),
                new HpUserModel(entity.getUserID(), entity.getXcUserID(), entity.getUserFullName(),
                        HpUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {}, entity.getUserImage()),
                        entity.getUsername(), entity.getUserEmail(), entity.getUserPhone(),
                        HpUtils.getInstance().fromJSON(new TypeReference<HpUserRoleModel>() {}, entity.getUserRole()),
                        entity.getLastLogin(), entity.getLastActivity(), entity.getRequireChangePassword(), entity.getUserCreated(),
                        entity.getUserUpdated()),
                entity.getRecipientID(),
                entity.getDeleted(),
                entity.getSending(),
                entity.getFailedSend(),
                entity.getUpdated());
    }

    /**
     * convert HpMessageModel to HpMessageEntity
     */
    public HpMessageEntity convertToEntity(HpMessageModel model) {
        return new HpMessageEntity(
                model.getMessageID(), model.getLocalID(), model.getBody(), model.getRecipientID(),
                model.getType(), model.getCreated(), model.getUpdated(), model.getHasRead(), model.getIsRead(),
                model.getDelivered(), model.getHidden(), model.getDeleted(), model.getSending(),
                model.getFailedSend(), model.getRoom().getRoomID(), model.getRoom().getRoomName(),
                model.getRoom().getRoomColor(), model.getRoom().getRoomType(),
                HpUtils.getInstance().toJsonString(model.getRoom().getRoomImage()),
                model.getUser().getUserID(), model.getUser().getXcUserID(), model.getUser().getName(),
                model.getUser().getUsername(), HpUtils.getInstance().toJsonString(model.getUser().getAvatarURL()),
                model.getUser().getEmail(), model.getUser().getPhoneNumber(),
                HpUtils.getInstance().toJsonString(model.getUser().getUserRole()),
                model.getUser().getLastLogin(), model.getUser().getLastActivity(),
                model.getUser().getRequireChangePassword(),
                model.getUser().getCreated(), model.getUser().getUpdated()
        );
    }

    /**
     * sending text messages
     */
    public void sendTextMessage(String textMessage) {
        Integer startIndex;
        if (textMessage.length() > CHARACTER_LIMIT) {
            // Message exceeds character limit
            List<HpMessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = HpUtils.getInstance().mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                HpMessageModel messageModel = buildTextMessage(substr, activeRoom);

                // Add entity to list
                messageEntities.add(HpChatManager.getInstance().convertToEntity(messageModel));

                // Send truncated message
                sendMessage(messageModel);
            }
        } else {
            HpMessageModel messageModel = buildTextMessage(textMessage, activeRoom);

            // Send message
            sendMessage(messageModel);
        }
        // Run queue after list is updated
        //checkAndSendPendingMessages();
    }

    private HpMessageModel buildTextMessage(String message, HpRoomModel room) {
        // Create new HpMessageModel based on text
        String[] splitedRoomID = room.getRoomID().split("-");
        String otherUserID = !splitedRoomID[0].equals(activeUser.getUserID()) ? splitedRoomID[0] : splitedRoomID[1];
        HpMessageModel messageModel = HpMessageModel.Builder(
                message,
                room,
                HpDefaultConstant.MessageType.TYPE_TEXT,
                System.currentTimeMillis(),
                activeUser, otherUserID);

        return messageModel;
    }

    /**
     * save text to draft
     */
    public void saveMessageToDraft(String message) {
        Log.e(TAG, "saveMessageToDraft: "+HpUtils.getInstance().toJsonString(activeRoom) );
        messageDrafts.put(activeRoom.getRoomID(), message);
    }

    public String getMessageFromDraft() {
        return messageDrafts.get(activeRoom);
    }

    /**
     * send pending messages from queue
     */
    public void checkAndSendPendingMessages() {
        Log.e(TAG, "checkAndSendPendingMessages: " + pendingMessages.size());
        if (!pendingMessages.isEmpty()) {
            HpMessageModel message = pendingMessages.entrySet().iterator().next().getValue();
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
     * sending Message to server
     */
    private void sendMessage(HpMessageModel messageModel) {
        // Call listener
        if (null != chatListeners && !chatListeners.isEmpty()) {
            for (HpChatListener chatListener : chatListeners)
                chatListener.onSendTextMessage(messageModel);
        }

        runSendMessageSequence(messageModel);
    }

    private void runSendMessageSequence(HpMessageModel messageModel) {
        Log.e(TAG, "runSendMessageSequence: " + HpConnectionManager.getInstance().getConnectionStatus());
        if (HpConnectionManager.getInstance().getConnectionStatus() == HpConnectionManager.ConnectionStatus.CONNECTED) {
            waitingResponses.put(messageModel.getLocalID(), messageModel);

            // Send message if socket is connected
            try {
                Log.e(TAG, "runSendMessageSequence: connected " + messageModel.getBody());
                sendEmit(kSocketNewMessage, HpMessageModel.BuilderEncrypt(messageModel));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            // Add message to queue if socket is not connected
            pendingMessages.put(messageModel.getLocalID(), messageModel);
            Log.e(TAG, "runSendMessageSequence: not connected");
        }
    }

    /**
     * Sending Emit to Server
     */
    private void sendEmit(String eventName, HpMessageModel messageModel) {
        HpEmitModel<HpMessageModel> hpEmitModel;
        hpEmitModel = new HpEmitModel<>(eventName, messageModel);
        HpConnectionManager.getInstance().send(HpUtils.getInstance().toJsonString(hpEmitModel));
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

    private void insertToList(Map<String, HpMessageModel> hashMap) {
        for (Map.Entry<String, HpMessageModel> message : hashMap.entrySet()) {
            saveMessages.add(convertToEntity(message.getValue()));
        }
    }

    private void checkPendingBackgroundTask() {
        // TODO: 05/09/18 nnti cek file manager upload queue juga
        isPendingMessageExist = false;
        isFileUploadExist = false;

        if (0 < pendingMessages.size())
            isPendingMessageExist = true;

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
        Log.e(TAG, "saveIncomingMessageAndDisconnect: ");
        HpConnectionManager.getInstance().close();
        saveUnsentMessage();
        if (null != scheduler && !scheduler.isShutdown())
            scheduler.shutdown();
        isFinishChatFlow = true;
    }

    public void deleteActiveRoom() {
        activeRoom = null;
    }

    /**
     * this is when receive new message from socket
     *
     * @param newMessage
     */
    private void receiveMessageFromSocket(HpMessageModel newMessage, String eventName) {
        // Remove from waiting response hashmap
        if (kSocketNewMessage.equals(eventName))
            waitingResponses.remove(newMessage.getLocalID());

        // Insert decrypted message to database
        incomingMessages.put(newMessage.getLocalID(), newMessage);

        // Receive message in active room
        if (null != chatListeners && !chatListeners.isEmpty() && null != activeRoom && newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID())) {
            for (HpChatListener chatListener : chatListeners) {
                if (kSocketNewMessage.equals(eventName))
                    chatListener.onReceiveMessageInActiveRoom(newMessage);
                else if (kSocketUpdateMessage.equals(eventName))
                    chatListener.onUpdateMessageInActiveRoom(newMessage);
                else if (kSocketDeleteMessage.equals(eventName))
                    chatListener.onDeleteMessageInActiveRoom(newMessage);
            }
        }
        // Receive message outside active room
        else if (null != chatListeners && !chatListeners.isEmpty() && (null == activeRoom || !newMessage.getRoom().getRoomID().equals(activeRoom.getRoomID()))) {
            for (HpChatListener chatListener : chatListeners) {
                if (kSocketNewMessage.equals(eventName))
                    chatListener.onReceiveMessageInOtherRoom(newMessage);
                else if (kSocketUpdateMessage.equals(eventName))
                    chatListener.onUpdateMessageInOtherRoom(newMessage);
                else if (kSocketDeleteMessage.equals(eventName))
                    chatListener.onDeleteMessageInOtherRoom(newMessage);
            }
        }
    }

    public void saveNewMessageToList() {
        if (0 == incomingMessages.size())
            return;

        insertToList(incomingMessages);
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

        insertToList(waitingResponses);
        waitingResponses.clear();
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
            saveMessageToDatabase();
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void saveUnsentMessage() {
        saveNewMessageToList();
        savePendingMessageToList();
        saveWaitingMessageToList();
        saveMessageToDatabase();
    }

    public void putUnsentMessageToList() {
        saveNewMessageToList();
        savePendingMessageToList();
        saveWaitingMessageToList();
    }

    private void saveMessageToDatabase() {
        if (0 == saveMessages.size()) return;

        HpDataManager.getInstance().insertToDatabase(saveMessages, true);
    }

    public List<HpMessageEntity> getSaveMessages() {
        return saveMessages;
    }

    public void clearSaveMessages() {
        saveMessages.clear();
    }

    public void setPendingRetryAttempt(int counter) {
        pendingRetryAttempt = counter;
    }

    public boolean isFinishChatFlow() {
        return isFinishChatFlow;
    }

    public void setFinishChatFlow(boolean finishChatFlow) {
        isFinishChatFlow = finishChatFlow;
    }
}
