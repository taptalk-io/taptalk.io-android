package com.moselo.HomingPigeon.Data.RecentSearch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;

@Entity(tableName = "Recent_Search")
public class TAPRecentSearchEntity {
    @PrimaryKey @NonNull @ColumnInfo(name = "roomID") private String roomID;
    @ColumnInfo(name = "name") private String roomName;
    @ColumnInfo(name = "color") private String roomColor;
    @ColumnInfo(name = "type") private Integer roomType;
    @ColumnInfo(name = "roomImage") private String roomImage;
    @ColumnInfo(name = "created") private Long created;

    @Ignore
    public TAPRecentSearchEntity(HpRoomModel roomModel, Long created) {
        this.roomID = roomModel.getRoomID();
        this.roomName = roomModel.getRoomName();
        this.roomColor = roomModel.getRoomColor();
        this.roomType = roomModel.getRoomType();
        this.roomImage = TAPUtils.getInstance().toJsonString(roomModel.getRoomImage());
        this.created = created;
    }

    public TAPRecentSearchEntity() {
    }

    public static TAPRecentSearchEntity Builder(HpSearchChatModel searchChatModel) {
        HpRoomModel room = searchChatModel.getRoom();
        if (null == room) return null;

        TAPRecentSearchEntity model = new TAPRecentSearchEntity();
        model.setRoomID(room.getRoomID());
        model.setRoomName(room.getRoomName());
        model.setRoomColor(room.getRoomColor());
        model.setRoomType(room.getRoomType());
        model.setRoomImage(TAPUtils.getInstance().toJsonString(room.getRoomImage()));
        model.setCreated(System.currentTimeMillis());
        return model;
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

    public Integer getRoomType() {
        return roomType;
    }

    public void setRoomType(Integer roomType) {
        this.roomType = roomType;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}
