package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_CHAT_ROOM_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_EXCEEDED_MAX_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ROOM_MAX_PINNED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ProjectConfigKeys.ROOM_MAX_PINNED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

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
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMutedChatRoomListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreRoomListListener;
import io.taptalk.TapTalk.Listener.TapCoreUpdateMessageStatusListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMutedRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapMutedRoomListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public class TapCoreRoomListManager {

    private static HashMap<String, TapCoreRoomListManager> instances;
    private List<TapCoreRoomListListener> coreRoomListListeners;
    private TAPChatListener chatListener;

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

    private List<TapCoreRoomListListener> getCoreRoomListListeners() {
        return null == coreRoomListListeners ? coreRoomListListeners = new ArrayList<>() : coreRoomListListeners;
    }

    public synchronized void addCoreRoomListListener(TapCoreRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        if (getCoreRoomListListeners().isEmpty()) {
            if (chatListener == null) {
                chatListener = new TAPChatListener() {
                    @Override
                    public void onChatCleared(TAPRoomModel room) {
                        super.onChatCleared(room);
                        listener.onChatRoomDeleted(room.getRoomID());
                    }

                    @Override
                    public void onMuteOrUnmuteRoom(TAPRoomModel room, Long expiredAt) {
                        super.onMuteOrUnmuteRoom(room, expiredAt);
                        if (expiredAt != null) {
                            listener.onChatRoomMuted(room.getRoomID(), expiredAt);
                        } else {
                            listener.onChatRoomUnmuted(room.getRoomID());
                        }
                    }

                    @Override
                    public void onPinRoom(TAPRoomModel room) {
                        super.onPinRoom(room);
                        listener.onChatRoomPinned(room.getRoomID());
                    }

                    @Override
                    public void onUnpinRoom(TAPRoomModel room) {
                        super.onUnpinRoom(room);
                        listener.onChatRoomUnpinned(room.getRoomID());
                    }

                    @Override
                    public void onMarkRoomAsRead(String roomID) {
                        super.onMarkRoomAsRead(roomID);
                        listener.onChatRoomMarkedAsRead(roomID);
                    }

                    @Override
                    public void onMarkRoomAsUnread(String roomID) {
                        super.onMarkRoomAsUnread(roomID);
                        listener.onChatRoomMarkedAsUnread(roomID);
                    }
                };
                TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
            }
        }
        getCoreRoomListListeners().remove(listener);
        getCoreRoomListListeners().add(listener);
    }

    public synchronized void removeCoreRoomListListener(TapCoreRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getCoreRoomListListeners().remove(listener);
        if (getCoreRoomListListeners().isEmpty()) {
            TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        }
    }

    public void fetchNewMessageToDatabase(TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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

                            if (null == message.getIsDelivered() || (null != message.getIsDelivered() && !message.getIsDelivered())) {
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

                            TAPUtils.handleReceivedSystemMessage(instanceKey, message);
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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

                            if (null == message.getIsDelivered() || (null != message.getIsDelivered() && !message.getIsDelivered())) {
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

                            TAPUtils.handleReceivedSystemMessage(instanceKey, message);
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
                            List<String> userIds = new ArrayList<>();
                            if (response.getMessages().size() > 0) {
                                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                                List<TAPUserModel> userModels = new ArrayList<>();
                                for (HashMap<String, Object> messageMap : response.getMessages()) {
                                    try {
                                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                        TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                                        tempMessage.add(entity);

                                        // Save undelivered messages to list
                                        if (null == message.getIsDelivered() || (null != message.getIsDelivered() && !message.getIsDelivered())) {
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

    public void getMutedChatRoomList(TapCoreGetMutedChatRoomListener listener) {
        TAPDataManager.getInstance(instanceKey).getMutedRoomIds(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetMutedRoomIdsResponse response) {
                HashMap<String, Long> roomMap = new HashMap<>();
                for (TapMutedRoomListModel mutedRoom : response.getMutedRooms()) {
                    roomMap.put(mutedRoom.getRoomID(), mutedRoom.getExpiredAt());
                }
                TAPDataManager.getInstance(instanceKey).saveMutedRoomIDs(roomMap);
                if (null != listener) {
                    listener.onSuccess(new ArrayList<>(response.getMutedRooms()));
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

    public void muteChatRooms(List<String> roomIDs, Long expiredAt, TapCommonListener listener) {
        TAPDataManager.getInstance(instanceKey).muteRoom(roomIDs, expiredAt, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                super.onSuccess(response);
                HashMap<String, Long> mutedRooms = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs();
                for (String roomId : response.getUnreadRoomIDs()) {
                    mutedRooms.put(roomId, expiredAt);
                }
                TAPDataManager.getInstance(instanceKey).saveMutedRoomIDs(mutedRooms);
                listener.onSuccess("Successfully mute rooms");
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

    public void muteChatRoom(String roomID, Long expiredAt, TapCommonListener listener) {
        List<String> roomIds = new ArrayList<>();
        roomIds.add(roomID);
        muteChatRooms(roomIds, expiredAt, listener);
    }

    public void unmuteChatRooms(List<String> roomIDs, TapCommonListener listener) {
        TAPDataManager.getInstance(instanceKey).unmuteRoom(roomIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                super.onSuccess(response);
                HashMap<String, Long> mutedRooms = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs();
                for (String roomId : response.getUnreadRoomIDs()) {
                    mutedRooms.remove(roomId);
                }
                TAPDataManager.getInstance(instanceKey).saveMutedRoomIDs(mutedRooms);
                listener.onSuccess("Successfully unmute rooms");
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

    public void unmuteChatRoom(String roomID, TapCommonListener listener) {
        List<String> roomIds = new ArrayList<>();
        roomIds.add(roomID);
        unmuteChatRooms(roomIds, listener);
    }

    public Integer getMaxPinnedRoom() {
        Map<String, String> coreConfigs = TapTalk.getCoreConfigs(instanceKey);
        if (coreConfigs != null) {
            String maxPinnedRoom = coreConfigs.get(ROOM_MAX_PINNED);
            if (maxPinnedRoom != null) {
                return Integer.valueOf(maxPinnedRoom);
            }
        }
        return Integer.valueOf(DEFAULT_ROOM_MAX_PINNED);
    }

    public void getPinnedChatRoomIDs(TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getPinnedRoomIds(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                ArrayList<String> roomIDs = new ArrayList<>(response.getUnreadRoomIDs());
                TAPDataManager.getInstance(instanceKey).savePinnedRoomIDs(roomIDs);
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

    public void pinChatRooms(List<String> roomIDs, TapCommonListener listener) {
        int pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().size();
        if (pinnedRooms < getMaxPinnedRoom()) {
            TAPDataManager.getInstance(instanceKey).pinRoom(roomIDs, new TAPDefaultDataView<>() {
                @Override
                public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                    super.onSuccess(response);
                    ArrayList<String> pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs();
                    for (String roomId : response.getUnreadRoomIDs()) {
                        if (!pinnedRooms.contains(roomId)) {
                            pinnedRooms.add(0, roomId);
                        }
                    }
                    TAPDataManager.getInstance(instanceKey).savePinnedRoomIDs(pinnedRooms);
                    listener.onSuccess("Successfully pin rooms");
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
        } else {
            listener.onError(ERROR_CODE_EXCEEDED_MAX_SIZE, "You have reached the maximum of pinned chat rooms.");
        }
    }

    public void pinChatRoom(String roomID, TapCommonListener listener) {
        List<String> roomIds = new ArrayList<>();
        roomIds.add(roomID);
        pinChatRooms(roomIds, listener);
    }

    public void unpinChatRooms(List<String> roomIDs, TapCommonListener listener) {
        TAPDataManager.getInstance(instanceKey).unpinRoom(roomIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                super.onSuccess(response);
                ArrayList<String> pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs();
                for (String roomId : response.getUnreadRoomIDs()) {
                    pinnedRooms.remove(roomId);
                }
                TAPDataManager.getInstance(instanceKey).savePinnedRoomIDs(pinnedRooms);
                listener.onSuccess("Successfully unpin rooms");
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

    public void unpinChatRoom(String roomID, TapCommonListener listener) {
        List<String> roomIds = new ArrayList<>();
        roomIds.add(roomID);
        unpinChatRooms(roomIds, listener);
    }
}
