package io.taptalk.TapTalk.Const;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import io.taptalk.TapTalk.Helper.TapTalk;

public class TAPDefaultConstant {

    public static final class RoomDatabase {
        public static final int kDatabaseVersion = 2;
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
        public static final int TYPE_ORDER_CARD = 3001;
        public static final int TYPE_CONFIRM_MY_PAYMENT = 2004;

        public static final int TYPE_HIDDEN = 0x9196;
    }

    public static final class BubbleType {
        public static final int TYPE_LOG = 0;
        public static final int TYPE_BUBBLE_TEXT_RIGHT = 10011;
        public static final int TYPE_BUBBLE_TEXT_LEFT = 10012;
        public static final int TYPE_BUBBLE_IMAGE_RIGHT = 10021;
        public static final int TYPE_BUBBLE_IMAGE_LEFT = 10022;
        public static final int TYPE_BUBBLE_VIDEO_RIGHT = 10031;
        public static final int TYPE_BUBBLE_VIDEO_LEFT = 10032;
        public static final int TYPE_BUBBLE_FILE_RIGHT = 10041;
        public static final int TYPE_BUBBLE_FILE_LEFT = 10042;
        public static final int TYPE_BUBBLE_LOCATION_RIGHT = 10051;
        public static final int TYPE_BUBBLE_LOCATION_LEFT = 10052;
        public static final int TYPE_BUBBLE_PRODUCT_LIST = 2001;
        public static final int TYPE_BUBBLE_ORDER_CARD = 3001;
        public static final int TYPE_EMPTY = 9999;
    }

    public static final class MessageData {
        public static final String ITEMS = "items";
        public static final String FILE_ID = "fileID";
        public static final String FILE_NAME = "fileName";
        public static final String IMAGE_URL = "imageURL";
        public static final String MEDIA_TYPE = "mediaType";
        public static final String DURATION = "duration";
        public static final String SIZE = "size";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String CAPTION = "caption";
        public static final String THUMBNAIL = "thumbnail";
        public static final String FILE_URI = "fileUri";
        public static final String USER_INFO = "userInfo";
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }

    public static final class Extras {
        public static final String MESSAGE = "kTAPExtraMessage";
        public static final String ROOM = "kTAPExtraRoom";
        public static final String URL_MESSAGE = "kTAPExtraUrlMessage";
        public static final String COPY_MESSAGE = "kTAPExtraCopyMessage";
        public static final String MY_ID = "kTAPExtraMyID";
        public static final String GROUP_MEMBERS = "kTAPExtraGroupMembers";
        public static final String GROUP_NAME = "kTAPExtraGroupName";
        public static final String GROUP_IMAGE = "kTAPExtraGroupImage";
        public static final String IS_TYPING = "kTAPExtraIsTyping";
        public static final String QUOTE = "kTAPExtraQuote";
        public static final String URI = "kTAPExtraUri";
        public static final String MEDIA_PREVIEWS = "kTAPExtraMediaPreviews";
    }

    public static final class RequestCode {
        public static final int CREATE_GROUP = 10;
        public static final int PICK_GROUP_IMAGE = 11;
        public static final int SEND_IMAGE_FROM_CAMERA = 12;
        public static final int SEND_MEDIA_FROM_GALLERY = 13;
        public static final int SEND_MEDIA_FROM_PREVIEW = 14;
        public static final int FORWARD_MESSAGE = 15;
        public static final int PICK_LOCATION = 16;
        public static final int SEND_FILE = 17;
    }

    public static final class PermissionRequest {
        public static final int PERMISSION_CAMERA_CAMERA = 11;
        public static final int PERMISSION_READ_EXTERNAL_STORAGE_GALLERY = 21;
        public static final int PERMISSION_READ_EXTERNAL_STORAGE_FILE = 22;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA = 31;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE = 32;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO = 34;
        public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE = 35;
        public static final int PERMISSION_LOCATION = 41;
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
        public static final String UploadFileData = "kTAPUploadFileData";
        public static final String UploadLocalID = "kTAPUploadLocalID";
        public static final String UploadFailed = "kTAPUploadFailed";
        public static final String UploadFailedErrorMessage = "kTAPUploadFailedErrorMessage";
        public static final String UploadRetried = "kTAPUploadRetried";
        public static final String UploadCancelled = "kTAPUploadCancelled";
    }

    public static final class DownloadBroadcastEvent {
        public static final String DownloadProgressLoading = "kTAPDownloadProgressLoading";
        public static final String DownloadLocalID = "kTAPDownloadLocalID";
        public static final String DownloadFinish = "kTAPDownloadFinish";
        public static final String DownloadFailed = "kTAPDownloadFailed";
        public static final String DownloadFile = "kTAPDownloadFile";
        public static final String CancelDownload = "kTAPCancelDownload";
        public static final String OpenFile = "kTAPOpenFile";
    }

    public static final class LongPressBroadcastEvent {
        public static final String LongPressChatBubble = "kTAPLongPressChatBubble";
        public static final String LongPressLink = "kTAPLongPressLink";
        public static final String LongPressEmail = "kTAPLongPressEmail";
        public static final String LongPressPhone = "kTAPLongPressPhone";
    }

    public static final class TokenHeaderConst {
        public static final int NOT_USE_REFRESH_TOKEN = 1;
        public static final int USE_REFRESH_TOKEN = 2;
        public static final int MULTIPART_CONTENT_TYPE = 3;
    }

    public static final class OldDataConst {
        public static final String K_LAST_DELETE_TIMESTAMP = "kTAPLastDeleteTimestamp";
    }

    public static final class MediaType {
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_GIF = "image/gif";
    }

    public static final class QuoteAction {
        public static final int REPLY = 1;
        public static final int FORWARD = 2;
    }

    public static final class CustomHeaderKey {
        public static final String USER_AGENT = "kTAPUserAgent";
        public static final String APP_ID = "kTAPApplicationID";
        public static final String APP_SECRET = "kTAPApplicationSecret";
    }

    public static final class Location {
        public static final String LONGITUDE = "kTAPlongitude";
        public static final String LATITUDE = "kTAPlatitude";
        public static final String LOCATION_NAME = "kTAPlocationName";
        public static final String POSTAL_CODE = "kTAPPostalCode";
    }

    public static final class BaseUrl {
        public static final String BASE_URL_API_PRODUCTION = "https://hp.moselo.com/api/v1/";
        public static final String BASE_URL_SOCKET_PRODUCTION = "https://hp.moselo.com/";
        public static final String BASE_WSS_PRODUCTION = "wss://hp.moselo.com/pigeon";

        public static final String BASE_URL_API_STAGING = "https://hp-staging.moselo.com/api/v1/";
        public static final String BASE_URL_SOCKET_STAGING = "https://hp-staging.moselo.com/";
        public static final String BASE_WSS_STAGING = "wss://hp-staging.moselo.com/pigeon";

        public static final String BASE_URL_API_DEVELOPMENT = "https://hp-dev.moselo.com/api/v1/";
        public static final String BASE_URL_SOCKET_DEVELOPMENT = "https://hp-dev.moselo.com/";
        public static final String BASE_WSS_DEVELOPMENT = "wss://hp-dev.moselo.com/pigeon";
    }

    public static final Intent[] AUTO_START_INTENTS = {
            new Intent().setComponent(new ComponentName("com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
                    Uri.parse("mobilemanager://function/entry/AutoStart"))
    };

    public static final String K_REFRESH_TOKEN = "kTAPRefreshToken";
    public static final String K_REFRESH_TOKEN_EXPIRY = "kTAPRefreshTokenExpiry";
    public static final String K_ACCESS_TOKEN = "kTAPAccessToken";
    public static final String K_ACCESS_TOKEN_EXPIRY = "kTAPAccessTokenExpiry";
    public static final String K_AUTH_TICKET = "kTAPAuthTicket";
    public static final String K_MY_USERNAME = "kTAPMyUsername";
    public static final String K_USER = "kTAPUser";
    public static final String K_RECIPIENT_ID = "kTAPRecipientID";
    public static final String K_LAST_UPDATED = "kTAPLastUpdated";
    public static final String K_IS_ROOM_LIST_SETUP_FINISHED = "kTAPIsRoomListSetupFinished";
    public static final String K_IS_WRITE_STORAGE_PERMISSION_REQUESTED = "kTAPIsWriteStoragePermissionRequested";
    public static final String K_USER_LAST_ACTIVITY = "kTAPUserLastActivity";
    public static final String K_FILE_URI_MAP = "kTAPFileUriMap";
    public static final String K_FILE_PATH_MAP = "kTAPFilePathMap";
    public static final String K_MEDIA_VOLUME = "kTAPMediaVolume";
    public static final String ENCRYPTION_KEY = "kHT0sVGIKKpnlJE5BNkINYtuf19u6+Kk811iMuWQ5tM";
    public static final String DB_ENCRYPT_PASS = "MoseloOlesom";

    public static final int NUM_OF_ITEM = 50;
    public static final int GROUP_MEMBER_LIMIT = 50;
    public static final int DEFAULT_ANIMATION_TIME = 200;
    public static final int IMAGE_MAX_DIMENSION = 2000;
    public static final int THUMB_MAX_DIMENSION = 20;
    public static final int IMAGE_COMPRESSION_QUALITY = 50;

    public static final long TYPING_EMIT_DELAY = 10000L;
    public static final long TYPING_INDICATOR_TIMEOUT = 15000L;

    public static final String SCAN_RESULT = "kTAPScanResult";
    public static final String ADDED_CONTACT = "kTAPAddedContact";

    public static final int CLOSE_FOR_RECONNECT_CODE = 666;

    public static final String TAP_NOTIFICATION_CHANNEL = "taptalk_channel_id";

    public static final String FILEPROVIDER_AUTHORITY = TapTalk.appContext.getPackageName() + ".fileprovider";
    public static final String CONTACT_LIST = "kTAPContactList";
    public static final String REFRESH_TOKEN_RENEWED = "TAPRefreshTokenRenewed";
}
