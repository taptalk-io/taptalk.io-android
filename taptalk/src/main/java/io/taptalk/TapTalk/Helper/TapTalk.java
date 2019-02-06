package io.taptalk.TapTalk.Helper;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Interface.TAPLoginInterface;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Interface.TapTalkOpenChatRoomInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TapTalk.View.Activity.TAPProfileActivity;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_REPLY_REQ_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_TEXT_REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TAP_NOTIFICATION_CHANNEL;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentDevelopment;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentProduction;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkEnvironment.TapTalkEnvironmentStaging;

public class TapTalk {
    private static final String TAG = TapTalk.class.getSimpleName();
    public static TapTalk tapTalk;
    public static Context appContext;
    public static boolean isForeground;
//    public static boolean isOpenDefaultProfileEnabled = true;
    private static String clientAppName = "";
    private static int clientAppIcon = R.drawable.tap_ic_launcher_background;

    private Thread.UncaughtExceptionHandler defaultUEH;
    private List<TAPListener> tapListeners = new ArrayList<>();

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            TAPChatManager.getInstance().saveIncomingMessageAndDisconnect();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public enum TapTalkEnvironment {
        TapTalkEnvironmentProduction,
        TapTalkEnvironmentStaging,
        TapTalkEnvironmentDevelopment
    }

    public static TapTalk init(Context context, String appID, String appSecret, String userAgent, TAPListener tapListener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, appID, appSecret, userAgent, tapListener)) : tapTalk;
    }

    public TapTalk(@NonNull final Context appContext, @NonNull String appID, @NonNull String appSecret
            , @NonNull String userAgent, @NonNull TAPListener tapListener) {
        //init Hawk for Preference
        //ini ngecek fungsinya kalau dev hawknya ga di encrypt sisanya hawknya di encrypt
        if (BuildConfig.BUILD_TYPE.equals("dev"))
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        else Hawk.init(appContext).build();

        TAPCacheManager.getInstance(appContext).initAllCache();

        //ini buat bkin database bisa di akses (setiap tambah repo harus tambah ini)
        TAPDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(MY_CONTACT_DB, (Application) appContext);
        //ini buat ambil context dr app utama karena library module ga bsa punya app context sndiri
        TapTalk.appContext = appContext;
        clientAppName = appContext.getResources().getString(R.string.app_name);

        //save header requirement
        new Thread(() -> {
            TAPDataManager.getInstance().saveApplicationID(appID);
            TAPDataManager.getInstance().saveApplicationSecret(appSecret);
            TAPDataManager.getInstance().saveUserAgent(userAgent);
        }).start();

        if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
            //TAPConnectionManager.getInstance().connect();
            TAPOldDataManager.getInstance().startAutoCleanProcess();
        }

        TAPDataManager.getInstance().updateSendingMessageToFailed();

        //init stetho tapi hanya untuk DEBUG State
        if (BuildConfig.DEBUG)
            Stetho.initialize(
                    Stetho.newInitializerBuilder(appContext)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(appContext))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(appContext))
                            .build()
            );

        tapListeners.add(tapListener);

        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                isForeground = true;
                TAPChatManager.getInstance().setFinishChatFlow(false);
                appContext.startService(new Intent(TapTalk.appContext, TapTalkEndAppService.class));
                TAPNetworkStateManager.getInstance().registerCallback(TapTalk.appContext);
                TAPChatManager.getInstance().triggerSaveNewMessage();
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
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
            }
        });
    }

    public static void saveAuthTicketAndGetAccessToken(String authTicket, TAPLoginInterface loginInterface) {
        if (null == authTicket || "".equals(authTicket)) {
            loginInterface.onLoginFailed(new TAPErrorModel("401", "Invalid Auth Ticket", ""));
        } else {
            TAPDataManager.getInstance().saveAuthTicket(authTicket);
            TAPDataManager.getInstance().getAccessTokenFromApi(new TapDefaultDataView<TAPGetAccessTokenResponse>() {
                @Override
                public void onSuccess(TAPGetAccessTokenResponse response) {
                    super.onSuccess(response);
                    TAPDataManager.getInstance().removeAuthTicket();

                    TAPDataManager.getInstance().saveAccessToken(response.getAccessToken());
                    TAPDataManager.getInstance().saveRefreshToken(response.getRefreshToken());
                    TAPDataManager.getInstance().saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
                    TAPDataManager.getInstance().saveAccessTokenExpiry(response.getAccessTokenExpiry());
                    registerFcmToken();

                    TAPDataManager.getInstance().saveActiveUser(response.getUser());
                    TAPApiManager.getInstance().setLogout(false);
                    TAPConnectionManager.getInstance().connect();
                    loginInterface.onLoginSuccess();
                }

                @Override
                public void onError(TAPErrorModel error) {
                    super.onError(error);
                    loginInterface.onLoginFailed(error);
                }

                @Override
                public void onError(String errorMessage) {
                    super.onError(errorMessage);
                    loginInterface.onLoginFailed(new TAPErrorModel("500", errorMessage, ""));
                }
            });
        }
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
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            TAPApiManager.getInstance().setLogout(true);
            TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
            TAPChatManager.getInstance().disconnectAfterRefreshTokenExpired();
            TAPDataManager.getInstance().deleteAllPreference();
            TAPDataManager.getInstance().deleteAllFromDatabase();

            for (TAPListener listener : getTapTalkListeners()) {
                listener.onRefreshTokenExpiredOrInvalid();
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

    public static List<TAPCustomKeyboardItemModel> requestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
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
        for (TAPListener listener : tapListeners) {
            List<TAPCustomKeyboardItemModel> customKeyboardItems = listener.onRequestCustomKeyboardItems(activeUser, otherUser);
            if (null != customKeyboardItems) {
                return customKeyboardItems;
            }
        }
        return null;
    }

    public static void triggerMessageQuoteClicked(Activity activity, TAPMessageModel messageModel) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            try {
                tapTalk.triggerMessageQuoteClicked(activity, messageModel, (HashMap<String, Object>) messageModel.getData().get(USER_INFO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void triggerMessageQuoteClicked(Activity activity, TAPMessageModel messageModel, HashMap<String, Object> userInfo) {
        for (TAPListener listener : tapListeners) {
            listener.onMessageQuoteClicked(activity, messageModel, userInfo);
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
            setChatMessage(notificationMessage.getBody());
            setChatSender(notificationMessage.getUser().getName());
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
            intent.putExtra(K_ROOM, roomModel);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        private void createNotificationChannel() {
            NotificationManager notificationManager = (NotificationManager) TapTalk.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null == notificationManager.getNotificationChannel(TAP_NOTIFICATION_CHANNEL)) {
                NotificationChannel notificationChannel = new NotificationChannel(TAP_NOTIFICATION_CHANNEL, "Homing Pigeon Notifications", NotificationManager.IMPORTANCE_HIGH);

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

    public static void openTapTalkUserProfile(Context context, TAPUserModel userModel) {
        TAPDataManager.getInstance().getRoomModel(userModel, new TAPDatabaseListener<TAPRoomModel>() {
            @Override
            public void onSelectFinished(TAPRoomModel roomModel) {
                Intent intent = new Intent(context, TAPProfileActivity.class);
                intent.putExtra(K_ROOM, roomModel);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
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
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            tapTalk.tapListeners.add(listener);
        }
    }

    public static void triggerListenerProductLeftButtonClicked(Activity activity, TAPProductModel productModel
            , String recipientXcUserID, TAPRoomModel room) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            for (TAPListener tapListener : TapTalk.getTapTalkListeners()) {
                tapListener.onProductLeftButtonClicked(activity, productModel, recipientXcUserID, room);
            }
        }
    }

    public static void triggerListenerProductRightButtonClicked(Activity activity, TAPProductModel productModel
            , String recipientXcUserID, TAPRoomModel room) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            for (TAPListener tapListener : TapTalk.getTapTalkListeners()) {
                tapListener.onProductRightButtonClicked(activity, productModel, recipientXcUserID, room);
            }
        }
    }

    public static List<TAPListener> getTapTalkListeners() {
        return tapTalk.tapListeners;
    }

    public static void sendTextMessageWithRecipientUser(String message, TAPUserModel recipientUser, TAPSendMessageWithIDListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
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
                productHashMap.put("recipientXcUserID", recipientUserModel.getXcUserID());
                productHashMap.put("items", new ArrayList<>(tempProductModel));
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
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (null != task.getResult()) {
                                String fcmToken = task.getResult().getToken();
                                TAPDataManager.getInstance().registerFcmTokenToServer(fcmToken, new TapDefaultDataView<TAPCommonResponse>() {
                                });
                                TAPDataManager.getInstance().saveFirebaseToken(fcmToken);
                            }
                        });
            } else {
                TAPDataManager.getInstance().registerFcmTokenToServer(TAPDataManager.getInstance().getFirebaseToken(), new TapDefaultDataView<TAPCommonResponse>() {
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
                        TAPDataManager.getInstance().getActiveUser().getUserID(),
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

    public static void setTapTalkEnvironment(@NonNull TapTalkEnvironment environment) {
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
}
