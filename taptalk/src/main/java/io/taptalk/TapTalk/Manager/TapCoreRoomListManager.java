package io.taptalk.TapTalk.Manager;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreUpdateMessageStatusListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_CHAT_ROOM_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

import android.util.Log;

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
                    List<TAPMessageEntity> messagesToSave = new ArrayList<>();
                    List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    List<TAPUserModel> userModels = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            messagesToSave.add(entity);

                            if (null == message.getDelivered() || (null != message.getDelivered() && !message.getDelivered())) {
                                deliveredMessages.add(message);
                            }

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

                    if (deliveredMessages.size() > 0) {
                        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(deliveredMessages);
                    }

                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                    }

                    TAPDataManager.getInstance(instanceKey).insertToDatabase(messagesToSave, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
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

    public void fetchNewMessage(TapCoreGetMessageListener listener) {
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
                    List<TAPMessageModel> resultMessages = new ArrayList<>();
                    List<TAPMessageEntity> messagesToSave = new ArrayList<>();
                    List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    List<TAPUserModel> userModels = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            resultMessages.add(message);
                            messagesToSave.add(entity);

                            if (null == message.getDelivered() || (null != message.getDelivered() && !message.getDelivered())) {
                                deliveredMessages.add(message);
                            }

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

                    if (deliveredMessages.size() > 0) {
                        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(deliveredMessages);
                    }

                    if (userIds.size() > 0) {
                        TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                    }

                    TAPDataManager.getInstance(instanceKey).insertToDatabase(messagesToSave, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            if (null != listener) {
                                listener.onSuccess(resultMessages);
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
            public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
                onSelectFinishedWithUnreadCount(entities, unreadMap, mentionMap);
            }

            @Override
            public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                for (TAPMessageEntity e : entities) {
                    TAPRoomListModel roomList = TAPRoomListModel.buildWithLastMessage(TAPMessageModel.fromMessageEntity(e));
                    if (null != unreadMap && null != unreadMap.get(e.getRoomID())) {
                        roomList.setNumberOfUnreadMessages(unreadMap.get(e.getRoomID()));
                    }
                    if (null != mentionMap && null != mentionMap.get(e.getRoomID())) {
                        roomList.setNumberOfUnreadMentions(mentionMap.get(e.getRoomID()));
                    }
                    roomListModel.add(roomList);
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
            public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
                onSelectFinishedWithUnreadCount(entities, unreadMap, mentionMap);
            }

            @Override
            public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
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
                                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                                List<String> userIds = new ArrayList<>();
                                List<TAPUserModel> userModels = new ArrayList<>();
                                for (HashMap<String, Object> messageMap : response.getMessages()) {
                                    try {
                                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                        TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                                        tempMessage.add(entity);

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
                                        if (null != message.getUpdated() &&
                                                TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(message.getRoom().getRoomID()) < message.getUpdated()
                                        ) {
                                            TAPDataManager.getInstance(instanceKey).saveLastUpdatedMessageTimestamp(message.getRoom().getRoomID(), message.getUpdated());
                                        }
                                    } catch (Exception e) {
                                        if (null != listener) {
                                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                        }
                                    }
                                }
                                TAPContactManager.getInstance(instanceKey).updateUserData(userModels);

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
                                    @Override
                                    public void onInsertFinished() {
                                        getRoomListFromCache(new TapCoreGetRoomListListener() {
                                            @Override
                                            public void onSuccess(List<TAPRoomListModel> tapRoomListModel) {
                                                if (null != listener) {
                                                    // Trigger listener callback
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

    public void searchLocalRoomListWithKeyword(String keyword, TapCoreGetRoomListListener listener) {
        TAPDataManager.getInstance(instanceKey).searchAllRoomsFromDatabase(keyword, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
                ArrayList<TAPRoomListModel> searchResultArray = new ArrayList<>();
                if (entities.size() > 0) {
                    for (TAPMessageEntity entity : entities) {
                        String myId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
                        // Exclude active user's own room
                        if (!entity.getRoomID().equals(TAPChatManager.getInstance(instanceKey).arrangeRoomId(myId, myId))) {
                            TAPRoomListModel result = TAPRoomListModel.buildWithLastMessage(TAPMessageModel.fromMessageEntity(entity));
                            Integer unreadCount = unreadMap.get(entity.getRoomID());
                            if (null != unreadCount) {
                                result.setNumberOfUnreadMessages(unreadCount);
                            }
                            Integer mentionCount = mentionMap.get(entity.getRoomID());
                            if (null != mentionCount) {
                                result.setNumberOfUnreadMentions(mentionCount);
                            }
                            if (result.getLastMessage().getRoom().getType() != TYPE_PERSONAL) {
                                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(result.getLastMessage().getRoom().getRoomID());
                                if (null != room) {
                                    result.getLastMessage().setRoom(room);
                                }
                            }
                            searchResultArray.add(result);
                        }
                    }
                }
                if (null != listener) {
                    listener.onSuccess(searchResultArray);
                }
            }
        });
    }

    public void markChatRoomAsUnread(String roomID, @Nullable TapCommonListener listener) {
        List<String> roomIDs = new ArrayList<>();
        roomIDs.add(roomID);
        markChatRoomsAsUnread(roomIDs, listener);
    }

    public void markChatRoomsAsUnread(List<String> roomIDs, @Nullable TapCommonListener listener) {
        TAPDataManager.getInstance(instanceKey).markRoomAsUnread(roomIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                // Save updated IDs to preference
                ArrayList<String> unreadRoomListIds = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs();
                for (String unreadID : response.getUnreadRoomIDs()) {
                    if (!unreadRoomListIds.contains(unreadID)) {
                        unreadRoomListIds.add(unreadID);
                    }
                }
                TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(unreadRoomListIds);
                if (null != listener) {
                    String successMessage = "Successfully marked room as unread.";
                    listener.onSuccess(successMessage);
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

    public void removeUnreadMarkFromChatRoom(String roomID, TapCommonListener listener) {
        ArrayList<String> roomIds = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs();
        if (roomIds.contains(roomID)) {
            TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, new TAPDatabaseListener<>() {
                @Override
                public void onSelectFinished(List<TAPMessageEntity> entities) {
                    if (!entities.isEmpty()) {
                        List<String> unreadList = new ArrayList<>();
                        unreadList.add(entities.get(0).getMessageID());
                        TapCoreMessageManager.getInstance(instanceKey).markMessagesAsRead(unreadList, new TapCoreUpdateMessageStatusListener() {
                            @Override
                            public void onSuccess(List<String> updatedMessageIDs) {
                                roomIds.remove(roomID);
                                TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(roomIds);
                                if (null != listener) {
                                    listener.onSuccess("Successfully removed unread mark from the selected chat room.");
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                if (null != listener) {
                                    listener.onError(errorCode, errorMessage);
                                }
                            }
                        });
                    } else {
                        if (null != listener) {
                            listener.onError(ERROR_CODE_CHAT_ROOM_NOT_FOUND,"The selected chat room is not marked as unread.");
                        }
                    }
                }
            });
        } else {
            if (null != listener) {
                listener.onError(ERROR_CODE_CHAT_ROOM_NOT_FOUND,"The selected chat room is not marked as unread.");
            }
        }
    }

    public void removeUnreadMarkFromChatRooms(List<String> roomIDs, TapCommonListener listener) {
        ArrayList<String> unreadRoomIDs = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs();
        List<String> unreadList = new ArrayList<>();
        final int[] count = {0};
        for (String roomID : roomIDs) {
            if (unreadRoomIDs.contains(roomID)) {
                TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, new TAPDatabaseListener<>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        if (!entities.isEmpty()) {
                            unreadList.add(entities.get(0).getMessageID());
                        }
                        if (++count[0] == roomIDs.size()) {
                            markUnreadChatRoomMessagesAsRead(unreadList, unreadRoomIDs, listener);
                        }
                    }
                });
            }
            else if (++count[0] == roomIDs.size()) {
                markUnreadChatRoomMessagesAsRead(unreadList, unreadRoomIDs, listener);
            }
        }
    }

    private void markUnreadChatRoomMessagesAsRead(List<String> unreadList, ArrayList<String> unreadRoomIDs, TapCommonListener listener) {
        if (!unreadList.isEmpty()) {
            TapCoreMessageManager.getInstance(instanceKey).markMessagesAsRead(unreadList, new TapCoreUpdateMessageStatusListener() {
                @Override
                public void onSuccess(List<String> updatedMessageIDs) {
                    unreadRoomIDs.removeAll(unreadList);
                    TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(unreadRoomIDs);
                    if (null != listener) {
                        listener.onSuccess("Successfully removed unread mark from the selected chat room(s).");
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    if (null != listener) {
                        listener.onError(errorCode, errorMessage);
                    }
                }
            });
        } else {
            if (null != listener) {
                listener.onError(ERROR_CODE_CHAT_ROOM_NOT_FOUND,"The selected chat rooms are not marked as unread.");
            }
        }
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

    public void getMarkedAsUnreadChatRoomList(TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getUnreadRoomIds(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                ArrayList<String> roomIDs = new ArrayList<>(response.getUnreadRoomIDs());
                TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(roomIDs);
                if (null != listener) {
                    listener.onSuccess(roomIDs);
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
