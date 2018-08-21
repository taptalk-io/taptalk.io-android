package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.UserModel;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class DataManager {
    private static DataManager instance;

    public static DataManager getInstance() {
        if (null == instance) {
            instance =  new DataManager();
        }
        return instance;
    }

    public UserModel getUserModel(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, null));
    }

    public void saveUserModel(Context context, UserModel userModel){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, Utils.getInstance().toJsonString(userModel))
                .apply();
    }
}
