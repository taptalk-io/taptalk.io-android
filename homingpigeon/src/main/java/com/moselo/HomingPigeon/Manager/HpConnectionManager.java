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
import com.moselo.HomingPigeon.Model.ErrorModel;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.moselo.HomingPigeon.Helper.HomingPigeon.appContext;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_ID;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.APP_KEY_SECRET;
import static com.moselo.HomingPigeon.Manager.HpConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class HpConnectionManager {

    private String TAG = HpConnectionManager.class.getSimpleName();
    private static HpConnectionManager instance;
    private WebSocketClient webSocketClient;
    private String webSocketEndpoint = "wss://hp-staging.moselo.com:8080/pigeon";
    //        private String webSocketEndpoint = "ws://echo.websocket.org";
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
                Log.e(TAG, "onClose2: "+code);
                Log.e(TAG, "onClose: "+reason);
                connectionStatus = ConnectionStatus.DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketDisconnected();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: "+ex.getMessage());
                connectionStatus = ConnectionStatus.DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketError();
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                Log.e(TAG, "reconnect: ");
                connectionStatus = ConnectionStatus.CONNECTING;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (HomingPigeonSocketInterface listener : socketListeners)
                        listener.onSocketConnecting();
                }
            }
        };
    }

    private void initNetworkListener() {

        HomingPigeonNetworkInterface networkListener = () -> {
            Log.e(TAG, "initNetworkListener: "+connectionStatus );
            if (ConnectionStatus.CONNECTING == connectionStatus ||
                    ConnectionStatus.DISCONNECTED == connectionStatus) {
                reconnect();
            } else if (NOT_CONNECTED == connectionStatus && HpDataManager.getInstance().checkAccessTokenAvailable(appContext)) {
                connect();
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
        if ((ConnectionStatus.DISCONNECTED == connectionStatus || NOT_CONNECTED == connectionStatus) &&
                HpNetworkStateManager.getInstance().hasNetworkConnection(appContext)) {
            Log.e(TAG, "connect: " );
            callApiToValidateAccessToken();
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
        if (reconnectAttempt < 120) reconnectAttempt++;
        long delay = RECONNECT_DELAY * (long) reconnectAttempt;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (ConnectionStatus.DISCONNECTED == connectionStatus && !HpChatManager.getInstance().isFinishChatFlow()) {
                    connectionStatus = ConnectionStatus.CONNECTING;
                    callApiToValidateAccessToken();
                }
            }
        }, delay);
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    private Map<String, String> createHeaderForConnectWebSocket() {
        Map<String, String> websocketHeader = new HashMap<>();

        String appKey = Base64.encodeToString((APP_KEY_ID+":"+APP_KEY_SECRET).getBytes(), Base64.NO_WRAP);
        String deviceID = Settings.Secure.getString(appContext.getContentResolver(),Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;

        if (HpDataManager.getInstance().checkAccessTokenAvailable(appContext)) {
            websocketHeader.put("Authorization", "Bearer " + HpDataManager.getInstance().getAccessToken(appContext));
            websocketHeader.put("App-Key", appKey);
            websocketHeader.put("Device-Identifier", deviceID);
            websocketHeader.put("Device-Model", android.os.Build.MODEL);
            websocketHeader.put("Device-Platform", "android");
            websocketHeader.put("Device-OS-Version", deviceOsVersion);
            websocketHeader.put("App-Version", BuildConfig.VERSION_NAME);
            websocketHeader.put("User-Agent", "android");
        }

        return websocketHeader;
    }

    private void callApiToValidateAccessToken() {
        Log.e(TAG, "callApiToValidateAccessToken: " );
        HpDataManager.getInstance().validateAccessToken(new HpDefaultDataView<ErrorModel>() {
            @Override
            public void onSuccess(ErrorModel response) {
                Log.e(TAG, "onSuccess: " );
                try {
                    if (NOT_CONNECTED == connectionStatus) {
                        Log.e(TAG, "onSuccess: Reconnect" );
                        webSocketUri = new URI(webSocketEndpoint);
                        initWebSocketClient(webSocketUri, createHeaderForConnectWebSocket());
                        connectionStatus = ConnectionStatus.CONNECTING;
                        webSocketClient.connect();
                    } else {
                        Log.e(TAG, "onSuccess: Reconnect" );
                        connectionStatus = ConnectionStatus.CONNECTING;
                        webSocketClient.reconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ErrorModel error) {
                super.onError(error);
                Log.e(TAG, "onError: "+error.getCode() );
            }
        });
    }
}
