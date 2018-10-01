package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class RoomModel implements Parcelable {

    @JsonProperty("roomID") @JsonAlias("id") private String roomID;
    @JsonProperty("name") private String roomName;
    @JsonProperty("color") private String roomColor;
    @JsonProperty("imageURL") private ImageURL roomImage;
    @JsonProperty("type") private int roomType;
    @JsonProperty("unreadCount") private int unreadCount;
    @Nullable @JsonProperty("groupParticipants") private List<UserModel> groupParticipants;
    @Nullable @JsonProperty("numOfParticipants") private Integer numOfParticipants;
    private boolean isMuted;
    private boolean isSelected;

    public RoomModel(String roomID, String roomName, int roomType) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomType = roomType;
    }

    public RoomModel() {
    }

    public static RoomModel Builder(String roomID, String roomName, int roomType){
        return new RoomModel(roomID, roomName, roomType);
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

    public String getRoomColor() {
        return roomColor;
    }

    public void setRoomColor(String roomColor) {
        this.roomColor = roomColor;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public ImageURL getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(ImageURL roomImage) {
        this.roomImage = roomImage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Nullable
    public List<UserModel> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(@Nullable List<UserModel> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }

    @Nullable
    public Integer getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(@Nullable Integer numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeString(this.roomName);
        dest.writeString(this.roomColor);
        dest.writeParcelable(this.roomImage, flags);
        dest.writeInt(this.roomType);
        dest.writeInt(this.unreadCount);
        dest.writeTypedList(this.groupParticipants);
        dest.writeValue(this.numOfParticipants);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected RoomModel(Parcel in) {
        this.roomID = in.readString();
        this.roomName = in.readString();
        this.roomColor = in.readString();
        this.roomImage = in.readParcelable(ImageURL.class.getClassLoader());
        this.roomType = in.readInt();
        this.unreadCount = in.readInt();
        this.groupParticipants = in.createTypedArrayList(UserModel.CREATOR);
        this.numOfParticipants = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isMuted = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RoomModel> CREATOR = new Parcelable.Creator<RoomModel>() {
        @Override
        public RoomModel createFromParcel(Parcel source) {
            return new RoomModel(source);
        }

        @Override
        public RoomModel[] newArray(int size) {
            return new RoomModel[size];
        }
    };
}
