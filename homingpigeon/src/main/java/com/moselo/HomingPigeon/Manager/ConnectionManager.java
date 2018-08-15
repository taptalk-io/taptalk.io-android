package com.moselo.HomingPigeon.Manager;

import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionManager {

    private static ConnectionManager instance;
    private WebSocketClient mWebSocketClient;
    private String webSocketEndpoint = "wss://echo.websocket.org";
    private URI webSocketUri;
    private HomingPigeonSocketListener listener;

    public static ConnectionManager getInstance() {
        if (null == instance) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public ConnectionManager() {
        try {

            webSocketUri = new URI(webSocketEndpoint);
            initWebSocketClient(webSocketUri);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initWebSocketClient(URI webSocketUri) {

        mWebSocketClient = new WebSocketClient(webSocketUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                if (null != listener) listener.onConnect();
            }

            @Override
            public void onMessage(String message) {
                if (null != listener) listener.onNewMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (null != listener) listener.onDisconnect();
            }

            @Override
            public void onError(Exception ex) {
                if (null != listener) listener.onReconnect();
            }
        };
    }

    public void setSocketListener(HomingPigeonSocketListener listener) {
        if (null != this.listener) {
            this.listener = null;
        }
        this.listener = listener;
    }

    public void sendMessage(String messageString) {
        mWebSocketClient.send(messageString);
    }

    public void tryToReconnect() {
        if (null != mWebSocketClient
                && !mWebSocketClient.isOpen()
                && !mWebSocketClient.isConnecting())
            mWebSocketClient.connect();
    }

    public void disconnectSocket(){
        if (null != mWebSocketClient
                && (mWebSocketClient.isOpen() || mWebSocketClient.isConnecting()))
            mWebSocketClient.close();
    }

    public boolean isSocketOpen(){
        if (null != mWebSocketClient && mWebSocketClient.isOpen()){
            return true;
        } else {
            return false;
        }
    }
}
