package io.taptalk.TapTalk.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapUIListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPAddGroupMemberActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;
import io.taptalk.TapTalk.View.Fragment.TAPMainRoomListFragment;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_OPEN_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapUI {

    private static TapUI instance;

    private List<TapUIListener> tapUIListeners;

    public static TapUI getInstance() {
        return null == instance ? instance = new TapUI() : instance;
    }

    private List<TapUIListener> getTapUIListeners() {
        return null == tapUIListeners ? tapUIListeners = new ArrayList<>() : tapUIListeners;
    }

    public void addUIListener(TapUIListener listener) {
        getTapUIListeners().add(listener);
    }

    public void removeUIListener(TapUIListener listener) {
        getTapUIListeners().remove(listener);
    }

    public TAPMainRoomListFragment getRoomListFragment() {
        return TAPMainRoomListFragment.newInstance();
    }

    public void openRoomList(Context context) {
        Intent intent = new Intent(context, TAPRoomListActivity.class);
        context.startActivity(intent);
    }

    public void openGroupChatCreator(Context context) {
        Intent intent = new Intent(context, TAPAddGroupMemberActivity.class);
        intent.putExtra(GROUP_ACTION, CREATE_GROUP);
        context.startActivity(intent);
    }

    public void openNewChat(Context context) {
        Intent intent = new Intent(context, TAPNewChatActivity.class);
        context.startActivity(intent);
    }

    public void openBarcodeScanner(Context context) {
        Intent intent = new Intent(context, TAPBarcodeScannerActivity.class);
        context.startActivity(intent);
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel) {
        TAPUtils.getInstance().startChatActivity(context, roomModel, null, null);
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel, String scrollToMessageWithLocalID) {
        TAPUtils.getInstance().startChatActivity(context, roomModel, null, scrollToMessageWithLocalID);
    }

    public void openChatRoom(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        TAPUtils.getInstance().startChatActivity(
                context,
                roomID,
                roomName,
                roomImage,
                roomType,
                roomColor);
    }

    public void openChatRoomWithUserID(
            Context context,
            String userID,
            @Nullable String prefilledText,
            @Nullable String customQuoteTitle,
            @Nullable String customQuoteContent,
            @Nullable String customQuoteImageURL,
            @Nullable HashMap<String, Object> userInfo,
            TapCommonListener listener) {
        String roomID = TAPChatManager.getInstance().arrangeRoomId(
                TAPChatManager.getInstance().getActiveUser().getUserID(),
                userID);
        if (null != customQuoteTitle) {
            TAPChatManager.getInstance().setQuotedMessage(roomID, customQuoteTitle, customQuoteContent, customQuoteImageURL);
            if (null != userInfo) {
                TAPChatManager.getInstance().saveUserInfo(roomID, userInfo);
            }
        }
        if (null != prefilledText) {
            TAPChatManager.getInstance().saveMessageToDraft(roomID, prefilledText);
        }
        TAPUserModel user = TAPContactManager.getInstance().getUserData(userID);

        if (null == user) {
            TAPDataManager.getInstance().getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {

                    openChatRoom(
                            context,
                            roomID,
                            response.getUser().getName(),
                            response.getUser().getAvatarURL(),
                            TYPE_PERSONAL,
                            "");
                    listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
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
            openChatRoom(
                    context,
                    roomID,
                    user.getName(),
                    user.getAvatarURL(),
                    TYPE_PERSONAL,
                    "");
            listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
        }
    }

    public void openChatRoomWithXCUserID(
            Context context,
            String xcUserID,
            @Nullable String prefilledText,
            @Nullable String customQuoteTitle,
            @Nullable String customQuoteContent,
            @Nullable String customQuoteImageURL,
            @Nullable HashMap<String, Object> userInfo,
            TapCommonListener listener) {
        TAPUtils.getInstance().getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                String roomID = TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        user.getUserID());
                if (null != customQuoteTitle) {
                    TAPChatManager.getInstance().setQuotedMessage(roomID, customQuoteTitle, customQuoteContent, customQuoteImageURL);
                    if (null != userInfo) {
                        TAPChatManager.getInstance().saveUserInfo(roomID, userInfo);
                    }
                }
                if (null != prefilledText) {
                    TAPChatManager.getInstance().saveMessageToDraft(roomID, prefilledText);
                }
                openChatRoom(
                        context,
                        roomID,
                        user.getName(),
                        user.getAvatarURL(),
                        TYPE_PERSONAL,
                        "");
                listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onError(ERROR_CODE_OTHERS, errorMessage);
            }
        });
    }

    public void openChatRoomWithOtherUser(Context context, TAPUserModel otherUser) {
        openChatRoom(
                context,
                TAPChatManager.getInstance().arrangeRoomId(TAPChatManager.getInstance().getActiveUser().getUserID(), otherUser.getUserID()),
                otherUser.getName(),
                otherUser.getAvatarURL(),
                TYPE_PERSONAL,
                "");
    }

    public void openChatRoomWithRoomModel(
            Context context,
            TAPRoomModel roomModel,
            @Nullable String prefilledText,
            @Nullable String customQuoteTitle,
            @Nullable String customQuoteContent,
            @Nullable String customQuoteImageURL,
            @Nullable HashMap<String, Object> userInfo) {
        if (null != customQuoteTitle) {
            TAPChatManager.getInstance().setQuotedMessage(roomModel.getRoomID(), customQuoteTitle, customQuoteContent, customQuoteImageURL);
            if (null != userInfo) {
                TAPChatManager.getInstance().saveUserInfo(roomModel.getRoomID(), userInfo);
            }
        }
        openChatRoomWithRoomModel(context, roomModel);
    }

    public static void openTapTalkUserProfile(Context context, TAPUserModel userModel) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        TAPDataManager.getInstance().getRoomModel(userModel, new TAPDatabaseListener<TAPRoomModel>() {
            @Override
            public void onSelectFinished(TAPRoomModel roomModel) {
                if (null == contextWeakReference.get()) {
                    return;
                }
                Intent intent = new Intent(contextWeakReference.get(), TAPChatProfileActivity.class);
                intent.putExtra(ROOM, roomModel);
                contextWeakReference.get().startActivity(intent);
                if (contextWeakReference.get() instanceof Activity) {
                    ((Activity) contextWeakReference.get()).overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
                }
            }
        });
    }

    public static void openTapTalkUserProfile(Context context, String xcUserID) {
        TAPUtils.getInstance().getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel userModel) {
                openTapTalkUserProfile(context, userModel);
            }

            @Override
            public void onSelectFailed(String errorMessage) {
            }
        });
    }

    public void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        TAPCustomBubbleManager.getInstance().addCustomBubbleMap(baseCustomBubble);
    }

    void triggerChatRoomProfileButtonTapped(Activity activity, TAPUserModel user) {
        for (TapUIListener listener : getTapUIListeners()) {
            listener.onTapTalkChatRoomProfileButtonTapped(activity, user);
        }
    }

    void triggerMessageQuoteTapped(Activity activity, TAPMessageModel messageModel) {
        for (TapUIListener listener : getTapUIListeners()) {
            HashMap<String, Object> userInfo = null;
            if (null != messageModel.getData() && null != messageModel.getData().get(USER_INFO)) {
                userInfo = (HashMap<String, Object>) messageModel.getData().get(USER_INFO);
            }
            listener.onTapTalkMessageQuoteTapped(activity, messageModel, userInfo);
        }
    }
}
