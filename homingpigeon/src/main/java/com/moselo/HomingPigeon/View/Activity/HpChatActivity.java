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
import com.moselo.HomingPigeon.Helper.GlideApp;
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

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.NUM_OF_ITEM;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Sorting.ASCENDING;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Sorting.DESCENDING;

public class HpChatActivity extends HpBaseChatActivity {

    private String TAG = HpChatActivity.class.getSimpleName();

    //interface for swipe back
    public interface SwipeBackInterface {
        void onSwipeBack();
    }

    private SwipeBackInterface swipeInterface = () -> HpUtils.getInstance().dismissKeyboard(HpChatActivity.this);

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

    //endless scroll Listener
    HpEndlessScrollListener endlessScrollListener;

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

        if (vm.isInitialAPICallFinished())
            callApiAfter();
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
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            rvCustomKeyboard.setVisibility(View.GONE);
            ivButtonChatMenu.setImageResource(R.drawable.hp_ic_chatmenu_hamburger);
        } else {
            HpChatManager.getInstance().putUnsentMessageToList();
            super.onBackPressed();
        }
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

        @Override
        public void onOutsideClicked() {
            HpUtils.getInstance().dismissKeyboard(HpChatActivity.this);
        }
    };

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpChatViewModel.class);
        vm.setRoom(getIntent().getParcelableExtra(HpDefaultConstant.K_ROOM));
        vm.setMyUserModel(HpDataManager.getInstance().getActiveUser());
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

        tvRoomName.setText(vm.getRoom().getRoomName());

        if (null != vm.getRoom().getRoomImage()) {
            GlideApp.with(this).load(vm.getRoom().getRoomImage().getThumbnail()).into(civRoomImage);
        } else {
            // TODO: 16 October 2018 TEMPORARY
            civRoomImage.setImageTintList(ColorStateList.valueOf(Integer.parseInt(vm.getRoom().getRoomColor())));
        }

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

        //ini listener buat scroll pagination (di Init View biar kebuat cuman sekali aja)
        endlessScrollListener = new HpEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < hpMessageAdapter.getItems().size()) {
                    new Thread(() -> {
                        vm.getMessageByTimestamp(vm.getRoom().getRoomID(), dbListenerPaging, vm.getLastTimestamp());
                        state = STATE.WORKING;
                    }).start();
                }
            }
        };

        // Load items from database
        vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);

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
        sblChat.setSwipeInterface(swipeInterface);

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
                    callApiAfter();
                }
            }
        };
        HpConnectionManager.getInstance().addSocketListener(socketListener);
    }

    private void updateMessageDecoration() {
        //ini buat margin atas sma bawah chatnya (recyclerView Message List)
        if (rvMessageList.getItemDecorationCount() > 0) {
            rvMessageList.removeItemDecorationAt(0);
        }
        rvMessageList.addItemDecoration(new HpVerticalDecoration(HpUtils.getInstance().dpToPx(10), 0, hpMessageAdapter.getItemCount() - 1));
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        //ngecekin yang mau di kirim itu kosong atau nggak
        if (!TextUtils.isEmpty(message.trim())) {
            //ngereset isi edit text yang buat kirim chat
            etChat.setText("");
            //tutup bubble yang lagi expand
            hpMessageAdapter.shrinkExpandedBubble();
            HpChatManager.getInstance().sendTextMessage(message);
            //scroll to Bottom
            rvMessageList.scrollToPosition(0);
        }
    }

    private void addNewTextMessage(final HpMessageModel newMessage) {
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
        boolean ownMessage = newMessage.getUser().getUserID().equals(HpDataManager
                .getInstance().getActiveUser().getUserID());
        runOnUiThread(() -> {
            if (vm.getMessagePointer().containsKey(newID)) {
                //ngecekin dlu ada ga di hashMap
                //kalau ada brati di update dlu
                vm.updateMessagePointer(newMessage);
                hpMessageAdapter.notifyItemChanged(hpMessageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else if (vm.isOnBottom() || ownMessage) {
                //kalau ga ada di hashMap kita liat lagi itu pesan kita sndiri atau nggak kalau pesan kita brati di scroll ke bawah
                //selain itu kalau user lagi di bottom juga di scroll ke bawah
                hpMessageAdapter.addMessage(newMessage);
                // Scroll recycler to bottom
                rvMessageList.scrollToPosition(0);
            } else {
                //kalau slain itu lgsg di masukin aja sama update unreadCount
                hpMessageAdapter.addMessage(newMessage);
                // Show unread badge
                vm.setUnreadCount(vm.getUnreadCount() + 1);
                tvBadgeUnread.setVisibility(View.VISIBLE);
                tvBadgeUnread.setText(vm.getUnreadCount() + "");
            }

            updateMessageDecoration();
        });
    }

    //ngecek kalau messagenya udah ada di hash map brati udah ada di recycler view update aja
    // tapi kalau belum ada brati belom ada di recycler view jadi harus d add
    private void addBeforeTextMessage(final HpMessageModel newMessage, List<HpMessageModel> tempBeforeMessages) {
        String newID = newMessage.getLocalID();
        runOnUiThread(() -> {
            if (vm.getMessagePointer().containsKey(newID)) {
                //kalau udah ada cek posisinya dan update data yang ada di dlem modelnya
                vm.updateMessagePointer(newMessage);
                hpMessageAdapter.notifyItemChanged(hpMessageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
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
    private void addAfterTextMessage(final HpMessageModel newMessage, List<HpMessageModel> tempAfterMessages) {
        String newID = newMessage.getLocalID();
        runOnUiThread(() -> {
            if (vm.getMessagePointer().containsKey(newID)) {
                //kalau udah ada cek posisinya dan update data yang ada di dlem modelnya
                vm.updateMessagePointer(newMessage);
                hpMessageAdapter.notifyItemChanged(hpMessageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
            } else {
                new Thread(() -> {
                    //kalau belom ada masukin kedalam list dan hash map
                    tempAfterMessages.add(newMessage);
                    vm.addMessagePointer(newMessage);
                }).start();
            }
            //updateMessageDecoration();
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

    private HpDatabaseListener<HpMessageEntity> dbListener = new HpDatabaseListener<HpMessageEntity>() {
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

            if (null != hpMessageAdapter && 0 == hpMessageAdapter.getItems().size()) {
                runOnUiThread(() -> {
                    // First load
                    hpMessageAdapter.setMessages(models);
                    if (models.size() == 0) {
                        // Chat is empty
                        // TODO: 24 September 2018 CHECK ROOM TYPE, PROFILE DESCRIPTION, CHANGE HIS/HER ACCORDING TO GENDER
                        clEmptyChat.setVisibility(View.VISIBLE);
                        tvChatEmptyGuide.setText(Html.fromHtml("<b><font color='#784198'>" + vm.getRoom().getRoomName() + "</font></b> is an expert<br/>don't forget to check out his/her services!"));
                        tvProfileDescription.setText("Hey there! If you are looking for handmade gifts to give to someone special, please check out my list of services and pricing below!");
                        if (null != vm.getMyUserModel().getAvatarURL()) {
                            Log.e(TAG, "onSelectFinished: " + vm.getMyUserModel().getAvatarURL().getThumbnail());
                            GlideApp.with(HpChatActivity.this).load(vm.getMyUserModel().getAvatarURL().getThumbnail()).into(civMyAvatar);
                        } else {
                            // TODO: 16 October 2018 TEMPORARY
                            Log.e(TAG, "onSelectFinished: avatar null");
                            civMyAvatar.setImageTintList(ColorStateList.valueOf(Integer.parseInt(vm.getRoom().getRoomColor())));
                        }
                        if (null != vm.getRoom().getRoomImage()) {
                            GlideApp.with(HpChatActivity.this).load(vm.getRoom().getRoomImage().getThumbnail()).into(civOtherUserAvatar);
                        } else {
                            // TODO: 16 October 2018 TEMPORARY
                            civOtherUserAvatar.setImageTintList(ColorStateList.valueOf(Integer.parseInt(vm.getRoom().getRoomColor())));
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

                    /* Call Message List API
                    Kalau misalnya lastUpdatednya ga ada di preference last updated dan min creatednya sama
                    Kalau misalnya ada di preference last updatednya ambil dari yang ada di preference (min created ambil dari getCreated)
                    kalau last updated dari getUpdated */
                    callApiAfter();
                });

            } else if (null != hpMessageAdapter) {
                runOnUiThread(() -> {
                    if (clEmptyChat.getVisibility() == View.VISIBLE) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                    flMessageList.setVisibility(View.VISIBLE);
                    hpMessageAdapter.setMessages(models);
                    new Thread(() -> vm.setMessageModels(hpMessageAdapter.getItems())).start();
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

    private HpDatabaseListener<HpMessageEntity> dbListenerPaging = new HpDatabaseListener<HpMessageEntity>() {
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

            if (null != hpMessageAdapter) {
                if (NUM_OF_ITEM > entities.size() && STATE.DONE != state) {
                    callApiBefore(messageBeforeViewPaging);
                } else if (STATE.WORKING == state) {
                    state = STATE.LOADED;
                }

                runOnUiThread(() -> {
                    flMessageList.setVisibility(View.VISIBLE);
                    hpMessageAdapter.addMessage(models);
                    new Thread(() -> vm.setMessageModels(hpMessageAdapter.getItems())).start();

                    if (rvMessageList.getVisibility() != View.VISIBLE)
                        rvMessageList.setVisibility(View.VISIBLE);
                    if (state == STATE.DONE) updateMessageDecoration();
                });
            }
        }
    };

    private HpDefaultDataView<HpGetMessageListbyRoomResponse> messageAfterView = new HpDefaultDataView<HpGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(HpGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<HpMessageEntity> responseMessages = new ArrayList<>();
            //messageAfterModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<HpMessageModel> messageAfterModels = new ArrayList<>();
            for (HpMessageModel message : response.getMessages()) {
                try {
                    HpMessageModel temp = HpMessageModel.BuilderDecrypt(message);
                    responseMessages.add(HpChatManager.getInstance().convertToEntity(temp));
                    addAfterTextMessage(temp, messageAfterModels);
                    new Thread(() -> {
                        //ini buat update last update timestamp yang ada di preference
                        //ini di taruh di new Thread biar ga bkin scrollingnya lag
                        if (null != temp.getUpdated() &&
                                HpDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < temp.getUpdated()) {
                            HpDataManager.getInstance().saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), temp.getUpdated());
                        }
                    }).start();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            //sorting message balikan dari api after
            //messageAfterModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageAfterModels, ASCENDING);
            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);
                //masukin datanya ke dalem recyclerView
                //posisinya dimasukin ke index 0 karena brati dy message baru yang belom ada
                hpMessageAdapter.addMessage(0, messageAfterModels);
                //ini buat ngecek kalau user lagi ada di bottom pas masuk data lgsg di scroll jdi ke paling bawah lagi
                //kalau user ga lagi ada di bottom ga usah di turunin
                if (vm.isOnBottom()) rvMessageList.scrollToPosition(0);
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(hpMessageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            HpDataManager.getInstance().insertToDatabase(responseMessages, false, new HpDatabaseListener() {
            });

            //ngecek isInitialApiCallFinished karena kalau dari onResume, api before itu ga perlu untuk di panggil lagi
            if (0 < vm.getMessageModels().size() && NUM_OF_ITEM > vm.getMessageModels().size() && !vm.isInitialAPICallFinished()) {
                callApiBefore(messageBeforeView);
            }
            //ubah initialApiCallFinished jdi true (brati udah dipanggil pas onCreate / pas pertama kali di buka
            vm.setInitialAPICallFinished(true);
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

            if (0 < vm.getMessageModels().size())
                callApiBefore(messageBeforeView);
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

            if (0 < vm.getMessageModels().size())
                callApiBefore(messageBeforeView);
        }
    };

    //message before yang di panggil setelah api after pas awal (cuman di panggil sekali doang)
    private HpDefaultDataView<HpGetMessageListbyRoomResponse> messageBeforeView = new HpDefaultDataView<HpGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(HpGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<HpMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<HpMessageModel> messageBeforeModels = new ArrayList<>();
            for (HpMessageModel message : response.getMessages()) {
                try {
                    HpMessageModel temp = HpMessageModel.BuilderDecrypt(message);
                    responseMessages.add(HpChatManager.getInstance().convertToEntity(temp));
                    addBeforeTextMessage(temp, messageBeforeModels);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, DESCENDING);

            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                flMessageList.setVisibility(View.VISIBLE);

                //ini di taronya di belakang karena message before itu buat message yang lama-lama
                hpMessageAdapter.addMessage(messageBeforeModels);
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(hpMessageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            HpDataManager.getInstance().insertToDatabase(responseMessages, false, new HpDatabaseListener() {
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
        }
    };


    //message before yang di panggil pas pagination db balikin data di bawah limit
    private HpDefaultDataView<HpGetMessageListbyRoomResponse> messageBeforeViewPaging = new HpDefaultDataView<HpGetMessageListbyRoomResponse>() {
        @Override
        public void startLoading() {
        }

        @Override
        public void onSuccess(HpGetMessageListbyRoomResponse response) {
            //response message itu entity jadi buat disimpen ke database
            List<HpMessageEntity> responseMessages = new ArrayList<>();
            //messageBeforeModels itu model yang buat diisi sama hasil api after yang belum ada di recyclerView
            List<HpMessageModel> messageBeforeModels = new ArrayList<>();
            for (HpMessageModel message : response.getMessages()) {
                try {
                    HpMessageModel temp = HpMessageModel.BuilderDecrypt(message);
                    responseMessages.add(HpChatManager.getInstance().convertToEntity(temp));
                    addBeforeTextMessage(temp, messageBeforeModels);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }

            //ini ngecek kalau misalnya balikan apinya itu perPagenya > pageCount brati berenti ga usah pagination lagi (State.DONE)
            //selain itu paginationnya bisa lanjut lagi
            state = response.getMetadata().getPerPage() > response.getMetadata().getPageCount() ? STATE.DONE : STATE.LOADED;
            if (state == STATE.DONE) updateMessageDecoration();

            //sorting message balikan dari api before
            //messageBeforeModels ini adalah message balikan api yang belom ada di recyclerView
            mergeSort(messageBeforeModels, DESCENDING);
            runOnUiThread(() -> {
                //ini di taronya di belakang karena message before itu buat message yang lama-lama
                hpMessageAdapter.addMessage(messageBeforeModels);
                //mastiin message models yang ada di view model sama isinya kyak yang ada di recyclerView
                new Thread(() -> vm.setMessageModels(hpMessageAdapter.getItems())).start();

                if (rvMessageList.getVisibility() != View.VISIBLE)
                    rvMessageList.setVisibility(View.VISIBLE);
                if (state == STATE.DONE) updateMessageDecoration();
            });

            HpDataManager.getInstance().insertToDatabase(responseMessages, false, new HpDatabaseListener() {
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
        }

        @Override
        public void onError(Throwable throwable) {
            super.onError(throwable);
        }
    };

    public void callApiBefore(HpDefaultDataView<HpGetMessageListbyRoomResponse> beforeView) {
        /*call api before rules:
        * parameternya max created adalah Created yang paling kecil dari yang ada di recyclerView*/
        new Thread(() -> HpDataManager.getInstance().getMessageListByRoomBefore(vm.getRoom().getRoomID()
                , vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated()
                , beforeView)).start();
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
            if (vm.getMessageModels().size() > 0 && !HpDataManager.getInstance().checkKeyInLastMessageTimestamp(vm.getRoom().getRoomID())) {
                HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(), messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                HpDataManager.getInstance().getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        HpDataManager.getInstance().getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
    }

    private void mergeSort(List<HpMessageModel> messages, int sortDirection) {
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
        List<HpMessageModel> leftList = new ArrayList<>(leftListSize);
        //bikin list kanan sejumlah sisanya (size - median)
        List<HpMessageModel> rightList = new ArrayList<>(rightListSize);

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

    private void merge(List<HpMessageModel> messagesAll, List<HpMessageModel> leftList, List<HpMessageModel> rightList, int leftSize, int rightSize, int sortDirection) {
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

}
