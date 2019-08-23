package io.taptalk.TapTalk.Helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.stetho.Stetho;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapTalkListener;
import io.taptalk.TapTalk.Listener.TapProjectConfigsListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Manager.TapCoreProjectConfigsManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapConfigs;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.BuildConfig;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_API_DEVELOPMENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_API_PRODUCTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_API_STAGING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_SOCKET_DEVELOPMENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_SOCKET_PRODUCTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_URL_SOCKET_STAGING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_WSS_DEVELOPMENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_WSS_PRODUCTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BaseUrl.BASE_WSS_STAGING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACCESS_TOKEN_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INVALID_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
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
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentDevelopment;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentProduction;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentStaging;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class TapTalk {
    private static final String TAG = TapTalk.class.getSimpleName();
    public static TapTalk tapTalk;
    public static Context appContext;
    public static boolean isForeground, isAutoConnectDisabled;
    private static TapTalkScreenOrientation screenOrientation = TapTalkScreenOrientation.TapTalkOrientationDefault;
    //    public static boolean isOpenDefaultProfileEnabled = true;
    private static String clientAppName = "";
    private static int clientAppIcon = R.drawable.tap_ic_taptalk_logo;
    private static boolean isRefreshTokenExpired;
    private Intent intent;

    private Thread.UncaughtExceptionHandler defaultUEH;
    private List<TapTalkListener> tapTalkListeners = new ArrayList<>();

    private static Map<String, String> coreConfigs;
    private static Map<String, String> projectConfigs;
    private static Map<String, String> customConfigs;
    public static TapTalkImplementationType implementationType;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            TAPChatManager.getInstance().saveIncomingMessageAndDisconnect();
            TAPContactManager.getInstance().saveUserDataMapToDatabase();
            TAPFileDownloadManager.getInstance().saveFileProviderPathToPreference();
            TAPFileDownloadManager.getInstance().saveFileMessageUriToPreference();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public enum TapTalkEnvironment {
        TapTalkEnvironmentProduction,
        TapTalkEnvironmentStaging,
        TapTalkEnvironmentDevelopment
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

    public TapTalk(@NonNull final Context appContext, @NonNull String appID, @NonNull String appSecret
            , @NonNull String userAgent,
                   int clientAppIcon, String clientAppName,
                   TapTalkImplementationType type,
                   @NonNull TapTalkListener tapTalkListener) {

        TapTalk.appContext = appContext;
        clientAppName = appContext.getResources().getString(R.string.tap_app_name);

        // Init Hawk for preference
        if (BuildConfig.BUILD_TYPE.equals("dev")) {
            // No encryption for dev build
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        } else {
            Hawk.init(appContext).build();
        }

        implementationType = type;

        TAPCacheManager.getInstance(appContext).initAllCache();

        // Init database
        TAPDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(MY_CONTACT_DB, (Application) appContext);
        // Update here when adding database table

        // Save header requirement
        TAPDataManager.getInstance().saveApplicationID(appID);
        TAPDataManager.getInstance().saveApplicationSecret(appSecret);
        TAPDataManager.getInstance().saveUserAgent(userAgent);

        // Init configs
        presetConfigs();
        refreshRemoteConfigs(new io.taptalk.TapTalk.Listener.TapCommonListener() {});

        if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
            //TAPConnectionManager.getInstance().connect();
            TAPContactManager.getInstance().setMyCountryCode(TAPDataManager.getInstance().getMyCountryCode());

            TAPFileDownloadManager.getInstance().getFileProviderPathFromPreference();
            TAPFileDownloadManager.getInstance().getFileMessageUriFromPreference();
            TAPOldDataManager.getInstance().startAutoCleanProcess();
        }

        TAPDataManager.getInstance().updateSendingMessageToFailed();
        TAPContactManager.getInstance().setContactSyncPermissionAsked(TAPDataManager.getInstance().isContactSyncPermissionAsked());

        //init stetho tapi hanya untuk DEBUG State
        if (BuildConfig.DEBUG)
            Stetho.initialize(
                    Stetho.newInitializerBuilder(appContext)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(appContext))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(appContext))
                            .build()
            );

        tapTalkListeners.add(tapTalkListener);
        TAPContactManager.getInstance().loadAllUserDataFromDatabase();

        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                isForeground = true;
                TAPContactManager.getInstance().loadAllUserDataFromDatabase();
                TAPGroupManager.Companion.getGetInstance().loadAllRoomDataFromPreference();
                TAPChatManager.getInstance().setFinishChatFlow(false);
                TAPNetworkStateManager.getInstance().registerCallback(TapTalk.appContext);
                TAPChatManager.getInstance().triggerSaveNewMessage();
                TAPFileDownloadManager.getInstance().getFileProviderPathFromPreference();
                TAPFileDownloadManager.getInstance().getFileMessageUriFromPreference();
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

                // Start service on first load
                if (null == intent) {
                    intent = new Intent(TapTalk.appContext, TapTalkEndAppService.class);
                    appContext.startService(intent);
                }
            }

            @Override
            public void onAppGotoBackground() {
                isForeground = false;
                TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
                TAPDataManager.getInstance().setNeedToQueryUpdateRoomList(true);
                TAPNetworkStateManager.getInstance().unregisterCallback(TapTalk.appContext);
                TAPChatManager.getInstance().updateMessageWhenEnterBackground();
                TAPMessageStatusManager.getInstance().updateMessageStatusWhenAppToBackground();
                TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(true);
                TAPFileDownloadManager.getInstance().saveFileProviderPathToPreference();
                TAPFileDownloadManager.getInstance().saveFileMessageUriToPreference();
            }
        });
    }

    public static TapTalk init(Context context, String appKeyID, String appKeySecret, String userAgent, int clientAppIcon, String clientAppName, TapTalkImplementationType type, TapTalkListener listener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, appKeyID, appKeySecret, userAgent, clientAppIcon, clientAppName, type, listener)) : tapTalk;
    }

    public static TapTalk init(Context context, String appKeyID, String appKeySecret, int clientAppIcon, String clientAppName, TapTalkImplementationType type, TapTalkListener listener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, appKeyID, appKeySecret, "android", clientAppIcon, clientAppName, type, listener)) : tapTalk;
    }

    public static void authenticate(String authTicket, boolean connectOnSuccess, TapCommonListener listener) {
        if (null == authTicket || "".equals(authTicket)) {
            listener.onError(ERROR_CODE_INVALID_AUTH_TICKET, ERROR_MESSAGE_INVALID_AUTH_TICKET);
        } else {
            TAPDataManager.getInstance().saveAuthTicket(authTicket);
            TAPDataManager.getInstance().getAccessTokenFromApi(new TAPDefaultDataView<TAPGetAccessTokenResponse>() {
                @Override
                public void onSuccess(TAPGetAccessTokenResponse response) {
                    TAPDataManager.getInstance().removeAuthTicket();
                    TAPDataManager.getInstance().saveAccessToken(response.getAccessToken());
                    TAPDataManager.getInstance().saveRefreshToken(response.getRefreshToken());
                    TAPDataManager.getInstance().saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
                    TAPDataManager.getInstance().saveAccessTokenExpiry(response.getAccessTokenExpiry());
                    registerFcmToken();

                    new Thread(() -> TAPDataManager.getInstance().getMyContactListFromAPI(new TAPDefaultDataView<TAPContactResponse>() {
                        @Override
                        public void onSuccess(TAPContactResponse response) {
                            List<TAPUserModel> userModels = new ArrayList<>();
                            for (TAPContactModel contact : response.getContacts()) {
                                userModels.add(contact.getUser().setUserAsContact());
                            }
                            TAPDataManager.getInstance().insertMyContactToDatabase(userModels);
                            TAPContactManager.getInstance().updateUserData(userModels);
                        }
                    })).start();

                    TAPDataManager.getInstance().saveActiveUser(response.getUser());
                    TAPApiManager.getInstance().setLogout(false);
                    if (connectOnSuccess) {
                        TAPConnectionManager.getInstance().connect();
                    }
                    listener.onSuccess(SUCCESS_MESSAGE_AUTHENTICATE);

                    if (isRefreshTokenExpired) {
                        isRefreshTokenExpired = false;
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

    private static void registerFcmToken() {
        new Thread(() -> {
            if (!TAPDataManager.getInstance().checkFirebaseToken()) {
                try {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                        if (null != task.getResult()) {
                            String fcmToken = task.getResult().getToken();
                            TAPDataManager.getInstance().registerFcmTokenToServer(fcmToken, new TAPDefaultDataView<TAPCommonResponse>() {});
                            TAPDataManager.getInstance().saveFirebaseToken(fcmToken);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TAPDataManager.getInstance().registerFcmTokenToServer(TAPDataManager.getInstance().getFirebaseToken(), new TAPDefaultDataView<TAPCommonResponse>() {});
            }
        }).start();
    }

    public static void initializeGooglePlacesApiKey(String apiKey) {
        Places.initialize(appContext, apiKey);
    }

    public static void connect(TapCommonListener listener) {
        if (isAuthenticated()) {
            TAPConnectionManager.getInstance().connect(listener);
        } else {
            listener.onError(ERROR_CODE_ACCESS_TOKEN_UNAVAILABLE, ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE);
        }
    }

    public static void disconnect() {
        TAPConnectionManager.getInstance().close(NOT_CONNECTED);
    }

    public static void enableAutoConnect() {
        isAutoConnectDisabled = false;
    }

    public static void disableAutoConnect() {
        isAutoConnectDisabled = true;
    }

    public static boolean isAuthenticated() {
        return TAPDataManager.getInstance().checkAccessTokenAvailable();
    }

    public static boolean isConnected() {
        return TAPConnectionManager.getInstance().getConnectionStatus() == CONNECTED;
    }

    public static boolean isAutoConnectEnabled() {
        return !isAutoConnectDisabled;
    }

    public static int getClientAppIcon() {
        return clientAppIcon;
    }

    public static String getClientAppName() {
        return clientAppName;
    }

    public static void addTapTalkListener(TapTalkListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            tapTalk.tapTalkListeners.add(listener);
        }
    }



    public static List<TapTalkListener> getTapTalkListeners() {
        return tapTalk.tapTalkListeners;
    }

    public static void setTapTalkEnvironmentProduction() {
        setTapTalkEnvironment(TapTalkEnvironmentProduction);
    }

    public static void setTapTalkEnvironmentStaging() {
        setTapTalkEnvironment(TapTalkEnvironmentStaging);
    }

    public static void setTapTalkEnvironmentDevelopment() {
        setTapTalkEnvironment(TapTalkEnvironmentDevelopment);
    }

    private static void setTapTalkEnvironment(@NonNull TapTalkEnvironment environment) {
        if (TapTalkEnvironmentProduction == environment) {
            TAPApiManager.setBaseUrlApi(BASE_URL_API_PRODUCTION);
            TAPApiManager.setBaseUrlSocket(BASE_URL_SOCKET_PRODUCTION);
            TAPConnectionManager.getInstance().setWebSocketEndpoint(BASE_WSS_PRODUCTION);
        } else if (TapTalkEnvironmentStaging == environment) {
            TAPApiManager.setBaseUrlApi(BASE_URL_API_STAGING);
            TAPApiManager.setBaseUrlSocket(BASE_URL_SOCKET_STAGING);
            TAPConnectionManager.getInstance().setWebSocketEndpoint(BASE_WSS_STAGING);
        } else if (TapTalkEnvironmentDevelopment == environment) {
            TAPApiManager.setBaseUrlApi(BASE_URL_API_DEVELOPMENT);
            TAPApiManager.setBaseUrlSocket(BASE_URL_SOCKET_DEVELOPMENT);
            TAPConnectionManager.getInstance().setWebSocketEndpoint(BASE_WSS_DEVELOPMENT);
        }
    }

    public static void refreshActiveUser(TapCommonListener listener) {
        new Thread(() -> {
            if (null != TAPChatManager.getInstance().getActiveUser()) {
                TAPDataManager.getInstance().getUserByIdFromApi(TAPChatManager.getInstance().getActiveUser().getUserID(), new TAPDefaultDataView<TAPGetUserResponse>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        TAPDataManager.getInstance().saveActiveUser(response.getUser());
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

    public static TAPUserModel getTaptalkActiveUser() {
        if (null == TAPDataManager.getInstance().getActiveUser()) {
            return null;
        }
        return TAPDataManager.getInstance().getActiveUser();
    }

    public static void refreshRemoteConfigs(io.taptalk.TapTalk.Listener.TapCommonListener listener) {
        TapCoreProjectConfigsManager.getInstance().getProjectConfigs(new TapProjectConfigsListener() {
            @Override
            public void onSuccess(TapConfigs config) {
                coreConfigs = config.getCoreConfigs();
                projectConfigs = config.getProjectConfigs();
                customConfigs = config.getCustomConfigs();
                TAPDataManager.getInstance().saveCoreConfigs(coreConfigs);
                TAPDataManager.getInstance().saveProjectConfigs(projectConfigs);
                TAPDataManager.getInstance().saveCustomConfigs(customConfigs);
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

    private static void presetConfigs() {
        coreConfigs = TAPDataManager.getInstance().getCoreConfigs();
        projectConfigs = TAPDataManager.getInstance().getProjectConfigs();
        customConfigs = TAPDataManager.getInstance().getCustomConfigs();

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

    // TODO: 22 August 2019 CORE MODEL
    public static Map<String, String> getCoreConfigs() {
        return new HashMap<>(coreConfigs);
    }

    public static Map<String, String> getProjectConfigs() {
        return new HashMap<>(projectConfigs);
    }

    public static Map<String, String> getCustomConfigs() {
        return new HashMap<>(customConfigs);
    }

    public static void updateApplicationBadgeCount() {
        TAPNotificationManager.getInstance().updateUnreadCount();
    }

    public static void logoutAndClearAllTapTalkData(TapCommonListener listener) {
        TAPDataManager.getInstance().logout(new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                clearAllTapTalkData();
                listener.onSuccess(response.getMessage());
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

    public static void clearAllTapTalkData() {
        TAPDataManager.getInstance().deleteAllPreference();
        TAPDataManager.getInstance().deleteAllFromDatabase();
        TAPDataManager.getInstance().deleteAllManagerData();
        TAPApiManager.getInstance().setLogout(true);
        TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
        TAPChatManager.getInstance().disconnectAfterRefreshTokenExpired();
        isRefreshTokenExpired = true;
    }

    /**
     * =============================================================================================
     * TEMP
     * =============================================================================================
     */

    private static void setTapTalkScreenOrientation(TapTalkScreenOrientation orientation) {
        TapTalk.screenOrientation = orientation;
    }

    private static TapTalkScreenOrientation getTapTalkScreenOrientation() {
        return TapTalk.screenOrientation;
    }

    // Enable/disable in-app notification after chat fragment goes inactive or to background
    private static void setInAppNotificationEnabled(boolean enabled) {
        TAPNotificationManager.getInstance().setRoomListAppear(!enabled);
    }

//    private static void setOpenTapTalkUserProfileByDefaultEnabled(boolean enabled) {
//        isOpenDefaultProfileEnabled = enabled;
//    }

    private void createAndShowBackgroundNotification(Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        TAPNotificationManager.getInstance().createAndShowBackgroundNotification(context, notificationIcon, destinationClass, newMessageModel);
    }
}
