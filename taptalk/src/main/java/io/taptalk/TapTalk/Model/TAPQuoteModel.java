package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPQuoteModel implements Parcelable {
    @JsonProperty("title") private String title;
    @JsonProperty("content") private String content;
    @JsonProperty("fileID") private String fileID;
    @JsonProperty("imageURLString") private String imageURLString;
    @JsonProperty("fileType") private String fileType;

    public TAPQuoteModel(String title, String content, String fileID, String imageURLString, String fileType) {
        this.title = title;
        this.content = content;
        this.fileID = fileID;
        this.imageURLString = imageURLString;
        this.fileType = fileType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getImageURLString() {
        return imageURLString;
    }

    public void setImageURLString(String imageURLString) {
        this.imageURLString = imageURLString;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.fileID);
        dest.writeString(this.imageURLString);
        dest.writeString(this.fileType);
    }

    public TAPQuoteModel() {
    }

    protected TAPQuoteModel(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.fileID = in.readString();
        this.imageURLString = in.readString();
        this.fileType = in.readString();
    }

    public static final Creator<TAPQuoteModel> CREATOR = new Creator<TAPQuoteModel>() {
        @Override
        public TAPQuoteModel createFromParcel(Parcel source) {
            return new TAPQuoteModel(source);
        }

        @Override
        public TAPQuoteModel[] newArray(int size) {
            return new TAPQuoteModel[size];
        }
    };
}
