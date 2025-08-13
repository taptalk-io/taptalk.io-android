package io.taptalk.TapTalk.Manager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.BroadcastReceiver.TAPReplyBroadcastReceiver;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Helper.TapTalk.getTapTalkListeners;

public class TAPNotificationManager {
    private static final String TAG = TAPNotificationManager.class.getSimpleName();
    private static HashMap<String, TAPNotificationManager> instances;
    private static Map<String, List<TAPMessageModel>> notificationMessagesMap;
    private String instanceKey = "";
    private boolean isRoomListAppear;

    public TAPNotificationManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TAPNotificationManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPNotificationManager instance = new TAPNotificationManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPNotificationManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
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
            notificationMessage.setBody(TAPChatManager.getInstance(instanceKey).formattingSystemMessage(notificationMessage));
            getNotificationMessagesMap().get(messageRoomID).add(notificationMessage);
        } else if (checkMapContainsRoomID(messageRoomID)) {
            getNotificationMessagesMap().get(messageRoomID).add(notificationMessage);
        } else if (TYPE_SYSTEM_MESSAGE == notificationMessage.getType()) {
            notificationMessage.setBody(TAPChatManager.getInstance(instanceKey).formattingSystemMessage(notificationMessage));
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
                .setSmallIcon(TapTalk.getClientAppIcon(instanceKey))
                .setContentTitle(TapTalk.getClientAppName(instanceKey))
                .setContentText(summaryContent)
                .setStyle(new NotificationCompat.InboxStyle())
                .setGroup(NOTIFICATION_GROUP_DEFAULT)
                .setGroupSummary(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setContentIntent(addPendingIntentForSummaryNotification(context, TapTalk.getGroupNotificationPendingIntentClass()))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent addPendingIntentForSummaryNotification(Context context, Class aClass) {
        Intent intent = new Intent(context, aClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
        }
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
                                                            TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getType() ?
                                                                    tempNotificationListMessage.get(0).getUser().getFullname() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                                    tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getName());
                    break;
                case 2:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(0).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(0).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getType() ?
                                                            tempNotificationListMessage.get(0).getUser().getFullname() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                            tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(1).getType() ? tempNotificationListMessage.get(1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(1).getRoom().getType() ?
                                                            tempNotificationListMessage.get(1).getUser().getFullname() + ": " + tempNotificationListMessage.get(1).getBody() :
                                                            tempNotificationListMessage.get(1).getBody(),
                            tempNotificationListMessage.get(1).getCreated(),
                            tempNotificationListMessage.get(1).getRoom().getName());
                    break;
                case 3:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(0).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(0).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(0).getType() ? tempNotificationListMessage.get(0).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(0).getRoom().getType() ?
                                                            tempNotificationListMessage.get(0).getUser().getFullname() + ": " + tempNotificationListMessage.get(0).getBody() :
                                                            tempNotificationListMessage.get(0).getBody(),
                            tempNotificationListMessage.get(0).getCreated(),
                            tempNotificationListMessage.get(0).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(1).getType() ? tempNotificationListMessage.get(1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(1).getRoom().getType() ?
                                                            tempNotificationListMessage.get(1).getUser().getFullname() + ": " + tempNotificationListMessage.get(1).getBody() :
                                                            tempNotificationListMessage.get(1).getBody(),
                            tempNotificationListMessage.get(1).getCreated(),
                            tempNotificationListMessage.get(1).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(2).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(2).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(2).getType() ? tempNotificationListMessage.get(2).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(2).getRoom().getType() ?
                                                            tempNotificationListMessage.get(2).getUser().getFullname() + ": " + tempNotificationListMessage.get(2).getBody() :
                                                            tempNotificationListMessage.get(2).getBody(),
                            tempNotificationListMessage.get(2).getCreated(),
                            tempNotificationListMessage.get(2).getRoom().getName());
                    break;
                default:
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom().getType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getUser().getFullname() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 4).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom().getType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getUser().getFullname() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 3).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom().getType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getUser().getFullname() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 2).getRoom().getName());
                    messageStyle.addMessage(null == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getUser() ? NEW_MESSAGE :
                                    null == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom() ? NEW_MESSAGE :
                                            TYPE_SYSTEM_MESSAGE == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getType() ? tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody() :
                                                    TYPE_GROUP == tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom().getType() ?
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getUser().getFullname() + ": " + tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody() :
                                                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getBody(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getCreated(),
                            tempNotificationListMessage.get(tempNotificationListMessageSize - 1).getRoom().getName());
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
        TapTalk instance = TapTalk.getTapTalkInstance(instanceKey);
        if (instance != null && instance.implementationType == TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI) {
            if (TapTalk.isForeground) {
                new NotificationBuilder(context, instanceKey)
                        .setNotificationMessage(newMessageModel)
                        .setSmallIcon(TapTalk.getClientAppIcon(instanceKey))
                        .setNeedReply(false)
                        .setOnClickAction(TapUIChatActivity.class)
                        .show();
            }
        }
        else {
            List<TapListener> listeners = getTapTalkListeners(instanceKey);
            if (listeners != null && !listeners.isEmpty()) {
                for (TapListener listener : listeners) {
                    listener.onNotificationReceived(newMessageModel);
                }
            }
        }
    }

    public void createAndShowBackgroundNotification(Context context, int notificationIcon, Class destinationClass, TAPMessageModel newMessageModel) {
        TAPContactManager.getInstance(instanceKey).loadAllUserDataFromDatabase();
        TAPContactManager.getInstance(instanceKey).saveUserDataToDatabase(newMessageModel.getUser());
        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDeliveredFromNotification(newMessageModel);
        if (newMessageModel.getUser() != TAPChatManager.getInstance(instanceKey).getActiveUser() ||
                UPDATE_USER.equals(newMessageModel.getAction())) {
            TAPContactManager.getInstance(instanceKey).updateUserData(newMessageModel.getUser());
        }
        TapTalk instance = TapTalk.getTapTalkInstance(instanceKey);
        if (instance != null && instance.implementationType == TapTalk.TapTalkImplementationType.TapTalkImplementationTypeUI) {
            if (!TapTalk.isForeground || (null != TAPChatManager.getInstance(instanceKey).getActiveRoom()
                    && !TAPChatManager.getInstance(instanceKey).getActiveRoom().getRoomID().equals(newMessageModel.getRoom().getRoomID()))) {
                new NotificationBuilder(context, instanceKey)
                        .setNotificationMessage(newMessageModel)
                        .setSmallIcon(TapTalk.getClientAppIcon(instanceKey))
                        .setNeedReply(false)
                        .setOnClickAction(destinationClass)
                        .show();
            }
        }
        else {
            List<TapListener> listeners = getTapTalkListeners(instanceKey);
            if (listeners != null && !listeners.isEmpty()) {
                for (TapListener listener : listeners) {
                    listener.onNotificationReceived(newMessageModel);
                }
            }
        }
    }

    public void cancelNotificationWhenEnterRoom(Context context, String roomID) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(roomID, 0);
        TAPNotificationManager.getInstance(instanceKey).removeNotificationMessagesMap(roomID);

        if (0 == TAPNotificationManager.getInstance(instanceKey).getNotificationMessagesMap().size()) {
            notificationManager.cancel(0);
        }
    }

    public void saveNotificationMessageMapToPreference() {
        if (0 < getNotificationMessagesMap().size()) {
            TAPDataManager.getInstance(instanceKey).saveNotificationMessageMap(TAPUtils.toJsonString(getNotificationMessagesMap()));
        }
    }

    public void updateNotificationMessageMapWhenAppKilled() {
        if (TAPDataManager.getInstance(instanceKey).checkNotificationMap() && 0 == getNotificationMessagesMap().size()) {
            Map<String, List<TAPMessageModel>> tempNotifMessage = TAPUtils.fromJSON(
                    new TypeReference<Map<String, List<TAPMessageModel>>>() {
                    },
                    TAPDataManager.getInstance(instanceKey).getNotificationMessageMap());
            setNotificationMessagesMap(tempNotifMessage);
            TAPDataManager.getInstance(instanceKey).removeNotificationMap();
        }
    }

    public void updateUnreadCount() {
        new Thread(() -> TAPDataManager.getInstance(instanceKey).getUnreadCount(new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onCountedUnreadCount(int unreadCount) {
                List<TapListener> listeners = getTapTalkListeners(instanceKey);
                if (listeners != null && !listeners.isEmpty()) {
                    for (TapListener listener : listeners) {
                        listener.onTapTalkUnreadChatRoomBadgeCountUpdated(unreadCount);
                    }
                }
            }
        })).start();
    }

    public static class NotificationBuilder {
        public Context context;
        private String instanceKey = "";
        public String chatSender = "";
        public String chatMessage = "";
        public TAPMessageModel notificationMessage;
        public int smallIcon;
        boolean isNeedReply;
        TAPRoomModel roomModel;
        NotificationCompat.Builder notificationBuilder;
        private Class aClass;

        public NotificationBuilder(Context context, String instanceKey) {
            this.context = context;
            this.instanceKey = instanceKey;
        }

        public NotificationBuilder setNotificationMessage(TAPMessageModel notificationMessage) {
            this.notificationMessage = notificationMessage;
            TAPNotificationManager.getInstance(instanceKey).addNotificationMessageToMap(notificationMessage);
            if (null != notificationMessage &&
                    null != notificationMessage.getRoom() && null != notificationMessage.getUser() &&
                    TYPE_GROUP == notificationMessage.getRoom().getType()) {
                setChatMessage(notificationMessage.getUser().getFullname() + ": " + notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getName());
            } else if (null != notificationMessage) {
                setChatMessage(notificationMessage.getBody());
                setChatSender(notificationMessage.getRoom().getName());
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
            int messageSize = 0;
            for (Map.Entry<String, List<TAPMessageModel>> item : notificationMessagesMap.entrySet()) {
                if (notificationMessage.getRoom().getRoomID().equals(item.getKey())) {
                    messageSize = item.getValue().size();
                }
            }
            this.notificationBuilder = TAPNotificationManager.getInstance(instanceKey).createNotificationBubble(this);
            addReply();
            if (null != roomModel && null != aClass) addPendingIntentWhenClicked();
            this.notificationBuilder.setNumber(messageSize);
            return this.notificationBuilder.build();
        }

        private void addReply() {
            if (isNeedReply && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteInput remoteInput = new RemoteInput.Builder(K_TEXT_REPLY)
                        .setLabel(REPLY).build();
                Intent intent = new Intent(context, TAPReplyBroadcastReceiver.class);
                intent.setAction(K_TEXT_REPLY);
                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, K_REPLY_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action = new NotificationCompat.Action.Builder(smallIcon,
                        REPLY, replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                notificationBuilder.addAction(action);
            }
        }

        private void addPendingIntentWhenClicked() {
//            Intent intent = new Intent(context, aClass);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(ROOM, roomModel);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
//            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setContentIntent(TapUIChatActivity.generatePendingIntent(context, instanceKey, roomModel));
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

            Map<String, List<TAPMessageModel>> notificationMap = TAPNotificationManager.getInstance(instanceKey).getNotificationMessagesMap();
            List<TAPMessageModel> currentRoomNotificationList = notificationMap.get(notificationMessage.getRoom().getRoomID());

            // Check message state to remove notification instead of show
            if ((notificationMessage.getIsRead() != null && notificationMessage.getIsRead()) ||
                (notificationMessage.getIsDeleted() != null && notificationMessage.getIsDeleted()) ||
                (notificationMessage.getIsHidden() != null && notificationMessage.getIsHidden())
            ) {
                if (currentRoomNotificationList != null && currentRoomNotificationList.size() > 0) {
                    for (TAPMessageModel message : new ArrayList<>(currentRoomNotificationList)) {
                        if (notificationMessage.getMessageID() != null &&
                                notificationMessage.getMessageID().equals(message.getMessageID())
                        ) {
                            // Remove notification message from the list
                            currentRoomNotificationList.remove(message);
                            currentRoomNotificationList.remove(notificationMessage);
                            if (currentRoomNotificationList.isEmpty()) {
                                // Remove list from notification map
                                notificationMap.remove(notificationMessage.getRoom().getRoomID());
                            }
                            break;
                        }
                    }
                }
            }

            if (currentRoomNotificationList != null && !currentRoomNotificationList.isEmpty()) {
                // Show notification
                notificationManager.notify(notificationMessage.getRoom().getRoomID(), 0, build());
            } else {
                // Remove notification
                notificationManager.cancel(notificationMessage.getRoom().getRoomID(), 0);
            }

            if (notificationMap.size() > 1) {
                // Show grouped notification
                notificationManager.notify(0, TAPNotificationManager.getInstance(instanceKey).createSummaryNotificationBubble(context, TapUIRoomListActivity.class).build());
            } else if (notificationMap.isEmpty()) {
                // Remove grouped notification
                notificationManager.cancel(0);
            }
        }
    }
}
