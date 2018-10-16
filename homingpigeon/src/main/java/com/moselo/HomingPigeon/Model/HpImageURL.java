package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpImageURL implements Parcelable {
    @JsonProperty("fullsize") private String fullsize;
    @JsonProperty("thumbnail") private String thumbnail;

    public HpImageURL() { }

    // TODO: 28/09/18 diilangin nnti setelah fix
    public static HpImageURL BuilderDummy() {
        HpImageURL dummy = new HpImageURL();
        dummy.setFullsize("https://scontent-atl3-1.cdninstagram.com/vp/abc8eb103edb23d5c0b640a0e3b84b54/5C605C18/t51.2885-15/e35/37722110_423966801343577_5720254155977654272_n.jpg");
        dummy.setThumbnail("https://scontent-atl3-1.cdninstagram.com/vp/abc8eb103edb23d5c0b640a0e3b84b54/5C605C18/t51.2885-15/e35/37722110_423966801343577_5720254155977654272_n.jpg");
        return dummy;
    }

    public String getFullsize() {
        return fullsize;
    }

    public void setFullsize(String fullsize) {
        this.fullsize = fullsize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullsize);
        dest.writeString(this.thumbnail);
    }

    protected HpImageURL(Parcel in) {
        this.fullsize = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<HpImageURL> CREATOR = new Parcelable.Creator<HpImageURL>() {
        @Override
        public HpImageURL createFromParcel(Parcel source) {
            return new HpImageURL(source);
        }

        @Override
        public HpImageURL[] newArray(int size) {
            return new HpImageURL[size];
        }
    };
}
