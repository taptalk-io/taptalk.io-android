package io.taptalk.TapTalk.Model.RequestModel;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPUpdateRoomRequest implements Parcelable {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("name") private String roomName;

    public TAPUpdateRoomRequest() {}

    public TAPUpdateRoomRequest(String roomID, String roomName) {
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeString(this.roomName);
    }

    protected TAPUpdateRoomRequest(Parcel in) {
        this.roomID = in.readString();
        this.roomName = in.readString();
    }

    public static final Creator<TAPUpdateRoomRequest> CREATOR = new Creator<TAPUpdateRoomRequest>() {
        @Override
        public TAPUpdateRoomRequest createFromParcel(Parcel source) {
            return new TAPUpdateRoomRequest(source);
        }

        @Override
        public TAPUpdateRoomRequest[] newArray(int size) {
            return new TAPUpdateRoomRequest[size];
        }
    };
}
