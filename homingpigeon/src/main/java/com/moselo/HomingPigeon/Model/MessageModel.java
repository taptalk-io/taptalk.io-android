package com.moselo.HomingPigeon.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.EncryptorManager;

import java.security.GeneralSecurityException;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageModel {

    @Nullable @JsonProperty("messageID") private String messageID;
    @NonNull @JsonProperty("localID") private String localID;
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("type") private int type;
    @JsonProperty("message") private String message;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private UserModel user;
    @Nullable @JsonProperty("deliveredTo") private List<Object> deliveredTo;
    @Nullable @JsonProperty("seenBy") private List<SeenByModel> seenBy;
    @Nullable @JsonProperty("deleted") private Integer deleted;
    @Nullable @JsonProperty("isSending") private Integer isSending;
    @Nullable @JsonProperty("isFailedSend") private Integer isFailedSend;

    public MessageModel(String messageID, @NonNull String localID, String message, String roomID, Integer type, Long created, UserModel user, @Nullable Integer deleted, @Nullable Integer isSending, @Nullable Integer isFailedSend) {
        this.messageID = messageID;
        this.localID = localID;
        this.message = message;
        this.roomID = roomID;
        this.type = type;
        this.created = created;
        this.user = user;
        this.deleted = deleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, String room, Integer type, Long created, UserModel user) {
        String localID = Utils.getInstance().generateRandomString(32);
        return new MessageModel("", localID, message, room, type, created, user, 0, 1, 0);
    }

    public static MessageModel encryptMessage(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                EncryptorManager.getInstance().encrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoomID(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getDeleted(),
                messageModel.getIsSending(),
                messageModel.getIsFailedSend());
    }

    public static MessageModel decryptMessage(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                EncryptorManager.getInstance().decrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoomID(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getDeleted(),
                messageModel.getIsSending(),
                messageModel.getIsFailedSend());
    }

//    public static MessageModel encryptMessage(String message, String room, Integer type, Long created, UserModel user) throws GeneralSecurityException {
//        String localID = Utils.getInstance().generateRandomString(32);
//        return new MessageModel(localID, EncryptorManager.getInstance().encrypt(message, localID), room, type, created, user, 0, 1, 0);
//    }

//    public static MessageModel encryptMessage(String message, String room, Integer type, Long created, UserModel user) throws GeneralSecurityException {
//        String localID = Utils.getInstance().generateRandomString(32);
//        return new MessageModel(localID, EncryptorManager.getInstance().encrypt(message, localID), room, type, created, user, 0, 1, 0);
//    }
//
//    public static MessageModel BuilderDecrypt(String localID, String message, String room, Integer type, Long created, UserModel user, Integer deleted, Integer isSending, Integer isFailedSend) throws GeneralSecurityException {
//        return new MessageModel(localID, EncryptorManager.getInstance().decrypt(message, localID), room, type, created, user, deleted, isSending, isFailedSend);
//    }
//
//    public static MessageModel BuilderDecrypt(MessageModel messageModel) throws GeneralSecurityException {
//        return new MessageModel(
//                messageModel.getLocalID(),
//                EncryptorManager.getInstance().decrypt(messageModel.getMessage(), messageModel.getLocalID()),
//                messageModel.getRoomID(),
//                messageModel.getType(),
//                messageModel.getCreated(),
//                messageModel.getUser(),
//                messageModel.getDeleted(),
//                messageModel.getIsSending(),
//                messageModel.getIsFailedSend());
//    }

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
