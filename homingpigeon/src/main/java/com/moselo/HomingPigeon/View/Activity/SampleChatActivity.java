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

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.MessageViewModel;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonGetChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Manager.EncryptorManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
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
    private MessageViewModel mVM;

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
    public void onNewTextMessage(final MessageModel message) {
        Log.e(TAG, "onNewTextMessage: " + message.getLocalID());
        message.setIsSending(0);
        addNewTextMessage(message);
    }

    private void initViewModel() {
        mVM = ViewModelProviders.of(this).get(MessageViewModel.class);
        mVM.setRoomId(getIntent().getStringExtra(DefaultConstant.K_ROOM_ID));
        mVM.setMyUserModel(DataManager.getInstance().getUserModel(this));
        mVM.getMessageEntities(new HomingPigeonGetChatListener() {
            @Override
            public void onGetMessages(List<MessageEntity> entities) {
                final List<MessageModel> models = new ArrayList<>();
                for (MessageEntity entity : entities) {
                    try {
                        MessageModel model = MessageModel.BuilderDecrypt(
                                entity.getMessage(),
                                Utils.getInstance().fromJSON(new TypeReference<RoomModel>() {}, entity.getRoom()),
                                entity.getType(),
                                entity.getCreated(),
                                Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, entity.getUser()));
                        models.add(model);
                        mVM.setMessageModels(models);
                        if (null != adapter) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setMessages(models);
                                    rvChatList.scrollToPosition(0);
                                }
                            });
                        }
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
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
            List<MessageModel> messages = ChatManager.getInstance().buildEncryptedTextMessages(
                    message,
                    mVM.getRoomId(),
                    mVM.getMyUserModel());
            ChatManager.getInstance().sendTextMessage(messages);
            for (MessageModel messageModel : messages) addNewTextMessage(messageModel);
        }
    }

    private void addNewTextMessage(final MessageModel newMessage) {
        try {
            newMessage.setMessage(EncryptorManager.getInstance().decrypt(newMessage.getMessage()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Overwrite if list already contains the same message
                    boolean isMessageAdded = false;
                    int index = 0;
                    for (MessageModel messageModel : adapter.getItems()) {
                        if (null != messageModel.getLocalID() && messageModel.getLocalID().equals(newMessage.getLocalID())) {
                            adapter.setMessageAt(index, newMessage);
                            isMessageAdded = true;
                            break;
                        }
                        else index++;
                    }
                    if (!isMessageAdded) adapter.addMessage(newMessage);
                    Log.e(TAG, "isMessageAdded: " + isMessageAdded);

                    //Scroll recycler to bottom or show unread badge
                    if (newMessage.getUser().getUserID().equals(DataManager.getInstance()
                            .getUserModel(SampleChatActivity.this).getUserID()) ||
                            mVM.isOnBottom())
                        rvChatList.scrollToPosition(0);
                    else {
                        tvBadgeUnread.setVisibility(View.VISIBLE);
                        tvBadgeUnread.setText(mVM.getUnreadCount() + "");
                        mVM.setUnreadCount(mVM.getUnreadCount() + 1);
                    }
                }
            });
            addMessageToDatabase(newMessage);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void addMessageToDatabase(MessageModel messageModel) {
        try {
            mVM.insert(new MessageEntity(
                    messageModel.getLocalID(),
                    Utils.getInstance().toJsonString(messageModel.getRoom()),
                    messageModel.getType(),
                    EncryptorManager.getInstance().encrypt(messageModel.getMessage()),
                    messageModel.getCreated(),
                    Utils.getInstance().toJsonString(messageModel.getUser())
            ));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
