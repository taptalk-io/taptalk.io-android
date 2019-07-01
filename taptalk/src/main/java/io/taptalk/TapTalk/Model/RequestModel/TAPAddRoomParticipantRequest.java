package io.taptalk.TapTalk.Model.RequestModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPAddRoomParticipantRequest implements Parcelable {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("userIDs") private List<String> userIDs;

    public TAPAddRoomParticipantRequest(String roomID, List<String> userIDs) {
        this.roomID = roomID;
        this.userIDs = userIDs;
    }

    public TAPAddRoomParticipantRequest() {}

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeStringList(this.userIDs);
    }

    protected TAPAddRoomParticipantRequest(Parcel in) {
        this.roomID = in.readString();
        this.userIDs = in.createStringArrayList();
    }

    public static final Creator<TAPAddRoomParticipantRequest> CREATOR = new Creator<TAPAddRoomParticipantRequest>() {
        @Override
        public TAPAddRoomParticipantRequest createFromParcel(Parcel source) {
            return new TAPAddRoomParticipantRequest(source);
        }

        @Override
        public TAPAddRoomParticipantRequest[] newArray(int size) {
            return new TAPAddRoomParticipantRequest[size];
        }
    };
}
