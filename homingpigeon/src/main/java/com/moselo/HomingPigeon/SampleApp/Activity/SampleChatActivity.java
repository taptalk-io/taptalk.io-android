package com.moselo.HomingPigeon.SampleApp.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.MessageViewModel;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsCallback;
import com.moselo.HomingPigeon.Helper.AESCrypto.JsEncryptor;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonEncryptorListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.EncryptorManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.SampleApp.Adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_MY_USERNAME;
import static com.moselo.HomingPigeon.SampleApp.Helper.Const.K_THEIR_USERNAME;

public class SampleChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = SampleChatActivity.class.getSimpleName();
    private ChatManager chatManager;
    private EncryptorManager encryptorManager;
    HomingPigeonEncryptorListener encryptorListener;
    private MessageAdapter adapter;
    private LinearLayoutManager llm;
    private MessageViewModel vm;


    private RecyclerView rvChatList;
    private EditText etChat;
    private TextView tvAvatar, tvUsername, tvUserStatus, tvLastMessageTime, tvBadgeUnread;
    private ImageView ivSend, ivToBottom;
    private String roomID;
    private UserModel myUserModel;
    private List<MessageModel> chatMessageModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_chat);

        initHelper();
        initView();
        initViewModel();
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        myUserModel = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {},prefs.getString(K_USER,"{}"));

        adapter = new MessageAdapter(this);
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
            public void onChanged(final List<MessageEntity> chatMessages) {
                if (adapter.getItemCount() == 0) {
                    Toast.makeText(SampleChatActivity.this, "Masuk OnChange", Toast.LENGTH_SHORT).show();
                    for (final MessageEntity entity : chatMessages) {
                        JsEncryptor jsEncryptor = JsEncryptor.evaluateAllScripts(SampleChatActivity.this);
                        jsEncryptor.decrypt(entity.getMessage(), "homingpigeon", new JsCallback() {
                            @Override
                            public void onResult(String s) {
                                Log.e(TAG, "onResult: " + s);
                                MessageModel model = MessageModel.Builder(s,
                                        Utils.getInstance().fromJSON(new TypeReference<RoomModel>() {},entity.getRoom()),
                                        entity.getType(),entity.getCreated(),
                                        Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, entity.getUser()));
                                chatMessageModels.add(model);
                                if (chatMessages.size() == chatMessageModels.size()) {
                                    adapter.setMessages(chatMessageModels);
                                    rvChatList.scrollToPosition(0);
                                    Log.e(TAG, "onResult 2: ");
                                }
                            }

                            @Override
                            public void onError(String s) {

                            }
                        });
                    }

                }
                else if (0 < chatMessages.size()) {
                    JsEncryptor jsEncryptor = JsEncryptor.evaluateAllScripts(SampleChatActivity.this);
                    jsEncryptor.decrypt(chatMessages.get(0).getMessage(), "homingpigeon", new JsCallback() {
                        @Override
                        public void onResult(String s) {
                            MessageModel model = MessageModel.Builder(s,
                                    Utils.getInstance().fromJSON(new TypeReference<RoomModel>() {},chatMessages.get(0).getRoom()),
                                    chatMessages.get(0).getType(),chatMessages.get(0).getCreated(),
                                    Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, chatMessages.get(0).getUser()));

                            if (adapter.getItemAt(0).getMessageID().equals(chatMessages.get(0).getId())) {
                                Log.e(TAG, "onChanged: " + model);
                                adapter.setMessageAt(0, model);
                            }
                            else {
                                Log.e(TAG, "onChanged: " + model);
                                adapter.addMessage(model);
                            }
                            rvChatList.scrollToPosition(0);

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
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
        encryptorManager = EncryptorManager.getInstance(this);
        encryptorManager.setListener(new HomingPigeonEncryptorListener() {
            @Override
            public void onEncryptResult(String encryptedMessage) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SampleChatActivity.this);
                UserModel user = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, prefs.getString(DefaultConstant.K_USER, "{}"));
                ChatManager.getInstance().sendTextMessage(encryptedMessage, roomID, user);
            }

            @Override
            public void onDecryptResult(String decryptedMessage) {
//                addMessage(vm.getUsername(), TYPE_BUBBLE_RIGHT, decryptedMessage);
            }

            @Override
            public void onError(String error) {

            }
        });

        chatManager = ChatManager.getInstance();
        chatManager.setChatListener(new HomingPigeonChatListener() {
            @Override
            public void onNewTextMessage(MessageModel message) {
                addMessage(message);
            }
        });
    }

    private void attemptSend() {
        String message = etChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            encryptorManager.encrypt(message, "homingpigeon");
            etChat.setText("");
        }
    }

    private void addLog(String message) {
//        vm.insert(new MessageEntity("", TYPE_LOG, message));
    }

    private void addMessage(MessageModel messageModel) {

        vm.insert(new MessageEntity(messageModel.getMessageID(),
                Utils.getInstance().toJsonString(messageModel.getRoom()),
                messageModel.getType(),
                messageModel.getMessage(),
                messageModel.getCreated(),
                Utils.getInstance().toJsonString(messageModel.getUser())));
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
}
