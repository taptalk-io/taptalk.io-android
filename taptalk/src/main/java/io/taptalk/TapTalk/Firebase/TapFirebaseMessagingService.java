package io.taptalk.TapTalk.Firebase;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPDataManager;

@Keep
public class TapFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = TapFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (TapTalk.isTapTalkNotification(remoteMessage)) {
            // TODO: 27 Mar 2020 IDENTIFY OWNER INSTANCE, REMOVE LOOP
            for (String instanceKey : TapTalk.getInstanceKeys()) {
                TapTalk.handleTapTalkPushNotification(instanceKey, remoteMessage);

                if (TapTalk.isLoggingEnabled) {
                    Log.d(TAG, "onMessageReceived: " + TAPUtils.toJsonString(remoteMessage));
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        for (String instanceKey : TapTalk.getInstanceKeys()) {
            TapTalk.saveFirebaseToken(instanceKey, s);
        }
    }
}
