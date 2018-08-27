package com.moselo.HomingPigeon.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moselo.HomingPigeon.Helper.AESCrypt;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.EncryptorManager;

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

    @Nullable @JsonProperty("messageID") private String messageID;
    @NonNull @JsonProperty("localID") private String localID;
    @JsonProperty("room") private RoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("message") private String message;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private UserModel user;
    @Nullable @JsonProperty("deliveredTo") private List<Object> deliveredTo;
    @Nullable @JsonProperty("seenBy") private List<SeenByModel> seenBy;
    @Nullable @JsonProperty("deleted") private Integer deleted;
    @Nullable @JsonProperty("isSending") private Integer isSending;
    @Nullable @JsonProperty("isFailedSend") private Integer isFailedSend;

    public MessageModel(@NonNull String localID, String message, RoomModel room, int type, long created, UserModel user, int isSending) {
        this.localID = localID;
        this.message = message;
        this.room = room;
        this.type = type;
        this.created = created;
        this.user = user;
        this.isSending = isSending;
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, RoomModel room, int type, long created, UserModel user){
        String localID = Utils.getInstance().generateRandomString(32);
        return new MessageModel(localID, message, room, type, created, user, 1);
    }

    public static MessageModel BuilderEncrypt(String message, RoomModel room, int type, long created, UserModel user) throws GeneralSecurityException {
        String localID = Utils.getInstance().generateRandomString(32);
        return new MessageModel(localID, EncryptorManager.getInstance().encrypt(message, localID), room, type, created, user, 1);
    }

    public static MessageModel BuilderDecrypt(String localID, String message, RoomModel room, int type, long created, UserModel user) throws GeneralSecurityException {
        return new MessageModel(localID, EncryptorManager.getInstance().decrypt(message, localID), room, type, created, user, 0);
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

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    @Nullable
    public List<Object> getDeliveredTo() {
        return deliveredTo;
    }

    public void setDeliveredTo(@Nullable List<Object> deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    @Nullable
    public List<SeenByModel> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(@Nullable List<SeenByModel> seenBy) {
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
