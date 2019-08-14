package io.taptalk.TapTalk.Model.RequestModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPCreateRoomRequest implements Parcelable {
    @JsonProperty("name") private String roomName;
    @JsonProperty("type") private int roomType;
    @JsonProperty("userIDs") private List<String> participantsIDs;

    public TAPCreateRoomRequest(String roomName, int roomType, List<String> participantsIDs) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.participantsIDs = participantsIDs;
    }

    public TAPCreateRoomRequest() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public List<String> getParticipantsIDs() {
        return participantsIDs;
    }

    public void setParticipantsIDs(List<String> participantsIDs) {
        this.participantsIDs = participantsIDs;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomName);
        dest.writeInt(this.roomType);
        dest.writeStringList(this.participantsIDs);
    }

    protected TAPCreateRoomRequest(Parcel in) {
        this.roomName = in.readString();
        this.roomType = in.readInt();
        this.participantsIDs = in.createStringArrayList();
    }

    public static final Creator<TAPCreateRoomRequest> CREATOR = new Creator<TAPCreateRoomRequest>() {
        @Override
        public TAPCreateRoomRequest createFromParcel(Parcel source) {
            return new TAPCreateRoomRequest(source);
        }

        @Override
        public TAPCreateRoomRequest[] newArray(int size) {
            return new TAPCreateRoomRequest[size];
        }
    };
}
