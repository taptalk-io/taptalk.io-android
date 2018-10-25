package com.moselo.HomingPigeon.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Model.HpRoomModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Notification.K_TEXT_REPLY;

public class HpReplyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String text = getMessageText(intent).toString();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        HpChatManager.getInstance().sendDirectReplyTextMessage(text, HpRoomModel.BuilderDummy());
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(K_TEXT_REPLY);
        }
        return "";
    }
}
