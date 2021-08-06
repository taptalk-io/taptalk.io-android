package io.taptalk.TapTalk.Manager;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;

@Keep
public class TapCoreRoomListManager {

    private static HashMap<String, TapCoreRoomListManager> instances;

    private String instanceKey = "";

    public TapCoreRoomListManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapCoreRoomListManager getInstance() {
        return getInstance("");
    }

    public static TapCoreRoomListManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapCoreRoomListManager instance = new TapCoreRoomListManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapCoreRoomListManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public void fetchNewMessageToDatabase(TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getNewAndUpdatedMessage(new TAPDefaultDataView<TAPGetRoomListResponse>() {
            @Override
            public void onSuccess(TAPGetRoomListResponse response) {
                if (response.getMessages().size() > 0) {
                    List<TAPMessageEntity> tempMessage = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    List<TAPUserModel> userModels = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            tempMessage.add(entity);

                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                userIds.add(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            } else {
                                userModels.add(message.getUser());
                            }
                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance(instanceKey).deletePhysicalFile(entity);
                            }
                        } catch (Exception e) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                    }

                    TAPContactManager.getInstance(instanceKey).updateUserData(userModels);

                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                    }
                    TAPDataManager.getInstance(instanceKey).insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            List<TAPMessageModel> messages = new ArrayList<>();
                            for (HashMap<String, Object> h : response.getMessages()) {
                                messages.add(TAPEncryptorManager.getInstance().decryptMessage(h));
                            }
                            if (null != listener) {
                                listener.onSuccess("Successfully updated message.");
                            }
                        }

                        @Override
                        public void onInsertFailed(String errorMessage) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, "Failed to update message.");
                            }
                        }
                    });
                } else {
                    if (null != listener) {
                        listener.onSuccess("No message was updated.");
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getNewAndUpdatedMessage(new TAPDefaultDataView<TAPGetRoomListResponse>() {
            @Override
            public void onSuccess(TAPGetRoomListResponse response) {
                if (response.getMessages().size() > 0) {
                    List<TAPMessageEntity> tempMessage = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    List<TAPUserModel> userModels = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            tempMessage.add(entity);

                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                userIds.add(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            } else {
                                userModels.add(message.getUser());
                            }
                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance(instanceKey).deletePhysicalFile(entity);
                            }
                        } catch (Exception e) {
                            if (null != listener) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                    }
                    TAPContactManager.getInstance(instanceKey).updateUserData(userModels);
                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                    }
                    TAPDataManager.getInstance(instanceKey).insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getRoomList(TAPChatManager.getInstance(instanceKey).getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
                onSelectFinishedWithUnreadCount(entities, unreadMap, mentionCount);
            }

            @Override
            public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
                super.onSelectFinished(entities);
                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                for (TAPMessageEntity e : entities) {
                    roomListModel.add(TAPRoomListModel.buildWithLastMessage(TAPMessageModel.fromMessageEntity(e)));
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
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        if (null == TAPChatManager.getInstance(instanceKey).getActiveUser()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getRoomList(TAPChatManager.getInstance(instanceKey).getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
                onSelectFinishedWithUnreadCount(entities, unreadMap, mentionCount);
            }

            @Override
            public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
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
                    TAPDataManager.getInstance(instanceKey).getMessageRoomListAndUnread(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), new TAPDefaultDataView<TAPGetRoomListResponse>() {
                        @Override
                        public void onSuccess(TAPGetRoomListResponse response) {
                            if (response.getMessages().size() > 0) {
                                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                                List<String> userIds = new ArrayList<>();
                                List<TAPUserModel> userModels = new ArrayList<>();
                                for (HashMap<String, Object> messageMap : response.getMessages()) {
                                    try {
                                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                        TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                                        tempMessage.add(entity);
                                        roomListModel.add(TAPRoomListModel.buildWithLastMessage(message));

                                        // Save undelivered messages to list
                                        if (null == message.getDelivered() || (null != message.getDelivered() && !message.getDelivered())) {
                                            deliveredMessages.add(message);
                                        }

                                        if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                            // User is self, get other user data from API
                                            userIds.add(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                                        } else {
                                            // Save user data to contact manager
                                            userModels.add(message.getUser());
                                        }
                                        if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                            TAPDataManager.getInstance(instanceKey).deletePhysicalFile(entity);
                                        }
                                    } catch (Exception e) {
                                        if (null != listener) {
                                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                        }
                                    }
                                }
                                TAPContactManager.getInstance(instanceKey).updateUserData(userModels);

                                // Trigger listener callback
                                if (null != listener) {
                                    listener.onSuccess(roomListModel);
                                }

                                // Update status to delivered
                                if (deliveredMessages.size() > 0) {
                                    TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(deliveredMessages);
                                }

                                // Get updated other user data from API
                                if (userIds.size() > 0) {
                                    TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                                }

                                // Save message to database
                                TAPDataManager.getInstance(instanceKey).insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
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
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    private final TAPDefaultDataView<TAPGetMultipleUserResponse> getMultipleUserView = new TAPDefaultDataView<TAPGetMultipleUserResponse>() {
        @Override
        public void onSuccess(TAPGetMultipleUserResponse response) {
            if (null == response || response.getUsers().isEmpty()) {
                return;
            }
            new Thread(() -> TAPContactManager.getInstance(instanceKey).updateUserData(response.getUsers())).start();
        }
    };
}
