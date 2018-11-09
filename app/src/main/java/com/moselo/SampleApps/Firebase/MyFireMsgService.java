package com.moselo.SampleApps.Firebase;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpNotificationManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Sample.R;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;

import java.security.GeneralSecurityException;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        HpMessageModel notifModel = HpUtils.getInstance().fromJSON(new TypeReference<HpMessageModel>() {}, remoteMessage.getData().get("body"));
        try {
            HpNotificationManager.getInstance().createAndShowBackgroundNotification(this, R.mipmap.ic_launcher, HpMessageModel.BuilderDecrypt(notifModel));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
        HomingPigeon.saveFirebaseToken(s);
    }
}
