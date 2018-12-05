package io.moselo.SampleApps;

import android.app.Application;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapTalkListener;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    TapTalkListener tapTalkListener = new TapTalkListener() {
        @Override
        public void onRefreshTokenExpiredOrInvalid() {
            TapTalk.refreshTokenExpired();
        }

        @Override
        public void onLoginSuccess(TAPUserModel myUserModel) {
            Toast.makeText(getApplicationContext(), "LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, tapTalkListener);
        TapTalk.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        TapTalk.addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
