package com.moselo.HomingPigeon.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.EncryptorManager;

import java.security.GeneralSecurityException;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageModel {

    @Nullable @JsonProperty("messageID") @JsonAlias("id") private String messageID;
    @NonNull @JsonProperty("localID") private String localID;
    @JsonProperty("room") private RoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("body") private String message;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private UserModel user;
    @JsonProperty("recipientID") private String recipientID;
    @Nullable @JsonProperty("deliveredTo") private List<Object> deliveredTo;
    @Nullable @JsonProperty("seenBy") private List<SeenByModel> seenBy;
    @Nullable @JsonProperty("isDeleted") private Boolean isDeleted;
    @Nullable @JsonProperty("isSending") private Boolean isSending;
    @Nullable @JsonProperty("isFailedSend") private Boolean isFailedSend;
    @Nullable @JsonProperty("updated") private Long updated;

    public MessageModel(@Nullable String messageID, @NonNull String localID, String message, RoomModel room,
                        Integer type, Long created, UserModel user, String recipientID, @Nullable Boolean isDeleted,
                        @Nullable Boolean isSending, @Nullable Boolean isFailedSend) {
        this.messageID = messageID;
        this.localID = localID;
        this.message = message;
        this.room = room;
        this.type = type;
        this.created = created;
        this.user = user;
        this.recipientID = recipientID;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        // TODO: 3 September 2018 ADD deliveredTo & seenBy
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, RoomModel room, Integer type, Long created, UserModel user, String recipientID) {
        String localID = Utils.getInstance().generateRandomString(32);
        return new MessageModel("", localID, message, room, type, created, user, recipientID, false, true, false);
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
                messageModel.getRecipientID(),
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
                messageModel.getRecipientID(),
                messageModel.getIsDeleted(),
                messageModel.getIsSending(),
                messageModel.getIsFailedSend());
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

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
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
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(@Nullable Boolean isDeleted) {
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
