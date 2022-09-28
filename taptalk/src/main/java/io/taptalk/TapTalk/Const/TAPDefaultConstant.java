package io.taptalk.TapTalk.Const;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import io.taptalk.TapTalk.Helper.TapTalk;

public class TAPDefaultConstant {

    public static final class RoomDatabase {
        public static final int kDatabaseVersion = 10;
        public static final String DATABASE_NAME = "message_database";
    }

    public static final class DatabaseType {
        public static final String MESSAGE_DB = "kTAPMessageDB";
        public static final String SEARCH_DB = "kTAPSearchDB";
        public static final String MY_CONTACT_DB = "kTAPMyContactDB";
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
        public static final String kSocketUserUpdated = "user/updated";
        public static final String kSocketClearChat = "room/clearChat";
    }

    public static final class RoomType {
        public static final int TYPE_PERSONAL = 1;
        public static final int TYPE_GROUP = 2;
        public static final int TYPE_CHANNEL = 3;
        public static final int TYPE_TRANSACTION = 4;
    }

    public static final class MessageType {
        public static final int TYPE_TEXT = 1001;
        public static final int TYPE_IMAGE = 1002;
        public static final int TYPE_VIDEO = 1003;
        public static final int TYPE_FILE = 1004;
        public static final int TYPE_LOCATION = 1005;
        public static final int TYPE_CONTACT = 1006;
        public static final int TYPE_STICKER = 1007;
        public static final int TYPE_VOICE = 1008;
        public static final int TYPE_AUDIO = 1009;
        public static final int TYPE_LINK = 1010;

        public static final int TYPE_PRODUCT = 2001;
        public static final int TYPE_CATEGORY = 2002;
        public static final int TYPE_CONFIRM_MY_PAYMENT = 2004;

        public static final int TYPE_HIDDEN = 0x9196;

        public static final int TYPE_SYSTEM_MESSAGE = 9001;
        public static final int TYPE_UNREAD_MESSAGE_IDENTIFIER = 9002;
        public static final int TYPE_LOADING_MESSAGE_IDENTIFIER = 9003;
        public static final int TYPE_DATE_SEPARATOR = 9005;
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
        public static final int TYPE_BUBBLE_VOICE_RIGHT = 10043;
        public static final int TYPE_BUBBLE_VOICE_LEFT = 10044;
        public static final int TYPE_BUBBLE_LOCATION_RIGHT = 10051;
        public static final int TYPE_BUBBLE_LOCATION_LEFT = 10052;
        public static final int TYPE_BUBBLE_PRODUCT_LIST = 2001;
        public static final int TYPE_BUBBLE_SYSTEM_MESSAGE = 9001;
        public static final int TYPE_BUBBLE_UNREAD_STATUS = 9002;
        public static final int TYPE_BUBBLE_LOADING = 9003;
        public static final int TYPE_BUBBLE_DELETED_RIGHT = 90041;
        public static final int TYPE_BUBBLE_DELETED_LEFT = 90042;
        public static final int TYPE_BUBBLE_DATE_SEPARATOR = 9005;
        public static final int TYPE_EMPTY = 9999;
    }

    public static final class MessageData {
        public static final String ITEMS = "items";
        public static final String FILE_ID = "fileID";
        public static final String FILE_NAME = "fileName";
        public static final String FILE_URL = "url";
        public static final String URL = "url";
        public static final String URLS = "urls";
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
        public static final String IS_PLAYING = "isPLaying";
    }

    public static final class QuoteFileType {
        public static final String FILE = "file";
        public static final String IMAGE = "image";
        public static final String VIDEO = "video";
    }

    public static final class IntentType {
        public static final String INTENT_TYPE_ALL = "*/*";
        public static final String INTENT_TYPE_IMAGE = "image/*";
        public static final String INTENT_TYPE_VIDEO = "video/*";
        public static final String GALLERY = "Gallery";
        public static final String SELECT_PICTURE = "Select Picture";
        public static final String OPEN_FILE = "Open File";
    }

    public static final class Extras {
        public static final String INSTANCE_KEY = "kTAPExtraInstanceKey";
        public static final String MESSAGE = "kTAPExtraMessage";
        public static final String ROOM = "kTAPExtraRoom";
        public static final String ROOM_ID = "kTAPExtraRoomID";
        public static final String URL_MESSAGE = "kTAPExtraUrlMessage";
        public static final String COPY_MESSAGE = "kTAPExtraCopyMessage";
        public static final String MY_ID = "kTAPExtraMyID";
        public static final String GROUP_MEMBERS = "kTAPExtraGroupMembers";
        public static final String GROUP_MEMBER_IDS = "kTAPExtraGroupMembersIDs";
        public static final String GROUP_NAME = "kTAPExtraGroupName";
        public static final String GROUP_IMAGE = "kTAPExtraGroupImage";
        public static final String GROUP_ACTION = "kTAPExtraGroupAction";
        public static final String IS_TYPING = "kTAPExtraIsTyping";
        public static final String GROUP_TYPING_MAP = "kTAPExtraGroupTypingMap";
        public static final String IS_ADMIN = "kTAPExtraIsAdmin";
        public static final String QUOTE = "kTAPExtraQuote";
        public static final String URI = "kTAPExtraUri";
        public static final String MEDIA_PREVIEWS = "kTAPExtraMediaPreviews";
        public static final String COUNTRY_LIST = "kTAPExtraCountryList";
        public static final String COUNTRY_ID = "kTAPExtraCountryID";
        public static final String COUNTRY_CALLING_CODE = "kTAPExtraCountryCallingCode";
        public static final String COUNTRY_FLAG_URL = "kTAPExtraCountryFlagUrl";
        public static final String MOBILE_NUMBER = "kTAPExtraMobileNumber";
        public static final String JUMP_TO_MESSAGE = "kTAPJumpToMessage";
        public static final String CLOSE_ACTIVITY = "kTAPCloseActivity";
        public static final String IS_NON_PARTICIPANT_USER_PROFILE = "kTAPIsNotParticipantUserProfile";
        public static final String FROM_NOTIF = "kTAPFromNotification";
        public static final String SOCKET_CONNECTED = "kSocketConnected";
        public static final String IS_NEED_REFRESH = "kNeedRefresh";
    }

    public static final class RequestCode {
        public static final int COUNTRY_PICK = 11;
        public static final int EDIT_PROFILE = 21;
        public static final int OPEN_GROUP_PROFILE = 22;
        public static final int OPEN_MEMBER_PROFILE = 23;
        public static final int OPEN_PERSONAL_PROFILE = 24;
        public static final int REGISTER = 31;
        public static final int GROUP_ADD_MEMBER = 4;
        public static final int GROUP_UPDATE_DATA = 41;
        public static final int GROUP_OPEN_MEMBER_PROFILE = 42;
        public static final int SEND_IMAGE_FROM_CAMERA = 51;
        public static final int SEND_MEDIA_FROM_GALLERY = 52;
        public static final int SEND_MEDIA_FROM_PREVIEW = 53;
        public static final int SEND_FILE = 54;
        public static final int FORWARD_MESSAGE = 61;
        public static final int PICK_LOCATION = 62;
        public static final int CREATE_GROUP = 71;
        public static final int EDIT_GROUP = 81;
        public static final int PICK_PROFILE_IMAGE_CAMERA = 91;
        public static final int PICK_PROFILE_IMAGE_GALLERY = 92;
        public static final int PICK_GROUP_IMAGE_CAMERA = 93;
        public static final int PICK_GROUP_IMAGE_GALLERY = 94;
        public static final int OPEN_STARRED_MESSAGES = 95;
        public static final int OPEN_PINNED_MESSAGES = 96;
        public static final int OPEN_SHARED_MEDIA = 97;
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
        public static final int PERMISSION_READ_CONTACT = 51;
        public static final int PERMISSION_RECORD_AUDIO = 61;
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
        public static final String TAP_NOTIFICATION_CHANNEL = "taptalk_channel_id";
        public static final String NOTIFICATION_CHANNEL_DEFAULT_NAME = "Chat Notifications";
        public static final String NOTIFICATION_CHANNEL_DEFAULT_DESCRIPTION = "TapTalk Notifications";
        public static final String NOTIFICATION_GROUP_DEFAULT = "TapTalkDefaultNotificationGroup";
        public static final String NEW_MESSAGE = "New Message";
        public static final String REPLY = "Reply";
        public static final String K_FIREBASE_TOKEN = "kTAPFirebaseToken";
        public static final String K_TEXT_REPLY = "kTAPTextReply";
        public static final String K_NOTIFICATION_MESSAGE_MAP = "kTAPNotificationMessageMap";
        public static final int K_REPLY_REQ_CODE = 1;
    }

    public static final class ApiErrorCode {
        public static final String HTTP_HEADER_VALIDATION_FAILED = "40001";
        public static final String API_PARAMETER_VALIDATION_FAILED = "40002";
        public static final String HTTP_HEADER_AUTHORIZATION_NOT_PROVIDED = "40101";
        public static final String HTTP_HEADER_AUTHORIZATION_INVALID = "40102";
        public static final String INVALID_TOKEN = "40103";
        public static final String TOKEN_EXPIRED = "40104";
        public static final String TOKEN_DOES_NOT_BELONG_TO_CLIENT = "40105";
        public static final String USER_NOT_FOUND_FOR_THE_SPECIFIED_TOKEN = "40106";
        public static final String PERMISSION_DENIED = "40301";
        public static final String USER_NOT_FOUND = "40401";
        public static final String ROOM_NOT_FOUND = "40401";
        public static final String SERVER_KEY_NOT_PROVIDED = "49101";
        public static final String INVALID_SERVER_KEY = "49102";
        public static final String SERVER_KEY_NOT_FOUND = "49103";
        public static final String SERVER_KEY_INCOMPATIBLE = "49104";
        public static final String SERVER_KEY_EXPIRED = "49106";
        public static final String SERVER_KEY_NOT_ENABLED_BY_CUSTOMER = "49201";
        public static final String SERVER_NOT_AVAILABLE = "49202";
        public static final String ERROR_VALIDATING_SERVER_KEY = "50001";
        public static final String OTHER_ERRORS = "99999";
    }

    public static final class ClientErrorCodes {
        public static final String ERROR_CODE_INIT_TAPTALK = "90000";
        public static final String ERROR_CODE_ACTIVE_USER_NOT_FOUND = "90001";
        public static final String ERROR_CODE_ACCESS_TOKEN_UNAVAILABLE = "90002";
        public static final String ERROR_CODE_ALREADY_CONNECTED = "90003";
        public static final String ERROR_CODE_NO_INTERNET = "90004";
        public static final String ERROR_CODE_INVALID_AUTH_TICKET = "90005";
        public static final String ERROR_CODE_NOT_AUTHENTICATED = "90006";
        public static final String ERROR_CODE_GROUP_DELETED = "90101";
        public static final String ERROR_CODE_ADMIN_REQUIRED = "90102";
        public static final String ERROR_CODE_URI_NOT_FOUND = "90301";
        public static final String ERROR_CODE_EXCEEDED_MAX_SIZE = "90302";
        public static final String ERROR_CODE_UPLOAD_CANCELLED = "90303";
        public static final String ERROR_CODE_IMAGE_UNAVAILABLE = "90304";
        public static final String ERROR_CODE_DOWNLOAD_INVALID_MESSAGE_TYPE = "90305";
        public static final String ERROR_CODE_CAPTION_EXCEEDS_LIMIT = "90306";
        public static final String ERROR_CODE_PRODUCT_EMPTY = "90307";
        public static final String ERROR_CODE_DOWNLOAD_CANCELLED = "90308";
        public static final String ERROR_CODE_EDIT_INVALID_MESSAGE_TYPE = "90309";
        public static final String ERROR_CODE_CHAT_ROOM_NOT_FOUND = "90404";
        public static final String ERROR_CODE_OTHERS = "99999";
    }

    public static final class ClientErrorMessages {
        public static final String ERROR_MESSAGE_INIT_TAPTALK = "Please initialize TapTalk library, read documentation for detailed information.";
        public static final String ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND = "Active user not found";
        public static final String ERROR_MESSAGE_ACCESS_TOKEN_UNAVAILABLE = "Access token is not available, call authenticate() before connecting";
        public static final String ERROR_MESSAGE_ALREADY_CONNECTED = "Already connected";
        public static final String ERROR_MESSAGE_NO_INTERNET = "No internet connection";
        public static final String ERROR_MESSAGE_INVALID_AUTH_TICKET = "Invalid auth ticket";
        public static final String ERROR_MESSAGE_NOT_AUTHENTICATED = "User is not authenticated";
        public static final String ERROR_MESSAGE_GROUP_DELETED = "The group has already been deleted";
        public static final String ERROR_MESSAGE_ADMIN_REQUIRED = "Please assign another admin before leaving";
        public static final String ERROR_MESSAGE_URI_NOT_FOUND = "Uri is required in message data";
        public static final String ERROR_MESSAGE_EXCEEDED_MAX_SIZE = "Selected file exceeded %s upload limit";
        public static final String ERROR_MESSAGE_UPLOAD_CANCELLED = "Upload was cancelled";
        public static final String ERROR_MESSAGE_DOWNLOAD_CANCELLED = "Download was cancelled";
        public static final String ERROR_MESSAGE_IMAGE_UNAVAILABLE = "Could not process compressed image";
        public static final String ERROR_MESSAGE_DOWNLOAD_INVALID_MESSAGE_TYPE = "Invalid message type. Allowed types are image (1002), video (1003), file (1004)";
        public static final String ERROR_MESSAGE_CAPTION_EXCEEDS_LIMIT = "Message or caption exceeds the %d character limit";
        public static final String ERROR_MESSAGE_PRODUCT_EMPTY = "Provided product list is empty";
        public static final String ERROR_MESSAGE_CHAT_ROOM_NOT_FOUND = "Selected chat room is not found";
        public static final String ERROR_MESSAGE_EDIT_INVALID_MESSAGE_TYPE = "Invalid message type. Allowed types are text (1001), image (1002), video (1003)";
    }

    public static final class ClientSuccessMessages {
        public static final String SUCCESS_MESSAGE_CONNECT = "Connected successfully";
        public static final String SUCCESS_MESSAGE_REFRESH_CONFIG = "Project configs refreshed successfully";
        public static final String SUCCESS_MESSAGE_DELETE_GROUP = "Chat room deleted successfully";
        public static final String SUCCESS_MESSAGE_LEAVE_GROUP = "Left chat room successfully";
        public static final String SUCCESS_MESSAGE_AUTHENTICATE = "Authenticated successfully";
        public static final String SUCCESS_MESSAGE_REFRESH_ACTIVE_USER = "Active user refreshed successfully";
        public static final String SUCCESS_MESSAGE_OPEN_ROOM = "Opened chat room successfully";
    }

    public static final class UploadBroadcastEvent {
        public static final String UploadProgressLoading = "kTAPUploadProgressLading";
        public static final String UploadProgressFinish = "kTAPUploadProgressFinish";
        public static final String UploadImageData = "kTAPUploadImageData";
        public static final String UploadFileData = "kTAPUploadFileData";
        public static final String UploadLocalID = "kTAPUploadLocalID";
        public static final String UploadFailed = "kTAPUploadFailed";
        public static final String UploadFailedErrorMessage = "kTAPUploadFailedErrorMessage";
        //        public static final String UploadRetried = "kTAPUploadRetried";
        public static final String UploadCancelled = "kTAPUploadCancelled";
        public static final String UploadProgress = "kTAPUploadProgress";
    }

    public static final class DownloadBroadcastEvent {
        public static final String DownloadProgressLoading = "kTAPDownloadProgressLoading";
        public static final String DownloadLocalID = "kTAPDownloadLocalID";
        public static final String DownloadFinish = "kTAPDownloadFinish";
        public static final String DownloadFailed = "kTAPDownloadFailed";
        public static final String DownloadFile = "kTAPDownloadFile";
        public static final String CancelDownload = "kTAPCancelDownload";
        public static final String OpenFile = "kTAPOpenFile";
        public static final String DownloadedFile = "kTAPDownloadedFile";
        public static final String DownloadErrorCode = "kTAPDownloadErrorCode";
        public static final String DownloadErrorMessage = "kTAPDownloadErrorMessage";
        public static final String PlayPauseVoiceNote = "kTAPPlayPauseVoiceNote";
    }

    public static final class LongPressBroadcastEvent {
        public static final String LongPressChatBubble = "kTAPLongPressChatBubble";
        public static final String LongPressLink = "kTAPLongPressLink";
        public static final String LongPressEmail = "kTAPLongPressEmail";
        public static final String LongPressPhone = "kTAPLongPressPhone";
        public static final String LongPressMention = "kTAPLongPressMention";
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
        public static final int EDIT = 3;
    }

    public static final class SystemMessageAction {
        public static final String CREATE_ROOM = "room/create";
        public static final String UPDATE_ROOM = "room/update";
        public static final String DELETE_ROOM = "room/delete";
        public static final String LEAVE_ROOM = "room/leave";
        public static final String ROOM_ADD_PARTICIPANT = "room/addParticipant";
        public static final String ROOM_REMOVE_PARTICIPANT = "room/removeParticipant";
        public static final String ROOM_PROMOTE_ADMIN = "room/promoteAdmin";
        public static final String ROOM_DEMOTE_ADMIN = "room/demoteAdmin";
        public static final String UPDATE_USER = "user/update";
        public static final String DELETE_USER = "user/delete";
        public static final String PIN_MESSAGE = "message/pin";
        public static final String UNPIN_MESSAGE = "message/unpin";
    }

    public static final class ProjectConfigType {
        public static final String CORE = "kTAPConfigCore";
        public static final String PROJECT = "kTAPConfigProject";
        public static final String CUSTOM = "kTAPConfigCustom";
    }

    public static final class ProjectConfigKeys {
        public static final String CHANNEL_MAX_PARTICIPANTS = "channelMaxParticipants";
        public static final String CHAT_MEDIA_MAX_FILE_SIZE = "chatMediaMaxFileSize";
        public static final String GROUP_MAX_PARTICIPANTS = "groupMaxParticipants";
        public static final String ROOM_PHOTO_MAX_FILE_SIZE = "roomPhotoMaxFileSize";
        public static final String USER_PHOTO_MAX_FILE_SIZE = "userPhotoMaxFileSize";
        public static final String ROOM_MAX_PINNED = "roomMaxPinned";

        public static final String USERNAME_IGNORE_CASE = "usernameIgnoreCase";
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

    public static class ChatProfileMenuType {
        public static final int MENU_NOTIFICATION = 1;
        public static final int MENU_ROOM_COLOR = 2;
        public static final int MENU_ROOM_SEARCH_CHAT = 3;
        public static final int MENU_BLOCK = 4;
        public static final int MENU_CLEAR_CHAT = 5;
        public static final int MENU_VIEW_MEMBERS = 6;
        public static final int MENU_EXIT_GROUP = 7;
        public static final int MENU_ADD_TO_CONTACTS = 8;
        public static final int MENU_SEND_MESSAGE = 9;
        public static final int MENU_PROMOTE_ADMIN = 10;
        public static final int MENU_DEMOTE_ADMIN = 11;
        public static final int MENU_REMOVE_MEMBER = 12;
        public static final int MENU_DELETE_GROUP = 13;
        public static final int MENU_EDIT_GROUP = 14;
        public static final int MENU_REPORT = 15;
        public static final int MENU_STARRED_MESSAGES = 16;
        public static final int MENU_SHARED_MEDIA = 17;
    }

    public static final String K_REFRESH_TOKEN = "kTAPRefreshToken";
    public static final String K_REFRESH_TOKEN_EXPIRY = "kTAPRefreshTokenExpiry";
    public static final String K_ACCESS_TOKEN = "kTAPAccessToken";
    public static final String K_ACCESS_TOKEN_EXPIRY = "kTAPAccessTokenExpiry";
    public static final String K_AUTH_TICKET = "kTAPAuthTicket";
    public static final String K_MY_USERNAME = "kTAPMyUsername";
    public static final String K_USER = "kTAPUser";
    public static final String K_USER_ID = "kTAPUserID";
    public static final String K_RECIPIENT_ID = "kTAPRecipientID";
    public static final String K_LAST_UPDATED = "kTAPLastUpdated";
    public static final String K_LAST_ROOM_MESSAGE_DELETE_TIME = "kTAPLastRoomMessageDeleteTime";
    public static final String K_IS_ROOM_LIST_SETUP_FINISHED = "kTAPIsRoomListSetupFinished";
    public static final String K_USER_LAST_ACTIVITY = "kTAPUserLastActivity";
    public static final String K_GROUP_DATA_MAP = "kGroupDataMap";
    public static final String K_FILE_URI_MAP = "kTAPFileUriMap";
    public static final String K_FILE_PATH_MAP = "kTAPFilePathMap";
    public static final String K_MEDIA_VOLUME = "kTAPMediaVolume";
    public static final String IS_PERMISSION_SYNC_ASKED = "kTAPIsPermissionSyncAsked";
    public static final String IS_CONTACT_SYNC_ALLOWED_BY_USER = "kTAPIsContactSyncAllowedByUser";
    public static final String K_CHAT_ROOM_CONTACT_ACTION = "kTAPChatRoomContactAction";
    public static final String K_IS_CONTACT_LIST_UPDATED = "kTAPIsContactListUpdated";
    public static final String K_UNREAD_ROOM_LIST = "kTAPUnreadRoomList";
    public static final String K_PINNED_MESSAGE = "kTAPPinnedMessage";
    public static final String K_MUTED_ROOM_LIST = "kTAPMutedRoomList";
    public static final String K_PINNED_ROOM_LIST = "kTAPPinnedRoomList";
    public static final String K_STARRED_MESSAGE = "kTAPStarredMessage";

    public static final String ENCRYPTION_KEY = "kHT0sVGIKKpnlJE5BNkINYtuf19u6+Kk811iMuWQ5tM";

    public static final String DEFAULT_CHAT_MEDIA_MAX_FILE_SIZE = "26214400";
    public static final String DEFAULT_ROOM_PHOTO_MAX_FILE_SIZE = "10485760";
    public static final String DEFAULT_USER_PHOTO_MAX_FILE_SIZE = "10485760";
    public static final String DEFAULT_GROUP_MAX_PARTICIPANTS = "100";
    public static final String DEFAULT_CHANNEL_MAX_PARTICIPANTS = "5000";
    public static final String DEFAULT_ROOM_MAX_PINNED= "10";

    public static final int CHARACTER_LIMIT = 4000;
    public static final int MAX_ITEMS_PER_PAGE = 50;
    public static final int MAX_PRODUCT_SIZE = 20;
    public static final int DEFAULT_MAX_CAPTION_LENGTH = 100;
    public static final int SHORT_ANIMATION_TIME = 100;
    public static final int DEFAULT_ANIMATION_TIME = 200;
    public static final int IMAGE_MAX_DIMENSION = 2000;
    public static final int THUMB_MAX_DIMENSION = 20;
    public static final int DEFAULT_IMAGE_COMPRESSION_QUALITY = 50;

    public static final long TYPING_EMIT_DELAY = 7000L;
    public static final long TYPING_INDICATOR_TIMEOUT = 10000L;

    public static final String SCAN_RESULT = "kTAPScanResult";
    public static final String ADDED_CONTACT = "kTAPAddedContact";

    public static final int CLOSE_FOR_RECONNECT_CODE = 666;

    public static final String FILEPROVIDER_AUTHORITY = TapTalk.appContext.getPackageName() + ".fileprovider";
    public static final String CONTACT_LIST = "kTAPContactList";
    public static final String REFRESH_TOKEN_RENEWED = "kTAPRefreshTokenRenewed";
    public static final String RELOAD_ROOM_LIST = "kTAPReloadRoomList";
    public static final String CLEAR_ROOM_LIST = "kTAPClearRoomList";
    public static final String CLEAR_ROOM_LIST_BADGE = "kTAPClearRoomListBadge";
    public static final String RELOAD_PROFILE_PICTURE = "kTAPReloadProfilePicture";
    public static final String OPEN_CHAT = "kTAPOpenChat";
    public static final String LAST_CALL_COUNTRY_TIMESTAMP = "kLastCallCountryTimestamp";
    public static final String K_COUNTRY_LIST = "kTAPCountryList";
    public static final String K_COUNTRY_PICK = "kTAPCountryPick";
    public static final String MY_COUNTRY_CODE = "kMyCountryCode";
    public static final String MY_COUNTRY_FLAG_URL = "kMyCountryFlagUrl";
    public static final String UNREAD_INDICATOR_LOCAL_ID = "kTAPUnreadIndicatorLocalIdentity";
    public static final String LOADING_INDICATOR_LOCAL_ID = "kTAPFetchingOlderMessagesLocalID";
    public static final String RIGHT_BUBBLE_SPACE_APPEND = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String LEFT_BUBBLE_SPACE_APPEND = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
}
