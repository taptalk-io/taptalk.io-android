package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPReplyToModel implements Parcelable {
    @JsonProperty("messageID") private String messageID;
    @JsonProperty("localID") private String localID;
    @JsonProperty("messageType") private Integer messageType;

    public TAPReplyToModel(String messageID, String localID, Integer messageType) {
        this.messageID = messageID;
        this.localID = localID;
        this.messageType = messageType;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageID);
        dest.writeString(this.localID);
        dest.writeValue(this.messageType);
    }

    public TAPReplyToModel() {
    }

    protected TAPReplyToModel(Parcel in) {
        this.messageID = in.readString();
        this.localID = in.readString();
        this.messageType = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<TAPReplyToModel> CREATOR = new Creator<TAPReplyToModel>() {
        @Override
        public TAPReplyToModel createFromParcel(Parcel source) {
            return new TAPReplyToModel(source);
        }

        @Override
        public TAPReplyToModel[] newArray(int size) {
            return new TAPReplyToModel[size];
        }
    };
}
