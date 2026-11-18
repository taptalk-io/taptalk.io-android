package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TAPSetUploadSessionDoneResponse {
    @JsonProperty("success") private Boolean success;
    @JsonProperty("message") private String message;
    @JsonProperty("missingChunkIndexes") private List<Integer> missingChunkIndexes;
    @JsonProperty("uploadedSize") private String uploadedSize;
    @JsonProperty("fileSize") private String fileSize;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Integer> getMissingChunkIndexes() {
        return missingChunkIndexes;
    }

    public void setMissingChunkIndexes(List<Integer> missingChunkIndexes) {
        this.missingChunkIndexes = missingChunkIndexes;
    }

    public String getUploadedSize() {
        return uploadedSize;
    }

    public void setUploadedSize(String uploadedSize) {
        this.uploadedSize = uploadedSize;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
