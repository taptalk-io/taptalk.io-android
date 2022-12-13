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
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreChatRoomListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Listener.TapUIChatProfileListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomCustomNavigationBarListener;
import io.taptalk.TapTalk.Listener.TapUIMyAccountListener;
import io.taptalk.TapTalk.Listener.TapUIRoomListListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPAddGroupMemberActivity;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Activity.TapPinnedMessagesActivity;
import io.taptalk.TapTalk.View.Activity.TapStarredMessagesActivity;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIRoomListFragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_OPEN_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TapUI {

    private static HashMap<String, TapUI> instances;

    private String instanceKey = "";

    private List<TapUIRoomListListener> tapUIRoomListListeners;
    private List<TapUIChatRoomListener> tapUIChatRoomListeners;
    private List<TapUIChatProfileListener> tapUIChatProfileListeners;
    private List<TapUICustomKeyboardListener> tapUICustomKeyboardListeners;
    private List<TapUIChatRoomCustomNavigationBarListener> tapUIChatRoomCustomNavigationBarListeners;
    private List<TapUIMyAccountListener> tapUIMyAccountListeners;
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
    private boolean isChangeProfilePictureButtonHidden;
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
    private boolean isEditBioTextFieldHidden;
    private boolean isUsernameInChatProfileHidden;
    private boolean isMobileNumberInChatProfileHidden;
    private boolean isEmailAddressInChatProfileVisible;
    private boolean isAddContactDisabled;
    private boolean isReportButtonInChatProfileVisible;
    private boolean isReportButtonInUserProfileVisible;
    private boolean isReportButtonInGroupProfileVisible;
    private boolean isReportMessageMenuEnabled;
    private boolean isMarkAsReadRoomListSwipeMenuDisabled;
    private boolean isMarkAsUnreadRoomListSwipeMenuDisabled;
    private boolean isStarMessageMenuDisabled;
    private boolean isSendVoiceNoteMenuDisabled;
    private boolean isEditMessageMenuDisabled;
    private boolean isDeleteAccountButtonVisible;
    private boolean isSavedMessagesMenuDisabled;
    private boolean isPinMessageMenuDisabled;
    private boolean isMuteRoomListSwipeMenuDisabled;
    private boolean isLinkPreviewInMessageDisabled;
    private boolean isPinRoomListSwipeMenuDisabled;
    private boolean isDeleteRoomListSwipeMenuDisabled;
    private boolean isScheduledMessageFeatureDisabled;

    public enum LongPressMenuType {
        TYPE_TEXT_MESSAGE,
        TYPE_IMAGE_MESSAGE,
        TYPE_VIDEO_MESSAGE,
        TYPE_FILE_MESSAGE,
        TYPE_VOICE_MESSAGE,
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

    private List<TapUIChatProfileListener> getChatProfileListeners() {
        return null == tapUIChatProfileListeners ? tapUIChatProfileListeners = new ArrayList<>() : tapUIChatProfileListeners;
    }

    public void addChatProfileListener(TapUIChatProfileListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatProfileListeners().remove(listener);
        getChatProfileListeners().add(listener);
    }

    public void removeChatProfileListener(TapUIChatProfileListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatProfileListeners().remove(listener);
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

    private List<TapUIChatRoomCustomNavigationBarListener> getChatRoomCustomNavigationBarListeners() {
        return null == tapUIChatRoomCustomNavigationBarListeners ? tapUIChatRoomCustomNavigationBarListeners = new ArrayList<>() : tapUIChatRoomCustomNavigationBarListeners;
    }

    public void addChatRoomCustomNavigationBarListener(TapUIChatRoomCustomNavigationBarListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatRoomCustomNavigationBarListeners().remove(listener);
        getChatRoomCustomNavigationBarListeners().add(listener);
    }

    public void removeChatRoomCustomNavigationBarListener(TapUIChatRoomCustomNavigationBarListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getChatRoomCustomNavigationBarListeners().remove(listener);
    }

    private List<TapUIMyAccountListener> getMyAccountListeners() {
        return null == tapUIMyAccountListeners ? tapUIMyAccountListeners = new ArrayList<>() : tapUIMyAccountListeners;
    }

    public void addMyAccountListener(TapUIMyAccountListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getMyAccountListeners().remove(listener);
        getMyAccountListeners().add(listener);
    }

    public void removeMyAccountListener(TapUIMyAccountListener listener) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        getMyAccountListeners().remove(listener);
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
                            response.getUser().getFullname(),
                            response.getUser().getImageURL(),
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
                    user.getFullname(),
                    user.getImageURL(),
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
                        user.getFullname(),
                        user.getImageURL(),
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
                otherUser.getFullname(),
                otherUser.getImageURL(),
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

    public void openSavedMessagesChatRoom(Context context) {
        TapCoreChatRoomManager.getInstance(instanceKey).getSavedMessagesChatRoom(new TapCoreGetRoomListener() {
            @Override
            public void onSuccess(TAPRoomModel room) {
                super.onSuccess(room);
                openChatRoomWithRoomModel(context, room);
            }
        });
    }

    public void openStarredMessagesPage(Context context, TAPRoomModel room) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapStarredMessagesActivity.Companion.start(context, instanceKey, room);
    }

    public void openPinnedMessagesChatRoom(Context context, TAPRoomModel room) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        TapPinnedMessagesActivity.Companion.start(context, instanceKey, room);
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

    public boolean isChangeProfilePictureButtonVisible() {
        return !isChangeProfilePictureButtonHidden;
    }

    public void setChangeProfilePictureButtonVisible(boolean isVisible) {
        isChangeProfilePictureButtonHidden = !isVisible;
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

    public boolean isStarMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isStarMessageMenuDisabled;
    }

    public void setStarMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isStarMessageMenuDisabled = !isEnabled;
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

    public boolean isEditBioTextFieldVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isEditBioTextFieldHidden;
    }

    public void setEditBioTextFieldVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isEditBioTextFieldHidden = !isVisible;
    }

    public boolean isUsernameInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isUsernameInChatProfileHidden;
    }

    public void setUsernameInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isUsernameInChatProfileHidden = !isVisible;
    }

    public boolean isMobileNumberInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isMobileNumberInChatProfileHidden;
    }

    public void setMobileNumberInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMobileNumberInChatProfileHidden = !isVisible;
    }

    public boolean isEmailAddressInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isEmailAddressInChatProfileVisible;
    }

    public void setEmailAddressInChatProfileVisible(boolean emailAddressInChatProfileVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isEmailAddressInChatProfileVisible = emailAddressInChatProfileVisible;
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

    public boolean isReportButtonInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReportButtonInChatProfileVisible;
    }

    public void setReportButtonInChatProfileVisible(boolean reportButtonInChatProfileVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isReportButtonInChatProfileVisible = reportButtonInChatProfileVisible;
    }

    public boolean isReportButtonInUserProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReportButtonInUserProfileVisible;
    }

    public void setReportButtonInUserProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isReportButtonInUserProfileVisible = isVisible;
    }

    public boolean isReportButtonInGroupProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReportButtonInGroupProfileVisible;
    }

    public void setReportButtonInGroupProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isReportButtonInGroupProfileVisible = isVisible;
    }

    public boolean isReportMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isReportMessageMenuEnabled;
    }

    public void setReportMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isReportMessageMenuEnabled = isEnabled;
    }

    public boolean isMarkAsUnreadRoomListSwipeMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isMarkAsUnreadRoomListSwipeMenuDisabled;
    }

    public void setMarkAsUnreadRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMarkAsUnreadRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isMarkAsReadRoomListSwipeMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isMarkAsReadRoomListSwipeMenuDisabled;
    }

    public void setMarkAsReadRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMarkAsReadRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isSendVoiceNoteMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isSendVoiceNoteMenuDisabled;
    }

    public void setSendVoiceNoteMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSendVoiceNoteMenuDisabled = !isEnabled;
    }

    public boolean isEditMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isEditMessageMenuDisabled;
    }

    public void setEditMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isEditMessageMenuDisabled = !isEnabled;
    }

    public boolean isSavedMessagesMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return !isSavedMessagesMenuDisabled;
    }

    public void setSavedMessagesMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isSavedMessagesMenuDisabled = !isEnabled;
    }

    public boolean isPinMessageMenuEnabled() {
        return !isPinMessageMenuDisabled;
    }

    public void setPinMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isPinMessageMenuDisabled = !isEnabled;
    }

    public boolean isMuteRoomListSwipeMenuEnabled() {
        return !isMuteRoomListSwipeMenuDisabled;
    }

    public void setMuteRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isMuteRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isLinkPreviewInMessageEnabled() {
        return !isLinkPreviewInMessageDisabled;
    }

    public void setLinkPreviewInMessageEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isLinkPreviewInMessageDisabled = !isEnabled;
    }

    public boolean isPinRoomListSwipeMenuEnabled() {
        return !isPinRoomListSwipeMenuDisabled;
    }

    public void setPinRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isPinRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isDeleteRoomListSwipeMenuEnabled() {
        return !isDeleteRoomListSwipeMenuDisabled;
    }

    public void setDeleteRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isDeleteRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isScheduledMessageFeatureEnabled() {
        return !isScheduledMessageFeatureDisabled;
    }

    public void setScheduledMessageFeatureEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isScheduledMessageFeatureDisabled = !isEnabled;
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
        defaultLongPressMenuMap.put(TYPE_LINK, LongPressMenuType.TYPE_TEXT_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_IMAGE, LongPressMenuType.TYPE_IMAGE_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_VIDEO, LongPressMenuType.TYPE_VIDEO_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_FILE, LongPressMenuType.TYPE_FILE_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_LOCATION, LongPressMenuType.TYPE_LOCATION_MESSAGE);
        defaultLongPressMenuMap.put(TYPE_VOICE, LongPressMenuType.TYPE_VOICE_MESSAGE);
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
     * ACCOUNT RELATED
     * ==========================================================================================
     */

    public boolean isDeleteAccountButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized()) {
            return false;
        }
        return isDeleteAccountButtonVisible;
    }

    public void setDeleteAccountButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized()) {
            return;
        }
        isDeleteAccountButtonVisible = isVisible;
    }

    public void triggerDeleteButtonInMyAccountPageTapped(Activity activity) {
        for (TapUIMyAccountListener listener : getMyAccountListeners()) {
            if (null != listener) {
                listener.onDeleteButtonInMyAccountPageTapped(activity);
            }
        }
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
            activity.finish();
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
        if (room.getType() == TYPE_TRANSACTION) {
            return;
        }
        if (getChatRoomListeners().isEmpty()) {
            TAPChatProfileActivity.Companion.start(activity, instanceKey, room, user);
        } else {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                if (room.getType() == TYPE_PERSONAL) {
                    if (null != listener) {
                        listener.onTapTalkUserProfileButtonTapped(activity, room, user);
                    }
                } else if (room.getType() == TYPE_GROUP && null != user) {
                    if (null != listener) {
                        listener.onTapTalkGroupMemberAvatarTapped(activity, room, user);
                    }
                } else if (room.getType() == TYPE_GROUP) {
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
                TAPChatProfileActivity.Companion.start(activity, instanceKey, message.getRoom(), user);
            } else {
                // Open personal profile
                TAPChatProfileActivity.Companion.start(
                        activity,
                        instanceKey,
                        TAPRoomModel.Builder(
                                TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(),
                                        user.getUserID()),
                                user.getFullname(),
                                TYPE_PERSONAL,
                                user.getImageURL(),
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

    void triggerChatProfileReportUserButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel reportedUser) {
        if (getChatProfileListeners().isEmpty()) {
            return;
        }

        for (TapUIChatProfileListener listener : getChatProfileListeners()) {
            if (null != listener) {
                listener.onReportUserButtonTapped(activity, room, reportedUser);
            }
        }
    }

    void triggerChatProfileReportGroupButtonTapped(Activity activity, TAPRoomModel room) {
        if (getChatProfileListeners().isEmpty()) {
            return;
        }

        for (TapUIChatProfileListener listener : getChatProfileListeners()) {
            if (null != listener) {
                listener.onReportGroupButtonTapped(activity, room);
            }
        }
    }

    void triggerChatProfileStarredMessageButtonTapped(Activity activity, TAPRoomModel room) {
        if (getChatRoomListeners().isEmpty()) {
            TapStarredMessagesActivity.Companion.start(activity, instanceKey, room);
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onTapTalkStarredMessageButtonTapped(activity, room);
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

    void triggerSavedMessageBubbleArrowTapped(TAPMessageModel messageModel) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onSavedMessageBubbleArrowTapped(messageModel);
            }
        }
    }

    void triggerPinnedMessageTapped(TAPMessageModel message) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onPinnedMessageTapped(message);
            }
        }
    }

    void triggerReportMessageButtonTapped(Activity activity, TAPMessageModel message) {
        if (getChatRoomListeners().isEmpty()) {
            return;
        }

        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            if (null != listener) {
                listener.onReportMessageButtonTapped(activity, message);
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

    TapBaseChatRoomCustomNavigationBarFragment getChatRoomCustomNavigationBar(Activity activity, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
        for (TapUIChatRoomCustomNavigationBarListener listener : getChatRoomCustomNavigationBarListeners()) {
            if (null != listener) {
                return listener.setCustomChatRoomNavigationBar(activity, room, activeUser, recipientUser);
            }
        }
        return null;
    }

    String getRoomListTitleText(TAPRoomListModel roomList, int position, Context context) {
        if (getRoomListListeners().isEmpty()) {
            return TAPChatManager.getInstance(instanceKey).getDefaultRoomListTitleText(roomList, position, context);
        }
        for (TapUIRoomListListener listener : getRoomListListeners()) {
            return listener.setRoomListTitleText(roomList, position, context);
        }
        return "";
    }

    String getRoomListContentText(TAPRoomListModel roomList, int position, Context context) {
        if (getRoomListListeners().isEmpty()) {
            return TAPChatManager.getInstance(instanceKey).getDefaultRoomListContentText(roomList, position, context);
        }
        for (TapUIRoomListListener listener : getRoomListListeners()) {
            return listener.setRoomListContentText(roomList, position, context);
        }
        return "";
    }
}
