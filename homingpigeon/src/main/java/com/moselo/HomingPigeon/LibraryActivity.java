package com.moselo.HomingPigeon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.java_websocket.client.WebSocketClient;

public class LibraryActivity extends AppCompatActivity {

    private WebSocketClient mWsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
    }
}
