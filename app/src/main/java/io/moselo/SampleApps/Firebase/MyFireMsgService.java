package io.moselo.SampleApps.Firebase;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.GeneralSecurityException;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TaptalkSample.R;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        TAPNotificationManager.getInstance().updateNotificationMessageMapWhenAppKilled();
        TAPMessageModel notifModel = TAPUtils.getInstance().fromJSON(new TypeReference<TAPMessageModel>() {
        }, remoteMessage.getData().get("body"));
        try {
            TAPNotificationManager.getInstance().createAndShowBackgroundNotification(this, R.mipmap.ic_launcher, TAPMessageModel.BuilderDecrypt(notifModel));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
        TapTalk.saveFirebaseToken(s);
    }
}
