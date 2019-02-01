package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_WIDTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;

public class TAPDataImageModel implements Parcelable {
    @Nullable @JsonProperty("fileID") private String fileID;
    @Nullable @JsonProperty("mediaType") private String mediaType;
    @Nullable @JsonProperty("size") private Long size;
    @Nullable @JsonProperty("width") private Integer width;
    @Nullable @JsonProperty("height") private Integer height;
    @Nullable @JsonProperty("caption") private String caption;
    @Nullable @JsonProperty("fileUri") private String fileUri;
    @Nullable @JsonProperty("thumbnail") private String thumbnail;

    public TAPDataImageModel(@Nullable Integer width, @Nullable Integer height, @Nullable String caption,
                             @Nullable String thumbnail, @Nullable String fileUri) {
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.fileUri = fileUri;
        this.thumbnail = thumbnail;
    }

    public TAPDataImageModel(@Nullable String fileID, @Nullable String thumbnail, @Nullable String mediaType,
                             @Nullable Long size, @Nullable Integer width,
                             @Nullable Integer height, @Nullable String caption) {
        this.fileID = fileID;
        this.mediaType = mediaType;
        this.size = size;
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.thumbnail = thumbnail;
    }

    public TAPDataImageModel(HashMap<String, Object> imageDataMap) {
        this.fileID = (String) imageDataMap.get(FILE_ID);
        this.mediaType = (String) imageDataMap.get(MEDIA_TYPE);
        this.size = (Long) imageDataMap.get(IMAGE_SIZE);
        this.width = (Integer) imageDataMap.get(IMAGE_WIDTH);
        this.height = (Integer) imageDataMap.get(IMAGE_HEIGHT);
        this.caption = (String) imageDataMap.get(CAPTION);
        this.fileUri = (String) imageDataMap.get(FILE_URI);
        this.thumbnail = (String) imageDataMap.get(THUMBNAIL);
    }

    public TAPDataImageModel() {
    }

    public static TAPDataImageModel Builder(String fileID, @Nullable String thumbnail, String mediaType, Long size,
                                            Integer width, Integer height, String caption) {
        return new TAPDataImageModel(fileID, thumbnail, mediaType, size, width, height, caption);
    }

    public HashMap<String, Object> toHashMapWithoutFileUri() {
        HashMap<String, Object> dataMap = TAPUtils.getInstance().toHashMap(this);
        dataMap.remove(FILE_URI);
        return dataMap;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> dataMap = TAPUtils.getInstance().toHashMap(this);
        return dataMap;
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
    public Integer getWidth() {
        return width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }

    public void setHeight(@Nullable Integer height) {
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

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@Nullable String thumbnail) {
        this.thumbnail = thumbnail;
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
        dest.writeString(this.thumbnail);
    }

    protected TAPDataImageModel(Parcel in) {
        this.fileID = in.readString();
        this.mediaType = in.readString();
        this.size = (Long) in.readValue(Long.class.getClassLoader());
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.caption = in.readString();
        this.fileUri = in.readString();
        this.thumbnail = in.readString();
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
