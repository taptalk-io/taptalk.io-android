package io.taptalk.TapTalk.Manager;

import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;

public class TAPOldDataManagerNew {
    private static final String TAG = TAPOldDataManagerNew.class.getSimpleName();
    private static TAPOldDataManagerNew instance;

    public static TAPOldDataManagerNew getInstance() {
        return null == instance ? instance = new TAPOldDataManagerNew() : instance;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            if (TAPDataManager.getInstance().checkLastDeleteTimestamp()) {
                TAPDataManager.getInstance().getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        autoCleanProcessFromRoomQueryResult(entities);
                    }
                });
            } else {
                TAPDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
            }
        }).start();
    }

    private void autoCleanProcessFromRoomQueryResult(List<TAPMessageEntity> entities) {
        for (TAPMessageEntity roomEntity : entities) {
            
        }
    }
}
