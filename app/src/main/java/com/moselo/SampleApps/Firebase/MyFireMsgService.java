package com.moselo.SampleApps.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Sample.R;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived: " );
        new HomingPigeon.NotificationBuilder(this)
                .setChatMessage("ISdalkdsfjljdflkqjwflkqwjfkleqwjfklqwejfklewflkwnfklnweefklenwfklenwkflnweqklfnweqklfnweklfnwqeklfnlkwefnklewqfnklewfnlkewfnlekwfnelwkfnewklI")
                .setChatSender("Welly Kencana")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .show();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
        HomingPigeon.saveFirebaseToken(s);
    }
}
