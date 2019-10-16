package io.taptalk.TapTalk.Manager;

import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.Taptalk.BuildConfig;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;

public class TAPOldDataManager {
    private static final String TAG = TAPOldDataManager.class.getSimpleName();
    private static TAPOldDataManager instance;

    public static TAPOldDataManager getInstance() {
        return null == instance ? instance = new TAPOldDataManager() : instance;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            boolean isOverOneWeek = TAPTimeFormatter.getInstance().isOverOneWeek(TAPDataManager.getInstance().getLastDeleteTimestamp());
//            boolean isOverOneWeek = (System.currentTimeMillis() - TAPDataManager.getInstance().getLastDeleteTimestamp()) >= TimeUnit.MINUTES.toMillis(1);
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "startAutoCleanProcess: " + (TAPDataManager.getInstance().isLastDeleteTimestampExists() && isOverOneWeek) + "\n" + TAPTimeFormatter.getInstance().formatTime(TAPDataManager.getInstance().getLastDeleteTimestamp(), "yyyy/MM/dd - HH:mm:ss"));
            }

            if (TAPDataManager.getInstance().isLastDeleteTimestampExists() && isOverOneWeek) {
                TAPDataManager.getInstance().getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        autoCleanProcessFromRoomQueryResult(entities, currentTimestamp);
                    }
                });
            } else if (!TAPDataManager.getInstance().isLastDeleteTimestampExists()) {
                TAPDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
            }
        }).start();
    }

    public void cleanRoomPhysicalData(String roomId, TAPDatabaseListener listener) {
        new Thread(() -> TAPDataManager.getInstance().getRoomMediaMessage(roomId, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                for (TAPMessageEntity message : entities) {
                    TAPDataManager.getInstance().deletePhysicalFile(message);
                }
                listener.onDeleteFinished();
            }
        })).start();
    }

    private void autoCleanProcessFromRoomQueryResult(List<TAPMessageEntity> entities, long currentTimestamp) {
        final long[] smallestTimestamp = {TAPTimeFormatter.getInstance().oneMonthAgoTimeStamp(currentTimestamp)};
//        final long[] smallestTimestamp = {System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)};
        for (TAPMessageEntity roomEntity : entities) {
            // Check messages in existing rooms
            TAPDataManager.getInstance().getMinCreatedOfUnreadMessage(roomEntity.getRoomID(), new TAPDatabaseListener<Long>() {
                @Override
                public void onSelectFinished(Long minCreated) {
                    if (0L != minCreated && minCreated < smallestTimestamp[0]) {
                        smallestTimestamp[0] = minCreated;
                    }
                    TAPDataManager.getInstance().getAllMessagesInRoomFromDatabase(roomEntity.getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            if (null != entities && MAX_ITEMS_PER_PAGE < entities.size()) {
                                if (entities.get(MAX_ITEMS_PER_PAGE).getCreated() < smallestTimestamp[0]) {
                                    smallestTimestamp[0] = entities.get(MAX_ITEMS_PER_PAGE).getCreated();
                                }
                                // Delete messages and files if room has more than 50 messages
                                TAPDataManager.getInstance().getRoomMediaMessageBeforeTimestamp(roomEntity.getRoomID(), smallestTimestamp[0], new TAPDatabaseListener<TAPMessageEntity>() {
                                    @Override
                                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                                        for (TAPMessageEntity message : entities) {
                                            TAPDataManager.getInstance().deletePhysicalFile(message);
                                        }
                                        TAPDataManager.getInstance().deleteRoomMessageBeforeTimestamp(roomEntity.getRoomID(), smallestTimestamp[0], new TAPDatabaseListener() {
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
        TAPDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
    }
}
