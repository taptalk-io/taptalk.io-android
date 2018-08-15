package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionManager {

    private static ConnectionManager instance;
    private WebSocketClient mWebSocketClient;
    private String webSocketEndpoint = "wss://echo.websocket.org";
    private URI webSocketUri;
    private HomingPigeonSocketListener listener;
    private Context appContext;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED
    }

    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;


    public static ConnectionManager getInstance(Context appContext) {
        if (null == instance) {
            instance = new ConnectionManager(appContext);
        }

        return instance;
    }

    public ConnectionManager(Context appContext) {
        try {

            webSocketUri = new URI(webSocketEndpoint);
            initWebSocketClient(webSocketUri);
            this.appContext = appContext;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initWebSocketClient(URI webSocketUri) {

        mWebSocketClient = new WebSocketClient(webSocketUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connectionStatus = ConnectionStatus.CONNECTING;
                if (null != appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsConnecting);
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onMessage(String message) {
                JSONObject object = new JSONObject();
                try {
                    object.put("message",message);
                    listener.onNewMessage(DefaultConstant.ConnectionEvent.kSocketSendMessage,object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (null != appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsDisconnected);
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Exception ex) {
                if (null != appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsConnectionError);
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                if (null != appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsReconnect);
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                }
            }
        };
    }

    public void setSocketListener(HomingPigeonSocketListener listener) {
        if (null != this.listener) {
            this.listener = null;
        }
        this.listener = listener;
    }

    public void sendEmit(String messageString) {
        mWebSocketClient.send(messageString);
    }

    public void connect() {
        if (null != mWebSocketClient
                && !mWebSocketClient.isOpen()
                && !mWebSocketClient.isConnecting())
            mWebSocketClient.connect();
    }

    public void close() {
        if (null != mWebSocketClient
                && (mWebSocketClient.isOpen() || mWebSocketClient.isConnecting()))
            mWebSocketClient.close();
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
}
