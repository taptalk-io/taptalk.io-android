package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;

public class TAPRoomListModel {
    private TAPMessageModel lastMessage;
    private String lastMessageTimestamp;
    private LinkedHashMap<String, TAPUserModel> typingUsers;
    private int numberOfUnreadMessages;
    private int numberOfUnreadMentions;
    private int defaultAvatarBackgroundColor; // Save default color in model to prevent lag on bind
    private String title;
    public enum Type { SELECTABLE_ROOM, SELECTABLE_CONTACT, SECTION }
    private Type type;
    private boolean isMarkedAsUnread;
    private boolean isMuted;
    private boolean isPinned;

    public TAPRoomListModel(TAPMessageModel lastMessage, int unreadCount) {
        this.lastMessage = lastMessage;
        this.numberOfUnreadMessages = unreadCount;
        this.lastMessageTimestamp = TAPTimeFormatter.durationString(lastMessage.getCreated());

        TAPRoomModel room = lastMessage.getRoom();
        if (null != TapTalk.appContext) {
            defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalk.appContext, room.getName());
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
        } else {
            if (null != TapTalk.appContext) {
                defaultAvatarBackgroundColor = TAPUtils.getRandomColor(TapTalk.appContext, room.getName());
            }
        }
    }

    public static TAPRoomListModel buildWithLastMessage(TAPMessageModel lastMessage) {
        TAPRoomListModel roomListModel = new TAPRoomListModel();
        roomListModel.setLastMessage(lastMessage);
        roomListModel.setLastMessageTimestamp(TAPTimeFormatter.durationString(lastMessage.getCreated()));

        TAPRoomModel room = lastMessage.getRoom();
        if (null != TapTalk.appContext) {
            roomListModel.setDefaultAvatarBackgroundColor(TAPUtils.getRandomColor(TapTalk.appContext, room.getName()));
        }

        return roomListModel;
    }

    public TAPRoomListModel() {
    }

    public static TAPRoomListModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPRoomListModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
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

    public void setLastMessageTimestampWithLong(long timestamp) {
        setLastMessageTimestamp(TAPTimeFormatter.durationString(timestamp));
    }

    /**
     * @deprecated use {@link #getNumberOfUnreadMessages()} instead.
     */
    @Deprecated
    public int getUnreadCount() {
        return numberOfUnreadMessages;
    }

    public int getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }

    /**
     * @deprecated use {@link #setNumberOfUnreadMessages(int)} instead.
     */
    @Deprecated
    public void setUnreadCount(int unreadCount) {
        this.numberOfUnreadMessages = unreadCount;
    }

    public void setNumberOfUnreadMessages(int numberOfUnreadMessages) {
        this.numberOfUnreadMessages = numberOfUnreadMessages;
    }

    /**
     * @deprecated use {@link #getNumberOfUnreadMentions()} instead.
     */
    @Deprecated
    public int getUnreadMentions() {
        return numberOfUnreadMentions;
    }

    public int getNumberOfUnreadMentions() {
        return numberOfUnreadMentions;
    }

    /**
     * @deprecated use {@link #setNumberOfUnreadMentions(int)} instead.
     */
    @Deprecated
    public void setUnreadMentions(int unreadMentions) {
        this.numberOfUnreadMentions = unreadMentions;
    }

    public void setNumberOfUnreadMentions(int numberOfUnreadMentions) {
        this.numberOfUnreadMentions = numberOfUnreadMentions;
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
        return firstTypingUser.getFullname().split(" ")[0];
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

    public boolean isMarkedAsUnread() {
        return isMarkedAsUnread;
    }

    public void setMarkedAsUnread(boolean markedAsUnread) {
        isMarkedAsUnread = markedAsUnread;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
