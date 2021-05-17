package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPMessageStatusManager {
    private static final String TAG = TAPMessageStatusManager.class.getSimpleName();
    private static HashMap<String, TAPMessageStatusManager> instances;

    private String instanceKey = "";
    private List<String> readMessageQueue;
    private List<String> messagesMarkedAsRead; // Message IDs already processed as read by API
    private List<TAPMessageModel> deliveredMessageQueue;
    private int readRequestID = 0;
    private int deliveredRequestID = 0;
    private Map<Integer, List<String>> apiReadRequestMap;
    private Map<Integer, List<TAPMessageModel>> apiDeliveredRequestMap;
    private Map<String, Integer> unreadList, unreadMention;

    public static TAPMessageStatusManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPMessageStatusManager instance = new TAPMessageStatusManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPMessageStatusManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPMessageStatusManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public List<String> getReadMessageQueue() {
        return null == readMessageQueue ? readMessageQueue = new ArrayList<>() : readMessageQueue;
    }

    public void addReadMessageQueue(String readMessageId) {
        getReadMessageQueue().add(readMessageId);
    }

    public void addReadMessageQueue(List<String> readMessagesId) {
        getReadMessageQueue().addAll(readMessagesId);
    }

    public void removeReadMessageQueue(List<String> readMessageId) {
        getReadMessageQueue().removeAll(readMessageId);
    }

    public void removeReadMessageQueue(String readMessageId) {
        getReadMessageQueue().remove(readMessageId);
    }

    public void clearReadMessageQueue() {
        getReadMessageQueue().clear();
    }

    public List<String> getMessagesMarkedAsRead() {
        return null == messagesMarkedAsRead ? messagesMarkedAsRead = new ArrayList<>() : messagesMarkedAsRead;
    }

    public List<TAPMessageModel> getDeliveredMessageQueue() {
        return null == deliveredMessageQueue ? deliveredMessageQueue = new ArrayList<>() : deliveredMessageQueue;
    }

    public void addDeliveredMessageQueue(TAPMessageModel deliveredMessage) {
        getDeliveredMessageQueue().add(deliveredMessage);
    }

    public void removeDeliveredMessageQueue(List<TAPMessageModel> deliveredMessages) {
        getDeliveredMessageQueue().removeAll(deliveredMessages);
    }

    public void clearDeliveredMessageQueue() {
        getDeliveredMessageQueue().clear();
    }

    public Map<Integer, List<String>> getApiReadRequestMap() {
        return null == apiReadRequestMap ? apiReadRequestMap = new LinkedHashMap<>() : apiReadRequestMap;
    }

    public void addApiReadRequestMapItem(int readRequestID, List<String> apiReadRequestMapItem) {
        if (getApiReadRequestMap().containsKey(readRequestID))
            getApiReadRequestMap().get(readRequestID).addAll(apiReadRequestMapItem);
        else
            getApiReadRequestMap().put(readRequestID, apiReadRequestMapItem);
    }

    public void removeApiReadRequestMapItem(int readRequestID) {
        getApiReadRequestMap().remove(readRequestID);
    }

    private Map<Integer, List<TAPMessageModel>> getApiDeliveredRequestMap() {
        return null == apiDeliveredRequestMap ? apiDeliveredRequestMap = new LinkedHashMap<>() : apiDeliveredRequestMap;
    }

    private void addApiDeliveredRequestMapItem(int deliveredRequestID, List<TAPMessageModel> apiDeliveredRequestMapItem) {
        if (getApiDeliveredRequestMap().containsKey(deliveredRequestID))
            getApiDeliveredRequestMap().get(deliveredRequestID).addAll(apiDeliveredRequestMapItem);
        else
            getApiDeliveredRequestMap().put(deliveredRequestID, apiDeliveredRequestMapItem);
    }

    private void removeApiDeliveredRequestMapItem(int deliveredRequestID) {
        getApiDeliveredRequestMap().remove(deliveredRequestID);
    }

    public Map<String, Integer> getUnreadList() {
        return null == unreadList ? unreadList = new LinkedHashMap<>() : unreadList;
    }

    public void addUnreadList(String roomID, int unread) {
        if (getUnreadList().containsKey(roomID)) {
            int tempUnread = getUnreadList().get(roomID) + unread;
            getUnreadList().put(roomID, tempUnread);
        } else {
            getUnreadList().put(roomID, unread);
        }
    }

    public void addUnreadListByOne(String roomID) {
        addUnreadList(roomID, 1);
    }

    public void clearUnreadListPerRoomID(String roomID) {
        getUnreadList().remove(roomID);
    }

    public void clearUnreadList() {
        getUnreadList().clear();
    }

    public Map<String, Integer> getUnreadMention() {
        return null == unreadMention ? unreadMention = new LinkedHashMap<>() : unreadMention;
    }

    public void addUnreadMention(String roomID, int unread) {
        if (getUnreadMention().containsKey(roomID)) {
            int tempUnread = getUnreadMention().get(roomID) + unread;
            getUnreadMention().put(roomID, tempUnread);
        } else {
            getUnreadMention().put(roomID, unread);
        }
    }

    public void addUnreadMentionByOne(String roomID) {
        addUnreadMention(roomID, 1);
    }

    public void clearUnreadMentionPerRoomID(String roomID) {
        getUnreadMention().remove(roomID);
    }

    public void clearUnreadMention() {
        getUnreadMention().clear();
    }

    public void triggerCallMessageStatusApi() {
        new Thread(() -> {
            // Mark delivered
            if (0 < getDeliveredMessageQueue().size()) {
                List<TAPMessageModel> tempMessageDeliveredModel = new ArrayList<>(getDeliveredMessageQueue());
                addApiDeliveredRequestMapItem(deliveredRequestID, tempMessageDeliveredModel);
                updateMessageStatusToDelivered(deliveredRequestID, new ArrayList<>(tempMessageDeliveredModel));
                deliveredRequestID++;
            }

            // Mark read
            if (0 < getReadMessageQueue().size()) {
                List<String> tempMessageReadModelIds = new ArrayList<>(getReadMessageQueue());
                addApiReadRequestMapItem(readRequestID, tempMessageReadModelIds);
                updateMessageStatusToRead(readRequestID, new ArrayList<>(tempMessageReadModelIds));
                readRequestID++;
            }
        }).start();
    }

    public void updateMessageStatusWhenAppToBackground() {
        //updateMessageReadStatusInView();
        if (0 < getDeliveredMessageQueue().size()) {
            updateMessageStatusToDelivered(deliveredRequestID, new ArrayList<>(getDeliveredMessageQueue()));
            //clearDeliveredMessageQueue();
        }
    }

    public void updateMessageStatusToDeliveredFromNotification(TAPMessageModel newMessageModel) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            messageIds.add(newMessageModel.getMessageID());
            TAPDataManager.getInstance(instanceKey).updateMessageStatusAsDelivered(messageIds, new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    public void updateMessageStatusToDelivered(List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            TAPUserModel myUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                if (null != myUser && !model.getUser().getUserID().equals(myUser.getUserID())
                        && null != model.getSending() && !model.getSending()
                        && null != model.getDelivered() && !model.getDelivered()
                        && null != model.getIsRead() && !model.getIsRead())
                    messageIds.add(model.getMessageID());
            }
            TAPDataManager.getInstance(instanceKey).updateMessageStatusAsDelivered(messageIds, new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    private void updateMessageStatusToDelivered(int tempDeliveredRequestID, List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                messageIds.add(model.getMessageID());
            }
            removeDeliveredMessageQueue(newMessageModels);
            TAPDataManager.getInstance(instanceKey).updateMessageStatusAsDelivered(messageIds, new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
                @Override
                public void onSuccess(TAPUpdateMessageStatusResponse response) {
                    if (null != response.getUpdatedMessageIDs()) {
                        new Thread(() -> {
                            if (getApiDeliveredRequestMap().containsKey(tempDeliveredRequestID)) {
                                removeApiDeliveredRequestMapItem(tempDeliveredRequestID);
                            }
                            List<TAPMessageEntity> messageEntities = new ArrayList<>();
                            for (TAPMessageModel messageModel : newMessageModels) {
                                if (null != messageModel) {
                                    messageModel.updateDeliveredMessage();
                                    messageEntities.add(TAPMessageEntity.fromMessageModel(messageModel));
                                }
                            }
                            if (!messageEntities.isEmpty()) {
                                TAPDataManager.getInstance(instanceKey).insertToDatabase(messageEntities, false);
                            }
                        }).start();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    onApiError();
                }

                @Override
                public void onError(TAPErrorModel error) {
                    onApiError();
                }

                private void onApiError() {
                    new Thread(() -> {
                        if (getApiDeliveredRequestMap().containsKey(tempDeliveredRequestID)) {
                            addApiDeliveredRequestMapItem(deliveredRequestID, getApiDeliveredRequestMap().get(tempDeliveredRequestID));
                            removeApiDeliveredRequestMapItem(tempDeliveredRequestID);
                        }
                    }).start();
                }
            });
        }).start();
    }

    private void updateMessageStatusToRead(int tempReadRequestID, List<String> newMessageIds) {
        new Thread(() -> {
            getMessagesMarkedAsRead().addAll(newMessageIds);
            removeReadMessageQueue(newMessageIds);
            TAPDataManager.getInstance(instanceKey).updateMessageStatusAsRead(newMessageIds, new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
                @Override
                public void onSuccess(TAPUpdateMessageStatusResponse response) {
                    if (null != response.getUpdatedMessageIDs()) {
                        new Thread(() -> {
                            if (getApiReadRequestMap().containsKey(tempReadRequestID)) {
                                removeApiDeliveredRequestMapItem(tempReadRequestID);
                            }
                            TAPDataManager.getInstance(instanceKey).updateMessagesAsReadInDatabase(newMessageIds);
                        }).start();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    onApiError();
                }

                @Override
                public void onError(TAPErrorModel error) {
                    onApiError();
                }

                private void onApiError() {
                    new Thread(() -> {
                        if (getApiReadRequestMap().containsKey(tempReadRequestID)) {
                            addApiReadRequestMapItem(readRequestID, getApiReadRequestMap().get(tempReadRequestID));
                            removeApiReadRequestMapItem(tempReadRequestID);
                        }
                    }).start();
                }
            });
        }).start();
    }

    public void resetMessageStatusManager() {
        clearReadMessageQueue();
        clearDeliveredMessageQueue();
        readRequestID = 0;
        deliveredRequestID = 0;
        getApiDeliveredRequestMap().clear();
        getApiReadRequestMap().clear();
        clearUnreadList();
        clearUnreadMention();
    }
}
