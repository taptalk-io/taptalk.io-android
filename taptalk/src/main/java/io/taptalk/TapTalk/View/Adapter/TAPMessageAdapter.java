package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;

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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.PlayPauseVoiceNote;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IS_PLAYING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.WIDTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
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

public class TAPMessageAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseChatViewHolder> {

    private static final String TAG = TAPMessageAdapter.class.getSimpleName();
    private String instanceKey = "";
    private TAPChatListener chatListener;
    private List<TAPMessageModel> pendingAnimationMessages, animatingMessages;
    private ArrayList<String> starredMessageIds = new ArrayList<>();
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
    public int lastPosition = -1;

    public enum RoomType {
        DEFAULT, STARRED
    }

    public TAPMessageAdapter(
            String instanceKey,
            RequestManager glide,
            TAPChatListener chatListener,
            Map<String, List<Integer>> messageMentionIndexes,
            ArrayList<String> starredMessageIds
    ) {
        myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
        this.instanceKey = instanceKey;
        this.chatListener = chatListener;
        this.glide = glide;
        this.messageMentionIndexes = messageMentionIndexes;
        pendingAnimationMessages = new ArrayList<>();
        animatingMessages = new ArrayList<>();
        this.starredMessageIds = starredMessageIds;
    }

    public TAPMessageAdapter(
            String instanceKey,
            RequestManager glide,
            TAPChatListener chatListener,
            Map<String, List<Integer>> messageMentionIndexes,
            RoomType roomType
    ) {
        myUserModel = TAPChatManager.getInstance(instanceKey).getActiveUser();
        this.instanceKey = instanceKey;
        this.chatListener = chatListener;
        this.glide = glide;
        this.messageMentionIndexes = messageMentionIndexes;
        this.roomType = roomType;
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
                return new UnsupportedVH(parent, R.layout.tap_cell_chat_bubble_text_left, viewType);
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
            if (null != messageModel && null != messageModel.getHidden() && messageModel.getHidden()) {
                // Return empty layout if item is hidden
                return TYPE_EMPTY;
            } else if (null != messageModel && null != messageModel.getIsDeleted() && messageModel.getIsDeleted() && isMessageFromMySelf(messageModel)) {
                return TYPE_BUBBLE_DELETED_RIGHT;
            } else if (null != messageModel && null != messageModel.getIsDeleted() && messageModel.getIsDeleted()) {
                return TYPE_BUBBLE_DELETED_LEFT;
            } else if (null != messageModel) {
                messageType = messageModel.getType();
            }

            switch (messageType) {
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
                    return messageType;
            }
        } catch (Exception e) {
            return TYPE_LOG;
        }
    }

    private boolean isMessageFromMySelf(TAPMessageModel messageModel) {
        return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }

    public class TextVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        private FrameLayout flBubble;
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
        private View vSeparator;

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
            vSeparator = itemView.findViewById(R.id.v_separator);

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            setMessageBodyText(tvMessageBody, item, item.getBody());
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            //expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);
            checkAndAnimateHighlight(item, ivBubbleHighlight);

            markMessageAsRead(item, myUserModel);
            if (roomType != RoomType.STARRED) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            }
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);


            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            if (roomType != RoomType.STARRED) {
                flBubble.setOnClickListener(v -> onStatusImageClicked(item));
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
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
    }

    public class ImageVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwardedQuote;
        private ConstraintLayout clQuote;
        private ConstraintLayout clForwarded;
        private LinearLayout llTimestampIconImage;
        private FrameLayout flBubble;
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
        private View vSeparator;

        private TAPMessageModel obtainedItem;
        private Drawable thumbnail;

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
            vSeparator = itemView.findViewById(R.id.v_separator);

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, null);
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

            markMessageAsRead(item, myUserModel);
            if (roomType != RoomType.STARRED) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            } else {
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
                }
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            }
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivImageBody, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void openImageDetailPreview(TAPMessageModel message) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(message);
            } else {
                TAPImageDetailPreviewActivity.start(itemView.getContext(), instanceKey, message, rcivImageBody);
            }
        }

        private void setProgress(TAPMessageModel item) {
            String localID = item.getLocalID();
            Integer uploadProgressValue = TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(localID);
            Integer downloadProgressValue = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID());
            if (null != item.getFailedSend() && item.getFailedSend()) {
                flProgress.setVisibility(View.VISIBLE);
                pbProgress.setVisibility(View.GONE);
            } else if ((null == uploadProgressValue || (null != item.getSending() && !item.getSending()))
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
                if (roomType != RoomType.STARRED) {
                    setLinkDetection(itemView.getContext(), item, tvMessageBody);
                }
                tvMessageBody.setVisibility(View.VISIBLE);
                llTimestampIconImage.setVisibility(View.GONE);
                tvMessageTimestamp.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.VISIBLE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessageBody);
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
            }

            if (null != widthDimension && null != heightDimension && widthDimension.intValue() > 0 && heightDimension.intValue() > 0) {
                rcivImageBody.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
            } else {
                rcivImageBody.setImageDimensions(rcivImageBody.getMaxWidth(), rcivImageBody.getMaxWidth());
            }

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                rcivImageBody.setImageDrawable(thumbnail);
                fixImageOrVideoViewSize(item, rcivImageBody, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
            }

            if (null != imageUrl && !imageUrl.isEmpty()) {
                // Load image from URL
                glide.load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .apply(new RequestOptions()
                                .placeholder(thumbnail)
                                .centerCrop())
                        .listener(imageBodyListener)
                        .into(rcivImageBody);
                rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
            } else if (null != fileID && !fileID.isEmpty()) {
                new Thread(() -> {
                    BitmapDrawable cachedImage = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(fileID);
                    if (null != cachedImage) {
                        // Load image from cache
                        activity.runOnUiThread(() -> {
                            glide.load(cachedImage)
                                    .transition(DrawableTransitionOptions.withCrossFade(100))
                                    .apply(new RequestOptions()
                                            .placeholder(thumbnail)
                                            .centerCrop())
                                    .listener(imageBodyListener)
                                    .into(rcivImageBody);
                            rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
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
                }).start();
            } else if (null != imageUri && !imageUri.isEmpty()) {
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
                return false;
            }
        };

        private void setImageViewButtonProgress(TAPMessageModel item) {
            if (null != item.getFailedSend() && item.getFailedSend()) {
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
            } else if ((null == TAPFileUploadManager.getInstance(instanceKey).getUploadProgressPercent(item.getLocalID()) || (null != item.getSending() && !item.getSending()))
                    && null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID())) {
                // Progress done
                flProgress.setVisibility(View.GONE);
            } else {
                // Uploading / Downloading
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownloadWhite)));
                flProgress.setOnClickListener(v -> {
                     if (roomType == RoomType.STARRED) {
                        chatListener.onOutsideClicked(item);
                     } else {
                         TAPDataManager.getInstance(instanceKey)
                                 .cancelUploadImage(itemView.getContext(), item.getLocalID());
                     }
                });
                if (isMessageFromMySelf(item) && null != item.getSending() && item.getSending()) {
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
            boolean noCaption = true;
            if (null != message.getData() && null != message.getData().get(CAPTION)) {
                String caption = (String) message.getData().get(CAPTION);
                if (null != caption && !caption.isEmpty()) {
                    noCaption = false;
                }
            }
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending, noCaption);
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
        private FrameLayout flBubble;
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
        private View vSeparator;

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
            vSeparator = itemView.findViewById(R.id.v_separator);

            if (bubbleType == TYPE_BUBBLE_VIDEO_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, null);
            }

            tvMediaInfo.setVisibility(View.VISIBLE);
            tvMessageTimestamp.setText(item.getMessageStatusText());
            tvMessageTimestampImage.setText(item.getMessageStatusText());

            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setVideoProgress(item, position);
            fixBubbleMarginForGroupRoom(item, flBubble);

            markMessageAsRead(item, myUserModel);
            if (roomType != RoomType.STARRED) {
                setLinkDetection(itemView.getContext(), item, tvMessageBody);
            } else {
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
                }
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            }
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivVideoThumbnail, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void setVideoProgress(TAPMessageModel item, int position) {
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
                if (roomType != RoomType.STARRED) {
                    setLinkDetection(itemView.getContext(), item, tvMessageBody);
                }
                tvMessageBody.setVisibility(View.VISIBLE);
                llTimestampIconImage.setVisibility(View.GONE);
                tvMessageTimestamp.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
                    ivMessageStatus.setVisibility(View.VISIBLE);
                }
                setStarredIcon(item.getMessageID(), ivStarMessageBody);
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
            if (null == TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(item.getLocalID()) || null != dataUri) {
                rcivVideoThumbnail.setImageDrawable(thumbnail);
                fixImageOrVideoViewSize(item, rcivVideoThumbnail, llTimestampIconImage, clForwardedQuote, tvMessageTimestamp, ivMessageStatus);
            }

            if (null != item.getFailedSend() && item.getFailedSend()) {
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
                glide.load(videoUri)
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .apply(new RequestOptions()
                                .placeholder(thumbnail)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .listener(videoThumbnailListener)
                        .into(rcivVideoThumbnail);
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
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
                            key = TAPUtils.removeNonAlphaNumeric(fileUrl).toLowerCase();
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
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
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
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void openVideoPlayer(TAPMessageModel message) {
            if (null == message.getData()) {
                return;
            }
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(message);
            } else {
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
                    TAPVideoPlayerActivity.start(itemView.getContext(), instanceKey, videoUri, message);
                }
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
            boolean noCaption = true;
            if (null != message.getData() && null != message.getData().get(CAPTION)) {
                String caption = (String) message.getData().get(CAPTION);
                if (null != caption && !caption.isEmpty()) {
                    noCaption = false;
                }
            }
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivMessageStatusImage, ivSending, noCaption);
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
        private FrameLayout flBubble;
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
        private TextView tvMessageTimestamp;
        private TextView tvMessageStatus;
        private TextView tvForwardedFrom;
        private TextView tvQuoteTitle;
        private TextView tvQuoteContent;
        private View vQuoteBackground;
        private View vQuoteDecoration;
        private ProgressBar pbProgress;
        private ImageView ivStarMessage;
        private View vSeparator;

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
            tvMessageTimestamp = itemView.findViewById(R.id.tv_message_timestamp);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivStarMessage = itemView.findViewById(R.id.iv_star_message);
            vSeparator = itemView.findViewById(R.id.v_separator);

            if (bubbleType == TYPE_BUBBLE_FILE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setFileProgress(item);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            if (roomType != RoomType.STARRED) {
                flBubble.setOnClickListener(v -> flFileIcon.performClick());
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
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

            if (null != item.getFailedSend() && item.getFailedSend()) {
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
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
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
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
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

        private void downloadFile(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void openFile(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
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
        private FrameLayout flBubble;
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
        private View vSeparator;
        private SeekBar seekBar;

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
            vSeparator = itemView.findViewById(R.id.v_separator);
            seekBar = itemView.findViewById(R.id.seek_bar);

            if (bubbleType == TYPE_BUBBLE_VOICE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            setFileProgress(item);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            setStarredIcon(item.getMessageID(), ivStarMessage);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            if (roomType != RoomType.STARRED) {
                flBubble.setOnClickListener(v -> flVoiceIcon.performClick());
                //ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
            } else {
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
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
            if (null != item.getFailedSend() && item.getFailedSend()) {
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
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent) && (null != fileUri || TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(item))) {
                // File has finished downloading or uploading
                tvMessageStatus.setText(item.getMessageStatusText());
                tvVoiceTime.setText(durationString);
                ivVoiceIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_play_white));
                pbProgress.setVisibility(View.GONE);
                seekBar.getThumb().mutate().setAlpha(255);
                seekBar.setOnSeekBarChangeListener(null);
                seekBar.setProgress(0);
                seekBar.setEnabled(false);
                // TODO: 18/04/22 handle play pause voice note MU
                // TODO: 18/04/22 handle seekbar logic MU
                flVoiceIcon.setOnClickListener(v -> playPauseVoiceNote(seekBar, (Activity) itemView.getContext(), tvVoiceTime, ivVoiceIcon, fileUri, item, getAbsoluteAdapterPosition()));
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
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
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(DownloadFile);
                intent.putExtra(MESSAGE, item);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelDownload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(CancelDownload);
                intent.putExtra(DownloadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void cancelUpload(TAPMessageModel item) {
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(UploadCancelled);
                intent.putExtra(UploadLocalID, item.getLocalID());
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
            }
        }

        private void playPauseVoiceNote(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri, TAPMessageModel item, int currentPosition) {
            // TODO: 18/04/22 play voice note MU
            if (roomType == RoomType.STARRED) {
                chatListener.onOutsideClicked(item);
            } else {
                Intent intent = new Intent(PlayPauseVoiceNote);
                intent.putExtra(MESSAGE, item);
                intent.putExtra(IS_PLAYING, true);
                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                seekBar.setOnSeekBarChangeListener(seekBarChangeListener(activity, tvDuration, image));
                playBubbleVoiceNote(seekBar, activity, tvDuration, image, fileUri, currentPosition);
            }
        }

    }


    public class LocationVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clBubbleTop;
        private ConstraintLayout clForwardedQuote;
        private ConstraintLayout clQuote;
        private ConstraintLayout clForwarded;
        private FrameLayout flBubble;
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
        private View vSeparator;

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
            vSeparator = itemView.findViewById(R.id.v_separator);

            if (bubbleType == TYPE_BUBBLE_LOCATION_LEFT) {
                clBubbleTop = itemView.findViewById(R.id.cl_bubble_top);
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
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
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            checkAndAnimateHighlight(item, ivBubbleHighlight);
            fixBubbleMarginForGroupRoom(item, flBubble);

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

            vMapBorder.setOnClickListener(v -> {
                if (roomType == RoomType.STARRED) {
                    chatListener.onOutsideClicked(item);
                } else {
                    openMapDetail(mapData);
                }
            });
            if (roomType == RoomType.STARRED) {
                if (position != 0) {
                    vSeparator.setVisibility(View.VISIBLE);
                } else {
                    vSeparator.setVisibility(View.GONE);
                }
                flBubble.setOnClickListener(v -> chatListener.onOutsideClicked(item));
            }
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked(item));
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
        if (item.getRoom().getType() == TYPE_GROUP) {
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
                checkAndUpdateMessageStatus(this, item, null, null, civAvatar, tvAvatarLabel, tvUserName);
            }
            markMessageAsRead(item, myUserModel);
//            enableLongPress(itemView.getContext(), clContainer, item);
        }
    }

    public class UnsupportedVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer;
        private ConstraintLayout clForwarded;
        private ConstraintLayout clQuote;
        private FrameLayout flBubble;
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
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == item) {
                return;
            }
            if (!animatingMessages.contains(item)) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }
            tvMessageTimestamp.setText(item.getMessageStatusText());
            tvMessageBody.setText(R.string.tap_message_type_unsupported);
            clForwarded.setVisibility(View.GONE);
            clQuote.setVisibility(View.GONE);
            vQuoteBackground.setVisibility(View.GONE);

            markMessageAsRead(item, myUserModel);
            setStarredIcon(item.getMessageID(), ivStarMessage);

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
        showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, null, ivSending, false);
    }

    private void showMessageAsDelivered(View itemView,
                                        FrameLayout flBubble,
                                        TextView tvMessageStatus,
                                        @Nullable ImageView ivMessageStatus,
                                        @Nullable ImageView ivMessageStatusImage,
                                        @Nullable ImageView ivSending,
                                        boolean isImageWithoutCaption) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatRoomMessageDelivered)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivMessageStatusImage) {
            ivMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
            int iconColor = isImageWithoutCaption ? R.color.tapIconChatRoomMessageDeliveredImage : R.color.tapIconChatRoomMessageDelivered;
            ImageViewCompat.setImageTintList(ivMessageStatusImage, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), iconColor)));
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
            showMessageAsDelivered(itemView, flBubble, tvMessageStatus, ivMessageStatus, ivSending);
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
        if (item.getType() == TYPE_TEXT) {
            originalText = item.getBody();
        } else if ((item.getType() == TYPE_IMAGE || item.getType() == TYPE_VIDEO) && null != item.getData()) {
            originalText = (String) item.getData().get(CAPTION);
        } else if (item.getType() == TYPE_LOCATION && null != item.getData()) {
            originalText = (String) item.getData().get(ADDRESS);
        } else {
            return;
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
                                             @Nullable TextView tvAvatarLabel,
                                             @Nullable TextView tvUserName) {
        if (isMessageFromMySelf(item) && null != ivMessageStatus && null != ivSending) {
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead()) {
                vh.onMessageRead(item);
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                vh.onMessageDelivered(item);
            }
            // Message failed to send
            else if (null != item.getFailedSend() && item.getFailedSend()) {
                vh.onMessageFailedToSend(item);
            }
            // Message sent
            else if ((null != item.getSending() && !item.getSending())) {
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
                // Hide avatar and name for personal room
                if (null != civAvatar) {
                    civAvatar.setVisibility(View.GONE);
                }
                if (null != tvAvatarLabel) {
                    tvAvatarLabel.setVisibility(View.GONE);
                }
                if (null != tvUserName) {
                    tvUserName.setVisibility(View.GONE);
                }
            } else {
                // Load avatar and name for other room types
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserData(item.getUser().getUserID());
                if (null != civAvatar && null != tvAvatarLabel && null != user && null != user.getImageURL() && !user.getImageURL().getThumbnail().isEmpty()) {
                    glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (vh.itemView.getContext() instanceof Activity) {
                                ((Activity) vh.itemView.getContext()).runOnUiThread(() -> showInitial(vh, item, civAvatar, tvAvatarLabel));
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
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel && null != item.getUser().getImageURL() && !item.getUser().getImageURL().getThumbnail().isEmpty()) {
                    glide.load(item.getUser().getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (vh.itemView.getContext() instanceof Activity) {
                                ((Activity) vh.itemView.getContext()).runOnUiThread(() -> showInitial(vh, item, civAvatar, tvAvatarLabel));
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
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel) {
                    showInitial(vh, item, civAvatar, tvAvatarLabel);
                }
                if (null != tvUserName) {
                    tvUserName.setText(item.getUser().getFullname());
                    tvUserName.setVisibility(View.VISIBLE);
                }
                if (null != civAvatar) {
                    civAvatar.setOnClickListener(v -> {
                        // Open group member profile
                        if (roomType == RoomType.STARRED) {
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

    private void showInitial(TAPBaseChatViewHolder vh, TAPMessageModel item,
                             CircleImageView civAvatar, TextView tvAvatarLabel) {
        // Show initial
        ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(vh.itemView.getContext(), item.getUser().getFullname())));
        civAvatar.setImageDrawable(ContextCompat.getDrawable(vh.itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setText(TAPUtils.getInitials(item.getUser().getFullname(), 2));
        civAvatar.setVisibility(View.VISIBLE);
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
        if (null != forwardFrom && null != forwardFrom.getFullname() && !forwardFrom.getFullname().isEmpty()) {
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
                tvQuoteTitle.setText(itemView.getResources().getString(R.string.tap_you));
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
                String key = "";
                if (null != quoteFileID && !quoteFileID.isEmpty()) {
                    key = quoteFileID;
                } else if (null != quoteImageURL && !quoteImageURL.isEmpty()) {
                    key = TAPUtils.removeNonAlphaNumeric(quoteImageURL).toLowerCase();
                }
                // Get quote image from cache
                // TODO: 8 March 2019 IMAGE MIGHT NOT EXIST IN CACHE
                if (!key.isEmpty()) {
                    String finalKey = key;
                    new Thread(() -> {
                        BitmapDrawable image = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(finalKey);
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
                if (roomType == RoomType.STARRED) {
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
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(TAPMessageModel item) {
        chatListener.onReplyMessage(item);
    }

    private void resendMessage(TAPMessageModel item) {
        if (roomType == RoomType.STARRED) {
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
                                    (null != item.getDelivered() && item.getDelivered())) {
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
                            .withEndAction(() -> ivBubbleHighlight.setVisibility(View.GONE))
                            .start();
                }, 1000L))
                .start();
        highlightedMessage = null;
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

        if (((null != item.getQuote() &&
                null != item.getQuote().getTitle() &&
                !item.getQuote().getTitle().isEmpty()) ||
                (null != item.getForwardFrom() &&
                        null != item.getForwardFrom().getFullname() &&
                        !item.getForwardFrom().getFullname().isEmpty())) &&
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
            clForwardedQuote.setVisibility(View.VISIBLE);
        } else if (rcivImageBody.getMeasuredWidth() < (llTimestampIconImage.getMeasuredWidth() + TAPUtils.dpToPx(12))) {
            // Thumbnail width may not be smaller than timestamp width (no caption)
            rcivImageBody.getLayoutParams().width = llTimestampIconImage.getMeasuredWidth() + TAPUtils.dpToPx(12);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clForwardedQuote.setVisibility(View.GONE);
        } else if (isMessageFromMySelf(item) && rcivImageBody.getMeasuredWidth() < (tvMessageTimestamp.getMeasuredWidth() + ivMessageStatus.getMeasuredWidth() + TAPUtils.dpToPx(22))) {
            // Thumbnail width may not be smaller than timestamp width (with caption, right bubble)
            rcivImageBody.getLayoutParams().width = tvMessageTimestamp.getMeasuredWidth() + ivMessageStatus.getMeasuredWidth() + TAPUtils.dpToPx(22);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clForwardedQuote.setVisibility(View.GONE);
        } else if (!isMessageFromMySelf(item) && rcivImageBody.getMeasuredWidth() < (tvMessageTimestamp.getMeasuredWidth() + TAPUtils.dpToPx(20))) {
            // Thumbnail width may not be smaller than timestamp width (with caption, left bubble)
            rcivImageBody.getLayoutParams().width = tvMessageTimestamp.getMeasuredWidth() + tvMessageTimestamp.getMeasuredWidth() + TAPUtils.dpToPx(20);
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.CENTER_CROP);
            clForwardedQuote.setVisibility(View.GONE);
        } else {
            // Set default image size
            rcivImageBody.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rcivImageBody.setScaleType(ImageView.ScaleType.FIT_CENTER);
            clForwardedQuote.setVisibility(View.GONE);
        }
    }

    private void setStarredIcon(String id, ImageView imageView) {
        if (starredMessageIds.contains(id) || roomType == RoomType.STARRED) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    // update list when context use star message feature
    public void setStarredMessageIds(List<String> starredMessageIds) {
        this.starredMessageIds.clear();
        this.starredMessageIds.addAll(starredMessageIds);
    }


    private void loadMediaPlayer(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri) {
        try {
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(activity, fileUri);
            audioPlayer.setOnPreparedListener(preparedListener(seekBar, activity, tvDuration, image));
            audioPlayer.setOnCompletionListener(completionListener(seekBar, activity, image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener(Activity activity, TextView tvDuration, ImageView image) {
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
                    audioPlayer.seekTo(audioPlayer.getDuration() * seekBar.getProgress() / seekBar.getMax());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_pause_white));
                audioPlayer.seekTo(audioPlayer.getDuration() * seekBar.getProgress() / seekBar.getMax());
            }
        };
    }

    private MediaPlayer.OnPreparedListener preparedListener(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image) {
        return mediaPlayer -> {
            duration = mediaPlayer.getDuration();
            isMediaPlaying = true;
            startProgressTimer(seekBar, activity);
            tvDuration.setText(TAPUtils.getMediaDurationString(duration, duration));
            mediaPlayer.seekTo(pausedPosition);
            mediaPlayer.setOnSeekCompleteListener(onSeekListener(seekBar, activity, tvDuration));
            audioPlayer = mediaPlayer;
            activity.runOnUiThread(() -> {
                audioPlayer.start();
                image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_pause_white));
            });
        };
    }

    private MediaPlayer.OnSeekCompleteListener onSeekListener(SeekBar seekBar, Activity activity, TextView tvDuration) {
       return mediaPlayer -> {
           try {
               tvDuration.setText(TAPUtils.getMediaDurationString(mediaPlayer.getCurrentPosition(), duration));
           } catch (Exception e) {
               e.printStackTrace();
           }
           if (!isSeeking && isMediaPlaying) {
               mediaPlayer.start();
               startProgressTimer(seekBar, activity);
           } else {
               pausedPosition = mediaPlayer.getCurrentPosition();
               if (pausedPosition >= duration) {
                   pausedPosition = 0;
               }
           }
       };
    }

    private MediaPlayer.OnCompletionListener completionListener(SeekBar seekBar, Activity activity, ImageView image) {
        return mediaPlayer -> setFinishPlayingState(seekBar, activity, image);
    }

    private void setFinishPlayingState(SeekBar seekBar, Activity activity, ImageView image) {
            seekBar.setEnabled(false);
            pausedPosition = 0;
            image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_play_white));
    }

    private void startProgressTimer(SeekBar seekBar, Activity activity) {
        if (null != durationTimer) {
            return;
        }
        durationTimer = new Timer();
        durationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    if (audioPlayer != null) {
                        seekBar.setProgress(audioPlayer.getCurrentPosition() * seekBar.getMax() / duration);
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

    private void playBubbleVoiceNote(SeekBar seekBar, Activity activity, TextView tvDuration, ImageView image, Uri fileUri, int currentPosition) {
        try {
            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();
                voiceUri = fileUri;
                loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri);
                audioPlayer.prepareAsync();
                lastPosition = currentPosition;
            } else {
                if (audioPlayer.isPlaying()) {
                    if (voiceUri != fileUri) {
                        voiceUri = fileUri;
                        pausedPosition = 0;
                        removePlayer();
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri);
                        notifyItemChanged(lastPosition);
                        audioPlayer.prepareAsync();
                        lastPosition = currentPosition;
                    } else {
                        pauseBubbleVoiceNote(image, activity);
                    }
                } else {
                    if (voiceUri != fileUri) {
                        voiceUri = fileUri;
                        pausedPosition = 0;
                        removePlayer();
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri);
                        notifyItemChanged(lastPosition);
                    } else {
                        audioPlayer.release();
                        audioPlayer = null;
                        audioPlayer = new MediaPlayer();
                        loadMediaPlayer(seekBar, activity, tvDuration, image, fileUri);
                    }
                    audioPlayer.prepareAsync();
                    lastPosition = currentPosition;
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

}
