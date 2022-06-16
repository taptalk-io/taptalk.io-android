package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_CANCELLED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_PRODUCT_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_DOWNLOAD_CANCELLED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_PRODUCT_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorCode;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadErrorMessage;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_PRODUCT_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ITEMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.API.RequestBody.ProgressRequestBody;
import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCoreFileDownloadListener;
import io.taptalk.TapTalk.Listener.TapCoreFileUploadListener;
import io.taptalk.TapTalk.Listener.TapCoreGetAllMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreUpdateMessageStatusListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Model.RequestModel.TapGetStarredMessagesRequest;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapStarMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapUnstarMessageResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public class TapCoreMessageManager {

    private static HashMap<String, TapCoreMessageManager> instances;

    private List<TapCoreMessageListener> coreMessageListeners;
    private List<TAPMessageModel> pendingCallbackNewMessages;
    private List<TAPMessageModel> pendingCallbackUpdatedMessages;
    private List<TAPMessageModel> pendingCallbackDeletedMessages;
    private TAPChatListener chatListener;

    private String instanceKey = "";
    private Timer newMessageListenerBulkCallbackTimer;
    private Timer updatedMessageListenerBulkCallbackTimer;
    private Timer deletedMessageListenerBulkCallbackTimer;
    private boolean isUploadMessageFileToExternalServerEnabled;
    private long messageListenerBulkCallbackDelay = 0L;

    private static final int ITEM_LOAD_LIMIT = 500;

    public TapCoreMessageManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapCoreMessageManager getInstance() {
        return getInstance("");
    }

    public static TapCoreMessageManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapCoreMessageManager instance = new TapCoreMessageManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapCoreMessageManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    private List<TapCoreMessageListener> getCoreMessageListeners() {
        return null == coreMessageListeners ? coreMessageListeners = new ArrayList<>() : coreMessageListeners;
    }

    private List<TAPMessageModel> getPendingCallbackNewMessages() {
        return null == pendingCallbackNewMessages ? pendingCallbackNewMessages = new ArrayList<>() : pendingCallbackNewMessages;
    }

    private List<TAPMessageModel> getPendingCallbackUpdatedMessages() {
        return null == pendingCallbackUpdatedMessages ? pendingCallbackUpdatedMessages = new ArrayList<>() : pendingCallbackUpdatedMessages;
    }

    private List<TAPMessageModel> getPendingCallbackDeletedMessages() {
        return null == pendingCallbackDeletedMessages ? pendingCallbackDeletedMessages = new ArrayList<>() : pendingCallbackDeletedMessages;
    }

    private void startNewMessageListenerBulkCallbackTimer() {
        if (newMessageListenerBulkCallbackTimer != null) {
            newMessageListenerBulkCallbackTimer.cancel();
        }
        newMessageListenerBulkCallbackTimer = new Timer();
        newMessageListenerBulkCallbackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getPendingCallbackNewMessages().size() > 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onReceiveNewMessage(getPendingCallbackNewMessages());
                        }
                    }
                } else if (getPendingCallbackNewMessages().size() == 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onReceiveNewMessage(getPendingCallbackNewMessages().get(0));
                        }
                    }
                }
                getPendingCallbackNewMessages().clear();
                newMessageListenerBulkCallbackTimer.cancel();
            }
        }, messageListenerBulkCallbackDelay, messageListenerBulkCallbackDelay);
    }

    private void startUpdatedMessageListenerBulkCallbackTimer() {
        if (updatedMessageListenerBulkCallbackTimer != null) {
            updatedMessageListenerBulkCallbackTimer.cancel();
        }
        updatedMessageListenerBulkCallbackTimer = new Timer();
        updatedMessageListenerBulkCallbackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getPendingCallbackUpdatedMessages().size() > 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onReceiveUpdatedMessage(getPendingCallbackUpdatedMessages());
                        }
                    }
                } else if (getPendingCallbackUpdatedMessages().size() == 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onReceiveUpdatedMessage(getPendingCallbackUpdatedMessages().get(0));
                        }
                    }
                }
                getPendingCallbackUpdatedMessages().clear();
                updatedMessageListenerBulkCallbackTimer.cancel();
            }
        }, messageListenerBulkCallbackDelay, messageListenerBulkCallbackDelay);
    }

    private void startDeletedMessageListenerBulkCallbackTimer() {
        if (deletedMessageListenerBulkCallbackTimer != null) {
            deletedMessageListenerBulkCallbackTimer.cancel();
        }
        deletedMessageListenerBulkCallbackTimer = new Timer();
        deletedMessageListenerBulkCallbackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getPendingCallbackDeletedMessages().size() > 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onMessageDeleted(getPendingCallbackDeletedMessages());
                        }
                    }
                } else if (getPendingCallbackDeletedMessages().size() == 1) {
                    for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                        if (null != listener) {
                            listener.onMessageDeleted(getPendingCallbackDeletedMessages().get(0));
                        }
                    }
                }
                getPendingCallbackDeletedMessages().clear();
                deletedMessageListenerBulkCallbackTimer.cancel();
            }
        }, messageListenerBulkCallbackDelay, messageListenerBulkCallbackDelay);
    }

    public void addMessageListener(TapCoreMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        if (getCoreMessageListeners().isEmpty()) {
            if (null == chatListener) {
                chatListener = new TAPChatListener() {
                    @Override
                    public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                        if (messageListenerBulkCallbackDelay == 0L) {
                            for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                                if (null != listener) {
                                    listener.onReceiveNewMessage(message);
                                }
                            }
                        } else {
                            getPendingCallbackNewMessages().add(message);
                            startNewMessageListenerBulkCallbackTimer();
                        }
                    }

                    @Override
                    public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                        onReceiveMessageInActiveRoom(message);
                    }

                    @Override
                    public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                        if (null != message &&  null != message.getIsDeleted() && message.getIsDeleted()) {
                            onDeleteMessageInActiveRoom(message);
                            return;
                        }
                        if (messageListenerBulkCallbackDelay == 0L) {
                            for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                                if (null != listener) {
                                    listener.onReceiveUpdatedMessage(message);
                                }
                            }
                        } else {
                            getPendingCallbackUpdatedMessages().add(message);
                            startUpdatedMessageListenerBulkCallbackTimer();
                        }
                    }

                    @Override
                    public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                        onUpdateMessageInActiveRoom(message);
                    }

                    @Override
                    public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
                        if (messageListenerBulkCallbackDelay == 0L) {
                            for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                                if (null != listener) {
                                    listener.onMessageDeleted(message);
                                }
                            }
                        } else {
                            getPendingCallbackDeletedMessages().add(message);
                            startDeletedMessageListenerBulkCallbackTimer();
                        }
                    }

                    @Override
                    public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
                        onDeleteMessageInActiveRoom(message);
                    }
                };
            }
            TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
        }
        getCoreMessageListeners().remove(listener);
        getCoreMessageListeners().add(listener);
    }

    public void removeMessageListener(TapCoreMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getCoreMessageListeners().remove(listener);
        if (getCoreMessageListeners().isEmpty()) {
            TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        }
    }

    public long getMessageListenerBulkCallbackDelay() {
        return messageListenerBulkCallbackDelay;
    }

    public void setMessageListenerBulkCallbackDelay(long messageListenerBulkCallbackDelay) {
        this.messageListenerBulkCallbackDelay = messageListenerBulkCallbackDelay;
    }

    public void sendTextMessage(String messageBody, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendTextMessageWithRoomModel(messageBody, room, listener);
    }

    public void sendTextMessage(String messageBody, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendTextMessageWithRoomModel(messageBody, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendVideoMessage(TapTalk.appContext, room, videoUri, caption, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendVideoMessage(TapTalk.appContext, room, videoUri, caption, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendVoiceMessage(File file, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendVoiceMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendVoiceMessage(Uri uri, TAPRoomModel room, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendVoiceMessage(Uri uri, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        ArrayList<TAPMessageModel> messagesToForward = new ArrayList<>();
        messagesToForward.add(messageToForward);
        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messagesToForward, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
    }

    public void sendForwardedMessages(List<TAPMessageModel> messagesToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        ArrayList<TAPMessageModel> messages = new ArrayList<>(messagesToForward);
        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messages, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
    }

    public TAPMessageModel constructTapTalkMessageModel(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return TAPMessageModel.Builder(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData);
    }

    public TAPMessageModel constructTapTalkMessageModelWithQuote(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData, TAPMessageModel quotedMessage) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return TAPMessageModel.BuilderWithQuotedMessage(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData, quotedMessage);
    }

    public TAPMessageModel constructTapTalkMessageModelWithQuote(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData, String quoteTitle, String quoteContent, String quoteImageUrl) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return TAPMessageModel.BuilderWithQuote(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData, quoteTitle, quoteContent, quoteImageUrl);
    }

    public void sendCustomMessage(TAPMessageModel customMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendMessage(customMessage, listener);
    }

    public void deleteMessage(TAPMessageModel message) {
        TAPDataManager.getInstance(instanceKey).deleteMessagesAPI(message.getRoom().getRoomID(), message.getMessageID(), true);
    }

    public void deleteLocalMessage(String localID) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(localID);
    }

    public void editMessage(TAPMessageModel message, String updatedText, TapCoreSendMessageListener listener) {
        if (message.getType() == TYPE_TEXT) {
            message.setBody(updatedText);
        } else if (message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) {
            HashMap<String, Object> data = message.getData();
            if (data != null) {
                data.put(CAPTION, updatedText);
                message.setData(data);
            }
        } else {
            return;
        }
        TAPChatManager.getInstance(instanceKey).editMessage(message, updatedText, listener);
    }

    public void uploadImage(Context context, Uri uri, TapCoreFileUploadListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        try {
            TAPFileUploadManager.getInstance(instanceKey).uploadImage(context, uri, new ProgressRequestBody.UploadCallbacks() {
                @Override
                public void onProgressUpdate(int percentage, long bytes) {
                    if (null != listener) {
                        listener.onProgress(percentage, bytes);
                    }
                }

                @Override
                public void onError() {

                }

                @Override
                public void onFinish() {

                }
            }, new TAPDefaultDataView<TAPUploadFileResponse>() {
                @Override
                public void onSuccess(TAPUploadFileResponse response) {
                    if (null != listener && null != response) {
                        listener.onSuccess(response.getFileID(), response.getFileURL());
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
        } catch (Exception e) {
            if (null != listener) {
                listener.onError(ERROR_CODE_OTHERS, e.getLocalizedMessage());
            }
        }
    }

    public void cancelMessageFileUpload(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(TapTalk.appContext, message, message.getRoom().getRoomID());
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(message.getLocalID());
    }

    public void downloadMessageFile(TAPMessageModel message, TapCoreFileDownloadListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(message, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        if (!TAPUtils.hasPermissions(TapTalk.appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (null != listener) {
                listener.onError(message, ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE, ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE);
            }
        } else {
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message);

            BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (null == action) {
                        return;
                    }
                    String localID = intent.getStringExtra(DownloadLocalID);
                    if (null == localID || !localID.equals(message.getLocalID())) {
                        return;
                    }
                    switch (action) {
                        case DownloadProgressLoading:
                            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(localID);
                            Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(localID);
                            if (null != downloadProgressPercent && null != downloadProgressBytes) {
                                if (null != listener) {
                                    listener.onProgress(message, downloadProgressPercent, downloadProgressBytes);
                                }
                            }
                            break;
                        case DownloadFinish:
                            if (null != listener) {
                                File downloadedFile = null;
                                if (null != intent.getExtras()) {
                                    downloadedFile = (File) intent.getExtras().get(DownloadedFile);
                                }
                                listener.onSuccess(message, downloadedFile);
                            }
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                        case DownloadFailed:
                            if (null != listener) {
                                listener.onError(message, intent.getStringExtra(DownloadErrorCode), intent.getStringExtra(DownloadErrorMessage));
                            }
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                        case CancelDownload:
                            if (null != listener) {
                                listener.onError(message, ERROR_CODE_DOWNLOAD_CANCELLED, ERROR_MESSAGE_DOWNLOAD_CANCELLED);
                            }
                            TAPBroadcastManager.unregister(TapTalk.appContext, this);
                            break;
                    }
                }
            };

            TAPBroadcastManager.register(TapTalk.appContext, downloadProgressReceiver,
                    DownloadProgressLoading, DownloadFinish, DownloadFailed, CancelDownload);
        }
    }

    public void cancelMessageFileDownload(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        Intent intent = new Intent(CancelDownload);
        intent.putExtra(DownloadLocalID, message.getLocalID());
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(message.getLocalID());
    }

    public HashMap<String, String> constructTapTalkProductModel(
            String productID,
            String productName,
            String productCurrency,
            String productPrice,
            String productRating,
            String productWeight,
            String productDescription,
            String productImageURL,
            String leftOrSingleButtonOptionText,
            String rightButtonOptionText,
            String leftOrSingleButtonOptionColor,
            String rightButtonOptionColor) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        HashMap<String, String> product = new HashMap<>();
        product.put("id", null == productID ? "" : productID);
        product.put("name", null == productName ? "" : productName);
        product.put("currency", null == productCurrency ? "" : productCurrency);
        product.put("price", null == productPrice ? "" : productPrice);
        product.put("rating", null == productRating ? "" : productRating);
        product.put("weight", null == productWeight ? "" : productWeight);
        product.put("description", null == productDescription ? "" : productDescription);
        product.put("imageURL", null == productImageURL ? "" : productImageURL);
        product.put("buttonOption1Text", null == leftOrSingleButtonOptionText ? "" : leftOrSingleButtonOptionText);
        product.put("buttonOption2Text", null == rightButtonOptionText ? "" : rightButtonOptionText);
        product.put("buttonOption1Color", null == leftOrSingleButtonOptionColor ? "" : leftOrSingleButtonOptionColor);
        product.put("buttonOption2Color", null == rightButtonOptionColor ? "" : rightButtonOptionColor);
        return product;
    }

    public void sendProductMessage(List<HashMap<String, String>> products, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        if (null == products || products.size() == 0) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_PRODUCT_EMPTY, ERROR_MESSAGE_PRODUCT_EMPTY);
            }
            return;
        }
        if (products.size() > MAX_PRODUCT_SIZE) {
            products = products.subList(0, MAX_PRODUCT_SIZE);
        }
        HashMap<String, Object> productHashMap = new LinkedHashMap<>();
        productHashMap.put(ITEMS, new ArrayList<>(products));
        TAPChatManager.getInstance(instanceKey).sendProductMessageToServer(productHashMap, room, listener);
    }

    public void markMessageAsDelivered(String messageID) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        List<String> messageIDs = new ArrayList<>();
        messageIDs.add(messageID);
        markMessagesAsDelivered(messageIDs);
    }

    public void markMessageAsDelivered(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDeliveredFromNotification(message);
    }

    public void markMessagesAsDelivered(List<String> messageIDs) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).updateMessageStatusAsDelivered(
                messageIDs,
                new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
        });
    }

    public void markMessageAsRead(String messageID) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(messageID);
    }

    public void markMessageAsRead(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        markMessageAsRead(message.getMessageID());
    }

    public void markMessagesAsRead(List<String> messageIDs) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(messageIDs);
    }

    public void markMessagesAsRead(List<String> messageIDs, TapCoreUpdateMessageStatusListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).updateMessageStatusAsRead(messageIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPUpdateMessageStatusResponse response) {
                if (null != response.getUpdatedMessageIDs()) {
                    TAPDataManager.getInstance(instanceKey).updateMessagesAsReadInDatabase(messageIDs);
                    if (null != listener) {
                        listener.onSuccess(response.getUpdatedMessageIDs());
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

    public void markAllMessagesInRoomAsRead(String roomID) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(
                roomID,
                new TAPDatabaseListener<>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        List<String> pendingReadList = new ArrayList<>();
                        for (TAPMessageEntity entity : entities) {
                            if ((null == entity.getIsRead() || !entity.getIsRead()) &&
                                    !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(entity.getMessageID()) &&
                                    !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(entity.getMessageID())
                            ) {
                                // Add message ID to pending list if new message has not been read or not in mark read queue
                                pendingReadList.add(entity.getMessageID());
                            }
                        }
                        markMessagesAsRead(pendingReadList);
                    }
                });
        TapCoreRoomListManager.getInstance(instanceKey).removeUnreadMarkFromChatRoom(roomID, null);
    }

    public void markAllMessagesInRoomAsRead(String roomID, TapCoreUpdateMessageStatusListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(
                roomID,
                new TAPDatabaseListener<>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        if (entities == null || entities.isEmpty()) {
                            if (null != listener) {
                                listener.onSuccess(new ArrayList<>());
                            }
                        }
                        else {
                            List<String> pendingReadList = new ArrayList<>();
                            for (TAPMessageEntity entity : entities) {
                                if ((null == entity.getIsRead() || !entity.getIsRead()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(entity.getMessageID()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(entity.getMessageID())
                                ) {
                                    // Add message ID to pending list if new message has not been read or not in mark read queue
                                    pendingReadList.add(entity.getMessageID());
                                }
                            }
                            markMessagesAsRead(pendingReadList, listener);
                        }
                    }
                });
        TapCoreRoomListManager.getInstance(instanceKey).removeUnreadMarkFromChatRoom(roomID, null);
    }

//    private void markLastMessageInRoomAsRead(String roomID) {
//        ArrayList<String> roomIds = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs();
//        if (roomIds.contains(roomID)) {
//            TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(roomID, new TAPDatabaseListener<>() {
//                @Override
//                public void onSelectFinished(List<TAPMessageEntity> entities) {
//                    if (!entities.isEmpty()) {
//                        List<String> unreadList = new ArrayList<>();
//                        unreadList.add(entities.get(0).getMessageID());
//                        markMessagesAsRead(unreadList, null);
//                        roomIds.remove(roomID);
//                        TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(roomIds);
//                    }
//                }
//            });
//        }
//    }

    public void getLocalMessages(String roomID, TapCoreGetMessageListener listener) {
        getLocalMessages(roomID, false, listener);
    }

    public void getLocalMessages(String roomID, boolean excludeHidden, TapCoreGetMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getAllMessagesInRoomFromDatabase(roomID, excludeHidden, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                new Thread(() -> {
                    List<TAPMessageModel> messages = new ArrayList<>();
                    for (TAPMessageEntity entity : entities) {
                        TAPMessageModel message = TAPMessageModel.fromMessageEntity(entity);
                        messages.add(message);
                    }
                    if (null != listener) {
                        new Handler(Looper.getMainLooper()).post(() -> listener.onSuccess(messages));
                    }
                }).start();
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getLocalMessages(String roomID, long maxCreatedTimestamp, int numberOfItems, TapCoreGetMessageListener listener) {
        getLocalMessages(roomID, maxCreatedTimestamp, numberOfItems, false, listener);
    }

    public void getLocalMessages(String roomID, long maxCreatedTimestamp, int numberOfItems, boolean excludeHidden, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getRoomMessagesFromDatabaseBeforeTimestampDesc(roomID, maxCreatedTimestamp, numberOfItems, excludeHidden, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                new Thread(() -> {
                    List<TAPMessageModel> messages = new ArrayList<>();
                    for (TAPMessageEntity entity : entities) {
                        TAPMessageModel message = TAPMessageModel.fromMessageEntity(entity);
                        messages.add(message);
                    }
                    if (null != listener) {
                        new Handler(Looper.getMainLooper()).post(() -> listener.onSuccess(messages));
                    }
                }).start();
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getOlderMessagesBeforeTimestamp(String roomID, long maxCreatedTimestamp, int numberOfItems, TapCoreGetOlderMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getMessageListByRoomBefore(roomID, maxCreatedTimestamp, numberOfItems,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageBeforeModels = new ArrayList<>();
                        List<TAPMessageEntity> entities = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                messageBeforeModels.add(message);
                                entities.add(TAPMessageEntity.fromMessageModel(message));
                            } catch (Exception e) {
                                if (null != listener) {
                                    listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                }
                            }
                        }

                        TAPDataManager.getInstance(instanceKey).insertToDatabase(entities, false, new TAPDatabaseListener<TAPMessageEntity>() {
                            @Override
                            public void onInsertFinished() {
                                if (null != listener) {
                                    listener.onSuccess(messageBeforeModels, response.getHasMore());
                                }
                            }

                            @Override
                            public void onInsertFailed(String errorMessage) {
                                if (null != listener) {
                                    listener.onSuccess(messageBeforeModels, response.getHasMore());
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

    public void getNewerMessages(String roomID, TapCoreGetMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseAsc(roomID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                long minCreatedTimestamp;
                if (entities.size() > 0) {
                    minCreatedTimestamp = entities.get(0).getCreated();
                }
                else {
                    minCreatedTimestamp = 0L;
                }
                long lastUpdateTimestamp = TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID);
                if (lastUpdateTimestamp == 0L) {
                    lastUpdateTimestamp = minCreatedTimestamp;
                }
                getNewerMessagesAfterTimestamp(roomID, minCreatedTimestamp, lastUpdateTimestamp, listener);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public void getNewerMessagesAfterTimestamp(String roomID, long minCreatedTimestamp, long lastUpdateTimestamp, TapCoreGetMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }

        TAPDataManager.getInstance(instanceKey).getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        List<TAPMessageEntity> entities = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                                messageAfterModels.add(message);
                                entities.add(TAPMessageEntity.fromMessageModel(message));

                                if (null != message.getUpdated() &&
                                        TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID) < message.getUpdated()
                                ) {
                                    TAPDataManager.getInstance(instanceKey).saveLastUpdatedMessageTimestamp(roomID, message.getUpdated());
                                }
                            } catch (Exception e) {
                                if (null != listener) {
                                    listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                                }
                            }
                        }

                        if (0 < messageAfterModels.size()) {
                            TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(messageAfterModels);
                        }

                        TAPDataManager.getInstance(instanceKey).insertToDatabase(entities, false, new TAPDatabaseListener<TAPMessageEntity>() {
                            @Override
                            public void onInsertFinished() {
                                if (null != listener) {
                                    listener.onSuccess(messageAfterModels);
                                }
                            }

                            @Override
                            public void onInsertFailed(String errorMessage) {
                                if (null != listener) {
                                    listener.onSuccess(messageAfterModels);
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

    public void getAllOlderMessagesBeforeTimestamp(long maxCreatedTimestamp, String roomID, TapCoreGetMessageListener listener) {
        List<TAPMessageModel> resultMessages = new ArrayList<>();
        getOlderMessagesBeforeTimestamp(roomID, maxCreatedTimestamp, ITEM_LOAD_LIMIT, new TapCoreGetOlderMessageListener() {
            @Override
            public void onSuccess(List<TAPMessageModel> messages, Boolean hasMoreData) {
                resultMessages.addAll(messages);

                if (hasMoreData) {
                    // Fetch more older messages from API
                    long maxCreated = messages.get(messages.size() - 1).getCreated();
                    getOlderMessagesBeforeTimestamp(roomID, maxCreated, ITEM_LOAD_LIMIT, this);
                } else {
                    if (null != listener) {
                        listener.onSuccess(resultMessages);
                    }
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

    public void getMessagesFromServer(String roomID, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getOldestCreatedTimeFromRoom(roomID, new TAPDatabaseListener<>() {
            @Override
            public void onSelectFinished(Long createdTime) {
                getAllOlderMessagesBeforeTimestamp(createdTime, roomID, listener);
            }
        });
    }

    public void getAllMessagesFromRoom(String roomID, TapCoreGetAllMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        HashMap<String, TAPMessageModel> messageMap = new HashMap<>();
        List<TAPMessageModel> allMessages = new ArrayList<>();
        List<TAPMessageModel> olderMessages = new ArrayList<>();
        List<TAPMessageModel> newerMessages = new ArrayList<>();

        // Get messages from database
        getLocalMessages(roomID, new TapCoreGetMessageListener() {
            @Override
            public void onSuccess(List<TAPMessageModel> messages) {
                new Thread(() -> {
                    if (null != listener) {
                        listener.onGetLocalMessagesCompleted(messages);
                    }
                    allMessages.addAll(messages);
                    for (TAPMessageModel message : messages) {
                        messageMap.put(message.getLocalID(), message);
                    }

                    long lastTimestamp;
                    if (0 < allMessages.size()) {
                        lastTimestamp = allMessages.get(allMessages.size() - 1).getCreated();
                    } else {
                        lastTimestamp = System.currentTimeMillis();
                    }

                    // Fetch older messages from API
                    getAllOlderMessagesBeforeTimestamp(lastTimestamp, roomID, new TapCoreGetMessageListener() {
                        @Override
                        public void onSuccess(List<TAPMessageModel> messages) {
                            new Thread(() -> {
                                List<TAPMessageModel> filteredMessages = new ArrayList<>();
                                for (TAPMessageModel message : messages) {
                                    if (!messageMap.containsKey(message.getLocalID())) {
                                        filteredMessages.add(message);
                                    }
                                    messageMap.put(message.getLocalID(), message);
                                }
                                allMessages.addAll(filteredMessages);
                                olderMessages.addAll(filteredMessages);

                                // Fetch newer messages from API
                                long lastUpdateTimestamp = TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID);
                                long minCreatedTimestamp = 0L;
                                if (allMessages.size() > 0) {
                                    minCreatedTimestamp = allMessages.get(0).getCreated();
                                }
                                getNewerMessagesAfterTimestamp(roomID, minCreatedTimestamp, lastUpdateTimestamp, new TapCoreGetMessageListener() {
                                    @Override
                                    public void onSuccess(List<TAPMessageModel> messages) {
                                        new Thread(() -> {
                                            List<TAPMessageModel> filteredMessages = new ArrayList<>();
                                            for (TAPMessageModel message : messages) {
                                                if (!messageMap.containsKey(message.getLocalID())) {
                                                    filteredMessages.add(message);
                                                }
                                                messageMap.put(message.getLocalID(), message);
                                            }
                                            allMessages.addAll(filteredMessages);
                                            newerMessages.addAll(filteredMessages);

                                            TAPUtils.mergeSort(allMessages, ASCENDING);
                                            if (null != listener) {
                                                new Handler(Looper.getMainLooper()).post(() -> listener.onGetAllMessagesCompleted(allMessages, olderMessages, newerMessages));
                                            }
//                                            getLocalMessages(roomID, new TapCoreGetMessageListener() {
//                                                @Override
//                                                public void onSuccess(List<TAPMessageModel> messages) {
//                                                    if (null != listener) {
//                                                        new Handler(Looper.getMainLooper()).post(() -> listener.onGetAllMessagesCompleted(messages, olderMessages, newerMessages));
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onError(String errorCode, String errorMessage) {
//                                                    TAPUtils.mergeSort(allMessages, ASCENDING);
//                                                    if (null != listener) {
//                                                        new Handler(Looper.getMainLooper()).post(() -> listener.onGetAllMessagesCompleted(allMessages, olderMessages, newerMessages));
//                                                    }
//                                                }
//                                            });
                                        }).start();
                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMessage) {
                                        if (null != listener) {
                                            new Handler(Looper.getMainLooper()).post(() -> listener.onError(errorCode, errorMessage));
                                        }
                                    }
                                });
                            }).start();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            if (null != listener) {
                                listener.onError(errorCode, errorMessage);
                            }
                        }
                    });
                }).start();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                if (null != listener) {
                    listener.onError(errorCode, errorMessage);
                }
            }
        });
    }

    public void getUnreadMessagesFromRoom(String roomID, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(roomID,
            new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onSelectFinished(List<TAPMessageEntity> entities) {
                    ArrayList<TAPMessageModel> unreadMessages = new ArrayList<>();
                    if (0 < entities.size()) {
                        for (TAPMessageEntity entity : entities) {
                            unreadMessages.add(TAPMessageModel.fromMessageEntity(entity));
                        }
                    }
                    listener.onSuccess(unreadMessages);
                }
            }
        );
    }

    public void getMediaMessagesFromRoom(String roomID, long lastTimestamp, int numberOfItems, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getRoomMedias(lastTimestamp, roomID, numberOfItems, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                ArrayList<TAPMessageModel> mediaMessages = new ArrayList<>();
                for (TAPMessageEntity entity : entities) {
                    mediaMessages.add(TAPMessageModel.fromMessageEntity(entity));
                }
                listener.onSuccess(mediaMessages);
            }
        });
    }

    public void searchLocalMessageWithKeyword(String keyword, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).searchAllMessagesFromDatabase(keyword, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                ArrayList<TAPMessageModel> searchResultArray = new ArrayList<>();
                if (entities.size() > 0) {
                    for (TAPMessageEntity entity : entities) {
                        searchResultArray.add(TAPMessageModel.fromMessageEntity(entity));
                    }
                }
                if (null != listener) {
                    listener.onSuccess(searchResultArray);
                }
            }
        });
    }

    public void searchLocalRoomMessageWithKeyword(String keyword, String roomID, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).searchAllRoomMessagesFromDatabase(keyword, roomID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                ArrayList<TAPMessageModel> searchResultArray = new ArrayList<>();
                if (entities.size() > 0) {
                    for (TAPMessageEntity entity : entities) {
                        searchResultArray.add(TAPMessageModel.fromMessageEntity(entity));
                    }
                }
                if (null != listener) {
                    listener.onSuccess(searchResultArray);
                }
            }
        });
    }

    public void getLocalMessageByLocalID(String localID, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getMessageByLocalIDFromDatabase(localID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                ArrayList<TAPMessageModel> messagesArray = new ArrayList<>();
                if (entities.size() > 0) {
                    for (TAPMessageEntity entity : entities) {
                        messagesArray.add(TAPMessageModel.fromMessageEntity(entity));
                    }
                }
                if (null != listener) {
                    listener.onSuccess(messagesArray);
                }
            }
        });
    }

    public void getLocalMessagesByLocalID(List<String> localIDs, TapCoreGetMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getMessageByLocalIDsFromDatabase(localIDs, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                ArrayList<TAPMessageModel> messagesArray = new ArrayList<>();
                if (entities.size() > 0) {
                    for (TAPMessageEntity entity : entities) {
                        messagesArray.add(TAPMessageModel.fromMessageEntity(entity));
                    }
                }
                if (null != listener) {
                    listener.onSuccess(messagesArray);
                }
            }
        });
    }

    public boolean isUploadMessageFileToExternalServerEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isUploadMessageFileToExternalServerEnabled;
    }

    public void setUploadMessageFileToExternalServerEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        this.isUploadMessageFileToExternalServerEnabled = isEnabled;
    }

    public void onFileUploadProgress(TAPMessageModel tapMessageModel, int percentCompleted, long bytesUploaded) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).addUploadProgressMap(tapMessageModel.getLocalID(), percentCompleted, bytesUploaded);
        Intent intent = new Intent(UploadProgressLoading);
        intent.putExtra(UploadLocalID, tapMessageModel.getLocalID());
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        TapSendMessageInterface listener = TAPFileUploadManager.getInstance(instanceKey).getSendMessageListeners().get(tapMessageModel.getLocalID());
        if (null != listener) {
            listener.onProgress(tapMessageModel, percentCompleted, bytesUploaded);
        }
    }

    public void onFileUploadCompleted(TAPMessageModel tapMessageModel, String fileUrl) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).onFileUploadFromExternalServerFinished(tapMessageModel, fileUrl);
    }

    void triggerRequestMessageFileUpload(TAPMessageModel messageModel, Uri fileUri) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        for (TapCoreMessageListener listener : getCoreMessageListeners()) {
            if (null != listener) {
                listener.onRequestMessageFileUpload(messageModel, fileUri);
            }
        }
    }

    public void getStarredMessages(String roomId, int pageNumber, int numberOfItems, TapCoreGetOlderMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getStarredMessages(roomId, pageNumber, numberOfItems, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPGetMessageListByRoomResponse response) {
                super.onSuccess(response);
                if (listener != null) {
                    List<TAPMessageModel> starredMessageList = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            starredMessageList.add(message);

                        } catch (Exception e) {
                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                        }
                    }
                    listener.onSuccess(starredMessageList, response.getHasMore());
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

    public void starMessage(String roomID, String messageID) {
        starMessage(roomID, messageID, null);
    }

    public void starMessage(String roomID, String messageID, TapCoreGetStringArrayListener listener) {
        List<String> idList = new ArrayList<>();
        idList.add(messageID);
        starMessages(roomID, idList, listener);
    }

    public void starMessages(String roomID, List<String> messageIDs) {
        starMessages(roomID, messageIDs, null);
    }

    public void starMessages(String roomID, List<String> messageIDs, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).starMessage(roomID, messageIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapStarMessageResponse response) {
                if (null != listener) {
                    listener.onSuccess(new ArrayList<>(response.getStarredMessageIDs()));
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

    public void unstarMessage(String roomID, String messageID) {
        unstarMessage(roomID, messageID, null);
    }
    public void unstarMessage(String roomID, String messageID, TapCoreGetStringArrayListener listener) {
        List<String> idList = new ArrayList<>();
        idList.add(messageID);
        unstarMessages(roomID, idList, listener);
    }

    public void unstarMessages(String roomID, List<String> messageIDs) {
        unstarMessages(roomID, messageIDs, null);
    }

    public void unstarMessages(String roomID, List<String> messageIDs, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).unStarMessage(roomID, messageIDs, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapUnstarMessageResponse response) {
                if (null != listener) {
                    listener.onSuccess(new ArrayList<>(response.getUnstarredMessageIDs()));
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

    public void getStarredMessageIds(String roomId, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getStarredMessageIds(roomId, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapStarMessageResponse response) {
                super.onSuccess(response);
                listener.onSuccess(new ArrayList<>(response.getStarredMessageIDs()));
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

    /**
     * =============================================================================================
     * TEMP
     * =============================================================================================
     */

    private void getUserFromRecipientUserAndSendProductRequestMessage(String message, @NonNull TAPUserModel recipientUser, TAPSendMessageWithIDListener listener) {
        new Thread(() -> {
            try {
                final TAPUserModel myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
                createAndSendProductRequestMessage(message, myUserModel, recipientUser, listener);
            } catch (Exception e) {
                e.printStackTrace();
                listener.sendFailed(new TAPErrorModel("", e.getMessage(), ""));
            }
        }).start();
    }

    private void createAndSendProductRequestMessage(String message, TAPUserModel myUserModel, TAPUserModel otherUserModel, TAPSendMessageWithIDListener listener) {
        TAPRoomModel roomModel = TAPRoomModel.Builder(TAPChatManager.getInstance(instanceKey).arrangeRoomId(myUserModel.getUserID(), otherUserModel.getUserID()),
                otherUserModel.getFullname(), 1, otherUserModel.getImageURL(), "#FFFFFF");
        TAPChatManager.getInstance(instanceKey).sendTextMessageWithRoomModel(message, roomModel);
        listener.sendSuccess();
    }
}
