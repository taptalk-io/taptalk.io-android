package com.moselo.HomingPigeon.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpRoomModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Notification.K_TEXT_REPLY;

public class HpReplyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String text = getMessageText(intent).toString();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        HpChatManager.getInstance().sendDirectReplyTextMessage(text, new HpRoomModel("3-4", "Kevin Reynaldo",
                1, HpImageURL.BuilderDummy(), "#2eccad"));
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(K_TEXT_REPLY);
        }
        return "";
    }
}
