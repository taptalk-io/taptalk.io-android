package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPMessageStatusManager {
    private static final String TAG = TAPMessageStatusManager.class.getSimpleName();
    private static TAPMessageStatusManager instance;
    private List<TAPMessageModel> readMessageQueue;
    private List<TAPMessageModel> deliveredMessageQueue;
    private int readRequestID = 0;
    private int deliveredRequestID = 0;
    private Map<Integer, List<TAPMessageModel>> apiReadRequestMap;
    private Map<Integer, List<TAPMessageModel>> apiDeliveredRequestMap;
    private Map<String, Integer> unreadList;

    public static TAPMessageStatusManager getInstance() {
        return null == instance ? instance = new TAPMessageStatusManager() : instance;
    }

    public List<TAPMessageModel> getReadMessageQueue() {
        return null == readMessageQueue ? readMessageQueue = new ArrayList<>() : readMessageQueue;
    }

    public void addReadMessageQueue(TAPMessageModel readMessage) {
        getReadMessageQueue().add(readMessage);
    }

    public void removeReadMessageQueue(List<TAPMessageModel> readMessages) {
        getReadMessageQueue().removeAll(readMessages);
    }

    public void removeReadMessageQueue(TAPMessageModel readMessage) {
        getReadMessageQueue().remove(readMessage);
    }

    public void clearReadMessageQueue() {
        getReadMessageQueue().clear();
    }

    public List<TAPMessageModel> getDeliveredMessageQueue() {
        return null == deliveredMessageQueue ? deliveredMessageQueue = new ArrayList<>() : deliveredMessageQueue;
    }

    public void addDeliveredMessageQueue(TAPMessageModel deliveredMessage) {
        getDeliveredMessageQueue().add(deliveredMessage);
    }

    public void clearDeliveredMessageQueue() {
        getDeliveredMessageQueue().clear();
    }

    public Map<Integer, List<TAPMessageModel>> getApiReadRequestMap() {
        return null == apiReadRequestMap ? apiReadRequestMap = new LinkedHashMap<>() : apiReadRequestMap;
    }

    public void addApiReadRequestMapItem(int readRequestID, List<TAPMessageModel> apiReadRequestMapItem) {
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

    private void addUnreadList(String roomID, int unread) {
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

    public void triggerCallMessageStatusApi() {
        new Thread(() -> {
            //ini buat delivered
            if (0 < getDeliveredMessageQueue().size()) {
                List<TAPMessageModel> tempMessageDeliveredModel = new ArrayList<>(getDeliveredMessageQueue());
                addApiDeliveredRequestMapItem(deliveredRequestID, tempMessageDeliveredModel);
                deliveredApiCallProcedure(tempMessageDeliveredModel);
                clearDeliveredMessageQueue();
                deliveredRequestID++;
            }

            //ini buat read
            if (0 < getReadMessageQueue().size()) {
                List<TAPMessageModel> tempMessageReadModel = new ArrayList<>(getReadMessageQueue());
                addApiReadRequestMapItem(readRequestID, tempMessageReadModel);
                readApiCallProcedure(tempMessageReadModel);
                clearReadMessageQueue();
                readRequestID++;
            }
        }).start();
    }

    public void updateMessageStatusWhenAppToBackground() {
        //updateMessageReadStatusInView();
        if (0 < getDeliveredMessageQueue().size()) {
            deliveredApiCallProcedure(new ArrayList<>(getDeliveredMessageQueue()));
            clearDeliveredMessageQueue();
        }
    }

    private void deliveredApiCallProcedure(List<TAPMessageModel> tempMessageModel) {
        updateMessageStatusToDelivered(deliveredRequestID, new ArrayList<>(tempMessageModel));
        new Thread(() -> {
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            for (TAPMessageModel messageModel : tempMessageModel) {
                messageModel.updateDeliveredMessage();
                messageEntities.add(TAPChatManager.getInstance().convertToEntity(messageModel));
            }
            TAPDataManager.getInstance().insertToDatabase(messageEntities, false);
        }).start();
    }

    private void readApiCallProcedure(List<TAPMessageModel> tempMessageModel) {
        updateMessageStatusToRead(readRequestID, new ArrayList<>(tempMessageModel));
        new Thread(() -> {
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            for (TAPMessageModel messageModel : tempMessageModel) {
                if (null != messageModel) {
                    messageModel.updateReadMessage();
                    messageEntities.add(TAPChatManager.getInstance().convertToEntity(messageModel));
                }
            }
            TAPDataManager.getInstance().insertToDatabase(messageEntities, false);
        }).start();
    }

    public void updateMessageStatusToDeliveredFromNotification(TAPMessageModel newMessageModel) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            messageIds.add(newMessageModel.getMessageID());
            TAPDataManager.getInstance().updateMessageStatusAsDelivered(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    public void updateMessageStatusToDelivered(List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            TAPUserModel myUser = TAPChatManager.getInstance().getActiveUser();
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                if (null != myUser && !model.getUser().getUserID().equals(myUser.getUserID())
                        && null != model.getSending() && !model.getSending()
                        && null != model.getDelivered() && !model.getDelivered()
                        && null != model.getIsRead() && !model.getIsRead())
                    messageIds.add(model.getMessageID());
            }
            TAPDataManager.getInstance().updateMessageStatusAsDelivered(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    private void updateMessageStatusToDelivered(int tempDeliveredRequestID, List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                messageIds.add(model.getMessageID());
            }
            TAPDataManager.getInstance().updateMessageStatusAsDelivered(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
                @Override
                public void onSuccess(TAPUpdateMessageStatusResponse response) {
                    super.onSuccess(response);
                    new Thread(() -> {
                        if (getApiDeliveredRequestMap().containsKey(tempDeliveredRequestID))
                            removeApiDeliveredRequestMapItem(tempDeliveredRequestID);
                    }).start();
                }

                @Override
                public void onError(String errorMessage) {
                    super.onError(errorMessage);
                    onApiError();
                }

                @Override
                public void onError(TAPErrorModel error) {
                    super.onError(error);
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

    private void updateMessageStatusToRead(int tempReadRequestID, List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                if (null != model)
                    messageIds.add(model.getMessageID());
            }
            TAPDataManager.getInstance().updateMessageStatusAsRead(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
                @Override
                public void onSuccess(TAPUpdateMessageStatusResponse response) {
                    super.onSuccess(response);
                    new Thread(() -> {
                        if (getApiDeliveredRequestMap().containsKey(tempReadRequestID))
                            removeApiDeliveredRequestMapItem(tempReadRequestID);
                    }).start();
                }

                @Override
                public void onError(String errorMessage) {
                    super.onError(errorMessage);
                    onApiError();
                }

                @Override
                public void onError(TAPErrorModel error) {
                    super.onError(error);
                    onApiError();
                }

                private void onApiError() {
                    new Thread(() -> {
                        if (getApiDeliveredRequestMap().containsKey(tempReadRequestID)) {
                            addApiReadRequestMapItem(readRequestID, getApiDeliveredRequestMap().get(tempReadRequestID));
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
    }
}
