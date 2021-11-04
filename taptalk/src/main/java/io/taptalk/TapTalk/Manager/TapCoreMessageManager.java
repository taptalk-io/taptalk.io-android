package io.taptalk.TapTalk.Manager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
import io.taptalk.TapTalk.Listener.TapCoreMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUploadFileResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_PRODUCT_SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ITEMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

@Keep
public class TapCoreMessageManager {

    private static HashMap<String, TapCoreMessageManager> instances;

    private List<TapCoreMessageListener> coreMessageListeners;
    private TAPChatListener chatListener;

    private String instanceKey = "";
    private boolean isUploadMessageFileToExternalServerEnabled;

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

    public void addMessageListener(TapCoreMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        if (getCoreMessageListeners().isEmpty()) {
            if (null == chatListener) {
                chatListener = new TAPChatListener() {
                    @Override
                    public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                        for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                            if (null != listener) {
                                listener.onReceiveNewMessage(message);
                            }
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
                        for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                            if (null != listener) {
                                listener.onReceiveUpdatedMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                        onUpdateMessageInActiveRoom(message);
                    }

                    @Override
                    public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
                        for (TapCoreMessageListener listener : getCoreMessageListeners()) {
                            if (null != listener) {
                                listener.onMessageDeleted(message);
                            }
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

    public void sendForwardedMessage(TAPMessageModel messageToForward, TAPRoomModel room, TapCoreSendMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), messageToForward, TAPDefaultConstant.QuoteAction.FORWARD);
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
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        if (null == products || products.size() == 0) {
            if (null != listener) {
                listener.onError(ERROR_CODE_PRODUCT_EMPTY, ERROR_MESSAGE_PRODUCT_EMPTY);
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

    public void markMessageAsDelivered(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDeliveredFromNotification(message);
    }

    public void markMessageAsRead(TAPMessageModel message) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(message.getMessageID());
    }

    public void getLocalMessages(String roomID, TapCoreGetMessageListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            }
            return;
        }
        List<TAPMessageModel> messageList = new ArrayList<>();
        TAPDataManager.getInstance(instanceKey).getAllMessagesInRoomFromDatabase(roomID, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                List<TAPMessageModel> models = new ArrayList<>();
                for (TAPMessageEntity entity : entities) {
                    TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                    models.add(model);
                }
                messageList.addAll(models);

                if (null != listener) {
                    listener.onSuccess(messageList);
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
                        });

                        if (null != listener) {
                            listener.onSuccess(messageBeforeModels, response.getHasMore());
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
                long lastUpdateTimestamp = TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID);
                long minCreatedTimestamp = 0L;
                if (entities.size() > 0) {
                    minCreatedTimestamp = entities.get(0).getCreated();
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
                        });

                        if (null != listener) {
                            listener.onSuccess(messageAfterModels);
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
                getOlderMessagesBeforeTimestamp(roomID, lastTimestamp, MAX_ITEMS_PER_PAGE, new TapCoreGetOlderMessageListener() {
                    @Override
                    public void onSuccess(List<TAPMessageModel> messages, Boolean hasMoreData) {
                        List<TAPMessageModel> filteredMessages = new ArrayList<>();
                        for (TAPMessageModel message : messages) {
                            if (!messageMap.containsKey(message.getLocalID())) {
                                filteredMessages.add(message);
                            }
                            messageMap.put(message.getLocalID(), message);
                        }
                        allMessages.addAll(filteredMessages);
                        olderMessages.addAll(filteredMessages);

                        if (hasMoreData) {
                            // Fetch more older messages from API
                            long maxCreated = messages.get(messages.size() - 1).getCreated();
                            getOlderMessagesBeforeTimestamp(roomID, maxCreated, MAX_ITEMS_PER_PAGE, this);
                        } else {
                            // Fetch newer messages from API
                            long lastUpdateTimestamp = TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(roomID);
                            long minCreatedTimestamp = 0L;
                            if (allMessages.size() > 0) {
                                minCreatedTimestamp = allMessages.get(0).getCreated();
                            }
                            getNewerMessagesAfterTimestamp(roomID, minCreatedTimestamp, lastUpdateTimestamp, new TapCoreGetMessageListener() {
                                @Override
                                public void onSuccess(List<TAPMessageModel> messages) {
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
                                        listener.onGetAllMessagesCompleted(allMessages, olderMessages, newerMessages);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
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
