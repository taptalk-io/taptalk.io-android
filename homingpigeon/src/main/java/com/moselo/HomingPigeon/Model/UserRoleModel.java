package com.moselo.HomingPigeon.Model;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserRoleModel implements Parcelable {
    @JsonProperty("userRoleID") private String id;
    @JsonProperty("name") private String name;
    @JsonProperty("iconURL") private String iconURL;

    public UserRoleModel(String id, String name, String iconURL) {
        this.id = id;
        this.name = name;
        this.iconURL = iconURL;
    }

    public UserRoleModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserRoleModel() {
    }

    public static UserRoleModel Builder(String userRoleID, String userRoleName){
        return new UserRoleModel(userRoleID, userRoleName);
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

    protected UserRoleModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.iconURL = in.readString();
    }

    public static final Parcelable.Creator<UserRoleModel> CREATOR = new Parcelable.Creator<UserRoleModel>() {
        @Override
        public UserRoleModel createFromParcel(Parcel source) {
            return new UserRoleModel(source);
        }

        @Override
        public UserRoleModel[] newArray(int size) {
            return new UserRoleModel[size];
        }
    };
}
