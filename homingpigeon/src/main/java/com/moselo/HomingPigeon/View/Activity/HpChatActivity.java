package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.HomingPigeonDialog;
import com.moselo.HomingPigeon.Helper.HpChatRecyclerView;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;
import com.moselo.HomingPigeon.Helper.HpEndlessScrollListener;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.HpVerticalDecoration;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Helper.SwipeBackLayout.SwipeBackLayout;
import com.moselo.HomingPigeon.Listener.HpChatListener;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Listener.HpSocketListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpCustomKeyboardModel;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetMessageListbyRoomResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpCustomKeyboardAdapter;
import com.moselo.HomingPigeon.View.Adapter.HpMessageAdapter;
import com.moselo.HomingPigeon.View.BottomSheet.HpAttachmentBottomSheet;
import com.moselo.HomingPigeon.ViewModel.HpChatViewModel;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_COLOR;

public class HpChatActivity extends HpBaseChatActivity {

    private String TAG = HpChatActivity.class.getSimpleName();

    // View
    private SwipeBackLayout sblChat;
    private HpChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private FrameLayout flMessageList;
    private ConstraintLayout clEmptyChat, clChatComposer;
    private EditText etChat;
    private ImageView ivButtonBack, ivRoomIcon, ivButtonChatMenu, ivButtonAttach, ivButtonSend, ivToBottom;
    private CircleImageView civRoomImage, civMyAvatar, civOtherUserAvatar;
    private TextView tvRoomName, tvRoomStatus, tvChatEmptyGuide, tvProfileDescription, tvBadgeUnread;
    private View vStatusBadge;

    // RecyclerView
    private HpMessageAdapter hpMessageAdapter;
    private HpCustomKeyboardAdapter hpCustomKeyboardAdapter;
    private LinearLayoutManager messageLayoutManager;

    // RoomDatabase
    private HpChatViewModel vm;

    private HpSocketListener socketListener;

    //enum Scrolling
    private enum STATE {
        WORKING, LOADED, DONE
    }

    private STATE state = STATE.LOADED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_chat);

        initViewModel();
        initView();
        initHelper();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HpChatManager.getInstance().removeChatListener(chatListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HpChatManager.getInstance().setActiveRoom(vm.getRoom());
        etChat.setText(HpChatManager.getInstance().getMessageFromDraft());
    }

    @Override
    protected void onPause() {
        super.onPause();
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) HpChatManager.getInstance().saveMessageToDraft(draft);
        HpChatManager.getInstance().deleteActiveRoom();
    }

    @Override
    public void onBackPressed() {
        HpChatManager.getInstance().putUnsentMessageToList();
        super.onBackPressed();
    }

    HpChatListener chatListener = new HpChatListener() {
        @Override
        public void onReceiveMessageInActiveRoom(HpMessageModel message) {
            addNewTextMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(HpMessageModel message) {
            // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
            addNewTextMessage(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(HpMessageModel message) {
            // TODO: 06/09/18 HARUS DICEK LAGI NANTI SETELAH BISA
            addNewTextMessage(message);
        }

        @Override
        public void onReceiveMessageInOtherRoom(HpMessageModel message) {
            super.onReceiveMessageInOtherRoom(message);
        }

        @Override
        public void onUpdateMessageInOtherRoom(HpMessageModel message) {
            super.onUpdateMessageInOtherRoom(message);
        }

        @Override
        public void onDeleteMessageInOtherRoom(HpMessageModel message) {
            super.onDeleteMessageInOtherRoom(message);
        }

        @Override
        public void onSendTextMessage(HpMessageModel message) {
            addNewTextMessage(message);
            vm.addMessagePointer(message);
        }

        @Override
        public void onRetrySendMessage(HpMessageModel message) {
            vm.delete(message.getLocalID());
            HpChatManager.getInstance().sendTextMessage(message.getBody());
        }

        @Override
        public void onSendFailed(HpMessageModel message) {
            vm.updateMessagePointer(message);
            vm.removeMessagePointer(message.getLocalID());
            runOnUiThread(() -> hpMessageAdapter.notifyItemRangeChanged(0, hpMessageAdapter.getItemCount()));
        }

        @Override
        public void onMessageClicked(HpMessageModel message, boolean isExpanded) {
            super.onMessageClicked(message, isExpanded);
        }
    };

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpChatViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(HpDefaultConstant.K_ROOM));
        vm.setMyUserModel(HpDataManager.getInstance().getActiveUser(this));
        //vm.setPendingMessages(HpChatManager.getInstance().getMessageQueueInActiveRoom());
    }

    @Override
    protected void initView() {
        sblChat = getSwipeBackLayout();
        flMessageList = (FrameLayout) findViewById(R.id.fl_message_list);
        clEmptyChat = (ConstraintLayout) findViewById(R.id.cl_empty_chat);
        clChatComposer = (ConstraintLayout) findViewById(R.id.cl_chat_composer);
        ivButtonBack = (ImageView) findViewById(R.id.iv_button_back);
        ivRoomIcon = (ImageView) findViewById(R.id.iv_room_icon);
        ivButtonChatMenu = (ImageView) findViewById(R.id.iv_chat_menu);
        ivButtonAttach = (ImageView) findViewById(R.id.iv_attach);
        ivButtonSend = (ImageView) findViewById(R.id.iv_send);
        ivToBottom = (ImageView) findViewById(R.id.iv_to_bottom);
        civRoomImage = (CircleImageView) findViewById(R.id.civ_room_image);
        civMyAvatar = (CircleImageView) findViewById(R.id.civ_my_avatar);
        civOtherUserAvatar = (CircleImageView) findViewById(R.id.civ_other_user_avatar);
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomStatus = (TextView) findViewById(R.id.tv_room_status);
        tvChatEmptyGuide = (TextView) findViewById(R.id.tv_chat_empty_guide);
        tvProfileDescription = (TextView) findViewById(R.id.tv_profile_description);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        rvMessageList = (HpChatRecyclerView) findViewById(R.id.rv_message_list);
        rvCustomKeyboard = (RecyclerView) findViewById(R.id.rv_custom_keyboard);
        etChat = (EditText) findViewById(R.id.et_chat);
        tvBadgeUnread = (TextView) findViewById(R.id.tv_badge_unread);

        getWindow().setBackgroundDrawable(null);

        tvRoomName.setText(getIntent().getStringExtra(ROOM_NAME));

        // TODO: 24 September 2018 LOAD ROOM IMAGE
        civRoomImage.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));

        // TODO: 24 September 2018 UPDATE ROOM STATUS
        tvRoomStatus.setText("User Status");

        hpMessageAdapter = new HpMessageAdapter(this, chatListener);
        hpMessageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.setAdapter(hpMessageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvMessageList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        // TODO: 25 September 2018 CHANGE MENU ACCORDING TO USER ROLES
        List<HpCustomKeyboardModel> customKeyboardMenus = new ArrayList<>();
        customKeyboardMenus.add(new HpCustomKeyboardModel(HpCustomKeyboardModel.Type.SEE_PRICE_LIST));
        customKeyboardMenus.add(new HpCustomKeyboardModel(HpCustomKeyboardModel.Type.READ_EXPERT_NOTES));
        customKeyboardMenus.add(new HpCustomKeyboardModel(HpCustomKeyboardModel.Type.SEND_SERVICES));
        customKeyboardMenus.add(new HpCustomKeyboardModel(HpCustomKeyboardModel.Type.CREATE_ORDER_CARD));
        hpCustomKeyboardAdapter = new HpCustomKeyboardAdapter(customKeyboardMenus);
        rvCustomKeyboard.setAdapter(hpCustomKeyboardAdapter);
        rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
        rvMessageList.addOnScrollListener(new HpEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < hpMessageAdapter.getItemCount()) {
                    vm.getMessageByTimestamp(vm.getRoom().getRoomID(), dbListener, vm.getLastTimestamp());
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

        sblChat.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> attemptSend());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
    }

    private void initHelper() {
        HpChatManager.getInstance().addChatListener(chatListener);
    }

    private void initListener() {
        socketListener = new HpSocketListener() {
            @Override
            public void onSocketConnected() {
                if (!vm.isInitialAPICallFinished()) {
                    // Call Message List API
                    if (vm.getMessageModels().size() > 0) {
                        HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(), vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(), vm.getMessageModels().get(0).getUpdated(), messageView);
                    } else {
                        HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(), (long) 0, (long) 0, messageView);
                    }
                }
            }
        };
        HpConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private void updateMessageDecoration() {
        if (rvMessageList.getItemDecorationCount() > 0) {
            rvMessageList.removeItemDecorationAt(0);
        }
        rvMessageList.addItemDecoration(new HpVerticalDecoration(HpUtils.getInstance().dpToPx(10), 0, hpMessageAdapter.getItemCount() - 1));
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message.trim())) {
            etChat.setText("");
            hpMessageAdapter.shrinkExpandedBubble();
            HpChatManager.getInstance().sendTextMessage(message);
            rvMessageList.scrollToPosition(0);
        }
    }

    private void addNewTextMessage(final HpMessageModel newMessage) {
        runOnUiThread(() -> {
            if (clEmptyChat.getVisibility() == View.VISIBLE) {
                clEmptyChat.setVisibility(View.GONE);
                flMessageList.setVisibility(View.VISIBLE);
            }
            // Replace pending message with new message
            String newID = newMessage.getLocalID();
            boolean ownMessage = newMessage.getUser().getUserID().equals(HpDataManager
                    .getInstance().getActiveUser(HpChatActivity.this).getUserID());
            if (vm.getMessagePointer().containsKey(newID)) {
                vm.updateMessagePointer(newMessage);
                hpMessageAdapter.notifyItemChanged(hpMessageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (vm.isOnBottom() || ownMessage) {
                // Scroll recycler to bottom
                hpMessageAdapter.addMessage(newMessage);
                rvMessageList.scrollToPosition(0);
            } else if (!ownMessage) {
                // Show unread badge
                hpMessageAdapter.addMessage(newMessage);
                vm.setUnreadCount(vm.getUnreadCount() + 1);
                tvBadgeUnread.setVisibility(View.VISIBLE);
                tvBadgeUnread.setText(vm.getUnreadCount() + "");
            }
            // Remove pending message
            if (ownMessage) {
                vm.removeMessagePointer(newID);
            }
            updateMessageDecoration();
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
            showNormalKeyboard();
        } else {
            showCustomKeyboard();
        }
    }

    private void showNormalKeyboard() {
        rvCustomKeyboard.setVisibility(View.GONE);
        ivButtonChatMenu.setImageResource(R.drawable.hp_ic_chatmenu_hamburger);
        etChat.requestFocus();
    }

    private void showCustomKeyboard() {
        HpUtils.getInstance().dismissKeyboard(this);
        etChat.clearFocus();
        new Handler().postDelayed(() -> {
            rvCustomKeyboard.setVisibility(View.VISIBLE);
            ivButtonChatMenu.setImageResource(R.drawable.hp_ic_chatmenu_keyboard);
        }, 150L);
    }

    private void openAttachMenu() {
        HpUtils.getInstance().dismissKeyboard(this);
        HpAttachmentBottomSheet attachBottomSheet = new HpAttachmentBottomSheet();
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                ivButtonChatMenu.setVisibility(View.GONE);
                if (s.toString().trim().length() > 0) {
                    ivButtonSend.setImageResource(R.drawable.hp_ic_send_active);
                } else {
                    ivButtonSend.setImageResource(R.drawable.hp_ic_send_inactive);
                }
            } else {
                ivButtonChatMenu.setVisibility(View.VISIBLE);
                ivButtonSend.setImageResource(R.drawable.hp_ic_send_inactive);
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
                ivButtonChatMenu.setImageResource(R.drawable.hp_ic_chatmenu_hamburger);
                HpUtils.getInstance().showKeyboard(HpChatActivity.this, etChat);
            }
        }
    };

    private HpDatabaseListener dbListener = new HpDatabaseListener() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            final List<HpMessageModel> models = new ArrayList<>();
            for (HpMessageEntity entity : entities) {
                HpMessageModel model = HpChatManager.getInstance().convertToModel(entity);
                models.add(model);
                vm.addMessagePointer(model);
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
            }

            runOnUiThread(() -> {
                if (null != hpMessageAdapter && 0 == hpMessageAdapter.getItems().size()) {
                    // First load
                    hpMessageAdapter.setMessages(models);
                    if (models.size() == 0) {
                        // Chat is empty
                        // TODO: 24 September 2018 CHECK ROOM TYPE, LOAD USER AVATARS, PROFILE DESCRIPTION, CHANGE HIS/HER ACCORDING TO GENDER
                        clEmptyChat.setVisibility(View.VISIBLE);
                        tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + getIntent().getStringExtra(ROOM_NAME) + "</font></b> is an expert<br/>don't forget to check out his/her services!"));
                        tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
                        civOtherUserAvatar.setImageTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));
                        // TODO: 1 October 2018 ONLY SHOW CUSTOM KEYBOARD WHEN AVAILABLE
                        showCustomKeyboard();
                    } else {
                        // Message exists
                        vm.addMessageModels(models);
                        flMessageList.setVisibility(View.VISIBLE);
                    }
                    rvMessageList.scrollToPosition(0);
                    updateMessageDecoration();
                    // Call Message List API
                    if (vm.getMessageModels().size() > 0) {
                        HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(), models.get(models.size() - 1).getCreated(), models.get(0).getUpdated(), messageView);
                    } else {
                        HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(), (long) 0, (long) 0, messageView);
                    }
                } else if (null != hpMessageAdapter) {
                    vm.addMessageModels(models);
                    flMessageList.setVisibility(View.VISIBLE);
                    hpMessageAdapter.notifyDataSetChanged();
                    state = 0 == entities.size() ? STATE.DONE : STATE.LOADED;
                    if (rvMessageList.getVisibility() != View.VISIBLE)
                        rvMessageList.setVisibility(View.VISIBLE);
                    if (state == STATE.DONE) updateMessageDecoration();
                }
            });
        }
    };

    private HpDefaultDataView<HpGetMessageListbyRoomResponse> messageView = new HpDefaultDataView<HpGetMessageListbyRoomResponse>() {
        @Override
        public void onSuccess(HpGetMessageListbyRoomResponse response) {
            Log.e(TAG, "onSuccess: " + HpUtils.getInstance().toJsonString(response));
            vm.setInitialAPICallFinished(true);

            List<HpMessageEntity> responseMessages = new ArrayList<>();
            for (HpMessageModel message : response.getMessages()) {
                try {
                    HpMessageModel temp = HpMessageModel.BuilderDecrypt(message);
                    responseMessages.add(HpChatManager.getInstance().convertToEntity(temp));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            HpDataManager.getInstance().insertToDatabase(responseMessages, false, new HpDatabaseListener() {
                @Override
                public void onInsertFinished() {
                    Log.e(TAG, "onInsertFinished: ");
                    vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
                    if (null != responseMessages.get(0) && HpDataManager.getInstance().getLastUpdatedMessageTimestamp(HpChatActivity.this) < responseMessages.get(0).getUpdated()) {
                        HpDataManager.getInstance().saveLastUpdatedMessageTimestamp(HpChatActivity.this, responseMessages.get(0).getUpdated());
                        Log.e(TAG, "onInsertFinished - last updated: " + HpDataManager.getInstance().getLastUpdatedMessageTimestamp(HpChatActivity.this));
                    }
                }
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            if (BuildConfig.DEBUG) {
                new HomingPigeonDialog.Builder(HpChatActivity.this)
                        .setTitle("Error")
                        .setMessage(error.getMessage())
                        .show();
            }
            Log.e(TAG, "onError: " + error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            if (BuildConfig.DEBUG) {
                new HomingPigeonDialog.Builder(HpChatActivity.this)
                        .setTitle("Error")
                        .setMessage(errorMessage)
                        .show();
            }
            Log.e(TAG, "onError: " + errorMessage);
        }
    };
}
