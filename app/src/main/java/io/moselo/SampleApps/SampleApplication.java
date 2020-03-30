package io.moselo.SampleApps;

import android.app.Activity;

import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import io.moselo.SampleApps.Activity.TAPLoginActivity;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalkSample.BuildConfig;
import io.taptalk.TapTalkSample.R;

import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_APP_KEY_ID;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_APP_KEY_SECRET;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_BASE_URL;

public class SampleApplication extends MultiDexApplication {

    private static final String TAG = "SampleApplication";
    public static final String INSTANCE_KEY_DEV = "TAPTALK-DEV";
    public static final String INSTANCE_KEY_STAGING = "TAPTALK-STAGING";

    TapListener tapListenerDev = new TapListener(INSTANCE_KEY_DEV) {
        @Override
        public void onTapTalkRefreshTokenExpired() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY_DEV);
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTapTalkNotification(INSTANCE_KEY_DEV, message);
        }

        @Override
        public void onUserLogout() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY_DEV);
        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {
            TapUIRoomListActivity.start(activity, INSTANCE_KEY_DEV, null, true);
        }
    };

    TapListener tapListenerStaging = new TapListener(INSTANCE_KEY_STAGING) {
        @Override
        public void onTapTalkRefreshTokenExpired() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY_STAGING);
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTapTalkNotification(INSTANCE_KEY_STAGING, message);
        }

        @Override
        public void onUserLogout() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY_STAGING);
        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {
            TapUIRoomListActivity.start(activity, INSTANCE_KEY_STAGING, null, true);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.setLoggingEnabled(true);
        if (BuildConfig.BUILD_TYPE.equals("release")) {
            TapTalk.initializeAnalyticsForSampleApps("b476744eb06c9b3285d19dca3d7781c7");
        } else if (BuildConfig.BUILD_TYPE.equals("staging")) {
            TapTalk.initializeAnalyticsForSampleApps("1b400091d6ab3e08584cadffd57a7a40");
        } else {
            TapTalk.initializeAnalyticsForSampleApps("84f4d93bf3c34abe56fac7b2faaaa8b1");
        }
//        TapTalk.init(
//                this,
//                TAPTALK_SDK_APP_KEY_ID,
//                TAPTALK_SDK_APP_KEY_SECRET,
//                R.drawable.ic_taptalk_logo,
//                getString(R.string.app_name),
//                TAPTALK_SDK_BASE_URL,
//                TapTalkImplementationTypeCombine,
//                tapListener);
//        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
        TapTalk.initNewInstance(
                INSTANCE_KEY_DEV,
                this,
                TAPTALK_SDK_APP_KEY_ID,
                TAPTALK_SDK_APP_KEY_SECRET,
                R.drawable.ic_taptalk_logo,
                getString(R.string.app_name),
                TAPTALK_SDK_BASE_URL,
                TapTalkImplementationTypeCombine,
                tapListenerDev);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        TapTalk.initNewInstance(
                INSTANCE_KEY_STAGING,
                this,
                "b43b48745dfa0e44k1",
                "MzI5XzEuMV/9hcHBfa2V5X2lkX2FuZD/oxNTM2OTk3ODc3MjI0NzI4",
                R.drawable.ic_taptalk_logo,
                getString(R.string.app_name),
                "https://engine-stg.taptalk.io",
                TapTalkImplementationTypeCombine,
                tapListenerStaging);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
        TapUI.getInstance(INSTANCE_KEY_DEV).setConnectionStatusIndicatorVisible(true);
        TapUI.getInstance(INSTANCE_KEY_DEV).setLogoutButtonVisible(true);
        TapUI.getInstance(INSTANCE_KEY_STAGING).setConnectionStatusIndicatorVisible(true);
        TapUI.getInstance(INSTANCE_KEY_STAGING).setLogoutButtonVisible(true);
    }
}
