package com.moselo.HomingPigeon.Manager;

import android.util.Log;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;

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
                boolean isOverOneWeek = HpTimeFormatter.getInstance().checkOverOneWeekOrNot(HpDataManager.getInstance().getLastDeleteTimestamp());

                if (isOverOneWeek)
                    HpDataManager.getInstance().getRoomList(false, new HpDatabaseListener<HpMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<HpMessageEntity> entities) {
                            loopingRoomEntitiesArrayToGetAllMessage(entities);
                        }
                    });
            } else {
                HpDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
                Log.e(TAG, "startAutoCleanProcess: updated");
            }
        }).start();
    }

    private void loopingRoomEntitiesArrayToGetAllMessage(List<HpMessageEntity> entities) {
        new Thread(() -> {
            for (HpMessageEntity entity : entities) {
                HpDataManager.getInstance().getMessagesFromDatabaseAsc(entity.getRoomID(), new HpDatabaseListener<HpMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<HpMessageEntity> entities) {
                        int entitiesSize = entities.size();
                        if (50 < entitiesSize)
                            loopToCheckMessageAndExecuteDeleteQuery(entities, entitiesSize);
                    }
                });
            }
        }).start();
    }

    private void loopToCheckMessageAndExecuteDeleteQuery(List<HpMessageEntity> entities, int entitiesSize) {
        new Thread(() -> {
            List<HpMessageEntity> deleteMessageTempList = new ArrayList<>();
            for (int index = 0; index < entitiesSize - 50; index++) {
                if (HpTimeFormatter.getInstance().checkOverOneMonthOrNot(entities.get(index).getCreated())) {
                    deleteMessageTempList.add(entities.get(index));
                }
            }

            if (!deleteMessageTempList.isEmpty()) {
                HpDataManager.getInstance().deleteMessage(deleteMessageTempList, new HpDatabaseListener() {
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
