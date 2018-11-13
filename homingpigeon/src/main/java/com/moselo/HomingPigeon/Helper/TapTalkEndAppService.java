package com.moselo.HomingPigeon.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpNotificationManager;

public class TapTalkEndAppService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        HpNotificationManager.getInstance().saveNotificationMessageMapToPreference();
        HpChatManager.getInstance().saveIncomingMessageAndDisconnect();
        HpChatManager.getInstance().deleteActiveRoom();
        stopSelf();
    }
}
