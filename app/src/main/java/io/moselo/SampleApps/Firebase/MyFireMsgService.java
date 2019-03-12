package io.moselo.SampleApps.Firebase;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;
import io.taptalk.TaptalkSample.R;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        TAPNotificationManager.getInstance().updateNotificationMessageMapWhenAppKilled();
        HashMap<String, Object> notificationMap = TAPUtils.getInstance().fromJSON(new TypeReference<HashMap<String, Object>>() {
        }, remoteMessage.getData().get("body"));
        try {
            //Log.e(TAG, "onMessageReceived: " + TAPUtils.getInstance().toJsonString(remoteMessage));
            TAPNotificationManager.getInstance().createAndShowBackgroundNotification(this, R.mipmap.ic_launcher,
                    TAPRoomListActivity.class,
                    TAPEncryptorManager.getInstance().decryptMessage(notificationMap));
        } catch (Exception e) {
            Log.e(TAG, "onMessageReceived: ",e );
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        TapTalk.saveFirebaseToken(s);
    }
}
