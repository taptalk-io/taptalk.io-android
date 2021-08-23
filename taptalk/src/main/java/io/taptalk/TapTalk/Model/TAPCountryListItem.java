package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPCountryListItem implements Parcelable {
    @JsonProperty("id") private int countryID;
    @JsonProperty("commonName") private String commonName;
    @JsonProperty("officialName") private String officialName;
    @JsonProperty("flagIconURL") private String flagIconUrl;
    @JsonProperty("iso2Code") private String iso2Code;
    @JsonProperty("iso3Code") private String iso3Code;
    @JsonProperty("callingCode") private String callingCode;
    @JsonProperty("currencyCode") private String currencyCode;
    @JsonProperty("isEnabled") private boolean isEnabled;
    @JsonProperty("isHidden") private boolean isHidden;

    public TAPCountryListItem() {
    }

    public TAPCountryListItem(int countryID, String commonName, String officialName, String flagIconUrl, String iso2Code,
                              String iso3Code, String callingCode, String currencyCode,
                              boolean isEnabled, boolean isHidden) {
        this.countryID = countryID;
        this.commonName = commonName;
        this.officialName = officialName;
        this.flagIconUrl = flagIconUrl;
        this.iso2Code = iso2Code;
        this.iso3Code = iso3Code;
        this.callingCode = callingCode;
        this.currencyCode = currencyCode;
        this.isEnabled = isEnabled;
        this.isHidden = isHidden;
    }

    public static TAPCountryListItem fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPCountryListItem>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getFlagIconUrl() {
        return flagIconUrl;
    }

    public void setFlagIconUrl(String flagIconUrl) {
        this.flagIconUrl = flagIconUrl;
    }

    public String getIso2Code() {
        return iso2Code;
    }

    public void setIso2Code(String iso2Code) {
        this.iso2Code = iso2Code;
    }

    public String getIso3Code() {
        return iso3Code;
    }

    public void setIso3Code(String iso3Code) {
        this.iso3Code = iso3Code;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.countryID);
        dest.writeString(this.commonName);
        dest.writeString(this.officialName);
        dest.writeString(this.flagIconUrl);
        dest.writeString(this.iso2Code);
        dest.writeString(this.iso3Code);
        dest.writeString(this.callingCode);
        dest.writeString(this.currencyCode);
        dest.writeByte(this.isEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHidden ? (byte) 1 : (byte) 0);
    }

    protected TAPCountryListItem(Parcel in) {
        this.countryID = in.readInt();
        this.commonName = in.readString();
        this.officialName = in.readString();
        this.flagIconUrl = in.readString();
        this.iso2Code = in.readString();
        this.iso3Code = in.readString();
        this.callingCode = in.readString();
        this.currencyCode = in.readString();
        this.isEnabled = in.readByte() != 0;
        this.isHidden = in.readByte() != 0;
    }

    public static final Creator<TAPCountryListItem> CREATOR = new Creator<TAPCountryListItem>() {
        @Override
        public TAPCountryListItem createFromParcel(Parcel source) {
            return new TAPCountryListItem(source);
        }

        @Override
        public TAPCountryListItem[] newArray(int size) {
            return new TAPCountryListItem[size];
        }
    };
}
