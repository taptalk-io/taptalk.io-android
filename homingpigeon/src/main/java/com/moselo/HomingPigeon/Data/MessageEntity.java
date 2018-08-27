package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "Message_Table")
public class MessageEntity {

    @Nullable @ColumnInfo(name = "MessageID", index = true) private String messageID;
    @PrimaryKey() @NonNull @ColumnInfo(name = "localID", index = true) private String localID;
    @ColumnInfo(name = "Room") private String room;
    @ColumnInfo(name = "MessageType") private Integer type;
    @ColumnInfo(name = "Message") private String message;
    @ColumnInfo(name = "Created") private Long created;
    @ColumnInfo(name = "User") private String user;
    @Nullable @ColumnInfo(name = "deliveredTo") private String deliveredTo;
    @Nullable @ColumnInfo(name = "seenBy") private String seenBy;
    @Nullable @ColumnInfo(name = "deleted") private Integer deleted;
    @Nullable @ColumnInfo(name = "isSending") private Integer isSending;
    @Nullable @ColumnInfo(name = "isFailedSend") private Integer isFailedSend;

    public MessageEntity(@Nullable String messageID, @NonNull String localID, String room, int type, String message, long created, String user) {
        this.messageID = messageID;
        this.localID = localID;
        this.room = room;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
    }

    @Ignore
    public MessageEntity(@Nullable String messageID, @NonNull String localID, String room, int type, String message, long created, String user, @Nullable String deliveredTo, @Nullable String seenBy, @Nullable Integer deleted, @Nullable Integer isSending, @Nullable Integer isFailedSend) {
        this.messageID = messageID;
        this.localID = localID;
        this.room = room;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
        this.deliveredTo = deliveredTo;
        this.seenBy = seenBy;
        this.deleted = deleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
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
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(@Nullable Integer deleted) {
        this.deleted = deleted;
    }

    @Nullable
    public Integer getIsSending() {
        return isSending;
    }

    public void setIsSending(@Nullable Integer isSending) {
        this.isSending = isSending;
    }

    @Nullable
    public Integer getIsFailedSend() {
        return isFailedSend;
    }

    public void setIsFailedSend(@Nullable Integer isFailedSend) {
        this.isFailedSend = isFailedSend;
    }
}
