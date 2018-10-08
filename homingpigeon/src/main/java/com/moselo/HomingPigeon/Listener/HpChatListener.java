package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.HomingPigeonChatInterface;
import com.moselo.HomingPigeon.Model.MessageModel;

public abstract class HpChatListener implements HomingPigeonChatInterface {
    @Override public void onReceiveMessageInActiveRoom(MessageModel message) {}
    @Override public void onUpdateMessageInActiveRoom(MessageModel message) {}
    @Override public void onDeleteMessageInActiveRoom(MessageModel message) {}
    @Override public void onReceiveMessageInOtherRoom(MessageModel message) {}
    @Override public void onUpdateMessageInOtherRoom(MessageModel message) {}
    @Override public void onDeleteMessageInOtherRoom(MessageModel message) {}
    @Override public void onSendTextMessage(MessageModel message) {}
    @Override public void onRetrySendMessage(MessageModel message) {}
    @Override public void onSendFailed(MessageModel message) {}
    @Override public void onMessageClicked(MessageModel message, boolean isExpanded) {}
}
