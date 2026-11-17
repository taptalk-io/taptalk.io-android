package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPCreateUploadSessionResponse implements Parcelable {
    @Nullable @JsonProperty("uploadSessionID") private String uploadSessionID;
    @Nullable @JsonProperty("expiryTime") private Long expiryTime;

    public TAPCreateUploadSessionResponse(@Nullable String uploadSessionID, @Nullable Long expiryTime) {
        this.uploadSessionID = uploadSessionID;
        this.expiryTime = expiryTime;
    }

    public TAPCreateUploadSessionResponse() {
    }

    @Nullable
    public String getUploadSessionID() {
        return uploadSessionID;
    }

    public void setUploadSessionID(@Nullable String uploadSessionID) {
        this.uploadSessionID = uploadSessionID;
    }

    @Nullable
    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(@Nullable Long expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uploadSessionID);
        if (this.expiryTime != null) {
            dest.writeLong(this.expiryTime);
        }
    }

    protected TAPCreateUploadSessionResponse(Parcel in) {
        this.uploadSessionID = in.readString();
        this.expiryTime = in.readLong();
    }

    public static final Creator<TAPCreateUploadSessionResponse> CREATOR = new Creator<>() {
        @Override
        public TAPCreateUploadSessionResponse createFromParcel(Parcel source) {
            return new TAPCreateUploadSessionResponse(source);
        }

        @Override
        public TAPCreateUploadSessionResponse[] newArray(int size) {
            return new TAPCreateUploadSessionResponse[size];
        }
    };
}
