package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TapLongPressMenuItem implements Parcelable {
    private String id;
    private String text;
    private int iconRes;

    public TapLongPressMenuItem() {
    }

    public TapLongPressMenuItem(String id, String text, int iconRes) {
        this.id = id;
        this.text = text;
        this.iconRes = iconRes;
    }

    public static TapLongPressMenuItem fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TapLongPressMenuItem>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeInt(this.iconRes);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.text = source.readString();
        this.iconRes = source.readInt();
    }

    protected TapLongPressMenuItem(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.iconRes = in.readInt();
    }

    public static final Parcelable.Creator<TapLongPressMenuItem> CREATOR = new Parcelable.Creator<TapLongPressMenuItem>() {
        @Override
        public TapLongPressMenuItem createFromParcel(Parcel source) {
            return new TapLongPressMenuItem(source);
        }

        @Override
        public TapLongPressMenuItem[] newArray(int size) {
            return new TapLongPressMenuItem[size];
        }
    };
}
