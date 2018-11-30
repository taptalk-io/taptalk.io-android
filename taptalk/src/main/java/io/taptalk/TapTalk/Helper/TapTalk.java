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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPCustomKeyboardManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
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
    private TapTalkInterface tapTalkInterface;

    private List<TAPCustomKeyboardItemModel> customKeyboardItemModels;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            TAPChatManager.getInstance().saveIncomingMessageAndDisconnect();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public static TapTalk init(Context context, TapTalkInterface tapTalkInterface) {
        return tapTalk == null ? (tapTalk = new TapTalk(context, tapTalkInterface)) : tapTalk;
    }

    public TapTalk(final Context appContext, TapTalkInterface tapTalkInterface) {
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

        this.tapTalkInterface = tapTalkInterface;

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
            }
        });

        TAPCustomKeyboardManager.getInstance().addCustomKeyboardListener(tapTalkInterface);

        // TODO: 30 November 2018 DUMMY CUSTOM KEYBOARD ITEMS
        TAPCustomKeyboardItemModel seePriceList = new TAPCustomKeyboardItemModel("1", appContext.getDrawable(R.drawable.tap_ic_star_yellow), "See price list");
        TAPCustomKeyboardItemModel readExpertNotes = new TAPCustomKeyboardItemModel("2", appContext.getDrawable(R.drawable.tap_ic_search_grey), "Read expert's notes");
        TAPCustomKeyboardItemModel sendServices = new TAPCustomKeyboardItemModel("3", appContext.getDrawable(R.drawable.tap_ic_gallery_green_blue), "Send services");
        TAPCustomKeyboardItemModel createOrderCard = new TAPCustomKeyboardItemModel("4", appContext.getDrawable(R.drawable.tap_ic_documents_green_blue), "Create order card");
        List<TAPCustomKeyboardItemModel> userToExpert = new ArrayList<>();
        List<TAPCustomKeyboardItemModel> expertToUser = new ArrayList<>();
        List<TAPCustomKeyboardItemModel> expertToExpert = new ArrayList<>();
        userToExpert.add(seePriceList);
        userToExpert.add(readExpertNotes);
        expertToUser.add(sendServices);
        expertToUser.add(createOrderCard);
        expertToExpert.add(seePriceList);
        expertToExpert.add(readExpertNotes);
        expertToExpert.add(sendServices);
        expertToExpert.add(createOrderCard);
        expertToExpert.add(seePriceList);
        expertToExpert.add(readExpertNotes);
        expertToExpert.add(sendServices);
        expertToExpert.add(createOrderCard);
        TAPCustomKeyboardManager.getInstance().addCustomKeyboardGroup("1", "2", userToExpert);
        TAPCustomKeyboardManager.getInstance().addCustomKeyboardGroup("2", "1", expertToUser);
        TAPCustomKeyboardManager.getInstance().addCustomKeyboardGroup("2", "2", expertToExpert);
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
}
