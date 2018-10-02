package com.moselo.HomingPigeon.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Manager.HpEncryptorManager;

import java.security.GeneralSecurityException;

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
    @Nullable @JsonProperty("hasRead") private Boolean hasRead;
    @Nullable @JsonProperty("isRead") private Boolean isRead;
    @Nullable @JsonProperty("isDelivered") private Boolean isDelivered;
    @Nullable @JsonProperty("isHidden") private Boolean isHidden;
    @Nullable @JsonProperty("isDeleted") private Boolean isDeleted;
    @Nullable @JsonProperty("isSending") private Boolean isSending;
    @Nullable @JsonProperty("isFailedSend") private Boolean isFailedSend;
    @Nullable @JsonProperty("updated") private Long updated;
    private Long unreadCount;
    private Boolean isExpanded = false;

    public MessageModel(@Nullable String messageID, @NonNull String localID, String message, RoomModel room,
                        Integer type, Long created, UserModel user, String recipientID, @Nullable Boolean isDeleted,
                        @Nullable Boolean isSending, @Nullable Boolean isFailedSend, Long unreadCount) {
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
        this.unreadCount = unreadCount;
        // TODO: 3 September 2018 ADD deliveredTo & seenBy
    }

    public MessageModel() {
    }

    public static MessageModel Builder(String message, RoomModel room, Integer type, Long created, UserModel user, String recipientID) {
        String localID = HpUtils.getInstance().generateRandomString(32);
        return new MessageModel("0", localID, message, room, type, created, user, recipientID, false, true, false, (long) 0);
    }

    public static MessageModel BuilderEncrypt(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                HpEncryptorManager.getInstance().encrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getRecipientID(),
                messageModel.getDeleted(),
                messageModel.getSending(),
                messageModel.getFailedSend(),
                messageModel.getUnreadCount());
    }

    public static MessageModel BuilderDecrypt(MessageModel messageModel) throws GeneralSecurityException {
        return new MessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                HpEncryptorManager.getInstance().decrypt(messageModel.getMessage(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getRecipientID(),
                messageModel.getDeleted(),
                messageModel.getSending(),
                messageModel.getFailedSend(),
                messageModel.getUnreadCount());
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

    @Nullable
    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(@Nullable Long updated) {
        this.updated = updated;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void updateValue(MessageModel model){
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.message = model.getMessage();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();
        this.isDeleted = model.getDeleted();
        this.isSending = model.getSending();
        this.isFailedSend = model.getFailedSend();
        // TODO: 3 September 2018 ADD deliveredTo & seenBy
    }
}
