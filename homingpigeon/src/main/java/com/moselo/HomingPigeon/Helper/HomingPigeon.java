package com.moselo.HomingPigeon.Helper;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.moselo.HomingPigeon.API.Api.HpApiManager;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BroadcastReceiver.HpReplyBroadcastReceiver;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Interface.HomingPigeonTokenInterface;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpNetworkStateManager;
import com.moselo.HomingPigeon.Manager.HpNotificationManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpCommonResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpLoginActivity;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.MESSAGE_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.MY_CONTACT_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.SEARCH_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.HP_NOTIFICATION_CHANNEL;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Notification.K_REPLY_REQ_CODE;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Notification.K_TEXT_REPLY;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;
    private Thread.UncaughtExceptionHandler defaultUEH;
    private HomingPigeonTokenInterface hpTokenInterface;
    private static int clientAppIcon = R.drawable.hp_ic_launcher_background;
    private static String clientAppName = "";

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            HpChatManager.getInstance().saveIncomingMessageAndDisconnect();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public static HomingPigeon init(Context context, HomingPigeonTokenInterface hpTokenInterface) {
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context, hpTokenInterface)) : homingPigeon;
    }

    public HomingPigeon(final Context appContext, HomingPigeonTokenInterface hpTokenInterface) {
        //init Hawk for Preference
        //ini ngecek fungsinya kalau dev hawknya ga di encrypt sisanya hawknya di encrypt
        if (BuildConfig.BUILD_TYPE.equals("dev"))
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        else Hawk.init(appContext).build();

        //ini buat bkin database bisa di akses (setiap tambah repo harus tambah ini)
        HpDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        HpDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        HpDataManager.getInstance().initDatabaseManager(MY_CONTACT_DB, (Application) appContext);
        //ini buat ambil context dr app utama karena library module ga bsa punya app context sndiri
        HomingPigeon.appContext = appContext;
        clientAppName = appContext.getResources().getString(R.string.app_name);

        if (HpDataManager.getInstance().checkAccessTokenAvailable())
            HpConnectionManager.getInstance().connect();

        HpDataManager.getInstance().updateSendingMessageToFailed();

        //init stetho tapi hanya untuk DEBUG State
        if (BuildConfig.DEBUG)
            Stetho.initialize(
                    Stetho.newInitializerBuilder(appContext)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(appContext))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(appContext))
                            .build()
            );

        this.hpTokenInterface = hpTokenInterface;

        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                HpChatManager.getInstance().setFinishChatFlow(false);
                appContext.startService(new Intent(HomingPigeon.appContext, HomingPigeonEndAppService.class));
                HpNetworkStateManager.getInstance().registerCallback(HomingPigeon.appContext);
                HpChatManager.getInstance().triggerSaveNewMessage();
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
                isForeground = true;
            }

            @Override
            public void onAppGotoBackground() {
                HpRoomListViewModel.setShouldNotLoadFromAPI(false);
                HpNetworkStateManager.getInstance().unregisterCallback(HomingPigeon.appContext);
                HpChatManager.getInstance().updateMessageWhenEnterBackground();
                isForeground = false;
            }
        });
    }

    public static void saveAuthTicketAndGetAccessToken(String authTicket, HpDefaultDataView<HpGetAccessTokenResponse> view) {
        HpDataManager.getInstance().saveAuthTicket(authTicket);
        HpDataManager.getInstance().getAccessTokenFromApi(view);
    }

    public static void checkActiveUserToShowPage(Activity activity) {
        if (null != activity) {
            Intent intent;
            if (HpDataManager.getInstance().checkAccessTokenAvailable()) {
                intent = new Intent(activity, HpRoomListActivity.class);
            } else {
                intent = new Intent(activity, HpLoginActivity.class);
            }
            activity.startActivity(intent);
            activity.finish();
        } else {
            throw new NullPointerException("The Activity that passed was null");
        }
    }

    // TODO: 15/10/18 saat integrasi harus di ilangin
    public static void refreshTokenExpired() {
        HpApiManager.getInstance().setLogout(true);
        HpChatManager.getInstance().disconnectAfterRefreshTokenExpired();
        HpDataManager.getInstance().deleteAllPreference();
        HpDataManager.getInstance().deleteAllFromDatabase();
        Intent intent = new Intent(appContext, HpLoginActivity.class);
        appContext.startActivity(intent);
    }

    public static void saveFirebaseToken(String newFirebaseToken) {
        if (!HpDataManager.getInstance().checkFirebaseToken(newFirebaseToken)) {
            HpDataManager.getInstance().saveFirebaseToken(newFirebaseToken);
            HpDataManager.getInstance().registerFcmTokenToServer(newFirebaseToken, new HpDefaultDataView<HpCommonResponse>() {});
        }
    }

    public static void saveAppInfo(int clientAppIcon, String clientAppName) {
        HomingPigeon.clientAppIcon = clientAppIcon;
        HomingPigeon.clientAppName = clientAppName;
    }

    public static int getClientAppIcon() {
        return clientAppIcon;
    }

    public static String getClientAppName() {
        return clientAppName;
    }

    //Builder buat setting isi dari Notification chat
    public static class NotificationBuilder {
        public Context context;
        public String chatSender = "", chatMessage = "";
        public HpMessageModel notificationMessage;
        public int smallIcon;
        boolean isNeedReply;
        HpRoomModel roomModel;
        NotificationCompat.Builder notificationBuilder;
        private Class aClass;

        public NotificationBuilder(Context context) {
            this.context = context;
        }

        public NotificationBuilder setNotificationMessage(HpMessageModel notificationMessage) {
            this.notificationMessage = notificationMessage;
            HpNotificationManager.getInstance().addNotifMessageToMap(notificationMessage);
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
            this.notificationBuilder = HpNotificationManager.getInstance().createNotificationBubble(this);
            addReply();
            if (null != roomModel && null != aClass) addPendingIntentWhenClicked();
            return this.notificationBuilder.build();
        }

        private void addReply() {
            if (isNeedReply && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteInput remoteInput = new RemoteInput.Builder(K_TEXT_REPLY)
                        .setLabel("Reply").build();
                Intent intent = new Intent(context, HpReplyBroadcastReceiver.class);
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
            NotificationManager notificationManager = (NotificationManager) HomingPigeon.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null == notificationManager.getNotificationChannel(HP_NOTIFICATION_CHANNEL)) {
                NotificationChannel notificationChannel = new NotificationChannel(HP_NOTIFICATION_CHANNEL, "Homing Pigeon Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("HomingPigeon Notification");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.parseColor("#2eccad"));
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        public void show() {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HomingPigeon.appContext);
            createNotificationChannel();

            notificationManager.notify(notificationMessage.getRoom().getRoomID(), 0, build());

            if (1 < HpNotificationManager.getInstance().getNotifMessagesMap().size()) {
                notificationManager.notify(0, HpNotificationManager.getInstance().createSummaryNotificationBubble(context, aClass).build());
            }

        }
    }

    public static Context appContext;
}
