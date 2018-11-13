package com.moselo.TapTalk.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.moselo.HomingPigeon.Manager.TAPChatManager;
import com.moselo.HomingPigeon.Manager.TAPNotificationManager;

public class TapTalkEndAppService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        TAPNotificationManager.getInstance().saveNotificationMessageMapToPreference();
        TAPChatManager.getInstance().saveIncomingMessageAndDisconnect();
        TAPChatManager.getInstance().deleteActiveRoom();
        stopSelf();
    }
}
