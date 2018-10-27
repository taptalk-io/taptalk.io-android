package com.moselo.HomingPigeon.Manager;

import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;

public class HpNotificationManager {
    private static HpNotificationManager instance;
    private String channelID = "fcm_fallback_notification_channel";

    public static HpNotificationManager getInstance() {
        return null == instance ? (instance = new HpNotificationManager()) : instance;
    }

    public String getChannelID() {
        return channelID;
    }

    //buat create notification when the apps is in background
    public NotificationCompat.Builder createNotificationBubbleInBackground(HomingPigeon.NotificationBuilder builder) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(builder.context, channelID)
                .setSmallIcon(builder.smallIcon)
                .setStyle(new NotificationCompat.MessagingStyle(builder.chatSender)
                        .addMessage(builder.chatMessage, System.currentTimeMillis(), builder.chatSender))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_MESSAGE);
    }

    public void createAndShowInAppNotification(Context context, HpMessageModel newMessageModel) {
        new HomingPigeon.NotificationBuilder(context)
                .setChatMessage(newMessageModel.getBody())
                .setChatSender(newMessageModel.getUser().getName())
                .setMessageID(Integer.parseInt(newMessageModel.getMessageID()))
                .setSmallIcon(HomingPigeon.getClientAppIcon())
                .setNeedReply(false)
                .setOnClickAction(newMessageModel.getRoom(), HpRoomListActivity.class)
                .show();
    }

}
