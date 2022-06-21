package io.moselo.SampleApps;

import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.TapTalkSample.BuildConfig.GCP_ANALYTICS_KEY;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_APP_KEY_ID;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_APP_KEY_SECRET;
import static io.taptalk.TapTalkSample.BuildConfig.TAPTALK_SDK_BASE_URL;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import io.moselo.SampleApps.Activity.TAPLoginActivity;
import io.moselo.SampleApps.Activity.TapDeleteAccountActivity;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Listener.TapUIMyAccountListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalkSample.BuildConfig;
import io.taptalk.TapTalkSample.R;

public class SampleApplication extends MultiDexApplication {

    private static final String TAG = "SampleApplication";
    public static final String INSTANCE_KEY = "";
    public static final String INSTANCE_KEY_DEV = "TAPTALK-DEV";
    public static final String INSTANCE_KEY_STAGING = "TAPTALK-STAGING";

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.setLoggingEnabled(true);
        TapTalk.initializeAnalyticsForSampleApps(GCP_ANALYTICS_KEY);
        TapTalk.init(
                this,
                TAPTALK_SDK_APP_KEY_ID,
                TAPTALK_SDK_APP_KEY_SECRET,
                R.drawable.ic_taptalk_logo,
                getString(R.string.app_name),
                TAPTALK_SDK_BASE_URL,
                TapTalkImplementationTypeCombine,
                tapListener);
        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        TapUI.getInstance(INSTANCE_KEY).setLogoutButtonVisible(true);
        TapUI.getInstance(INSTANCE_KEY).setConnectionStatusIndicatorVisible(false);
        TapUI.getInstance(INSTANCE_KEY).setDeleteAccountButtonVisible(true);
        TapUI.getInstance(INSTANCE_KEY).addMyAccountListener(new TapUIMyAccountListener() {
            @Override
            public void onDeleteButtonInMyAccountPageTapped(@NonNull Activity activity) {
                super.onDeleteButtonInMyAccountPageTapped(activity);
                TapDeleteAccountActivity.Companion.start(activity, INSTANCE_KEY);
            }
        });

        if (BuildConfig.DEBUG) {
            TapUI.getInstance(INSTANCE_KEY).setCloseButtonInRoomListVisible(true);
        }
    }

    TapListener tapListener = new TapListener(INSTANCE_KEY) {
        @Override
        public void onTapTalkRefreshTokenExpired() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY);
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTapTalkNotification(INSTANCE_KEY, message);
        }

        @Override
        public void onUserLogout() {
            TAPLoginActivity.start(getApplicationContext(), INSTANCE_KEY);
        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {
            TapUIRoomListActivity.start(activity, INSTANCE_KEY, null, true);
        }
    };
}
