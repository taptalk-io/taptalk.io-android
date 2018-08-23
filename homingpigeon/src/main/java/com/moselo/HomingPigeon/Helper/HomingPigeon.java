package com.moselo.HomingPigeon.Helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.moselo.HomingPigeon.Manager.ConnectionManager;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;
    public static boolean isForeground = true;

    public static HomingPigeon init(Context context){
        if (null == homingPigeon)
            homingPigeon = new HomingPigeon(context);

        return homingPigeon;
    }

    public HomingPigeon(Context appContext) {
        HomingPigeon.appContext = appContext;
        AppVisibilityDetector.init((Application) appContext, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                Log.e(HomingPigeon.class.getSimpleName(), "onAppGotoForeground: connect" );
                ConnectionManager.getInstance().connect();
                isForeground = true;
            }

            @Override
            public void onAppGotoBackground() {
                Log.e(HomingPigeon.class.getSimpleName(), "onAppGotoForeground: disconnect" );
                ConnectionManager.getInstance().close();
                isForeground = false;
            }
        });
    }

    public static Context appContext;
}
