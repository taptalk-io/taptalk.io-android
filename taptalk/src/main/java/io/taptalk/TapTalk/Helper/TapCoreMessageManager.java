package io.taptalk.TapTalk.Helper;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapSendMessageInterface;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Interface.TapMessageInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;

public class TapCoreMessageManager {

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

    public static void markMessageAsRead(TAPMessageModel message) {
        TAPMessageStatusManager.getInstance().addReadMessageQueue(message);
    }

    public static void getOlderMessagesBeforeTimestamp(String roomID, long maxCreatedTimestamp, TapMessageInterface listener) {
        TAPDataManager.getInstance().getMessageListByRoomBefore(roomID, maxCreatedTimestamp,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(String.valueOf(OTHER_ERRORS), e.getMessage());
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
                        listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
                    }
                });
    }

    public static void getNewerMessagesAfterTimestamp(String roomID, long minCreatedTimestamp, long lastUpdateTimestamp, TapMessageInterface listener) {
        TAPDataManager.getInstance().getMessageListByRoomAfter(roomID, minCreatedTimestamp, lastUpdateTimestamp,
                new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
                    @Override
                    public void onSuccess(TAPGetMessageListByRoomResponse response) {
                        List<TAPMessageModel> messageAfterModels = new ArrayList<>();
                        for (HashMap<String, Object> messageMap : response.getMessages()) {
                            try {
                                messageAfterModels.add(TAPEncryptorManager.getInstance().decryptMessage(messageMap));
                            } catch (Exception e) {
                                listener.onError(String.valueOf(OTHER_ERRORS), e.getMessage());
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
                        listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
                    }
                });
    }
}
