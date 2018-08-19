package com.moselo.HomingPigeon.Manager;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

public class ChatManager {

    private static ChatManager instance;
    private HomingPigeonChatListener chatListener;

    //untuk define socket listener
    HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {

        @Override
        public void onNewMessage(String eventName, String emitData) {
            switch (eventName) {
                case DefaultConstant.ConnectionEvent.kEventOpenRoom:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketCloseRoom:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketNewMessage:
                    EmitModel<MessageModel> tempObject = Utils.getInstance()
                            .fromJSON(new TypeReference<EmitModel<MessageModel>>() {}, emitData);
                    if (null != chatListener) chatListener.onSendTextMessage(tempObject.getData().getMessage());
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUpdateMessage:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketDeleteMessage:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketOpenMessage:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketStartTyping:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketStopTyping:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketAuthentication:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUserOnline:
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUserOffline:
                    break;
            }
        }
    };

    public static ChatManager getInstance() {
        if (null == instance) {
            instance = new ChatManager();
        }
        return instance;
    }

    public ChatManager() {
        ConnectionManager.getInstance().setSocketListener(socketListener);
    }

    public void setChatListener(HomingPigeonChatListener chatListener) {
        if (null != this.chatListener)
            this.chatListener = null;
        this.chatListener = chatListener;
    }

    public void sendTextMessage(String messageText, String roomID, String userID) {
        RoomModel roomModel = RoomModel.Builder(roomID);
        UserModel userModel = UserModel.Builder(userID);
        MessageModel messageModel = MessageModel.Builder(messageText, roomModel
                , DefaultConstant.MessageType.TYPE_TEXT, System.currentTimeMillis() / 1000, userModel);
        EmitModel<MessageModel> emitModel = new EmitModel<>(DefaultConstant.ConnectionEvent.kSocketNewMessage,
                messageModel);
        sendMessage(Utils.getInstance().toJsonString(emitModel));
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().sendEmit(message);
    }
}
