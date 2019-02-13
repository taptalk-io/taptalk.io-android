package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPTypingModel implements Parcelable {
    @JsonProperty("roomID") private String roomID;

    public TAPTypingModel(String roomID) {
        this.roomID = roomID;
    }

    public TAPTypingModel() {
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
    }

    protected TAPTypingModel(Parcel in) {
        this.roomID = in.readString();
    }

    public static final Parcelable.Creator<TAPTypingModel> CREATOR = new Parcelable.Creator<TAPTypingModel>() {
        @Override
        public TAPTypingModel createFromParcel(Parcel source) {
            return new TAPTypingModel(source);
        }

        @Override
        public TAPTypingModel[] newArray(int size) {
            return new TAPTypingModel[size];
        }
    };
}
