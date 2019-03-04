package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public class TAPDataFileModel implements Parcelable {
    @Nullable @JsonProperty("fileID") private String fileID;
    @Nullable @JsonProperty("fileName") private String fileName;
    @Nullable @JsonProperty("mediaType") private String mediaType;
    @Nullable @JsonProperty("fileUri") private String fileUri;
    @Nullable @JsonProperty("size") private Number size;

    public TAPDataFileModel(@Nullable String fileID, @Nullable String fileName, @Nullable String mediaType, @Nullable String fileUri, @Nullable Number size) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.fileUri = fileUri;
        this.size = size;
    }

    public TAPDataFileModel() {
    }

    @Nullable
    public String getFileID() {
        return fileID;
    }

    public void setFileID(@Nullable String fileID) {
        this.fileID = fileID;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(@Nullable String mediaType) {
        this.mediaType = mediaType;
    }

    @Nullable
    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(@Nullable String fileUri) {
        this.fileUri = fileUri;
    }

    @Nullable
    public Number getSize() {
        return size;
    }

    public void setSize(@Nullable Number size) {
        this.size = size;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileID);
        dest.writeString(this.fileName);
        dest.writeString(this.mediaType);
        dest.writeString(this.fileUri);
        dest.writeSerializable(this.size);
    }

    protected TAPDataFileModel(Parcel in) {
        this.fileID = in.readString();
        this.fileName = in.readString();
        this.mediaType = in.readString();
        this.fileUri = in.readString();
        this.size = (Number) in.readSerializable();
    }

    public static final Parcelable.Creator<TAPDataFileModel> CREATOR = new Parcelable.Creator<TAPDataFileModel>() {
        @Override
        public TAPDataFileModel createFromParcel(Parcel source) {
            return new TAPDataFileModel(source);
        }

        @Override
        public TAPDataFileModel[] newArray(int size) {
            return new TAPDataFileModel[size];
        }
    };
}
