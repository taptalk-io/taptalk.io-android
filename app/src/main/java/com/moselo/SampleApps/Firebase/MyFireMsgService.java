package com.moselo.SampleApps.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moselo.HomingPigeon.Helper.HomingPigeon;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: "+s );
        HomingPigeon.saveFirebaseToken(s);
    }
}
