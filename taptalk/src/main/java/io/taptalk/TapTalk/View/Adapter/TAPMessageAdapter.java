package io.taptalk.TapTalk.View.Adapter;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_DELETED_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_DELETED_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_FILE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_FILE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_LOADING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_LOCATION_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_LOCATION_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_UNREAD_STATUS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VOICE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VOICE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.LinkPreviewImageLoaded;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.PlayPauseVoiceNote;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_GIF;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.VIDEO_MP4;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DESCRIPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IS_PLAYING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.TITLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.WIDTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LINK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VOICE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteFileType.VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.CREATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_ADD_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_DEMOTE_ADMIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_PROMOTE_ADMIN;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_REMOVE_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPForwardFromModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPQuoteModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;

public class TAPMessageAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseChatViewHolder> {

    private static final String TAG = TAPMessageAdapter.class.getSimpleName();
    private String instanceKey = "";
    private TAPChatListener chatListener;
    private List<TAPMessageModel> pendingAnimationMessages, animatingMessages;
    private ArrayList<String> starredMessageIds = new ArrayList<>();
    private ArrayList<String> pinnedMessageIds = new ArrayList<>();
    private TAPMessageModel highlightedMessage;
    private TAPUserModel myUserModel;
    private Map<String, List<Integer>> messageMentionIndexes;
    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private RequestManager glide;
    private float initialTranslationX = TAPUtils.dpToPx(-22);
    private long defaultAnimationTime = 200L;
    private RoomType roomType = RoomType.DEFAULT;
    private boolean isMediaPlaying, isSeeking;
    private Uri voiceUri;
    private MediaPlayer audioPlayer = null;
    private Timer durationTimer;
    private int duration, pausedPosition;
    public String lastLocalId = "";
    private SeekBar playingSeekbar;
    private HashMap<String, BitmapDrawable> linkPreviewImages;

    private TAPChatViewModel vm;

    public enum RoomType {
        DEFAULT, STARRED, PINNED, DETAIL
    }

    public TAPMessageAdapter(
            String instanceKey,
            RequestManager glide,
            TAPChatListener chatListener,
            TAPChatViewModel vm
    ) {
        myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
        this.instanceKey = instanceKey;
        this.chatListener = chatListener;
        this.glide = glide;
        this.messageMentionIndexes = vm.getMessageMentionIndexes();
        pendingAnimationMessages = new ArrayList<>();
        animatingMessages = new ArrayList<>();
        this.starredMessageIds = vm.getStarredMessageIds();
        this.pinnedMessageIds = vm.getPinnedMessageIds();
        this.vm = vm;
    }

    public TAPMessageAdapter(
            String instanceKey,
            RequestManager glide,
            TAPChatListener chatListener,
            TAPChatViewModel vm,
            RoomType roomType
    ) {
        myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
        this.instanceKey = instanceKey;
        this.chatListener = chatListener;
        this.glide = glide;
        this.vm = vm;
        this.messageMentionIndexes = vm.getMessageMentionIndexes();
        this.roomType = roomType;
        pendingAnimationMessages = new ArrayList<>();
        animatingMessages = new ArrayList<>();
    }

    public TAPMessageAdapter(
            String instanceKey,
            RequestManager glide,
            TAPChatViewModel vm
    ) {
        myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
        this.instanceKey = instanceKey;
        this.chatListener = new TAPChatListener() {};
        this.glide = glide;
        this.vm = vm;
        this.messageMentionIndexes = vm.getMessageMentionIndexes();
        this.roomType = RoomType.DETAIL;
        pendingAnimationMessages = new ArrayList<>();
        animatingMessages = new ArrayList<>();
    }

    @NonNull
    @Override
    public TAPBaseChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.tap_cell_chat_bubble_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.tap_cell_chat_bubble_text_left, viewType);
            case TYPE_BUBBLE_IMAGE_RIGHT:
                return new ImageVH(parent, R.layout.tap_cell_chat_bubble_image_right, viewType);
            case TYPE_BUBBLE_IMAGE_LEFT:
                return new ImageVH(parent, R.layout.tap_cell_chat_bubble_image_left, viewType);
            case TYPE_BUBBLE_VIDEO_RIGHT:
                return new VideoVH(parent, R.layout.tap_cell_chat_bubble_image_right, viewType);
            case TYPE_BUBBLE_VIDEO_LEFT:
                return new VideoVH(parent, R.layout.tap_cell_chat_bubble_image_left, viewType);
            case TYPE_BUBBLE_FILE_RIGHT:
                return new FileVH(parent, R.layout.tap_cell_chat_bubble_file_right, viewType);
            case TYPE_BUBBLE_FILE_LEFT:
                return new FileVH(parent, R.layout.tap_cell_chat_bubble_file_left, viewType);
            case TYPE_BUBBLE_VOICE_RIGHT:
                return new VoiceVH(parent, R.layout.tap_cell_chat_bubble_voice_right, viewType);
            case TYPE_BUBBLE_VOICE_LEFT:
                return new VoiceVH(parent, R.layout.tap_cell_chat_bubble_voice_left, viewType);
            case TYPE_BUBBLE_LOCATION_RIGHT:
                return new LocationVH(parent, R.layout.tap_cell_chat_bubble_location_right, viewType);
            case TYPE_BUBBLE_LOCATION_LEFT:
                return new LocationVH(parent, R.layout.tap_cell_chat_bubble_location_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                ProductVH prodHolder = new ProductVH(parent, R.layout.tap_cell_chat_bubble_product_list);
                prodHolder.setIsRecyclable(false);
                return prodHolder;
            case TYPE_BUBBLE_UNREAD_STATUS:
                return new BasicVH(parent, R.layout.tap_cell_unread_status);
            case TYPE_BUBBLE_LOADING:
                return new LoadingVH(parent, R.layout.tap_cell_chat_loading);
            case TYPE_EMPTY:
                return new EmptyVH(parent, R.layout.tap_cell_empty);
            case TYPE_BUBBLE_DELETED_RIGHT:
                return new DeletedVH(parent, R.layout.tap_cell_chat_bubble_deleted_right, viewType);
            case TYPE_BUBBLE_DELETED_LEFT:
                return new DeletedVH(parent, R.layout.tap_cell_chat_bubble_deleted_left, viewType);
            case TYPE_BUBBLE_SYSTEM_MESSAGE:
                return new SystemMessageVH(parent, R.layout.tap_cell_chat_system_message);
            case TYPE_BUBBLE_DATE_SEPARATOR:
                return new DateSeparatorVH(parent, R.layout.tap_cell_chat_date_separator);
            default:
                TAPBaseCustomBubble customBubble = TAPCustomBubbleManager.getInstance(instanceKey).getCustomBubbleMap().get(viewType);
                if (null != customBubble) {
                    return customBubble.createCustomViewHolder(parent, this, myUserModel, customBubble.getCustomBubbleListener());
                }
                return new TextVH(parent, R.layout.tap_cell_chat_bubble_text_left, viewType);
//                return new UnsupportedVH(parent, R.layout.tap_cell_chat_bubble_text_left, viewType);
//                return new EmptyVH(parent, R.layout.tap_cell_empty);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            TAPMessageModel messageModel = getItemAt(position);
            int messageType = 0;
            if (null != messageModel && null != messageModel.getIsHidden() && messageModel.getIsHidden()) {
                // Return empty layout if item is hidden or deleted in saved messages room
                return TYPE_EMPTY;
            } else if (null != messageModel && null != messageModel.getIsDeleted() && messageModel.getIsDeleted() && isMessageFromMySelf(messageModel)) {
                return TYPE_BUBBLE_DELETED_RIGHT;
            } else if (null != messageModel && null != messageModel.getIsDeleted() && messageModel.getIsDeleted()) {
                return TYPE_BUBBLE_DELETED_LEFT;
            } else if (null != messageModel) {
                messageType = messageModel.getType();
            }

            switch (messageType) {
                case TYPE_LINK:
                case TYPE_TEXT:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    } else {
                        return TYPE_BUBBLE_TEXT_LEFT;
                    }
                case TYPE_IMAGE:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_IMAGE_RIGHT;
                    } else {
                        return TYPE_BUBBLE_IMAGE_LEFT;
                    }
                case TYPE_VIDEO:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_VIDEO_RIGHT;
                    } else {
                        return TYPE_BUBBLE_VIDEO_LEFT;
                    }
                case TYPE_FILE:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_FILE_RIGHT;
                    } else {
                        return TYPE_BUBBLE_FILE_LEFT;
                    }
                case TYPE_VOICE:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_VOICE_RIGHT;
                    } else {
                        return TYPE_BUBBLE_VOICE_LEFT;
                    }
                case TYPE_LOCATION:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_LOCATION_RIGHT;
                    } else {
                        return TYPE_BUBBLE_LOCATION_LEFT;
                    }
                case TYPE_PRODUCT:
                    return TYPE_BUBBLE_PRODUCT_LIST;
                case TYPE_SYSTEM_MESSAGE:
                    return TYPE_BUBBLE_SYSTEM_MESSAGE;
                case TYPE_UNREAD_MESSAGE_IDENTIFIER:
                    return TYPE_BUBBLE_UNREAD_STATUS;
                case TYPE_LOADING_MESSAGE_IDENTIFIER:
                    return TYPE_BUBBLE_LOADING;
                case TYPE_DATE_SEPARATOR:
                    return TYPE_BUBBLE_DATE_SEPARATOR;
                default:
                    TAPBaseCustomBubble customBubble = TAPCustomBubbleManager.getInstance(instanceKey).getCustomBubbleMap().get(messageType);
                    if (null != customBubble) {
                        return messageType;
                    }
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    } else {
                        return TYPE_BUBBLE_TEXT_LEFT;
                    }
            }
        } catch (Exception e) {
            return TYPE_LOG;
        }
    }

    private boolean isMessageFromMySelf(TAPMessageModel messageModel) {
        if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            // forwarded message in saved messages room use left bubble
            return messageModel.getForwardFrom() == null || messageModel.getForwardFrom().getLocalID().isEmpty();
        } else
            return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }

    public class TextVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        public FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivBubbleHighlight;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private TextView tvAvatarLabel;
        private TextView tvUserName;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ImageView ivStarMessage;
        private ImageView ivPinMessage;
        private View vSeparator;
        private View vBubbleArea;
        private ImageView ivSelect;
        private TextView tvEdited;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ConstraintLayout clLink;
        private TextView tvLinkTitle;
        private TextView tvLinkContent;
        private TAPRoundedCornerImageView rcivLinkImage;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private Group gReadCount;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvEdited = itemView.findViewById(R.id.tv_edited);
            clLink = itemView.findViewById(R.id.cl_link);
            tvLinkTitle = itemView.findViewById(R.id.tv_link_title);
            tvLinkContent = itemView.findViewById(R.id.tv_link_content);
            rcivLinkImage = itemView.findViewById(R.id.rciv_link_image);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            gReadCount = itemView.findViewById(R.id.g_read_count);

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            if (item.getIsMessageEdited() != null && item.getIsMessageEdited()) {
                tvEdited.setVisibility(View.VISIBLE);
            } else {
                tvEdited.setVisibility(View.GONE);
            }
            setMessageBodyText(tvMessageBody, item, item.getBody());
            setLinkPreview(item);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setSelectedState(item, ivSelect, vBubbleArea);
            setOriginalMessageButton(item, ibOriginalMessage);
            //expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);
            checkAndAnimateHighlight(item, ivBubbleHighlight);

            markMessageAsRead(item, myUserModel);
            if (!isBubbleTapOnly()) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            }
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);
            setPinnedIcon(item.getMessageID(), ivPinMessage);
            setReadCountIcon(gReadCount, tvReadCount, item.getMessageID());

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            if (!isBubbleTapOnly()) {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    onStatusImageClicked(item);
                });
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    chatListener.onOutsideClicked(item);
                });
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        private void setLinkPreview(TAPMessageModel item) {
            if (!TapUI.getInstance(instanceKey).isLinkPreviewInMessageEnabled() || item.getData() == null) {
                clLink.setVisibility(View.GONE);
                clLink.setOnLongClickListener(null);
            }
            else {
                String title = (String) item.getData().get(TITLE);
                if (title != null) {
                    String description = (String) item.getData().get(DESCRIPTION);
                    String url = (String) item.getData().get(URL);
                    String image = (String) item.getData().get(IMAGE);
                    clLink.setVisibility(View.VISIBLE);
                    if (!title.isEmpty()) {
                        tvLinkTitle.setText(title);
                        tvLinkTitle.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvLinkTitle.setVisibility(View.GONE);
                    }

                    if (description == null || description.isEmpty()) {
                        tvLinkContent.setVisibility(View.GONE);
                    }
                    else {
                        tvLinkContent.setVisibility(View.VISIBLE);
                        tvLinkContent.setText(description);
                    }

                    if (image == null || image.isEmpty()) {
                        rcivLinkImage.setImageDrawable(null);
                        rcivLinkImage.setVisibility(View.GONE);
                    }
                    else {
//                        rcivLinkImage.setVisibility(View.VISIBLE);
//                        glide.load(image).fitCenter().into(rcivLinkImage);
                        loadLinkPreviewImage(rcivLinkImage, image);
                    }
                    clLink.setOnClickListener(view -> {
                        if (itemView.getContext() != null && itemView.getContext() instanceof Activity) {
                            TAPUtils.openUrl(instanceKey, (Activity) itemView.getContext(), url);
                        }
                    });
                    clLink.setOnLongClickListener(v -> {
                        Intent intent = new Intent(LongPressLink);
                        intent.putExtra(MESSAGE, item);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, url);
                        LocalBroadcastManager.getInstance(itemView.getContext()).sendBroadcast(intent);
                        return true;
                    });
                }
                else {
                    clLink.setVisibility(View.GONE);
                    clLink.setOnLongClickListener(null);
                }
            }
        }

        private void loadLinkPreviewImage(ImageView imageView, String url) {
            String key = TAPUtils.getUriKeyFromUrl(url);
            if (linkPreviewImages == null) {
                linkPreviewImages = new HashMap<>();
            }
            BitmapDrawable image = linkPreviewImages.get(key);
            if (image == null) {
                Glide.with(TapTalk.appContext).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        // Put empty image to map
                        linkPreviewImages.put(key, new BitmapDrawable());
                        imageView.setImageDrawable(null);
                        imageView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        // Image loaded
                        BitmapDrawable image = new BitmapDrawable(appContext.getResources(), bitmap);
                        linkPreviewImages.put(key, image);
                        imageView.setImageDrawable(image);
                        imageView.setVisibility(View.VISIBLE);
                        LocalBroadcastManager.getInstance(itemView.getContext()).sendBroadcast(new Intent(LinkPreviewImageLoaded));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
            }
            else if (image.getBitmap() != null && image.getBitmap().getByteCount() > 0) {
                // Image already saved
                imageView.setImageDrawable(image);
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                // Hide image
                imageView.setImageDrawable(null);
                imageView.setVisibility(View.GONE);
            }
        }
    }

    public class ImageVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwardedQuote;
        private ConstraintLayout clQuote;
        private ConstraintLayout clForwarded;
        private LinearLayout llTimestampIconImage;
        public FrameLayout flBubble;
        private FrameLayout flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivImageBody;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private ImageView ivMessageStatus;
        private ImageView ivMessageStatusImage;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivButtonProgress;
        private ImageView ivBubbleHighlight;
        private TextView tvAvatarLabel;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageTimestampImage;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ProgressBar pbProgress;
        private ImageView ivStarMessage;
        private ImageView ivStarMessageBody;
        private ImageView ivPinMessage;
        private ImageView ivPinMessageBody;
        private View vSeparator;
        private View vBubbleArea;
        private ImageView ivSelect;
        private TextView tvEdited;
        private TextView tvEditedBody;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private ImageView ivReadCountBody;
        private TextView tvReadCountBody;
        private Group gReadCountBody;

        private TAPMessageModel obtainedItem;
        private Drawable thumbnail;
//        private String cacheKey;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            llTimestampIconImage = itemView.findViewById(R.id.ll_timestamp_icon_image);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivImageBody = itemView.findViewById(R.id.rciv_image);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageTimestampImage = itemView.findViewById(R.id.tv_message_timestamp_image);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivStarMessageBody = itemView.findViewById(R.id.iv_star_message_body);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            ivPinMessageBody = itemView.findViewById(R.id.iv_pin_body);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvEdited = itemView.findViewById(R.id.tv_edited);
            tvEditedBody = itemView.findViewById(R.id.tv_edited_body);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            ivReadCountBody = itemView.findViewById(R.id.iv_read_count_body);
            tvReadCountBody = itemView.findViewById(R.id.tv_read_count_body);
            gReadCountBody = itemView.findViewById(R.id.g_read_count_body);

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivMessageStatusImage = itemView.findViewById(R.id.iv_message_status_image);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            obtainedItem = item;
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, null);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            tvMessageTimestampImage.setText(item.getMessageStatusText());
            setImageViewButtonProgress(item);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setProgress(item);
            setImageData(item, position);
            fixBubbleMarginForGroupRoom(item, flBubble);
            setSelectedState(item, ivSelect, vBubbleArea);
            setOriginalMessageButton(item, ibOriginalMessage);

            markMessageAsRead(item, myUserModel);
            if (!isBubbleTapOnly()) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            } else {
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
            flBubble.setOnClickListener(v -> {
                chatListener.onBubbleTapped(item);
                chatListener.onOutsideClicked(item);
            });
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivImageBody, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void openImageDetailPreview(TAPMessageModel message) {
            HashMap<String, Object> messageData = message.getData();
            if (messageData == null) {
                return;
            }
            String mediaType = TAPUtils.getMimeTypeFromMessage(message);
            if (mediaType == null || !mediaType.contains("image")) {
                mediaType = IMAGE_JPEG;
                TAPMessageModel messageCopy = message.copyMessageModel();
                messageData.put(MEDIA_TYPE, mediaType);
                messageCopy.setData(messageData);
                TAPImageDetailPreviewActivity.start(itemView.getContext(), instanceKey, messageCopy, rcivImageBody);
            }
            else if (!mediaType.equals(IMAGE_GIF)) {
                // FIXME: PREVIEW GIF CRASHES (Cannot cast GifDrawable to BitmapDrawable)
                TAPImageDetailPreviewActivity.start(itemView.getContext(), instanceKey, message, rcivImageBody);
            }
        }

        private void setProgress(TAPMessageModel item) {
            String localID = item.getLocalID();
            Integer uploadProgressValue = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(localID);
            Integer downloadProgressValue = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID());
            if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                flProgress.setVisibility(View.VISIBLE);
                pbProgress.setVisibility(View.GONE);
            } else if ((null == uploadProgressValue || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressValue) {
                flProgress.setVisibility(View.GONE);
//                flBubble.setForeground(null);
            } else {
                flProgress.setVisibility(View.VISIBLE);
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
                if (null != uploadProgressValue) {
                    pbProgress.setProgress(uploadProgressValue);
                } else {
                    pbProgress.setProgress(downloadProgressValue);
                }
            }
        }

        private void setImageData(TAPMessageModel item, int position) {
//            rcivImageBody.setImageDrawable(null);
//            cacheKey = null;

            if (null == item.getData()) {
                return;
            }

            Activity activity = (Activity) itemView.getContext();
            Number widthDimension = (Number) item.getData().get(WIDTH);
            Number heightDimension = (Number) item.getData().get(HEIGHT);
            String imageUri = (String) item.getData().get(FILE_URI);
            String imageUrl = (String) item.getData().get(FILE_URL);
            String imageCaption = (String) item.getData().get(CAPTION);
            String fileID = (String) item.getData().get(FILE_ID);

            if (null == imageUrl || imageUrl.isEmpty()) {
                imageUrl = (String) item.getData().get(URL);
            }

            if (null == thumbnail) {
                thumbnail = new BitmapDrawable(
                        itemView.getContext().getResources(),
                        TAPFileUtils.decodeBase64(
                                (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                        item.getData().get(THUMBNAIL))));
                if (thumbnail.getIntrinsicHeight() <= 0) {
                    // Set placeholder image if thumbnail fails to load
                    thumbnail = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_grey_e4);
                }
            }

            if (null != imageCaption && !imageCaption.isEmpty()) {
                // Show caption
//                rcivImageBody.setBottomLeftRadius(TAPUtils.dpToPx(2));
//                rcivImageBody.setBottomRightRadius(TAPUtils.dpToPx(2));
                //tvMessageBody.setText(imageCaption);
                setMessageBodyText(tvMessageBody, item, imageCaption);
                if (!isBubbleTapOnly()) {
                    setLinkDetection(itemView.getContext(), item, tvMessageBody);
                }
                tvMessageBody.setVisibility(View.VISIBLE);
                llTimestampIconImage.setVisibility(View.GONE);
                tvMessageTimestamp.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.VISIBLE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessageBody);
                setPinnedIcon(item.getMessageID(), ivPinMessageBody);
                setReadCountIcon(gReadCountBody, tvReadCountBody, item.getMessageID());
                setEditedMessage(item.getIsMessageEdited() != null && item.getIsMessageEdited(), tvEditedBody, tvEdited, ivStarMessage);
            } else {
                // Hide caption
//                rcivImageBody.setBottomLeftRadius(TAPUtils.dpToPx(13));
//                rcivImageBody.setBottomRightRadius(TAPUtils.dpToPx(13));
                tvMessageBody.setVisibility(View.GONE);
                llTimestampIconImage.setVisibility(View.VISIBLE);
                tvMessageTimestamp.setVisibility(View.GONE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.GONE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessage);
                setPinnedIcon(item.getMessageID(), ivPinMessage);
                setReadCountIcon(ivReadCount, tvReadCount, item.getMessageID());
                setEditedMessage(item.getIsMessageEdited() != null && item.getIsMessageEdited(), tvEdited, tvEditedBody, ivStarMessageBody);
            }

            if (null != widthDimension && null != heightDimension && widthDimension.intValue() > 0 && heightDimension.intValue() > 0) {
                rcivImageBody.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
            } else {
                rcivImageBody.setImageDimensions(rcivImageBody.getMaxWidth(), rcivImageBody.getMaxWidth());
            }

            // Load thumbnail when download is not in progress
//            if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                rcivImageBody.setImageDrawable(thumbnail);
                fixImageOrVideoViewSize(item, rcivImageBody, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
//            }

//            if (null != imageUrl && !imageUrl.isEmpty()) {
//                // Load image from URL
//                glide.load(imageUrl)
//                        .transition(DrawableTransitionOptions.withCrossFade(100))
//                        .apply(new RequestOptions()
//                                .placeholder(thumbnail)
//                                .centerCrop())
//                        .listener(imageBodyListener)
//                        .into(rcivImageBody);
//                rcivImageBody.setOnClickListener(v -> {
//                    openImageDetailPreview(item);
//                });
//            } else if (null != fileID && !fileID.isEmpty()) {
//                new Thread(() -> {
//                    BitmapDrawable cachedImage = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(fileID);
//                    if (null != cachedImage) {
//                        // Load image from cache
//                        activity.runOnUiThread(() -> {
//                            glide.load(cachedImage)
//                                    .transition(DrawableTransitionOptions.withCrossFade(100))
//                                    .apply(new RequestOptions()
//                                            .placeholder(thumbnail)
//                                            .centerCrop())
//                                    .listener(imageBodyListener)
//                                    .into(rcivImageBody);
//                            rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
//                        });
//                    } else {
//                        activity.runOnUiThread(() -> rcivImageBody.setOnClickListener(v -> {
//                        }));
//                        if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
//                            // Download image
//                            if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
//                                TAPFileDownloadManager.getInstance(instanceKey).downloadImage(TapTalk.appContext, item);
//                            } else {
//                                activity.runOnUiThread(() -> flProgress.setVisibility(View.GONE));
//                                TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(item.getLocalID());
//                            }
//                        }
//                    }
//                }).start();
            if ((null != imageUrl && !imageUrl.isEmpty()) || (null != fileID && !fileID.isEmpty())) {
                BitmapDrawable cachedImage = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(imageUrl, fileID);
                if (null != cachedImage) {
                    // Load image from cache
                    activity.runOnUiThread(() -> {
                        glide.clear(rcivImageBody);
                        glide.load(cachedImage)
                                .transition(DrawableTransitionOptions.withCrossFade(100))
                                .apply(new RequestOptions()
                                        .placeholder(thumbnail)
                                        .centerCrop())
                                .listener(imageBodyListener)
                                .into(rcivImageBody);
                        rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
                    });
                } else if (null != imageUrl && !imageUrl.isEmpty()) {
                    // Load image from URL
                    String finalImageUrl = imageUrl;
                    activity.runOnUiThread(() -> {
//                        cacheKey = TAPUtils.getUriKeyFromUrl(finalImageUrl);
                        glide.clear(rcivImageBody);
                        glide.load(finalImageUrl)
                                .transition(DrawableTransitionOptions.withCrossFade(100))
                                .apply(new RequestOptions()
                                        .placeholder(thumbnail)
                                        .centerCrop())
                                .listener(imageBodyListener)
                                .into(rcivImageBody);
                        rcivImageBody.setOnClickListener(v -> {
                            openImageDetailPreview(item);
                        });
                    });
                } else {
                    activity.runOnUiThread(() -> rcivImageBody.setOnClickListener(v -> {
                    }));
                    if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                        // Download image
                        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
                            TAPFileDownloadManager.getInstance(instanceKey).downloadImage(TapTalk.appContext, item);
                        } else {
                            activity.runOnUiThread(() -> flProgress.setVisibility(View.GONE));
                            TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(item.getLocalID());
                        }
                    }
                }
            }
            else if (null != imageUri && !imageUri.isEmpty()) {
                // Message is not sent to server, load image from Uri
                rcivImageBody.setOnClickListener(v -> {
                });
                Number size = (Number) item.getData().get(SIZE);
                if (null != size && size.longValue() > TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize()) {
                    activity.runOnUiThread(() -> {
//                        if (isMessageFromMySelf(item)) {
//                            flBubble.setForeground(bubbleOverlayRight);
//                        } else {
//                            flBubble.setForeground(bubbleOverlayLeft);
//                        }
                        rcivImageBody.setImageDrawable(thumbnail);
                        fixImageOrVideoViewSize(item, rcivImageBody, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
                    });
                } else {
                    activity.runOnUiThread(() -> {
//                        if (isMessageFromMySelf(item)) {
//                            flBubble.setForeground(bubbleOverlayRight);
//                        } else {
//                            flBubble.setForeground(bubbleOverlayLeft);
//                        }
//                        cacheKey = TAPUtils.getUriKeyFromUrl(imageUri);
                        glide.clear(rcivImageBody);
                        glide.load(imageUri)
                                .transition(DrawableTransitionOptions.withCrossFade(100))
                                .apply(new RequestOptions()
                                        .placeholder(thumbnail)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                                .listener(imageBodyListener)
                                .into(rcivImageBody);
                    });
                }
            }
        }

        private RequestListener<Drawable> imageBodyListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                fixImageOrVideoViewSize(obtainedItem, rcivImageBody, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
                clContainer.requestLayout();
//                if (cacheKey != null && !cacheKey.isEmpty() && resource instanceof BitmapDrawable) {
//                    TAPCacheManager.getInstance(itemView.getContext()).addBitmapDrawableToCache(cacheKey, (BitmapDrawable) resource);
//                }
                return false;
            }
        };

        private void setImageViewButtonProgress(TAPMessageModel item) {
            if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                // Failed to send
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownloadWhite)));
                flProgress.setOnClickListener(v -> resendMessage(item));
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                tvMessageStatus.setVisibility(View.VISIBLE);
            } else if ((null == TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(item.getLocalID())
                    && null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID()))
                    || null != TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                // Not downloaded
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_orange));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownloadWhite)));
                flProgress.setOnClickListener(v -> {});
                tvMessageStatus.setVisibility(View.GONE);
            } else if ((null == TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(item.getLocalID()) || (null != item.getIsSending() && !item.getIsSending()))
                    && null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                // Progress done
                flProgress.setVisibility(View.GONE);
            } else {
                // Uploading / Downloading
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownloadWhite)));
                flProgress.setOnClickListener(v -> {
                     if (isBubbleTapOnly()) {
                        chatListener.onOutsideClicked(item);
                     } else {
                         TAPDataManager.getInstance(instanceKey)
                                 .cancelUploadImage(itemView.getContext(), item.getLocalID());
                     }
                });
                if (isMessageFromMySelf(item) && null != item.getIsSending() && item.getIsSending()) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_sending));
                } else {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_downloading));
                }
                tvMessageStatus.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            boolean noCaption = true;
            if (null != message.getData() && null != message.getData().get(CAPTION)) {
                String caption = (String) message.getData().get(CAPTION);
                if (null != caption && !caption.isEmpty()) {
                    noCaption = false;
                }
            }
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending, noCaption);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }
    }

    public class VideoVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwardedQuote;
        private ConstraintLayout clQuote;
        private ConstraintLayout clForwarded;
        private LinearLayout llTimestampIconImage;
        public FrameLayout flBubble;
        private FrameLayout flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivVideoThumbnail;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private ImageView ivMessageStatus;
        private ImageView ivMessageStatusImage;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivButtonProgress;
        private ImageView ivBubbleHighlight;
        private TextView tvAvatarLabel;
        private TextView tvMediaInfo;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageTimestampImage;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ProgressBar pbProgress;
        private ImageView ivStarMessage;
        private ImageView ivStarMessageBody;
        private ImageView ivPinMessage;
        private ImageView ivPinMessageBody;
        private View vSeparator;
        private View vBubbleArea;
        private ImageView ivSelect;
        private TextView tvEdited;
        private TextView tvEditedBody;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private ImageView ivReadCountBody;
        private TextView tvReadCountBody;
        private Group gReadCountBody;

        private TAPMessageModel obtainedItem;
        private Uri videoUri;
        private Drawable thumbnail;

        VideoVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            llTimestampIconImage = itemView.findViewById(R.id.ll_timestamp_icon_image);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivVideoThumbnail = itemView.findViewById(R.id.rciv_image);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            tvMediaInfo = itemView.findViewById(R.id.tv_media_info);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageTimestampImage = itemView.findViewById(R.id.tv_message_timestamp_image);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivStarMessageBody = itemView.findViewById(R.id.iv_star_message_body);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            ivPinMessageBody = itemView.findViewById(R.id.iv_pin_body);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvEdited = itemView.findViewById(R.id.tv_edited);
            tvEditedBody = itemView.findViewById(R.id.tv_edited_body);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            ivReadCountBody = itemView.findViewById(R.id.iv_read_count_body);
            tvReadCountBody = itemView.findViewById(R.id.tv_read_count_body);
            gReadCountBody = itemView.findViewById(R.id.g_read_count_body);

            if (bubbleType == TYPE_BUBBLE_VIDEO_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivMessageStatusImage = itemView.findViewById(R.id.iv_message_status_image);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            obtainedItem = item;
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, null);
            }

            tvMediaInfo.setVisibility(View.VISIBLE);
            tvMessageTimestamp.setText(item.getMessageStatusText());
            tvMessageTimestampImage.setText(item.getMessageStatusText());

            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setSelectedState(item, ivSelect, vBubbleArea);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setVideoProgress(item, position);
            fixBubbleMarginForGroupRoom(item, flBubble);
            setOriginalMessageButton(item, ibOriginalMessage);

            markMessageAsRead(item, myUserModel);
            if (!isBubbleTapOnly()) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            } else {
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
            flBubble.setOnClickListener(v -> {
                chatListener.onBubbleTapped(item);
                chatListener.onOutsideClicked(item);
            });
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivVideoThumbnail, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void setVideoProgress(TAPMessageModel item, int position) {
//            rcivVideoThumbnail.setImageDrawable(null);

            if (null == item.getData()) {
                return;
            }

            String localID = item.getLocalID();
            Number duration = (Number) item.getData().get(DURATION);
            Number size = (Number) item.getData().get(SIZE);
            Number widthDimension = (Number) item.getData().get(WIDTH);
            Number heightDimension = (Number) item.getData().get(HEIGHT);
            String videoCaption = (String) item.getData().get(CAPTION);
            String dataUri = (String) item.getData().get(FILE_URI);

            Integer uploadProgressPercent = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(localID);
            videoUri = null != dataUri ? Uri.parse(dataUri) : TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item);

            if (null == thumbnail) {
                thumbnail = new BitmapDrawable(
                        itemView.getContext().getResources(),
                        TAPFileUtils.decodeBase64(
                                (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                        item.getData().get(THUMBNAIL))));
                if (thumbnail.getIntrinsicHeight() <= 0) {
                    // Set placeholder image if thumbnail fails to load
                    thumbnail = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_grey_e4);
                }
            }

            if (null != videoCaption && !videoCaption.isEmpty()) {
                // Show caption
//                rcivImageBody.setBottomLeftRadius(TAPUtils.dpToPx(2));
//                rcivImageBody.setBottomRightRadius(TAPUtils.dpToPx(2));
                //tvMessageBody.setText(videoCaption);
                setMessageBodyText(tvMessageBody, item, videoCaption);
                if (!isBubbleTapOnly()) {
                    setLinkDetection(itemView.getContext(), item, tvMessageBody);
                }
                tvMessageBody.setVisibility(View.VISIBLE);
                llTimestampIconImage.setVisibility(View.GONE);
                tvMessageTimestamp.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.VISIBLE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessageBody);
                setPinnedIcon(item.getMessageID(), ivPinMessageBody);
                setReadCountIcon(gReadCountBody, tvReadCountBody, item.getMessageID());
                setEditedMessage(item.getIsMessageEdited() != null && item.getIsMessageEdited(), tvEditedBody, tvEdited, ivStarMessage);
            } else {
                // Hide caption
//                rcivVideoThumbnail.setBottomLeftRadius(TAPUtils.dpToPx(13));
//                rcivVideoThumbnail.setBottomRightRadius(TAPUtils.dpToPx(13));
                tvMessageBody.setVisibility(View.GONE);
                llTimestampIconImage.setVisibility(View.VISIBLE);
                tvMessageTimestamp.setVisibility(View.GONE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.GONE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessage);
                setPinnedIcon(item.getMessageID(), ivPinMessage);
                setReadCountIcon(ivReadCount, tvReadCount, item.getMessageID());
                setEditedMessage(item.getIsMessageEdited() != null && item.getIsMessageEdited(), tvEdited, tvEditedBody, ivStarMessageBody);
            }

            if (null != widthDimension && null != heightDimension && widthDimension.intValue() > 0 && heightDimension.intValue() > 0) {
                rcivVideoThumbnail.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
            } else {
                rcivVideoThumbnail.setImageDimensions(rcivVideoThumbnail.getMaxWidth(), rcivVideoThumbnail.getMaxWidth());
            }

            // Fix media info text width
//            rcivVideoThumbnail.post(() -> {
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) tvMediaInfo.getLayoutParams();
//                params.matchConstraintMaxWidth = rcivVideoThumbnail.getMeasuredWidth() - TAPUtils.dpToPx(16);
//                tvMediaInfo.setLayoutParams(params);
//            });

            // Load thumbnail when download is not in progress
//            if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID()) || null != dataUri) {
                rcivVideoThumbnail.setImageDrawable(thumbnail);
                fixImageOrVideoViewSize(item, rcivVideoThumbnail, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
//            }

            if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                // Message failed to send
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                if (size != null && size.longValue() > 0L) {
                    tvMediaInfo.setText(TAPUtils.getStringSizeLengthFile(size.longValue()));
                    tvMediaInfo.setVisibility(View.VISIBLE);
                } else {
                    tvMediaInfo.setText("");
                    tvMediaInfo.setVisibility(View.GONE);
                }
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownloadWhite)));
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
//                    flBubble.setForeground(bubbleOverlayRight);
                    rcivVideoThumbnail.setOnClickListener(v -> resendMessage(item));
                } else {
//                    flBubble.setForeground(bubbleOverlayLeft);
                    rcivVideoThumbnail.setOnClickListener(v -> downloadVideo(item));
                }
                glide.clear(rcivVideoThumbnail);
                glide.load(videoUri)
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .apply(new RequestOptions()
                                .placeholder(thumbnail)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .listener(videoThumbnailListener)
                        .into(rcivVideoThumbnail);
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent) && (null != videoUri ||
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(item))) {
                // Video has finished downloading or uploading
                if (duration != null && duration.longValue() > 0L) {
                    tvMediaInfo.setText(TAPUtils.getMediaDurationString(duration.intValue(), duration.intValue()));
                    tvMediaInfo.setVisibility(View.VISIBLE);
                } else {
                    tvMediaInfo.setText("");
                    tvMediaInfo.setVisibility(View.GONE);
                }
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_play_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFilePlayMedia)));
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item));
                new Thread(() -> {
                    BitmapDrawable videoThumbnail = null;
                    String key = "";
                    String fileID = (String) item.getData().get(FILE_ID);
                    if (null != fileID && !fileID.isEmpty()) {
                        key = fileID;
                        videoThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(key);
                    }
                    if (null == videoThumbnail) {
                        String fileUrl = (String) item.getData().get(FILE_URL);
                        if (null != fileUrl && !fileUrl.isEmpty()) {
                            key = TAPUtils.getUriKeyFromUrl(fileUrl);
                            videoThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(key);
                        }
                    }
                    if (null == videoThumbnail) {
                        // Get full-size thumbnail from Uri
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        try {
//                            Uri parsedUri = TAPFileUtils.parseFileUri(videoUri);
//                            retriever.setDataSource(itemView.getContext(), parsedUri);
                            retriever.setDataSource(itemView.getContext(), videoUri);
                            videoThumbnail = new BitmapDrawable(itemView.getContext().getResources(), retriever.getFrameAtTime());
                            TAPCacheManager.getInstance(itemView.getContext()).addBitmapDrawableToCache(key, videoThumbnail);
                        } catch (Exception e) {
                            e.printStackTrace();
                            videoThumbnail = (BitmapDrawable) thumbnail;
                        }
                    }
                    // Load full-size thumbnail from cache
                    if (null != videoThumbnail) {
                        BitmapDrawable finalVideoThumbnail = videoThumbnail;
                        ((Activity) itemView.getContext()).runOnUiThread(() -> {
                            glide.clear(rcivVideoThumbnail);
                            glide.load(finalVideoThumbnail)
                                    .transition(DrawableTransitionOptions.withCrossFade(100))
                                    .apply(new RequestOptions()
                                            .placeholder(thumbnail)
                                            .centerCrop())
                                    .listener(videoThumbnailListener)
                                    .into(rcivVideoThumbnail);
                            rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item));
                        });
                    }
                }).start();
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent)) {
                // Video is not downloaded
                String videoSize = null == size ? "" : TAPUtils.getStringSizeLengthFile(size.longValue());
                String videoDuration = null == duration ? "" : TAPUtils.getMediaDurationString(duration.intValue(), duration.intValue());
                if ((size != null && size.longValue() > 0L) || (duration != null && duration.longValue() > 0L)) {
                    tvMediaInfo.setText(String.format("%s%s%s", videoSize, (videoSize.isEmpty() || videoDuration.isEmpty()) ? "" : " - ", videoDuration));
                    tvMediaInfo.setVisibility(View.VISIBLE);
                } else {
                    tvMediaInfo.setText("");
                    tvMediaInfo.setVisibility(View.GONE);
                }
                if (TAPFileDownloadManager.getInstance(instanceKey).getFailedDownloads().contains(item.getLocalID())) {
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownloadWhite)));
                } else {
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_orange));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownloadWhite)));
                }
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> downloadVideo(item));
            } else {
                // File is downloading or uploading
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownloadWhite)));
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
                if (null != uploadProgressPercent) {
                    // Uploading
                    Long uploadProgressBytes = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressBytes(localID);
                    tvMediaInfo.setText(TAPUtils.getFileDisplayProgress(item, uploadProgressBytes));
                    if (tvMediaInfo.getText().length() > 0) {
                        tvMediaInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvMediaInfo.setVisibility(View.GONE);
                    }
                    pbProgress.setProgress(uploadProgressPercent);
                    rcivVideoThumbnail.setOnClickListener(v -> cancelUpload(item));
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_sending));
                } else {
                    // Downloading
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(localID);
                    tvMediaInfo.setText(TAPUtils.getFileDisplayProgress(item, downloadProgressBytes));
                    if (tvMediaInfo.getText().length() > 0) {
                        tvMediaInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvMediaInfo.setVisibility(View.GONE);
                    }
                    pbProgress.setProgress(downloadProgressPercent);
                    rcivVideoThumbnail.setOnClickListener(v -> cancelDownload(item));
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_downloading));
                }
                tvMessageStatus.setVisibility(View.VISIBLE);
            }
        }

        private RequestListener<Drawable> videoThumbnailListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                fixImageOrVideoViewSize(obtainedItem, rcivVideoThumbnail, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
                return false;
            }
        };

        private void downloadVideo(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void openVideoPlayer(TAPMessageModel message) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(message);
            } else if (null != message.getData()) {
                Uri videoUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(message);
                if (null == videoUri || !TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(message)) {
                    // Prompt download
                    this.videoUri = null;
                    String fileID = (String) message.getData().get(FILE_ID);
                    TAPCacheManager.getInstance(TapTalk.appContext).removeFromCache(fileID);
                    notifyItemChanged(getLayoutPosition());
                    new TapTalkDialog.Builder(itemView.getContext())
                            .setTitle(itemView.getContext().getString(R.string.tap_error_could_not_find_file))
                            .setMessage(itemView.getContext().getString(R.string.tap_error_redownload_file))
                            .setCancelable(true)
                            .setPrimaryButtonTitle(itemView.getContext().getString(R.string.tap_ok))
                            .setSecondaryButtonTitle(itemView.getContext().getString(R.string.tap_cancel))
                            .setPrimaryButtonListener(v -> downloadVideo(message))
                            .show();
                } else {
                    // Open video
                    HashMap<String, Object> messageData = message.getData();
                    String mediaType = TAPUtils.getMimeTypeFromMessage(message);
                    if (mediaType != null && !mediaType.contains("video")) {
                        mediaType = VIDEO_MP4;
                        TAPMessageModel messageCopy = message.copyMessageModel();
                        messageData.put(MEDIA_TYPE, mediaType);
                        messageCopy.setData(messageData);
                        TAPVideoPlayerActivity.start(itemView.getContext(), instanceKey, videoUri, messageCopy);
                    }
                    else {
                        TAPVideoPlayerActivity.start(itemView.getContext(), instanceKey, videoUri, message);
                    }
                }
            }
            chatListener.onBubbleTapped(message);
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            boolean noCaption = true;
            if (null != message.getData() && null != message.getData().get(CAPTION)) {
                String caption = (String) message.getData().get(CAPTION);
                if (null != caption && !caption.isEmpty()) {
                    noCaption = false;
                }
            }
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending, noCaption);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
        }
    }

    public class FileVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        public FrameLayout flBubble;
        private FrameLayout flFileIcon;
        private CircleImageView civAvatar;
        private ImageView ivFileIcon;
        private ImageView ivMessageStatus;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivBubbleHighlight;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private TextView tvAvatarLabel;
        private TextView tvUserName;
        private TextView tvFileName;
        private TextView tvFileInfo;
        private TextView tvFileInfoDummy;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ProgressBar pbProgress;
        private ImageView ivStarMessage;
        private ImageView ivPinMessage;
        private View vSeparator;
        private View vBubbleArea;
        private ImageView ivSelect;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private Group gReadCount;

        private Uri fileUri;

        FileVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flFileIcon = itemView.findViewById(R.id.fl_file_icon);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileInfo = itemView.findViewById(R.id.tv_file_info);
            tvFileInfoDummy = itemView.findViewById(R.id.tv_file_info_dummy);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            gReadCount = itemView.findViewById(R.id.g_read_count);

            if (bubbleType == TYPE_BUBBLE_FILE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    flFileIcon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_file_button_left_ripple));
                } else {
                    flFileIcon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_file_button_left));
                }
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    flFileIcon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_file_button_right_ripple));
                } else {
                    flFileIcon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_file_button_right));
                }
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setSelectedState(item, ivSelect, vBubbleArea);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setFileProgress(item);
            setFileCaption(item);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);
            setPinnedIcon(item.getMessageID(), ivPinMessage);
            setReadCountIcon(gReadCount, tvReadCount, item.getMessageID());
            setOriginalMessageButton(item, ibOriginalMessage);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            if (!isBubbleTapOnly()) {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    flFileIcon.performClick();
                });
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    chatListener.onOutsideClicked(item);
                });
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        private void setFileProgress(TAPMessageModel item) {
            if (null == item.getData()) {
                return;
            }
            String localID = item.getLocalID();
            Integer uploadProgressPercent = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(localID);
//            String key = TAPUtils.getUriKeyFromMessage(item);
            fileUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item);

//            String space = isMessageFromMySelf(item) ? RIGHT_BUBBLE_SPACE_APPEND : LEFT_BUBBLE_SPACE_APPEND;
            tvFileName.setText(TAPUtils.getFileDisplayName(item));
//            tvFileInfoDummy.setText(String.format("%s%s", TAPUtils.getFileDisplayDummyInfo(itemView.getContext(), item), space));
            tvFileInfoDummy.setText(TAPUtils.getFileDisplayDummyInfo(itemView.getContext(), item));

            if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                // Message failed to send
                tvFileInfo.setText(TAPUtils.getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(),
                                isMessageFromMySelf(item) ?
                                R.color.tapIconFileRetryUploadDownloadPrimary :
                                R.color.tapIconFileRetryUploadDownloadWhite)));
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                if (isMessageFromMySelf(item)) {
                    flFileIcon.setOnClickListener(v -> resendMessage(item));
                } else {
                    flFileIcon.setOnClickListener(v -> downloadFile(item));
                }
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent) && (null != fileUri ||
                    TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(item))) {
                // File has finished downloading or uploading
                tvMessageStatus.setText(item.getMessageStatusText());
                tvFileInfo.setText(TAPUtils.getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_documents_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(),
                                isMessageFromMySelf(item) ?
                                        R.color.tapIconFilePrimary :
                                        R.color.tapIconFileWhite)));
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> openFile(item));
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent)) {
                // File is not downloaded
                tvFileInfo.setText(TAPUtils.getFileDisplayInfo(item));
                if (TAPFileDownloadManager.getInstance(instanceKey).getFailedDownloads().contains(item.getLocalID())) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                    ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                    ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(
                            ContextCompat.getColor(itemView.getContext(),
                                    isMessageFromMySelf(item) ?
                                            R.color.tapIconFileRetryUploadDownloadPrimary :
                                            R.color.tapIconFileRetryUploadDownloadWhite)));
                } else {
                    tvMessageStatus.setText(item.getMessageStatusText());
                    ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_orange));
                    ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownloadWhite)));
                    ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(
                            ContextCompat.getColor(itemView.getContext(),
                                    isMessageFromMySelf(item) ?
                                            R.color.tapIconFileUploadDownloadPrimary :
                                            R.color.tapIconFileUploadDownloadWhite)));
                }
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> downloadFile(item));
            } else {
                // File is downloading or uploading
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(),
                                isMessageFromMySelf(item) ?
                                        R.color.tapIconFileCancelUploadDownloadPrimary :
                                        R.color.tapIconFileCancelUploadDownloadWhite)));
                pbProgress.setMax(100);
                pbProgress.setProgressDrawable(ContextCompat.getDrawable(itemView.getContext(),
                        isMessageFromMySelf(item) ?
                                R.drawable.tap_file_circular_progress_primary :
                                R.drawable.tap_file_circular_progress_white));
                pbProgress.setVisibility(View.VISIBLE);
                tvMessageStatus.setText(item.getMessageStatusText());
                if (null != uploadProgressPercent) {
                    Long uploadProgressBytes = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressBytes(localID);
                    tvFileInfo.setText(TAPUtils.getFileDisplayProgress(item, uploadProgressBytes));
                    pbProgress.setProgress(uploadProgressPercent);
                    flFileIcon.setOnClickListener(v -> cancelUpload(item));
                } else {
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(localID);
                    tvFileInfo.setText(TAPUtils.getFileDisplayProgress(item, downloadProgressBytes));
                    pbProgress.setProgress(downloadProgressPercent);
                    flFileIcon.setOnClickListener(v -> cancelDownload(item));
                }
            }
        }

        private void setFileCaption(TAPMessageModel item) {
            if (item.getData() != null) {
                String caption = (String) item.getData().get(CAPTION);
                if (caption != null && !caption.isEmpty()) {
                    setMessageBodyText(tvMessageBody, item, caption);
                    tvMessageBody.setVisibility(View.VISIBLE);
                } else {
                    tvMessageBody.setVisibility(View.GONE);
                }
            } else {
                tvMessageBody.setVisibility(View.GONE);
            }
        }

        private void downloadFile(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void openFile(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(OpenFile);
                intent.putExtra(MESSAGE, item);
                intent.putExtra(FILE_URI, fileUri);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }
    }

    public class VoiceVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        public FrameLayout flBubble;
        private FrameLayout flVoiceIcon;
        private CircleImageView civAvatar;
        private ImageView ivVoiceIcon;
        private ImageView ivMessageStatus;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivBubbleHighlight;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private TextView tvAvatarLabel;
        private TextView tvUserName;
        private TextView tvVoiceTime;
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ProgressBar pbProgress;
        private ImageView ivStarMessage;
        private ImageView ivPinMessage;
        private View vSeparator;
        private SeekBar seekBar;
        private View vBubbleArea;
        private ImageView ivSelect;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private Group gReadCount;

        private Uri fileUri;

        VoiceVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flVoiceIcon = itemView.findViewById(R.id.fl_voice_icon);
            ivVoiceIcon = itemView.findViewById(R.id.iv_voice_icon);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            tvVoiceTime = itemView.findViewById(R.id.tv_voice_time);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            vSeparator = itemView.findViewById(R.id.v_separator);
            seekBar = itemView.findViewById(R.id.seek_bar);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            gReadCount = itemView.findViewById(R.id.g_read_count);

            if (bubbleType == TYPE_BUBBLE_VOICE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setSelectedState(item, ivSelect, vBubbleArea);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setFileProgress(item);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);
            setPinnedIcon(item.getMessageID(), ivPinMessage);
            setReadCountIcon(gReadCount, tvReadCount, item.getMessageID());
            setOriginalMessageButton(item, ibOriginalMessage);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            if (!isBubbleTapOnly()) {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    flVoiceIcon.performClick();
                });
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> {
                    chatListener.onBubbleTapped(item);
                    chatListener.onOutsideClicked(item);
                });
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        private void setFileProgress(TAPMessageModel item) {
            if (null == item.getData()) {
                return;
            }
            String localID = item.getLocalID();
            Integer uploadProgressPercent = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(localID);
//            String key = TAPUtils.getUriKeyFromMessage(item);
            fileUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(item);

//            String space = isMessageFromMySelf(item) ? RIGHT_BUBBLE_SPACE_APPEND : LEFT_BUBBLE_SPACE_APPEND;
//            tvFileName.setText(TAPUtils.getFileDisplayName(item));
//            tvFileInfoDummy.setText(String.format("%s%s", TAPUtils.getFileDisplayDummyInfo(itemView.getContext(), item), space));
//            tvFileInfoDummy.setText(TAPUtils.getFileDisplayDummyInfo(itemView.getContext(), item));

            Number duration = (Number) item.getData().get(DURATION);
            String durationString = "00:00";
            if (duration != null && duration.longValue() > 0L) {
                durationString = TAPUtils.getMediaDurationString(duration.intValue(), duration.intValue());
            }
            if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                // Message failed to send
                tvVoiceTime.setText(durationString);
                ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white_thin));
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                if (isMessageFromMySelf(item)) {
                    // TODO: 18/04/22 handle resend voice note MU
                    flVoiceIcon.setOnClickListener(v -> resendMessage(item));
                } else {
                    flVoiceIcon.setOnClickListener(v -> downloadFile(item));
                }
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent) && (null != fileUri || TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(item))) {
                // File has finished downloading or uploading
                tvMessageStatus.setText(item.getMessageStatusText());
                tvVoiceTime.setText(durationString);
                ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_play_white));
                pbProgress.setVisibility(View.GONE);
                // TODO: 18/04/22 handle play pause voice note MU
                // TODO: 18/04/22 handle seekbar logic MU
                flVoiceIcon.setOnClickListener(v -> playPauseVoiceNote(seekBar, (Activity) itemView.getContext(), tvVoiceTime, ivVoiceIcon, fileUri, item, item.getLocalID(), duration != null ? duration.intValue() : 1));
                if (!lastLocalId.equals(item.getLocalID())) {
                    seekBar.getThumb().mutate().setAlpha(255);
                    seekBar.setOnSeekBarChangeListener(null);
                    seekBar.setProgress(0);
                    seekBar.setEnabled(false);
                    tvVoiceTime.setText(durationString);
                } else {
                    seekBar.setProgress(pausedPosition);
                    playingSeekbar = seekBar;
                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener(tvVoiceTime));
                    if (audioPlayer != null && audioPlayer.isPlaying()) {
                        seekBar.setEnabled(true);
                        ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_pause_white));
                    } else {
                        seekBar.setEnabled(false);
                    }
                }
            } else if (((null == uploadProgressPercent || (null != item.getIsSending() && !item.getIsSending()))
                    && null == downloadProgressPercent)) {
                // File is not downloaded
                tvVoiceTime.setText(durationString);
                if (TAPFileDownloadManager.getInstance(instanceKey).getFailedDownloads().contains(item.getLocalID())) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                    ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white_thin));
                } else {
                    tvMessageStatus.setText(item.getMessageStatusText());
                    ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_orange));
                }
                seekBar.getThumb().mutate().setAlpha(0);
                seekBar.setEnabled(false);
                pbProgress.setVisibility(View.GONE);
                flVoiceIcon.setOnClickListener(v -> downloadFile(item));
            } else {
                // File is downloading or uploading
                seekBar.getThumb().mutate().setAlpha(0);
                seekBar.setEnabled(false);
                ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                pbProgress.setMax(100);
                pbProgress.setVisibility(View.VISIBLE);
                tvMessageStatus.setText(item.getMessageStatusText());
                if (null != uploadProgressPercent) {
                    Long uploadProgressBytes = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressBytes(localID);
                    tvVoiceTime.setText(TAPUtils.getFileDisplayProgress(item, uploadProgressBytes));
                    pbProgress.setProgress(uploadProgressPercent);
                    flVoiceIcon.setOnClickListener(v -> cancelUpload(item));
                } else {
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(localID);
                    tvVoiceTime.setText(TAPUtils.getFileDisplayProgress(item, downloadProgressBytes));
                    pbProgress.setProgress(downloadProgressPercent);
                    flVoiceIcon.setOnClickListener(v -> cancelDownload(item));
                }
            }
        }

        private void downloadFile(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void playPauseVoiceNote(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri, TAPMessageModel item, String currentLocalId, int audioDuration) {
            // TODO: 18/04/22 play voice note MU
            if (isBubbleTapOnly()) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(PlayPauseVoiceNote);
                intent.putExtra(MESSAGE, item);
                intent.putExtra(IS_PLAYING, true);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                playBubbleVoiceNote(seekBar, activity, tvDuration, image, fileUri, currentLocalId, audioDuration);
            }
            chatListener.onBubbleTapped(item);
        }
    }

    public class LocationVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clBubbleTop;
        private ConstraintLayout clForwardedQuote;
        private ConstraintLayout clQuote;
        private ConstraintLayout clForwarded;
        public FrameLayout flBubble;
        private FrameLayout flMapViewContainer;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private ImageView ivMessageStatus;
        //private ImageView ivReply;
        private ImageView ivSending;
        private ImageView ivBubbleHighlight;
        private TextView tvAvatarLabel;
        private TextView tvUserName;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private View vMapBorder;
        private MapView mapView;
        private ImageView ivStarMessage;
        private ImageView ivPinMessage;
        private View vSeparator;
        private View vBubbleArea;
        private ImageView ivSelect;
        private ImageButton ibOriginalMessage;
        private Barrier barrier;
        private Space space;
        private ImageView ivReadCount;
        private TextView tvReadCount;
        private Group gReadCount;

        LocationVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flMapViewContainer = itemView.findViewById(R.id.fl_map_view_container);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            ivBubbleHighlight = itemView.findViewById(R.id.iv_bubble_highlight);
            //ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            vMapBorder = itemView.findViewById(R.id.v_map_border);
            mapView = itemView.findViewById(R.id.map_view);
            mapView.onCreate(new Bundle());
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vBubbleArea = itemView.findViewById(R.id.v_bubble_area);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivReadCount = itemView.findViewById(R.id.iv_read_count);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            gReadCount = itemView.findViewById(R.id.g_read_count);

            if (bubbleType == TYPE_BUBBLE_LOCATION_LEFT) {
                clBubbleTop = itemView.findViewById(R.id.cl_bubble_top);
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
                ibOriginalMessage = itemView.findViewById(R.id.ib_original_message);
                barrier = itemView.findViewById(R.id.barrier);
                space = itemView.findViewById(R.id.space);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            HashMap<String, Object> mapData = item.getData();
            if (null == item || null == mapData) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, space, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setSelectedState(item, ivSelect, vBubbleArea);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            fixBubbleMarginForGroupRoom(item, flBubble);
            setOriginalMessageButton(item, ibOriginalMessage);

            if ((null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty()) ||
                    (null != tvUserName && View.VISIBLE == tvUserName.getVisibility() && null != item.getRoom() && TYPE_GROUP == item.getRoom().getType())) {
                // Fix layout when quote/forward exists
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mapView.setOutlineProvider(null);
                }
                if (null != clBubbleTop) {
                    clBubbleTop.setVisibility(View.VISIBLE);
                }
                clForwardedQuote.setVisibility(View.VISIBLE);
                //vMapBorder.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_stroke_e4e4e4_1dp_insettop_insetbottom_1dp));
            } else {
                if (null != clBubbleTop) {
                    if (tvUserName.getVisibility() == View.VISIBLE) {
                        clBubbleTop.setVisibility(View.VISIBLE);
                    } else {
                        clBubbleTop.setVisibility(View.GONE);
                    }
                }
                clForwardedQuote.setVisibility(View.GONE);
            }
            setMapData(item);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int clipDrawableRes = isMessageFromMySelf(item) ? R.drawable.tap_bg_bubble_media_clip_mask_right : R.drawable.tap_bg_bubble_media_clip_mask_left;
                flMapViewContainer.setBackground(ContextCompat.getDrawable(itemView.getContext(), clipDrawableRes));
                flMapViewContainer.setClipToOutline(true);
            }

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), vMapBorder, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);
            setPinnedIcon(item.getMessageID(), ivPinMessage);
            setReadCountIcon(gReadCount, tvReadCount, item.getMessageID());

            vMapBorder.setOnClickListener(v -> {
                if (isBubbleTapOnly()) {
                    chatListener.onOutsideClicked(item);
                } else {
                    openMapDetail(mapData);
                }
            });
            if (isBubbleTapOnly()) {
                if (roomType == RoomType.STARRED) {
                    if (position != 0) {
                        vSeparator.setVisibility(View.VISIBLE);
                    } else {
                        vSeparator.setVisibility(View.GONE);
                    }
                }
            }
            flBubble.setOnClickListener(v -> {
                chatListener.onBubbleTapped(item);
                chatListener.onOutsideClicked(item);
            });
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            vBubbleArea.setOnClickListener(v -> chatListener.onMessageSelected(item));
            //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void setMapData(TAPMessageModel item) {
            HashMap<String, Object> mapData = item.getData();
            if (null == mapData || null == mapData.get(ADDRESS) || null == mapData.get(LATITUDE) || null == mapData.get(LONGITUDE)) {
                return;
            }
            setMessageBodyText(tvMessageBody, item, (String) mapData.get(ADDRESS));
            if (tvMessageBody.getText().length() == 0) {
                tvMessageBody.setVisibility(View.GONE);
            } else {
                tvMessageBody.setVisibility(View.VISIBLE);
            }
            try {
                mapView.getMapAsync(googleMap -> {
                    Number latitude = (Number) mapData.get(LATITUDE);
                    Number longitude = (Number) mapData.get(LONGITUDE);
                    if (null != latitude && null != longitude) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                latitude.doubleValue(),
                                longitude.doubleValue()), 16f));
                        googleMap.getUiSettings().setAllGesturesEnabled(false);
                        mapView.onResume();
                    }
                });
            } catch (Exception e) {
                mapView.onPause();
                e.printStackTrace();
            }
        }

        private void openMapDetail(HashMap<String, Object> mapData) {
            Number latitude = (Number) mapData.get(LATITUDE);
            Number longitude = (Number) mapData.get(LONGITUDE);
            if (null == latitude || null == longitude) {
                latitude = 0.0;
                longitude = 0.0;
            }
            TAPUtils.openMaps((Activity) itemView.getContext(), latitude.doubleValue(), longitude.doubleValue());
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }
    }

    private void fixBubbleMarginForGroupRoom(TAPMessageModel item, View flBubble) {
        if (isMessageFromMySelf(item) || !(flBubble.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) flBubble.getLayoutParams();
        if (item.getRoom().getType() == TYPE_GROUP || TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey)) {
            // Fix bubble margin on group room
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(TAPUtils.dpToPx(48));
            } else {
                params.rightMargin = TAPUtils.dpToPx(48);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(TAPUtils.dpToPx(82));
            } else {
                params.rightMargin = TAPUtils.dpToPx(82);
            }
        }
    }

    public class ProductVH extends TAPBaseChatViewHolder {

        RecyclerView rvProductList;
        TAPProductListAdapter adapter;
        private List<TAPProductModel> items;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (null != item.getData())
                items = TAPUtils.convertObject(item.getData().get("items")
                        , new TypeReference<List<TAPProductModel>>() {
                        });
            else items = new ArrayList<>();
            adapter = new TAPProductListAdapter(instanceKey, items, item, myUserModel, chatListener);
            markMessageAsRead(item, myUserModel);

            rvProductList.setAdapter(adapter);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            if (rvProductList.getItemDecorationCount() > 0) {
                rvProductList.removeItemDecorationAt(0);
            }
            rvProductList.addItemDecoration(new TAPHorizontalDecoration(
                    0, 0,
                    TAPUtils.dpToPx(16),
                    TAPUtils.dpToPx(8),
                    adapter.getItemCount(),
                    0, 0));
            OverScrollDecoratorHelper.setUpOverScroll(rvProductList, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        }
    }

    public class SystemMessageVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private TextView tv_message;

        SystemMessageVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            tv_message = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (null != item.getAction()) {
                String systemMessageAction = item.getAction() != null ?
                        item.getAction() : "";

                switch (systemMessageAction) {
                    case CREATE_ROOM:
                    case UPDATE_ROOM:
                    case DELETE_ROOM:
                    case LEAVE_ROOM:
                        item.setBody(TAPChatManager.getInstance(instanceKey).formattingSystemMessage(item));
                        break;

                    case ROOM_ADD_PARTICIPANT:
                    case ROOM_REMOVE_PARTICIPANT:
                    case ROOM_PROMOTE_ADMIN:
                    case ROOM_DEMOTE_ADMIN:
                        item.setBody(TAPChatManager.getInstance(instanceKey).formattingSystemMessage(item));
                        break;

                    default:
                        item.setBody(TAPChatManager.getInstance(instanceKey).formattingSystemMessage(item));
                }
            }
            markMessageAsRead(item, myUserModel);
            tv_message.setText(item.getBody());
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            enableLongPress(itemView.getContext(), clContainer, item);
        }
    }

    public class DateSeparatorVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private TextView tvDateSeparator;

        DateSeparatorVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            tvDateSeparator = itemView.findViewById(R.id.tv_date_indicator);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            markMessageAsRead(item, myUserModel);
            tvDateSeparator.setText(item.getBody());
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
        }
    }

    public class LogVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private TextView tvLogMessage;

        LogVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            tvLogMessage = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            tvLogMessage.setText(TAPUtils.toJsonString(item));
            //tvLogMessage.setText(item.getBody());
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            markMessageAsRead(item, myUserModel);
        }
    }

    public class LoadingVH extends TAPBaseChatViewHolder {

        private ImageView ivLoadingProgress;

        LoadingVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivLoadingProgress = itemView.findViewById(R.id.iv_loading_progress);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (null == ivLoadingProgress.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(itemView.getContext(), ivLoadingProgress);
            }
            markMessageAsRead(item, myUserModel);
        }
    }

    public class BasicVH extends TAPBaseChatViewHolder {

        BasicVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            markMessageAsRead(item, myUserModel);
        }
    }

    public class EmptyVH extends TAPBaseChatViewHolder {

        EmptyVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            markMessageAsRead(item, myUserModel);
            // TODO: 28 June 2019 CHECK MESSAGE STATUS FOR DELETED MESSAGE
        }
    }

    public class DeletedVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private CircleImageView civAvatar;
        private TextView tvAvatarLabel, tvUserName;

        DeletedVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);
            if (bubbleType == TYPE_BUBBLE_DELETED_LEFT) {
                clContainer = itemView.findViewById(R.id.cl_container);
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, null, null, civAvatar, null, tvAvatarLabel, tvUserName);
            }
            markMessageAsRead(item, myUserModel);
//            enableLongPress(itemView.getContext(), clContainer, item);
        }
    }

    public class UnsupportedVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        public FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus;
        private ImageView ivSending;
        private TextView tvAvatarLabel;
        private TextView tvUserName;
        private TextView tvMessageBody;
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private View vQuoteBackground;
        private ImageView ivStarMessage;
        private ImageView ivPinMessage;

        UnsupportedVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            ivPinMessage = itemView.findViewById(R.id.iv_pin);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, null, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            tvMessageBody.setText(R.string.tap_message_type_unsupported);
            clForwarded.setVisibility(View.GONE);
            clQuote.setVisibility(View.GONE);
            vQuoteBackground.setVisibility(View.GONE);

            markMessageAsRead(item, myUserModel);
            setStarredIcon(item.getMessageID(), ivStarMessage);
            setPinnedIcon(item.getMessageID(), ivPinMessage);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
        }

        @Override
        protected void onMessageSending(TAPMessageModel message) {
            showMessageAsSending(itemView, message, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageFailedToSend(TAPMessageModel message) {
            showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageSent(TAPMessageModel message) {
            showMessageAsSent(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageDelivered(TAPMessageModel message) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }

        @Override
        protected void onMessageRead(TAPMessageModel message) {
            showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
        }
    }

    private void showMessageAsSending(View itemView,
                                      TAPMessageModel item,
                                      FrameLayout flBubble,
                                      TextView tvMessageStatus,
                                      @Nullable ImageView ivMessageStatus,
                                      @Nullable ImageView ivSending) {
        showMessageAsSending(itemView, item, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending);
    }

    private void showMessageAsSending(View itemView,
                                      TAPMessageModel item,
                                      FrameLayout flBubble,
                                      TextView tvMessageStatus,
                                      @Nullable ImageView ivMessageStatus,
                                      @Nullable ImageView ivMessageStatusImage,
                                      @Nullable ImageView ivSending) {
        pendingAnimationMessages.add(item);
        flBubble.setTranslationX(initialTranslationX);
        if (null != ivSending) {
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            ivSending.setAlpha(1f);
        }
        if (null != ivMessageStatus) {
            ivMessageStatus.setVisibility(View.INVISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setVisibility(View.INVISIBLE);
        }
        if (item.getType() == TYPE_IMAGE || item.getType() == TYPE_VIDEO) {
            tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_sending));
            tvMessageStatus.setVisibility(View.VISIBLE);
        } else {
            tvMessageStatus.setVisibility(View.GONE);
        }
    }

    private void showMessageFailedToSend(View itemView,
                                         FrameLayout flBubble,
                                         TextView tvMessageStatus,
                                         @Nullable ImageView ivMessageStatus,
                                         @Nullable ImageView ivSending) {
        showMessageFailedToSend(itemView, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending);
    }

    private void showMessageFailedToSend(View itemView,
                                         FrameLayout flBubble,
                                         TextView tvMessageStatus,
                                         @Nullable ImageView ivMessageStatus,
                                         @Nullable ImageView ivMessageStatusImage,
                                         @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageFailed)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
            ImageViewCompat.setImageTintList(ivMessageStatusImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageFailed)));
            ivMessageStatusImage.setVisibility(View.VISIBLE);
        }
        tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
        tvMessageStatus.setVisibility(View.VISIBLE);
        if (null != ivSending) {
            ivSending.setAlpha(0f);
        }
        flBubble.setTranslationX(0);
    }

    private void showMessageAsSent(TAPMessageModel item,
                                   View itemView,
                                   FrameLayout flBubble,
                                   TextView tvMessageStatus,
                                   @Nullable ImageView ivMessageStatus,
                                   @Nullable ImageView ivSending) {
        showMessageAsSent(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending, false);
    }

    private void showMessageAsSent(TAPMessageModel item,
                                   View itemView,
                                   FrameLayout flBubble,
                                   TextView tvMessageStatus,
                                   @Nullable ImageView ivMessageStatus,
                                   @Nullable ImageView ivMessageStatusImage,
                                   @Nullable ImageView ivSending,
                                   boolean isImageWithoutCaption) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageSent)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
            int iconColor = isImageWithoutCaption ? R.color.tapIconChatRoomMessageSentImage : R.color.tapIconChatRoomMessageSent;
            ImageViewCompat.setImageTintList(ivMessageStatusImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), iconColor)));
            ivMessageStatusImage.setVisibility(View.VISIBLE);
        }
        tvMessageStatus.setVisibility(View.GONE);
        animateSend(item, flBubble, ivSending, ivMessageStatus);
    }

    private void showMessageAsDelivered(View itemView,
                                        FrameLayout flBubble,
                                        TextView tvMessageStatus,
                                        @Nullable ImageView ivMessageStatus,
                                        @Nullable ImageView ivSending) {
        showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending);
    }

    private void showMessageAsDelivered(View itemView,
                                        FrameLayout flBubble,
                                        TextView tvMessageStatus,
                                        @Nullable ImageView ivMessageStatus,
                                        @Nullable ImageView ivMessageStatusImage,
                                        @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageDelivered)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
            ImageViewCompat.setImageTintList(ivMessageStatusImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageDeliveredImage)));
            ivMessageStatusImage.setVisibility(View.VISIBLE);
        }
        tvMessageStatus.setVisibility(View.GONE);
        if (null != ivSending) {
            ivSending.setAlpha(0f);
        }
        flBubble.setTranslationX(0);
    }

    private void showMessageAsRead(View itemView,
                                   FrameLayout flBubble,
                                   TextView tvMessageStatus,
                                   @Nullable ImageView ivMessageStatus,
                                   @Nullable ImageView ivSending) {
        showMessageAsRead(itemView, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending);
    }

    private void showMessageAsRead(View itemView,
                                   FrameLayout flBubble,
                                   TextView tvMessageStatus,
                                   @Nullable ImageView ivMessageStatus,
                                   @Nullable ImageView ivMessageStatusImage,
                                   @Nullable ImageView ivSending) {
        if (TapUI.getInstance(instanceKey).isReadStatusHidden()) {
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending);
            return;
        }
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageRead)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
            ImageViewCompat.setImageTintList(ivMessageStatusImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageRead)));
            ivMessageStatusImage.setVisibility(View.VISIBLE);
        }
        tvMessageStatus.setVisibility(View.GONE);
        if (null != ivSending) {
            ivSending.setAlpha(0f);
        }
        flBubble.setTranslationX(0);
    }

    private void setMessageBodyText(TextView tvMessageBody, TAPMessageModel item, String body) {
        String originalText;
        String spaceAppend = "";
        if ((item.getType() == TYPE_IMAGE || item.getType() == TYPE_VIDEO || item.getType() == TYPE_FILE) && null != item.getData()) {
            originalText = (String) item.getData().get(CAPTION);
        } else if (item.getType() == TYPE_LOCATION && null != item.getData()) {
            originalText = (String) item.getData().get(ADDRESS);
        } else {
            originalText = item.getBody();
        }
        if (null == originalText) {
            return;
        }
//        if (item.getType() != TYPE_TEXT &&
//                    item.getType() != TYPE_IMAGE &&
//                    item.getType() != TYPE_VIDEO &&
//                    item.getType() != TYPE_LOCATION) {
//            spaceAppend = "";
//        } else if (isMessageFromMySelf(item)) {
//            spaceAppend = RIGHT_BUBBLE_SPACE_APPEND;
//        } else {
//            spaceAppend = LEFT_BUBBLE_SPACE_APPEND;
//        }
        // Check for mentions
        SpannableString span = generateMentionSpan(item, body, spaceAppend);
        if (null != span) {
            tvMessageBody.setText(span);
        } else {
            tvMessageBody.setText(String.format("%s%s", body, spaceAppend));
        }
    }

    private SpannableString generateMentionSpan(TAPMessageModel item, String body, String spaceAppend) {
        List<Integer> indexes = messageMentionIndexes.get(item.getLocalID());
        if (null == indexes || indexes.isEmpty()) {
            return null;
        }
        SpannableString span = new SpannableString(body + spaceAppend);
        int i = 1;
        while (i < indexes.size()) {
            String username = body.substring(indexes.get(i - 1) + 1, indexes.get(i));
                span.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(TapTalk.appContext,
                                        isMessageFromMySelf(item) ?
                                                R.color.tapLeftBubbleMessageBodyURLColor :
                                                R.color.tapRightBubbleMessageBodyURLColor)),
                        indexes.get(i - 1), indexes.get(i), 0);
                span.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        chatListener.onMentionClicked(item, username);
                    }
                }, indexes.get(i - 1), indexes.get(i), 0);
            i += 2;
        }
        return span;
    }

    private void checkAndUpdateMessageStatus(TAPBaseChatViewHolder vh, TAPMessageModel item,
                                             @Nullable ImageView ivMessageStatus,
                                             @Nullable ImageView ivSending,
                                             @Nullable CircleImageView civAvatar,
                                             @Nullable Space space,
                                             @Nullable TextView tvAvatarLabel,
                                             @Nullable TextView tvUserName) {
        if (isMessageFromMySelf(item) && null != ivMessageStatus && null != ivSending) {
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead()) {
                vh.onMessageRead(item);
            }
            // Message is delivered
            else if (null != item.getIsDelivered() && item.getIsDelivered()) {
                vh.onMessageDelivered(item);
            }
            // Message failed to send
            else if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                vh.onMessageFailedToSend(item);
            }
            // Message sent
            else if ((null != item.getIsSending() && !item.getIsSending())) {
                vh.onMessageSent(item);
            }
            // Message sending
            else {
                vh.onMessageSending(item);
            }
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
        } else {
            // Message from others
            if (item.getRoom().getType() == TYPE_PERSONAL) {
                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) && item.getForwardFrom() != null) {
                    // Saved messages room
                    if (null != tvUserName) {
                        tvUserName.setText(item.getForwardFrom().getFullname());
                        tvUserName.setVisibility(View.VISIBLE);
                    }
                    if (null != civAvatar) {
                        civAvatar.setOnClickListener(v -> {
                            // Open group member profile
                            if (isBubbleTapOnly()) {
                                chatListener.onOutsideClicked(item);
                            } else {
                                Activity activity = (Activity) vh.itemView.getContext();
                                if (null == activity) {
                                    return;
                                }
                                chatListener.onGroupMemberAvatarClicked(item);
                            }
                        });
                    }
                    if (null != civAvatar && null != tvAvatarLabel) {
                        String forwardedFromUserID = item.getForwardFrom().getUserID();
                        TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
                        TAPUserModel user;
                        if (forwardedFromUserID.equals(activeUser.getUserID())) {
                            user = activeUser;
                        } else {
                            user = TAPContactManager.getInstance(instanceKey).getUserData(forwardedFromUserID);
                        }
                        if (user != null) {
                            if (null != user.getImageURL() && !user.getImageURL().getThumbnail().isEmpty()) {
                                glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        if (vh.itemView.getContext() instanceof Activity) {
                                            ((Activity) vh.itemView.getContext()).runOnUiThread(() -> showInitial(vh, item.getForwardFrom().getFullname(), civAvatar, space, tvAvatarLabel));
                                        }
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(civAvatar);
                                ImageViewCompat.setImageTintList(civAvatar, null);
                                civAvatar.setVisibility(View.VISIBLE);
                                if (space != null) space.setVisibility(View.VISIBLE);
                                tvAvatarLabel.setVisibility(View.GONE);
                            } else {
                                showInitial(vh, item.getForwardFrom().getFullname(), civAvatar, space, tvAvatarLabel);
                            }
                        } else {
                            chatListener.onRequestUserData(item);
                            showInitial(vh, item.getForwardFrom().getFullname(), civAvatar, space, tvAvatarLabel);
                        }
                        civAvatar.setOnClickListener(v -> {
                            // Open group member profile
                            if (isBubbleTapOnly()) {
                                chatListener.onOutsideClicked(item);
                            } else {
                                Activity activity = (Activity) vh.itemView.getContext();
                                if (null == activity) {
                                    return;
                                }
                                chatListener.onGroupMemberAvatarClicked(item);
                            }
                        });
                    }
                } else {
                    // Hide avatar and name for personal room
                    if (null != civAvatar) {
                        civAvatar.setVisibility(View.GONE);
                        if (space != null) {
                            if (!vm.isSelectState()) {
                                space.setVisibility(View.GONE);
                            } else {
                                space.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    if (null != tvAvatarLabel) {
                        tvAvatarLabel.setVisibility(View.GONE);
                    }
                    if (null != tvUserName) {
                        tvUserName.setVisibility(View.GONE);
                    }
                }
            } else {
                // Load avatar and name for other room types
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserData(item.getUser().getUserID());
                if (user == null) {
                    user = item.getUser();
                }
                if (null != civAvatar && null != tvAvatarLabel && ((null != user && null != user.getDeleted() && user.getDeleted() > 0L) || (null != item.getUser() && null != item.getUser().getDeleted() && item.getUser().getDeleted() > 0L))) {
                    glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(civAvatar);
                    ImageViewCompat.setImageTintList(civAvatar, null);
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel && null != user && null != user.getImageURL() && !user.getImageURL().getThumbnail().isEmpty()) {
                    glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (vh.itemView.getContext() instanceof Activity) {
                                ((Activity) vh.itemView.getContext()).runOnUiThread(() -> showInitial(vh, item.getUser().getFullname(), civAvatar, space, tvAvatarLabel));
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(civAvatar);
                    ImageViewCompat.setImageTintList(civAvatar, null);
                    civAvatar.setVisibility(View.VISIBLE);
                    if (space != null) space.setVisibility(View.VISIBLE);
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel) {
                    showInitial(vh, item.getUser().getFullname(), civAvatar, space, tvAvatarLabel);
                }
                if (null != tvUserName) {
                    if ((null != user && null != user.getDeleted() && user.getDeleted() > 0L) || (null != item.getUser().getDeleted() && item.getUser().getDeleted() > 0L)) {
                        tvUserName.setText(R.string.tap_deleted_user);
                    } else {
                        tvUserName.setText(item.getUser().getFullname());
                    }
                    tvUserName.setVisibility(View.VISIBLE);
                }
                if (null != civAvatar) {
                    civAvatar.setOnClickListener(v -> {
                        // Open group member profile
                        if (isBubbleTapOnly()) {
                            chatListener.onOutsideClicked(item);
                        } else {
                            Activity activity = (Activity) vh.itemView.getContext();
                            if (null == activity) {
                                return;
                            }
                            chatListener.onGroupMemberAvatarClicked(item);
                        }
                    });
                }
            }
            chatListener.onMessageRead(item);
        }
    }

    private void showInitial(TAPBaseChatViewHolder vh, String fullName,
                             CircleImageView civAvatar, Space space, TextView tvAvatarLabel) {
        // Show initial
        ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(vh.itemView.getContext(), fullName)));
        civAvatar.setImageDrawable(ContextCompat.getDrawable(vh.itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setText(TAPUtils.getInitials(fullName, 2));
        civAvatar.setVisibility(View.VISIBLE);
        if (space != null) space.setVisibility(View.VISIBLE);
        tvAvatarLabel.setVisibility(View.VISIBLE);
    }

//    private void expandOrShrinkBubble(TAPMessageModel item, View itemView, FrameLayout flBubble,
//                                      TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
//                                      ImageView ivReply, boolean animate) {
//        if (item.isExpanded()) {
//            // Expand bubble
//            expandedBubble = item;
//            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
//                // Right Bubble
//                if (animate) {
//                    // Animate expand
//                    animateFadeInToBottom(tvMessageStatus);
//                    animateFadeOutToBottom(ivMessageStatus);
//                    //animateShowToLeft(ivReply);
//                } else {
//                    tvMessageStatus.setVisibility(View.VISIBLE);
//                    ivMessageStatus.setVisibility(View.GONE);
//                    //ivReply.setVisibility(View.VISIBLE);
//                }
//                if (null == bubbleOverlayRight) {
//                    bubbleOverlayRight = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_transparent_black_8dp_1dp_8dp_8dp);
//                }
//                flBubble.setForeground(bubbleOverlayRight);
//            } else {
//                // Left Bubble
//                if (animate) {
//                    // Animate expand
//                    animateFadeInToBottom(tvMessageStatus);
//                    //animateShowToRight(ivReply);
//                } else {
//                    tvMessageStatus.setVisibility(View.VISIBLE);
//                    //ivReply.setVisibility(View.VISIBLE);
//                }
//                if (null == bubbleOverlayLeft) {
//                    bubbleOverlayLeft = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_transparent_black_1dp_8dp_8dp_8dp);
//                }
//                flBubble.setForeground(bubbleOverlayLeft);
//            }
//        } else {
//            // Shrink bubble
//            flBubble.setForeground(null);
//            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
//                // Right bubble
//                if ((null != item.getFailedSend() && item.getFailedSend())) {
//                    // Message failed to send
//                    //ivReply.setVisibility(View.GONE);
//                    ivMessageStatus.setVisibility(View.VISIBLE);
//                    ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_circle_transparent));
//                    ImageViewCompat.setImageTintList(ivMessageStatus, null);
//                    tvMessageStatus.setVisibility(View.VISIBLE);
//                } else if (null != item.getSending() && !item.getSending()) {
//                    if (null != item.getIsRead() && item.getIsRead() && !TapUI.getInstance(instanceKey).isReadStatusHidden()) {
//                        // Message has been read
//                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
//                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageRead)));
//                    } else if (null != item.getDelivered() && item.getDelivered()) {
//                        // Message is delivered
//                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
//                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageDelivered)));
//                    } else if (null != item.getSending() && !item.getSending()) {
//                        // Message sent
//                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
//                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageSent)));
//                    }
//                    if (animate) {
//                        // Animate shrink
//                        //animateHideToRight(ivReply);
//                        animateFadeInToTop(ivMessageStatus);
//                        animateFadeOutToTop(tvMessageStatus);
//                    } else {
//                        //ivReply.setVisibility(View.GONE);
//                        ivMessageStatus.setVisibility(View.VISIBLE);
//                        tvMessageStatus.setVisibility(View.GONE);
//                    }
//                } else if (null != item.getSending() && item.getSending()) {
//                    // Message is sending
//                    //ivReply.setVisibility(View.GONE);
//                }
//            }
//            // Message from others
//            else if (animate) {
//                // Animate shrink
//                //animateHideToLeft(ivReply);
//                animateFadeOutToTop(tvMessageStatus);
//            } else {
//                //ivReply.setVisibility(View.GONE);
//                tvMessageStatus.setVisibility(View.GONE);
//            }
//        }
//    }

    private void showForwardedFrom(TAPMessageModel item, ConstraintLayout clForwardedFrom, TextView tvForwardedFrom) {
        TAPForwardFromModel forwardFrom = item.getForwardFrom();
        if (null != forwardFrom && null != forwardFrom.getFullname() && !forwardFrom.getFullname().isEmpty() &&
        !TAPUtils.isSavedMessagesRoom(item.getRoom().getRoomID(), instanceKey)) {
            // Show forwarded layout
            clForwardedFrom.setVisibility(View.VISIBLE);
            tvForwardedFrom.setText(forwardFrom.getFullname());
        } else {
            clForwardedFrom.setVisibility(View.GONE);
        }
    }

    private void showOrHideQuote(TAPMessageModel item, View itemView,
                                 ConstraintLayout clQuote, TextView tvQuoteTitle,
                                 TextView tvQuoteContent, TAPRoundedCornerImageView rcivQuoteImage,
                                 View vQuoteBackground, View vQuoteDecoration) {
        TAPQuoteModel quote = item.getQuote();
        if (null != quote && null != quote.getTitle() && !quote.getTitle().isEmpty()) {
            // Show quote
            clQuote.setVisibility(View.VISIBLE);
            vQuoteBackground.setVisibility(View.VISIBLE);

            if (null != item.getReplyTo() && null != item.getReplyTo().getUserID()
                    && item.getReplyTo().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                if (TAPUtils.isSavedMessagesRoom(vm.getRoom().getRoomID(), instanceKey) &&
                        item.getForwardFrom() != null && !item.getForwardFrom().getLocalID().isEmpty()) {
                    tvQuoteTitle.setText(quote.getTitle());
                } else {
                    tvQuoteTitle.setText(itemView.getResources().getString(R.string.tap_you));
                }
            } else {
                tvQuoteTitle.setText(quote.getTitle());
            }

            tvQuoteContent.setText(quote.getContent());
            String quoteImageURL = quote.getImageURL();
            String quoteFileID = quote.getFileID();
            if (quote.getFileType().equals(String.valueOf(TYPE_FILE)) || quote.getFileType().equals(FILE)) {
                // Load file icon
                glide.clear(rcivQuoteImage);
                rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_documents_white));
                if (isMessageFromMySelf(item)) {
                    ImageViewCompat.setImageTintList(rcivQuoteImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapColorPrimary)));
                    rcivQuoteImage.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_white));
                } else {
                    ImageViewCompat.setImageTintList(rcivQuoteImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapWhite)));
                    rcivQuoteImage.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_primary_icon));
                }
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
                rcivQuoteImage.setVisibility(View.VISIBLE);
            } else if (null != quoteImageURL && !quoteImageURL.isEmpty()) {
                // Get quote image from URL
                glide.clear(rcivQuoteImage);
                glide.load(quoteImageURL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), false);
                        rcivQuoteImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ImageViewCompat.setImageTintList(rcivQuoteImage, null);
                        rcivQuoteImage.setBackground(null);
                        rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        rcivQuoteImage.setVisibility(View.VISIBLE);
                        updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
                        return false;
                    }
                }).into(rcivQuoteImage);
                tvQuoteContent.setMaxLines(1);
            } else if (quote.getFileType().equals(String.valueOf(TYPE_IMAGE)) ||
                    quote.getFileType().equals(String.valueOf(TYPE_VIDEO)) ||
                    quote.getFileType().equals(IMAGE) ||
                    quote.getFileType().equals(VIDEO)) {
                glide.clear(rcivQuoteImage);
//                String key = "";
//                if (null != quoteFileID && !quoteFileID.isEmpty()) {
//                    key = quoteFileID;
//                } else if (null != quoteImageURL && !quoteImageURL.isEmpty()) {
//                    key = TAPUtils.removeNonAlphaNumeric(quoteImageURL).toLowerCase();
//                }
                // Get quote image from cache
                // TODO: 8 March 2019 IMAGE MIGHT NOT EXIST IN CACHE
//                if (!key.isEmpty()) {
                if (null != quoteFileID && !quoteFileID.isEmpty()) {
//                    String finalKey = key;
                    new Thread(() -> {
//                        BitmapDrawable image = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(finalKey);
                        BitmapDrawable image = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(quoteImageURL, quoteFileID);
                        ((Activity) itemView.getContext()).runOnUiThread(() -> {
                            if (null != image) {
                                ImageViewCompat.setImageTintList(rcivQuoteImage, null);
                                rcivQuoteImage.setImageDrawable(image);
                                rcivQuoteImage.setBackground(null);
                                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                rcivQuoteImage.setVisibility(View.VISIBLE);
                            } else {
                                rcivQuoteImage.setVisibility(View.GONE);
                            }
                        });
                    }).start();
                }
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
            } else {
                // Show no image
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), false);
                rcivQuoteImage.setVisibility(View.GONE);
            }
            vQuoteBackground.setOnClickListener(v -> {
                if (isBubbleTapOnly()) {
                    chatListener.onOutsideClicked(item);
                } else {
                    chatListener.onMessageQuoteClicked(item);
                }
            });
        } else {
            // Hide quote
            clQuote.setVisibility(View.GONE);
            vQuoteBackground.setVisibility(View.GONE);
        }
    }

    private void updateQuoteBackground(View itemView, View vQuoteBackground, boolean isMessageFromMyself, boolean hasImage) {
        if (isMessageFromMyself && hasImage) {
            vQuoteBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_bubble_quote_right_8dp));
        } else if (isMessageFromMyself) {
            vQuoteBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_bubble_quote_right_4dp));
        } else if (hasImage) {
            vQuoteBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_bubble_quote_left_8dp));
        } else {
            vQuoteBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_bubble_quote_left_4dp));
        }
    }

//    private void onBubbleClicked(TAPMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, ImageView ivMessageStatus, ImageView ivReply) {
//        if (null != item.getFailedSend() && item.getFailedSend()) {
//            resendMessage(item);
//        }
//        else if (item.getType() == TYPE_TEXT &&
//                ((null != item.getSending() && !item.getSending()) ||
//                        (null != item.getDelivered() && item.getDelivered()) ||
//                        (null != item.getIsRead() && item.getIsRead()))) {
//            if (item.isExpanded()) {
//                // Shrink bubble
//                item.setExpanded(false);
//            } else {
//                // Expand clicked bubble
//                shrinkExpandedBubble();
//                item.setExpanded(true);
//            }
//            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, true);
//        }
//    }

    private void onStatusImageClicked(TAPMessageModel item) {
        if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(TAPMessageModel item) {
        chatListener.onReplyMessage(item);
    }

    private void resendMessage(TAPMessageModel item) {
        if (isBubbleTapOnly()) {
            chatListener.onOutsideClicked(item);
        } else {
            removeMessage(item);
            chatListener.onRetrySendMessage(item);
        }
    }

    private void animateSend(TAPMessageModel item, FrameLayout flBubble,
                             ImageView ivSending, ImageView ivMessageStatus) {
        if (!pendingAnimationMessages.contains(item)) {
            // Set bubble state to post-animation
            flBubble.setTranslationX(0);
            ivMessageStatus.setTranslationX(0);
            ivSending.setAlpha(0f);
        } else {
            // Animate bubble
            pendingAnimationMessages.remove(item);
            animatingMessages.add(item);
            flBubble.setTranslationX(initialTranslationX);
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            new Handler().postDelayed(() -> {
                flBubble.animate()
                        .translationX(0)
                        .setDuration(160L)
                        .start();
                ivSending.animate()
                        .translationX(TAPUtils.dpToPx(36))
                        .translationY(TAPUtils.dpToPx(-23))
                        .setDuration(360L)
                        .setInterpolator(new AccelerateInterpolator(0.5f))
                        .withEndAction(() -> {
                            ivSending.setAlpha(0f);
                            animatingMessages.remove(item);
                            if ((null != item.getIsRead() && item.getIsRead()) ||
                                    (null != item.getIsDelivered() && item.getIsDelivered())) {
                                notifyItemChanged(getItems().indexOf(item));
                            }
                        })
                        .start();
            }, 200L);

            // Animate reply button
            //if (null != ivReply) {
            //    animateShowToLeft(ivReply);
            //}
        }
    }

    private void animateFadeInToTop(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(TAPUtils.dpToPx(24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateFadeInToBottom(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(TAPUtils.dpToPx(-24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        new Handler().postDelayed(() -> chatListener.onBubbleExpanded(), 50L);
    }

    private void animateFadeOutToTop(View view) {
        view.animate()
                .translationY(TAPUtils.dpToPx(-24))
                .alpha(0)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationY(0);
                })
                .start();
    }

    private void animateFadeOutToBottom(View view) {
        view.animate()
                .translationY(TAPUtils.dpToPx(24))
                .alpha(0)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationY(0);
                })
                .start();
    }

    private void animateShowToLeft(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(TAPUtils.dpToPx(32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .start();
    }

    private void animateShowToRight(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(TAPUtils.dpToPx(-32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .start();
    }

    private void animateHideToLeft(View view) {
        view.animate()
                .translationX(TAPUtils.dpToPx(-32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationX(0);
                })
                .start();
    }

    private void animateHideToRight(View view) {
        view.animate()
                .translationX(TAPUtils.dpToPx(32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationX(0);
                })
                .start();
    }

    private void checkAndAnimateHighlight(TAPMessageModel item, View ivBubbleHighlight) {
        if (null == highlightedMessage || highlightedMessage != item) {
            ivBubbleHighlight.setVisibility(View.GONE);
            return;
        }
        ivBubbleHighlight.setAlpha(0f);
        ivBubbleHighlight.setVisibility(View.VISIBLE);
        ivBubbleHighlight.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(200L)
                .withEndAction(() -> new Handler().postDelayed(() -> {
                    ivBubbleHighlight.animate()
                            .alpha(0f)
                            .setDuration(750L)
                            .withEndAction(() -> {
                                ivBubbleHighlight.setVisibility(View.GONE);
                                highlightedMessage = null;
                            })
                            .start();
                }, 1000L))
                .start();
    }

    public void setMessages(List<TAPMessageModel> messages) {
        setItems(messages, false);
    }

    public void addMessage(TAPMessageModel message) {
        addItem(0, message);
    }

    public void addMessage(TAPMessageModel message, int position, boolean isNotify) {
        getItems().add(position, message);
        if (isNotify) notifyItemInserted(position);
    }

    public void addMessage(List<TAPMessageModel> messages) {
        addItem(messages, true);
    }

    public void addOlderMessagesFromApi(List<TAPMessageModel> messages) {
        addItem(messages, false);
        notifyDataSetChanged();
    }

    public void addMessage(int position, List<TAPMessageModel> messages, boolean isNotify) {
        addItem(position, messages, isNotify);
    }

    public void setMessageAt(int position, TAPMessageModel message) {
        setItemAt(position, message);
        notifyItemChanged(position);
    }

    public void removeMessageAt(int position) {
        removeItemAt(position);
    }

    public void removeMessage(TAPMessageModel message) {
        removeItem(message);
    }

    public void highlightMessage(TAPMessageModel message) {
        if (!getItems().contains(message)) {
            return;
        }
        this.highlightedMessage = message;
        notifyItemChanged(getItems().indexOf(message));
    }

    private void fixImageOrVideoViewSize(
            TAPMessageModel item,
            TAPRoundedCornerImageView rcivImageBody,
            LinearLayout llTimestampIconImage,
            ConstraintLayout clForwardedQuote,
            TextView tvMessageTimestamp,
            ImageView ivMessageStatus
    ) {
        if (null == item.getData()) {
            return;
        }
        Number widthDimension = (Number) item.getData().get(WIDTH);
        Number heightDimension = (Number) item.getData().get(HEIGHT);

        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rcivImageBody.measure(measureSpec, measureSpec);
        llTimestampIconImage.measure(measureSpec, measureSpec);

        boolean hasQuoteOrForwarded =
            (null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
            (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty());

        if (hasQuoteOrForwarded &&
            null != widthDimension &&
            null != heightDimension
        ) {
            // Fix layout when quote/forward exists
            float imageRatio = widthDimension.floatValue() / heightDimension.floatValue();
            // Set image width to maximum
            rcivImageBody.getLayoutParams().width = 0;
            if (imageRatio > (float) rcivImageBody.getMaxWidth() / (float) rcivImageBody.getMinHeight()) {
                // Set minimum height if image width exceeds limit
                rcivImageBody.getLayoutParams().height = rcivImageBody.getMinHeight();
            } else if (imageRatio < (float) rcivImageBody.getMaxHeight() / (float) rcivImageBody.getMaxWidth()) {
                // Set maximum height if image height exceeds limit
                rcivImageBody.getLayoutParams().height = rcivImageBody.getMaxHeight();
            } else {
                // Set default image height
                rcivImageBody.getLayoutParams().height = (int) (rcivImageBody.getMaxWidth() * heightDimension.floatValue() / widthDimension.floatValue());
            }
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (rcivImageBody.getMeasuredWidth() < (llTimestampIconImage.getMeasuredWidth() + TAPUtils.dpToPx(12))) {
            // Thumbnail width may not be smaller than timestamp width (no caption)
            rcivImageBody.getLayoutParams().width = llTimestampIconImage.getMeasuredWidth() + TAPUtils.dpToPx(12);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (isMessageFromMySelf(item) && rcivImageBody.getMeasuredWidth() < (tvMessageTimestamp.getMeasuredWidth() + ivMessageStatus.getMeasuredWidth() + TAPUtils.dpToPx(22))) {
            // Thumbnail width may not be smaller than timestamp width (with caption, right bubble)
            rcivImageBody.getLayoutParams().width = tvMessageTimestamp.getMeasuredWidth() + ivMessageStatus.getMeasuredWidth() + TAPUtils.dpToPx(22);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (!isMessageFromMySelf(item) && rcivImageBody.getMeasuredWidth() < (tvMessageTimestamp.getMeasuredWidth() + TAPUtils.dpToPx(20))) {
            // Thumbnail width may not be smaller than timestamp width (with caption, left bubble)
            rcivImageBody.getLayoutParams().width = tvMessageTimestamp.getMeasuredWidth() + tvMessageTimestamp.getMeasuredWidth() + TAPUtils.dpToPx(20);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (null != widthDimension && null != heightDimension && widthDimension.intValue() > 0 && heightDimension.intValue() > 0) {
            // Set default image size
            rcivImageBody.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.FIT_CENTER);
            clForwardedQuote.setVisibility(View.GONE);
        } else {
            // Set container to square if dimension is not present
            rcivImageBody.getLayoutParams().width = rcivImageBody.getMaxWidth();
            rcivImageBody.getLayoutParams().height = rcivImageBody.getMaxWidth();
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clForwardedQuote.setVisibility(View.GONE);
        }
        if (hasQuoteOrForwarded) {
            clForwardedQuote.setVisibility(View.VISIBLE);
        } else {
            clForwardedQuote.setVisibility(View.GONE);
        }
        rcivImageBody.requestLayout();
    }

    private void setStarredIcon(String id, ImageView imageView) {
        if (starredMessageIds.contains(id) || roomType == RoomType.STARRED) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void setPinnedIcon(String id, ImageView imageView) {
        if (pinnedMessageIds.contains(id) || roomType == RoomType.PINNED) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void setEditedMessage(boolean isEdited, TextView shownTextView, TextView hiddenTextView, ImageView hiddenImage) {
        if (isEdited) {
            shownTextView.setVisibility(View.VISIBLE);
        } else {
            shownTextView.setVisibility(View.GONE);
        }
        hiddenTextView.setVisibility(View.GONE);
        hiddenImage.setVisibility(View.GONE);

    }

    private void setReadCountIcon(View view, TextView tvReadCount, String messageId) {
        if (!TapUI.getInstance(instanceKey).isReadStatusHidden()) {
            Integer readCount = vm.getMessageReadCountMap().get(messageId);
            if (/*view.getVisibility() != View.VISIBLE && */readCount != null && readCount > 0) {
                view.setVisibility(View.VISIBLE);
                tvReadCount.setVisibility(View.VISIBLE);
                tvReadCount.setText(String.format(Locale.getDefault(), "%d •", readCount));
            } else {
                view.setVisibility(View.GONE);
                tvReadCount.setVisibility(View.GONE);
            }
        }
    }

    // update list when context use star message feature
    public void setStarredMessageIds(List<String> starredMessageIds) {
        this.starredMessageIds.clear();
        this.starredMessageIds.addAll(starredMessageIds);
    }

    // update list when context use pin message feature
    public void setPinnedMessageIds(List<String> pinnedMessageIds) {
        this.pinnedMessageIds.clear();
        this.pinnedMessageIds.addAll(pinnedMessageIds);
    }


    private void loadMediaPlayer(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri, int audioDuration) {
        try {
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(activity, fileUri);
            playingSeekbar = seekBar;
            playingSeekbar.setOnSeekBarChangeListener(seekBarChangeListener(tvDuration));
            audioPlayer.setOnPreparedListener(preparedListener(activity, tvDuration, image, audioDuration));
            audioPlayer.setOnCompletionListener(completionListener(activity, image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener(TextView tvDuration) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    String currentTimeString = TAPUtils.getMediaDurationString(audioPlayer.getCurrentPosition(), audioPlayer.getDuration());
                    tvDuration.setText(currentTimeString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isSeeking) {
                    audioPlayer.seekTo(audioPlayer.getDuration() * playingSeekbar.getProgress() / playingSeekbar.getMax());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                audioPlayer.seekTo(audioPlayer.getDuration() * playingSeekbar.getProgress() / playingSeekbar.getMax());
            }
        };
    }

    private MediaPlayer.OnPreparedListener preparedListener(Activity activity, TextView tvDuration, ImageView image, int audioDuration) {
        return mediaPlayer -> {
            duration = audioPlayer.getDuration();
            isMediaPlaying = true;
            playingSeekbar.setEnabled(true);
            stopProgressTimer();
            startProgressTimer(activity);
            tvDuration.setText(TAPUtils.getMediaDurationString(duration, duration));
            mediaPlayer.seekTo(pausedPosition);
            mediaPlayer.setOnSeekCompleteListener(onSeekListener(activity, tvDuration));
            audioPlayer = mediaPlayer;
            activity.runOnUiThread(() -> {
                audioPlayer.start();
                image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_pause_white));
            });
        };
    }

    private MediaPlayer.OnSeekCompleteListener onSeekListener(Activity activity, TextView tvDuration) {
       return mediaPlayer -> {
           try {
               tvDuration.setText(TAPUtils.getMediaDurationString(mediaPlayer.getCurrentPosition(), duration));
           } catch (Exception e) {
               e.printStackTrace();
           }
           if (!isSeeking && isMediaPlaying) {
               mediaPlayer.start();
               startProgressTimer(activity);
           } else {
               pausedPosition = mediaPlayer.getCurrentPosition();
               if (pausedPosition >= duration) {
                   pausedPosition = 0;
               }
           }
       };
    }

    private MediaPlayer.OnCompletionListener completionListener(Activity activity, ImageView image) {
        return mediaPlayer -> setFinishPlayingState(activity, image);
    }

    private void setFinishPlayingState(Activity activity, ImageView image) {
            playingSeekbar.setEnabled(false);
            pausedPosition = 0;
            playingSeekbar.setProgress(0);
            image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_play_white));
            notifyItemChanged(getItems().indexOf(vm.getMessagePointer().get(lastLocalId)));
    }

    private void startProgressTimer(Activity activity) {
        if (null != durationTimer) {
            return;
        }
        durationTimer = new Timer();
        durationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    if (audioPlayer != null && duration != 0) {
                        playingSeekbar.setProgress(audioPlayer.getCurrentPosition() * playingSeekbar.getMax() / duration);
                    }
                });
            }
        }, 0, 10L);
    }

    private void stopProgressTimer() {
        if (null == durationTimer) {
            return;
        }
        durationTimer.cancel();
        durationTimer = null;
    }

    private void playBubbleVoiceNote(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri, String currentLocalId, int audioDuration) {
        try {
            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();
                voiceUri = fileUri;
                loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri, audioDuration);
                audioPlayer.prepareAsync();
                lastLocalId = currentLocalId;
            } else {
                if (audioPlayer.isPlaying()) {
                    if (!lastLocalId.equals(currentLocalId)) {
                        voiceUri = fileUri;
                        pausedPosition = 0;
                        removePlayer();
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri, audioDuration);
                        notifyItemChanged(getItems().indexOf(vm.getMessagePointer().get(lastLocalId)));
                        audioPlayer.prepareAsync();
                        lastLocalId = currentLocalId;
                    } else {
                        pauseBubbleVoiceNote(image, activity);
                    }
                } else {
                    if (!lastLocalId.equals(currentLocalId)) {
                        voiceUri = fileUri;
                        pausedPosition = 0;
                        removePlayer();
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri, audioDuration);
                        notifyItemChanged(getItems().indexOf(vm.getMessagePointer().get(lastLocalId)));
                    } else {
                        audioPlayer.release();
                        audioPlayer = null;
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri, audioDuration);
                    }
                    audioPlayer.prepareAsync();
                    lastLocalId = currentLocalId;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void pauseBubbleVoiceNote(ImageView image, Activity activity) {
        isMediaPlaying = false;
        pausedPosition = audioPlayer.getCurrentPosition();
        audioPlayer.pause();
        stopProgressTimer();
        image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_play_white));
    }

    public void removePlayer() {
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            audioPlayer.release();
            audioPlayer = null;
        }
        stopProgressTimer();
    }

    private void setSelectedState(TAPMessageModel message, ImageView selectRadioButton, View bubbleArea) {
        if (vm.isSelectState()) {
            selectRadioButton.setVisibility(View.VISIBLE);

            bubbleArea.setVisibility(View.VISIBLE);
            if (vm.getSelectedMessages().contains(message)) {
                glide.load(R.drawable.tap_ic_circle_active_check).fitCenter().into(selectRadioButton);
            } else {
                glide.load(R.drawable.tap_ic_circle_inactive_transparent).fitCenter().into(selectRadioButton);
            }
        } else {
            selectRadioButton.setVisibility(View.GONE);
            bubbleArea.setVisibility(View.GONE);
        }
    }

    private void setOriginalMessageButton(TAPMessageModel message, View button) {
        if (button != null) {
            if (vm.isSelectState() || !TAPUtils.isSavedMessagesRoom(message.getRoom().getRoomID(), instanceKey) || isBubbleTapOnly()) {
                button.setVisibility(View.GONE);
            } else {
                button.setOnClickListener(view -> chatListener.onArrowButtonClicked(message));
                button.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getLastLocalId() {
        return lastLocalId;
    }

    private boolean isBubbleTapOnly() {
        return roomType == RoomType.STARRED || roomType == RoomType.PINNED/* || roomType == RoomType.DETAIL*/;
    }
}
