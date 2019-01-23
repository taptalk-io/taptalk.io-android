package io.taptalk.TapTalk.Model.RequestModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPFileDownloadRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("fileID") private String fileID;
    @Nullable @JsonProperty("isThumbnail") private Boolean isThumbnail;

    public TAPFileDownloadRequest(String roomID, String fileID) {
        this.roomID = roomID;
        this.fileID = fileID;
    }

    public TAPFileDownloadRequest(String roomID, String fileID, @Nullable Boolean isThumbnail) {
        this.roomID = roomID;
        this.fileID = fileID;
        this.isThumbnail = isThumbnail;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    @Nullable
    public Boolean getThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(@Nullable Boolean thumbnail) {
        isThumbnail = thumbnail;
    }
}
