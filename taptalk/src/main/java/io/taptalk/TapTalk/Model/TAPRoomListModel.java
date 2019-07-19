package io.taptalk.TapTalk.Model;

import java.util.LinkedHashMap;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;

public class TAPRoomListModel {
    private TAPMessageModel lastMessage;
    private String lastMessageTimestamp;
    private int unreadCount;
    private LinkedHashMap<String, TAPUserModel> typingUsers;

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

    public LinkedHashMap<String, TAPUserModel> getTypingUsers() {
        return null == typingUsers ? typingUsers = new LinkedHashMap<>() : typingUsers;
    }

    public void removeTypingUser(String userID) {
        getTypingUsers().remove(userID);
    }

    public boolean removeAndCheckIfNeedToDismissTyping(String userID) {
        if (null == userID || getTypingUsers().containsKey(userID)) return false;

        removeTypingUser(userID);

        return 0 == getTypingUsers().size();
    }

    public void addTypingUsers(TAPUserModel typingUsers) {
        if (null != typingUsers) {
            getTypingUsers().put(typingUsers.getUserID(), typingUsers);
        }
    }

    public int getTypingUsersSize() {
        return getTypingUsers().size();
    }

    public int getTypingUsersSizeMinusOne() {
        return getTypingUsers().size() - 1;
    }

    public String getFirstTypingUserName() {
        if (0 >= getTypingUsersSize()) return "";

        TAPUserModel firstTypingUser = getTypingUsers().entrySet().iterator().next().getValue();
        return firstTypingUser.getName().split(" ")[0];
    }
}
