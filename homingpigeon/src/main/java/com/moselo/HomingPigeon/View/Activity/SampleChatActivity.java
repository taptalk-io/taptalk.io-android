package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.ViewModel.ChatViewModel;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.EndlessScrollListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.View.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.View.Helper.Const.K_THEIR_USERNAME;

public class SampleChatActivity extends BaseActivity implements View.OnClickListener, HomingPigeonChatListener {

    private String TAG = SampleChatActivity.class.getSimpleName();

    // View
    private RecyclerView rvChatList;
    private EditText etChat;
    private TextView tvAvatar, tvUsername, tvUserStatus, tvLastMessageTime, tvBadgeUnread;
    private ImageView ivSend, ivToBottom;

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
        setContentView(R.layout.activity_sample_chat);

        initViewModel();
        initView();
        initHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstance().removeChatListener(this);
        DataManager.getInstance().updatePendingStatus();
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
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_send) {
            attemptSend();
        } else if (viewId == R.id.iv_to_bottom) {
            rvChatList.scrollToPosition(0);
            ivToBottom.setVisibility(View.INVISIBLE);
            tvBadgeUnread.setVisibility(View.INVISIBLE);
            vm.setUnreadCount(0);
        }
    }

    @Override
    public void onReceiveTextMessageInActiveRoom(final MessageModel message) {
        addNewTextMessage(message);
    }

    @Override
    public void onReceiveTextMessageInOtherRoom(MessageModel message) {
        // TODO: 28 August 2018 REPLACE
        addNewTextMessage(message);
    }

    @Override
    public void onRetrySendMessage(MessageModel message) {
        vm.delete(message.getLocalID());
        ChatManager.getInstance().sendTextMessage(message.getMessage());
    }

    @Override
    public void onSendFailed(final MessageModel message) {
        vm.updatePendingMessage(message);
        vm.removePendingMessage(message.getLocalID());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                Log.e("KRIM", "onSendFailed: "+message);
            }
        });
    }

    @Override
    public void onSendTextMessage(MessageModel message) {
        addNewTextMessage(message);
        vm.addPendingMessage(message);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(ChatViewModel.class);
        vm.setRoomID(getIntent().getStringExtra(DefaultConstant.K_ROOM_ID));
        vm.setMyUserModel(DataManager.getInstance().getActiveUser(this));
        //vm.setPendingMessages(ChatManager.getInstance().getMessageQueueInActiveRoom());
        vm.getMessageEntities(vm.getRoomID(), new HomingPigeonGetChatListener() {
            @Override
            public void onGetMessages(List<MessageEntity> entities) {
                loadMessageFromDatabase(entities);
            }
        });
    }

    private void initView() {
        tvAvatar = findViewById(R.id.tv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserStatus = findViewById(R.id.tv_last_message);
        tvLastMessageTime = findViewById(R.id.tv_last_message_time);
        rvChatList = findViewById(R.id.rv_chatlist);
        etChat = findViewById(R.id.et_chat);
        tvBadgeUnread = findViewById(R.id.tv_badge_unread);
        ivSend = findViewById(R.id.iv_send);
        ivToBottom = findViewById(R.id.iv_to_bottom);

        tvAvatar.setText(getIntent().getStringExtra(K_THEIR_USERNAME).substring(0, 1).toUpperCase());
        tvAvatar.setBackgroundTintList(ColorStateList.valueOf(getIntent().getIntExtra(K_COLOR, 0)));
        tvUsername.setText(getIntent().getStringExtra(K_THEIR_USERNAME));
        tvUserStatus.setText("User Status");
        tvLastMessageTime.setVisibility(View.GONE);

        adapter = new MessageAdapter(this, this);
        adapter.setMessages(vm.getMessageModels());
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        llm.setStackFromEnd(true);
        rvChatList.setAdapter(adapter);
        rvChatList.setLayoutManager(llm);
        rvChatList.setHasFixedSize(false);

        final HomingPigeonGetChatListener scrollChatListener = new HomingPigeonGetChatListener() {
            @Override
            public void onGetMessages(List<MessageEntity> entities) {
                loadMessageFromDatabase(entities);
            }
        };

        rvChatList.addOnScrollListener(new EndlessScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < adapter.getItemCount()) {
                    vm.getMessageByTimestamp(vm.getRoomID(), scrollChatListener, vm.getLastTimestamp());
                    state = STATE.WORKING;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvChatList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (llm.findFirstVisibleItemPosition() == 0) {
                        vm.setOnBottom(true);
                        vm.setUnreadCount(0);
                        ivToBottom.setVisibility(View.INVISIBLE);
                        tvBadgeUnread.setVisibility(View.INVISIBLE);
                    } else {
                        vm.setOnBottom(false);
                        ivToBottom.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        etChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.send || actionId == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });

        ivSend.setOnClickListener(this);
        ivToBottom.setOnClickListener(this);
    }

    private void initHelper() {
        ChatManager.getInstance().addChatListener(this);
    }

    private void loadMessageFromDatabase(List<MessageEntity> entities) {
        final List<MessageModel> models = new ArrayList<>();
        for (MessageEntity entity : entities) {
            models.add(ChatManager.getInstance().convertToModel(entity));
        }
        vm.setMessageModels(models);
        if (vm.getMessageModels().size() > 0) {
            vm.setLastTimestamp(models.get(vm.getMessageModels().size() - 1).getCreated());
        }
        if (null != adapter && 0 == adapter.getItems().size()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setMessages(models);
                    rvChatList.scrollToPosition(0);
                }
            });
        }
        else if (null != adapter) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addMessage(models);
                }
            });
            state = 0 == entities.size() ? STATE.DONE : STATE.LOADED;
        }
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            etChat.setText("");
            ChatManager.getInstance().sendTextMessage(message);
        }
    }

    private void addNewTextMessage(final MessageModel newMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Replace pending message with new message
                String newID = newMessage.getLocalID();
                if (vm.getPendingMessages().containsKey(newID)) {
                    vm.updatePendingMessage(newMessage);
                    vm.removePendingMessage(newID);
                    adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                }
                else {
                    adapter.addMessage(newMessage);
                }

                //Scroll recycler to bottom or show unread badge
                if (newMessage.getUser().getUserID().equals(DataManager.getInstance()
                        .getActiveUser(SampleChatActivity.this).getUserID()) ||
                        vm.isOnBottom()){
                    rvChatList.scrollToPosition(0);
                }
                else {
                    tvBadgeUnread.setVisibility(View.VISIBLE);
                    tvBadgeUnread.setText(vm.getUnreadCount() + "");
                    vm.setUnreadCount(vm.getUnreadCount() + 1);
                }
            }
        });
    }
}
