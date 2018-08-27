package com.moselo.SampleApps;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.moselo.HomingPigeon.Helper.HomingPigeon;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HomingPigeon.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
