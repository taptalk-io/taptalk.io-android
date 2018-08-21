package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moselo.HomingPigeon.Helper.AESCrypt;
import com.moselo.HomingPigeon.Helper.Utils;

import java.security.GeneralSecurityException;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "message",
        "messageID",
        "localID",
        "room",
        "type",
        "created",
        "user",
        "deliveredTo",
        "seenBy",
        "deleted",
        "isSending",
        "isFailedSend",
})
public class MessageModel {

    @JsonProperty("message") private String message;
    @Nullable @JsonProperty("messageID") private String messageID;
    @Nullable @JsonProperty("localID") private String localID;
    @JsonProperty("room") private RoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("created") private long created;
    @JsonProperty("user") private UserModel user;
    @Nullable @JsonProperty("deliveredTo") private List<Object> deliveredTo;
    @Nullable @JsonProperty("seenBy") private List<SeenByModel> seenBy;
    @Nullable @JsonProperty("deleted") private Integer deleted;
    @Nullable @JsonProperty("isSending") private int isSending;
    @Nullable @JsonProperty("isFailedSend") private int isFailedSend;

    public MessageModel(String message, RoomModel room, int type, long created, UserModel user) {
        this.message = message;
        localID = Utils.getInstance().generateRandomString(32);
        this.room = room;
        this.type = type;
        this.created = created;
        this.user = user;
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, RoomModel room, int type, long created, UserModel user){
        return new MessageModel(message, room, type, created, user);
    }

    public static MessageModel BuilderEncrypt(String message, RoomModel room, int type, long created, UserModel user) throws GeneralSecurityException {
        Log.e(MessageModel.class.getSimpleName(), message );
        return new MessageModel(AESCrypt.encrypt("homingpigeon", message ), room, type, created, user);
    }

    @Nullable @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @JsonProperty("messageID")
    public String getMessageID() {
        return messageID;
    }

    @JsonProperty("messageID")
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    @Nullable @JsonProperty("localID")
    public String getLocalID() {
        return localID;
    }

    @JsonProperty("localID")
    public void setLocalID(@Nullable String localID) {
        this.localID = localID;
    }

    @JsonProperty("type")
    public int getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = type;
    }

    @JsonProperty("created")
    public long getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(long created) {
        this.created = created;
    }

    @Nullable @JsonProperty("deliveredTo")
    public List<Object> getDeliveredTo() {
        return deliveredTo;
    }

    @JsonProperty("deliveredTo")
    public void setDeliveredTo(@Nullable List<Object> deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    @Nullable @JsonProperty("deleted")
    public Integer getDeleted() {
        return deleted;
    }

    @JsonProperty("deleted")
    public void setDeleted(@Nullable Integer deleted) {
        this.deleted = deleted;
    }

    @Nullable @JsonProperty("isSending")
    public int getIsSending() {
        return isSending;
    }

    @JsonProperty("isSending")
    public void setIsSending(@Nullable int isSending) {
        this.isSending = isSending;
    }

    @Nullable @JsonProperty("isFailedSend")
    public int getIsFailedSend() {
        return isFailedSend;
    }

    @JsonProperty("isFailedSend")
    public void setIsFailedSend(@Nullable int isFailedSend) {
        this.isFailedSend = isFailedSend;
    }

    @JsonProperty("room")
    public RoomModel getRoom() {
        return room;
    }

    @JsonProperty("room")
    public void setRoom(RoomModel room) {
        this.room = room;
    }

    @JsonProperty("user")
    public UserModel getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(UserModel user) {
        this.user = user;
    }

    @Nullable @JsonProperty("seenBy")
    public List<SeenByModel> getSeenBy() {
        return seenBy;
    }

    @JsonProperty("seenBy")
    public void setSeenBy(@Nullable List<SeenByModel> seenBy) {
        this.seenBy = seenBy;
    }
}
