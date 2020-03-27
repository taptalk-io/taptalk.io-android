package io.moselo.SampleApps;

import android.app.Activity;
import android.content.Intent;

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
    public static final String INSTANCE_KEY = "TAPTALK-DEV";

    TapListener tapListener = new TapListener(INSTANCE_KEY) { // TODO: 027, 27 Mar 2020 INSTANCE KEY CONSTRUCTOR
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
            TapTalk.showTapTalkNotification(INSTANCE_KEY, message); // TODO: 027, 27 Mar 2020 INSTANCE KEY PARAM
        }

        @Override
        public void onUserLogout() {
            Intent intent = new Intent(getApplicationContext(), TAPLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {
            TapUIRoomListActivity.start(activity, INSTANCE_KEY, null, true);
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
                INSTANCE_KEY,
                this,
                TAPTALK_SDK_APP_KEY_ID,
                TAPTALK_SDK_APP_KEY_SECRET,
                R.drawable.ic_taptalk_logo,
                getString(R.string.app_name),
                TAPTALK_SDK_BASE_URL,
                TapTalkImplementationTypeCombine,
                tapListener);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
        TapUI.getInstance().setLogoutButtonVisible(true);
    }
}
