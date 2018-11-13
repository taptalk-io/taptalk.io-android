package com.moselo.TapTalk.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import com.moselo.HomingPigeon.Manager.TAPChatManager;
import com.moselo.HomingPigeon.Model.TAPRoomModel;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Notification.K_TEXT_REPLY;

public class TAPReplyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String text = getMessageText(intent).toString();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        TAPChatManager.getInstance().sendDirectReplyTextMessage(text, TAPRoomModel.BuilderDummy());
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(K_TEXT_REPLY);
        }
        return "";
    }
}
