package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackLayout;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.View.Fragment.TAPConnectionStatusFragment;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;
import io.taptalk.Taptalk.BuildConfig;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_TYPING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_RES_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_URLS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.NUM_OF_ITEM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE_TO_DISK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_PREVIEW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.DESCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_EMIT_DELAY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadRetried;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;

public class TAPChatActivity extends TAPBaseChatActivity {

    private String TAG = TAPChatActivity.class.getSimpleName();

    //interface for swipe back
    public interface SwipeBackInterface {
        void onSwipeBack();
    }

    private SwipeBackInterface swipeInterface = () -> TAPUtils.getInstance().dismissKeyboard(TAPChatActivity.this);

    // View
    private SwipeBackLayout sblChat;
    private TAPChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private FrameLayout flMessageList;
    private ConstraintLayout clContainer, clEmptyChat, clQuote, clChatComposer, clRoomOnlineStatus, clRoomTypingStatus;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivButtonCancelReply, ivButtonChatMenu, ivButtonAttach,
            ivButtonSend, ivToBottom, ivRoomTypingIndicator;
    private CircleImageView civRoomImage, civMyAvatar, civOtherUserAvatar;
    private TAPRoundedCornerImageView rcivQuoteImage;
    private TextView tvRoomName, tvRoomStatus, tvChatEmptyGuide, tvProfileDescription, tvQuoteTitle,
            tvQuoteContent, tvBadgeUnread, tvRoomTypingStatus;
    private View vStatusBadge, vQuoteDecoration;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAPChatManager.getInstance().updateUnreadCountInRoomList(TAPChatManager.getInstance().getOpenRoom());
        TAPChatManager.getInstance().setOpenRoom(null); // Reset open room
        TAPChatManager.getInstance().removeChatListener(chatListener);
        vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable); // Stop offline timer
        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(true);
        TAPBroadcastManager.unregister(this, broadcastReceiver);
        TAPFileDownloadManager.getInstance().clearFailedDownloads(); // Remove failed download list from active room
    }

    @Override
    protected void onResume() {
        super.onResume();
        TAPChatManager.getInstance().setActiveRoom(vm.getRoom());
        etChat.setText(TAPChatManager.getInstance().getMessageFromDraft());
        showQuoteLayout(TAPChatManager.getInstance().getQuotedMessage(), false);
        callApiGetUserByUserID();
        if (vm.isInitialAPICallFinished() && TAPConnectionManager.getInstance().getConnectionStatus() == CONNECTED) {
            callApiAfter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraftToManager();
        sendTypingEmit(false);
        TAPChatManager.getInstance().deleteActiveRoom();
    }

    @Override
    public void onBackPressed() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            hideKeyboards();
        } else {
            //TAPNotificationManager.getInstance().updateUnreadCount();
            new Thread(() -> TAPChatManager.getInstance().putUnsentMessageToList()).start();
            super.onBackPressed();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
            case RESULT_OK:
                // Set active room to prevent null pointer when returning to chat
                TAPChatManager.getInstance().setActiveRoom(vm.getRoom());
                switch (requestCode) {
                    case SEND_IMAGE_FROM_CAMERA:
                        if (null == vm.getCameraImageUri()) {
                            return;
                        }
                        ArrayList<TAPImagePreviewModel> imageCameraUris = new ArrayList<>();
                        imageCameraUris.add(TAPImagePreviewModel.Builder(vm.getCameraImageUri(), true));
                        openImagePreviewPage(imageCameraUris);
                        break;
                    case SEND_IMAGE_FROM_GALLERY:
                        if (null == intent) {
                            return;
                        }
                        ArrayList<TAPImagePreviewModel> imageGalleryUris = new ArrayList<>();
                        ClipData clipData = intent.getClipData();
                        if (null != clipData) {
                            //ini buat lebih dari 1 image selection
                            TAPUtils.getInstance().getUrisFromClipData(clipData, imageGalleryUris, true);
                        } else {
                            //ini buat 1 image selection
                            imageGalleryUris.add(TAPImagePreviewModel.Builder(intent.getData(), true));
                        }
                        openImagePreviewPage(imageGalleryUris);
                        break;
                    case SEND_IMAGE_FROM_PREVIEW:
                        ArrayList<TAPImagePreviewModel> images = intent.getParcelableArrayListExtra(K_IMAGE_RES_CODE);
                        if (null != images && 0 < images.size()) {
                            TAPChatManager.getInstance().sendImageMessage(TapTalk.appContext, vm.getRoom().getRoomID(), images);
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
                    vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TAPChatActivity.this, SEND_IMAGE_FROM_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.getInstance().pickImageFromGallery(TAPChatActivity.this, SEND_IMAGE_FROM_GALLERY, true);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE_TO_DISK:
                    if (null != messageAdapter) {
                        messageAdapter.notifyDataSetChanged();
                    }
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
            registerBroadcastManager();
        }
    }

    private void checkPermissions() {
        // Check and request write storage permission (only request once)
        if (!TAPDataManager.getInstance().isWriteStoragePermissionRequested() &&
                !TAPUtils.getInstance().hasPermissions(
                        TAPChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new TapTalkDialog.Builder(TAPChatActivity.this)
                    .setTitle(getString(R.string.tap_permission_request))
                    .setMessage(getString(R.string.tap_write_storage_permission))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setCancelable(false)
                    .setPrimaryButtonListener(v -> {
                                ActivityCompat.requestPermissions(TAPChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE_TO_DISK);
                                TAPDataManager.getInstance().setWriteStoragePermissionRequested(true);
                            }
                    )
                    .show();
        }
    }

    private void bindViews() {
        sblChat = getSwipeBackLayout();
        flMessageList = (FrameLayout) findViewById(R.id.fl_message_list);
        clContainer = (ConstraintLayout) findViewById(R.id.cl_container);
        clEmptyChat = (ConstraintLayout) findViewById(R.id.cl_empty_chat);
        clQuote = (ConstraintLayout) findViewById(R.id.cl_quote);
        clChatComposer = (ConstraintLayout) findViewById(R.id.cl_chat_composer);
        clRoomOnlineStatus = (ConstraintLayout) findViewById(R.id.cl_room_online_status);
        clRoomTypingStatus = (ConstraintLayout) findViewById(R.id.cl_room_typing_status);
        ivButtonBack = (ImageView) findViewById(R.id.iv_button_back);
        ivRoomIcon = (ImageView) findViewById(R.id.iv_room_icon);
        ivButtonCancelReply = (ImageView) findViewById(R.id.iv_cancel_reply);
        ivButtonChatMenu = (ImageView) findViewById(R.id.iv_chat_menu);
        ivButtonAttach = (ImageView) findViewById(R.id.iv_attach);
        ivButtonSend = (ImageView) findViewById(R.id.iv_send);
        ivToBottom = (ImageView) findViewById(R.id.iv_to_bottom);
        ivRoomTypingIndicator = (ImageView) findViewById(R.id.iv_room_typing_indicator);
        civRoomImage = (CircleImageView) findViewById(R.id.civ_room_image);
        civMyAvatar = (CircleImageView) findViewById(R.id.civ_my_avatar);
        civOtherUserAvatar = (CircleImageView) findViewById(R.id.civ_other_user_avatar);
        rcivQuoteImage = (TAPRoundedCornerImageView) findViewById(R.id.rciv_quote_image);
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomStatus = (TextView) findViewById(R.id.tv_room_status);
        tvRoomTypingStatus = (TextView) findViewById(R.id.tv_room_typing_status);
        tvChatEmptyGuide = (TextView) findViewById(R.id.tv_chat_empty_guide);
        tvProfileDescription = (TextView) findViewById(R.id.tv_profile_description);
        tvQuoteTitle = (TextView) findViewById(R.id.tv_quote_title);
        tvQuoteContent = (TextView) findViewById(R.id.tv_quote_content);
        tvBadgeUnread = (TextView) findViewById(R.id.tv_badge_unread);
        rvMessageList = (TAPChatRecyclerView) findViewById(R.id.rv_message_list);
        rvCustomKeyboard = (RecyclerView) findViewById(R.id.rv_custom_keyboard);
        etChat = (EditText) findViewById(R.id.et_chat);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        vQuoteDecoration = findViewById(R.id.v_quote_decoration);
        fConnectionStatus = (TAPConnectionStatusFragment) getSupportFragmentManager().findFragmentById(R.id.f_connection_status);
    }

    private boolean initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPChatViewModel.class);
        if (null == vm.getRoom()) {
            vm.setRoom(getIntent().getParcelableExtra(K_ROOM));
        }
        if (null == vm.getMyUserModel()) {
            vm.setMyUserModel(TAPDataManager.getInstance().getActiveUser());
        }
        if (null == vm.getOtherUserModel()) {
            vm.setOtherUserModel(TAPContactManager.getInstance().getUserData(vm.getOtherUserID()));
        }

        if (null == vm.getRoom()) {
            Toast.makeText(TapTalk.appContext, getString(R.string.tap_error_room_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return null != vm.getMyUserModel() && null != vm.getOtherUserModel();
    }

    private void initView() {

        getWindow().setBackgroundDrawable(null);

        // Set room name
        tvRoomName.setText(vm.getRoom().getRoomName());

        if (null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
            // Load room image
            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage);
        } else if (null != vm.getOtherUserModel().getAvatarURL().getThumbnail() && !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
            // Load user avatar URL
            // TODO: 1 February 2019 CHECK IF ROOM IS GROUP
            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomImage);
            vm.getRoom().setRoomImage(vm.getOtherUserModel().getAvatarURL());
        } else {
            // Use default profile picture if image is empty
            civRoomImage.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.getOtherUserModel().getUserRole() && null != vm.getOtherUserModel().getUserRole().getRoleIconURL() && !vm.getOtherUserModel().getUserRole().getRoleIconURL().isEmpty()) {
            glide.load(vm.getOtherUserModel().getUserRole().getRoleIconURL()).into(ivRoomIcon);
            ivRoomIcon.setVisibility(View.VISIBLE);
        } else {
            ivRoomIcon.setVisibility(View.GONE);
        }

        // Set room status
        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
            vm.setOtherUserTyping(true);
            showTypingIndicator();
        }

        // TODO: 24 September 2018 CALL ONLINE STATUS API
        //showUserOffline();

        // Initialize chat message RecyclerView
        messageAdapter = new TAPMessageAdapter(glide, chatListener);
        messageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.setAdapter(messageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        // FIXME: 9 November 2018 IMAGES CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0);
        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvMessageList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        // Initialize custom keyboard
        vm.setCustomKeyboardItems(TapTalk.requestCustomKeyboardItems(vm.getMyUserModel(), vm.getOtherUserModel()));
        if (null != vm.getCustomKeyboardItems() && vm.getCustomKeyboardItems().size() > 0) {
            // Enable custom keyboard
            vm.setCustomKeyboardEnabled(true);
            customKeyboardAdapter = new TAPCustomKeyboardAdapter(vm.getCustomKeyboardItems(), customKeyboardItemModel -> {
                for (TAPListener tapListener : TapTalk.getTapTalkListeners()) {
                    tapListener.onCustomKeyboardItemClicked(TAPChatActivity.this, customKeyboardItemModel, vm.getMyUserModel(), vm.getOtherUserModel());
                }
            });
            rvCustomKeyboard.setAdapter(customKeyboardAdapter);
            rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        } else {
            // Disable custom keyboard
            vm.setCustomKeyboardEnabled(false);
            ivButtonChatMenu.setVisibility(View.GONE);
        }

        // TODO: 1 February 2019 UPDATE WELCOME MESSAGE
        if (null != vm.getOtherUserModel().getUserRole() && vm.getOtherUserModel().getUserRole().getCode().equals("expert")) {
            tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + vm.getRoom().getRoomName() + "</font></b> is an Expert."));
            tvProfileDescription.setText("Hi, there! If you are looking for creative gifts for someone special, please check his/her services!");
        } else {
            tvChatEmptyGuide.setText("Are you looking for creative gifts?");
            tvProfileDescription.setText("Discuss with your friend and discover more about the creative gift in our lists.");
        }

        //ini listener buat scroll pagination (di Init View biar kebuat cuman sekali aja)
        endlessScrollListener = new TAPEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < messageAdapter.getItems().size()) {
                    new Thread(() -> {
                        vm.getMessageByTimestamp(vm.getRoom().getRoomID(), dbListenerPaging, vm.getLastTimestamp());
                        state = STATE.WORKING;
                    }).start();
                }
            }
        };

        // Load items from database for the First Time (First Load)
        if (vm.getMessageModels().size() == 0) {
            vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show/hide ivToBottom
            rvMessageList.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                    vm.setOnBottom(true);
                    ivToBottom.setVisibility(View.INVISIBLE);
                    tvBadgeUnread.setVisibility(View.INVISIBLE);
                    vm.clearUnreadMessages();
                } else {
                    vm.setOnBottom(false);
                    ivToBottom.setVisibility(View.VISIBLE);
                }
            });
        }

        LayoutTransition containerTransition = clContainer.getLayoutTransition();
        containerTransition.addTransitionListener(containerTransitionListener);

        etChat.addTextChangedListener(chatWatcher);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

        sblChat.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        sblChat.setSwipeInterface(swipeInterface);

        civRoomImage.setOnClickListener(v -> openRoomProfile());
        ivButtonBack.setOnClickListener(v -> closeActivity());
        ivButtonCancelReply.setOnClickListener(v -> hideQuoteLayout());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> buildAndSendTextMessage());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
        flMessageList.setOnClickListener(v -> chatListener.onOutsideClicked());
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
                    callApiGetUserByUserID();
                }
                callApiAfter();
                restartFailedDownloads();
            }
        };
        TAPConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private void registerBroadcastManager() {
        TAPBroadcastManager.register(this, broadcastReceiver, UploadProgressLoading
                , UploadProgressFinish, UploadFailed, UploadCancelled, UploadRetried,
                DownloadProgressLoading, DownloadFinish, DownloadFailed);
    }

    private void closeActivity() {
        rvCustomKeyboard.setVisibility(View.GONE);
        onBackPressed();
    }

    private void cancelNotificationWhenEnterRoom() {
        TAPNotificationManager.getInstance().cancelNotificationWhenEnterRoom(this, vm.getRoom().getRoomID());
        TAPNotificationManager.getInstance().clearNotifMessagesMap(vm.getRoom().getRoomID());
    }

    private void openRoomProfile() {
        for (TAPListener tapListener : TapTalk.getTapTalkListeners()) {
            // TODO: 21 December 2018 HANDLE GROUP CHAT
            tapListener.onUserProfileClicked(TAPChatActivity.this, vm.getOtherUserModel());
        }
        // TODO: 21 December 2018 TEMPORARILY DISABLED FEATURE
//        if (TapTalk.isOpenDefaultProfileEnabled) {
//            Intent intent = new Intent(this, TAPProfileActivity.class);
//            intent.putExtra(K_ROOM, vm.getRoom());
//            startActivity(intent);
//            overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
//        }
    }

    private void loadProfilePicture(String image, ImageView imageView) {
        glide.load(image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                imageView.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    private void updateUnreadCount() {
        runOnUiThread(() -> {
            if (vm.isOnBottom() || vm.getUnreadCount() == 0) {
                tvBadgeUnread.setVisibility(View.INVISIBLE);
            } else if (vm.getUnreadCount() > 0) {
                tvBadgeUnread.setText(String.valueOf(vm.getUnreadCount()));
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else if (View.VISIBLE == ivToBottom.getVisibility()) {
                ivToBottom.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateMessageDecoration() {
        //ini buat margin atas sma bawah chatnya (recyclerView Message List)
        if (rvMessageList.getItemDecorationCount() > 0) {
            rvMessageList.removeItemDecorationAt(0);
        }
        rvMessageList.addItemDecoration(new TAPVerticalDecoration(TAPUtils.getInstance().dpToPx(10), 0, messageAdapter.getItemCount() - 1));
    }

    // Previously attemptSend
    private void buildAndSendTextMessage() {
        String message = etChat.getText().toString();
        //ngecekin yang mau di kirim itu kosong atau nggak
        if (!TextUtils.isEmpty(message.trim())) {
            //ngereset isi edit text yang buat kirim chat
            etChat.setText("");
            //tutup bubble yang lagi expand
            messageAdapter.shrinkExpandedBubble();
            TAPChatManager.getInstance().sendTextMessage(message);
            //scroll to Bottom
            rvMessageList.scrollToPosition(0);
        }
    }

    // Previously addNewTextMessage
    private void updateMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                //ini ngecek kalau masih ada logo empty chat ilangin dlu
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                    flMessageList.setVisibility(View.VISIBLE);
                }
            });
            // Replace pending message with new message
            String newID = newMessage.getLocalID();
            //nentuin itu messagenya yang ngirim user sndiri atau lawan chat user
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPDataManager
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
                } else if (vm.isOnBottom() || ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    Log.e(TAG, "rio:2 " + newID);
                    messageAdapter.addMessage(newMessage);
                    rvMessageList.scrollToPosition(0);
                    vm.addMessagePointer(newMessage);
                } else {
                    // Message from other people is received when recycler is scrolled up
                    messageAdapter.addMessage(newMessage);
                    vm.addUnreadMessage(newMessage);
                    vm.addMessagePointer(newMessage);
                    Log.e(TAG, "updateUnreadCount: 1");
                    updateUnreadCount();
                }
                updateMessageDecoration();
            });
        }
    }

    // Previously addNewTextMessage
    private void addNewMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                //ini ngecek kalau masih ada logo empty chat ilangin dlu
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                    flMessageList.setVisibility(View.VISIBLE);
                }
            });
            //nentuin itu messagenya yang ngirim user sndiri atau lawan chat user
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPDataManager
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
                    Log.e(TAG, "updateUnreadCount: 2");
                    updateUnreadCount();
                }
                updateMessageDecoration();
            });
        }
    }

    private void updateMessageFromSocket(TAPMessageModel message) {
        runOnUiThread(() -> {
            int position = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
            Log.e(TAG, "updateMessageFromSocket: " + position + " " + message.getLocalID() + " " + message.getHidden());
            if (-1 != position) {
                Log.e(TAG, "rio:1 " + message.getLocalID());
                vm.updateMessagePointer(message);
                //update data yang ada di adapter soalnya kalau cumah update data yang ada di view model dy ga berubah
                messageAdapter.getItemAt(position).updateValue(message);
                messageAdapter.notifyItemChanged(position);
            } else {
                new Thread(() -> updateMessage(message)).start();
            }
        });
    }

    //ngecek kalau messagenya udah ada di hash map brati udah ada di recycler view update aja
    // tapi kalau belum ada brati belom ada di recycler view jadi harus d add
    private void addBeforeTextMessage(final TAPMessageModel newMessage, List<TAPMessageModel> tempBeforeMessages) {
        String newID = newMessage.getLocalID();
        runOnUiThread(() -> {
            if (vm.getMessagePointer().containsKey(newID)) {
                //kalau udah ada cek posisinya dan update data yang ada di dlem modelnya
                vm.updateMessagePointer(newMessage);
                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else {
                new Thread(() -> {
                    //kalau belom ada masukin kedalam list dan hash map
                    tempBeforeMessages.add(newMessage);
                    vm.addMessagePointer(newMessage);
                }).start();
            }
            //updateMessageDecoration();
        });
    }

    //ngecek kalau messagenya udah ada di hash map brati udah ada di recycler view update aja
    // tapi kalau belum ada brati belom ada di recycler view jadi harus d add
    private void addAfterTextMessage(final TAPMessageModel newMessage, List<TAPMessageModel> tempAfterMessages) {
        String newID = newMessage.getLocalID();
        runOnUiThread(() -> {
            if (vm.getMessagePointer().containsKey(newID)) {
                //kalau udah ada cek posisinya dan update data yang ada di dlem modelnya
                Log.e(TAG, "addAfterTextMessage:2 "+newMessage.getCreated()+" "+newMessage.getBody()+" "+newID );
                vm.updateMessagePointer(newMessage);
                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (!vm.getMessagePointer().containsKey(newID)) {
                new Thread(() -> {
                    //kalau belom ada masukin kedalam list dan hash map
                    tempAfterMessages.add(newMessage);
                    vm.addMessagePointer(newMessage);
                    Log.e(TAG, "addAfterTextMessage: "+newMessage.getCreated()+" "+newMessage.getBody()+" "+newID  );
                    //runOnUiThread(() -> messageAdapter.addMessage(newMessage));
                }).start();
            }
            //updateMessageDecoration();
        });
    }

    private void showQuoteLayout(@Nullable TAPMessageModel message, boolean showKeyboard) {
        if (null == message) {
            return;
        }
        vm.setQuotedMessage(message);
        runOnUiThread(() -> {
            clQuote.setVisibility(View.VISIBLE);
            tvQuoteTitle.setText(message.getUser().getName());
            tvQuoteContent.setText(message.getBody());
            // Add other quotable message type here
            if (message.getType() == TYPE_IMAGE && null != message.getData()) {
                // Show image quote
                vQuoteDecoration.setVisibility(View.GONE);
                // TODO: 29 January 2019 IMAGE MIGHT NOT EXIST IN CACHE
                rcivQuoteImage.setImageDrawable(TAPCacheManager.getInstance(this).getBitmapDrawable((String) message.getData().get(FILE_ID)));
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteContent.setMaxLines(1);
            } else if (null != message.getData() && null != message.getData().get(IMAGE_URL)) {
                // Unknown message type
                glide.load((String) message.getData().get(IMAGE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show text quote
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);
                tvQuoteContent.setMaxLines(2);
            }
            if (showKeyboard) {
                TAPUtils.getInstance().showKeyboard(this, etChat);
            }
        });
    }

    private void hideQuoteLayout() {
        vm.setQuotedMessage(null);
        runOnUiThread(() -> clQuote.setVisibility(View.GONE));
    }

    private void scrollToBottom() {
        rvMessageList.scrollToPosition(0);
        ivToBottom.setVisibility(View.INVISIBLE);
        vm.clearUnreadMessages();
        Log.e(TAG, "updateUnreadCount: 3");
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
        ivButtonChatMenu.setImageResource(R.drawable.tap_ic_chatmenu_hamburger);
        etChat.requestFocus();
    }

    private void showCustomKeyboard() {
        TAPUtils.getInstance().dismissKeyboard(this);
        etChat.clearFocus();
        new Handler().postDelayed(() -> {
            rvCustomKeyboard.setVisibility(View.VISIBLE);
            ivButtonChatMenu.setImageResource(R.drawable.tap_ic_chatmenu_keyboard);
        }, 150L);
    }

    private void hideKeyboards() {
        rvCustomKeyboard.setVisibility(View.GONE);
        ivButtonChatMenu.setImageResource(R.drawable.tap_ic_chatmenu_hamburger);
        TAPUtils.getInstance().dismissKeyboard(this);
    }

    private void openAttachMenu() {
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
            if (!vm.isOtherUserTyping()) {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(getDrawable(R.drawable.tap_bg_circle_vibrantgreen));
            tvRoomStatus.setText(getString(R.string.tap_active_now));
            vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        runOnUiThread(() -> {
            if (!vm.isOtherUserTyping()) {
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
        vm.setOtherUserTyping(true);
        typingIndicatorTimeoutTimer.cancel();
        typingIndicatorTimeoutTimer.start();
        runOnUiThread(() -> {
            clRoomTypingStatus.setVisibility(View.VISIBLE);
            clRoomOnlineStatus.setVisibility(View.GONE);
            glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
            tvRoomTypingStatus.setText(getString(R.string.tap_typing));
        });
    }

    private void hideTypingIndicator() {
        vm.setOtherUserTyping(false);
        typingIndicatorTimeoutTimer.cancel();
        runOnUiThread(() -> {
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
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

    private void openImagePreviewPage(ArrayList<TAPImagePreviewModel> imageUris) {
        Intent intent = new Intent(TAPChatActivity.this, TAPImagePreviewActivity.class);
        intent.putExtra(K_IMAGE_URLS, imageUris);
        startActivityForResult(intent, SEND_IMAGE_FROM_PREVIEW);
    }

    //ini Fungsi buat manggil Api Before
    private void fetchBeforeMessageFromAPIAndUpdateUI(TapDefaultDataView<TAPGetMessageListByRoomResponse> beforeView) {
        /*fetchBeforeMessageFromAPIAndUpdateUI rules:
         * parameternya max created adalah Created yang paling kecil dari yang ada di recyclerView*/
        new Thread(() -> {
            //ini ngecek kalau misalnya isi message modelnya itu kosong manggil api before maxCreated = current TimeStamp
            if (0 < vm.getMessageModels().size())
                TAPDataManager.getInstance().getMessageListByRoomBefore(vm.getRoom().getRoomID()
                        , vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated()
                        , beforeView);
            else TAPDataManager.getInstance().getMessageListByRoomBefore(vm.getRoom().getRoomID()
                    , System.currentTimeMillis()
                    , beforeView);
        }).start();
    }

    private void callApiGetUserByUserID() {
        new Thread(() -> {
            if (TAPChatManager.getInstance().isNeedToCalledUpdateRoomStatusAPI())
                TAPDataManager.getInstance().getUserByIdFromApi(vm.getOtherUserID(), new TapDefaultDataView<TAPGetUserResponse>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        TAPUserModel userResponse = response.getUser();
                        TAPContactManager.getInstance().updateUserDataMap(userResponse);
                        TAPOnlineStatusModel onlineStatus = TAPOnlineStatusModel.Builder(userResponse);
                        setChatRoomStatus(onlineStatus);
                        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(false);

                        if (null == vm.getOtherUserModel()) {
                            vm.setOtherUserModel(response.getUser());
                            initRoom();
                        }
                    }
                });
        }).start();
    }

    private void callApiAfter() {
        /*call api after rules:
        --> kalau chat ga kosong, dan kita udah ada lastTimeStamp di preference
            brati parameternya minCreated = created pling kecil dari yang ada di recyclerView
            dan last Updatenya = dari preference
        --> kalau chat ga kosong, dan kita belum ada lastTimeStamp di preference
            brati parameternya minCreated = lastUpdated = created paling kecil dari yang ada di recyclerView
        --> selain itu ga usah manggil api after

        ps: di jalanin di new Thread biar ga ganggun main Thread aja*/
        new Thread(() -> {
            if (vm.getMessageModels().size() > 0 && !TAPDataManager.getInstance().checkKeyInLastMessageTimestamp(vm.getRoom().getRoomID())) {
                TAPDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                TAPDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
    }

    private void restartFailedDownloads() {
        if (TAPFileDownloadManager.getInstance().hasFailedDownloads() &&
                TAPConnectionManager.getInstance().getConnectionStatus() ==
                        CONNECTED) {
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
        //merge proses divide
        if (messageListSize < 2) {
            return;
        }

        //ambil nilai tengah
        int leftListSize = messageListSize / 2;
        //sisa dari mediannya
        int rightListSize = messageListSize - leftListSize;
        //bkin list kiri sejumlah median sizenya
        List<TAPMessageModel> leftList = new ArrayList<>(leftListSize);
        //bikin list kanan sejumlah sisanya (size - median)
        List<TAPMessageModel> rightList = new ArrayList<>(rightListSize);

        for (int index = 0; index < leftListSize; index++)
            leftList.add(index, messages.get(index));

        for (int index = leftListSize; index < messageListSize; index++)
            rightList.add((index - leftListSize), messages.get(index));

        //recursive
        mergeSort(leftList, sortDirection);
        mergeSort(rightList, sortDirection);

        //setelah selesai lalu di gabungin sambil di sort
        merge(messages, leftList, rightList, leftListSize, rightListSize, sortDirection);
    }

    private void merge(List<TAPMessageModel> messagesAll, List<TAPMessageModel> leftList, List<TAPMessageModel> rightList, int leftSize, int rightSize, int sortDirection) {
        //Merge adalah fungsi buat Conquernya

        //index left buat nentuin index leftList
        //index right buat nentuin index rightList
        //index combine buat nentuin index saat gabungin jd 1 list
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

        //looping untuk masukin sisa di list masing masing
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
            updateMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
            // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
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
        public void onSendTextMessage(TAPMessageModel message) {
            addNewMessage(message);
            hideQuoteLayout();
        }

        @Override
        public void onSendImageMessage(TAPMessageModel message) {
            // TODO: 5 November 2018 TESTING IMAGE MESSAGE STATUS
            message.setNeedAnimateSend(true);
            message.setSending(false);
            addNewMessage(message);
        }

        @Override
        public void onReplyMessage(TAPMessageModel message) {
            showQuoteLayout(message, true);
            TAPChatManager.getInstance().removeUserInfo(vm.getRoom().getRoomID());
        }

        @Override
        public void onRetrySendMessage(TAPMessageModel message) {
            vm.delete(message.getLocalID());
            switch (message.getType()) {
                case TAPDefaultConstant.MessageType.TYPE_TEXT:
                    TAPChatManager.getInstance().sendTextMessage(message.getBody());
                    break;
                case TYPE_IMAGE:
                    // TODO: 7 January 2019 RESEND IMAGE MESSAGE
//                    TAPChatManager.getInstance().showDummyImageMessage(message.getBody());
                    break;
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

            message.setIsRead(true);
            vm.removeUnreadMessage(message.getLocalID());
            Log.e(TAG, "updateUnreadCount: 4");
            updateUnreadCount();
        }

        @Override
        public void onMessageQuoteClicked(TAPMessageModel message) {
            TapTalk.triggerMessageQuoteClicked(TAPChatActivity.this, message);
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
            showTypingIndicator();
        }

        @Override
        public void onReceiveStopTyping(TAPTypingModel typingModel) {
            hideTypingIndicator();
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
            switch (action) {
                case UploadProgressLoading:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case UploadProgressFinish:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID) &&
                            intent.getSerializableExtra(UploadImageData) instanceof HashMap) {
                        TAPMessageModel messageModel = vm.getMessagePointer().get(localID);
                        messageModel.setData((HashMap<String, Object>) intent.getSerializableExtra(UploadImageData));
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(messageModel));
                    }
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

                        TAPFileUploadManager.getInstance().cancelUpload(TAPChatActivity.this, cancelledMessageModel,
                                vm.getRoom().getRoomID());

                        vm.removeFromUploadingList(localID);
                        vm.removeMessagePointer(localID);
                        messageAdapter.removeMessageAt(itemPos);
                    }
                    break;
                case UploadRetried:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel failedMessageModel = vm.getMessagePointer().get(localID);
                        TAPDataManager.getInstance().updateFailedMessageToSending(localID);
                        // Set Start Point for Progress
                        TAPFileUploadManager.getInstance().addUploadProgressMap(failedMessageModel.getLocalID(), 0);
                        failedMessageModel.setFailedSend(false);
                        failedMessageModel.setSending(true);
                        new Thread(() -> {
                            TAPMessageModel imageMessageRetry = failedMessageModel.copyMessageModel();
                            TAPChatManager.getInstance().sendImageMessage(TAPChatActivity.this, vm.getRoom().getRoomID(), imageMessageRetry);
                        }).start();
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(failedMessageModel));
                    }
                    break;
                case DownloadProgressLoading:
                case DownloadFinish:
                    localID = intent.getStringExtra(DownloadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                case DownloadFailed:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance().addFailedDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
            }
        }
    };

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null != TAPChatActivity.this.getCurrentFocus() && TAPChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0 && s.toString().trim().length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_active);
            } else if (null != TAPChatActivity.this.getCurrentFocus() && TAPChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_inactive);
            } else if (s.length() > 0 && s.toString().trim().length() > 0) {
                if (vm.isCustomKeyboardEnabled()) {
                    ivButtonChatMenu.setVisibility(View.VISIBLE);
                }
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_active);
            } else {
                if (vm.isCustomKeyboardEnabled()) {
                    ivButtonChatMenu.setVisibility(View.VISIBLE);
                }
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_inactive);
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
                rvCustomKeyboard.setVisibility(View.GONE);
                ivButtonChatMenu.setImageResource(R.drawable.tap_ic_chatmenu_hamburger);
                TAPUtils.getInstance().showKeyboard(TAPChatActivity.this, etChat);

                if (0 < etChat.getText().toString().length()) {
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
            } else if (hasFocus) {
                TAPUtils.getInstance().showKeyboard(TAPChatActivity.this, etChat);
            }
        }
    };

    LayoutTransition.TransitionListener containerTransitionListener = new LayoutTransition.TransitionListener() {
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
            }

            if (null != messageAdapter && 0 == messageAdapter.getItems().size()) {
                runOnUiThread(() -> {
                    // First load
                    messageAdapter.setMessages(models);
                    if (models.size() == 0) {
                        // Chat is empty
                        // TODO: 24 September 2018 CHECK ROOM TYPE
                        clEmptyChat.setVisibility(View.VISIBLE);

                        if (null != vm.getMyUserModel().getAvatarURL() && !vm.getMyUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getMyUserModel().getAvatarURL().getThumbnail(), civMyAvatar);
                        } else {
                            civMyAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
                        }
                        if (null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civOtherUserAvatar);
                        } else if (null != vm.getOtherUserModel().getAvatarURL().getThumbnail() && !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civOtherUserAvatar);
                        } else {
                            civOtherUserAvatar.setImageDrawable(getDrawable(R.drawable.tap_img_default_avatar));
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
                    }
                    rvMessageList.scrollToPosition(0);
                    updateMessageDecoration();

                    //ini buat ngecek kalau room nya kosong manggil api before aja
                    //sebaliknya kalau roomnya ada isinya manggil after baru before*
                    //* = kalau jumlah itemnya < 50
                    if (0 < vm.getMessageModels().size() && NUM_OF_ITEM > vm.getMessageModels().size()) {
                    /* Call Message List API
                    Kalau misalnya lastUpdatednya ga ada di preference last updated dan min creatednya sama
                    Kalau misalnya ada di preference last updatednya ambil dari yang ada di preference (min created ambil dari getCreated)
                    kalau last updated dari getUpdated */
                        callApiAfter();
                    } else if (NUM_OF_ITEM <= vm.getMessageModels().size()) {
                        rvMessageList.addOnScrollListener(endlessScrollListener);
                        callApiAfter();
                    } else {
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
                    new Thread(() -> vm.setMessageModels(messageAdapter.getItems())).start();
                    if (rvMessageList.getVisibility() != View.VISIBLE) {
                        rvMessageList.setVisibility(View.VISIBLE);
                    }
                    if (state == STATE.DONE) {
                        updateMessageDecoration();
                    }
                });
                if (NUM_OF_ITEM > entities.size() && 1 < entities.size()) {
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
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                models.add(model);
                vm.addMessagePointer(model);
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
            }

            if (null != messageAdapter) {
                if (NUM_OF_ITEM > entities.size() && STATE.DONE != state) {
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeViewPaging);
                } else if (STATE.WORKING == state) {
                    state = STATE.LOADED;
                }

                runOnUiThread(() -> {
                    flMessageList.setVisibility(View.VISIBLE);
                    messageAdapter.addMessage(models);
                    new Thread(() -> vm.setMessageModels(messageAdapter.getItems())).start();

                    if (rvMessageList.getVisibility() != View.VISIBLE)
                        rvMessageList.setVisibility(View.VISIBLE);
                    if (state == STATE.DONE) updateMessageDecoration();
                });
            }
        }
    };

    private TAPAttachmentListener attachmentListener = new TAPAttachmentListener() {
        @Override
        public void onCameraSelected() {
            fConnectionStatus.hideUntilNextConnect(true);
            vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TAPChatActivity.this, SEND_IMAGE_FROM_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            fConnectionStatus.hideUntilNextConnect(true);
            TAPUtils.getInstance().pickImageFromGallery(TAPChatActivity.this, SEND_IMAGE_FROM_GALLERY, true);
        }
    };

    private TapDefaultDataView<TAPGetMessageListByRoomResponse> messageAfterView = new TapDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageAfterModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageAfterModels = new ArrayList<>();
            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    addAfterTextMessage(message, messageAfterModels);
//                    new Thread(() -> {
                        responseMessages.add(TAPChatManager.getInstance().convertToEntity(message));

                        //ini buat update last update timestamp yang ada di preference
                        //ini di taruh di new Thread biar ga bkin scrollingnya lag
                        if (null != message.getUpdated() &&
                                TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < message.getUpdated()) {
                            TAPDataManager.getInstance().saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), message.getUpdated());
                        }
//                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //sorting message balikan dari api after
            //messageAfterModels ini adalah message balikan api yang belom ada di recyclerView
            if (0 < messageAfterModels.size()) {
                TAPMessageStatusManager.getInstance().updateMessageStatusToDeliveredFromNotification(messageAfterModels);
                mergeSort(messageAfterModels, ASCENDING);
            }

            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);
                //masukin datanya ke dalem recyclerView
                //posisinya dimasukin ke index 0 karena brati dy message baru yang belom ada
                messageAdapter.addMessage(0, messageAfterModels);
                updateMessageDecoration();
                //ini buat ngecek kalau user lagi ada di bottom pas masuk data lgsg di scroll jdi ke paling bawah lagi
                //kalau user ga lagi ada di bottom ga usah di turunin
                if (vm.isOnBottom()) rvMessageList.scrollToPosition(0);
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(messageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            if (0 < responseMessages.size())
                TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
                });

            //ngecek isInitialApiCallFinished karena kalau dari onResume, api before itu ga perlu untuk di panggil lagi
            if (0 < vm.getMessageModels().size() && NUM_OF_ITEM > vm.getMessageModels().size() && !vm.isInitialAPICallFinished()) {
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
            }
            //ubah initialApiCallFinished jdi true (brati udah dipanggil pas onCreate / pas pertama kali di buka
            vm.setInitialAPICallFinished(true);
        }

        @Override
        public void onError(TAPErrorModel error) {
            if (BuildConfig.DEBUG) {
                new TapTalkDialog.Builder(TAPChatActivity.this)
                        .setTitle("Error")
                        .setMessage(error.getMessage())
                        .show();
            }

            if (0 < vm.getMessageModels().size())
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
        }

        @Override
        public void onError(String errorMessage) {
            if (BuildConfig.DEBUG) {
                new TapTalkDialog.Builder(TAPChatActivity.this)
                        .setTitle("Error")
                        .setMessage(errorMessage)
                        .show();
            }

            if (0 < vm.getMessageModels().size())
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
        }
    };

    //message before yang di panggil setelah api after pas awal (cuman di panggil sekali doang)
    private TapDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeView = new TapDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>();
            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    addBeforeTextMessage(message, messageBeforeModels);

                    new Thread(() -> responseMessages.add(TAPChatManager.getInstance().convertToEntity(message))).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, ASCENDING);

            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE && 0 < messageBeforeModels.size()) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);

                //ini di taronya di belakang karena message before itu buat message yang lama-lama
                messageAdapter.addMessageFirstFromAPI(messageBeforeModels);
                updateMessageDecoration();
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(messageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
            if (NUM_OF_ITEM > response.getMessages().size() && 1 < response.getMessages().size()) {
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

    //message before yang di panggil pas pagination db balikin data di bawah limit
    private TapDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeViewPaging = new TapDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>();
            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    addBeforeTextMessage(message, messageBeforeModels);

                    new Thread(() -> responseMessages.add(TAPChatManager.getInstance().convertToEntity(message))).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //ini ngecek kalau misalnya balikan apinya itu perPagenya > pageCount brati berenti ga usah pagination lagi (State.DONE)
            //selain itu paginationnya bisa lanjut lagi
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;
            if (state == STATE.DONE) updateMessageDecoration();

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, ASCENDING);
            runOnUiThread(() -> {
                //ini di taronya di belakang karena message before itu buat message yang lama-lama
                messageAdapter.addMessage(messageBeforeModels);
                updateMessageDecoration();
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(messageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            TAPDataManager.getInstance().insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
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
                    tvRoomStatus.setText(TAPTimeFormatter.getInstance().getLastActivityString(TAPChatActivity.this, lastActive));
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
}
