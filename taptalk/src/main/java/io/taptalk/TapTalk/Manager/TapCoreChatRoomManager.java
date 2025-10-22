package io.taptalk.TapTalk.Manager;

import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreChatRoomListener;
import io.taptalk.TapTalk.Listener.TapCoreCreateGroupWithPictureListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.PERMISSION_DENIED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.ROOM_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ADMIN_REQUIRED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_GROUP_DELETED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ADMIN_REQUIRED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_GROUP_DELETED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_DELETE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_LEAVE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

@Keep
public class TapCoreChatRoomManager {

    private static HashMap<String, TapCoreChatRoomManager> instances;

    private String instanceKey = "";
    private List<TapCoreChatRoomListener> coreChatRoomListeners;
    private TAPChatListener chatListener;

    public TapCoreChatRoomManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapCoreChatRoomManager getInstance() {
        return getInstance("");
    }

    public static TapCoreChatRoomManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapCoreChatRoomManager instance = new TapCoreChatRoomManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapCoreChatRoomManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    private List<TapCoreChatRoomListener> getCoreChatRoomListeners() {
        return null == coreChatRoomListeners ? coreChatRoomListeners = new ArrayList<>() : coreChatRoomListeners;
    }

    public synchronized void addChatRoomListener(TapCoreChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        if (getCoreChatRoomListeners().isEmpty()) {
            if (null == chatListener) {
                chatListener = new TAPChatListener() {
                    @Override
                    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {
                        for (TapCoreChatRoomListener listener : getCoreChatRoomListeners()) {
                            if (null != listener) {
                                listener.onReceiveUpdatedChatRoomData(room, recipientUser);
                            }
                        }
                    }

                    @Override
                    public void onReceiveStartTyping(TAPTypingModel typingModel) {
                        for (TapCoreChatRoomListener listener : getCoreChatRoomListeners()) {
                            if (null != listener) {
                                listener.onReceiveStartTyping(typingModel.getRoomID(), typingModel.getUser());
                            }
                        }
                    }

                    @Override
                    public void onReceiveStopTyping(TAPTypingModel typingModel) {
                        for (TapCoreChatRoomListener listener : getCoreChatRoomListeners()) {
                            if (null != listener) {
                                listener.onReceiveStopTyping(typingModel.getRoomID(), typingModel.getUser());
                            }
                        }
                    }

                    @Override
                    public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
                        for (TapCoreChatRoomListener listener : getCoreChatRoomListeners()) {
                            if (null != listener) {
                                listener.onReceiveOnlineStatus(onlineStatus.getUser(), onlineStatus.getOnline(), onlineStatus.getLastActive());
                            }
                        }
                    }
                };
            }
            TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
        }
        getCoreChatRoomListeners().remove(listener);
        getCoreChatRoomListeners().add(listener);
    }

    public synchronized void removeChatRoomListener(TapCoreChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getCoreChatRoomListeners().remove(listener);
        if (getCoreChatRoomListeners().isEmpty()) {
            TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        }
    }

    public TAPRoomModel getActiveChatRoom() {
        return TAPChatManager.getInstance(instanceKey).getActiveRoom();
    }

    public void getPersonalChatRoom(TAPUserModel recipientUser, TapCoreGetRoomListener listener) {
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
        }
        try {
            TAPRoomModel roomModel = TAPRoomModel.Builder(
                    TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                            TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                            recipientUser.getUserID()),
                    recipientUser.getFullname(),
                    TYPE_PERSONAL,
                    recipientUser.getImageURL(),
                    "");
            if (null != listener) {
                listener.onSuccess(roomModel);
            }
        } catch (Exception e) {
            if (null != listener) {
                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
            }
        }
    }

    public void getPersonalChatRoom(String recipientUserID, TapCoreGetRoomListener listener) {
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
        }
        TAPUserModel recipient = TAPContactManager.getInstance(instanceKey).getUserData(recipientUserID);
        if (null == recipient) {
            TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(recipientUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    getPersonalChatRoom(response.getUser(), listener);
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
            getPersonalChatRoom(recipient, listener);
        }
    }

    public void createGroupChatRoom(String groupName, List<String> participantUserIDs, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
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

    public void createGroupChatRoomWithPicture(String groupName, List<String> participantUserIDs, Uri groupPictureUri, TapCoreCreateGroupWithPictureListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                if (null == response.getRoom()) {
                    if (null != listener) {
                        listener.onSuccess(response.getRoom(), false);
                    }
                } else {
                    TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                    TAPFileUploadManager.getInstance(instanceKey).uploadRoomPicture(TapTalk.appContext,
                            groupPictureUri, response.getRoom().getRoomID(), new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                                @Override
                                public void onSuccess(TAPUpdateRoomResponse response) {
                                    TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                                    if (null != listener) {
                                        listener.onSuccess(room, true);
                                    }
                                }

                                @Override
                                public void onError(TAPErrorModel error) {
                                    if (null != listener) {
                                        listener.onSuccess(response.getRoom(), false);
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    if (null != listener) {
                                        listener.onSuccess(response.getRoom(), false);
                                    }
                                }
                            });
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

    public void updateGroupPicture(String groupRoomID, Uri groupPictureUri, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).uploadRoomPicture(TapTalk.appContext,
                groupPictureUri, groupRoomID, new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                    @Override
                    public void onSuccess(TAPUpdateRoomResponse response) {
                        TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                        if (null != listener) {
                            listener.onSuccess(room);
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

    public void updateGroupChatRoom(String groupRoomID, String groupName, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).updateChatRoom(groupRoomID, groupName,
                new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                    @Override
                    public void onSuccess(TAPUpdateRoomResponse response) {
                        TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                        if (null != listener) {
                            listener.onSuccess(room);
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

    @Nullable
    public TAPRoomModel getLocalGroupChatRoom(String groupRoomID) {
        return TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(groupRoomID);
    }

    @Nullable
    public TAPRoomModel getLocalChatRoomData(String roomID) {
        TAPRoomModel groupRoom = getLocalGroupChatRoom(roomID);
        if (groupRoom != null) {
            return groupRoom;
        }
        if (roomID.contains("-")) {
            String otherUserID = TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(roomID);
            TAPUserModel otherUser = TAPContactManager.getInstance(instanceKey).getUserData(otherUserID);
            if (otherUser != null) {
                return new TAPRoomModel(
                    roomID,
                    otherUser.getFullname(),
                    TYPE_PERSONAL,
                    otherUser.getImageURL(),
                    ""
                );
            }
        }
        return null;
    }

    @Deprecated
    /**
     * @deprecated Use {@link #getChatRoomData(String, TapCoreGetRoomListener)} instead.
     */
    public void getGroupChatRoom(String groupRoomID, TapCoreGetRoomListener listener) {
        getChatRoomData(groupRoomID, listener);
    }

    public void getChatRoomData(String roomID, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getChatRoomData(roomID, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (error.getCode().equals(PERMISSION_DENIED)) {
                    if (null != listener) {
                        listener.onError(error.getCode(), error.getMessage());
                    }
                }
                else if (error.getCode().equals(ROOM_NOT_FOUND)) {
                    TAPGroupManager.Companion.getInstance(instanceKey).removeGroupData(roomID);
                    if (null != listener) {
                        listener.onError(error.getCode(), error.getMessage());
                    }
                }
                else if (null != listener) {
                    TAPRoomModel roomModel = getLocalGroupChatRoom(roomID);
                    if (null != roomModel) {
                        listener.onSuccess(roomModel);
                    } else {
                        listener.onError(error.getCode(), error.getMessage());
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                onError(new TAPErrorModel(ERROR_CODE_OTHERS, errorMessage, ""));
            }
        });
    }

    public void getChatRoomByXcRoomID(String xcRoomID, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPRoomModel roomModel = getLocalGroupChatRoom(xcRoomID);
        if (null == roomModel) {
            TAPDataManager.getInstance(instanceKey).getChatRoomByXcRoomID(xcRoomID, new TAPDefaultDataView<TAPCreateRoomResponse>() {
                @Override
                public void onSuccess(TAPCreateRoomResponse response) {
                    TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                    if (null != listener) {
                        listener.onSuccess(room);
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
        } else {
            if (null != listener) {
                listener.onSuccess(roomModel);
            }
        }
    }

    public void deleteLocalGroupChatRoom(String roomID, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(roomID, new TAPDatabaseListener() {
            @Override
            public void onDeleteFinished() {
                TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(roomID, new TAPDatabaseListener() {
                    @Override
                    public void onDeleteFinished() {
                        TAPGroupManager.Companion.getInstance(instanceKey).removeGroupData(roomID);
                        TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
                        if (null != listener) {
                            listener.onSuccess(SUCCESS_MESSAGE_DELETE_GROUP);
                        }
                    }
                });
            }
        });
    }

    public void deleteGroupChatRoom(TAPRoomModel groupChatRoomModel, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).deleteChatRoom(groupChatRoomModel, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (response.getSuccess()) {
                    deleteLocalGroupChatRoom(groupChatRoomModel.getRoomID(), listener);
                } else {
                    if (null != listener) {
                        listener.onError(ERROR_CODE_GROUP_DELETED, ERROR_MESSAGE_GROUP_DELETED);
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

    public void leaveGroupChatRoom(String groupRoomID, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).leaveChatRoom(groupRoomID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (response.getSuccess()) {
                    TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(groupRoomID, new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(groupRoomID, new TAPDatabaseListener() {
                                @Override
                                public void onDeleteFinished() {
                                    TAPGroupManager.Companion.getInstance(instanceKey).removeGroupData(groupRoomID);
                                    if (null != listener) {
                                        listener.onSuccess(SUCCESS_MESSAGE_LEAVE_GROUP);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    if (null != listener) {
                        listener.onError(ERROR_CODE_ADMIN_REQUIRED, ERROR_MESSAGE_ADMIN_REQUIRED);
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

    public void addGroupChatMembers(String groupRoomID, List<String> userIDs, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).addRoomParticipant(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
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

    public void removeGroupChatMembers(String groupRoomID, List<String> userIDs, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).removeRoomParticipant(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
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

    public void promoteGroupAdmins(String groupRoomID, List<String> userIDs, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).promoteGroupAdmins(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
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

    public void demoteGroupAdmins(String groupRoomID, List<String> userIDs, TapCoreGetRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).demoteGroupAdmins(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).updateGroupDataFromResponse(response);
                if (null != listener) {
                    listener.onSuccess(room);
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

    public void sendStartTypingEmit(String roomID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendStartTypingEmit(roomID);
    }

    public void sendStopTypingEmit(String roomID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendStopTypingEmit(roomID);
    }

    public void getSavedMessagesChatRoom(TapCoreGetRoomListener listener) {
        if (TAPChatManager.getInstance(instanceKey).getActiveUser() == null) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        String userId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
        TAPRoomModel room = TAPRoomModel.Builder(String.format("%s-%s", userId, userId), TapTalk.appContext.getString(R.string.tap_saved_messages), TYPE_PERSONAL, new TAPImageURL("", ""), "");
        if (null != listener) {
            listener.onSuccess(room);
        }
    }

    public void deleteAllChatRoomMessages(String roomID, TapCommonListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        List<String> roomIDs = new ArrayList<>();
        roomIDs.add(roomID);
        TAPDataManager.getInstance(instanceKey).clearChat(roomIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetUnreadRoomIdsResponse response) {
                TapCoreChatRoomManager.getInstance(instanceKey).deleteLocalGroupChatRoom(roomID, new TapCommonListener() {
                    @Override
                    public void onSuccess(String successMessage) {
                        TAPDataManager.getInstance(instanceKey).saveLastRoomMessageDeleteTime();
                        TAPDataManager.getInstance(instanceKey).removePinnedRoomID(roomID);
                        TAPDataManager.getInstance(instanceKey).removeStarredMessageIds(roomID);
                        TAPDataManager.getInstance(instanceKey).removePinnedMessageIds(roomID);
                        TAPDataManager.getInstance(instanceKey).removeUnreadRoomID(roomID);
                        if (null != listener) {
                            listener.onSuccess("Successfully deleted chat room messages.");
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
