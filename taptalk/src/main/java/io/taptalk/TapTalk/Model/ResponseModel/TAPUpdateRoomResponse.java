package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TAPRoomModel;

public class TAPUpdateRoomResponse implements Parcelable {
    @Nullable
    @JsonProperty("room")
    private TAPRoomModel room;

    @Nullable
    @JsonProperty("expiredAt")
    private Long expiredAt;

    public TAPUpdateRoomResponse() {
    }

    public TAPUpdateRoomResponse(@Nullable TAPRoomModel room) {
        this.room = room;
    }

    @Nullable
    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(@Nullable TAPRoomModel room) {
        this.room = room;
    }

    @Nullable
    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(@Nullable Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.room, flags);
        dest.writeLong(this.expiredAt);
    }

    protected TAPUpdateRoomResponse(Parcel in) {
        this.room = in.readParcelable(TAPRoomModel.class.getClassLoader());
        this.expiredAt = in.readLong();
    }

    public static final Creator<TAPUpdateRoomResponse> CREATOR = new Creator<TAPUpdateRoomResponse>() {
        @Override
        public TAPUpdateRoomResponse createFromParcel(Parcel source) {
            return new TAPUpdateRoomResponse(source);
        }

        @Override
        public TAPUpdateRoomResponse[] newArray(int size) {
            return new TAPUpdateRoomResponse[size];
        }
    };
}
