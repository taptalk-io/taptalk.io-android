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
    private HomingPigeonChatListener chatListener;

    //untuk define socket listener
    HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {

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
                    if (null != chatListener) chatListener.onNewTextMessage(tempObject.getData());
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

    public void sendTextMessage(String messageText, String roomID, UserModel userModel) {
        RoomModel roomModel = RoomModel.Builder(roomID);
        MessageModel messageModel = MessageModel.Builder(messageText, roomModel
                , DefaultConstant.MessageType.TYPE_TEXT, System.currentTimeMillis(), userModel);
        EmitModel<MessageModel> emitModel = new EmitModel<>(kSocketNewMessage, messageModel);
        sendMessage(Utils.getInstance().toJsonString(emitModel));
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().sendEmit(message);
    }
}
