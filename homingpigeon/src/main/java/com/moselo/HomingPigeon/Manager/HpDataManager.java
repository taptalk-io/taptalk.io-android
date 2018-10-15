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
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_LAST_UPDATED;
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

    public HpUserModel getActiveUser() {
        return Hawk.get(K_USER,null);
    }

    public boolean checkActiveUser() {
        if (null == getActiveUser())
            return false;
        else return true;
    }

    public void saveActiveUser(HpUserModel user) {
        Hawk.put(K_USER, user);
    }

    public void saveAuthTicket(String authTicket) {
        saveStringPreference(authTicket, K_AUTH_TICKET);
    }

    public void saveAccessToken(String accessToken) {
        saveStringPreference(accessToken, K_ACCESS_TOKEN);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(accessTokenExpiry, K_ACCESS_TOKEN_EXPIRY);
    }

    public void saveRefreshToken(String refreshToken) {
        saveStringPreference(refreshToken, K_REFRESH_TOKEN);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(refreshTokenExpiry, K_REFRESH_TOKEN_EXPIRY);
    }

    public void saveLastUpdatedMessageTimestamp(Long lastUpdated) {
        saveLongTimestampPreference(lastUpdated, K_LAST_UPDATED);
    }

    public String getRefreshToken() {
        return getStringPreference(K_REFRESH_TOKEN);
    }

    public String getAccessToken() {
        return getStringPreference(K_ACCESS_TOKEN);
    }

    public String getAuthToken() {
        return getStringPreference(K_AUTH_TICKET);
    }

    public Long getLastUpdatedMessageTimestamp() {
        return getLongTimestampPreference(K_LAST_UPDATED);
    }

    private void saveStringPreference(String string, String key) {
        Hawk.put(key, string);
    }

    private void saveLongTimestampPreference(Long timestamp, String key) {
        Hawk.put(key, timestamp);
    }

    private String getStringPreference(String key) {
        return Hawk.get(key, "0");
    }

    private Long getLongTimestampPreference(String key) {
        return Hawk.get(key, Long.parseLong("0"));
    }

    public Boolean checkRefreshTokenAvailable() {
        return checkPreferenceKeyAvailable(K_REFRESH_TOKEN);
    }

    public Boolean checkAccessTokenAvailable() {
        return checkPreferenceKeyAvailable(K_ACCESS_TOKEN);
    }

    public Boolean checkAuthTicketAvailable() {
        return checkPreferenceKeyAvailable(K_AUTH_TICKET);
    }

    private Boolean checkPreferenceKeyAvailable(String key) {
        return Hawk.contains(key);
    }

    public void deleteAuthTicket() {
        deletePreference(K_AUTH_TICKET);
    }

    public void deleteAccessToken() {
        deletePreference(K_ACCESS_TOKEN);
    }

    private void deletePreference(String key) {
        Hawk.delete(key);
    }

    // TODO: 14/09/18 TEMP
    public String getRecipientID() {
        return Hawk.get(K_RECIPIENT_ID, "0");
    }

    // TODO: 14/09/18 TEMP
    public void saveRecipientID(String recipientID) {
        Hawk.put(K_RECIPIENT_ID, recipientID);
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

    /**
     * API CALLS
     */

    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, HpDefaultDataView<HpAuthTicketResponse> view) {
        HpApiManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username, new DefaultSubscriber<>(view));
    }

    public void getAccessTokenFromApi(HpDefaultDataView<HpGetAccessTokenResponse> view) {
        HpApiManager.getInstance().getAccessToken(new DefaultSubscriber<>(view));
    }

    public void refreshAccessToken(HpDefaultDataView<HpGetAccessTokenResponse> view) {
        HpApiManager.getInstance().refreshAccessToken(new DefaultSubscriber<>(view));
    }

    public void validateAccessToken(HpDefaultDataView<HpErrorModel> view) {
        HpApiManager.getInstance().validateAccessToken(new DefaultSubscriber<>(view));
    }

    public void getRoomListFromAPI(String userID, HpDefaultDataView<HpGetRoomListResponse> view) {
        HpApiManager.getInstance().getRoomList(userID, new DefaultSubscriber<>(view));
    }

    public void getMessageListByRoomAfter(String roomID, Long minCreated, Long lastUpdated, HpDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        HpApiManager.getInstance().getMessageListByRoomAfter(roomID, minCreated, lastUpdated, new DefaultSubscriber<>(view));
    }

    public void getMessageListByRoomBefore(String roomID, Long maxCreated, HpDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        HpApiManager.getInstance().getMessageListByRoomBefore(roomID, maxCreated, new DefaultSubscriber<>(view));
    }
}
