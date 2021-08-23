package io.taptalk.TapTalk.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPMediaPreviewModel implements Parcelable {
    private Uri uri;
    private String caption;
    private Boolean sizeExceedsLimit;
    private int type;
    private boolean isSelected, isLoading;

    public TAPMediaPreviewModel(Uri uri, int type, boolean isSelected) {
        this.uri = uri;
        this.type = type;
        this.isSelected = isSelected;
        this.caption = "";
    }

    public static TAPMediaPreviewModel Builder(Uri uri, int type, boolean isSelected) {
        return new TAPMediaPreviewModel(uri, type, isSelected);
    }

    public static TAPMediaPreviewModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPMediaPreviewModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean isSizeExceedsLimit() {
        return sizeExceedsLimit;
    }

    public void setSizeExceedsLimit(Boolean sizeExceedsLimit) {
        this.sizeExceedsLimit = sizeExceedsLimit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.caption);
        dest.writeValue(this.sizeExceedsLimit);
        dest.writeInt(this.type);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
    }

    protected TAPMediaPreviewModel(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.caption = in.readString();
        this.sizeExceedsLimit = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.type = in.readInt();
        this.isSelected = in.readByte() != 0;
        this.isLoading = in.readByte() != 0;
    }

    public static final Creator<TAPMediaPreviewModel> CREATOR = new Creator<TAPMediaPreviewModel>() {
        @Override
        public TAPMediaPreviewModel createFromParcel(Parcel source) {
            return new TAPMediaPreviewModel(source);
        }

        @Override
        public TAPMediaPreviewModel[] newArray(int size) {
            return new TAPMediaPreviewModel[size];
        }
    };
}
