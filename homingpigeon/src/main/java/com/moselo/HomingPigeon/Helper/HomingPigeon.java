package com.moselo.HomingPigeon.Helper;

import android.content.Context;

public class HomingPigeon {
    public static HomingPigeon homingPigeon;

    public static HomingPigeon init(Context context){
        if (null == homingPigeon)
            homingPigeon = new HomingPigeon(context);

        return homingPigeon;
    }

    public HomingPigeon(Context appContext) {
        this.appContext = appContext;
    }

    public static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }
}
