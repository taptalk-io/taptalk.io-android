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
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPCustomKeyboardManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAuthTicketResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.BuildConfig;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MESSAGE_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DatabaseType.SEARCH_DB;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_REPLY_REQ_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_TEXT_REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TAP_NOTIFICATION_CHANNEL;

public class TapTalk {
    public static TapTalk tapTalk;
    public static Context appContext;
    public static boolean isForeground;
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

    public static TapTalk init(Context context, TAPListener tapListener) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, tapListener)) : tapTalk;
    }

    public TapTalk(final Context appContext, TAPListener tapListener) {
        //init Hawk for Preference
        //ini ngecek fungsinya kalau dev hawknya ga di encrypt sisanya hawknya di encrypt
        if (BuildConfig.BUILD_TYPE.equals("dev"))
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        else Hawk.init(appContext).build();

        //ini buat bkin database bisa di akses (setiap tambah repo harus tambah ini)
        TAPDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        TAPDataManager.getInstance().initDatabaseManager(MY_CONTACT_DB, (Application) appContext);
        //ini buat ambil context dr app utama karena library module ga bsa punya app context sndiri
        TapTalk.appContext = appContext;
        clientAppName = appContext.getResources().getString(R.string.app_name);

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

        TAPCustomKeyboardManager.getInstance().addCustomKeyboardListener(tapListener);
    }

    public static void saveAuthTicketAndGetAccessToken(String authTicket, TapDefaultDataView<TAPGetAccessTokenResponse> view) {
        TAPDataManager.getInstance().saveAuthTicket(authTicket);
        TAPDataManager.getInstance().getAccessTokenFromApi(view);
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
        TAPApiManager.getInstance().setLogout(true);
        TAPChatManager.getInstance().disconnectAfterRefreshTokenExpired();
        TAPDataManager.getInstance().deleteAllPreference();
        TAPDataManager.getInstance().deleteAllFromDatabase();
        Intent intent = new Intent(appContext, TAPLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        appContext.startActivity(intent);
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

    // TODO: 05/12/18 harus diilangin pas diintegrasi

    public static void login() {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            try {
                tapTalk.tempForceLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tempForceLogin() throws Exception {
        String ipAddress = TAPUtils.getInstance().getStringFromURL(new URL("https://api.ipify.org/"));
        String userAgent = "android";
        String userPlatform = "android";
        String xcUserID = "10";
        String fullname = "Jefry Lorentono";
        String email = "jefry@moselo.com";
        String phone = "08979809026";
        String username = "jefry";
        String deviceID = Settings.Secure.getString(TapTalk.appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        TAPDataManager.getInstance().getAuthTicket(ipAddress, userAgent, userPlatform, deviceID, xcUserID,
                fullname, email, phone, username, authView);
    }

    private TapDefaultDataView<TAPAuthTicketResponse> authView = new TapDefaultDataView<TAPAuthTicketResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(TAPAuthTicketResponse response) {
            super.onSuccess(response);
            TAPApiManager.getInstance().setLogout(false);
            TapTalk.saveAuthTicketAndGetAccessToken(response.getTicket()
                    , accessTokenView);
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            new TapTalkDialog.Builder(appContext)
                    .setTitle("ERROR " + error.getCode())
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle("OK")
                    .show();
        }
    };

    private TapDefaultDataView<TAPGetAccessTokenResponse> accessTokenView = new TapDefaultDataView<TAPGetAccessTokenResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(TAPGetAccessTokenResponse response) {
            super.onSuccess(response);
            TAPDataManager.getInstance().deleteAuthTicket();

            TAPDataManager.getInstance().saveAccessToken(response.getAccessToken());
            TAPDataManager.getInstance().saveRefreshToken(response.getRefreshToken());
            TAPDataManager.getInstance().saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
            TAPDataManager.getInstance().saveAccessTokenExpiry(response.getAccessTokenExpiry());
            registerFcmToken();

            TAPDataManager.getInstance().saveActiveUser(response.getUser());
            TAPConnectionManager.getInstance().connect();
            for (TAPListener listener : tapListeners)
                listener.onLoginSuccess(response.getUser());
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            new TapTalkDialog.Builder(appContext)
                    .setTitle("ERROR " + error.getCode())
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle("OK")
                    .show();
        }
    };

    private void registerFcmToken() {
        new Thread(() -> TAPDataManager.getInstance().registerFcmTokenToServer(TAPDataManager.getInstance().getFirebaseToken(), new TapDefaultDataView<TAPCommonResponse>() {
        })).start();
    }

    public static void addTapTalkListener(TAPListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            tapTalk.tapListeners.add(listener);
        }
    }

    public static void sendTextMessageWithXcUserID(String message, String xcUserID, TAPSendMessageWithIDListener listener) {
        if (null == tapTalk) {
            throw new IllegalStateException(appContext.getString(R.string.init_taptalk));
        } else {
            tapTalk.getUserFromXcUserIDAndSendProductRequestMessage(message, xcUserID, listener);
        }
    }

    private void getUserFromXcUserIDAndSendProductRequestMessage(String message, String xcUserID, TAPSendMessageWithIDListener listener) {
        new Thread(() -> {
            TAPUserModel otherUserModel = new TAPUserModel();
            final TAPUserModel myUserModel = TAPChatManager.getInstance().getActiveUser();
            TAPDataManager.getInstance().getUserWithXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
                @Override
                public void onSelectFinished(TAPUserModel entity) {
                    if (null == entity) {
                        TAPDataManager.getInstance().getUserByXcUserIdFromApi(xcUserID, new TapDefaultDataView<TAPGetUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetUserResponse response) {
                                if (null != response && null != response.getUser()) {
                                    otherUserModel.updateValue(response.getUser());
                                    createAndSendProductRequestMessage(message, myUserModel, otherUserModel, listener);
                                } else {
                                    listener.sendFailed(new TAPErrorModel("404", "User Not Found", ""));
                                }
                            }

                            @Override
                            public void onError(TAPErrorModel error) {
                                listener.sendFailed(error);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                listener.sendFailed(new TAPErrorModel("404", "User Not Found", ""));
                            }
                        });
                    } else {
                        otherUserModel.updateValue(entity);
                        createAndSendProductRequestMessage(message, myUserModel, otherUserModel, listener);
                    }
                }
            });
        }).start();
    }

    private void createAndSendProductRequestMessage(String message, TAPUserModel myUserModel, TAPUserModel otherUserModel, TAPSendMessageWithIDListener listener) {
        TAPRoomModel roomModel = TAPRoomModel.Builder(TAPChatManager.getInstance().arrangeRoomId(myUserModel.getUserID(), otherUserModel.getUserID()),
                otherUserModel.getName(), 1, otherUserModel.getAvatarURL(), "#FFFFFF");
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(message, roomModel);
        listener.sendSuccess();
    }
}