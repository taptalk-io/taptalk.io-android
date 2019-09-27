package io.moselo.SampleApps;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Listener.TapUIListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TaptalkSample.BuildConfig;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    private static final String TAG = "SampleApplication";

    TapListener tapListener = new TapListener() {
        @Override
        public void onTapTalkRefreshTokenExpired() {
            Intent intent = new Intent(getApplicationContext(), TAPLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTaptalkNotification(message);
        }
    };

    TapUIListener tapUIListener = new TapUIListener() {
        @Override
        public void onTapTalkUserProfileButtonTapped(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
            super.onTapTalkUserProfileButtonTapped(activity, room, user);
        }

        @Override
        public void onTapTalkGroupChatProfileButtonTapped(Activity activity, TAPRoomModel room) {
            super.onTapTalkGroupChatProfileButtonTapped(activity, room);
        }

        @Override
        public void onTapTalkMessageQuoteTapped(Activity activity, TAPMessageModel message, HashMap<String, Object> userInfo) {
            super.onTapTalkMessageQuoteTapped(activity, message, userInfo);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if ("dev".equals(BuildConfig.BUILD_TYPE)) {
            // Dev
            TapTalk.init(this, "d1e5dfe23d1e00bf54bc2316f",
                    "NTQzMTBjZDI5YWNjNTEuMS4x/ZDY4MTg3Yjg/OTA0MTQwNDFhMDYw/MGI0YjA5NTJjM2Fh",
                    //R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name),
                    R.drawable.ic_taptalk_logo, "TapTalk Dev Sample App", "https://engine-dev.taptalk.io/api",
                    TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine,
                    tapListener);
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        } else if ("staging".equals(BuildConfig.BUILD_TYPE)) {
            // Staging
            TapTalk.init(this, "b43b48745dfa0e44k1",
                    "MzI5XzEuMV/9hcHBfa2V5X2lkX2FuZD/oxNTM2OTk3ODc3MjI0NzI4",
                    R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name), "https://engine-stg.taptalk.io/api",
                    TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI,
                    tapListener);
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        } else {
            // Production
            TapTalk.init(this, "d1e5dfe23d1e00bf54bc2316f",
                    "NTQzMTBjZDI5YWNjNTEuMS4x/ZDY4MTg3Yjg/OTA0MTQwNDFhMDYw/MGI0YjA5NTJjM2Fh",
                    R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name), "https://engine.taptalk.io/api",
                    TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI,
                    tapListener);
        }
        TapTalk.initializeGooglePlacesApiKey("AIzaSyA1kCb7yq2shvC3BnzriJLcTfzQdmzSnPA"); // TODO: 19 August 2019 REPLACE KEY WITH DUMMY FOR LIBRARY BUILD
        //TapTalk.setTapTalkScreenOrientation(TapTalk.TapTalkScreenOrientation.TapTalkOrientationPortrait); // FIXME: 23 May 2019 SCREEN ORIENTATION FORCED TO PORTRAIT
        TapUI.getInstance().addUIListener(tapUIListener);
        TapUI.getInstance().addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
    }
}
