package io.taptalk.TapTalk.Data.Message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "Message_Table", indices = @Index(value = "roomID"))
public class TAPMessageEntity {

    @Nullable @ColumnInfo(name = "messageID") private String messageID;
    @PrimaryKey() @NonNull @ColumnInfo(name = "localID") private String localID;
    @Nullable @ColumnInfo(name = "filterID") private String filterID;
    @ColumnInfo(name = "body") private String body;
    @ColumnInfo(name = "recipientID") private String recipientID;
    @ColumnInfo(name = "type") private Integer type;
    @ColumnInfo(name = "created") private Long created;
    @Nullable @ColumnInfo(name = "data") private String data;
    @Nullable @ColumnInfo(name = "quote") private String quote;
    @Nullable @ColumnInfo(name = "replyTo") private String replyTo;
    @Nullable @ColumnInfo(name = "forwardFrom") private String forwardFrom;
    @Nullable @ColumnInfo(name = "updated") private Long updated;
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
    @Nullable @ColumnInfo(name = "lastActivity") private Long lastActivity;
    @Nullable @ColumnInfo(name = "requireChangePassword") private Boolean requireChangePassword;
    @Nullable @ColumnInfo(name = "userCreated") private Long userCreated;
    @Nullable @ColumnInfo(name = "userUpdated") private Long userUpdated;
    @Nullable @ColumnInfo(name = "userDeleted") private Long userDeleted;

    public TAPMessageEntity(@Nullable String messageID, @NonNull String localID, @Nullable String filterID, String body,
                            String recipientID, Integer type, Long created,
                            @Nullable String data, @Nullable String quote, @Nullable String replyTo,
                            @Nullable String forwardFrom, @Nullable Long updated,
                            @Nullable Boolean isRead, @Nullable Boolean isDelivered,
                            @Nullable Boolean isHidden, @Nullable Boolean isDeleted,
                            @Nullable Boolean isSending, @Nullable Boolean isFailedSend, String roomID,
                            @Nullable String roomName, @Nullable String roomColor, @Nullable Integer roomType,
                            @Nullable String roomImage, String userID, String xcUserID, String userFullName,
                            @Nullable String username, String userImage, @Nullable String userEmail,
                            @Nullable String userPhone, @Nullable String userRole, @Nullable Long lastLogin,
                            @Nullable Long lastActivity, @Nullable Boolean requireChangePassword,
                            @Nullable Long userCreated, @Nullable Long userUpdated, @Nullable Long userDeleted) {
        this.messageID = messageID;
        this.localID = localID;
        this.filterID = filterID;
        this.body = body;
        this.recipientID = recipientID;
        this.type = type;
        this.created = created;
        this.data = data;
        this.quote = quote;
        this.replyTo = replyTo;
        this.forwardFrom = forwardFrom;
        this.updated = updated;
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
        this.lastActivity = lastActivity;
        this.requireChangePassword = requireChangePassword;
        this.userCreated = userCreated;
        this.userUpdated = userUpdated;
        this.userDeleted = userDeleted;
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

    @Nullable
    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(@Nullable String filterID) {
        this.filterID = filterID;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Nullable
    public String getData() {
        return data;
    }

    public void setData(@Nullable String data) {
        this.data = data;
    }

    @Nullable
    public String getQuote() {
        return quote;
    }

    public void setQuote(@Nullable String quote) {
        this.quote = quote;
    }

    @Nullable
    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(@Nullable String replyTo) {
        this.replyTo = replyTo;
    }

    @Nullable
    public String getForwardFrom() {
        return forwardFrom;
    }

    public void setForwardFrom(@Nullable String forwardFrom) {
        this.forwardFrom = forwardFrom;
    }

    @Nullable
    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(@Nullable Long updated) {
        this.updated = updated;
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
    public Long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(@Nullable Long lastActivity) {
        this.lastActivity = lastActivity;
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

    @Nullable
    public Long getUserDeleted() {
        return userDeleted;
    }

    public void setUserDeleted(@Nullable Long userDeleted) {
        this.userDeleted = userDeleted;
    }
}
