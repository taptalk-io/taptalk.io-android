package io.taptalk.TapTalk.Manager;

import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;

@Keep
public class TapCoreRoomListManager {

    private static TapCoreRoomListManager instance;

    public static TapCoreRoomListManager getInstance() {
        return null == instance ? instance = new TapCoreRoomListManager() : instance;
    }

    public void fetchNewMessageToDatabase(TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        TAPDataManager.getInstance().getNewAndUpdatedMessage(new TAPDefaultDataView<TAPGetRoomListResponse>() {
            @Override
            public void onSuccess(TAPGetRoomListResponse response) {
                if (response.getMessages().size() > 0) {
                    List<TAPMessageEntity> tempMessage = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPChatManager.getInstance().convertToEntity(message);
                            tempMessage.add(entity);

                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                                userIds.add(TAPChatManager.getInstance().getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            } else {
                                TAPContactManager.getInstance().updateUserData(message.getUser());
                            }
                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance().deletePhysicalFile(entity);
                            }
                        } catch (Exception e) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                    }
                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance().getMultipleUsersByIdFromApi(userIds, new TAPDefaultDataView<TAPGetMultipleUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetMultipleUserResponse response) {
                                if (null == response || response.getUsers().isEmpty()) {
                                    return;
                                }
                                new Thread(() -> TAPContactManager.getInstance().updateUserData(response.getUsers())).start();
                            }
                        });
                    }
                    TAPDataManager.getInstance().insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            List<TAPMessageModel> messages = new ArrayList<>();
                            for (HashMap<String, Object> h : response.getMessages()) {
                                messages.add(TAPEncryptorManager.getInstance().decryptMessage(h));
                            }
                            if (null != listener) {
                                listener.onSuccess("Successfully Update Message");
                            }
                        }

                        @Override
                        public void onInsertFailed(String errorMessage) {
                            if (null != listener) {
                                listener.onSuccess("Failed Update Message");
                            }
                        }
                    });
                } else {
                    if (null != listener) {
                        listener.onSuccess("Failed Update Message");
                    }
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    private void fetchNewMessage(TapCoreGetMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        TAPDataManager.getInstance().getNewAndUpdatedMessage(new TAPDefaultDataView<TAPGetRoomListResponse>() {
            @Override
            public void onSuccess(TAPGetRoomListResponse response) {
                if (response.getMessages().size() > 0) {
                    List<TAPMessageEntity> tempMessage = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPChatManager.getInstance().convertToEntity(message);
                            tempMessage.add(entity);

                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                                userIds.add(TAPChatManager.getInstance().getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            } else {
                                TAPContactManager.getInstance().updateUserData(message.getUser());
                            }
                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance().deletePhysicalFile(entity);
                            }
                        } catch (Exception e) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                    }
                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance().getMultipleUsersByIdFromApi(userIds, new TAPDefaultDataView<TAPGetMultipleUserResponse>() {
                            @Override
                            public void onSuccess(TAPGetMultipleUserResponse response) {
                                if (null == response || response.getUsers().isEmpty()) {
                                    return;
                                }
                                new Thread(() -> TAPContactManager.getInstance().updateUserData(response.getUsers())).start();
                            }
                        });
                    }
                    TAPDataManager.getInstance().insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            List<TAPMessageModel> messages = new ArrayList<>();
                            for (HashMap<String, Object> h : response.getMessages()) {
                                messages.add(TAPEncryptorManager.getInstance().decryptMessage(h));
                            }
                            if (null != listener) {
                                listener.onSuccess(messages);
                            }
                        }

                        @Override
                        public void onInsertFailed(String errorMessage) {
                            if (null != listener) {
                                listener.onSuccess(new ArrayList<>());
                            }
                        }
                    });
                } else {
                    if (null != listener) {
                        listener.onSuccess(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getRoomListFromCache(TapCoreGetRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                super.onSelectFinished(entities);
                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                for (TAPMessageEntity e : entities) {
                    roomListModel.add(TAPRoomListModel.buildWithLastMessage(TAPChatManager.getInstance().convertToModel(e)));
                }
                if (null != listener) {
                    listener.onSuccess(roomListModel);
                }
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getUpdatedRoomList(TapCoreGetRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                if (entities.size() > 0) {
                    fetchNewMessage(new TapCoreGetMessageListener() {
                        @Override
                        public void onSuccess(List<TAPMessageModel> tapMessageModels) {
                            getRoomListFromCache(new TapCoreGetRoomListListener() {
                                @Override
                                public void onSuccess(List<TAPRoomListModel> tapRoomListModel) {
                                    if (null != listener) {
                                        listener.onSuccess(tapRoomListModel);
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    if (null != listener) {
                                        listener.onError(errorCode, errorMessage);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            if (null != listener) {
                                listener.onError(errorCode, errorMessage);
                            }
                        }
                    });
                } else {
                    TAPDataManager.getInstance().getMessageRoomListAndUnread(TAPChatManager.getInstance().getActiveUser().getUserID(), new TAPDefaultDataView<TAPGetRoomListResponse>() {
                        @Override
                        public void onSuccess(TAPGetRoomListResponse response) {
                            super.onSuccess(response);
                            List<TAPRoomListModel> roomListModel = new ArrayList<>();
                            for (TAPMessageEntity e : entities) {
                                roomListModel.add(TAPRoomListModel.buildWithLastMessage(TAPChatManager.getInstance().convertToModel(e)));
                            }
                            if (null != listener) {
                                listener.onSuccess(roomListModel);
                            }
                        }

                        @Override
                        public void onError(TAPErrorModel error) {
                            if (null != listener) {
                                listener.onError(error.getCode(), error.getMessage());
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, errorMessage);
                            }
                        }
                    });
                }
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }
}