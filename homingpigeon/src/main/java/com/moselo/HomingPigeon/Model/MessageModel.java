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
    @JsonProperty("room") private RoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("body") private String message;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private UserModel user;
    @Nullable @JsonProperty("deliveredTo") private List<Object> deliveredTo;
    @Nullable @JsonProperty("seenBy") private List<SeenByModel> seenBy;
    @Nullable @JsonProperty("isDeleted") private Integer isDeleted;
    @Nullable @JsonProperty("isSending") private Integer isSending;
    @Nullable @JsonProperty("isFailedSend") private Integer isFailedSend;

    public MessageModel(@Nullable String messageID, @NonNull String localID, String message, RoomModel room,
                        Integer type, Long created, UserModel user, @Nullable Integer isDeleted,
                        @Nullable Integer isSending, @Nullable Integer isFailedSend) {
        this.messageID = messageID;
        this.localID = localID;
        this.message = message;
        this.room = room;
        this.type = type;
        this.created = created;
        this.user = user;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        // TODO: 3 September 2018 ADD deliveredTo & seenBy
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, RoomModel room, Integer type, Long created, UserModel user) {
        String localID = Utils.getInstance().generateRandomString(32);
        return new MessageModel("", localID, message, room, type, created, user, 0, 1, 0);
    }

    public static MessageModel BuilderEncrypt(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                EncryptorManager.getInstance().encrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getIsDeleted(),
                messageModel.getIsSending(),
                messageModel.getIsFailedSend());
    }

    public static MessageModel BuilderDecrypt(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                EncryptorManager.getInstance().decrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getIsDeleted(),
                messageModel.getIsSending(),
                messageModel.getIsFailedSend());
    }

//    public static MessageModel BuilderEncrypt(String message, String room, Integer type, Long created, UserModel user) throws GeneralSecurityException {
//        String localID = Utils.getInstance().generateRandomString(32);
//        return new MessageModel(localID, EncryptorManager.getInstance().encrypt(message, localID), room, type, created, user, 0, 1, 0);
//    }

//    public static MessageModel BuilderEncrypt(String message, String room, Integer type, Long created, UserModel user) throws GeneralSecurityException {
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
    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(@Nullable Integer isDeleted) {
        this.isDeleted = isDeleted;
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

    public void updateValue(MessageModel model){
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.message = model.getMessage();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();
        this.isDeleted = model.getIsDeleted();
        this.isSending = model.getIsSending();
        this.isFailedSend = model.getIsFailedSend();
        // TODO: 3 September 2018 ADD deliveredTo & seenBy
    }
}
