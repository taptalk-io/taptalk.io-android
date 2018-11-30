package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.GeneralSecurityException;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;

/**
 * If this class has more attribute, don't forget to add it to copyMessageModel function
 */
public class TAPMessageModel implements Parcelable {
    @Nullable @JsonProperty("messageID") @JsonAlias("id") private String messageID;
    @NonNull @JsonProperty("localID") private String localID;
    @Nullable @JsonProperty("filterID") private String filterID;
    @JsonProperty("room") private TAPRoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("body") private String body;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private TAPUserModel user;
    @JsonProperty("recipientID") private String recipientID;
    @Nullable @JsonProperty("isRead") private Boolean isRead;
    @Nullable @JsonProperty("isDelivered") private Boolean isDelivered;
    @Nullable @JsonProperty("isHidden") private Boolean isHidden;
    @Nullable @JsonProperty("isDeleted") private Boolean isDeleted;
    @Nullable @JsonProperty("isSending") private Boolean isSending;
    @Nullable @JsonProperty("isFailedSend") private Boolean isFailedSend;
    @Nullable @JsonProperty("updated") private Long updated;
    @Nullable @JsonProperty("deleted") private Long deleted;
    @JsonIgnore private TAPMessageModel replyTo; // TODO: 1 November 2018 TESTING REPLY LAYOUT
    @JsonIgnore private boolean isExpanded, isFirstLoadFinished, isNeedAnimateSend;
    @JsonIgnore private int imageWidth, imageHeight;

    public TAPMessageModel(@Nullable String messageID, @NonNull String localID, @Nullable String filterID, String body,
                           TAPRoomModel room, Integer type, Long created, TAPUserModel user,
                           String recipientID, @Nullable Boolean isDeleted,
                           @Nullable Boolean isSending, @Nullable Boolean isFailedSend,
                           @Nullable Boolean isDelivered, @Nullable Boolean isRead,
                           @Nullable Boolean isHidden, @Nullable Long updated, @Nullable Long deleted) {
        this.messageID = messageID;
        this.localID = localID;
        this.filterID = filterID;
        this.body = body;
        this.room = room;
        this.type = type;
        this.created = created;
        this.user = user;
        this.recipientID = recipientID;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.isDelivered = isDelivered;
        this.isRead = isRead;
        this.isHidden = isHidden;
        this.updated = updated;
        this.deleted = deleted;
        // Update when adding fields to model
    }

    public TAPMessageModel() {
    }

    public static TAPMessageModel Builder(String message, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        return new TAPMessageModel("0", localID, "", message, room, type, created, user, recipientID, false, true, false, false, false, false, created, null);
    }

    public static TAPMessageModel BuilderEncrypt(TAPMessageModel messageModel) throws GeneralSecurityException {
        return new TAPMessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                messageModel.getFilterID(),
                TAPEncryptorManager.getInstance().encrypt(messageModel.getBody(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getRecipientID(),
                messageModel.getIsDeleted(),
                messageModel.getSending(),
                messageModel.getFailedSend(),
                messageModel.getDelivered(),
                messageModel.getIsRead(),
                messageModel.getHidden(),
                messageModel.getUpdated(),
                messageModel.getDeleted());
    }

    public static TAPMessageModel BuilderDecrypt(TAPMessageModel messageModel) throws GeneralSecurityException {
        return new TAPMessageModel(
                messageModel.getMessageID(),
                messageModel.getLocalID(),
                messageModel.getFilterID(),
                TAPEncryptorManager.getInstance().decrypt(messageModel.getBody(), messageModel.getLocalID()),
                messageModel.getRoom(),
                messageModel.getType(),
                messageModel.getCreated(),
                messageModel.getUser(),
                messageModel.getRecipientID(),
                messageModel.getIsDeleted(),
                messageModel.getSending(),
                messageModel.getFailedSend(),
                messageModel.getDelivered(),
                messageModel.getIsRead(),
                messageModel.getHidden(),
                messageModel.getUpdated(),
                messageModel.getDeleted());
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

    @Nullable
    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(@Nullable String filterID) {
        this.filterID = filterID;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
        this.user = user;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
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
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(@Nullable Boolean deleted) {
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

    public void setDeleted(@Nullable Long deleted) {
        this.deleted = deleted;
    }

    @Nullable
    public Long getDeleted() {
        return deleted;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isFirstLoadFinished() {
        return isFirstLoadFinished;
    }

    public void setFirstLoadFinished(boolean firstLoadFinished) {
        isFirstLoadFinished = firstLoadFinished;
    }

    public boolean isNeedAnimateSend() {
        return isNeedAnimateSend;
    }

    public void setNeedAnimateSend(boolean sendAnimateFinished) {
        isNeedAnimateSend = sendAnimateFinished;
    }

    public TAPMessageModel getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(TAPMessageModel replyTo) {
        this.replyTo = replyTo;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void updateValue(TAPMessageModel model) {
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.body = model.getBody();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();
        this.isDeleted = model.getIsDeleted();
        this.isSending = model.getSending();
        this.isFailedSend = model.getFailedSend();
        this.updated = model.getUpdated();
        if (null != this.isDelivered && !this.isDelivered)
            this.isDelivered = model.getDelivered();
        if (null != this.isRead && !this.isRead)
            this.isRead = model.getIsRead();
        // Update when adding fields to model
    }

    public TAPMessageModel updateValueToReturnModel(TAPMessageModel model) {
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.body = model.getBody();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();
        this.isDeleted = model.getIsDeleted();
        this.isSending = model.getSending();
        this.isFailedSend = model.getFailedSend();
        this.updated = model.getUpdated();
        if (null != this.isDelivered && !this.isDelivered)
            this.isDelivered = model.getDelivered();
        if (null != this.isRead && !this.isRead)
            this.isRead = model.getIsRead();
        return this;
        // Update when adding fields to model
    }

    public void updateReadMessage() {
        if (null != this.isRead && !this.isRead)
            this.isRead = true;
        if (null != this.isDelivered && !this.isDelivered)
            this.isDelivered = true;
    }

    public void updateDeliveredMessage() {
        if (null != this.isDelivered && !this.isDelivered)
            this.setDelivered(true);
    }

    public TAPMessageModel copyMessageModel() {
        return new TAPMessageModel(
                getMessageID(),
                getLocalID(),
                getLocalID(),
                getBody(),
                getRoom(),
                getType(),
                getCreated(),
                getUser(),
                getRecipientID(),
                getIsDeleted(),
                getSending(),
                getFailedSend(),
                getDelivered(),
                getIsRead(),
                getHidden(),
                getUpdated(),
                getDeleted());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageID);
        dest.writeString(this.localID);
        dest.writeString(this.filterID);
        dest.writeParcelable(this.room, flags);
        dest.writeInt(this.type);
        dest.writeString(this.body);
        dest.writeValue(this.created);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.recipientID);
        dest.writeValue(this.isRead);
        dest.writeValue(this.isDelivered);
        dest.writeValue(this.isHidden);
        dest.writeValue(this.isDeleted);
        dest.writeValue(this.isSending);
        dest.writeValue(this.isFailedSend);
        dest.writeValue(this.updated);
        dest.writeValue(this.deleted);
        dest.writeParcelable(this.replyTo, flags);
        dest.writeByte(this.isExpanded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFirstLoadFinished ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedAnimateSend ? (byte) 1 : (byte) 0);
        dest.writeInt(this.imageWidth);
        dest.writeInt(this.imageHeight);
    }

    protected TAPMessageModel(Parcel in) {
        this.messageID = in.readString();
        this.localID = in.readString();
        this.filterID = in.readString();
        this.room = in.readParcelable(TAPRoomModel.class.getClassLoader());
        this.type = in.readInt();
        this.body = in.readString();
        this.created = (Long) in.readValue(Long.class.getClassLoader());
        this.user = in.readParcelable(TAPUserModel.class.getClassLoader());
        this.recipientID = in.readString();
        this.isRead = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDelivered = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isHidden = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDeleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSending = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isFailedSend = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.updated = (Long) in.readValue(Long.class.getClassLoader());
        this.deleted = (Long) in.readValue(Long.class.getClassLoader());
        this.replyTo = in.readParcelable(TAPMessageModel.class.getClassLoader());
        this.isExpanded = in.readByte() != 0;
        this.isFirstLoadFinished = in.readByte() != 0;
        this.isNeedAnimateSend = in.readByte() != 0;
        this.imageWidth = in.readInt();
        this.imageHeight = in.readInt();
    }

    public static final Creator<TAPMessageModel> CREATOR = new Creator<TAPMessageModel>() {
        @Override
        public TAPMessageModel createFromParcel(Parcel source) {
            return new TAPMessageModel(source);
        }

        @Override
        public TAPMessageModel[] newArray(int size) {
            return new TAPMessageModel[size];
        }
    };
}
