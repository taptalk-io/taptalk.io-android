package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moselo.HomingPigeon.Manager.TAPChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;

import java.util.List;

public class HpRoomModel implements Parcelable {

    @JsonProperty("roomID") @JsonAlias("id") private String roomID;
    @JsonProperty("name") private String roomName;
    @JsonProperty("color") private String roomColor;
    @JsonProperty("type") private int roomType;
    @JsonProperty("unreadCount") private int unreadCount;
    @Nullable @JsonProperty("imageURL") private HpImageURL roomImage;
    @Nullable @JsonProperty("groupParticipants") private List<HpUserModel> groupParticipants;
    @Nullable @JsonProperty("numOfParticipants") private Integer numOfParticipants;
    private boolean isMuted;

    public HpRoomModel(String roomID, String roomName, int roomType, HpImageURL roomImage, String roomColor) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomType = roomType;
        // TODO: 11/10/18 INI NNTI DI ILANGIN 
        this.roomImage = HpImageURL.BuilderDummy();
        this.roomColor = roomColor;
    }

    public HpRoomModel() {
    }

    public static HpRoomModel Builder(String roomID, String roomName, int roomType, HpImageURL roomImage, String roomColor){
        return new HpRoomModel(roomID, roomName, roomType, roomImage, roomColor);
    }

    public static HpRoomModel BuilderDummy(){
        return new HpRoomModel(TAPChatManager.getInstance().arrangeRoomId(HpDataManager.getInstance().getActiveUser().getUserID(), "4"), "Kevin Reynaldo", 1, HpImageURL.BuilderDummy(), "#2eccad");
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

    @Nullable
    public HpImageURL getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(@Nullable HpImageURL roomImage) {
        this.roomImage = roomImage;
    }

    @Nullable
    public List<HpUserModel> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(@Nullable List<HpUserModel> groupParticipants) {
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
    }

    protected HpRoomModel(Parcel in) {
        this.roomID = in.readString();
        this.roomName = in.readString();
        this.roomColor = in.readString();
        this.roomImage = in.readParcelable(HpImageURL.class.getClassLoader());
        this.roomType = in.readInt();
        this.unreadCount = in.readInt();
        this.groupParticipants = in.createTypedArrayList(HpUserModel.CREATOR);
        this.numOfParticipants = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isMuted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<HpRoomModel> CREATOR = new Parcelable.Creator<HpRoomModel>() {
        @Override
        public HpRoomModel createFromParcel(Parcel source) {
            return new HpRoomModel(source);
        }

        @Override
        public HpRoomModel[] newArray(int size) {
            return new HpRoomModel[size];
        }
    };
}
