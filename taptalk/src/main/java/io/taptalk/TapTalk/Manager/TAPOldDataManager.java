package io.taptalk.TapTalk.Manager;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPOldDataManager {
    private static final String TAG = TAPOldDataManager.class.getSimpleName();
    private static TAPOldDataManager instance;

    public static TAPOldDataManager getInstance() {
        return null == instance ? instance = new TAPOldDataManager() : instance;
    }

    public void startAutoCleanProcess() {
        new Thread(() -> {
            long currentTimestamp = System.currentTimeMillis();
            boolean isOverOneWeek = TAPTimeFormatter.getInstance().checkOverOneWeekOrNot(TAPDataManager.getInstance().getLastDeleteTimestamp());

            if (TAPDataManager.getInstance().checkLastDeleteTimestamp() && isOverOneWeek) {
                TAPDataManager.getInstance().getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        autoCleanProcessFromRoomQueryResult(entities, currentTimestamp);
                    }
                });
            } else {
                TAPDataManager.getInstance().saveLastDeleteTimestamp(currentTimestamp);
            }
        }).start();
    }

    public void startCleanRoomPhysicalData(String roomId, TAPDatabaseListener listener) {
        new Thread(() -> {
            cleanRoomPhysicalData(roomId, listener);
        }).start();
    }

    private void autoCleanProcessFromRoomQueryResult(List<TAPMessageEntity> entities, long currentTimestamp) {
        final long[] smallestTimestamp = {TAPTimeFormatter.getInstance().oneMonthAgoTimeStamp(currentTimestamp)};
        for (TAPMessageEntity roomEntity : entities) {
            TAPDataManager.getInstance().getMinCreatedOfUnreadMessage(roomEntity.getRoomID(), new TAPDatabaseListener<Long>() {
                @Override
                public void onSelectFinished(Long minCreated) {
                    if (0L != minCreated && minCreated < smallestTimestamp[0])
                        smallestTimestamp[0] = minCreated;

                    TAPDataManager.getInstance().getAllMessagesInRoomFromDatabase(roomEntity.getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            if (null != entities && 51 <= entities.size()) {

                                if (entities.get(50).getCreated() < smallestTimestamp[0])
                                    smallestTimestamp[0] = entities.get(50).getCreated();

                                TAPDataManager.getInstance().getRoomMediaMessageBeforeTimestamp(roomEntity.getRoomID(), smallestTimestamp[0], new TAPDatabaseListener<TAPMessageEntity>() {
                                    @Override
                                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                                        for (TAPMessageEntity message : entities) {
//                                            if (TYPE_IMAGE == message.getType()) {
//                                                try {
//                                                    //apus file image fisiknya
//                                                    HashMap<String, Object> messageData = TAPUtils.getInstance().toHashMap(message.getData());
//                                                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache((String) messageData.get(FILE_ID));
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                    Log.e(TAG, "onSelectFinished: ", e);
//                                                }
//                                            } else if (TYPE_VIDEO == message.getType()
//                                                    || TYPE_FILE == message.getType()) {
//                                                //apus file fisiknya
//                                                HashMap<String, Object> messageData = TAPUtils.getInstance().toHashMap(message.getData());
//                                                if (null != TAPFileDownloadManager.getInstance().getFileMessageUri(roomEntity.getRoomID(), (String) messageData.get(FILE_ID))) {
//                                                    TapTalk.appContext.getContentResolver().delete(TAPFileDownloadManager.getInstance().getFileMessageUri(roomEntity.getRoomID(), (String) messageData.get(FILE_ID)), null, null);
//                                                    TAPFileDownloadManager.getInstance().removeFileMessageUri(roomEntity.getRoomID(), (String) messageData.get(FILE_ID));
//                                                }
//                                            }
                                            TAPDataManager.getInstance().deletePhysicalFile(message);
                                        }

                                        TAPDataManager.getInstance().deleteRoomMessageBeforeTimestamp(roomEntity.getRoomID(), smallestTimestamp[0], new TAPDatabaseListener() {
                                            @Override
                                            public void onDeleteFinished() {
                                                //Log.e(TAG, "onDeleteFinished: ");
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
    }

    private void cleanRoomPhysicalData(String roomId, TAPDatabaseListener listener) {
        TAPDataManager.getInstance().getRoomMediaMessage(roomId, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                for (TAPMessageEntity message : entities) {
                    if (TYPE_IMAGE == message.getType()) {
                        try {
                            //apus file image fisiknya
                            HashMap<String, Object> messageData = TAPUtils.getInstance().toHashMap(message.getData());
                            TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache((String) messageData.get(FILE_ID));
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Log.e(TAG, "onSelectFinished: ", e);
                        }
                    } else if (TYPE_VIDEO == message.getType() || TYPE_FILE == message.getType()) {
                        //apus file fisiknya
                        HashMap<String, Object> messageData = TAPUtils.getInstance().toHashMap(message.getData());
                        if (null != TAPFileDownloadManager.getInstance().getFileMessageUri(roomId, (String) messageData.get(FILE_ID))) {
                            TapTalk.appContext.getContentResolver().delete(TAPFileDownloadManager.getInstance().getFileMessageUri(roomId, (String) messageData.get(FILE_ID)), null, null);
                            TAPFileDownloadManager.getInstance().removeFileMessageUri(roomId, (String) messageData.get(FILE_ID));
                        }
                    }
                }
                listener.onDeleteFinished();
            }
        });
    }
}
