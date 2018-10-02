package com.moselo.HomingPigeon.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;

public class HomingPigeonEndAppService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        ChatManager.getInstance().saveIncomingMessageAndDisconnect();
        ChatManager.getInstance().deleteActiveRoom();
        stopSelf();
    }
}
