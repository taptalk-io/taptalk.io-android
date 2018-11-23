package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Listener.TAPMessageStatusListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPMessageStatusManager {
    private static final String TAG = TAPMessageStatusManager.class.getSimpleName();
    private static TAPMessageStatusManager instance;
    private ScheduledExecutorService messageStatusScheduler;
    private List<TAPMessageModel> readMessageQueue;
    private List<TAPMessageModel> deliveredMessageQueue;
    private List<TAPMessageStatusListener> messageStatusListeners;
    private int readRequestID = 0;
    private int deliveredRequestID = 0;
    private Map<Integer, List<TAPMessageModel>> apiReadRequestMap;
    private Map<Integer, List<TAPMessageModel>> apiDeliveredRequestMap;

    public static TAPMessageStatusManager getInstance() {
        return null == instance ? instance = new TAPMessageStatusManager() : instance;
    }

    public List<TAPMessageStatusListener> getMessageStatusListeners() {
        return null == messageStatusListeners ? messageStatusListeners = new ArrayList<>() : messageStatusListeners;
    }

    public void addMessageStatusListener(TAPMessageStatusListener messageStatusListener) {
        getMessageStatusListeners().add(messageStatusListener);
    }

    public void removeMessageStatusListener(TAPMessageStatusListener messageStatusListener) {
        getMessageStatusListeners().remove(messageStatusListener);
    }

    public void clearMessageStatusListeners() {
        getMessageStatusListeners().clear();
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
        getApiReadRequestMap().put(readRequestID, apiReadRequestMapItem);
    }

    public void removeApiReadRequestMapItem(int readRequestID) {
        getApiReadRequestMap().remove(readRequestID);
    }

    public Map<Integer, List<TAPMessageModel>> getApiDeliveredRequestMap() {
        return null == apiDeliveredRequestMap ? apiDeliveredRequestMap = new LinkedHashMap<>() : apiDeliveredRequestMap;
    }

    public void addApiDeliveredRequestMapItem(int deliveredRequestID, List<TAPMessageModel> apiDeliveredRequestMapItem) {
        if (getApiDeliveredRequestMap().containsKey(deliveredRequestID))
            getApiDeliveredRequestMap().get(deliveredRequestID).addAll(apiDeliveredRequestMapItem);
        else
            getApiDeliveredRequestMap().put(deliveredRequestID, apiDeliveredRequestMapItem);
    }

    public void removeApiDeliveredRequestMapItem(int deliveredRequestID) {
        getApiDeliveredRequestMap().remove(deliveredRequestID);
    }

    public void triggerCallMessageApiScheduler() {
        if (null == messageStatusScheduler || messageStatusScheduler.isShutdown()) {
            messageStatusScheduler = Executors.newSingleThreadScheduledExecutor();
        } else {
            messageStatusScheduler.shutdown();
            messageStatusScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        messageStatusScheduler.scheduleAtFixedRate(() -> {
            new Thread(() -> {
                //ini buat read
                // TODO: 15/11/18 call API read Message ID
                addApiReadRequestMapItem(readRequestID, new ArrayList<>(getReadMessageQueue()));
                updateMessageReadStatusInView();
                readRequestID++;
            }).start();

            new Thread(() -> {
                //ini buat delivered
                if (0 < getDeliveredMessageQueue().size()) {
                    addApiDeliveredRequestMapItem(deliveredRequestID, new ArrayList<>(getDeliveredMessageQueue()));
                    deliveredApiCallProcedure();
                    updateMessageDeliveredStatusInView();
                    deliveredRequestID++;
                }
            }).start();
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void shutdownMessageApiScheduler() {
        if (null != messageStatusScheduler && !messageStatusScheduler.isShutdown())
            messageStatusScheduler.shutdown();
    }

    public void updateMessageStatusWhenAppToBackground() {
        //updateMessageReadStatusInView();

        if (0 < getDeliveredMessageQueue().size())
            deliveredApiCallProcedure();

        shutdownMessageApiScheduler();
    }

    public void deliveredApiCallProcedure() {
        List<TAPMessageModel> tempMessageModel = new ArrayList<>(getDeliveredMessageQueue());
        updateMessageStatusToDelivered(deliveredRequestID, tempMessageModel);
        new Thread(() -> {
            List<TAPMessageEntity> messageEntities = new ArrayList<>();
            for (TAPMessageModel messageModel : tempMessageModel) {
                messageEntities.add(TAPChatManager.getInstance().convertToEntity(messageModel));
            }
            TAPDataManager.getInstance().insertToDatabase(messageEntities, false);
        }).start();
        clearDeliveredMessageQueue();
    }

    private void updateMessageReadStatusInView() {
        List<TAPMessageModel> tempMessageReadModel = new ArrayList<>(getReadMessageQueue());
        for (TAPMessageStatusListener listener : getMessageStatusListeners()) {
            listener.onReadStatus(tempMessageReadModel);
        }
        clearReadMessageQueue();
    }

    private void updateMessageDeliveredStatusInView() {
        List<TAPMessageModel> tempMessageDeliveredModel = new ArrayList<>(getReadMessageQueue());
        for (TAPMessageStatusListener listener : getMessageStatusListeners()) {
            listener.onDeliveredStatus(tempMessageDeliveredModel);
        }
        clearDeliveredMessageQueue();
    }

    public void updateMessageStatusToDeliveredFromNotification(TAPMessageModel newMessageModel) {
        new Thread(() -> {
            List<String> messageIds = new ArrayList<>();
            messageIds.add(newMessageModel.getMessageID());
            TAPDataManager.getInstance().updateMessageStatusAsDelivered(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    public void updateMessageStatusToDeliveredFromNotification(List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            TAPUserModel myUser = TAPDataManager.getInstance().getActiveUser();
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                if (!model.getUser().getUserID().equals(myUser.getUserID())
                        && null != model.getSending() && !model.getSending()
                        && null != model.getDelivered() && !model.getDelivered()
                        && null != model.getIsRead() && !model.getIsRead())
                    messageIds.add(model.getMessageID());
            }
            TAPDataManager.getInstance().updateMessageStatusAsDelivered(messageIds, new TapDefaultDataView<TAPUpdateMessageStatusResponse>() {
            });
        }).start();
    }

    public void updateMessageStatusToDelivered(int tempDeliveredRequestID, List<TAPMessageModel> newMessageModels) {
        new Thread(() -> {
            TAPUserModel myUser = TAPDataManager.getInstance().getActiveUser();
            List<String> messageIds = new ArrayList<>();
            for (TAPMessageModel model : newMessageModels) {
                if (!model.getUser().getUserID().equals(myUser.getUserID())
                        && null != model.getSending() && !model.getSending()
                        && null != model.getDelivered() && !model.getDelivered()
                        && null != model.getIsRead() && !model.getIsRead())
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
}
