package com.moselo.SampleApps;

import android.app.Application;

import com.moselo.HomingPigeon.Helper.HomingPigeon;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HomingPigeon.init(this);
    }
}
