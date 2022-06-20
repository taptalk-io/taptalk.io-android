package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.VIDEO;

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
    @JsonProperty("isMessageEdited")
    private Boolean isMessageEdited;
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
    @Nullable
    @JsonProperty("action")
    private String action;
    @Nullable
    @JsonProperty("target")
    private TAPMessageTargetModel target;
    @JsonIgnore
    private String messageStatusText;

    public TAPMessageModel(@Nullable String messageID,
                           @NonNull String localID,
                           @Nullable String filterID,
                           String body,
                           TAPRoomModel room,
                           Integer type,
                           Long created,
                           TAPUserModel user,
                           String recipientID,
                           @Nullable HashMap<String, Object> data,
                           @Nullable TAPQuoteModel quote,
                           @Nullable TAPReplyToModel replyTo,
                           @Nullable TAPForwardFromModel forwardFrom,
                           @Nullable Boolean isDeleted,
                           @Nullable Boolean isSending,
                           @Nullable Boolean isFailedSend,
                           @Nullable Boolean isDelivered,
                           @Nullable Boolean isRead,
                           @Nullable Boolean isHidden,
                           @Nullable Boolean isMessageEdited,
                           @Nullable Long updated,
                           @Nullable Long deleted,
                           @Nullable String action,
                           @Nullable TAPMessageTargetModel target) {
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
        this.isMessageEdited = isMessageEdited;
        this.updated = updated;
        this.deleted = deleted;
        this.target = target;
        this.action = action;
        // Update when adding fields to model

        updateMessageStatusText();
    }

    public TAPMessageModel() {
    }

    public static TAPMessageModel Builder(String body, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data) {
        String localID = TAPUtils.generateRandomString(32);
        return new TAPMessageModel("0", localID, "", body, room, type, created, user, recipientID, data, null, null, null, false, true, false, false, false, false, false, created, null, null, null);
    }

    public static TAPMessageModel BuilderWithQuotedMessage(String body, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data, TAPMessageModel quotedMessage) {
        String localID = TAPUtils.generateRandomString(32);
        String quoteTitle, quoteContent;
        if (quotedMessage.getType() == TYPE_FILE) {
            quoteTitle = TAPUtils.getFileDisplayName(quotedMessage);
            quoteContent = TAPUtils.getFileDisplayInfo(quotedMessage);
        } else {
            quoteTitle = quotedMessage.getUser().getFullname();
            quoteContent = quotedMessage.getBody();
        }
        String quoteFileID = null == quotedMessage.getData() ? "" : (String) quotedMessage.getData().get(FILE_ID);
        String quoteImageURL = null == quotedMessage.getData() ? "" : null != quotedMessage.getData().get(FILE_URL) ? (String) quotedMessage.getData().get(FILE_URL) : (String) quotedMessage.getData().get(IMAGE_URL);
        String quoteFileType = quotedMessage.getType() == TYPE_IMAGE ? IMAGE :
                quotedMessage.getType() == TYPE_VIDEO ? VIDEO :
                        quotedMessage.getType() == TYPE_FILE ? FILE : "";
        TAPQuoteModel quote = new TAPQuoteModel(quoteTitle, quoteContent, quoteFileID, quoteImageURL, quoteFileType);
        TAPReplyToModel reply = new TAPReplyToModel(quotedMessage.getMessageID()
                , quotedMessage.getLocalID(), quotedMessage.getType()
                , quotedMessage.getUser());
        return new TAPMessageModel("0", localID, "", body, room, type, created, user, recipientID, data, quote, reply, null, false, true, false, false, false, false, false, created, null, null, null);
    }

    public static TAPMessageModel BuilderWithQuote(String body, TAPRoomModel room, Integer type, Long created, TAPUserModel user, String recipientID, @Nullable HashMap<String, Object> data, String quoteTitle, String quoteContent, String quoteImageUrl) {
        String localID = TAPUtils.generateRandomString(32);

        TAPQuoteModel quote = new TAPQuoteModel(quoteTitle, quoteContent, "", quoteImageUrl, (null == quoteImageUrl || quoteImageUrl.isEmpty()) ? "" : IMAGE);
        return new TAPMessageModel("0", localID, "", body, room, type, created, user, recipientID, data, quote, null, null, false, true, false, false, false, false, false, created, null, null, null);
    }

    public static TAPMessageModel BuilderForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, Long created, TAPUserModel user, String recipientID) {
        String localID = TAPUtils.generateRandomString(32);
        TAPForwardFromModel forwardFrom;
        if (null == messageToForward.getForwardFrom() || null == messageToForward.getForwardFrom().getFullname() || messageToForward.getForwardFrom().getFullname().isEmpty()) {
            forwardFrom = new TAPForwardFromModel(messageToForward.getUser().getUserID(), messageToForward.getUser().getXcUserID(), messageToForward.getUser().getFullname(), messageToForward.getMessageID(), messageToForward.getLocalID());
        } else {
            forwardFrom = messageToForward.getForwardFrom();
        }
        return new TAPMessageModel("0", localID, "", messageToForward.getBody(), room, messageToForward.getType(), created, user, recipientID, messageToForward.getData(), messageToForward.getQuote(), messageToForward.getReplyTo(), forwardFrom, false, true, false, false, false, false, false, created, null, null, null);
    }

    public static TAPMessageModel BuilderResendMessage(TAPMessageModel message, Long created) {
        String localID = TAPUtils.generateRandomString(32);
        return new TAPMessageModel("0", localID, "", message.getBody(), message.getRoom(), message.getType(), created, message.getUser(), message.getRecipientID(), message.getData(), message.getQuote(), message.getReplyTo(), message.getForwardFrom(), false, true, false, false, false, false, false, created, null, null, null);
    }

    public static TAPMessageModel fromMessageEntity(TAPMessageEntity messageEntity) {
        return new TAPMessageModel(
                messageEntity.getMessageID(),
                messageEntity.getLocalID(),
                messageEntity.getFilterID(),
                messageEntity.getBody(),
                TAPRoomModel.Builder(messageEntity),
                messageEntity.getType(),
                messageEntity.getCreated(),
                new TAPUserModel(messageEntity.getUserID(), messageEntity.getXcUserID(), messageEntity.getUserFullName(),
                        TAPUtils.fromJSON(new TypeReference<TAPImageURL>() {
                        }, messageEntity.getUserImage()),
                        messageEntity.getUsername(), messageEntity.getUserEmail(), messageEntity.getUserPhone(),
                        TAPUtils.fromJSON(new TypeReference<TAPUserRoleModel>() {
                        }, messageEntity.getUserRole()),
                        messageEntity.getLastLogin(), messageEntity.getLastActivity(), messageEntity.getRequireChangePassword(), messageEntity.getUserCreated(),
                        messageEntity.getUserUpdated(), messageEntity.getUserDeleted()),
                messageEntity.getRecipientID(),
                null == messageEntity.getData() ? null : TAPUtils.toHashMap(messageEntity.getData()),
                null == messageEntity.getQuote() ? null : TAPUtils.fromJSON(new TypeReference<TAPQuoteModel>() {
                }, messageEntity.getQuote()),
                null == messageEntity.getReplyTo() ? null : TAPUtils.fromJSON(new TypeReference<TAPReplyToModel>() {
                }, messageEntity.getReplyTo()),
                null == messageEntity.getForwardFrom() ? null : TAPUtils.fromJSON(new TypeReference<TAPForwardFromModel>() {
                }, messageEntity.getForwardFrom()),
                messageEntity.getIsDeleted(),
                messageEntity.getSending(),
                messageEntity.getFailedSend(),
                messageEntity.getDelivered(),
                messageEntity.getIsRead(),
                messageEntity.getHidden(),
                messageEntity.getMessageEdited(),
                messageEntity.getUpdated(),
                messageEntity.getUserDeleted(),
                messageEntity.getAction(),
                messageEntity.getTarget());
    }

    public void updateMessageStatusText() {
        if (TapTalk.checkTapTalkInitialized() && null != TapTalk.appContext && null != created && created > 0L) {
            messageStatusText = TAPTimeFormatter.durationChatString(TapTalk.appContext, created);
//            messageStatusText = TAPTimeFormatter.formatClock(created);
        }
    }

    public TAPMessageModel updateMessageStatus(TAPMessageModel model) {
        if (null != model && null != model.isDeleted && (null == this.isDeleted || !this.isDeleted))
            this.isDeleted = model.getIsDeleted();
        if (null != model && null != model.deleted && (null == this.deleted || 0 == this.deleted))
            this.deleted = model.getDeleted();
        if (null != model && null != model.isHidden && (null == this.isHidden || !this.isHidden))
            this.isHidden = model.getIsHidden();
        if (null != model && null != model.isMessageEdited && (null == this.isMessageEdited || !this.isMessageEdited))
            this.isMessageEdited = model.getIsMessageEdited();
        if (null != model && null != model.isDelivered && (null != this.isDelivered && !this.isDelivered))
            this.isDelivered = model.getIsDelivered();
        if (null != model && null != model.isRead && (null != this.isRead && !this.isRead))
            this.isRead = model.getIsRead();

        return this;
    }

    public void updateValue(TAPMessageModel model) {
        // Validate null/empty value from new model
        if (null != model.messageID && !"".equals(model.messageID))
            this.messageID = model.getMessageID();
        if (!"".equals(model.localID)) this.localID = model.getLocalID();
        if (!"".equals(model.filterID)) this.filterID = model.getFilterID();
        if (null != model.body && !"".equals(model.body)) this.body = model.getBody();
        if (null != model.room) this.room = model.getRoom();
        this.type = model.getType();
        if (null != model.created && 0L != model.created) this.created = model.getCreated();
        if (null != model.user) this.user = model.getUser();
        if (null == this.data && null != model.data) {
            this.data = model.getData();
        } else if (null != model.data && (model.type == TYPE_IMAGE || model.type == TYPE_VIDEO) &&
                (null == model.isSending || !model.isSending) &&
                (null == model.isFailedSend || !model.isFailedSend)) {
            this.data.putAll(model.data);
            this.data.remove(FILE_URI); // Remove file Uri from data if type is image or video
        } else if (null != model.data) {
            this.data.putAll(model.data);
        }
        if (null != model.getQuote()) this.quote = model.getQuote();
        if (null != model.getRecipientID() && !"".equals(model.getRecipientID()))
            this.recipientID = model.getRecipientID();
        if (null != model.getReplyTo()) this.replyTo = model.getReplyTo();
        if (null != model.getForwardFrom()) this.forwardFrom = model.getForwardFrom();
        if (null != model.getIsDeleted() && (null == this.isDeleted || !this.isDeleted))
            this.isDeleted = model.getIsDeleted();
        if (null != model.getIsSending()) this.isSending = model.getIsSending();
        if (null != model.getIsFailedSend()) this.isFailedSend = model.getIsFailedSend();
        if (null != model.getUpdated() && 0L < model.getUpdated())
            this.updated = model.getUpdated();
        if (null != model.getDeleted() && 0L < model.getDeleted() && (null == this.deleted || 0 == this.deleted))
            this.deleted = model.getDeleted();
        if (null != model.getIsHidden() && (null == this.isHidden || !this.isHidden))
            this.isHidden = model.getIsHidden();
        if (null != model.getIsMessageEdited() && (null == this.isMessageEdited || !this.isMessageEdited))
            this.isMessageEdited = model.getIsMessageEdited();
        if (null != model.getIsDelivered() && (null != this.isDelivered && !this.isDelivered))
            this.isDelivered = model.getIsDelivered();
        if (null != model.getIsRead() && (null != this.isRead && !this.isRead))
            this.isRead = model.getIsRead();
        if (null != model.getAction()) this.action = model.getAction();
        if (null != model.getTarget()) this.target = model.getTarget();
        updateMessageStatusText();
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
            this.setIsDelivered(true);
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
                getIsSending(),
                getIsFailedSend(),
                getIsDelivered(),
                getIsRead(),
                getIsHidden(),
                getIsMessageEdited(),
                getUpdated(),
                getDeleted(),
                getAction(),
                getTarget());
        // Update when adding fields to model
    }

    public static TAPMessageModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPMessageModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
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

    @JsonIgnore
    @Nullable
    @Deprecated
    /**
     * @deprecated Use {@link #getIsDelivered()} instead.
     */
    public Boolean getDelivered() {
        return isDelivered;
    }

    @Nullable
    public Boolean getIsDelivered() {
        return isDelivered;
    }

    @JsonIgnore
    @Deprecated
    /**
     * @deprecated Use {@link #setIsDelivered()} instead.
     */
    public void setDelivered(@Nullable Boolean delivered) {
        isDelivered = delivered;
    }

    public void setIsDelivered(@Nullable Boolean delivered) {
        isDelivered = delivered;
    }

    @JsonIgnore
    @Nullable
    @Deprecated
    /**
     * @deprecated Use {@link #getIsHidden()} instead.
     */
    public Boolean getHidden() {
        return isHidden;
    }

    @Nullable
    public Boolean getIsHidden() {
        return isHidden;
    }

    @JsonIgnore
    @Deprecated
    /**
     * @deprecated Use {@link #setIsHidden()} instead.
     */
    public void setHidden(@Nullable Boolean hidden) {
        isHidden = hidden;
    }

    public void setIsHidden(@Nullable Boolean hidden) {
        isHidden = hidden;
    }

    @Nullable
    public Boolean getIsMessageEdited() {
        return isMessageEdited;
    }

    public void setIsMessageEdited(@Nullable Boolean edited) {
        isMessageEdited = edited;
    }

    @Nullable
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(@Nullable Boolean deleted) {
        isDeleted = deleted;
    }

    @JsonIgnore
    @Nullable
    @Deprecated
    /**
     * @deprecated Use {@link #getIsSending()} instead.
     */
    public Boolean getSending() {
        return isSending;
    }

    @Nullable
    public Boolean getIsSending() {
        return isSending;
    }

    @JsonIgnore
    @Deprecated
    /**
     * @deprecated Use {@link #setIsSending()} instead.
     */
    public void setSending(@Nullable Boolean sending) {
        isSending = sending;
    }

    @JsonIgnore
    public void setIsSending(@Nullable Boolean sending) {
        isSending = sending;
    }

    @JsonIgnore
    @Nullable
    @Deprecated
    /**
     * @deprecated Use {@link #getIsFailedSend()} instead.
     */
    public Boolean getFailedSend() {
        return isFailedSend;
    }

    @Nullable
    public Boolean getIsFailedSend() {
        return isFailedSend;
    }

    @JsonIgnore
    @Deprecated
    /**
     * @deprecated Use {@link #setIsFailedSend()} instead.
     */
    public void setFailedSend(@Nullable Boolean failedSend) {
        isFailedSend = failedSend;
    }

    @JsonIgnore
    public void setIsFailedSend(@Nullable Boolean failedSend) {
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

    @Nullable
    public String getAction() {
        return action;
    }

    public void setAction(@Nullable String action) {
        this.action = action;
    }

    @Nullable
    public TAPMessageTargetModel getTarget() {
        return target;
    }

    public void setTarget(@Nullable TAPMessageTargetModel target) {
        this.target = target;
    }

    @JsonIgnore
    public String getMessageStatusText() {
        return messageStatusText;
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
        dest.writeValue(this.isMessageEdited);
        dest.writeValue(this.isDeleted);
        dest.writeValue(this.isSending);
        dest.writeValue(this.isFailedSend);
        dest.writeValue(this.updated);
        dest.writeValue(this.deleted);
        dest.writeString(this.action);
        dest.writeParcelable(this.target, flags);
        dest.writeString(this.messageStatusText);
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
        this.isMessageEdited = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDeleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSending = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isFailedSend = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.updated = (Long) in.readValue(Long.class.getClassLoader());
        this.deleted = (Long) in.readValue(Long.class.getClassLoader());
        this.action = in.readString();
        this.target = in.readParcelable(TAPMessageTargetModel.class.getClassLoader());
        this.messageStatusText = in.readString();
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
