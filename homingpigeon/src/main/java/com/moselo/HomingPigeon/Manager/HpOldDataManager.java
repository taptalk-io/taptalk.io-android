package com.moselo.HomingPigeon.Manager;

import android.util.Log;

import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Helper.TAPTimeFormatter;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;

import java.util.ArrayList;
import java.util.List;

public class HpOldDataManager {
    private static final String TAG = HpOldDataManager.class.getSimpleName();
    private static HpOldDataManager instance;

    public static HpOldDataManager getInstance() {
        return null == instance ? instance = new HpOldDataManager() : instance;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            if (HpDataManager.getInstance().checkLastDeleteTimestamp()) {
                boolean isOverOneWeek = TAPTimeFormatter.getInstance().checkOverOneWeekOrNot(HpDataManager.getInstance().getLastDeleteTimestamp());

                if (isOverOneWeek)
                    HpDataManager.getInstance().getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            loopingRoomEntitiesArrayToGetAllMessage(entities);
                        }
                    });
            } else {
                HpDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
                Log.e(TAG, "startAutoCleanProcess: updated");
            }
        }).start();
    }

    private void loopingRoomEntitiesArrayToGetAllMessage(List<TAPMessageEntity> entities) {
        new Thread(() -> {
            for (TAPMessageEntity entity : entities) {
                HpDataManager.getInstance().getMessagesFromDatabaseAsc(entity.getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
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
                HpDataManager.getInstance().deleteMessage(deleteMessageTempList, new TAPDatabaseListener() {
                    @Override
                    public void onDeleteFinished() {
                        Log.e(TAG, "loopToCheckMessageAndExecuteDeleteQuery: deleted");
                        HpDataManager.getInstance().saveLastDeleteTimestamp(System.currentTimeMillis());
                    }
                });
            }
        }).start();
    }

}
