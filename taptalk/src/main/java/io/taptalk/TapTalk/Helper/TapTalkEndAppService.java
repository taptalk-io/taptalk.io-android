package io.taptalk.TapTalk.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;

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
