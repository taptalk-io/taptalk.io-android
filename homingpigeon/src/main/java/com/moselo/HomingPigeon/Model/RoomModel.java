package com.moselo.HomingPigeon.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class RoomModel {

    @JsonProperty("roomID") private String roomID;
    @JsonProperty("roomName") private String roomName;
    @JsonProperty("roomColor") private String roomColor;
    @JsonProperty("roomImage") private String roomImage;
    @JsonProperty("roomType") private int roomType;
    @JsonProperty("unreadCount") private int unreadCount;
    @Nullable @JsonProperty("groupParticipants") private List<UserModel> groupParticipants;
    @Nullable @JsonProperty("numOfParticipants") private Integer numOfParticipants;
    private boolean isMuted;
    private boolean isSelected;

    public RoomModel(String roomID, int roomType) {
        this.roomID = roomID;
        this.roomType = roomType;
    }

    public RoomModel() {
    }

    public static RoomModel Builder(String roomID, int roomType){
        return new RoomModel(roomID, roomType);
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

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
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
}
