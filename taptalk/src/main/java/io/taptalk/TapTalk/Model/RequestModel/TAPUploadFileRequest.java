package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class TAPUploadFileRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("file") private File file;
    @JsonProperty("caption") private String caption;

    public TAPUploadFileRequest(String roomID, File file, String caption) {
        this.roomID = roomID;
        this.file = file;
        this.caption = caption;
    }

    public TAPUploadFileRequest() {}

    public static TAPUploadFileRequest Builder(String roomID, File file, String caption) {
        return new TAPUploadFileRequest(roomID, file, caption);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
