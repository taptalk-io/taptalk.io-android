package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TAPUserRoleModel implements Parcelable {
    @JsonProperty("userRoleCode") @JsonAlias("userRoleID")
    private String userRoleCode;
    @JsonProperty("name") private String roleName;
    @JsonProperty("iconURL") private String roleIconURL;

    public TAPUserRoleModel(String userRoleCode, String roleName, String roleIconURL) {
        this.userRoleCode = userRoleCode;
        this.roleName = roleName;
        this.roleIconURL = roleIconURL;
    }

    public TAPUserRoleModel(String userRoleCode, String roleName) {
        this.userRoleCode = userRoleCode;
        this.roleName = roleName;
    }

    public TAPUserRoleModel() {
    }

    public static TAPUserRoleModel Builder(String userRoleID, String userRoleName){
        return new TAPUserRoleModel(userRoleID, userRoleName);
    }

    public String getUserRoleCode() {
        return userRoleCode;
    }

    public void setUserRoleCode(String userRoleCode) {
        this.userRoleCode = userRoleCode;
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
        dest.writeString(this.userRoleCode);
        dest.writeString(this.roleName);
        dest.writeString(this.roleIconURL);
    }

    protected TAPUserRoleModel(Parcel in) {
        this.userRoleCode = in.readString();
        this.roleName = in.readString();
        this.roleIconURL = in.readString();
    }

    public static final Parcelable.Creator<TAPUserRoleModel> CREATOR = new Parcelable.Creator<TAPUserRoleModel>() {
        @Override
        public TAPUserRoleModel createFromParcel(Parcel source) {
            return new TAPUserRoleModel(source);
        }

        @Override
        public TAPUserRoleModel[] newArray(int size) {
            return new TAPUserRoleModel[size];
        }
    };
}
