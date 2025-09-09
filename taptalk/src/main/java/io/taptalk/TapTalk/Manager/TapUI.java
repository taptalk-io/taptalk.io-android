package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_OPEN_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.DATA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.CALL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.COMPOSE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.COPY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.DELETE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.EDIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.OPEN_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.PIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.REPORT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.RESCHEDULE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.SAVE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.SEND_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.SEND_NOW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.SMS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.STAR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.UNPIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.UNSTAR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressMenuID.VIEW_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TWO_DAYS_IN_MILLIS;

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
import io.taptalk.TapTalk.Interface.TapLongPressInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Listener.TapUIChatProfileListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomCustomNavigationBarListener;
import io.taptalk.TapTalk.Listener.TapUIChatRoomListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Listener.TapUIMyAccountListener;
import io.taptalk.TapTalk.Listener.TapUIRoomListListener;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPAddGroupMemberActivity;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Activity.TapPinnedMessagesActivity;
import io.taptalk.TapTalk.View.Activity.TapStarredMessagesActivity;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.TapTalk.View.Fragment.TapUIRoomListFragment;

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
    private boolean isAllowDeleteOthersMessageForAdminDisabled;
    private boolean isSaveMediaToGalleryMenuDisabled;
    private boolean isSaveDocumentMenuDisabled;
    private boolean isOpenLinkMenuDisabled;
    private boolean isOpenLinkWithExternalBrowserEnabled;
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
    private boolean isSharedMediaMenuDisabled;
    private boolean isSharedMediaMediasTabHidden;
    private boolean isSharedMediaLinksTabHidden;
    private boolean isSharedMediaDocumentsTabHidden;
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
    private boolean isBlockUserMenuEnabled;
    private boolean isMessageInfoMenuDisabled;
    private boolean isGroupInCommonMenuDisabled;

    public enum LongPressMenuType {
        TYPE_TEXT_MESSAGE,
        TYPE_IMAGE_MESSAGE,
        TYPE_VIDEO_MESSAGE,
        TYPE_FILE_MESSAGE,
        TYPE_VOICE_MESSAGE,
        TYPE_LOCATION_MESSAGE,
        TYPE_NONE
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getRoomListListeners().remove(listener);
        getRoomListListeners().add(listener);
    }

    public void removeRoomListListener(TapUIRoomListListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getRoomListListeners().remove(listener);
    }

    private List<TapUIChatRoomListener> getChatRoomListeners() {
        return null == tapUIChatRoomListeners ? tapUIChatRoomListeners = new ArrayList<>() : tapUIChatRoomListeners;
    }

    public void addChatRoomListener(TapUIChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatRoomListeners().remove(listener);
        getChatRoomListeners().add(listener);
    }

    public void removeChatRoomListener(TapUIChatRoomListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatRoomListeners().remove(listener);
    }

    private List<TapUIChatProfileListener> getChatProfileListeners() {
        return null == tapUIChatProfileListeners ? tapUIChatProfileListeners = new ArrayList<>() : tapUIChatProfileListeners;
    }

    public void addChatProfileListener(TapUIChatProfileListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatProfileListeners().remove(listener);
        getChatProfileListeners().add(listener);
    }

    public void removeChatProfileListener(TapUIChatProfileListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatProfileListeners().remove(listener);
    }

    private List<TapUICustomKeyboardListener> getCustomKeyboardListeners() {
        return null == tapUICustomKeyboardListeners ? tapUICustomKeyboardListeners = new ArrayList<>() : tapUICustomKeyboardListeners;
    }

    public void addCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getCustomKeyboardListeners().remove(listener);
        getCustomKeyboardListeners().add(listener);
    }

    public void removeCustomKeyboardListener(TapUICustomKeyboardListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getCustomKeyboardListeners().remove(listener);
    }

    private List<TapUIChatRoomCustomNavigationBarListener> getChatRoomCustomNavigationBarListeners() {
        return null == tapUIChatRoomCustomNavigationBarListeners ? tapUIChatRoomCustomNavigationBarListeners = new ArrayList<>() : tapUIChatRoomCustomNavigationBarListeners;
    }

    public void addChatRoomCustomNavigationBarListener(TapUIChatRoomCustomNavigationBarListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatRoomCustomNavigationBarListeners().remove(listener);
        getChatRoomCustomNavigationBarListeners().add(listener);
    }

    public void removeChatRoomCustomNavigationBarListener(TapUIChatRoomCustomNavigationBarListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getChatRoomCustomNavigationBarListeners().remove(listener);
    }

    private List<TapUIMyAccountListener> getMyAccountListeners() {
        return null == tapUIMyAccountListeners ? tapUIMyAccountListeners = new ArrayList<>() : tapUIMyAccountListeners;
    }

    public void addMyAccountListener(TapUIMyAccountListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        getMyAccountListeners().remove(listener);
        getMyAccountListeners().add(listener);
    }

    public void removeMyAccountListener(TapUIMyAccountListener listener) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return TapUIMainRoomListFragment.newInstance(instanceKey);
    }

    public void openRoomList(Context context) {
        openRoomList(context, "");
    }

    public void openRoomList(Context context, TAPRoomModel room) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapUIRoomListActivity.start(context, instanceKey, room);
    }

    public void openRoomList(Context context, String roomID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapUIRoomListActivity.start(context, instanceKey, roomID, false);
    }

    public void openGroupChatCreator(Context context) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPAddGroupMemberActivity.start(context, instanceKey);
    }

    public void openNewChat(Context context) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPNewChatActivity.start(context, instanceKey);
    }

    public void openBarcodeScanner(Context context) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TAPBarcodeScannerActivity.start(context, instanceKey);
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapUIChatActivity.start(context, instanceKey, roomModel, null, null, TapTalk.isConnected(instanceKey));
    }

    public void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel, String scrollToMessageWithLocalID) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapUIChatActivity.start(context, instanceKey, roomModel, null, scrollToMessageWithLocalID, TapTalk.isConnected(instanceKey));
    }

    public void openChatRoom(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapStarredMessagesActivity.Companion.start(context, instanceKey, room);
    }

    public void openPinnedMessagesChatRoom(Context context, TAPRoomModel room) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        TapPinnedMessagesActivity.Companion.start(context, instanceKey, room);
    }

    public TapUIRoomListFragment getCurrentTapTalkRoomListFragment() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return currentTapTalkRoomListFragment;
    }

    public void setCurrentTapTalkRoomListFragment(TapUIRoomListFragment currentTapTalkRoomListFragment) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        this.currentTapTalkRoomListFragment = currentTapTalkRoomListFragment;
    }

    public TapUIChatActivity getCurrentTapTalkChatActivity() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return currentTapTalkChatActivity;
    }

    public void setCurrentTapTalkChatActivity(TapUIChatActivity currentTapTalkChatActivity) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        this.currentTapTalkChatActivity = currentTapTalkChatActivity;
    }

    public AppCompatActivity getCurrentForegroundTapTalkActivity() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return null;
        }
        return currentForegroundTapTalkActivity;
    }

    public void setCurrentForegroundTapTalkActivity(AppCompatActivity currentForegroundTapTalkActivity) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSearchChatBarHidden;
    }

    public void setSearchChatBarInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSearchChatBarHidden = !isVisible;
    }

    public boolean isCloseRoomListButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isCloseRoomListButtonVisible;
    }

    public void setCloseButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isCloseRoomListButtonVisible = isVisible;
    }

    public boolean isMyAccountButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isMyAccountButtonHidden;
    }

    public void setMyAccountButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMyAccountButtonHidden = !isVisible;
    }

    public boolean isNewChatButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isNewChatButtonHidden;
    }

    public void setNewChatButtonInRoomListVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isNewChatButtonHidden = !isVisible;
    }

    public boolean isProfileButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isProfileButtonHidden;
    }

    public void setProfileButtonInChatRoomVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isProfileButtonHidden = !isVisible;
    }

    public boolean isNewContactMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isNewContactMenuButtonHidden;
    }

    public void setNewContactMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isNewContactMenuButtonHidden = !isVisible;
    }

    public boolean isScanQRMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isScanQRMenuButtonHidden;
    }

    public void setScanQRMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isScanQRMenuButtonHidden = !isVisible;
    }

    public boolean isNewGroupMenuButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isNewGroupMenuButtonHidden;
    }

    public void setNewGroupMenuButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isLogoutButtonVisible;
    }

    public void setLogoutButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isLogoutButtonVisible = isVisible;
    }

    public boolean isReadStatusHidden() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReadStatusHidden;
    }

    public void setReadStatusVisible(boolean isReadStatusVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        this.isReadStatusHidden = !isReadStatusVisible;
    }

    public boolean isConnectionStatusIndicatorVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isConnectionStatusIndicatorHidden;
    }

    public void setConnectionStatusIndicatorVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isConnectionStatusIndicatorHidden = !isVisible;
    }

    public boolean isDocumentAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isDocumentAttachmentDisabled;
    }

    public void setDocumentAttachmentEnabled(boolean documentAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isDocumentAttachmentDisabled = !documentAttachmentEnabled;
    }

    public boolean isCameraAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isCameraAttachmentDisabled;
    }

    public void setCameraAttachmentEnabled(boolean cameraAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isCameraAttachmentDisabled = !cameraAttachmentEnabled;
    }

    public boolean isGalleryAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isGalleryAttachmentDisabled;
    }

    public void setGalleryAttachmentEnabled(boolean galleryAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isGalleryAttachmentDisabled = !galleryAttachmentEnabled;
    }

    public boolean isLocationAttachmentDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isLocationAttachmentDisabled;
    }

    public void setLocationAttachmentEnabled(boolean locationAttachmentEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isLocationAttachmentDisabled = !locationAttachmentEnabled;
    }

    public boolean isReplyMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReplyMessageMenuDisabled;
    }

    public void setReplyMessageMenuEnabled(boolean replyMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isReplyMessageMenuDisabled = !replyMessageMenuEnabled;
    }

    public boolean isForwardMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isForwardMessageMenuDisabled;
    }

    public void setForwardMessageMenuEnabled(boolean forwardMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isForwardMessageMenuDisabled = !forwardMessageMenuEnabled;
    }

    public boolean isStarMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isStarMessageMenuDisabled;
    }

    public void setStarMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isStarMessageMenuDisabled = !isEnabled;
    }

    public boolean isSharedMediaMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSharedMediaMenuDisabled;
    }

    public void setSharedMediaMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSharedMediaMenuDisabled = !isEnabled;
    }

    public boolean isSharedMediaMediasTabVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSharedMediaMediasTabHidden;
    }

    public void setSharedMediaMediasTabVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSharedMediaMediasTabHidden = !isVisible;
    }

    public boolean isSharedMediaLinksTabVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSharedMediaLinksTabHidden;
    }

    public void setSharedMediaLinksTabVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSharedMediaLinksTabHidden = !isVisible;
    }

    public boolean isSharedMediaDocumentsTabVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSharedMediaDocumentsTabHidden;
    }

    public void setSharedMediaDocumentsTabVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSharedMediaDocumentsTabHidden = !isVisible;
    }

    public boolean isCopyMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isCopyMessageMenuDisabled;
    }

    public void setCopyMessageMenuEnabled(boolean copyMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isCopyMessageMenuDisabled = !copyMessageMenuEnabled;
    }

    public boolean isDeleteMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isDeleteMessageMenuDisabled;
    }

    public void setDeleteMessageMenuEnabled(boolean deleteMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isDeleteMessageMenuDisabled = !deleteMessageMenuEnabled;
    }

    public boolean isAllowDeleteOthersMessageForAdminDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isAllowDeleteOthersMessageForAdminDisabled;
    }

    public void setAllowDeleteOthersMessageForAdminEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isAllowDeleteOthersMessageForAdminDisabled = !isEnabled;
    }

    public boolean isSaveMediaToGalleryMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isSaveMediaToGalleryMenuDisabled;
    }

    public void setSaveMediaToGalleryMenuEnabled(boolean saveMediaToGalleryMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSaveMediaToGalleryMenuDisabled = !saveMediaToGalleryMenuEnabled;
    }

    public boolean isSaveDocumentMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isSaveDocumentMenuDisabled;
    }

    public void setSaveDocumentMenuEnabled(boolean saveDocumentMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSaveDocumentMenuDisabled = !saveDocumentMenuEnabled;
    }

    public boolean isOpenLinkMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isOpenLinkMenuDisabled;
    }

    public void setOpenLinkMenuEnabled(boolean openLinkMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isOpenLinkMenuDisabled = !openLinkMenuEnabled;
    }

    public boolean isOpenLinkWithExternalBrowserEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isOpenLinkWithExternalBrowserEnabled;
    }

    public void setOpenLinkWithExternalBrowserEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isOpenLinkWithExternalBrowserEnabled = isEnabled;
    }

    public boolean isComposeEmailMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isComposeEmailMenuDisabled;
    }

    public void setComposeEmailMenuEnabled(boolean composeEmailMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isComposeEmailMenuDisabled = !composeEmailMenuEnabled;
    }

    public boolean isDialNumberMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isDialNumberMenuDisabled;
    }

    public void setDialNumberMenuEnabled(boolean dialNumberMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isDialNumberMenuDisabled = !dialNumberMenuEnabled;
    }

    public boolean isSendSMSMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isSendSMSMenuDisabled;
    }

    public void setSendSMSMenuEnabled(boolean sendSMSMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSendSMSMenuDisabled = !sendSMSMenuEnabled;
    }

    public boolean isViewProfileMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isViewProfileMenuDisabled;
    }

    public void setViewProfileMenuEnabled(boolean viewProfileMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isViewProfileMenuDisabled = !viewProfileMenuEnabled;
    }

    public boolean isSendMessageMenuDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isSendMessageMenuDisabled;
    }

    public void setSendMessageMenuEnabled(boolean sendMessageMenuEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSendMessageMenuDisabled = !sendMessageMenuEnabled;
    }

    public boolean isMentionUsernameDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isMentionUsernameDisabled;
    }

    public void setMentionUsernameEnabled(boolean mentionUsernameEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMentionUsernameDisabled = !mentionUsernameEnabled;
    }

    public boolean isAddToContactsButtonInChatRoomVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isAddToContactsButtonInChatRoomHidden;
    }

    public void setAddToContactsButtonInChatRoomVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isAddToContactsButtonInChatRoomHidden = !isVisible;
    }

    public boolean isAddToContactsButtonInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isAddToContactsButtonInChatProfileHidden;
    }

    public void setAddToContactsButtonInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isAddToContactsButtonInChatProfileHidden = !isVisible;
    }

    public boolean isEditBioTextFieldVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isEditBioTextFieldHidden;
    }

    public void setEditBioTextFieldVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isEditBioTextFieldHidden = !isVisible;
    }

    public boolean isUsernameInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isUsernameInChatProfileHidden;
    }

    public void setUsernameInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isUsernameInChatProfileHidden = !isVisible;
    }

    public boolean isMobileNumberInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isMobileNumberInChatProfileHidden;
    }

    public void setMobileNumberInChatProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMobileNumberInChatProfileHidden = !isVisible;
    }

    public boolean isEmailAddressInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isEmailAddressInChatProfileVisible;
    }

    public void setEmailAddressInChatProfileVisible(boolean emailAddressInChatProfileVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isEmailAddressInChatProfileVisible = emailAddressInChatProfileVisible;
    }

    public boolean isAddContactDisabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isAddContactDisabled;
    }

    public void setAddContactEnabled(boolean addContactEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isAddContactDisabled = !addContactEnabled;
    }

    public boolean isReportButtonInChatProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReportButtonInChatProfileVisible;
    }

    public void setReportButtonInChatProfileVisible(boolean reportButtonInChatProfileVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isReportButtonInChatProfileVisible = reportButtonInChatProfileVisible;
    }

    public boolean isReportButtonInUserProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReportButtonInUserProfileVisible;
    }

    public void setReportButtonInUserProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isReportButtonInUserProfileVisible = isVisible;
    }

    public boolean isReportButtonInGroupProfileVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReportButtonInGroupProfileVisible;
    }

    public void setReportButtonInGroupProfileVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isReportButtonInGroupProfileVisible = isVisible;
    }

    public boolean isReportMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isReportMessageMenuEnabled;
    }

    public void setReportMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isReportMessageMenuEnabled = isEnabled;
    }

    public boolean isMarkAsUnreadRoomListSwipeMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isMarkAsUnreadRoomListSwipeMenuDisabled;
    }

    public void setMarkAsUnreadRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMarkAsUnreadRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isMarkAsReadRoomListSwipeMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isMarkAsReadRoomListSwipeMenuDisabled;
    }

    public void setMarkAsReadRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMarkAsReadRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isSendVoiceNoteMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSendVoiceNoteMenuDisabled;
    }

    public void setSendVoiceNoteMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSendVoiceNoteMenuDisabled = !isEnabled;
    }

    public boolean isEditMessageMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isEditMessageMenuDisabled;
    }

    public void setEditMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isEditMessageMenuDisabled = !isEnabled;
    }

    public boolean isSavedMessagesMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isSavedMessagesMenuDisabled;
    }

    public void setSavedMessagesMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isSavedMessagesMenuDisabled = !isEnabled;
    }

    public boolean isPinMessageMenuEnabled() {
        return !isPinMessageMenuDisabled;
    }

    public void setPinMessageMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isPinMessageMenuDisabled = !isEnabled;
    }

    public boolean isMuteRoomListSwipeMenuEnabled() {
        return !isMuteRoomListSwipeMenuDisabled;
    }

    public void setMuteRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMuteRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isLinkPreviewInMessageEnabled() {
        return !isLinkPreviewInMessageDisabled;
    }

    public void setLinkPreviewInMessageEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isLinkPreviewInMessageDisabled = !isEnabled;
    }

    public boolean isPinRoomListSwipeMenuEnabled() {
        return !isPinRoomListSwipeMenuDisabled;
    }

    public void setPinRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isPinRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isDeleteRoomListSwipeMenuEnabled() {
        return !isDeleteRoomListSwipeMenuDisabled;
    }

    public void setDeleteRoomListSwipeMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isDeleteRoomListSwipeMenuDisabled = !isEnabled;
    }

    public boolean isScheduledMessageFeatureEnabled() {
        return !isScheduledMessageFeatureDisabled;
    }

    public void setScheduledMessageFeatureEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isScheduledMessageFeatureDisabled = !isEnabled;
    }

    public boolean isMessageInfoMenuEnabled() {
        return !isMessageInfoMenuDisabled;
    }

    public void setMessageInfoMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isMessageInfoMenuDisabled = !isEnabled;
    }

    public boolean isGroupInCommonMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return !isGroupInCommonMenuDisabled;
    }

    public void setGroupInCommonMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isGroupInCommonMenuDisabled = !isEnabled;
    }

    /**
     * ==========================================================================================
     * CUSTOM BUBBLE
     * ==========================================================================================
     */

    public void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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

    public List<TapLongPressMenuItem> getDefaultMessageLongPressMenuItems(Context context, TAPMessageModel messageModel) {
        if (messageModel == null) {
            // Message is null
            return new ArrayList<>();
        }
        if (messageModel.getIsFailedSend() != null && messageModel.getIsFailedSend()) {
            // Message failed to send
            // TODO: ADD ITEMS FOR FAILED MESSAGE
            return new ArrayList<>();
        }
        if (messageModel.getIsDeleted() != null && messageModel.getIsDeleted()) {
            // Deleted message
            return new ArrayList<>();
        }
        if (messageModel.getIsSending() != null && messageModel.getIsSending()) {
            // Message is sending
            return new ArrayList<>();
        }
        LongPressMenuType longPressMenuType = getLongPressMenuForMessageType(messageModel.getType());
        if (longPressMenuType == LongPressMenuType.TYPE_NONE) {
            return new ArrayList<>();
        }

        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();
        HashMap<String, Object> messageData = messageModel.getData();
        String activeUserID = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();

        if (!isReplyMessageMenuDisabled() &&
            !TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(messageModel.getRoom().getRoomID()))
        ) {
            // Reply
            longPressMenuItems.add(new TapLongPressMenuItem(
                REPLY,
                context.getString(R.string.tap_reply),
                R.drawable.tap_ic_reply_orange
            ));
        }

        if (!isForwardMessageMenuDisabled()) {
            // Forward
            longPressMenuItems.add(new TapLongPressMenuItem(
                FORWARD,
                context.getString(R.string.tap_forward),
                R.drawable.tap_ic_forward_orange
            ));
        }

        String caption = "";
        if (messageData != null && messageData.get(CAPTION) != null) {
            caption = (String) messageData.get(CAPTION);
        }
        if (!isCopyMessageMenuDisabled() &&
            (longPressMenuType == LongPressMenuType.TYPE_TEXT_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_LOCATION_MESSAGE ||
            ((longPressMenuType == LongPressMenuType.TYPE_IMAGE_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_VIDEO_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_FILE_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_VOICE_MESSAGE) &&
            caption != null && !caption.isEmpty()))
        ) {
            // Copy
            longPressMenuItems.add(new TapLongPressMenuItem(
                COPY,
                context.getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange
            ));
        }

        if (!isSaveMediaToGalleryMenuDisabled() &&
            (longPressMenuType == LongPressMenuType.TYPE_IMAGE_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_VIDEO_MESSAGE) &&
            TAPCacheManager.getInstance(TapTalk.appContext).containsCache(messageModel)
        ) {
            // Save to gallery
            longPressMenuItems.add(new TapLongPressMenuItem(
                SAVE,
                context.getString(R.string.tap_save_to_gallery),
                R.drawable.tap_ic_download_orange
            ));
        }
        if (!isSaveDocumentMenuDisabled() &&
            longPressMenuType == LongPressMenuType.TYPE_FILE_MESSAGE &&
            TAPCacheManager.getInstance(TapTalk.appContext).containsCache(messageModel)
        ) {
            // Save to downloads
            longPressMenuItems.add(new TapLongPressMenuItem(
                SAVE,
                context.getString(R.string.tap_save_to_downloads),
                R.drawable.tap_ic_download_orange
            ));
        }

        if (isStarMessageMenuEnabled()) {
            ArrayList<String> starredMessageIds = TAPDataManager.getInstance(instanceKey).getStarredMessageIds(messageModel.getRoom().getRoomID());
            if (starredMessageIds.contains(messageModel.getMessageID())) {
                // Unstar
                longPressMenuItems.add(new TapLongPressMenuItem(
                    UNSTAR,
                    context.getString(R.string.tap_unstar),
                    R.drawable.tap_ic_star_filled_primary
                ));
            }
            else {
                // Star
                longPressMenuItems.add(new TapLongPressMenuItem(
                    STAR,
                    context.getString(R.string.tap_star),
                    R.drawable.tap_ic_star_outline
                ));
            }
        }

        if (isEditMessageMenuEnabled() &&
            (longPressMenuType == LongPressMenuType.TYPE_TEXT_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_IMAGE_MESSAGE ||
            longPressMenuType == LongPressMenuType.TYPE_VIDEO_MESSAGE) &&
            null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
            messageModel.getUser().getUserID().equals(activeUserID) &&
            (null == messageModel.getIsSending() || !messageModel.getIsSending()) &&
            (messageModel.getForwardFrom() == null || messageModel.getForwardFrom().getLocalID().isEmpty()) &&
            System.currentTimeMillis() - messageModel.getCreated() < TWO_DAYS_IN_MILLIS
        ) {
            // Edit
            longPressMenuItems.add(new TapLongPressMenuItem(
                EDIT,
                context.getString(R.string.tap_edit),
                R.drawable.tap_ic_edit_orange
            ));
        }

        if (isPinMessageMenuEnabled()) {
            ArrayList<String> pinnedMessageIds = TAPDataManager.getInstance(instanceKey).getPinnedMessageIds(messageModel.getRoom().getRoomID());
            if (pinnedMessageIds.contains(messageModel.getMessageID())) {
                // Unpin
                longPressMenuItems.add(new TapLongPressMenuItem(
                    UNPIN,
                    context.getString(R.string.tap_unpin),
                    R.drawable.tap_ic_unpin_message_orange
                ));
            }
            else {
                // Pin
                longPressMenuItems.add(new TapLongPressMenuItem(
                    PIN,
                    context.getString(R.string.tap_pin),
                    R.drawable.tap_ic_pin_message_orange
                ));
            }
        }

        boolean isMessageFromSelf = activeUserID.equals(messageModel.getUser().getUserID());

        if (isMessageInfoMenuEnabled() &&
            messageModel.getRoom().getType() != TYPE_PERSONAL &&
            null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
            isMessageFromSelf &&
            (null == messageModel.getIsSending() || !messageModel.getIsSending())
        ) {
            // Message Info
            longPressMenuItems.add(new TapLongPressMenuItem(
                INFO,
                context.getString(R.string.tap_message_info),
                R.drawable.tap_ic_info_outline_primary
            ));
        }

        boolean isActiveUserAdmin = false;
        if (messageModel.getRoom().getType() != TYPE_PERSONAL) {
            TAPRoomModel obtainedRoom = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(messageModel.getRoom().getRoomID());
            if (obtainedRoom != null &&
                obtainedRoom.getAdmins() != null &&
                obtainedRoom.getAdmins().contains(activeUserID)
            ) {
                isActiveUserAdmin = true;
            }
        }

        if (!isDeleteMessageMenuDisabled &&
            null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
            (isMessageFromSelf || (isActiveUserAdmin && !isAllowDeleteOthersMessageForAdminDisabled)) &&
            (null == messageModel.getIsSending() || !messageModel.getIsSending())
        ) {
            // Delete
            longPressMenuItems.add(new TapLongPressMenuItem(
                DELETE,
                context.getString(R.string.tap_delete_message),
                R.drawable.tap_ic_delete_red
            ));
        }

        if (isReportMessageMenuEnabled &&
            null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
            !isMessageFromSelf
        ) {
            // Report
            longPressMenuItems.add(new TapLongPressMenuItem(
                REPORT,
                context.getString(R.string.tap_report),
                R.drawable.tap_ic_warning_triangle_red
            ));
        }

        return longPressMenuItems;
    }

    public List<TapLongPressMenuItem> getDefaultLinkLongPressMenuItems(Context context, String url) {
        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();
        HashMap<String, Object> info = new HashMap<>();
        info.put(DATA, url);

        if (!TapUI.getInstance(instanceKey).isOpenLinkMenuDisabled) {
            // Open link
            longPressMenuItems.add(new TapLongPressMenuItem(
                OPEN_LINK,
                context.getString(R.string.tap_open),
                R.drawable.tap_ic_open_link_orange,
                info
            ));
        }

        if (!TapUI.getInstance(instanceKey).isCopyMessageMenuDisabled) {
            // Copy
            longPressMenuItems.add(new TapLongPressMenuItem(
                COPY,
                context.getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ));
        }

        return longPressMenuItems;
    }

    public List<TapLongPressMenuItem> getDefaultEmailLongPressMenuItems(Context context, String emailAddress) {
        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();

        if (!isComposeEmailMenuDisabled) {
            // Compose email
            HashMap<String, Object> info = new HashMap<>();
            info.put(DATA, "mailto:" + emailAddress);
            longPressMenuItems.add(new TapLongPressMenuItem(
                COMPOSE,
                context.getString(R.string.tap_compose),
                R.drawable.tap_ic_mail_orange,
                info
            ));
        }

        if (!isCopyMessageMenuDisabled) {
            // Copy
            HashMap<String, Object> info = new HashMap<>();
            info.put(DATA, emailAddress);
            longPressMenuItems.add(new TapLongPressMenuItem(
                COPY,
                context.getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ));
        }

        return longPressMenuItems;
    }

    public List<TapLongPressMenuItem> getDefaultPhoneLongPressMenuItems(Context context, String phoneNumber) {
        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();
        HashMap<String, Object> info = new HashMap<>();
        info.put(DATA, phoneNumber);

        if (!isDialNumberMenuDisabled) {
            // Dial number
            longPressMenuItems.add(new TapLongPressMenuItem(
                CALL,
                context.getString(R.string.tap_call),
                R.drawable.tap_ic_call_orange,
                info
            ));
        }

        if (!isSendSMSMenuDisabled) {
            // Send SMS
            longPressMenuItems.add(new TapLongPressMenuItem(
                SMS,
                context.getString(R.string.tap_send_sms),
                R.drawable.tap_ic_sms_orange,
                info
            ));
        }

        if (!isCopyMessageMenuDisabled) {
            // Copy
            longPressMenuItems.add(new TapLongPressMenuItem(
                COPY,
                context.getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ));
        }

        return longPressMenuItems;
    }

    public List<TapLongPressMenuItem> getDefaultMentionLongPressMenuItems(Context context, String username) {
        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();
        HashMap<String, Object> info = new HashMap<>();
        info.put(DATA, username);

        if (!TapUI.getInstance(instanceKey).isViewProfileMenuDisabled &&
            TAPChatManager.getInstance(instanceKey).getActiveUser() != null &&
            TAPChatManager.getInstance(instanceKey).getActiveUser().getUsername() != null &&
            !TAPChatManager.getInstance(instanceKey).getActiveUser().getUsername().equals(username)
        ) {
            // View profile
            longPressMenuItems.add(new TapLongPressMenuItem(
                VIEW_PROFILE,
                context.getString(R.string.tap_view_profile),
                R.drawable.tap_ic_contact_orange,
                info
            ));
        }

        if (!TapUI.getInstance(instanceKey).isSendMessageMenuDisabled &&
            TAPChatManager.getInstance(instanceKey).getActiveUser() != null &&
            TAPChatManager.getInstance(instanceKey).getActiveUser().getUsername() != null &&
            !TAPChatManager.getInstance(instanceKey).getActiveUser().getUsername().equals(username)
        ) {
            // Send message
            longPressMenuItems.add(new TapLongPressMenuItem(
                SEND_MESSAGE,
                context.getString(R.string.tap_send_message),
                R.drawable.tap_ic_sms_orange,
                info
            ));
        }

        if (!isCopyMessageMenuDisabled) {
            // Copy
            longPressMenuItems.add(new TapLongPressMenuItem(
                COPY,
                context.getString(R.string.tap_copy),
                R.drawable.tap_ic_copy_orange,
                info
            ));
        }

        return longPressMenuItems;
    }

    public List<TapLongPressMenuItem> getDefaultScheduledMessageLongPressMenuItems(Context context) {
        ArrayList<TapLongPressMenuItem> longPressMenuItems = new ArrayList<>();

        // Send Now
        longPressMenuItems.add(new TapLongPressMenuItem(
            SEND_NOW,
            context.getString(R.string.tap_send_now),
            R.drawable.tap_ic_circle_arrow_up_primary
        ));
        // Reschedule
        longPressMenuItems.add(new TapLongPressMenuItem(
            RESCHEDULE,
            context.getString(R.string.tap_reschedule),
            R.drawable.tap_ic_clock_grey
        ));
        // Copy
        longPressMenuItems.add(new TapLongPressMenuItem(
            COPY,
            context.getString(R.string.tap_copy),
            R.drawable.tap_ic_copy_orange
        ));
        // Edit
        longPressMenuItems.add(new TapLongPressMenuItem(
            EDIT,
            context.getString(R.string.tap_edit),
            R.drawable.tap_ic_edit_orange
        ));
        // Delete
        longPressMenuItems.add(new TapLongPressMenuItem(
            DELETE,
            context.getString(R.string.tap_delete),
            R.drawable.tap_ic_delete_red
        ));

        return longPressMenuItems;
    }

    /**
     * ==========================================================================================
     * ACCOUNT RELATED
     * ==========================================================================================
     */

    public boolean isDeleteAccountButtonVisible() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isDeleteAccountButtonVisible;
    }

    public void setDeleteAccountButtonVisible(boolean isVisible) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
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

    public boolean isBlockUserMenuEnabled() {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return false;
        }
        return isBlockUserMenuEnabled;
    }

    public void setBlockUserMenuEnabled(boolean isEnabled) {
        if (!TapTalk.checkTapTalkInitialized(instanceKey)) {
            return;
        }
        isBlockUserMenuEnabled = isEnabled;
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

    void triggerChatProfileGroupsInCommonButtonTapped(Activity activity, TAPRoomModel room) {
        if (getChatProfileListeners().isEmpty()) {
            return;
        }

        for (TapUIChatProfileListener listener : getChatProfileListeners()) {
            if (null != listener) {
                listener.onGroupInCommonItemTapped(activity, room);
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

    List<TapLongPressMenuItem> getMessageLongPressMenuItems(Context context, TAPMessageModel message) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                return listener.setMessageLongPressMenuItems(context, message);
            }
        }
        return getDefaultMessageLongPressMenuItems(context, message);
    }

    List<TapLongPressMenuItem> getScheduledMessageLongPressMenuItems(Context context, TAPMessageModel message) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                return listener.setScheduledMessageLongPressMenuItems(context, message);
            }
        }
        return getDefaultScheduledMessageLongPressMenuItems(context);
    }

    List<TapLongPressMenuItem> getLinkLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String url) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                return listener.setLinkLongPressMenuItems(context, message, url);
            }
        }
        return getDefaultLinkLongPressMenuItems(context, url);
    }

    List<TapLongPressMenuItem> getEmailLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String emailAddress) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                return listener.setEmailLongPressMenuItems(context, message, emailAddress);
            }
        }
        return getDefaultEmailLongPressMenuItems(context, emailAddress);
    }

    List<TapLongPressMenuItem> getPhoneLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String phoneNumber) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                return listener.setPhoneLongPressMenuItems(context, message, phoneNumber);
            }
        }
        return getDefaultPhoneLongPressMenuItems(context, phoneNumber);
    }

    List<TapLongPressMenuItem> getMentionLongPressMenuItems(Context context, @Nullable TAPMessageModel message, String username) {
        if (!getChatRoomListeners().isEmpty()) {
        for (TapUIChatRoomListener listener : getChatRoomListeners()) {
            return listener.setMentionLongPressMenuItems(context, message, username);
        }
        }
        return getDefaultMentionLongPressMenuItems(context, username);
    }

    void triggerLongPressMenuItemSelected(Activity activity, TapLongPressMenuItem longPressMenuItem, @Nullable TAPMessageModel message) {
        if (!getChatRoomListeners().isEmpty()) {
            for (TapUIChatRoomListener listener : getChatRoomListeners()) {
                listener.onLongPressMenuItemSelected(activity, longPressMenuItem, message);
            }
        }
        else {
            ArrayList<TapLongPressInterface> defaultListeners = TAPLongPressActionBottomSheet.Companion.getListeners();
            if (!defaultListeners.isEmpty()) {
                for (TapLongPressInterface listener : defaultListeners) {
                    listener.onLongPressMenuItemSelected(longPressMenuItem, message);
                }
            }
        }
    }
}
