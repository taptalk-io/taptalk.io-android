package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Helper.ChatRecyclerView;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.EndlessScrollListener;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Helper.VerticalDecoration;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.NetworkStateManager;
import com.moselo.HomingPigeon.Model.CustomKeyboardModel;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.CustomKeyboardAdapter;
import com.moselo.HomingPigeon.View.Adapter.MessageAdapter;
import com.moselo.HomingPigeon.View.BottomSheet.AttachmentBottomSheet;
import com.moselo.HomingPigeon.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_COLOR;

public class ChatActivity extends BaseActivity implements HomingPigeonChatListener, HomingPigeonDatabaseListener {

    private String TAG = ChatActivity.class.getSimpleName();

    // View
    private ChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private FrameLayout flMessageList;
    private LinearLayout llConnectionStatus;
    private ConstraintLayout clEmptyChat, clChatInput;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivConnectionStatus, ivButtonChatMenu, ivButtonAttach, ivButtonSend, ivToBottom;
    private CircleImageView civRoomImage, civMyAvatar, civOtherUserAvatar;
    private TextView tvRoomName, tvRoomStatus, tvConnectionStatus, tvChatEmptyGuide, tvProfileDescription, tvBadgeUnread;
    private View vStatusBadge;
    private ProgressBar pbConnecting;

    // RecyclerView
    private MessageAdapter messageAdapter;
    private CustomKeyboardAdapter customKeyboardAdapter;
    private LinearLayoutManager messageLayoutManager;

    // RoomDatabase
    private ChatViewModel vm;

    @Override
    public void onSelectFinished(List<MessageEntity> entities) {
        final List<MessageModel> models = new ArrayList<>();
        for (MessageEntity entity : entities) {
            MessageModel model = ChatManager.getInstance().convertToModel(entity);
            models.add(model);
            vm.addMessagePointer(model);
        }
        vm.setMessageModels(models);

        if (vm.getMessageModels().size() > 0) {
            vm.setLastTimestamp(models.get(vm.getMessageModels().size() - 1).getCreated());
        }

        runOnUiThread(() -> {
            if (null != messageAdapter && 0 == messageAdapter.getItems().size()) {
                // First load
                messageAdapter.setMessages(models);
                rvMessageList.scrollToPosition(0);
                if (vm.getMessageModels().size() == 0) {
                    // Chat is empty
                    // TODO: 24 September 2018 CHECK ROOM TYPE, LOAD USER AVATARS, PROFILE DESCRIPTION, CHANGE HIS/HER ACCORDING TO GENDER
                    clEmptyChat.setVisibility(View.VISIBLE);
                    tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + getIntent().getStringExtra(ROOM_NAME) + "</font></b> is an expert<br/>don't forget to check out his/her services!"));
                    tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
                    civOtherUserAvatar.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));
                } else {
                    // Message exists
                    flMessageList.setVisibility(View.VISIBLE);
                }
            } else if (null != messageAdapter) {
                runOnUiThread(() -> messageAdapter.addMessage(models));
                state = 0 == entities.size() ? STATE.DONE : STATE.LOADED;
                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            }
        });
    }

    //enum Scrolling
    private enum STATE {
        WORKING, LOADED, DONE
    }

    private STATE state = STATE.LOADED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViewModel();
        initView();
        initHelper();
        initConnectionStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstance().removeChatListener(this);
        ConnectionManager.getInstance().removeSocketListener(socketListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setActiveRoom(vm.getRoom());
        etChat.setText(ChatManager.getInstance().getMessageFromDraft());
    }

    @Override
    protected void onPause() {
        super.onPause();
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) ChatManager.getInstance().saveMessageToDraft(draft);
        ChatManager.getInstance().setActiveRoom(null);
    }

    @Override
    public void onBackPressed() {
        ChatManager.getInstance().putUnsentMessageToList();
        ChatManager.getInstance().deleteActiveRoom();
        if (isFinishing()) Utils.getInstance().dismissKeyboard(this);
        super.onBackPressed();
    }

    @Override
    public void onReceiveMessageInActiveRoom(final MessageModel message) {
        Log.e(TAG, "onReceiveMessageInActiveRoom: " + Utils.getInstance().toJsonString(message));
        addNewTextMessage(message);
    }

    @Override
    public void onUpdateMessageInActiveRoom(MessageModel message) {
        // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
        addNewTextMessage(message);
    }

    @Override
    public void onDeleteMessageInActiveRoom(MessageModel message) {
        // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
        addNewTextMessage(message);
    }

    @Override
    public void onReceiveMessageInOtherRoom(MessageModel message) {
        // TODO: 28 August 2018 REPLACE
    }

    @Override
    public void onUpdateMessageInOtherRoom(MessageModel message) {
        // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
    }

    @Override
    public void onDeleteMessageInOtherRoom(MessageModel message) {
        // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
    }

    @Override
    public void onSendTextMessage(MessageModel message) {
        addNewTextMessage(message);
        vm.addMessagePointer(message);
    }

    @Override
    public void onRetrySendMessage(MessageModel message) {
        vm.delete(message.getLocalID());
        ChatManager.getInstance().sendTextMessage(message.getMessage());
    }

    @Override
    public void onSendFailed(final MessageModel message) {
        vm.updateMessagePointer(message);
        vm.removeMessagePointer(message.getLocalID());
        runOnUiThread(() -> messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount()));
    }

    @Override
    public void onMessageClicked(MessageModel message, boolean isExpanded) {

    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ChatViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(DefaultConstant.K_ROOM));
        vm.setMyUserModel(DataManager.getInstance().getActiveUser(this));
        //vm.setPendingMessages(ChatManager.getInstance().getMessageQueueInActiveRoom());
        vm.getMessageEntities(vm.getRoom().getRoomID(), this);
    }

    @Override
    protected void initView() {
        flMessageList = findViewById(R.id.fl_message_list);
        llConnectionStatus = findViewById(R.id.ll_connection_status);
        clEmptyChat = findViewById(R.id.cl_empty_chat);
        clChatInput = findViewById(R.id.cl_chat_input);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivRoomIcon = findViewById(R.id.iv_room_icon);
        ivConnectionStatus = findViewById(R.id.iv_connection_status);
        ivButtonChatMenu = findViewById(R.id.iv_chat_menu);
        ivButtonAttach = findViewById(R.id.iv_attach);
        ivButtonSend = findViewById(R.id.iv_send);
        ivToBottom = findViewById(R.id.iv_to_bottom);
        civRoomImage = findViewById(R.id.civ_room_image);
        civMyAvatar = findViewById(R.id.civ_my_avatar);
        civOtherUserAvatar = findViewById(R.id.civ_other_user_avatar);
        tvRoomName = findViewById(R.id.tv_room_name);
        tvRoomStatus = findViewById(R.id.tv_room_status);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        tvChatEmptyGuide = findViewById(R.id.tv_chat_empty_guide);
        tvProfileDescription = findViewById(R.id.tv_profile_description);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        rvMessageList = findViewById(R.id.rv_message_list);
        rvCustomKeyboard = findViewById(R.id.rv_custom_keyboard);
        etChat = findViewById(R.id.et_chat);
        tvBadgeUnread = findViewById(R.id.tv_badge_unread);
        pbConnecting = findViewById(R.id.pb_connecting);

        pbConnecting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        tvRoomName.setText(getIntent().getStringExtra(ROOM_NAME));

        // TODO: 24 September 2018 LOAD ROOM IMAGE
        civRoomImage.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));

        // TODO: 24 September 2018 UPDATE ROOM STATUS
        tvRoomStatus.setText("User Status");

        messageAdapter = new MessageAdapter(this, this);
        messageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.setAdapter(messageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // TODO: 25 September 2018 CHANGE MENU ACCORDING TO USER ROLES
        List<CustomKeyboardModel> customKeyboardMenus = new ArrayList<>();
        customKeyboardMenus.add(new CustomKeyboardModel(CustomKeyboardModel.Type.SEE_PRICE_LIST));
        customKeyboardMenus.add(new CustomKeyboardModel(CustomKeyboardModel.Type.READ_EXPERT_NOTES));
        customKeyboardMenus.add(new CustomKeyboardModel(CustomKeyboardModel.Type.SEND_SERVICES));
        customKeyboardMenus.add(new CustomKeyboardModel(CustomKeyboardModel.Type.CREATE_ORDER_CARD));
        customKeyboardAdapter = new CustomKeyboardAdapter(customKeyboardMenus);
        rvCustomKeyboard.setAdapter(customKeyboardAdapter);
        rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCustomKeyboard.setVisibility(View.VISIBLE);

        if (vm.getMessageModels().size() > 0) {
            clEmptyChat.setVisibility(View.GONE);
        } else {
            // Chat is empty
            // TODO: 24 September 2018 CHECK ROOM TYPE, LOAD USER AVATARS, PROFILE DESCRIPTION
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.chat_other_user_is_expert), getIntent().getStringExtra(ROOM_NAME))));
            tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
            civOtherUserAvatar.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));
        }

        final HomingPigeonDatabaseListener scrollChatListener = this;

        rvMessageList.addOnScrollListener(new EndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < messageAdapter.getItemCount()) {
                    vm.getMessageByTimestamp(vm.getRoom().getRoomID(), scrollChatListener, vm.getLastTimestamp());
                    state = STATE.WORKING;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvMessageList.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                    vm.setOnBottom(true);
                    vm.setUnreadCount(0);
                    ivToBottom.setVisibility(View.INVISIBLE);
                    tvBadgeUnread.setVisibility(View.INVISIBLE);
                } else {
                    vm.setOnBottom(false);
                    ivToBottom.setVisibility(View.VISIBLE);
                }
            });
        }

        etChat.addTextChangedListener(chatWatcher);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> attemptSend());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
    }

    private void initHelper() {
        ChatManager.getInstance().addChatListener(this);
    }

    private void updateMessageDecoration() {
        if (rvMessageList.getItemDecorationCount() > 0) {
            rvMessageList.removeItemDecorationAt(0);
        }
        rvMessageList.addItemDecoration(new VerticalDecoration(Utils.getInstance().dpToPx(10), 0, messageAdapter.getItemCount() - 1));
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message.trim())) {
            etChat.setText("");
            ChatManager.getInstance().sendTextMessage(message);
            rvMessageList.scrollToPosition(0);
        }
    }

    private void addNewTextMessage(final MessageModel newMessage) {
        runOnUiThread(() -> {
            if (clEmptyChat.getVisibility() == View.VISIBLE) {
                clEmptyChat.setVisibility(View.GONE);
                flMessageList.setVisibility(View.VISIBLE);
            }
            // Replace pending message with new message
            Log.e(TAG, "addNewTextMessage: " + Utils.getInstance().toJsonString(newMessage));
            String newID = newMessage.getLocalID();
            boolean ownMessage = newMessage.getUser().getUserID().equals(DataManager
                    .getInstance().getActiveUser(ChatActivity.this).getUserID());
            if (vm.getMessagePointer().containsKey(newID)) {
                vm.updateMessagePointer(newMessage);
                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (vm.isOnBottom() || ownMessage) {
                // Scroll recycler to bottom
                messageAdapter.addMessage(newMessage);
                rvMessageList.scrollToPosition(0);
            } else if (!ownMessage) {
                // Show unread badge
                messageAdapter.addMessage(newMessage);
                vm.setUnreadCount(vm.getUnreadCount() + 1);
                tvBadgeUnread.setVisibility(View.VISIBLE);
                tvBadgeUnread.setText(vm.getUnreadCount() + "");
            }
            // Remove pending message
            if (ownMessage) {
                vm.removeMessagePointer(newID);
            }
        });
    }

    private void scrollToBottom() {
        rvMessageList.scrollToPosition(0);
        ivToBottom.setVisibility(View.INVISIBLE);
        tvBadgeUnread.setVisibility(View.INVISIBLE);
        vm.setUnreadCount(0);
    }

    private void toggleCustomKeyboard() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            // Show normal keyboard
            rvCustomKeyboard.setVisibility(View.GONE);
            ivButtonChatMenu.setImageResource(R.drawable.ic_chatmenu_hamburger);
            etChat.requestFocus();
        } else {
            // Show Custom Keyboard
            Utils.getInstance().dismissKeyboard(this);
            etChat.clearFocus();
            new Handler().postDelayed(() -> {
                rvCustomKeyboard.setVisibility(View.VISIBLE);
                ivButtonChatMenu.setImageResource(R.drawable.ic_chatmenu_keyboard);
            }, 150L);
        }
    }

    private void openAttachMenu() {
        AttachmentBottomSheet attachBottomSheet = new AttachmentBottomSheet();
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void initConnectionStatus() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        if (!NetworkStateManager.getInstance().hasNetworkConnection(this))
            socketListener.onSocketDisconnected();
    }

    private void setStatusConnected() {
        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connected);
            tvConnectionStatus.setText(getString(R.string.connected));
            ivConnectionStatus.setImageResource(R.drawable.ic_connected_white);
            ivConnectionStatus.setVisibility(View.VISIBLE);
            pbConnecting.setVisibility(View.GONE);
            llConnectionStatus.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> llConnectionStatus.setVisibility(View.GONE), 500L);
        });
    }

    private void setStatusConnecting() {
        if (!NetworkStateManager.getInstance().hasNetworkConnection(this)) return;

        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_connecting);
            tvConnectionStatus.setText(R.string.connecting);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    private void setStatusWaitingForNetwork() {
        if (NetworkStateManager.getInstance().hasNetworkConnection(this)) return;

        runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.bg_status_offline);
            tvConnectionStatus.setText(R.string.waiting_for_network);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    // Update connection status UI
    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {

        }

        @Override
        public void onSocketConnected() {
            setStatusConnected();
        }

        @Override
        public void onSocketDisconnected() {
            setStatusWaitingForNetwork();
        }

        @Override
        public void onSocketConnecting() {
            setStatusConnecting();
        }

        @Override
        public void onSocketError() {
            setStatusWaitingForNetwork();
        }
    };

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                if (s.toString().trim().length() > 0) {
                    ivButtonSend.setImageResource(R.drawable.ic_send_active);
                } else {
                    ivButtonSend.setImageResource(R.drawable.ic_send_inactive);
                }
            } else {
                ivButtonChatMenu.setVisibility(View.VISIBLE);
                ivButtonSend.setImageResource(R.drawable.ic_send_inactive);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnFocusChangeListener chatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                rvCustomKeyboard.setVisibility(View.GONE);
                ivButtonChatMenu.setImageResource(R.drawable.ic_chatmenu_hamburger);
                Utils.getInstance().showKeyboard(ChatActivity.this, etChat);
            }
        }
    };
}
