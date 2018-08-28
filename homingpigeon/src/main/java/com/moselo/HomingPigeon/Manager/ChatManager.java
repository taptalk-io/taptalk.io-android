package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Model.EmitModel;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kEventOpenRoom;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketAuthentication;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketCloseRoom;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketDeleteMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketNewMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketOpenMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketStartTyping;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketStopTyping;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUpdateMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUserOffline;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUserOnline;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class ChatManager {

    private static ChatManager instance;
    private List<HomingPigeonChatListener> chatListeners;
    private String activeRoom;
    private UserModel activeUser;

    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onNewMessage(String eventName, String emitData) {
            switch (eventName) {
                case kEventOpenRoom:
                    break;
                case kSocketCloseRoom:
                    break;
                case kSocketNewMessage:
                    EmitModel<MessageModel> messageEmit = Utils.getInstance()
                            .fromJSON(new TypeReference<EmitModel<MessageModel>>() {}, emitData);
                    // Receive message in active room
                    if (null != chatListeners && !chatListeners.isEmpty() && messageEmit.getData().getRoom().equals(activeRoom)) {
                        for (HomingPigeonChatListener chatListener : chatListeners)
                            chatListener.onReceiveTextMessageInActiveRoom(messageEmit.getData());
                    }
                    // Receive message outside active room
                    else if (null != chatListeners && !chatListeners.isEmpty() && !messageEmit.getData().getRoom().equals(activeRoom)) {
                        for (HomingPigeonChatListener chatListener : chatListeners)
                            chatListener.onReceiveTextMessageInOtherRoom(messageEmit.getData());
                    }
                    break;
                case kSocketUpdateMessage:
                    break;
                case kSocketDeleteMessage:
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

    public static ChatManager getInstance() {
        return instance == null ? (instance = new ChatManager()) : instance;
    }

    public ChatManager() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        setActiveUser(Utils.getInstance().fromJSON(new TypeReference<UserModel>() {},
                PreferenceManager.getDefaultSharedPreferences(HomingPigeon.appContext)
                        .getString(K_USER, null)));
        chatListeners = new ArrayList<>();
    }

    public void addChatListener(HomingPigeonChatListener chatListener) {
        chatListeners.add(chatListener);
    }

    public void removeChatListener(HomingPigeonChatListener chatListener) {
        chatListeners.remove(chatListener);
    }

    public void removeChatListenerAt(int index) {
        chatListeners.remove(index);
    }

    public void clearChatListener() {
        chatListeners.clear();
    }

    public String getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(String roomId) {
        this.activeRoom = roomId;
    }

    public UserModel getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(UserModel user) {
        this.activeUser = user;
    }

    public void saveActiveUser(Context context, UserModel user) {
        this.activeUser = user;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, Utils.getInstance().toJsonString(user)).apply();
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
     * convert MessageEntity to MessageModel
     */
    public MessageModel convertToModel(MessageEntity entity) {
        MessageModel model = new MessageModel();
        try {
            model = MessageModel.BuilderDecrypt(
                    entity.getLocalId(),
                    entity.getMessage(),
                    entity.getRoomId(),
                    entity.getType(),
                    entity.getCreated(),
                    Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, entity.getUser()),
                    entity.getDeleted(),
                    entity.getIsSending(),
                    entity.getIsFailedSend());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * convert MessageModel to MessageEntity
     */
    public MessageEntity convertToEntity(MessageModel model) {
        MessageEntity entity = new MessageEntity();
        try {
            entity = new MessageEntity(
                    model.getMessageId(),
                    model.getLocalId(),
                    model.getRoom(),
                    model.getType(),
                    EncryptorManager.getInstance().encrypt(model.getMessage(), model.getLocalId()),
                    model.getCreated(),
                    Utils.getInstance().toJsonString(model.getUser()),
                    Utils.getInstance().toJsonString(model.getDeliveredTo()),
                    Utils.getInstance().toJsonString(model.getSeenBy()),
                    model.getDeleted(),
                    model.getIsSending(),
                    model.getIsFailedSend());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * sending text messages
     */
    public void sendTextMessage(String textMessage) {
        Log.e(ChatManager.class.getSimpleName(), "sendTextMessage: " + textMessage);
        Integer characterLimit = 1000;
        Integer startIndex;
        if (textMessage.length() > characterLimit) {
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += characterLimit) {
                String substr = Utils.getInstance().mySubString(textMessage, startIndex, characterLimit);
                buildAndSendTextMessage(substr);
            }
        } else {
            buildAndSendTextMessage(textMessage);
        }
    }

    private void buildAndSendTextMessage(String message) {
        MessageModel messageModel;
        try {
            messageModel = MessageModel.BuilderEncrypt(
                    message,
                    activeRoom,
                    DefaultConstant.MessageType.TYPE_TEXT,
                    System.currentTimeMillis(),
                    activeUser);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }
        EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
        sendMessage(Utils.getInstance().toJsonString(emitModel));
        if (null != chatListeners && !chatListeners.isEmpty()) {
            for (HomingPigeonChatListener chatListener : chatListeners)
                chatListener.onSendTextMessage(messageModel);
        }
    }

    /**
     * sending emit to server
     */
    private void sendMessage(String message) {
        ConnectionManager.getInstance().sendEmit(message);
    }
}
