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

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

/**
 * If this class has more attribute, don't forget to add it to copyMessageModel function
 */

public class TAPMessageModel implements Parcelable {
    @Nullable
    @JsonProperty("messageID")
    @JsonAlias("id")
    private String messageID;
    @NonNull
    @JsonProperty("localID")
    private String localID;
    @Nullable
    @JsonProperty("filterID")
    private String filterID;
    @JsonProperty("room") private TAPRoomModel room;
    @JsonProperty("type") private int type;
    @JsonProperty("body") private String body;
    @JsonProperty("created") private Long created;
    @JsonProperty("user") private TAPUserModel user;
    @JsonProperty("recipientID") private String recipientID;
    @Nullable
    @JsonProperty("data")
    private HashMap<String, Object> data;
    @Nullable
    @JsonProperty("quote")
    private TAPQuoteModel quote;
    @Nullable
    @JsonProperty("replyTo")
    private TAPReplyToModel replyTo;
    @Nullable
    @JsonProperty("forwardFrom")
    private TAPForwardFromModel forwardFrom;
    @Nullable
    @JsonProperty("isRead")
    private Boolean isRead;
    @Nullable
    @JsonProperty("isDelivered")
    private Boolean isDelivered;
    @Nullable
    @JsonProperty("isHidden")
    private Boolean isHidden;
    @Nullable
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    @Nullable
    @JsonProperty("isSending")
    private Boolean isSending;
    @Nullable
    @JsonProperty("isFailedSend")
    private Boolean isFailedSend;
    @Nullable
    @JsonProperty("updated")
    private Long updated;
    @Nullable
    @JsonProperty("deleted")
    private Long deleted;
    @JsonIgnore private String messageStatusText;
    @JsonIgnore private boolean isExpanded, isNeedAnimateSend, isAnimating;

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

    public static TAPMessageModel Builder(String body, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        return new TAPMessageModel("0", localID, "", body, room, type, created, user, recipientID, data, null, null, null, false, true, false, false, false, false, created, null);
    }

    public static TAPMessageModel BuilderWithQuotedMessage(String body, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data, TAPMessageModel quotedMessage) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        String quoteTitle, quoteContent;
        if (quotedMessage.getType() == TYPE_FILE) {
            quoteTitle = TAPUtils.getInstance().getFileDisplayName(quotedMessage);
            quoteContent = TAPUtils.getInstance().getFileDisplayInfo(quotedMessage);
        } else {
            quoteTitle = quotedMessage.getUser().getName();
            quoteContent = quotedMessage.getBody();
        }
        String quoteFileID = null == quotedMessage.getData() ? "" : (String) quotedMessage.getData().get(FILE_ID);
        String quoteImageURL = null == quotedMessage.getData() ? "" : (String) quotedMessage.getData().get(IMAGE_URL);
        String quoteFileType = String.valueOf(quotedMessage.getType()); // TODO: 8 March 2019 CHANGE FILE TYPE
        TAPQuoteModel quote = new TAPQuoteModel(quoteTitle, quoteContent, quoteFileID, quoteImageURL, quoteFileType);
        TAPReplyToModel reply = new TAPReplyToModel(quotedMessage.getMessageID()
                , quotedMessage.getLocalID(), quotedMessage.getType()
                , quotedMessage.getUser());
        return new TAPMessageModel("0", localID, "", body, room, type, created, user, recipientID, data, quote, reply, null, false, true, false, false, false, false, created, null);
    }

    public static TAPMessageModel BuilderForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, Long created, TAPUserModel user, String recipientID) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        TAPForwardFromModel forwardFrom;
        if (null == messageToForward.getForwardFrom() || null == messageToForward.getForwardFrom().getFullname() || messageToForward.getForwardFrom().getFullname().isEmpty()) {
            forwardFrom = new TAPForwardFromModel(messageToForward.getUser().getUserID(), messageToForward.getUser().getXcUserID(), messageToForward.getUser().getName(), messageToForward.getMessageID(), messageToForward.getLocalID());
        } else {
            forwardFrom = messageToForward.getForwardFrom();
        }
        return new TAPMessageModel("0", localID, "", messageToForward.getBody(), room, messageToForward.getType(), created, user, recipientID, messageToForward.getData(), messageToForward.getQuote(), messageToForward.getReplyTo(), forwardFrom, false, true, false, false, false, false, created, null);
    }

    public static TAPMessageModel BuilderResendMessage(TAPMessageModel message, Long created) {
        String localID = TAPUtils.getInstance().generateRandomString(32);
        return new TAPMessageModel("0", localID, "", message.getBody(), message.getRoom(), message.getType(), created, message.getUser(), message.getRecipientID(), message.getData(), message.getQuote(), message.getReplyTo(), message.getForwardFrom(), false, true, false, false, false, false, created, null);
    }

    public void updateMessageStatusText() {
        if (created > 0L && (null == messageStatusText || messageStatusText.isEmpty())) {
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

    public void putData(HashMap<String, Object> data) {
        if (null == this.data) {
            setData(data);
        } else {
            this.data.putAll(data);
        }
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

    public void updateValue(TAPMessageModel model) {
        this.messageID = model.getMessageID();
        this.localID = model.getLocalID();
        this.body = model.getBody();
        this.room = model.getRoom();
        this.type = model.getType();
        this.created = model.getCreated();
        this.user = model.getUser();

        if (null == this.data) {
            this.data = model.getData();
        } else if (null != model.data && (model.type == TYPE_IMAGE || model.type == TYPE_VIDEO) &&
                (null == model.isSending || !model.isSending) &&
                (null == model.isFailedSend || !model.isFailedSend)) {
            this.data.putAll(model.data);
            this.data.remove(FILE_URI); // Remove file Uri from data if type is image or video
        } else if (null != model.data) {
            this.data.putAll(model.data);
        }

        this.quote = model.getQuote();
        this.recipientID = model.getRecipientID();
        this.replyTo = model.getReplyTo();
        this.forwardFrom = model.getForwardFrom();
        this.isDeleted = model.getIsDeleted();
        this.isSending = model.getSending();
        this.isFailedSend = model.getFailedSend();
        this.updated = model.getUpdated();
        this.deleted = model.getDeleted();
        if (null == this.isHidden || !this.isHidden)
            this.isHidden = model.getHidden();
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
        if (null != getData()) setData(new HashMap<>(getData()));

        return new TAPMessageModel(
                getMessageID(),
                getLocalID(),
                getFilterID(),
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
        // Update when adding fields to model
    }

    public HashMap<String, Object> convertToHashMap() {
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageID", messageID);
        messageMap.put("localID", localID);
        messageMap.put("filterID", filterID);
        messageMap.put("body", body);
        messageMap.put("room", room);
        messageMap.put("type", type);
        messageMap.put("created", created);
        messageMap.put("user", user);
        messageMap.put("recipientID", recipientID);
        messageMap.put("data", data);
        messageMap.put("quote", quote);
        messageMap.put("replyTo", replyTo);
        messageMap.put("forwardFrom", forwardFrom);
        messageMap.put("isDeleted", isDeleted);
        messageMap.put("isSending", isSending);
        messageMap.put("isFailedSend", isFailedSend);
        messageMap.put("isDelivered", isDelivered);
        messageMap.put("isRead", isRead);
        messageMap.put("isHidden", isHidden);
        messageMap.put("updated", updated);
        messageMap.put("deleted", deleted);
        // Update when adding fields to model
        return messageMap;
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
