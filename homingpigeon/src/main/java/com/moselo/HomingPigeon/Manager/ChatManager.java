package com.moselo.HomingPigeon.Manager;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Model.EmitModel;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
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

public class ChatManager {

    private static ChatManager instance;
    private List<HomingPigeonChatListener> chatListeners;

    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onNewMessage(String eventName, String emitData) {
            switch (eventName) {
                case kEventOpenRoom:
                    break;
                case kSocketCloseRoom:
                    break;
                case kSocketNewMessage:
                    EmitModel<MessageModel> tempObject = Utils.getInstance()
                            .fromJSON(new TypeReference<EmitModel<MessageModel>>() {}, emitData);
                    if (null != chatListeners && !chatListeners.isEmpty()) {
                        for (HomingPigeonChatListener chatListener : chatListeners)
                            chatListener.onNewTextMessage(tempObject.getData());
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

    public MessageModel buildTextMessage(String message, String roomId, UserModel userModel) {
        MessageModel messageModel;
        RoomModel roomModel = RoomModel.Builder(roomId);
        messageModel = MessageModel.Builder(
                message,
                roomModel,
                DefaultConstant.MessageType.TYPE_TEXT,
                System.currentTimeMillis(),
                userModel);
        return messageModel;
    }

    private MessageModel buildEncryptedTextMessage(String message, String roomId, UserModel userModel) {
        MessageModel messageModel;
        RoomModel roomModel = RoomModel.Builder(roomId);
        try {
            messageModel = MessageModel.BuilderEncrypt(
                    message,
                    roomModel,
                    DefaultConstant.MessageType.TYPE_TEXT,
                    System.currentTimeMillis(),
                    userModel);
            return messageModel;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MessageModel> buildEncryptedTextMessages(String message, String roomId, UserModel userModel) {
        List<MessageModel> messageModels = new ArrayList<>();
        Integer characterLimit = 1000;
        Integer startIndex;
        Integer length = message.length();
        if (length > characterLimit) {
            for (startIndex = 0; startIndex < length; startIndex += characterLimit) {
                String substr = Utils.getInstance().mySubString(message, startIndex, characterLimit);
                MessageModel messageModel;
                messageModel = buildEncryptedTextMessage(substr, roomId, userModel);
                if (null != messageModel) messageModels.add(messageModel);
            }
        }
        else {
            MessageModel messageModel;
            messageModel = buildEncryptedTextMessage(message, roomId, userModel);
            messageModels.add(messageModel);
        }
        return messageModels;
    }

    public void sendTextMessage(String textMessage, String roomId, UserModel userModel) {
        Log.e(ChatManager.class.getSimpleName(), "sendTextMessage: " + textMessage);
        Integer characterLimit = 1000;
        Integer startIndex;
        if (textMessage.length() > characterLimit) {
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += characterLimit) {
                String substr = Utils.getInstance().mySubString(textMessage, startIndex, characterLimit);
                MessageModel messageModel;
                messageModel = buildEncryptedTextMessage(substr, roomId, userModel);
                if (null != messageModel) {
                    EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
                    sendMessage(Utils.getInstance().toJsonString(emitModel));
                }
            }
        } else {
            MessageModel messageModel;
            messageModel = buildEncryptedTextMessage(textMessage, roomId, userModel);
            if (null != messageModel) {
                EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
                sendMessage(Utils.getInstance().toJsonString(emitModel));
            }
        }
    }

    public void sendTextMessage(MessageModel textMessage) {
        Log.e(ChatManager.class.getSimpleName(), "sendTextMessage: " + textMessage.getMessage());
        EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, textMessage);
        sendMessage(Utils.getInstance().toJsonString(emitModel));
    }

    public void sendTextMessage(List<MessageModel> textMessages) {
        for (MessageModel textMessage : textMessages) {
            Log.e(ChatManager.class.getSimpleName(), "sendTextMessage: " + textMessage.getMessage());
            EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, textMessage);
            sendMessage(Utils.getInstance().toJsonString(emitModel));
        }
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().sendEmit(message);
    }
}
