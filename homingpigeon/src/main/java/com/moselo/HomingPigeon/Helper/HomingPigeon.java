package com.moselo.HomingPigeon.Helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;

import java.util.Timer;
import java.util.TimerTask;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.DatabaseType.MESSAGE_DB;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;
    private Thread.UncaughtExceptionHandler defaultUEH;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            ChatManager.getInstance().disconnectSocket();
            ChatManager.getInstance().insertPendingArrayAndUpdateMessage();
            defaultUEH.uncaughtException(thread, throwable);
        }
    };

    public static HomingPigeon init(Context context) {
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context)) : homingPigeon;
    }

    public HomingPigeon(final Context appContext) {
        DataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        HomingPigeon.appContext = appContext;
        ConnectionManager.getInstance().connect();
        DataManager.getInstance().updatePendingStatus();

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
                Log.e("riocv", "onAppGotoBackground: " );
                ConnectionManager.getInstance().close();
                NetworkStateManager.getInstance().unregisterCallback(HomingPigeon.appContext);
                ChatManager.getInstance().updateMessageWhenEnterBackground();
                isForeground = false;
            }
        });
    }

    public static Context appContext;
}
