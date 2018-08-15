package com.moselo.HomingPigeon.Manager;

import android.content.Context;

import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatManager {

    private static ChatManager instance;
    private HomingPigeonChatListener chatListener;
    private Context appContext;

    //untuk define socket listener
    HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {

        @Override
        public void onNewMessage(String eventName, JSONObject emitData) {
            switch (eventName){
                case DefaultConstant.ConnectionEvent.kEventOpenRoom :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketCloseRoom :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketSendMessage :
                    try {
                        chatListener.onSendTextMessage(emitData.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUpdateMessage :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketDeleteMessage :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketOpenMessage :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketStartTyping :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketStopTyping :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketAuthentication :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUserOnline :
                    break;
                case DefaultConstant.ConnectionEvent.kSocketUserOffline :
                    break;
            }
        }
    };

    public static ChatManager getInstance(Context appContext) {
        if (null == instance) {
            instance = new ChatManager(appContext);
        }
        return instance;
    }

    public ChatManager(Context appContext) {
        this.appContext = appContext;
        ConnectionManager.getInstance(appContext).setSocketListener(socketListener);
    }

    public void setChatListener(HomingPigeonChatListener chatListener) {
        if (null != this.chatListener)
            this.chatListener = null;
        this.chatListener = chatListener;
    }

    public void sendMessageText(String messageText) {
        ConnectionManager.getInstance(appContext).sendEmit(messageText);
    }
}
