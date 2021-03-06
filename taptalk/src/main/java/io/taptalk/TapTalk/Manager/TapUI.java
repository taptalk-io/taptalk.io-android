package io.taptalk.TapTalk.Manager;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
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
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIRoomListFragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_OPEN_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TapUI {

    private static HashMap<String, TapUI> instances;

    private String instanceKey = "";

    private List<TapUIRoomListListener> tapUIRoomListListeners;
    private List<TapUIChatRoomListener> tapUIChatRoomListeners;
    private List<TapUICustomKeyboardListener> tapUICustomKeyboardListeners;
    private HashMap<Integer, LongPressMenuType> longPressMenuMap;

    private TapUIRoomListFragment currentTapTalkRoomListFragment;
    private TapUIChatActivity currentTapTalkChatActivity;
    private AppCompatActivity currentForegroundTapTalkActivity;

    private Boolean isAllActivityActionBarEnabled = null;
    private Boolean isChatActivityActionBarEnabled = null;

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
    private boolean isConnectionStatusIndicatorHidden;
    private boolean isDocumentAttachmentDisabled;
    private boolean isCameraAttachmentDisabled;
    private boolean isGalleryAttachmentDisabled;
    private boolean isLocationAttachmentDisabled;
    private boolean isReplyMessageMenuDisabled;
    private boolean isForwardMessageMenuDisabled;
    private boolean isCopyMessageMenuDisabled;
    private boolean isDeleteMessageMenuDisabled;
    private boolean isSaveMediaToGalleryMenuDisabled;
    private boolean isSaveDocumentMenuDisabled;
    private boolean isOpenLinkMenuDisabled;
    private boolean isComposeEmailMenuDisabled;
    private boolean isDialNumberMenuDisabled;
    private boolean isSendSMSMenuDisabled;
    private boolean isViewProfileMenuDisabled;
    private boolean isSendMessageMenuDisabled;
    private boolean isMentionUsernameDisabled;
    private boolean isAddToContactsButtonInChatRoomHidden;
    private boolean isAddToContactsButtonInChatProfileHidden;
    private boolean isAddContactDisabled;

    public enum LongPressMenuType {
        TYPE_TEXT_MESSAGE,
        TYPE_IMAGE_MESSAGE,
        TYPE_VIDEO_MESSAGE,
        TYPE_FILE_MESSAGE,
        TYPE_LOCATION_MESSAGE,
        TYPE_NONE,
    }

    public TapUI(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapUI getInstance() {
        return getInstance("");
    }

    public static TapUI getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapUI instance = new TapUI(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapUI> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    /**
     * ==========================================================================================
     * LISTENERS
     * ==========================================================================================
     */

    private List<TapUIRoomListListener> getRoomListListeners() {
        return null == tapUIRoomListListeners ? tapUIRoomListListeners = new ArrayList<>() : tapUIRoomListListeners;
    }

    public void addRoomListListener(TapUIRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getRoomListListeners().remove(listener);
        getRoomListListeners().add(listener);
    }

    public void removeRoomListListener(TapUIRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getRoomListListeners().remove(listener);
    }

    private List<TapUIChatRoomListener> getChatRoomListeners() {
        return null == tapUIChatRoomListeners ? tapUIChatRoomListeners = new ArrayList<>() : tapUIChatRoomListeners;
    }

    public void addChatRoomListener(TapUIChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatRoomListeners().remove(listener);
        getChatRoomListeners().add(listener);
    }

    public void removeChatRoomListener(TapUIChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatRoomListeners().remove(listener);
    }

    private List<TapUICustomKeyboardListener> getCustomKeyboardListeners() {
        return null == tapUICustomKeyboardListeners ? tapUICustomKeyboardListeners = new ArrayList<>() : tapUICustomKeyboardListeners;
    }

    public void addCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getCustomKeyboardListeners().remove(listener);
        getCustomKeyboardListeners().add(listener);
    }

    public void removeCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getCustomKeyboardListeners().remove(listener);
    }

    /**
     * ==========================================================================================
     * ACTIVITY / FRAGMENT
     * ==========================================================================================
     */

    public TapUIMainRoomListFragment getRoomListFragment() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return TapUIMainRoomListFragment.newInstance(instanceKey);
    }

    public void openRoomList(Context context) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapUIRoomListActivity.start(context, instanceKey);
    }

    public void openGroupChatCreator(Context context) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPAddGroupMemberActivity.start(context, instanceKey);
    }

    public void openNewChat(Context context) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPNewChatActivity.start(context, instanceKey);
    }

    public void openBarcodeScanner(Context context) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPBarcodeScannerActivity.start(context, instanceKey);
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapUIChatActivity.start(context, instanceKey, roomModel, null, null, TapTalk.isConnected(instanceKey));
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel, String scrollToMessageWithLocalID) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapUIChatActivity.start(context, instanceKey, roomModel, null, scrollToMessageWithLocalID, TapTalk.isConnected(instanceKey));
    }

    public void openChatRoom(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapUIChatActivity.start(
                context,
                instanceKey,
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
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        String roomID = TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                userID);
        if (null != customQuoteTitle) {
            TAPChatManager.getInstance(instanceKey).setQuotedMessage(roomID, customQuoteTitle, customQuoteContent, customQuoteImageURL);
            if (null != userInfo) {
                TAPChatManager.getInstance(instanceKey).saveUserInfo(roomID, userInfo);
            }
        }
        if (null != prefilledText) {
            TAPChatManager.getInstance(instanceKey).saveMessageToDraft(roomID, prefilledText);
        }
        TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserData(userID);

        if (null == user) {
            TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(userID, new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    openChatRoom(
                            context,
                            roomID,
                            response.getUser().getName(),
                            response.getUser().getAvatarURL(),
                            TYPE_PERSONAL,
                            "");
                    if (null != listener) {
                        listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
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
        } else {
            openChatRoom(
                    context,
                    roomID,
                    user.getName(),
                    user.getAvatarURL(),
                    TYPE_PERSONAL,
                    "");
            if (null != listener) {
                listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
            }
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
        if (!TapTalk.checkTapTalkInitialized()) {
            listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_INIT_TAPTALK);
            return;
        }
        TAPUtils.getUserFromXcUserID(instanceKey, xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                String roomID = TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                        user.getUserID());
                if (null != customQuoteTitle) {
                    TAPChatManager.getInstance(instanceKey).setQuotedMessage(roomID, customQuoteTitle, customQuoteContent, customQuoteImageURL);
                    if (null != userInfo) {
                        TAPChatManager.getInstance(instanceKey).saveUserInfo(roomID, userInfo);
                    }
                }
                if (null != prefilledText) {
                    TAPChatManager.getInstance(instanceKey).saveMessageToDraft(roomID, prefilledText);
                }
                openChatRoom(
                        context,
                        roomID,
                        user.getName(),
                        user.getAvatarURL(),
                        TYPE_PERSONAL,
                        "");
                if (null != listener) {
                    listener.onSuccess(SUCCESS_MESSAGE_OPEN_ROOM);
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

    public void openChatRoomWithOtherUser(Context context, TAPUserModel otherUser) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        openChatRoom(
                context,
                TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), otherUser.getUserID()),
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
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        if (null != customQuoteTitle) {
            TAPChatManager.getInstance(instanceKey).setQuotedMessage(roomModel.getRoomID(), customQuoteTitle, customQuoteContent, customQuoteImageURL);
            if (null != userInfo) {
                TAPChatManager.getInstance(instanceKey).saveUserInfo(roomModel.getRoomID(), userInfo);
            }
        }
        openChatRoomWithRoomModel(context, roomModel);
    }

    public TapUIRoomListFragment getCurrentTapTalkRoomListFragment() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return currentTapTalkRoomListFragment;
    }

    public void setCurrentTapTalkRoomListFragment(TapUIRoomListFragment currentTapTalkRoomListFragment) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        this.currentTapTalkRoomListFragment = currentTapTalkRoomListFragment;
    }

    public TapUIChatActivity getCurrentTapTalkChatActivity() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return currentTapTalkChatActivity;
    }

    public void setCurrentTapTalkChatActivity(TapUIChatActivity currentTapTalkChatActivity) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        this.currentTapTalkChatActivity = currentTapTalkChatActivity;
    }

    public AppCompatActivity getCurrentForegroundTapTalkActivity() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return null;
        }
        return currentForegroundTapTalkActivity;
    }

    public void setCurrentForegroundTapTalkActivity(AppCompatActivity currentForegroundTapTalkActivity) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        this.currentForegroundTapTalkActivity = currentForegroundTapTalkActivity;
    }

    /**
     * ==========================================================================================
     * CHAT UI FEATURES
     * ==========================================================================================
     */

    public Boolean isAllTapTalkActivityActionBarEnabled() {
        return isAllActivityActionBarEnabled;
    }

    public void setAllTapTalkActivityActionBarEnabled(boolean allActivityActionBarEnabled) {
        isAllActivityActionBarEnabled = allActivityActionBarEnabled;
    }

    public Boolean isTapTalkChatActivityActionBarEnabled() {
        return isChatActivityActionBarEnabled;
    }

    public void setTapTalkChatActivityActionBarEnabled(boolean chatActivityActionBarEnabled) {
        isChatActivityActionBarEnabled = chatActivityActionBarEnabled;
    }

    public boolean isSearchChatBarVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isSearchChatBarHidden;
    }

    public void setSearchChatBarInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSearchChatBarHidden = !isVisible;
    }

    public boolean isCloseRoomListButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isCloseRoomListButtonVisible;
    }

    public void setCloseButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isCloseRoomListButtonVisible = isVisible;
    }

    public boolean isMyAccountButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isMyAccountButtonHidden;
    }

    public void setMyAccountButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMyAccountButtonHidden = !isVisible;
    }

    public boolean isNewChatButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isNewChatButtonHidden;
    }

    public void setNewChatButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isNewChatButtonHidden = !isVisible;
    }

    public boolean isProfileButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isProfileButtonHidden;
    }

    public void setProfileButtonInChatRoomVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isProfileButtonHidden = !isVisible;
    }

    public boolean isNewContactMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isNewContactMenuButtonHidden;
    }

    public void setNewContactMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isNewContactMenuButtonHidden = !isVisible;
    }

    public boolean isScanQRMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isScanQRMenuButtonHidden;
    }

    public void setScanQRMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isScanQRMenuButtonHidden = !isVisible;
    }

    public boolean isNewGroupMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isNewGroupMenuButtonHidden;
    }

    public void setNewGroupMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isNewGroupMenuButtonHidden = !isVisible;
    }

    public boolean isLogoutButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isLogoutButtonVisible;
    }

    public void setLogoutButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isLogoutButtonVisible = isVisible;
    }

    public boolean isReadStatusHidden() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReadStatusHidden;
    }

    public void setReadStatusVisible(boolean isReadStatusVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        this.isReadStatusHidden = !isReadStatusVisible;
    }

    public boolean isConnectionStatusIndicatorVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isConnectionStatusIndicatorHidden;
    }

    public void setConnectionStatusIndicatorVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isConnectionStatusIndicatorHidden = !isVisible;
    }

    public boolean isDocumentAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isDocumentAttachmentDisabled;
    }

    public void setDocumentAttachmentEnabled(boolean documentAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isDocumentAttachmentDisabled = !documentAttachmentEnabled;
    }

    public boolean isCameraAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isCameraAttachmentDisabled;
    }

    public void setCameraAttachmentEnabled(boolean cameraAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isCameraAttachmentDisabled = !cameraAttachmentEnabled;
    }

    public boolean isGalleryAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isGalleryAttachmentDisabled;
    }

    public void setGalleryAttachmentEnabled(boolean galleryAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isGalleryAttachmentDisabled = !galleryAttachmentEnabled;
    }

    public boolean isLocationAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isLocationAttachmentDisabled;
    }

    public void setLocationAttachmentEnabled(boolean locationAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isLocationAttachmentDisabled = !locationAttachmentEnabled;
    }

    public boolean isReplyMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReplyMessageMenuDisabled;
    }

    public void setReplyMessageMenuEnabled(boolean replyMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isReplyMessageMenuDisabled = !replyMessageMenuEnabled;
    }

    public boolean isForwardMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isForwardMessageMenuDisabled;
    }

    public void setForwardMessageMenuEnabled(boolean forwardMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isForwardMessageMenuDisabled = !forwardMessageMenuEnabled;
    }

    public boolean isCopyMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isCopyMessageMenuDisabled;
    }

    public void setCopyMessageMenuEnabled(boolean copyMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isCopyMessageMenuDisabled = !copyMessageMenuEnabled;
    }

    public boolean isDeleteMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isDeleteMessageMenuDisabled;
    }

    public void setDeleteMessageMenuEnabled(boolean deleteMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isDeleteMessageMenuDisabled = !deleteMessageMenuEnabled;
    }

    public boolean isSaveMediaToGalleryMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isSaveMediaToGalleryMenuDisabled;
    }

    public void setSaveMediaToGalleryMenuEnabled(boolean saveMediaToGalleryMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSaveMediaToGalleryMenuDisabled = !saveMediaToGalleryMenuEnabled;
    }

    public boolean isSaveDocumentMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isSaveDocumentMenuDisabled;
    }

    public void setSaveDocumentMenuEnabled(boolean saveDocumentMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSaveDocumentMenuDisabled = !saveDocumentMenuEnabled;
    }

    public boolean isOpenLinkMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isOpenLinkMenuDisabled;
    }

    public void setOpenLinkMenuEnabled(boolean openLinkMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isOpenLinkMenuDisabled = !openLinkMenuEnabled;
    }

    public boolean isComposeEmailMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isComposeEmailMenuDisabled;
    }

    public void setComposeEmailMenuEnabled(boolean composeEmailMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isComposeEmailMenuDisabled = !composeEmailMenuEnabled;
    }

    public boolean isDialNumberMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isDialNumberMenuDisabled;
    }

    public void setDialNumberMenuEnabled(boolean dialNumberMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isDialNumberMenuDisabled = !dialNumberMenuEnabled;
    }

    public boolean isSendSMSMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isSendSMSMenuDisabled;
    }

    public void setSendSMSMenuEnabled(boolean sendSMSMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSendSMSMenuDisabled = !sendSMSMenuEnabled;
    }

    public boolean isViewProfileMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isViewProfileMenuDisabled;
    }

    public void setViewProfileMenuEnabled(boolean viewProfileMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isViewProfileMenuDisabled = !viewProfileMenuEnabled;
    }

    public boolean isSendMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isSendMessageMenuDisabled;
    }

    public void setSendMessageMenuEnabled(boolean sendMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSendMessageMenuDisabled = !sendMessageMenuEnabled;
    }

    public boolean isMentionUsernameDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isMentionUsernameDisabled;
    }

    public void setMentionUsernameEnabled(boolean mentionUsernameEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMentionUsernameDisabled = !mentionUsernameEnabled;
    }

    public boolean isAddToContactsButtonInChatRoomVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isAddToContactsButtonInChatRoomHidden;
    }

    public void setAddToContactsButtonInChatRoomVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isAddToContactsButtonInChatRoomHidden = !isVisible;
    }

    public boolean isAddToContactsButtonInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isAddToContactsButtonInChatProfileHidden;
    }

    public void setAddToContactsButtonInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isAddToContactsButtonInChatProfileHidden = !isVisible;
    }

    public boolean isAddContactDisabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isAddContactDisabled;
    }

    public void setAddContactEnabled(boolean addContactEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isAddContactDisabled = !addContactEnabled;
    }

    /**
     * ==========================================================================================
     * CUSTOM BUBBLE
     * ==========================================================================================
     */

    public void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TAPCustomBubbleManager.getInstance(instanceKey).addCustomBubbleMap(baseCustomBubble);
    }

    private HashMap<Integer, LongPressMenuType> generateDefaultLongPressMenuMap() {
        HashMap<Integer, LongPressMenuType> defaultLongPressMenuMap = new HashMap<>();
        defaultLongPressMenuMap.put(TYPE_TEXT, LongPressMenuType.TYPE_TEXT_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_IMAGE, LongPressMenuType.TYPE_IMAGE_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_VIDEO, LongPressMenuType.TYPE_VIDEO_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_FILE, LongPressMenuType.TYPE_FILE_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_LOCATION, LongPressMenuType.TYPE_LOCATION_MESSAGE);
        return defaultLongPressMenuMap;
    }

    private HashMap<Integer, LongPressMenuType> getLongPressMenuMap() {
        if (null == longPressMenuMap) {
            longPressMenuMap = generateDefaultLongPressMenuMap();
        }
        return longPressMenuMap;
    }

    public LongPressMenuType getLongPressMenuForMessageType(int messageType) {
        LongPressMenuType longPressMenuType = getLongPressMenuMap().get(messageType);
        if (null != longPressMenuType) {
            return  longPressMenuType;
        }
        return LongPressMenuType.TYPE_NONE;
    }

    public void setLongPressMenuForMessageType(int messageType, LongPressMenuType longPressMenuType) {
        if (Integer.toString(messageType).charAt(0) != '3') {
            // Return if message type is not custom (starts with 3)
            return;
        }
        getLongPressMenuMap().put(messageType, longPressMenuType);
    }

    /**
     * ==========================================================================================
     * NON-PUBLIC METHODS
     * ==========================================================================================
     */

    void triggerSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        if (getRoomListListeners().isEmpty() && null != mainRoomListFragment) {
            mainRoomListFragment.showSearchChat();
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                if (null != listener) {
                    listener.onSearchChatBarTapped(activity, mainRoomListFragment);
                }
            }
        }
    }

    void triggerCloseRoomListTapped(Activity activity) {
        if (getRoomListListeners().isEmpty() && null != activity) {
            activity.onBackPressed();
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                if (null != listener) {
                    listener.onCloseRoomListTapped(activity);
                }
            }
        }
    }

    void triggerTapTalkAccountButtonTapped(Activity activity) {
        if (getRoomListListeners().isEmpty()) {
            TAPMyAccountActivity.Companion.start(activity, instanceKey);
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                if (null != listener) {
                    listener.onTapTalkAccountButtonTapped(activity);
                }
            }
        }
    }

    void triggerNewChatButtonTapped(Activity activity) {
        if (getRoomListListeners().isEmpty()) {
            TAPNewChatActivity.start(activity, instanceKey);
        } else {
            for (TapUIRoomListListener listener : getRoomListListeners()) {
                if (null != listener) {
                    listener.onNewChatButtonTapped(activity);
                }
            }
        }
    }

    void triggerChatRoomOpened(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkChatRoomOpened(activity, room, otherUser);
            }
        }
    }

    void triggerChatRoomClosed(Activity activity, TAPRoomModel room, @Nullable TAPUserModel otherUser) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkChatRoomClosed(activity, room, otherUser);
            }
        }
    }

    void triggerActiveUserSendMessage(Activity activity, TAPMessageModel messageModel, TAPRoomModel room) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkActiveUserSendMessage(activity, messageModel, room);
            }
        }
    }

    void triggerChatRoomProfileButtonTapped(Activity activity, TAPRoomModel room, @Nullable TAPUserModel user) {
        if (room.getRoomType() == TYPE_TRANSACTION) {
            return;
        }
        if (getChatRoomListeners().isEmpty()) {
            TAPChatProfileActivity.start(activity, instanceKey, room, user);
        } else {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                if (room.getRoomType() == TYPE_PERSONAL) {
                    if (null != listener) {
                        listener.onTapTalkUserProfileButtonTapped(activity, room, user);
                    }
                } else if (room.getRoomType() == TYPE_GROUP && null != user) {
                    if (null != listener) {
                        listener.onTapTalkGroupMemberAvatarTapped(activity, room, user);
                    }
                } else if (room.getRoomType() == TYPE_GROUP) {
                    if (null != listener) {
                        listener.onTapTalkGroupChatProfileButtonTapped(activity, room);
                    }
                }
            }
        }
    }

    void triggerUserMentionTapped(Activity activity, TAPMessageModel message, TAPUserModel user, boolean isRoomParticipant) {
        if (getChatRoomListeners().isEmpty()) {
            if (isRoomParticipant) {
                // Open member profile
                TAPChatProfileActivity.start(activity, instanceKey, message.getRoom(), user);
            } else {
                // Open personal profile
                TAPChatProfileActivity.start(
                        activity,
                        instanceKey,
                        TAPRoomModel.Builder(
                                TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                                        user.getUserID()),
                                user.getName(),
                                TYPE_PERSONAL,
                                user.getAvatarURL(),
                                ""), // TODO: 13 Apr 2020 ROOM COLOR
                        user,
                        true);
            }
        } else {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                listener.onTapTalkUserMentionTapped(activity, message, user, isRoomParticipant);
            }
        }
    }

    void triggerMessageQuoteTapped(Activity activity, TAPMessageModel messageModel) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            HashMap<String, Object> userInfo = null;
            if (null != messageModel.getData() && null != messageModel.getData().get(USER_INFO)) {
                userInfo = (HashMap<String, Object>) messageModel.getData().get(USER_INFO);
            }
            if (null != listener) {
                listener.onTapTalkMessageQuoteTapped(activity, messageModel, userInfo);
            }
        }
    }

    void triggerProductListBubbleLeftOrSingleButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkProductListBubbleLeftOrSingleButtonTapped(activity, product, room, recipient, isSingleOption);
            }
        }
    }

    void triggerProductListBubbleRightButtonTapped(Activity activity, TAPProductModel product, TAPRoomModel room, TAPUserModel recipient, boolean isSingleOption) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkProductListBubbleRightButtonTapped(activity, product, room, recipient, isSingleOption);
            }
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
            if (null != listener) {
                listener.onCustomKeyboardItemTapped(activity, customKeyboardItemModel, room, activeUser, recipientUser);
            }
        }
    }
}
