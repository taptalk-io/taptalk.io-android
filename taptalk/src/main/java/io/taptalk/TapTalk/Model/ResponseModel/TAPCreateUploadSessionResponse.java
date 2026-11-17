package io.taptalk.TapTalk.Model.ResponseModel;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCreateUploadSessionResponse {
    @Nullable @JsonProperty("uploadSessionID") private String uploadSessionID;
    @Nullable @JsonProperty("expiryTime") private Long expiryTime;

    public TAPCreateUploadSessionResponse(@Nullable String uploadSessionID, @Nullable Long expiryTime) {
        this.uploadSessionID = uploadSessionID;
        this.expiryTime = expiryTime;
    }

    public TAPCreateUploadSessionResponse() {
    }

    @Nullable
    public String getUploadSessionID() {
        return uploadSessionID;
    }

    public void setUploadSessionID(@Nullable String uploadSessionID) {
        this.uploadSessionID = uploadSessionID;
    }

    @Nullable
    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(@Nullable Long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
