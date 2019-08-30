package io.moselo.SampleApps;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TapUI;
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
        TapUI.getInstance().addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
    }
}
