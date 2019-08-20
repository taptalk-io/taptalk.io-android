package io.taptalk.TapTalk.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Interface.TapGetMessageInterface;
import io.taptalk.TapTalk.Interface.TapGetRoomListInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;

public class TapCoreRoomListManager {

    private static void fetchNewMessage(TapGetMessageInterface listener) {
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
                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
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
                            listener.onSuccess(messages);
                        }

                        @Override
                        public void onInsertFailed(String errorMessage) {
                            listener.onSuccess(new ArrayList<>());
                        }
                    });
                } else {
                    listener.onSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }

    public static void getRoomListFromCache(TapGetRoomListInterface listener) {
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                super.onSelectFinished(entities);
                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                for (TAPMessageEntity e : entities) {
                    roomListModel.add(TAPRoomListModel.buildWithLastMessage(TAPChatManager.getInstance().convertToModel(e)));
                }
                listener.onSuccess(roomListModel);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }

    public static void getUpdatedRoomList(TapGetRoomListInterface listener) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            return;
        }
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                if (entities.size() > 0) {
                    fetchNewMessage(new TapGetMessageInterface() {
                        @Override
                        public void onSuccess(List<TAPMessageModel> tapMessageModels) {
                            getRoomListFromCache(new TapGetRoomListInterface() {
                                @Override
                                public void onSuccess(List<TAPRoomListModel> tapRoomListModel) {
                                    listener.onSuccess(tapRoomListModel);
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    listener.onError(errorCode, errorMessage);
                                }
                            });
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            listener.onError(errorCode, errorMessage);
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
                            listener.onSuccess(roomListModel);
                        }

                        @Override
                        public void onError(TAPErrorModel error) {
                            listener.onError(error.getCode(), error.getMessage());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            listener.onError(ERROR_CODE_OTHERS, errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }
}