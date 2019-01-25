package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPFileDownloadRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("fileID") private String fileID;

    public TAPFileDownloadRequest(String roomID, String fileID) {
        this.roomID = roomID;
        this.fileID = fileID;
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
}
