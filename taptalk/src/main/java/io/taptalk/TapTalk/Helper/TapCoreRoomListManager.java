package io.taptalk.TapTalk.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapMessageInterface;
import io.taptalk.TapTalk.API.View.TapRoomListInterface;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
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

public class TapCoreRoomListManager {

    public static void fetchNewMessage(TapMessageInterface tapMessageInterface) {
        TAPDataManager.getInstance().getNewAndUpdatedMessage(new TAPDefaultDataView<TAPGetRoomListResponse>() {
            @Override
            public void onSuccess(TAPGetRoomListResponse response) {
                super.onSuccess(response);
                if (response.getMessages().size() > 0) {
                    List<TAPMessageEntity> tempMessage = new ArrayList<>();
                    List<String> userIds = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            TAPMessageEntity entity = TAPChatManager.getInstance().convertToEntity(message);
                            tempMessage.add(entity);

                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                                // User is self, get other user data from API
                                userIds.add(TAPChatManager.getInstance().getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            } else {
                                // Save user data to contact manager
                                TAPContactManager.getInstance().updateUserData(message.getUser());
                            }
                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance().deletePhysicalFile(entity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Get updated other user data from API
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

                    //hasil dari API disimpen ke dalem database
                    TAPDataManager.getInstance().insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                        @Override
                        public void onInsertFinished() {
                            List<TAPMessageModel> messages = new ArrayList<>();
                            for (HashMap<String, Object> h : response.getMessages()) {
                                messages.add(TAPEncryptorManager.getInstance().decryptMessage(h));
                            }
                            tapMessageInterface.onSuccess(messages);
                        }
                    });
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                tapMessageInterface.onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                tapMessageInterface.onError(errorMessage);
            }
        });
    }

    public static void getRoomListFromCache(TapRoomListInterface tapRoomListInterface) {
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                super.onSelectFinished(entities);
                List<TAPRoomListModel> roomListModel = new ArrayList<>();
                for (TAPMessageEntity e : entities) {
                    roomListModel.add(TAPRoomListModel.buildWithLastMessage(TAPChatManager.getInstance().convertToModel(e)));
                }
                tapRoomListInterface.onSuccess(roomListModel);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                tapRoomListInterface.onError(errorMessage);
            }
        });
    }

    public static void getUpdatedRoomList(TapRoomListInterface tapRoomListInterface) {
        if (null == TAPChatManager.getInstance().getActiveUser()) {
            tapRoomListInterface.onError("Active user not found");
            return;
        }
        TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                super.onSelectFinished(entities);
                if (entities.size() > 0) {
                    fetchNewMessage(new TapMessageInterface() {
                        @Override
                        public void onSuccess(List<TAPMessageModel> tapMessageModel) {
                            getRoomListFromCache(new TapRoomListInterface() {
                                @Override
                                public void onSuccess(List<TAPRoomListModel> tapRoomListModel) {
                                    tapRoomListInterface.onSuccess(tapRoomListModel);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    tapRoomListInterface.onError(errorMessage);
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            tapRoomListInterface.onError(errorMessage);
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
                            tapRoomListInterface.onSuccess(roomListModel);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            tapRoomListInterface.onError(errorMessage);
                        }

                        @Override
                        public void onError(TAPErrorModel error) {
                            tapRoomListInterface.onError(error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                tapRoomListInterface.onError(errorMessage);
            }
        });
    }
}