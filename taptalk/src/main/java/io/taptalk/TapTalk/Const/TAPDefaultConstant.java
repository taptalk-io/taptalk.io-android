package io.taptalk.TapTalk.Const;

public class TAPDefaultConstant {

    public static final class RoomDatabase {
        public static final int kDatabaseVersion = 1;
    }

    public static final class ConnectionEvent {
        public static final String kEventOpenRoom = "chat/openRoom";
        public static final String kSocketCloseRoom = "chat/closeRoom";
        public static final String kSocketNewMessage = "chat/sendMessage";
        public static final String kSocketUpdateMessage = "chat/updateMessage";
        public static final String kSocketDeleteMessage = "chat/deleteMessage";
        public static final String kSocketOpenMessage = "chat/openMessage";
        public static final String kSocketStartTyping = "chat/startTyping";
        public static final String kSocketStopTyping = "chat/stopTyping";
        public static final String kSocketAuthentication = "user/authentication";
        public static final String kSocketUserOnlineStatus = "user/status";
    }

    public static final class DatabaseType {
        public static final String MESSAGE_DB = "kTAPMessageDB";
        public static final String SEARCH_DB = "kTAPSearchDB";
        public static final String MY_CONTACT_DB = "kTAPMyContactDB";
    }

    public static final class MessageType {
        public static final int TYPE_TEXT = 1001;
        public static final int TYPE_IMAGE = 1002;
        public static final int TYPE_VIDEO = 1003;
        public static final int TYPE_FILE = 1004;
        public static final int TYPE_LOCATION = 1005;
        public static final int TYPE_CONTACT = 1006;
        public static final int TYPE_STICKER = 1007;

        public static final int TYPE_PRODUCT = 2001;
        public static final int TYPE_CATEGORY = 2002;
        public static final int TYPE_ORDER_CARD = 2003;
        public static final int TYPE_CONFIRM_MY_PAYMENT = 2004;

        public static final int TYPE_HIDDEN = 0x9196;
    }

    public static final class BubbleType {
        public static final int TYPE_LOG = 0;
        public static final int TYPE_BUBBLE_TEXT_RIGHT = 1001;
        public static final int TYPE_BUBBLE_TEXT_LEFT = 1002;
        public static final int TYPE_BUBBLE_IMAGE_RIGHT = 1011;
        public static final int TYPE_BUBBLE_IMAGE_LEFT = 1012;
        public static final int TYPE_BUBBLE_PRODUCT_LIST = 2001;
        public static final int TYPE_BUBBLE_ORDER_CARD = 3001;
        public static final int TYPE_EMPTY = 9999;
    }

    public static final class Extras {
        public static final String ROOM_NAME = "kTAPRoomName";
        public static final String MY_ID = "kTAPMyID";
        public static final String GROUP_MEMBERS = "kTAPGroupMembers";
        public static final String GROUP_NAME = "kTAPGroupName";
        public static final String GROUP_IMAGE = "kTAPGroupImage";
        public static final String IS_TYPING = "kTAPIsTyping";
    }

    public static final class RequestCode {
        public static final int CREATE_GROUP = 10;
        public static final int PICK_GROUP_IMAGE = 11;
        public static final int SEND_IMAGE_FROM_CAMERA = 12;
        public static final int SEND_IMAGE_FROM_GALLERY = 13;
        public static final int SEND_IMAGE_FROM_PREVIEW = 14;
    }

    public static final class PermissionRequest {
        public static final int PERMISSION_CAMERA_CAMERA = 1;
        public static final int PERMISSION_READ_EXTERNAL_STORAGE_GALLERY = 2;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA = 3;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE_TO_DISK = 4;
    }

    public static final class Sorting {
        public static final int DESCENDING = 1;
        public static final int ASCENDING = 2;
    }

    public static final class HttpResponseStatusCode {
        public static final int RESPONSE_SUCCESS = 200;
        public static final int RESPONSE_EMPTY = 204;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int TO_MANY_REQUEST = 429;
        public static final int APP_KEY_INVALID = 491;
        public static final int APP_NOT_AVAILABLE = 492;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public static final class Notification {
        public static final String K_FIREBASE_TOKEN = "kTAPFirebaseToken";
        public static final String K_TEXT_REPLY = "kTAPTextReply";
        public static final String K_NOTIFICATION_MESSAGE_MAP = "kTAPNotificationMessageMap";
        public static final int K_REPLY_REQ_CODE = 1;
    }

    public static final class ApiErrorCode {
        public static final int HTTP_HEADER_VALIDATION_FAILED = 40001;
        public static final int API_PARAMETER_VALIDATION_FAILED = 40002;
        public static final int SERVER_KEY_NOT_PROVIDED = 49101;
        public static final int INVALID_SERVER_KEY = 49102;
        public static final int SERVER_KEY_NOT_FOUND = 49103;
        public static final int SERVER_KEY_INCOMPATIBLE = 49104;
        public static final int SERVER_KEY_EXPIRED = 49106;
        public static final int SERVER_KEY_NOT_ENABLED_BY_CUSTOMER = 49201;
        public static final int SERVER_NOT_AVAILABLE = 49202;
        public static final int ERROR_VALIDATING_SERVER_KEY = 50001;
        public static final int OTHER_ERRORS = 99999;
    }

    public static final class UploadBroadcastEvent {
        public static final String UploadProgressLoading = "kTAPUploadProgressLading";
        public static final String UploadProgressFinish = "kTAPUploadProgressFinish";
        public static final String UploadImageData = "kTAPUploadImageData";
        public static final String UploadLocalID = "kTAPUploadLocalID";
        public static final String UploadFailed = "kTAPUploadFailed";
        public static final String UploadFailedErrorMessage = "kTAPUploadFailedErrorMessage";
        public static final String UploadRetried = "kTAPUploadRetried";
        public static final String UploadCancelled = "kTAPUploadCancelled";
    }

    public static final class DownloadBroadcastEvent {
        public static final String DownloadProgressLoading = "kDownloadProgressLoading";
        public static final String DownloadLocalID = "kDownloadLocalID";
        public static final String DownloadFinish = "kDownloadFinish";
    }

    public static final class TokenHeaderConst {
        public static final int NOT_USE_REFRESH_TOKEN = 1;
        public static final int USE_REFRESH_TOKEN = 2;
        public static final int MULTIPART_CONTENT_TYPE = 3;
    }

    public static final class OldDataConst {
        public static final String K_LAST_DELETE_TIMESTAMP = "kTAPLastDeleteTimestamp";
    }

    public static final class ImagePreview {
        public static final String K_IMAGE_RES_CODE = "kTAPImageResultCode";
        public static final String K_IMAGE_URLS = "kTAPImageUrls";
    }

    public static final class MediaType {
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_GIF = "image/gif";
    }

    public static final class CustomHeaderKey {
        public static final String USER_AGENT = "kTAPUserAgent";
        public static final String APP_ID = "kTAPApplicationID";
        public static final String APP_SECRET = "kTAPApplicationSecret";
    }

    public static final String K_REFRESH_TOKEN = "kTAPRefreshToken";
    public static final String K_REFRESH_TOKEN_EXPIRY = "kTAPRefreshTokenExpiry";
    public static final String K_ACCESS_TOKEN = "kTAPAccessToken";
    public static final String K_ACCESS_TOKEN_EXPIRY = "kTAPAccessTokenExpiry";
    public static final String K_AUTH_TICKET = "kTAPAuthTicket";
    public static final String K_MY_USERNAME = "kTAPMyUsername";
    public static final String K_USER = "kTAPUser";
    public static final String K_RECIPIENT_ID = "kTAPRecipientID";
    public static final String K_ROOM = "kTAPRoom";
    public static final String K_LAST_UPDATED = "kTAPLastUpdated";
    public static final String K_IS_ROOM_LIST_SETUP_FINISHED = "kTAPIsRoomListSetupFinished";
    public static final String K_IS_WRITE_STORAGE_PERMISSION_REQUESTED = "kTAPIsWriteStoragePermissionRequested";
    public static final String K_USER_LAST_ACTIVITY = "kTAPUserLastActivity";
    public static final String ENCRYPTION_KEY = "kHT0sVGIKKpnlJE5BNkINYtuf19u6+Kk811iMuWQ5tM";
    public static final String DB_ENCRYPT_PASS = "MoseloOlesom";

    public static final int NUM_OF_ITEM = 50;
    public static final int GROUP_MEMBER_LIMIT = 50;
    public static final int DEFAULT_ANIMATION_TIME = 200;
    public static final int IMAGE_MAX_DIMENSION = 2000;

    public static final long TYPING_EMIT_DELAY = 10000L;
    public static final long TYPING_INDICATOR_TIMEOUT = 15000L;

    public static final String SCAN_RESULT = "kTAPScanResult";
    public static final String ADDED_CONTACT = "kTAPAddedContact";

    public static final int CLOSE_FOR_RECONNECT_CODE = 666;

    public static final String TAP_NOTIFICATION_CHANNEL = "taptalk_channel_id";

    public static final String FILEPROVIDER_AUTHORITY = "io.taptalk.Taptalk.fileprovider";
    public static final String CONTACT_LIST = "kTAPContactList";
}
