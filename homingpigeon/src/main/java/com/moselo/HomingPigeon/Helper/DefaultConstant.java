package com.moselo.HomingPigeon.Helper;

public class DefaultConstant {

    public static final class RoomDatabase{
        public static final int kDatabaseVersion = 1;
    }

    public static final class ConnectionEvent{
        public static final String kEventOpenRoom = "chat/openRoom";
        public static final String kSocketCloseRoom = "chat/closeRoom";
        public static final String kSocketNewMessage = "chat/sendMessage";
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

    public static final class DatabaseType{
        public static final String MESSAGE_DB = "MessageDB";
    }

    public static final class MessageType {
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_IMAGE = 2;
        public static final int TYPE_FILE = 4;
        public static final int TYPE_LOCATION = 5;
        public static final int TYPE_CONTACT = 6;
        public static final int TYPE_STICKER = 3;
        public static final int TYPE_NEW_USER = 1000;
        public static final int TYPE_USER_LEAVE = 1001;
        public static final int TYPE_SEE_SERVICES_N_PRICES = 8001;
        public static final int TYPE_CONFIRM_MY_PAYMENT = 9004;
        public static final int TYPE_READ_EXPERT_NOTES = 8005;
        public static final int TYPE_PRODUCT = 9001;
        public static final int TYPE_CATEGORY = 9002;
        public static final int TYPE_ORDER_CARD = 9003;
        public static final int TYPE_HIDDEN = 0x9196;
        public static final int TYPE_WELCOME = 8006;
        public static final int TYPE_ASK_PRODUCT = 9007;
    }

    public static final String K_USER = "kUser";
    public static final String K_ROOM_ID = "kRoomID";
    public static final String ENCRYPTION_KEY = "kHT0sVGIKKpnlJE5BNkINYtuf19u6+Kk811iMuWQ5tM";
    public static final String DB_ENCRYPT_PASS = "MoseloOlesom";

    public static final int NUM_OF_ITEM = 10;


}
