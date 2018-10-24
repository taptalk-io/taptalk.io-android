package com.moselo.HomingPigeon.Manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.Person;

import com.moselo.HomingPigeon.Helper.HomingPigeon;

public class HpNotificationManager {
    private static HpNotificationManager instance;
    private String channelID = "fcm_fallback_notification_channel";

    public static HpNotificationManager getInstance() {
        return null == instance ? (instance = new HpNotificationManager()) : instance;
    }

    public String getChannelID() {
        return channelID;
    }

    //buat create notification
    public NotificationCompat.Builder createNotificationBubble(HomingPigeon.NotificationBuilder builder) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(builder.context, channelID)
                .setSmallIcon(builder.smallIcon)
                .setStyle(new NotificationCompat.MessagingStyle(builder.chatSender)
                .addMessage(builder.chatMessage, System.currentTimeMillis(), builder.chatSender))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return notifBuilder;
    }

}
