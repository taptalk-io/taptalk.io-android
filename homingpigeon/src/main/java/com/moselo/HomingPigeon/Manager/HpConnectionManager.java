package com.moselo.HomingPigeon.Manager;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moselo.HomingPigeon.API.Api.HpApiManager;
import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Interface.HomingPigeonNetworkInterface;
import com.moselo.HomingPigeon.Interface.HomingPigeonSocketInterface;
import com.moselo.HomingPigeon.Model.HpErrorModel;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.moselo.HomingPigeon.Helper.HomingPigeon.appContext;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_SECRET;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.CLOSE_FOR_RECONNECT_CODE;
import static com.moselo.HomingPigeon.Manager.HpConnectionManager.ConnectionStatus.CONNECTING;
import static com.moselo.HomingPigeon.Manager.HpConnectionManager.ConnectionStatus.DISCONNECTED;
import static com.moselo.HomingPigeon.Manager.HpConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class HpConnectionManager {

    private String TAG = HpConnectionManager.class.getSimpleName();
    private static HpConnectionManager instance;
    private WebSocketClient webSocketClient;
    private String webSocketEndpoint = "wss://hp-staging.moselo.com:8080/pigeon";
    //private String webSocketEndpoint = "ws://echo.websocket.org";
    private URI webSocketUri;
    private ConnectionStatus connectionStatus = NOT_CONNECTED;
    private List<HomingPigeonSocketInterface> socketListeners;

    private int reconnectAttempt;
    private final long RECONNECT_DELAY = 500;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, NOT_CONNECTED
    }

    public static HpConnectionManager getInstance() {
        return instance == null ? (instance = new HpConnectionManager()) : instance;
    }

    public HpConnectionManager() {
        try {
//            webSocketUri = new URI(webSocketEndpoint);
//            initWebSocketClient(webSocketUri);
            initNetworkListener();
            socketListeners = new ArrayList<>();
            reconnectAttempt = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWebSocketClient(URI webSocketUri, Map<String, String> header) {
        webSocketClient = new WebSocketClient(webSocketUri, header) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.e(TAG, "onOpen: ");
                connectionStatus = ConnectionStatus.CONNECTED;
                reconnectAttempt = 0;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketConnected();
                }
            }

            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                String tempMessage = StandardCharsets.UTF_8.decode(bytes).toString();
                try {
                    HashMap response = new ObjectMapper().readValue(tempMessage, HashMap.class);
                    Log.e(TAG, "onMessage: " + response);
                    if (null != socketListeners && !socketListeners.isEmpty()) {
                        for (HomingPigeonSocketInterface listener : socketListeners)
                            listener.onReceiveNewEmit(response.get("eventName").toString(), tempMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "onClose2: " + code);
                Log.e(TAG, "onClose: " + reason);
                connectionStatus = DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty() && code != CLOSE_FOR_RECONNECT_CODE) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketDisconnected();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: ", ex);
                connectionStatus = DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketError();
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                Log.e(TAG, "reconnect: ");
                connectionStatus = CONNECTING;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketConnecting();
                }
            }
        };
    }

    private void initNetworkListener() {
        HomingPigeonNetworkInterface networkListener = () -> {
            if (HpDataManager.getInstance().checkAccessTokenAvailable()) {
                Log.e(TAG, "initNetworkListener: " );
                HpDataManager.getInstance().validateAccessToken(validateAccessView);
                if (CONNECTING == connectionStatus ||
                        DISCONNECTED == connectionStatus) {
                    reconnect();
                } else if (NOT_CONNECTED == connectionStatus) {
                    connect();
                }
            }
        };
        HpNetworkStateManager.getInstance().addNetworkListener(networkListener);
    }

    public void addSocketListener(HomingPigeonSocketInterface listener) {
        socketListeners.add(listener);
    }

    public void removeSocketListener(HomingPigeonSocketInterface listener) {
        socketListeners.remove(listener);
    }

    public void removeSocketListenerAt(int index) {
        socketListeners.remove(index);
    }

    public void clearSocketListener() {
        socketListeners.clear();
    }

    public void send(String messageString) {
        if (webSocketClient.isOpen()) {
            webSocketClient.send(messageString.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void connect() {
        if ((DISCONNECTED == connectionStatus || NOT_CONNECTED == connectionStatus) &&
                HpNetworkStateManager.getInstance().hasNetworkConnection(HomingPigeon.appContext)) {
            try {
                webSocketUri = new URI(webSocketEndpoint);
                Map<String, String> websocketHeader = new HashMap<>();
                createHeaderForConnectWebSocket(websocketHeader);
                initWebSocketClient(webSocketUri, websocketHeader);
                connectionStatus = CONNECTING;
                webSocketClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void close() {
        if (ConnectionStatus.CONNECTED == connectionStatus ||
                CONNECTING == connectionStatus) {
            try {
                connectionStatus = DISCONNECTED;
                webSocketClient.close();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(int code) {
        if (ConnectionStatus.CONNECTED == connectionStatus ||
                CONNECTING == connectionStatus) {
            try {
                connectionStatus = DISCONNECTED;
                webSocketClient.close(code);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect() {
        if (reconnectAttempt < 120) reconnectAttempt++;
        long delay = RECONNECT_DELAY * (long) reconnectAttempt;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (DISCONNECTED == connectionStatus && !HpChatManager.getInstance().isFinishChatFlow()) {
                    connectionStatus = CONNECTING;
                    try {
                        if (null != socketListeners && !socketListeners.isEmpty()) {
                            for (HomingPigeonSocketInterface listener : socketListeners)
                                listener.onSocketConnecting();
                        }
                        close(CLOSE_FOR_RECONNECT_CODE);
                        connect();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        Log.e(TAG, "run: ", e);
                    }
                }
            }
        }, delay);

    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    private void createHeaderForConnectWebSocket(Map<String, String> websocketHeader) {
        //Map<String, String> websocketHeader = new HashMap<>();

        String appKey = Base64.encodeToString((APP_KEY_ID + ":" + APP_KEY_SECRET).getBytes(), Base64.NO_WRAP);
        String deviceID = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;

        if (HpDataManager.getInstance().checkAccessTokenAvailable()) {
            websocketHeader.put("Authorization", "Bearer " + HpDataManager.getInstance().getAccessToken());
            websocketHeader.put("App-Key", appKey);
            websocketHeader.put("Device-Identifier", deviceID);
            websocketHeader.put("Device-Model", android.os.Build.MODEL);
            websocketHeader.put("Device-Platform", "android");
            websocketHeader.put("Device-OS-Version", deviceOsVersion);
            websocketHeader.put("App-Version", BuildConfig.VERSION_NAME);
            websocketHeader.put("User-Agent", "android");
        }

        //return websocketHeader;
    }

    private HpDefaultDataView<HpErrorModel> validateAccessView = new HpDefaultDataView<HpErrorModel>() {
    };
}
