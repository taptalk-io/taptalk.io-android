package io.taptalk.TapTalk.Firebase;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPDataManager;

@Keep
public class TapFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = TapFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO: 27 Mar 2020 IDENTIFY OWNER INSTANCE
        if (TapTalk.isTapTalkNotification(remoteMessage)) {
            TapTalk.handleTapTalkPushNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        for (String instanceKey : TapTalk.getInstanceKeys()) {
            if (!TAPDataManager.getInstance(instanceKey).checkFirebaseToken(s)) {
                TAPDataManager.getInstance(instanceKey).saveFirebaseToken(s);
            }
        }
    }
}
