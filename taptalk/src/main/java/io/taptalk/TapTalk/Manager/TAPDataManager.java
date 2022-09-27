package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CustomHeaderKey.APP_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CustomHeaderKey.APP_SECRET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CustomHeaderKey.USER_AGENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IS_CONTACT_SYNC_ALLOWED_BY_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.IS_PERMISSION_SYNC_ASKED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ACCESS_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ACCESS_TOKEN_EXPIRY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_CHAT_ROOM_CONTACT_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_COUNTRY_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_FILE_PATH_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_FILE_URI_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_GROUP_DATA_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_IS_CONTACT_LIST_UPDATED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_IS_ROOM_LIST_SETUP_FINISHED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_LAST_ROOM_MESSAGE_DELETE_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_LAST_UPDATED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_MEDIA_VOLUME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_MUTED_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_PINNED_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_PINNED_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_REFRESH_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_REFRESH_TOKEN_EXPIRY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_UNREAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER_LAST_ACTIVITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LAST_CALL_COUNTRY_TIMESTAMP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MY_COUNTRY_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MY_COUNTRY_FLAG_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_FIREBASE_TOKEN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_NOTIFICATION_MESSAGE_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OldDataConst.K_LAST_DELETE_TIMESTAMP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigType.CORE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigType.CUSTOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigType.PROJECT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.Subscriber.TAPBaseSubscriber;
import io.taptalk.TapTalk.API.Subscriber.TAPDefaultSubscriber;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAuthTicketResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCheckUsernameResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPDeleteMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapCheckDeleteAccountStateResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMutedRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetPhotoListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetRoomIdsWithStateResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetSharedContentResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapPinMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapStarMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapUnstarMessageResponse;
import io.taptalk.TapTalk.Model.TAPCountryListItem;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapConfigs;
import okhttp3.ResponseBody;

public class TAPDataManager {
    private static final String TAG = TAPDataManager.class.getSimpleName();
    private static HashMap<String, TAPDataManager> instances;

    private String instanceKey = "";
    private TAPDefaultSubscriber validateSub;
    private TAPDefaultSubscriber roomListAndUnreadSub;

    private boolean isNeedToQueryUpdateRoomList;
    private TAPDefaultSubscriber contactListSubs;

    public static TAPDataManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPDataManager instance = new TAPDataManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPDataManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPDataManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public boolean isNeedToQueryUpdateRoomList() {
        return isNeedToQueryUpdateRoomList;
    }

    public void setNeedToQueryUpdateRoomList(boolean needToQueryUpdateRoomList) {
        isNeedToQueryUpdateRoomList = needToQueryUpdateRoomList;
    }

    /**
     * ==========================================================================================
     * ALL MANAGER DATA
     * ==========================================================================================
     */
    public void deleteAllManagerData() {
        setNeedToQueryUpdateRoomList(false);
        TAPCacheManager.getInstance(TapTalk.appContext).clearCache();
        TAPChatManager.getInstance(instanceKey).resetChatManager();
        TAPContactManager.getInstance(instanceKey).clearUserDataMap();
        TAPContactManager.getInstance(instanceKey).clearUserMapByPhoneNumber();
        TAPContactManager.getInstance(instanceKey).resetMyCountryCode();
        TAPContactManager.getInstance(instanceKey).resetContactSyncPermissionAsked();
        TAPContactManager.getInstance(instanceKey).resetContactSyncAllowedByUser();
        TAPFileDownloadManager.getInstance(instanceKey).resetTAPFileDownloadManager();
        TAPFileUploadManager.getInstance(instanceKey).resetFileUploadManager();
        TAPNotificationManager.getInstance(instanceKey).clearAllNotificationMessageMap();
        TAPMessageStatusManager.getInstance(instanceKey).resetMessageStatusManager();
    }

    /**
     * =========================================================================================== *
     * GENERIC METHODS FOR PREFERENCE
     * =========================================================================================== *
     */

    private void saveBooleanPreference(String key, boolean bool) {
        Hawk.put(instanceKey + key, bool);
    }

    private void saveStringPreference(String key, String string) {
        Hawk.put(instanceKey + key, string);
    }

    private void saveFloatPreference(String key, Float flt) {
        Hawk.put(instanceKey + key, flt);
    }

    private void saveLongTimestampPreference(String key, Long timestamp) {
        Hawk.put(instanceKey + key, timestamp);
    }

    private Boolean getBooleanPreference(String key) {
        return Hawk.get(instanceKey + key, false);
    }

    private String getStringPreference(String key) {
        return Hawk.get(instanceKey + key, "");
    }

    private Float getFloatPreference(String key) {
        return Hawk.get(instanceKey + key, null);
    }

    private Long getLongTimestampPreference(String key) {
        return Hawk.get(instanceKey + key, 0L);
    }

    private Boolean checkPreferenceKeyAvailable(String key) {
        return Hawk.contains(instanceKey + key);
    }

    private void removePreference(String key) {
        Hawk.delete(instanceKey + key);
    }

    /**
     * =========================================================================================== *
     * PUBLIC METHODS FOR PREFERENCE (CALLS GENERIC METHODS ABOVE)
     * PUBLIC METHODS MAY NOT HAVE KEY AS PARAMETER
     * =========================================================================================== *
     */

    public void deleteAllPreference() {
        removeActiveUser();
        removeAuthTicket();
        removeAccessToken();
        removeRefreshToken();
        removeConfigs();
        removeLastUpdatedMessageTimestampMap();
        removeUserLastActivityMap();
        removeRoomDataMap();
        removeRoomListSetupFinished();
        removeFileProviderPathMap();
        removeFileMessageUriMap();
        removeMediaVolumePreference();
        removeLastDeleteTimestamp();
        removeNotificationMap();
        removeLastCallCountryTimestamp();
        removeCountryList();
        removeMyCountryCode();
        removeMyCountryFlagUrl();
        removeContactSyncPermissionAsked();
        removeContactSyncAllowedByUser();
        removeChatRoomContactActionDismissed();
        removeContactListUpdated();
        removeUnreadRoomIDs();
        removeMutedRoomIds();
        removePinnedRoomIDs();
    }

    /**
     * PROJECT CONFIGS
     */
    public Map<String, String> getCoreConfigs() {
        return Hawk.get(instanceKey + CORE, new HashMap<>());
    }

    public void saveCoreConfigs(Map<String, String> coreProjectConfigs) {
        Hawk.put(instanceKey + CORE, coreProjectConfigs);
    }

    public Map<String, String> getProjectConfigs() {
        return Hawk.get(instanceKey + PROJECT, new HashMap<>());
    }

    public void saveProjectConfigs(Map<String, String> coreProjectConfigs) {
        Hawk.put(instanceKey + PROJECT, coreProjectConfigs);
    }

    public Map<String, String> getCustomConfigs() {
        return Hawk.get(instanceKey + CUSTOM, new HashMap<>());
    }

    public void saveCustomConfigs(Map<String, String> coreProjectConfigs) {
        Hawk.put(instanceKey + CUSTOM, coreProjectConfigs);
    }

    public void removeConfigs() {
        removePreference(CORE);
        removePreference(PROJECT);
        removePreference(CUSTOM);
    }

    /**
     * COUNTRY LIST
     */
    public ArrayList<TAPCountryListItem> getCountryList() {
        return Hawk.get(instanceKey + K_COUNTRY_LIST, new ArrayList<>());
    }

    public void saveCountryList(ArrayList<TAPCountryListItem> countries) {
        Hawk.put(instanceKey + K_COUNTRY_LIST, countries);
    }

    public void removeCountryList() {
        removePreference(K_COUNTRY_LIST);
    }

    /**
     * LAST GET COUNTRY TIMESTAMP
     */
    public long getLastCallCountryTimestamp() {
        return getLongTimestampPreference(LAST_CALL_COUNTRY_TIMESTAMP);
    }

    public void saveLastCallCountryTimestamp(long timestamp) {
        saveLongTimestampPreference(LAST_CALL_COUNTRY_TIMESTAMP, timestamp);
    }

    public void removeLastCallCountryTimestamp() {
        removePreference(LAST_CALL_COUNTRY_TIMESTAMP);
    }

    /**
     * ACTIVE USER
     */
    public boolean checkActiveUser() {
        return null != getActiveUser();
    }

    public TAPUserModel getActiveUser() {
        return Hawk.get(instanceKey + K_USER, null);
    }

    public void saveActiveUser(TAPUserModel user) {
        Hawk.put(instanceKey + K_USER, user);
        TAPChatManager.getInstance(instanceKey).setActiveUser(user);
    }

    public void removeActiveUser() {
        removePreference(K_USER);
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
        saveStringPreference(K_AUTH_TICKET, authTicket);
    }

    public void removeAuthTicket() {
        removePreference(K_AUTH_TICKET);
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
        saveStringPreference(K_ACCESS_TOKEN, accessToken);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(K_ACCESS_TOKEN_EXPIRY, accessTokenExpiry);
    }

    public long getAccessTokenExpiry() {
        return getLongTimestampPreference(K_ACCESS_TOKEN_EXPIRY);
    }

    public void removeAccessToken() {
        removePreference(K_ACCESS_TOKEN);
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
        saveStringPreference(K_REFRESH_TOKEN, refreshToken);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(K_REFRESH_TOKEN_EXPIRY, refreshTokenExpiry);
    }

    public void removeRefreshToken() {
        removePreference(K_REFRESH_TOKEN);
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
        return Hawk.get(instanceKey + K_LAST_UPDATED, null);
    }

    private void saveLastUpdatedMessageTimestampMap(String roomID, long lastUpdated) {
        HashMap<String, Long> tempLastUpdated;
        if (null != getLastUpdatedMessageTimestampMap())
            tempLastUpdated = getLastUpdatedMessageTimestampMap();
        else tempLastUpdated = new LinkedHashMap<>();

        tempLastUpdated.put(roomID, lastUpdated);
        Hawk.put(instanceKey + K_LAST_UPDATED, tempLastUpdated);
    }

    private void removeLastUpdatedMessageTimestampMap() {
        removePreference(K_LAST_UPDATED);
    }

    /**
     * LAST ROOM MESSAGE DELETE TIME
     */

    public void saveLastRoomMessageDeleteTime() {
        Hawk.put(instanceKey + K_LAST_ROOM_MESSAGE_DELETE_TIME, System.currentTimeMillis());
    }

    private Long getLastRoomMessageDeleteTime() {
        return Hawk.get(instanceKey + K_LAST_ROOM_MESSAGE_DELETE_TIME, null);
    }

    /**
     * NEWEST PINNED MESSAGE
     */

    public void saveNewestPinnedMessage(String roomID, TAPMessageModel message) {
        HashMap<String, TAPMessageModel> newestPinnedMessages = Hawk.get(instanceKey + K_PINNED_MESSAGE, null);
        if (newestPinnedMessages == null) {
            newestPinnedMessages = new LinkedHashMap<>();
        }
        newestPinnedMessages.put(roomID, message);
        Hawk.put(instanceKey + K_PINNED_MESSAGE, newestPinnedMessages);

    }

    public TAPMessageModel getNewestPinnedMessage(String roomID) {
        HashMap<String, TAPMessageModel> newestPinnedMessages = Hawk.get(instanceKey + K_PINNED_MESSAGE, null);
        if (newestPinnedMessages != null) {
            return newestPinnedMessages.get(roomID);
        } else return null;
    }
    /**
     * USER LAST ACTIVITY
     */

    public HashMap<String, Long> getUserLastActivityMap() {
        return Hawk.get(instanceKey + K_USER_LAST_ACTIVITY);
    }

    public void saveUserLastActivityMap(HashMap<String, Long> userLastActivityMap) {
        Hawk.put(instanceKey + K_USER_LAST_ACTIVITY, userLastActivityMap);
    }

    public void removeUserLastActivityMap() {
        removePreference(K_USER_LAST_ACTIVITY);
    }

    /**
     * SAVE GROUP DATA
     */
    public HashMap<String, TAPRoomModel> getRoomDataMap() {
        return Hawk.get(instanceKey + K_GROUP_DATA_MAP);
    }

    public void saveRoomDataMap(HashMap<String, TAPRoomModel> roomDataMap) {
        Hawk.put(instanceKey + K_GROUP_DATA_MAP, roomDataMap);
    }

    public void removeRoomDataMap() {
        removePreference(K_GROUP_DATA_MAP);
    }

    public boolean isRoomDataMapAvailable() {
        return Hawk.contains(K_GROUP_DATA_MAP) && null != Hawk.get(instanceKey + K_GROUP_DATA_MAP);
    }

    /**
     * CONTACT SYNC PERMISSION
     */

    public boolean isContactSyncPermissionAsked() {
        return getBooleanPreference(IS_PERMISSION_SYNC_ASKED);
    }

    public void saveContactSyncPermissionAsked(boolean userSyncPermissionAsked) {
        saveBooleanPreference(IS_PERMISSION_SYNC_ASKED, userSyncPermissionAsked);
    }

    public void removeContactSyncPermissionAsked() {
        removePreference(IS_PERMISSION_SYNC_ASKED);
    }

    public boolean isContactSyncAllowedByUser() {
        return getBooleanPreference(IS_CONTACT_SYNC_ALLOWED_BY_USER);
    }

    public void saveContactSyncAllowedByUser(boolean isContactSyncAllowed) {
        saveBooleanPreference(IS_CONTACT_SYNC_ALLOWED_BY_USER, isContactSyncAllowed);
    }

    public void removeContactSyncAllowedByUser() {
        removePreference(IS_CONTACT_SYNC_ALLOWED_BY_USER);
    }

    /**
     * CHAT ROOM CONTACT ACTION
     */

    private HashMap<String, Boolean> getChatRoomContactActionsMap() {
        return Hawk.get(instanceKey + K_CHAT_ROOM_CONTACT_ACTION, new HashMap<>());
    }

    public boolean isChatRoomContactActionDismissed(String roomID) {
        Boolean isDismissed = getChatRoomContactActionsMap().get(roomID);
        return null == isDismissed ? false : isDismissed;
    }

    public void saveChatRoomContactActionDismissed(String roomID) {
        HashMap<String, Boolean> map = getChatRoomContactActionsMap();
        map.put(roomID, true);
        Hawk.put(instanceKey + K_CHAT_ROOM_CONTACT_ACTION, map);
    }

    public void removeChatRoomContactActionDismissed() {
        removePreference(K_CHAT_ROOM_CONTACT_ACTION);
    }

    /**
     * CHAT ROOM SWIPE BUTTON
     */

    public ArrayList<String> getUnreadRoomIDs() {
        return Hawk.get(instanceKey + K_UNREAD_ROOM_LIST, new ArrayList<>());
    }

    public void saveUnreadRoomIDs(ArrayList<String> unreadRoomIDs) {
        Hawk.put(instanceKey + K_UNREAD_ROOM_LIST, unreadRoomIDs);
    }

    public boolean isUnreadRoomIDsEmpty() {
        return getUnreadRoomIDs().isEmpty();
    }

    public void removeUnreadRoomIDs() {
        removePreference(instanceKey + K_UNREAD_ROOM_LIST);
    }

    public HashMap<String, Long> getMutedRoomIDs() {
        return Hawk.get(instanceKey + K_MUTED_ROOM_LIST, new HashMap<>());
    }

    public void saveMutedRoomIDs(HashMap<String, Long> mutedRoomIDs) {
        Hawk.put(instanceKey + K_MUTED_ROOM_LIST, mutedRoomIDs);
    }

    public boolean isMutedRoomIDsEmpty() {
        return getMutedRoomIDs().isEmpty();
    }

    public void removeMutedRoomIds() {
        removePreference(instanceKey + K_MUTED_ROOM_LIST);
    }

    public ArrayList<String> getPinnedRoomIDs() {
        return Hawk.get(instanceKey + K_PINNED_ROOM_LIST, new ArrayList<>());
    }

    public void savePinnedRoomIDs(ArrayList<String> pinnedRoomIDs) {
        Hawk.put(instanceKey + K_PINNED_ROOM_LIST, pinnedRoomIDs);
    }

    public boolean isPinnedRoomIDsEmpty() {
        return getPinnedRoomIDs().isEmpty();
    }

    public void removePinnedRoomIDs() {
        removePreference(instanceKey + K_PINNED_ROOM_LIST);
    }

    public void removePinnedRoomID(String roomId) {
        ArrayList<String> pinnedRoomIDs = getPinnedRoomIDs();
        pinnedRoomIDs.remove(roomId);
        savePinnedRoomIDs(pinnedRoomIDs);
    }


    /**
     * MY COUNTRY CODE
     */
    public String getMyCountryCode() {
        return getStringPreference(MY_COUNTRY_CODE);
    }

    public void saveMyCountryCode(String myCountryCode) {
        saveStringPreference(MY_COUNTRY_CODE, myCountryCode);
        TAPContactManager.getInstance(instanceKey).setMyCountryCode(myCountryCode);
    }

    public void removeMyCountryCode() {
        removePreference(MY_COUNTRY_CODE);
    }

    public String getMyCountryFlagUrl() {
        return getStringPreference(MY_COUNTRY_FLAG_URL);
    }

    public void saveMyCountryFlagUrl(String myCountryFlagUrl) {
        saveStringPreference(MY_COUNTRY_FLAG_URL, myCountryFlagUrl);
    }

    public void removeMyCountryFlagUrl() {
        removePreference(MY_COUNTRY_FLAG_URL);
    }

    /**
     * ROOM LIST FIRST SETUP
     */

    public void setRoomListSetupFinished() {
        saveLongTimestampPreference(K_IS_ROOM_LIST_SETUP_FINISHED, System.currentTimeMillis());
    }

    public Boolean isRoomListSetupFinished() {
        return checkPreferenceKeyAvailable(K_IS_ROOM_LIST_SETUP_FINISHED);
    }

    private void removeRoomListSetupFinished() {
        removePreference(K_IS_ROOM_LIST_SETUP_FINISHED);
    }

    /**
     * FILE PROVIDER PATH
     */
    public HashMap<String, String> getFileProviderPathMap() {
        return Hawk.get(instanceKey + K_FILE_PATH_MAP, null);
    }

    public void saveFileProviderPathMap(HashMap<String, String> fileProviderPathMap) {
        Hawk.put(instanceKey + K_FILE_PATH_MAP, fileProviderPathMap);
    }

    public void removeFileProviderPathMap() {
        removePreference(K_FILE_PATH_MAP);
    }

    /**
     * FILE URI CACHE
     */
    public HashMap<String, HashMap<String, String>> getFileMessageUriMap() {
        return Hawk.get(instanceKey + K_FILE_URI_MAP, null);
    }

    public void saveFileMessageUriMap(HashMap<String, HashMap<String, String>> fileUriMap) {
        Hawk.put(instanceKey + K_FILE_URI_MAP, fileUriMap);
    }

    public void removeFileMessageUriMap() {
        removePreference(K_FILE_URI_MAP);
    }

    /**
     * LATEST SETTING FOR MEDIA VOLUME
     */
    public void saveMediaVolumePreference(float volume) {
        saveFloatPreference(K_MEDIA_VOLUME, volume);
    }

    public float getMediaVolumePreference() {
        Float volume = getFloatPreference(K_MEDIA_VOLUME);
        return volume == null ? 1f : volume;
    }

    public void removeMediaVolumePreference() {
        removePreference(K_MEDIA_VOLUME);
    }

    /**
     * Firebase Token
     */
    public void saveFirebaseToken(String firebaseToken) {
        saveStringPreference(K_FIREBASE_TOKEN, firebaseToken);
    }

    public String getFirebaseToken() {
        return getStringPreference(K_FIREBASE_TOKEN);
    }

    public Boolean checkFirebaseToken(String newFirebaseToken) {
        if (!checkPreferenceKeyAvailable(K_FIREBASE_TOKEN)) {
            return false;
        } else {
            return newFirebaseToken.equals(getFirebaseToken());
        }
    }

    public Boolean checkFirebaseToken() {
        return checkPreferenceKeyAvailable(K_FIREBASE_TOKEN) &&
                null != getFirebaseToken() &&
                !getFirebaseToken().isEmpty() &&
                !"0".equals(getFirebaseToken());
    }

    /**
     * Old Data (Auto Clean) Last Delete Timestamp
     */
    public void saveLastDeleteTimestamp(Long lastDeleteTimestamp) {
        saveLongTimestampPreference(K_LAST_DELETE_TIMESTAMP, lastDeleteTimestamp);
    }

    public Long getLastDeleteTimestamp() {
        return getLongTimestampPreference(K_LAST_DELETE_TIMESTAMP);
    }

    public Boolean isLastDeleteTimestampExists() {
        if (!checkPreferenceKeyAvailable(K_LAST_DELETE_TIMESTAMP) || null == getLastDeleteTimestamp()) {
            return false;
        } else {
            return 0L != getLastDeleteTimestamp();
        }
    }

    private void removeLastDeleteTimestamp() {
        removePreference(K_LAST_DELETE_TIMESTAMP);
    }

    /**
     * Notification Message Map
     */
    public void saveNotificationMessageMap(String notificationMessagesMap) {
        saveStringPreference(K_NOTIFICATION_MESSAGE_MAP, notificationMessagesMap);
    }

    public String getNotificationMessageMap() {
        return getStringPreference(K_NOTIFICATION_MESSAGE_MAP);
    }

    public void clearNotificationMessageMap() {
        removePreference(K_NOTIFICATION_MESSAGE_MAP);
    }

    public boolean checkNotificationMap() {
        return checkPreferenceKeyAvailable(K_NOTIFICATION_MESSAGE_MAP);
    }

    private void removeNotificationMap() {
        removePreference(K_NOTIFICATION_MESSAGE_MAP);
    }

    /**
     * DELETE FILE
     */
    public void deletePhysicalFile(TAPMessageEntity message) {
        if (TYPE_IMAGE == message.getType()) {
            try {
                // Delete image from cache
                HashMap<String, Object> messageData = TAPUtils.toHashMap(message.getData());
                TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache((String) messageData.get(FILE_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (TYPE_VIDEO == message.getType() || TYPE_FILE == message.getType()) {
            HashMap<String, Object> messageData = TAPUtils.toHashMap(message.getData());
            if (null == messageData) {
                return;
            }
            Uri fileMessageUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(TAPMessageModel.fromMessageEntity(message));
            if (null != fileMessageUri && "content".equals(fileMessageUri.getScheme()) &&
                    null != fileMessageUri.getPath() &&
                    fileMessageUri.getPath().contains(TapTalk.getClientAppName(instanceKey))) {
                try {
                    // Delete file from TapTalk folder
                    TapTalk.appContext.getContentResolver().delete(fileMessageUri, null, null);
                    TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(
                            message.getRoomID(), (String) messageData.get(FILE_ID));
                } catch (IllegalArgumentException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "deletePhysicalFile: " + e.getMessage());
                    }
                }
            }
        }
    }

    // FIXME: GET CONTACT LIST FROM SERVER ONCE AT APPLICATION START TO FIX DATABASE CONTACT
    /**
     * CONTACT LIST UPDATE
     */
    public void setContactListUpdated() {
        saveLongTimestampPreference(K_IS_CONTACT_LIST_UPDATED, System.currentTimeMillis());
    }

    public Boolean isContactListUpdated() {
        return checkPreferenceKeyAvailable(K_IS_CONTACT_LIST_UPDATED);
    }

    private void removeContactListUpdated() {
        removePreference(K_IS_CONTACT_LIST_UPDATED);
    }

    /**
     * API HEADER
     */
    public void saveApplicationID(String applicationID) {
        saveStringPreference(APP_ID, applicationID);
    }

    public String getApplicationID() {
        return getStringPreference(APP_ID);
    }

    public boolean checkApplicationIDAvailability() {
        return checkPreferenceKeyAvailable(APP_ID);
    }

    public void removeApplicationID() {
        removePreference(APP_ID);
    }

    public void saveApplicationSecret(String applicationSecret) {
        saveStringPreference(APP_SECRET, applicationSecret);
    }

    public String getApplicationSecret() {
        return getStringPreference(APP_SECRET);
    }

    public boolean checkApplicationSecretAvailability() {
        return checkPreferenceKeyAvailable(APP_SECRET);
    }

    public void removeApplicationSecret() {
        removePreference(APP_SECRET);
    }

    public void saveUserAgent(String userAgent) {
        saveStringPreference(USER_AGENT, userAgent);
    }

    public String getUserAgent() {
        return getStringPreference(USER_AGENT);
    }

    public boolean checkUserAgentAvailability() {
        return checkPreferenceKeyAvailable(USER_AGENT);
    }

    public void removeUserAgent() {
        removePreference(USER_AGENT);
    }

    /**
     * =========================================================================================== *
     * DATABASE METHODS
     * =========================================================================================== *
     */

    // initialized Database Managernya yang di panggil di class Homing Pigeon
    public void initDatabaseManager(String databaseType, Application application) {
        TAPDatabaseManager.getInstance(instanceKey).setRepository(databaseType, application);
    }

    // Message
    public void deleteMessage(List<TAPMessageEntity> messageEntities, TAPDatabaseListener listener) {
        TAPDatabaseManager.getInstance(instanceKey).deleteMessage(new ArrayList<>(messageEntities), listener);
    }

    public void deleteRoomMessageBeforeTimestamp(String roomID, long minimumTimestamp, TAPDatabaseListener listener) {
        TAPDatabaseManager.getInstance(instanceKey).deleteRoomMessageBeforeTimestamp(roomID, minimumTimestamp, listener);
    }

    public void insertToDatabase(TAPMessageEntity messageEntity) {
        TAPDatabaseManager.getInstance(instanceKey).insert(messageEntity);
    }

    public void insertToDatabase(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages) {
        TAPDatabaseManager.getInstance(instanceKey).insert(new ArrayList<>(messageEntities), isClearSaveMessages);
    }

    public void insertToDatabase(List<TAPMessageEntity> messageEntities, boolean isClearSaveMessages, TAPDatabaseListener listener) {
        TAPDatabaseManager.getInstance(instanceKey).insert(new ArrayList<>(messageEntities), isClearSaveMessages, listener);
    }

    public void deleteFromDatabase(String messageLocalID) {
        TAPDatabaseManager.getInstance(instanceKey).delete(messageLocalID);
    }

    public void updateSendingMessageToFailed() {
        TAPDatabaseManager.getInstance(instanceKey).updatePendingStatus();
    }

    public void updateSendingMessageToFailed(String localID) {
        TAPDatabaseManager.getInstance(instanceKey).updatePendingStatus(localID);
    }

    public void updateFailedMessageToSending(String localID) {
        TAPDatabaseManager.getInstance(instanceKey).updateFailedStatusToSending(localID);
    }

    public void updateMessageAsReadInDatabase(String messageID) {
        TAPDatabaseManager.getInstance(instanceKey).updateMessageAsRead(messageID);
    }

    public void updateMessagesAsReadInDatabase(List<String> messageIDs) {
        TAPDatabaseManager.getInstance(instanceKey).updateMessagesAsRead(messageIDs);
    }

    public LiveData<List<TAPMessageEntity>> getMessagesLiveData() {
        return TAPDatabaseManager.getInstance(instanceKey).getMessagesLiveData();
    }

    public void getAllMessagesInRoomFromDatabase(String roomID, boolean excludeHidden, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getAllMessagesInRoom(roomID, excludeHidden, listener);
    }

    public void getMessagesFromDatabaseDesc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMessagesDesc(roomID, listener);
    }

    public void getMessagesFromDatabaseDesc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener, long lastTimestamp) {
        TAPDatabaseManager.getInstance(instanceKey).getMessagesDesc(roomID, listener, lastTimestamp);
    }

    public void getRoomMessagesFromDatabaseBeforeTimestampDesc(String roomID, long lastTimestamp, int itemLimit, boolean excludeHidden, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomMessagesBeforeTimestampDesc(roomID, listener, lastTimestamp, itemLimit, excludeHidden);
    }

    public void getMessagesFromDatabaseAsc(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMessagesAsc(roomID, listener);
    }

    public void searchAllMessagesFromDatabase(String keyword, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).searchAllMessages(keyword, listener);
    }

    public void searchAllRoomMessagesFromDatabase(String keyword, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).searchAllRoomMessages(keyword, roomID, listener);
    }

    public void getMessageByLocalIDFromDatabase(String localID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMessageByLocalID(localID, listener);
    }

    public void getMessageByLocalIDsFromDatabase(List<String> localIDs, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMessageByLocalIDs(localIDs, listener);
    }

    public void getMessageBySenderUserIDFromDatabase(String userID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMessageBySenderUserID(userID, listener);
    }

    public void getRoomList(List<TAPMessageEntity> saveMessages, boolean isCheckUnreadFirst, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getRoomList(getActiveUser().getUserID(), getActiveUser().getUsername(), saveMessages, isCheckUnreadFirst, listener);
    }

    public void getRoomLastMessage(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomLastMessage(roomID, listener);
    }

    public void getAllUnreadMessagesFromRoom(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser())
            return;
        TAPDatabaseManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(getActiveUser().getUserID(), roomID, listener);
    }

    public void getAllUnreadMentionsFromRoom(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser())
            return;
        TAPDatabaseManager.getInstance(instanceKey).getAllUnreadMentionsFromRoom(getActiveUser().getUserID(), getActiveUser().getUsername(), roomID, listener);
    }

    public void getRoomList(boolean isCheckUnreadFirst, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getRoomList(getActiveUser().getUserID(), getActiveUser().getUsername(), isCheckUnreadFirst, listener);
    }

    public void searchAllRoomsFromDatabase(String keyword, TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).searchAllRooms(getActiveUser().getUserID(), getActiveUser().getUsername(), keyword, listener);
    }

    public void getRoomModel(TAPUserModel userModel, TAPDatabaseListener<TAPRoomModel> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getRoom(getActiveUser().getUserID(), userModel, listener);
    }

    public void getRoomMedias(Long lastTimestamp, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        getRoomMedias(lastTimestamp, roomID, MAX_ITEMS_PER_PAGE, listener);
    }

    public void getRoomMedias(Long lastTimestamp, String roomID, int numberOfItems, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomMedias(lastTimestamp, roomID, numberOfItems, listener);
    }

    public void getRoomFiles(Long lastTimestamp, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        getRoomFiles(lastTimestamp, roomID, MAX_ITEMS_PER_PAGE, listener);
    }

    public void getRoomFiles(Long lastTimestamp, String roomID, int numberOfItems, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomFiles(lastTimestamp, roomID, numberOfItems, listener);
    }

    public void getRoomLinks(Long lastTimestamp, String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        getRoomLinks(lastTimestamp, roomID, MAX_ITEMS_PER_PAGE, listener);
    }

    public void getRoomLinks(Long lastTimestamp, String roomID, int numberOfItems, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomLinks(lastTimestamp, roomID, numberOfItems, listener);
    }

    public void getRoomMediaMessageBeforeTimestamp(String roomID, long minimumTimestamp, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomMediaMessageBeforeTimestamp(roomID, minimumTimestamp, listener);
    }

    public void getRoomMediaMessage(String roomID, TAPDatabaseListener<TAPMessageEntity> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getRoomMediaMessage(roomID, listener);
    }

    public void getUnreadCountPerRoom(String roomID, final TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getUnreadCountPerRoom(getActiveUser().getUserID(), getActiveUser().getUsername(), roomID, listener);
    }

    public void getUnreadCount(final TAPDatabaseListener<TAPMessageEntity> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getUnreadCount(getActiveUser().getUserID(), listener);
    }

    public void getMinCreatedOfUnreadMessage(String roomID, final TAPDatabaseListener<Long> listener) {
        if (null == getActiveUser()) {
            return;
        }
        TAPDatabaseManager.getInstance(instanceKey).getMinCreatedOfUnreadMessage(getActiveUser().getUserID(), roomID, listener);
    }

    public void getOldestCreatedTimeFromRoom(String roomID, final TAPDatabaseListener<Long> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getOldestCreatedTimeFromRoom(roomID, listener);
    }

    public void deleteAllMessage() {
        TAPDatabaseManager.getInstance(instanceKey).deleteAllMessage();
    }

    public void deleteMessageByRoomId(String roomId, TAPDatabaseListener listener) {
        TAPDatabaseManager.getInstance(instanceKey).deleteMessageByRoomId(roomId, listener);
    }

    // Recent Search
    public void insertToDatabase(TAPRecentSearchEntity recentSearchEntity) {
        TAPDatabaseManager.getInstance(instanceKey).insert(recentSearchEntity);
    }

    public void deleteFromDatabase(TAPRecentSearchEntity recentSearchEntity) {
        TAPDatabaseManager.getInstance(instanceKey).delete(recentSearchEntity);
    }

    public void deleteFromDatabase(List<TAPRecentSearchEntity> recentSearchEntities) {
        TAPDatabaseManager.getInstance(instanceKey).delete(recentSearchEntities);
    }

    public void deleteAllRecentSearch() {
        TAPDatabaseManager.getInstance(instanceKey).deleteAllRecentSearch();
    }

    public LiveData<List<TAPRecentSearchEntity>> getRecentSearchLive() {
        return TAPDatabaseManager.getInstance(instanceKey).getRecentSearchLive();
    }

    // My Contact
    public void getMyContactList(TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getMyContactList(listener);
    }

    public void getNonContactUsersFromDatabase(TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getNonContactUsers(listener);
    }

    public LiveData<List<TAPUserModel>> getMyContactList() {
        return TAPDatabaseManager.getInstance(instanceKey).getMyContactList();
    }

    public void searchContactsByName(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).searchContactsByName(keyword, listener);
    }

    public void searchContactsByNameAndUsername(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).searchContactsByNameAndUsername(keyword, listener);
    }

    public void searchNonContactUsersFromDatabase(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).searchNonContactUsers(keyword, listener);
    }

    public void insertMyContactToDatabase(TAPUserModel... userModels) {
        TAPDatabaseManager.getInstance(instanceKey).insertMyContact(userModels);
    }

    public void insertMyContactToDatabase(TAPDatabaseListener<TAPUserModel> listener, TAPUserModel... userModels) {
        TAPDatabaseManager.getInstance(instanceKey).insertMyContact(listener, userModels);
    }

    public void insertMyContactToDatabase(List<TAPUserModel> userModels) {
        TAPDatabaseManager.getInstance(instanceKey).insertMyContact(userModels);
    }

    // Set isContact value to 0 or 1 then insert user model to database
    public void checkContactAndInsertToDatabase(TAPUserModel userModel) {
        TAPDatabaseManager.getInstance(instanceKey).checkContactAndInsert(userModel);
    }

    public void getUserWithXcUserID(String xcUserID, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getUserWithXcUserID(xcUserID, listener);
    }

    public void insertAndGetMyContact(List<TAPUserModel> userModels, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).insertAndGetMyContact(userModels, listener);
    }

    public void deleteMyContactFromDatabase(TAPUserModel... userModels) {
        TAPDatabaseManager.getInstance(instanceKey).deleteMyContact(userModels);
    }

    public void deleteMyContactFromDatabase(List<TAPUserModel> userModels) {
        TAPDatabaseManager.getInstance(instanceKey).deleteMyContact(userModels);
    }

    public void deleteAllContact() {
        TAPDatabaseManager.getInstance(instanceKey).deleteAllContact();
    }

    public void updateMyContact(TAPUserModel userModels) {
        TAPDatabaseManager.getInstance(instanceKey).updateMyContact(userModels);
    }

    public void checkUserInMyContacts(String userID, TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).checkUserInMyContacts(userID, listener);
    }

    public void getAllUserData(TAPDatabaseListener<TAPUserModel> listener) {
        TAPDatabaseManager.getInstance(instanceKey).getAllUserData(listener);
    }

    //General
    public void deleteAllFromDatabase() {
        deleteAllMessage();
        deleteAllRecentSearch();
        deleteAllContact();
    }

    /**
     * =========================================================================================== *
     * API CALLS
     * =========================================================================================== *
     */

    public void getAuthTicket(String ipAddress, String userAgent, String userPlatform, String userDeviceID, String xcUserID
            , String fullname, String email, String phone, String username, TAPDefaultDataView<TAPAuthTicketResponse> view) {
        TAPApiManager.getInstance(instanceKey).getAuthTicket(ipAddress, userAgent, userPlatform, userDeviceID, xcUserID,
                fullname, email, phone, username, new TAPDefaultSubscriber<>(view));
    }

    public void getAccessTokenFromApi(TAPDefaultDataView<TAPGetAccessTokenResponse> view) {
        TAPApiManager.getInstance(instanceKey).getAccessToken(new TAPDefaultSubscriber<>(view));
    }

    public void requestOTPLogin(int countryID, String phone, String channel, TAPDefaultDataView<TAPOTPResponse> view) {
        TAPApiManager.getInstance(instanceKey).requestOTPLogin("phone", countryID, phone, channel, new TAPDefaultSubscriber<>(view));
    }

    public void verifyOTPLogin(long otpID, String otpKey, String otpCode, TAPDefaultDataView<TAPLoginOTPVerifyResponse> view) {
        TAPApiManager.getInstance(instanceKey).verifyingOTPLogin(otpID, otpKey, otpCode, new TAPDefaultSubscriber<>(view));
    }

    public void refreshAccessToken(TAPDefaultDataView<TAPGetAccessTokenResponse> view) {
        TAPApiManager.getInstance(instanceKey).refreshAccessToken(new TAPDefaultSubscriber<>(view));
    }

    public void validateAccessToken(TAPDefaultDataView<TAPErrorModel> view) {
        if (checkAccessTokenAvailable()) {
            if (null != validateSub) {
                validateSub.unsubscribe();
            }
            TAPApiManager.getInstance(instanceKey).validateAccessToken(validateSub = new TAPDefaultSubscriber<>(view));
        }
    }

    public void registerFcmTokenToServer(String fcmToken, TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).registerFcmTokenToServer(fcmToken, new TAPDefaultSubscriber<>(view));
    }

    public void sendCustomMessage(String roomID, String localID, Integer messageType, String body, HashMap<String, Object> data, String filterID, Boolean isHidden, TAPDefaultDataView<TAPCommonResponse> view) {
        String recipientUserID = "";
        if (roomID.contains("-")) {
            recipientUserID = TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(roomID);
        }
        TAPApiManager.getInstance(instanceKey).sendCustomMessage(recipientUserID.isEmpty() ? roomID : "", recipientUserID, localID, messageType, body, data, filterID, isHidden, new TAPDefaultSubscriber<>(view));
    }

    public void getMessageRoomListAndUnread(String userID, TAPDefaultDataView<TAPGetRoomListResponse> view) {
        if (null != roomListAndUnreadSub) roomListAndUnreadSub.unsubscribe();
        TAPApiManager.getInstance(instanceKey).getRoomList(userID, roomListAndUnreadSub = new TAPDefaultSubscriber<>(view));
    }

    public void unsubscribeRoomListAndUnreadApi() {
        if (null != roomListAndUnreadSub) roomListAndUnreadSub.unsubscribe();
    }

    public void getNewAndUpdatedMessage(TAPDefaultDataView<TAPGetRoomListResponse> view) {
        TAPApiManager.getInstance(instanceKey).getPendingAndUpdatedMessage(new TAPDefaultSubscriber<>(view));
    }

    public void getMessageListByRoomAfter(String roomID, Long minCreated, Long lastUpdated, TAPDefaultDataView<TAPGetMessageListByRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getMessageListByRoomAfter(roomID, minCreated, lastUpdated, new TAPDefaultSubscriber<>(view));
    }

    public void getMessageListByRoomBefore(String roomID, Long maxCreated, Integer limit, TAPDefaultDataView<TAPGetMessageListByRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getMessageListByRoomBefore(roomID, maxCreated, limit, new TAPDefaultSubscriber<>(view));
    }

    public void updateMessageStatusAsDelivered(List<String> messageIDs, TAPDefaultDataView<TAPUpdateMessageStatusResponse> view) {
        if (!messageIDs.isEmpty())
            TAPApiManager.getInstance(instanceKey).updateMessageStatusAsDelivered(messageIDs, new TAPDefaultSubscriber<>(view));
    }

    public void updateMessageStatusAsRead(List<String> messageIDs, TAPDefaultDataView<TAPUpdateMessageStatusResponse> view) {
        TAPApiManager.getInstance(instanceKey).updateMessageStatusAsRead(messageIDs, new TAPDefaultSubscriber<>(view));
    }

    public void deleteMessagesAPI(String roomID, List<String> messageIDs, boolean isForEveryone, TAPDefaultDataView<TAPDeleteMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).deleteMessagesAPI(roomID, messageIDs, isForEveryone, new TAPDefaultSubscriber<>(view));
    }

    public void deleteMessagesAPI(String roomID, String messageID, boolean isForEveryone, TAPDefaultDataView<TAPDeleteMessageResponse> view) {
        List<String> messageIDs = new ArrayList<>();
        messageIDs.add(messageID);
        TAPApiManager.getInstance(instanceKey).deleteMessagesAPI(roomID, messageIDs, isForEveryone, new TAPDefaultSubscriber<>(view));
    }

    public void deleteMessagesAPI(String roomID, String messageID, boolean isForEveryone) {
        deleteMessagesAPI(roomID, messageID, isForEveryone, new TAPDefaultDataView<TAPDeleteMessageResponse>() {
        });
    }

    public void getMyContactListFromAPI(TAPDefaultDataView<TAPContactResponse> view) {
        if (null != contactListSubs) contactListSubs.unsubscribe();
        TAPApiManager.getInstance(instanceKey).getMyContactListFromAPI(contactListSubs = new TAPDefaultSubscriber<>(view));
    }

    public void unsubscribeContactListFromAPI() {
        if (null != contactListSubs) contactListSubs.unsubscribe();
    }

    public void addContactApi(String userID, TAPDefaultDataView<TAPAddContactResponse> view) {
        TAPApiManager.getInstance(instanceKey).addContact(userID, new TAPDefaultSubscriber<>(view));
    }

    public void addContactByPhone(List<String> phones, TAPDefaultDataView<TAPAddContactByPhoneResponse> view) {
        TAPApiManager.getInstance(instanceKey).addContactByPhone(phones, new TAPDefaultSubscriber<>(view));
    }

    public void removeContactApi(String userID, TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).removeContact(userID, new TAPDefaultSubscriber<>(view));
    }

    public void getCountryList(TAPDefaultDataView<TAPCountryListResponse> view) {
        TAPApiManager.getInstance(instanceKey).getCountryList(new TAPDefaultSubscriber<>(view));
    }

    public void register(String fullName, String username, Integer countryID, String phone, String email, String password, TAPDefaultDataView<TAPRegisterResponse> view) {
        TAPApiManager.getInstance(instanceKey).register(fullName, username, countryID, phone, email, password, new TAPDefaultSubscriber<>(view));
    }

    public void logout(TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).logout(new TAPDefaultSubscriber<>(view));
    }

    //Group and Chat Room
    public void createGroupChatRoom(String roomName, List<String> participantIDs, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).createChatRoom(roomName, TYPE_GROUP, participantIDs, new TAPDefaultSubscriber<>(view));
    }

    public void getChatRoomData(String roomID, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getChatRoomData(roomID, new TAPDefaultSubscriber<>(view));
    }

    public void getChatRoomByXcRoomID(String xcRoomID, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getChatRoomByXcRoomID(xcRoomID, new TAPDefaultSubscriber<>(view));
    }

    public void updateChatRoom(String roomID, String roomName, TAPDefaultDataView<TAPUpdateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).updateChatRoom(roomID, roomName, new TAPDefaultSubscriber<>(view));
    }

    public void addRoomParticipant(String roomID, List<String> userIDs, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).addRoomParticipant(roomID, userIDs, new TAPDefaultSubscriber<>(view));
    }

    public void removeRoomParticipant(String roomID, List<String> userIDs, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).removeRoomParticipant(roomID, userIDs, new TAPDefaultSubscriber<>(view));
    }

    public void leaveChatRoom(String roomID, TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).leaveChatRoom(roomID, new TAPDefaultSubscriber<>(view));
    }

    public void promoteGroupAdmins(String roomID, List<String> userIDs, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).promoteGroupAdmins(roomID, userIDs, new TAPDefaultSubscriber<>(view));
    }

    public void demoteGroupAdmins(String roomID, List<String> userIDs, TAPDefaultDataView<TAPCreateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).demoteGroupAdmins(roomID, userIDs, new TAPDefaultSubscriber<>(view));
    }

    public void deleteChatRoom(TAPRoomModel room, TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).deleteChatRoom(room, getActiveUser().getUserID(),
                getAccessTokenExpiry(), new TAPDefaultSubscriber<>(view));
    }

    // Search User
    private TAPDefaultSubscriber<TAPBaseResponse<TAPGetUserResponse>, TAPDefaultDataView<TAPGetUserResponse>, TAPGetUserResponse> searchUserSubscriber;

    public void getUserByIdFromApi(String id, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).getUserByID(id, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void getUserByXcUserIdFromApi(String xcUserID, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).getUserByXcUserID(xcUserID, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void getUserByUsernameFromApi(String username, boolean ignoreCase, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).getUserByUsername(username, ignoreCase, searchUserSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void getMultipleUsersByIdFromApi(List<String> ids, TAPDefaultDataView<TAPGetMultipleUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).getMultipleUserByID(ids, new TAPDefaultSubscriber<>(view));
    }

    public void cancelUserSearchApiCall() {
        if (null != searchUserSubscriber) {
            searchUserSubscriber.unsubscribe();
        }
    }

    // Check Username
    private TAPDefaultSubscriber<TAPBaseResponse<TAPCheckUsernameResponse>, TAPDefaultDataView<TAPCheckUsernameResponse>, TAPCheckUsernameResponse> checkUsernameSubscriber;

    public void checkUsernameExists(String username, TAPDefaultDataView<TAPCheckUsernameResponse> view) {
        TAPApiManager.getInstance(instanceKey).checkUsernameExists(username, checkUsernameSubscriber = new TAPDefaultSubscriber<>(view));
    }

    public void cancelCheckUsernameApiCall() {
        if (null != checkUsernameSubscriber) {
            checkUsernameSubscriber.unsubscribe();
        }
    }

    // Upload File
    private HashMap<String, TAPDefaultSubscriber<TAPBaseResponse<TAPUploadFileResponse>, TAPDefaultDataView<TAPUploadFileResponse>, TAPUploadFileResponse>> uploadSubscribers;

    public void uploadImage(String localID, File imageFile, String roomID, String caption, String mimeType,
                            ProgressRequestBody.UploadCallbacks uploadCallback,
                            TAPDefaultDataView<TAPUploadFileResponse> view) {
        TAPApiManager.getInstance(instanceKey).uploadImage(imageFile, roomID, caption, mimeType, uploadCallback, getUploadSubscriber(roomID, localID, view));
    }

    public void uploadVideo(String localID, File videoFile, String roomID, String caption, String mimeType,
                            ProgressRequestBody.UploadCallbacks uploadCallback,
                            TAPDefaultDataView<TAPUploadFileResponse> view) {
        TAPApiManager.getInstance(instanceKey).uploadVideo(videoFile, roomID, caption, mimeType, uploadCallback, getUploadSubscriber(roomID, localID, view));
    }

    public void uploadFile(String localID, File file, String roomID, String mimeType,
                           ProgressRequestBody.UploadCallbacks uploadCallback,
                           TAPDefaultDataView<TAPUploadFileResponse> view) {
        TAPApiManager.getInstance(instanceKey).uploadFile(file, roomID, mimeType, uploadCallback, getUploadSubscriber(roomID, localID, view));
    }

    public void uploadAudio(String localID, File file, String roomID, String mimeType,
                           ProgressRequestBody.UploadCallbacks uploadCallback,
                           TAPDefaultDataView<TAPUploadFileResponse> view) {
        TAPApiManager.getInstance(instanceKey).uploadAudio(file, roomID, mimeType, uploadCallback, getUploadSubscriber(roomID, localID, view));
    }

    private HashMap<String, TAPDefaultSubscriber<TAPBaseResponse<TAPUploadFileResponse>, TAPDefaultDataView<TAPUploadFileResponse>, TAPUploadFileResponse>>
    getUploadSubscribers() {
        return null == uploadSubscribers ? uploadSubscribers = new HashMap<>() : uploadSubscribers;
    }

    private TAPDefaultSubscriber<TAPBaseResponse<TAPUploadFileResponse>, TAPDefaultDataView<TAPUploadFileResponse>, TAPUploadFileResponse>
    getUploadSubscriber(String roomID, String localID, TAPDefaultDataView<TAPUploadFileResponse> view) {
        getUploadSubscribers().put(roomID, new TAPDefaultSubscriber<>(view, localID));
        return getUploadSubscribers().get(roomID);
    }

    public void unSubscribeToUploadImage(String roomID) {
        if (null == getUploadSubscribers().get(roomID)) {
            return;
        }
        getUploadSubscribers().get(roomID).unsubscribe();
    }

    public void cancelUploadImage(Context context, String localID) {
        Intent intent = new Intent(UploadCancelled);
        intent.putExtra(UploadLocalID, localID);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void uploadProfilePicture(File imageFile, String mimeType,
                                     ProgressRequestBody.UploadCallbacks uploadCallback,
                                     TAPDefaultDataView<TAPGetUserResponse> view) {
        cancelUploadProfilePicture();
        uploadProfilePictureSubscriber = new TAPDefaultSubscriber<>(view);
        TAPApiManager.getInstance(instanceKey).uploadProfilePicture(imageFile, mimeType, uploadCallback, uploadProfilePictureSubscriber);
    }

    private void cancelUploadProfilePicture() {
        if (null != uploadProfilePictureSubscriber) {
            uploadProfilePictureSubscriber.unsubscribe();
        }
    }

    private TAPDefaultSubscriber<TAPBaseResponse<TAPGetUserResponse>, TAPDefaultDataView<TAPGetUserResponse>, TAPGetUserResponse> uploadProfilePictureSubscriber;

    //Upload Room Picture
    public void uploadRoomPicture(File imageFile, String mimeType, String roomID, TAPDefaultDataView<TAPUpdateRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).uploadGroupPicture(imageFile, mimeType, roomID, new TAPDefaultSubscriber<>(view));
    }

    // File Download
    private HashMap<String, TAPBaseSubscriber<TAPDefaultDataView<ResponseBody>>> downloadSubscribers; // Key is message local ID

    public void downloadFile(String roomID, String localID, String fileID, @Nullable Number fileSize, TAPDefaultDataView<ResponseBody> view) {
        TAPApiManager.getInstance(instanceKey).downloadFile(roomID, localID, fileID, fileSize, getNewDownloadSubscriber(localID, view));
    }

    public void cancelFileDownload(String localID) {
        TAPBaseSubscriber<TAPDefaultDataView<ResponseBody>> downloadSubscriber = getDownloadSubscribers().get(localID);
        if (null != downloadSubscriber) {
            downloadSubscriber.unsubscribe();
            removeDownloadSubscriber(localID);
        }
    }

    public void removeDownloadSubscriber(String localID) {
        getDownloadSubscribers().remove(localID);
    }

    private HashMap<String, TAPBaseSubscriber<TAPDefaultDataView<ResponseBody>>> getDownloadSubscribers() {
        return null == downloadSubscribers ? downloadSubscribers = new HashMap<>() : downloadSubscribers;
    }

    private TAPBaseSubscriber<TAPDefaultDataView<ResponseBody>> getNewDownloadSubscriber(String localID, TAPDefaultDataView<ResponseBody> view) {
        getDownloadSubscribers().put(localID, new TAPBaseSubscriber<>(view));
        return getDownloadSubscribers().get(localID);
    }

    public void getProjectConfig(TAPDefaultDataView<TapConfigs> view) {
        TAPApiManager.getInstance(instanceKey).getProjectConfig(new TAPDefaultSubscriber<>(view));
    }

    public void updateBio(String bio, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).updateBio(bio, new TAPDefaultSubscriber<>(view));
    }

    public void getPhotoList(String userId, TAPDefaultDataView<TapGetPhotoListResponse> view) {
        TAPApiManager.getInstance(instanceKey).getPhotoList(userId, new TAPDefaultSubscriber<>(view));
    }

    public void setMainPhoto(int id, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).setMainPhoto(id, new TAPDefaultSubscriber<>(view));
    }

    public void removePhoto(int id, Long createdTime, TAPDefaultDataView<TAPGetUserResponse> view) {
        TAPApiManager.getInstance(instanceKey).removePhoto(id, createdTime, new TAPDefaultSubscriber<>(view));
    }

    public void starMessage(String roomId, List<String> messageIds, TAPDefaultDataView<TapStarMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).starMessage(roomId, messageIds, new TAPDefaultSubscriber<>(view));
    }

    public void unStarMessage(String roomId, List<String> messageIds, TAPDefaultDataView<TapUnstarMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).unStarMessage(roomId, messageIds, new TAPDefaultSubscriber<>(view));
    }

    public void getStarredMessages(String roomId, int pageNumber, int pageSize, TAPDefaultDataView<TAPGetMessageListByRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getStarredMessages(roomId, pageNumber, pageSize, new TAPDefaultSubscriber<>(view));
    }

    public void getStarredMessageIds(String roomId, TAPDefaultDataView<TapStarMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).getStarredMessageIds(roomId, new TAPDefaultSubscriber<>(view));
    }

    public void markRoomAsUnread(List<String> roomIds, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).markRoomAsUnread(roomIds, new TAPDefaultSubscriber<>(view));
    }

    public void getUnreadRoomIds(TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).getUnreadRoomIds(new TAPDefaultSubscriber<>(view));
    }

    public void requestDeleteAccountOtp(String channel, TAPDefaultDataView<TAPOTPResponse> view) {
        TAPApiManager.getInstance(instanceKey).requestDeleteAccountOtp(channel, new TAPDefaultSubscriber<>(view));
    }

    public void checkDeleteAccountState(TAPDefaultDataView<TapCheckDeleteAccountStateResponse> view) {
        TAPApiManager.getInstance(instanceKey).checkDeleteAccountState(new TAPDefaultSubscriber<>(view));
    }

    public void verifyOtpDeleteAccount(long otpID, String otpKey, String otpCode, String reason, TAPDefaultDataView<TAPCommonResponse> view) {
        TAPApiManager.getInstance(instanceKey).verifyOtpDeleteAccount(otpID, otpKey, otpCode, reason, new TAPDefaultSubscriber<>(view));
    }

    public void getPinnedMessages(String roomId, int pageNumber, int pageSize, TAPDefaultDataView<TAPGetMessageListByRoomResponse> view) {
        TAPApiManager.getInstance(instanceKey).getPinnedMessages(roomId, pageNumber, pageSize, new TAPDefaultSubscriber<>(view));
    }

    public void getPinnedMessageIds(String roomId, TAPDefaultDataView<TapPinMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).getPinnedMessageIds(roomId, new TAPDefaultSubscriber<>(view));
    }

    public void pinMessages(String roomId, List<String> messageIds, TAPDefaultDataView<TapPinMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).pinMessages(roomId, messageIds, new TAPDefaultSubscriber<>(view));
    }

    public void unPinMessages(String roomId, List<String> messageIds, TAPDefaultDataView<TapPinMessageResponse> view) {
        TAPApiManager.getInstance(instanceKey).unpinMessages(roomId, messageIds, new TAPDefaultSubscriber<>(view));
    }

    public void getSharedMedia(String roomId, Long minCreated, Long maxCreated, TAPDefaultDataView<TapGetSharedContentResponse> view) {
        TAPApiManager.getInstance(instanceKey).getSharedMedia(roomId, minCreated, maxCreated, new TAPDefaultSubscriber<>(view));
    }

    public void getMutedRoomIds(TAPDefaultDataView<TapGetMutedRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).getMutedRoomIds(new TAPDefaultSubscriber<>(view));
    }

    public void muteRoom(List<String> roomIds, Long expiredAt, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).muteRoom(roomIds, expiredAt, new TAPDefaultSubscriber<>(view));
    }

    public void unmuteRoom(List<String> roomIds, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).unmuteRoom(roomIds, new TAPDefaultSubscriber<>(view));
    }

    public void getPinnedRoomIds(TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).getPinnedRoomIds(new TAPDefaultSubscriber<>(view));
    }

    public void pinRoom(List<String> roomIds, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).pinRoom(roomIds, new TAPDefaultSubscriber<>(view));
    }

    public void unpinRoom(List<String> roomIds, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).unpinRoom(roomIds, new TAPDefaultSubscriber<>(view));
    }

    public void clearChat(List<String> roomIds, TAPDefaultDataView<TapGetUnreadRoomIdsResponse> view) {
        TAPApiManager.getInstance(instanceKey).clearChat(roomIds, new TAPDefaultSubscriber<>(view));
    }

    public void getRoomIdsWithState(TAPDefaultDataView<TapGetRoomIdsWithStateResponse> view) {
        TAPApiManager.getInstance(instanceKey).getRoomIdsWithState(new TAPDefaultSubscriber<>(view));
    }
}
