package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPUploadFileRequest {
    @JsonProperty("roomID") private String roomID;
    @JsonProperty("file") private String fileUri;
    @JsonProperty("caption") private String caption;

    public TAPUploadFileRequest(String roomID, String fileUri, String caption) {
        this.roomID = roomID;
        this.fileUri = fileUri;
        this.caption = caption;
    }

    public TAPUploadFileRequest() {}

    public static TAPUploadFileRequest Builder(String roomID, String fileUri, String caption) {
        return new TAPUploadFileRequest(roomID, fileUri, caption);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
