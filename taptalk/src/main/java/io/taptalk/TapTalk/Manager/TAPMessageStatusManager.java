package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPChatActivity;

public class TAPMessageStatusManager {
    private static final String TAG = TAPMessageStatusManager.class.getSimpleName();
    private static TAPMessageStatusManager instance;
    private ScheduledExecutorService messageStatusScheduler;
    private List<TAPMessageModel> readMessageQueue;
    private int requestID = 0;
    private Map<Integer, List<TAPMessageModel>> apiRequestMap;

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

    public Map<Integer, List<TAPMessageModel>> getApiRequestMap() {
        return null == apiRequestMap ? apiRequestMap = new LinkedHashMap<>() : apiRequestMap;
    }

    public void addApiRequestMapItem(int requestID, List<TAPMessageModel> apiRequestMapItem) {
        getApiRequestMap().put(requestID, apiRequestMapItem);
    }

    public void removeApiRequestMapItem(int requestID) {
        getApiRequestMap().remove(requestID);
    }

    public void triggerCallReadMessageApiScheduler(TAPChatActivity.MessageStatusInterface msgStatusInterface) {
        if (null == messageStatusScheduler || messageStatusScheduler.isShutdown()) {
            messageStatusScheduler = Executors.newSingleThreadScheduledExecutor();
        } else {
            messageStatusScheduler.shutdown();
            messageStatusScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        messageStatusScheduler.scheduleAtFixedRate(() -> {
            // TODO: 15/11/18 call API read Message ID
            addApiRequestMapItem(requestID, new ArrayList<>(getReadMessageQueue()));
            updateMessageStatusInView(msgStatusInterface);
            requestID++;
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void shutdownReadMessageApiScheduler() {
        if (null != messageStatusScheduler && !messageStatusScheduler.isShutdown())
            messageStatusScheduler.shutdown();
    }

    public void updateMessageStatusWhenCloseRoom(TAPChatActivity.MessageStatusInterface msgStatusInterface) {
        updateMessageStatusInView(msgStatusInterface);
        shutdownReadMessageApiScheduler();
    }

    private void updateMessageStatusInView(TAPChatActivity.MessageStatusInterface msgStatusInterface) {
        msgStatusInterface.onReadStatus(new ArrayList<>(getReadMessageQueue()));
        clearReadMessageQueue();
    }


}
