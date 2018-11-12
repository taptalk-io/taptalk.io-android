package com.moselo.HomingPigeon.Model;

public class HpRoomListModel {
    private HpMessageModel lastMessage;
    private int unreadCount;

    public HpRoomListModel(HpMessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public static HpRoomListModel buildWithLastMessage(HpMessageModel lastMessage) {
        HpRoomListModel roomModel = new HpRoomListModel();
        roomModel.setLastMessage(lastMessage);
        return roomModel;
    }

    public HpRoomListModel() {
    }

    public HpMessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(HpMessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
