package io.taptalk.TapTalk.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class TAPImagePreviewModel implements Parcelable {
    private Uri imageUris;
    private boolean isSelected;

    public TAPImagePreviewModel(Uri imageUris, boolean isSelected) {
        this.imageUris = imageUris;
        this.isSelected = isSelected;
    }

    public static TAPImagePreviewModel Builder(Uri imageUris, boolean isSelected) {
        return new TAPImagePreviewModel(imageUris, isSelected);
    }

    public Uri getImageUris() {
        return imageUris;
    }

    public void setImageUris(Uri imageUris) {
        this.imageUris = imageUris;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.imageUris, flags);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected TAPImagePreviewModel(Parcel in) {
        this.imageUris = in.readParcelable(Uri.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TAPImagePreviewModel> CREATOR = new Parcelable.Creator<TAPImagePreviewModel>() {
        @Override
        public TAPImagePreviewModel createFromParcel(Parcel source) {
            return new TAPImagePreviewModel(source);
        }

        @Override
        public TAPImagePreviewModel[] newArray(int size) {
            return new TAPImagePreviewModel[size];
        }
    };
}
