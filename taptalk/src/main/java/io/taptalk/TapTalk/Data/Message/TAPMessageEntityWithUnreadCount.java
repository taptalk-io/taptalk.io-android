package io.taptalk.TapTalk.Data.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPMessageTargetModel;

public class TAPMessageEntityWithUnreadCount extends TAPMessageEntity {

    private int unreadCount;
    private int unreadMentionCount;

    public TAPMessageEntityWithUnreadCount(@Nullable String messageID, @NonNull String localID, @Nullable String filterID, String body, String recipientID, Integer type, Long created, @Nullable String data, @Nullable String quote, @Nullable String replyTo, @Nullable String forwardFrom, @Nullable Long updated, @Nullable Long deleted, @Nullable Boolean isRead, @Nullable Boolean isDelivered, @Nullable Boolean isHidden, @Nullable Boolean isDeleted, @Nullable Boolean isSending, @Nullable Boolean isFailedSend, String roomID, @Nullable String xcRoomID, @Nullable String roomName, @Nullable String roomColor, @Nullable Integer roomType, @Nullable String roomImage, @Nullable Boolean isRoomLocked, @Nullable Boolean isRoomDeleted, @Nullable Long roomLockedTimestamp, @Nullable Long roomDeletedTimestamp, String userID, String xcUserID, String userFullName, @Nullable String username, String userImage, @Nullable String userEmail, @Nullable String userPhone, @Nullable String userRole, @Nullable Long lastLogin, @Nullable Long lastActivity, @Nullable Boolean requireChangePassword, @Nullable Long userCreated, @Nullable Long userUpdated, @Nullable Long userDeleted, @Nullable String action, @Nullable TAPMessageTargetModel target) {
        super(messageID, localID, filterID, body, recipientID, type, created, data, quote, replyTo, forwardFrom, updated, deleted, isRead, isDelivered, isHidden, isDeleted, isSending, isFailedSend, roomID, xcRoomID, roomName, roomColor, roomType, roomImage, isRoomLocked, isRoomDeleted, roomLockedTimestamp, roomDeletedTimestamp, userID, xcUserID, userFullName, username, userImage, userEmail, userPhone, userRole, lastLogin, lastActivity, requireChangePassword, userCreated, userUpdated, userDeleted, action, target);
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadMentionCount() {
        return unreadMentionCount;
    }

    public void setUnreadMentionCount(int unreadMentionCount) {
        this.unreadMentionCount = unreadMentionCount;
    }
}
