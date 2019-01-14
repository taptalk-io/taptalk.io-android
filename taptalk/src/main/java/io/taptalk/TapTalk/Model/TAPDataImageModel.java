package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPDataImageModel implements Parcelable {
    @Nullable @JsonProperty("fileID") private String fileID;
    @Nullable @JsonProperty("mediaType") private String mediaType;
    @Nullable @JsonProperty("size") private Long size;
    @Nullable @JsonProperty("width") private Long width;
    @Nullable @JsonProperty("height") private Long height;
    @Nullable @JsonProperty("caption") private String caption;
    @Nullable @JsonProperty("fileUri") private String fileUri;

    public TAPDataImageModel(@Nullable String fileUri, @Nullable String caption) {
        this.fileUri = fileUri;
        this.caption = caption;
    }

    public TAPDataImageModel() {
    }

    @Nullable
    public String getFileID() {
        return fileID;
    }

    public void setFileID(@Nullable String fileID) {
        this.fileID = fileID;
    }

    @Nullable
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(@Nullable String mediaType) {
        this.mediaType = mediaType;
    }

    @Nullable
    public Long getSize() {
        return size;
    }

    public void setSize(@Nullable Long size) {
        this.size = size;
    }

    @Nullable
    public Long getWidth() {
        return width;
    }

    public void setWidth(@Nullable Long width) {
        this.width = width;
    }

    @Nullable
    public Long getHeight() {
        return height;
    }

    public void setHeight(@Nullable Long height) {
        this.height = height;
    }

    @Nullable
    public String getCaption() {
        return caption;
    }

    public void setCaption(@Nullable String caption) {
        this.caption = caption;
    }

    @Nullable
    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(@Nullable String fileUri) {
        this.fileUri = fileUri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileID);
        dest.writeString(this.mediaType);
        dest.writeValue(this.size);
        dest.writeValue(this.width);
        dest.writeValue(this.height);
        dest.writeString(this.caption);
        dest.writeString(this.fileUri);
    }

    protected TAPDataImageModel(Parcel in) {
        this.fileID = in.readString();
        this.mediaType = in.readString();
        this.size = (Long) in.readValue(Long.class.getClassLoader());
        this.width = (Long) in.readValue(Long.class.getClassLoader());
        this.height = (Long) in.readValue(Long.class.getClassLoader());
        this.caption = in.readString();
        this.fileUri = in.readString();
    }

    public static final Creator<TAPDataImageModel> CREATOR = new Creator<TAPDataImageModel>() {
        @Override
        public TAPDataImageModel createFromParcel(Parcel source) {
            return new TAPDataImageModel(source);
        }

        @Override
        public TAPDataImageModel[] newArray(int size) {
            return new TAPDataImageModel[size];
        }
    };
}
