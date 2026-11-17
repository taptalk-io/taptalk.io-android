package io.taptalk.TapTalk.Model.RequestModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCreateUploadSessionRequest implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.mediaType);
        dest.writeLong(this.fileSize);
        dest.writeInt(this.chunkCount);
    }

    protected TAPCreateUploadSessionRequest(Parcel in) {
        this.fileName = in.readString();
        this.mediaType = in.readString();
        this.fileSize = in.readLong();
        this.chunkCount = in.readInt();
    }

    public static final Creator<TAPCreateUploadSessionRequest> CREATOR = new Creator<>() {
        @Override
        public TAPCreateUploadSessionRequest createFromParcel(Parcel source) {
            return new TAPCreateUploadSessionRequest(source);
        }

        @Override
        public TAPCreateUploadSessionRequest[] newArray(int size) {
            return new TAPCreateUploadSessionRequest[size];
        }
    };
}
