package io.taptalk.TapTalk.View.Activity;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CHARACTER_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_CAPTION_EXCEEDS_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.LinkPreviewImageLoaded;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.PlayPauseVoiceNote;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.CLOSE_ACTIVITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.DATA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_TYPING_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_NEED_REFRESH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.JUMP_TO_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MEDIA_PREVIEWS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.SOCKET_CONNECTED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LOCATION_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressMention;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DESCRIPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IS_PLAYING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TITLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_AUDIO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OPEN_CHAT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_RECORD_AUDIO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.EDIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.FORWARD_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_GROUP_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_PERSONAL_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_PINNED_MESSAGES;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_PREVIEW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.PIN_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_ADD_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_REMOVE_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UNPIN_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_EMIT_DELAY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UNREAD_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.getTapTalkListeners;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.CHAT_BUBBLE_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.EMAIL_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.LINK_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.MENTION_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.PHONE_TYPE;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.DEFAULT;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.FINISH;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.HOLD_RECORD;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.LOCKED_RECORD;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.PAUSE;
import static io.taptalk.TapTalk.ViewModel.TAPChatViewModel.RECORDING_STATE.PLAY;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.MaxHeightRecyclerView;
import io.taptalk.TapTalk.Helper.OnSwipeTouchListener;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Helper.TapVerticalIndicator;
import io.taptalk.TapTalk.Helper.audiorecorder.TapAudioManager;
import io.taptalk.TapTalk.Interface.TapAudioListener;
import io.taptalk.TapTalk.Interface.TapLongPressInterface;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPGeneralListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener;
import io.taptalk.TapTalk.Listener.TapCoreGetIntegerListener;
import io.taptalk.TapTalk.Listener.TapCoreGetOlderMessageListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Manager.TapCoreContactManager;
import io.taptalk.TapTalk.Manager.TapCoreMessageManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.Model.TapLongPressMenuItem;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;
import io.taptalk.TapTalk.View.Adapter.TapUserMentionListAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet;
import io.taptalk.TapTalk.View.BottomSheet.TapTimePickerBottomSheetFragment;
import io.taptalk.TapTalk.View.Fragment.TAPConnectionStatusFragment;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;
import io.taptalk.TapTalk.databinding.TapActivityChatBinding;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TapUIChatActivity extends TAPBaseActivity {

    private String TAG = TapUIChatActivity.class.getSimpleName();

    // View
    private TapActivityChatBinding vb;
//    private SwipeBackLayout sblChat;
    private TAPChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private MaxHeightRecyclerView rvUserMentionList;
    private FrameLayout flMessageList;
    private FrameLayout flRoomUnavailable;
    private FrameLayout flLoading;
    private LinearLayout llButtonDeleteChat;
    private TextView tvDeleteChat;
    private ImageView ivDelete;
    private ProgressBar pbDelete;
    private ConstraintLayout clContainer;
    private ConstraintLayout clActionBar;
    private ConstraintLayout clContactAction;
    private ConstraintLayout clUnreadButton;
    private ConstraintLayout clChatComposerAndHistory;
    private ConstraintLayout clEmptyChat;
    private ConstraintLayout clQuote;
    private ConstraintLayout clChatComposer;
    private ConstraintLayout clUserMentionList;
    private ConstraintLayout clRoomStatus;
    private ConstraintLayout clRoomOnlineStatus;
    private ConstraintLayout clRoomTypingStatus;
    private ConstraintLayout clChatHistory;
    private EditText etChat;
    private ImageView ivButtonBack;
    private ImageView ivRoomIcon;
    private ImageView ivButtonDismissContactAction;
    private ImageView ivUnreadButtonImage;
    private ImageView ivButtonCancelReply;
    private ImageView ivChatMenu;
    private ImageView ivButtonChatMenu;
    private ImageView ivButtonAttach;
    private ImageView ivSend;
    private ImageView ivButtonSend;
    private ImageView ivToBottom;
    private ImageView ivMentionAnchor;
    private ImageView ivRoomTypingIndicator;
    private ImageView ivLoadingPopup;
    private CircleImageView civRoomImage;
    private CircleImageView civMyAvatarEmpty;
    private CircleImageView civRoomAvatarEmpty;
    private TAPRoundedCornerImageView rcivQuoteImage;
    private TextView tvRoomName;
    private TextView tvRoomStatus;
    private TextView tvRoomImageLabel;
    private TextView tvDateIndicator;
    private TextView tvUnreadButtonCount;
    private TextView tvChatEmptyGuide;
    private TextView tvMyAvatarLabelEmpty;
    private TextView tvRoomAvatarLabelEmpty;
    private TextView tvProfileDescription;
    private TextView tvQuoteTitle;
    private TextView tvQuoteContent;
    private TextView tvBadgeUnread;
    private TextView tvBadgeMentionCount;
    private TextView tvButtonBlockContact;
    private TextView tvButtonAddToContacts;
    private TextView tvRoomTypingStatus;
    private TextView tvChatHistoryContent;
    private TextView tvLoadingText;
    private View vRoomImage;
    private View vStatusBadge;
    private View vQuoteDecoration;
    private TAPConnectionStatusFragment fConnectionStatus;
    private CardView cvEmptySavedMessages;

//  Voice Note
    private ImageView ivVoiceNote;
    private ConstraintLayout clVoiceNote;
    private ImageView ivVoiceNoteControl;
    private TextView tvRecordTime;
    private ImageView ivRecording;
    private TextView tvSlideLabel;
    private ImageView ivLeft;
    private ImageView ivRemoveVoiceNote;
    private ConstraintLayout clSwipeVoiceNote;
    private Group gTooltip;
    private TapAudioManager audioManager;
    private SeekBar seekBar;

    // RecyclerView
    private TAPMessageAdapter messageAdapter;
    private TAPCustomKeyboardAdapter customKeyboardAdapter;
    private TapUserMentionListAdapter userMentionListAdapter;
    private LinearLayoutManager messageLayoutManager;
    private SimpleItemAnimator messageAnimator;
    private TAPEndlessScrollListener endlessScrollListener;

    private TAPChatViewModel vm;
    private RequestManager glide;
    private TAPSocketListener socketListener;

    // Pinned Messages
    private ConstraintLayout clPinnedMessage;
    private TapVerticalIndicator clPinnedIndicator;
    private TAPRoundedCornerImageView rcivPinnedImage;
    private TextView tvPinnedLabel;
    private TextView tvPinnedMessage;
    private ImageButton ibPinnedMessages;

    // Multiple forward
    private ConstraintLayout clForward;
    private TextView tvForwardCount;
    private final static int MAX_FORWARD_COUNT = 30;
    private ImageView ivForward;

    // Link Preview
    private ConstraintLayout clLink;
    private TAPRoundedCornerImageView rcivLink;
    private TextView tvLinkTitle;
    private TextView tvLinkContent;
    private ImageView ivCloseLink;

    // Schedule Message
    private CardView cvSchedule;
    private View vScreen;
    private Group gScheduleMessage;
    private ImageView ivSchedule;

    // Blocked Contacts
    private TextView btnUnblock;

    private Handler linkHandler;
    private Runnable linkRunnable;

    // Custom Navigation Bar
    private FragmentContainerView customNavigationBarFragmentContainerView;
    private TapBaseChatRoomCustomNavigationBarFragment customNavigationBarFragment;

    // Scroll state
    private enum STATE {WORKING, LOADED, DONE}
    private TapUIChatActivity.STATE state = TapUIChatActivity.STATE.LOADED;

    // Saved Messages
    private boolean isLastMessageNeedRefresh = false;
    private boolean isArrowButtonTapped = false;
    private final static int PAGE_SIZE = 50;

    /**
     * =========================================================================================== *
     * START ACTIVITY
     * =========================================================================================== *
     */

    public static void start(Context context, String instanceKey, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        start(context, instanceKey, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, null, TapTalk.isConnected(instanceKey));
    }

    public static void start(Context context, String instanceKey, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor, String jumpToMessageLocalID) {
        start(context, instanceKey, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, jumpToMessageLocalID, TapTalk.isConnected(instanceKey));
    }

    // Open chat room from notification
    public static void start(Context context, String instanceKey, TAPRoomModel roomModel) {
        start(context, instanceKey, roomModel, null, null, TapTalk.isConnected(instanceKey));
    }

    // Open chat room from Room List
    public static void start(Context context, String instanceKey, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser) {
        start(context, instanceKey, roomModel, typingUser, null, TapTalk.isConnected(instanceKey));
    }

    public static void start(Context context, String instanceKey, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser, @Nullable String jumpToMessageLocalID, boolean isConnected) {
        TAPChatManager.getInstance(instanceKey).saveUnsentMessage();
        Intent intent = new Intent(context, TapUIChatActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ROOM, roomModel);

        if (null != typingUser) {
            Gson gson = new Gson();
            String list = gson.toJson(typingUser);
            intent.putExtra(GROUP_TYPING_MAP, list);
        }
        if (null != jumpToMessageLocalID) {
            intent.putExtra(JUMP_TO_MESSAGE, jumpToMessageLocalID);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(SOCKET_CONNECTED, isConnected);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(() -> TAPUtils.dismissKeyboard(activity));
            activity.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static PendingIntent generatePendingIntent(Context context, String instanceKey, TAPRoomModel roomModel) {
        Intent intent = new Intent(context, TapUIChatActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ROOM, roomModel);
        intent.putExtra(SOCKET_CONNECTED, TapTalk.isConnected(instanceKey));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
        }
    }

    /**
     * =========================================================================================== *
     * OVERRIDE METHODS
     * =========================================================================================== *
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = TapActivityChatBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        glide = Glide.with(this);
        audioManager = TapAudioManager.Companion.getInstance(instanceKey, audioListener);
        linkHandler = new Handler();
        bindViews();
        initRoom();
        registerBackgroundBroadcastReceiver();
        TAPChatManager.getInstance(instanceKey).triggerChatRoomOpened(this, vm.getRoom(), vm.getOtherUserModel());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isArrowButtonTapped = false;
        TAPChatManager.getInstance(instanceKey).setActiveRoom(vm.getRoom());
        TapUI.getInstance(instanceKey).setCurrentTapTalkChatActivity(this);
        registerForegroundBroadcastReceiver();
        etChat.setText(TAPChatManager.getInstance(instanceKey).getMessageFromDraft(vm.getRoom().getRoomID()));
        checkForwardLayout(vm.getQuotedMessage(), vm.getForwardedMessages(), vm.getQuoteAction());

        getStarredMessageIds();
        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled()) {
            getPinnedMessageIds();
            getPinnedMessages("");
        }
        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getType()) {
            callApiGetUserByUserID();
        } else {
            getRoomDataFromApi();
        }

        if (vm.isInitialAPICallFinished() && vm.getMessageModels().size() == 0 && TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
            fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
        }
        checkInitSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraftToManager();
        sendTypingEmit(false);
        TAPChatManager.getInstance(instanceKey).deleteActiveRoom();
        TapUI.getInstance(instanceKey).setCurrentTapTalkChatActivity(null);
        TAPBroadcastManager.unregister(this, foregroundBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendTypingEmitDelayTimer.cancel();
        typingIndicatorTimeoutTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Reload UI in room list
        Intent intent = new Intent(RELOAD_ROOM_LIST);
        intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
        if (isLastMessageNeedRefresh) {
            intent.putExtra(IS_NEED_REFRESH, true);
        }
        LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

        TAPBroadcastManager.unregister(this, backgroundBroadcastReceiver);
        TAPChatManager.getInstance(instanceKey).updateUnreadCountInRoomList(TAPChatManager.getInstance(instanceKey).getOpenRoom());
        TAPChatManager.getInstance(instanceKey).setOpenRoom(null); // Reset open room
        TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(socketListener);
        vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable); // Stop offline timer
        TAPChatManager.getInstance(instanceKey).setNeedToCallUpdateRoomStatusAPI(true);
        TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads(); // Remove failed download list from active room
        TAPChatManager.getInstance(instanceKey).triggerChatRoomClosed(this, vm.getRoom(), vm.getOtherUserModel());

        // TODO: 09/07/20 Disconnect if last activity socket is not connected
        if (!getIntent().getBooleanExtra(SOCKET_CONNECTED, false) && TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED) {
            if (TapTalk.isConnected(instanceKey)) {
                TapTalk.disconnect(instanceKey);
            }
        }

        if (vm.getMediaPlayer() != null) {
            vm.getMediaPlayer().release();
        }
        messageAdapter.removePlayer();
    }

    @Override
    public void onBackPressed() {
        if (vm.isDeleteGroup() && !TAPGroupManager.Companion.getInstance(instanceKey).getRefreshRoomList()) {
            TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
        }
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            hideKeyboards();
        } else {
            if (vm.isSelectState()) {
                hideSelectState();
            } else {
                //TAPNotificationManager.getInstance(instanceKey).updateUnreadCount();
                new Thread(() -> TAPChatManager.getInstance(instanceKey).putUnsentMessageToList()).start();
                if (isTaskRoot()) {
                    // Trigger listener callback if no other activity is open
                    List<TapListener> listeners = getTapTalkListeners(instanceKey);
                    if (listeners != null && !listeners.isEmpty()) {
                        for (TapListener listener : listeners) {
                            listener.onTaskRootChatRoomClosed(this);
                        }
                    }
                }
                setResult(RESULT_OK);
                try {
                    super.onBackPressed();
                    overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (gScheduleMessage.getVisibility() == View.VISIBLE) {
            hideScheduleMessageButton();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.DEFAULT); // For file picker
        if (resultCode == RESULT_OK) {
            // Set active room to prevent null pointer when returning to chat
            TAPChatManager.getInstance(instanceKey).setActiveRoom(vm.getRoom());
            switch (requestCode) {
                case SEND_IMAGE_FROM_CAMERA:
                    if (null != intent && null != intent.getData()) {
                        vm.setCameraImageUri(intent.getData());
                    }
                    if (null == vm.getCameraImageUri()) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> imageCameraUris = new ArrayList<>();
                    imageCameraUris.add(TAPMediaPreviewModel.Builder(vm.getCameraImageUri(), TYPE_IMAGE, true));
                    openMediaPreviewPage(imageCameraUris);
                    break;
                case SEND_MEDIA_FROM_GALLERY:
                    if (null == intent) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> galleryMediaPreviews = new ArrayList<>();
                    ClipData clipData = intent.getClipData();
                    if (null != clipData) {
                        // Multiple media selection
                        galleryMediaPreviews = TAPUtils.getPreviewsFromClipData(TapUIChatActivity.this, clipData, true);
                    }
                    else {
                        // Single media selection
                        Uri uri = intent.getData();
                        TAPMediaPreviewModel preview = TAPUtils.getPreviewFromUri(TapUIChatActivity.this, uri, true);
                        if (preview != null) {
                            galleryMediaPreviews.add(preview);
                        }
                    }
                    if (!galleryMediaPreviews.isEmpty()) {
                        ArrayList<TAPMediaPreviewModel> filteredGalleryMediaPreviews = new ArrayList<>();
                        for (TAPMediaPreviewModel media : galleryMediaPreviews) {
                            if (media.getType() == TYPE_IMAGE || media.getType() == TYPE_VIDEO) {
                                filteredGalleryMediaPreviews.add(media);
                            }
                        }
                        if (filteredGalleryMediaPreviews.isEmpty()) {
                            Toast.makeText(TapUIChatActivity.this, getString(R.string.tap_error_invalid_file_format), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            openMediaPreviewPage(filteredGalleryMediaPreviews);
                        }
                    }
                    break;
                case SEND_MEDIA_FROM_PREVIEW:
                    ArrayList<TAPMediaPreviewModel> medias = intent.getParcelableArrayListExtra(MEDIA_PREVIEWS);
                    if (null != medias && 0 < medias.size()) {
                        sendMediasFromPreview(medias);
                    }
                    break;
                case FORWARD_MESSAGE:
                    TAPRoomModel room = intent.getParcelableExtra(ROOM);
                    if (vm.isSelectState()) {
                        hideSelectState();
                    }
                    if (TAPUtils.isSavedMessagesRoom(room.getRoomID(), instanceKey)) {
                        if (!room.getRoomID().equals(vm.getRoom().getRoomID())) {
                            start(TapUIChatActivity.this, instanceKey, room);
                        }
                        vm.setForwardedMessages(null, 0);
                        vm.setQuotedMessage(null, 0);
                    } else {
                        TAPChatManager.getInstance(instanceKey).setForwardedMessages(room.getRoomID(), intent.getParcelableArrayListExtra(MESSAGE), FORWARD);
                        if (room.getRoomID().equals(vm.getRoom().getRoomID())) {
                            // Show message in composer
                            checkForwardLayout(null, intent.getParcelableArrayListExtra(MESSAGE), FORWARD);
                        } else {
                            // Open selected chat room
                            start(TapUIChatActivity.this, instanceKey, room);
                            finish();
                        }
                    }
                    break;
                case PICK_LOCATION:
                    String address = intent.getStringExtra(LOCATION_NAME) == null ? "" : intent.getStringExtra(LOCATION_NAME);
                    Double latitude = intent.getDoubleExtra(LATITUDE, 0.0);
                    Double longitude = intent.getDoubleExtra(LONGITUDE, 0.0);
                    TAPChatManager.getInstance(instanceKey).sendLocationMessage(vm.getRoom(), address, latitude, longitude);
                    break;
                case SEND_FILE:
                    File tempFile = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Uri uri = null;
                        if (null != intent.getClipData()) {
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                                uri = intent.getClipData().getItemAt(i).getUri();
                            }
                        } else {
                            uri = intent.getData();
                        }

                        if (uri != null) {
                            // Write temporary file to cache for upload for Android 11+
                            tempFile = TAPFileUtils.createTemporaryCachedFile(this, uri);
                        }
//                    } else {
//                        String filePath = intent.getStringExtra(RESULT_FILE_PATH);
//                        if (filePath != null && !filePath.isEmpty()) {
//                            tempFile = new File(filePath);
//                        }
//                    }

                    if (null != tempFile) {
                        if (TAPFileUploadManager.getInstance(instanceKey).isSizeAllowedForUpload(tempFile.length())) {
                            TAPChatManager.getInstance(instanceKey).sendFileMessage(TapUIChatActivity.this, vm.getRoom(), tempFile);
                        } else {
                            new TapTalkDialog.Builder(TapUIChatActivity.this)
                                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                    .setTitle(getString(R.string.tap_sorry))
                                    .setMessage(String.format(getString(R.string.tap_format_s_maximum_file_size), TAPUtils.getStringSizeLengthFile(TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize())))
                                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                    .show();
                        }
                    }
                    break;

                case OPEN_GROUP_PROFILE:
                    if (intent != null) {
                        TAPMessageModel messageModel = intent.getParcelableExtra(MESSAGE);
                        if (messageModel != null) {
                            goToMessage(messageModel);
                        } else {
                            vm.setDeleteGroup(true);
                            closeActivity();
                        }
                    }
                    break;
                case OPEN_MEMBER_PROFILE:
                    if (intent != null) {
                        TAPMessageModel message = intent.getParcelableExtra(MESSAGE);
                        if (message != null) {
                           goToMessage(message);
                        } else if (intent.getBooleanExtra(CLOSE_ACTIVITY, false)) {
                            closeActivity();
                        }
                    }
                    break;
                case OPEN_PINNED_MESSAGES:
                    if (intent != null) {
                        TAPMessageModel message = intent.getParcelableExtra(MESSAGE);
                        if (message != null) {
                            scrollToMessage(message.getLocalID());
                        }
                        if (intent.getBooleanExtra(IS_NEED_REFRESH, false)) {
                            vm.setPinnedMessagesPageNumber(1);
                            getPinnedMessages("");
                        }
                    }
                case OPEN_PERSONAL_PROFILE:
                    if (intent != null) {
                        TAPMessageModel message = intent.getParcelableExtra(MESSAGE);
                        if (message != null) {
                            scrollToMessage(message.getLocalID());
                        }
                    }
                    break;
            }
        }
        else {
            switch (requestCode) {
                case FORWARD_MESSAGE:
                    if (vm.isSelectState()) {
                        hideSelectState();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (TAPUtils.allPermissionsGranted(grantResults)) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                case PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA:
                    vm.setCameraImageUri(TAPUtils.takePicture(instanceKey, TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE:
                    if (null != messageAdapter) {
                        messageAdapter.notifyDataSetChanged();
                    }
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_FILE:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON);
                        TAPUtils.openDocumentPicker(TapUIChatActivity.this, SEND_FILE);
//                    } else {
//                        TAPUtils.openDocumentPicker(TapUIChatActivity.this);
//                    }
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO:
                    if (null != attachmentListener) {
                        attachmentListener.onSaveVideoToGallery(vm.getPendingDownloadMessage());
                    }
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE:
                    startFileDownload(vm.getPendingDownloadMessage());
                    break;
                case PERMISSION_LOCATION:
                    TAPUtils.openLocationPicker(TapUIChatActivity.this, instanceKey);
                    break;
                case PERMISSION_RECORD_AUDIO:
                    startRecording();
                    break;
            }
        }
    }

    /**
     * =========================================================================================== *
     * INIT CHAT ROOM
     * =========================================================================================== *
     */

    private void initRoom() {
        if (initViewModel()) {
            initView();
            initListener();
            cancelNotificationWhenEnterRoom();
            //registerBroadcastManager();

//            if (null != clChatHistory) {
//                clChatHistory.setVisibility(View.GONE);
//            }
//            if (null != clChatComposer) {
//                clChatComposer.setVisibility(View.VISIBLE);
//            }

        } else if (vm.getMessageModels().size() == 0) {
            initView();
        }
    }

    private void bindViews() {
//        sblChat = getSwipeBackLayout();
        flMessageList = vb.flMessageList;
        flRoomUnavailable = vb.flRoomUnavailable;
        flLoading = vb.layoutPopupLoadingScreen.flLoading;
        llButtonDeleteChat = vb.llButtonDeleteChat;
        tvDeleteChat = vb.tvDelete;
        ivDelete = vb.ivDelete;
        pbDelete = vb.pbDelete;
        clContainer = vb.clContainer;
        clActionBar = vb.clActionBar;
        clContactAction = vb.clContactAction;
        clUnreadButton = vb.clUnreadButton;
        clEmptyChat = vb.clEmptyChat;
        clChatComposerAndHistory = vb.clChatComposerAndHistory;
        clChatHistory = vb.clChatHistory;
        clQuote = vb.clQuoteLayout;
        clChatComposer = vb.clChatComposer;
        clUserMentionList = vb.layoutUserMentionList.clUserMentionList;
        clRoomStatus = vb.clRoomStatus;
        clRoomOnlineStatus = vb.clRoomOnlineStatus;
        clRoomTypingStatus = vb.clRoomTypingStatus;
        ivButtonBack = vb.ivButtonBack;
        ivRoomIcon = vb.ivRoomIcon;
        ivButtonDismissContactAction = vb.ivButtonDismissContactAction;
        ivUnreadButtonImage = vb.ivUnreadButtonImage;
        ivButtonCancelReply = vb.ivCancelReply;
        ivChatMenu = vb.ivChatMenu;
        ivButtonChatMenu = vb.ivChatMenuArea;
        ivButtonAttach = vb.ivAttach;
        ivSend = vb.ivSend;
        ivButtonSend = vb.ivSendArea;
        ivToBottom = vb.ivToBottom;
        ivMentionAnchor = vb.ivMentionAnchor;
        ivRoomTypingIndicator = vb.ivRoomTypingIndicator;
        ivLoadingPopup = vb.layoutPopupLoadingScreen.ivLoadingImage;
        civRoomImage = vb.civRoomImage;
        civMyAvatarEmpty = vb.civMyAvatarEmpty;
        civRoomAvatarEmpty = vb.civRoomAvatarEmpty;
        rcivQuoteImage = vb.rcivQuoteLayoutImage;
        tvRoomName = vb.tvRoomName;
        tvRoomStatus = vb.tvRoomStatus;
        tvRoomImageLabel = vb.tvRoomImageLabel;
        tvRoomTypingStatus = vb.tvRoomTypingStatus;
        tvButtonBlockContact = vb.tvButtonBlockContact;
        tvButtonAddToContacts = vb.tvButtonAddToContacts;
        tvDateIndicator = vb.tvDateIndicator;
        tvUnreadButtonCount = vb.tvUnreadButtonCount;
        tvChatEmptyGuide = vb.tvChatEmptyGuide;
        tvMyAvatarLabelEmpty = vb.tvMyAvatarLabelEmpty;
        tvRoomAvatarLabelEmpty = vb.tvRoomAvatarLabelEmpty;
        tvProfileDescription = vb.tvProfileDescription;
        tvQuoteTitle = vb.tvQuoteLayoutTitle;
        tvQuoteContent = vb.tvQuoteLayoutContent;
        tvBadgeUnread = vb.tvBadgeUnread;
        tvBadgeMentionCount = vb.tvBadgeMentionCount;
        tvChatHistoryContent = vb.tvChatHistoryContent;
        tvLoadingText = vb.layoutPopupLoadingScreen.tvLoadingText;
        rvMessageList = vb.rvMessageList;
        rvCustomKeyboard = vb.rvCustomKeyboard;
        rvUserMentionList = vb.layoutUserMentionList.rvUserMentionList;
        etChat = vb.etChat;
        vRoomImage = vb.vRoomImage;
        vStatusBadge = vb.vRoomStatusBadge;
        vQuoteDecoration = vb.vQuoteLayoutDecoration;
        fConnectionStatus = (TAPConnectionStatusFragment) getSupportFragmentManager().findFragmentById(R.id.f_connection_status);
        ivVoiceNote = vb.ivVoiceNote;
        clVoiceNote = vb.clVoiceNote;
        ivVoiceNoteControl = vb.ivVoiceNoteControl;
        tvRecordTime = vb.tvRecordTime;
        ivRecording = vb.ivRecording;
        tvSlideLabel = vb.tvSlideLabel;
        ivLeft = vb.ivLeft;
        ivRemoveVoiceNote = vb.ivRemoveVoiceNote;
        clSwipeVoiceNote = vb.clSwipeVoiceNote;
        gTooltip = vb.gTooltip;
        seekBar = vb.seekBar;
        clForward = vb.clForward;
        tvForwardCount = vb.tvForwardCount;
        ivForward = vb.ivForward;
        cvEmptySavedMessages = vb.cvEmptySavedMessage;
        clPinnedMessage = vb.clPinnedMessage;
        clPinnedIndicator = vb.clPinnedMessageIndicator;
        rcivPinnedImage = vb.rcivPinnedImage;
        tvPinnedLabel = vb.tvPinnedLabel;
        tvPinnedMessage = vb.tvPinnedMessage;
        ibPinnedMessages = vb.ibPinMessages;
        clLink = vb.clLink;
        rcivLink = vb.rcivLink;
        tvLinkTitle = vb.tvLinkTitle;
        tvLinkContent = vb.tvLinkContent;
        ivCloseLink = vb.ivCloseLink;
        cvSchedule = vb.cvSchedule;
        vScreen = vb.vScreen;
        gScheduleMessage = vb.gScheduleMessage;
        ivSchedule = vb.ivSchedule;
        btnUnblock = vb.btnUnblock;
        customNavigationBarFragmentContainerView = vb.customActionBarFragmentContainer;
    }

    private boolean initViewModel() {
        vm = new ViewModelProvider(this, new TAPChatViewModel.TAPChatViewModelFactory(getApplication(), instanceKey)).get(TAPChatViewModel.class);
        if (null == vm.getRoom()) {
            vm.setRoom(getIntent().getParcelableExtra(ROOM));
        }
        if (null == vm.getMyUserModel()) {
            vm.setMyUserModel(TAPChatManager.getInstance(instanceKey).getActiveUser());
        }

        if (null == vm.getRoom()) {
            Toast.makeText(TapTalk.appContext, getString(R.string.tap_error_room_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (null == vm.getOtherUserModel() && TYPE_PERSONAL == vm.getRoom().getType()) {
            setOtherUserModel(TAPContactManager.getInstance(instanceKey).getUserData(vm.getOtherUserID()));
        }

        // Updated 2020/02/10
        if (TYPE_PERSONAL != vm.getRoom().getType() && TAPGroupManager.Companion.getInstance(instanceKey).checkIsRoomDataAvailable(vm.getRoom().getRoomID())) {
            TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(vm.getRoom().getRoomID());
            if (null != room && null != room.getName() && !room.getName().isEmpty()) {
                vm.setRoom(room);
            }
        }

        if (null != getIntent().getStringExtra(JUMP_TO_MESSAGE) && !vm.isInitialAPICallFinished()) {
            vm.setTappedMessageLocalID(getIntent().getStringExtra(JUMP_TO_MESSAGE));
        }

        getInitialUnreadCount();

        return null != vm.getMyUserModel() && (null != vm.getOtherUserModel() || (TYPE_PERSONAL != vm.getRoom().getType()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        getWindow().setBackgroundDrawable(null);

        if (null != getIntent().getStringExtra(GROUP_TYPING_MAP)) {
            try {
                String tempGroupTyping = getIntent().getStringExtra(GROUP_TYPING_MAP);
                Gson gson = new Gson();
                Type typingType = new TypeToken<LinkedHashMap<String, TAPUserModel>>() {
                }.getType();
                vm.setGroupTyping(gson.fromJson(tempGroupTyping, typingType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        customNavigationBarFragment = TAPChatManager.getInstance(instanceKey).getChatRoomCustomNavigationBar(this, vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel());
        if (null != customNavigationBarFragment) {
            // Show custom navigation bar
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.custom_action_bar_fragment_container, customNavigationBarFragment)
                .commit();
            getSupportFragmentManager()
                .beginTransaction()
                .show(customNavigationBarFragment)
                .commit();
            customNavigationBarFragmentContainerView.setVisibility(View.VISIBLE);
            clActionBar.setVisibility(View.GONE);

            customNavigationBarFragment.setRoom(vm.getRoom());
            customNavigationBarFragment.setRecipientUser(vm.getOtherUserModel());
            customNavigationBarFragment.setTypingUsers(vm.getGroupTyping());
        }
        else {
            // Setup default navigation bar
            // Set room name
            if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
                tvRoomName.setText(R.string.tap_saved_messages);
            } else if (vm.getRoom().getType() == TYPE_PERSONAL && null != vm.getOtherUserModel() &&
                    (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                    !vm.getOtherUserModel().getFullname().isEmpty()) {
                tvRoomName.setText(vm.getOtherUserModel().getFullname());
            } else {
                tvRoomName.setText(vm.getRoom().getName());
            }

            setNavigationBarProfilePicture();

            // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
            if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getUserRole() &&
                    null != vm.getOtherUserModel().getUserRole().getIconURL() && null != vm.getRoom() &&
                    TYPE_PERSONAL == vm.getRoom().getType() &&
                    !vm.getOtherUserModel().getUserRole().getIconURL().isEmpty()) {
                glide.load(vm.getOtherUserModel().getUserRole().getIconURL()).into(ivRoomIcon);
                ivRoomIcon.setVisibility(View.VISIBLE);
            } else {
                ivRoomIcon.setVisibility(View.GONE);
            }

//        // Set typing status
//        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
//            vm.setOtherUserTyping(true);
//            showTypingIndicator();
//        }

            if (0 < vm.getGroupTypingSize()) {
                showTypingIndicator();
            }
        }

        vm.setStarredMessageIds(TAPDataManager.getInstance(instanceKey).getStarredMessageIds(vm.getRoom().getRoomID()));

        // Initialize chat message RecyclerView
        messageAdapter = new TAPMessageAdapter(instanceKey, glide, chatListener, vm);
        messageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.instanceKey = instanceKey;
        rvMessageList.setAdapter(messageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        rvMessageList.setupSwipeHelper(this, position -> {
            showQuoteLayout(messageAdapter.getItemAt(position), REPLY, true);
        });
        rvMessageList.setupSwipeInfoHelper(this, position -> {
            if (position >= 0 && vm.getMessageModels().size() > position) {
                TAPMessageModel message = vm.getMessageModels().get(position);
                TapMessageInfoActivity.Companion.start(
                    TapUIChatActivity.this,
                    instanceKey,
                    message,
                    vm.getRoom(),
                    vm.getStarredMessageIds().contains(message.getMessageID()),
                    vm.getPinnedMessageIds().contains(message.getMessageID()),
                    getBubbleViewFromAdapter(position)
                );
            }
        });
        // FIXME: 9 November 2018 IMAGES/VIDEOS CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0);
        messageAnimator = (SimpleItemAnimator) rvMessageList.getItemAnimator();
        if (null != messageAnimator) {
            messageAnimator.setSupportsChangeAnimations(false);
        }
        rvMessageList.setItemAnimator(null);
        if (endlessScrollListener != null) {
            rvMessageList.removeOnScrollListener(endlessScrollListener);
        }
        rvMessageList.removeOnScrollListener(messageListScrollListener);
        rvMessageList.addOnScrollListener(messageListScrollListener);
//        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL); FIXME: 8 Apr 2020 DISABLED OVERSCROLL DECORATOR

        // Listener for scroll pagination
        endlessScrollListener = new TAPEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!vm.isOnBottom()) {
                    loadMessagesFromDatabase();
                }
            }
        };
        // Disable swipe in deleted or blocked user room
        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getType() && null != vm.getOtherUserModel() &&
                ((null != vm.getOtherUserModel().getDeleted() && vm.getOtherUserModel().getDeleted() > 0L)
                || (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled() && TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(vm.getOtherUserID())))) {
            rvMessageList.disableSwipe();
        }
        // Initialize custom keyboard
        vm.setCustomKeyboardItems(TAPChatManager.getInstance(instanceKey).getCustomKeyboardItems(vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel()));
        if (null != vm.getCustomKeyboardItems() && vm.getCustomKeyboardItems().size() > 0) {
            // Enable custom keyboard
            vm.setCustomKeyboardEnabled(true);
            customKeyboardAdapter = new TAPCustomKeyboardAdapter(vm.getCustomKeyboardItems(), customKeyboardItemModel -> {
                TAPChatManager.getInstance(instanceKey).triggerCustomKeyboardItemTapped(TapUIChatActivity.this, customKeyboardItemModel, vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel());
                hideUnreadButton();
            });
            rvCustomKeyboard.setAdapter(customKeyboardAdapter);
            rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
            ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        } else {
            // Disable custom keyboard
            vm.setCustomKeyboardEnabled(false);
            ivChatMenu.setVisibility(View.GONE);
            ivButtonChatMenu.setVisibility(View.GONE);
        }

        showAttachmentButton();

        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_personal_chat_room_empty_guide_title), vm.getRoom().getName())));
            tvProfileDescription.setText(String.format(getString(R.string.tap_format_s_personal_chat_room_empty_guide_content), vm.getRoom().getName()));
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getType() && null != vm.getRoom().getParticipants()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_group_chat_room_empty_guide_title), vm.getRoom().getName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
            tvRoomStatus.setText(String.format("%d Members", vm.getRoom().getParticipants().size()));
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
            new Thread(() -> {
                for (TAPUserModel user : vm.getRoom().getParticipants()) {
                    vm.addRoomParticipantByUsername(user);
                }
            }).start();
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_group_chat_room_empty_guide_title), vm.getRoom().getName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
            clRoomOnlineStatus.setVisibility(View.GONE);
        } else {
            clRoomOnlineStatus.setVisibility(View.GONE);
        }

        // Load items from database for the first time
        if (vm.getRoom().isDeleted()) {
            //showRoomIsUnavailableState();
            if (vm.getRoom().getType() == TYPE_PERSONAL) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            } else {
                showChatAsHistory(getString(R.string.tap_group_unavailable));
            }
        } else if (null != vm.getOtherUserModel()) {
            if (null != vm.getOtherUserModel().getDeleted()) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            } else if (TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(vm.getOtherUserID())) {
                showUnblockButton();
            }
        }
//        else if (vm.getMessageModels().size() == 0 && !vm.getRoom().isRoomDeleted()) {
//            //vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
//            getAllUnreadMessage();
//        }

        if (vm.getRoom().isLocked()) {
            // Hide chat composer if room is locked
            lockChatRoom();
        }

        if (vm.getMessageModels().isEmpty()) {
            getAllUnreadMessage();
        }
        else {
            showMessageList();
            updateMessageDecoration();
        }

        LayoutTransition containerTransition = clContainer.getLayoutTransition();
        containerTransition.addTransitionListener(containerTransitionListener);

        etChat.addTextChangedListener(chatWatcher);
        etChat.setOnEditorActionListener(chatEditorListener);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

//        sblChat.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
//        sblChat.setSwipeInterface(swipeInterface);

        vRoomImage.setOnClickListener(v -> openRoomProfile());
        ivButtonBack.setOnClickListener(v -> closeActivity());
        tvButtonBlockContact.setOnClickListener(v -> blockContact());
        tvButtonAddToContacts.setOnClickListener(v -> addUserToContacts());
        ivButtonDismissContactAction.setOnClickListener(v -> dismissContactAction());
        ivButtonCancelReply.setOnClickListener(v -> hideQuoteLayout());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> buildAndSendTextMessage());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
        ivMentionAnchor.setOnClickListener(v -> scrollToMessage(vm.getUnreadMentions().entrySet().iterator().next().getValue().getLocalID()));
        flMessageList.setOnClickListener(v -> chatListener.onOutsideClicked(null));
        flLoading.setOnClickListener(v -> {
        });
        ivForward.setOnClickListener(v -> forwardMessages());
        ibPinnedMessages.setOnClickListener(v -> TapPinnedMessagesActivity.Companion.start(this, instanceKey, vm.getRoom()));
        clPinnedMessage.setOnClickListener(v -> {
            if (!vm.isLoadPinnedMessages()) {
                pinnedMessageLayoutOnClick();
            }
        });
        ivButtonSend.setOnLongClickListener(view -> {
            if (TapUI.getInstance(instanceKey).isScheduledMessageFeatureEnabled() && vm.getQuoteAction() == null) {
                showScheduleMessageButton();
            }
            return true;
        });
        vScreen.setOnClickListener(v -> hideScheduleMessageButton());

        cvSchedule.setOnClickListener(v -> {
            hideScheduleMessageButton();
            TapTimePickerBottomSheetFragment timePicker = new TapTimePickerBottomSheetFragment(new TAPGeneralListener<>() {
                @Override
                public void onClick(int position, Long item) {
                    super.onClick(position, item);
                    TapScheduledMessageActivity.Companion.start(TapUIChatActivity.this, instanceKey, vm.getRoom(), etChat.getText().toString(), item);
                    etChat.setText("");
                }
            });
            timePicker.show(getSupportFragmentManager(), "");
        });
        // TODO: 11/10/22 handle tapUI for scheduled message icon appearance MU
        ivSchedule.setOnClickListener(v ->  {
            if (TapUI.getInstance(instanceKey).isScheduledMessageFeatureEnabled()) {
                TapScheduledMessageActivity.Companion.start(this, instanceKey, vm.getRoom());
            }
        });

        if (TapUI.getInstance(instanceKey).isSendVoiceNoteMenuEnabled()) {
            ivVoiceNote.setOnClickListener(v -> {
                showTooltip();
            });
            // TODO: 25/04/22 temporarily disabled for improvement MU
//        ivVoiceNote.setOnTouchListener(swipeTouchListener);
            ivVoiceNote.setOnLongClickListener(v -> {
                startRecording();
                return true;
            });
            ivVoiceNoteControl.setOnClickListener(v -> onVoiceNoteControlClick());
            ivRemoveVoiceNote.setOnClickListener(v -> removeRecording());
            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        } else {
            ivVoiceNote.setVisibility(View.GONE);
        }
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled()) {
            btnUnblock.setOnClickListener(v -> {
                new TapTalkDialog.Builder(TapUIChatActivity.this)
                        .setTitle(String.format(getString(R.string.tap_unblock_s_format), vm.getOtherUserModel().getFullname()))
                        .setMessage(getString(R.string.tap_sure_unblock_wording))
                        .setCancelable(true)
                        .setPrimaryButtonTitle(getString(R.string.tap_yes))
                        .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                        .setPrimaryButtonListener(view -> {
                            showLoadingPopup();
                            TapCoreContactManager.getInstance(instanceKey).unblockUser(vm.getOtherUserID(), unblockUserView);
                        })
                        .show();
            });
        }

//        // TODO: 19 July 2019 SHOW CHAT AS HISTORY IF ACTIVE USER IS NOT IN PARTICIPANT LIST
//        if (null == vm.getRoom().getGroupParticipants()) {
//            showChatAsHistory(getString(R.string.tap_not_a_participant));
//        }

        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled()) {
            TAPMessageModel newestMessage = TAPDataManager.getInstance(instanceKey).getNewestPinnedMessage(vm.getRoom().getRoomID());
            if (newestMessage != null) {
                vm.addPinnedMessage(newestMessage);
                vm.addPinnedMessageId(newestMessage.getMessageID());
                setPinnedMessage(TAPDataManager.getInstance(instanceKey).getNewestPinnedMessage(vm.getRoom().getRoomID()));
            } else {
                vm.setLoadPinnedMessages(false);
            }
        }

        if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled()) {
            ivCloseLink.setOnClickListener(v -> {
                vm.getLinkHashMap().remove(TITLE);
                vm.getLinkHashMap().remove(DESCRIPTION);
                vm.getLinkHashMap().remove(IMAGE);
                vm.getLinkHashMap().remove(TYPE);
                hideLinkPreview(false);
            });
        }

        llButtonDeleteChat.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_button_destructive_ripple));
        ivButtonAttach.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_attachment_ripple));
        ivToBottom.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_scroll_to_bottom_ripple));
        ivMentionAnchor.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_scroll_to_bottom_ripple));
        clUnreadButton.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_white_rounded_8dp_ripple));

        if (vm.isOnBottom() &&
            rvMessageList.getScrollState() == SCROLL_STATE_IDLE &&
            messageAdapter.getItemCount() > 0
        ) {
            rvMessageList.scrollToPosition(0);
        }

        // Restore recording state
        if (vm.getRecordingState() == LOCKED_RECORD) {
            setLockedRecordingState();
            audioManager.getRecordingTime().observe(TapUIChatActivity.this, s -> tvRecordTime.setText(s));
        }
        else if (vm.getRecordingState() == HOLD_RECORD) {
            setHoldRecordingState();
        }
        else if (vm.getRecordingState() == FINISH) {
            setFinishedRecordingState();
        }
        else if (vm.getRecordingState() == PLAY) {
            setPlayingState();
        }
        else if (vm.getRecordingState() == PAUSE) {
            setPausedState();
        }
    }

    private void checkInitSocket() {
        if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED) {
            if (!TapTalk.isConnected(instanceKey) && TapTalk.isForeground) {
                TapTalk.connect(instanceKey, new TapCommonListener() {
                    @Override
                    public void onSuccess(String successMessage) {
                        super.onSuccess(successMessage);
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Log.e(TAG, errorMessage);
                    }
                });
            }
        }
    }

    private void setNavigationBarProfilePicture() {
        if (!TapUI.getInstance(instanceKey).isProfileButtonVisible()) {
            civRoomImage.setVisibility(View.GONE);
            vRoomImage.setVisibility(View.GONE);
            tvRoomImageLabel.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() && null != vm.getOtherUserModel() &&
                (null != vm.getOtherUserModel().getDeleted() && vm.getOtherUserModel().getDeleted() > 0L)
        ) {
            // Deleted user
            glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(civRoomImage);
            ImageViewCompat.setImageTintList(civRoomImage, null);
            tvRoomImageLabel.setVisibility(View.GONE);
            clRoomStatus.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() &&
                TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)
        ) {
            // Saved Messages
            glide.load(R.drawable.tap_ic_bookmark_round).fitCenter().into(civRoomImage);
            ImageViewCompat.setImageTintList(civRoomImage, null);
            tvRoomImageLabel.setVisibility(View.GONE);
            clRoomStatus.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getType() &&
                null != vm.getOtherUserModel()
        ) {
            if (null != vm.getOtherUserModel().getImageURL().getThumbnail() &&
                    !vm.getOtherUserModel().getImageURL().getThumbnail().isEmpty()
            ) {
                // Load user avatar URL
                loadProfilePicture(vm.getOtherUserModel().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
            } else {
                // No profile picture / blocked
                loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
            }
            vm.getRoom().setImageURL(vm.getOtherUserModel().getImageURL());
        } else if (null != vm.getRoom() && !vm.getRoom().isDeleted() && null != vm.getRoom().getImageURL() && !vm.getRoom().getImageURL().getThumbnail().isEmpty()) {
            // Load room image
            loadProfilePicture(vm.getRoom().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
        } else {
            loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
        }
    }

    private void setRoomState() {
        if (vm.getRoom().isDeleted()) {
            //showRoomIsUnavailableState();
            if (vm.getRoom().getType() == TYPE_PERSONAL) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            } else {
                showChatAsHistory(getString(R.string.tap_group_unavailable));
            }
        } else if (null != vm.getOtherUserModel()) {
            if (null != vm.getOtherUserModel().getDeleted()) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            } else if (TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(vm.getOtherUserID())) {
                showUnblockButton();
            } else {
                showDefaultChatEditText();
            }
        }
    }

    private void showAttachmentButton() {
        // Show / hide attachment button
        if (TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled() &&
                TapUI.getInstance(instanceKey).isCameraAttachmentDisabled() &&
                TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled() &&
                TapUI.getInstance(instanceKey).isLocationAttachmentDisabled()
        ) {
            ivButtonAttach.setVisibility(View.GONE);
        } else {
            ivButtonAttach.setVisibility(View.VISIBLE);
        }

        // Show / hide scheduled message button
        if (TapUI.getInstance(instanceKey).isScheduledMessageFeatureEnabled()) {
            ivSchedule.setVisibility(View.VISIBLE);
        } else {
            ivSchedule.setVisibility(View.GONE);
        }

        int rightPaddingDp = 12;
        if (ivButtonAttach.getVisibility() == View.VISIBLE && ivSchedule.getVisibility() == View.VISIBLE) {
            rightPaddingDp = 80;
        }
        else if (ivButtonAttach.getVisibility() == View.VISIBLE) {
            rightPaddingDp = 48;
        }
        etChat.setPadding(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(6),
                TAPUtils.dpToPx(rightPaddingDp),
                TAPUtils.dpToPx(6)
        );
    }

    private void initListener() {
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
        TapCoreContactManager.getInstance(instanceKey).addContactListener(coreContactListener);

        socketListener = new TAPSocketListener() {
            @Override
            public void onSocketConnected() {
                if (!vm.isInitialAPICallFinished()) {
                    // Call get room details API
                    if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getType()) {
                        callApiGetUserByUserID();
                    }
                    else {
                        getRoomDataFromApi();
                    }
                }
                // Updated 2020-05-15
                if (vm.getMessageModels().size() > 0) {
                    callApiAfter();
                } else {
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                }
                restartFailedDownloads();
            }
        };
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
    }

    private void registerForegroundBroadcastReceiver() {
        TAPBroadcastManager.register(
            this,
            foregroundBroadcastReceiver,
            OpenFile,
            CancelDownload,
            PlayPauseVoiceNote,
            LongPressChatBubble,
            LongPressEmail,
            LongPressLink,
            LongPressPhone,
            LongPressMention,
            LinkPreviewImageLoaded
        );
    }

    private void registerBackgroundBroadcastReceiver() {
        TAPBroadcastManager.register(
            this,
            backgroundBroadcastReceiver,
            UploadProgressLoading,
            UploadProgressFinish,
            UploadFailed,
            UploadCancelled,
            DownloadProgressLoading,
            DownloadFinish,
            DownloadFailed,
            DownloadFile
        );
    }

    private void cancelNotificationWhenEnterRoom() {
        TAPNotificationManager.getInstance(instanceKey).cancelNotificationWhenEnterRoom(this, vm.getRoom().getRoomID());
        TAPNotificationManager.getInstance(instanceKey).clearNotificationMessagesMap(vm.getRoom().getRoomID());
    }

    /**
     * =========================================================================================== *
     * UI ACTIONS
     * =========================================================================================== *
     */

    private TAPChatListener chatListener = new TAPChatListener() {
        @Override
        public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            checkChatRoomLocked(message);
            handleSystemMessageAction(message);
            updateMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
            if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled()) {
                for (int index = 0; index < vm.getPinnedMessages().size(); index++) {
                    String id = vm.getPinnedMessages().get(index).getMessageID();
                    if (id != null && id.equals(message.getMessageID())) {
                        if (message.getDeleted() != null && message.getDeleted() > 0) {
                            vm.removePinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIds().indexOf(message.getMessageID())));
                            vm.removePinnedMessageId(message.getMessageID());
                            if (vm.getPinnedMessages().isEmpty()) {
                                setPinnedMessage(null);
                            } else {
                                if (vm.getPinnedMessageIndex() < vm.getPinnedMessages().size()) {
                                    if (vm.getPinnedMessageIndex() >= vm.getPinnedMessageIds().size()) {
                                        vm.setPinnedMessageIndex(0);
                                        TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), vm.getPinnedMessages().get(0));
                                        runOnUiThread(() -> clPinnedIndicator.select(0));
                                    }
                                    setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                                } else if (vm.getPinnedMessageIndex() < vm.getPinnedMessages().size()) {
                                    setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                                } else {
                                    getPinnedMessages("");
                                }
                            }
                        } else {
                            vm.replacePinnedMessage(index, message);
                            if (index == vm.getPinnedMessageIndex()) {
                                setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                                if (index == 0) {
                                    TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), message);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
            if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled()) {
                vm.removePinnedMessage(message);
                vm.removePinnedMessageId(message.getMessageID());
                if (vm.getPinnedMessages().isEmpty()) {
                    setPinnedMessage(null);
                } else {
                    if (vm.getPinnedMessageIndex() < vm.getPinnedMessages().size()) {
                        if (vm.getPinnedMessageIndex() >= vm.getPinnedMessageIds().size()) {
                            vm.setPinnedMessageIndex(0);
                            TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), vm.getPinnedMessages().get(0));
                            runOnUiThread(() -> clPinnedIndicator.select(0));
                        }
                        setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                    } else {
                        getPinnedMessages("");
                    }
                }
                messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                runOnUiThread(() -> clPinnedIndicator.setSize(vm.getPinnedMessageIds().size()));
            }
            updateMessageFromSocket(message);
        }

        @Override
        public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
            if (
//                    null == TAPChatManager.getInstance(instanceKey).getOpenRoom() ||
//                    !TAPChatManager.getInstance(instanceKey).getOpenRoom()
//                            .equals(message.getRoom().getRoomID()) ||
                    null == vm.getRoom() ||
                            !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())
            ) {
                return;
            }
            updateMessage(message);
        }

        @Override
        public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onSendMessage(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            TAPChatManager.getInstance(instanceKey).triggerActiveUserSendMessage(TapUIChatActivity.this, message, vm.getRoom());
            //addNewMessage(message);
            updateMessage(message);
            hideQuoteLayout();
            hideUnreadButton();
        }

        @Override
        public void onReplyMessage(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            showQuoteLayout(message, REPLY, true);
            TAPChatManager.getInstance(instanceKey).removeUserInfo(vm.getRoom().getRoomID());
        }

        @Override
        public void onRetrySendMessage(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            messageAdapter.removeMessage(message);
            vm.delete(message.getLocalID());
            if ((message.getType() == TYPE_IMAGE ||
                message.getType() == TYPE_VIDEO ||
                message.getType() == TYPE_FILE ||
                message.getType() == TYPE_VOICE ||
                message.getType() == TYPE_AUDIO)
            ) {
                if (null != message.getData() &&
                    null != message.getData().get(FILE_ID) &&
                    null != message.getData().get(FILE_URL) &&
                    !(((String) message.getData().get(FILE_ID)).isEmpty()) &&
                    !(((String) message.getData().get(FILE_URL)).isEmpty())
                ) {
                    // Resend message
                    TAPChatManager.getInstance(instanceKey).resendMessage(message);
                }
                else if (
                    null != message.getData() &&
                    (null != message.getData().get(FILE_URI) &&
                    !(((String) message.getData().get(FILE_URI)).isEmpty()))
                ) {
                    // Re-upload image/video
                    TAPChatManager.getInstance(instanceKey).retryUpload(TapUIChatActivity.this, message);
                }
                else {
                    // Data not found
                    Toast.makeText(
                            TapUIChatActivity.this,
                            getString(R.string.tap_error_resend_media_data_not_found),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
            else {
                // Resend message
                TAPChatManager.getInstance(instanceKey).resendMessage(message);
            }
        }

        @Override
        public void onSendFailed(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            vm.updateMessagePointer(message);
            vm.removeMessagePointer(message.getLocalID());
            runOnUiThread(() -> messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount()));
        }

        @Override
        public void onMessageRead(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            if (vm.getUnreadCount() != 0) {
                //message.setIsRead(true);
                vm.removeUnreadMessage(message.getLocalID());
                updateUnreadCount();
            }
            vm.removeUnreadMention(message.getLocalID());
            updateMentionCount();
        }

        @Override
        public void onMentionClicked(TAPMessageModel message, String username) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, participant, true);
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, user, false);
                } else {
                    callApiGetUserByUsername(username, message);
                }
            }
        }

        @Override
        public void onMessageQuoteClicked(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            TAPChatManager.getInstance(instanceKey).triggerMessageQuoteTapped(TapUIChatActivity.this, message);
            if (null != message.getReplyTo() &&
                    null != message.getReplyTo().getLocalID() &&
                    !message.getReplyTo().getLocalID().isEmpty() &&
                    message.getReplyTo().getMessageType() != -1 && // FIXME: 25 October 2019 MESSAGE TYPE -1 IS USED FOR DUMMY MESSAGE IN CHAT MANAGER setQuotedMessage
                    (null == message.getForwardFrom() ||
                            null == message.getForwardFrom().getFullname() ||
                            message.getForwardFrom().getFullname().isEmpty())) {
                scrollToMessage(message.getReplyTo().getLocalID());
            }
        }

        @Override
        public void onGroupMemberAvatarClicked(TAPMessageModel message) {
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) ||
                    (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) &&
                    (message.getForwardFrom() == null || message.getForwardFrom().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())))) {
                return;
            }
            if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) && message.getForwardFrom() != null) {
                String roomId = TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), message.getForwardFrom().getUserID());
                TAPUserModel userModel = TAPContactManager.getInstance(instanceKey).getUserData(message.getForwardFrom().getUserID());
                if (userModel != null) {
                    TAPChatProfileActivity.Companion.start(TapUIChatActivity.this, instanceKey, TAPRoomModel.Builder(roomId, userModel.getFullname(), TYPE_PERSONAL, userModel.getImageURL(), ""), userModel);
                    hideUnreadButton();
                } else {
                    TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(message.getForwardFrom().getUserID(), new TAPDefaultDataView<>() {
                        @Override
                        public void onSuccess(TAPGetUserResponse response) {
                            super.onSuccess(response);
                            TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
                            TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(TapUIChatActivity.this,
                                    TAPRoomModel.Builder(roomId, response.getUser().getFullname(), TYPE_PERSONAL, response.getUser().getImageURL(), ""), response.getUser());
                            TAPChatProfileActivity.Companion.start(TapUIChatActivity.this, instanceKey, TAPRoomModel.Builder(roomId, response.getUser().getFullname(), TYPE_PERSONAL, response.getUser().getImageURL(), ""), response.getUser());
                            hideUnreadButton();
                        }
                    });
                }
            } else {
                openGroupMemberProfile(message.getUser());
            }
        }

        @Override
        public void onBubbleTapped(TAPMessageModel message) {
            if ((null != message.getIsFailedSend() && message.getIsFailedSend()) ||
                (null != message.getIsSending() && message.getIsSending()) ||
                (null != message.getIsDeleted() && message.getIsDeleted())
            ) {
                return;
            }
            getMessageReadCount(message);
        }

        @Override
        public void onOutsideClicked(TAPMessageModel message) {
            hideKeyboards();
        }

        @Override
        public void onBubbleExpanded() {
            if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onLayoutLoaded(TAPMessageModel message) {
            if (/*message.getUser().getUserID().equals(vm.getMyUserModel().getUserID()) ||*/
                    messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
            if (null == vm.getRoom() ||
                    vm.getRoom().getType() != TYPE_PERSONAL ||
                    !vm.getOtherUserID().equals(onlineStatus.getUser().getUserID())
            ) {
                return;
            }
            setChatRoomStatus(onlineStatus);
        }

        @Override
        public void onReceiveStartTyping(TAPTypingModel typingModel) {
            if (null == vm.getRoom() ||
                    null == typingModel.getUser() ||
                    !typingModel.getRoomID().equals(vm.getRoom().getRoomID()) ||
                    (vm.getRoom().getType() == TYPE_PERSONAL &&
                            !vm.getOtherUserID().equals(typingModel.getUser().getUserID())) ||
                    (vm.getRoom().getType() != TYPE_PERSONAL &&
                            null != vm.getRoom().getParticipants() &&
                            !vm.getRoom().getParticipants().contains(typingModel.getUser()))
            ) {
                return;
            }
            vm.addGroupTyping(typingModel.getUser());
            showTypingIndicator();
        }

        @Override
        public void onReceiveStopTyping(TAPTypingModel typingModel) {
            if (null == vm.getRoom() ||
                    null == typingModel.getUser() ||
                    !typingModel.getRoomID().equals(vm.getRoom().getRoomID()) ||
                    (vm.getRoom().getType() == TYPE_PERSONAL &&
                            !vm.getOtherUserID().equals(typingModel.getUser().getUserID())) ||
                    (vm.getRoom().getType() != TYPE_PERSONAL &&
                            null != vm.getRoom().getParticipants() &&
                            !vm.getRoom().getParticipants().contains(typingModel.getUser()))
            ) {
                return;
            }
            if (typingModel.getRoomID().equals(vm.getRoom().getRoomID())) {
                vm.removeGroupTyping(typingModel.getUser().getUserID());
                if (0 < vm.getGroupTypingSize()) {
                    showTypingIndicator();
                } else {
                    hideTypingIndicator();
                }
            }
        }

        @Override
        public void onMessageSelected(TAPMessageModel message) {
            if (vm.getSelectedMessages().contains(message)) {
                vm.removeSelectedMessage(message);
            } else {
                vm.addSelectedMessage(message);
            }
            // handle read count
            if (!TapUI.getInstance(instanceKey).isReadStatusHidden()) {
                // TODO: 04/11/22 call get read count API MU
            }
            messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID())));
            String forwardCountText = vm.getSelectedMessages().size() + "/" + MAX_FORWARD_COUNT +" " + getString(R.string.tap_selected);
            tvForwardCount.setText(forwardCountText);
        }

        @Override
        public void onArrowButtonClicked(TAPMessageModel message) {
            super.onArrowButtonClicked(message);
            if (!isArrowButtonTapped) {
                if (message.getForwardFrom() != null) {
                    TAPChatManager.getInstance(instanceKey).triggerSavedMessageBubbleArrowTapped(message);
                    isArrowButtonTapped = true;
                    String roomId = message.getForwardFrom().getRoomID();
                    String localId = message.getForwardFrom().getLocalID();
                    if (roomId.contains("-")) {
                        //PERSONAL
                        if (TAPUtils.isSavedMessagesRoom(roomId, instanceKey)) {
                            scrollToMessage(localId);
                            isArrowButtonTapped = false;
                        } else {
                            String otherUserId = TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(roomId);
                            TAPUserModel otherUser = TAPContactManager.getInstance(instanceKey).getUserData(otherUserId);
                            if (otherUser != null) {
                                TapUIChatActivity.start(TapUIChatActivity.this, instanceKey, roomId, otherUser.getFullname(), otherUser.getImageURL(), TYPE_PERSONAL, message.getRoom().getColor(), localId);
                            } else {
                                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(message.getForwardFrom().getUserID(), new TAPDefaultDataView<>() {
                                    @Override
                                    public void onSuccess(TAPGetUserResponse response) {
                                        super.onSuccess(response);
                                        TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
                                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID())));
                                        TapUIChatActivity.start(TapUIChatActivity.this, instanceKey, roomId, response.getUser().getFullname(), response.getUser().getImageURL(), TYPE_PERSONAL, message.getRoom().getColor(), localId);
                                    }
                                });
                            }
                        }
                    } else {
                        //GROUP
                        TAPRoomModel groupModel = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(roomId);
                        if (groupModel != null) {
                            TapUIChatActivity.start(TapUIChatActivity.this, instanceKey, roomId, groupModel.getName(), groupModel.getImageURL(), TYPE_GROUP, groupModel.getColor(), localId);
                        } else {
                            TAPDataManager.getInstance(instanceKey).getChatRoomData(roomId, new TAPDefaultDataView<>() {
                                @Override
                                public void onSuccess(TAPCreateRoomResponse response) {
                                    super.onSuccess(response);
                                    TAPRoomModel room = response.getRoom();
                                    if (room != null) {
                                        room.setAdmins(response.getAdmins());
                                        room.setParticipants(response.getParticipants());
                                        TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(room);
                                        TapUIChatActivity.start(TapUIChatActivity.this, instanceKey, roomId, room.getName(), room.getImageURL(), TYPE_GROUP, room.getColor(), localId);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

        @Override
        public void onRequestUserData(TAPMessageModel message) {
            super.onRequestUserData(message);
            if (message.getForwardFrom() != null) {
                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(message.getForwardFrom().getUserID(), new TAPDefaultDataView<>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        super.onSuccess(response);
                        TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID())));
                    }
                });
            }
        }
    };

    private final TapCoreContactListener coreContactListener = new TapCoreContactListener() {
        @Override
        public void onContactBlocked(@NonNull TAPUserModel user) {
            if (vm.getRoom().getType() == TYPE_PERSONAL &&
                vm.getRoom().getRoomID().equals(TAPChatManager.getInstance(instanceKey).arrangeRoomId(vm.getMyUserModel().getUserID(), user.getUserID()))
            ) {
                showUnblockButton();
                runOnUiThread(() -> clContactAction.setVisibility(View.GONE));
            }
        }

        @Override
        public void onContactUnblocked(@NonNull TAPUserModel user) {
            setRoomState();
        }
    };

    public void closeActivity() {
        rvCustomKeyboard.setVisibility(View.GONE);
        onBackPressed();
    }

    private void blockContact() {
        // TODO: 19 November 2019
        clContactAction.setVisibility(View.GONE);
    }

    private void addUserToContacts() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance(instanceKey).addContactApi(vm.getOtherUserID(), addContactView);
    }

    private void dismissContactAction() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance(instanceKey).saveChatRoomContactActionDismissed(vm.getRoom().getRoomID());
    }

    private void openRoomProfile() {
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), vm.getOtherUserModel());
        hideUnreadButton();
    }

    private void openGroupMemberProfile(TAPUserModel groupMember) {
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), groupMember);
        hideUnreadButton();
    }

    private void loadProfilePicture(String image, ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        glide.load(image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                runOnUiThread(() -> loadInitialsToProfilePicture(imageView, tvAvatarLabel));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ImageViewCompat.setImageTintList(imageView, null);
                tvAvatarLabel.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void loadInitialsToProfilePicture(ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        if (tvAvatarLabel == tvMyAvatarLabelEmpty) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(this, TAPChatManager.getInstance(instanceKey).getActiveUser().getFullname())));
            tvAvatarLabel.setText(TAPUtils.getInitials(TAPChatManager.getInstance(instanceKey).getActiveUser().getFullname(), 2));
        } else {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(this, vm.getRoom().getName())));
            tvAvatarLabel.setText(TAPUtils.getInitials(vm.getRoom().getName(), vm.getRoom().getType() == TYPE_PERSONAL ? 2 : 1));
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setVisibility(View.VISIBLE);
    }

    private void showMessageList() {
        flMessageList.setVisibility(View.VISIBLE);
        cvEmptySavedMessages.setVisibility(View.GONE);
        clEmptyChat.setVisibility(View.GONE);

//        if (rvMessageList.getVisibility() != View.VISIBLE) {
            rvMessageList.setVisibility(View.VISIBLE);
//        }

//        flMessageList.post(() -> {
//            TAPMessageModel message = messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition());
//            if (null != message) {
//                tvDateIndicator.setVisibility(View.VISIBLE);
//                tvDateIndicator.setText(TAPTimeFormatter.dateStampString(this, message.getCreated()));
//            }
//        });
    }

    private void updateUnreadCount() {
        runOnUiThread(() -> {
            if (vm.isOnBottom() || vm.getUnreadCount() == 0) {
                tvBadgeUnread.setVisibility(View.GONE);
                if (View.GONE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.GONE);
                }
            } else if (vm.getUnreadCount() > 0) {
                tvBadgeUnread.setText(String.valueOf(vm.getUnreadCount()));
                tvBadgeUnread.setVisibility(View.VISIBLE);
                if (View.VISIBLE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.VISIBLE);
                }
            } else if (View.VISIBLE == ivToBottom.getVisibility()) {
                ivToBottom.setVisibility(View.GONE);
            }
        });
    }

    private void updateMentionCount() {
        runOnUiThread(() -> {
            if (!TapUI.getInstance(instanceKey).isMentionUsernameDisabled() && vm.getUnreadMentionCount() > 0) {
                tvBadgeMentionCount.setText(String.valueOf(vm.getUnreadMentionCount()));
                tvBadgeMentionCount.setVisibility(View.VISIBLE);
                if (View.VISIBLE != ivMentionAnchor.getVisibility()) {
                    ivMentionAnchor.setVisibility(View.VISIBLE);
                }
            } else {
                ivMentionAnchor.setVisibility(View.GONE);
                tvBadgeMentionCount.setVisibility(View.GONE);
            }
        });
    }

    private void updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread(() -> {
            while (rvMessageList.getItemDecorationCount() > 0) {
                rvMessageList.removeItemDecorationAt(0);
            }
            rvMessageList.addItemDecoration(new TAPVerticalDecoration(TAPUtils.dpToPx(10), 0, messageAdapter.getItemCount() - 1));
        });
    }

    private void checkForwardLayout(@Nullable TAPMessageModel message, @Nullable ArrayList<TAPMessageModel> messages, int quoteAction) {
        runOnUiThread(() -> {
            switch (quoteAction) {
                case FORWARD:
                    if (messages == null || messages.isEmpty()) {
                        return;
                    }
                    if (messages.size() > 1) {
                        vm.setForwardedMessages(messages, quoteAction);
                        clQuote.setVisibility(View.VISIBLE);
                        vQuoteDecoration.setVisibility(View.VISIBLE);
                        rcivQuoteImage.setVisibility(View.GONE);
                        String titleText = String.format(getString(R.string.tap_forward_sf_messages), messages.size());
                        tvQuoteTitle.setText(titleText);
                        ArrayList<String> senders = new ArrayList<>();
                        for (TAPMessageModel forwardedMessage : messages) {
                            String name;
                            if (forwardedMessage.getForwardFrom() != null && !forwardedMessage.getForwardFrom().getFullname().isEmpty()) {
                                name = TAPUtils.getFirstWordOfString(forwardedMessage.getForwardFrom().getFullname());
                            } else {
                                name = TAPUtils.getFirstWordOfString(forwardedMessage.getUser().getFullname());
                            }
                            if (!senders.contains(name)) {
                                senders.add(name);
                            }
                        }
                        String quoteContent = getString(R.string.tap_from) + " " + TAPUtils.concatStringList(senders);
                        tvQuoteContent.setText(quoteContent);
                        tvQuoteContent.setMaxLines(2);
                        boolean hadFocus = etChat.hasFocus();
                        if (!hadFocus && etChat.getSelectionEnd() == 0) {
                            etChat.setSelection(etChat.getText().length());
                        }
                    } else {
                        showQuoteLayout(messages.get(0), quoteAction, false);
                    }
                    break;
                case EDIT:
                    showQuoteLayout(message, quoteAction, true);
                    break;
                default:
                    showQuoteLayout(message, quoteAction, false);
                    break;
            }
        });
    }

    private void showEditLayout(@Nullable TAPMessageModel message, int quoteAction) {
        if (null == message) {
            return;
        }
        vm.setQuotedMessage(message, quoteAction);
        runOnUiThread(() -> {
            clQuote.setVisibility(View.VISIBLE);
            rcivQuoteImage.setPadding(0, 0, 0, 0);
            // Add other quotable message type here
            if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) && null != message.getData()) {
                // Show image quote
                vQuoteDecoration.setVisibility(View.GONE);
                // TODO: 29 January 2019 IMAGE MIGHT NOT EXIST IN CACHE
                Drawable drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(message);
//                Drawable drawable = null;
//                String fileID = (String) message.getData().get(FILE_ID);
//                if (null != fileID && !fileID.isEmpty()) {
//                    drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(fileID);
//                }
//                if (null == drawable) {
//                    String fileUrl = (String) message.getData().get(FILE_URL);
//                    if (null != fileUrl && !fileUrl.isEmpty()) {
//                        drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(TAPUtils.removeNonAlphaNumeric(fileUrl).toLowerCase());
//                    }
//                }
                if (null != drawable) {
                    rcivQuoteImage.setImageDrawable(drawable);
                } else {
                    // Show small thumbnail
                    Drawable thumbnail = new BitmapDrawable(
                            getResources(),
                            TAPFileUtils.decodeBase64(
                                    (String) (null == message.getData().get(THUMBNAIL) ? "" :
                                            message.getData().get(THUMBNAIL))));
                    rcivQuoteImage.setImageDrawable(thumbnail);
                }
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteTitle.setText(R.string.tap_edit_message);
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
                etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(TapTalk.getMaxCaptionLength(instanceKey))});
                etChat.setText(message.getData().get(CAPTION).toString());
            } else if (null != message.getData() && null != message.getData().get(FILE_URL) && (message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO)) {
                // Show image quote from file URL
                glide.load((String) message.getData().get(FILE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);
                tvQuoteTitle.setText(R.string.tap_edit_message);
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
                etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(TapTalk.getMaxCaptionLength(instanceKey))});
                etChat.setText(message.getData().get(CAPTION).toString());
            } else if (null != message.getData() && null != message.getData().get(IMAGE_URL)) {
                // Show image quote from image URL
                glide.load((String) message.getData().get(IMAGE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);
                tvQuoteTitle.setText(R.string.tap_edit_message);
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
                etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(TapTalk.getMaxCaptionLength(instanceKey))});
                etChat.setText(message.getData().get(CAPTION).toString());
            } else {
                // Show text quote
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);
                tvQuoteTitle.setText(R.string.tap_edit_message);
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(2);
                etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(CHARACTER_LIMIT)});
                etChat.setText(message.getBody());
            }
            boolean hadFocus = etChat.hasFocus();
            TAPUtils.showKeyboard(this, etChat);
            new Handler().postDelayed(() -> etChat.requestFocus(), 300L);
            if (!hadFocus && etChat.getSelectionEnd() == 0) {
                etChat.setSelection(etChat.getText().length());
            }
        });
    }

    private void showQuoteLayout(@Nullable TAPMessageModel message, int quoteAction, boolean showKeyboard) {
        if (null == message) {
            return;
        }
        vm.setQuotedMessage(message, quoteAction);
        boolean quotedOwnMessage = null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getUser().getUserID());
        runOnUiThread(() -> {
            glide.clear(rcivQuoteImage);
            rcivQuoteImage.setImageDrawable(null);
            rcivQuoteImage.setColorFilter(null);
            rcivQuoteImage.setBackground(null);
            rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            rcivQuoteImage.setPadding(0, 0, 0, 0);
            clQuote.setVisibility(View.VISIBLE);

            RequestListener<Drawable> glideListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    runOnUiThread(() -> {
                        if (message.getType() == TYPE_LINK) {
                            // Show link icon
                            vQuoteDecoration.setVisibility(View.GONE);
                            rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_link_white));
                            rcivQuoteImage.setBackgroundDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_rounded_primary_8dp));
                            int padding = TAPUtils.dpToPx(10);
                            rcivQuoteImage.setPadding(padding, padding, padding, padding);
                            rcivQuoteImage.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconFileWhite));
                            rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            rcivQuoteImage.setVisibility(View.VISIBLE);
                        }
                        else {
                            vQuoteDecoration.setVisibility(View.VISIBLE);
                            rcivQuoteImage.setVisibility(View.GONE);
                        }
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    runOnUiThread(() -> {
                        vQuoteDecoration.setVisibility(View.GONE);
                        rcivQuoteImage.setColorFilter(null);
                        rcivQuoteImage.setBackground(null);
                        rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        rcivQuoteImage.setVisibility(View.VISIBLE);
                    });
                    return false;
                }
            };

            // Add other quotable message type here
            if ((message.getType() == TYPE_IMAGE ||
                message.getType() == TYPE_VIDEO) &&
                null != message.getData()
            ) {
                // Show image quote
                vQuoteDecoration.setVisibility(View.GONE);
                Drawable drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(message);
                if (null != drawable) {
                    rcivQuoteImage.setImageDrawable(drawable);
                }
                else if (null == rcivQuoteImage.getDrawable() && null != message.getData().get(FILE_URL)) {
                    // Show image from url
                    String url = (String) message.getData().get(FILE_URL);
                    glide.load(url).listener(glideListener).into(rcivQuoteImage);
                }
                else if (null == rcivQuoteImage.getDrawable() && null != message.getData().get(THUMBNAIL)) {
                    // Show small thumbnail
                    Drawable thumbnail = new BitmapDrawable(
                            getResources(),
                            TAPFileUtils.decodeBase64((String) message.getData().get(THUMBNAIL))
                    );
                    rcivQuoteImage.setImageDrawable(thumbnail);
                }
                if (null != rcivQuoteImage.getDrawable()) {
                    rcivQuoteImage.setColorFilter(null);
                    rcivQuoteImage.setBackground(null);
                    rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    rcivQuoteImage.setVisibility(View.VISIBLE);
                }
                else {
                    // Hide image
                    vQuoteDecoration.setVisibility(View.VISIBLE);
                    rcivQuoteImage.setVisibility(View.GONE);
                }

                if (quoteAction == EDIT) {
                    tvQuoteTitle.setText(R.string.tap_edit_message);
                }
                else if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                }
                else {
                    tvQuoteTitle.setText(message.getUser().getFullname());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else if (message.getType() == TYPE_FILE && null != message.getData()) {
                // Show file quote
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_documents_white));
                rcivQuoteImage.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconFileWhite));
                rcivQuoteImage.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_quote_layout_file));
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                if (quoteAction == EDIT) {
                    tvQuoteTitle.setText(R.string.tap_edit_message);
                    tvQuoteContent.setText(message.getBody());
                }
                else {
                    tvQuoteTitle.setText(TAPUtils.getFileDisplayName(message));
                    tvQuoteContent.setText(TAPUtils.getFileDisplayInfo(message));
                }
                tvQuoteContent.setMaxLines(1);
            } else if (
                null != message.getData() &&
                (null != message.getData().get(FILE_URL) ||
                null != message.getData().get(IMAGE_URL) ||
                null != message.getData().get(IMAGE))
            ) {
                // Show image quote from URL
                String url = "";
                if (null != message.getData().get(IMAGE_URL)) {
                    url = (String) message.getData().get(IMAGE_URL);
                }
                else if (null != message.getData().get(IMAGE)) {
                    url = (String) message.getData().get(IMAGE);
                }
                else if (null != message.getData().get(FILE_URL) && message.getType() != TYPE_LINK) {
                    url = (String) message.getData().get(FILE_URL);
                }
                if (url != null && !url.isEmpty()) {
                    glide.load(url)
                        .placeholder(R.drawable.tap_ic_link_white)
                        .listener(glideListener)
                        .error(R.drawable.tap_ic_link_white)
                        .into(rcivQuoteImage);
                }
                else if (message.getType() == TYPE_LINK) {
                    // Show link icon
                    vQuoteDecoration.setVisibility(View.GONE);
                    rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_link_white));
                    rcivQuoteImage.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_rounded_primary_8dp));
                    int padding = TAPUtils.dpToPx(10);
                    rcivQuoteImage.setPadding(padding, padding, padding, padding);
                    rcivQuoteImage.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconFileWhite));
                    rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    rcivQuoteImage.setVisibility(View.VISIBLE);
                }
                else {
                    // Hide image
                    vQuoteDecoration.setVisibility(View.VISIBLE);
                    rcivQuoteImage.setVisibility(View.GONE);
                }

                if (quoteAction == EDIT) {
                    tvQuoteTitle.setText(R.string.tap_edit_message);
                }
                else if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                }
                else {
                    tvQuoteTitle.setText(message.getUser().getFullname());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show text quote
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);

                if (quoteAction == EDIT) {
                    tvQuoteTitle.setText(R.string.tap_edit_message);
                }
                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                }
                else {
                    tvQuoteTitle.setText(message.getUser().getFullname());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(2);
            }
            if (quoteAction == EDIT) {
                if (message.getData() != null && message.getData().get(CAPTION) != null && message.getData().get(CAPTION) instanceof String) {
                    etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(TapTalk.getMaxCaptionLength(instanceKey))});
                    etChat.setText(message.getData().get(CAPTION).toString());
                }
                else {
                    etChat.setFilters(new InputFilter[] {new InputFilter.LengthFilter(CHARACTER_LIMIT)});
                    etChat.setText(message.getBody());
                }
            }
            boolean hadFocus = etChat.hasFocus();
            if (/*hadFocus && */showKeyboard) {
                TAPUtils.showKeyboard(this, etChat);
                //clContainer.post(() -> etChat.requestFocus());
                // FIXME: 17 Apr 2020
                new Handler().postDelayed(() -> etChat.requestFocus(), 300L);
            }
            if (!hadFocus && etChat.getSelectionEnd() == 0) {
                etChat.setSelection(etChat.getText().length());
            }
        });
    }

    private void hideQuoteLayout() {
        if (vm.getQuoteAction() == EDIT) {
            etChat.setFilters(new InputFilter[] { });
            etChat.setText("");
        }
        vm.setQuotedMessage(null, 0);
        vm.setForwardedMessages(null, 0);
        boolean hasFocus = etChat.hasFocus();
        if (clQuote.getVisibility() == View.VISIBLE) {
            runOnUiThread(() -> {
                clQuote.setVisibility(View.GONE);
                if (hasFocus) {
                    clQuote.post(() -> etChat.requestFocus());
                }
            });
        }
    }

    private void showScheduleMessageButton() {
        gScheduleMessage.setVisibility(View.VISIBLE);
        hideKeyboards();
    }

    private void hideScheduleMessageButton() {
        gScheduleMessage.setVisibility(View.GONE);
        hideKeyboards();
    }

    private void pinnedMessageLayoutOnClick() {
        scrollToMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()).getLocalID());
        vm.increasePinnedMessageIndex(1);
        clPinnedIndicator.select(vm.getPinnedMessageIndex());
        setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
        int indexLimit = PAGE_SIZE * (vm.getPinnedMessagesPageNumber() - 1) - PAGE_SIZE / 2;
        if (vm.getPinnedMessageIndex() >= indexLimit && vm.isHasMorePinnedMessages() && vm.getPinnedMessages().size() <= PAGE_SIZE * vm.getPinnedMessagesPageNumber()) {
            vm.setLoadPinnedMessages(true);
            getPinnedMessages("");
        }
    }

    private void setPinnedMessage(TAPMessageModel message) {
        runOnUiThread(() ->{
            if (message == null) {
                vm.setPinnedMessageIndex(0);
                clPinnedMessage.setVisibility(View.GONE);
            } else {
                if (clPinnedMessage.getVisibility() == View.GONE) {
                    clPinnedMessage.setVisibility(View.VISIBLE);
                }
                int number = vm.getPinnedMessageIds().size() - vm.getPinnedMessageIndex();
                if (vm.getPinnedMessageIndex() == 0) {
                    tvPinnedLabel.setText(getString(R.string.tap_pinned_message));
                } else {
                    tvPinnedLabel.setText(String.format(Locale.getDefault(), "%s #%d", getString(R.string.tap_pinned_message), number));
                }
                tvPinnedMessage.setText(message.getBody());
                if (message.getData() != null) {
                    Drawable drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(message);
//                    Drawable drawable = null;
//                    String fileID = (String) message.getData().get(FILE_ID);
//                    if (null != fileID && !fileID.isEmpty()) {
//                        drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(fileID);
//                    }
//                    if (null == drawable) {
//                        String fileUrl = (String) message.getData().get(FILE_URL);
//                        if (null != fileUrl && !fileUrl.isEmpty()) {
//                            drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(TAPUtils.removeNonAlphaNumeric(fileUrl).toLowerCase());
//                        }
//                    }
                    if (null != drawable) {
                        rcivPinnedImage.setImageDrawable(drawable);
                        rcivPinnedImage.setVisibility(View.VISIBLE);
                    } else {
                        if (message.getData().get(THUMBNAIL) != null) {
                            // Show small thumbnail
                            Drawable thumbnail = new BitmapDrawable(
                                    getResources(),
                                    TAPFileUtils.decodeBase64((String) (message.getData().get(THUMBNAIL))));
                            if (thumbnail.getIntrinsicHeight() <= 0) {
                                // Set placeholder image if thumbnail fails to load
                                thumbnail = ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_grey_e4);
                            }
                            rcivPinnedImage.setImageDrawable(thumbnail);
                            rcivPinnedImage.setVisibility(View.VISIBLE);
                        } else {
                            rcivPinnedImage.setVisibility(View.GONE);
                        }
                    }
                } else {
                    rcivPinnedImage.setVisibility(View.GONE);
                }
            }
        });
    }

    private void scrollToBottom() {
        ivToBottom.setVisibility(View.GONE);
        rvMessageList.scrollToPosition(0);
        tvDateIndicator.setVisibility(View.GONE);
        vm.setOnBottom(true);
        vm.clearUnreadMessages();
        vm.clearUnreadMentions();
        updateUnreadCount();
        updateMentionCount();
    }

    private void toggleCustomKeyboard() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            showNormalKeyboard();
        } else {
            showCustomKeyboard();
        }
    }

    private void showNormalKeyboard() {
        runOnUiThread(() -> {
            rvCustomKeyboard.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
            } else {
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
            }
            ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
            ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
            //etChat.requestFocus();
            TAPUtils.showKeyboard(this, etChat);
        });
    }

    private void showCustomKeyboard() {
        runOnUiThread(() -> {
            TAPUtils.dismissKeyboard(this);
            etChat.clearFocus();
            new Handler().postDelayed(() -> {
                rvCustomKeyboard.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_show_keyboard_ripple));
                } else {
                    ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_show_keyboard));
                }
                ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_keyboard_white));
                ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerShowKeyboard));
            }, 150L);
        });
    }

    private void hideKeyboards() {
        runOnUiThread(() -> {
            rvCustomKeyboard.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
            } else {
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
            }
            ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
            ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
            TAPUtils.dismissKeyboard(this);
        });
    }

    private void setLockedRecordingState() {
        hideChatField();
        setSendButtonDisabled();
        ivVoiceNote.setVisibility(View.GONE);
        clSwipeVoiceNote.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setVisibility(View.VISIBLE);
        vm.setRecordingState(LOCKED_RECORD);
    }

    private void setHoldRecordingState() {
        // TODO: 12/04/22 voice note recording improvement MU
        hideChatField();
        setSendButtonDisabled();
        clVoiceNote.setVisibility(View.VISIBLE);
        clSwipeVoiceNote.setVisibility(View.VISIBLE);
        vm.setRecordingState(HOLD_RECORD);
    }

    private void setDefaultRecordingState() {
        etChat.setVisibility(View.VISIBLE);
        vm.setRecordingState(DEFAULT);
        etChat.setText(etChat.getText().toString());
        ivVoiceNote.setVisibility(View.VISIBLE);
        showAttachmentButton();
        if (vm.isCustomKeyboardEnabled() && etChat.getText().toString().isEmpty()) {
            ivChatMenu.setVisibility(View.VISIBLE);
        }
        clSwipeVoiceNote.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        ivVoiceNoteControl.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_stop_orange));
        ivVoiceNoteControl.setTranslationX(TAPUtils.dpToPx(getResources(), -2f));
        ivVoiceNoteControl.setTranslationY(TAPUtils.dpToPx(getResources(), 2f));
    }

    private void setFinishedRecordingState() {
        seekBar.setVisibility(View.VISIBLE);
        vm.setRecordingState(FINISH);
        hideChatField();
        setSendButtonDisabled();
        ivVoiceNote.setVisibility(View.GONE);
        clSwipeVoiceNote.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setVisibility(View.VISIBLE);
        seekBar.setEnabled(false);
        ivVoiceNoteControl.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_play_orange));
        ivVoiceNoteControl.setTranslationX(0f);
        ivVoiceNoteControl.setTranslationY(0f);
    }

    private void setPlayingState() {
        vm.setRecordingState(PLAY);
        hideChatField();
        setSendButtonDisabled();
        ivVoiceNote.setVisibility(View.GONE);
        clSwipeVoiceNote.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_pause_orange));
        ivVoiceNoteControl.setTranslationX(TAPUtils.dpToPx(getResources(), -2f));
        ivVoiceNoteControl.setTranslationY(TAPUtils.dpToPx(getResources(), 2f));
    }

    private void setPausedState() {
        vm.setRecordingState(PAUSE);
        hideChatField();
        setSendButtonDisabled();
        ivVoiceNote.setVisibility(View.GONE);
        clSwipeVoiceNote.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setVisibility(View.VISIBLE);
        ivVoiceNoteControl.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_play_orange));
        ivVoiceNoteControl.setTranslationX(0f);
        ivVoiceNoteControl.setTranslationY(0f);
    }

    private void hideChatField() {
        etChat.setVisibility(View.GONE);
        ivButtonAttach.setVisibility(View.GONE);
        ivSchedule.setVisibility(View.GONE);
        ivChatMenu.setVisibility(View.GONE);
    }

    private void startRecording() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RECORD_AUDIO);
//            }
//        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RECORD_AUDIO);
        if (!TAPUtils.hasPermissions(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{ Manifest.permission.RECORD_AUDIO },
                PERMISSION_RECORD_AUDIO
            );
        }
        else if (!TAPUtils.hasPermissions(this, TAPUtils.getStoragePermissions(true))) {
            ActivityCompat.requestPermissions(
                this,
                TAPUtils.getStoragePermissions(true),
                PERMISSION_RECORD_AUDIO
            );
        }
        else {
            messageAdapter.removePlayer();
            messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(messageAdapter.getLastLocalId())));
            setLockedRecordingState();
            audioManager.startRecording();
            audioManager.getRecordingTime().observe(TapUIChatActivity.this, s -> tvRecordTime.setText(s));
        }
    }

    private void stopRecording() {
        if (vm.getRecordingState() == HOLD_RECORD || vm.getRecordingState() == LOCKED_RECORD) {
            audioManager.stopRecording();
        }
    }

    private void removeRecording() {
        stopRecording();
        audioManager.deleteRecording(this);
        vm.setPausedPosition(0);
        stopProgressTimer();
        if (vm.getMediaPlayer() != null) {
            if (vm.getMediaPlayer().isPlaying()) {
                vm.getMediaPlayer().stop();
            }
            vm.setMediaPlayer(null);
        }
        seekBar.setProgress(0);
        setDefaultRecordingState();
    }

    private void resumeVoiceNote() {
        seekBar.setEnabled(true);
        try {
            if (vm.getMediaPlayer() == null) {
                vm.setMediaPlayer(new MediaPlayer());
                loadMediaPlayer();
                vm.getMediaPlayer().prepareAsync();
            } else {
                setPlayingState();
                startProgressTimer();
                vm.getMediaPlayer().start();
                if (!messageAdapter.lastLocalId.isEmpty()) {
                    messageAdapter.removePlayer();
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(messageAdapter.lastLocalId)));
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void pauseVoiceNote() {
        setPausedState();
        vm.setMediaPlaying(false);
        vm.setPausedPosition(vm.getMediaPlayer().getCurrentPosition());
        vm.getMediaPlayer().pause();
        stopProgressTimer();
    }

    private void onVoiceNoteControlClick() {
        switch (vm.getRecordingState()) {
            case PLAY:
                pauseVoiceNote();
                break;
            case PAUSE:
            case FINISH:
                resumeVoiceNote();
                break;
            case DEFAULT:
            case HOLD_RECORD:
            case LOCKED_RECORD:
                if (!messageAdapter.lastLocalId.isEmpty()) {
                    messageAdapter.removePlayer();
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(messageAdapter.lastLocalId)));
                }
                showFinishedRecording();
                break;
        }
    }

    private void showFinishedRecording() {
        if (vm.getRecordingState() == HOLD_RECORD || vm.getRecordingState() == LOCKED_RECORD || vm.getRecordingState() == FINISH) {
            if (audioManager.getRecordingSeconds() < 1) {
                removeRecording();
            } else {
                stopRecording();
                vm.setAudioFile(audioManager.getRecording());
                MediaScannerConnection.scanFile(
                        TapUIChatActivity.this,
                        new String[]{audioManager.getRecording().getAbsolutePath()}, null,
                        (s, uri) -> {
                            try {
                                Log.v("onScanCompleted", uri.getPath());
                                vm.setVoiceUri(uri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                setFinishedRecordingState();
                setSendButtonEnabled();
            }
        }
    }

    private void loadMediaPlayer() {
        try {
            vm.getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
            vm.getMediaPlayer().setDataSource(TapUIChatActivity.this, vm.getVoiceUri());
            vm.getMediaPlayer().setOnPreparedListener(preparedListener);
            vm.getMediaPlayer().setOnCompletionListener(completionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            try {
                String currentTimeString = TAPUtils.getMediaDurationString(vm.getMediaPlayer().getCurrentPosition(), vm.getMediaPlayer().getDuration());
                tvRecordTime.setText(currentTimeString);
            } catch (Exception e) {
                Log.e(TAG, "onProgressChanged: " + e.getMessage());
            }
            if (vm.isSeeking()) {
                vm.getMediaPlayer().seekTo(vm.getMediaPlayer().getDuration() * seekBar.getProgress() / seekBar.getMax());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            vm.setSeeking(true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            vm.setSeeking(false);
            if (vm.isMediaPlaying()) {
                setPlayingState();
            }
            vm.getMediaPlayer().seekTo(vm.getMediaPlayer().getDuration() * seekBar.getProgress() / seekBar.getMax());
        }
    };

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            vm.setDuration(mediaPlayer.getDuration());
            vm.setMediaPlaying(true);
            startProgressTimer();
            tvRecordTime.setText(TAPUtils.getMediaDurationString(vm.getDuration(), vm.getDuration()));
            mediaPlayer.seekTo(vm.getPausedPosition());
            mediaPlayer.setOnSeekCompleteListener(onSeekListener);
            vm.setMediaPlayer(mediaPlayer);
            runOnUiThread(() -> {
                vm.getMediaPlayer().start();
                setPlayingState();
            });
        }
    };

    private MediaPlayer.OnSeekCompleteListener onSeekListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            try {
                tvRecordTime.setText(TAPUtils.getMediaDurationString(mediaPlayer.getCurrentPosition(), vm.getDuration()));
            } catch (Exception e) {
                Log.e(TAG, "onProgressChanged: " + e.getMessage());
            }
            if (!vm.isSeeking() && vm.isMediaPlaying()) {
                mediaPlayer.start();
                startProgressTimer();
            } else {
                vm.setPausedPosition(mediaPlayer.getCurrentPosition());
                if (vm.getPausedPosition() >= vm.getDuration()) {
                    vm.setPausedPosition(0);
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = mediaPlayer -> setFinishedRecordingState();

    private void startProgressTimer() {
        if (null != vm.getDurationTimer()) {
            return;
        }
        vm.setDurationTimer(new Timer());
        vm.getDurationTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (vm.getMediaPlayer() != null && vm.getDuration() > 0) {
                        seekBar.setProgress(vm.getMediaPlayer().getCurrentPosition() * seekBar.getMax() / vm.getDuration());
                    }
                });
            }
        }, 0, 10L);
    }

    private void stopProgressTimer() {
        if (null == vm.getDurationTimer()) {
            return;
        }
        vm.getDurationTimer().cancel();
        vm.setDurationTimer(null);
    }

    // TODO: 25/04/22 use later for improvement MU
    private OnSwipeTouchListener swipeTouchListener = new OnSwipeTouchListener(TapUIChatActivity.this) {
        @Override
        public boolean onSwipeLeft() {
            setDefaultRecordingState();
            return true;
        }

        @Override
        public boolean onSwipeTop() {
            setLockedRecordingState();
            return true;
        }

        @Override
        public boolean onActionUp() {
                stopRecording();
            return true;
        }
    };

    private void showTooltip() {
        if (gTooltip.getVisibility() == View.GONE) {
            gTooltip.setVisibility(View.VISIBLE);
            new Handler(getMainLooper()).postDelayed(() -> {
                gTooltip.setVisibility(View.GONE);
            }, 2000);
        } else {
            gTooltip.setVisibility(View.GONE);
        }
    }

    private void openAttachMenu() {
        //if (!etChat.hasFocus()) {
        //    etChat.requestFocus();
        //}
        hideKeyboards();
        TAPAttachmentBottomSheet attachBottomSheet = new TAPAttachmentBottomSheet(instanceKey, attachmentListener);
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void handleLongPressMenuSelected(@Nullable TAPMessageModel message, TapLongPressMenuItem longPressMenuItem) {
        String data = "";
        if (longPressMenuItem.getUserInfo() != null && longPressMenuItem.getUserInfo().get(DATA) != null) {
            data = (String) longPressMenuItem.getUserInfo().get(DATA);
        }
        switch (longPressMenuItem.getId()) {
            case TAPDefaultConstant.LongPressMenuID.REPLY:
                if (message != null) {
                    attachmentListener.onReplySelected(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.FORWARD:
                if (message != null) {
                    attachmentListener.onForwardSelected(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.COPY:
                if (!data.isEmpty()) {
                    attachmentListener.onCopySelected(data);
                }
                else if (message != null) {
                    switch (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message.getType())) {
                        case TYPE_IMAGE_MESSAGE:
                        case TYPE_VIDEO_MESSAGE:
                        case TYPE_FILE_MESSAGE:
                        case TYPE_VOICE_MESSAGE:
                            // TODO: 4 March 2019 TEMPORARY CLIPBOARD FOR IMAGE & VIDEO
                            if (null != message.getData() && message.getData().get(CAPTION) instanceof String) {
                                attachmentListener.onCopySelected((String) message.getData().get(CAPTION));
                            }
                            break;
                        case TYPE_LOCATION_MESSAGE:
                            if (null != message.getData() && message.getData().get(ADDRESS) instanceof String) {
                                attachmentListener.onCopySelected((String) message.getData().get(ADDRESS));
                            }
                            break;
                        default:
                            attachmentListener.onCopySelected(message.getBody());
                            break;
                    }
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.SAVE:
                if (message != null) {
                    switch (TapUI.getInstance(instanceKey).getLongPressMenuForMessageType(message.getType())) {
                        case TYPE_IMAGE_MESSAGE:
                            attachmentListener.onSaveImageToGallery(message);
                            break;
                        case TYPE_VIDEO_MESSAGE:
                            attachmentListener.onSaveVideoToGallery(message);
                            break;
                        default:
                            attachmentListener.onSaveToDownloads(message);
                            break;
                    }
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.STAR:
            case TAPDefaultConstant.LongPressMenuID.UNSTAR:
                if (message != null) {
                    attachmentListener.onMessageStarred(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.EDIT:
                if (message != null) {
                    attachmentListener.onEditMessage(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.PIN:
            case TAPDefaultConstant.LongPressMenuID.UNPIN:
                if (message != null) {
                    attachmentListener.onMessagePinned(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.INFO:
                if (message != null) {
                    attachmentListener.onViewMessageInfo(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.DELETE:
                if (message != null) {
                    attachmentListener.onDeleteMessage(vm.getRoom().getRoomID(), message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.REPORT:
                if (message != null) {
                    attachmentListener.onReportMessage(message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.OPEN_LINK:
                if (!data.isEmpty()) {
                    attachmentListener.onOpenLinkSelected(data);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.COMPOSE:
                if (!data.isEmpty()) {
                    attachmentListener.onComposeSelected(data);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.CALL:
                if (!data.isEmpty()) {
                    attachmentListener.onPhoneCallSelected(data);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.SMS:
                if (!data.isEmpty()) {
                    attachmentListener.onPhoneSmsSelected(data);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.VIEW_PROFILE:
                if (!data.isEmpty() && message != null) {
                    attachmentListener.onViewProfileSelected(data, message);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.SEND_MESSAGE:
                if (!data.isEmpty()) {
                    attachmentListener.onSendMessageSelected(data);
                }
                break;
            case TAPDefaultConstant.LongPressMenuID.RESCHEDULE:
                if (message != null) {
                    attachmentListener.onRescheduleMessage(message);
                }
                break;
        }
    }

    private final TapLongPressInterface longPressListener = (longPressMenuItem, messageModel) -> {
        handleLongPressMenuSelected(messageModel, longPressMenuItem);
    };

    private final TAPAttachmentListener attachmentListener = new TAPAttachmentListener(instanceKey) {
        @Override
        public void onCameraSelected() {
            if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            vm.setCameraImageUri(TAPUtils.takePicture(instanceKey, TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            TAPUtils.pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
        }

        @Override
        public void onLocationSelected() {
            TAPUtils.openLocationPicker(TapUIChatActivity.this, instanceKey);
        }

        @Override
        public void onDocumentSelected() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                TapTalk.setTapTalkSocketConnectionMode(instanceKey, TapTalk.TapTalkSocketConnectionMode.ALWAYS_ON);
                TAPUtils.openDocumentPicker(TapUIChatActivity.this, SEND_FILE);
//            } else {
//                TAPUtils.openDocumentPicker(TapUIChatActivity.this);
//            }
        }

        @Override
        public void onCopySelected(String text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }

        @Override
        public void onReplySelected(TAPMessageModel message) {
            chatListener.onReplyMessage(message);
        }

        @Override
        public void onForwardSelected(TAPMessageModel message) {
            if (vm.getMediaPlayer() != null && vm.getMediaPlayer().isPlaying()) {
                pauseVoiceNote();
            }
            vm.addSelectedMessage(message);
            showSelectState();
        }

        @Override
        public void onOpenLinkSelected(String url) {
            TAPUtils.openUrl(instanceKey, TapUIChatActivity.this, url);
        }

        @Override
        public void onComposeSelected(String emailRecipient) {
            TAPUtils.composeEmail(TapUIChatActivity.this, emailRecipient);
        }

        @Override
        public void onPhoneCallSelected(String phoneNumber) {
            TAPUtils.openDialNumber(TapUIChatActivity.this, phoneNumber);
        }

        @Override
        public void onPhoneSmsSelected(String phoneNumber) {
            if (!TAPUtils.composeSMS(TapUIChatActivity.this, phoneNumber)) {
                Toast.makeText(TapUIChatActivity.this, R.string.tap_error_unable_to_send_sms, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSaveImageToGallery(TAPMessageModel message) {
            if (!TAPUtils.hasPermissions(TapUIChatActivity.this, TAPUtils.getStoragePermissions(false))) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, TAPUtils.getStoragePermissions(false), PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE);
            } else if (null != message.getData()) {
                new Thread(() -> {
                    vm.setPendingDownloadMessage(null);
                    BitmapDrawable bitmapDrawable = TAPCacheManager.getInstance(TapUIChatActivity.this).getBitmapDrawable(message);
                    Bitmap bitmap = null;
                    if (bitmapDrawable != null) {
                        bitmap = bitmapDrawable.getBitmap();
                    }
                    else {
                        String fileUrl = (String) message.getData().get(FILE_URL);
                        if (null != fileUrl && !fileUrl.isEmpty()) {
                            // Get bitmap from url
                            try {
                                URL imageUrl = new URL((String) message.getData().get(FILE_URL));
                                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
//                    String fileID = (String) message.getData().get(FILE_ID);
//                    if (null != fileID && !fileID.isEmpty()) {
//                        // Get bitmap from cache
//                        bitmap = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable((String) message.getData().get(FILE_ID)).getBitmap();
//                    } else if (null != fileUrl && !fileUrl.isEmpty()) {
//                        // Get bitmap from url
//                        try {
//                            URL imageUrl = new URL((String) message.getData().get(FILE_URL));
//                            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    if (null != bitmap) {
                        TAPFileDownloadManager.getInstance(instanceKey).writeImageFileToDisk(
                                TapUIChatActivity.this,
                                System.currentTimeMillis(),
                                bitmap,
                                TAPUtils.getMimeTypeFromMessage(message),
                                new TapTalkActionInterface() {
                                    @Override
                                    public void onSuccess(String message) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                                    }
                                });
                    }
                }).start();
            }
        }

        @Override
        public void onSaveVideoToGallery(TAPMessageModel message) {
            if (!TAPUtils.hasPermissions(TapUIChatActivity.this, TAPUtils.getStoragePermissions(false))) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, TAPUtils.getStoragePermissions(false), PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
            } else if (null != message.getData()) {
                String fileID = (String) message.getData().get(FILE_ID);
                String fileUrl = (String) message.getData().get(FILE_URL);
                if (((null != fileID && !fileID.isEmpty()) || (null != fileUrl && !fileUrl.isEmpty()))) {
                    vm.setPendingDownloadMessage(null);
                    writeFileToDisk(message);
                }
            }
        }

        @Override
        public void onSaveToDownloads(TAPMessageModel message) {
            if (null != message.getData()) {
                String fileID = (String) message.getData().get(FILE_ID);
                String fileUrl = (String) message.getData().get(FILE_URL);
                if (((null != fileID && !fileID.isEmpty()) || (null != fileUrl && !fileUrl.isEmpty()))) {
                    writeFileToDisk(message);
                }
            }
        }

        private void writeFileToDisk(TAPMessageModel message) {
            TAPFileDownloadManager.getInstance(instanceKey).writeFileToDisk(TapUIChatActivity.this, message, new TapTalkActionInterface() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        }

        @Override
        public void onViewProfileSelected(String username, TAPMessageModel message) {
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, participant, true);
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, user, false);
                } else {
                    callApiGetUserByUsername(username, message);
                }
            }
        }

        @Override
        public void onSendMessageSelected(String username) {
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TapUI.getInstance(instanceKey).openChatRoomWithOtherUser(TapUIChatActivity.this, participant);
                closeActivity();
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TapUI.getInstance(instanceKey).openChatRoomWithOtherUser(TapUIChatActivity.this, user);
                    closeActivity();
                } else {
                    callApiGetUserByUsername(username, null);
                }
            }
        }

        @Override
        public void onMessageStarred(TAPMessageModel message) {
            super.onMessageStarred(message);
            String messageId = message.getMessageID();
            if (vm.getStarredMessageIds().contains(messageId)) {
                //unstar
                TapCoreMessageManager.getInstance(instanceKey).unstarMessage(message.getRoom().getRoomID(), messageId);
                vm.removeStarredMessageId(messageId);
            } else {
                //star
                TapCoreMessageManager.getInstance(instanceKey).starMessage(message.getRoom().getRoomID(), messageId);
                vm.addStarredMessageId(messageId);
            }
            TAPDataManager.getInstance(instanceKey).saveStarredMessageIds(vm.getRoom().getRoomID(), vm.getStarredMessageIds());
            messageAdapter.setStarredMessageIds(vm.getStarredMessageIds());
            if (vm.getMessagePointer().containsKey(message.getLocalID())) {
                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID())));
            }
        }

        @Override
        public void onEditMessage(TAPMessageModel message) {
            super.onEditMessage(message);
            if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                return;
            }
            showQuoteLayout(message, EDIT, true);
        }

        @Override
        public void onDeleteMessage(String roomID, TAPMessageModel message) {
            super.onDeleteMessage(roomID, message);
            isLastMessageNeedRefresh = true;
        }

        @Override
        public void onMessagePinned(TAPMessageModel message) {
            super.onMessagePinned(message);
            String messageId = message.getMessageID();
            if (vm.getPinnedMessageIds().contains(messageId)) {
                //unpin
                TapCoreMessageManager.getInstance(instanceKey).unpinMessage(message.getRoom().getRoomID(), messageId);
            } else {
                //pin
                TapCoreMessageManager.getInstance(instanceKey).pinMessage(message.getRoom().getRoomID(), messageId);
            }
        }

        @Override
        public void onReportMessage(TAPMessageModel message) {
            super.onReportMessage(message);
            TAPChatManager.getInstance(instanceKey).triggerReportMessageButtonTapped(TapUIChatActivity.this, message);
            TapReportActivity.Companion.start(TapUIChatActivity.this, instanceKey, message, TapReportActivity.ReportType.MESSAGE);
        }

        @Override
        public void onViewMessageInfo(TAPMessageModel message) {
            super.onViewMessageInfo(message);
            TapMessageInfoActivity.Companion.start(
                TapUIChatActivity.this,
                instanceKey,
                message,
                vm.getRoom(),
                vm.getStarredMessageIds().contains(message.getMessageID()),
                vm.getPinnedMessageIds().contains(message.getMessageID()),
                getBubbleViewFromAdapter(messageAdapter.getItems().indexOf(message))
            );
        }
    };

    private FrameLayout getBubbleViewFromAdapter(int position) {
        if (position < 0 || position >= messageAdapter.getItemCount()) {
            return null;
        }
        RecyclerView.ViewHolder vh = rvMessageList.findViewHolderForAdapterPosition(position);
        if (vh != null) {
            if (vh instanceof TAPMessageAdapter.TextVH) {
                return ((TAPMessageAdapter.TextVH) vh).flBubble;
            }
            else if (vh instanceof TAPMessageAdapter.ImageVH) {
                return ((TAPMessageAdapter.ImageVH) vh).flBubble;
            }
            else if (vh instanceof TAPMessageAdapter.VideoVH) {
                return ((TAPMessageAdapter.VideoVH) vh).flBubble;
            }
            else if (vh instanceof TAPMessageAdapter.FileVH) {
                return ((TAPMessageAdapter.FileVH) vh).flBubble;
            }
            else if (vh instanceof TAPMessageAdapter.VoiceVH) {
                return ((TAPMessageAdapter.VoiceVH) vh).flBubble;
            }
            else if (vh instanceof TAPMessageAdapter.LocationVH) {
                return ((TAPMessageAdapter.LocationVH) vh).flBubble;
            }
        }
        return null;
    }

    private void setChatRoomStatus(TAPOnlineStatusModel onlineStatus) {
        vm.setOnlineStatus(onlineStatus);
        if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && onlineStatus.getOnline()) {
            // User is online
            showUserOnline();
        } else if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && !onlineStatus.getOnline()) {
            // User is offline
            showUserOffline();
        }
    }

    private void showUserOnline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
//                clRoomStatus.setVisibility(View.VISIBLE);
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_active));
            tvRoomStatus.setText(getString(R.string.tap_active_now));
            vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
//                clRoomStatus.setVisibility(View.VISIBLE);
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            lastActivityRunnable.run();
        });
    }

    private Runnable lastActivityRunnable = new Runnable() {
        final int INTERVAL = 1000 * 60;

        @Override
        public void run() {
            Long lastActive = vm.getOnlineStatus().getLastActive();
            if (lastActive == 0) {
                runOnUiThread(() -> {
                    clRoomOnlineStatus.setVisibility(View.GONE);
                    vStatusBadge.setBackground(null);
                    tvRoomStatus.setText("");
                });
            } else {
                runOnUiThread(() -> {
                    //vStatusBadge.setBackground(getDrawable(R.drawable.tap_bg_circle_butterscotch));
                    clRoomOnlineStatus.setVisibility(View.VISIBLE);
                    vStatusBadge.setVisibility(View.GONE);
                    tvRoomStatus.setText(TAPTimeFormatter.getLastActivityString(TapUIChatActivity.this, lastActive));
                });
            }
            vm.getLastActivityHandler().postDelayed(this, INTERVAL);
        }
    };

    private void sendTypingEmit(boolean isTyping) {
        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() != CONNECTED) {
            return;
        }
        String currentRoomID = vm.getRoom().getRoomID();
        if (isTyping && !vm.isActiveUserTyping()) {
            TAPChatManager.getInstance(instanceKey).sendStartTypingEmit(currentRoomID);
            vm.setActiveUserTyping(true);
            sendTypingEmitDelayTimer.cancel();
            sendTypingEmitDelayTimer.start();
        } else if (!isTyping && vm.isActiveUserTyping()) {
            TAPChatManager.getInstance(instanceKey).sendStopTypingEmit(currentRoomID);
            vm.setActiveUserTyping(false);
            sendTypingEmitDelayTimer.cancel();
        }
    }

    private void showTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        typingIndicatorTimeoutTimer.start();
        runOnUiThread(() -> {
//            clRoomStatus.setVisibility(View.VISIBLE);
            clRoomTypingStatus.setVisibility(View.VISIBLE);
            clRoomOnlineStatus.setVisibility(View.GONE);

            if (TYPE_PERSONAL == vm.getRoom().getType()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(getString(R.string.tap_typing));
            } else if (1 < vm.getGroupTypingSize()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_d_people_typing), vm.getGroupTypingSize()));
            } else {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                //tvRoomTypingStatus.setText(getString(R.string.tap_typing));
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_s_typing_single), vm.getFirstTypingUserName()));
            }
        });
    }

    private void hideTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        runOnUiThread(() -> {
            vm.getGroupTyping().clear();
//            clRoomStatus.setVisibility(View.VISIBLE);
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
        });
    }

    private CountDownTimer sendTypingEmitDelayTimer = new CountDownTimer(TYPING_EMIT_DELAY, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            vm.setActiveUserTyping(false);
        }
    };

    private CountDownTimer typingIndicatorTimeoutTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            hideTypingIndicator();
        }
    };

    private void saveDraftToManager() {
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) {
            TAPChatManager.getInstance(instanceKey).saveMessageToDraft(vm.getRoom().getRoomID(), draft);
        } else {
            TAPChatManager.getInstance(instanceKey).removeDraft(vm.getRoom().getRoomID());
        }
    }

    private void openMediaPreviewPage(ArrayList<TAPMediaPreviewModel> mediaPreviews) {
        TAPMediaPreviewActivity.start(TapUIChatActivity.this, instanceKey, mediaPreviews, new ArrayList<>(vm.getRoomParticipantsByUsername().values()));
    }

    private void sendMediasFromPreview(List<TAPMediaPreviewModel> medias) {
        TapCoreSendMessageListener listener = new TapCoreSendMessageListener() {
            @Override
            public void onTemporaryMessageCreated(TAPMessageModel message) {
                updateMessageAdapter(message);
            }

            @Override
            public void onStart(TAPMessageModel message) {
                updateMessageAdapter(message);
                new Handler(Looper.getMainLooper()).postDelayed(this::checkAndSendRemainingMedias, 50L);
            }

            private void updateMessageAdapter(TAPMessageModel message) {
                updateMessage(message);
                hideQuoteLayout();
                hideUnreadButton();
            }

            private void checkAndSendRemainingMedias() {
                if (!vm.getPendingSendMedias().isEmpty()) {
                    sendMediasFromPreview(vm.getPendingSendMedias());
                }
            }
        };
        for (TAPMediaPreviewModel media : medias) {
            if (media.getType() == TYPE_IMAGE) {
                if (media.getUrl() != null && !media.getUrl().isEmpty()) {
                    TapCoreMessageManager.getInstance(instanceKey).sendImageMessage(
                        media.getUrl(),
                        media.getCaption(),
                        vm.getRoom(),
                        vm.getQuotedMessage(),
                        true,
                        listener
                    );
                    int index = medias.indexOf(media);
                    ArrayList<TAPMediaPreviewModel> pendingMedias = new ArrayList<>(medias.subList(index + 1, medias.size()));
                    vm.setPendingSendMedias(pendingMedias);
                    break;
                }
                else {
                    TAPChatManager.getInstance(instanceKey).createImageMessageModelAndAddToUploadQueue(
                        this,
                        vm.getRoom(),
                        media.getUri(),
                        media.getCaption()
                    );
                }
            }
            else if (media.getType() == TYPE_VIDEO) {
                if (media.getUrl() != null && !media.getUrl().isEmpty()) {
                    TapCoreMessageManager.getInstance(instanceKey).sendVideoMessage(
                        this,
                        media.getUrl(),
                        media.getCaption(),
                        vm.getRoom(),
                        vm.getQuotedMessage(),
                        true,
                        listener
                    );
                    int index = medias.indexOf(media);
                    ArrayList<TAPMediaPreviewModel> pendingMedias = new ArrayList<>(medias.subList(index + 1, medias.size()));
                    vm.setPendingSendMedias(pendingMedias);
                    break;
                }
                else {
                    TAPChatManager.getInstance(instanceKey).createVideoMessageModelAndAddToUploadQueue(
                        this,
                        vm.getRoom(),
                        media.getUri(),
                        media.getCaption()
                    );
                }
            }
        }
    }

    // Previously callApiGetGroupData
    private void getRoomDataFromApi() {
        TAPDataManager.getInstance(instanceKey).getChatRoomData(vm.getRoom().getRoomID(), new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                vm.setRoom(response.getRoom());
                vm.getRoom().setAdmins(response.getAdmins());
                vm.getRoom().setParticipants(response.getParticipants());
                TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());

                setNavigationBarProfilePicture();

                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)){
                    tvRoomName.setText(R.string.tap_saved_messages);
                } else {
                    tvRoomName.setText(vm.getRoom().getName());
                }

                if (vm.getRoom().getType() == TYPE_GROUP && null != vm.getRoom().getParticipants()) {
                    // Show number of participants for group room
                    tvRoomStatus.setText(String.format(getString(R.string.tap_format_d_group_member_count), vm.getRoom().getParticipants().size()));
                    clRoomOnlineStatus.setVisibility(View.VISIBLE);
                }
                else {
                    clRoomOnlineStatus.setVisibility(View.GONE);
                }
                if (null != vm.getRoom().getParticipants()) {
                    new Thread(() -> {
                        vm.getRoomParticipantsByUsername().clear();
                        for (TAPUserModel user : vm.getRoom().getParticipants()) {
                            vm.addRoomParticipantByUsername(user);
                        }
                    }).start();
                }
                TAPChatManager.getInstance(instanceKey).triggerUpdatedChatRoomDataReceived(vm.getRoom(), vm.getRoom().getType() == TYPE_PERSONAL ? vm.getOtherUserModel() : null);
            }
        });
    }

    private void callApiGetUserByUserID() {
        if (TAPChatManager.getInstance(instanceKey).isNeedToCalledUpdateRoomStatusAPI() &&
                TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(vm.getOtherUserID(), new TAPDefaultDataView<TAPGetUserResponse>() {
                @Override
                public void onSuccess(TAPGetUserResponse response) {
                    TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
                    TAPUserModel updatedContact = TAPContactManager.getInstance(instanceKey).getUserData(response.getUser().getUserID());
                    TAPOnlineStatusModel onlineStatus = TAPOnlineStatusModel.Builder(updatedContact);
                    setChatRoomStatus(onlineStatus);
                    TAPChatManager.getInstance(instanceKey).setNeedToCallUpdateRoomStatusAPI(false);

                    vm.getRoom().setName(updatedContact.getFullname());
                    vm.getRoom().setImageURL(updatedContact.getImageURL());
                    setOtherUserModel(updatedContact);
                    if (null == vm.getOtherUserModel()) {
                        initRoom();
                    } else {
                        setOtherUserModel(updatedContact);
                        if (TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(vm.getOtherUserID())) {
                            showUnblockButton();
                        }
                    }

                    if (!TapUI.getInstance(instanceKey).isAddContactDisabled() &&
                        TapUI.getInstance(instanceKey).isAddToContactsButtonInChatRoomVisible() &&
                        !TAPDataManager.getInstance(instanceKey).isChatRoomContactActionDismissed(vm.getRoom().getRoomID()) &&
                        !TAPDataManager.getInstance(instanceKey).getBlockedUserIds().contains(vm.getOtherUserID()) &&
                        (null == updatedContact.getIsContact() || updatedContact.getIsContact() == 0)
                    ) {
                        clContactAction.setVisibility(View.VISIBLE);
                    } else {
                        clContactAction.setVisibility(View.GONE);
                    }

                    TAPChatManager.getInstance(instanceKey).triggerUpdatedChatRoomDataReceived(vm.getRoom(), vm.getOtherUserModel());
                }

                @Override
                public void onError(TAPErrorModel error) {
                    if (null != error.getCode() && error.getCode().equals(USER_NOT_FOUND)) {
                        showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
                    }
                }
            });
        } else if (null == vm.getOtherUserModel()) {
            showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
        }
    }

    // For mentioned user data
    private void callApiGetUserByUsername(String username, @Nullable TAPMessageModel message) {
        TAPDataManager.getInstance(instanceKey).getUserByUsernameFromApi(username, true, new TAPDefaultDataView<TAPGetUserResponse>() {
            private boolean isCanceled = false;

            @Override
            public void startLoading() {
                showLoadingPopup();
                tvLoadingText.setOnClickListener(v -> {
                    hideLoadingPopup();
                    isCanceled = true;
                });
            }

            @Override
            public void onSuccess(TAPGetUserResponse response) {
                TAPContactManager.getInstance(instanceKey).updateUserData(response.getUser());
                TAPUserModel updatedContact = TAPContactManager.getInstance(instanceKey).getUserData(response.getUser().getUserID());
                if (!isCanceled) {
                    hideLoadingPopup();
                    if (null == message) {
                        // Open chat room if message is null (from send message menu)
                        TapUI.getInstance(instanceKey).openChatRoomWithOtherUser(TapUIChatActivity.this, updatedContact);
                        closeActivity();
                    } else {
                        // Open profile if message is not null (from mention tap/view profile menu)
                        TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, updatedContact, false);
                    }
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (!isCanceled) {
                    hideLoadingPopup();
                    showErrorDialog(getString(R.string.tap_error), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isCanceled) {
                    hideLoadingPopup();
                    showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general));
                }
            }
        });
    }

    private void showChatAsHistory(String message) {
        runOnUiThread(() -> {
            if (null != clChatHistory) {
                clChatHistory.setVisibility(View.VISIBLE);
            }
            if (null != tvChatHistoryContent) {
                tvChatHistoryContent.setText(message);
            }
            if (null != btnUnblock) {
                btnUnblock.setVisibility(View.GONE);
            }
            if (null != clChatComposer) {
                TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                rvCustomKeyboard.setVisibility(View.GONE);
                clChatComposer.setVisibility(View.INVISIBLE);
                etChat.clearFocus();
            }
            if (null != llButtonDeleteChat) {
                llButtonDeleteChat.setOnClickListener(llDeleteGroupClickListener);
            }
            if (null != tvDeleteChat) {
                if (TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(vm.getRoom().getRoomID())) {
                    tvDeleteChat.setText(R.string.tap_unpin_and_delete_chat);
                }
            }
        });
    }

    private void showUnblockButton() {
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled()) {
            runOnUiThread(() -> {
                if (null != clChatHistory) {
                    clChatHistory.setVisibility(View.GONE);
                }
                if (null != clChatComposer) {
                    TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    rvCustomKeyboard.setVisibility(View.GONE);
                    clChatComposer.setVisibility(View.INVISIBLE);
                    etChat.clearFocus();
                }
                if (null != btnUnblock) {
                    btnUnblock.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showDefaultChatEditText() {
        runOnUiThread(() -> {
            if (null != clChatHistory) {
                clChatHistory.setVisibility(View.GONE);
            }
            if (null != btnUnblock) {
                btnUnblock.setVisibility(View.GONE);
            }
            if (null != clChatComposer) {
                clChatComposer.setVisibility(View.VISIBLE);
            }
            if (null != civRoomImage) {
                vRoomImage.setClickable(true);
            }
            hideKeyboards();
        });
    }

    private void checkChatRoomLocked(TAPMessageModel message) {
        if (null == message || null == message.getRoom()) {
            return;
        }
        if (message.getRoom().isLocked()) {
            lockChatRoom();
        } else {
            runOnUiThread(() -> clChatComposer.setVisibility(View.VISIBLE));
        }
    }

    private void lockChatRoom() {
        runOnUiThread(() -> {
            etChat.setText("");
            hideQuoteLayout();
            hideKeyboards();
            clChatComposer.setVisibility(View.GONE);
        });
    }

    private void handleSystemMessageAction(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (null != vm.getRoom() && TYPE_PERSONAL != vm.getRoom().getType() &&
                (ROOM_ADD_PARTICIPANT.equals(message.getAction()) ||
                        ROOM_REMOVE_PARTICIPANT.equals(message.getAction())) &&
                !TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getTarget().getTargetID())) {
            // Another member removed from group
            getRoomDataFromApi();
        } else if ((ROOM_REMOVE_PARTICIPANT.equals(message.getAction()) &&
                null != message.getTarget() &&
                vm.getMyUserModel().getUserID().equals(message.getTarget().getTargetID())) ||
                (LEAVE_ROOM.equals(message.getAction()) &&
                        vm.getMyUserModel().getUserID().equals(message.getUser().getUserID()))) {
            // Active user removed from group
            showChatAsHistory(getString(R.string.tap_not_a_participant));
        } else if (ROOM_ADD_PARTICIPANT.equals(message.getAction())) { // TODO: 27 Jan 2020 CHECK TARGET ID?
            // New group participant added
            showDefaultChatEditText();
        } else if (DELETE_ROOM.equals(message.getAction())) {
            // Room deleted
            //TAPChatManager.getInstance(instanceKey).deleteMessageFromIncomingMessages(message.getLocalID());
            //showRoomIsUnavailableState();
            showChatAsHistory(getString(R.string.tap_group_unavailable));
        } else if (PIN_MESSAGE.equals(message.getAction())) {
            if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled() && message.getData() != null) {
                String messageId = (String) message.getData().get("messageID");
                String localId = (String) message.getData().get("localID");
                HashMap<String, Object> data = new LinkedHashMap<>();
                String encryptedData = (String) message.getData().get("data");
                if (encryptedData != null && !encryptedData.isEmpty()) {
                    data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(encryptedData, localId));
                }
                String body = "";
                String encryptedBody = (String) message.getData().get("body");
                if (encryptedBody != null) {
                    body = TAPEncryptorManager.getInstance().decrypt(encryptedBody, localId);
                }
                BigInteger createdTimeInteger = (BigInteger) message.getData().get("createdTime");
                Long createdTime = 0L;
                if (createdTimeInteger != null) {
                    createdTime = createdTimeInteger.longValue();
                }
                if (createdTime > 0L) {
                    TAPMessageModel pinnedMessage = new TAPMessageModel();
                    pinnedMessage.setMessageID(messageId);
                    if (localId != null) {
                        pinnedMessage.setLocalID(localId);
                    }
                    if (data != null && !data.isEmpty()) {
                        pinnedMessage.setData(data);
                    }
                    pinnedMessage.setBody(body);
                    pinnedMessage.setCreated(createdTime);

                    if (vm.getPinnedMessages().isEmpty()) {
                        vm.addPinnedMessageId(messageId);
                        messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                        vm.addPinnedMessage(pinnedMessage);
                        vm.setPinnedMessageIndex(vm.getPinnedMessages().size()-1);
                        runOnUiThread(() -> {
                            clPinnedIndicator.setSize(vm.getPinnedMessageIds().size());
                            clPinnedIndicator.select(vm.getPinnedMessageIndex());
                            messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localId)));
                            });
                        setPinnedMessage(pinnedMessage);
                    } else {
                        if (vm.getPinnedMessages().get(vm.getPinnedMessages().size() - 1).getCreated() < createdTime) {
                            for (int index = 0; index < vm.getPinnedMessages().size(); index++) {
                                if (createdTime > vm.getPinnedMessages().get(index).getCreated()) {
                                    if (!vm.getPinnedMessageIds().contains(messageId)) {
                                        vm.addPinnedMessageId(index, messageId);
                                        messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                                        runOnUiThread(() -> clPinnedIndicator.setSize(vm.getPinnedMessageIds().size()));
                                    }
                                    if (!vm.getPinnedMessages().contains(pinnedMessage)) {
                                        vm.addPinnedMessage(index, pinnedMessage);
                                    }
                                    if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID()) || vm.getPinnedMessageIds().get(0).equals(messageId)) {
                                            vm.setPinnedMessageIndex(index);
                                            runOnUiThread(() -> clPinnedIndicator.select(vm.getPinnedMessageIndex()));
                                            setPinnedMessage(pinnedMessage);
                                    }
                                    break;
                                }
                            }
                        } else if (vm.getPinnedMessages().size() == vm.getPinnedMessageIds().size()) {
                            if (!vm.getPinnedMessageIds().contains(messageId)) {
                                vm.addPinnedMessageId(messageId);
                                messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                                runOnUiThread(() -> clPinnedIndicator.setSize(vm.getPinnedMessageIds().size()));
                            }
                            if (!vm.getPinnedMessages().contains(pinnedMessage)) {
                                vm.addPinnedMessage(pinnedMessage);
                            }
                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                vm.setPinnedMessageIndex(vm.getPinnedMessages().size() - 1);
                                runOnUiThread(() -> clPinnedIndicator.select(vm.getPinnedMessageIndex()));
                                setPinnedMessage(pinnedMessage);
                            }
                        } else {
                            getPinnedMessages(messageId);
                        }
                    }
                }
                if (vm.getMessagePointer().containsKey(localId)) {
                    runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localId))));
                }
            }
        } else if (UNPIN_MESSAGE.equals(message.getAction())) {
            if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled() && message.getData() != null) {
                String messageId = (String) message.getData().get("messageID");
                String localId = (String) message.getData().get("localID");
                vm.removePinnedMessageId(messageId);
                messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                runOnUiThread(() -> clPinnedIndicator.setSize(vm.getPinnedMessageIds().size()));
                for (int index = 0; index < vm.getPinnedMessages().size(); index++) {
                    String id = vm.getPinnedMessages().get(index).getMessageID();
                    if (id != null && id.equals(messageId)) {
                        vm.removePinnedMessage(vm.getPinnedMessages().get(index));
                        if (vm.getPinnedMessageIndex() == index) {
                            if (vm.getPinnedMessages().isEmpty()) {
                                setPinnedMessage(null);
                            } else {
                                setPinnedMessage(vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                            }
                        }
                        break;
                    }
                }
                if (vm.getPinnedMessageIds().isEmpty()) {
                    setPinnedMessage(null);
                    TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), null);
                } else {
                    if (vm.getPinnedMessageIndex() >= vm.getPinnedMessageIds().size()) {
                        vm.setPinnedMessageIndex(0);
                        TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), vm.getPinnedMessages().get(vm.getPinnedMessageIndex()));
                        runOnUiThread(() -> clPinnedIndicator.select(vm.getPinnedMessageIndex()));
                    } else {
                        if (vm.getPinnedMessageIndex() >= vm.getPinnedMessages().size()) {
                            getPinnedMessages(messageId);
                        }
                    }
                }
                if (vm.getMessagePointer().containsKey(localId)) {
                    runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localId))));
                }
            }
        } else {
            updateRoomDetailFromSystemMessage(message);
        }
    }

    private void updateRoomDetailFromSystemMessage(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (UPDATE_ROOM.equals(message.getAction())) {
            // Update room details
            vm.getRoom().setName(message.getRoom().getName());
            vm.getRoom().setImageURL(message.getRoom().getImageURL());
            TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());
            runOnUiThread(() -> {
                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)){
                    tvRoomName.setText(R.string.tap_saved_messages);
                } else {
                    tvRoomName.setText(vm.getRoom().getName());
                }
                if (null != vm.getRoom().getImageURL()) {
                    civRoomImage.post(this::setNavigationBarProfilePicture);
                }
            });
            TAPChatManager.getInstance(instanceKey).triggerUpdatedChatRoomDataReceived(vm.getRoom(), vm.getRoom().getType() == TYPE_PERSONAL ? vm.getOtherUserModel() : null);
        } else if (vm.getRoom().getType() == TYPE_PERSONAL &&
                UPDATE_USER.equals(message.getAction()) &&
                message.getUser().getUserID().equals(vm.getOtherUserID())) {
            // Update user details
            vm.getRoom().setName(message.getUser().getFullname());
            vm.getRoom().setImageURL(message.getUser().getImageURL());
            setOtherUserModel(message.getUser());
            runOnUiThread(() -> {
                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)){
                    tvRoomName.setText(R.string.tap_saved_messages);
                } else {
                    tvRoomName.setText(vm.getOtherUserModel().getFullname());
                }
                if (null != vm.getRoom().getImageURL()) {
                    civRoomImage.post(this::setNavigationBarProfilePicture);
                }
                setChatRoomStatus(TAPOnlineStatusModel.Builder(message.getUser()));
            });
            TAPChatManager.getInstance(instanceKey).triggerUpdatedChatRoomDataReceived(vm.getRoom(), vm.getRoom().getType() == TYPE_PERSONAL ? vm.getOtherUserModel() : null);
        }
    }

    private void buildAndSendTextMessage() {
        String message = etChat.getText().toString().trim();
        if (vm.getRecordingState() == FINISH || vm.getRecordingState() == PLAY || vm.getRecordingState() == PAUSE) {
            //send voice note
            TAPChatManager.getInstance(instanceKey).sendVoiceNoteMessage(this, vm.getRoom(), audioManager.getRecording());
            vm.setPausedPosition(0);
            stopProgressTimer();
            if (vm.getMediaPlayer() != null) {
                if (vm.getMediaPlayer().isPlaying()) {
                    vm.getMediaPlayer().stop();
                }
                vm.setMediaPlayer(null);
            }
            seekBar.setProgress(0);
            setDefaultRecordingState();
        } else if (vm.getRecordingState() == DEFAULT) {
            if (vm.getQuotedMessage() != null && vm.getQuoteAction() == EDIT) {
                // edit message
                TAPMessageModel messageModel = vm.getQuotedMessage();
                if ((messageModel.getType() == TYPE_TEXT || messageModel.getType() == TYPE_LINK) && !TextUtils.isEmpty(message)) {
                    messageModel.setBody(message);
                    HashMap<String, Object> data = messageModel.getData();
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    data.put(TITLE, vm.getLinkHashMap().get(TITLE));
                    data.put(DESCRIPTION, vm.getLinkHashMap().get(DESCRIPTION));
                    data.put(IMAGE, vm.getLinkHashMap().get(IMAGE));
                    data.put(TYPE, vm.getLinkHashMap().get(TYPE));
                    data.put(TAPDefaultConstant.MessageData.URL, vm.getLinkHashMap().get(TAPDefaultConstant.MessageData.URL));
                    messageModel.setData(data);
                } else if (messageModel.getType() == TYPE_IMAGE || messageModel.getType() == TYPE_VIDEO) {
                    HashMap<String, Object> data = messageModel.getData();
                    if (data != null) {
                        data.put(CAPTION, message);
                        messageModel.setData(data);
                    }
                } else {
                    return;
                }
                TAPChatManager.getInstance(instanceKey).editMessage(messageModel, message, new TapCoreSendMessageListener() {
                    @Override
                    public void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage) {
                        if (errorCode.equals(ERROR_CODE_CAPTION_EXCEEDS_LIMIT)) {
                            new TapTalkDialog.Builder(TapUIChatActivity.this)
                                    .setTitle(getString(R.string.tap_error_unable_to_edit_message))
                                    .setMessage(errorMessage)
                                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                    .show();
                        }
                    }
                }, true);
                hideQuoteLayout();
            } else if (!TextUtils.isEmpty(message)) {
                String firstUrl = vm.getLinkHashMap().get(TAPDefaultConstant.MessageData.URL);
                if (firstUrl != null && !firstUrl.isEmpty()) {
                    // send message as link
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(TITLE, vm.getLinkHashMap().get(TITLE));
                    data.put(DESCRIPTION, vm.getLinkHashMap().get(DESCRIPTION));
                    data.put(IMAGE, vm.getLinkHashMap().get(IMAGE));
                    data.put(TYPE, vm.getLinkHashMap().get(TYPE));
                    data.put(TAPDefaultConstant.MessageData.URL, firstUrl);
                    TAPChatManager.getInstance(instanceKey).sendLinkMessage(message, data);
                } else {
                    // send message as text
                    TAPChatManager.getInstance(instanceKey).sendTextMessage(message);
                }
                rvMessageList.post(this::scrollToBottom);
            } else {
                TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(vm.getRoom());
                ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
                ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
            }
            etChat.setText("");
        } else {
            TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(vm.getRoom());
            ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
        }
    }

    private void updateMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (null == newMessage.getIsHidden() || !newMessage.getIsHidden()) {
                    if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) && cvEmptySavedMessages.getVisibility() == View.VISIBLE) {
                        cvEmptySavedMessages.setVisibility(View.GONE);
                    } else if (clEmptyChat.getVisibility() == View.VISIBLE) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                    showMessageList();
                }
            });
            // Replace pending message with new message
            String newID = newMessage.getLocalID();
            updateMessageMentionIndexes(newMessage);
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
            runOnUiThread(() -> {
                if (vm.getMessagePointer().containsKey(newID)) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    int index = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID));
                    vm.updateMessagePointer(newMessage);
                    messageAdapter.notifyItemChanged(index);
                    if (TYPE_IMAGE == newMessage.getType() && ownMessage) {
                        TAPFileUploadManager.getInstance(instanceKey).removeUploadProgressMap(newMessage.getLocalID());
                    }
                } else {
                    // Check previous message date and add new message
                    TAPMessageModel previousMessage = messageAdapter.getItemAt(0);
                    String currentDate = TAPTimeFormatter.formatDate(newMessage.getCreated());
                    if ((null == newMessage.getIsHidden() || !newMessage.getIsHidden()) &&
                            newMessage.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                            newMessage.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                            newMessage.getType() != TYPE_DATE_SEPARATOR &&
                            (null == previousMessage || !currentDate.equals(TAPTimeFormatter
                                    .formatDate(previousMessage.getCreated())))
                    ) {
                        // Generate date separator if first message or date is different
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, newMessage);
                        vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
                        vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), 0);
                        runOnUiThread(() -> messageAdapter.addMessage(dateSeparator));
                    }

                    // Add new message
                    runOnUiThread(() -> messageAdapter.addMessage(newMessage));
                    vm.addMessagePointer(newMessage);
                    if (vm.isOnBottom()/* && !ownMessage*/) {
                        // Scroll recycler to bottom if recycler is already on bottom
                        vm.setScrollFromKeyboard(true);
                        scrollToBottom();
                    }
//                    else if (ownMessage && !(PIN_MESSAGE.equals(newMessage.getAction()) || UNPIN_MESSAGE.equals(newMessage.getAction()))) {
//                        // Scroll recycler to bottom if own message
//                        scrollToBottom();
//                    }
                    else if (!ownMessage && (newMessage.getAction() == null || !UNPIN_MESSAGE.equals(newMessage.getAction()))) {
                        // Message from other people is received when recycler is scrolled up
                        vm.addUnreadMessage(newMessage);
                        updateUnreadCount();
                        updateMentionCount();
                    }
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();

            if (null != vm.getPendingAfterResponse() &&
                    !TAPChatManager.getInstance(instanceKey).hasPendingMessages()) {
                messageAfterView.onSuccess(vm.getPendingAfterResponse());
            }
        }
    }

    private void addNewMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (null == newMessage.getIsHidden() || !newMessage.getIsHidden()) {
                    if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) && cvEmptySavedMessages.getVisibility() == View.VISIBLE) {
                        cvEmptySavedMessages.setVisibility(View.GONE);
                    } else if (clEmptyChat.getVisibility() == View.VISIBLE) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                    showMessageList();
                }
            });
            updateMessageMentionIndexes(newMessage);
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());

            TAPMessageModel previousMessage = messageAdapter.getItemAt(0);
            String currentDate = TAPTimeFormatter.formatDate(newMessage.getCreated());
            if ((null == newMessage.getIsHidden() || !newMessage.getIsHidden()) &&
                    newMessage.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                    newMessage.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                    newMessage.getType() != TYPE_DATE_SEPARATOR &&
                    (null == previousMessage || !currentDate.equals(TAPTimeFormatter
                            .formatDate(previousMessage.getCreated())))
            ) {
                // Generate date separator if first message or date is different
                TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, newMessage);
                vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
                vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), 0);
                runOnUiThread(() -> messageAdapter.addMessage(dateSeparator));
            }

            runOnUiThread(() -> messageAdapter.addMessage(newMessage));
            vm.addMessagePointer(newMessage);

            runOnUiThread(() -> {
                if (vm.isOnBottom() || ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    ivToBottom.setVisibility(View.GONE);
                    rvMessageList.scrollToPosition(0);
                } else {
                    // Message from other people is received when recycler is scrolled up
                    vm.addUnreadMessage(newMessage);
                    updateUnreadCount();
                    updateMentionCount();
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();
        }
    }

    private void updateMessageFromSocket(TAPMessageModel message) {
        if (null == vm.getRoom() || !message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
            return;
        }
        rvMessageList.post(() -> runOnUiThread(() -> {
            int position = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
            if (-1 != position) {
                // Update message in pointer and adapter
                TAPMessageModel existingMessage = messageAdapter.getItemAt(position).copyMessageModel();
                vm.updateMessagePointer(message);
                if (null != existingMessage) {
                    if (message.getIsHidden() != null && message.getIsHidden() &&
                        (existingMessage.getIsHidden() == null || !existingMessage.getIsHidden()) &&
                        messageAdapter.getItemCount() > (position + 1)
                    ) {
                        // Message was updated to hidden, check if need to remove date separator
                        TAPMessageModel messageAbove = null;
                        for (int aboveIndex = position + 1; aboveIndex < messageAdapter.getItemCount(); aboveIndex++) {
                            // Get first visible message above updated message
                            TAPMessageModel loopedMessage = messageAdapter.getItemAt(aboveIndex);
                            if (loopedMessage.getIsHidden() == null || !loopedMessage.getIsHidden()) {
                                messageAbove = loopedMessage;
                                break;
                            }
                        }
                        if (messageAbove != null && messageAbove.getType() == TYPE_DATE_SEPARATOR) {
                            if (position == 0) {
                                // Updated message is at first index, remove date separator above
                                removeDateSeparator(messageAbove);
                                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
                                    TAPDataManager.getInstance(instanceKey).getRoomLastMessage(vm.getRoom().getRoomID(), new TAPDatabaseListener<>() {
                                        @Override
                                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                                            super.onSelectFinished(entities);
                                            if (entities.size() == 0) {
                                                runOnUiThread(() -> {
                                                    cvEmptySavedMessages.setVisibility(View.VISIBLE);
                                                    flMessageList.setVisibility(View.GONE);
                                                });
                                            }
                                        }
                                    });
                                }
                            } else {
                                TAPMessageModel messageBelow = null;
                                for (int belowIndex = position - 1; belowIndex >= 0; belowIndex--) {
                                    // Get first visible message below updated message
                                    TAPMessageModel loopedMessage = messageAdapter.getItemAt(belowIndex);
                                    if (loopedMessage.getIsHidden() == null || !loopedMessage.getIsHidden()) {
                                        messageBelow = loopedMessage;
                                        break;
                                    }
                                }
                                if (messageBelow == null ||
                                    !TAPTimeFormatter.dateStampString(this, message.getCreated()).equals(
                                    TAPTimeFormatter.dateStampString(this, messageAdapter.getItemAt(position - 1).getCreated()))
                                ) {
                                    // Message below updated message has different date, remove date separator
                                    removeDateSeparator(messageAbove);
                                    if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
                                        TAPDataManager.getInstance(instanceKey).getRoomLastMessage(vm.getRoom().getRoomID(), new TAPDatabaseListener<>() {
                                            @Override
                                            public void onSelectFinished(List<TAPMessageEntity> entities) {
                                                super.onSelectFinished(entities);
                                                if (entities.size() == 0) {
                                                    runOnUiThread(() -> {
                                                        cvEmptySavedMessages.setVisibility(View.VISIBLE);
                                                        flMessageList.setVisibility(View.GONE);
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                    // Update message and notify
                    existingMessage.updateValue(message);
                    messageAdapter.notifyItemChanged(position);
                }
            }
            if (0 == position) {
                updateFirstVisibleMessageIndex();
            }
        }));
    }

    private void removeDateSeparator(TAPMessageModel message) {
        vm.getDateSeparators().remove(message.getLocalID());
        vm.getDateSeparatorIndexes().remove(message.getLocalID());
        messageAdapter.removeMessage(message);
    }

    private List<TAPMessageModel> addBeforeTextMessage(final TAPMessageModel newMessage) {
        List<TAPMessageModel> tempBeforeMessages = new ArrayList<>();
        String newID = newMessage.getLocalID();

        if (vm.getMessagePointer().containsKey(newID)) {
            // Update existing message
            vm.updateMessagePointer(newMessage);
            runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID))));
        } else {
            // Add new message to pointer
            tempBeforeMessages.add(newMessage);
            vm.addMessagePointer(newMessage);
        }
        //updateMessageDecoration();
        return tempBeforeMessages;
    }

    private void updateFirstVisibleMessageIndex() {
        new Thread(() -> {
            vm.setFirstVisibleItemIndex(0);
            TAPMessageModel message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            while (null != message && null != message.getIsHidden() && message.getIsHidden()) {
                vm.setFirstVisibleItemIndex(vm.getFirstVisibleItemIndex() + 1);
                message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            }
        }).start();
    }

    private void scrollToMessage(String localID) {
        TAPMessageModel message = vm.getMessagePointer().get(localID);
        if (null != message) {
            vm.setTappedMessageLocalID(null);
            if ((null != message.getIsDeleted() && message.getIsDeleted()) ||
                    (null != message.getIsHidden() && message.getIsHidden())) {
                // Message does not exist
                runOnUiThread(() -> {
                    Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                    hideUnreadButtonLoading();
                });
            } else {
                // Scroll to message
                runOnUiThread(() -> {
                    messageLayoutManager.scrollToPositionWithOffset(messageAdapter.getItems().indexOf(message), TAPUtils.dpToPx(128));
                    rvMessageList.post(() -> {
                        if (messageLayoutManager.findFirstVisibleItemPosition() > 0) {
                            vm.setOnBottom(false);
                            ivToBottom.setVisibility(View.VISIBLE);
                        }
                        hideUnreadButton();
                        hideUnreadButtonLoading();
                        messageAdapter.highlightMessage(message);
                    });
                });
            }
        } else if (state != STATE.DONE) {
            // Find message in database/API
            vm.setTappedMessageLocalID(localID);
            showUnreadButtonLoading();
            loadMessagesFromDatabase();
        } else {
            // Message not found
            runOnUiThread(() -> {
                Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                hideUnreadButtonLoading();
            });
        }
    }

    private void goToMessage(TAPMessageModel message) {
        if (vm.getRoom().getRoomID().equals(message.getRoom().getRoomID())) {
            scrollToMessage(message.getLocalID());
        } else {
            Intent roomIntent = new Intent(OPEN_CHAT);
            roomIntent.putExtra(MESSAGE, message);
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(roomIntent);
            closeActivity();
        }
    }

    private TAPMessageModel insertUnreadMessageIdentifier(long created, TAPUserModel user) {
        TAPMessageModel unreadIndicator = new TAPMessageModel();
        unreadIndicator.setType(TYPE_UNREAD_MESSAGE_IDENTIFIER);
        unreadIndicator.setLocalID(UNREAD_INDICATOR_LOCAL_ID);
        unreadIndicator.setCreated(created - 1);
        unreadIndicator.setUser(user);

        vm.addMessagePointer(unreadIndicator);
        vm.setUnreadIndicator(unreadIndicator);

        return unreadIndicator;
    }

    private void getInitialUnreadCount() {
        // Get room's unread count
        vm.setInitialUnreadCount(vm.getRoom().getUnreadCount());
        if (0 == vm.getInitialUnreadCount()) {
            // Query unread count from database
            TAPDataManager.getInstance(instanceKey).getUnreadCountPerRoom(vm.getRoom().getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onCountedUnreadCount(String roomID, int unreadCount, int mentionCount) {
                    if (!roomID.equals(vm.getRoom().getRoomID())) {
                        vm.setInitialUnreadCount(0);
                        hideUnreadButton();
                        return;
                    }
                    vm.setInitialUnreadCount(unreadCount);
                    if (vm.isUnreadButtonShown() && clUnreadButton.getVisibility() == View.GONE) {
                        vm.setUnreadButtonShown(false);
                        showUnreadButton(vm.getUnreadIndicator());
                    }
                }
            });
        }

        // Get unread mentions
        if (!TapUI.getInstance(instanceKey).isMentionUsernameDisabled()) {
            TAPDataManager.getInstance(instanceKey).getAllUnreadMentionsFromRoom(vm.getRoom().getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onSelectFinished(List<TAPMessageEntity> entities) {
                    if (!entities.isEmpty()) {
                        for (TAPMessageEntity entity : entities) {
                            TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                            vm.addUnreadMention(model);
                        }
                        updateMentionCount();
                    }
                }
            });
        }
    }

    private void showUnreadButton(@Nullable TAPMessageModel unreadIndicator) {
        if (0 >= vm.getInitialUnreadCount() || vm.isUnreadButtonShown()) {
            return;
        }
        vm.setUnreadButtonShown(true);
        rvMessageList.post(() -> runOnUiThread(() -> {
            if (vm.isAllUnreadMessagesHidden()) {
                // All unread messages are hidden
                return;
            }
            if (null != unreadIndicator) {
                View view = messageLayoutManager.findViewByPosition(messageAdapter.getItems().indexOf(unreadIndicator));
                if (null != view) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    if (location[1] < TAPUtils.getScreenHeight()) {
                        // Do not show button if unread indicator is visible on screen
                        return;
                    }
                }
            }
            tvUnreadButtonCount.setText(String.format(getString(R.string.tap_format_s_unread_messages),
                    vm.getInitialUnreadCount() > 99 ? getString(R.string.tap_over_99) : vm.getInitialUnreadCount()));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_up_circle_orange));
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(v -> scrollToMessage(UNREAD_INDICATOR_LOCAL_ID));
        }));
    }

    public void hideUnreadButton() {
        if (null != ivUnreadButtonImage.getAnimation()) {
            return;
        }
        runOnUiThread(() -> {
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void showUnreadButtonLoading() {
        runOnUiThread(() -> {
            tvUnreadButtonCount.setText(getString(R.string.tap_loading));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivUnreadButtonImage.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivUnreadButtonImage);
            }
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void hideUnreadButtonLoading() {
        runOnUiThread(() -> {
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }


    private void showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator();
        rvMessageList.post(() -> runOnUiThread(() -> {
            vm.addMessagePointer(vm.getLoadingIndicator(true));
            messageAdapter.addItem(vm.getLoadingIndicator(false)); // Add loading indicator to last index
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
        }));
    }

    private void hideLoadingOlderMessagesIndicator() {
        rvMessageList.post(() -> runOnUiThread(() -> {
            TAPMessageModel loadingIndicator = vm.getLoadingIndicator(false);
            if (!messageAdapter.getItems().contains(loadingIndicator)) {
                return;
            }
            int index = messageAdapter.getItems().indexOf(loadingIndicator);
            vm.removeMessagePointer(LOADING_INDICATOR_LOCAL_ID);
            if (index >= 0 &&
                    index < messageAdapter.getItemCount() &&
                    messageAdapter.getItemAt(index).getType() == TYPE_LOADING_MESSAGE_IDENTIFIER
            ) {
                messageAdapter.removeMessage(loadingIndicator);
                messageAdapter.setMessages(vm.getMessageModels());
                if (null != messageAdapter.getItemAt(index)) {
                    messageAdapter.notifyItemChanged(index);
                } else {
                    messageAdapter.notifyItemRemoved(index);
                }
                updateMessageDecoration();
            }
        }));
    }

    private RecyclerView.OnScrollListener messageListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // Show/hide ivToBottom
            if (messageLayoutManager.findFirstVisibleItemPosition() <= vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(true);
                ivToBottom.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.GONE);
                vm.clearUnreadMessages();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex() && !vm.isScrollFromKeyboard()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                hideUnreadButton();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                vm.setScrollFromKeyboard(false);
            }

            if (newState == SCROLL_STATE_IDLE) {
                // Start hide date indicator timer if state is idle
                hideDateIndicatorTimer.start();
            } else if (newState == SCROLL_STATE_DRAGGING) {
                // Show date indicator
                TAPMessageModel firstVisibleMessage = messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition());
                if (firstVisibleMessage != null) {
                    hideDateIndicatorTimer.cancel();
                    tvDateIndicator.setText(TAPTimeFormatter.dateStampString(TapUIChatActivity.this, firstVisibleMessage.getCreated()));
                    tvDateIndicator.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Update date indicator text
            if (tvDateIndicator.getVisibility() == View.VISIBLE) {
                tvDateIndicator.setText(TAPTimeFormatter.dateStampString(TapUIChatActivity.this,
                        messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition()).getCreated()));
                tvDateIndicator.setVisibility(View.VISIBLE);
            }
        }

        private CountDownTimer hideDateIndicatorTimer = new CountDownTimer(1000L, 100L) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                runOnUiThread(() -> tvDateIndicator.setVisibility(View.GONE));
            }
        };
    };

    private Runnable setLinkRunnable(String text) {
        return new Runnable() {

            String firstUrl = TAPUtils.getFirstUrlFromString(text);

            @Override
            public void run() {
                if (!firstUrl.isEmpty() && !firstUrl.startsWith("http://") && !firstUrl.startsWith("https://")) {
                    firstUrl = "http://" + firstUrl;
                }
                if (firstUrl.equals(vm.getCurrentLinkPreviewUrl())) {
                    return;
                }
                if (!firstUrl.isEmpty() && !firstUrl.equals(vm.getLinkHashMap().get(TAPDefaultConstant.MessageData.URL))) {
                    // Contains url
                    showLinkPreviewLoading(firstUrl);
                    Observable.fromCallable(() -> {
                        Document document;
                        HashMap<String, String> linkMap = new HashMap<>();
                        document = Jsoup.connect(firstUrl).get();

                        Element title = document.selectFirst("meta[property='og:title']");
                        if (title != null && title.attr("content") != null && !title.attr("content").isEmpty()) {
                            linkMap.put(TITLE, title.attr("content"));
                        }
                        else if (!document.title().isEmpty()) {
                            linkMap.put(TITLE, document.title());
                        }
                        linkMap.put(TAPDefaultConstant.MessageData.URL, firstUrl);
                        Element img = document.selectFirst("meta[property='og:image']");
                        if (img != null) {
                            linkMap.put(IMAGE, img.attr("content"));
                        }
                        else {
                            img = document.selectFirst("img");
                            if (img != null) {
                                linkMap.put(IMAGE, img.absUrl("src"));
                            }
                        }
                        Element desc = document.selectFirst("meta[property='og:description']");
                        if (desc != null && desc.attr("content") != null && !desc.attr("content").isEmpty()) {
                            linkMap.put(DESCRIPTION, desc.attr("content"));
                        }
                        Element type = document.selectFirst("meta[property='og:type']");
                        if (type != null) {
                            linkMap.put(TYPE, type.attr("content"));
                        }
                        return linkMap;
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onNext(HashMap<String, String> linkMap) {
                                    if (firstUrl.equals(TAPUtils.setUrlWithProtocol(TAPUtils.getFirstUrlFromString(etChat.getText().toString())))) {
                                        if (linkMap.isEmpty() || (!linkMap.containsKey(TITLE) && !linkMap.containsKey(DESCRIPTION) && !linkMap.containsKey(IMAGE))) {
                                            hideLinkPreview(true);
                                            return;
                                        }
                                        vm.setLinkHashMap(linkMap);
                                        updateLinkPreview(firstUrl, linkMap.get(TITLE), linkMap.get(DESCRIPTION), linkMap.get(IMAGE) == null ? "" : linkMap.get(IMAGE));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (firstUrl.equals(TAPUtils.setUrlWithProtocol(TAPUtils.getFirstUrlFromString(etChat.getText().toString())))) {
                                        hideLinkPreview(true);
                                    }
                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                } else {
                    // Text only
                    hideLinkPreview(true);
                }
            }
        };
    }

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            linkHandler.removeCallbacks(linkRunnable);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (vm.getRecordingState() == DEFAULT) {
                if (s.length() > 0 && s.toString().trim().length() > 0) {
                    if (TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled()) {
                        // Delay 0.3 sec before url check
                        linkRunnable = setLinkRunnable(s.toString());
                        linkHandler.postDelayed(linkRunnable, 300);
                    }
                    // Hide chat menu and enable send button when EditText is filled
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                    if (vm.getQuoteAction() == EDIT) {
                        String caption = vm.getQuotedMessage().getData() != null? (String) vm.getQuotedMessage().getData().get(CAPTION) : "";
                        caption = caption != null? caption : "";
                        if (((vm.getQuotedMessage().getType() == TYPE_TEXT || vm.getQuotedMessage().getType() == TYPE_LINK) && vm.getQuotedMessage().getBody().equals(s.toString())) || ((vm.getQuotedMessage().getType() == TYPE_IMAGE || vm.getQuotedMessage().getType() == TYPE_VIDEO) &&
                                caption.equals(s.toString()))) {
                            setSendButtonDisabled();
                        } else {
                            setSendButtonEnabled();
                        }
                    } else {
                        setSendButtonEnabled();
                    }
                    checkAndSearchUserMentionList();
                    //checkAndHighlightTypedText();
                } else if (s.length() > 0) {
                    // Hide chat menu but keep send button disabled if trimmed text is empty
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                    setSendButtonDisabled();
                    hideUserMentionList();
                    hideLinkPreview(true);
                    //} else if (s.length() > 0 && s.toString().trim().length() > 0) {
                    //    if (vm.isCustomKeyboardEnabled()) {
                    //        ivChatMenu.setVisibility(View.VISIBLE);
                    //        ivButtonChatMenu.setVisibility(View.VISIBLE);
                    //    }
                    //    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, );(R.drawable.tap_bg_chat_composer_send_ripple));
                    //    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
                } else {
                    if (vm.isCustomKeyboardEnabled() && s.length() == 0) {
                        // Show chat menu if text is empty
                        ivChatMenu.setVisibility(View.VISIBLE);
                        ivButtonChatMenu.setVisibility(View.VISIBLE);
                    } else {
                        ivChatMenu.setVisibility(View.GONE);
                        ivButtonChatMenu.setVisibility(View.GONE);
                    }
                    String caption = (vm.getQuotedMessage() != null && vm.getQuotedMessage().getData() != null)? (String) vm.getQuotedMessage().getData().get(CAPTION) : "";
                    caption = caption != null? caption : "";
                    if (vm.getQuoteAction() == FORWARD || (vm.getQuoteAction() == EDIT && ((vm.getQuotedMessage().getType() == TYPE_IMAGE || vm.getQuotedMessage().getType() == TYPE_VIDEO) &&
                            !caption.equals(s.toString())))) {
                        // Enable send button if message to forward exists
                        setSendButtonEnabled();
                    } else {
                        // Disable send button
                        setSendButtonDisabled();
                    }
                    hideUserMentionList();
                    hideLinkPreview(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            sendTypingEmit(s.length() > 0);
        }
    };

    private TextView.OnEditorActionListener chatEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            TAPUtils.dismissKeyboard(TapUIChatActivity.this, etChat);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                etChat.clearFocus();
            }
            return true;
        }
    };

    private void showLinkPreviewLoading(String url) {
        tvLinkTitle.setText(R.string.tap_loading_dots);
        tvLinkTitle.setTextColor(ContextCompat.getColor(this, R.color.tapMediaLinkColor));
        tvLinkContent.setText(url);
        tvLinkContent.setVisibility(View.VISIBLE);
        glide.load(R.drawable.tap_ic_link_white).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(rcivLink);
        int padding = TAPUtils.dpToPx(8);
        rcivLink.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_rounded_primary_8dp));
        rcivLink.setPadding(padding, padding, padding, padding);
        rcivLink.setVisibility(View.VISIBLE);
        clLink.setVisibility(View.VISIBLE);
        vm.clearLinkHashMap();
        clQuote.setVisibility(View.GONE);
    }

    private void updateLinkPreview(String url, String linkTitle, String linkContent, String imageUrl) {
        vm.setCurrentLinkPreviewUrl(url);
        tvLinkTitle.setText(linkTitle);
        tvLinkTitle.setTextColor(ContextCompat.getColor(this, R.color.tapTitleLabelColor));
        if (linkContent == null || linkContent.isEmpty()) {
            tvLinkContent.setVisibility(View.GONE);
        } else {
            tvLinkContent.setVisibility(View.VISIBLE);
            tvLinkContent.setText(linkContent);
        }
        if (imageUrl != null && imageUrl.isEmpty()) {
            rcivLink.setVisibility(View.GONE);
        } else {
            glide.load(imageUrl).placeholder(R.drawable.tap_ic_link_white).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    int padding = TAPUtils.dpToPx(8);
                    rcivLink.setBackgroundDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_rounded_primary_8dp));
                    rcivLink.setPadding(padding, padding, padding, padding);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    rcivLink.setBackgroundDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_white_rounded_8dp));
                    rcivLink.setPadding(0,0,0,0);
                    return false;
                }
            }).error(R.drawable.tap_ic_link_white).into(rcivLink);
            rcivLink.setVisibility(View.VISIBLE);
        }
    }

    private void hideLinkPreview(boolean isClearLinkMap) {
        boolean hadFocus = etChat.hasFocus();
        vm.setCurrentLinkPreviewUrl("");
        if (isClearLinkMap) {
            vm.clearLinkHashMap();
        }
        else if (vm.getQuotedMessage() != null) {
            clQuote.setVisibility(View.VISIBLE);
        }
        clLink.setVisibility(View.GONE);

        if (hadFocus && isClearLinkMap) {
            etChat.post(() -> etChat.requestFocus());
        }
    }

    private void setSendButtonDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive_ripple));
        } else {
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
        }
        ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
        ivButtonSend.setEnabled(false);
    }

    private void setSendButtonEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_ripple));
        } else {
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send));
        }
        ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
        ivButtonSend.setEnabled(true);
    }

    private void checkAndSearchUserMentionList() {
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled() || vm.getRoomParticipantsByUsername().isEmpty()) {
            hideUserMentionList();
            return;
        }
        String s = etChat.getText().toString();
        if (!s.contains("@")) {
            // Return if text does not contain @
            hideUserMentionList();
            return;
        }
        int cursorIndex = etChat.getSelectionStart();
        int loopIndex = etChat.getSelectionStart();
        while (loopIndex > 0) {
            // Loop text from cursor index to the left
            loopIndex--;
            char c = s.charAt(loopIndex);
            if (c == ' ' || c == '\n') {
                // Found space before @, return
                hideUserMentionList();
                return;
            }
            if (c == '@') {
                // Found @, start searching user
                String keyword = s.substring(loopIndex + 1, cursorIndex).toLowerCase();
                if (keyword.isEmpty()) {
                    // Show all participants
                    List<TAPUserModel> searchResult = new ArrayList<>(vm.getRoomParticipantsByUsername().values());
                    searchResult.remove(vm.getMyUserModel());
                    showUserMentionList(searchResult, loopIndex, cursorIndex);
                } else {
                    // Search participants from keyword
                    int finalLoopIndex = loopIndex;
                    new Thread(() -> {
                        List<TAPUserModel> searchResult = new ArrayList<>();
                        for (Map.Entry<String, TAPUserModel> entry : vm.getRoomParticipantsByUsername().entrySet()) {
                            if (null != entry.getValue().getUsername() &&
                                    !entry.getValue().getUsername().equals(vm.getMyUserModel().getUsername()) &&
                                    (entry.getValue().getFullname().toLowerCase().contains(keyword) ||
                                            entry.getValue().getUsername().toLowerCase().contains(keyword))) {
                                // Add result if name/username matches and not self
                                searchResult.add(entry.getValue());
                            }
                        }
                        runOnUiThread(() -> showUserMentionList(searchResult, finalLoopIndex, cursorIndex));
                    }).start();
                }
                return;
            }
        }
        hideUserMentionList();
    }

    private void updateMessageMentionIndexes(TAPMessageModel message) {
        vm.getMessageMentionIndexes().remove(message.getLocalID());
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled() || vm.getRoom().getType() == TYPE_PERSONAL) {
            return;
        }
        String originalText;
        if (message.getType() == TYPE_TEXT || message.getType() == TYPE_LINK) {
            originalText = message.getBody();
        } else if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) && null != message.getData()) {
            originalText = (String) message.getData().get(CAPTION);
        } else if (message.getType() == TYPE_LOCATION && null != message.getData()) {
            originalText = (String) message.getData().get(ADDRESS);
        } else {
            return;
        }
        if (null == originalText) {
            return;
        }
        List<Integer> mentionIndexes = new ArrayList<>();
        if (originalText.contains("@")) {
            int length = originalText.length();
            int startIndex = -1;
            for (int i = 0; i < length; i++) {
                if (originalText.charAt(i) == '@' && startIndex == -1) {
                    // Set index of @ (mention start index)
                    startIndex = i;
                } else {
                    boolean endOfMention = originalText.charAt(i) == ' ' ||
                            originalText.charAt(i) == '\n';
                    if (i == (length - 1) && startIndex != -1) {
                        // End of string (mention end index)
                        int endIndex = endOfMention ? i : (i + 1);
                        //String username = originalText.substring(startIndex + 1, endIndex);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (endIndex > (startIndex + 1)) {
                            mentionIndexes.add(startIndex);
                            mentionIndexes.add(endIndex);
                        }
                        //}
                        startIndex = -1;
                    } else if (endOfMention && startIndex != -1) {
                        // End index for mentioned username
                        //String username = originalText.substring(startIndex + 1, i);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (i > (startIndex + 1)) {
                            mentionIndexes.add(startIndex);
                            mentionIndexes.add(i);
                        }
                        //}
                        startIndex = -1;
                    }
                }
            }
            if (!mentionIndexes.isEmpty()) {
                vm.getMessageMentionIndexes().put(message.getLocalID(), mentionIndexes);
            }
        }
    }

    private void showUserMentionList(List<TAPUserModel> searchResult, int loopIndex, int cursorIndex) {
        if (!searchResult.isEmpty()) {
            // Show search result in list
            userMentionListAdapter = new TapUserMentionListAdapter(searchResult, user -> {
                // Append username to typed text
                if (etChat.getText().length() >= cursorIndex) {
                    etChat.getText().replace(loopIndex + 1, cursorIndex, user.getUsername() + " ");
                }
            });
            rvUserMentionList.setMaxHeight(TAPUtils.dpToPx(160));
            rvUserMentionList.setAdapter(userMentionListAdapter);
            if (null == rvUserMentionList.getLayoutManager()) {
                rvUserMentionList.setLayoutManager(new LinearLayoutManager(
                        TapUIChatActivity.this, LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                        try {
                            super.onLayoutChildren(recycler, state);
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            clUserMentionList.setVisibility(View.VISIBLE);
        } else {
            // Result is empty
            hideUserMentionList();
        }
    }

    private void hideUserMentionList() {
        boolean hasFocus = etChat.hasFocus();
        clUserMentionList.setVisibility(View.GONE);
        if (hasFocus) {
            clUserMentionList.post(() -> {
                rvUserMentionList.setAdapter(null);
                rvUserMentionList.post(() -> etChat.requestFocus());
            });
        }
    }

    private void checkAndHighlightTypedText() {
        if (TapUI.getInstance(instanceKey).isMentionUsernameDisabled() || vm.getRoomParticipantsByUsername().isEmpty()) {
            hideUserMentionList();
            return;
        }
        String s = etChat.getText().toString();
        // Check for mentions
        if (vm.getRoom().getType() == TYPE_PERSONAL || !s.contains("@")) {
            return;
        }
        SpannableString span = new SpannableString(s);
        int cursorIndex = etChat.getSelectionStart();
        int mentionStartIndex = -1;
        int length = s.length();
        boolean isSpanSet = false;
        for (int i = 0; i < length; i++) {
            if (mentionStartIndex == -1 && s.charAt(i) == '@') {
                // Set index of @
                mentionStartIndex = i;
            } else {
                boolean endOfMention = s.charAt(i) == ' ' || s.charAt(i) == '\n';
                if (mentionStartIndex != -1 && i == (length - 1)) {
                    // End of string
                    int mentionEndIndex = endOfMention ? i : (i + 1);
                    String username = s.substring(mentionStartIndex + 1, mentionEndIndex);
                    if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        span.setSpan(new ForegroundColorSpan(
                                        ContextCompat.getColor(TapTalk.appContext,
                                                R.color.tapLeftBubbleMessageBodyURLColor)),
                                mentionStartIndex, mentionEndIndex, 0);
                        isSpanSet = true;
                    }
                    mentionStartIndex = -1;
                } else if (mentionStartIndex != -1 && endOfMention) {
                    // End index for mentioned username
                    String username = s.substring(mentionStartIndex + 1, i);
                    if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        span.setSpan(new ForegroundColorSpan(
                                        ContextCompat.getColor(TapTalk.appContext,
                                                R.color.tapLeftBubbleMessageBodyURLColor)),
                                mentionStartIndex, i, 0);
                        isSpanSet = true;
                    }
                    mentionStartIndex = -1;
                }
            }
        }
        if (isSpanSet) {
            etChat.removeTextChangedListener(chatWatcher);
            etChat.setText(span);
            etChat.setSelection(cursorIndex); // FIXME: 24 Apr 2020 TAP AND HOLD BACKSPACE NOT WORKING IF TEXT CONTAINS @
            etChat.addTextChangedListener(chatWatcher);
        }
    }

    private View.OnFocusChangeListener chatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && vm.isCustomKeyboardEnabled()) {
                vm.setScrollFromKeyboard(true);
                rvCustomKeyboard.setVisibility(View.GONE);
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_burger_menu_ripple));
                ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
                ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
                TAPUtils.showKeyboard(TapUIChatActivity.this, etChat);

                if (0 < etChat.getText().toString().length()) {
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
            } else if (hasFocus) {
                vm.setScrollFromKeyboard(true);
                TAPUtils.showKeyboard(TapUIChatActivity.this, etChat);
            }
//            else {
//                etChat.requestFocus();
//            }
        }
    };

    private LayoutTransition.TransitionListener containerTransitionListener = new LayoutTransition.TransitionListener() {
        @Override
        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            // Change animation state
            if (vm.getContainerAnimationState() != vm.PROCESSING) {
                vm.setContainerAnimationState(vm.ANIMATING);
            }
        }

        @Override
        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (vm.getContainerAnimationState() == vm.ANIMATING) {
                processPendingMessages();
            }
        }

        private void processPendingMessages() {
            vm.setContainerAnimationState(vm.PROCESSING);
            if (vm.getPendingRecyclerMessages().size() > 0) {
                // Copy list to prevent concurrent exception
                List<TAPMessageModel> pendingMessages = new ArrayList<>(vm.getPendingRecyclerMessages());
                for (TAPMessageModel pendingMessage : pendingMessages) {
                    // Loop the copied list to add messages
                    if (vm.getContainerAnimationState() != vm.PROCESSING) {
                        return;
                    }
                    updateMessage(pendingMessage);
                }
                // Remove added messages from pending message list
                vm.getPendingRecyclerMessages().removeAll(pendingMessages);
                if (vm.getPendingRecyclerMessages().size() > 0) {
                    // Redo process if pending message is not empty
                    processPendingMessages();
                    return;
                }
            }
            // Change state to idle when processing finished
            vm.setContainerAnimationState(vm.IDLE);
        }
    };

    private void showLoadingPopup() {
        runOnUiThread(() -> {
            ivLoadingPopup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivLoadingPopup.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivLoadingPopup);
            }
            tvLoadingText.setVisibility(View.INVISIBLE);
            flLoading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                if (flLoading.getVisibility() == View.VISIBLE) {
                    tvLoadingText.setText(getString(R.string.tap_cancel));
                    tvLoadingText.setVisibility(View.VISIBLE);
                }
            }, 1000L);
        });
    }

    private void hideLoadingPopup() {
        runOnUiThread(() -> flLoading.setVisibility(View.GONE));
    }

    private void showErrorDialog(String title, String message) {
        runOnUiThread(() -> new TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .show());
    }

    private final BroadcastReceiver foregroundBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }
            Uri fileUri;
            TAPMessageModel message = null;
            if (null != intent.getParcelableExtra(MESSAGE) && intent.getParcelableExtra(MESSAGE) instanceof TAPMessageModel) {
                message = intent.getParcelableExtra(MESSAGE);
            }
            switch (action) {
                case OpenFile:
                    if (message != null && message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                        fileUri = intent.getParcelableExtra(FILE_URI);
                        vm.setOpenedFileMessage(message);
                        if (null != fileUri) {
                            String mimeType = TAPFileUtils.getMimeTypeFromUri(TapUIChatActivity.this, fileUri);
                            if ((mimeType == null || mimeType.isEmpty() || mimeType.contains("octet-stream")) && message.getData() != null && message.getData().containsKey(FILE_URL)) {
                                String url = (String) message.getData().get(FILE_URL);
                                mimeType = TAPUtils.getMimeTypeFromUrl(url);
                            }
                            if ((mimeType == null || mimeType.isEmpty() || mimeType.contains("octet-stream")) && message.getData() != null && message.getData().containsKey(MEDIA_TYPE)) {
                                mimeType = TAPUtils.getMimeTypeFromMessage(message);
                            }
                            if (mimeType != null && !mimeType.isEmpty()) {
                                if (!TAPUtils.openFile(instanceKey, TapUIChatActivity.this, fileUri, mimeType)) {
                                    showDownloadFileDialog();
                                }
                            }
                        } else {
                            showDownloadFileDialog();
                        }
                    }
                    break;
                case PlayPauseVoiceNote:
                    if (message != null && message.getRoom().getRoomID().equals(vm.getRoom().getRoomID())) {
                        showFinishedRecording();
                        boolean isPlaying = intent.getBooleanExtra(IS_PLAYING, false);
                        if (isPlaying) {
                            if (vm.getMediaPlayer() != null && vm.getMediaPlayer().isPlaying()) {
                                pauseVoiceNote();
                            }
                        }
                    }
                    break;
                case LongPressChatBubble:
                    if (message != null &&
                        message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) &&
                        (TYPE_PERSONAL != vm.getRoom().getType() ||
                        (null != vm.getOtherUserModel() &&
                        (null == vm.getOtherUserModel().getDeleted() ||
                        vm.getOtherUserModel().getDeleted() <= 0L)))
                    ) {
                        TAPLongPressActionBottomSheet chatBubbleBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(instanceKey, CHAT_BUBBLE_TYPE, (TAPMessageModel) intent.getParcelableExtra(MESSAGE), longPressListener, vm.getStarredMessageIds(), vm.getPinnedMessageIds());
                        chatBubbleBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressLink:
                    if (message != null &&
                        message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) &&
                        null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        TAPLongPressActionBottomSheet linkBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(
                            instanceKey,
                            LINK_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE),
                            intent.getStringExtra(URL_MESSAGE),
                            longPressListener
                        );
                        linkBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressEmail:
                    if (message != null &&
                        message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) &&
                        null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        TAPLongPressActionBottomSheet emailBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(
                            instanceKey,
                            EMAIL_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE),
                            intent.getStringExtra(URL_MESSAGE),
                            longPressListener
                        );
                        emailBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressPhone:
                    if (message != null &&
                        message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) &&
                        null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        TAPLongPressActionBottomSheet phoneBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(
                            instanceKey,
                            PHONE_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE),
                            intent.getStringExtra(URL_MESSAGE),
                            longPressListener
                        );
                        phoneBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressMention:
                    if (message != null &&
                        message.getRoom().getRoomID().equals(vm.getRoom().getRoomID()) &&
                        null != intent.getStringExtra(URL_MESSAGE) &&
                        null != intent.getStringExtra(COPY_MESSAGE)
                    ) {
                        TAPLongPressActionBottomSheet mentionBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(
                            instanceKey,
                            MENTION_TYPE,
                            intent.getParcelableExtra(MESSAGE),
                            intent.getStringExtra(COPY_MESSAGE),
                            intent.getStringExtra(URL_MESSAGE),
                            longPressListener
                        );
                        mentionBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LinkPreviewImageLoaded:
                    if (vm.isOnBottom() && rvMessageList.getScrollState() == SCROLL_STATE_IDLE) {
                        // Scroll recycler to bottom
                        rvMessageList.scrollToPosition(0);
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver backgroundBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }
            String localID;
            switch (action) {
                case UploadProgressLoading:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case UploadProgressFinish:
                    localID = intent.getStringExtra(UploadLocalID);
                    TAPMessageModel messageModel = vm.getMessagePointer().get(localID);
                    if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadImageData) &&
                            intent.getSerializableExtra(UploadImageData) instanceof HashMap) {
                        // Set image data
                        messageModel.setData((HashMap<String, Object>) intent.getSerializableExtra(UploadImageData));
                    } else if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadFileData) &&
                            intent.getSerializableExtra(UploadFileData) instanceof HashMap) {
                        // Put file data
                        messageModel.putData((HashMap<String, Object>) intent.getSerializableExtra(UploadFileData));
                    }
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(messageModel));
                    break;
                case UploadFailed:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel failedMessageModel = vm.getMessagePointer().get(localID);
                        failedMessageModel.setIsFailedSend(true);
                        failedMessageModel.setIsSending(false);
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(failedMessageModel));
                    }
                    break;
                case UploadCancelled:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel cancelledMessageModel = vm.getMessagePointer().get(localID);
                        vm.delete(localID);
                        int itemPos = messageAdapter.getItems().indexOf(cancelledMessageModel);

                        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(TapUIChatActivity.this, cancelledMessageModel,
                                vm.getRoom().getRoomID());

                        vm.removeFromUploadingList(localID);
                        vm.removeMessagePointer(localID);
                        messageAdapter.removeMessageAt(itemPos);
                    }
                    break;
                case DownloadProgressLoading:
                case DownloadFinish:
                    localID = intent.getStringExtra(DownloadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFailed:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFile:
                    startFileDownload(intent.getParcelableExtra(MESSAGE));
                    break;
                case CancelDownload:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
            }
        }
    };

    /**
     * =========================================================================================== *
     * LOAD MESSAGES
     * =========================================================================================== *
     */

    private void getAllUnreadMessage() {
        TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(TAPChatManager.getInstance(instanceKey).getOpenRoom(),
                new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        if (0 < entities.size()) {
                            vm.setLastUnreadMessageLocalID(entities.get(0).getLocalID());
//                            new Thread(() -> {
                            boolean allUnreadHidden = true; // Flag to check hidden unread when looping
                            for (TAPMessageEntity entity : entities) {
                                if (null == entity.getHidden() || !entity.getHidden()) {
                                    allUnreadHidden = false;
                                    break;
                                }
                            }
                            if (allUnreadHidden) {
                                vm.setAllUnreadMessagesHidden(true);
                            }
//                            }).start();
                        }
                        //vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
                        loadMessagesFromDatabase();
                    }
                });
    }

    private void loadMessagesFromDatabase() {
        if (state != STATE.LOADED || vm.getRoom() == null || vm.getRoom().getRoomID().isEmpty()) {
            return;
        }

        state = STATE.WORKING;
        final boolean isFirstLoad = vm.getLastTimestamp() == 0L;

        TAPDatabaseListener<TAPMessageEntity> listener = new TAPDatabaseListener<>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                final List<TAPMessageModel> models = new ArrayList<>();
                String previousDate = "";
                TAPMessageModel previousMessage = null;
                if (null != messageAdapter && !messageAdapter.getItems().isEmpty()) {
                    // Obtain previous message & date
                    int offset = 1;
                    while (null == previousMessage) {
                        if (messageAdapter.getItems().size() < offset) {
                            break;
                        }
                        previousMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
                        if ((null != previousMessage.getIsHidden() && previousMessage.getIsHidden()) ||
                                previousMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                                previousMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                                previousMessage.getType() == TYPE_DATE_SEPARATOR
                        ) {
                            previousMessage = null;
                            offset++;
                        }
                    }
                    if (null != previousMessage) {
                        previousDate = TAPTimeFormatter.formatDate(previousMessage.getCreated());
                    }
                }
                LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
                LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
                int visibleCount = 0;

                if (0 < entities.size()) {
                    vm.setLastTimestamp(entities.get(entities.size() - 1).getCreated());
                }

                for (TAPMessageEntity entity : entities) {
                    if (!vm.getMessagePointer().containsKey(entity.getLocalID())) {
                        TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                        models.add(model);
                        vm.addMessagePointer(model);

                        if (null == model.getIsHidden() || !model.getIsHidden()) {
                            visibleCount++;
                            if (vm.isAllMessagesHidden()) {
                                vm.setAllMessagesHidden(false);
                            }
                        }

                        updateMessageMentionIndexes(model);

                        if ((null == model.getIsRead() || !model.getIsRead()) &&
                                TAPUtils.isActiveUserMentioned(model, vm.getMyUserModel())) {
                            // Add unread mention
                            vm.addUnreadMention(model);
                        }

                        if ((null == entity.getHidden() || !entity.getHidden()) &&
                            entity.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                            entity.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                            entity.getType() != TYPE_DATE_SEPARATOR
                        ) {
                            String currentDate = TAPTimeFormatter.formatDate(model.getCreated());
                            if (null != previousMessage && !currentDate.equals(previousDate)) {
                                // Generate date separator if date is different
                                int index = models.contains(previousMessage) ? models.indexOf(previousMessage) + 1 : 0;
                                TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                                dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                                dateSeparatorIndex.put(dateSeparator.getLocalID(), index);
                            }
                            previousDate = currentDate;
                            previousMessage = model;
                        }
                    }
                }

                if (null == messageAdapter) {
                    return;
                }

                insertDateSeparators(dateSeparators, dateSeparatorIndex, models);

                vm.setVisibleMessageBubbleCount(vm.getVisibleMessageBubbleCount() + visibleCount);

                if (state == STATE.WORKING) {
                    state = STATE.LOADED;
                }

                int currentVisibleCount = visibleCount;
                runOnUiThread(() -> {
                    messageAdapter.addMessage(models);

                    if (models.isEmpty() || vm.isAllMessagesHidden()) {
                        if (entities.size() >= MAX_ITEMS_PER_PAGE) {
                            // All loaded messages are hidden, load more from database
                            loadMessagesFromDatabase();
                        }
                        else if (models.isEmpty() && STATE.DONE != state) {
                            // Fetch older messages from API
                            showLoadingOlderMessagesIndicator();
                            fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                        }
                        else if (messageAdapter.getItemCount() <= 0) {
                            // Chat is empty
                            hideLoadingOlderMessagesIndicator();

                            if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
                                cvEmptySavedMessages.setVisibility(View.VISIBLE);
                            }
                            else {
                                clEmptyChat.setVisibility(View.VISIBLE);
                            }

                            // Load my avatar
                            if (null != vm.getMyUserModel().getImageURL() &&
                                !vm.getMyUserModel().getImageURL().getThumbnail().isEmpty()
                            ) {
                                loadProfilePicture(vm.getMyUserModel().getImageURL().getThumbnail(), civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                            }
                            else {
                                loadInitialsToProfilePicture(civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                            }

                            // Load room avatar
                            if (null != vm.getRoom() &&
                                TYPE_PERSONAL == vm.getRoom().getType() &&
                                null != vm.getOtherUserModel() &&
                                null != vm.getOtherUserModel().getImageURL().getThumbnail() &&
                                !vm.getOtherUserModel().getImageURL().getThumbnail().isEmpty()
                            ) {
                                loadProfilePicture(vm.getOtherUserModel().getImageURL().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                            }
                            else if (null != vm.getRoom() &&
                                    null != vm.getRoom().getImageURL() &&
                                    !vm.getRoom().getImageURL().getThumbnail().isEmpty()
                            ) {
                                loadProfilePicture(vm.getRoom().getImageURL().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                            }
                            else {
                                loadInitialsToProfilePicture(civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                            }
                        }
                    }
                    else {
                        // Loaded more messages
                        if (entities.size() < MAX_ITEMS_PER_PAGE && STATE.DONE != state) {
                            // Fetch older messages from API
                            if (0 == entities.size()) {
                                showLoadingOlderMessagesIndicator();
                            }
                            else {
                                vm.setNeedToShowLoading(true);
                            }
                            fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                        }

                        if (vm.isNeedToShowLoading()) {
                            // Show loading if Before API is called
                            vm.setNeedToShowLoading(false);
                            showLoadingOlderMessagesIndicator();
                        }
                        else {
                            hideLoadingOlderMessagesIndicator();
                        }

                        // Insert unread indicator
                        TAPMessageModel lastUnreadMessage = vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID());
                        if (null != lastUnreadMessage && null == vm.getUnreadIndicator() && !vm.isAllUnreadMessagesHidden()) {
                            TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(lastUnreadMessage.getCreated(), lastUnreadMessage.getUser());
                            messageAdapter.getItems().add(messageAdapter.getItems().indexOf(lastUnreadMessage) + 1, unreadIndicator);
                        }

                        vm.setMessageModels(messageAdapter.getItems());
                        showMessageList();
                        showUnreadButton(vm.getUnreadIndicator());
                        updateMentionCount();
                        checkChatRoomLocked(models.get(0));
                        updateMessageDecoration();
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }

                        if (vm.getVisibleMessageBubbleCount() < MAX_ITEMS_PER_PAGE && currentVisibleCount < (MAX_ITEMS_PER_PAGE / 2)) {
                            // Load more messages if half or more loaded items are hidden
                            loadMessagesFromDatabase();
                        }
                    }

                    if (isFirstLoad) {
//                        if (vm.isCustomKeyboardEnabled() && 0 == etChat.getText().toString().trim().length()) {
//                            showCustomKeyboard();
//                        }
                        rvMessageList.scrollToPosition(0);

                        if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size()) {
                            // Message not empty and below 50, fetch newer messages from API
                            callApiAfter();
                            if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapUIChatActivity.this)) {
                                insertDateSeparatorToLastIndex(false);
                            }
                        }
                        else if (MAX_ITEMS_PER_PAGE <= vm.getMessageModels().size()) {
                            // Message count over 50, fetch newer messages from API and add pagination listener
                            rvMessageList.addOnScrollListener(endlessScrollListener);
                            callApiAfter();
                        }
                    }
                });
            }
        };

        if (vm.getLastTimestamp() == 0L) {
            TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(vm.getRoom().getRoomID(), listener);
        }
        else {
            TAPDataManager.getInstance(instanceKey).getMessagesFromDatabaseDesc(vm.getRoom().getRoomID(), listener, vm.getLastTimestamp());
        }
    }

    private void callApiAfter() {
        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            return;
        }
        if (!vm.isInitialAPICallFinished()) {
            showLoadingOlderMessagesIndicator();
        }
        new Thread(() -> {
            if (vm.getMessageModels().size() > 0 && !TAPDataManager.getInstance(instanceKey).checkKeyInLastMessageTimestamp(vm.getRoom().getRoomID())) {
                // Set oldest message's create time as minCreated and lastUpdated if last updated timestamp does not exist in preference
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                // Set oldest message's create time as minCreated, last updated timestamp is obtained from preference
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
    }

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageAfterView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            if (TAPChatManager.getInstance(instanceKey).hasPendingMessages()) {
                vm.setPendingAfterResponse(response);
                return;
            }
            vm.setPendingAfterResponse(null);
            List<TAPMessageEntity> responseMessages = new ArrayList<>(); // Entities to be saved to database
            List<TAPMessageModel> messageAfterModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            List<String> unreadMessageIds = new ArrayList<>(); // Results to be marked as read
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
            TAPMessageModel updateRoomDetailSystemMessage = null;

            int unreadMessageIndex = -1; // Index for unread message identifier
            long smallestUnreadCreated = 0L;

            vm.setAllUnreadMessagesHidden(false); // Set initial value for unread identifier/button flag
            int allUnreadHidden = 0; // Flag to check hidden unread when looping
            boolean allMessagesHidden = true; // Flag to check whether empty chat layout should be removed

            messageAdapter.getItems().removeAll(vm.getDateSeparators().values());
            vm.getDateSeparators().clear();

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    if (message != null) {
                        String newID = message.getLocalID();
                        if (vm.getMessagePointer().containsKey(newID)) {
                            // Update existing message
                            vm.updateMessagePointer(message);
                            runOnUiThread(() -> {
                                messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                            });
                        } else if (!vm.getMessagePointer().containsKey(newID)) {
                            // Insert new message to list and HashMap
                            messageAfterModels.add(message);
                            vm.addMessagePointer(message);

                            if ("".equals(vm.getLastUnreadMessageLocalID())
                                    && (smallestUnreadCreated > message.getCreated() || 0L == smallestUnreadCreated)
                                    && (null != message.getIsRead() && !message.getIsRead())
                                    && null == vm.getUnreadIndicator()) {
                                // Update first unread message index
                                unreadMessageIndex = messageAfterModels.indexOf(message);
                                smallestUnreadCreated = message.getCreated();
                            }

                            if (allMessagesHidden && (null == message.getIsHidden() || !message.getIsHidden())) {
                                allMessagesHidden = false;
                            }

                            updateMessageMentionIndexes(message);

                            if ((null == message.getIsRead() || !message.getIsRead()) &&
                                    TAPUtils.isActiveUserMentioned(message, vm.getMyUserModel())) {
                                // Add unread mention
                                vm.addUnreadMention(message);
                            }

                            if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                                    null != message.getAction() &&
                                    (message.getAction().equals(UPDATE_ROOM) ||
                                            message.getAction().equals(UPDATE_USER)) &&
                                    (null == updateRoomDetailSystemMessage ||
                                            updateRoomDetailSystemMessage.getCreated() < message.getCreated())) {
                                // Store update room system message
                                updateRoomDetailSystemMessage = message;
                            }
                        }

                        if (null == message.getIsRead() || !message.getIsRead()) {
    //                        TAPMessageModel messageFromPointer = vm.getMessagePointer().get(message.getLocalID());
    //                        if (null != messageFromPointer) {
    //                            messageFromPointer = messageFromPointer.copyMessageModel();
    //                        }
                            if (!TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getUser().getUserID()) &&
    //                                (null == message.getHidden() || !message.getHidden()) &&
    //                                (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
    //                                (null == messageFromPointer || null == messageFromPointer.getHidden() || !messageFromPointer.getHidden()) &&
                                    !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(message.getMessageID()) &&
                                    !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(message.getMessageID())
                            ) {
                                // Add message ID to pending list if new message has not been read or not in mark read queue
                                unreadMessageIds.add(message.getMessageID());
                            }

                            if (allUnreadHidden != -1 && null != message.getIsHidden() && message.getIsHidden()) {
                                allUnreadHidden = 1;
                            } else {
                                // Set allUnreadHidden to false
                                allUnreadHidden = -1;
                            }
                        }

                        responseMessages.add(TAPMessageEntity.fromMessageModel(message));
                        new Thread(() -> {
                            // Update last updated timestamp in preference (new thread to prevent stutter when scrolling)
                            if (null != message.getUpdated() &&
                                    TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < message.getUpdated()
                            ) {
                                TAPDataManager.getInstance(instanceKey).saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), message.getUpdated());
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != updateRoomDetailSystemMessage) {
                // Update room detail if update room system message exists in API result
                updateRoomDetailFromSystemMessage(updateRoomDetailSystemMessage);
            }

            if (!vm.isInitialAPICallFinished()) {
                // Add unread messages to count
                vm.setInitialUnreadCount(vm.getInitialUnreadCount() + unreadMessageIds.size());
            }

            if (allUnreadHidden == 1) {
                // All unread messages are hidden
                vm.setAllUnreadMessagesHidden(true);
            }
            if (-1 != unreadMessageIndex && 0L != smallestUnreadCreated && !vm.isAllUnreadMessagesHidden()) {
                // Insert unread indicator
                TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                        messageAfterModels.get(unreadMessageIndex).getCreated(),
                        messageAfterModels.get(unreadMessageIndex).getUser());
                messageAfterModels.add(unreadMessageIndex, unreadIndicator);
            }

            if (0 < messageAfterModels.size()) {
                // Update new message status to delivered
                TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(messageAfterModels);
            }

            // Insert new messages to first index
            messageAdapter.addMessage(0, messageAfterModels, false);
            // Sort adapter items according to timestamp
            TAPUtils.mergeSort(messageAdapter.getItems(), ASCENDING);

            String previousDate = "";
            TAPMessageModel previousMessage = null;
            for (TAPMessageModel message : messageAdapter.getItems()) {
                if ((null == message.getIsHidden() || !message.getIsHidden()) &&
                        message.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.formatDate(message.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), messageAdapter.getItems().indexOf(previousMessage) + 1);
                    }
                    previousDate = currentDate;
                    previousMessage = message;
                }
            }
            insertDateSeparators(dateSeparators, dateSeparatorIndex, messageAdapter.getItems());

            boolean finalAllMessagesHidden = allMessagesHidden;
            runOnUiThread(() -> {
                if (!finalAllMessagesHidden) {
                    if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) && cvEmptySavedMessages.getVisibility() == View.VISIBLE) {
                        cvEmptySavedMessages.setVisibility(View.GONE);
                    } else if (clEmptyChat.getVisibility() == View.VISIBLE) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                }
                showMessageList();
                updateMessageDecoration();
                // Moved outside UI thread 30/04/2020
//                // Insert new messages to first index
//                messageAdapter.addMessage(0, messageAfterModels, false);
//                // Sort adapter items according to timestamp
//                TAPUtils.mergeSort(messageAdapter.getItems(), ASCENDING);
                showUnreadButton(vm.getUnreadIndicator());
                updateMentionCount();

                if (vm.isOnBottom() && 0 < messageAfterModels.size()) {
                    // Scroll recycler to bottom
                    rvMessageList.scrollToPosition(0);
                }
                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                if (state == STATE.DONE) {
                    insertDateSeparatorToLastIndex(true);
                    updateMessageDecoration();
                }
            });

            if (0 < responseMessages.size()) {
                // Save entities to database
                TAPDataManager.getInstance(instanceKey).insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
                });
            }

            if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size() && !vm.isInitialAPICallFinished()) {
                // Fetch older messages on first call
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
            } else {
                hideLoadingOlderMessagesIndicator();
            }

            if (!vm.isInitialAPICallFinished()) {
                vm.setInitialAPICallFinished(true);
                setAllUnreadMessageToRead(unreadMessageIds);
            }
            checkIfChatIsAvailableAndUpdateUI();
            updateFirstVisibleMessageIndex();
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            checkIfChatIsAvailableAndUpdateUI();
            if (0 < vm.getMessageModels().size()) {
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                hideLoadingOlderMessagesIndicator();
            }
        }

        private void checkIfChatIsAvailableAndUpdateUI() {
            if (messageAdapter.getItems().isEmpty()) {
                return;
            }
            TAPMessageModel lastMessage = messageAdapter.getItems().get(0);
            if ((ROOM_REMOVE_PARTICIPANT.equals(lastMessage.getAction()) &&
                    null != lastMessage.getTarget() &&
                    vm.getMyUserModel().getUserID().equals(lastMessage.getTarget().getTargetID())) ||
                    (LEAVE_ROOM.equals(lastMessage.getAction()) &&
                            vm.getMyUserModel().getUserID().equals(lastMessage.getUser().getUserID()))) {
                // User has been removed / left from group
                showChatAsHistory(getString(R.string.tap_not_a_participant));
            } else if (DELETE_ROOM.equals(lastMessage.getAction())) {
                // Room was deleted
                //showRoomIsUnavailableState();
                showChatAsHistory(getString(R.string.tap_group_unavailable));
            } else {
                checkChatRoomLocked(lastMessage);
            }
        }
    };

    private void setAllUnreadMessageToRead(List<String> unreadMessageIds) {
        new Thread(() -> {
            // Clear unread badge from room list
            Intent intent = new Intent(CLEAR_ROOM_LIST_BADGE);
            intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

            // Mark filtered API result messages as read
            markMessageAsRead(unreadMessageIds);

            // Mark messages from database as read
            TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(TAPChatManager.getInstance(instanceKey).getOpenRoom(),
                    new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            List<String> pendingReadList = new ArrayList<>();
                            for (TAPMessageEntity entity : entities) {
                                TAPMessageModel messageFromPointer = vm.getMessagePointer().get(entity.getLocalID());
                                if (null != messageFromPointer) {
                                    messageFromPointer = messageFromPointer.copyMessageModel();
                                }
                                if (!unreadMessageIds.contains(entity.getMessageID()) &&
                                        (null == entity.getIsRead() || !entity.getIsRead()) &&
                                        (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(entity.getMessageID()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(entity.getMessageID())) {
                                    // Add message ID to pending list if new message has not been read or not in mark read queue
                                    pendingReadList.add(entity.getMessageID());
                                }
                            }
                            markMessageAsRead(pendingReadList);
                        }
                    });
        }).start();
    }

    private void fetchBeforeMessageFromAPIAndUpdateUI(TAPDefaultDataView<TAPGetMessageListByRoomResponse> beforeView) {
        if (state != STATE.LOADED || vm.getLastTimestamp() >= vm.getLastBeforeTimestamp()) {
            return;
        }
        state = STATE.WORKING;
        new Thread(() -> {
            long timestamp;
            if (0 < vm.getMessageModels().size()) {
                // Use oldest message's create time as parameter
                timestamp = vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated();
            } else {
                // Use current timestamp as parameter if message list is empty
                timestamp = System.currentTimeMillis();
            }
            TAPDataManager.getInstance(instanceKey).getMessageListByRoomBefore(
                    vm.getRoom().getRoomID(),
                    timestamp,
                    MAX_ITEMS_PER_PAGE,
                    beforeView
            );
        }).start();
    }

    private final TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            List<TAPMessageEntity> responseMessages = new ArrayList<>();  // Entities to be saved to database
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
            int visibleCount = 0;

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    if (message != null) {
                        messageBeforeModels.addAll(addBeforeTextMessage(message));
                        responseMessages.add(TAPMessageEntity.fromMessageModel(message));
                        updateMessageMentionIndexes(message);

                        if (message.getIsHidden() == null || !message.getIsHidden()) {
                            visibleCount++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Check if room has more messages
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;

            vm.setLastBeforeTimestamp(vm.getLastTimestamp());
            if (0 < messageBeforeModels.size()) {
                vm.setLastTimestamp(messageBeforeModels.get(messageBeforeModels.size() - 1).getCreated());
            }

            // Sort adapter items according to timestamp
            TAPUtils.mergeSort(messageBeforeModels, ASCENDING);

            String previousDate = "";
            TAPMessageModel previousMessage = null;
            if (null != messageAdapter && !messageAdapter.getItems().isEmpty()) {
                int offset = 1;
                while (null == previousMessage) {
                    if (messageAdapter.getItems().size() < offset) {
                        break;
                    }
                    previousMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
                    if ((null != previousMessage.getIsHidden() && previousMessage.getIsHidden()) ||
                            previousMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_DATE_SEPARATOR
                    ) {
                        previousMessage = null;
                        offset++;
                    }
                }
                if (null != previousMessage) {
                    previousDate = TAPTimeFormatter.formatDate(previousMessage.getCreated());
                }
            }
            for (TAPMessageModel message : messageBeforeModels) {
                if ((null == message.getIsHidden() || !message.getIsHidden()) &&
                        message.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.formatDate(message.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        int index = messageBeforeModels.contains(previousMessage) ?
                                messageBeforeModels.indexOf(previousMessage) + 1 : 0;
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), index);
                    }
                    previousDate = currentDate;
                    previousMessage = message;
                }
            }

            insertDateSeparators(dateSeparators, dateSeparatorIndex, messageBeforeModels);

            vm.setVisibleMessageBubbleCount(vm.getVisibleMessageBubbleCount() + visibleCount);

            int currentVisibleCount = visibleCount;
            runOnUiThread(() -> {
                if (messageBeforeModels.size() > 0) {
                    showMessageList();
                }
                hideLoadingOlderMessagesIndicator();

                if (!(
                        0 < messageAdapter.getItems().size() &&
                        (
                            ROOM_REMOVE_PARTICIPANT.equals(messageAdapter.getItems().get(0).getAction()) &&
                            TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getTarget().getTargetID())
                        ) ||
                        (
                            0 < messageAdapter.getItems().size() &&
                            DELETE_ROOM.equals(messageAdapter.getItems().get(0).getAction())
                        ) ||
                        (
                            0 < messageAdapter.getItems().size() &&
                            LEAVE_ROOM.equals(messageAdapter.getItems().get(0).getAction()) &&
                            TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getUser().getUserID())
                        )
                    )
                ) {
                    showMessageList();
                }

                // Add messages to last index
                messageAdapter.addOlderMessagesFromApi(messageBeforeModels);

                vm.setMessageModels(messageAdapter.getItems());
                if (null != vm.getTappedMessageLocalID()) {
                    scrollToMessage(vm.getTappedMessageLocalID());
                }

                setRecyclerViewAnimator();

                if (state == STATE.DONE) {
                    insertDateSeparatorToLastIndex(false);
                    updateMessageDecoration();
                }
                else {
                    if (state == STATE.LOADED) {
                        rvMessageList.addOnScrollListener(endlessScrollListener);
                    }
                    if (vm.getVisibleMessageBubbleCount() < MAX_ITEMS_PER_PAGE &&  currentVisibleCount < (MAX_ITEMS_PER_PAGE / 2)) {
                        // Load more messages if half or more loaded items are hidden
                        loadMessagesFromDatabase();
                    }
                }
            });

            TAPDataManager.getInstance(instanceKey).insertToDatabase(responseMessages, false, new TAPDatabaseListener<TAPMessageEntity>() {});
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage() != null ? error.getMessage() : "");
        }

        @Override
        public void onError(String errorMessage) {
            setRecyclerViewAnimator();
            hideLoadingOlderMessagesIndicator();
        }

        private void setRecyclerViewAnimator() {
            if (null == rvMessageList.getItemAnimator()) {
                // Set default item animator for recycler view
                new Handler().postDelayed(() ->
                        rvMessageList.post(() ->
                                rvMessageList.setItemAnimator(messageAnimator)), 200L);
            }
        }
    };

    private void markMessageAsRead(List<String> readMessageIds) {
        if (null == readMessageIds || readMessageIds.isEmpty()) {
            return;
        }
        new Thread(() -> {
            //TAPMessageStatusManager.getInstance(instanceKey).addUnreadList(vm.getRoom().getRoomID(), readMessageIds.size());
            TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(readMessageIds);
        }).start();
    }

    private void insertDateSeparators(LinkedHashMap<String, TAPMessageModel> dateSeparators,
                                      LinkedHashMap<String, Integer> dateSeparatorIndex,
                                      List<TAPMessageModel> insertToList) {
        if (dateSeparators.isEmpty()) {
            return;
        }
        int separatorCount = 0;
        for (Map.Entry<String, TAPMessageModel> entry : dateSeparators.entrySet()) {
            Integer baseIndex = dateSeparatorIndex.get(entry.getKey());
            if (null != baseIndex) {
                int index = baseIndex + separatorCount;
                if (index <= insertToList.size()) {
                    insertToList.add(index, entry.getValue());
                    separatorCount++;
                }
            }
        }
        vm.getDateSeparators().putAll(dateSeparators);
    }

    private void insertDateSeparatorToLastIndex(boolean notifyDataSet) {
        if (null == messageAdapter || messageAdapter.getItems().isEmpty()) {
            return;
        }
        TAPMessageModel firstMessage = null;
        int offset = 1;
        while (null == firstMessage) {
            if (messageAdapter.getItems().size() < offset) {
                return;
            }
            firstMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
            if ((null != firstMessage.getIsHidden() && firstMessage.getIsHidden()) ||
                    firstMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                    firstMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                    firstMessage.getType() == TYPE_DATE_SEPARATOR
            ) {
                firstMessage = null;
                offset++;
            }
        }
        TAPMessageModel dateSeparator = vm.generateDateSeparator(this, firstMessage);
        vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
        vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), messageAdapter.getItems().size());
        runOnUiThread(() -> {
            messageAdapter.addItem(messageAdapter.getItems().size(), dateSeparator);
            if (notifyDataSet) {
                // Notify all to fix message bubble duplicated when After API is called on DONE state
                messageAdapter.notifyDataSetChanged();
            } else {
                messageAdapter.notifyItemInserted(messageAdapter.getItems().indexOf(dateSeparator));
            }
        });
    }

    /**
     * =========================================================================================== *
     * FILE TRANSFER
     * =========================================================================================== *
     */

    private void startFileDownload(TAPMessageModel message) {
        if (!TAPUtils.hasPermissions(this, TAPUtils.getStoragePermissions(true))) {
            // Request storage permission
            vm.setPendingDownloadMessage(message);
            ActivityCompat.requestPermissions(
                this,
                TAPUtils.getStoragePermissions(true),
                PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            );
        }
        else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message);
        }
    }

    private void showDownloadFileDialog() {
        // Prompt download if file does not exist
        if (null == vm.getOpenedFileMessage()) {
            return;
        }
        if (null != vm.getOpenedFileMessage().getData()) {
            String fileId = (String) vm.getOpenedFileMessage().getData().get(FILE_ID);
            String fileUrl = (String) vm.getOpenedFileMessage().getData().get(FILE_URL);
            if (null != fileUrl) {
                fileUrl = TAPUtils.getUriKeyFromUrl(fileUrl);
            }
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.getRoom().getRoomID(), fileId);
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.getRoom().getRoomID(), fileUrl);
        }
        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getOpenedFileMessage()));
        new TapTalkDialog.Builder(TapUIChatActivity.this)
                .setTitle(getString(R.string.tap_error_could_not_find_file))
                .setMessage(getString(R.string.tap_error_redownload_file))
                .setCancelable(true)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setPrimaryButtonListener(v -> startFileDownload(vm.getOpenedFileMessage()))
                .show();
    }

    private void restartFailedDownloads() {
        if (TAPFileDownloadManager.getInstance(instanceKey).hasFailedDownloads() &&
                TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
            // Notify chat bubbles with failed download
            for (String localID : TAPFileDownloadManager.getInstance(instanceKey).getFailedDownloads()) {
                if (vm.getMessagePointer().containsKey(localID)) {
                    runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))));
                }
            }
            //TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads();
        }
    }

    /**
     * =========================================================================================== *
     * OTHERS
     * =========================================================================================== *
     */

//    private void showRoomIsUnavailableState() {
//        new DeleteRoomAsync().execute(vm.getRoom().getRoomID());
//        runOnUiThread(() -> {
//            tvMessage.setText(getResources().getString(R.string.tap_group_unavailable));
//            flRoomUnavailable.setVisibility(View.VISIBLE);
//            flMessageList.setVisibility(View.GONE);
//            clEmptyChat.setVisibility(View.GONE);
//            clChatComposerAndHistory.setVisibility(View.GONE);
//            if (null != vRoomImage) {
//                vRoomImage.setClickable(false);
//            }
//        });
//    }

//    private void markMessageAsRead(TAPMessageModel readMessage) {
//        new Thread(() -> {
//            if (null != readMessage.getIsRead() && !readMessage.getIsRead()) {
//                TAPMessageStatusManager.getInstance(instanceKey).addUnreadListByOne(readMessage.getRoom().getRoomID());
//                TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(readMessage);
//            }
//        }).start();
//    }

    private void getStarredMessageIds() {
        if (vm.isStarredIdsLoaded()) {
            return;
        }
//        vmsetStarredIdsLoaded(false);
        TapCoreMessageManager.getInstance(instanceKey).getStarredMessageIds(vm.getRoom().getRoomID(), new TapCoreGetStringArrayListener() {
            @Override
            public void onSuccess(@NonNull ArrayList<String> arrayList) {
                super.onSuccess(arrayList);
                vm.setStarredIdsLoaded(true);
                vm.setStarredMessageIds(arrayList);
                messageAdapter.setStarredMessageIds(arrayList);
                if (vm.isPinnedIdsLoaded()) {
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(@Nullable String errorCode, @Nullable String errorMessage) {
                super.onError(errorCode, errorMessage);
                vm.setStarredIdsLoaded(true);
                if (vm.isPinnedIdsLoaded()) {
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getPinnedMessageIds() {
        if (vm.isPinnedIdsLoaded()) {
            return;
        }
//        vm.setPinnedIdsLoaded(false);
        TapCoreMessageManager.getInstance(instanceKey).getPinnedMessageIDs(vm.getRoom().getRoomID(), new TapCoreGetStringArrayListener() {
            @Override
            public void onSuccess(@NonNull ArrayList<String> arrayList) {
                super.onSuccess(arrayList);
                vm.setPinnedIdsLoaded(true);
                vm.setPinnedMessageIds(arrayList);
                messageAdapter.setPinnedMessageIds(arrayList);
                clPinnedIndicator.setSize(vm.getPinnedMessageIds().size());
                if (vm.isStarredIdsLoaded()) {
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(@Nullable String errorCode, @Nullable String errorMessage) {
                super.onError(errorCode, errorMessage);
                vm.setPinnedIdsLoaded(true);
                if (vm.isStarredIdsLoaded()) {
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void getPinnedMessages(String messageId) {
        TapCoreMessageManager.getInstance(instanceKey).getPinnedMessages(vm.getRoom().getRoomID(), vm.getPinnedMessagesPageNumber(), PAGE_SIZE, new TapCoreGetOlderMessageListener() {
            @Override
            public void onSuccess(List<TAPMessageModel> messages, Boolean hasMoreData) {
                super.onSuccess(messages, hasMoreData);
                TAPMessageModel newestPinnedMessage = null;
                TAPMessageModel shownMessage = null;
                if (vm.getPinnedMessagesPageNumber() == 1) {
                    if (!messages.isEmpty()) {
                        newestPinnedMessage = messages.get(0);
                        vm.setPinnedMessages(messages);
                        shownMessage = vm.getPinnedMessages().get(vm.getPinnedMessageIndex());
                    } else {
                        vm.clearPinnedMessages();
                    }
                    TAPDataManager.getInstance(instanceKey).saveNewestPinnedMessage(vm.getRoom().getRoomID(), newestPinnedMessage);
                } else {
                    if (!messages.isEmpty()) {
                        for (int messageIndex = 0; messageIndex < messages.size(); messageIndex++)  {
                            if (!vm.getPinnedMessages().contains(messages.get(messageIndex))) {
                                vm.addPinnedMessage(messages.get(messageIndex));
                            }
                        }
                        if (!messageId.isEmpty()) {
                            runOnUiThread(() -> {
                                for (int index = 0; index < vm.getPinnedMessages().size(); index++) {
                                    if (vm.getPinnedMessages().get(index).getMessageID() != null && vm.getPinnedMessages().get(index).getMessageID().equals(messageId)) {
                                        vm.addPinnedMessageId(index, messageId);
                                        messageAdapter.setPinnedMessageIds(vm.getPinnedMessageIds());
                                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(vm.getPinnedMessages().get(index).getLocalID())));
                                        break;
                                    }
                                }
                            });
                        }
                    }
                    shownMessage = vm.getPinnedMessages().get(vm.getPinnedMessageIndex());
                }
                vm.setHasMorePinnedMessages(hasMoreData);
                if (vm.isHasMorePinnedMessages()) {
                    vm.setPinnedMessagesPageNumber(vm.getPinnedMessagesPageNumber() + 1);
                }
                vm.setLoadPinnedMessages(false);
                setPinnedMessage(shownMessage);
            }
        });
    }

    private TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void onSuccess(TAPAddContactResponse response) {
            TAPUserModel newContact = response.getUser().setUserAsContact();
            TAPContactManager.getInstance(instanceKey).updateUserData(newContact);
        }
    };

    private TapCoreGetContactListener unblockUserView = new TapCoreGetContactListener() {
        @Override
        public void onSuccess(TAPUserModel user) {
            hideLoadingPopup();
            ArrayList<String> blockedUserIDs = TAPDataManager.getInstance(instanceKey).getBlockedUserIds();
            blockedUserIDs.remove(vm.getOtherUserID());
            TAPDataManager.getInstance(instanceKey).saveBlockedUserIds(blockedUserIDs);
            setRoomState();
        }

        @Override
        public void onError(String errorCode, String errorMessage) {
            hideLoadingPopup();
            showErrorDialog(getString(R.string.tap_error), errorMessage);
        }
    };

    private View.OnClickListener llDeleteGroupClickListener = v ->  {
        String title;
        String message;
        if (TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(vm.getRoom().getRoomID())) {
            title = getString(R.string.tap_unpin_and_delete_chat);
            message = getString(R.string.tap_sure_unpin_delete_conversation);
        } else {
            title = getString(R.string.tap_delete_chat);
            message = getString(R.string.tap_sure_delete_conversation);
        }
        new TapTalkDialog(new TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_delete_for_me))
                .setPrimaryButtonListener(view -> {
                    showDeleteRoomLoading();
                    TapCoreChatRoomManager.getInstance(instanceKey).deleteAllChatRoomMessages(vm.getRoom().getRoomID(), new TapCommonListener() {
                        @Override
                        public void onSuccess(String successMessage) {
                            super.onSuccess(successMessage);
                            runOnUiThread(() -> {
                                hideDeleteRoomLoading();
                                vm.setDeleteGroup(true);
                                closeActivity();
                            });
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            super.onError(errorCode, errorMessage);
                            runOnUiThread(() -> {
                                hideDeleteRoomLoading();
                                new TapTalkDialog(new TapTalkDialog.Builder(TapUIChatActivity.this)
                                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                                    .setTitle(getString(R.string.tap_fail_delete_chatroom))
                                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                    .setPrimaryButtonListener(v -> {})
                                    .setCancelable(false))
                                    .show();
                            });
                        }
                    });
                })
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener(secondaryView -> {}))
                .show();
    };

    private void showDeleteRoomLoading() {
        runOnUiThread(() -> {
            tvDeleteChat.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            pbDelete.setVisibility(View.VISIBLE);
            llButtonDeleteChat.setEnabled(false);
        });
    }

    private void hideDeleteRoomLoading() {
        runOnUiThread(() -> {
            tvDeleteChat.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.VISIBLE);
            pbDelete.setVisibility(View.GONE);
            llButtonDeleteChat.setEnabled(true);
        });
    }

    private class DeleteRoomAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... roomIDs) {
            TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(roomIDs[0], new TAPDatabaseListener() {
                @Override
                public void onDeleteFinished() {
                    super.onDeleteFinished();
                    TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(roomIDs[0], new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            super.onDeleteFinished();
                            if (!TAPGroupManager.Companion.getInstance(instanceKey).getRefreshRoomList())
                                TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
                        }
                    });
                }
            });
            return null;
        }
    }

    private TapAudioListener audioListener = new TapAudioListener() {
        @Override
        public void onPrepared() {

        }

        @Override
        public void onSeekComplete() {

        }

        @Override
        public void onPlayComplete() {
//            setFinishedRecordingState();
//            seekBar.setProgress(0);

        }
    };

    private void showSelectState() {
        vm.setSelectState(true);
        vRoomImage.setEnabled(false);
        clForward.setVisibility(View.VISIBLE);
        ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
        String forwardCountText = vm.getSelectedMessages().size() + "/" + MAX_FORWARD_COUNT +" " + getString(R.string.tap_selected);
        tvForwardCount.setText(forwardCountText);
        messageAdapter.notifyDataSetChanged();
        if (clQuote.getVisibility() == View.VISIBLE) {
            clQuote.setVisibility(View.GONE);
        }
        hideKeyboards();
        etChat.setText("");
        rvMessageList.disableSwipe();

        if (null != customNavigationBarFragment) {
            customNavigationBarFragment.onShowMessageSelection(vm.getSelectedMessages());
        }
    }

    private void hideSelectState() {
        vm.setSelectState(false);
        vRoomImage.setEnabled(true);
        clForward.setVisibility(View.GONE);
        ivButtonBack.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
        vm.clearSelectedMessages();
        messageAdapter.notifyDataSetChanged();
        rvMessageList.enableSwipe();

        if (null != customNavigationBarFragment) {
            customNavigationBarFragment.onHideMessageSelection();
        }
    }

    private void forwardMessages() {
        TAPForwardPickerActivity.start(TapUIChatActivity.this, instanceKey, vm.getSelectedMessages());
    }

    private void setOtherUserModel(TAPUserModel otherUserModel) {
        if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            vm.setOtherUserModel(TAPChatManager.getInstance(instanceKey).getActiveUser());
        } else {
            vm.setOtherUserModel(otherUserModel);
        }
    }

    private void getMessageReadCount(TAPMessageModel message) {
        if (vm.getRoom().getType() != TYPE_PERSONAL &&
            message.getIsRead() != null && message.getIsRead() &&
            TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled() &&
            !TapUI.getInstance(instanceKey).isReadStatusHidden()
        ) {
            TapCoreMessageManager.getInstance(instanceKey).getMessageTotalRead(message.getMessageID(), new TapCoreGetIntegerListener() {
                @Override
                public void onSuccess(int readCount) {
                    if (readCount > 0) {
                        vm.getMessageReadCountMap().put(message.getMessageID(), readCount);
                        int index = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
                        if (index >= 0) {
                            messageAdapter.notifyItemChanged(index);
                        }
                    }
                }

                @Override
                public void onError(@Nullable String errorCode, @Nullable String errorMessage) {

                }
            });
        }
    }

//    private SwipeBackLayout.SwipeBackInterface swipeInterface = new SwipeBackLayout.SwipeBackInterface() {
//        @Override
//        public void onSwipeBack() {
//            TAPUtils.dismissKeyboard(TapUIChatActivity.this);
//        }
//
//        @Override
//        public void onSwipeToFinishActivity() {
//            if (isTaskRoot()) {
//                // Trigger listener callback if no other activity is open
//                for (TapListener listener : TapTalk.getTapTalkListeners(instanceKey)) {
//                    listener.onTaskRootChatRoomClosed(TapUIChatActivity.this);
//                }
//            }
//        }
//    };
}
