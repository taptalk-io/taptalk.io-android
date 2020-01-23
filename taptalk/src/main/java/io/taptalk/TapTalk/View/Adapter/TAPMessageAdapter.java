package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import io.taptalk.TapTalk.Model.TAPForwardFromModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPQuoteModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity;
import io.taptalk.Taptalk.R;

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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.WIDTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
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
    private TAPChatListener chatListener;
    private TAPMessageModel expandedBubble;
    private TAPUserModel myUserModel;
    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private RequestManager glide;
    private float initialTranslationX = TAPUtils.getInstance().dpToPx(-22);
    private long defaultAnimationTime = 200L;

    public TAPMessageAdapter(RequestManager glide, TAPChatListener chatListener) {
        myUserModel = TAPChatManager.getInstance().getActiveUser();
        if (null == myUserModel) {
            myUserModel = TAPChatManager.getInstance().getActiveUser();
        }
        this.chatListener = chatListener;
        this.glide = glide;
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
            default:
                TAPBaseCustomBubble customBubble = TAPCustomBubbleManager.getInstance().getCustomBubbleMap().get(viewType);
                if (null != customBubble) {
                    return customBubble.createCustomViewHolder(parent, this, myUserModel, customBubble.getCustomBubbleListener());
                }
                return new EmptyVH(parent, R.layout.tap_cell_empty);
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
                case TYPE_LOCATION:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_LOCATION_RIGHT;
                    } else {
                        return TYPE_BUBBLE_LOCATION_LEFT;
                    }
                case TYPE_PRODUCT:
                    return TYPE_BUBBLE_PRODUCT_LIST;
//                case TYPE_ORDER_CARD:
//                    return TYPE_BUBBLE_ORDER_CARD;
                case TYPE_SYSTEM_MESSAGE:
                    return TYPE_BUBBLE_SYSTEM_MESSAGE;
                case TYPE_UNREAD_MESSAGE_IDENTIFIER:
                    return TYPE_BUBBLE_UNREAD_STATUS;
                case TYPE_LOADING_MESSAGE_IDENTIFIER:
                    return TYPE_BUBBLE_LOADING;
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

        private ConstraintLayout clContainer, clForwarded, clQuote;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private TextView tvAvatarLabel, tvUserName, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);

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
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }

            tvMessageBody.setText(item.getBody());

            if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getText(R.string.tap_message_send_failed));
            } else {
                tvMessageStatus.setText(item.getMessageStatusText());
            }

            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            markMessageAsRead(item, myUserModel);
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }
    }

    public class ImageVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer, clForwardedQuote, clQuote, clForwarded;
        private FrameLayout flBubble, flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivImageBody, rcivQuoteImage;
        private ImageView ivMessageStatus, ivReply, ivSending, ivButtonProgress;
        private TextView tvAvatarLabel, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration;
        private ProgressBar pbProgress;

        private Drawable thumbnail;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivImageBody = itemView.findViewById(R.id.rciv_image);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, null);
            }

            tvMessageStatus.setText(item.getMessageStatusText());

            setImageViewButtonProgress(item);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            setProgress(item);
            setImageData(item);
            fixBubbleMarginForGroupRoom(item, flBubble);

            markMessageAsRead(item, myUserModel);
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivImageBody, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void openImageDetailPreview(TAPMessageModel message) {
            Intent intent = new Intent(itemView.getContext(), TAPImageDetailPreviewActivity.class);
            intent.putExtra(MESSAGE, message);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    ((Activity) itemView.getContext()),
                    rcivImageBody,
                    itemView.getContext().getString(R.string.tap_transition_view_image));
            itemView.getContext().startActivity(intent, options.toBundle());
        }

        private void setProgress(TAPMessageModel item) {
            String localID = item.getLocalID();
            Integer uploadProgressValue = TAPFileUploadManager.getInstance().getUploadProgressPercent(localID);
            Integer downloadProgressValue = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID());
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

        private void setImageData(TAPMessageModel item) {
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

            if (((null != item.getQuote() &&
                    null != item.getQuote().getTitle() &&
                    !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() &&
                            null != item.getForwardFrom().getFullname() &&
                            !item.getForwardFrom().getFullname().isEmpty())) &&
                    null != widthDimension &&
                    null != heightDimension) {
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
                rcivImageBody.setTopLeftRadius(0);
                rcivImageBody.setTopRightRadius(0);
                clForwardedQuote.setVisibility(View.VISIBLE);
            } else {
                rcivImageBody.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
                rcivImageBody.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                rcivImageBody.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (isMessageFromMySelf(item)) {
                    rcivImageBody.setTopLeftRadius(TAPUtils.getInstance().dpToPx(9));
                    rcivImageBody.setTopRightRadius(TAPUtils.getInstance().dpToPx(2));
                } else {
                    rcivImageBody.setTopLeftRadius(TAPUtils.getInstance().dpToPx(2));
                    rcivImageBody.setTopRightRadius(TAPUtils.getInstance().dpToPx(9));
                }
                clForwardedQuote.setVisibility(View.GONE);
            }

            if (null == thumbnail) {
                thumbnail = new BitmapDrawable(
                        itemView.getContext().getResources(),
                        TAPFileUtils.getInstance().decodeBase64(
                                (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                        item.getData().get(THUMBNAIL))));
                if (thumbnail.getIntrinsicHeight() <= 0) {
                    // Set placeholder image if thumbnail fails to load
                    thumbnail = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_grey_e4);
                }
            }

            // Set caption
            if (null != imageCaption && !imageCaption.isEmpty()) {
                rcivImageBody.setBottomLeftRadius(0);
                rcivImageBody.setBottomRightRadius(0);
                tvMessageBody.setVisibility(View.VISIBLE);
                tvMessageBody.setText(imageCaption);
                setLinkDetection(itemView.getContext(), tvMessageBody);
            } else {
                rcivImageBody.setBottomLeftRadius(TAPUtils.getInstance().dpToPx(9));
                rcivImageBody.setBottomRightRadius(TAPUtils.getInstance().dpToPx(9));
                tvMessageBody.setVisibility(View.GONE);
            }

            if (null != widthDimension && null != heightDimension) {
                if (0 == widthDimension.intValue() || 0 == heightDimension.intValue()) {
                    rcivImageBody.setImageDimensions(750, 750);
                } else {
                    rcivImageBody.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
                }
            }

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                rcivImageBody.setImageDrawable(thumbnail);
            }

            if (null != imageUrl && !imageUrl.isEmpty()) {
                // Load image from URL
                glide.load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .apply(new RequestOptions()
                                .placeholder(thumbnail)
                                .centerCrop())
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
                                    .into(rcivImageBody);
                            rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
                        });
                    } else {
                        activity.runOnUiThread(() -> rcivImageBody.setOnClickListener(v -> {
                        }));
                        if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                            // Download image
                            if (TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
                                TAPFileDownloadManager.getInstance().downloadImage(TapTalk.appContext, item);
                            } else {
                                activity.runOnUiThread(() -> flProgress.setVisibility(View.GONE));
                                TAPFileDownloadManager.getInstance().addFailedDownload(item.getLocalID());
                            }
                        }
                    }
                }).start();
            } else if (null != imageUri && !imageUri.isEmpty()) {
                // Message is not sent to server, load image from Uri
                rcivImageBody.setOnClickListener(v -> {
                });
                Number size = (Number) item.getData().get(SIZE);
                if (null != size && size.longValue() > TAPFileUploadManager.getInstance().getMaxFileUploadSize()) {
                    activity.runOnUiThread(() -> {
//                        if (isMessageFromMySelf(item)) {
//                            flBubble.setForeground(bubbleOverlayRight);
//                        } else {
//                            flBubble.setForeground(bubbleOverlayLeft);
//                        }
                        rcivImageBody.setImageDrawable(thumbnail);
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
                                .into(rcivImageBody);
                    });
                }
            }
        }

        private void setImageViewButtonProgress(TAPMessageModel item) {
            if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownload)));
                flProgress.setOnClickListener(v -> resendMessage(item));
            } else if ((null == TAPFileUploadManager.getInstance().getUploadProgressPercent(item.getLocalID())
                    && null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID()))
                    || null != TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownload)));
                flProgress.setOnClickListener(v -> {
                });
            } else {
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownload)));
                flProgress.setOnClickListener(v -> TAPDataManager.getInstance()
                        .cancelUploadImage(itemView.getContext(), item.getLocalID()));
            }
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }
    }

    public class VideoVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer, clForwardedQuote, clQuote, clForwarded;
        private FrameLayout flBubble, flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivVideoThumbnail, rcivQuoteImage;
        private ImageView ivMessageStatus, ivReply, ivSending, ivButtonProgress;
        private TextView tvAvatarLabel, tvMediaInfo, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration;
        private ProgressBar pbProgress;

        private Uri videoUri;
        private Drawable thumbnail;

        VideoVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivVideoThumbnail = itemView.findViewById(R.id.rciv_image);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            tvMediaInfo = itemView.findViewById(R.id.tv_media_info);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);

            if (bubbleType == TYPE_BUBBLE_VIDEO_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, null);
            }

            tvMediaInfo.setVisibility(View.VISIBLE);
            tvMessageStatus.setText(item.getMessageStatusText());

            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            setVideoProgress(item);
            fixBubbleMarginForGroupRoom(item, flBubble);

            markMessageAsRead(item, myUserModel);
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivVideoThumbnail, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void setVideoProgress(TAPMessageModel item) {
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
            String key = TAPUtils.getInstance().getUriKeyFromMessage(item);

            Integer uploadProgressPercent = TAPFileUploadManager.getInstance().getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(localID);
            videoUri = null != dataUri ? Uri.parse(dataUri) : TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), key);

            if (((null != item.getQuote() &&
                    null != item.getQuote().getTitle() &&
                    !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() &&
                            null != item.getForwardFrom().getFullname() &&
                            !item.getForwardFrom().getFullname().isEmpty())) &&
                    null != widthDimension &&
                    null != heightDimension) {
                // Fix layout when quote/forward exists
                float imageRatio = widthDimension.floatValue() / heightDimension.floatValue();
                // Set image width to maximum
                rcivVideoThumbnail.getLayoutParams().width = 0;
                if (imageRatio > (float) rcivVideoThumbnail.getMaxWidth() / (float) rcivVideoThumbnail.getMinHeight()) {
                    // Set minimum height if image width exceeds limit
                    rcivVideoThumbnail.getLayoutParams().height = rcivVideoThumbnail.getMinHeight();
                } else if (imageRatio < (float) rcivVideoThumbnail.getMaxHeight() / (float) rcivVideoThumbnail.getMaxWidth()) {
                    // Set maximum height if image height exceeds limit
                    rcivVideoThumbnail.getLayoutParams().height = rcivVideoThumbnail.getMaxHeight();
                } else {
                    // Set default image height
                    rcivVideoThumbnail.getLayoutParams().height = (int) (rcivVideoThumbnail.getMaxWidth() * heightDimension.floatValue() / widthDimension.floatValue());
                }
                rcivVideoThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivVideoThumbnail.setTopLeftRadius(0);
                rcivVideoThumbnail.setTopRightRadius(0);
                clForwardedQuote.setVisibility(View.VISIBLE);
            } else {
                rcivVideoThumbnail.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
                rcivVideoThumbnail.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                rcivVideoThumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (isMessageFromMySelf(item)) {
                    rcivVideoThumbnail.setTopLeftRadius(TAPUtils.getInstance().dpToPx(9));
                    rcivVideoThumbnail.setTopRightRadius(TAPUtils.getInstance().dpToPx(2));
                } else {
                    rcivVideoThumbnail.setTopLeftRadius(TAPUtils.getInstance().dpToPx(2));
                    rcivVideoThumbnail.setTopRightRadius(TAPUtils.getInstance().dpToPx(9));
                }
                clForwardedQuote.setVisibility(View.GONE);
            }

            if (null == thumbnail) {
                thumbnail = new BitmapDrawable(
                        itemView.getContext().getResources(),
                        TAPFileUtils.getInstance().decodeBase64(
                                (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                        item.getData().get(THUMBNAIL))));
                if (thumbnail.getIntrinsicHeight() <= 0) {
                    // Set placeholder image if thumbnail fails to load
                    thumbnail = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_grey_e4);
                }
            }

            // Set caption
            if (null != videoCaption && !videoCaption.isEmpty()) {
                rcivVideoThumbnail.setBottomLeftRadius(0);
                rcivVideoThumbnail.setBottomRightRadius(0);
                tvMessageBody.setVisibility(View.VISIBLE);
                tvMessageBody.setText(videoCaption);
                setLinkDetection(itemView.getContext(), tvMessageBody);
            } else {
                rcivVideoThumbnail.setBottomLeftRadius(TAPUtils.getInstance().dpToPx(9));
                rcivVideoThumbnail.setBottomRightRadius(TAPUtils.getInstance().dpToPx(9));
                tvMessageBody.setVisibility(View.GONE);
            }

            if (null != widthDimension && null != heightDimension) {
                rcivVideoThumbnail.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
            }

            // Fix media info text width
            rcivVideoThumbnail.post(() -> {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) tvMediaInfo.getLayoutParams();
                params.matchConstraintMaxWidth = rcivVideoThumbnail.getMeasuredWidth() - TAPUtils.getInstance().dpToPx(16);
                tvMediaInfo.setLayoutParams(params);
            });

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID()) || null != dataUri) {
                rcivVideoThumbnail.setImageDrawable(thumbnail);
            }

            if (null != item.getFailedSend() && item.getFailedSend()) {
                // Message failed to send
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                tvMediaInfo.setText(size == null ? "" : TAPUtils.getInstance().getStringSizeLengthFile(size.longValue()));
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownload)));
                pbProgress.setVisibility(View.GONE);
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
                        .into(rcivVideoThumbnail);
            } else if ((((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent) && null != videoUri &&
                    TAPFileDownloadManager.getInstance().checkPhysicalFileExists(item))) {
                // Video has finished downloading or uploading
                tvMediaInfo.setText(null == duration ? "" : TAPUtils.getInstance().getMediaDurationString(duration.intValue(), duration.intValue()));
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_play_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFilePlayMedia)));
                pbProgress.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item, key, TAPFileDownloadManager.getInstance().checkPhysicalFileExists(item)));
                new Thread(() -> {
                    BitmapDrawable videoThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(key);
                    if (null == videoThumbnail) {
                        // Get full-size thumbnail from Uri
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        try {
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
                                    .into(rcivVideoThumbnail);
                            rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item, key, TAPFileDownloadManager.getInstance().checkPhysicalFileExists(item)));
                        });
                    }
                }).start();
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent)) {
                // Video is not downloaded
                String videoSize = null == size ? "" : TAPUtils.getInstance().getStringSizeLengthFile(size.longValue());
                String videoDuration = null == duration ? "" : TAPUtils.getInstance().getMediaDurationString(duration.intValue(), duration.intValue());
                tvMediaInfo.setText(String.format("%s%s%s", videoSize, (videoSize.isEmpty() || videoDuration.isEmpty()) ? "" : " - ", videoDuration));
                if (TAPFileDownloadManager.getInstance().getFailedDownloads().contains(item.getLocalID())) {
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownload)));
                } else {
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_white));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownload)));
                }
                pbProgress.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> downloadVideo(item));
            } else {
                // File is downloading or uploading
                ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownload)));
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
                if (null != uploadProgressPercent) {
                    Long uploadProgressBytes = TAPFileUploadManager.getInstance().getUploadProgressBytes(localID);
                    tvMediaInfo.setText(TAPUtils.getInstance().getFileDisplayProgress(item, uploadProgressBytes));
                    pbProgress.setProgress(uploadProgressPercent);
                    rcivVideoThumbnail.setOnClickListener(v -> cancelUpload(item));
                } else {
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance().getDownloadProgressBytes(localID);
                    tvMediaInfo.setText(TAPUtils.getInstance().getFileDisplayProgress(item, downloadProgressBytes));
                    pbProgress.setProgress(downloadProgressPercent);
                    rcivVideoThumbnail.setOnClickListener(v -> cancelDownload(item));
                }
            }
        }

        private void downloadVideo(TAPMessageModel item) {
            Intent intent = new Intent(DownloadFile);
            intent.putExtra(MESSAGE, item);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void cancelDownload(TAPMessageModel item) {
            Intent intent = new Intent(CancelDownload);
            intent.putExtra(DownloadLocalID, item.getLocalID());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void cancelUpload(TAPMessageModel item) {
            Intent intent = new Intent(UploadCancelled);
            intent.putExtra(UploadLocalID, item.getLocalID());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void openVideoPlayer(TAPMessageModel message, String key, boolean isPhysicalFileExist) {
            if (null == message.getData()) {
                return;
            }
            Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(message.getRoom().getRoomID(), key);
            if (null == videoUri || !isPhysicalFileExist) {
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
                TAPUtils.getInstance().openVideoPreview(itemView.getContext(), videoUri, message);
            }
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }
    }

    public class FileVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer, clForwarded, clQuote;
        private FrameLayout flBubble, flFileIcon;
        private CircleImageView civAvatar;
        private ImageView ivFileIcon, ivMessageStatus, ivReply, ivSending;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private TextView tvAvatarLabel, tvUserName, tvFileName, tvFileInfo, tvFileInfoDummy, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration;
        private ProgressBar pbProgress;

        private Uri fileUri;

        FileVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            clQuote = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flFileIcon = itemView.findViewById(R.id.fl_file_icon);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
            ivReply = itemView.findViewById(R.id.iv_reply);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileInfo = itemView.findViewById(R.id.tv_file_info);
            tvFileInfoDummy = itemView.findViewById(R.id.tv_file_info_dummy);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvForwardedFrom = itemView.findViewById(R.id.tv_forwarded_from);
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            pbProgress = itemView.findViewById(R.id.pb_progress);

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
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }

            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            setFileProgress(item);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            flBubble.setOnClickListener(v -> flFileIcon.performClick());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        private void setFileProgress(TAPMessageModel item) {
            if (null == item.getData()) {
                return;
            }
            String localID = item.getLocalID();
            Integer uploadProgressPercent = TAPFileUploadManager.getInstance().getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(localID);
            String key = TAPUtils.getInstance().getUriKeyFromMessage(item);
            fileUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), key);

            tvFileName.setText(TAPUtils.getInstance().getFileDisplayName(item));
            tvFileInfoDummy.setText(TAPUtils.getInstance().getFileDisplayDummyInfo(itemView.getContext(), item));

            if (null != item.getFailedSend() && item.getFailedSend()) {
                // Message failed to send
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownload)));
                pbProgress.setVisibility(View.GONE);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                if (isMessageFromMySelf(item)) {
                    flFileIcon.setOnClickListener(v -> resendMessage(item));
                } else {
                    flFileIcon.setOnClickListener(v -> downloadFile(item));
                }
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent) && null != fileUri &&
                    TAPFileDownloadManager.getInstance().checkPhysicalFileExists(item)) {
                // File has finished downloading or uploading
                tvMessageStatus.setText(item.getMessageStatusText());
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_documents_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFile)));
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> openFile(item));
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent)) {
                // File is not downloaded
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                if (TAPFileDownloadManager.getInstance().getFailedDownloads().contains(item.getLocalID())) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                    ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_white));
                    ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileRetryUploadDownload)));
                } else {
                    tvMessageStatus.setText(item.getMessageStatusText());
                    ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_white));
                    ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownload)));
                }
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> downloadFile(item));
            } else {
                // File is downloading or uploading
                ivFileIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                ImageViewCompat.setImageTintList(ivFileIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownload)));
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
                tvMessageStatus.setText(item.getMessageStatusText());
                if (null != uploadProgressPercent) {
                    Long uploadProgressBytes = TAPFileUploadManager.getInstance().getUploadProgressBytes(localID);
                    tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayProgress(item, uploadProgressBytes));
                    pbProgress.setProgress(uploadProgressPercent);
                    flFileIcon.setOnClickListener(v -> cancelUpload(item));
                } else {
                    Long downloadProgressBytes = TAPFileDownloadManager.getInstance().getDownloadProgressBytes(localID);
                    tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayProgress(item, downloadProgressBytes));
                    pbProgress.setProgress(downloadProgressPercent);
                    flFileIcon.setOnClickListener(v -> cancelDownload(item));
                }
            }
        }

        private void downloadFile(TAPMessageModel item) {
            Intent intent = new Intent(DownloadFile);
            intent.putExtra(MESSAGE, item);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void cancelDownload(TAPMessageModel item) {
            Intent intent = new Intent(CancelDownload);
            intent.putExtra(DownloadLocalID, item.getLocalID());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void cancelUpload(TAPMessageModel item) {
            Intent intent = new Intent(UploadCancelled);
            intent.putExtra(UploadLocalID, item.getLocalID());
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }

        private void openFile(TAPMessageModel item) {
            Intent intent = new Intent(OpenFile);
            intent.putExtra(MESSAGE, item);
            intent.putExtra(FILE_URI, fileUri);
            LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
        }
    }

    public class LocationVH extends TAPBaseChatViewHolder {

        private ConstraintLayout clContainer, clForwardedQuote, clQuote, clForwarded;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivQuoteImage;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvAvatarLabel, tvUserName, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration, vMapBorder;
        private MapView mapView;

        LocationVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clForwardedQuote = itemView.findViewById(R.id.cl_forwarded_quote); // Container for quote and forwarded layouts
            clQuote = itemView.findViewById(R.id.cl_quote);
            clForwarded = itemView.findViewById(R.id.cl_forwarded);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            rcivQuoteImage = itemView.findViewById(R.id.rciv_quote_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
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

            if (bubbleType == TYPE_BUBBLE_LOCATION_LEFT) {
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
            if (null == mapData) {
                return;
            }

            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, ivMessageStatus, ivSending, civAvatar, tvAvatarLabel, tvUserName);
            }

            if (null == item.getFailedSend() || (null != item.getFailedSend() && !item.getFailedSend())) {
                tvMessageStatus.setText(item.getMessageStatusText());
            }

            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            fixBubbleMarginForGroupRoom(item, flBubble);

            if ((null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty()) ||
                    (null != tvUserName && View.VISIBLE == tvUserName.getVisibility() && null != item.getRoom() && TYPE_GROUP == item.getRoom().getRoomType())) {
                // Fix layout when quote/forward exists
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mapView.setOutlineProvider(null);
                }
                clForwardedQuote.setVisibility(View.VISIBLE);
                vMapBorder.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_stroke_e4e4e4_1dp_insettop_insetbottom_1dp));
            } else {
                if (isMessageFromMySelf(item)) {
                    TAPUtils.getInstance().clipToRoundedRectangle(mapView, TAPUtils.getInstance().dpToPx(12), TAPUtils.ClipType.TOP_LEFT);
                    vMapBorder.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_rounded_8dp_1dp_0dp_0dp_stroke_e4e4e4_1dp_insetbottom_1dp));
                } else {
                    TAPUtils.getInstance().clipToRoundedRectangle(mapView, TAPUtils.getInstance().dpToPx(12), TAPUtils.ClipType.TOP_RIGHT);
                    vMapBorder.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_rounded_1dp_8dp_0dp_0dp_stroke_e4e4e4_1dp_insetbottom_1dp));
                }
                clForwardedQuote.setVisibility(View.GONE);
            }
            setMapData(mapData);

            markMessageAsRead(item, myUserModel);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), vMapBorder, item);

            vMapBorder.setOnClickListener(v -> openMapDetail(mapData));
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void setMapData(HashMap<String, Object> mapData) {
            if (null == mapData.get(ADDRESS) || null == mapData.get(LATITUDE) || null == mapData.get(LONGITUDE)) {
                return;
            }
            tvMessageBody.setText((String) mapData.get(ADDRESS));
            mapView.getMapAsync(googleMap -> {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        ((Number) mapData.get(LATITUDE)).doubleValue(),
                        ((Number) mapData.get(LONGITUDE)).doubleValue()), 16f));
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                mapView.onResume();
            });
        }

        private void openMapDetail(HashMap<String, Object> mapData) {
            Number latitude = null != mapData.get(LATITUDE) ? ((Number) mapData.get(LATITUDE)).doubleValue() : 0.0;
            Number longitude = null != mapData.get(LONGITUDE) ? ((Number) mapData.get(LONGITUDE)).doubleValue() : 0.0;
            TAPUtils.getInstance().openMaps((Activity) itemView.getContext(), latitude.doubleValue(), longitude.doubleValue());
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }
    }

    private void fixBubbleMarginForGroupRoom(TAPMessageModel item, View flBubble) {
        if (isMessageFromMySelf(item) || !(flBubble.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            return;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) flBubble.getLayoutParams();
        if (item.getRoom().getRoomType() == TYPE_GROUP) {
            // Fix bubble margin on group room
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(TAPUtils.getInstance().dpToPx(64));
            } else {
                params.rightMargin = TAPUtils.getInstance().dpToPx(64);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(TAPUtils.getInstance().dpToPx(110));
            } else {
                params.rightMargin = TAPUtils.getInstance().dpToPx(110);
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
            if (null != item.getData())
                items = TAPUtils.getInstance().convertObject(item.getData().get("items")
                        , new TypeReference<List<TAPProductModel>>() {
                        });
            else items = new ArrayList<>();
            adapter = new TAPProductListAdapter(items, item, myUserModel, chatListener);
            markMessageAsRead(item, myUserModel);

            rvProductList.setAdapter(adapter);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            if (rvProductList.getItemDecorationCount() > 0) {
                rvProductList.removeItemDecorationAt(0);
            }
            rvProductList.addItemDecoration(new TAPHorizontalDecoration(
                    0, 0,
                    TAPUtils.getInstance().dpToPx(16),
                    TAPUtils.getInstance().dpToPx(8),
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
            if (null != item.getAction()) {
                String systemMessageAction = item.getAction() != null ?
                        item.getAction() : "";

                switch (systemMessageAction) {
                    case CREATE_ROOM:
                    case UPDATE_ROOM:
                    case DELETE_ROOM:
                    case LEAVE_ROOM:
                        item.setBody(TAPChatManager.getInstance().formattingSystemMessage(item));
                        break;

                    case ROOM_ADD_PARTICIPANT:
                    case ROOM_REMOVE_PARTICIPANT:
                    case ROOM_PROMOTE_ADMIN:
                    case ROOM_DEMOTE_ADMIN:
                        item.setBody(TAPChatManager.getInstance().formattingSystemMessage(item));
                        break;

                    default:
                        item.setBody(TAPChatManager.getInstance().formattingSystemMessage(item));
                }
            }
            markMessageAsRead(item, myUserModel);
            tv_message.setText(item.getBody());
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            enableLongPress(itemView.getContext(), clContainer, item);
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
            tvLogMessage.setText(TAPUtils.getInstance().toJsonString(item));
            //tvLogMessage.setText(item.getBody());
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            markMessageAsRead(item, myUserModel);
        }
    }

    public class LoadingVH extends TAPBaseChatViewHolder {

        ImageView ivLoadingProgress;

        LoadingVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivLoadingProgress = itemView.findViewById(R.id.iv_loading_progress);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == ivLoadingProgress.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(itemView.getContext(), ivLoadingProgress);
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
            super.onBind(item, position);
            markMessageAsRead(item, myUserModel);
        }
    }

    public class EmptyVH extends TAPBaseChatViewHolder {

        EmptyVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            markMessageAsRead(item, myUserModel);
            // TODO: 28 June 2019 CHECK MESSAGE STATUS FOR DELETED MESSAGE
        }
    }

    public class DeletedVH extends TAPBaseChatViewHolder {

        CircleImageView civAvatar;
        TextView tvAvatarLabel, tvUserName;

        protected DeletedVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);
            if (bubbleType == TYPE_BUBBLE_DELETED_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
                tvUserName = itemView.findViewById(R.id.tv_user_name);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, null, null, civAvatar, tvAvatarLabel, tvUserName);
            }
        }
    }

    private void setMessageItem(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        // Message failed to send
        if (null != item.getFailedSend() && item.getFailedSend()) {
            tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
            if (null != ivMessageStatus) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_circle_transparent));
                ImageViewCompat.setImageTintList(ivMessageStatus, null);
                ivMessageStatus.setVisibility(View.VISIBLE);
            }
            if (null != ivSending) {
                ivSending.setAlpha(0f);
            }
            flBubble.setTranslationX(0);
            tvMessageStatus.setVisibility(View.VISIBLE);
            if (null != ivReply) {
                ivReply.setVisibility(View.GONE);
            }
        }
        // Message is sending
        else if (null != item.getSending() && item.getSending()) {
            item.setNeedAnimateSend(true);
            tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_sending));

            flBubble.setTranslationX(initialTranslationX);
            if (null != ivSending) {
                ivSending.setTranslationX(0);
                ivSending.setTranslationY(0);
                ivSending.setAlpha(1f);
            }
            if (null != ivMessageStatus) {
                ivMessageStatus.setVisibility(View.GONE);
            }
            if (null != ivReply) {
                ivReply.setVisibility(View.GONE);
            }
            tvMessageStatus.setVisibility(View.GONE);
        }
    }

    private void receiveSentEmit(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                 TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                 @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageSent)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        // Show status text and reply button for non-text bubbles
        if (item.getType() == TYPE_TEXT) {
            tvMessageStatus.setVisibility(View.GONE);
        } else if (null != ivReply) {
            tvMessageStatus.setVisibility(View.VISIBLE);
            tvMessageStatus.post(() -> chatListener.onLayoutLoaded(item));
            ivReply.setVisibility(View.VISIBLE);
        }
        animateSend(item, flBubble, ivSending, ivMessageStatus, ivReply);
    }

    private void receiveReadEmit(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                 TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                 @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageRead)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivSending) {
            ivSending.setAlpha(0f);
        }
        flBubble.setTranslationX(0);
        // Show status text and reply button for non-text bubbles
        if (item.getType() == TYPE_TEXT) {
            tvMessageStatus.setVisibility(View.GONE);
        } else if (null != ivReply) {
            tvMessageStatus.setVisibility(View.VISIBLE);
            ivReply.setVisibility(View.VISIBLE);
        }
    }

    private void receiveDeliveredEmit(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                      TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                      @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageDelivered)));
            ivMessageStatus.setVisibility(View.VISIBLE);
        }
        if (null != ivSending) {
            ivSending.setAlpha(0f);
        }
        flBubble.setTranslationX(0);
        tvMessageStatus.setVisibility(View.GONE);
        // Show status text and reply button for non-text bubbles
        if (item.getType() == TYPE_TEXT) {
            tvMessageStatus.setVisibility(View.GONE);
        } else if (null != ivReply) {
            tvMessageStatus.setVisibility(View.VISIBLE);
            ivReply.setVisibility(View.VISIBLE);
        }
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
                vh.receiveReadEvent(item);
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                vh.receiveDeliveredEvent(item);
            } else if (null != item.getFailedSend() && item.getFailedSend()) {
                vh.setMessage(item);
            }
            // Message sent
            else if ((null != item.getSending() && !item.getSending())) {
                vh.receiveSentEvent(item);
            } else {
                vh.setMessage(item);
            }
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
        } else {
            // Message from others
            if (item.getRoom().getRoomType() == TYPE_PERSONAL) {
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
                TAPUserModel user = TAPContactManager.getInstance().getUserData(item.getUser().getUserID());
                if (null != civAvatar && null != tvAvatarLabel && null != user && null != user.getAvatarURL() && !user.getAvatarURL().getThumbnail().isEmpty()) {
                    glide.load(user.getAvatarURL().getThumbnail()).into(civAvatar);
                    ImageViewCompat.setImageTintList(civAvatar, null);
                    civAvatar.setVisibility(View.VISIBLE);
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel && null != item.getUser().getAvatarURL() && !item.getUser().getAvatarURL().getThumbnail().isEmpty()) {
                    glide.load(item.getUser().getAvatarURL().getThumbnail()).into(civAvatar);
                    ImageViewCompat.setImageTintList(civAvatar, null);
                    civAvatar.setVisibility(View.VISIBLE);
                    tvAvatarLabel.setVisibility(View.GONE);
                } else if (null != civAvatar && null != tvAvatarLabel) {
//                    civAvatar.setImageDrawable(vh.itemView.getContext().getDrawable(R.drawable.tap_img_default_avatar));
                    ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vh.itemView.getContext(), item.getUser().getName())));
                    civAvatar.setImageDrawable(ContextCompat.getDrawable(vh.itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                    tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(item.getUser().getName(), 2));
                    civAvatar.setVisibility(View.VISIBLE);
                    tvAvatarLabel.setVisibility(View.VISIBLE);
                }
                if (null != tvUserName) {
                    tvUserName.setText(item.getUser().getName());
                    tvUserName.setVisibility(View.VISIBLE);
                }
                if (null != civAvatar) {
                    civAvatar.setOnClickListener(v -> {
                        // Open group member profile
                        Activity activity = (Activity) vh.itemView.getContext();
                        if (null == activity) {
                            return;
                        }
                        chatListener.onGroupMemberAvatarClicked(item);
                    });
                }
            }
            chatListener.onMessageRead(item);
        }
    }

    private void expandOrShrinkBubble(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                      TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                      ImageView ivReply, boolean animate) {
        if (item.isExpanded()) {
            // Expand bubble
            expandedBubble = item;
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                // Right Bubble
                if (animate) {
                    // Animate expand
                    animateFadeInToBottom(tvMessageStatus);
                    animateFadeOutToBottom(ivMessageStatus);
                    animateShowToLeft(ivReply);
                } else {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivReply.setVisibility(View.VISIBLE);
                }
                if (null == bubbleOverlayRight) {
                    bubbleOverlayRight = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_transparent_black_8dp_1dp_8dp_8dp);
                }
                flBubble.setForeground(bubbleOverlayRight);
            } else {
                // Left Bubble
                if (animate) {
                    // Animate expand
                    animateFadeInToBottom(tvMessageStatus);
                    animateShowToRight(ivReply);
                } else {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivReply.setVisibility(View.VISIBLE);
                }
                if (null == bubbleOverlayLeft) {
                    bubbleOverlayLeft = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_transparent_black_1dp_8dp_8dp_8dp);
                }
                flBubble.setForeground(bubbleOverlayLeft);
            }
        } else {
            // Shrink bubble
            flBubble.setForeground(null);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                // Right bubble
                if ((null != item.getFailedSend() && item.getFailedSend())) {
                    // Message failed to send
                    ivReply.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_retry_circle_transparent));
                    ImageViewCompat.setImageTintList(ivMessageStatus, null);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                } else if (null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        // Message has been read
                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageRead)));
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        // Message is delivered
                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageDelivered)));
                    } else if (null != item.getSending() && !item.getSending()) {
                        // Message sent
                        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
                        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageSent)));
                    }
                    if (animate) {
                        // Animate shrink
                        animateHideToRight(ivReply);
                        animateFadeInToTop(ivMessageStatus);
                        animateFadeOutToTop(tvMessageStatus);
                    } else {
                        ivReply.setVisibility(View.GONE);
                        ivMessageStatus.setVisibility(View.VISIBLE);
                        tvMessageStatus.setVisibility(View.GONE);
                    }
                } else if (null != item.getSending() && item.getSending()) {
                    // Message is sending
                    ivReply.setVisibility(View.GONE);
                }
            }
            // Message from others
            else if (animate) {
                // Animate shrink
                animateHideToLeft(ivReply);
                animateFadeOutToTop(tvMessageStatus);
            } else {
                ivReply.setVisibility(View.GONE);
                tvMessageStatus.setVisibility(View.GONE);
            }
        }
    }

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
                    && item.getReplyTo().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                tvQuoteTitle.setText(itemView.getResources().getString(R.string.tap_you));
            } else {
                tvQuoteTitle.setText(quote.getTitle());
            }

            tvQuoteContent.setText(quote.getContent());
            String quoteImageURL = quote.getImageURL();
            String quoteFileID = quote.getFileID();
            if (null != quoteImageURL && !quoteImageURL.isEmpty() &&
                    (quote.getFileType().equals(String.valueOf(TYPE_IMAGE)) ||
                            quote.getFileType().equals(IMAGE))) {
                // Get quote image from URL
                glide.load(quoteImageURL).into(rcivQuoteImage);
                ImageViewCompat.setImageTintList(rcivQuoteImage, null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setVisibility(View.VISIBLE);
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
                    key = TAPUtils.getInstance().removeNonAlphaNumeric(quoteImageURL).toLowerCase();
                }
                // Get quote image from cache
                // TODO: 8 March 2019 IMAGE MIGHT NOT EXIST IN CACHE
                if (!key.isEmpty()) {
                    String finalKey = key;
                    new Thread(() -> {
                        BitmapDrawable image = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(finalKey);
                        ((Activity) itemView.getContext()).runOnUiThread(() -> {
                            ImageViewCompat.setImageTintList(rcivQuoteImage, null);
                            rcivQuoteImage.setImageDrawable(image);
                            rcivQuoteImage.setBackground(null);
                            rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        });
                    }).start();
                }
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteContent.setMaxLines(1);
            } else if (quote.getFileType().equals(String.valueOf(TYPE_FILE)) || quote.getFileType().equals(FILE)) {
                // Load file icon
                glide.clear(rcivQuoteImage);
                rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_documents_white));
                ImageViewCompat.setImageTintList(rcivQuoteImage, tvQuoteTitle.getTextColors());
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), true);
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show no image
                updateQuoteBackground(itemView, vQuoteBackground, isMessageFromMySelf(item), false);
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);
                tvQuoteContent.setMaxLines(2);
            }
            clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
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

    private void onBubbleClicked(TAPMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, ImageView ivMessageStatus, ImageView ivReply) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        } else if (item.getType() == TYPE_TEXT &&
                ((null != item.getSending() && !item.getSending()) ||
                        (null != item.getDelivered() && item.getDelivered()) ||
                        (null != item.getIsRead() && item.getIsRead()))) {
            if (item.isExpanded()) {
                // Shrink bubble
                item.setExpanded(false);
            } else {
                // Expand clicked bubble
                shrinkExpandedBubble();
                item.setExpanded(true);
            }
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, true);
        }
    }

    private void onStatusImageClicked(TAPMessageModel item) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(TAPMessageModel item) {
        chatListener.onReplyMessage(item);
    }

    private void resendMessage(TAPMessageModel item) {
        removeMessage(item);
        chatListener.onRetrySendMessage(item);
    }

    private void animateSend(TAPMessageModel item, FrameLayout flBubble,
                             ImageView ivSending, ImageView ivMessageStatus,
                             @Nullable ImageView ivReply) {
        if (!item.isNeedAnimateSend()) {
            // Set bubble state to post-animation
            flBubble.setTranslationX(0);
            ivMessageStatus.setTranslationX(0);
            ivSending.setAlpha(0f);
        } else {
            // Animate bubble
            item.setNeedAnimateSend(false);
            item.setAnimating(true);
            flBubble.setTranslationX(initialTranslationX);
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            new Handler().postDelayed(() -> {
                flBubble.animate()
                        .translationX(0)
                        .setDuration(160L)
                        .start();
                ivSending.animate()
                        .translationX(TAPUtils.getInstance().dpToPx(36))
                        .translationY(TAPUtils.getInstance().dpToPx(-23))
                        .setDuration(360L)
                        .setInterpolator(new AccelerateInterpolator(0.5f))
                        .withEndAction(() -> {
                            ivSending.setAlpha(0f);
                            item.setAnimating(false);
                            if ((null != item.getIsRead() && item.getIsRead()) ||
                                    (null != item.getDelivered() && item.getDelivered())) {
                                notifyItemChanged(getItems().indexOf(item));
                            }
                        })
                        .start();
            }, 200L);

            // Animate reply button
            if (null != ivReply) {
                animateShowToLeft(ivReply);
            }
        }
    }

    private void animateFadeInToTop(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(TAPUtils.getInstance().dpToPx(24));
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
        view.setTranslationY(TAPUtils.getInstance().dpToPx(-24));
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
                .translationY(TAPUtils.getInstance().dpToPx(-24))
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
                .translationY(TAPUtils.getInstance().dpToPx(24))
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
        view.setTranslationX(TAPUtils.getInstance().dpToPx(32));
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
        view.setTranslationX(TAPUtils.getInstance().dpToPx(-32));
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
                .translationX(TAPUtils.getInstance().dpToPx(-32))
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
                .translationX(TAPUtils.getInstance().dpToPx(32))
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

    public void shrinkExpandedBubble() {
        if (null == expandedBubble) {
            return;
        }
        expandedBubble.setExpanded(false);
        notifyItemChanged(getItems().indexOf(expandedBubble));
    }
}
