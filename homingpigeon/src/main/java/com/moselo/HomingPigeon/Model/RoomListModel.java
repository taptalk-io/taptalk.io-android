package com.moselo.HomingPigeon.Model;

public class RoomListModel {
    private MessageModel lastMessage;
    private int unreadCount;

    public RoomListModel(MessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public static RoomListModel buildWithLastMessage(MessageModel lastMessage) {
        RoomListModel roomModel = new RoomListModel();
        roomModel.setLastMessage(lastMessage);
        return roomModel;
    }

    public RoomListModel() {
    }

    public MessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
