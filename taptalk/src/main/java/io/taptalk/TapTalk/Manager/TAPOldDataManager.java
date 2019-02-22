package io.taptalk.TapTalk.Manager;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;

public class TAPOldDataManager {
    private static final String TAG = TAPOldDataManager.class.getSimpleName();
    private static TAPOldDataManager instance;

    public static TAPOldDataManager getInstance() {
        return null == instance ? instance = new TAPOldDataManager() : instance;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            if (TAPDataManager.getInstance().checkLastDeleteTimestamp()) {
                boolean isOverOneWeek = TAPTimeFormatter.getInstance().checkOverOneWeekOrNot(TAPDataManager.getInstance().getLastDeleteTimestamp());

                if (isOverOneWeek)
                    TAPDataManager.getInstance().getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            loopingRoomEntitiesArrayToGetAllMessage(entities);
                        }
                    });
            } else {
                TAPDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
            }
        }).start();
    }

    private void loopingRoomEntitiesArrayToGetAllMessage(List<TAPMessageEntity> entities) {
        new Thread(() -> {
            for (TAPMessageEntity entity : entities) {
                TAPDataManager.getInstance().getMessagesFromDatabaseAsc(entity.getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        int entitiesSize = entities.size();
                        if (50 < entitiesSize)
                            loopToCheckMessageAndExecuteDeleteQuery(entities, entitiesSize);
                    }
                });
            }
        }).start();
    }

    private void loopToCheckMessageAndExecuteDeleteQuery(List<TAPMessageEntity> entities, int entitiesSize) {
        new Thread(() -> {
            List<TAPMessageEntity> deleteMessageTempList = new ArrayList<>();
            for (int index = 0; index < entitiesSize - 50; index++) {
                if (TAPTimeFormatter.getInstance().checkOverOneMonthOrNot(entities.get(index).getCreated())) {
                    deleteMessageTempList.add(entities.get(index));
                }
            }

            if (!deleteMessageTempList.isEmpty()) {
                TAPDataManager.getInstance().deleteMessage(deleteMessageTempList, new TAPDatabaseListener() {
                    @Override
                    public void onDeleteFinished() {
                        TAPDataManager.getInstance().saveLastDeleteTimestamp(System.currentTimeMillis());
                    }
                });
            }
        }).start();
    }

}
