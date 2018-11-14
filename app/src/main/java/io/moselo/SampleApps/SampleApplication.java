package io.moselo.SampleApps;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Interface.HomingPigeonTokenInterface;

import io.taptalk.Taptalk.Sample.R;

public class SampleApplication extends Application {

    HomingPigeonTokenInterface hpTokenInterface = HomingPigeon::refreshTokenExpired;

    @Override
    public void onCreate() {
        super.onCreate();
        HomingPigeon.init(this, hpTokenInterface);
        HomingPigeon.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
