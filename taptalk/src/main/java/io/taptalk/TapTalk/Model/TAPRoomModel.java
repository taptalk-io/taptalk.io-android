package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPRoomModel implements Parcelable {

    @JsonProperty("roomID") @JsonAlias("id") private String roomID;
    @JsonProperty("name") private String roomName;
    @JsonProperty("color") private String roomColor;
    @JsonProperty("type") private int roomType;
    @JsonProperty("unreadCount") private int unreadCount;
    @JsonProperty("deleted") private long deletedTimestamp;
    @JsonProperty("isDeleted") private boolean isRoomDeleted;
    @Nullable @JsonProperty("imageURL") private TAPImageURL roomImage;
    @Nullable @JsonIgnore private List<TAPUserModel> groupParticipants;
    @Nullable @JsonIgnore private Integer numOfParticipants;
    @Nullable @JsonIgnore @JsonProperty("admins") private List<String> admins;
    @JsonIgnore private boolean isMuted;

    public TAPRoomModel(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomImage = roomImage;
        this.roomColor = roomColor;
    }

    public TAPRoomModel(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor, int unreadCount) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomImage = roomImage;
        this.roomColor = roomColor;
        this.unreadCount = unreadCount;
    }

    public TAPRoomModel() {
    }

    public static TAPRoomModel Builder(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor) {
        return new TAPRoomModel(roomID, roomName, roomType, roomImage, roomColor);
    }

    public static TAPRoomModel Builder(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor, int unreadCount) {
        return new TAPRoomModel(roomID, roomName, roomType, roomImage, roomColor, unreadCount);
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getDeletedTimestamp() {
        return deletedTimestamp;
    }

    public void setDeletedTimestamp(long deletedTimestamp) {
        this.deletedTimestamp = deletedTimestamp;
    }

    public boolean isRoomDeleted() {
        return isRoomDeleted;
    }

    public void setRoomDeleted(boolean roomDeleted) {
        isRoomDeleted = roomDeleted;
    }

    @Nullable
    public TAPImageURL getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(@Nullable TAPImageURL roomImage) {
        this.roomImage = roomImage;
    }

    @Nullable
    public List<TAPUserModel> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(@Nullable List<TAPUserModel> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }

    @Nullable
    public Integer getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(@Nullable Integer numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    @Nullable
    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(@Nullable List<String> admins) {
        this.admins = admins;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
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
        dest.writeInt(this.roomType);
        dest.writeInt(this.unreadCount);
        dest.writeLong(this.deletedTimestamp);
        dest.writeByte(this.isRoomDeleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.roomImage, flags);
        dest.writeTypedList(this.groupParticipants);
        dest.writeValue(this.numOfParticipants);
        dest.writeStringList(this.admins);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
    }

    protected TAPRoomModel(Parcel in) {
        this.roomID = in.readString();
        this.roomName = in.readString();
        this.roomColor = in.readString();
        this.roomType = in.readInt();
        this.unreadCount = in.readInt();
        this.deletedTimestamp = in.readLong();
        this.isRoomDeleted = in.readByte() != 0;
        this.roomImage = in.readParcelable(TAPImageURL.class.getClassLoader());
        this.groupParticipants = in.createTypedArrayList(TAPUserModel.CREATOR);
        this.numOfParticipants = (Integer) in.readValue(Integer.class.getClassLoader());
        this.admins = in.createStringArrayList();
        this.isMuted = in.readByte() != 0;
    }

    public static final Creator<TAPRoomModel> CREATOR = new Creator<TAPRoomModel>() {
        @Override
        public TAPRoomModel createFromParcel(Parcel source) {
            return new TAPRoomModel(source);
        }

        @Override
        public TAPRoomModel[] newArray(int size) {
            return new TAPRoomModel[size];
        }
    };
}
