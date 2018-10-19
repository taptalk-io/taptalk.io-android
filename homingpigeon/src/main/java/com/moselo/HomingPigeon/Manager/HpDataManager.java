package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.API.Api.HpApiManager;
import com.moselo.HomingPigeon.API.DefaultSubscriber;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ACCESS_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_AUTH_TICKET;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_IS_ROOM_LIST_SETUP_FINISHED;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_LAST_UPDATED;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_RECIPIENT_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_REFRESH_TOKEN;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_REFRESH_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpDataManager {
    private static HpDataManager instance;

    public static HpDataManager getInstance() {
        return instance == null ? (instance = new HpDataManager()) : instance;
    }

    /**
     * =========================================================================================== *
     * GENERIC METHODS FOR PREFERENCE
     * =========================================================================================== *
     */

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

    private Boolean checkPreferenceKeyAvailable(String key) {
        return Hawk.contains(key);
    }

    private void deletePreference(String key) {
        Hawk.delete(key);
    }

    /**
     * =========================================================================================== *
     * PUBLIC METHODS FOR PREFERENCE (CALLS GENERIC METHODS ABOVE)
     * PUBLIC METHODS MAY NOT HAVE KEY AS PARAMETER
     * =========================================================================================== *
     */

    public void deleteAllPreference() {
        Hawk.deleteAll();
    }

    /**
     * ACTIVE USER
     */
    public boolean checkActiveUser() {
        if (null == getActiveUser())
            return false;
        else return true;
    }

    public HpUserModel getActiveUser() {
        return Hawk.get(K_USER, null);
    }

    public void saveActiveUser(HpUserModel user) {
        Hawk.put(K_USER, user);
    }

    /**
     * AUTH TICKET
     */

    public Boolean checkAuthTicketAvailable() {
        return checkPreferenceKeyAvailable(K_AUTH_TICKET);
    }

    public String getAuthTicket() {
        return getStringPreference(K_AUTH_TICKET);
    }

    public void saveAuthTicket(String authTicket) {
        saveStringPreference(authTicket, K_AUTH_TICKET);
    }

    public void deleteAuthTicket() {
        deletePreference(K_AUTH_TICKET);
    }

    /**
     * ACCESS TOKEN
     */

    public Boolean checkAccessTokenAvailable() {
        return checkPreferenceKeyAvailable(K_ACCESS_TOKEN);
    }

    public String getAccessToken() {
        return getStringPreference(K_ACCESS_TOKEN);
    }

    public void saveAccessToken(String accessToken) {
        saveStringPreference(accessToken, K_ACCESS_TOKEN);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(accessTokenExpiry, K_ACCESS_TOKEN_EXPIRY);
    }

    public void deleteAccessToken() {
        deletePreference(K_ACCESS_TOKEN);
    }

    /**
     * REFRESH TOKEN
     */

    public Boolean checkRefreshTokenAvailable() {
        return checkPreferenceKeyAvailable(K_REFRESH_TOKEN);
    }

    public String getRefreshToken() {
        return getStringPreference(K_REFRESH_TOKEN);
    }

    public void saveRefreshToken(String refreshToken) {
        saveStringPreference(refreshToken, K_REFRESH_TOKEN);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(refreshTokenExpiry, K_REFRESH_TOKEN_EXPIRY);
    }

    /**
     * LAST UPDATED MESSAGE
     */

    public Long getLastUpdatedMessageTimestamp(String roomID) {
        return null == getLastUpdatedMessageTimestampMap() ? Long.parseLong("0")
                : !getLastUpdatedMessageTimestampMap().containsKey(roomID) ? Long.parseLong("0") :
                getLastUpdatedMessageTimestampMap().get(roomID);
    }

    public boolean checkKeyInLastMessageTimestamp(String roomID) {
        return Long.parseLong("0") != getLastUpdatedMessageTimestamp(roomID);
    }

    public void saveLastUpdatedMessageTimestamp(String roomID, Long lastUpdated) {
        saveLastUpdatedMessageTimestampMap(roomID, lastUpdated);
    }

    private HashMap<String, Long> getLastUpdatedMessageTimestampMap() {
        return Hawk.get(K_LAST_UPDATED, null);
    }

    private void saveLastUpdatedMessageTimestampMap(String roomID, long lastUpdated) {
        HashMap<String, Long> tempLastUpdated;
        if (null != getLastUpdatedMessageTimestampMap())
            tempLastUpdated = getLastUpdatedMessageTimestampMap();
        else tempLastUpdated = new LinkedHashMap<>();

        tempLastUpdated.put(roomID, lastUpdated);
        Hawk.put(K_LAST_UPDATED, tempLastUpdated);
    }

    /**
     * ROOM LIST FIRST SETUP
     */

    public Boolean isRoomListSetupFinished() {
        return checkPreferenceKeyAvailable(K_IS_ROOM_LIST_SETUP_FINISHED);
    }

    public void setRoomListSetupFinished() {
        saveLongTimestampPreference(Calendar.getInstance().getTimeInMillis(), K_IS_ROOM_LIST_SETUP_FINISHED);
    }

    // TODO: 14/09/18 TEMP
    public String getRecipientID() {
        return Hawk.get(K_RECIPIENT_ID, "0");
    }

    // TODO: 14/09/18 TEMP
    public void saveRecipientID(String recipientID) {
        Hawk.put(K_RECIPIENT_ID, recipientID);
    }

    /**
     * =========================================================================================== *
     * DATABASE METHODS
     * =========================================================================================== *
     */

    //initialized Database Managernya yang di panggil di class Homing Pigeon
    public void initDatabaseManager(String databaseType, Application application) {
        HpDatabaseManager.getInstance().setRepository(databaseType, application);
    }

    //Message
    public void insertToDatabase(HpMessageEntity messageEntity) {
        HpDatabaseManager.getInstance().insert(messageEntity);
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

    public void searchAllMessagesFromDatabase(String keyword, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().searchAllMessages(keyword, listener);
    }

    public void getRoomList(String myID, List<HpMessageEntity> saveMessages, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(myID, saveMessages, isCheckUnreadFirst, listener);
    }

    public void getRoomList(String myID, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(myID, isCheckUnreadFirst, listener);
    }

    public void searchAllRoomsFromDatabase(String keyword, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().searchAllRooms(keyword, listener);
    }

    public void getUnreadCountPerRoom(String myID, String roomID, final HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getUnreadCountPerRoom(myID, roomID, listener);
    }

    public void deleteAllMessage() {
        HpDatabaseManager.getInstance().deleteAllMessage();
    }
    //Recent Search

    public void insertToDatabase(HpRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().insert(recentSearchEntity);
    }

    public void deleteFromDatabase(HpRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().delete(recentSearchEntity);
    }

    public void deleteFromDatabase(List<HpRecentSearchEntity> recentSearchEntities) {
        HpDatabaseManager.getInstance().delete(recentSearchEntities);
    }

    public void deleteAllRecentSearch() {
        HpDatabaseManager.getInstance().deleteAllRecentSearch();
    }

    public LiveData<List<HpRecentSearchEntity>> getRecentSearchLive() {
        return HpDatabaseManager.getInstance().getRecentSearchLive();
    }
    //My Contact

    public void getMyContactList(HpDatabaseListener<HpUserModel> listener) {
        HpDatabaseManager.getInstance().getMyContactList(listener);
    }
    public LiveData<List<HpUserModel>> getMyContactList() {
        return HpDatabaseManager.getInstance().getMyContactList();
    }

    public void searchAllMyContacts(String keyword, HpDatabaseListener<HpUserModel> listener) {
        HpDatabaseManager.getInstance().searchAllMyContacts(keyword, listener);
    }

    public void insertMyContactToDatabase(HpUserModel... userModels) {
        HpDatabaseManager.getInstance().insertMyContact(userModels);
    }

    public void insertMyContactToDatabase(List<HpUserModel> userModels) {
        HpDatabaseManager.getInstance().insertMyContact(userModels);
    }

    public void insertAndGetMyContact(List<HpUserModel> userModels, HpDatabaseListener<HpUserModel> listener) {
        HpDatabaseManager.getInstance().insertAndGetMyContact(userModels, listener);
    }

    public void deleteMyContactFromDatabase(HpUserModel... userModels) {
        HpDatabaseManager.getInstance().deleteMyContact(userModels);
    }

    public void deleteMyContactFromDatabase(List<HpUserModel> userModels) {
        HpDatabaseManager.getInstance().deleteMyContact(userModels);
    }

    public void deleteAllContact() {
        HpDatabaseManager.getInstance().deleteAllContact();
    }

    public void updateMyContact(HpUserModel userModels) {
        HpDatabaseManager.getInstance().updateMyContact(userModels);
    }

    //General
    public void deleteAllFromDatabase() {
        new Thread(this::deleteAllMessage).start();
        new Thread(this::deleteAllRecentSearch).start();
        new Thread(this::deleteAllContact).start();
    }

    /**
     * =========================================================================================== *
     * API CALLS
     * =========================================================================================== *
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

    public void getMessageRoomListAndUnread(String userID, HpDefaultDataView<HpGetRoomListResponse> view) {
        HpApiManager.getInstance().getRoomList(userID, new DefaultSubscriber<>(view));
    }

    public void getNewAndUpdatedMessage(HpDefaultDataView<HpGetRoomListResponse> view) {
        HpApiManager.getInstance().getPendingAndUpdatedMessage(new DefaultSubscriber<>(view));
    }

    public void getMessageListByRoomAfter(String roomID, Long minCreated, Long lastUpdated, HpDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        HpApiManager.getInstance().getMessageListByRoomAfter(roomID, minCreated, lastUpdated, new DefaultSubscriber<>(view));
    }

    public void getMessageListByRoomBefore(String roomID, Long maxCreated, HpDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        HpApiManager.getInstance().getMessageListByRoomBefore(roomID, maxCreated, new DefaultSubscriber<>(view));
    }
}
