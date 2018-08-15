package com.moselo.HomingPigeon.Manager;

import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;

public class ChatManager {

    private static ChatManager instance;
    private HomingPigeonSocketListener listener;

    public static ChatManager getInstance() {
        if (null == instance) {
            instance = new ChatManager();
        }
        return instance;
    }

    public ChatManager() {
    }

    public void setSocketListener(HomingPigeonSocketListener listener) {
        if (null != this.listener)
            this.listener = null;
        this.listener = listener;
    }

    public void sendMessageText(String messageText) {
        if (ConnectionManager.getInstance().isSocketOpen())
            ConnectionManager.getInstance().sendMessage(messageText);
    }
}
