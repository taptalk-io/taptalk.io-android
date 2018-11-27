package io.taptalk.TapTalk.Manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TAP_NOTIFICATION_CHANNEL;

public class TAPNotificationManager {
    private static final String TAG = TAPNotificationManager.class.getSimpleName();
    private static TAPNotificationManager instance;
    private String notificationGroup = "homing-pigeon";
    private Map<String, List<TAPMessageModel>> notifMessagesMap;
    private boolean isRoomListAppear;

    public static TAPNotificationManager getInstance() {
        return null == instance ? (instance = new TAPNotificationManager()) : instance;
    }

    public Map<String, List<TAPMessageModel>> getNotifMessagesMap() {
        return null == notifMessagesMap ? notifMessagesMap = new LinkedHashMap<>() : notifMessagesMap;
    }

    public void setNotifMessagesMap(Map<String, List<TAPMessageModel>> notifMessagesMap) {
        this.notifMessagesMap = notifMessagesMap;
    }

    public boolean isRoomListAppear() {
        return isRoomListAppear;
    }

    public void setRoomListAppear(boolean roomListAppear) {
        isRoomListAppear = roomListAppear;
    }

    public void addNotifMessageToMap(TAPMessageModel notifMessage) {
        String messageRoomID = notifMessage.getRoom().getRoomID();
        if (checkMapContainsRoomID(messageRoomID)) {
            getNotifMessagesMap().get(messageRoomID).add(notifMessage);
        } else {
            List<TAPMessageModel> listNotifMessagePerRoomID = new ArrayList<>();
            listNotifMessagePerRoomID.add(notifMessage);
            getNotifMessagesMap().put(messageRoomID, listNotifMessagePerRoomID);
        }
    }

    public void removeNotifMessagesMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            getNotifMessagesMap().remove(roomID);
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

    public List<TAPMessageModel> getListOfMessageFromMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            return getNotifMessagesMap().get(roomID);
        } else {
            return new ArrayList<>();
        }
    }

    public NotificationCompat.Builder createSummaryNotificationBubble(Context context, Class aClass) {
        int chatSize = 0, messageSize = 0;
        for (Map.Entry<String, List<TAPMessageModel>> item : notifMessagesMap.entrySet()) {
            chatSize++;
            messageSize += item.getValue().size();
        }
        String summaryContent = messageSize + " messages from " + chatSize + " chats";

        return new NotificationCompat.Builder(context, TAP_NOTIFICATION_CHANNEL)
                .setSmallIcon(TapTalk.getClientAppIcon())
                .setContentTitle(TapTalk.getClientAppName())
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
    public NotificationCompat.Builder createNotificationBubble(TapTalk.NotificationBuilder builder) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.MessagingStyle messageStyle = new NotificationCompat.MessagingStyle(builder.chatSender);
        String notifMessageRoomID = builder.notificationMessage.getRoom().getRoomID();
        String chatSender = builder.chatSender;
        List<TAPMessageModel> tempNotifListMessage = getListOfMessageFromMap(notifMessageRoomID);
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

        Log.e(TAG, "createNotificationBubble: " + builder.chatSender);

        return new NotificationCompat.Builder(builder.context, TAP_NOTIFICATION_CHANNEL)
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

    public void createAndShowInAppNotification(Context context, TAPMessageModel newMessageModel) {
        Log.e(TAG, "createAndShowInAppNotification: " + newMessageModel.getBody());
        if (TapTalk.isForeground) {
            new TapTalk.NotificationBuilder(context)
                    .setNotificationMessage(newMessageModel)
                    .setSmallIcon(TapTalk.getClientAppIcon())
                    .setNeedReply(false)
                    .setOnClickAction(TAPRoomListActivity.class)
                    .show();
        }
    }

    public void createAndShowBackgroundNotification(Context context, int notificationIcon, TAPMessageModel newMessageModel) {
        TAPDataManager.getInstance().insertToDatabase(TAPChatManager.getInstance().convertToEntity(newMessageModel));
        TAPMessageStatusManager.getInstance().updateMessageStatusToDeliveredFromNotification(newMessageModel);

        if (!TapTalk.isForeground || (null != TAPChatManager.getInstance().getActiveRoom()
                && !TAPChatManager.getInstance().getActiveRoom().getRoomID().equals(newMessageModel.getRoom().getRoomID()))) {
            new TapTalk.NotificationBuilder(context)
                    .setNotificationMessage(newMessageModel)
                    .setSmallIcon(notificationIcon)
                    .setNeedReply(false)
                    .setOnClickAction(TAPRoomListActivity.class)
                    .show();
        }
    }

    public void cancelNotificationWhenEnterRoom(Context context, String roomID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(roomID, 0);
        TAPNotificationManager.getInstance().removeNotifMessagesMap(roomID);

        if (0 == TAPNotificationManager.getInstance().getNotifMessagesMap().size()) {
            notificationManager.cancel(0);
        }
    }

    public void saveNotificationMessageMapToPreference() {
        if (0 < getNotifMessagesMap().size()) {
            TAPDataManager.getInstance().saveNotificationMessageMap(TAPUtils.getInstance().toJsonString(getNotifMessagesMap()));
        }
    }

    public void updateNotificationMessageMapWhenAppKilled() {
        if (TAPDataManager.getInstance().checkNotificationMap() && 0 == getNotifMessagesMap().size()) {
            Map<String, List<TAPMessageModel>> tempNotifMessage = TAPUtils.getInstance().fromJSON(
                    new TypeReference<Map<String, List<TAPMessageModel>>>() {
                    },
                    TAPDataManager.getInstance().getNotificationMessageMap());
            setNotifMessagesMap(tempNotifMessage);
            TAPDataManager.getInstance().clearNotificationMessageMap();
        }
    }
}
