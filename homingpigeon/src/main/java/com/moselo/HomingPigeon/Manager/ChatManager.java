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

    public void sendTextMessage(String messageText, String roomID, UserModel userModel) {
        Log.e(ChatManager.class.getSimpleName(), "sendTextMessage: "+messageText );
        RoomModel roomModel = RoomModel.Builder(roomID);
        Integer characterLimit = 1000;
        Integer startIndex;
        if (messageText.length() > characterLimit) {
            Integer length = messageText.length();
            for (startIndex = 0; startIndex < length; startIndex += characterLimit) {
                String substr = Utils.mySubString(messageText, startIndex, characterLimit);
                MessageModel messageModel;
                try {
                    messageModel = MessageModel.BuilderEncrypt(substr, roomModel
                            , DefaultConstant.MessageType.TYPE_TEXT, System.currentTimeMillis(), userModel);
                    EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
                    sendMessage(Utils.getInstance().toJsonString(emitModel));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MessageModel messageModel;
            try {
                messageModel = MessageModel.BuilderEncrypt(messageText, roomModel,
                        DefaultConstant.MessageType.TYPE_TEXT, System.currentTimeMillis(), userModel);
                EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
                sendMessage(Utils.getInstance().toJsonString(emitModel));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().sendEmit(message);
    }
}
