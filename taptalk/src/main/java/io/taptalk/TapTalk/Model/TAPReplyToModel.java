package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPReplyToModel implements Parcelable {
    @JsonProperty("messageID") private String messageID;
    @JsonProperty("localID") private String localID;
    @JsonProperty("messageType") private Integer messageType;
    @JsonProperty("userID") private String userID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullname") private String fullname;

    public TAPReplyToModel(String messageID, String localID, Integer messageType,
                           String userID, String xcUserID, String fullname) {
        this.messageID = messageID;
        this.localID = localID;
        this.messageType = messageType;
        this.xcUserID = xcUserID;
        this.userID = userID;
        this.fullname = fullname;
    }

    public TAPReplyToModel(String messageID, String localID, Integer messageType,
                           TAPUserModel user) {
        this.messageID = messageID;
        this.localID = localID;
        this.messageType = messageType;
        this.xcUserID = user.getXcUserID();
        this.userID = user.getUserID();
        this.fullname = user.getName();
    }

    public TAPReplyToModel() {
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getXcUserID() {
        return xcUserID;
    }

    public void setXcUserID(String xcUserID) {
        this.xcUserID = xcUserID;
    }

    public String getUsername() {
        return fullname;
    }

    public void setUsername(String fullname) {
        this.fullname = fullname;
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
        dest.writeString(this.userID);
        dest.writeString(this.xcUserID);
        dest.writeString(this.fullname);
    }

    protected TAPReplyToModel(Parcel in) {
        this.messageID = in.readString();
        this.localID = in.readString();
        this.messageType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userID = in.readString();
        this.xcUserID = in.readString();
        this.fullname = in.readString();
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
