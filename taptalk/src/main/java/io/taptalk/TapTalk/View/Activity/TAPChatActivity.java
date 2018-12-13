package io.taptalk.TapTalk.View.Activity;

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.SwipeBackLayout.SwipeBackLayout;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapTalkListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPCustomKeyboardManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListbyRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPCourierModel;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPOrderModel;
import io.taptalk.TapTalk.Model.TAPPairIdNameModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRecipientModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;
import io.taptalk.Taptalk.BuildConfig;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_TYPING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.K_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.NUM_OF_ITEM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.ACCEPTED_BY_SELLER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.ACTIVE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.CONFIRMED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.NOT_CONFIRMED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.PAYMENT_INCOMPLETE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.WAITING_PAYMENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.WAITING_REVIEW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.DESCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_EMIT_DELAY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;

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
    private ConstraintLayout clContainer, clEmptyChat, clReply, clChatComposer, clRoomOnlineStatus, clRoomTypingStatus;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivButtonCancelReply, ivButtonChatMenu, ivButtonAttach,
            ivButtonSend, ivToBottom, ivRoomTypingIndicator;
    private CircleImageView civRoomImage, civMyAvatar, civOtherUserAvatar;
    private TextView tvRoomName, tvRoomStatus, tvChatEmptyGuide, tvProfileDescription, tvReplySender,
            tvReplyBody, tvBadgeUnread, tvRoomTypingStatus;
    private View vStatusBadge;

    // RecyclerView
    private TAPMessageAdapter messageAdapter;
    private TAPCustomKeyboardAdapter customKeyboardAdapter;
    private LinearLayoutManager messageLayoutManager;

    // RoomDatabase
    private TAPChatViewModel vm;

    private TAPSocketListener socketListener;

    //enum Scrolling
    private enum STATE {
        WORKING, LOADED, DONE
    }

    private STATE state = STATE.LOADED;

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

        initViewModel();
        initView();
        initHelper();
        initListener();
        cancelNotificationWhenEnterRoom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAPChatManager.getInstance().updateUnreadCountInRoomList(TAPChatManager.getInstance().getOpenRoom());
        //ini buat reset openRoom
        TAPChatManager.getInstance().setOpenRoom(null);
        TAPChatManager.getInstance().removeChatListener(chatListener);
        // Stop offline timer
        vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TAPChatManager.getInstance().setActiveRoom(vm.getRoom());
        etChat.setText(TAPChatManager.getInstance().getMessageFromDraft());
        addNetworkListener();
        callApiGetUserByUserID();
        if (vm.isInitialAPICallFinished()) {
            callApiAfter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraftToManager();
        removeNetworkListener();
        sendTypingEmit(false);
        TAPChatManager.getInstance().deleteActiveRoom();
    }

    @Override
    public void onBackPressed() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            hideKeyboards();
        } else {
            TAPChatManager.getInstance().putUnsentMessageToList();
            super.onBackPressed();
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
                        if (null == vm.getCameraImageUri()) return;
                        TAPChatManager.getInstance().sendImageMessage(TAPChatActivity.this, vm.getCameraImageUri());
                        break;
                    case SEND_IMAGE_FROM_GALLERY:
                        if (null == intent) return;
                        TAPChatManager.getInstance().sendImageMessage(TAPChatActivity.this, intent.getData());
                        break;
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA:
                case PERMISSION_WRITE_EXTERNAL_STORAGE:
                    vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TAPChatActivity.this, SEND_IMAGE_FROM_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE:
                    TAPUtils.getInstance().pickImageFromGallery(TAPChatActivity.this, SEND_IMAGE_FROM_GALLERY);
                    break;
            }
        }
    }

    /**
     * =========================================================================================== *
     * PRIVATE METHODS
     * =========================================================================================== *
     */

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPChatViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(K_ROOM));
        vm.setMyUserModel(TAPDataManager.getInstance().getActiveUser());
        vm.setOtherUserModel(TAPContactManager.getInstance().getUserData(vm.getOtherUserID()));
    }

    @Override
    protected void initView() {
        sblChat = getSwipeBackLayout();
        flMessageList = (FrameLayout) findViewById(R.id.fl_message_list);
        clContainer = (ConstraintLayout) findViewById(R.id.cl_container);
        clEmptyChat = (ConstraintLayout) findViewById(R.id.cl_empty_chat);
        clReply = (ConstraintLayout) findViewById(R.id.cl_reply);
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
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomStatus = (TextView) findViewById(R.id.tv_room_status);
        tvRoomTypingStatus = (TextView) findViewById(R.id.tv_room_typing_status);
        tvChatEmptyGuide = (TextView) findViewById(R.id.tv_chat_empty_guide);
        tvProfileDescription = (TextView) findViewById(R.id.tv_profile_description);
        tvReplySender = (TextView) findViewById(R.id.tv_reply_sender);
        tvReplyBody = (TextView) findViewById(R.id.tv_reply_body);
        tvBadgeUnread = (TextView) findViewById(R.id.tv_badge_unread);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        rvMessageList = (TAPChatRecyclerView) findViewById(R.id.rv_message_list);
        rvCustomKeyboard = (RecyclerView) findViewById(R.id.rv_custom_keyboard);
        etChat = (EditText) findViewById(R.id.et_chat);

        getWindow().setBackgroundDrawable(null);

        // Set room name
        tvRoomName.setText(vm.getRoom().getRoomName());

        if (null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
            // Load room image
            Glide.with(this).load(vm.getRoom().getRoomImage().getThumbnail()).into(civRoomImage);
        } else {
            // Use random color if image is empty
            civRoomImage.setColorFilter(new PorterDuffColorFilter(TAPUtils.getInstance().getRandomColor(vm.getRoom().getRoomName()), PorterDuff.Mode.SRC_IN));
        }

        // Set room status
        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
            showTypingIndicator();
        }

        // TODO: 24 September 2018 CALL ONLINE STATUS API
        //showUserOffline();

        // Initialize chat message RecyclerView
        messageAdapter = new TAPMessageAdapter(chatListener);
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
            customKeyboardAdapter = new TAPCustomKeyboardAdapter(vm.getCustomKeyboardItems(),
                    customKeyboardItemModel -> TAPCustomKeyboardManager.getInstance()
                            .onCustomKeyboardItemClicked(customKeyboardItemModel,
                                    vm.getMyUserModel(), vm.getOtherUserModel()));
            rvCustomKeyboard.setAdapter(customKeyboardAdapter);
            rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        } else {
            // Disable custom keyboard
            vm.setCustomKeyboardEnabled(false);
            ivButtonChatMenu.setVisibility(View.GONE);
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
        vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show/hide ivToBottom
            rvMessageList.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                    vm.setOnBottom(true);
                    ivToBottom.setVisibility(View.INVISIBLE);
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
        ivButtonCancelReply.setOnClickListener(v -> hideReplyLayout());
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
                    callApiAfter();
                }
            }
        };
        TAPConnectionManager.getInstance().addSocketListener(socketListener);

        // TODO: 30 November 2018 TESTING LISTENER, REMOVE THIS LATER
        TAPCustomKeyboardManager.getInstance().addCustomKeyboardListener(customKeyboardListener);
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
        Intent intent = new Intent(this, TAPProfileActivity.class);
        intent.putExtra(K_ROOM, vm.getRoom());
        startActivity(intent);
    }

    private void updateUnreadCount() {
        runOnUiThread(() -> {
            if (vm.isOnBottom() || vm.getUnreadCount() == 0) {
                tvBadgeUnread.setVisibility(View.INVISIBLE);
            } else if (vm.getUnreadCount() > 0) {
                tvBadgeUnread.setText(String.valueOf(vm.getUnreadCount()));
                tvBadgeUnread.setVisibility(View.VISIBLE);
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
    private void addNewMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            // TODO: 12 December 2018 TEMPORARY ORDER CARD LOGIC
            checkAndUpdateOrderCard(newMessage);
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
                if (vm.getMessagePointer().containsKey(newID)) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    vm.updateMessagePointer(newMessage);
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                } else if (vm.isOnBottom() || ownMessage) {
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
        }
    }

    private void checkAndUpdateOrderCard(TAPMessageModel newMessage) {
        if (newMessage.getType() != TYPE_ORDER_CARD) {
            return;
        }
        // Only the latest card of the same order ID may be shown
        TAPMessageModel oldOrderCard = vm.getPreviousOrderWithSameID(newMessage);

        // Hide newMessage if it is older than ongoing card
        if (null != oldOrderCard && oldOrderCard.getCreated() > newMessage.getCreated()) {
            newMessage.setHidden(true);
            return;
        }

        // Hide previous card with the same order ID
        if (null != oldOrderCard) {
            oldOrderCard.setHidden(true);
            vm.removeOngoingOrderCard(oldOrderCard);
            // TODO: 19 November 2018 UPDATE MESSAGE IN DATABASE, UNCOMMENT BELOW
            //TAPDataManager.getInstance().insertToDatabase(TAPChatManager.getInstance().convertToEntity(oldOrderCard));
            runOnUiThread(() -> messageAdapter.notifyItemRemoved(messageAdapter.getItems().indexOf(oldOrderCard)));
        }

        int orderStatus = vm.getOrderModel(newMessage).getOrderStatus();
        switch (orderStatus) {
            // Add order card to pointer if status is not canceled/completed
            case NOT_CONFIRMED_BY_CUSTOMER:
            case CONFIRMED_BY_CUSTOMER:
            case ACCEPTED_BY_SELLER:
            case WAITING_PAYMENT:
            case PAYMENT_INCOMPLETE:
            case ACTIVE:
            case WAITING_REVIEW:
            default:
                vm.addOngoingOrderCard(newMessage);
                break;
        }
    }

    private void updateMessageFromSocket(TAPMessageModel message) {
        runOnUiThread(() -> {
            int position = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
            if (-1 != position) {
                new Thread(() -> vm.updateMessagePointer(message)).start();
                //update data yang ada di adapter soalnya kalau cumah update data yang ada di view model dy ga berubah
                messageAdapter.getItemAt(position).updateValue(message);
                messageAdapter.notifyItemChanged(position);
            } else {
                new Thread(() -> addNewMessage(message)).start();
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
                vm.updateMessagePointer(newMessage);
                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (!vm.getMessagePointer().containsKey(newID)) {
                new Thread(() -> {
                    //kalau belom ada masukin kedalam list dan hash map
                    tempAfterMessages.add(newMessage);
                    vm.addMessagePointer(newMessage);
                }).start();
            }
            //updateMessageDecoration();
        });
    }

    private void showReplyLayout(TAPMessageModel message) {
        // TODO: 1 November 2018 HANDLE NON-TEXT MESSAGES
        vm.setReplyTo(message);
        clReply.setVisibility(View.VISIBLE);
        tvReplySender.setText(message.getUser().getName());
        tvReplyBody.setText(message.getBody());
        etChat.requestFocus();
        TAPUtils.getInstance().showKeyboard(this, etChat);
    }

    private void hideReplyLayout() {
        vm.setReplyTo(null);
        clReply.setVisibility(View.GONE);
    }

    private void scrollToBottom() {
        rvMessageList.scrollToPosition(0);
        ivToBottom.setVisibility(View.INVISIBLE);
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
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(getDrawable(R.drawable.tap_bg_circle_vibrantgreen));
            tvRoomStatus.setText(getString(R.string.active_now));
            vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        runOnUiThread(() -> {
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
            lastActivityRunnable.run();
        });
    }

    private void sendTypingEmit(boolean isTyping) {
        if (TAPConnectionManager.getInstance().getConnectionStatus() != TAPConnectionManager.ConnectionStatus.CONNECTED) {
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
            Glide.with(this).load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
            tvRoomTypingStatus.setText(getString(R.string.typing));
        });
    }

    private void hideTypingIndicator() {
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

    private void addNetworkListener() {
        TAPNetworkStateManager.getInstance().addNetworkListener(networkListener);
    }

    private void removeNetworkListener() {
        TAPNetworkStateManager.getInstance().removeNetworkListener(networkListener);
    }

    //ini Fungsi buat manggil Api Before
    private void fetchBeforeMessageFromAPIAndUpdateUI(TapDefaultDataView<TAPGetMessageListbyRoomResponse> beforeView) {
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
                        TAPOnlineStatusModel onlineStatus = TAPOnlineStatusModel.Builder(response.getUser());
                        setChatRoomStatus(onlineStatus);
                        TAPChatManager.getInstance().setNeedToCalledUpdateRoomStatusAPI(false);
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
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(), messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                TAPDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
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
            addNewMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
            // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
        }

        @Override
        public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
            super.onReceiveMessageInOtherRoom(message);

            if (null != TAPChatManager.getInstance().getOpenRoom() &&
                    TAPChatManager.getInstance().getOpenRoom().equals(message.getRoom().getRoomID()))
                addNewMessage(message);
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
            // TODO: 1 November 2018 TESTING REPLY LAYOUT
            if (null != vm.getReplyTo()) {
                message.setReplyTo(vm.getReplyTo());
                vm.setReplyTo(null);
            }
            addNewMessage(message);
            if (clReply.getVisibility() == View.VISIBLE) {
                clReply.setVisibility(View.GONE);
            }
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
            showReplyLayout(message);
        }

        @Override
        public void onRetrySendMessage(TAPMessageModel message) {
            vm.delete(message.getLocalID());
            switch (message.getType()) {
                case TAPDefaultConstant.MessageType.TYPE_TEXT:
                    TAPChatManager.getInstance().sendTextMessage(message.getBody());
                    break;
                case TAPDefaultConstant.MessageType.TYPE_IMAGE:
                    TAPChatManager.getInstance().sendImageMessage(message.getBody());
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
            updateUnreadCount();
        }

        @Override
        public void onBubbleExpanded() {
            if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onOutsideClicked() {
            hideKeyboards();
        }

        @Override
        public void onLayoutLoaded(TAPMessageModel message) {
            if (message.getUser().getUserID().equals(vm.getMyUserModel().getUserID())
                    || messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                rvMessageList.scrollToPosition(0);
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

    // TODO: 29 November 2018 TESTING CUSTOM KEYBOARD MENU
    private TapTalkListener customKeyboardListener = new TapTalkListener() {
        @Override
        public void onCustomKeyboardItemClicked(TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
            switch (customKeyboardItemModel.getItemID()) {
                case "2":
                    // TODO: 15 November 2018 DUMMY ORDER CARD FROM OTHER USER
                    TAPUserModel expert = new TAPUserModel(vm.getOtherUserID(), "", vm.getRoom().getRoomName(), vm.getRoom().getRoomImage(), "", "", "08123456789", null, System.currentTimeMillis(), System.currentTimeMillis(), false, System.currentTimeMillis(), System.currentTimeMillis());
                    TAPOrderModel order = new TAPOrderModel();
                    TAPImageURL dummyThumb = new TAPImageURL(
                            "https://images.pexels.com/photos/722421/pexels-photo-722421.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                            "https://images.pexels.com/photos/722421/pexels-photo-722421.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    TAPProductModel dummyProduct = new TAPProductModel(
                            "Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product Dummy Product",
                            dummyThumb,
                            new TAPPairIdNameModel("", ""),
                            "0",
                            6175927328506457372L,
                            "");
                    dummyProduct.setDescription("Vestibulum rutrum quam vitae fringilla tincidunt. Suspendisse nec tortor urna. Ut laoreet sodales nisi, quis iaculis ullaadadas");
                    dummyProduct.setRating(4f);
                    List<TAPProductModel> dummyProductList = new ArrayList<>();
                    dummyProductList.add(dummyProduct);
                    TAPRecipientModel recipient = new TAPRecipientModel();
                    recipient.setRecipientID(Integer.valueOf(vm.getMyUserModel().getUserID()));
                    recipient.setRecipientName(vm.getMyUserModel().getName());
                    recipient.setPhoneNumber(vm.getMyUserModel().getPhoneNumber());
                    recipient.setAddress("Jl. Dempo 1 no. 51");
                    recipient.setPostalCode("12120");
                    recipient.setRegion("Kebayoran Baru");
                    recipient.setCity("Jakarta Selatan");
                    recipient.setProvince("DKI Jakarta");
                    TAPCourierModel courier = new TAPCourierModel();
                    courier.setCourierType("Same Day");
                    courier.setCourierCost(20000L);
                    courier.setCourierLogo(dummyThumb);
                    order.setCustomer(vm.getMyUserModel());
                    order.setSeller(expert);
                    order.setProducts(dummyProductList);
                    order.setRecipient(recipient);
                    order.setCourier(courier);
                    order.setOrderID("MD-987654321");
                    order.setOrderName("Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order Dummy Order");
                    order.setNotes("Mauris non tempor quam, et lacinia sapien. Mauris non tempor quam, et lacinia sapien. Mauris non tempor quam, et lacinia sapien.");
                    order.setOrderStatus(0);
                    order.setOrderTime(System.currentTimeMillis());
                    order.setAdditionalCost(55555L);
                    order.setDiscount(333333L);
                    order.setTotalPrice(7777777L);
                    String dummyOrderString = TAPUtils.getInstance().toJsonString(order);
                    TAPMessageModel orderCard = TAPMessageModel.Builder(
                            dummyOrderString,
                            vm.getRoom(),
                            TYPE_ORDER_CARD,
                            System.currentTimeMillis(),
                            expert,
                            vm.getMyUserModel().getUserID());
                    sendCustomKeyboardMessage(orderCard);
                    break;
                case "3":
                    // TODO: 12 November 2018 DUMMY PRODUCT LIST
                    TAPImageURL dummyThumb3 = new TAPImageURL(
                            "https://images.pexels.com/photos/1029919/pexels-photo-1029919.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                            "https://images.pexels.com/photos/1029919/pexels-photo-1029919.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    TAPProductModel dummyProduct3 = new TAPProductModel(
                            "Dummy Product",
                            dummyThumb3,
                            new TAPPairIdNameModel("", ""),
                            "0",
                            99999999L,
                            "");
                    dummyProduct3.setDescription("Vestibulum rutrum quam vitae fringilla tincidunt. Suspendisse nec tortor urna. Ut laoreet sodales nisi, quis iaculis ullaadadas");
                    dummyProduct3.setRating(4f);
                    List<TAPProductModel> dummyProductList3 = new ArrayList<>();
                    dummyProductList3.add(dummyProduct3);
                    dummyProductList3.add(dummyProduct3);
                    dummyProductList3.add(dummyProduct3);
                    dummyProductList3.add(dummyProduct3);
                    String dummyProductListString = TAPUtils.getInstance().toJsonString(dummyProductList3);
                    TAPMessageModel services = TAPMessageModel.Builder(
                            dummyProductListString,
                            vm.getRoom(),
                            TYPE_PRODUCT,
                            System.currentTimeMillis(),
                            vm.getMyUserModel(),
                            vm.getOtherUserID());
                    sendCustomKeyboardMessage(services);
                    break;
                case "4":
                    // TODO: 15 November 2018 DUMMY ORDER CARD
                    TAPUserModel customer = new TAPUserModel(vm.getOtherUserID(), "", vm.getRoom().getRoomName(), vm.getRoom().getRoomImage(), "", "", "08123456789", null, System.currentTimeMillis(), System.currentTimeMillis(), false, System.currentTimeMillis(), System.currentTimeMillis());
                    TAPOrderModel order4 = new TAPOrderModel();
                    TAPImageURL dummyThumb4 = new TAPImageURL(
                            "https://images.pexels.com/photos/1029919/pexels-photo-1029919.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                            "https://images.pexels.com/photos/1029919/pexels-photo-1029919.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    TAPProductModel dummyProduct4 = new TAPProductModel(
                            "Dummy Product",
                            dummyThumb4,
                            new TAPPairIdNameModel("", ""),
                            "0",
                            99999999L,
                            "");
                    dummyProduct4.setDescription("Vestibulum rutrum quam vitae fringilla tincidunt. Suspendisse nec tortor urna. Ut laoreet sodales nisi, quis iaculis ullaadadas");
                    dummyProduct4.setRating(4f);
                    List<TAPProductModel> dummyProductList4 = new ArrayList<>();
                    dummyProductList4.add(dummyProduct4);
                    TAPRecipientModel recipient4 = new TAPRecipientModel();
                    recipient4.setRecipientID(Integer.valueOf(vm.getOtherUserID()));
                    recipient4.setRecipientName(customer.getName());
                    recipient4.setPhoneNumber(customer.getPhoneNumber());
                    recipient4.setAddress("Jl. Kyai Maja no 25C");
                    recipient4.setPostalCode("12120");
                    recipient4.setRegion("Kebayoran Baru");
                    recipient4.setCity("Jakarta Selatan");
                    recipient4.setProvince("DKI Jakarta");
                    TAPCourierModel courier4 = new TAPCourierModel();
                    courier4.setCourierType("Instant Courier");
                    courier4.setCourierCost(30000L);
                    courier4.setCourierLogo(dummyThumb4);
                    order4.setCustomer(customer);
                    order4.setSeller(vm.getMyUserModel());
                    order4.setProducts(dummyProductList4);
                    order4.setRecipient(recipient4);
                    order4.setCourier(courier4);
                    order4.setOrderID("MD-123456789");
                    order4.setOrderName("Dummy Order");
                    order4.setNotes("Mauris non tempor quam, et lacinia sapien. Mauris non tempor quam, et lacinia sapien. Mauris non tempor quam, et lacinia sapien.");
                    order4.setOrderStatus(0);
                    order4.setOrderTime(System.currentTimeMillis());
                    order4.setAdditionalCost(88888L);
                    order4.setDiscount(1111111L);
                    order4.setTotalPrice(9999999L);
                    String dummyOrderString4 = TAPUtils.getInstance().toJsonString(order4);
                    TAPMessageModel orderCard4 = TAPMessageModel.Builder(
                            dummyOrderString4,
                            vm.getRoom(),
                            TYPE_ORDER_CARD,
                            System.currentTimeMillis(),
                            vm.getMyUserModel(),
                            vm.getOtherUserID());
                    sendCustomKeyboardMessage(orderCard4);
                    break;
            }
        }

        private void sendCustomKeyboardMessage(TAPMessageModel message) {
            hideKeyboards();
            addNewMessage(message);
        }
    };

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0 && s.toString().trim().length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_active);
            } else if (s.length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                ivButtonSend.setImageResource(R.drawable.tap_ic_send_inactive);
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
                    addNewMessage(pendingMessage);
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
                        // TODO: 24 September 2018 CHECK ROOM TYPE, PROFILE DESCRIPTION, CHANGE HIS/HER ACCORDING TO GENDER
                        clEmptyChat.setVisibility(View.VISIBLE);
                        tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + vm.getRoom().getRoomName() + "</font></b> is an expert<br/>don't forget to check out his/her services!"));
                        tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
                        if (null != vm.getMyUserModel().getAvatarURL() && !vm.getMyUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            Glide.with(TAPChatActivity.this).load(vm.getMyUserModel().getAvatarURL().getThumbnail()).into(civMyAvatar);
                        } else {
                            civMyAvatar.setColorFilter(new PorterDuffColorFilter(TAPUtils.getInstance().getRandomColor(vm.getMyUserModel().getName()), PorterDuff.Mode.SRC_IN));
                        }
                        if (null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                            Glide.with(TAPChatActivity.this).load(vm.getRoom().getRoomImage().getThumbnail()).into(civOtherUserAvatar);
                        } else {
                            civOtherUserAvatar.setColorFilter(new PorterDuffColorFilter(TAPUtils.getInstance().getRandomColor(vm.getRoom().getRoomName()), PorterDuff.Mode.SRC_IN));
                        }
                        // TODO: 1 October 2018 ONLY SHOW CUSTOM KEYBOARD WHEN AVAILABLE
                        showCustomKeyboard();
                    } else {
                        // Message exists
                        vm.setMessageModels(models);
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
                    if (0 < vm.getMessageModels().size()) {
                    /* Call Message List API
                    Kalau misalnya lastUpdatednya ga ada di preference last updated dan min creatednya sama
                    Kalau misalnya ada di preference last updatednya ambil dari yang ada di preference (min created ambil dari getCreated)
                    kalau last updated dari getUpdated */
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
                    if (rvMessageList.getVisibility() != View.VISIBLE)
                        rvMessageList.setVisibility(View.VISIBLE);
                    if (state == STATE.DONE) updateMessageDecoration();
                });
            }

            if (NUM_OF_ITEM > entities.size()) state = STATE.DONE;
            else {
                rvMessageList.addOnScrollListener(endlessScrollListener);
                state = STATE.LOADED;
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
            vm.setCameraImageUri(TAPUtils.getInstance().takePicture(TAPChatActivity.this, SEND_IMAGE_FROM_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            TAPUtils.getInstance().pickImageFromGallery(TAPChatActivity.this, SEND_IMAGE_FROM_GALLERY);
        }
    };

    private TapTalkNetworkInterface networkListener = () -> {
        if (vm.isInitialAPICallFinished()) {
            callApiAfter();
        }
    };

    private TapDefaultDataView<TAPGetMessageListbyRoomResponse> messageAfterView = new TapDefaultDataView<TAPGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(TAPGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageAfterModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageAfterModels = new ArrayList<>();
            for (TAPMessageModel message : response.getMessages()) {
                try {
                    TAPMessageModel temp = TAPMessageModel.BuilderDecrypt(message);
                    addAfterTextMessage(temp, messageAfterModels);
                    new Thread(() -> {
                        responseMessages.add(TAPChatManager.getInstance().convertToEntity(temp));

                        //ini buat update last update timestamp yang ada di preference
                        //ini di taruh di new Thread biar ga bkin scrollingnya lag
                        if (null != temp.getUpdated() &&
                                TAPDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < temp.getUpdated()) {
                            TAPDataManager.getInstance().saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), temp.getUpdated());
                        }
                    }).start();
                } catch (GeneralSecurityException e) {
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
    private TapDefaultDataView<TAPGetMessageListbyRoomResponse> messageBeforeView = new TapDefaultDataView<TAPGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(TAPGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>();
            for (TAPMessageModel message : response.getMessages()) {
                try {
                    TAPMessageModel temp = TAPMessageModel.BuilderDecrypt(message);
                    addBeforeTextMessage(temp, messageBeforeModels);

                    new Thread(() -> responseMessages.add(TAPChatManager.getInstance().convertToEntity(temp))).start();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, DESCENDING);

            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE && 0 < messageBeforeModels.size()) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);

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

    //message before yang di panggil pas pagination db balikin data di bawah limit
    private TapDefaultDataView<TAPGetMessageListbyRoomResponse> messageBeforeViewPaging = new TapDefaultDataView<TAPGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(TAPGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<TAPMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>();
            for (TAPMessageModel message : response.getMessages()) {
                try {
                    TAPMessageModel temp = TAPMessageModel.BuilderDecrypt(message);
                    addBeforeTextMessage(temp, messageBeforeModels);

                    new Thread(() -> responseMessages.add(TAPChatManager.getInstance().convertToEntity(temp))).start();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            //ini ngecek kalau misalnya balikan apinya itu perPagenya > pageCount brati berenti ga usah pagination lagi (State.DONE)
            //selain itu paginationnya bisa lanjut lagi
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;
            if (state == STATE.DONE) updateMessageDecoration();

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, DESCENDING);
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
