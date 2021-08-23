package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPTypingModel implements Parcelable {
    @JsonProperty("roomID") private String roomID;
    @Nullable
    @JsonProperty("user")
    private TAPUserModel user;

    public TAPTypingModel(String roomID) {
        this.roomID = roomID;
    }

    public TAPTypingModel() {
    }

    public static TAPTypingModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPTypingModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Nullable
    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(@Nullable TAPUserModel user) {
        this.user = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeParcelable(this.user, flags);
    }

    protected TAPTypingModel(Parcel in) {
        this.roomID = in.readString();
        this.user = in.readParcelable(TAPUserModel.class.getClassLoader());
    }

    public static final Creator<TAPTypingModel> CREATOR = new Creator<TAPTypingModel>() {
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
