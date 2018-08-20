package com.moselo.HomingPigeon.SampleApp.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.MessageViewModel;
import com.moselo.HomingPigeon.Helper.AESCrypto.JsEncryptor;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.SampleApp.Adapter.MessageAdapter;
import com.moselo.HomingPigeon.SampleApp.Helper.Const;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER_ID;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_MY_USERNAME;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_THEIR_USERNAME;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.TYPE_BUBBLE_RIGHT;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.TYPE_LOG;

public class SampleChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "]]]]";
    private ChatManager chatManager;
    private MessageAdapter adapter;
    private LinearLayoutManager llm;
    private MessageViewModel vm;
    private JsEncryptor jsEncryptor;
    private ObjectMapper objectMapper;
    private TypeReference<MessageEntity> typeReference;

    private RecyclerView rvChatList;
    private EditText etChat;
    private TextView tvAvatar, tvUsername, tvUserStatus, tvLastMessageTime, tvBadgeUnread;
    private ImageView ivSend, ivToBottom;
    private String roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_chat);

        initView();
        initViewModel();
        initHelper();
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
        roomID = getIntent().getStringExtra(DefaultConstant.K_ROOM_ID);

        adapter = new MessageAdapter(this, getIntent().getStringExtra(K_MY_USERNAME));
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvChatList.setAdapter(adapter);
        rvChatList.setLayoutManager(llm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvChatList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (llm.findFirstVisibleItemPosition() == 0) {
                        vm.setOnBottom(true);
                        ivToBottom.setVisibility(View.INVISIBLE);
                        tvBadgeUnread.setVisibility(View.INVISIBLE);
                        vm.setUnreadCount(0);
                    }
                    else {
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

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(MessageViewModel.class);
        vm.setUsername(getIntent().getStringExtra(K_MY_USERNAME));
        vm.getAllMessages().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable List<MessageEntity> chatMessages) {
                if (adapter.getItemCount() == 0) {
                    adapter.setMessages(chatMessages);
                }
                else if (null != chatMessages && 0 < chatMessages.size()) {
                    if (adapter.getItemAt(0).getId() == chatMessages.get(0).getId()) {
                        adapter.setMessageAt(0, chatMessages.get(0));
                    }
                    else {
                        adapter.addMessage(chatMessages.get(0));
                    }
                }
                if (vm.isOnBottom()) {
                    rvChatList.scrollToPosition(0);
                }
                else {
                    tvBadgeUnread.setVisibility(View.VISIBLE);
                    vm.setUnreadCount(vm.getUnreadCount() + 1);
                    tvBadgeUnread.setText(vm.getUnreadCount() + "");
                }
            }
        });
    }

    private void initHelper() {
        jsEncryptor = JsEncryptor.evaluateAllScripts(this);
        objectMapper = new ObjectMapper();
        typeReference = new TypeReference<MessageEntity>() {};

        chatManager = ChatManager.getInstance();
        chatManager.setChatListener(new HomingPigeonChatListener() {
            @Override
            public void onNewTextMessage(String message) {
                addMessage(vm.getUsername(), TYPE_BUBBLE_RIGHT, message);
            }
        });
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            ChatManager.getInstance().sendTextMessage(message, roomID, prefs.getString(K_USER_ID, "0"));
            etChat.setText("");
        }
    }

    private void addLog(String message) {
        vm.insert(new MessageEntity("", TYPE_LOG, message));
    }

    private void addMessage(String userName, int type, String message) {
        vm.insert(new MessageEntity(userName, type, message));
    }

    private void addMessage(MessageEntity chatMessage) {
        vm.insert(chatMessage);
    }

    private void addMessage(List<MessageEntity> chatMessages) {
        vm.insert(chatMessages);
    }

    private TextWatcher typingWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isTyping = s.length() > 0;
            vm.setTyping(isTyping);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.iv_send) {
            attemptSend();
        }else if (viewID == R.id.iv_to_bottom){
            rvChatList.scrollToPosition(0);
            ivToBottom.setVisibility(View.INVISIBLE);
            tvBadgeUnread.setVisibility(View.INVISIBLE);
            vm.setUnreadCount(0);
        }
    }
}
