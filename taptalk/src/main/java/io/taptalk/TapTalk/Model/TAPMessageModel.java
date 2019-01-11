package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;

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
    @Nullable @JsonProperty("data") private HashMap<String, Object> data;
    @Nullable @JsonProperty("quote") private TAPQuoteModel quote;
    @Nullable @JsonProperty("replyTo") private TAPReplyToModel replyTo;
    @Nullable @JsonProperty("forwardFrom") private TAPForwardFromModel forwardFrom;
    @Nullable @JsonProperty("isRead") private Boolean isRead;
    @Nullable @JsonProperty("isDelivered") private Boolean isDelivered;
    @Nullable @JsonProperty("isHidden") private Boolean isHidden;
    @Nullable @JsonProperty("isDeleted") private Boolean isDeleted;
    @Nullable @JsonProperty("isSending") private Boolean isSending;
    @Nullable @JsonProperty("isFailedSend") private Boolean isFailedSend;
    @Nullable @JsonProperty("updated") private Long updated;
    @Nullable @JsonProperty("deleted") private Long deleted;
    @JsonIgnore private String messageStatusText;
    @JsonIgnore private boolean isExpanded, isNeedAnimateSend, isAnimating;
    @JsonIgnore private int imageWidth, imageHeight, progress = 0;

    public TAPMessageModel(@Nullable String messageID, @NonNull String localID, @Nullable String filterID, String body,
                           TAPRoomModel room, Integer type, Long created, TAPUserModel user,
                           String recipientID, @Nullable HashMap<String, Object> data,
                           @Nullable TAPQuoteModel quote, @Nullable TAPReplyToModel replyTo,
                           @Nullable TAPForwardFromModel forwardFrom, @Nullable Boolean isDeleted,
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
        this.data = data;
        this.quote = quote;
        this.replyTo = replyTo;
        this.forwardFrom = forwardFrom;
        this.isDeleted = isDeleted;
        this.isSending = isSending;
        this.isFailedSend = isFailedSend;
        this.isDelivered = isDelivered;
        this.isRead = isRead;
        this.isHidden = isHidden;
        this.updated = updated;
        this.deleted = deleted;
        // Update when adding fields to model

        updateMessageStatusText();
    }

    public TAPMessageModel() {
    }

    public static TAPMessageModel Builder(String message, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        return new TAPMessageModel("0", localID, "", message, room, type, created, user, recipientID, data,null,null,null, false, true, false, false, false, false, created, null);
    }

//    public static TAPMessageModel BuilderEncrypt(TAPMessageModel messageModel) throws GeneralSecurityException {
//        return new TAPMessageModel(
//                messageModel.getMessageID(),
//                messageModel.getLocalID(),
//                messageModel.getFilterID(),
//                TAPEncryptorManager.getInstance().encrypt(messageModel.getBody(), messageModel.getLocalID()),
//                messageModel.getRoom(),
//                messageModel.getType(),
//                messageModel.getCreated(),
//                messageModel.getUser(),
//                messageModel.getRecipientID(),
//                messageModel.getData(),
//                messageModel.getIsDeleted(),
//                messageModel.getSending(),
//                messageModel.getFailedSend(),
//                messageModel.getDelivered(),
//                messageModel.getIsRead(),
//                messageModel.getHidden(),
//                messageModel.getUpdated(),
//                messageModel.getDeleted());
//    }
//
//    public static TAPMessageModel BuilderDecrypt(TAPMessageModel messageModel) throws GeneralSecurityException {
//        return new TAPMessageModel(
//                messageModel.getMessageID(),
//                messageModel.getLocalID(),
//                messageModel.getFilterID(),
//                TAPEncryptorManager.getInstance().decrypt(messageModel.getBody(), messageModel.getLocalID()),
//                messageModel.getRoom(),
//                messageModel.getType(),
//                messageModel.getCreated(),
//                messageModel.getUser(),
//                messageModel.getRecipientID(),
//                messageModel.getData(),
//                messageModel.getIsDeleted(),
//                messageModel.getSending(),
//                messageModel.getFailedSend(),
//                messageModel.getDelivered(),
//                messageModel.getIsRead(),
//                messageModel.getHidden(),
//                messageModel.getUpdated(),
//                messageModel.getDeleted());
//    }

    private void updateMessageStatusText() {
        if (created > 0L) {
            messageStatusText = TAPTimeFormatter.getInstance().durationChatString(TapTalk.appContext, created);
        }
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
    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(@Nullable HashMap<String, Object> data) {
        this.data = data;
    }

    @Nullable
    public TAPQuoteModel getQuote() {
        return quote;
    }

    public void setQuote(@Nullable TAPQuoteModel quote) {
        this.quote = quote;
    }

    @Nullable
    public TAPReplyToModel getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(@Nullable TAPReplyToModel replyTo) {
        this.replyTo = replyTo;
    }

    @Nullable
    public TAPForwardFromModel getForwardFrom() {
        return forwardFrom;
    }

    public void setForwardFrom(@Nullable TAPForwardFromModel forwardFrom) {
        this.forwardFrom = forwardFrom;
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

    public String getMessageStatusText() {
        return messageStatusText;
    }

    public void setMessageStatusText(String messageStatusText) {
        this.messageStatusText = messageStatusText;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isNeedAnimateSend() {
        return isNeedAnimateSend;
    }

    public void setNeedAnimateSend(boolean sendAnimateFinished) {
        isNeedAnimateSend = sendAnimateFinished;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void updateValue(TAPMessageModel model) {
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.body = model.getBody();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();
        this.data = model.getData();
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
                getData(),
                getQuote(),
                getReplyTo(),
                getForwardFrom(),
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
        dest.writeSerializable(this.data);
        dest.writeParcelable(this.quote, flags);
        dest.writeParcelable(this.replyTo, flags);
        dest.writeParcelable(this.forwardFrom, flags);
        dest.writeValue(this.isRead);
        dest.writeValue(this.isDelivered);
        dest.writeValue(this.isHidden);
        dest.writeValue(this.isDeleted);
        dest.writeValue(this.isSending);
        dest.writeValue(this.isFailedSend);
        dest.writeValue(this.updated);
        dest.writeValue(this.deleted);
        dest.writeString(this.messageStatusText);
        dest.writeByte(this.isExpanded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedAnimateSend ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnimating ? (byte) 1 : (byte) 0);
        dest.writeInt(this.imageWidth);
        dest.writeInt(this.imageHeight);
        dest.writeInt(this.progress);
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
        this.data = (HashMap<String, Object>) in.readSerializable();
        this.quote = in.readParcelable(TAPQuoteModel.class.getClassLoader());
        this.replyTo = in.readParcelable(TAPReplyToModel.class.getClassLoader());
        this.forwardFrom = in.readParcelable(TAPForwardFromModel.class.getClassLoader());
        this.isRead = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDelivered = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isHidden = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDeleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSending = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isFailedSend = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.updated = (Long) in.readValue(Long.class.getClassLoader());
        this.deleted = (Long) in.readValue(Long.class.getClassLoader());
        this.messageStatusText = in.readString();
        this.isExpanded = in.readByte() != 0;
        this.isNeedAnimateSend = in.readByte() != 0;
        this.isAnimating = in.readByte() != 0;
        this.imageWidth = in.readInt();
        this.imageHeight = in.readInt();
        this.progress = in.readInt();
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
