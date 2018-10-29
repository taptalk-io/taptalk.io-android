package com.moselo.HomingPigeon.Manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpNotificationManager {
    private static HpNotificationManager instance;
    private String channelID = "fcm_fallback_notification_channel";
    private Map<String, List<HpMessageModel>> notifMessagesMap;
    private boolean isRoomListAppear;

    public static HpNotificationManager getInstance() {
        return null == instance ? (instance = new HpNotificationManager()) : instance;
    }

    public String getChannelID() {
        return channelID;
    }

    public Map<String, List<HpMessageModel>> getNotifMessagesMap() {
        return null == notifMessagesMap ? notifMessagesMap = new LinkedHashMap<>() : notifMessagesMap;
    }

    public boolean isRoomListAppear() {
        return isRoomListAppear;
    }

    public void setRoomListAppear(boolean roomListAppear) {
        isRoomListAppear = roomListAppear;
    }

    public void addNotifMessageToMap(HpMessageModel notifMessage) {
        String messageRoomID = notifMessage.getRoom().getRoomID();
        if (checkMapContainsRoomID(messageRoomID)) {
            getNotifMessagesMap().get(messageRoomID).add(notifMessage);
        } else {
            List<HpMessageModel> listNotifMessagePerRoomID = new ArrayList<>();
            listNotifMessagePerRoomID.add(notifMessage);
            getNotifMessagesMap().put(messageRoomID, listNotifMessagePerRoomID);
        }
    }

    public void clearNotifMessagesMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            getNotifMessagesMap().get(roomID).clear();
        }
    }

    public boolean checkMapContainsRoomID(String roomID) {
        return getNotifMessagesMap().containsKey(roomID);
    }

    public List<HpMessageModel> getListOfMessageFromMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            return getNotifMessagesMap().get(roomID);
        } else {
            return new ArrayList<>();
        }
    }

    //buat create notification when the apps is in background
    public NotificationCompat.Builder createNotificationBubbleInBackground(HomingPigeon.NotificationBuilder builder) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.MessagingStyle messageStyle = new NotificationCompat.MessagingStyle(builder.chatSender);
        String notifMessageRoomID = builder.notificationMessage.getRoom().getRoomID();
        String chatSender = builder.chatSender;
        List<HpMessageModel> tempNotifListMessage = getListOfMessageFromMap(notifMessageRoomID);
        int tempNotifListMessageSize = tempNotifListMessage.size();

        if (checkMapContainsRoomID(notifMessageRoomID)) {
            switch (tempNotifListMessageSize) {
                case 0:
                    messageStyle.addMessage("New Message", System.currentTimeMillis(), chatSender);
                    break;
                case 1:
                    messageStyle.addMessage(tempNotifListMessage.get(0).getBody(),
                            tempNotifListMessage.get(0).getCreated(),
                            tempNotifListMessage.get(0).getUser().getName());
                    break;
                case 2:
                    messageStyle.addMessage(tempNotifListMessage.get(0).getBody(),
                            tempNotifListMessage.get(0).getCreated(),
                            tempNotifListMessage.get(0).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(1).getBody(),
                            tempNotifListMessage.get(1).getCreated(),
                            tempNotifListMessage.get(1).getUser().getName());
                    break;
                case 3:
                    messageStyle.addMessage(tempNotifListMessage.get(0).getBody(),
                            tempNotifListMessage.get(0).getCreated(),
                            tempNotifListMessage.get(0).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(1).getBody(),
                            tempNotifListMessage.get(1).getCreated(),
                            tempNotifListMessage.get(1).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(2).getBody(),
                            tempNotifListMessage.get(2).getCreated(),
                            tempNotifListMessage.get(2).getUser().getName());
                    break;
                default:
                    messageStyle.addMessage(tempNotifListMessage.get(tempNotifListMessageSize - 4).getBody(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 4).getCreated(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 4).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(tempNotifListMessageSize - 3).getBody(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 3).getCreated(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 3).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(tempNotifListMessageSize - 2).getBody(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 2).getCreated(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 2).getUser().getName());
                    messageStyle.addMessage(tempNotifListMessage.get(tempNotifListMessageSize - 1).getBody(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 1).getCreated(),
                            tempNotifListMessage.get(tempNotifListMessageSize - 1).getUser().getName());
                    break;
            }
        }

        return new NotificationCompat.Builder(builder.context, channelID)
                .setSmallIcon(builder.smallIcon)
                .setStyle(messageStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_MESSAGE);
    }

    public void createAndShowInAppNotification(Context context, HpMessageModel newMessageModel) {
        if (HomingPigeon.isForeground)
            new HomingPigeon.NotificationBuilder(context)
                    .setNotificationMessage(newMessageModel)
                    .setSmallIcon(HomingPigeon.getClientAppIcon())
                    .setNeedReply(false)
                    .setOnClickAction(HpRoomListActivity.class)
                    .show();
    }

    public void cancelNotificationWhenEnterRoom(Context context, String roomID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(roomID, 0);
    }

}
