package io.moselo.SampleApps;

import android.app.Application;

import com.facebook.stetho.Stetho;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkTokenInterface;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    TapTalkTokenInterface hpTokenInterface = TapTalk::refreshTokenExpired;

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, hpTokenInterface);
        TapTalk.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
