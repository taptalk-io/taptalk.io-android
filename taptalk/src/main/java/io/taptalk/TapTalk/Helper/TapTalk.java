package io.taptalk.TapTalk.Helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.stetho.Stetho;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreProjectConfigsListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Manager.TapCoreProjectConfigsManager;
import io.taptalk.TapTalk.Manager.TapCoreRoomListManager;
import io.taptalk.TapTalk.Manager.TapLocaleManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapConfigs;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACCESS_TOKEN_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INVALID_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INVALID_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_AUTHENTICATE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_REFRESH_ACTIVE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_REFRESH_CONFIG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_CHANNEL_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_GROUP_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.CHANNEL_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.GROUP_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.USERNAME_IGNORE_CASE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.REFRESH_TOKEN_RENEWED;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkScreenOrientation.TapTalkOrientationDefault;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class TapTalk implements LifecycleObserver {
    private static final String TAG = TapTalk.class.getSimpleName();
    private static HashMap<String, TapTalk> tapTalkInstances;
    private static ArrayList<String> instanceKeys;
    public static Context appContext;
    public static String mixpanelToken = "";
    public static boolean isForeground;
    public static boolean isLoggingEnabled = false;
    private static Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    private static boolean handleMessageIfAppsCrashing = true;

    private String instanceKey = "";
    private Map<String, String> coreConfigs;
    private Map<String, String> projectConfigs;
    private Map<String, String> customConfigs;
    private List<TapListener> tapListeners = new ArrayList<>();
    private TapTalkScreenOrientation screenOrientation = TapTalkOrientationDefault;
    private TAPChatListener chatListener;
    private String clientAppName = "";
    private int clientAppIcon = R.drawable.tap_ic_taptalk_logo;
    private boolean isRefreshTokenExpired, isAutoConnectDisabled, isAutoContactSyncDisabled;
    private boolean listenerInit = false;
    public TapTalkImplementationType implementationType;
    public String tapTalkUserAgent = "android";
    private static Class groupPendingIntentClass = TapUIRoomListActivity.class;

    public enum TapTalkEnvironment {
        TapTalkEnvironmentProduction,
        TapTalkEnvironmentStaging,
        TapTalkEnvironmentDevelopment;
    }

    public enum TapTalkImplementationType {
        TapTalkImplementationTypeCore,
        TapTalkImplementationTypeUI,
        TapTalkImplementationTypeCombine
    }

    public enum TapTalkScreenOrientation {
        TapTalkOrientationDefault,
        TapTalkOrientationPortrait,
        TapTalkOrientationLandscape // FIXME: 6 February 2019 Activity loads portrait by default then changes to landscape after onCreate
    }

    public static TapTalk getTapTalkInstance(String instanceKey) {
        return getTapTalkInstances().get(instanceKey);
    }

    public static HashMap<String, TapTalk> getTapTalkInstances() {
        return null == tapTalkInstances ? tapTalkInstances = new HashMap<>() : tapTalkInstances;
    }

    public static ArrayList<String> getInstanceKeys() {
        return null == instanceKeys ? instanceKeys = new ArrayList<>() : instanceKeys;
    }

    public TapTalk(
            String instanceKey,
            @NonNull final Context appContext,
            @NonNull String appID,
            @NonNull String appSecret,
            @NonNull String userAgent,
            int clientAppIcon,
            String clientAppName,
            String appBaseURL,
            TapTalkImplementationType type,
            @NonNull TapListener tapListener) {

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        if (null == TapTalk.appContext || instanceKey.equals("")) {
            TapTalk.appContext = appContext;
        }

        this.instanceKey = instanceKey;
        this.clientAppIcon = clientAppIcon;
        this.clientAppName = clientAppName;

        // Init Base URL
        TAPApiManager.setBaseUrlApi(instanceKey, generateApiBaseURL(appBaseURL));
        TAPApiManager.setBaseUrlSocket(instanceKey, generateSocketBaseURL(appBaseURL));
        TAPConnectionManager.getInstance(instanceKey).setWebSocketEndpoint(generateWSSBaseURL(appBaseURL));

        // Init Hawk for preference
        if (!Hawk.isBuilt()) {
            if (BuildConfig.BUILD_TYPE.equals("dev")) {
                // No encryption for dev build
                Hawk.init(appContext).setEncryption(new NoEncryption()).build();
            } else {
                Hawk.init(appContext).build();
            }
        }

        this.implementationType = type;

        TAPCacheManager.getInstance(appContext).initAllCache();

        // Init database
        TAPDataManager.getInstance(instanceKey).initDatabaseManager(MESSAGE_DB, (Application) appContext);
        TAPDataManager.getInstance(instanceKey).initDatabaseManager(SEARCH_DB, (Application) appContext);
        TAPDataManager.getInstance(instanceKey).initDatabaseManager(MY_CONTACT_DB, (Application) appContext);
        // Update here when adding database table

        // Save header requirement
        TAPDataManager.getInstance(instanceKey).saveApplicationID(appID);
        TAPDataManager.getInstance(instanceKey).saveApplicationSecret(appSecret);
        TAPDataManager.getInstance(instanceKey).saveUserAgent(userAgent);

        // Init configs
        presetConfigs();
        // Refresh remote configs moved to init method
//        refreshRemoteConfigs(this, instanceKey, new TapCommonListener() {});

        if (TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            //TAPConnectionManager.getInstance(instanceKey).connect();
            TAPContactManager.getInstance(instanceKey).setMyCountryCode(TAPDataManager.getInstance(instanceKey).getMyCountryCode());

            TAPFileDownloadManager.getInstance(instanceKey).getFileProviderPathFromPreference();
            TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUriFromPreference();
            TAPOldDataManager.getInstance(instanceKey).startAutoCleanProcess();
            AnalyticsManager.getInstance(instanceKey).identifyUser();
        }

        TAPDataManager.getInstance(instanceKey).updateSendingMessageToFailed();
        TAPContactManager.getInstance(instanceKey).setContactSyncPermissionAsked(TAPDataManager.getInstance(instanceKey).isContactSyncPermissionAsked());
        TAPContactManager.getInstance(instanceKey).setContactSyncAllowedByUser(TAPDataManager.getInstance(instanceKey).isContactSyncAllowedByUser());

        // Init Stetho for debug build
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(appContext)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(appContext))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(appContext))
                            .build()
            );
        }

        if (!tapListeners.contains(tapListener)) {
            tapListeners.add(tapListener);
        }

        TAPContactManager.getInstance(instanceKey).loadAllUserDataFromDatabase();

        if (null != TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable() &&
                TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            initListener();
        }

        if (!listenerInit) {
            handleAppToForeground();
        }
    }

    public static void initializeAnalyticsForSampleApps(String analyticsKey) {
        mixpanelToken = analyticsKey;
    }

    private void initListener() {
        chatListener = new TAPChatListener() {
            @Override
            public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }

            @Override
            public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }

            @Override
            public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }

            @Override
            public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }

            @Override
            public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }

            @Override
            public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
                updateApplicationBadgeCount(instanceKey);
            }
        };
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
    }

    public void putGlobalChatListener() {
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
    }

    public void removeGlobalChatListener() {
        TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
    }

    /**
     * =============================================================================================
     * INITIALIZATION
     * =============================================================================================
     */

    public static TapTalk init(Context context, String appKeyID, String appKeySecret, int clientAppIcon, String clientAppName, String appBaseURL, TapTalkImplementationType type, TapListener listener) {
        return initNewInstance("", context, appKeyID, appKeySecret, "android", clientAppIcon, clientAppName, appBaseURL, type, listener);
    }

    public static TapTalk init(Context context, String appKeyID, String appKeySecret, String userAgent, int clientAppIcon, String clientAppName, String appBaseURL, TapTalkImplementationType type, TapListener listener) {
        return initNewInstance("", context, appKeyID, appKeySecret, userAgent, clientAppIcon, clientAppName, appBaseURL, type, listener);
    }

    public static TapTalk initNewInstance(String instanceKey, Context context, String appKeyID, String appKeySecret, int clientAppIcon, String clientAppName, String appBaseURL, TapTalkImplementationType type, TapListener listener) {
        return initNewInstance(instanceKey, context, appKeyID, appKeySecret, "android", clientAppIcon, clientAppName, appBaseURL, type, listener);
    }

    public static TapTalk initNewInstance(String instanceKey, Context context, String appKeyID, String appKeySecret, String userAgent, int clientAppIcon, String clientAppName, String appBaseURL, TapTalkImplementationType type, TapListener listener) {
        if (!getTapTalkInstances().containsKey(instanceKey)) {
            TapTalk instance = new TapTalk(instanceKey, context, appKeyID, appKeySecret, userAgent, clientAppIcon, clientAppName, appBaseURL, type, listener);
            getTapTalkInstances().put(instanceKey, instance);
            getInstanceKeys().add(instanceKey);
            refreshRemoteConfigs(instanceKey, new TapCommonListener() {
            });
        }
        return getTapTalkInstances().get(instanceKey);
    }

    public static void initializeGooglePlacesApiKey(String apiKey) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        Places.initialize(appContext, apiKey);
    }

    public static boolean checkTapTalkInitialized() {
        if (getTapTalkInstances().isEmpty()) {
            return false;
        }
        return true;
    }

    public static void setLoggingEnabled(boolean enabled) {
        isLoggingEnabled = enabled;
    }

    public static void setGroupNotificationPendingIntentClass(Class groupNotificationPendingIntentClass) {
        groupPendingIntentClass = groupNotificationPendingIntentClass;
    }

    public static Class getGroupNotificationPendingIntentClass() {
        return groupPendingIntentClass;
    }

    public static void setTapTalkCustomUserAgent(String userAgent) {
        setTapTalkCustomUserAgent("", userAgent);
    }

    public static void setTapTalkCustomUserAgent(String instanceKey, String userAgent) {
        getTapTalkInstance(instanceKey).tapTalkUserAgent = userAgent;
    }

    private String generateSocketBaseURL(String baseURL) {
        return baseURL + "/";
    }

    private String generateWSSBaseURL(String baseURL) {
        return (baseURL + "/connect").replace("https", "wss");
    }

    private String generateApiBaseURL(String baseURL) {
        return baseURL + "/v1/";
    }

    /**
     * =============================================================================================
     * AUTHENTICATION
     * =============================================================================================
     */

    public static void authenticateWithAuthTicket(String authTicket, boolean connectOnSuccess, TapCommonListener listener) {
        authenticateWithAuthTicket("", authTicket, connectOnSuccess, listener);
    }

    public static void authenticateWithAuthTicket(String instanceKey, String authTicket, boolean connectOnSuccess, TapCommonListener listener) {
        if (!checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        if (null == authTicket || "".equals(authTicket)) {
            listener.onError(ERROR_CODE_INVALID_AUTH_TICKET, ERROR_MESSAGE_INVALID_AUTH_TICKET);
        } else {
            TAPDataManager.getInstance(instanceKey).saveAuthTicket(authTicket);
            TAPDataManager.getInstance(instanceKey).getAccessTokenFromApi(new TAPDefaultDataView<TAPGetAccessTokenResponse>() {
                @Override
                public void onSuccess(TAPGetAccessTokenResponse response) {
                    TAPDataManager.getInstance(instanceKey).removeAuthTicket();
                    TAPDataManager.getInstance(instanceKey).saveAccessToken(response.getAccessToken());
                    TAPDataManager.getInstance(instanceKey).saveRefreshToken(response.getRefreshToken());
                    TAPDataManager.getInstance(instanceKey).saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
                    TAPDataManager.getInstance(instanceKey).saveAccessTokenExpiry(response.getAccessTokenExpiry());

                    new Thread(() -> {
                        if (!TAPDataManager.getInstance(instanceKey).checkFirebaseToken()) {
                            try {
                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                                    if (null != task.getResult()) {
                                        String fcmToken = task.getResult().getToken();
                                        TAPDataManager.getInstance(instanceKey).registerFcmTokenToServer(fcmToken, new TAPDefaultDataView<TAPCommonResponse>() {
                                        });
                                        TAPDataManager.getInstance(instanceKey).saveFirebaseToken(fcmToken);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            TAPDataManager.getInstance(instanceKey).registerFcmTokenToServer(TAPDataManager.getInstance(instanceKey).getFirebaseToken(), new TAPDefaultDataView<TAPCommonResponse>() {
                            });
                        }
                    }).start();

                    new Thread(() -> TAPDataManager.getInstance(instanceKey).getMyContactListFromAPI(new TAPDefaultDataView<TAPContactResponse>() {
                        @Override
                        public void onSuccess(TAPContactResponse response) {
                            List<TAPUserModel> userModels = new ArrayList<>();
                            for (TAPContactModel contact : response.getContacts()) {
                                userModels.add(contact.getUser().setUserAsContact());
                            }
                            TAPContactManager.getInstance(instanceKey).updateUserData(userModels);
                        }
                    })).start();

                    TAPDataManager.getInstance(instanceKey).saveActiveUser(response.getUser());
                    TAPApiManager.getInstance(instanceKey).setLoggedOut(false);
                    if (connectOnSuccess) {
                        TAPConnectionManager.getInstance(instanceKey).connect();
                    }
                    listener.onSuccess(SUCCESS_MESSAGE_AUTHENTICATE);

                    if (getTapTalkInstance(instanceKey).isRefreshTokenExpired) {
                        getTapTalkInstance(instanceKey).isRefreshTokenExpired = false;
                        Intent intent = new Intent(REFRESH_TOKEN_RENEWED);
                        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                    }
                }

                @Override
                public void onError(TAPErrorModel error) {
                    listener.onError(error.getCode(), error.getMessage());
                }

                @Override
                public void onError(String errorMessage) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            });
        }
    }

    public static boolean isAuthenticated() {
        return isAuthenticated("");
    }

    public static boolean isAuthenticated(String instanceKey) {
        return TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable();
    }

    public static void logoutAndClearAllTapTalkData() {
        logoutAndClearAllTapTalkData("");
    }

    public static void logoutAndClearAllTapTalkData(String instanceKey) {
        if (!checkTapTalkInitialized() || !isAuthenticated(instanceKey)) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).logout(new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                clearAllTapTalkData(instanceKey);
                for (TapListener listener : getTapTalkListeners(instanceKey)) {
                    listener.onUserLogout();
                }
                Intent intent = new Intent(CLEAR_ROOM_LIST);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }

            @Override
            public void onError(TAPErrorModel error) {
                clearAllTapTalkData(instanceKey);
                for (TapListener listener : getTapTalkListeners(instanceKey)) {
                    listener.onUserLogout();
                }
                Intent intent = new Intent(CLEAR_ROOM_LIST);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }

            @Override
            public void onError(String errorMessage) {
                clearAllTapTalkData(instanceKey);
                for (TapListener listener : getTapTalkListeners(instanceKey)) {
                    listener.onUserLogout();
                }
                Intent intent = new Intent(CLEAR_ROOM_LIST);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        });
    }

    public static void clearAllTapTalkData() {
        clearAllTapTalkData("");
    }

    public static void clearAllTapTalkData(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).deleteAllPreference();
        TAPDataManager.getInstance(instanceKey).deleteAllFromDatabase();
        TAPDataManager.getInstance(instanceKey).deleteAllManagerData();
        TAPApiManager.getInstance(instanceKey).setLoggedOut(true);
        TAPRoomListViewModel.setShouldNotLoadFromAPI(instanceKey, false);
        TAPChatManager.getInstance(instanceKey).disconnectAfterRefreshTokenExpired();
        getTapTalkInstance(instanceKey).isRefreshTokenExpired = true;
    }

    /**
     * =============================================================================================
     * CONNECTION
     * =============================================================================================
     */

    public static void connect(TapCommonListener listener) {
        connect("", listener);
    }

    public static void connect(String instanceKey, TapCommonListener listener) {
        if (!checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        if (isAuthenticated(instanceKey)) {
            TAPConnectionManager.getInstance(instanceKey).connect(listener);
        } else {
            listener.onError(ERROR_CODE_ACCESS_TOKEN_UNAVAILABLE, ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE);
        }
    }

    public static void disconnect() {
        disconnect("");
    }

    public static void disconnect(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        TAPConnectionManager.getInstance(instanceKey).close(NOT_CONNECTED);
    }

    public static boolean isConnected() {
        return isConnected("");
    }

    public static boolean isConnected(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return false;
        }
        return TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == CONNECTED;
    }

    public static void setAutoConnectEnabled(boolean enabled) {
        setAutoConnectEnabled("", enabled);
    }

    public static void setAutoConnectEnabled(String instanceKey, boolean enabled) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        getTapTalkInstance(instanceKey).isAutoConnectDisabled = !enabled;
    }

    public static boolean isAutoConnectEnabled() {
        return isAutoConnectEnabled("");
    }

    public static boolean isAutoConnectEnabled(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return false;
        }
        return !getTapTalkInstance(instanceKey).isAutoConnectDisabled;
    }

    /**
     * =============================================================================================
     * GENERAL
     * =============================================================================================
     */

    public static int getClientAppIcon() {
        return getClientAppIcon("");
    }

    public static int getClientAppIcon(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return -1;
        }
        return getTapTalkInstance(instanceKey).clientAppIcon;
    }

    public static String getClientAppName() {
        return getClientAppName("");
    }

    public static String getClientAppName(String instanceKey) {
        return getTapTalkInstance(instanceKey).clientAppName;
    }

    public static TapTalkImplementationType getTapTalkImplementationType() {
        return getTapTalkImplementationType("");
    }

    public static TapTalkImplementationType getTapTalkImplementationType(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return getTapTalkInstance(instanceKey).implementationType;
    }

    public static void updateApplicationBadgeCount() {
        updateApplicationBadgeCount("");
    }

    public static void updateApplicationBadgeCount(String instanceKey) {
        if (!isAuthenticated(instanceKey)) {
            return;
        }
        TAPNotificationManager.getInstance(instanceKey).updateUnreadCount();
    }

    // TODO: 22 August 2019 CORE MODEL
    public static Map<String, String> getCoreConfigs() {
        return getCoreConfigs("");
    }

    public static Map<String, String> getCoreConfigs(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return new HashMap<>(getTapTalkInstance(instanceKey).coreConfigs);
    }

    public static Map<String, String> getProjectConfigs() {
        return getProjectConfigs("");
    }

    public static Map<String, String> getProjectConfigs(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return new HashMap<>(getTapTalkInstance(instanceKey).projectConfigs);
    }

    public static Map<String, String> getCustomConfigs() {
        return getCustomConfigs("");
    }

    public static Map<String, String> getCustomConfigs(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return new HashMap<>(getTapTalkInstance(instanceKey).customConfigs);
    }

    public static void refreshRemoteConfigs(TapCommonListener listener) {
        refreshRemoteConfigs("", listener);
    }

    public static void refreshRemoteConfigs(String instanceKey, TapCommonListener listener) {
        TapCoreProjectConfigsManager.getInstance(instanceKey).getProjectConfigs(new TapCoreProjectConfigsListener() {
            @Override
            public void onSuccess(TapConfigs config) {
                getTapTalkInstance(instanceKey).coreConfigs = config.getCoreConfigs();
                getTapTalkInstance(instanceKey).projectConfigs = config.getProjectConfigs();
                getTapTalkInstance(instanceKey).customConfigs = config.getCustomConfigs();
                TAPDataManager.getInstance(instanceKey).saveCoreConfigs(getTapTalkInstance(instanceKey).coreConfigs);
                TAPDataManager.getInstance(instanceKey).saveProjectConfigs(getTapTalkInstance(instanceKey).projectConfigs);
                TAPDataManager.getInstance(instanceKey).saveCustomConfigs(getTapTalkInstance(instanceKey).customConfigs);
                if (null != listener) {
                    listener.onSuccess(SUCCESS_MESSAGE_REFRESH_CONFIG);
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    private void presetConfigs() {
        coreConfigs = TAPDataManager.getInstance(instanceKey).getCoreConfigs();
        projectConfigs = TAPDataManager.getInstance(instanceKey).getProjectConfigs();
        customConfigs = TAPDataManager.getInstance(instanceKey).getCustomConfigs();

        // Set default values if configs are empty
        if (coreConfigs.isEmpty()) {
            coreConfigs.put(CHAT_MEDIA_MAX_FILE_SIZE, DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE);
            coreConfigs.put(ROOM_PHOTO_MAX_FILE_SIZE, DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE);
            coreConfigs.put(USER_PHOTO_MAX_FILE_SIZE, DEFAULT_USER_PHOTO_MAX_FILE_SIZE);
            coreConfigs.put(GROUP_MAX_PARTICIPANTS, DEFAULT_GROUP_MAX_PARTICIPANTS);
            coreConfigs.put(CHANNEL_MAX_PARTICIPANTS, DEFAULT_CHANNEL_MAX_PARTICIPANTS);
        }

        if (projectConfigs.isEmpty()) {
            projectConfigs.put(USERNAME_IGNORE_CASE, "1");
        }
    }

    public static void setAutoContactSyncEnabled(boolean enabled) {
        setAutoContactSyncEnabled("", enabled);
    }

    public static void setAutoContactSyncEnabled(String instanceKey, boolean enabled) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        getTapTalkInstance(instanceKey).isAutoContactSyncDisabled = !enabled;
    }

    public static boolean isAutoContactSyncEnabled() {
        return isAutoContactSyncEnabled("");
    }

    public static boolean isAutoContactSyncEnabled(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return false;
        }
        return !getTapTalkInstance(instanceKey).isAutoContactSyncDisabled;
    }

    /**
     * =============================================================================================
     * USER
     * =============================================================================================
     */

    public static TAPUserModel getTapTalkActiveUser() {
        return getTapTalkActiveUser("");
    }

    public static TAPUserModel getTapTalkActiveUser(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return TAPChatManager.getInstance(instanceKey).getActiveUser();
    }

    public static void refreshActiveUser(TapCommonListener listener) {
        refreshActiveUser("", listener);
    }

    public static void refreshActiveUser(String instanceKey, TapCommonListener listener) {
        if (!checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        new Thread(() -> {
            if (null != TAPChatManager.getInstance(instanceKey).getActiveUser()) {
                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(
                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                        new TAPDefaultDataView<TAPGetUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetUserResponse response) {
                                TAPDataManager.getInstance(instanceKey).saveActiveUser(response.getUser());
                                listener.onSuccess(SUCCESS_MESSAGE_REFRESH_ACTIVE_USER);
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.onError(error.getCode(), error.getMessage());
                            }

                            @Override
                            public void onError(String errorMessage) {
                                listener.onError(ERROR_CODE_OTHERS, errorMessage);
                            }
                        });
            } else {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
        }).start();
    }

    /**
     * =============================================================================================
     * NOTIFICATION
     * =============================================================================================
     */

    public static boolean isTapTalkNotification(RemoteMessage remoteMessage) {
        return null != remoteMessage &&
                null != remoteMessage.getData() &&
                null != remoteMessage.getData().get("identifier") &&
                remoteMessage.getData().get("identifier").equals("io.taptalk.TapTalk");
    }

    public static void handleTapTalkPushNotification(RemoteMessage remoteMessage) {
        handleTapTalkPushNotification("", remoteMessage);
    }

//    public static void handleTapTalkPushNotification(String instanceKey, RemoteMessage remoteMessage) {
//        TAPNotificationManager.getInstance(instanceKey).updateNotificationMessageMapWhenAppKilled();
//        HashMap<String, Object> notificationMap = TAPUtils.fromJSON(new TypeReference<HashMap<String, Object>>() {
//        }, remoteMessage.getData().get("body"));
//        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(notificationMap);
//
//        try {
//            // Show background notification
//            TAPNotificationManager.getInstance(instanceKey).createAndShowBackgroundNotification(
//                    appContext,
//                    getClientAppIcon(instanceKey),
//                    TapUIChatActivity.class,
//                    message);
//        } catch (Exception e) {
//            Log.e(TAG, "onMessageReceived: ", e);
//            e.printStackTrace();
//        }
//    }

    // FIXME: 31 Mar 2020 TEMPORARY FIX TO DETECT BACKGROUND NOTIFICATION INSTANCE
    public static void handleTapTalkPushNotification(String instanceKey, RemoteMessage remoteMessage) {
        TAPNotificationManager.getInstance(instanceKey).updateNotificationMessageMapWhenAppKilled();
        HashMap<String, Object> notificationMap = TAPUtils.fromJSON(new TypeReference<HashMap<String, Object>>() {
        }, remoteMessage.getData().get("body"));
        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(notificationMap);
        if (getInstanceKeys().size() > 1) {
            identifyMessageAndShowNotification(instanceKey, message);
        } else {
            showBackgroundNotification(instanceKey, message);
        }
    }

    private static void identifyMessageAndShowNotification(String instanceKey, TAPMessageModel message) {
        TAPUserModel sender = message.getUser();
        String senderId = sender.getUserID();
        if (null == senderId || senderId.isEmpty()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(senderId, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                TAPUserModel userResponse = response.getUser();
                if (null == userResponse) {
                    return;
                }
                if (sender.getXcUserID().equals(userResponse.getXcUserID()) &&
                        sender.getName().equals(userResponse.getName()) &&
                        (null == sender.getUsername() ||
                                sender.getUsername().equals(userResponse.getUsername()))
                ) {
                    showBackgroundNotification(instanceKey, message);
                }
            }
        });
    }

    private static void showBackgroundNotification(String instanceKey, TAPMessageModel message) {
        try {
            // Show background notification
            TAPNotificationManager.getInstance(instanceKey).createAndShowBackgroundNotification(
                    appContext,
                    getClientAppIcon(instanceKey),
                    TapUIChatActivity.class,
                    message);
        } catch (Exception e) {
//            Log.e(TAG, "onMessageReceived: ", e);
            e.printStackTrace();
        }
    }

    public static void showTapTalkNotification(TAPMessageModel tapMessageModel) {
        showTapTalkNotification("", tapMessageModel);
    }

    public static void showTapTalkNotification(String instanceKey, TAPMessageModel tapMessageModel) {
        new TAPNotificationManager.NotificationBuilder(appContext, instanceKey)
                .setNotificationMessage(tapMessageModel)
                .setSmallIcon(getClientAppIcon(instanceKey))
                .setNeedReply(false)
                .setOnClickAction(TapUIChatActivity.class)
                .show();
    }

    public static void saveFirebaseToken(String token) {
        saveFirebaseToken("", token);
    }

    public static void saveFirebaseToken(String instanceKey, String token) {
        if (!TAPDataManager.getInstance(instanceKey).checkFirebaseToken(token)) {
            TAPDataManager.getInstance(instanceKey).saveFirebaseToken(token);
        }
    }

    /**
     * =============================================================================================
     * LANGUAGE
     * =============================================================================================
     */

    public enum Language {ENGLISH, INDONESIAN}

    public static void setDefaultLanguage(Language language) {
        String defaultLanguage;
        switch (language) {
            case INDONESIAN:
                defaultLanguage = "in";
                break;
            default:
                defaultLanguage = "en";
                break;
        }
        TapLocaleManager.setLocale((Application) appContext, defaultLanguage);

        // TODO: 27 Feb 2020 RESTART OPEN ACTIVITIES TO APPLY CHANGED RESOURCES
    }

    /**
     * =============================================================================================
     * TEMP
     * =============================================================================================
     */

    public static List<TapListener> getTapTalkListeners() {
        return getTapTalkListeners("");
    }

    public static List<TapListener> getTapTalkListeners(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return getTapTalkInstance(instanceKey).tapListeners;
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(TapTalk.appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void addTapTalkListener(TapListener listener) {
        addTapTalkListener("", listener);
    }

    public static void addTapTalkListener(String instanceKey, TapListener listener) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        if (!getTapTalkInstance(instanceKey).tapListeners.contains(listener)) {
            getTapTalkInstance(instanceKey).tapListeners.add(listener);
        }
    }

    public static void removeTapTalkListener(TapListener listener) {
        removeTapTalkListener("", listener);
    }

    public static void removeTapTalkListener(String instanceKey, TapListener listener) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        getTapTalkInstance(instanceKey).tapListeners.remove(listener);
    }

    private static void setTapTalkScreenOrientation(TapTalkScreenOrientation orientation) {
        setTapTalkScreenOrientation("", orientation);
    }

    private static void setTapTalkScreenOrientation(String instanceKey, TapTalkScreenOrientation orientation) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        getTapTalkInstance(instanceKey).screenOrientation = orientation;
    }

    private static TapTalkScreenOrientation getTapTalkScreenOrientation() {
        return getTapTalkScreenOrientation("");
    }

    private static TapTalkScreenOrientation getTapTalkScreenOrientation(String instanceKey) {
        if (!checkTapTalkInitialized()) {
            return null;
        }
        return getTapTalkInstance(instanceKey).screenOrientation;
    }

    // Enable/disable in-app notification after chat fragment goes inactive or to background
    private static void setInAppNotificationEnabled(boolean enabled) {
        setInAppNotificationEnabled("", enabled);
    }

    private static void setInAppNotificationEnabled(String instanceKey, boolean enabled) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        TAPNotificationManager.getInstance(instanceKey).setRoomListAppear(!enabled);
    }

    private void createAndShowBackgroundNotification(Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        createAndShowBackgroundNotification("", context, notificationIcon, destinationClass, newMessageModel);
    }

    private void createAndShowBackgroundNotification(String instanceKey, Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        if (!checkTapTalkInitialized()) {
            return;
        }
        TAPNotificationManager.getInstance(instanceKey).createAndShowBackgroundNotification(context, notificationIcon, destinationClass, newMessageModel);
    }

    public static void fetchNewMessageAndUpdatedBadgeCount() {
        fetchNewMessageAndUpdatedBadgeCount("");
    }

    public static void fetchNewMessageAndUpdatedBadgeCount(String instanceKey) {
        if (checkTapTalkInitialized() && isAuthenticated(instanceKey)) {
            TapCoreRoomListManager.getInstance(instanceKey).fetchNewMessageToDatabase(new TapCommonListener() {
                @Override
                public void onSuccess(String s) {
                    updateApplicationBadgeCount(instanceKey);
                }

                @Override
                public void onError(String s, String s1) {
                    updateApplicationBadgeCount(instanceKey);
                }
            });
        }
    }

    public static void handleAppToForeground() {
        isForeground = true;
        for (Map.Entry<String, TapTalk> entry : getTapTalkInstances().entrySet()) {
            TAPContactManager.getInstance(entry.getValue().instanceKey).loadAllUserDataFromDatabase();
            TAPGroupManager.Companion.getInstance(entry.getValue().instanceKey).loadAllRoomDataFromPreference();
            TAPChatManager.getInstance(entry.getValue().instanceKey).setFinishChatFlow(false);
            TAPNetworkStateManager.getInstance(entry.getValue().instanceKey).registerCallback(TapTalk.appContext);
            TAPChatManager.getInstance(entry.getValue().instanceKey).triggerSaveNewMessage();
            TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).getFileProviderPathFromPreference();
            TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).getFileMessageUriFromPreference();

            if (handleMessageIfAppsCrashing) {
                Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                    TAPChatManager.getInstance(entry.getValue().instanceKey).saveIncomingMessageAndDisconnect();
                    TAPContactManager.getInstance(entry.getValue().instanceKey).saveUserDataMapToDatabase();
                    TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).saveFileProviderPathToPreference();
                    TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).saveFileMessageUriToPreference();
                    System.exit(0);
                });
            } else {
                Thread.setDefaultUncaughtExceptionHandler(defaultUEH);
            }
        }
    }

    public static void handleAppToBackground() {
        isForeground = false;

        for (Map.Entry<String, TapTalk> entry : getTapTalkInstances().entrySet()) {
            TAPRoomListViewModel.setShouldNotLoadFromAPI(entry.getValue().instanceKey, false);
            TAPDataManager.getInstance(entry.getValue().instanceKey).setNeedToQueryUpdateRoomList(true);
            TAPNetworkStateManager.getInstance(entry.getValue().instanceKey).unregisterCallback(TapTalk.appContext);
            TAPChatManager.getInstance(entry.getValue().instanceKey).updateMessageWhenEnterBackground();
            TAPMessageStatusManager.getInstance(entry.getValue().instanceKey).updateMessageStatusWhenAppToBackground();
            TAPChatManager.getInstance(entry.getValue().instanceKey).setNeedToCalledUpdateRoomStatusAPI(true);
            TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).saveFileProviderPathToPreference();
            TAPFileDownloadManager.getInstance(entry.getValue().instanceKey).saveFileMessageUriToPreference();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        if (!getTapTalkInstances().isEmpty()) {
            handleAppToBackground();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        if (!getTapTalkInstances().isEmpty()) {
            handleAppToForeground();
        }
    }

    public static void allowTapTalkHandleMessageIfAppsCrashing(boolean handle) {
        handleMessageIfAppsCrashing = handle;
    }
}

