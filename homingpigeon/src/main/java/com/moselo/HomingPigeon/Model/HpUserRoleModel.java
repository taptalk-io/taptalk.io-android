package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HpUserRoleModel implements Parcelable {
    @JsonProperty("userRoleID") private String userRoleID;
    @JsonProperty("name") private String roleName;
    @JsonProperty("iconURL") private String roleIconURL;

    public HpUserRoleModel(String userRoleID, String roleName, String roleIconURL) {
        this.userRoleID = userRoleID;
        this.roleName = roleName;
        this.roleIconURL = roleIconURL;
    }

    public HpUserRoleModel(String userRoleID, String roleName) {
        this.userRoleID = userRoleID;
        this.roleName = roleName;
    }

    public HpUserRoleModel() {
    }

    public static HpUserRoleModel Builder(String userRoleID, String userRoleName){
        return new HpUserRoleModel(userRoleID, userRoleName);
    }

    public String getUserRoleID() {
        return userRoleID;
    }

    public void setUserRoleID(String userRoleID) {
        this.userRoleID = userRoleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleIconURL() {
        return roleIconURL;
    }

    public void setRoleIconURL(String roleIconURL) {
        this.roleIconURL = roleIconURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userRoleID);
        dest.writeString(this.roleName);
        dest.writeString(this.roleIconURL);
    }

    protected HpUserRoleModel(Parcel in) {
        this.userRoleID = in.readString();
        this.roleName = in.readString();
        this.roleIconURL = in.readString();
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
