package io.taptalk.TapTalk.Model.ResponseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPCountryListItem;

public class TAPCountryListResponse implements Parcelable {
    @JsonProperty("countries") private List<TAPCountryListItem> countries;

    public TAPCountryListResponse() {
    }

    public TAPCountryListResponse(List<TAPCountryListItem> countries) {
        this.countries = countries;
    }

    public List<TAPCountryListItem> getCountries() {
        return countries;
    }

    public void setCountries(List<TAPCountryListItem> countries) {
        this.countries = countries;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.countries);
    }

    protected TAPCountryListResponse(Parcel in) {
        this.countries = in.createTypedArrayList(TAPCountryListItem.CREATOR);
    }

    public static final Parcelable.Creator<TAPCountryListResponse> CREATOR = new Parcelable.Creator<TAPCountryListResponse>() {
        @Override
        public TAPCountryListResponse createFromParcel(Parcel source) {
            return new TAPCountryListResponse(source);
        }

        @Override
        public TAPCountryListResponse[] newArray(int size) {
            return new TAPCountryListResponse[size];
        }
    };
}
