package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCountryListItem;

public class TAPRegisterResponse implements Parcelable {
    @JsonProperty("userID") private String userID;
    @JsonProperty("ticket") private String ticket;

    public TAPRegisterResponse() {
    }

    public TAPRegisterResponse(String userID, String ticket) {
        this.userID = userID;
        this.ticket = ticket;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.ticket);
    }

    protected TAPRegisterResponse(Parcel in) {
        this.userID = in.readString();
        this.ticket = in.readString();
    }

    public static final Creator<TAPRegisterResponse> CREATOR = new Creator<TAPRegisterResponse>() {
        @Override
        public TAPRegisterResponse createFromParcel(Parcel source) {
            return new TAPRegisterResponse(source);
        }

        @Override
        public TAPRegisterResponse[] newArray(int size) {
            return new TAPRegisterResponse[size];
        }
    };
}
