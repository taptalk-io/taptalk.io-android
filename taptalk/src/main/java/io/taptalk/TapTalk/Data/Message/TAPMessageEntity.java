package io.taptalk.TapTalk.Data.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPMessageTargetModel;

@Entity(tableName = "Message_Table", indices = @Index(value = "roomID"))
public class TAPMessageEntity {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "localID")
    private String localID;
    @Nullable
    @ColumnInfo(name = "messageID")
    private String messageID;
    @Nullable
    @ColumnInfo(name = "filterID")
    private String filterID;
    @ColumnInfo(name = "body") private String body;
    @ColumnInfo(name = "recipientID") private String recipientID;
    @ColumnInfo(name = "type") private Integer type;
    @ColumnInfo(name = "created") private Long created;
    @Nullable
    @ColumnInfo(name = "data")
    private String data;
    @Nullable
    @ColumnInfo(name = "quote")
    private String quote;
    @Nullable
    @ColumnInfo(name = "replyTo")
    private String replyTo;
    @Nullable
    @ColumnInfo(name = "forwardFrom")
    private String forwardFrom;
    @Nullable
    @ColumnInfo(name = "updated")
    private Long updated;
    @Nullable
    @ColumnInfo(name = "deleted")
    private Long deleted;
    @Nullable
    @ColumnInfo(name = "isRead")
    private Boolean isRead;
    @Nullable
    @ColumnInfo(name = "isDelivered")
    private Boolean isDelivered;
    @Nullable
    @ColumnInfo(name = "isHidden")
    private Boolean isHidden;
    @Nullable
    @ColumnInfo(name = "isDeleted")
    private Boolean isDeleted;
    @Nullable
    @ColumnInfo(name = "isSending")
    private Boolean isSending;
    @Nullable
    @ColumnInfo(name = "isFailedSend")
    private Boolean isFailedSend;
    @ColumnInfo(name = "roomID") private String roomID;
    @Nullable
    @ColumnInfo(name = "xcRoomID")
    private String xcRoomID;
    @Nullable
    @ColumnInfo(name = "roomName")
    private String roomName;
    @Nullable
    @ColumnInfo(name = "roomColor")
    private String roomColor;
    @Nullable
    @ColumnInfo(name = "roomImage")
    private String roomImage;
    @Nullable
    @ColumnInfo(name = "roomType")
    private Integer roomType;
    @Nullable
    @ColumnInfo(name = "isRoomLocked")
    private Boolean isRoomLocked;
    @Nullable
    @ColumnInfo(name = "isRoomDeleted")
    private Boolean isRoomDeleted;
    @Nullable
    @ColumnInfo(name = "roomLockedTimestamp")
    private Long roomLockedTimestamp;
    @Nullable
    @ColumnInfo(name = "roomDeletedTimestamp")
    private Long roomDeletedTimestamp;
    @ColumnInfo(name = "userID") private String userID;
    @ColumnInfo(name = "xcUserID") private String xcUserID;
    @ColumnInfo(name = "userFullName") private String userFullName;
    @Nullable
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "userImage") private String userImage;
    @Nullable
    @ColumnInfo(name = "userEmail")
    private String userEmail;
    @Nullable
    @ColumnInfo(name = "userPhone")
    private String userPhone;
    @Nullable
    @ColumnInfo(name = "userRole")
    private String userRole;
    @Nullable
    @ColumnInfo(name = "lastLogin")
    private Long lastLogin;
    @Nullable
    @ColumnInfo(name = "lastActivity")
    private Long lastActivity;
    @Nullable
    @ColumnInfo(name = "requireChangePassword")
    private Boolean requireChangePassword;
    @Nullable
    @ColumnInfo(name = "userCreated")
    private Long userCreated;
    @Nullable
    @ColumnInfo(name = "userUpdated")
    private Long userUpdated;
    @Nullable
    @ColumnInfo(name = "userDeleted")
    private Long userDeleted;
    @Nullable
    @ColumnInfo(name = "action")
    private String action;
    @Nullable
    @Embedded
    private TAPMessageTargetModel target;

    public TAPMessageEntity(@Nullable String messageID, @NonNull String localID, @Nullable String filterID,
                            String body, String recipientID, Integer type, Long created,
                            @Nullable String data, @Nullable String quote, @Nullable String replyTo,
                            @Nullable String forwardFrom, @Nullable Long updated, @Nullable Long deleted,
                            @Nullable Boolean isRead, @Nullable Boolean isDelivered,
                            @Nullable Boolean isHidden, @Nullable Boolean isDeleted,
                            @Nullable Boolean isSending, @Nullable Boolean isFailedSend, String roomID,
                            @Nullable String xcRoomID, @Nullable String roomName, @Nullable String roomColor,
                            @Nullable Integer roomType, @Nullable String roomImage,
                            @Nullable Boolean isRoomLocked, @Nullable Boolean isRoomDeleted,
                            @Nullable Long roomLockedTimestamp, @Nullable Long roomDeletedTimestamp,
                            String userID, String xcUserID, String userFullName,
                            @Nullable String username, String userImage, @Nullable String userEmail,
                            @Nullable String userPhone, @Nullable String userRole, @Nullable Long lastLogin,
                            @Nullable Long lastActivity, @Nullable Boolean requireChangePassword,
                            @Nullable Long userCreated, @Nullable Long userUpdated, @Nullable Long userDeleted,
                            @Nullable String action, @Nullable TAPMessageTargetModel target) {
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
        this.deleted = deleted;
        this.isRead = isRead;
        this.isDelivered = isDelivered;
        this.isHidden = isHidden;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.roomID = roomID;
        this.xcRoomID = xcRoomID;
        this.roomName = roomName;
        this.roomColor = roomColor;
        this.roomType = roomType;
        this.roomImage = roomImage;
        this.isRoomLocked = isRoomLocked;
        this.isRoomDeleted = isRoomDeleted;
        this.roomLockedTimestamp = roomLockedTimestamp;
        this.roomDeletedTimestamp = roomDeletedTimestamp;
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
        this.action = action;
        this.target = target;
    }

    public static TAPMessageEntity fromMessageModel(TAPMessageModel messageModel) {
        return new TAPMessageEntity(
                messageModel.getMessageID(), messageModel.getLocalID(), messageModel.getFilterID(), messageModel.getBody(),
                messageModel.getRecipientID(), messageModel.getType(), messageModel.getCreated(),
                null == messageModel.getData() ? null : TAPUtils.toJsonString(messageModel.getData()),
                null == messageModel.getQuote() ? null : TAPUtils.toJsonString(messageModel.getQuote()),
                null == messageModel.getReplyTo() ? null : TAPUtils.toJsonString(messageModel.getReplyTo()),
                null == messageModel.getForwardFrom() ? null : TAPUtils.toJsonString(messageModel.getForwardFrom()),
                messageModel.getUpdated(), messageModel.getDeleted(),
                messageModel.getIsRead(), messageModel.getDelivered(), messageModel.getHidden(), messageModel.getIsDeleted(),
                messageModel.getSending(), messageModel.getFailedSend(), messageModel.getRoom().getRoomID(),
                messageModel.getRoom().getXcRoomID(), messageModel.getRoom().getRoomName(),
                messageModel.getRoom().getRoomColor(), messageModel.getRoom().getRoomType(),
                TAPUtils.toJsonString(messageModel.getRoom().getRoomImage()),
                messageModel.getRoom().isLocked(), messageModel.getRoom().isRoomDeleted(),
                messageModel.getRoom().getLockedTimestamp(), messageModel.getRoom().getDeletedTimestamp(),
                messageModel.getUser().getUserID(), messageModel.getUser().getXcUserID(),
                messageModel.getUser().getName(), messageModel.getUser().getUsername(),
                TAPUtils.toJsonString(messageModel.getUser().getAvatarURL()),
                messageModel.getUser().getEmail(), messageModel.getUser().getPhoneNumber(),
                TAPUtils.toJsonString(messageModel.getUser().getUserRole()),
                messageModel.getUser().getLastLogin(), messageModel.getUser().getLastActivity(),
                messageModel.getUser().getRequireChangePassword(),
                messageModel.getUser().getCreated(), messageModel.getUser().getUpdated(),
                messageModel.getUser().getDeleted(), messageModel.getAction(), messageModel.getTarget()
        );
    }

    @NonNull
    public String getLocalID() {
        return localID;
    }

    public void setLocalID(@NonNull String localID) {
        this.localID = localID;
    }

    @Nullable
    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(@Nullable String messageID) {
        this.messageID = messageID;
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
    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(@Nullable Long deleted) {
        deleted = deleted;
    }

    @Nullable
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(@Nullable Boolean isDeleted) {
        isDeleted = isDeleted;
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
    public String getXcRoomID() {
        return xcRoomID;
    }

    public void setXcRoomID(@Nullable String xcRoomID) {
        this.xcRoomID = xcRoomID;
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
    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(@Nullable String roomImage) {
        this.roomImage = roomImage;
    }

    @Nullable
    public Integer getRoomType() {
        return roomType;
    }

    public void setRoomType(@Nullable Integer roomType) {
        this.roomType = roomType;
    }

    @Nullable
    public Boolean getRoomLocked() {
        return isRoomLocked;
    }

    public void setRoomLocked(@Nullable Boolean roomLocked) {
        isRoomLocked = roomLocked;
    }

    @Nullable
    public Boolean getRoomDeleted() {
        return isRoomDeleted;
    }

    public void setRoomDeleted(@Nullable Boolean roomDeleted) {
        isRoomDeleted = roomDeleted;
    }

    @Nullable
    public Long getRoomDeletedTimestamp() {
        return roomDeletedTimestamp;
    }

    public void setRoomDeletedTimestamp(@Nullable Long roomDeletedTimestamp) {
        this.roomDeletedTimestamp = roomDeletedTimestamp;
    }

    @Nullable
    public Long getRoomLockedTimestamp() {
        return roomLockedTimestamp;
    }

    public void setRoomLockedTimestamp(@Nullable Long roomLockedTimestamp) {
        this.roomLockedTimestamp = roomLockedTimestamp;
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

    @Nullable
    public String getAction() {
        return action;
    }

    public void setAction(@Nullable String action) {
        this.action = action;
    }

    @Nullable
    public TAPMessageTargetModel getTarget() {
        return target;
    }

    public void setTarget(@Nullable TAPMessageTargetModel target) {
        this.target = target;
    }

    @Nullable
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(@Nullable Boolean isRead) {
        this.isRead = isRead;
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
}
