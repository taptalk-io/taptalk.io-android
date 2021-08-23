package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPDataLocationModel implements Parcelable {
    @Nullable
    @JsonProperty("address")
    private String address;
    @Nullable
    @JsonProperty("latitude")
    private Double latitude;
    @Nullable
    @JsonProperty("longitude")
    private Double longitude;

    public TAPDataLocationModel(@Nullable String address, @Nullable Double latitude, @Nullable Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TAPDataLocationModel() {
    }

    public static TAPDataLocationModel Builder(String address, Double latitude, Double longitude) {
        return new TAPDataLocationModel(address, latitude, longitude);
    }

    public static TAPDataLocationModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPDataLocationModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Double getDoubleitude() {
        return longitude;
    }

    public void setDoubleitude(@Nullable Double longitude) {
        this.longitude = longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    protected TAPDataLocationModel(Parcel in) {
        this.address = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<TAPDataLocationModel> CREATOR = new Parcelable.Creator<TAPDataLocationModel>() {
        @Override
        public TAPDataLocationModel createFromParcel(Parcel source) {
            return new TAPDataLocationModel(source);
        }

        @Override
        public TAPDataLocationModel[] newArray(int size) {
            return new TAPDataLocationModel[size];
        }
    };
}
