package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_CANCELLED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_PRODUCT_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_URI_NOT_FOUND;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DESCRIPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ITEMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SITE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TITLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URLS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.THUMB_MAX_DIMENSION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

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
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreCreateScheduledMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreFileDownloadListener;
import io.taptalk.TapTalk.Listener.TapCoreFileUploadListener;
import io.taptalk.TapTalk.Listener.TapCoreGetAllMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetIntegerArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreGetIntegerListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageDetailsListener;
import io.taptalk.TapTalk.Listener.TapCoreGetMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetScheduledMessagesListener;
import io.taptalk.TapTalk.Listener.TapCoreGetSharedContentMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreUpdateMessageStatusListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateMessageStatusResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapCreateScheduledMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMessageDetailResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetMessageTotalReadResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetScheduledMessageItem;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetScheduledMessageListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetSharedContentResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapPinMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapScheduledMessageModel;
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel;
import io.taptalk.TapTalk.Model.ResponseModel.TapStarMessageResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapUnstarMessageResponse;
import io.taptalk.TapTalk.Model.TAPDataImageModel;
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

    public synchronized void addMessageListener(TapCoreMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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

    public synchronized void removeMessageListener(TapCoreMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendTextMessageWithRoomModel(messageBody, room, listener);
    }

    public void sendTextMessage(String messageBody, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendTextMessage(messageBody, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendLocationMessage(address, latitude, longitude, room, listener);
    }

    public void sendLocationMessage(Double latitude, Double longitude, String address, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendLocationMessage(latitude, longitude, address, room, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, imageUri, caption, listener);
    }

    public void sendImageMessage(Uri imageUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendImageMessage(imageUri, caption, room, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendImageMessage(TapTalk.appContext, room, image, caption, listener);
    }

    public void sendImageMessage(Bitmap image, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendImageMessage(image, caption, room, listener);
    }

    public void sendImageMessage(String imageUrl, String caption, TAPRoomModel room, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPMessageModel temporaryMessage = TAPChatManager.getInstance(instanceKey).createTemporaryMediaMessageWithUrl(
            TYPE_IMAGE,
            imageUrl,
            caption,
            room,
            TAPChatManager.getInstance(instanceKey).getQuotedMessage(room.getRoomID())
        );
        if (!fetchMetadata) {
            // Send message without metadata
            TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
            sendCustomMessage(temporaryMessage, listener);
            return;
        }
        if (listener != null) {
            listener.onTemporaryMessageCreated(temporaryMessage);
        }
        Glide.with(TapTalk.appContext).asBitmap().load(imageUrl).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                // Send message without metadata
                TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
                sendCustomMessage(temporaryMessage, listener);
//                if (listener != null) {
//                    listener.onError(null, ERROR_CODE_OTHERS, e != null ? e.getLocalizedMessage() : "Unable to retrieve image data.");
//                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                TAPMessageModel message = TAPChatManager.getInstance(instanceKey).createImageMessageModel(resource, caption, room);
                // Set local ID, quote, reply
                message.setLocalID(temporaryMessage.getLocalID());
                if (temporaryMessage.getQuote() != null && message.getQuote() == null) {
                    message.setQuote(temporaryMessage.getQuote());
                }
                if (temporaryMessage.getReplyTo() != null && message.getReplyTo() == null) {
                    message.setReplyTo(temporaryMessage.getReplyTo());
                }
                try {
                    HashMap<String, Object> messageData = message.getData();
                    if (messageData == null) {
                        messageData = new HashMap<>();
                    }
                    messageData.put(FILE_URL, imageUrl);
                    String mediaType = TAPUtils.getMimeTypeFromUrl(imageUrl);
                    if (mediaType == null || mediaType.isEmpty()) {
                        mediaType = IMAGE_JPEG;
                    }
                    messageData.put(MEDIA_TYPE, mediaType);
                    messageData.remove(FILE_URI);

                    // Create thumbnail
                    HashMap<String, Object> finalMessageData = messageData;
                    TAPFileUploadManager.getInstance(instanceKey).createAndResizeImageFile(resource, THUMB_MAX_DIMENSION, new TAPFileUploadManager.BitmapInterface() {
                        @Override
                        public void onBitmapReady(Bitmap thumbBitmap) {
                            String thumbBase64 = TAPFileUtils.encodeToBase64(thumbBitmap);
                            finalMessageData.put(THUMBNAIL, thumbBase64);
                            message.setData(finalMessageData);
                            sendCustomMessage(message, listener);
                        }

                        @Override
                        public void onBitmapError() {
                            message.setData(finalMessageData);
                            sendCustomMessage(message, listener);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(message, ERROR_CODE_OTHERS, e.getLocalizedMessage());
                    }
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public void sendImageMessage(String imageUrl, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendImageMessage(imageUrl, caption, room, fetchMetadata, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendVideoMessage(TapTalk.appContext, room, videoUri, caption, listener);
    }

    public void sendVideoMessage(Uri videoUri, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendVideoMessage(videoUri, caption, room, listener);
    }

    public void sendVideoMessage(Context context, String videoUrl, String caption, TAPRoomModel room, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPMessageModel temporaryMessage = TAPChatManager.getInstance(instanceKey).createTemporaryMediaMessageWithUrl(
            TYPE_VIDEO,
            videoUrl,
            caption,
            room,
            TAPChatManager.getInstance(instanceKey).getQuotedMessage(room.getRoomID())
        );
        if (!fetchMetadata) {
            // Send message without metadata
            TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
            sendCustomMessage(temporaryMessage, listener);
            return;
        }
        if (listener != null) {
            listener.onTemporaryMessageCreated(temporaryMessage);
        }
        TAPChatManager.getInstance(instanceKey).createVideoMessageModel(context, videoUrl, caption, room, new TapCoreSendMessageListener() {
            @Override
            public void onStart(TAPMessageModel message) {
                if (message != null) {
                    // Set local ID, quote, reply, and send message
                    message.setLocalID(temporaryMessage.getLocalID());
                    if (temporaryMessage.getQuote() != null && message.getQuote() == null) {
                        message.setQuote(temporaryMessage.getQuote());
                    }
                    if (temporaryMessage.getReplyTo() != null && message.getReplyTo() == null) {
                        message.setReplyTo(temporaryMessage.getReplyTo());
                    }
                    sendCustomMessage(message, listener);
                }
            }

            @Override
            public void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage) {
                // Send message without metadata
                TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
                sendCustomMessage(temporaryMessage, listener);
//                if (listener != null) {
//                    listener.onError(message, errorCode, errorMessage);
//                }
            }
        });
    }

    public void sendVideoMessage(Context context, String videoUrl, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendVideoMessage(context, videoUrl, caption, room, fetchMetadata, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendFileMessage(file, room, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendFileMessage(uri, room, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, String caption, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, file, caption, listener);
    }

    public void sendFileMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, String caption, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendFileMessage(file, room, caption, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, String caption, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendFileMessage(TapTalk.appContext, room, uri, caption, listener);
    }

    public void sendFileMessage(Uri uri, TAPRoomModel room, TAPMessageModel quotedMessage, String caption, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendFileMessage(uri, room, caption, listener);
    }

    public void sendFileMessage(String fileUrl, String caption, TAPRoomModel room, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        sendFileMessage(fileUrl, caption, room, "", "", fetchMetadata, listener);
    }

    public void sendFileMessage(String fileUrl, String caption, TAPRoomModel room, String fileName, String mimeType, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPMessageModel temporaryMessage = TAPChatManager.getInstance(instanceKey).createTemporaryMediaMessageWithUrl(
            TYPE_FILE,
            fileUrl,
            caption,
            fileName,
            mimeType,
            room,
            TAPChatManager.getInstance(instanceKey).getQuotedMessage(room.getRoomID())
        );
        if (!fetchMetadata) {
            // Send message without metadata
            TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
            sendCustomMessage(temporaryMessage, listener);
            return;
        }
        if (listener != null) {
            listener.onTemporaryMessageCreated(temporaryMessage);
        }
        TAPChatManager.getInstance(instanceKey).createFileMessageModel(
            fileUrl,
            room,
            caption,
            fileName,
            mimeType,
            new TapCoreSendMessageListener() {
                @Override
                public void onStart(TAPMessageModel message) {
                    if (message != null) {
                        // Set local ID, quote, reply, and send message
                        message.setLocalID(temporaryMessage.getLocalID());
                        if (temporaryMessage.getQuote() != null && message.getQuote() == null) {
                            message.setQuote(temporaryMessage.getQuote());
                        }
                        if (temporaryMessage.getReplyTo() != null && message.getReplyTo() == null) {
                            message.setReplyTo(temporaryMessage.getReplyTo());
                        }
                        sendCustomMessage(message, listener);
                    }
                }

                @Override
                public void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage) {
                    // Send message without metadata
                    TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), null, 0);
                    sendCustomMessage(temporaryMessage, listener);
//                    if (listener != null) {
//                        listener.onError(message, errorCode, errorMessage);
//                    }
                }
            }
        );
    }

    public void sendFileMessage(String fileUrl, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        sendFileMessage(fileUrl, caption, room, quotedMessage, "", "", fetchMetadata, listener);
    }

    public void sendFileMessage(String fileUrl, String caption, TAPRoomModel room, TAPMessageModel quotedMessage, String fileName, String mimeType, boolean fetchMetadata, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendFileMessage(fileUrl, caption, room, fileName, mimeType, fetchMetadata, listener);
    }

    public void sendVoiceMessage(File file, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, file, listener);
    }

    public void sendVoiceMessage(File file, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendVoiceMessage(file, room, listener);
    }

    public void sendVoiceMessage(Uri uri, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(TapTalk.appContext, room, uri, listener);
    }

    public void sendVoiceMessage(Uri uri, TAPRoomModel room, TAPMessageModel quotedMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendVoiceMessage(uri, room, listener);
    }

    public void sendLinkMessage(String messageBody, TAPRoomModel room, List<String> urls, @Nullable String title, @Nullable String description, @Nullable String image, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put(URLS, urls);
        data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
        if (title != null) {
            data.put(TITLE, title);
        }
        if (description != null) {
            data.put(DESCRIPTION, description);
        }
        if (image != null) {
            data.put(IMAGE, image);
        }
        TAPChatManager.getInstance(instanceKey).sendLinkMessageWithRoomModel(messageBody, room, data, listener);
    }

    public void sendLinkMessage(String messageBody, TAPRoomModel room, List<String> urls, @Nullable String title, @Nullable String description, @Nullable String image, @Nullable String siteName, @Nullable String type,  TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put(URLS, urls);
        data.put(URL, TAPUtils.setUrlWithProtocol(urls.get(0)));
        if (title != null) {
            data.put(TITLE, title);
        }
        if (description != null) {
            data.put(DESCRIPTION, description);
        }
        if (image != null) {
            data.put(IMAGE, image);
        }
        if (siteName != null) {
            data.put(SITE_NAME, siteName);
        }
        if (type != null) {
            data.put(TYPE, type);
        }
        TAPChatManager.getInstance(instanceKey).sendLinkMessageWithRoomModel(messageBody, room, data, listener);
    }

    public void sendLinkMessage(String messageBody, TAPRoomModel room, TAPMessageModel quotedMessage, List<String> urls, @Nullable String title, @Nullable String description, @Nullable String image, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendLinkMessage(messageBody, room, urls, title, description, image, listener);
    }

    public void sendLinkMessage(String messageBody, TAPRoomModel room, TAPMessageModel quotedMessage, List<String> urls, @Nullable String title, @Nullable String description, @Nullable String image, @Nullable String siteName, @Nullable String type, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), quotedMessage, REPLY);
        sendLinkMessage(messageBody, room, urls, title, description, image, siteName, type, listener);
    }

    public void sendForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(messageToForward, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        ArrayList<TAPMessageModel> messagesToForward = new ArrayList<>();
        messagesToForward.add(messageToForward);
        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messagesToForward, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
    }

    public void sendForwardedMessages(List<TAPMessageModel> messagesToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        ArrayList<TAPMessageModel> messages = new ArrayList<>(messagesToForward);
        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messages, TAPDefaultConstant.QuoteAction.FORWARD);
        TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
    }

    public void sendForwardedMessageToMultipleRooms(TAPMessageModel messageToForward, List<TAPRoomModel> rooms, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(messageToForward, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        ArrayList<TAPMessageModel> messagesToForward = new ArrayList<>();
        messagesToForward.add(messageToForward);
        for (TAPRoomModel room : rooms) {
            TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messagesToForward, TAPDefaultConstant.QuoteAction.FORWARD);
            TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
        }
    }

    public void sendForwardedMessagesToMultipleRooms(List<TAPMessageModel> messagesToForward, List<TAPRoomModel> rooms, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(null, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        ArrayList<TAPMessageModel> messages = new ArrayList<>(messagesToForward);
        for (TAPRoomModel room : rooms) {
            TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), messages, TAPDefaultConstant.QuoteAction.FORWARD);
            TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(room, listener);
        }
    }

    public TAPMessageModel constructTapTalkMessageModel(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return TAPMessageModel.Builder(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData);
    }

    public TAPMessageModel constructTapTalkMessageModelWithQuote(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData, TAPMessageModel quotedMessage) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return TAPMessageModel.BuilderWithQuotedMessage(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData, quotedMessage, instanceKey);
    }

    public TAPMessageModel constructTapTalkMessageModelWithQuote(String messageBody, TAPRoomModel room, Integer type, @Nullable HashMap<String, Object> messageData, String quoteTitle, String quoteContent, String quoteImageUrl) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return TAPMessageModel.BuilderWithQuote(messageBody, room, type, System.currentTimeMillis(), TAPChatManager.getInstance(instanceKey).getActiveUser(), TYPE_PERSONAL == room.getType() ? TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()) : "0", messageData, quoteTitle, quoteContent, quoteImageUrl);
    }

    public void sendCustomMessage(TAPMessageModel customMessage, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).sendMessage(customMessage, listener);
    }

    public void deleteMessage(TAPMessageModel message) {
        TAPDataManager.getInstance(instanceKey).deleteMessagesAPI(message.getRoom().getRoomID(), message.getMessageID(), true);
    }

    public void deleteLocalMessage(String localID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(localID);
    }

    public void editMessage(TAPMessageModel message, String updatedText, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).editMessage(message, updatedText, listener, false);
    }

    public void editMessage(TAPMessageModel newMessage, TapCoreSendMessageListener listener) {
        TAPChatManager.getInstance(instanceKey).editMessage(newMessage, listener);
    }

    // For uploadImage, uploadVideo, uploadFile
    private ProgressRequestBody.UploadCallbacks uploadCallBack(TapCoreFileUploadListener listener) {
        return  new ProgressRequestBody.UploadCallbacks() {
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
        };
    }

    // For uploadImage, uploadVideo, uploadFile
//    private TAPDefaultDataView<TAPUploadFileResponse> uploadDataView(TapCoreFileUploadListener listener) {
//        return new TAPDefaultDataView<>() {
//            @Override
//            public void onSuccess(TAPUploadFileResponse response) {
//                if (null != listener && null != response) {
//                    listener.onSuccess(response.getFileID(), response.getFileURL());
//                }
//            }
//
//            @Override
//            public void onError(TAPErrorModel error) {
//                if (null != listener) {
//                    listener.onError(error.getCode(), error.getMessage());
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                if (null != listener) {
//                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
//                }
//            }
//        };
//    }

    public void uploadImage(Context context, Uri uri, TapCoreFileUploadListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        try {
            TAPFileUploadManager.getInstance(instanceKey).uploadImage(context, uri, uploadCallBack(listener), listener);
        } catch (Exception e) {
            if (null != listener) {
                listener.onError(ERROR_CODE_OTHERS, e.getLocalizedMessage());
            }
        }
    }

    public void uploadVideo(Context context, Uri uri, TapCoreFileUploadListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        try {
            TAPFileUploadManager.getInstance(instanceKey).uploadVideo(context, uri, uploadCallBack(listener), listener);
        } catch (Exception e) {
            if (null != listener) {
                listener.onError(ERROR_CODE_OTHERS, e.getLocalizedMessage());
            }
        }
    }

    public void uploadFile(Context context, Uri uri, TapCoreFileUploadListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        try {
            TAPFileUploadManager.getInstance(instanceKey).uploadFile(context, uri, uploadCallBack(listener), listener);
        } catch (Exception e) {
            if (null != listener) {
                listener.onError(ERROR_CODE_OTHERS, e.getLocalizedMessage());
            }
        }
    }

    public void cancelMessageFileUpload(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(TapTalk.appContext, message, message.getRoom().getRoomID());
        TAPDataManager.getInstance(instanceKey).deleteFromDatabase(message.getLocalID());
    }

    public void downloadMessageFile(TAPMessageModel message, TapCoreFileDownloadListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            if (null != listener) {
                listener.onError(message, ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        if (!TAPUtils.hasPermissions(TapTalk.appContext, TAPUtils.getStoragePermissions(true))) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        List<String> messageIDs = new ArrayList<>();
        messageIDs.add(messageID);
        markMessagesAsDelivered(messageIDs);
    }

    public void markMessageAsDelivered(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDeliveredFromNotification(message);
    }

    public void markMessagesAsDelivered(List<String> messageIDs) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPDataManager.getInstance(instanceKey).updateMessageStatusAsDelivered(
                messageIDs,
                new TAPDefaultDataView<TAPUpdateMessageStatusResponse>() {
        });
    }

    public void markMessageAsRead(String messageID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(messageID);
    }

    public void markMessageAsRead(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        markMessageAsRead(message.getMessageID());
    }

    public void markMessagesAsRead(List<String> messageIDs) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(messageIDs);
    }

    public void markMessagesAsRead(List<String> messageIDs, TapCoreUpdateMessageStatusListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
                                if (message != null) {
                                    messageBeforeModels.add(message);
                                    entities.add(TAPMessageEntity.fromMessageModel(message));
                                }
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
                                if (message != null) {
                                    messageAfterModels.add(message);
                                    entities.add(TAPMessageEntity.fromMessageModel(message));

                                    if (null != message.getUpdated() &&
                                            TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID) < message.getUpdated()
                                    ) {
                                        TAPDataManager.getInstance(instanceKey).saveLastUpdatedMessageTimestamp(roomID, message.getUpdated());
                                    }
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isUploadMessageFileToExternalServerEnabled;
    }

    public void setUploadMessageFileToExternalServerEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        this.isUploadMessageFileToExternalServerEnabled = isEnabled;
    }

    public void onFileUploadProgress(TAPMessageModel tapMessageModel, int percentCompleted, long bytesUploaded) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPFileUploadManager.getInstance(instanceKey).onFileUploadFromExternalServerFinished(tapMessageModel, fileUrl);
    }

    void triggerRequestMessageFileUpload(TAPMessageModel messageModel, Uri fileUri) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        for (TapCoreMessageListener listener : getCoreMessageListeners()) {
            if (null != listener) {
                listener.onRequestMessageFileUpload(messageModel, fileUri);
            }
        }
    }

    public void getStarredMessageIds(String roomId, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getStarredMessageIds(roomId, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapStarMessageResponse response) {
                ArrayList<String> starredMessageIDs = new ArrayList<>(response.getStarredMessageIDs());
                TAPDataManager.getInstance(instanceKey).saveStarredMessageIds(roomId, starredMessageIDs);
                listener.onSuccess(starredMessageIDs);
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

    public void getStarredMessages(String roomId, int pageNumber, int numberOfItems, TapCoreGetOlderMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getStarredMessages(roomId, pageNumber, numberOfItems, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPGetMessageListByRoomResponse response) {
                ArrayList<String> starredMessageIDs = TAPDataManager.getInstance(instanceKey).getStarredMessageIds(roomId);
                if (listener != null) {
                    List<TAPMessageModel> starredMessageList = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            if (message != null) {
                                starredMessageList.add(message);
                                if (!starredMessageIDs.contains(message.getMessageID())) {
                                    starredMessageIDs.add(message.getMessageID());
                                }
                            }
                        } catch (Exception e) {
                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                        }
                    }
                    listener.onSuccess(starredMessageList, response.getHasMore());
                }
                TAPDataManager.getInstance(instanceKey).saveStarredMessageIds(roomId, starredMessageIDs);
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

    public void getPinnedMessageIDs(String roomId, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).getPinnedMessageIds(roomId, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapPinMessageResponse response) {
                ArrayList<String> pinnedMessageIDs = new ArrayList<>(response.getMessageIDs());
                TAPDataManager.getInstance(instanceKey).savePinnedMessageIds(roomId, pinnedMessageIDs);
                listener.onSuccess(new ArrayList<>(pinnedMessageIDs));
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

    public void getPinnedMessages(String roomId, int pageNumber, int numberOfItems, TapCoreGetOlderMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getPinnedMessages(roomId, pageNumber, numberOfItems, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPGetMessageListByRoomResponse response) {
                ArrayList<String> pinnedMessageIDs = TAPDataManager.getInstance(instanceKey).getPinnedMessageIds(roomId);
                if (listener != null) {
                    List<TAPMessageModel> pinnedMessageList = new ArrayList<>();
                    for (HashMap<String, Object> messageMap : response.getMessages()) {
                        try {
                            TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                            if (message != null) {
                                pinnedMessageList.add(message);
                                if (!pinnedMessageIDs.contains(message.getMessageID())) {
                                    pinnedMessageIDs.add(message.getMessageID());
                                }
                            }
                        } catch (Exception e) {
                            listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                        }
                    }
                    listener.onSuccess(pinnedMessageList, response.getHasMore());
                }
                TAPDataManager.getInstance(instanceKey).savePinnedMessageIds(roomId, pinnedMessageIDs);
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

    public void pinMessage(String roomID, String messageID) {
        pinMessage(roomID, messageID, null);
    }

    public void pinMessage(String roomID, String messageID, TapCoreGetStringArrayListener listener) {
        List<String> idList = new ArrayList<>();
        idList.add(messageID);
        pinMessage(roomID, idList, listener);
    }

    public void pinMessage(String roomID, List<String> messageIDs) {
        pinMessage(roomID, messageIDs, null);
    }

    public void pinMessage(String roomID, List<String> messageIDs, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).pinMessages(roomID, messageIDs, new TAPDefaultDataView<>() {

            @Override
            public void onSuccess(TapPinMessageResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    listener.onSuccess(new ArrayList<>(response.getMessageIDs()));
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

    public void unpinMessage(String roomID, String messageID) {
        unpinMessage(roomID, messageID, null);
    }
    public void unpinMessage(String roomID, String messageID, TapCoreGetStringArrayListener listener) {
        List<String> idList = new ArrayList<>();
        idList.add(messageID);
        unpinMessage(roomID, idList, listener);
    }

    public void unpinMessage(String roomID, List<String> messageIDs) {
        unpinMessage(roomID, messageIDs, null);
    }

    public void unpinMessage(String roomID, List<String> messageIDs, TapCoreGetStringArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).unPinMessages(roomID, messageIDs, new TAPDefaultDataView<>() {

            @Override
            public void onSuccess(TapPinMessageResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    listener.onSuccess(new ArrayList<>(response.getMessageIDs()));
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

    public void getSharedContentMessages(TAPRoomModel room,  long minCreatedTimestamp, long maxCreatedTimestamp, TapCoreGetSharedContentMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).getSharedMedia(room.getRoomID(), minCreatedTimestamp, maxCreatedTimestamp, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetSharedContentResponse response) {
                super.onSuccess(response);
                List<TAPMessageModel> mediaList = new ArrayList<>();
                List<TAPMessageModel> fileList = new ArrayList<>();
                List<TAPMessageModel> linkList = new ArrayList<>();
                if (response.getMedia() != null) {
                    for (TapSharedMediaItemModel item : response.getMedia()) {
                        String dataString = "";
                        if (item.getData() != null) {
                            dataString = item.getData();
                        }
                        HashMap<String, Object> data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(dataString, item.getLocalID()));
                        TAPMessageModel message = TAPMessageModel.Builder(
                                "",
                                room,
                                item.getMessageType(),
                                item.getCreated(),
                                new TAPUserModel(item.getUserID(), item.getUserFullname()),
                                "",
                                data
                        );
                        mediaList.add(message);
                    }
                }
                if (response.getFiles() != null) {
                    for (TapSharedMediaItemModel item : response.getFiles()) {
                        String dataString = "";
                        if (item.getData() != null) {
                            dataString = item.getData();
                        }
                        HashMap<String, Object> data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(dataString, item.getLocalID()));
                        TAPMessageModel message = TAPMessageModel.Builder(
                                "",
                                room,
                                item.getMessageType(),
                                item.getCreated(),
                                new TAPUserModel(item.getUserID(), item.getUserFullname()),
                                "",
                                data
                        );
                        fileList.add(message);
                    }
                }
                if (response.getLinks() != null) {
                    for (TapSharedMediaItemModel item : response.getLinks()) {
                        String dataString = "";
                        if (item.getData() != null) {
                            dataString = item.getData();
                        }
                        HashMap<String, Object> data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(dataString, item.getLocalID()));
                        TAPMessageModel message = TAPMessageModel.Builder(
                                "",
                                room,
                                item.getMessageType(),
                                item.getCreated(),
                                new TAPUserModel(item.getUserID(), item.getUserFullname()),
                                "",
                                data
                        );
                        linkList.add(message);
                    }
                }
                listener.onSuccess(mediaList, fileList, linkList);
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

    public void createScheduledMessage(TAPMessageModel message, long scheduledTime, TapCoreCreateScheduledMessageListener listener) {
        TAPDataManager.getInstance(instanceKey).createScheduledMessage(message, scheduledTime, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapCreateScheduledMessageResponse response) {
                super.onSuccess(response);
                if (null != listener && null != response.getCreatedItem() && null != response.getCreatedItem().getMessage()) {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(response.getCreatedItem().getMessage());
                    if (null != message) {
                        TapScheduledMessageModel resultModel = new TapScheduledMessageModel(
                            response.getCreatedItem().getUpdatedTime(),
                            response.getCreatedItem().getScheduledTime(),
                            response.getCreatedItem().getCreatedTime(),
                            response.getCreatedItem().getId(),
                            message
                        );
                        listener.onSuccess(resultModel);
                    }
                    else {
                        listener.onError(ERROR_CODE_OTHERS, "Unable to decrypt message");
                    }
                }
                else if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, "Unable to decrypt message");
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

    public void getScheduledMessages(String roomID, TapCoreGetScheduledMessagesListener listener) {
        TAPDataManager.getInstance(instanceKey).getScheduledMessages(roomID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetScheduledMessageListResponse response) {
                if (response.getItems() != null) {
                    List<TapScheduledMessageModel> scheduledMessages = new ArrayList<>();
                    for (TapGetScheduledMessageItem item : response.getItems()) {
                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(item.getMessage());
                        if (message != null) {
                            scheduledMessages.add(new TapScheduledMessageModel(
                                item.getUpdatedTime(),
                                item.getScheduledTime(),
                                item.getCreatedTime(),
                                item.getId(),
                                message
                            ));
                        }
                    }
                    if (null != listener) {
                        listener.onSuccess(scheduledMessages);
                    }
                } else if (null != listener) {
                    listener.onSuccess(new ArrayList<>());
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

    public void sendScheduledMessageNow(Integer scheduledMessageID, String roomID, TapCoreGetIntegerArrayListener listener) {
        List<Integer> scheduledMessageIDs = new ArrayList<>();
        scheduledMessageIDs.add(scheduledMessageID);
        sendScheduledMessagesNow(scheduledMessageIDs, roomID, listener);
    }

    public void sendScheduledMessagesNow(List<Integer> scheduledMessageIDs, String roomID, TapCoreGetIntegerArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).sendScheduledMessageNow(scheduledMessageIDs, roomID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapIdsResponse response) {
                if (null != listener) {
                    if (response.getIds() != null) {
                        listener.onSuccess(new ArrayList<>(response.getIds()));
                    } else {
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

    public void editScheduledMessageContent(int scheduledMessageID, TAPMessageModel message, String updatedText, TapCommonListener listener) {
        TAPChatManager.getInstance(instanceKey).editScheduledMessage(scheduledMessageID, message, updatedText, listener, false);
    }

    public void editScheduledMessageContent(int scheduledMessageID, TAPMessageModel updatedMessage, TapCommonListener listener) {
        TAPChatManager.getInstance(instanceKey).editScheduledMessage(scheduledMessageID, updatedMessage, listener);
    }

    public void editScheduledMessageTime(int scheduledMessageID, long scheduledTime, TapCommonListener listener) {
        TAPDataManager.getInstance(instanceKey).editScheduledMessageTime(scheduledMessageID, scheduledTime, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TAPCommonResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
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

    public void deleteScheduledMessage(Integer scheduledMessageID, String roomID, TapCoreGetIntegerArrayListener listener) {
        List<Integer> scheduledMessageIDs = new ArrayList<>();
        scheduledMessageIDs.add(scheduledMessageID);
        deleteScheduledMessages(scheduledMessageIDs, roomID, listener);
    }

    public void deleteScheduledMessages(List<Integer> scheduledMessageIDs, String roomID, TapCoreGetIntegerArrayListener listener) {
        TAPDataManager.getInstance(instanceKey).deleteScheduledMessages(scheduledMessageIDs, roomID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapIdsResponse response) {
                if (null != listener) {
                    if (response.getIds() != null) {
                        listener.onSuccess(new ArrayList<>(response.getIds()));
                    } else {
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

    public void getMessageDetails(String messageID, TapCoreGetMessageDetailsListener listener) {
        TAPDataManager.getInstance(instanceKey).getMessageDetails(messageID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetMessageDetailResponse response) {
                super.onSuccess(response);
                if (null != listener) {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(response.getMessage());
                    if (null != message) {
                        listener.onSuccess(message, response.getDeliveredTo(), response.getReadBy());
                    }
                    else {
                        listener.onError(ERROR_CODE_OTHERS, "Unable to decrypt message");
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

    public void getMessageTotalRead(String messageID, TapCoreGetIntegerListener listener) {
        TAPDataManager.getInstance(instanceKey).getMessageTotalRead(messageID, new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetMessageTotalReadResponse response) {
                if (null != listener) {
                    listener.onSuccess(response.getCount());
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
