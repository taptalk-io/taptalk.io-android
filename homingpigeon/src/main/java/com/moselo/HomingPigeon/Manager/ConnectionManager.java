package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Model.EmitModel;
import com.moselo.HomingPigeon.Model.MessageModel;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ConnectionManager {

    private static ConnectionManager instance;
    private WebSocketClient mWebSocketClient;
    private String webSocketEndpoint = "ws://35.198.219.49:8080/ws";
    private URI webSocketUri;
    private HomingPigeonSocketListener listener;

    public enum ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED
    }

    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;


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
                connectionStatus = ConnectionStatus.CONNECTING;
                Log.e(ConnectionManager.class.getSimpleName(), "onOpen: "+HomingPigeon.appContext );
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsConnecting);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onMessage(String message) {
//                Log.e(ConnectionManager.class.getSimpleName(), "onMessage2: "+message);
//                JSONObject object = new JSONObject();
//                try {
//                    object.put("message",message);
//                    listener.onNewMessage(DefaultConstant.ConnectionEvent.kSocketNewMessage,object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                String tempMessage = StandardCharsets.UTF_8.decode(bytes).toString();
                Log.e(ConnectionManager.class.getSimpleName(), tempMessage );
                EmitModel<MessageModel> tempObject = Utils.getInstance().fromJSON(new TypeReference<EmitModel<MessageModel>>() {},tempMessage);
                listener.onNewMessage(tempObject.getEventName(), tempObject.getData());
//                JSONObject object = new JSONObject();
//                try {
//                    object.put("message",tempMessage);
//                    Log.e(ConnectionManager.class.getSimpleName(), "onMessage: "+object.get("message"));
//                    listener.onNewMessage(DefaultConstant.ConnectionEvent.kSocketNewMessage,object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                super.onMessage(bytes);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsDisconnected);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Exception ex) {
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsConnectionError);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
                }
            }

            @Override
            public void reconnect() {
                super.reconnect();
                if (null != HomingPigeon.appContext) {
                    Intent intent = new Intent(DefaultConstant.ConnectionBroadcast.kIsReconnect);
                    LocalBroadcastManager.getInstance(HomingPigeon.appContext).sendBroadcast(intent);
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
        Log.e(ConnectionManager.class.getSimpleName(), messageString +" & "+messageString.getBytes(StandardCharsets.UTF_8) );
        mWebSocketClient.send(messageString.getBytes(StandardCharsets.UTF_8));
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
