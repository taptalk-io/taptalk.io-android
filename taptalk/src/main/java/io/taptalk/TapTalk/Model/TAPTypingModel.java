package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPTypingModel implements Parcelable {
    @JsonProperty("roomID") private String roomID;
    @Nullable @JsonProperty("user") private TAPUserModel userModel;

    public TAPTypingModel(String roomID, @Nullable TAPUserModel userModel) {
        this.roomID = roomID;
        this.userModel = userModel;
    }

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

    @Nullable
    public TAPUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(@Nullable TAPUserModel userModel) {
        this.userModel = userModel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeParcelable(this.userModel, flags);
    }

    protected TAPTypingModel(Parcel in) {
        this.roomID = in.readString();
        this.userModel = in.readParcelable(TAPUserModel.class.getClassLoader());
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
