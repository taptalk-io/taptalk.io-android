package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPCreateRoomResponse implements Parcelable {
    @Nullable @JsonProperty("room") private TAPRoomModel room;
    @Nullable @JsonProperty("participants") private List<TAPUserModel> participants;
    @Nullable @JsonProperty("adminUserIDs") @JsonAlias("admins")
    private List<String> admins;

    public TAPCreateRoomResponse(@Nullable TAPRoomModel room, @Nullable List<TAPUserModel> participants, @Nullable List<String> admins) {
        this.room = room;
        this.participants = participants;
        this.admins = admins;
    }

    public TAPCreateRoomResponse() {}

    @Nullable
    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(@Nullable TAPRoomModel room) {
        this.room = room;
    }

    @Nullable
    public List<TAPUserModel> getParticipants() {
        return participants;
    }

    public void setParticipants(@Nullable List<TAPUserModel> participants) {
        this.participants = participants;
    }

    @Nullable
    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(@Nullable List<String> admins) {
        this.admins = admins;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.room, flags);
        dest.writeTypedList(this.participants);
        dest.writeStringList(this.admins);
    }

    protected TAPCreateRoomResponse(Parcel in) {
        this.room = in.readParcelable(TAPRoomModel.class.getClassLoader());
        this.participants = in.createTypedArrayList(TAPUserModel.CREATOR);
        this.admins = in.createStringArrayList();
    }

    public static final Creator<TAPCreateRoomResponse> CREATOR = new Creator<TAPCreateRoomResponse>() {
        @Override
        public TAPCreateRoomResponse createFromParcel(Parcel source) {
            return new TAPCreateRoomResponse(source);
        }

        @Override
        public TAPCreateRoomResponse[] newArray(int size) {
            return new TAPCreateRoomResponse[size];
        }
    };
}
