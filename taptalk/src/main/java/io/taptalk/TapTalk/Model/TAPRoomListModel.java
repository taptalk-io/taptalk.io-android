package io.taptalk.TapTalk.Model;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;

public class TAPRoomListModel {
    private TAPMessageModel lastMessage;
    private String lastMessageTimestamp;
    private int unreadCount;
    private boolean isTyping;

    public TAPRoomListModel(TAPMessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = TAPTimeFormatter.getInstance().durationString(lastMessage.getCreated());
    }

    public static TAPRoomListModel buildWithLastMessage(TAPMessageModel lastMessage) {
        TAPRoomListModel roomModel = new TAPRoomListModel();
        roomModel.setLastMessage(lastMessage);
        roomModel.setLastMessageTimestamp(TAPTimeFormatter.getInstance().durationString(lastMessage.getCreated()));
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

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long timestamp) {
        setLastMessageTimestamp(TAPTimeFormatter.getInstance().durationString(lastMessage.getCreated()));
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }
}
