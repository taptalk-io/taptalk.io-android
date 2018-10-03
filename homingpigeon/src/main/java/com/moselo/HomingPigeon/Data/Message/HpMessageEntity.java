package com.moselo.HomingPigeon.Data.Message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Helper.HpUtils;

@Entity(tableName = "Message_Table", indices = @Index(value = "RoomID"))
public class HpMessageEntity {

    @Nullable @ColumnInfo(name = "MessageID") private String messageID;
    @PrimaryKey() @NonNull @ColumnInfo(name = "localID") private String localID;
    @ColumnInfo(name = "RoomID") private String roomID;
    @Nullable @ColumnInfo(name = "RoomName") private String roomName;
    @Nullable @ColumnInfo(name = "RoomColor") private String roomColor;
    @Nullable @ColumnInfo(name = "RoomType") private Integer roomType;
    @Nullable @ColumnInfo(name = "RoomImage") private String roomImage;
    @ColumnInfo(name = "messageType") private Integer type;
    @ColumnInfo(name = "message") private String message;
    @ColumnInfo(name = "created") private Long created;
    @ColumnInfo(name = "user") private String user;
    @ColumnInfo(name = "recipientID") private String recipientID;
    @Nullable @ColumnInfo(name = "hasRead") private Boolean hasRead;
    @Nullable @ColumnInfo(name = "isRead") private Boolean isRead;
    @Nullable @ColumnInfo(name = "isDelivered") private Boolean isDelivered;
    @Nullable @ColumnInfo(name = "isHidden") private Boolean isHidden;
    @Nullable @ColumnInfo(name = "isDeleted") private Boolean isDeleted;
    @Nullable @ColumnInfo(name = "isSending") private Boolean isSending;
    @Nullable @ColumnInfo(name = "isFailedSend") private Boolean isFailedSend;
    @Nullable @ColumnInfo(name = "updated") private Long updated;

    @Ignore
    public HpMessageEntity(@Nullable String messageID, @NonNull String localID, String roomID,
                           Integer type, String message, long created, String user, String recipientID) {
        this.messageID = messageID;
        this.localID = localID;
        this.roomID = roomID;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
        this.recipientID = recipientID;
    }

    /**
     *  UPDATE THIS CONSTRUCTOR WHEN ADDING COLUMNS
     */
    @Ignore
    public HpMessageEntity(@Nullable String messageID, @NonNull String localID, String roomID,
                           @Nullable String roomName, @Nullable String roomColor,
                           @Nullable Integer roomType, @Nullable String roomImage, Integer type,
                           String message, Long created, String user, String recipientID,
                           @Nullable Boolean hasRead, @Nullable Boolean isRead,
                           @Nullable Boolean isDelivered, @Nullable Boolean isHidden,
                           @Nullable Boolean isDeleted, @Nullable Boolean isSending,
                           @Nullable Boolean isFailedSend, @Nullable Long updated) {
        this.messageID = messageID;
        if (localID.equals(""))
            this.localID =  HpUtils.getInstance().generateRandomString(32);
        else this.localID = localID;
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomColor = roomColor;
        this.roomType = roomType;
        this.roomImage = roomImage;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
        this.recipientID = recipientID;
        this.hasRead = hasRead;
        this.isRead = isRead;
        this.isDelivered = isDelivered;
        this.isHidden = isHidden;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.updated = updated;
    }

    public HpMessageEntity() {

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
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

    public void setDeleted(@Nullable Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Nullable
    public Boolean getIsSending() {
        return isSending;
    }

    public void setIsSending(@Nullable Boolean isSending) {
        this.isSending = isSending;
    }

    @Nullable
    public Boolean getIsFailedSend() {
        return isFailedSend;
    }

    public void setIsFailedSend(@Nullable Boolean isFailedSend) {
        this.isFailedSend = isFailedSend;
    }

    @Nullable
    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(@Nullable Long updated) {
        this.updated = updated;
    }
}
