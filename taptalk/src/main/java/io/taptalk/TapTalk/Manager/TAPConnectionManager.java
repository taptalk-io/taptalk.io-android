package io.taptalk.TapTalk.Manager;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.Taptalk.BuildConfig;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.APP_KEY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.APP_KEY_SECRET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLOSE_FOR_RECONNECT_CODE;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTING;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.DISCONNECTED;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class TAPConnectionManager {

    private String TAG = TAPConnectionManager.class.getSimpleName();
    private static TAPConnectionManager instance;
    private WebSocketClient webSocketClient;
    private String webSocketEndpoint = BuildConfig.BASE_WSS;
    //private String webSocketEndpoint = "ws://echo.websocket.org";
    private URI webSocketUri;
    private ConnectionStatus connectionStatus = NOT_CONNECTED;
    private List<TapTalkSocketInterface> socketListeners;

    private int reconnectAttempt;
    private final long RECONNECT_DELAY = 500;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, NOT_CONNECTED
    }

    public static TAPConnectionManager getInstance() {
        return instance == null ? (instance = new TAPConnectionManager()) : instance;
    }

    public TAPConnectionManager() {
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
                    for (TapTalkSocketInterface listener : socketListeners)
                        listener.onSocketConnected();
                }
            }

            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                String tempMessage = StandardCharsets.UTF_8.decode(bytes).toString();
                Log.e(TAG, "onMessage: "+tempMessage );
                String messages[] = tempMessage.split("\\r?\\n");
                // TODO: 23/11/18 NANTI HARUS DIUBAH KARENA COMPLEXITYNYA JELEK
                for (String message  : messages) {
                    try {
                        HashMap response = new ObjectMapper().readValue(message, HashMap.class);
                        if (null != socketListeners && !socketListeners.isEmpty()) {
                            for (TapTalkSocketInterface listener : socketListeners)
                                listener.onReceiveNewEmit(response.get("eventName").toString(), message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "onClose2: " + code);
                Log.e(TAG, "onClose: " + reason);
                connectionStatus = DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty() && code != CLOSE_FOR_RECONNECT_CODE) {
                    for (TapTalkSocketInterface listener : socketListeners)
                        listener.onSocketDisconnected();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: ", ex);
                connectionStatus = DISCONNECTED;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (TapTalkSocketInterface listener : socketListeners)
                        listener.onSocketError();
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                Log.e(TAG, "reconnect: ");
                connectionStatus = CONNECTING;
                if (null != socketListeners && !socketListeners.isEmpty()) {
                    for (TapTalkSocketInterface listener : socketListeners)
                        listener.onSocketConnecting();
                }
            }
        };
    }

    private void initNetworkListener() {
        TapTalkNetworkInterface networkListener = () -> {
            if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
                Log.e(TAG, "initNetworkListener: " );
                TAPDataManager.getInstance().validateAccessToken(validateAccessView);
                if (CONNECTING == connectionStatus ||
                        DISCONNECTED == connectionStatus) {
                    reconnect();
                } else if (NOT_CONNECTED == connectionStatus) {
                    connect();
                }
            }
        };
        TAPNetworkStateManager.getInstance().addNetworkListener(networkListener);
    }

    public void addSocketListener(TapTalkSocketInterface listener) {
        socketListeners.add(listener);
    }

    public void removeSocketListener(TapTalkSocketInterface listener) {
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
                TAPNetworkStateManager.getInstance().hasNetworkConnection(appContext)) {
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
                if (DISCONNECTED == connectionStatus && !TAPChatManager.getInstance().isFinishChatFlow()) {
                    connectionStatus = CONNECTING;
                    try {
                        if (null != socketListeners && !socketListeners.isEmpty()) {
                            for (TapTalkSocketInterface listener : socketListeners)
                                listener.onSocketConnecting();
                        }
                        TAPDataManager.getInstance().validateAccessToken(new TapDefaultDataView<TAPErrorModel>() {});
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

        if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
            websocketHeader.put("Authorization", "Bearer " + TAPDataManager.getInstance().getAccessToken());
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

    private TapDefaultDataView<TAPErrorModel> validateAccessView = new TapDefaultDataView<TAPErrorModel>() {
    };
}
