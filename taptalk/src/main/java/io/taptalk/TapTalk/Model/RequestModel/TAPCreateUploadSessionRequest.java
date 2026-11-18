package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCreateUploadSessionRequest {
    @JsonProperty("fileName") private String fileName;
    @JsonProperty("mediaType") private String mediaType;
    @JsonProperty("fileSize") private long fileSize;
    @JsonProperty("chunkCount") private int chunkCount;

    public TAPCreateUploadSessionRequest(String fileName, String mediaType, long fileSize, int chunkCount) {
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.chunkCount = chunkCount;
    }

    public TAPCreateUploadSessionRequest() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }
}
