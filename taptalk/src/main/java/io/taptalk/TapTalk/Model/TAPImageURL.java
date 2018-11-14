package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPImageURL implements Parcelable {
    @JsonProperty("fullsize") private String fullsize;
    @JsonProperty("thumbnail") private String thumbnail;

    public TAPImageURL() { }

    // TODO: 28/09/18 diilangin nnti setelah fix
    public static TAPImageURL BuilderDummy() {
        TAPImageURL dummy = new TAPImageURL();
        dummy.setFullsize("https://instagram.fcgk6-1.fna.fbcdn.net/vp/a957263ad8322a1661f604e2942f1acc/5C5B8666/t51.2885-15/e35/41659851_331857600921431_1280889939227049984_n.jpg");
        dummy.setThumbnail("https://instagram.fcgk6-1.fna.fbcdn.net/vp/a957263ad8322a1661f604e2942f1acc/5C5B8666/t51.2885-15/e35/41659851_331857600921431_1280889939227049984_n.jpg");
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

    protected TAPImageURL(Parcel in) {
        this.fullsize = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<TAPImageURL> CREATOR = new Parcelable.Creator<TAPImageURL>() {
        @Override
        public TAPImageURL createFromParcel(Parcel source) {
            return new TAPImageURL(source);
        }

        @Override
        public TAPImageURL[] newArray(int size) {
            return new TAPImageURL[size];
        }
    };
}
