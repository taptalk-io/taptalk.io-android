package io.taptalk.TapTalk.Manager;

import android.provider.Settings;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapCommonInterface;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Listener.TAPSocketMessageListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLOSE_FOR_RECONNECT_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ALREADY_CONNECTED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_NO_INTERNET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_ALREADY_CONNECTED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorMessages.ERROR_MESSAGE_NO_INTERNET;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientSuccessMessages.SUCCESS_MESSAGE_CONNECT;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTING;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.DISCONNECTED;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class TAPConnectionManager {
    private static final String TAG = TAPConnectionManager.class.getSimpleName();
    private static HashMap<String, TAPConnectionManager> instances;

    private String instanceKey = "";
    private WebSocketClient webSocketClient;
    @NonNull private String webSocketEndpoint = "wss://engine.taptalk.io/connect";
    private URI webSocketUri;
    private ConnectionStatus connectionStatus = NOT_CONNECTED;
    private List<TapTalkSocketInterface> socketListeners;
    private TAPSocketMessageListener socketMessageListener;
    private int reconnectAttempt;
    private final long RECONNECT_DELAY = 500;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, NOT_CONNECTED
    }

    public static TAPConnectionManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPConnectionManager instance = new TAPConnectionManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPConnectionManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPConnectionManager(String instanceKey) {
        try {
            this.instanceKey = instanceKey;
//            webSocketUri = new URI(webSocketEndpoint);
//            initWebSocketClient(webSocketUri);
            initNetworkListener();
            socketListeners = new ArrayList<>();
            reconnectAttempt = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public String getWebSocketEndpoint() {
        return webSocketEndpoint;
    }

    public void setWebSocketEndpoint(@NonNull String webSocketEndpoint) {
        this.webSocketEndpoint = webSocketEndpoint;
    }

    private void initWebSocketClient(URI webSocketUri, Map<String, String> header) {
        webSocketClient = new WebSocketClient(webSocketUri, header) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connectionStatus = CONNECTED;
                reconnectAttempt = 0;
                List<TapTalkSocketInterface> socketListenersCopy = new ArrayList<>(socketListeners);
                if (null != socketListeners && !socketListenersCopy.isEmpty()) {
                    for (TapTalkSocketInterface listener : socketListenersCopy) {
                        listener.onSocketConnected();
                    }
                }
            }

            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                //String tempMessage = StandardCharsets.UTF_8.decode(bytes).toString();
                String tempMessage = Charset.forName("UTF-8").decode(bytes).toString();
                String messages[] = tempMessage.split("\\r?\\n");
                // TODO: 23/11/18 NANTI HARUS DIUBAH KARENA COMPLEXITYNYA JELEK
                for (String message : messages) {
                    try {
                        HashMap response = new ObjectMapper().readValue(message, HashMap.class);
                        if (null != socketMessageListener) {
                            socketMessageListener.onReceiveNewEmit(response.get("eventName").toString(), message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (CONNECTED == connectionStatus || CONNECTING == connectionStatus) {
                    connectionStatus = DISCONNECTED;
                }
                TAPChatManager.getInstance(instanceKey).setNeedToCallUpdateRoomStatusAPI(true);
                List<TapTalkSocketInterface> socketListenersCopy = new ArrayList<>(socketListeners);
                if (null != socketListeners && !socketListenersCopy.isEmpty() && code != CLOSE_FOR_RECONNECT_CODE) {
                    for (TapTalkSocketInterface listener : socketListenersCopy) {
                        listener.onSocketDisconnected();
                    }
                }
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                if (CONNECTED == connectionStatus || CONNECTING == connectionStatus) {
                    connectionStatus = DISCONNECTED;
                }
                TAPChatManager.getInstance(instanceKey).setNeedToCallUpdateRoomStatusAPI(true);
                List<TapTalkSocketInterface> socketListenersCopy = new ArrayList<>(socketListeners);
                if (null != socketListeners && !socketListenersCopy.isEmpty()) {
                    for (TapTalkSocketInterface listener : socketListenersCopy)
                        listener.onSocketError();
                }
            }
        };
    }

    private void initNetworkListener() {
        TapTalkNetworkInterface networkListener = () -> {
            if (TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable() &&
                    (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.NOT_CONNECTED ||
                            TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.DISCONNECTED)) {
                TAPDataManager.getInstance(instanceKey).validateAccessToken(validateAccessView);
            }
        };
        TAPNetworkStateManager.getInstance(instanceKey).addNetworkListener(networkListener);
    }

    public void addSocketListener(TapTalkSocketInterface listener) {
        socketListeners.remove(listener);
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

    public void setSocketMessageListener(TAPSocketMessageListener socketMessageListener) {
        this.socketMessageListener = socketMessageListener;
    }

    public void send(String messageString) {
        if (webSocketClient.isOpen()) {
            webSocketClient.send(messageString.getBytes(Charset.forName("UTF-8")));
        }
    }

    public void connect() {
        if ((DISCONNECTED == connectionStatus || NOT_CONNECTED == connectionStatus) && TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            try {
                webSocketUri = new URI(getWebSocketEndpoint());
                Map<String, String> webSocketHeader = new HashMap<>();
                createHeaderForConnectWebSocket(webSocketHeader);
                initWebSocketClient(webSocketUri, webSocketHeader);
                connectionStatus = CONNECTING;
                triggerOnSocketConnectingListener();
                webSocketClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(TapCommonInterface listener) {
        if (CONNECTED == connectionStatus || CONNECTING == connectionStatus) {
            listener.onError(ERROR_CODE_ALREADY_CONNECTED, ERROR_MESSAGE_ALREADY_CONNECTED);
        } else if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(appContext)) {
            listener.onError(ERROR_CODE_NO_INTERNET, ERROR_MESSAGE_NO_INTERNET);
        } else {
            try {
                webSocketUri = new URI(getWebSocketEndpoint());
                Map<String, String> websocketHeader = new HashMap<>();
                createHeaderForConnectWebSocket(websocketHeader);
                initWebSocketClient(webSocketUri, websocketHeader);
                connectionStatus = CONNECTING;
                triggerOnSocketConnectingListener();
                webSocketClient.connect();
                listener.onSuccess(SUCCESS_MESSAGE_CONNECT);
            } catch (Exception e) {
                listener.onError(ERROR_CODE_OTHERS, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void close(ConnectionStatus connectionStatus) {
        if (CONNECTED == this.connectionStatus || CONNECTING == this.connectionStatus) {
            try {
                this.connectionStatus = connectionStatus == NOT_CONNECTED ? connectionStatus : DISCONNECTED;
                webSocketClient.close();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        close(DISCONNECTED);
    }

    public void close(int code) {
        if (CONNECTED == connectionStatus || CONNECTING == connectionStatus) {
            try {
                connectionStatus = DISCONNECTED;
                webSocketClient.close(code);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect() {
//        if (reconnectAttempt < 120) reconnectAttempt++;
//        long delay = RECONNECT_DELAY * (long) reconnectAttempt;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (DISCONNECTED == connectionStatus &&
                        !TAPChatManager.getInstance(instanceKey).isFinishChatFlow() &&
                        TAPNetworkStateManager.getInstance(instanceKey)
                                .hasNetworkConnection(TapTalk.appContext)) {
                    connectionStatus = CONNECTING;
                    triggerOnSocketConnectingListener();
                    TAPDataManager.getInstance(instanceKey).validateAccessToken(validateAccessView);
                }
            }
        }, 1000);
    }

    public void reconnectOnly() {
        if (DISCONNECTED == connectionStatus &&
                !TAPChatManager.getInstance(instanceKey).isFinishChatFlow()) {
            connectionStatus = CONNECTING;
            triggerOnSocketConnectingListener();
            closeConnectionAndReconnect();
        } else if (CONNECTING == connectionStatus &&
                !TAPChatManager.getInstance(instanceKey).isFinishChatFlow()) {
            closeConnectionAndReconnect();
        }
    }

    private void triggerOnSocketConnectingListener() {
        try {
            List<TapTalkSocketInterface> socketListenersCopy = new ArrayList<>(socketListeners);
            if (null != socketListeners && !socketListenersCopy.isEmpty()) {
                for (TapTalkSocketInterface listener : socketListenersCopy)
                    listener.onSocketConnecting();
            }
        } catch (Exception ignored) {

        }
    }

    private void closeConnectionAndReconnect() {
        close(CLOSE_FOR_RECONNECT_CODE);
        connect();
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    private void createHeaderForConnectWebSocket(Map<String, String> websocketHeader) {
        //Map<String, String> websocketHeader = new HashMap<>();

        String APP_KEY_ID = TAPDataManager.getInstance(instanceKey).getApplicationID();
        String APP_KEY_SECRET = TAPDataManager.getInstance(instanceKey).getApplicationSecret();
        String appKey = Base64.encodeToString((APP_KEY_ID + ":" + APP_KEY_SECRET).getBytes(), Base64.NO_WRAP);

        String userAgent = TAPDataManager.getInstance(instanceKey).getUserAgent();
        String deviceID = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;

        if (TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            websocketHeader.put("Authorization", "Bearer " + TAPDataManager.getInstance(instanceKey).getAccessToken());
            websocketHeader.put("App-Key", appKey);
            websocketHeader.put("Device-Identifier", deviceID);
            websocketHeader.put("Device-Model", android.os.Build.MODEL);
            websocketHeader.put("Device-Platform", "android");
            websocketHeader.put("Device-OS-Version", deviceOsVersion);
            websocketHeader.put("App-Version", BuildConfig.VERSION_NAME);
            websocketHeader.put("User-Agent", userAgent);
        }
        //return websocketHeader;
    }

    private TAPDefaultDataView<TAPErrorModel> validateAccessView = new TAPDefaultDataView<TAPErrorModel>() {
        @Override
        public void onSuccess(TAPErrorModel response) {
            if (CONNECTING == connectionStatus || DISCONNECTED == connectionStatus) {
                reconnectOnly();
            } else if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON && NOT_CONNECTED == connectionStatus) {
                connect(new TapCommonInterface() {
                    @Override
                    public void onSuccess(String successMessage) {
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                    }
                });
            }
        }
    };
}
