package com.moselo.HomingPigeon.Helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.DatabaseType.MESSAGE_DB;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;

    public static HomingPigeon init(Context context) {
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context)) : homingPigeon;
    }

    public HomingPigeon(Context appContext) {
        DataManager.getInstance().initDatabaseManager(MESSAGE_DB, (Application) appContext);
        HomingPigeon.appContext = appContext;
        ConnectionManager.getInstance().connect();
        appContext.startService(new Intent(HomingPigeon.appContext, HomingPigeonService.class));
        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                NetworkStateManager.getInstance().registerCallback(HomingPigeon.appContext);
                isForeground = true;
            }

            @Override
            public void onAppGotoBackground() {
                ConnectionManager.getInstance().close();
                NetworkStateManager.getInstance().unregisterCallback(HomingPigeon.appContext);
                ChatManager.getInstance().updateMessageWhenEnterBackground();
                isForeground = false;
            }
        });
    }

    public static Context appContext;
}
