package com.moselo.SampleApps;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Interface.HomingPigeonTokenInterface;

public class SampleApplication extends Application {

    HomingPigeonTokenInterface hpTokenInterface = HomingPigeon::refreshTokenExpired;

    @Override
    public void onCreate() {
        super.onCreate();
        HomingPigeon.init(this, hpTokenInterface);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
