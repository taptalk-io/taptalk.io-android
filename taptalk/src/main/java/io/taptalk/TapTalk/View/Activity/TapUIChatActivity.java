package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackLayout;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet;
import io.taptalk.TapTalk.View.Fragment.TAPConnectionStatusFragment;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.CLOSE_ACTIVITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_TYPING_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.JUMP_TO_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MEDIA_PREVIEWS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LOCATION_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.FORWARD_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_GROUP_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_PREVIEW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.DESCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_ADD_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_REMOVE_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_EMIT_DELAY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UNREAD_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity.RESULT_FILE_PATH;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.CHAT_BUBBLE_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.EMAIL_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.LINK_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.PHONE_TYPE;

public class TapUIChatActivity extends TAPBaseChatActivity {

    private String TAG = TapUIChatActivity.class.getSimpleName();

    // View
    private SwipeBackLayout sblChat;
    private TAPChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private FrameLayout flMessageList, flRoomUnavailable, flChatComposerAndHistory;
    private LinearLayout llButtonDeleteChat;
    private ConstraintLayout clContainer, clContactAction, clUnreadButton,
            clEmptyChat, clQuote, clChatComposer, clRoomOnlineStatus, clRoomTypingStatus,
            clChatHistory;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivButtonDismissContactAction, ivUnreadButtonImage,
            ivButtonCancelReply, ivChatMenu, ivButtonChatMenu, ivButtonAttach, ivSend, ivButtonSend,
            ivToBottom, ivRoomTypingIndicator;
    private CircleImageView civRoomImage, civMyAvatarEmpty, civRoomAvatarEmpty;
    private TAPRoundedCornerImageView rcivQuoteImage;
    private TextView tvRoomName, tvRoomStatus, tvRoomImageLabel, tvUnreadButtonCount,
            tvChatEmptyGuide, tvMyAvatarLabelEmpty, tvRoomAvatarLabelEmpty, tvProfileDescription,
            tvQuoteTitle, tvQuoteContent, tvBadgeUnread, tvButtonBlockContact, tvButtonAddToContacts,
            tvRoomTypingStatus, tvChatHistoryContent, tvMessage;
    private View vRoomImage, vStatusBadge, vQuoteDecoration;
    private TAPConnectionStatusFragment fConnectionStatus;

    // RecyclerView
    private TAPMessageAdapter messageAdapter;
    private TAPCustomKeyboardAdapter customKeyboardAdapter;
    private LinearLayoutManager messageLayoutManager;

    // RoomDatabase
    private TAPChatViewModel vm;

    private TAPSocketListener socketListener;

    private RequestManager glide;

    //enum Scrolling
    private enum STATE {
        WORKING, LOADED, DONE
    }

    private STATE state = STATE.WORKING;

    private boolean deleteGroup = false;

    //endless scroll Listener
    TAPEndlessScrollListener endlessScrollListener;

    /**
     * =========================================================================================== *
     * OVERRIDE METHODS
     * =========================================================================================== *
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_chat);

        glide = Glide.with(this);
        bindViews();
        initRoom();
        registerBroadcastManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TAPChatManager.getInstance().setActiveRoom(vm.getRoom());
        etChat.setText(TAPChatManager.getInstance().getMessageFromDraft());
        showQuoteLayout(vm.getQuotedMessage(), vm.getQuoteAction(), false);
//        if (0 < etChat.getText().length() && etChat.getText().length() >= vm.getPreviousEditTextSelectionIndex()) {
//            etChat.setSelection(vm.getPreviousEditTextSelectionIndex());
//        }

        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            callApiGetUserByUserID();
        } else {
            callApiGetGroupData();
        }

        if (vm.isInitialAPICallFinished() && vm.getMessageModels().size() > 0 && TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
            // TODO: 17 Dec 2019 API ALREADY CALLED ON SOCKET CONNECT?
            callApiAfter();
        } else if (vm.isInitialAPICallFinished() && TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
            fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraftToManager();
        sendTypingEmit(false);
        TAPChatManager.getInstance().deleteActiveRoom();
//        vm.setPreviousEditTextSelectionIndex(etChat.getSelectionEnd());
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendTypingEmitDelayTimer.cancel();
        typingIndicatorTimeoutTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Reload UI in room list
        Intent intent = new Intent(RELOAD_ROOM_LIST);
        intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
        LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

        TAPBroadcastManager.unregister(this, broadcastReceiver);
        TAPChatManager.getInstance().updateUnreadCountInRoomList(TAPChatManager.getInstance().getOpenRoom());
        TAPChatManager.getInstance().setOpenRoom(null); // Reset open room
        TAPChatManager.getInstance().removeChatListener(chatListener);
        TAPConnectionManager.getInstance().removeSocketListener(socketListener);
        vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable); // Stop offline timer
        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(true);
        TAPFileDownloadManager.getInstance().clearFailedDownloads(); // Remove failed download list from active room
    }

    @Override
    public void onBackPressed() {
        if (deleteGroup && !TAPGroupManager.Companion.getGetInstance().getRefreshRoomList()) {
            TAPGroupManager.Companion.getGetInstance().setRefreshRoomList(true);
        }
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            hideKeyboards();
        } else {
            //TAPNotificationManager.getInstance().updateUnreadCount();
            new Thread(() -> TAPChatManager.getInstance().putUnsentMessageToList()).start();
            if (isTaskRoot()) {
                // Trigger listener callback if no other activity is open
                for (TapListener listener : TapTalk.getTapTalkListeners()) {
                    listener.onTaskRootChatRoomClosed(this);
                }
            }
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            // Set active room to prevent null pointer when returning to chat
            TAPChatManager.getInstance().setActiveRoom(vm.getRoom());
            switch (requestCode) {
                case SEND_IMAGE_FROM_CAMERA:
                    if (null != intent && null != intent.getData()) {
                        vm.setCameraImageUri(intent.getData());
                    }
                    if (null == vm.getCameraImageUri()) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> imageCameraUris = new ArrayList<>();
                    imageCameraUris.add(TAPMediaPreviewModel.Builder(vm.getCameraImageUri(), TYPE_IMAGE, true));
                    openMediaPreviewPage(imageCameraUris);
                    break;
                case SEND_MEDIA_FROM_GALLERY:
                    if (null == intent) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> galleryMediaPreviews = new ArrayList<>();
                    ClipData clipData = intent.getClipData();
                    if (null != clipData) {
                        // Multiple media selection
                        galleryMediaPreviews = TAPUtils.getInstance().getPreviewsFromClipData(TapUIChatActivity.this, clipData, true);
                    } else {
                        // Single media selection
                        Uri uri = intent.getData();
                        galleryMediaPreviews.add(TAPMediaPreviewModel.Builder(uri, TAPUtils.getInstance().getMessageTypeFromFileUri(TapUIChatActivity.this, uri), true));
                    }
                    openMediaPreviewPage(galleryMediaPreviews);
                    break;
                case SEND_MEDIA_FROM_PREVIEW:
                    ArrayList<TAPMediaPreviewModel> medias = intent.getParcelableArrayListExtra(MEDIA_PREVIEWS);
                    if (null != medias && 0 < medias.size()) {
                        TAPChatManager.getInstance().sendImageOrVideoMessage(TapTalk.appContext, vm.getRoom(), medias);
                    }
                    break;
                case FORWARD_MESSAGE:
                    TAPRoomModel room = intent.getParcelableExtra(ROOM);
                    if (room.getRoomID().equals(vm.getRoom().getRoomID())) {
                        // Show message in composer
                        showQuoteLayout(intent.getParcelableExtra(MESSAGE), FORWARD, false);
                    } else {
                        // Open selected chat room
                        TAPChatManager.getInstance().setQuotedMessage(room.getRoomID(), intent.getParcelableExtra(MESSAGE), FORWARD);
                        TAPUtils.getInstance().startChatActivity(TapUIChatActivity.this, room);
                        finish();
                    }
                    break;
                case PICK_LOCATION:
                    String address = intent.getStringExtra(LOCATION_NAME) == null ? "" : intent.getStringExtra(LOCATION_NAME);
                    Double latitude = intent.getDoubleExtra(LATITUDE, 0.0);
                    Double longitude = intent.getDoubleExtra(LONGITUDE, 0.0);
                    TAPChatManager.getInstance().sendLocationMessage(address, latitude, longitude);
                    break;
                case SEND_FILE:
                    File tempFile = new File(intent.getStringExtra(RESULT_FILE_PATH));
                    if (null != tempFile) {
                        if (TAPFileUploadManager.getInstance().isSizeAllowedForUpload(tempFile.length()))
                            TAPChatManager.getInstance().sendFileMessage(TapUIChatActivity.this, tempFile);
                        else {
                            new TapTalkDialog.Builder(TapUIChatActivity.this)
                                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                    .setTitle("Sorry")
                                    .setMessage("Maximum file size is " + TAPUtils.getInstance().getStringSizeLengthFile(TAPFileUploadManager.getInstance().getMaxFileUploadSize()) + ".")
                                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                    .show();
                        }
                    }
                    break;

                case OPEN_GROUP_PROFILE:
                    deleteGroup = true;
                    rvCustomKeyboard.setVisibility(View.GONE);
                    onBackPressed();
                    break;
                case OPEN_MEMBER_PROFILE:
                    if (intent.getBooleanExtra(CLOSE_ACTIVITY, false)) {
                        rvCustomKeyboard.setVisibility(View.GONE);
                        onBackPressed();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                case PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA:
                    vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.getInstance().pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE:
                    if (null != messageAdapter) {
                        messageAdapter.notifyDataSetChanged();
                    }
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_FILE:
                    TAPUtils.getInstance().openDocumentPicker(TapUIChatActivity.this);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO:
                    if (null != attachmentListener) {
                        attachmentListener.onSaveVideoToGallery(vm.getPendingDownloadMessage());
                    }
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE:
                    startFileDownload(vm.getPendingDownloadMessage());
                    break;
                case PERMISSION_LOCATION:
                    TAPUtils.getInstance().openLocationPicker(TapUIChatActivity.this);
                    break;
            }
        }
    }

    /**
     * =========================================================================================== *
     * PRIVATE METHODS
     * =========================================================================================== *
     */

    private void initRoom() {
        if (initViewModel()) {
            initView();
            initHelper();
            initListener();
            cancelNotificationWhenEnterRoom();
            //registerBroadcastManager();

//            if (null != clChatHistory) {
//                clChatHistory.setVisibility(View.GONE);
//            }
//            if (null != clChatComposer) {
//                clChatComposer.setVisibility(View.VISIBLE);
//            }

        } else if (vm.getMessageModels().size() == 0) {
            initView();
        }
    }

    private void bindViews() {
        sblChat = getSwipeBackLayout();
        flMessageList = (FrameLayout) findViewById(R.id.fl_message_list);
        flRoomUnavailable = (FrameLayout) findViewById(R.id.fl_room_unavailable);
        flChatComposerAndHistory = (FrameLayout) findViewById(R.id.fl_chat_composer_and_history);
        llButtonDeleteChat = (LinearLayout) findViewById(R.id.ll_button_delete_chat);
        clContainer = (ConstraintLayout) findViewById(R.id.cl_container);
        clContactAction = (ConstraintLayout) findViewById(R.id.cl_contact_action);
        clUnreadButton = (ConstraintLayout) findViewById(R.id.cl_unread_button);
        clEmptyChat = (ConstraintLayout) findViewById(R.id.cl_empty_chat);
        clChatHistory = (ConstraintLayout) findViewById(R.id.cl_chat_history);
        clQuote = (ConstraintLayout) findViewById(R.id.cl_quote);
        clChatComposer = (ConstraintLayout) findViewById(R.id.cl_chat_composer);
        clRoomOnlineStatus = (ConstraintLayout) findViewById(R.id.cl_room_online_status);
        clRoomTypingStatus = (ConstraintLayout) findViewById(R.id.cl_room_typing_status);
        ivButtonBack = (ImageView) findViewById(R.id.iv_button_back);
        ivRoomIcon = (ImageView) findViewById(R.id.iv_room_icon);
        ivButtonDismissContactAction = (ImageView) findViewById(R.id.iv_button_dismiss_contact_action);
        ivUnreadButtonImage = (ImageView) findViewById(R.id.iv_unread_button_image);
        ivButtonCancelReply = (ImageView) findViewById(R.id.iv_cancel_reply);
        ivChatMenu = (ImageView) findViewById(R.id.iv_chat_menu);
        ivButtonChatMenu = (ImageView) findViewById(R.id.iv_chat_menu_area);
        ivButtonAttach = (ImageView) findViewById(R.id.iv_attach);
        ivSend = (ImageView) findViewById(R.id.iv_send);
        ivButtonSend = (ImageView) findViewById(R.id.iv_send_area);
        ivToBottom = (ImageView) findViewById(R.id.iv_to_bottom);
        ivRoomTypingIndicator = (ImageView) findViewById(R.id.iv_room_typing_indicator);
        civRoomImage = (CircleImageView) findViewById(R.id.civ_room_image);
        civMyAvatarEmpty = (CircleImageView) findViewById(R.id.civ_my_avatar_empty);
        civRoomAvatarEmpty = (CircleImageView) findViewById(R.id.civ_room_avatar_empty);
        rcivQuoteImage = (TAPRoundedCornerImageView) findViewById(R.id.rciv_quote_image);
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomStatus = (TextView) findViewById(R.id.tv_room_status);
        tvRoomImageLabel = (TextView) findViewById(R.id.tv_room_image_label);
        tvRoomTypingStatus = (TextView) findViewById(R.id.tv_room_typing_status);
        tvButtonBlockContact = (TextView) findViewById(R.id.tv_button_block_contact);
        tvButtonAddToContacts = (TextView) findViewById(R.id.tv_button_add_to_contacts);
        tvUnreadButtonCount = (TextView) findViewById(R.id.tv_unread_button_count);
        tvChatEmptyGuide = (TextView) findViewById(R.id.tv_chat_empty_guide);
        tvMyAvatarLabelEmpty = (TextView) findViewById(R.id.tv_my_avatar_label_empty);
        tvRoomAvatarLabelEmpty = (TextView) findViewById(R.id.tv_room_avatar_label_empty);
        tvProfileDescription = (TextView) findViewById(R.id.tv_profile_description);
        tvQuoteTitle = (TextView) findViewById(R.id.tv_quote_title);
        tvQuoteContent = (TextView) findViewById(R.id.tv_quote_content);
        tvBadgeUnread = (TextView) findViewById(R.id.tv_badge_unread);
        tvChatHistoryContent = (TextView) findViewById(R.id.tv_chat_history_content);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        rvMessageList = (TAPChatRecyclerView) findViewById(R.id.rv_message_list);
        rvCustomKeyboard = (RecyclerView) findViewById(R.id.rv_custom_keyboard);
        etChat = (EditText) findViewById(R.id.et_chat);
        vRoomImage = findViewById(R.id.v_room_image);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        vQuoteDecoration = findViewById(R.id.v_quote_decoration);
        fConnectionStatus = (TAPConnectionStatusFragment) getSupportFragmentManager().findFragmentById(R.id.f_connection_status);
    }

    private boolean initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPChatViewModel.class);
        if (null == vm.getRoom()) {
            vm.setRoom(getIntent().getParcelableExtra(ROOM));
        }
        if (null == vm.getMyUserModel()) {
            vm.setMyUserModel(TAPChatManager.getInstance().getActiveUser());
        }

        if (null == vm.getRoom()) {
            Toast.makeText(TapTalk.appContext, getString(R.string.tap_error_room_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        if (null == vm.getOtherUserModel() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            vm.setOtherUserModel(TAPContactManager.getInstance().getUserData(vm.getOtherUserID()));
        }

        if (TYPE_GROUP == vm.getRoom().getRoomType() && TAPGroupManager.Companion
                .getGetInstance().checkIsRoomDataAvailable(vm.getRoom().getRoomID())) {
            vm.setRoom(TAPGroupManager.Companion.getGetInstance().getGroupData(vm.getRoom().getRoomID()));
        }

        if (null != getIntent().getStringExtra(JUMP_TO_MESSAGE) && !vm.isInitialAPICallFinished()) {
            vm.setTappedMessageLocalID(getIntent().getStringExtra(JUMP_TO_MESSAGE));
        }

        getInitialUnreadCount();

        return null != vm.getMyUserModel() && (null != vm.getOtherUserModel() || (TYPE_PERSONAL != vm.getRoom().getRoomType()));
    }

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        // Set room name
        if (vm.getRoom().getRoomType() == TYPE_PERSONAL && null != vm.getOtherUserModel() &&
                (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                !vm.getOtherUserModel().getName().isEmpty()) {
            tvRoomName.setText(vm.getOtherUserModel().getName());
        } else {
            tvRoomName.setText(vm.getRoom().getRoomName());
        }

        if (!TapUI.getInstance().isProfileButtonVisible()) {
            civRoomImage.setVisibility(View.GONE);
            vRoomImage.setVisibility(View.GONE);
            tvRoomImageLabel.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getRoomType() && null != vm.getOtherUserModel() &&
                (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                null != vm.getOtherUserModel().getAvatarURL().getThumbnail() &&
                !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
            // Load user avatar URL
            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
            vm.getRoom().setRoomImage(vm.getOtherUserModel().getAvatarURL());
        } else if (null != vm.getRoom() && !vm.getRoom().isRoomDeleted() && null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
            // Load room image
            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel);
        } else {
            loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getUserRole() &&
                null != vm.getOtherUserModel().getUserRole().getRoleIconURL() && null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getRoomType() &&
                !vm.getOtherUserModel().getUserRole().getRoleIconURL().isEmpty()) {
            glide.load(vm.getOtherUserModel().getUserRole().getRoleIconURL()).into(ivRoomIcon);
            ivRoomIcon.setVisibility(View.VISIBLE);
        } else {
            ivRoomIcon.setVisibility(View.GONE);
        }

//        // Set typing status
//        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
//            vm.setOtherUserTyping(true);
//            showTypingIndicator();
//        }

        if (null != getIntent().getStringExtra(GROUP_TYPING_MAP)) {
            try {
                String tempGroupTyping = getIntent().getStringExtra(GROUP_TYPING_MAP);
                Gson gson = new Gson();
                Type typingType = new TypeToken<LinkedHashMap<String, TAPUserModel>>() {
                }.getType();
                vm.setGroupTyping(gson.fromJson(tempGroupTyping, typingType));
                if (0 < vm.getGroupTypingSize()) showTypingIndicator();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize chat message RecyclerView
        messageAdapter = new TAPMessageAdapter(glide, chatListener);
        messageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.setAdapter(messageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        // FIXME: 9 November 2018 IMAGES/VIDEOS CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0);
        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvMessageList.getItemAnimator();
        if (null != messageAnimator) {
            messageAnimator.setSupportsChangeAnimations(false);
        }

        // Hide chat composer if room is locked
        if (vm.getRoom().isLocked()) {
            clChatComposer.setVisibility(View.GONE);
        }

        // Initialize custom keyboard
        vm.setCustomKeyboardItems(TAPChatManager.getInstance().getCustomKeyboardItems(vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel()));
        if (null != vm.getCustomKeyboardItems() && vm.getCustomKeyboardItems().size() > 0) {
            // Enable custom keyboard
            vm.setCustomKeyboardEnabled(true);
            customKeyboardAdapter = new TAPCustomKeyboardAdapter(vm.getCustomKeyboardItems(), customKeyboardItemModel -> {
                TAPChatManager.getInstance().triggerCustomKeyboardItemTapped(TapUIChatActivity.this, customKeyboardItemModel, vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel());
                hideUnreadButton();
            });
            rvCustomKeyboard.setAdapter(customKeyboardAdapter);
            rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        } else {
            // Disable custom keyboard
            vm.setCustomKeyboardEnabled(false);
            ivChatMenu.setVisibility(View.GONE);
            ivButtonChatMenu.setVisibility(View.GONE);
        }

        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_personal_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(String.format(getString(R.string.tap_personal_chat_room_empty_guide_content), vm.getRoom().getRoomName()));
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType() && null != vm.getRoom().getGroupParticipants()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_group_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
            tvRoomStatus.setText(String.format("%d Members", vm.getRoom().getGroupParticipants().size()));
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_group_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
        }

        // Listener for scroll pagination
        endlessScrollListener = new TAPEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!vm.isOnBottom()) {
                    loadMoreMessagesFromDatabase();
                }
            }
        };

        // Load items from database for the first time
        if (vm.getRoom().isRoomDeleted()) {
            //showRoomIsUnavailableState();
            showChatAsHistory(getString(R.string.tap_group_unavailable));
        } else if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getDeleted()) {
            showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
        }
//        else if (vm.getMessageModels().size() == 0 && !vm.getRoom().isRoomDeleted()) {
//            //vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
//            getAllUnreadMessage();
//        }

        if (vm.getMessageModels().size() == 0) {
            getAllUnreadMessage();
        }

        rvMessageList.addOnScrollListener(messageListScrollListener);

        LayoutTransition containerTransition = clContainer.getLayoutTransition();
        containerTransition.addTransitionListener(containerTransitionListener);

        etChat.addTextChangedListener(chatWatcher);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

        sblChat.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        sblChat.setSwipeInterface(swipeInterface);

        vRoomImage.setOnClickListener(v -> openRoomProfile());
        ivButtonBack.setOnClickListener(v -> closeActivity());
        tvButtonBlockContact.setOnClickListener(v -> blockContact());
        tvButtonAddToContacts.setOnClickListener(v -> addUserToContacts());
        ivButtonDismissContactAction.setOnClickListener(v -> dismissContactAction());
        ivButtonCancelReply.setOnClickListener(v -> hideQuoteLayout());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> buildAndSendTextMessage());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
        flMessageList.setOnClickListener(v -> chatListener.onOutsideClicked());

//        // TODO: 19 July 2019 SHOW CHAT AS HISTORY IF ACTIVE USER IS NOT IN PARTICIPANT LIST
//        if (null == vm.getRoom().getGroupParticipants()) {
//            showChatAsHistory(getString(R.string.tap_not_a_participant));
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llButtonDeleteChat.setBackground(getDrawable(R.drawable.tap_bg_button_destructive_ripple));
            ivButtonAttach.setBackground(getDrawable(R.drawable.tap_bg_chat_composer_attachment_ripple));
            ivToBottom.setBackground(getDrawable(R.drawable.tap_bg_scroll_to_bottom_ripple));
            clUnreadButton.setBackground(getDrawable(R.drawable.tap_bg_white_rounded_8dp_ripple));
        }
    }

    private void initHelper() {
        TAPChatManager.getInstance().addChatListener(chatListener);
    }

    private void initListener() {
        socketListener = new TAPSocketListener() {
            @Override
            public void onSocketConnected() {
                if (!vm.isInitialAPICallFinished()) {
                    // Call Message List API
                    if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType()) {
                        callApiGetGroupData();
                    } else if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType())
                        callApiGetUserByUserID();
                } else if (vm.getMessageModels().size() > 0) {
                    callApiAfter();
                } else {
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                }
                restartFailedDownloads();
            }
        };
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private void registerBroadcastManager() {
        TAPBroadcastManager.register(this, broadcastReceiver, UploadProgressLoading,
                UploadProgressFinish, UploadFailed, UploadCancelled,
                DownloadProgressLoading, DownloadFinish, DownloadFailed, DownloadFile, OpenFile,
                CancelDownload, LongPressChatBubble, LongPressEmail, LongPressLink, LongPressPhone);
    }

    private void closeActivity() {
        rvCustomKeyboard.setVisibility(View.GONE);
        onBackPressed();
    }

    private void blockContact() {
        // TODO: 19 November 2019
        clContactAction.setVisibility(View.GONE);
    }

    private void addUserToContacts() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance().addContactApi(vm.getOtherUserID(), addContactView);
    }

    private void dismissContactAction() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance().saveChatRoomContactActionDismissed(vm.getRoom().getRoomID());
    }

    private void cancelNotificationWhenEnterRoom() {
        TAPNotificationManager.getInstance().cancelNotificationWhenEnterRoom(this, vm.getRoom().getRoomID());
        TAPNotificationManager.getInstance().clearNotificationMessagesMap(vm.getRoom().getRoomID());
    }

    private void openRoomProfile() {
        TAPChatManager.getInstance().triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), vm.getOtherUserModel());
        hideUnreadButton();
    }

    private void openGroupMemberProfile(TAPUserModel groupMember) {
        TAPChatManager.getInstance().triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), groupMember);
        hideUnreadButton();
    }

    private void loadProfilePicture(String image, ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        glide.load(image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadInitialsToProfilePicture(imageView, tvAvatarLabel);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ImageViewCompat.setImageTintList(imageView, null);
                tvAvatarLabel.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void loadInitialsToProfilePicture(ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        if (tvAvatarLabel == tvMyAvatarLabelEmpty) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(this, TAPChatManager.getInstance().getActiveUser().getName())));
            tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(TAPChatManager.getInstance().getActiveUser().getName(), 2));
        } else {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(this, vm.getRoom().getRoomName())));
            tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(vm.getRoom().getRoomName(), vm.getRoom().getRoomType() == TYPE_PERSONAL ? 2 : 1));
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setVisibility(View.VISIBLE);
    }

    private void updateUnreadCount() {
        runOnUiThread(() -> {
            if (vm.isOnBottom() || vm.getUnreadCount() == 0) {
                tvBadgeUnread.setVisibility(View.INVISIBLE);
                if (View.INVISIBLE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.INVISIBLE);
                }
            } else if (vm.getUnreadCount() > 0) {
                tvBadgeUnread.setText(String.valueOf(vm.getUnreadCount()));
                tvBadgeUnread.setVisibility(View.VISIBLE);
                if (View.VISIBLE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.VISIBLE);
                }
            } else if (View.VISIBLE == ivToBottom.getVisibility()) {
                ivToBottom.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        if (rvMessageList.getItemDecorationCount() > 0) {
            rvMessageList.removeItemDecorationAt(0);
        }
        rvMessageList.addItemDecoration(new TAPVerticalDecoration(TAPUtils.getInstance().dpToPx(10), 0, messageAdapter.getItemCount() - 1));
    }

    private void buildAndSendTextMessage() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message.trim())) {
            etChat.setText("");
            messageAdapter.shrinkExpandedBubble();
            TAPChatManager.getInstance().sendTextMessage(message);
            rvMessageList.scrollToPosition(0);
        } else {
            TAPChatManager.getInstance().checkAndSendForwardedMessage(vm.getRoom());
            ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
        }
    }

    private void updateMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                    flMessageList.setVisibility(View.VISIBLE);
                }
            });
            // Replace pending message with new message
            String newID = newMessage.getLocalID();
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager
                    .getInstance().getActiveUser().getUserID());
            runOnUiThread(() -> {
                if (vm.getMessagePointer().containsKey(newID) &&
                        TYPE_IMAGE == newMessage.getType() &&
                        TAPChatManager.getInstance().getActiveUser().getUserID()
                                .equals(newMessage.getUser().getUserID())) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    vm.updateMessagePointer(newMessage);
                    TAPFileUploadManager.getInstance().removeUploadProgressMap(newMessage.getLocalID());
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                } else if (vm.getMessagePointer().containsKey(newID)) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    vm.updateMessagePointer(newMessage);
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                } else if (vm.isOnBottom() && !ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    vm.setScrollFromKeyboard(true);
                    messageAdapter.addMessage(newMessage);
                    rvMessageList.scrollToPosition(0);
                    vm.addMessagePointer(newMessage);
                } else if (ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    messageAdapter.addMessage(newMessage);
                    rvMessageList.scrollToPosition(0);
                    vm.addMessagePointer(newMessage);
                } else {
                    // Message from other people is received when recycler is scrolled up
                    messageAdapter.addMessage(newMessage);
                    vm.addUnreadMessage(newMessage);
                    vm.addMessagePointer(newMessage);
                    updateUnreadCount();
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();
        }
    }

    private void addNewMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                    flMessageList.setVisibility(View.VISIBLE);
                }
            });
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager
                    .getInstance().getActiveUser().getUserID());
            runOnUiThread(() -> {
                messageAdapter.addMessage(newMessage);
                vm.addMessagePointer(newMessage);
                if (vm.isOnBottom() || ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    rvMessageList.scrollToPosition(0);
                } else {
                    // Message from other people is received when recycler is scrolled up
                    vm.addUnreadMessage(newMessage);
                    updateUnreadCount();
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();
        }
    }

    private void updateMessageFromSocket(TAPMessageModel message) {
        runOnUiThread(() -> {
            int position = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
            if (-1 != position) {
                // Update message in pointer and adapter
                vm.updateMessagePointer(message);
                TAPMessageModel existingMessage = messageAdapter.getItemAt(position);
                if (null != existingMessage) {
                    existingMessage.updateValue(message);
                    messageAdapter.notifyItemChanged(position);
                }
            }
//            else {
//                new Thread(() -> updateMessage(message)).start();
//            }
            if (0 == position) {
                updateFirstVisibleMessageIndex();
            }
        });
    }

    private List<TAPMessageModel> addBeforeTextMessage(final TAPMessageModel newMessage) {
        List<TAPMessageModel> tempBeforeMessages = new ArrayList<>();
        String newID = newMessage.getLocalID();

        if (vm.getMessagePointer().containsKey(newID)) {
            // Update existing message
            vm.updateMessagePointer(newMessage);
            runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID))));
        } else {
            // Add new message to pointer
            tempBeforeMessages.add(newMessage);
            vm.addMessagePointer(newMessage);
        }
        //updateMessageDecoration();
        return tempBeforeMessages;
    }

    private void updateFirstVisibleMessageIndex() {
        new Thread(() -> {
            vm.setFirstVisibleItemIndex(0);
            TAPMessageModel message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            while (null != message && null != message.getHidden() && message.getHidden()) {
                vm.setFirstVisibleItemIndex(vm.getFirstVisibleItemIndex() + 1);
                message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            }
        }).start();
    }

    private void showQuoteLayout(@Nullable TAPMessageModel message, int quoteAction, boolean showKeyboard) {
        if (null == message) {
            return;
        }
        vm.setQuotedMessage(message, quoteAction);
        boolean quotedOwnMessage = null != TAPChatManager.getInstance().getActiveUser() &&
                TAPChatManager.getInstance().getActiveUser().getUserID().equals(message.getUser().getUserID());
        runOnUiThread(() -> {
            clQuote.setVisibility(View.VISIBLE);
            // Add other quotable message type here
            if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) && null != message.getData()) {
                // Show image quote
                vQuoteDecoration.setVisibility(View.GONE);
                // TODO: 29 January 2019 IMAGE MIGHT NOT EXIST IN CACHE
                Drawable drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(TAPUtils.getInstance().getUriKeyFromMessage(message));
                if (null != drawable) {
                    rcivQuoteImage.setImageDrawable(drawable);
                } else {
                    // Show small thumbnail
                    Drawable thumbnail = new BitmapDrawable(
                            getResources(),
                            TAPFileUtils.getInstance().decodeBase64(
                                    (String) (null == message.getData().get(THUMBNAIL) ? "" :
                                            message.getData().get(THUMBNAIL))));
                    rcivQuoteImage.setImageDrawable(thumbnail);
                }
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else if (message.getType() == TYPE_FILE && null != message.getData()) {
                // Show file quote
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_documents_white));
                rcivQuoteImage.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconFile));
                rcivQuoteImage.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_quote_layout_file));
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteTitle.setText(TAPUtils.getInstance().getFileDisplayName(message));
                tvQuoteContent.setText(TAPUtils.getInstance().getFileDisplayInfo(message));
                tvQuoteContent.setMaxLines(1);
            } else if (null != message.getData() && null != message.getData().get(FILE_URL)) {
                // Show image quote from file URL
                glide.load((String) message.getData().get(FILE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else if (null != message.getData() && null != message.getData().get(IMAGE_URL)) {
                // Show image quote from image URL
                glide.load((String) message.getData().get(IMAGE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show text quote
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(2);
            }
            boolean hadFocus = etChat.hasFocus();
            if (showKeyboard) {
                TAPUtils.getInstance().showKeyboard(this, etChat);
            }
            if (!hadFocus && etChat.getSelectionEnd() == 0) {
                etChat.setSelection(etChat.getText().length());
            }
        });
    }

    private void hideQuoteLayout() {
        vm.setQuotedMessage(null, 0);
        boolean hasFocus = etChat.hasFocus();
        if (clQuote.getVisibility() == View.VISIBLE) {
            runOnUiThread(() -> {
                clQuote.setVisibility(View.GONE);
                if (hasFocus) {
                    clQuote.post(() -> etChat.requestFocus());
                }
            });
        }
    }

    private void scrollToBottom() {
        rvMessageList.scrollToPosition(0);
        ivToBottom.setVisibility(View.INVISIBLE);
        vm.setOnBottom(true);
        vm.clearUnreadMessages();
        updateUnreadCount();
    }

    private void toggleCustomKeyboard() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            showNormalKeyboard();
        } else {
            showCustomKeyboard();
        }
    }

    private void showNormalKeyboard() {
        rvCustomKeyboard.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
        } else {
            ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
        }
        ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
        ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
        //etChat.requestFocus();
        TAPUtils.getInstance().showKeyboard(this, etChat);
    }

    private void showCustomKeyboard() {
        TAPUtils.getInstance().dismissKeyboard(this);
        etChat.clearFocus();
        new Handler().postDelayed(() -> {
            rvCustomKeyboard.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_show_keyboard_ripple));
            } else {
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_show_keyboard));
            }
            ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_keyboard_white));
            ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerShowKeyboard));
        }, 150L);
    }

    private void hideKeyboards() {
        rvCustomKeyboard.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
        } else {
            ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
        }
        ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
        ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
        TAPUtils.getInstance().dismissKeyboard(this);
    }

    private void openAttachMenu() {
        if (!etChat.hasFocus()) {
            etChat.requestFocus();
        }
        TAPUtils.getInstance().dismissKeyboard(this);
        TAPAttachmentBottomSheet attachBottomSheet = new TAPAttachmentBottomSheet(attachmentListener);
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void setChatRoomStatus(TAPOnlineStatusModel onlineStatus) {
        vm.setOnlineStatus(onlineStatus);
        if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && onlineStatus.getOnline()) {
            // User is online
            showUserOnline();
        } else if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && !onlineStatus.getOnline()) {
            // User is offline
            showUserOffline();
        }
    }

    private void showUserOnline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_active));
            tvRoomStatus.setText(getString(R.string.tap_active_now));
            vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            lastActivityRunnable.run();
        });
    }

    private void sendTypingEmit(boolean isTyping) {
        if (TAPConnectionManager.getInstance().getConnectionStatus() != CONNECTED) {
            return;
        }
        String currentRoomID = vm.getRoom().getRoomID();
        if (isTyping && !vm.isActiveUserTyping()) {
            TAPChatManager.getInstance().sendStartTypingEmit(currentRoomID);
            vm.setActiveUserTyping(true);
            sendTypingEmitDelayTimer.cancel();
            sendTypingEmitDelayTimer.start();
        } else if (!isTyping && vm.isActiveUserTyping()) {
            TAPChatManager.getInstance().sendStopTypingEmit(currentRoomID);
            vm.setActiveUserTyping(false);
            sendTypingEmitDelayTimer.cancel();
        }
    }

    private void showTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        typingIndicatorTimeoutTimer.start();
        runOnUiThread(() -> {
            clRoomTypingStatus.setVisibility(View.VISIBLE);
            clRoomOnlineStatus.setVisibility(View.GONE);

            if (TYPE_PERSONAL == vm.getRoom().getRoomType()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(getString(R.string.tap_typing));
            } else if (1 < vm.getGroupTypingSize()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_people_typing), vm.getGroupTypingSize()));
            } else {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                //tvRoomTypingStatus.setText(getString(R.string.tap_typing));
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_typing_single), vm.getFirstTypingUserName()));
            }
        });
    }

    private void hideTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        runOnUiThread(() -> {
            if (0 < vm.getGroupTypingSize()) {
                showTypingIndicator();
            } else {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveDraftToManager() {
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) {
            TAPChatManager.getInstance().saveMessageToDraft(draft);
        } else {
            TAPChatManager.getInstance().removeDraft();
        }
    }

    private void openMediaPreviewPage(ArrayList<TAPMediaPreviewModel> mediaPreviews) {
        Intent intent = new Intent(TapUIChatActivity.this, TAPMediaPreviewActivity.class);
        intent.putExtra(MEDIA_PREVIEWS, mediaPreviews);
        startActivityForResult(intent, SEND_MEDIA_FROM_PREVIEW);
    }

    private void fetchBeforeMessageFromAPIAndUpdateUI(TAPDefaultDataView<TAPGetMessageListByRoomResponse> beforeView) {
        new Thread(() -> {
            if (0 < vm.getMessageModels().size()) {
                // Use oldest message's create time as parameter
                TAPDataManager.getInstance().getMessageListByRoomBefore(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(), MAX_ITEMS_PER_PAGE,
                        beforeView);
            } else {
                // Use current timestamp as parameter if message list is empty
                TAPDataManager.getInstance().getMessageListByRoomBefore(vm.getRoom().getRoomID(),
                        System.currentTimeMillis(), MAX_ITEMS_PER_PAGE,
                        beforeView);
            }
        }).start();
    }

    private void callApiGetGroupData() {
        new Thread(() -> TAPDataManager.getInstance().getChatRoomData(vm.getRoom().getRoomID(), new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                vm.setRoom(response.getRoom());
                vm.getRoom().setAdmins(response.getAdmins());
                vm.getRoom().setGroupParticipants(response.getParticipants());
                TAPGroupManager.Companion.getGetInstance().addGroupData(vm.getRoom());

                if (null != vm.getRoom() && null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                    // Load room image
                    loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel);
                } else {
                    // Room image is empty
                    loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
                }

                tvRoomName.setText(vm.getRoom().getRoomName());

                if (null != vm.getRoom().getGroupParticipants()) {
                    tvRoomStatus.setText(String.format(getString(R.string.tap_group_member_count), vm.getRoom().getGroupParticipants().size()));
                }
            }
        })).start();
    }

    private void callApiGetUserByUserID() {
        new Thread(() -> {
            if (TAPChatManager.getInstance().isNeedToCalledUpdateRoomStatusAPI() &&
                    TAPNetworkStateManager.getInstance().hasNetworkConnection(this))
                TAPDataManager.getInstance().getUserByIdFromApi(vm.getOtherUserID(), new TAPDefaultDataView<TAPGetUserResponse>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        TAPUserModel userResponse = response.getUser();
                        TAPContactManager.getInstance().updateUserData(userResponse);
                        TAPOnlineStatusModel onlineStatus = TAPOnlineStatusModel.Builder(userResponse);
                        setChatRoomStatus(onlineStatus);
                        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(false);

                        if (null == vm.getOtherUserModel()) {
                            vm.setOtherUserModel(response.getUser());
                            initRoom();
                        }

                        if (!TAPDataManager.getInstance().isChatRoomContactActionDismissed(vm.getRoom().getRoomID()) &&
                                (null == vm.getOtherUserModel().getIsContact() || vm.getOtherUserModel().getIsContact() == 0)) {
                            clContactAction.setVisibility(View.VISIBLE);
                        } else {
                            clContactAction.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(TAPErrorModel error) {
                        if (null != error.getCode() && error.getCode().equals(String.valueOf(USER_NOT_FOUND))) {
                            showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
                        }
                    }
                });
            else if (null == vm.getOtherUserModel()) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            }
        }).start();
    }

    private void showChatAsHistory(String message) {
        if (null != clChatHistory) {
            runOnUiThread(() -> clChatHistory.setVisibility(View.VISIBLE));
        }
        if (null != tvChatHistoryContent) {
            runOnUiThread(() -> tvChatHistoryContent.setText(message));
        }
        if (null != clChatComposer) {
            runOnUiThread(() -> {
                TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
                rvCustomKeyboard.setVisibility(View.GONE);
                clChatComposer.setVisibility(View.INVISIBLE);
                etChat.clearFocus();
            });
        }

        if (null != vRoomImage) {
            vRoomImage.setClickable(false);
        }

        if (null != llButtonDeleteChat) {
            llButtonDeleteChat.setOnClickListener(llDeleteGroupClickListener);
        }
    }

    private void showDefaultChatEditText() {
        if (null != clChatHistory) {
            runOnUiThread(() -> clChatHistory.setVisibility(View.GONE));
        }

        if (null != clChatComposer) {
            clChatComposer.setVisibility(View.VISIBLE);
        }

        if (null != civRoomImage) {
            vRoomImage.setClickable(true);
        }
        hideKeyboards();
    }

    private void checkChatRoomLocked(TAPMessageModel message) {
        if (null != message && message.getRoom().isLocked()) {
            clChatComposer.setVisibility(View.GONE);
        } else {
            clChatComposer.setVisibility(View.VISIBLE);
        }
    }

    private void handleSystemMessageAction(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType() &&
                (ROOM_ADD_PARTICIPANT.equals(message.getAction()) ||
                        ROOM_REMOVE_PARTICIPANT.equals(message.getAction())) &&
                !TAPChatManager.getInstance().getActiveUser().getUserID().equals(message.getTarget().getTargetID())) {
            // Another member removed from group
            callApiGetGroupData();
        } else if ((ROOM_REMOVE_PARTICIPANT.equals(message.getAction()) &&
                null != message.getTarget() &&
                vm.getMyUserModel().getUserID().equals(message.getTarget().getTargetID())) ||
                (LEAVE_ROOM.equals(message.getAction()) &&
                        vm.getMyUserModel().getUserID().equals(message.getUser().getUserID()))) {
            // Active user removed from group
            showChatAsHistory(getString(R.string.tap_not_a_participant));
        } else if (ROOM_ADD_PARTICIPANT.equals(message.getAction())) {
            // New group participant added
            showDefaultChatEditText();
        } else if (DELETE_ROOM.equals(message.getAction())) {
            // Room deleted
            //TAPChatManager.getInstance().deleteMessageFromIncomingMessages(message.getLocalID());
            //showRoomIsUnavailableState();
            showChatAsHistory(getString(R.string.tap_group_unavailable));
        } else {
            updateRoomDetailFromSystemMessage(message);
        }
    }

    private void updateRoomDetailFromSystemMessage(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (UPDATE_ROOM.equals(message.getAction())) {
            // Update room details
            vm.getRoom().setRoomName(message.getRoom().getRoomName());
            vm.getRoom().setRoomImage(message.getRoom().getRoomImage());
            TAPGroupManager.Companion.getGetInstance().addGroupData(vm.getRoom());
            tvRoomName.setText(vm.getRoom().getRoomName());
            if (null != vm.getRoom().getRoomImage()) {
                civRoomImage.post(() -> loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel));
            }
        } else if (vm.getRoom().getRoomType() == TYPE_PERSONAL &&
                UPDATE_USER.equals(message.getAction()) &&
                message.getUser().getUserID().equals(vm.getOtherUserID())) {
            // Update user details
            vm.getRoom().setRoomName(message.getUser().getName());
            vm.getRoom().setRoomImage(message.getUser().getAvatarURL());
            vm.setOtherUserModel(message.getUser());
            tvRoomName.setText(vm.getOtherUserModel().getName());
            if (null != vm.getRoom().getRoomImage()) {
                civRoomImage.post(() -> loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomImage, tvRoomImageLabel));
            }
        }
    }

    private void callApiAfter() {
        if (!vm.isInitialAPICallFinished()) {
            showLoadingOlderMessagesIndicator();
        }
        new Thread(() -> {
            if (vm.getMessageModels().size() > 0 && !TAPDataManager.getInstance().checkKeyInLastMessageTimestamp(vm.getRoom().getRoomID())) {
                // Set oldest message's create time as minCreated and lastUpdated if last updated timestamp does not exist in preference
                TAPDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                // Set oldest message's create time as minCreated, last updated timestamp is obtained from preference
                TAPDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
    }

    private void restartFailedDownloads() {
        if (TAPFileDownloadManager.getInstance().hasFailedDownloads() &&
                TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
            // Notify chat bubbles with failed download
            for (String localID : TAPFileDownloadManager.getInstance().getFailedDownloads()) {
                if (vm.getMessagePointer().containsKey(localID)) {
                    runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))));
                }
            }
            //TAPFileDownloadManager.getInstance().clearFailedDownloads();
        }
    }

    private void mergeSort(List<TAPMessageModel> messages, int sortDirection) {
        int messageListSize = messages.size();

        if (messageListSize < 2) {
            return;
        }

        int leftListSize = messageListSize / 2;
        int rightListSize = messageListSize - leftListSize;
        List<TAPMessageModel> leftList = new ArrayList<>(leftListSize);
        List<TAPMessageModel> rightList = new ArrayList<>(rightListSize);

        for (int index = 0; index < leftListSize; index++)
            leftList.add(index, messages.get(index));

        for (int index = leftListSize; index < messageListSize; index++)
            rightList.add((index - leftListSize), messages.get(index));

        mergeSort(leftList, sortDirection);
        mergeSort(rightList, sortDirection);

        merge(messages, leftList, rightList, leftListSize, rightListSize, sortDirection);
    }

    private void merge(List<TAPMessageModel> messagesAll, List<TAPMessageModel> leftList, List<TAPMessageModel> rightList, int leftSize, int rightSize, int sortDirection) {
        int indexLeft = 0, indexRight = 0, indexCombine = 0;

        while (indexLeft < leftSize && indexRight < rightSize) {
            if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() < rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() >= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() > rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() <= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            }
        }

        while (indexLeft < leftSize) {
            messagesAll.set(indexCombine, leftList.get(indexLeft));
            indexLeft += 1;
            indexCombine += 1;
        }

        while (indexRight < rightSize) {
            messagesAll.set(indexCombine, rightList.get(indexRight));
            indexRight += 1;
            indexCombine += 1;
        }
    }

    /**
     * =========================================================================================== *
     * LISTENERS / DATA VIEWS
     * =========================================================================================== *
     */

    private TAPChatListener chatListener = new TAPChatListener() {
        @Override
        public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
            checkChatRoomLocked(message);
            handleSystemMessageAction(message);
            updateMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
            super.onReceiveMessageInOtherRoom(message);

            if (null != TAPChatManager.getInstance().getOpenRoom() &&
                    TAPChatManager.getInstance().getOpenRoom().equals(message.getRoom().getRoomID()))
                updateMessage(message);
        }

        @Override
        public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
            super.onUpdateMessageInOtherRoom(message);
        }

        @Override
        public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
            super.onDeleteMessageInOtherRoom(message);
        }

        @Override
        public void onSendMessage(TAPMessageModel message) {
            addNewMessage(message);
            hideQuoteLayout();
            hideUnreadButton();
        }

        @Override
        public void onReplyMessage(TAPMessageModel message) {
            if (null != vm.getRoom()) {
                showQuoteLayout(message, REPLY, true);
                TAPChatManager.getInstance().removeUserInfo(vm.getRoom().getRoomID());
            }
        }

        @Override
        public void onRetrySendMessage(TAPMessageModel message) {
            vm.delete(message.getLocalID());
            if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO || message.getType() == TYPE_FILE) && null != message.getData() &&
                    (null == message.getData().get(FILE_ID) || ((String) message.getData().get(FILE_ID)).isEmpty())) {
                // Re-upload image/video
                TAPChatManager.getInstance().retryUpload(TapUIChatActivity.this, message);
            } else {
                // Resend message
                TAPChatManager.getInstance().resendMessage(message);
            }
        }

        @Override
        public void onSendFailed(TAPMessageModel message) {
            vm.updateMessagePointer(message);
            vm.removeMessagePointer(message.getLocalID());
            runOnUiThread(() -> messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount()));
        }

        @Override
        public void onMessageRead(TAPMessageModel message) {
            if (vm.getUnreadCount() == 0) return;

            //message.setIsRead(true);
            vm.removeUnreadMessage(message.getLocalID());
            updateUnreadCount();
        }

        @Override
        public void onMessageQuoteClicked(TAPMessageModel message) {
            TAPChatManager.getInstance().triggerMessageQuoteTapped(TapUIChatActivity.this, message);
            if (null != message.getReplyTo() &&
                    null != message.getReplyTo().getLocalID() &&
                    !message.getReplyTo().getLocalID().isEmpty() &&
                    message.getReplyTo().getMessageType() != -1 && // FIXME: 25 October 2019 MESSAGE TYPE -1 IS USED FOR DUMMY MESSAGE IN CHAT MANAGER setQuotedMessage
                    (null == message.getForwardFrom() ||
                            null == message.getForwardFrom().getFullname() ||
                            message.getForwardFrom().getFullname().isEmpty())) {
                scrollToMessage(message.getReplyTo().getLocalID());
            }
        }

        @Override
        public void onGroupMemberAvatarClicked(TAPMessageModel message) {
            openGroupMemberProfile(message.getUser());
        }

        @Override
        public void onOutsideClicked() {
            hideKeyboards();
        }

        @Override
        public void onBubbleExpanded() {
            if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onLayoutLoaded(TAPMessageModel message) {
            if (/*message.getUser().getUserID().equals(vm.getMyUserModel().getUserID()) ||*/
                    messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
            setChatRoomStatus(onlineStatus);
        }

        @Override
        public void onReceiveStartTyping(TAPTypingModel typingModel) {
            if (typingModel.getRoomID().equals(vm.getRoom().getRoomID())) {
                vm.addGroupTyping(typingModel.getUser());
                showTypingIndicator();
            }
        }

        @Override
        public void onReceiveStopTyping(TAPTypingModel typingModel) {
            if (typingModel.getRoomID().equals(vm.getRoom().getRoomID())) {
                vm.removeGroupTyping(typingModel.getUser().getUserID());
                hideTypingIndicator();
            }
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }
            String localID;
            Uri fileUri;
            switch (action) {
                case UploadProgressLoading:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case UploadProgressFinish:
                    localID = intent.getStringExtra(UploadLocalID);
                    TAPMessageModel messageModel = vm.getMessagePointer().get(localID);
                    if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadImageData) &&
                            intent.getSerializableExtra(UploadImageData) instanceof HashMap) {
                        // Set image data
                        messageModel.setData((HashMap<String, Object>) intent.getSerializableExtra(UploadImageData));
                    } else if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadFileData) &&
                            intent.getSerializableExtra(UploadFileData) instanceof HashMap) {
                        // Put file data
                        messageModel.putData((HashMap<String, Object>) intent.getSerializableExtra(UploadFileData));
                    }
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(messageModel));
                    break;
                case UploadFailed:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel failedMessageModel = vm.getMessagePointer().get(localID);
                        failedMessageModel.setFailedSend(true);
                        failedMessageModel.setSending(false);
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(failedMessageModel));
                    }
                    break;
                case UploadCancelled:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel cancelledMessageModel = vm.getMessagePointer().get(localID);
                        vm.delete(localID);
                        int itemPos = messageAdapter.getItems().indexOf(cancelledMessageModel);

                        TAPFileUploadManager.getInstance().cancelUpload(TapUIChatActivity.this, cancelledMessageModel,
                                vm.getRoom().getRoomID());

                        vm.removeFromUploadingList(localID);
                        vm.removeMessagePointer(localID);
                        messageAdapter.removeMessageAt(itemPos);
                    }
                    break;
                case DownloadProgressLoading:
                case DownloadFinish:
                    localID = intent.getStringExtra(DownloadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFailed:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance().addFailedDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFile:
                    startFileDownload(intent.getParcelableExtra(MESSAGE));
                    break;
                case CancelDownload:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance().cancelFileDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case OpenFile:
                    TAPMessageModel message = intent.getParcelableExtra(MESSAGE);
                    fileUri = intent.getParcelableExtra(FILE_URI);
                    vm.setOpenedFileMessage(message);
                    if (null != fileUri && null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
                        if (!TAPUtils.getInstance().openFile(TapUIChatActivity.this, fileUri, (String) message.getData().get(MEDIA_TYPE))) {
                            showDownloadFileDialog();
                        }
                    } else {
                        showDownloadFileDialog();
                    }
                    break;
                case LongPressChatBubble:
                    if (null != intent.getParcelableExtra(MESSAGE) && intent.getParcelableExtra(MESSAGE) instanceof TAPMessageModel) {
                        TAPLongPressActionBottomSheet chatBubbleBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(CHAT_BUBBLE_TYPE, intent.getParcelableExtra(MESSAGE), attachmentListener);
                        chatBubbleBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressLink:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet linkBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(LINK_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        linkBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressEmail:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet emailBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(EMAIL_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        emailBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressPhone:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet phoneBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(PHONE_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        phoneBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
            }
        }
    };

    private void startFileDownload(TAPMessageModel message) {
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.setPendingDownloadMessage(message);
            ActivityCompat.requestPermissions(
                    TapUIChatActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE);
        } else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance().downloadMessageFile(message);
        }
    }

    private void showDownloadFileDialog() {
        // Prompt download if file does not exist
        if (null == vm.getOpenedFileMessage()) {
            return;
        }
        if (null != vm.getOpenedFileMessage().getData()) {
            String fileId = (String) vm.getOpenedFileMessage().getData().get(FILE_ID);
            String fileUrl = (String) vm.getOpenedFileMessage().getData().get(FILE_URL);
            if (null != fileUrl) {
                fileUrl = TAPUtils.getInstance().removeNonAlphaNumeric(fileUrl).toLowerCase();
            }
            TAPFileDownloadManager.getInstance().removeFileMessageUri(vm.getRoom().getRoomID(), fileId);
            TAPFileDownloadManager.getInstance().removeFileMessageUri(vm.getRoom().getRoomID(), fileUrl);
        }
        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getOpenedFileMessage()));
        new TapTalkDialog.Builder(TapUIChatActivity.this)
                .setTitle(getString(R.string.tap_error_could_not_find_file))
                .setMessage(getString(R.string.tap_error_redownload_file))
                .setCancelable(true)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setPrimaryButtonListener(v -> startFileDownload(vm.getOpenedFileMessage()))
                .show();
    }

    private void loadMoreMessagesFromDatabase() {
        if (state == STATE.LOADED && 0 < messageAdapter.getItems().size()) {
            new Thread(() -> {
                vm.getMessageByTimestamp(vm.getRoom().getRoomID(), dbListenerPaging, vm.getLastTimestamp());
                state = STATE.WORKING;
            }).start();
        }
    }

    private void scrollToMessage(String localID) {
        if (vm.getMessagePointer().containsKey(localID)) {
            vm.setTappedMessageLocalID(null);
            if (null != vm.getMessagePointer().get(localID).getIsDeleted() &&
                    vm.getMessagePointer().get(localID).getIsDeleted() &&
                    null != vm.getMessagePointer().get(localID).getHidden() &&
                    vm.getMessagePointer().get(localID).getHidden()) {
                // Message does not exist
                runOnUiThread(() -> {
                    Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                    hideUnreadButtonLoading();
                });
            } else {
                // Scroll to message
                runOnUiThread(() -> {
                    rvMessageList.scrollToPosition(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    rvMessageList.post(() -> {
                        if (messageLayoutManager.findFirstVisibleItemPosition() > 0) {
                            vm.setOnBottom(false);
                            ivToBottom.setVisibility(View.VISIBLE);
                            hideUnreadButton();
                            hideUnreadButtonLoading();
                        }
                    });
                });
            }
        } else if (state != STATE.DONE) {
            // Find quoted message in database/API
            vm.setTappedMessageLocalID(localID);
            showUnreadButtonLoading();
            loadMoreMessagesFromDatabase();
        } else {
            // Message not found
            runOnUiThread(() -> {
                Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                hideUnreadButtonLoading();
            });
        }
    }

    private TAPMessageModel insertUnreadMessageIdentifier(long created, TAPUserModel user) {
        TAPMessageModel unreadIndicator = new TAPMessageModel();
        unreadIndicator.setType(TYPE_UNREAD_MESSAGE_IDENTIFIER);
        unreadIndicator.setLocalID(UNREAD_INDICATOR_LOCAL_ID);
        unreadIndicator.setCreated(created - 1);
        unreadIndicator.setUser(user);

        vm.addMessagePointer(unreadIndicator);
        vm.setUnreadIndicator(unreadIndicator);

        return unreadIndicator;
    }

    private void getInitialUnreadCount() {
        // Get room's unread count
        vm.setInitialUnreadCount(vm.getRoom().getUnreadCount());
        if (0 == vm.getInitialUnreadCount()) {
            // Query unread count from database
            TAPDataManager.getInstance().getUnreadCountPerRoom(vm.getRoom().getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onCountedUnreadCount(String roomID, int unreadCount) {
                    if (!roomID.equals(vm.getRoom().getRoomID())) {
                        vm.setInitialUnreadCount(0);
                        hideUnreadButton();
                        return;
                    }
                    vm.setInitialUnreadCount(unreadCount);
                    if (vm.isUnreadButtonShown() && clUnreadButton.getVisibility() == View.GONE) {
                        vm.setUnreadButtonShown(false);
                        showUnreadButton(vm.getUnreadIndicator());
                    }
                }
            });
        }
    }

    private void showUnreadButton(@Nullable TAPMessageModel unreadIndicator) {
        if (0 >= vm.getInitialUnreadCount() || vm.isUnreadButtonShown()) {
            return;
        }
        vm.setUnreadButtonShown(true);
        rvMessageList.post(() -> runOnUiThread(() -> {
            if (vm.isAllUnreadMessagesHidden()) {
                // All unread messages are hidden
                return;
            }
            if (null != unreadIndicator) {
                View view = messageLayoutManager.findViewByPosition(messageAdapter.getItems().indexOf(unreadIndicator));
                if (null != view) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    if (location[1] < TAPUtils.getInstance().getScreenHeight()) {
                        // Do not show button if unread indicator is visible on screen
                        return;
                    }
                }
            }
            tvUnreadButtonCount.setText(String.format(getString(R.string.tap_s_unread_messages),
                    vm.getInitialUnreadCount() > 99 ? getString(R.string.tap_over_99) : vm.getInitialUnreadCount()));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_up_circle_orange));
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(v -> scrollToMessage(UNREAD_INDICATOR_LOCAL_ID));
        }));
    }

    private void hideUnreadButton() {
        if (null != ivUnreadButtonImage.getAnimation()) {
            return;
        }
        runOnUiThread(() -> {
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void showUnreadButtonLoading() {
        runOnUiThread(() -> {
            tvUnreadButtonCount.setText(getString(R.string.tap_loading));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivUnreadButtonImage.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(this, ivUnreadButtonImage);
            }
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void hideUnreadButtonLoading() {
        runOnUiThread(() -> {
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator();
        rvMessageList.post(() -> runOnUiThread(() -> {
            vm.addMessagePointer(vm.getLoadingIndicator(true));
            messageAdapter.addItem(vm.getLoadingIndicator(false)); // Add loading indicator to last index
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
        }));
    }

    private void hideLoadingOlderMessagesIndicator() {
        if (!messageAdapter.getItems().contains(vm.getLoadingIndicator(false))) {
            return;
        }
        vm.removeMessagePointer(LOADING_INDICATOR_LOCAL_ID);
        rvMessageList.post(() -> runOnUiThread(() -> {
            int index = messageAdapter.getItems().indexOf(vm.getLoadingIndicator(false));
            messageAdapter.removeMessage(vm.getLoadingIndicator(false));
            messageAdapter.notifyItemRemoved(index);
            updateMessageDecoration();
        }));
    }

    private RecyclerView.OnScrollListener messageListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // Show/hide ivToBottom
            if (messageLayoutManager.findFirstVisibleItemPosition() <= vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(true);
                ivToBottom.setVisibility(View.INVISIBLE);
                tvBadgeUnread.setVisibility(View.INVISIBLE);
                vm.clearUnreadMessages();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex() && !vm.isScrollFromKeyboard()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                hideUnreadButton();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                vm.setScrollFromKeyboard(false);
            }
        }
    };

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null != TapUIChatActivity.this.getCurrentFocus() && TapUIChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0 && s.toString().trim().length() > 0) {
                // Hide chat menu and enable send button when EditText is filled
                ivChatMenu.setVisibility(View.GONE);
                ivButtonChatMenu.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_ripple));
                } else {
                    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send));
                }
                ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
            } else if (null != TapUIChatActivity.this.getCurrentFocus() && TapUIChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0) {
                // Hide chat menu but keep send button disabled if trimmed text is empty
                ivChatMenu.setVisibility(View.GONE);
                ivButtonChatMenu.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_inactive_ripple));
                } else {
                    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
                }
                ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
                //} else if (s.length() > 0 && s.toString().trim().length() > 0) {
                //    if (vm.isCustomKeyboardEnabled()) {
                //        ivChatMenu.setVisibility(View.VISIBLE);
                //        ivButtonChatMenu.setVisibility(View.VISIBLE);
                //    }
                //    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, );(R.drawable.tap_bg_chat_composer_send_ripple));
                //    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
            } else {
                if (vm.isCustomKeyboardEnabled() && s.length() == 0) {
                    // Show chat menu if text is empty
                    ivChatMenu.setVisibility(View.VISIBLE);
                    ivButtonChatMenu.setVisibility(View.VISIBLE);
                } else {
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
                if (vm.getQuoteAction() == FORWARD) {
                    // Enable send button if message to forward exists
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_ripple));
                    } else {
                        ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send));
                    }
                    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
                } else {
                    // Disable send button
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_inactive_ripple));
                    } else {
                        ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
                    }
                    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            sendTypingEmit(s.length() > 0);
        }
    };

    private View.OnFocusChangeListener chatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && vm.isCustomKeyboardEnabled()) {
                vm.setScrollFromKeyboard(true);
                rvCustomKeyboard.setVisibility(View.GONE);
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_burger_menu_ripple));
                ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
                ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
                TAPUtils.getInstance().showKeyboard(TapUIChatActivity.this, etChat);

                if (0 < etChat.getText().toString().length()) {
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
            } else if (hasFocus) {
                vm.setScrollFromKeyboard(true);
                TAPUtils.getInstance().showKeyboard(TapUIChatActivity.this, etChat);
            }
//            else {
//                etChat.requestFocus();
//            }
        }
    };

    private LayoutTransition.TransitionListener containerTransitionListener = new LayoutTransition.TransitionListener() {
        @Override
        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            // Change animation state
            if (vm.getContainerAnimationState() != vm.PROCESSING) {
                vm.setContainerAnimationState(vm.ANIMATING);
            }
        }

        @Override
        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (vm.getContainerAnimationState() == vm.ANIMATING) {
                processPendingMessages();
            }
        }

        private void processPendingMessages() {
            vm.setContainerAnimationState(vm.PROCESSING);
            if (vm.getPendingRecyclerMessages().size() > 0) {
                // Copy list to prevent concurrent exception
                List<TAPMessageModel> pendingMessages = new ArrayList<>(vm.getPendingRecyclerMessages());
                for (TAPMessageModel pendingMessage : pendingMessages) {
                    // Loop the copied list to add messages
                    if (vm.getContainerAnimationState() != vm.PROCESSING) {
                        return;
                    }
                    updateMessage(pendingMessage);
                }
                // Remove added messages from pending message list
                vm.getPendingRecyclerMessages().removeAll(pendingMessages);
                if (vm.getPendingRecyclerMessages().size() > 0) {
                    // Redo process if pending message is not empty
                    processPendingMessages();
                    return;
                }
            }
            // Change state to idle when processing finished
            vm.setContainerAnimationState(vm.IDLE);
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            final List<TAPMessageModel> models = new ArrayList<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                models.add(model);
                vm.addMessagePointer(model);
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
//                vm.setLastTimestamp(models.get(0).getCreated());
            }

            if (vm.getMessagePointer().containsKey(vm.getLastUnreadMessageLocalID()) && !vm.isAllUnreadMessagesHidden()) {
                TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                        vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID()).getCreated(),
                        vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID()).getUser());
                models.add(models.indexOf(vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID())) + 1, unreadIndicator);
            }

            if (null != messageAdapter && 0 == messageAdapter.getItems().size()) {
                runOnUiThread(() -> {
                    // First load
                    messageAdapter.setMessages(models);
                    if (models.size() == 0) {
                        // Chat is empty
                        // TODO: 24 September 2018 CHECK ROOM TYPE
                        clEmptyChat.setVisibility(View.VISIBLE);

                        // Load my avatar
                        if (null != vm.getMyUserModel().getAvatarURL() && !vm.getMyUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getMyUserModel().getAvatarURL().getThumbnail(), civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                        } else {
                            loadInitialsToProfilePicture(civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                        }

                        // Load room avatar
                        if (null != vm.getRoom() &&
                                TYPE_PERSONAL == vm.getRoom().getRoomType() &&
                                null != vm.getOtherUserModel() &&
                                null != vm.getOtherUserModel().getAvatarURL().getThumbnail() &&
                                !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        } else if (null != vm.getRoom() &&
                                null != vm.getRoom().getRoomImage() &&
                                !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        } else {
                            loadInitialsToProfilePicture(civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        }
                        if (vm.isCustomKeyboardEnabled() && 0 == etChat.getText().toString().trim().length()) {
                            showCustomKeyboard();
                        }
                    } else {
                        // Message exists
                        vm.setMessageModels(models);
                        state = STATE.LOADED;
                        if (clEmptyChat.getVisibility() == View.VISIBLE) {
                            clEmptyChat.setVisibility(View.GONE);
                        }
                        flMessageList.setVisibility(View.VISIBLE);
                        showUnreadButton(vm.getUnreadIndicator());
                        checkChatRoomLocked(models.get(0));
                    }
                    rvMessageList.scrollToPosition(0);
                    updateMessageDecoration();

                    if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size()) {
                        // Only Fetch newer messages from API if message is below 50
                        callApiAfter();
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    } else if (MAX_ITEMS_PER_PAGE <= vm.getMessageModels().size()) {
                        // Fetch newer messages from API and add pagination listener if message is over 50
                        rvMessageList.addOnScrollListener(endlessScrollListener);
                        callApiAfter();
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    } else {
                        // Fetch older messages from API if room has no message
                        fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                    }
                });
            } else if (null != messageAdapter) {
                runOnUiThread(() -> {
                    if (clEmptyChat.getVisibility() == View.VISIBLE) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                    flMessageList.setVisibility(View.VISIBLE);
                    messageAdapter.setMessages(models);
                    new Thread(() -> {
                        vm.setMessageModels(messageAdapter.getItems());
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    }).start();
                    if (rvMessageList.getVisibility() != View.VISIBLE) {
                        rvMessageList.setVisibility(View.VISIBLE);
                    }
                    if (state == STATE.DONE) {
                        updateMessageDecoration();
                    }
                    if (!models.isEmpty()) {
                        checkChatRoomLocked(models.get(0));
                    }
                });
                if (MAX_ITEMS_PER_PAGE > entities.size() && 1 < entities.size()) {
                    state = STATE.DONE;
                } else {
                    rvMessageList.addOnScrollListener(endlessScrollListener);
                    state = STATE.LOADED;
                }
            }
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbListenerPaging = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            final List<TAPMessageModel> models = new ArrayList<>();
            for (TAPMessageEntity entity : entities) {
                if (!vm.getMessagePointer().containsKey(entity.getLocalID())) {
                    TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                    models.add(model);
                    vm.addMessagePointer(model);
                }
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
            }

            if (null != messageAdapter) {
                if (MAX_ITEMS_PER_PAGE > entities.size() && STATE.DONE != state) {
                    if (0 == entities.size()) {
                        showLoadingOlderMessagesIndicator();
                    } else {
                        vm.setNeedToShowLoading(true);
                    }
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeViewPaging);
                } else if (STATE.WORKING == state) {
                    state = STATE.LOADED;
                }

                runOnUiThread(() -> {
                    //flMessageList.setVisibility(View.VISIBLE);
                    messageAdapter.addMessage(models);

                    if (vm.isNeedToShowLoading()) {
                        // Show loading if Before API is called
                        vm.setNeedToShowLoading(false);
                        showLoadingOlderMessagesIndicator();
                    }

                    // Insert unread indicator
                    if (vm.getMessagePointer().containsKey(vm.getLastUnreadMessageLocalID()) &&
                            null == vm.getUnreadIndicator() && !vm.isAllUnreadMessagesHidden()) {
                        TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                                vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID()).getCreated(),
                                vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID()).getUser());
                        messageAdapter.getItems().add(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID())) + 1, unreadIndicator);
                    }

                    new Thread(() -> {
                        vm.setMessageModels(messageAdapter.getItems());
                        showUnreadButton(vm.getUnreadIndicator());
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    }).start();

                    if (rvMessageList.getVisibility() != View.VISIBLE) {
                        rvMessageList.setVisibility(View.VISIBLE);
                    }
                    if (state == STATE.DONE) {
                        updateMessageDecoration();
                    }
                });
            }
        }
    };

    private TAPAttachmentListener attachmentListener = new TAPAttachmentListener() {
        @Override
        public void onCameraSelected() {
            if (TAPConnectionManager.getInstance().getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            if (TAPConnectionManager.getInstance().getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            TAPUtils.getInstance().pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
        }

        @Override
        public void onLocationSelected() {
            TAPUtils.getInstance().openLocationPicker(TapUIChatActivity.this);
        }

        @Override
        public void onDocumentSelected() {
            TAPUtils.getInstance().openDocumentPicker(TapUIChatActivity.this);
        }

        @Override
        public void onCopySelected(String text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }

        @Override
        public void onReplySelected(TAPMessageModel message) {
            chatListener.onReplyMessage(message);
        }

        @Override
        public void onForwardSelected(TAPMessageModel message) {
            Intent intent = new Intent(TapUIChatActivity.this, TAPForwardPickerActivity.class);
            intent.putExtra(MESSAGE, message);
            startActivityForResult(intent, FORWARD_MESSAGE);
            overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }

        @Override
        public void onOpenLinkSelected(String url) {
            TAPUtils.getInstance().openCustomTabLayout(TapUIChatActivity.this, url);
        }

        @Override
        public void onComposeSelected(String emailRecipient) {
            TAPUtils.getInstance().composeEmail(TapUIChatActivity.this, emailRecipient);
        }

        @Override
        public void onPhoneCallSelected(String phoneNumber) {
            TAPUtils.getInstance().openDialNumber(TapUIChatActivity.this, phoneNumber);
        }

        @Override
        public void onPhoneSmsSelected(String phoneNumber) {
            TAPUtils.getInstance().composeSMS(TapUIChatActivity.this, phoneNumber);
        }

        @Override
        public void onSaveImageToGallery(TAPMessageModel message) {
            if (!TAPUtils.getInstance().hasPermissions(TapUIChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE);
            } else if (null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
                new Thread(() -> {
                    vm.setPendingDownloadMessage(null);
                    Bitmap bitmap = null;
                    if (null != message.getData().get(FILE_ID)) {
                        // Get bitmap from cache
                        bitmap = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable((String) message.getData().get(FILE_ID)).getBitmap();
                    } else if (null != message.getData().get(FILE_URL)) {
                        // Get bitmap from URL
                        try {
                            URL imageUrl = new URL((String) message.getData().get(FILE_URL));
                            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (null != bitmap) {
                        TAPFileDownloadManager.getInstance().writeImageFileToDisk(TapUIChatActivity.this,
                                System.currentTimeMillis(), bitmap,
                                (String) message.getData().get(MEDIA_TYPE), new TapTalkActionInterface() {
                                    @Override
                                    public void onSuccess(String message) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                                    }
                                });
                    }
                }).start();
            }
        }

        @Override
        public void onSaveVideoToGallery(TAPMessageModel message) {
            if (!TAPUtils.getInstance().hasPermissions(TapUIChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
            } else if (null != message.getData() &&
                    null != message.getData().get(FILE_ID) &&
                    null != message.getData().get(MEDIA_TYPE) &&
                    null != message.getData().get(FILE_ID)) {
                vm.setPendingDownloadMessage(null);
                TAPFileDownloadManager.getInstance().writeFileToDisk(TapUIChatActivity.this, message, new TapTalkActionInterface() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            }
            // TODO: 6 Dec 2019 HANDLE FILE URL
        }

        @Override
        public void onSaveToDownloads(TAPMessageModel message) {
            if (null != message.getData() &&
                    null != message.getData().get(FILE_ID) &&
                    null != message.getData().get(MEDIA_TYPE) &&
                    null != message.getData().get(FILE_ID)) {
                TAPFileDownloadManager.getInstance().writeFileToDisk(TapUIChatActivity.this, message, new TapTalkActionInterface() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            }
            // TODO: 6 Dec 2019 HANDLE FILE URL
        }
    };

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageAfterView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            List<TAPMessageEntity> responseMessages = new ArrayList<>(); // Entities to be saved to database
            List<TAPMessageModel> messageAfterModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            List<String> unreadMessageIds = new ArrayList<>(); // Results to be marked as read
            TAPMessageModel updateRoomDetailSystemMessage = null;

            int unreadMessageIndex = -1; // Index for unread message identifier
            long smallestUnreadCreated = 0L;

            vm.setAllUnreadMessagesHidden(false); // Set initial value for unread identifier/button flag
            int allUnreadsHidden = -1; // Flag to check hidden unread when looping

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    String newID = message.getLocalID();
                    if (vm.getMessagePointer().containsKey(newID)) {
                        // Update existing message
                        runOnUiThread(() -> {
                            vm.updateMessagePointer(message);
                            messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                        });
                    } else if (!vm.getMessagePointer().containsKey(newID)) {
                        // Insert new message to list and HashMap
                        messageAfterModels.add(message);
                        TAPMessageModel messageFromPointer = vm.getMessagePointer().get(message.getLocalID());
                        if (null != messageFromPointer) {
                            messageFromPointer = messageFromPointer.copyMessageModel();
                        }
                        vm.addMessagePointer(message);
                        if ((null == message.getIsRead() || !message.getIsRead()) &&
                                (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
                                !TAPMessageStatusManager.getInstance().getReadMessageQueue().contains(message.getMessageID()) &&
                                !TAPMessageStatusManager.getInstance().getMessagesMarkedAsRead().contains(message.getMessageID())) {
                            // Add message ID to pending list if new message has not been read or not in mark read queue
                            unreadMessageIds.add(message.getMessageID());
                        }

                        if ("".equals(vm.getLastUnreadMessageLocalID())
                                && (smallestUnreadCreated > message.getCreated() || 0L == smallestUnreadCreated)
                                && (null != message.getIsRead() && !message.getIsRead())
                                && null == vm.getUnreadIndicator()) {
                            unreadMessageIndex = messageAfterModels.indexOf(message);
                            smallestUnreadCreated = message.getCreated();
                            if (null != message.getHidden() && !message.getHidden()) {
                                allUnreadsHidden = 0;
                            } else if (null != message.getHidden() && message.getHidden() && allUnreadsHidden != 0) {
                                allUnreadsHidden = 1;
                            }
                        }

                        if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                                null != message.getAction() &&
                                (message.getAction().equals(UPDATE_ROOM) ||
                                        message.getAction().equals(UPDATE_USER)) &&
                                (null == updateRoomDetailSystemMessage ||
                                        updateRoomDetailSystemMessage.getCreated() < message.getCreated())) {
                            // Store update room system message
                            updateRoomDetailSystemMessage = message;
                        }
                    }

                    responseMessages.add(TAPChatManager.getInstance().convertToEntity(message));
                    new Thread(() -> {
                        // Update last updated timestamp in preference (new thread to prevent stutter when scrolling)
                        if (null != message.getUpdated() &&
                                TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < message.getUpdated()) {
                            TAPDataManager.getInstance().saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), message.getUpdated());
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != updateRoomDetailSystemMessage) {
                // Update room detail if update room system message exists in API result
                updateRoomDetailFromSystemMessage(updateRoomDetailSystemMessage);
            }

            if (!vm.isInitialAPICallFinished()) {
                // Add unread messages to count
                vm.setInitialUnreadCount(vm.getInitialUnreadCount() + unreadMessageIds.size());
            }

            if (allUnreadsHidden == 1) {
                // All unread messages are hidden
                vm.setAllUnreadMessagesHidden(true);
            }
            if (-1 != unreadMessageIndex && 0L != smallestUnreadCreated && !vm.isAllUnreadMessagesHidden()) {
                // Insert unread indicator
                TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                        messageAfterModels.get(unreadMessageIndex).getCreated(),
                        messageAfterModels.get(unreadMessageIndex).getUser());
                messageAfterModels.add(unreadMessageIndex, unreadIndicator);
            }

            if (0 < messageAfterModels.size()) {
                // Update new message status to delivered
                TAPMessageStatusManager.getInstance().updateMessageStatusToDelivered(messageAfterModels);
            }

            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);
                updateMessageDecoration();
                // Insert new messages to first index
                messageAdapter.addMessage(0, messageAfterModels, false);
                // Sort adapter items according to timestamp
                mergeSort(messageAdapter.getItems(), ASCENDING);
                showUnreadButton(vm.getUnreadIndicator());

                if (vm.isOnBottom() && 0 < messageAfterModels.size()) {
                    // Scroll recycler to bottom
                    rvMessageList.scrollToPosition(0);
                }
                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                if (state == STATE.DONE) {
                    updateMessageDecoration();
                }
                if (!messageAfterModels.isEmpty()) {
                    checkChatRoomLocked(messageAfterModels.get(0));
                }
            });

            if (0 < responseMessages.size()) {
                // Save entities to database
                TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
                });
            }

            if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size() && !vm.isInitialAPICallFinished()) {
                // Fetch older messages on first call
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
            } else {
                hideLoadingOlderMessagesIndicator();
            }

            if (!vm.isInitialAPICallFinished()) {
                vm.setInitialAPICallFinished(true);
                setAllUnreadMessageToRead(unreadMessageIds);
            }
            checkIfChatIsAvailableAndUpdateUI();
            updateFirstVisibleMessageIndex();
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            checkIfChatIsAvailableAndUpdateUI();
            if (0 < vm.getMessageModels().size()) {
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
            }
        }

        private void checkIfChatIsAvailableAndUpdateUI() {
            if (messageAdapter.getItems().isEmpty()) {
                return;
            }
            TAPMessageModel lastMessage = messageAdapter.getItems().get(0);
            if ((ROOM_REMOVE_PARTICIPANT.equals(lastMessage.getAction()) &&
                    null != lastMessage.getTarget() &&
                    vm.getMyUserModel().getUserID().equals(lastMessage.getTarget().getTargetID())) ||
                    (LEAVE_ROOM.equals(lastMessage.getAction()) &&
                            vm.getMyUserModel().getUserID().equals(lastMessage.getUser().getUserID()))) {
                // User has been removed / left from group
                showChatAsHistory(getString(R.string.tap_not_a_participant));
            } else if (DELETE_ROOM.equals(lastMessage.getAction())) {
                // Room was deleted
                //showRoomIsUnavailableState();
                showChatAsHistory(getString(R.string.tap_group_unavailable));
            }
        }
    };

    private void setAllUnreadMessageToRead(List<String> unreadMessageIds) {
        new Thread(() -> {
            // Clear unread badge from room list
            Intent intent = new Intent(CLEAR_ROOM_LIST_BADGE);
            intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

            // Mark filtered API result messages as read
            markMessageAsRead(unreadMessageIds);

            // Mark messages from database as read
            TAPDataManager.getInstance().getAllUnreadMessagesFromRoom(TAPChatManager.getInstance().getOpenRoom(),
                    new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            List<String> pendingReadList = new ArrayList<>();
                            for (TAPMessageEntity entity : entities) {
                                TAPMessageModel messageFromPointer = vm.getMessagePointer().get(entity.getLocalID());
                                if (null != messageFromPointer) {
                                    messageFromPointer = messageFromPointer.copyMessageModel();
                                }
                                if (!unreadMessageIds.contains(entity.getMessageID()) &&
                                        (null == entity.getIsRead() || !entity.getIsRead()) &&
                                        (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
                                        !TAPMessageStatusManager.getInstance().getReadMessageQueue().contains(entity.getMessageID()) &&
                                        !TAPMessageStatusManager.getInstance().getMessagesMarkedAsRead().contains(entity.getMessageID())) {
                                    // Add message ID to pending list if new message has not been read or not in mark read queue
                                    pendingReadList.add(entity.getMessageID());
                                }
                            }
                            markMessageAsRead(pendingReadList);
                        }
                    });
        }).start();
    }

    private void getAllUnreadMessage() {
        TAPDataManager.getInstance().getAllUnreadMessagesFromRoom(TAPChatManager.getInstance().getOpenRoom(),
                new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        if (0 < entities.size()) {
                            vm.setLastUnreadMessageLocalID(entities.get(0).getLocalID());
                            new Thread(() -> {
                                int allUnreadsHidden = -1; // Flag to check hidden unread when looping
                                for (TAPMessageEntity entity : entities) {
                                    if (null != entity.getHidden() && !entity.getHidden()) {
                                        allUnreadsHidden = 0;
                                    } else if (null != entity.getHidden() && entity.getHidden() && allUnreadsHidden != 0) {
                                        allUnreadsHidden = 1;
                                    }
                                }
                                if (allUnreadsHidden == 1) {
                                    vm.setAllUnreadMessagesHidden(true);
                                }
                            }).start();
                        }
                        vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
                    }
                });
    }

//    private void showRoomIsUnavailableState() {
//        new DeleteRoomAsync().execute(vm.getRoom().getRoomID());
//        runOnUiThread(() -> {
//            tvMessage.setText(getResources().getString(R.string.tap_group_unavailable));
//            flRoomUnavailable.setVisibility(View.VISIBLE);
//            flMessageList.setVisibility(View.GONE);
//            clEmptyChat.setVisibility(View.GONE);
//            flChatComposerAndHistory.setVisibility(View.GONE);
//            if (null != vRoomImage) {
//                vRoomImage.setClickable(false);
//            }
//        });
//    }

//    private void markMessageAsRead(TAPMessageModel readMessage) {
//        new Thread(() -> {
//            if (null != readMessage.getIsRead() && !readMessage.getIsRead()) {
//                TAPMessageStatusManager.getInstance().addUnreadListByOne(readMessage.getRoom().getRoomID());
//                TAPMessageStatusManager.getInstance().addReadMessageQueue(readMessage);
//            }
//        }).start();
//    }

    private void markMessageAsRead(List<String> readMessageIds) {
        if (null == readMessageIds || readMessageIds.isEmpty()) {
            return;
        }
        new Thread(() -> {
            //TAPMessageStatusManager.getInstance().addUnreadList(vm.getRoom().getRoomID(), readMessageIds.size());
            TAPMessageStatusManager.getInstance().addReadMessageQueue(readMessageIds);
        }).start();
    }

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            List<TAPMessageEntity> responseMessages = new ArrayList<>();  // Entities to be saved to database
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    messageBeforeModels.addAll(addBeforeTextMessage(message));
                    responseMessages.add(TAPChatManager.getInstance().convertToEntity(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Sort adapter items according to timestamp
            mergeSort(messageBeforeModels, ASCENDING);

            List<TAPMessageModel> finalMessageBeforeModels = messageBeforeModels;
            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE && 0 < finalMessageBeforeModels.size()) {
                    clEmptyChat.setVisibility(View.GONE);
                }

                hideLoadingOlderMessagesIndicator();

                if (!(0 < messageAdapter.getItems().size() && (ROOM_REMOVE_PARTICIPANT.equals(messageAdapter.getItems().get(0).getAction())
                        && TAPChatManager.getInstance().getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getTarget().getTargetID()))
                        || (0 < messageAdapter.getItems().size() && DELETE_ROOM.equals(messageAdapter.getItems().get(0).getAction()))
                        || (0 < messageAdapter.getItems().size() && LEAVE_ROOM.equals(messageAdapter.getItems().get(0).getAction()) &&
                        TAPChatManager.getInstance().getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getUser().getUserID())))) {
                    flMessageList.setVisibility(View.VISIBLE);
                }

                // Add messages to last index
                messageAdapter.addOlderMessagesFromApi(finalMessageBeforeModels);

                if (0 < finalMessageBeforeModels.size())
                    vm.setLastTimestamp(finalMessageBeforeModels.get(finalMessageBeforeModels.size() - 1).getCreated());

                updateMessageDecoration();
                new Thread(() -> {
                    vm.setMessageModels(messageAdapter.getItems());
                    if (null != vm.getTappedMessageLocalID()) {
                        scrollToMessage(vm.getTappedMessageLocalID());
                    }
                }).start();

                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                if (state == STATE.DONE) {
                    updateMessageDecoration();
                }
            });

            TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
            if (MAX_ITEMS_PER_PAGE > response.getMessages().size() && 1 < response.getMessages().size()) {
                state = STATE.DONE;
            } else {
                rvMessageList.addOnScrollListener(endlessScrollListener);
                state = STATE.LOADED;
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
        }
    };

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeViewPaging = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            hideLoadingOlderMessagesIndicator();
            List<TAPMessageEntity> responseMessages = new ArrayList<>(); // Entities to be saved to database
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    messageBeforeModels.addAll(addBeforeTextMessage(message));
                    responseMessages.add(TAPChatManager.getInstance().convertToEntity(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Check if room has more messages
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;

            // Sort adapter items according to timestamp
            mergeSort(messageBeforeModels, ASCENDING);
            runOnUiThread(() -> {
                // Add messages to last index
                messageAdapter.addMessage(messageBeforeModels);

                if (0 < messageBeforeModels.size())
                    vm.setLastTimestamp(messageBeforeModels.get(messageBeforeModels.size() - 1).getCreated());

                new Thread(() -> {
                    vm.setMessageModels(messageAdapter.getItems());
                    if (null != vm.getTappedMessageLocalID()) {
                        scrollToMessage(vm.getTappedMessageLocalID());
                    }
                }).start();

                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                updateMessageDecoration();
            });

            TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(Throwable throwable) {
            hideLoadingOlderMessagesIndicator();
        }
    };

    private TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void onSuccess(TAPAddContactResponse response) {
            TAPUserModel newContact = response.getUser().setUserAsContact();
            TAPContactManager.getInstance().updateUserData(newContact);
        }
    };

    private Runnable lastActivityRunnable = new Runnable() {
        final int INTERVAL = 1000 * 60;

        @Override
        public void run() {
            Long lastActive = vm.getOnlineStatus().getLastActive();
            if (lastActive == 0) {
                runOnUiThread(() -> {
                    vStatusBadge.setVisibility(View.VISIBLE);
                    vStatusBadge.setBackground(null);
                    tvRoomStatus.setText("");
                });
            } else {
                runOnUiThread(() -> {
                    //vStatusBadge.setBackground(getDrawable(R.drawable.tap_bg_circle_butterscotch));
                    vStatusBadge.setVisibility(View.GONE);
                    tvRoomStatus.setText(TAPTimeFormatter.getInstance().getLastActivityString(TapUIChatActivity.this, lastActive));
                });
            }
            vm.getLastActivityHandler().postDelayed(this, INTERVAL);
        }
    };

    private CountDownTimer sendTypingEmitDelayTimer = new CountDownTimer(TYPING_EMIT_DELAY, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            vm.setActiveUserTyping(false);
        }
    };

    private CountDownTimer typingIndicatorTimeoutTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            hideTypingIndicator();
        }
    };

    private View.OnClickListener llDeleteGroupClickListener = v -> TAPOldDataManager.getInstance().cleanRoomPhysicalData(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
        @Override
        public void onDeleteFinished() {
            super.onDeleteFinished();
            TAPDataManager.getInstance().deleteMessageByRoomId(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
                @Override
                public void onDeleteFinished() {
                    super.onDeleteFinished();
                    deleteGroup = true;
                    onBackPressed();
                }
            });
        }
    });

    private static class DeleteRoomAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... roomIDs) {
            TAPOldDataManager.getInstance().cleanRoomPhysicalData(roomIDs[0], new TAPDatabaseListener() {
                @Override
                public void onDeleteFinished() {
                    super.onDeleteFinished();
                    TAPDataManager.getInstance().deleteMessageByRoomId(roomIDs[0], new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            super.onDeleteFinished();
                            if (!TAPGroupManager.Companion.getGetInstance().getRefreshRoomList())
                                TAPGroupManager.Companion.getGetInstance().setRefreshRoomList(true);
                        }
                    });
                }
            });
            return null;
        }
    }

    // Interface for swipe back
    public interface SwipeBackInterface {
        void onSwipeBack();

        void onSwipeToFinishActivity();
    }

    private SwipeBackInterface swipeInterface = new SwipeBackInterface() {
        @Override
        public void onSwipeBack() {
            TAPUtils.getInstance().dismissKeyboard(TapUIChatActivity.this);
        }

        @Override
        public void onSwipeToFinishActivity() {
            if (isTaskRoot()) {
                // Trigger listener callback if no other activity is open
                for (TapListener listener : TapTalk.getTapTalkListeners()) {
                    listener.onTaskRootChatRoomClosed(TapUIChatActivity.this);
                }
            }
        }
    };
}
