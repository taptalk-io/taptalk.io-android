package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPOnlineStatusModel implements Parcelable {
    @JsonProperty("user") private TAPUserModel user;
    @JsonProperty("isOnline") private Boolean isOnline;
    @JsonProperty("lastActive") private Long lastActive;

    public TAPUserModel getUser() {
        return user;
    }

    public void setUser(TAPUserModel user) {
        this.user = user;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Long getLastActive() {
        return lastActive;
    }

    public void setLastActive(Long lastActive) {
        this.lastActive = lastActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeValue(this.isOnline);
        dest.writeValue(this.lastActive);
    }

    public TAPOnlineStatusModel(TAPUserModel user, Boolean isOnline, Long lastActive) {
        this.user = user;
        this.isOnline = isOnline;
        this.lastActive = lastActive;
    }

    public TAPOnlineStatusModel() {
    }

    public TAPOnlineStatusModel(Boolean isOnline, Long lastActive) {
        this.isOnline = isOnline;
        this.lastActive = lastActive;
    }

    public static TAPOnlineStatusModel Builder(TAPUserModel user) {
        return new TAPOnlineStatusModel(user, user.getOnline(), user.getLastActivity());
    }

    protected TAPOnlineStatusModel(Parcel in) {
        this.user = in.readParcelable(TAPUserModel.class.getClassLoader());
        this.isOnline = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lastActive = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<TAPOnlineStatusModel> CREATOR = new Parcelable.Creator<TAPOnlineStatusModel>() {
        @Override
        public TAPOnlineStatusModel createFromParcel(Parcel source) {
            return new TAPOnlineStatusModel(source);
        }

        @Override
        public TAPOnlineStatusModel[] newArray(int size) {
            return new TAPOnlineStatusModel[size];
        }
    };
}
