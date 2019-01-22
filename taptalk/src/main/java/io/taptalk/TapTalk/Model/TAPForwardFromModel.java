package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPForwardFromModel implements Parcelable {
    @JsonProperty("userID") private String userID;
    @JsonProperty("xcUserID") private String xcUserID;
    @JsonProperty("fullname") private String fullname;
    @JsonProperty("messageID") private String messageID;
    @JsonProperty("localID") private String localID;

    public TAPForwardFromModel(String userID, String xcUserID, String fullname, String messageID, String localID) {
        this.userID = userID;
        this.xcUserID = xcUserID;
        this.fullname = fullname;
        this.messageID = messageID;
        this.localID = localID;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.xcUserID);
        dest.writeString(this.fullname);
        dest.writeString(this.messageID);
        dest.writeString(this.localID);
    }

    public TAPForwardFromModel() {
    }

    protected TAPForwardFromModel(Parcel in) {
        this.userID = in.readString();
        this.xcUserID = in.readString();
        this.fullname = in.readString();
        this.messageID = in.readString();
        this.localID = in.readString();
    }

    public static final Creator<TAPForwardFromModel> CREATOR = new Creator<TAPForwardFromModel>() {
        @Override
        public TAPForwardFromModel createFromParcel(Parcel source) {
            return new TAPForwardFromModel(source);
        }

        @Override
        public TAPForwardFromModel[] newArray(int size) {
            return new TAPForwardFromModel[size];
        }
    };
}
