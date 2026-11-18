package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPSetUploadSessionDoneRequest {
    @JsonProperty("uploadSessionID") private String uploadSessionID;

    public TAPSetUploadSessionDoneRequest(String uploadSessionID) {
        this.uploadSessionID = uploadSessionID;
    }

    public TAPSetUploadSessionDoneRequest() {
    }

    public String getUploadSessionID() {
        return uploadSessionID;
    }

    public void setUploadSessionID(String uploadSessionID) {
        this.uploadSessionID = uploadSessionID;
    }
}
