package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.API.Api.HpApiManager;
import com.moselo.HomingPigeon.API.DefaultSubscriber;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Model.ResponseModel.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetRoomListResponse;
import com.moselo.HomingPigeon.Model.UserModel;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_REFRESH_TOKEN;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_AUTH_TICKET;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_RECIPIENT_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_REFRESH_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpDataManager {
    private static HpDataManager instance;

    public static HpDataManager getInstance() {
        return instance == null ? (instance = new HpDataManager()) : instance;
    }

    public UserModel getActiveUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return HpUtils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, null));
    }

    public boolean checkActiveUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (null == HpUtils.getInstance().fromJSON(new TypeReference<UserModel>() {}, prefs.getString(K_USER, null)))
            return false;
        else return true;
    }

    public void saveActiveUser(Context context, UserModel user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, HpUtils.getInstance().toJsonString(user)).apply();
        HpChatManager.getInstance().setActiveUser(user);
    }

    public void saveAuthTicket(Context context, String authTicket) {
        saveStringPreference(context, authTicket, K_AUTH_TICKET);
    }

    public void saveAccessToken(Context context, String accessToken) {
        saveStringPreference(context, accessToken, K_ACCESS_TOKEN);
    }

    public void saveAccessTokenExpiry(Context context, Long accessTokenExpiry) {
        saveLongTimestampPreference(context, accessTokenExpiry, K_ACCESS_TOKEN_EXPIRY);
    }

    public void saveRefreshToken(Context context, String refreshToken) {
        saveStringPreference(context, refreshToken, K_REFRESH_TOKEN);
    }

    public void saveRefreshTokenExpiry(Context context, Long refreshTokenExpiry) {
        saveLongTimestampPreference(context, refreshTokenExpiry, K_REFRESH_TOKEN_EXPIRY);
    }

    private void saveStringPreference(Context context, String token, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, token).apply();
    }

    private void saveLongTimestampPreference(Context context, Long timestamp, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putLong(key, timestamp).apply();
    }

    public String getRefreshToken(Context context) {
        return getStringPreference(context, K_REFRESH_TOKEN);
    }

    public String getAccessToken(Context context) {
        return getStringPreference(context, K_ACCESS_TOKEN);
    }

    public String getAuthToken(Context context) {
        return getStringPreference(context, K_AUTH_TICKET);
    }

    private String getStringPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, "0");
    }

    private Long getLongTimestampPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, 0);
    }

    public Boolean checkRefreshTokenAvailable(Context context) {
        return checkPreferenceKeyAvailable(context, K_REFRESH_TOKEN);
    }

    public Boolean checkAccessTokenAvailable(Context context) {
        return checkPreferenceKeyAvailable(context, K_ACCESS_TOKEN);
    }

    public Boolean checkAuthTicketAvailable(Context context) {
        return checkPreferenceKeyAvailable(context, K_AUTH_TICKET);
    }

    private Boolean checkPreferenceKeyAvailable(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(key);
    }

    public void deleteAuthTicket(Context context) {
        deletePreference(context, K_AUTH_TICKET);
    }

    private void deletePreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(key).apply();
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
        HpDatabaseManager.getInstance().setRepository(databaseType, application);
    }

    public void insertToDatabase(HpMessageEntity messageEntity) {
        HpDatabaseManager.getInstance().insert(messageEntity);
    }

    public void insertToDatabase(HpRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().insert(recentSearchEntity);
    }

    public void insertToDatabase(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages) {
        HpDatabaseManager.getInstance().insert(messageEntities, isClearSaveMessages);
    }

    public void insertToDatabase(List<HpMessageEntity> messageEntities, boolean isClearSaveMessages, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().insert(messageEntities, isClearSaveMessages, listener);
    }

    public void deleteFromDatabase(String messageLocalID) {
        HpDatabaseManager.getInstance().delete(messageLocalID);
    }

    public void deleteFromDatabase(HpRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().delete(recentSearchEntity);
    }

    public void deleteFromDatabase(List<HpRecentSearchEntity> recentSearchEntities) {
        HpDatabaseManager.getInstance().delete(recentSearchEntities);
    }

    public void updateSendingMessageToFailed() {
        HpDatabaseManager.getInstance().updatePendingStatus();
    }

    public void updateSendingMessageToFailed(String localID) {
        HpDatabaseManager.getInstance().updatePendingStatus(localID);
    }

    public LiveData<List<HpMessageEntity>> getMessagesLiveData() {
        return HpDatabaseManager.getInstance().getMessagesLiveData();
    }

    public void getMessagesFromDatabase(String roomID, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getMessages(roomID, listener);
    }

    public void getMessagesFromDatabase(String roomID, HpDatabaseListener listener, long lastTimestamp) {
        HpDatabaseManager.getInstance().getMessages(roomID, listener, lastTimestamp);
    }

    public void getRoomList(String myID, List<HpMessageEntity> saveMessages, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(myID, saveMessages, isCheckUnreadFirst, listener);
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(myID, isCheckUnreadFirst, listener);
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getUnreadCountPerRoom(myID, roomID, listener);
    }

    public LiveData<List<HpRecentSearchEntity>> getRecentSearchLive() {
        return HpDataManager.getInstance().getRecentSearchLive();
    }

    //API
    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, HpDefaultDataView<AuthTicketResponse> view) {
        HpApiManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username, new DefaultSubscriber<>(view));
    }

    public void getAccessTokenFromApi(HpDefaultDataView<GetAccessTokenResponse> view) {
        HpApiManager.getInstance().getAccessToken(new DefaultSubscriber<>(view));
    }

    public void refreshAccessToken(HpDefaultDataView<GetAccessTokenResponse> view) {
        HpApiManager.getInstance().refreshAccessToken(new DefaultSubscriber<>(view));
    }

    public void getRoomListFromAPI(String userID, HpDefaultDataView<GetRoomListResponse> view) {
        HpApiManager.getInstance().getRoomList(userID, new DefaultSubscriber<>(view));
    }
}
