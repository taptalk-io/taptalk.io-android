package io.taptalk.TapTalk.Helper;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapProjectConfigsInterface;
import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Interface.TAPGetUserInterface;
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface;
import io.taptalk.TapTalk.Interface.TapCommonInterface;
import io.taptalk.TapTalk.Interface.TapTalkOpenChatRoomInterface;
import io.taptalk.TapTalk.Listener.TAPChatRoomListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapConfigs;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INVALID_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INVALID_AUTH_TICKET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_AUTHENTICATE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_REFRESH_CONFIG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_CHANNEL_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_GROUP_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ITEMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_REPLY_REQ_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_TEXT_REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.CHANNEL_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.CHAT_MEDIA_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.GROUP_MAX_PARTICIPANTS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.ROOM_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.USERNAME_IGNORE_CASE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.USER_PHOTO_MAX_FILE_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.REFRESH_TOKEN_RENEWED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TAP_NOTIFICATION_CHANNEL;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentDevelopment;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentProduction;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentStaging;
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
    private List<TAPListener> tapListeners = new ArrayList<>();
    private List<TAPChatRoomListener> tapChatRoomListeners = new ArrayList<>();

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
        TapTalkImplentationTypeCore,
        TapTalkImplentationTypeUI,
        TapTalkImplentationTypeCombine
    }

    public enum TapTalkScreenOrientation {
        TapTalkOrientationDefault,
        TapTalkOrientationPortrait,
        TapTalkOrientationLandscape // FIXME: 6 February 2019 Activity loads portrait by default then changes to landscape after onCreate
    }

    public static TapTalk init(Context context, String appID, String appSecret, String userAgent, int clientAppIcon, String clientAppName, TapTalkImplementationType type, TAPListener tapListener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, appID, appSecret, userAgent, clientAppIcon, clientAppName, type, tapListener)) : tapTalk;
    }

    public static TapTalk init(Context context, String appID, String appSecret, int clientAppIcon, String clientAppName, TapTalkImplementationType type, TAPListener tapListener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, appID, appSecret, "android", clientAppIcon, clientAppName, type, tapListener)) : tapTalk;
    }

    public TapTalk(@NonNull final Context appContext, @NonNull String appID, @NonNull String appSecret
            , @NonNull String userAgent,
                   int clientAppIcon, String clientAppName,
                   TapTalkImplementationType type,
                   @NonNull TAPListener tapListener) {

        TapTalk.appContext = appContext;
        clientAppName = appContext.getResources().getString(R.string.tap_app_name);

        // Init Hawk for freference
        if (BuildConfig.BUILD_TYPE.equals("dev")) {
            // No encryption for dev build
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        } else {
            Hawk.init(appContext).build();
        }

        implementationType = type;

        TAPCacheManager.getInstance(appContext).initAllCache();

        // Update when adding database table
        TAPDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(MY_CONTACT_DB, (Application) appContext);

        // Save header requirement
        TAPDataManager.getInstance().saveApplicationID(appID);
        TAPDataManager.getInstance().saveApplicationSecret(appSecret);
        TAPDataManager.getInstance().saveUserAgent(userAgent);

        // Init configs
        presetConfigs();
        refreshProjectConfigs(new TapCommonInterface() {
            @Override
            public void onSuccess(String successMessage) {

            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });

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

        tapListeners.add(tapListener);
        TAPContactManager.getInstance().loadAllUserDataFromDatabase();

        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                isForeground = true;

                //Load Back Group and User Data to Manager
                TAPContactManager.getInstance().loadAllUserDataFromDatabase();
                TAPGroupManager.Companion.getGetInstance().loadAllRoomDataFromPreference();

                TAPChatManager.getInstance().setFinishChatFlow(false);
                TAPNetworkStateManager.getInstance().registerCallback(TapTalk.appContext);
                TAPChatManager.getInstance().triggerSaveNewMessage();
                TAPFileDownloadManager.getInstance().getFileProviderPathFromPreference();
                TAPFileDownloadManager.getInstance().getFileMessageUriFromPreference();
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

                //di kasih selection biar dy cuman akan buat 1x selama apps belom di kill
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

    public static void authenticate(String authTicket, boolean connectOnSuccess, TapCommonInterface listener) {
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

    public static void loginWithRequestOTP(int countryID, String phoneNumber, TAPRequestOTPInterface requestOTPInterface) {
        TAPDataManager.getInstance().requestOTPLogin(countryID, phoneNumber, new TAPDefaultDataView<TAPLoginOTPResponse>() {
            @Override
            public void onSuccess(TAPLoginOTPResponse response) {
                super.onSuccess(response);
                requestOTPInterface.onRequestSuccess(response.getOtpID(), response.getOtpKey(), response.getPhoneWithCode(), response.isSuccess());
            }

            @Override
            public void onError(TAPErrorModel error) {
                super.onError(error);
                requestOTPInterface.onRequestFailed(error.getMessage(), error.getCode());
            }

            @Override
            public void onError(String errorMessage) {
                requestOTPInterface.onRequestFailed(errorMessage, "400");
            }
        });
    }

    public static void verifyOTP(long otpID, String otpKey, String otpCode, TAPVerifyOTPInterface verifyOTPInterface) {
        TAPDataManager.getInstance().verifyingOTPLogin(otpID, otpKey, otpCode, new TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
            @Override
            public void onSuccess(TAPLoginOTPVerifyResponse response) {
                if (response.isRegistered()) {
                    authenticate(response.getTicket(), true, new TapCommonInterface() {
                        @Override
                        public void onSuccess(String successMessage) {
                            verifyOTPInterface.verifyOTPSuccessToLogin();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            verifyOTPInterface.verifyOTPFailed(errorCode, errorMessage);
                        }
                    });
                } else {
                    verifyOTPInterface.verifyOTPSuccessToRegister();
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                verifyOTPInterface.verifyOTPFailed(error.getMessage(), error.getCode());
            }

            @Override
            public void onError(String errorMessage) {
                verifyOTPInterface.verifyOTPFailed(errorMessage, "400");
            }
        });
    }

    public static void checkActiveUserToShowPage(Activity activity) {
        if (null != activity) {
            Intent intent;
            if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
                intent = new Intent(activity, TAPRoomListActivity.class);
            } else {
                intent = new Intent(activity, TAPLoginActivity.class);
            }
            activity.startActivity(intent);
            activity.finish();
        } else {
            throw new NullPointerException("The Activity that passed was null");
        }
    }


    // TODO: 15/10/18 saat integrasi harus di ilangin
    public static void refreshTokenExpired() {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            TAPDataManager.getInstance().deleteAllPreference();
            TAPDataManager.getInstance().deleteAllFromDatabase();
            TAPDataManager.getInstance().deleteAllManagerData();
            TAPApiManager.getInstance().setLogout(true);
            TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
            TAPChatManager.getInstance().disconnectAfterRefreshTokenExpired();
            isRefreshTokenExpired = true;

            for (TAPListener listener : getTapTalkListeners()) {
                listener.onRefreshAuthTicket();
            }
        }
    }

    public static void saveFirebaseToken(String newFirebaseToken) {
        if (!TAPDataManager.getInstance().checkFirebaseToken(newFirebaseToken)) {
            TAPDataManager.getInstance().saveFirebaseToken(newFirebaseToken);
        }
    }

    public static void saveAppInfo(int clientAppIcon, String clientAppName) {
        TapTalk.clientAppIcon = clientAppIcon;
        TapTalk.clientAppName = clientAppName;
    }

    public static int getClientAppIcon() {
        return clientAppIcon;
    }

    public static String getClientAppName() {
        return clientAppName;
    }

    public static void initializeGooglePlacesApiKey(String apiKey) {
        Places.initialize(appContext, apiKey);
    }

    public static void connect(TapCommonInterface listener) {
        if (checkAccessTokenAvailability()) {
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

    public static List<TAPCustomKeyboardItemModel> requestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            try {
                return tapTalk.requestCustomKeyboardItemsFromClient(activeUser, otherUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<TAPCustomKeyboardItemModel> requestCustomKeyboardItemsFromClient(TAPUserModel activeUser, TAPUserModel otherUser) {
        for (TAPChatRoomListener listener : tapChatRoomListeners) {
            List<TAPCustomKeyboardItemModel> customKeyboardItems = listener.setCustomKeyboardItems(activeUser, otherUser);
            if (null != customKeyboardItems) {
                return customKeyboardItems;
            }
        }
        return null;
    }

    public static void triggerMessageQuoteClicked(Activity activity, TAPMessageModel messageModel) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            try {
                tapTalk.triggerMessageQuoteClicked(activity, messageModel, (HashMap<String, Object>) messageModel.getData().get(USER_INFO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void triggerMessageQuoteClicked(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo) {
        for (TAPChatRoomListener listener : tapChatRoomListeners) {
            listener.onTapTalkMessageQuoteTapped(activity, messageModel, userInfo);
        }
    }

    //Builder buat setting isi dari Notification chat
    public static class NotificationBuilder {
        public Context context;
        public String chatSender = "", chatMessage = "";
        public TAPMessageModel notificationMessage;
        public int smallIcon;
        boolean isNeedReply;
        TAPRoomModel roomModel;
        NotificationCompat.Builder notificationBuilder;
        private Class aClass;

        public NotificationBuilder(Context context) {
            this.context = context;
        }

        public NotificationBuilder setNotificationMessage(TAPMessageModel notificationMessage) {
            this.notificationMessage = notificationMessage;
            TAPNotificationManager.getInstance().addNotifMessageToMap(notificationMessage);
            if (null != notificationMessage &&
                    null != notificationMessage.getRoom() && null != notificationMessage.getUser() &&
                    TYPE_GROUP == notificationMessage.getRoom().getRoomType()) {
                //Log.e(TAG, "setNotificationMessage: " + TAPUtils.getInstance().toJsonString(notificationMessage));
                setChatMessage(notificationMessage.getUser().getName() + ": " + notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getRoomName());
            } else if (null != notificationMessage) {
                //Log.e(TAG, "setNotificationMessage:2 " + TAPUtils.getInstance().toJsonString(notificationMessage));
                setChatMessage(notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getRoomName());
            }
            return this;
        }

        private void setChatSender(String chatSender) {
            this.chatSender = chatSender;
        }

        private void setChatMessage(String chatMessage) {
            this.chatMessage = chatMessage;
        }

        public NotificationBuilder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public NotificationBuilder setNeedReply(boolean needReply) {
            isNeedReply = needReply;
            return this;
        }

        public NotificationBuilder setOnClickAction(Class aClass) {
            this.roomModel = notificationMessage.getRoom();
            this.aClass = aClass;
            return this;
        }

        public Notification build() {
            this.notificationBuilder = TAPNotificationManager.getInstance().createNotificationBubble(this);
            addReply();
            if (null != roomModel && null != aClass) addPendingIntentWhenClicked();
            return this.notificationBuilder.build();
        }

        private void addReply() {
            if (isNeedReply && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteInput remoteInput = new RemoteInput.Builder(K_TEXT_REPLY)
                        .setLabel("Reply").build();
                Intent intent = new Intent(context, TAPReplyBroadcastReceiver.class);
                intent.setAction(K_TEXT_REPLY);
                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, K_REPLY_REQ_CODE,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action = new NotificationCompat.Action.Builder(smallIcon,
                        "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                notificationBuilder.addAction(action);
            }
        }

        private void addPendingIntentWhenClicked() {
            Intent intent = new Intent(context, aClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ROOM, roomModel);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        private void createNotificationChannel() {
            NotificationManager notificationManager = (NotificationManager) TapTalk.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null == notificationManager.getNotificationChannel(TAP_NOTIFICATION_CHANNEL)) {
                NotificationChannel notificationChannel = new NotificationChannel(TAP_NOTIFICATION_CHANNEL, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("TapTalk Notification");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.parseColor("#2eccad"));
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        public void show() {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(TapTalk.appContext);
            createNotificationChannel();

            notificationManager.notify(notificationMessage.getRoom().getRoomID(), 0, build());

            if (1 < TAPNotificationManager.getInstance().getNotifMessagesMap().size()) {
                notificationManager.notify(0, TAPNotificationManager.getInstance().createSummaryNotificationBubble(context, aClass).build());
            }

        }
    }

    public static void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        TAPCustomBubbleManager.getInstance().addCustomBubbleMap(baseCustomBubble);
    }

    /**
     * Enable/disable in-app notification after chat fragment goes inactive or to background
     *
     * @param enabled
     */
    public static void setInAppNotificationEnabled(boolean enabled) {
        TAPNotificationManager.getInstance().setRoomListAppear(!enabled);
    }

//    public static void setOpenTapTalkUserProfileByDefaultEnabled(boolean enabled) {
//        isOpenDefaultProfileEnabled = enabled;
//    }

    public static void setTapTalkScreenOrientation(TapTalkScreenOrientation orientation) {
        TapTalk.screenOrientation = orientation;
    }

    public static TapTalkScreenOrientation getTapTalkScreenOrientation() {
        return TapTalk.screenOrientation;
    }

    public static void openTapTalkUserProfile(Context context, TAPUserModel userModel) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        TAPDataManager.getInstance().getRoomModel(userModel, new TAPDatabaseListener<TAPRoomModel>() {
            @Override
            public void onSelectFinished(TAPRoomModel roomModel) {
                if (null == contextWeakReference.get()) {
                    return;
                }
                Intent intent = new Intent(contextWeakReference.get(), TAPChatProfileActivity.class);
                intent.putExtra(ROOM, roomModel);
                contextWeakReference.get().startActivity(intent);
                if (contextWeakReference.get() instanceof Activity) {
                    ((Activity) contextWeakReference.get()).overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
                }
            }
        });
    }

    public static void openTapTalkUserProfile(Context context, String xcUserID) {
        TAPUtils.getInstance().getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel userModel) {
                openTapTalkUserProfile(context, userModel);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
            }
        });
    }

    public static void addTapTalkListener(TAPListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            tapTalk.tapListeners.add(listener);
        }
    }

    public static void triggerListenerProductLeftButtonClicked(Activity activity, TAPProductModel productModel
            , String recipientXcUserID, TAPRoomModel room) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            for (TAPChatRoomListener listener : TapTalk.getTapTalkChatRoomListeners()) {
                listener.onTapTalkProductListBubbleLeftButtonTapped(activity, productModel, recipientXcUserID, room);
            }
        }
    }

    public static void triggerListenerProductRightButtonClicked(Activity activity, TAPProductModel productModel
            , String recipientXcUserID, TAPRoomModel room) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            for (TAPChatRoomListener listener : TapTalk.getTapTalkChatRoomListeners()) {
                listener.onTapTalkProductListBubbleRightButtonTapped(activity, productModel, recipientXcUserID, room);
            }
        }
    }

    public static void triggerUpdateUnreadCountListener(int unreadCount) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            for (TAPListener tapListener : TapTalk.getTapTalkListeners()) {
                tapListener.onTapTalkUnreadChatRoomBadgeCountUpdated(unreadCount);
            }
        }
    }

    public static List<TAPListener> getTapTalkListeners() {
        return tapTalk.tapListeners;
    }

    public static List<TAPChatRoomListener> getTapTalkChatRoomListeners() {
        return tapTalk.tapChatRoomListeners;
    }

    // TODO: 20 February 2019 ADD LISTENER TO DETECT FAILURE?
    public static void sendImageMessage(Context context, Uri imageUri, String recipientXcUserID, String caption) {
        TAPUtils.getInstance().getUserFromXcUserID(recipientXcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                TAPChatManager.getInstance().sendImageMessage(
                        context,
                        TAPChatManager.getInstance().arrangeRoomId(
                                TAPChatManager.getInstance().getActiveUser().getUserID(),
                                user.getUserID()),
                        imageUri,
                        caption);
            }
        });
    }

    public static void sendImageMessage(Context context, Bitmap bitmap, String recipientXcUserID, String caption) {
        TAPUtils.getInstance().getUserFromXcUserID(recipientXcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                TAPChatManager.getInstance().sendImageMessage(
                        context,
                        TAPChatManager.getInstance().arrangeRoomId(
                                TAPChatManager.getInstance().getActiveUser().getUserID(),
                                user.getUserID()),
                        bitmap,
                        caption);
            }
        });
    }

    public static void sendImageMessage(Context context, Uri imageUri, TAPUserModel recipientUserModel, String caption) {
        TAPChatManager.getInstance().sendImageMessage(
                context,
                TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        recipientUserModel.getUserID()),
                imageUri,
                caption);
    }

    public static void sendImageMessage(Context context, Bitmap bitmap, TAPUserModel recipientUserModel, String caption) {
        TAPChatManager.getInstance().sendImageMessage(
                context,
                TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        recipientUserModel.getUserID()),
                bitmap,
                caption);
    }

    public static void sendTextMessageWithRecipientUser(String message, TAPUserModel recipientUser, TAPSendMessageWithIDListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.tap_init_taptalk));
        } else {
            tapTalk.getUserFromRecipientUserAndSendProductRequestMessage(message, recipientUser, listener);
        }
    }

    public static void sendProductMessage(List<TAPProductModel> productModels, TAPUserModel recipientUserModel) {
        int productSize = productModels.size();
        List<TAPProductModel> tempProductModel = new ArrayList<>();
        for (int index = 1; index <= productSize; index++) {
            tempProductModel.add(productModels.get(index - 1));
            if (index == productSize || index % 20 == 0) {
                HashMap<String, Object> productHashMap = new LinkedHashMap<>();
                productHashMap.put(ITEMS, new ArrayList<>(tempProductModel));
                TAPChatManager.getInstance().sendProductMessageToServer(productHashMap, recipientUserModel);
                tempProductModel.clear();
            }
        }
    }

    private void getUserFromRecipientUserAndSendProductRequestMessage(String message, @NonNull TAPUserModel recipientUser, TAPSendMessageWithIDListener listener) {
        new Thread(() -> {
            try {
                final TAPUserModel myUserModel = TAPChatManager.getInstance().getActiveUser();
                createAndSendProductRequestMessage(message, myUserModel, recipientUser, listener);
            } catch (Exception e) {
                e.printStackTrace();
                listener.sendFailed(new TAPErrorModel("", e.getMessage(), ""));
                Log.e(TAG, "getUserFromRecipientUserAndSendProductRequestMessage: ", e);
            }
        }).start();
    }

    private void createAndSendProductRequestMessage(String message, TAPUserModel myUserModel, TAPUserModel otherUserModel, TAPSendMessageWithIDListener listener) {
        TAPRoomModel roomModel = TAPRoomModel.Builder(TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), otherUserModel.getUserID()),
                otherUserModel.getName(), 1, otherUserModel.getAvatarURL(), "#FFFFFF");
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(message, roomModel);
        listener.sendSuccess();
    }

    private static void registerFcmToken() {
        new Thread(() -> {
            if (!TAPDataManager.getInstance().checkFirebaseToken()) {
                try {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (null != task.getResult()) {
                                    String fcmToken = task.getResult().getToken();
                                    TAPDataManager.getInstance().registerFcmTokenToServer(fcmToken, new TAPDefaultDataView<TAPCommonResponse>() {
                                    });
                                    TAPDataManager.getInstance().saveFirebaseToken(fcmToken);
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "registerFcmToken: ", e);
                }
            } else {
                TAPDataManager.getInstance().registerFcmTokenToServer(TAPDataManager.getInstance().getFirebaseToken(), new TAPDefaultDataView<TAPCommonResponse>() {
                });
            }
        }).start();
    }

    /**
     * for AccessToken Availability checking
     */
    public static boolean checkAccessTokenAvailability() {
        return TAPDataManager.getInstance().checkAccessTokenAvailable();
    }

    /**
     * Create Notification for Background
     */
    public void createAndShowBackgroundNotification(Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        TAPNotificationManager.getInstance().createAndShowBackgroundNotification(context, notificationIcon, destinationClass, newMessageModel);
    }

    /**
     * @param quoteTitle    is required to open room with predefined quote
     * @param quoteImageURL quote will only contain text if Image URL is empty
     * @param userInfo      (requires quoteTitle) will be returned on click action after the next message is delivered
     * @param listener      returns onOpenRoomSuccess when room is successfully opened, returns onOpenRoomFailed when other user data is not obtained
     */
    public static void openChatRoomWithUserID(
            Activity activity,
            String xcUserID,
            @Nullable String quoteTitle,
            @Nullable String quoteContent,
            @Nullable String quoteImageURL,
            @Nullable HashMap<String, Object> userInfo,
            @Nullable String prefilledText,
            TapTalkOpenChatRoomInterface listener) {
        TAPUtils.getInstance().getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                String roomID = TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        user.getUserID());
                if (null != quoteTitle) {
                    // Save quote to Chat Manager
                    TAPChatManager.getInstance().setQuotedMessage(roomID, quoteTitle, quoteContent, quoteImageURL);
                    if (null != userInfo) {
                        // Save user info to Chat Manager
                        TAPChatManager.getInstance().saveUserInfo(roomID, userInfo);
                    }
                }
                if (null != prefilledText) {
                    // Save pre-filled text as draft
                    TAPChatManager.getInstance().saveMessageToDraft(roomID, prefilledText);
                }
                // Start activity
                TAPUtils.getInstance().startChatActivity(
                        activity,
                        roomID,
                        user.getName(),
                        user.getAvatarURL(),
                        1,      // TODO: 28 January 2019 GET 1-1 ROOM TYPE
                        "");    // TODO: 28 January 2019 GET ROOM COLOR
                listener.onOpenRoomSuccess();
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onOpenRoomFailed(errorMessage);
            }
        });
    }

    public static void openChatRoomWithUserID(
            Activity activity,
            String xcUserID,
            TapTalkOpenChatRoomInterface listener) {
        openChatRoomWithUserID(activity, xcUserID, null, null, null, null, null, listener);
    }

    public static void openChatRoomWithUserID(
            Activity activity,
            String xcUserID,
            String prefilledText,
            TapTalkOpenChatRoomInterface listener) {
        openChatRoomWithUserID(activity, xcUserID, null, null, null, null, prefilledText, listener);
    }

    public static void openChatRoomWithUserID(
            Activity activity,
            String xcUserID,
            String quoteTitle,
            @Nullable String quoteContent,
            @Nullable String quoteImageURL,
            @Nullable HashMap<String, Object> userInfo,
            TapTalkOpenChatRoomInterface listener) {
        openChatRoomWithUserID(activity, xcUserID, quoteTitle, quoteContent, quoteImageURL, userInfo, null, listener);
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

    public static void refreshActiveUser() {
        new Thread(() -> {
            if (null != TAPChatManager.getInstance().getActiveUser()) {
                TAPDataManager.getInstance().getUserByIdFromApi(TAPChatManager.getInstance().getActiveUser().getUserID(), new TAPDefaultDataView<TAPGetUserResponse>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        TAPDataManager.getInstance().saveActiveUser(response.getUser());
                    }
                });
            }
        }).start();
    }

    public static void callUpdateUnreadCount() {
        TAPNotificationManager.getInstance().updateUnreadCount();
    }

    public static void addChatRoomListener(TAPChatRoomListener listener) {
        tapTalk.tapChatRoomListeners.add(listener);
    }

    public static void getTaptalkUserWithClientUserID(String clientUserID, TAPGetUserInterface getUserInterface) {
        TAPDataManager.getInstance().getUserByXcUserIdFromApi(clientUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
            @Override
            public void onSuccess(TAPGetUserResponse response) {
                getUserInterface.getUserSuccess(response.getUser());
            }

            @Override
            public void onError(TAPErrorModel error) {
                getUserInterface.getUserFailed(error.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                getUserInterface.getUserFailed(throwable);
            }
        });
    }

    public static TAPUserModel getTaptalkActiveUser() {
        if (null == TAPDataManager.getInstance().getActiveUser())
            return null;

        return TAPDataManager.getInstance().getActiveUser();
    }

    public static void refreshProjectConfigs(TapCommonInterface listener) {
        TapCoreProjectConfigsManager.getProjectConfigs(new TapProjectConfigsInterface() {
            @Override
            public void onSuccess(TapConfigs config) {
                coreConfigs = config.getCoreConfigs();
                projectConfigs = config.getProjectConfigs();
                customConfigs = config.getCustomConfigs();
                TAPDataManager.getInstance().saveCoreConfigs(coreConfigs);
                TAPDataManager.getInstance().saveProjectConfigs(projectConfigs);
                TAPDataManager.getInstance().saveCustomConfigs(customConfigs);
                listener.onSuccess(SUCCESS_MESSAGE_REFRESH_CONFIG);
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
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

    public static Map<String, String> getCoreConfigs() {
        return new HashMap<>(coreConfigs);
    }

    public static Map<String, String> getProjectConfigs() {
        return new HashMap<>(projectConfigs);
    }

    public static Map<String, String> getCustomConfigs() {
        return new HashMap<>(customConfigs);
    }
}
