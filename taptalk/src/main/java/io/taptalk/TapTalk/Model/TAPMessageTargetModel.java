package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPMessageTargetModel implements Parcelable {
    @Nullable
    @JsonProperty("targetType")
    private String targetType;
    @Nullable
    @JsonProperty("targetID")
    private String targetID;
    @Nullable
    @JsonProperty("targetXCID")
    private String targetXCID;
    @Nullable
    @JsonProperty("targetName")
    private String targetName;

    public static TAPMessageTargetModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPMessageTargetModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    @Nullable
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(@Nullable String targetType) {
        this.targetType = targetType;
    }

    @Nullable
    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(@Nullable String targetID) {
        this.targetID = targetID;
    }

    @Nullable
    public String getTargetXCID() {
        return targetXCID;
    }

    public void setTargetXCID(@Nullable String targetXCID) {
        this.targetXCID = targetXCID;
    }

    @Nullable
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(@Nullable String targetName) {
        this.targetName = targetName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.targetType);
        dest.writeString(this.targetID);
        dest.writeString(this.targetXCID);
        dest.writeString(this.targetName);
    }

    public TAPMessageTargetModel() {
    }

    protected TAPMessageTargetModel(Parcel in) {
        this.targetType = in.readString();
        this.targetID = in.readString();
        this.targetXCID = in.readString();
        this.targetName = in.readString();
    }

    public static final Creator<TAPMessageTargetModel> CREATOR = new Creator<TAPMessageTargetModel>() {
        @Override
        public TAPMessageTargetModel createFromParcel(Parcel source) {
            return new TAPMessageTargetModel(source);
        }

        @Override
        public TAPMessageTargetModel[] newArray(int size) {
            return new TAPMessageTargetModel[size];
        }
    };
}
