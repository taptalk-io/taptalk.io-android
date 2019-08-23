package io.taptalk.TapTalk.Manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapTalkListener;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.View.Activity.TAPChatActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_REPLY_REQ_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.K_TEXT_REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.NEW_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.NOTIFICATION_CHANNEL_DEFAULT_DESCRIPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.NOTIFICATION_CHANNEL_DEFAULT_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.NOTIFICATION_GROUP_DEFAULT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Notification.TAP_NOTIFICATION_CHANNEL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Helper.TapTalk.getTapTalkListeners;

public class TAPNotificationManager {
    private static final String TAG = TAPNotificationManager.class.getSimpleName();
    private static TAPNotificationManager instance;
    private Map<String, List<TAPMessageModel>> notificationMessagesMap;
    private boolean isRoomListAppear;


    public static TAPNotificationManager getInstance() {
        return null == instance ? (instance = new TAPNotificationManager()) : instance;
    }

    public Map<String, List<TAPMessageModel>> getNotificationMessagesMap() {
        return null == notificationMessagesMap ? notificationMessagesMap = new LinkedHashMap<>() : notificationMessagesMap;
    }

    public void setNotificationMessagesMap(Map<String, List<TAPMessageModel>> notificationMessagesMap) {
        this.notificationMessagesMap = notificationMessagesMap;
    }

    public boolean isRoomListAppear() {
        return isRoomListAppear;
    }

    public void setRoomListAppear(boolean roomListAppear) {
        isRoomListAppear = roomListAppear;
    }

    public void addNotificationMessageToMap(TAPMessageModel notificationMessage) {
        String messageRoomID = notificationMessage.getRoom().getRoomID();
        if (checkMapContainsRoomID(messageRoomID) && TYPE_SYSTEM_MESSAGE == notificationMessage.getType()) {
            notificationMessage.setBody(TAPChatManager.getInstance().formattingSystemMessage(notificationMessage));
            getNotificationMessagesMap().get(messageRoomID).add(notificationMessage);
        } else if (checkMapContainsRoomID(messageRoomID)) {
            getNotificationMessagesMap().get(messageRoomID).add(notificationMessage);
        } else if (TYPE_SYSTEM_MESSAGE == notificationMessage.getType()) {
            notificationMessage.setBody(TAPChatManager.getInstance().formattingSystemMessage(notificationMessage));
            List<TAPMessageModel> listNotificationMessagePerRoomID = new ArrayList<>();
            listNotificationMessagePerRoomID.add(notificationMessage);
            getNotificationMessagesMap().put(messageRoomID, listNotificationMessagePerRoomID);
        } else {
            List<TAPMessageModel> listNotificationMessagePerRoomID = new ArrayList<>();
            listNotificationMessagePerRoomID.add(notificationMessage);
            getNotificationMessagesMap().put(messageRoomID, listNotificationMessagePerRoomID);
        }
    }

    public void removeNotificationMessagesMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            getNotificationMessagesMap().remove(roomID);
        }
    }

    public void clearNotificationMessagesMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            getNotificationMessagesMap().get(roomID).clear();
        }
    }

    public void clearAllNotificationMessageMap() {
        getNotificationMessagesMap().clear();
    }

    public boolean checkMapContainsRoomID(String roomID) {
        return getNotificationMessagesMap().containsKey(roomID);
    }

    public List<TAPMessageModel> getListOfMessageFromMap(String roomID) {
        if (checkMapContainsRoomID(roomID)) {
            return getNotificationMessagesMap().get(roomID);
        } else {
            return new ArrayList<>();
        }
    }

    public NotificationCompat.Builder createSummaryNotificationBubble(Context context, Class aClass) {
        int chatSize = 0, messageSize = 0;
        for (Map.Entry<String, List<TAPMessageModel>> item : notificationMessagesMap.entrySet()) {
            chatSize++;
            messageSize += item.getValue().size();
        }
        String summaryContent = messageSize + " messages from " + chatSize + " chats";

        return new NotificationCompat.Builder(context, TAP_NOTIFICATION_CHANNEL)
                .setSmallIcon(TapTalk.getClientAppIcon())
                .setContentTitle(TapTalk.getClientAppName())
                .setContentText(summaryContent)
                .setStyle(new NotificationCompat.InboxStyle()/*.setSummaryText(summaryContent)*/)
                .setGroup(NOTIFICATION_GROUP_DEFAULT)
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
    public NotificationCompat.Builder createNotificationBubble(NotificationBuilder builder) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.MessagingStyle messageStyle = new NotificationCompat.MessagingStyle(builder.chatSender);
        String notificationMessageRoomID = builder.notificationMessage.getRoom().getRoomID();
        String chatSender = builder.chatSender;
        List<TAPMessageModel> tempNotificationListMessage = getListOfMessageFromMap(notificationMessageRoomID);
        int tempNotificationListMessageSize = tempNotificationListMessage.size();

        if (checkMapContainsRoomID(notificationMessageRoomID)) {
            switch (tempNotificationListMessageSize) {
                case 0:
                    messageStyle.addMessage(NEW_MESSAGE, System.currentTimeMillis(), chatSender);
                    break;
                case 1:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(0).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(0).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                    TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                            TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getRoomType() ?
                                                                    tempNotificationListMessage.get(0).getUser().getName() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                                    tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getRoomName());
                    break;
                case 2:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(0).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(0).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(0).getUser().getName() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                            tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(1).getType() ? tempNotificationListMessage.get(1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(1).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(1).getUser().getName() + ": " + tempNotificationListMessage.get(1).getBody() :
                                                            tempNotificationListMessage.get(1).getBody(),
                            tempNotificationListMessage.get(1).getCreated(),
                            tempNotificationListMessage.get(1).getRoom().getRoomName());
                    break;
                case 3:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(0).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(0).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(0).getUser().getName() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                            tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(1).getType() ? tempNotificationListMessage.get(1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(1).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(1).getUser().getName() + ": " + tempNotificationListMessage.get(1).getBody() :
                                                            tempNotificationListMessage.get(1).getBody(),
                            tempNotificationListMessage.get(1).getCreated(),
                            tempNotificationListMessage.get(1).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(2).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(2).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(2).getType() ? tempNotificationListMessage.get(2).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(2).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(2).getUser().getName() + ": " + tempNotificationListMessage.get(2).getBody() :
                                                            tempNotificationListMessage.get(2).getBody(),
                            tempNotificationListMessage.get(2).getCreated(),
                            tempNotificationListMessage.get(2).getRoom().getRoomName());
                    break;
                default:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getUser().getName() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getUser().getName() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getUser().getName() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom().getRoomName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom().getRoomType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getUser().getName() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom().getRoomName());
                    break;
            }
        }

        return new NotificationCompat.Builder(builder.context, TAP_NOTIFICATION_CHANNEL)
                .setContentTitle(builder.chatSender)
                .setContentText(builder.chatMessage)
                .setSmallIcon(builder.smallIcon)
                .setStyle(messageStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setGroup(NOTIFICATION_GROUP_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    public void createAndShowInAppNotification(Context context, TAPMessageModel newMessageModel) {
        if (TapTalk.implementationType == TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI) {
            if (TapTalk.isForeground) {
                new NotificationBuilder(context)
                        .setNotificationMessage(newMessageModel)
                        .setSmallIcon(TapTalk.getClientAppIcon())
                        .setNeedReply(false)
                        .setOnClickAction(TAPChatActivity.class)
                        .show();
            }
        } else {
            for (TapTalkListener listener : getTapTalkListeners()) {
                listener.onNotificationReceived(newMessageModel);
            }
        }
    }

    public void createAndShowBackgroundNotification(Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        TAPContactManager.getInstance().loadAllUserDataFromDatabase();
        TAPContactManager.getInstance().saveUserDataToDatabase(newMessageModel.getUser());
        TAPMessageStatusManager.getInstance().updateMessageStatusToDeliveredFromNotification(newMessageModel);
        TAPContactManager.getInstance().updateUserData(newMessageModel.getUser());
        if (TapTalk.implementationType == TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI) {
            if (!TapTalk.isForeground || (null != TAPChatManager.getInstance().getActiveRoom()
                    && !TAPChatManager.getInstance().getActiveRoom().getRoomID().equals(newMessageModel.getRoom().getRoomID()))) {
                new NotificationBuilder(context)
                        .setNotificationMessage(newMessageModel)
                        .setSmallIcon(notificationIcon)
                        .setNeedReply(false)
                        .setOnClickAction(destinationClass)
                        .show();
            }
        } else {
            for (TapTalkListener listener : getTapTalkListeners()) {
                listener.onNotificationReceived(newMessageModel);
            }
        }
    }

    public void cancelNotificationWhenEnterRoom(Context context, String roomID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(roomID, 0);
        TAPNotificationManager.getInstance().removeNotificationMessagesMap(roomID);

        if (0 == TAPNotificationManager.getInstance().getNotificationMessagesMap().size()) {
            notificationManager.cancel(0);
        }
    }

    public void saveNotificationMessageMapToPreference() {
        if (0 < getNotificationMessagesMap().size()) {
            TAPDataManager.getInstance().saveNotificationMessageMap(TAPUtils.getInstance().toJsonString(getNotificationMessagesMap()));
        }
    }

    public void updateNotificationMessageMapWhenAppKilled() {
        if (TAPDataManager.getInstance().checkNotificationMap() && 0 == getNotificationMessagesMap().size()) {
            Map<String, List<TAPMessageModel>> tempNotifMessage = TAPUtils.getInstance().fromJSON(
                    new TypeReference<Map<String, List<TAPMessageModel>>>() {
                    },
                    TAPDataManager.getInstance().getNotificationMessageMap());
            setNotificationMessagesMap(tempNotifMessage);
            TAPDataManager.getInstance().clearNotificationMessageMap();
        }
    }

    public void updateUnreadCount() {
        new Thread(() -> TAPDataManager.getInstance().getUnreadCount(new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onCountedUnreadCount(int unreadCount) {
                for (TapTalkListener listener : TapTalk.getTapTalkListeners()) {
                    listener.onTapTalkUnreadChatRoomBadgeCountUpdated(unreadCount);
                }
            }
        })).start();
    }

    public class NotificationBuilder {
        public Context context;
        public String chatSender = "", chatMessage = "";
        public TAPMessageModel notificationMessage;
        public int smallIcon;
        boolean isNeedReply;
        TAPRoomModel roomModel;
        NotificationCompat.Builder notificationBuilder;
        private Class aClass;

        public NotificationBuilder(Context context) {
            this.context = context;
        }

        public NotificationBuilder setNotificationMessage(TAPMessageModel notificationMessage) {
            this.notificationMessage = notificationMessage;
            TAPNotificationManager.getInstance().addNotificationMessageToMap(notificationMessage);
            if (null != notificationMessage &&
                    null != notificationMessage.getRoom() && null != notificationMessage.getUser() &&
                    TYPE_GROUP == notificationMessage.getRoom().getRoomType()) {
                //Log.e(TAG, "setNotificationMessage: " + TAPUtils.getInstance().toJsonString(notificationMessage));
                setChatMessage(notificationMessage.getUser().getName() + ": " + notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getRoomName());
            } else if (null != notificationMessage) {
                //Log.e(TAG, "setNotificationMessage:2 " + TAPUtils.getInstance().toJsonString(notificationMessage));
                setChatMessage(notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getRoomName());
            }
            return this;
        }

        private void setChatSender(String chatSender) {
            this.chatSender = chatSender;
        }

        private void setChatMessage(String chatMessage) {
            this.chatMessage = chatMessage;
        }

        public NotificationBuilder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public NotificationBuilder setNeedReply(boolean needReply) {
            isNeedReply = needReply;
            return this;
        }

        public NotificationBuilder setOnClickAction(Class aClass) {
            this.roomModel = notificationMessage.getRoom();
            this.aClass = aClass;
            return this;
        }

        public Notification build() {
            this.notificationBuilder = TAPNotificationManager.getInstance().createNotificationBubble(this);
            addReply();
            if (null != roomModel && null != aClass) addPendingIntentWhenClicked();
            return this.notificationBuilder.build();
        }

        private void addReply() {
            if (isNeedReply && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteInput remoteInput = new RemoteInput.Builder(K_TEXT_REPLY)
                        .setLabel(REPLY).build();
                Intent intent = new Intent(context, TAPReplyBroadcastReceiver.class);
                intent.setAction(K_TEXT_REPLY);
                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, K_REPLY_REQ_CODE,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action = new NotificationCompat.Action.Builder(smallIcon,
                        REPLY, replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                notificationBuilder.addAction(action);
            }
        }

        private void addPendingIntentWhenClicked() {
            Intent intent = new Intent(context, aClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ROOM, roomModel);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        private void createNotificationChannel() {
            NotificationManager notificationManager = (NotificationManager) TapTalk.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null == notificationManager.getNotificationChannel(TAP_NOTIFICATION_CHANNEL)) {
                NotificationChannel notificationChannel = new NotificationChannel(TAP_NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL_DEFAULT_NAME, NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel
                notificationChannel.setDescription(NOTIFICATION_CHANNEL_DEFAULT_DESCRIPTION);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(context.getColor(R.color.tapColorPrimary));
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        public void show() {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(TapTalk.appContext);
            createNotificationChannel();

            notificationManager.notify(notificationMessage.getRoom().getRoomID(), 0, build());

            if (1 < TAPNotificationManager.getInstance().getNotificationMessagesMap().size()) {
                notificationManager.notify(0, TAPNotificationManager.getInstance().createSummaryNotificationBubble(context, aClass).build());
            }

        }
    }
}
