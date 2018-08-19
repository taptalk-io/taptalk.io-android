package com.moselo.HomingPigeon.Testing;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Data.MessageViewModel;
import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.R;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private static final String TAG = LibraryActivity.class.getSimpleName();
    private WebSocketClient mWsClient;

    private RecyclerView rvChatlist;
    private EditText etChat;
    private ImageView ivSend;
    private MessageAdapter adapter;

    private MessageViewModel mViewModel;

    private enum STATE {
        LOGIN, CHAT
    }

    private STATE state = STATE.LOGIN;
    List<com.moselo.HomingPigeon.Data.MessageEntity> items = new ArrayList<>();
    private String username = "";

    HomingPigeonChatListener chatListener = new HomingPigeonChatListener() {
        @Override
        public void onSendTextMessage(String message) {
            List<com.moselo.HomingPigeon.Data.MessageEntity> entities = new ArrayList<>();
            if (0 == adapter.getItemCount()) {
                com.moselo.HomingPigeon.Data.MessageEntity logEntity = new com.moselo.HomingPigeon.Data.MessageEntity();
                logEntity.setMessage(message);
                logEntity.setType(2);
                entities.add(logEntity);
            } else {
                com.moselo.HomingPigeon.Data.MessageEntity msgEntity = new MessageEntity();
                msgEntity.setMessage(message);
                msgEntity.setType(1);
                msgEntity.setUserName(username);
                entities.add(msgEntity);
            }

            mViewModel.insert(entities);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        rvChatlist = findViewById(R.id.rv_chatlist);
        etChat = findViewById(R.id.et_chat);
        ivSend = findViewById(R.id.iv_send);

        mViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        username = etChat.getText().toString();

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATE.LOGIN == state) {
                    ChatManager.getInstance().setChatListener(chatListener);
                    ConnectionManager.getInstance().connect();
                } else {
//                    ChatManager.getInstance().sendMessageText(DefaultConstant.ConnectionEvent.kSocketNewMessage ,etChat.getText().toString());
                }
            }
        });

        adapter = new MessageAdapter(items);
        mViewModel.getAllMessages().observe(this, new Observer<List<com.moselo.HomingPigeon.Data.MessageEntity>>() {
            @Override
            public void onChanged(@Nullable List<com.moselo.HomingPigeon.Data.MessageEntity> messageEntities) {
                adapter.addItems(messageEntities);
                rvChatlist.scrollToPosition(0);
            }
        });
        rvChatlist.setAdapter(adapter);
        rvChatlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        rvChatlist.setHasFixedSize(true);
        BroadcastManager.register(this, receiver, DefaultConstant.ConnectionBroadcast.kIsConnecting);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case DefaultConstant.ConnectionBroadcast.kIsConnecting:
                    Log.e(TAG, "Connecting nih!");
                    state = STATE.CHAT;
                    username = etChat.getText().toString();
//                    ChatManager.getInstance().sendMessageText(DefaultConstant.ConnectionEvent.kSocketNewMessage, username + " Has Joined the Chat");
                    break;
                case DefaultConstant.ConnectionBroadcast.kIsConnected:
                    break;
                case DefaultConstant.ConnectionBroadcast.kIsConnectionError:
                    break;
                case DefaultConstant.ConnectionBroadcast.kIsDisconnected:
                    state = STATE.LOGIN;
                    break;
                case DefaultConstant.ConnectionBroadcast.kIsReconnect:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().close();
        BroadcastManager.unregister(this, receiver);
    }
}
