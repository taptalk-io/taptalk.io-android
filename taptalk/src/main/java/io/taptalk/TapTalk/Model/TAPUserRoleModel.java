package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPUserRoleModel implements Parcelable {
    @JsonProperty("code")
    @JsonAlias("userRoleID")
    private String code;
    @JsonProperty("name")
    @ColumnInfo(name = "roleName")
    private String name;
    @ColumnInfo(name = "roleIconURL")
    @JsonProperty("iconURL") private String iconURL;

    @Ignore
    public TAPUserRoleModel(String code, String name, String iconURL) {
        this.code = code;
        this.name = name;
        this.iconURL = iconURL;
    }

    @Ignore
    public TAPUserRoleModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public TAPUserRoleModel() {
    }

    public static TAPUserRoleModel Builder(String userRoleID, String userRoleName) {
        return new TAPUserRoleModel(userRoleID, userRoleName);
    }

    public static TAPUserRoleModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPUserRoleModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @deprecated use {@link #getName()} instead.
     */
    @Deprecated
    public String getRoleName() {
        return name;
    }

    public String getName() {
        return name;
    }

    /**
     * @deprecated use {@link #setName(String)} instead.
     */
    @Deprecated
    public void setRoleName(String roleName) {
        this.name = roleName;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated use {@link #getIconURL()} instead.
     */
    @Deprecated
    public String getRoleIconURL() {
        return iconURL;
    }

    public String getIconURL() {
        return iconURL;
    }

    /**
     * @deprecated use {@link #setIconURL(String)} instead.
     */
    @Deprecated
    public void setRoleIconURL(String roleIconURL) {
        this.iconURL = roleIconURL;
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
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.iconURL);
    }

    protected TAPUserRoleModel(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.iconURL = in.readString();
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
