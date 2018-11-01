package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Interface.HomingPigeonChatInterface;
import com.moselo.HomingPigeon.Model.HpMessageModel;

public abstract class HpChatListener implements HomingPigeonChatInterface {
    @Override public void onReceiveMessageInActiveRoom(HpMessageModel message) {}
    @Override public void onUpdateMessageInActiveRoom(HpMessageModel message) {}
    @Override public void onDeleteMessageInActiveRoom(HpMessageModel message) {}
    @Override public void onReceiveMessageInOtherRoom(HpMessageModel message) {}
    @Override public void onUpdateMessageInOtherRoom(HpMessageModel message) {}
    @Override public void onDeleteMessageInOtherRoom(HpMessageModel message) {}
    @Override public void onSendTextMessage(HpMessageModel message) {}
    @Override public void onSendImageMessage(HpMessageModel message) {}
    @Override public void onReplyMessage(HpMessageModel message) {}
    @Override public void onRetrySendMessage(HpMessageModel message) {}
    @Override public void onSendFailed(HpMessageModel message) {}
    @Override public void onMessageRead(HpMessageModel message) {}
    @Override public void onLayoutLoaded(HpMessageModel message) {}
    @Override public void onBubbleExpanded() {}
    @Override public void onOutsideClicked() {}
}
