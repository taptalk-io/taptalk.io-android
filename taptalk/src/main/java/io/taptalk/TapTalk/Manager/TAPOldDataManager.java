package io.taptalk.TapTalk.Manager;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.BuildConfig;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;

public class TAPOldDataManager {
    private static final String TAG = TAPOldDataManager.class.getSimpleName();
    private static HashMap<String, TAPOldDataManager> instances;

    private String instanceKey = "";

    public static TAPOldDataManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPOldDataManager instance = new TAPOldDataManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPOldDataManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPOldDataManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            boolean isOverOneWeek = TAPTimeFormatter.isOverOneWeek(TAPDataManager.getInstance(instanceKey).getLastDeleteTimestamp());
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Start auto clean process: " + (TAPDataManager.getInstance(instanceKey).isLastDeleteTimestampExists() && isOverOneWeek));
                Log.d(TAG, "Last auto clean time: " + TAPTimeFormatter.formatTime(TAPDataManager.getInstance(instanceKey).getLastDeleteTimestamp(), "yyyy MMM dd - HH:mm:ss"));
            }

            if (TAPDataManager.getInstance(instanceKey).isLastDeleteTimestampExists() && isOverOneWeek) {
                TAPDataManager.getInstance(instanceKey).getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        autoCleanProcessFromRoomQueryResult(entities, currentTimestamp);
                    }
                });
            } else if (!TAPDataManager.getInstance(instanceKey).isLastDeleteTimestampExists()) {
                TAPDataManager.getInstance(instanceKey).saveLastDeleteTimestamp(currentTimestamp);
            }
        }).start();
    }

    public void cleanRoomPhysicalData(String roomId, TAPDatabaseListener listener) {
        new Thread(() -> TAPDataManager.getInstance(instanceKey).getRoomMediaMessage(roomId, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                for (TAPMessageEntity message : entities) {
                    TAPDataManager.getInstance(instanceKey).deletePhysicalFile(message);
                }
                listener.onDeleteFinished();
            }
        })).start();
    }

    private void autoCleanProcessFromRoomQueryResult(List<TAPMessageEntity> entities, long currentTimestamp) {
        for (TAPMessageEntity roomEntity : entities) {
            try {
                // Delay to prevent OutOfMemoryError: pthread_create
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final long[] maxCreatedTimestamp = {TAPTimeFormatter.oneMonthAgoTimeStamp(currentTimestamp)};
            // Check messages in existing rooms
            TAPDataManager.getInstance(instanceKey).getMinCreatedOfUnreadMessage(roomEntity.getRoomID(), new TAPDatabaseListener<Long>() {
                @Override
                public void onSelectFinished(Long minCreated) {
                    if (0L != minCreated && minCreated < maxCreatedTimestamp[0]) {
                        maxCreatedTimestamp[0] = minCreated;
                    }
                    TAPDataManager.getInstance(instanceKey).getAllMessagesInRoomFromDatabase(roomEntity.getRoomID(), false, new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            if (null != entities && MAX_ITEMS_PER_PAGE < entities.size()) {
                                // Delete messages and files if room has more than 50 messages
                                if (entities.get(MAX_ITEMS_PER_PAGE).getCreated() < maxCreatedTimestamp[0]) {
                                    maxCreatedTimestamp[0] = entities.get(MAX_ITEMS_PER_PAGE).getCreated();
                                }
                                TAPDataManager.getInstance(instanceKey).getRoomMediaMessageBeforeTimestamp(roomEntity.getRoomID(), maxCreatedTimestamp[0], new TAPDatabaseListener<TAPMessageEntity>() {
                                    @Override
                                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                                        for (TAPMessageEntity message : entities) {
                                            TAPDataManager.getInstance(instanceKey).deletePhysicalFile(message);
                                        }
                                        TAPDataManager.getInstance(instanceKey).deleteRoomMessageBeforeTimestamp(roomEntity.getRoomID(), maxCreatedTimestamp[0], new TAPDatabaseListener() {
                                            @Override
                                            public void onDeleteFinished() {
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        // Update timestamp on finish
        TAPDataManager.getInstance(instanceKey).saveLastDeleteTimestamp(currentTimestamp);
    }
}
