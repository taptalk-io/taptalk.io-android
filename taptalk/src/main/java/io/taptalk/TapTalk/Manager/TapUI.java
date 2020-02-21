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
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomListener;
import io.taptalk.TapTalk.Listener.TapUIRoomListListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPAddGroupMemberActivity;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIRoomListFragment;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_OPEN_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_GROUP_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TapUI {

    private static TapUI instance;

    private List<TapUIRoomListListener> tapUIRoomListListeners;
    private List<TapUIChatRoomListener> tapUIChatRoomListeners;
    private List<TapUICustomKeyboardListener> tapUICustomKeyboardListeners;

    private boolean isSearchChatBarHidden;
    private boolean isCloseRoomListButtonVisible;
    private boolean isMyAccountButtonHidden;
    private boolean isNewChatButtonHidden;
    private boolean isProfileButtonHidden;
    private boolean isNewContactMenuButtonHidden;
    private boolean isScanQRMenuButtonHidden;
    private boolean isNewGroupMenuButtonHidden;
    private boolean isLogoutButtonVisible;
    private boolean isReadStatusHidden;

    public static TapUI getInstance() {
        return null == instance ? instance = new TapUI() : instance;
    }

    private List<TapUIRoomListListener> getRoomListListeners() {
        return null == tapUIRoomListListeners ? tapUIRoomListListeners = new ArrayList<>() : tapUIRoomListListeners;
    }

    public void addRoomListListener(TapUIRoomListListener listener) {
        getRoomListListeners().add(listener);
    }

    public void removeRoomListListener(TapUIRoomListListener listener) {
        getRoomListListeners().remove(listener);
    }

    private List<TapUIChatRoomListener> getChatRoomListeners() {
        return null == tapUIChatRoomListeners ? tapUIChatRoomListeners = new ArrayList<>() : tapUIChatRoomListeners;
    }

    public void addChatRoomListener(TapUIChatRoomListener listener) {
        getChatRoomListeners().add(listener);
    }

    public void removeChatRoomListener(TapUIChatRoomListener listener) {
        getChatRoomListeners().remove(listener);
    }

    private List<TapUICustomKeyboardListener> getCustomKeyboardListeners() {
        return null == tapUICustomKeyboardListeners ? tapUICustomKeyboardListeners = new ArrayList<>() : tapUICustomKeyboardListeners;
    }

    public void addCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        getCustomKeyboardListeners().add(listener);
    }

    public void removeCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        getCustomKeyboardListeners().remove(listener);
    }

    public TapUIMainRoomListFragment getRoomListFragment() {
        return TapUIMainRoomListFragment.newInstance();
    }

    public void openRoomList(Context context) {
        Intent intent = new Intent(context, TapUIRoomListActivity.class);
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
        TAPUtils.startChatActivity(context, roomModel, null, null);
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel, String scrollToMessageWithLocalID) {
        TAPUtils.startChatActivity(context, roomModel, null, scrollToMessageWithLocalID);
    }

    public void openChatRoom(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        TAPUtils.startChatActivity(
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
        TAPUtils.getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
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

    public boolean isSearchChatBarVisible() {
        return !isSearchChatBarHidden;
    }

    public void setSearchChatBarInRoomListVisible(boolean isVisible) {
        isSearchChatBarHidden = !isVisible;
    }

    public boolean isCloseRoomListButtonVisible() {
        return isCloseRoomListButtonVisible;
    }

    public void setCloseButtonInRoomListVisible(boolean isVisible) {
        isCloseRoomListButtonVisible = isVisible;
    }

    public boolean isMyAccountButtonVisible() {
        return !isMyAccountButtonHidden;
    }

    public void setMyAccountButtonInRoomListVisible(boolean isVisible) {
        isMyAccountButtonHidden = !isVisible;
    }

    public boolean isNewChatButtonVisible() {
        return !isNewChatButtonHidden;
    }

    public void setNewChatButtonInRoomListVisible(boolean isVisible) {
        isNewChatButtonHidden = !isVisible;
    }

    public boolean isProfileButtonVisible() {
        return !isProfileButtonHidden;
    }

    public void setProfileButtonInChatRoomVisible(boolean isVisible) {
        isProfileButtonHidden = !isVisible;
    }

    public boolean isNewContactMenuButtonVisible() {
        return !isNewContactMenuButtonHidden;
    }

    public void setNewContactMenuButtonVisible(boolean isVisible) {
        isNewContactMenuButtonHidden = !isVisible;
    }

    public boolean isScanQRMenuButtonVisible() {
        return !isScanQRMenuButtonHidden;
    }

    public void setScanQRMenuButtonVisible(boolean isVisible) {
        isScanQRMenuButtonHidden = !isVisible;
    }

    public boolean isNewGroupMenuButtonVisible() {
        return !isNewGroupMenuButtonHidden;
    }

    public void setNewGroupMenuButtonVisible(boolean isVisible) {
        isNewGroupMenuButtonHidden = !isVisible;
    }

    public boolean isLogoutButtonVisible() {
        return isLogoutButtonVisible;
    }

    public void setLogoutButtonVisible(boolean isVisible) {
        isLogoutButtonVisible = isVisible;
    }

    public boolean isReadStatusHidden() {
        return isReadStatusHidden;
    }

    public void setReadStatusHidden(boolean isReadStatusHidden) {
        this.isReadStatusHidden = isReadStatusHidden;
    }

    public void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        TAPCustomBubbleManager.getInstance().addCustomBubbleMap(baseCustomBubble);
    }

    void triggerSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        if (getRoomListListeners().isEmpty() && null != mainRoomListFragment) {
            mainRoomListFragment.showSearchChat();
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                listener.onSearchChatBarTapped(activity, mainRoomListFragment);
            }
        }
    }

    void triggerCloseRoomListTapped(Activity activity) {
        if (getRoomListListeners().isEmpty() && null != activity) {
            activity.onBackPressed();
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                listener.onCloseRoomListTapped(activity);
            }
        }
    }

    void triggerTapTalkAccountButtonTapped(Activity activity) {
        if (getRoomListListeners().isEmpty()) {
            WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
            Intent intent = new Intent(contextWeakReference.get(), TAPMyAccountActivity.class);
            contextWeakReference.get().startActivity(intent);
            contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                listener.onTapTalkAccountButtonTapped(activity);
            }
        }
    }

    void triggerNewChatButtonTapped(Activity activity) {
        if (getRoomListListeners().isEmpty()) {
            WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
            Intent intent = new Intent(contextWeakReference.get(), TAPNewChatActivity.class);
            contextWeakReference.get().startActivity(intent);
            contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                listener.onNewChatButtonTapped(activity);
            }
        }
    }

    void triggerChatRoomProfileButtonTapped(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
        if (room.getRoomType() == TYPE_TRANSACTION) {
            return;
        }
        if (getChatRoomListeners().isEmpty()) {
            WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
            Intent intent = new Intent(contextWeakReference.get(), TAPChatProfileActivity.class);
            intent.putExtra(ROOM, room);
            if (room.getRoomType() == TYPE_PERSONAL) {
                contextWeakReference.get().startActivity(intent);
            } else if (room.getRoomType() == TYPE_GROUP && null != user) {
                intent.putExtra(K_USER, user);
                contextWeakReference.get().startActivityForResult(intent, OPEN_MEMBER_PROFILE);
            } else if (room.getRoomType() == TYPE_GROUP) {
                contextWeakReference.get().startActivityForResult(intent, OPEN_GROUP_PROFILE);
            }
            contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        } else {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                if (room.getRoomType() == TYPE_PERSONAL) {
                    listener.onTapTalkUserProfileButtonTapped(activity, room, user);
                } else if (room.getRoomType() == TYPE_GROUP && null != user) {
                    listener.onTapTalkGroupMemberAvatarTapped(activity, room, user);
                } else if (room.getRoomType() == TYPE_GROUP) {
                    listener.onTapTalkGroupChatProfileButtonTapped(activity, room);
                }
            }
        }
    }

    void triggerMessageQuoteTapped(Activity activity, TAPMessageModel messageModel) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            HashMap<String, Object> userInfo = null;
            if (null != messageModel.getData() && null != messageModel.getData().get(USER_INFO)) {
                userInfo = (HashMap<String, Object>) messageModel.getData().get(USER_INFO);
            }
            listener.onTapTalkMessageQuoteTapped(activity, messageModel, userInfo);
        }
    }

    void triggerProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            listener.onTapTalkProductListBubbleLeftOrSingleButtonTapped(activity, product, room, recipient, isSingleOption);
        }
    }

    void triggerProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            listener.onTapTalkProductListBubbleRightButtonTapped(activity, product, room, recipient, isSingleOption);
        }
    }

    List<TAPCustomKeyboardItemModel> getCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, TAPUserModel recipientUser) {
        for (TapUICustomKeyboardListener listener : getCustomKeyboardListeners()) {
            List<TAPCustomKeyboardItemModel> items = listener.setCustomKeyboardItems(room, activeUser, recipientUser);
            if (null != items && !items.isEmpty()) {
                return items;
            }
        }
        return null;
    }

    void triggerCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPRoomModel room, TAPUserModel activeUser, TAPUserModel recipientUser) {
        for (TapUICustomKeyboardListener listener : getCustomKeyboardListeners()) {
            listener.onCustomKeyboardItemTapped(activity, customKeyboardItemModel, room, activeUser, recipientUser);
        }
    }
}
