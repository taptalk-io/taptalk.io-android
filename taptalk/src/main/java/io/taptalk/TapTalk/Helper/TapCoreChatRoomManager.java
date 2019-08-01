package io.taptalk.TapTalk.Helper;

import android.net.Uri;

import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapRoomInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TapCoreChatRoomManager {

    public static void createPersonalRoomWithUser(TAPUserModel model) {

    }

    public static void createGroupRoom(String groupName, List<String> participantIDs, TapRoomInterface roomInterface) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
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

    public static void createGroupRoom(String groupName, List<String> participantIDs, Uri uri, TapRoomInterface roomInterface) {
        TAPDataManager.getInstance().createGroupChatRoom(groupName, participantIDs, new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                TAPFileUploadManager.getInstance().uploadRoomPicture(TapTalk.appContext,
                        uri, response.getRoom().getRoomID(), new TAPDefaultDataView<TAPUpdateRoomResponse>() {
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
}
