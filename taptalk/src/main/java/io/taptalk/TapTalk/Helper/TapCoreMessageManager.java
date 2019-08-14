package io.taptalk.TapTalk.Helper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Interface.TapFileDownloadInterface;
import io.taptalk.TapTalk.Interface.TapGetMessageInterface;
import io.taptalk.TapTalk.Interface.TapReceiveMessageInterface;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorCode;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;

public class TapCoreMessageManager {

    public static void addMessageListener(TapReceiveMessageInterface listener) {
        TAPChatManager.getInstance().addChatListener(new TAPChatListener() {
            @Override
            public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                listener.onReceiveMessageInActiveRoom(message);
            }

            @Override
            public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                listener.onReceiveMessageInOtherRoom(message);
            }

            @Override
            public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                listener.onUpdateMessageInActiveRoom(message);
            }

            @Override
            public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                listener.onUpdateMessageInOtherRoom(message);
            }
        });
    }

    public static void sendTextMessage(String message, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(message, room, listener);
    }

    public static void sendTextMessage(String message, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendTextMessageWithRoomModel(message, room, listener);
    }

    public static void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendLocationMessage(address, latitude, longitude, room, listener);
    }
    public static void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public static void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public static void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }
    public static void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public static void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public static void sendVideoMessage(Uri uri, String caption, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendVideoMessage(TapTalk.appContext, room, uri, caption, listener);
    }

    public static void sendVideoMessage(Uri uri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendVideoMessage(TapTalk.appContext, room, uri, caption, listener);
    }

    public static void sendFileMessage(File file, TAPRoomModel room, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public static void sendFileMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), quotedMessage, TAPDefaultConstant.QuoteAction.REPLY);
        TAPChatManager.getInstance().sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public static void sendForwardedMessage(TAPMessageModel messageToForward, TapSendMessageInterface listener) {
        TAPChatManager.getInstance().setQuotedMessage(messageToForward.getRoom().getRoomID(), messageToForward, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance().checkAndSendForwardedMessage(messageToForward.getRoom(), listener);
    }

    public static void deleteLocalMessage(String localID) {
        TAPDataManager.getInstance().deleteFromDatabase(localID);
    }

    public static void cancelMessageFileUpload(TAPMessageModel message) {
        TAPFileUploadManager.getInstance().cancelUpload(TapTalk.appContext, message, message.getRoom().getRoomID());
        TAPDataManager.getInstance().deleteFromDatabase(message.getLocalID());
    }

    public static void downloadMessageFile(TAPMessageModel message, TapFileDownloadInterface listener) {
        if (!TAPUtils.getInstance().hasPermissions(TapTalk.appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listener.onError(ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE, ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE);
        } else {
            TAPFileDownloadManager.getInstance().downloadFile(TapTalk.appContext, message);

            BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String localID = intent.getStringExtra(DownloadLocalID);
                    Integer downloadProgressPercent = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(localID);
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance().getDownloadProgressBytes(localID);
                    if (null == action) {
                        return;
                    }
                    switch (action) {
                        case DownloadProgressLoading:
                            if (null != downloadProgressPercent && null != downloadProgressBytes) {
                                listener.onProgress(message, downloadProgressPercent, downloadProgressBytes);
                            }
                            break;
                        case DownloadFinish:
                            listener.onSuccess(intent.getParcelableExtra(DownloadedFile));
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                        case DownloadFailed:
                            listener.onError(intent.getStringExtra(DownloadErrorCode), intent.getStringExtra(DownloadErrorMessage));
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                    }
                }
            };

            TAPBroadcastManager.register(TapTalk.appContext, downloadProgressReceiver,
                    DownloadProgressLoading, DownloadFinish, DownloadFailed);
        }
    }

    public static void markMessageAsRead(TAPMessageModel message) {
        TAPMessageStatusManager.getInstance().addReadMessageQueue(message);
    }

    public static void getOlderMessagesBeforeTimestamp(String roomID, long maxCreatedTimestamp, int numberOfItems, TapGetMessageInterface listener) {
        TAPDataManager.getInstance().getMessageListByRoomBefore(roomID, maxCreatedTimestamp, numberOfItems,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                        listener.onSuccess(messageAfterModels);
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

    public static void getNewerMessagesAfterTimestamp(String roomID, long minCreatedTimestamp, long lastUpdateTimestamp, TapGetMessageInterface listener) {
        TAPDataManager.getInstance().getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                            }
                        }
                        listener.onSuccess(messageAfterModels);
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

    public static void getNewerMessages(String roomID, TapGetMessageInterface listener) {
        TAPDataManager.getInstance().getMessagesFromDatabaseAsc(roomID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                long lastUpdateTimestamp = TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(roomID);
                long minCreatedTimestamp = 0L;
                if (entities.size() > 0) {
                    minCreatedTimestamp = entities.get(0).getCreated();
                }
                TAPDataManager.getInstance().getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                        new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                            @Override
                            public void onSuccess(TAPGetMessageListByRoomResponse response) {
                                List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                                for (HashMap<String, Object> messageMap : response.getMessages()) {
                                    try {
                                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                        messageAfterModels.add(message);
                                        if (null != message.getUpdated() &&
                                                TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(roomID) < message.getUpdated()) {
                                            TAPDataManager.getInstance().saveLastUpdatedMessageTimestamp(roomID, message.getUpdated());
                                        }
                                    } catch (Exception e) {
                                        listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                    }
                                }
                                listener.onSuccess(messageAfterModels);
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

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }
}
