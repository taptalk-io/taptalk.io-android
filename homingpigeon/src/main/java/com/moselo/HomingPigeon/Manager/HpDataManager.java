package com.moselo.HomingPigeon.Manager;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.API.Api.TAPApiManager;
import com.moselo.HomingPigeon.API.TAPDefaultSubscriber;
import com.moselo.HomingPigeon.API.View.TapDefaultDataView;
import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchEntity;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpAuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpCommonResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpContactResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetUserResponse;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_ACCESS_TOKEN;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_ACCESS_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_AUTH_TICKET;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_IS_ROOM_LIST_SETUP_FINISHED;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_LAST_UPDATED;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_RECIPIENT_ID;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_REFRESH_TOKEN;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_REFRESH_TOKEN_EXPIRY;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_USER;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Notification.K_FIREBASE_TOKEN;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Notification.K_NOTIFICATION_MESSAGE_MAP;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.OldDataConst.K_LAST_DELETE_TIMESTAMP;

public class HpDataManager {
    private static final String TAG = HpDataManager.class.getSimpleName();
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
        HpChatManager.getInstance().setActiveUser(user);
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
     * Firebase Token
     */
    public void saveFirebaseToken(String firebaseToken) {
        saveStringPreference(firebaseToken, K_FIREBASE_TOKEN);
    }

    public String getFirebaseToken() {
        return getStringPreference(K_FIREBASE_TOKEN);
    }

    public Boolean checkFirebaseToken(String newFirebaseToken) {
        if (!checkPreferenceKeyAvailable(K_FIREBASE_TOKEN))
            return false;
        else if (newFirebaseToken.equals(getFirebaseToken())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Old Data (Auto Clean) Last Delete Timestamp
     */
    public void saveLastDeleteTimestamp(Long lastDeleteTimestamp) {
        saveLongTimestampPreference(lastDeleteTimestamp, K_LAST_DELETE_TIMESTAMP);
    }

    public Long getLastDeleteTimestamp() {
        return getLongTimestampPreference(K_LAST_DELETE_TIMESTAMP);
    }

    public Boolean checkLastDeleteTimestamp() {
        if (!checkPreferenceKeyAvailable(K_LAST_DELETE_TIMESTAMP) || null == getLastDeleteTimestamp())
            return false;
        else return 0 != getLastDeleteTimestamp();
    }

    /**
     * Notification Message Map
     */
    public void saveNotificationMessageMap(String notifMessagesMap) {
        Hawk.put(K_NOTIFICATION_MESSAGE_MAP, notifMessagesMap);
    }

    public String getNotificationMessageMap() {
        return Hawk.get(K_NOTIFICATION_MESSAGE_MAP, null);
    }

    public void clearNotificationMessageMap() {
        Hawk.delete(K_NOTIFICATION_MESSAGE_MAP);
    }

    public boolean checkNotificationMap() {
        return checkPreferenceKeyAvailable(K_NOTIFICATION_MESSAGE_MAP);
    }

    /**
     * =========================================================================================== *
     * DATABASE METHODS
     * =========================================================================================== *
     */

    // initialized Database Managernya yang di panggil di class Homing Pigeon
    public void initDatabaseManager(String databaseType, Application application) {
        HpDatabaseManager.getInstance().setRepository(databaseType, application);
    }

    // Message
    public void deleteMessage(List<TAPMessageEntity> messageEntities, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().deleteMessage(messageEntities, listener);
    }

    public void insertToDatabase(TAPMessageEntity messageEntity) {
        HpDatabaseManager.getInstance().insert(messageEntity);
    }

    public void insertToDatabase(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages) {
        HpDatabaseManager.getInstance().insert(messageEntities, isClearSaveMessages);
    }

    public void insertToDatabase(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages, HpDatabaseListener listener) {
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

    public LiveData<List<TAPMessageEntity>> getMessagesLiveData() {
        return HpDatabaseManager.getInstance().getMessagesLiveData();
    }

    public void getMessagesFromDatabaseDesc(String roomID, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getMessagesDesc(roomID, listener);
    }

    public void getMessagesFromDatabaseDesc(String roomID, HpDatabaseListener listener, long lastTimestamp) {
        HpDatabaseManager.getInstance().getMessagesDesc(roomID, listener, lastTimestamp);
    }

    public void getMessagesFromDatabaseAsc(String roomID, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getMessagesAsc(roomID, listener);
    }

    public void searchAllMessagesFromDatabase(String keyword, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().searchAllMessages(keyword, listener);
    }

    public void getRoomList(List<TAPMessageEntity> saveMessages, boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(getActiveUser().getUserID(), saveMessages, isCheckUnreadFirst, listener);
    }

    public void getRoomList(boolean isCheckUnreadFirst, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getRoomList(getActiveUser().getUserID(), isCheckUnreadFirst, listener);
    }

    public void searchAllRoomsFromDatabase(String keyword, HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().searchAllRooms(getActiveUser().getUserID(), keyword, listener);
    }

    public void getUnreadCountPerRoom(String roomID, final HpDatabaseListener listener) {
        HpDatabaseManager.getInstance().getUnreadCountPerRoom(getActiveUser().getUserID(), roomID, listener);
    }

    public void deleteAllMessage() {
        HpDatabaseManager.getInstance().deleteAllMessage();
    }

    // Recent Search
    public void insertToDatabase(TAPRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().insert(recentSearchEntity);
    }

    public void deleteFromDatabase(TAPRecentSearchEntity recentSearchEntity) {
        HpDatabaseManager.getInstance().delete(recentSearchEntity);
    }

    public void deleteFromDatabase(List<TAPRecentSearchEntity> recentSearchEntities) {
        HpDatabaseManager.getInstance().delete(recentSearchEntities);
    }

    public void deleteAllRecentSearch() {
        HpDatabaseManager.getInstance().deleteAllRecentSearch();
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchLive() {
        return HpDatabaseManager.getInstance().getRecentSearchLive();
    }

    // My Contact
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

    public void insertMyContactToDatabase(HpDatabaseListener<HpUserModel> listener, HpUserModel... userModels) {
        HpDatabaseManager.getInstance().insertMyContact(listener, userModels);
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

    // FIXME: 25 October 2018 MAKE FUNCTION RETURN BOOLEAN OR GET FRIEND STATUS FROM API
    public void checkUserInMyContacts(String userID, HpDatabaseListener<HpUserModel> listener) {
        HpDatabaseManager.getInstance().checkUserInMyContacts(userID, listener);
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
            , String fullname, String email, String phone, String username, TapDefaultDataView<HpAuthTicketResponse> view) {
        TAPApiManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username, new TAPDefaultSubscriber<>(view));
    }

    public void getAccessTokenFromApi(TapDefaultDataView<HpGetAccessTokenResponse> view) {
        TAPApiManager.getInstance().getAccessToken(new TAPDefaultSubscriber<>(view));
    }

    public void refreshAccessToken(TapDefaultDataView<HpGetAccessTokenResponse> view) {
        TAPApiManager.getInstance().refreshAccessToken(new TAPDefaultSubscriber<>(view));
    }

    public void validateAccessToken(TapDefaultDataView<HpErrorModel> view) {
        TAPApiManager.getInstance().validateAccessToken(new TAPDefaultSubscriber<>(view));
    }

    public void registerFcmTokenToServer(String fcmToken, TapDefaultDataView<HpCommonResponse> view) {
        TAPApiManager.getInstance().registerFcmTokenToServer(fcmToken, new TAPDefaultSubscriber(view));
    }

    public void getMessageRoomListAndUnread(String userID, TapDefaultDataView<HpGetRoomListResponse> view) {
        TAPApiManager.getInstance().getRoomList(userID, new TAPDefaultSubscriber<>(view));
    }

    public void getNewAndUpdatedMessage(TapDefaultDataView<HpGetRoomListResponse> view) {
        TAPApiManager.getInstance().getPendingAndUpdatedMessage(new TAPDefaultSubscriber<>(view));
    }

    public void getMessageListByRoomAfter(String roomID, Long minCreated, Long lastUpdated, TapDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        TAPApiManager.getInstance().getMessageListByRoomAfter(roomID, minCreated, lastUpdated, new TAPDefaultSubscriber<>(view));
    }

    public void getMessageListByRoomBefore(String roomID, Long maxCreated, TapDefaultDataView<HpGetMessageListbyRoomResponse> view) {
        TAPApiManager.getInstance().getMessageListByRoomBefore(roomID, maxCreated, new TAPDefaultSubscriber<>(view));
    }

    public void getMyContactListFromAPI(TapDefaultDataView<HpContactResponse> view) {
        TAPApiManager.getInstance().getMyContactListFromAPI(new TAPDefaultSubscriber<>(view));
    }

    public void addContactApi(String userID, TapDefaultDataView<HpCommonResponse> view) {
        TAPApiManager.getInstance().addContact(userID, new TAPDefaultSubscriber<>(view));
    }

    public void removeContactApi(String userID, TapDefaultDataView<HpCommonResponse> view) {
        TAPApiManager.getInstance().removeContact(userID, new TAPDefaultSubscriber<>(view));
    }

    // Search User
    private TAPDefaultSubscriber searchUserSubscriber;

    public void getUserByIdFromApi(String id, TapDefaultDataView<HpGetUserResponse> view) {
        TAPApiManager.getInstance().getUserByID(id, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void getUserByXcUserIdFromApi(String xcUserID, TapDefaultDataView<HpGetUserResponse> view) {
        TAPApiManager.getInstance().getUserByXcUserID(xcUserID, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void getUserByUsernameFromApi(String username, TapDefaultDataView<HpGetUserResponse> view) {
        TAPApiManager.getInstance().getUserByUsername(username, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    // FIXME: 25 October 2018
    public void cancelUserSearchApiCall() {
        if (null != searchUserSubscriber) {
            searchUserSubscriber.unsubscribe();
        }
    }
}
