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

import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.ChatViewModel;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.EndlessScrollListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.EncryptorManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.MessageAdapter;

import java.security.GeneralSecurityException;
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
    private ChatViewModel mVM;

    //enum Scrolling
    private enum STATE {
        WORKING, LOADED, DONE
    }

    private STATE state = STATE.LOADED;
    private long lastTimestamp = 0;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setActiveRoom(mVM.getRoomId());
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            mVM.setUnreadCount(0);
        }
    }

    @Override
    public void onReceiveTextMessageInActiveRoom(final MessageModel message) {
        message.setIsSending(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatManager.getInstance().removeFromUnsentList(message.getLocalId());
            }
        }).start();
        addNewTextMessage(message);
    }

    @Override
    public void onReceiveTextMessageInOtherRoom(MessageModel message) {
        // TODO: 28 August 2018 PUSH NOTIFICATION & SAVE MESSAGE TO DATABASE
    }

    @Override
    public void onSendTextMessage(MessageModel message) {
        addNewTextMessage(message);
    }

    private void initViewModel() {
        mVM = ViewModelProviders.of(this).get(ChatViewModel.class);
        mVM.setRoomId(getIntent().getStringExtra(DefaultConstant.K_ROOM_ID));
        mVM.setMyUserModel(DataManager.getInstance().getActiveUser(this));
        mVM.getMessageEntities(new HomingPigeonGetChatListener() {
            @Override
            public void onGetMessages(List<MessageEntity> entities) {
                final List<MessageModel> models = new ArrayList<>();
                for (MessageEntity entity : entities) {
                    models.add(ChatManager.getInstance().convertToModel(entity));
                }
                mVM.setMessageModels(models);
                if (mVM.getMessageModels().size() > 0) {
                    lastTimestamp = models.get(mVM.getMessageModels().size() - 1).getCreated();
                }
                if (null != adapter) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setMessages(models);
                            rvChatList.scrollToPosition(0);
                        }
                    });
                }
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

        adapter = new MessageAdapter(this);
        adapter.setMessages(mVM.getMessageModels());
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        llm.setStackFromEnd(true);
        rvChatList.setAdapter(adapter);
        rvChatList.setLayoutManager(llm);
        rvChatList.setHasFixedSize(false);

        final HomingPigeonGetChatListener scrollChatListener = new HomingPigeonGetChatListener() {
            @Override
            public void onGetMessages(List<MessageEntity> entities) {
                final List<MessageModel> models = new ArrayList<>();
                for (MessageEntity entity : entities) {
                    models.add(ChatManager.getInstance().convertToModel(entity));
                }

                mVM.setMessageModels(models);
                if (0 < mVM.getMessageModels().size()) {
                    lastTimestamp = models.get(mVM.getMessageModels().size() - 1).getCreated();
                }
                if (null != adapter) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addMessage(models);
                        }
                    });
                }
                state = 0 == entities.size() ? STATE.DONE : STATE.LOADED;
            }
        };

        rvChatList.addOnScrollListener(new EndlessScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (state == STATE.LOADED && 0 < adapter.getItemCount()) {
                    mVM.getMessageByTimestamp(scrollChatListener, lastTimestamp);
                    state = STATE.WORKING;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvChatList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (llm.findFirstVisibleItemPosition() == 0) {
                        mVM.setOnBottom(true);
                        mVM.setUnreadCount(0);
                        ivToBottom.setVisibility(View.INVISIBLE);
                        tvBadgeUnread.setVisibility(View.INVISIBLE);
                    } else {
                        mVM.setOnBottom(false);
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

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            etChat.setText("");
            ChatManager.getInstance().sendTextMessage(message);
        }
    }

    private void addNewTextMessage(final MessageModel newMessage) {
        try {
            Log.e("RioClarisa", newMessage.getMessage() );
            newMessage.setMessage(EncryptorManager.getInstance().decrypt(newMessage.getMessage(), newMessage.getLocalId()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Overwrite if list already contains the same message
                    boolean isMessageAdded = false;
                    int index = 0;
                    for (MessageModel messageModel : adapter.getItems()) {
                        if (messageModel.getLocalId().equals(newMessage.getLocalId())) {
                            adapter.setMessageAt(index, newMessage);
                            isMessageAdded = true;
                            break;
                        } else index++;
                    }
                    if (!isMessageAdded) {
                        adapter.addMessage(newMessage);
                        ChatManager.getInstance().addToUnsentList(newMessage);
                    }

                    //Scroll recycler to bottom or show unread badge
                    if (newMessage.getUser().getUserID().equals(DataManager.getInstance()
                            .getActiveUser(SampleChatActivity.this).getUserID()) ||
                            mVM.isOnBottom())
                        rvChatList.scrollToPosition(0);
                    else {
                        tvBadgeUnread.setVisibility(View.VISIBLE);
                        tvBadgeUnread.setText(mVM.getUnreadCount() + "");
                        mVM.setUnreadCount(mVM.getUnreadCount() + 1);
                    }
                }
            });
            mVM.insert(ChatManager.getInstance().convertToEntity(newMessage));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
