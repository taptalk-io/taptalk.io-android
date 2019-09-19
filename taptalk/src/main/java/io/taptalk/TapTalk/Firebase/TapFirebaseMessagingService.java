package io.taptalk.TapTalk.Firebase;

import android.support.annotation.Keep;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPDataManager;

@Keep
public class TapFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = TapFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (TapTalk.isTapTalkNotification(remoteMessage)) {
            TapTalk.handleTapTalkPushNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if (!TAPDataManager.getInstance().checkFirebaseToken(s)) {
            TAPDataManager.getInstance().saveFirebaseToken(s);
        }
    }
}
