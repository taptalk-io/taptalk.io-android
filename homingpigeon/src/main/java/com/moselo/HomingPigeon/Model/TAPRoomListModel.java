package com.moselo.HomingPigeon.Model;

public class TAPRoomListModel {
    private TAPMessageModel lastMessage;
    private int unreadCount;

    public TAPRoomListModel(TAPMessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public static TAPRoomListModel buildWithLastMessage(TAPMessageModel lastMessage) {
        TAPRoomListModel roomModel = new TAPRoomListModel();
        roomModel.setLastMessage(lastMessage);
        return roomModel;
    }

    public TAPRoomListModel() {
    }

    public TAPMessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TAPMessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
