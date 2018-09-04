package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class DataManager {
    private static DataManager instance;

    public static DataManager getInstance() {
        return instance == null ? (instance = new DataManager()) : instance;
    }

    public UserModel getActiveUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, null));
    }

    public void saveActiveUser(Context context, UserModel user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, Utils.getInstance().toJsonString(user)).apply();
        ChatManager.getInstance().setActiveUser(user);
    }

    public void initDatabaseManager(String databaseType, Application application ){
        DatabaseManager.getInstance().setRepository(databaseType, application);
    }

    public void insertToDatabase(MessageEntity messageEntity) {
        DatabaseManager.getInstance().insert(messageEntity);
    }

    public void insertToDatabase(List<MessageEntity> messageEntities) {
        DatabaseManager.getInstance().insert(messageEntities);
    }

    public void deleteFromDatabase(String messageLocalID) {
        DatabaseManager.getInstance().delete(messageLocalID);
    }

    public void updatePendingStatus() {
        DatabaseManager.getInstance().updatePendingStatus();
    }

    public void updatePendingStatus(String localID) {
        DatabaseManager.getInstance().updatePendingStatus(localID);
    }

    public LiveData<List<MessageEntity>> getMessagesLiveData() {
        return DatabaseManager.getInstance().getMessagesLiveData();
    }

    public void getMessagesFromDatabase(HomingPigeonGetChatListener listener) {
        DatabaseManager.getInstance().getMessages(listener);
    }

    public void getMessagesFromDatabase(HomingPigeonGetChatListener listener, long lastTimestamp) {
        DatabaseManager.getInstance().getMessages(listener, lastTimestamp);
    }
}
