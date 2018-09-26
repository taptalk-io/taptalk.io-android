package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.API.Api.ApiManager;
import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.API.DefaultSubscriber;
import com.moselo.HomingPigeon.API.View.DefaultDataView;
import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Model.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.List;

import rx.Subscriber;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_RECIPIENT_ID;
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

    public boolean checkActiveUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (null == Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, prefs.getString(K_USER, null)))
            return false;
        else return true;
    }

    public void saveActiveUser(Context context, UserModel user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, Utils.getInstance().toJsonString(user)).apply();
        ChatManager.getInstance().setActiveUser(user);
    }

    // TODO: 14/09/18 TEMP
    public String getRecipientID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(K_RECIPIENT_ID, "0");
    }

    // TODO: 14/09/18 TEMP
    public void saveRecipientID(Context context, String recipientID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_RECIPIENT_ID, recipientID).apply();
    }

    public void initDatabaseManager(String databaseType, Application application) {
        DatabaseManager.getInstance().setRepository(databaseType, application);
    }

    public void insertToDatabase(MessageEntity messageEntity) {
        DatabaseManager.getInstance().insert(messageEntity);
    }

    public void insertToDatabase(RecentSearchEntity recentSearchEntity) {
        DatabaseManager.getInstance().insert(recentSearchEntity);
    }

    public void insertToDatabase(List<MessageEntity> messageEntities) {
        DatabaseManager.getInstance().insert(messageEntities);
    }

    public void deleteFromDatabase(String messageLocalID) {
        DatabaseManager.getInstance().delete(messageLocalID);
    }

    public void deleteFromDatabase(RecentSearchEntity recentSearchEntity) {
        DatabaseManager.getInstance().delete(recentSearchEntity);
    }

    public void deleteFromDatabase(List<RecentSearchEntity> recentSearchEntities) {
        DatabaseManager.getInstance().delete(recentSearchEntities);
    }

    public void updateSendingMessageToFailed() {
        DatabaseManager.getInstance().updatePendingStatus();
    }

    public void updateSendingMessageToFailed(String localID) {
        DatabaseManager.getInstance().updatePendingStatus(localID);
    }

    public LiveData<List<MessageEntity>> getMessagesLiveData() {
        return DatabaseManager.getInstance().getMessagesLiveData();
    }

    public void getMessagesFromDatabase(String roomID, HomingPigeonDatabaseListener listener) {
        DatabaseManager.getInstance().getMessages(roomID, listener);
    }

    public void getMessagesFromDatabase(String roomID, HomingPigeonDatabaseListener listener, long lastTimestamp) {
        DatabaseManager.getInstance().getMessages(roomID, listener, lastTimestamp);
    }

    public LiveData<List<RecentSearchEntity>> getRecentSearchLive() {
        return DataManager.getInstance().getRecentSearchLive();
    }

    //API
    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, DefaultDataView<AuthTicketResponse> view) {
        ApiManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username, new DefaultSubscriber<>(view));
    }
}
