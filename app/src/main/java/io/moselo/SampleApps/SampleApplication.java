package io.moselo.SampleApps;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapTalkListener;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    TapTalkListener tapTalkListener = new TapTalkListener() {
        @Override
        public void onRefreshTokenExpiredOrInvalid() {
            TapTalk.refreshTokenExpired();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, tapTalkListener);
        TapTalk.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        Log.e("rioriorio", "onCreate: "+R.layout.sample_cell_chat_order_card );
        TapTalk.addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
