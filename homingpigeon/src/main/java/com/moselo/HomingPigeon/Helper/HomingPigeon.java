package com.moselo.HomingPigeon.Helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;

    public static HomingPigeon init(Context context){
        return homingPigeon == null ? (homingPigeon = new HomingPigeon(context)) : homingPigeon;
    }

    public HomingPigeon(Context appContext) {
        HomingPigeon.appContext = appContext;
        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
//                ConnectionManager.getInstance().connect();
                NetworkStateManager.getInstance().registerCallback(HomingPigeon.appContext);
                isForeground = true;
            }

            @Override
            public void onAppGotoBackground() {
                ConnectionManager.getInstance().close();
                NetworkStateManager.getInstance().unregisterCallback(HomingPigeon.appContext);
                isForeground = false;
            }
        });
    }

    public static Context appContext;
}
