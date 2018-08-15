package com.moselo.HomingPigeon.Helper;

public class DefaultConstant {

    public static final class ConnectionEvent{
        public static final String kEventOpenRoom = "chat/openRoom";
        public static final String kSocketCloseRoom = "chat/closeRoom";
        public static final String kSocketSendMessage = "chat/sendMessage";
        public static final String kSocketUpdateMessage = "chat/updateMessage";
        public static final String kSocketDeleteMessage = "chat/deleteMessage";
        public static final String kSocketOpenMessage = "chat/openMessage";
        public static final String kSocketStartTyping = "chat/startTyping";
        public static final String kSocketStopTyping = "chat/stopTyping";
        public static final String kSocketAuthentication = "user/authentication";
        public static final String kSocketUserOnline = "user/online";
        public static final String kSocketUserOffline = "user/offline";
    }

    public static final class ConnectionBroadcast{
        public static final String kIsConnecting = "kIsConnecting";
        public static final String kIsConnected = "kIsConnected";
        public static final String kIsConnectionError = "kIsConnectionError";
        public static final String kIsReconnect = "kIsReconnect";
        public static final String kIsDisconnected = "kIsDisconnected";
    }
}
