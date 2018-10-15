package com.moselo.HomingPigeon.Helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.stetho.Stetho;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpNetworkStateManager;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetAccessTokenResponse;
import com.moselo.HomingPigeon.View.Activity.HpLoginActivity;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;
import com.orhanobut.hawk.Hawk;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.MESSAGE_DB;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.DatabaseType.SEARCH_DB;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;
    private Thread.UncaughtExceptionHandler defaultUEH;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            HpChatManager.getInstance().saveIncomingMessageAndDisconnect();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public static HomingPigeon init(Context context) {
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context)) : homingPigeon;
    }

    public HomingPigeon(final Context appContext) {
        //init Hawk for Preference
        Hawk.init(appContext).build();
        HpDataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        HpDataManager.getInstance().initDatabaseManager(SEARCH_DB, (Application) appContext);
        HomingPigeon.appContext = appContext;
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

    public void saveAuthTicketAndGetAccessToken(String authTicket, HpDefaultDataView<HpGetAccessTokenResponse> view) {
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

    public static Context appContext;
}
