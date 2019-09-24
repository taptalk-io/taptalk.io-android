package io.moselo.SampleApps;

import android.app.Application;
import android.content.Intent;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
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
        public void onTapTalkChatRoomProfileButtonTapped(Activity activity, TAPUserModel user) {
            super.onTapTalkChatRoomProfileButtonTapped(activity, user);
        }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, "YOUR_APP_KEY_ID",
                "YOUR_APP_KEY_SECRET",
                R.drawable.ic_taptalk_logo, "TapTalk Dev Sample App", "YOUR_APP_BASE_URL",
                TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI,
                tapListener);
        TapTalk.initializeGooglePlacesApiKey("YOUR_GOOGLE_PLACES_API_KEY");

    }
}