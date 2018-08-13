package com.moselo.HomingPigeon.Testing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ToasterMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private static final String TAG = LibraryActivity.class.getSimpleName();
    private WebSocketClient mWsClient;

    private RecyclerView rvChatlist;
    private EditText etChat;
    private ImageView ivSend;
    private MessageAdapter adapter;

    private enum STATE {
        LOGIN, CHAT
    }

    private STATE state = STATE.LOGIN;
    List<MessageEntity> items = new ArrayList<>();
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        rvChatlist = findViewById(R.id.rv_chatlist);
        etChat = findViewById(R.id.et_chat);
        ivSend = findViewById(R.id.iv_send);

        try {
            URI uri = new URI("wss://echo.websocket.org");
            username = etChat.getText().toString();
            mWsClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToasterMessage.showToast(LibraryActivity.this, "Connect");
                        }
                    });
                    state = STATE.CHAT;
                    username = etChat.getText().toString();
                    mWsClient.send(username + " Has Joined the Chat");
                }

                @Override
                public void onMessage(String message) {
                    Log.e(TAG, "onMessage: "+message );
                    if (0 == adapter.getItemCount()) {
                        MessageEntity logEntity = new MessageEntity();
                        logEntity.setMessage(message);
                        logEntity.setType(2);
                        adapter.addItems(logEntity);
                        Log.e(TAG, "onMessage:2 " );
                    } else {
                        MessageEntity msgEntity = new MessageEntity();
                        msgEntity.setMessage(message);
                        msgEntity.setType(1);
                        msgEntity.setUsername(username);
                        adapter.addItems(msgEntity);
                        Log.e(TAG, "onMessage:3 " );
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "onError: ", ex);
                }
            };
            ToasterMessage.showToast(LibraryActivity.this, "Masuk " + state);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        mWsClient.connect();

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATE.LOGIN == state) {
                    if (!mWsClient.isOpen()) {
                        Log.e(TAG, "onClick: Masuk");
                        mWsClient.connect();
                    }
                } else {
                    mWsClient.send(etChat.getText().toString());
                }
            }
        });

        adapter = new MessageAdapter(items);
        rvChatlist.setAdapter(adapter);
        rvChatlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        rvChatlist.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mWsClient)
            mWsClient.close();
    }
}
