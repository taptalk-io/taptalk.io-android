package com.moselo.HomingPigeon.Helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;
import com.moselo.HomingPigeon.View.Activity.SampleLoginActivity;
import com.moselo.HomingPigeon.View.Activity.SampleRoomListActivity;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.DatabaseType.MESSAGE_DB;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;
    private Thread.UncaughtExceptionHandler defaultUEH;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            ChatManager.getInstance().saveIncomingMessageAndDisconnect();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public static HomingPigeon init(Context context) {
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context)) : homingPigeon;
    }

    public HomingPigeon(final Context appContext) {
        DataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        HomingPigeon.appContext = appContext;
        if (DataManager.getInstance().checkActiveUser(appContext))
            ConnectionManager.getInstance().connect();

        DataManager.getInstance().updateSendingMessageToFailed();

        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                appContext.startService(new Intent(HomingPigeon.appContext, HomingPigeonService.class));
                NetworkStateManager.getInstance().registerCallback(HomingPigeon.appContext);
                ChatManager.getInstance().triggerSaveNewMessage();
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
                isForeground = true;
            }

            @Override
            public void onAppGotoBackground() {
                NetworkStateManager.getInstance().unregisterCallback(HomingPigeon.appContext);
                ChatManager.getInstance().updateMessageWhenEnterBackground();
                isForeground = false;
            }
        });
    }

    public static void checkActiveUserToShowPage(Activity activity) {
        if (null != activity) {
            Intent intent;
            if (DataManager.getInstance().checkActiveUser(activity)) {
                intent = new Intent(activity, SampleRoomListActivity.class);
            } else {
                intent = new Intent(activity, SampleLoginActivity.class);
            }
            activity.startActivity(intent);
            activity.finish();
        } else {
            throw new NullPointerException("The Activity that passed was null");
        }
    }

    public static Context appContext;
}
