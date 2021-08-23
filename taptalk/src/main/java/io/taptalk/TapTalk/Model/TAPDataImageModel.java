package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.WIDTH;

public class TAPDataImageModel implements Parcelable {
    @Nullable
    @JsonProperty("fileID")
    private String fileID;
    @Nullable
    @JsonProperty("url")
    private String fileURL;
    @Nullable
    @JsonProperty("mediaType")
    private String mediaType;
    @Nullable
    @JsonProperty("size")
    private Number size;
    @Nullable
    @JsonProperty("width")
    private Number width;
    @Nullable
    @JsonProperty("height")
    private Number height;
    @Nullable
    @JsonProperty("caption")
    private String caption;
    @Nullable
    @JsonProperty("fileUri")
    private String fileUri;
    @Nullable
    @JsonProperty("thumbnail")
    private String thumbnail;

    public TAPDataImageModel(@Nullable Number width,
                             @Nullable Number height,
                             @Nullable Number size,
                             @Nullable String caption,
                             @Nullable String thumbnail,
                             @Nullable String fileUri
    ) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.caption = caption;
        this.fileUri = fileUri;
        this.thumbnail = thumbnail;
    }

    public TAPDataImageModel(@Nullable String fileID,
                             @Nullable String fileURL,
                             @Nullable String thumbnail,
                             @Nullable String mediaType,
                             @Nullable Number size,
                             @Nullable Number width,
                             @Nullable Number height,
                             @Nullable String caption
    ) {
        this.fileID = fileID;
        this.fileURL = fileURL;
        this.mediaType = mediaType;
        this.size = size;
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.thumbnail = thumbnail;
    }

    public TAPDataImageModel(HashMap<String, Object> imageDataMap) {
        this.fileID = (String) imageDataMap.get(FILE_ID);
        this.fileURL = (String) imageDataMap.get(FILE_URL);
        this.mediaType = (String) imageDataMap.get(MEDIA_TYPE);
        this.size = (Number) imageDataMap.get(SIZE);
        this.width = (Number) imageDataMap.get(WIDTH);
        this.height = (Number) imageDataMap.get(HEIGHT);
        this.caption = (String) imageDataMap.get(CAPTION);
        this.fileUri = (String) imageDataMap.get(FILE_URI);
        this.thumbnail = (String) imageDataMap.get(THUMBNAIL);
    }

    public TAPDataImageModel() {
    }

    public static TAPDataImageModel Builder(String fileID,
                                            String fileURL,
                                            @Nullable String thumbnail,
                                            String mediaType,
                                            Number size,
                                            Number width,
                                            Number height,
                                            String caption
    ) {
        return new TAPDataImageModel(fileID, fileURL, thumbnail, mediaType, size, width, height, caption);
    }

    public HashMap<String, Object> toHashMapWithoutFileUri() {
        HashMap<String, Object> dataMap = TAPUtils.toHashMap(this);
        if (null != dataMap) {
            dataMap.remove(FILE_URI);
        }
        return dataMap;
    }

    public static TAPDataImageModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPDataImageModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
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
    public Number getSize() {
        return size;
    }

    public void setSize(@Nullable Number size) {
        this.size = size;
    }

    @Nullable
    public Number getWidth() {
        return width;
    }

    public void setWidth(@Nullable Number width) {
        this.width = width;
    }

    @Nullable
    public Number getHeight() {
        return height;
    }

    public void setHeight(@Nullable Number height) {
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
        dest.writeString(this.fileURL);
        dest.writeString(this.mediaType);
        dest.writeSerializable(this.size);
        dest.writeSerializable(this.width);
        dest.writeSerializable(this.height);
        dest.writeString(this.caption);
        dest.writeString(this.fileUri);
        dest.writeString(this.thumbnail);
    }

    protected TAPDataImageModel(Parcel in) {
        this.fileID = in.readString();
        this.fileURL = in.readString();
        this.mediaType = in.readString();
        this.size = (Number) in.readSerializable();
        this.width = (Number) in.readSerializable();
        this.height = (Number) in.readSerializable();
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
