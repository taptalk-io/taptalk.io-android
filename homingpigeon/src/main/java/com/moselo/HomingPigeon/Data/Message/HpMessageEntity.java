package com.moselo.HomingPigeon.Data.Message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Helper.HpUtils;

@Entity(tableName = "Message_Table", indices = @Index(value = "roomID"))
public class HpMessageEntity {

    @Nullable @ColumnInfo(name = "messageID") private String messageID;
    @PrimaryKey() @NonNull @ColumnInfo(name = "localID") private String localID;
    @ColumnInfo(name = "body") private String body;
    @ColumnInfo(name = "recipientID") private String recipientID;
    @ColumnInfo(name = "messageType") private Integer messageType;
    @ColumnInfo(name = "messageCreated") private Long messageCreated;
    @Nullable @ColumnInfo(name = "messageUpdated") private Long messageUpdated;
    @Nullable @ColumnInfo(name = "hasRead") private Boolean hasRead;
    @Nullable @ColumnInfo(name = "isRead") private Boolean isRead;
    @Nullable @ColumnInfo(name = "isDelivered") private Boolean isDelivered;
    @Nullable @ColumnInfo(name = "isHidden") private Boolean isHidden;
    @Nullable @ColumnInfo(name = "isDeleted") private Boolean isDeleted;
    @Nullable @ColumnInfo(name = "isSending") private Boolean isSending;
    @Nullable @ColumnInfo(name = "isFailedSend") private Boolean isFailedSend;
    @ColumnInfo(name = "roomID") private String roomID;
    @Nullable @ColumnInfo(name = "roomName") private String roomName;
    @Nullable @ColumnInfo(name = "roomColor") private String roomColor;
    @Nullable @ColumnInfo(name = "roomImage") private String roomImage;
    @Nullable @ColumnInfo(name = "roomType") private Integer roomType;
    @ColumnInfo(name = "userID") private String userID;
    @ColumnInfo(name = "xcUserID") private String xcUserID;
    @ColumnInfo(name = "userFullName") private String userFullName;
    @Nullable @ColumnInfo(name = "username") private String username;
    @ColumnInfo(name = "userImage") private String userImage;
    @Nullable @ColumnInfo(name = "userEmail") private String userEmail;
    @Nullable @ColumnInfo(name = "userPhone") private String userPhone;
    @Nullable @ColumnInfo(name = "userRole") private String userRole;
    @Nullable @ColumnInfo(name = "lastLogin") private Long lastLogin;
    @Nullable @ColumnInfo(name = "requireChangePassword") private Boolean requireChangePassword;
    @Nullable @ColumnInfo(name = "userCreated") private Long userCreated;
    @Nullable @ColumnInfo(name = "userUpdated") private Long userUpdated;

    public HpMessageEntity(@Nullable String messageID, @NonNull String localID, String body,
                           String recipientID, Integer messageType, Long messageCreated,
                           @Nullable Long messageUpdated, @Nullable Boolean hasRead,
                           @Nullable Boolean isRead, @Nullable Boolean isDelivered,
                           @Nullable Boolean isHidden, @Nullable Boolean isDeleted,
                           @Nullable Boolean isSending, @Nullable Boolean isFailedSend, String roomID,
                           @Nullable String roomName, @Nullable String roomColor, @Nullable Integer roomType,
                           @Nullable String roomImage, String userID, String xcUserID, String userFullName,
                           @Nullable String username, String userImage, @Nullable String userEmail,
                           @Nullable String userPhone, @Nullable String userRole, @Nullable Long lastLogin,
                           @Nullable Boolean requireChangePassword, @Nullable Long userCreated,
                           @Nullable Long userUpdated) {
        this.messageID = messageID;
        this.localID = localID;
        this.body = body;
        this.recipientID = recipientID;
        this.messageType = messageType;
        this.messageCreated = messageCreated;
        this.messageUpdated = messageUpdated;
        this.hasRead = hasRead;
        this.isRead = isRead;
        this.isDelivered = isDelivered;
        this.isHidden = isHidden;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomColor = roomColor;
        this.roomType = roomType;
        this.roomImage = roomImage;
        this.userID = userID;
        this.xcUserID = xcUserID;
        this.userFullName = userFullName;
        this.username = username;
        this.userImage = userImage;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.lastLogin = lastLogin;
        this.requireChangePassword = requireChangePassword;
        this.userCreated = userCreated;
        this.userUpdated = userUpdated;
    }

    @Nullable
    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(@Nullable String messageID) {
        this.messageID = messageID;
    }

    @NonNull
    public String getLocalID() {
        return localID;
    }

    public void setLocalID(@NonNull String localID) {
        this.localID = localID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Long getMessageCreated() {
        return messageCreated;
    }

    public void setMessageCreated(Long messageCreated) {
        this.messageCreated = messageCreated;
    }

    @Nullable
    public Long getMessageUpdated() {
        return messageUpdated;
    }

    public void setMessageUpdated(@Nullable Long messageUpdated) {
        this.messageUpdated = messageUpdated;
    }

    @Nullable
    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(@Nullable Boolean hasRead) {
        this.hasRead = hasRead;
    }

    @Nullable
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(@Nullable Boolean read) {
        isRead = read;
    }

    @Nullable
    public Boolean getDelivered() {
        return isDelivered;
    }

    public void setDelivered(@Nullable Boolean delivered) {
        isDelivered = delivered;
    }

    @Nullable
    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(@Nullable Boolean hidden) {
        isHidden = hidden;
    }

    @Nullable
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(@Nullable Boolean deleted) {
        isDeleted = deleted;
    }

    @Nullable
    public Boolean getSending() {
        return isSending;
    }

    public void setSending(@Nullable Boolean sending) {
        isSending = sending;
    }

    @Nullable
    public Boolean getFailedSend() {
        return isFailedSend;
    }

    public void setFailedSend(@Nullable Boolean failedSend) {
        isFailedSend = failedSend;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Nullable
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(@Nullable String roomName) {
        this.roomName = roomName;
    }

    @Nullable
    public String getRoomColor() {
        return roomColor;
    }

    public void setRoomColor(@Nullable String roomColor) {
        this.roomColor = roomColor;
    }

    @Nullable
    public Integer getRoomType() {
        return roomType;
    }

    public void setRoomType(@Nullable Integer roomType) {
        this.roomType = roomType;
    }

    @Nullable
    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(@Nullable String roomImage) {
        this.roomImage = roomImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }

    @Nullable
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(@Nullable String userPhone) {
        this.userPhone = userPhone;
    }

    @Nullable
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(@Nullable String userRole) {
        this.userRole = userRole;
    }

    @Nullable
    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(@Nullable Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Nullable
    public Boolean getRequireChangePassword() {
        return requireChangePassword;
    }

    public void setRequireChangePassword(@Nullable Boolean requireChangePassword) {
        this.requireChangePassword = requireChangePassword;
    }

    @Nullable
    public Long getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(@Nullable Long userCreated) {
        this.userCreated = userCreated;
    }

    @Nullable
    public Long getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(@Nullable Long userUpdated) {
        this.userUpdated = userUpdated;
    }
}
