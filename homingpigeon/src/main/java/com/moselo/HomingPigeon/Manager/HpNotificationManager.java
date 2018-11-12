package com.moselo.HomingPigeon.Manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.moselo.HomingPigeon.Const.HpDefaultConstant.HP_NOTIFICATION_CHANNEL;

public class HpNotificationManager {
    private static final String TAG = HpNotificationManager.class.getSimpleName();
    private static HpNotificationManager instance;
    private String notificationGroup = "homing-pigeon";
    private Map<String, List<HpMessageModel>> notifMessagesMap;
    private boolean isRoomListAppear;

    public static HpNotificationManager getInstance() {
        return null == instance ? (instance = new HpNotificationManager()) : instance;
    }

    public Map<String, List<HpMessageModel>> getNotifMessagesMap() {
        return null == notifMessagesMap ? notifMessagesMap = new LinkedHashMap<>() : notifMessagesMap;
    }

    public void setNotifMessagesMap(Map<String, List<HpMessageModel>> notifMessagesMap) {
        this.notifMessagesMap = notifMessagesMap;
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

    public NotificationCompat.Builder createSummaryNotificationBubble(Context context, Class aClass) {
        int chatSize = 0, messageSize = 0;
        for (Map.Entry<String, List<HpMessageModel>> item : notifMessagesMap.entrySet()) {
            chatSize++;
            messageSize += item.getValue().size();
        }
        String summaryContent = messageSize + " messages from " + chatSize + " chats";

        return new NotificationCompat.Builder(context, HP_NOTIFICATION_CHANNEL)
                .setSmallIcon(HomingPigeon.getClientAppIcon())
                .setContentTitle(HomingPigeon.getClientAppName())
                .setContentText(summaryContent)
                .setStyle(new NotificationCompat.InboxStyle()/*.setSummaryText(summaryContent)*/)
                .setGroup(notificationGroup)
                .setGroupSummary(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setContentIntent(addPendingIntentForSummaryNotification(context, aClass))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    private PendingIntent addPendingIntentForSummaryNotification(Context context, Class aClass) {
        Intent intent = new Intent(context, aClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
    }

    //buat create notification when the apps is in background
    public NotificationCompat.Builder createNotificationBubble(HomingPigeon.NotificationBuilder builder) {
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

        return new NotificationCompat.Builder(builder.context, HP_NOTIFICATION_CHANNEL)
                .setContentTitle(builder.chatSender)
                .setContentText(builder.chatMessage)
                .setSmallIcon(builder.smallIcon)
                .setStyle(messageStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setGroup(notificationGroup)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    public void createAndShowInAppNotification(Context context, HpMessageModel newMessageModel) {
        if (HomingPigeon.isForeground) {
            new HomingPigeon.NotificationBuilder(context)
                    .setNotificationMessage(newMessageModel)
                    .setSmallIcon(HomingPigeon.getClientAppIcon())
                    .setNeedReply(false)
                    .setOnClickAction(HpRoomListActivity.class)
                    .show();
        }
    }

    public void createAndShowBackgroundNotification(Context context, int notificationIcon, HpMessageModel newMessageModel) {
        HpDataManager.getInstance().insertToDatabase(HpChatManager.getInstance().convertToEntity(newMessageModel));
        new HomingPigeon.NotificationBuilder(context)
                .setNotificationMessage(newMessageModel)
                .setSmallIcon(notificationIcon)
                .setNeedReply(false)
                .setOnClickAction(HpRoomListActivity.class)
                .show();
    }

    public void cancelNotificationWhenEnterRoom(Context context, String roomID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(roomID, 0);
    }

    public void saveNotificationMessageMapToPreference() {
        if (0 < getNotifMessagesMap().size()) {
            HpDataManager.getInstance().saveNotificationMessageMap(HpUtils.getInstance().toJsonString(getNotifMessagesMap()));
        }
    }

    public void updateNotificationMessageMapWhenAppKilled() {
        if (HpDataManager.getInstance().checkNotificationMap() && 0 == getNotifMessagesMap().size()) {
            Map<String, List<HpMessageModel>> tempNotifMessage = HpUtils.getInstance().fromJSON(
                    new TypeReference<Map<String, List<HpMessageModel>>>() {
                    },
                    HpDataManager.getInstance().getNotificationMessageMap());
            setNotifMessagesMap(tempNotifMessage);
            HpDataManager.getInstance().clearNotificationMessageMap();
        }
    }

}
