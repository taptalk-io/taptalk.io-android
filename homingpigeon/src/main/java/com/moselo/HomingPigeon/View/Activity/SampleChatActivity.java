package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
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

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsConnectionError;
import static com.moselo.HomingPigeon.View.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.View.Helper.Const.K_MY_USERNAME;
import static com.moselo.HomingPigeon.View.Helper.Const.K_THEIR_USERNAME;

public class SampleChatActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = SampleChatActivity.class.getSimpleName();

    private RecyclerView rvChatList;
    private EditText etChat;
    private TextView tvAvatar, tvUsername, tvUserStatus, tvLastMessageTime, tvBadgeUnread;
    private ImageView ivSend, ivToBottom;
    private String roomID;
    private UserModel myUserModel;

    //recyclerView
    private MessageAdapter adapter;
    private LinearLayoutManager llm;

    //RoomDatabase
    private MessageViewModel mVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_chat);

        //init ViewModel harus di atas init View karena ada make view model di dlem init view
        initViewModel();
        initView();
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
        myUserModel = DataManager.getInstance().getUserModel(this);

        adapter = new MessageAdapter(this);
        adapter.setMessages(mVM.getMessageModels());
        llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvChatList.setAdapter(adapter);
        rvChatList.setLayoutManager(llm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvChatList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (llm.findFirstVisibleItemPosition() == 0) {
                        mVM.setOnBottom(true);
                        ivToBottom.setVisibility(View.INVISIBLE);
                        tvBadgeUnread.setVisibility(View.INVISIBLE);
                        mVM.setUnreadCount(0);
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

    private void attemptSend() {
        String tempMessage = etChat.getText().toString();
        if (!TextUtils.isEmpty(tempMessage)) {
            ChatManager.getInstance().sendTextMessage(tempMessage,
                    roomID, myUserModel);
            etChat.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.iv_send) {
            attemptSend();
        } else if (viewID == R.id.iv_to_bottom) {
            rvChatList.scrollToPosition(0);
            ivToBottom.setVisibility(View.INVISIBLE);
            tvBadgeUnread.setVisibility(View.INVISIBLE);
            mVM.setUnreadCount(0);
        }
    }

    private void initViewModel() {
        mVM = ViewModelProviders.of(this).get(MessageViewModel.class);
        mVM.setUsername(getIntent().getStringExtra(K_MY_USERNAME));

        mVM.getAllMessages().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(List<MessageEntity> messageEntities) {
                try {
                    if (0 == mVM.getMessageModels().size()) {
                        List<MessageModel> models = new ArrayList<>();
                        for (MessageEntity entity : messageEntities) {
                            MessageModel model = MessageModel.BuilderDecrypt(entity.getMessage(),
                                    Utils.getInstance().fromJSON(new TypeReference<RoomModel>() {
                                    }, entity.getRoom()),
                                    entity.getType(), entity.getCreated(),
                                    Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
                                    }, entity.getUser()));
                            models.add(model);
                        }
                        mVM.setMessageModels(models);
                        if (null != adapter){
                            adapter.setMessages(models);
                            rvChatList.scrollToPosition(0);
                        }
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Log.e("><><><", "onChanged: ",e );
                }
            }
        });
    }

    private void initHelper() {
        ChatManager.getInstance().setChatListener(new HomingPigeonChatListener() {
            @Override
            public void onNewTextMessage(final MessageModel message) {
                try {
                    message.setMessage(EncryptorManager.getInstance().decrypt(message.getMessage()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addMessage(message);
                            rvChatList.scrollToPosition(0);
                        }
                    });
                    addMessageToDatabase(message);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMessageToDatabase(MessageModel messageModel) {
        try {
            mVM.insert(new MessageEntity(
                    messageModel.getLocalID(),
                    Utils.getInstance().toJsonString(messageModel.getRoom()),
                    messageModel.getType(), EncryptorManager.getInstance().encrypt(messageModel.getMessage()), messageModel.getCreated(),
                    Utils.getInstance().toJsonString(messageModel.getUser())
            ));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

}
