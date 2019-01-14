package io.taptalk.TapTalk.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPDataImageModel implements Parcelable {
    @JsonProperty("fileID") private String fileID;
    @JsonProperty("mediaType") private String mediaType;
    @JsonProperty("width") private Long width;
    @JsonProperty("height") private Long height;
    @JsonProperty("caption") private String caption;
    @Nullable @JsonProperty("fileUri") private Uri fileUri;

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> dataImageMap = TAPUtils.getInstance().toHashMap(this);
        dataImageMap.remove("fileUri");
        return dataImageMap;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Nullable
    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(@Nullable Uri fileUri) {
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
        dest.writeValue(this.width);
        dest.writeValue(this.height);
        dest.writeString(this.caption);
        dest.writeParcelable(this.fileUri, flags);
    }

    public TAPDataImageModel() {
    }

    protected TAPDataImageModel(Parcel in) {
        this.fileID = in.readString();
        this.mediaType = in.readString();
        this.width = (Long) in.readValue(Long.class.getClassLoader());
        this.height = (Long) in.readValue(Long.class.getClassLoader());
        this.caption = in.readString();
        this.fileUri = in.readParcelable(Uri.class.getClassLoader());
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
