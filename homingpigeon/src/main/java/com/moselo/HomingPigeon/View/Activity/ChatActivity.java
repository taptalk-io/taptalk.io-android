package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.EndlessScrollListener;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.MessageAdapter;
import com.moselo.HomingPigeon.View.BottomSheet.AttachmentBottomSheet;
import com.moselo.HomingPigeon.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_COLOR;

public class ChatActivity extends BaseActivity implements HomingPigeonChatListener {

    private String TAG = ChatActivity.class.getSimpleName();

    // View
    private RecyclerView rvMessageList, rvCustomKeyboard;
    private FrameLayout flMessageList;
    private ConstraintLayout clEmptyChat, clChatInput;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivButtonChatMenu, ivButtonAttach, ivButtonSend, ivToBottom;
    private CircleImageView civRoomImage, civMyAvatar, civOtherUserAvatar;
    private TextView tvRoomName, tvRoomStatus, tvChatEmptyGuide, tvProfileDescription, tvBadgeUnread;
    private View vStatusBadge;

    // RecyclerView
    private MessageAdapter adapter;
    private LinearLayoutManager llm;

    // RoomDatabase
    private ChatViewModel vm;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstance().removeChatListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setActiveRoom(vm.getRoomID());
        etChat.setText(ChatManager.getInstance().getMessageFromDraft());
    }

    @Override
    protected void onPause() {
        super.onPause();
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) ChatManager.getInstance().saveMessageToDraft(draft);
        ChatManager.getInstance().setActiveRoom("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChatManager.getInstance().saveUnsentMessage();
        ChatManager.getInstance().deleteActiveRoom();
    }

    @Override
    public void onReceiveMessageInActiveRoom(final MessageModel message) {
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
        Log.e(TAG, "onSendTextMessage: " + Utils.getInstance().toJsonString(message));
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
        runOnUiThread(() -> adapter.notifyItemRangeChanged(0, adapter.getItemCount()));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Clear EditText focus when clicking outside chat input
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (null != v && v instanceof EditText) {
                Rect outRect = new Rect();
                clChatInput.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    Utils.getInstance().dismissKeyboard(ChatActivity.this);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ChatViewModel.class);
        vm.setRoomID(getIntent().getStringExtra(DefaultConstant.K_ROOM_ID));
        vm.setMyUserModel(DataManager.getInstance().getActiveUser(this));
        //vm.setPendingMessages(ChatManager.getInstance().getMessageQueueInActiveRoom());
        vm.getMessageEntities(vm.getRoomID(), this::loadMessageFromDatabase);
    }

    @Override
    protected void initView() {
        flMessageList = findViewById(R.id.fl_message_list);
        clEmptyChat = findViewById(R.id.cl_empty_chat);
        clChatInput = findViewById(R.id.cl_chat_input);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivRoomIcon = findViewById(R.id.iv_room_icon);
        ivButtonChatMenu = findViewById(R.id.iv_chat_menu);
        ivButtonAttach = findViewById(R.id.iv_attach);
        ivButtonSend = findViewById(R.id.iv_send);
        ivToBottom = findViewById(R.id.iv_to_bottom);
        civRoomImage = findViewById(R.id.civ_room_image);
        civMyAvatar = findViewById(R.id.civ_my_avatar);
        civOtherUserAvatar = findViewById(R.id.civ_other_user_avatar);
        tvRoomName = findViewById(R.id.tv_room_name);
        tvRoomStatus = findViewById(R.id.tv_room_status);
        tvChatEmptyGuide = findViewById(R.id.tv_chat_empty_guide);
        tvProfileDescription = findViewById(R.id.tv_profile_description);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        rvMessageList = findViewById(R.id.rv_message_list);
        rvCustomKeyboard = findViewById(R.id.rv_custom_keyboard);
        etChat = findViewById(R.id.et_chat);
        tvBadgeUnread = findViewById(R.id.tv_badge_unread);

        tvRoomName.setText(getIntent().getStringExtra(ROOM_NAME));

        // TODO: 24 September 2018 LOAD ROOM IMAGE
        civRoomImage.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));

        // TODO: 24 September 2018 UPDATE ROOM STATUS
        tvRoomStatus.setText("User Status");

        adapter = new MessageAdapter(this, this);
        adapter.setMessages(vm.getMessageModels());
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        llm.setStackFromEnd(true);
        rvMessageList.setAdapter(adapter);
        rvMessageList.setLayoutManager(llm);
        rvMessageList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        if (vm.getMessageModels().size() > 0) {
            clEmptyChat.setVisibility(View.GONE);
        } else {
            // Chat is empty
            // TODO: 24 September 2018 CHECK ROOM TYPE, LOAD USER AVATARS, PROFILE DESCRIPTION
            tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + getIntent().getStringExtra(ROOM_NAME) + "</font></b> is an expert\ndon't forget to check out his/her services!"));
            tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
            civOtherUserAvatar.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));
        }

        final HomingPigeonDatabaseListener scrollChatListener = this::loadMessageFromDatabase;

        rvMessageList.addOnScrollListener(new EndlessScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < adapter.getItemCount()) {
                    vm.getMessageByTimestamp(vm.getRoomID(), scrollChatListener, vm.getLastTimestamp());
                    state = STATE.WORKING;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvMessageList.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (llm.findFirstVisibleItemPosition() == 0) {
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

        etChat.setOnEditorActionListener(chatEditorActionListener);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonChatMenu.setOnClickListener(v -> {
            // TODO: 24 September 2018 TOGGLE CUSTOM KEYBOARD
        });
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> attemptSend());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
    }

    private void initHelper() {
        ChatManager.getInstance().addChatListener(this);
    }

    private void loadMessageFromDatabase(List<MessageEntity> entities) {
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
            if (null != adapter && 0 == adapter.getItems().size()) {
                // First load
                adapter.setMessages(models);
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
            } else if (null != adapter) {
                runOnUiThread(() -> adapter.addMessage(models));
                state = 0 == entities.size() ? STATE.DONE : STATE.LOADED;
                if (rvMessageList.getVisibility() != View.VISIBLE) rvMessageList.setVisibility(View.VISIBLE);
            }
        });
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
            String newID = newMessage.getLocalID();
            boolean ownMessage = newMessage.getUser().getUserID().equals(DataManager
                    .getInstance().getActiveUser(ChatActivity.this).getUserID());
            if (vm.getMessagePointer().containsKey(newID)) {
                vm.updateMessagePointer(newMessage);
                adapter.notifyItemChanged(adapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (vm.isOnBottom() || ownMessage) {
                // Scroll recycler to bottom
                adapter.addMessage(newMessage);
                rvMessageList.scrollToPosition(0);
            } else if (!ownMessage) {
                // Show unread badge
                adapter.addMessage(newMessage);
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

    private void openAttachMenu() {
        AttachmentBottomSheet attachBottomSheet = new AttachmentBottomSheet();
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    // Send message on IME action
    private TextView.OnEditorActionListener chatEditorActionListener = (v, actionId, event) -> {
        if (actionId == R.id.send || actionId == EditorInfo.IME_NULL) {
            attemptSend();
            return true;
        }
        return false;
    };

    // Show/hide keyboard menu
    private View.OnFocusChangeListener chatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ivButtonChatMenu.setVisibility(View.GONE);
            } else {
                ivButtonChatMenu.setVisibility(View.VISIBLE);
            }
        }
    };
}
