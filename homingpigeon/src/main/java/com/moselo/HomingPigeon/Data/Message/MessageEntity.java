package com.moselo.HomingPigeon.Data.Message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "Message_Table", indices = @Index(value = "RoomID"))
public class MessageEntity {

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
    @Nullable @ColumnInfo(name = "deliveredTo") private String deliveredTo;
    @Nullable @ColumnInfo(name = "seenBy") private String seenBy;
    @Nullable @ColumnInfo(name = "isDeleted") private Boolean isDeleted;
    @Nullable @ColumnInfo(name = "isSending") private Boolean isSending;
    @Nullable @ColumnInfo(name = "isFailedSend") private Boolean isFailedSend;
    @Nullable @ColumnInfo(name = "updated") private Long updated;

    @Ignore
    public MessageEntity(@Nullable String messageID, @NonNull String localID, String roomID,
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

    @Ignore
    public MessageEntity(@Nullable String messageID, @NonNull String localID,
                         String room, Integer roomType, Integer type, String message, long created, String user, String recipientID,
                         @Nullable String deliveredTo, @Nullable String seenBy, @Nullable Boolean isDeleted,
                         @Nullable Boolean isSending, @Nullable Boolean isFailedSend, @Nullable Long updated) {
        this.messageID = messageID;
        this.localID = localID;
        this.roomID = room;
        this.roomType = roomType;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
        this.recipientID = recipientID;
        this.deliveredTo = deliveredTo;
        this.seenBy = seenBy;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.updated = updated;
    }

    public MessageEntity() {

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
    public String getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(@Nullable String deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    @Nullable
    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(@Nullable String seenBy) {
        this.seenBy = seenBy;
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
