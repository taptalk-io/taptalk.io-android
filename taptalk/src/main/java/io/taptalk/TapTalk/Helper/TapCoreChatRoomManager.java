package io.taptalk.TapTalk.Helper;

import android.net.Uri;

import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Interface.TapCreateGroupWithPictureInterface;
import io.taptalk.TapTalk.Interface.TapRoomInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapCoreChatRoomManager {

    public static void getPersonalChatRoomModel(TAPUserModel recipientUserModel, TapRoomInterface roomInterface) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            roomInterface.onError(TapTalk.appContext.getString(R.string.tap_error_active_user_not_found));
        }
        try {
            TAPRoomModel roomModel = TAPRoomModel.Builder(
                    TAPChatManager.getInstance().arrangeRoomId(
                            TAPChatManager.getInstance().getActiveUser().getUserID(),
                            recipientUserModel.getUserID()),
                    recipientUserModel.getName(),
                    TYPE_PERSONAL,
                    recipientUserModel.getAvatarURL(),
                    ""); // TODO: 1 August 2019 DEFINE ROOM COLOR
            roomInterface.onSuccess(roomModel);
        } catch (Exception e) {
            roomInterface.onError(e.getMessage());
        }
    }

    public static void getPersonalChatRoomModel(String recipientID, TapRoomInterface roomInterface) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            roomInterface.onError(TapTalk.appContext.getString(R.string.tap_error_active_user_not_found));
        }
        TAPUserModel recipient = TAPContactManager.getInstance().getUserData(recipientID);
        if (null == recipient) {
            TAPDataManager.getInstance().getUserByIdFromApi(recipientID, new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    getPersonalChatRoomModel(response.getUser(), roomInterface);
                }

                @Override
                public void onError(TAPErrorModel error) {
                    roomInterface.onError(error.getMessage());
                }

                @Override
                public void onError(String errorMessage) {
                    roomInterface.onError(errorMessage);
                }
            });
        } else {
            getPersonalChatRoomModel(recipient, roomInterface);
        }
    }

    public static void createGroupChatRoom(String groupName, List<String> participantUserIDs, TapRoomInterface roomInterface) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                roomInterface.onSuccess(response.getRoom());
            }

            @Override
            public void onError(TAPErrorModel error) {
                roomInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                roomInterface.onError(errorMessage);
            }
        });
    }

    public static void createGroupChatRoomWithPicture(String groupName, List<String> participantUserIDs, Uri groupPictureUri, TapCreateGroupWithPictureInterface roomInterface) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantUserIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                if (null == response.getRoom()) {
                    roomInterface.onSuccess(response.getRoom(), false);
                } else {
                    TAPFileUploadManager.getInstance().uploadRoomPicture(TapTalk.appContext,
                            groupPictureUri, response.getRoom().getRoomID(), new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                                @Override
                                public void onSuccess(TAPUpdateRoomResponse response) {
                                    roomInterface.onSuccess(response.getRoom(), true);
                                }

                                @Override
                                public void onError(TAPErrorModel error) {
                                    roomInterface.onSuccess(response.getRoom(), false);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    roomInterface.onSuccess(response.getRoom(), false);
                                }
                            });
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                roomInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                roomInterface.onError(errorMessage);
            }
        });
    }

    public static void updateGroupPicture(String groupID, Uri groupPictureUri, TapRoomInterface roomInterface) {
        TAPFileUploadManager.getInstance().uploadRoomPicture(TapTalk.appContext,
                groupPictureUri, groupID, new TAPDefaultDataView<TAPUpdateRoomResponse>() {
                    @Override
                    public void onSuccess(TAPUpdateRoomResponse response) {
                        roomInterface.onSuccess(response.getRoom());
                    }

                    @Override
                    public void onError(TAPErrorModel error) {
                        roomInterface.onError(error.getMessage());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        roomInterface.onError(errorMessage);
                    }
                });
    }

    public static void getGroupChatRoomModel(String groupRoomID, TapRoomInterface roomInterface) {
        TAPRoomModel roomModel = TAPGroupManager.Companion.getGetInstance().getGroupData(groupRoomID);
        if (null == roomModel) {
            TAPDataManager.getInstance().getChatRoomData(groupRoomID, new TAPDefaultDataView<TAPCreateRoomResponse>() {
                @Override
                public void onSuccess(TAPCreateRoomResponse response) {
                    roomInterface.onSuccess(response.getRoom());
                }

                @Override
                public void onError(TAPErrorModel error) {
                    roomInterface.onError(error.getMessage());
                }

                @Override
                public void onError(String errorMessage) {
                    roomInterface.onError(errorMessage);
                }
            });
        } else {
            roomInterface.onSuccess(roomModel);
        }
    }

    public static void sendStartTypingEmit(String roomID) {
        TAPChatManager.getInstance().sendStartTypingEmit(roomID);
    }

    public static void sendStopTypingEmit(String roomID) {
        TAPChatManager.getInstance().sendStopTypingEmit(roomID);
    }
}
