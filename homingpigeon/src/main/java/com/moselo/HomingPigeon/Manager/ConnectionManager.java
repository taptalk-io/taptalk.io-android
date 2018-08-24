package com.moselo.HomingPigeon.Manager;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Listener.HomingPigeonNetworkListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsConnecting;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsDisconnected;

public class ConnectionManager {

    private String TAG = ConnectionManager.class.getSimpleName();
    private static ConnectionManager instance;
    private WebSocketClient webSocketClient;
    private String webSocketEndpoint = "wss://35.198.219.49:8080/wss";
//    private String webSocketEndpoint = "ws://echo.websocket.org";
    private URI webSocketUri;
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    private List<HomingPigeonSocketListener> socketListeners;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED
    }

    public static ConnectionManager getInstance() {
        return instance == null ? (instance = new ConnectionManager()) : instance;
    }

    public ConnectionManager() {
        try {
            webSocketUri = new URI(webSocketEndpoint);
            initWebSocketClient(webSocketUri);
            initNetworkListener();
            socketListeners = new ArrayList<>();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initWebSocketClient(URI webSocketUri) {
        webSocketClient = new WebSocketClient(webSocketUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connectionStatus = ConnectionStatus.CONNECTED;
                Log.e(TAG, "onOpen: " + connectionStatus);
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(kIsConnecting);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                String tempMessage = StandardCharsets.UTF_8.decode(bytes).toString();
                try {
                    Map<String, Object> response = new ObjectMapper().readValue(tempMessage, HashMap.class);
                    if (null != socketListeners && !socketListeners.isEmpty()) {
                        for (HomingPigeonSocketListener listener : socketListeners)
                            listener.onNewMessage(response.get("eventName").toString(), tempMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connectionStatus = ConnectionStatus.DISCONNECTED;
                Log.e(TAG, "onClose: " + reason);
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(kIsDisconnected);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Exception ex) {
                connectionStatus = ConnectionStatus.DISCONNECTED;
                Log.e(TAG, "onError: " + ex.getMessage());
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsConnectionError);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                connectionStatus = ConnectionStatus.CONNECTING;
                Log.e(TAG, "reconnect: " + connectionStatus);
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsReconnect);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }
        };
    }

    private void initNetworkListener() {
        HomingPigeonNetworkListener networkListener = new HomingPigeonNetworkListener() {
            @Override
            public void onNetworkAvailable() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ConnectionStatus.DISCONNECTED == connectionStatus) {
                            connect();
                        }
                        else if (ConnectionStatus.CONNECTING == connectionStatus) {
                            close();
                            connect();
                        }
                    }
                }, 200L);

            }
        };
        NetworkStateManager.getInstance().addNetworkListener(networkListener);
    }

    public void addSocketListener(HomingPigeonSocketListener listener) {
        socketListeners.add(listener);
    }

    public void removeSocketListener(HomingPigeonSocketListener listener) {
        socketListeners.remove(listener);
    }

    public void removeSocketListenerAt(int index) {
        socketListeners.remove(index);
    }

    public void clearSocketListener() {
        socketListeners.clear();
    }

    public void sendEmit(String messageString) {
        if (webSocketClient.isOpen()) {
            webSocketClient.send(messageString.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void connect() {
        if (ConnectionStatus.DISCONNECTED == connectionStatus &&
                NetworkStateManager.getInstance().hasNetworkConnection(HomingPigeon.appContext)) {
            try {
                connectionStatus = ConnectionStatus.CONNECTING;
                webSocketClient.connect();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (ConnectionStatus.CONNECTED == connectionStatus ||
                ConnectionStatus.CONNECTING == connectionStatus) {
            try {
                connectionStatus = ConnectionStatus.DISCONNECTED;
                webSocketClient.close();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect() {
        if (ConnectionStatus.DISCONNECTED == connectionStatus) {
            try {
                connectionStatus = ConnectionStatus.CONNECTING;
                webSocketClient.reconnect();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
}
