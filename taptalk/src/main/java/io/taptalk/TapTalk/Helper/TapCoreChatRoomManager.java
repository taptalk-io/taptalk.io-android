package io.taptalk.TapTalk.Helper;

import android.net.Uri;

import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Interface.TapCommonInterface;
import io.taptalk.TapTalk.Interface.TapCreateGroupWithPictureInterface;
import io.taptalk.TapTalk.Interface.TapGetRoomInterface;
import io.taptalk.TapTalk.Interface.TapReceiveRoomStatusInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ADMIN_REQUIRED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_GROUP_DELETED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ADMIN_REQUIRED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_GROUP_DELETED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_DELETE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_LEAVE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapCoreChatRoomManager {

    public static void addRoomStatusListener(TapReceiveRoomStatusInterface listener) {
        TAPChatManager.getInstance().addChatListener(new TAPChatListener() {
            @Override
            public void onReceiveStartTyping(TAPTypingModel typingModel) {
                listener.onReceiveStartTyping(typingModel.getRoomID(), typingModel.getUser());
            }

            @Override
            public void onReceiveStopTyping(TAPTypingModel typingModel) {
                listener.onReceiveStopTyping(typingModel.getRoomID(), typingModel.getUser());
            }

            @Override
            public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
                listener.onReceiveOnlineStatus(onlineStatus.getUser(), onlineStatus.getOnline(), onlineStatus.getLastActive());
            }
        });
    }

    public static void getPersonalChatRoom(TAPUserModel recipientUser, TapGetRoomInterface listener) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
        }
        try {
            TAPRoomModel roomModel = TAPRoomModel.Builder(
                    TAPChatManager.getInstance().arrangeRoomId(
                            TAPChatManager.getInstance().getActiveUser().getUserID(),
                            recipientUser.getUserID()),
                    recipientUser.getName(),
                    TYPE_PERSONAL,
                    recipientUser.getAvatarURL(),
                    "");
            listener.onSuccess(roomModel);
        } catch (Exception e) {
            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
        }
    }

    public static void getPersonalChatRoom(String recipientUserID, TapGetRoomInterface listener) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
        }
        TAPUserModel recipient = TAPContactManager.getInstance().getUserData(recipientUserID);
        if (null == recipient) {
            TAPDataManager.getInstance().getUserByIdFromApi(recipientUserID, new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    getPersonalChatRoom(response.getUser(), listener);
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
        } else {
            getPersonalChatRoom(recipient, listener);
        }
    }

    public static void createGroupChatRoom(String groupName, List<String> participantUserIDs, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                listener.onSuccess(room);
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

    public static void createGroupChatRoomWithPicture(String groupName, List<String> participantUserIDs, Uri groupPictureUri, TapCreateGroupWithPictureInterface listener) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                if (null == response.getRoom()) {
                    listener.onSuccess(response.getRoom(), false);
                } else {
                    TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                    TAPFileUploadManager.getInstance().uploadRoomPicture(TapTalk.appContext,
                            groupPictureUri, response.getRoom().getRoomID(), new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                                @Override
                                public void onSuccess(TAPUpdateRoomResponse response) {
                                    TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                                    listener.onSuccess(room, true);
                                }

                                @Override
                                public void onError(TAPErrorModel error) {
                                    listener.onSuccess(response.getRoom(), false);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    listener.onSuccess(response.getRoom(), false);
                                }
                            });
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

    public static void updateGroupPicture(String groupRoomID, Uri groupPictureUri, TapGetRoomInterface listener) {
        TAPFileUploadManager.getInstance().uploadRoomPicture(TapTalk.appContext,
                groupPictureUri, groupRoomID, new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                    @Override
                    public void onSuccess(TAPUpdateRoomResponse response) {
                        TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                        listener.onSuccess(room);
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

    public static void updateGroupChatRoom(String groupRoomID, String groupName, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().updateChatRoom(groupRoomID, groupName,
                new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                    @Override
                    public void onSuccess(TAPUpdateRoomResponse response) {
                        TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                        listener.onSuccess(room);
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

    public static void getGroupChatRoom(String groupRoomID, TapGetRoomInterface listener) {
        TAPRoomModel roomModel = TAPGroupManager.Companion.getGetInstance().getGroupData(groupRoomID);
        if (null == roomModel) {
            TAPDataManager.getInstance().getChatRoomData(groupRoomID, new TAPDefaultDataView<TAPCreateRoomResponse>() {
                @Override
                public void onSuccess(TAPCreateRoomResponse response) {
                    TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                    listener.onSuccess(room);
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
        } else {
            listener.onSuccess(roomModel);
        }
    }

    public static void deleteGroupChatRoom(TAPRoomModel groupChatRoomModel, TapCommonInterface listener) {
        TAPDataManager.getInstance().deleteChatRoom(groupChatRoomModel, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse tapCommonResponse, String localID) {
                if (tapCommonResponse.getSuccess()) {
                    TAPOldDataManager.getInstance().startCleanRoomPhysicalData(groupChatRoomModel.getRoomID(), new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            TAPDataManager.getInstance().deleteMessageByRoomId(groupChatRoomModel.getRoomID(), new TAPDatabaseListener() {
                                @Override
                                public void onDeleteFinished() {
                                    TAPGroupManager.Companion.getGetInstance().removeGroupData(groupChatRoomModel.getRoomID());
                                    listener.onSuccess(SUCCESS_MESSAGE_DELETE_GROUP);
                                }
                            });
                        }
                    });
                } else {
                    listener.onError(ERROR_CODE_GROUP_DELETED, ERROR_MESSAGE_GROUP_DELETED);
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

    public static void leaveGroupChatRoom(String groupRoomID, TapCommonInterface listener) {
        TAPDataManager.getInstance().leaveChatRoom(groupRoomID, new TAPDefaultDataView<TAPCommonResponse>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (response.getSuccess()) {
                    TAPOldDataManager.getInstance().startCleanRoomPhysicalData(groupRoomID, new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            TAPDataManager.getInstance().deleteMessageByRoomId(groupRoomID, new TAPDatabaseListener() {
                                @Override
                                public void onDeleteFinished() {
                                    TAPGroupManager.Companion.getGetInstance().removeGroupData(groupRoomID);
                                    listener.onSuccess(SUCCESS_MESSAGE_LEAVE_GROUP);
                                }
                            });
                        }
                    });
                } else {
                    listener.onError(ERROR_CODE_ADMIN_REQUIRED, ERROR_MESSAGE_ADMIN_REQUIRED);
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

    public static void addGroupChatMembers(String groupRoomID, List<String> userIDs, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().addRoomParticipant(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                listener.onSuccess(room);
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

    public static void removeGroupChatMembers(String groupRoomID, List<String> userIDs, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().removeRoomParticipant(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                listener.onSuccess(room);
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

    public static void promoteGroupAdmins(String groupRoomID, List<String> userIDs, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().promoteGroupAdmins(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                listener.onSuccess(room);
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

    public static void demoteGroupAdmins(String groupRoomID, List<String> userIDs, TapGetRoomInterface listener) {
        TAPDataManager.getInstance().demoteGroupAdmins(groupRoomID, userIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPRoomModel room = TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);
                listener.onSuccess(room);
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

    public static void sendStartTypingEmit(String roomID) {
        TAPChatManager.getInstance().sendStartTypingEmit(roomID);
    }

    public static void sendStopTypingEmit(String roomID) {
        TAPChatManager.getInstance().sendStopTypingEmit(roomID);
    }
}
