package io.taptalk.TapTalk.Model;

import java.util.LinkedHashMap;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;

public class TAPRoomListModel {
    private TAPMessageModel lastMessage;
    private String lastMessageTimestamp;
    private LinkedHashMap<String, TAPUserModel> typingUsers;
    private int unreadCount;
    private int unreadMentions;
    private int defaultAvatarBackgroundColor; // Save default color in model to prevent lag on bind
    private String title;
    public enum Type { SELECTABLE_ROOM, SELECTABLE_CONTACT, SECTION }
    private Type type;

    public TAPRoomListModel(TAPMessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = TAPTimeFormatter.durationString(lastMessage.getCreated());

        TAPRoomModel room = lastMessage.getRoom();
        if (null == room.getRoomImage() || room.getRoomImage().getThumbnail().isEmpty()) {
            if (null != TapTalk.appContext) {
                defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalk.appContext, room.getRoomName());
            }
        }
    }

    public TAPRoomListModel(Type type) {
        this.type = type;
        this.lastMessage = new TAPMessageModel();
        TAPRoomModel room = lastMessage.getRoom();
        if (null == room) {
            if (null != TapTalk.appContext) {
                defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalk.appContext, "");
            }
        } else if (null == room.getRoomImage() || room.getRoomImage().getThumbnail().isEmpty()) {
            if (null != TapTalk.appContext) {
                defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalk.appContext, room.getRoomName());
            }
        }
    }

    public static TAPRoomListModel buildWithLastMessage(TAPMessageModel lastMessage) {
        TAPRoomListModel roomListModel = new TAPRoomListModel();
        roomListModel.setLastMessage(lastMessage);
        roomListModel.setLastMessageTimestamp(TAPTimeFormatter.durationString(lastMessage.getCreated()));

        TAPRoomModel room = lastMessage.getRoom();
        if (null == room.getRoomImage() || room.getRoomImage().getThumbnail().isEmpty() && null != TapTalk.appContext) {
            roomListModel.setDefaultAvatarBackgroundColor(TAPUtils.getRandomColor(TapTalk.appContext, room.getRoomName()));
        }

        return roomListModel;
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
        setLastMessageTimestamp(TAPTimeFormatter.durationString(lastMessage.getCreated()));
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadMentions() {
        return unreadMentions;
    }

    public void setUnreadMentions(int unreadMentions) {
        this.unreadMentions = unreadMentions;
    }

    public int getDefaultAvatarBackgroundColor() {
        return defaultAvatarBackgroundColor;
    }

    public void setDefaultAvatarBackgroundColor(int defaultAvatarBackgroundColor) {
        this.defaultAvatarBackgroundColor = defaultAvatarBackgroundColor;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
