package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HpUserRoleModel implements Parcelable {
    @JsonProperty("userRoleID") private String id;
    @JsonProperty("name") private String name;
    @JsonProperty("iconURL") private String iconURL;

    public HpUserRoleModel(String id, String name, String iconURL) {
        this.id = id;
        this.name = name;
        this.iconURL = iconURL;
    }

    public HpUserRoleModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public HpUserRoleModel() {
    }

    public static HpUserRoleModel Builder(String userRoleID, String userRoleName){
        return new HpUserRoleModel(userRoleID, userRoleName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.iconURL);
    }

    protected HpUserRoleModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.iconURL = in.readString();
    }

    public static final Parcelable.Creator<HpUserRoleModel> CREATOR = new Parcelable.Creator<HpUserRoleModel>() {
        @Override
        public HpUserRoleModel createFromParcel(Parcel source) {
            return new HpUserRoleModel(source);
        }

        @Override
        public HpUserRoleModel[] newArray(int size) {
            return new HpUserRoleModel[size];
        }
    };
}
