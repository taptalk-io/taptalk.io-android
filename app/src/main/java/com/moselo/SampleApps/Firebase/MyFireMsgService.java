package com.moselo.SampleApps.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.Sample.R;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;

public class MyFireMsgService extends FirebaseMessagingService {
    private static final String TAG = MyFireMsgService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived: " );
        new HomingPigeon.NotificationBuilder(this)
                .setNotificationMessage(HpMessageModel.Builder(
                        "TEST", HpRoomModel.BuilderDummy(), 1, System.currentTimeMillis(),
                        HpUserModel.Builder("4", "Kevin Reynaldo"), HpDataManager.getInstance().getActiveUser().getUserID()
                ))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setNeedReply(false)
                .setOnClickAction(HpRoomListActivity.class)
                .show();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
        HomingPigeon.saveFirebaseToken(s);
    }
}
