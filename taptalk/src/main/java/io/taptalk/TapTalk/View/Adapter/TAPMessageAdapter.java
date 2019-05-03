package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.TAPForwardFromModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPQuoteModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_FILE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_FILE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_LOCATION_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_LOCATION_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.HEIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.USER_INFO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.WIDTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPMessageAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseChatViewHolder> {

    private static final String TAG = TAPMessageAdapter.class.getSimpleName();
    private TAPChatListener chatListener;
    private TAPMessageModel expandedBubble;
    private TAPUserModel myUserModel;
    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private float initialTranslationX = TAPUtils.getInstance().dpToPx(-16);
    private long defaultAnimationTime = 200L;
    private RequestManager glide;

    public TAPMessageAdapter(RequestManager glide, TAPChatListener chatListener) {
        myUserModel = TAPChatManager.getInstance().getActiveUser();
        if (null == myUserModel)
            myUserModel = TAPChatManager.getInstance().getActiveUser();
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
            case TYPE_BUBBLE_ORDER_CARD:
                TAPBaseCustomBubble orderBubble = TAPCustomBubbleManager.getInstance().getCustomBubbleMap().get(TYPE_BUBBLE_ORDER_CARD);
                return orderBubble.createCustomViewHolder(parent, this, myUserModel, orderBubble.getCustomBubbleListener());
            case TYPE_EMPTY:
                return new EmptyVH(parent, R.layout.tap_cell_empty);
            default:
                return new LogVH(parent, R.layout.tap_cell_chat_log);
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
                case TYPE_ORDER_CARD:
                    return TYPE_BUBBLE_ORDER_CARD;
                default:
                    return TYPE_LOG;
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
        private TextView tvUsername, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
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
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (item.isAnimating()) {
                return;
            }

            tvMessageBody.setText(item.getBody());
            tvMessageStatus.setText(item.getMessageStatusText());
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);

            markMessageAsRead(item, myUserModel);

            checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, tvUsername);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));

            // TODO: 6 February 2019 TEMPORARY LISTENER FOR QUOTE
            if (null != item.getData() && item.getData().containsKey(USER_INFO)) {
                clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
            } else {
                clQuote.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            }
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
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
        private TextView tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
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
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, null);
            }

            tvMessageStatus.setText(item.getMessageStatusText());
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivImageBody, item);

            setImageViewButtonProgress(item);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);

            markMessageAsRead(item, myUserModel);
            setProgress(item);
            setImageData(item);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));

            // TODO: 6 February 2019 TEMPORARY LISTENER FOR QUOTE
            if (null != item.getData() && item.getData().containsKey(USER_INFO)) {
                clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
            }
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
                flBubble.setForeground(null);
            } else {
                flProgress.setVisibility(View.VISIBLE);
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
                if (null != uploadProgressValue)
                    pbProgress.setProgress(uploadProgressValue);
                else pbProgress.setProgress(downloadProgressValue);
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
            String imageCaption = (String) item.getData().get(CAPTION);
            String fileID = (String) item.getData().get(FILE_ID);

            if ((null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty())) {
                // Fix layout when quote/forward exists
                rcivImageBody.getLayoutParams().width = 0;
                if (null != widthDimension && null != heightDimension && (widthDimension.floatValue() / heightDimension.floatValue()) > 3) {
                    rcivImageBody.getLayoutParams().height = TAPUtils.getInstance().dpToPx(78);
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
                    thumbnail = itemView.getContext().getDrawable(R.drawable.tap_bg_grey_e4);
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

            // TODO: 18 January 2019 TEMP CHECK
            if (null != widthDimension && null != heightDimension) {
                rcivImageBody.setImageDimensions(widthDimension.intValue(), heightDimension.intValue());
            }

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                rcivImageBody.setImageDrawable(thumbnail);
            }

            if (null != fileID && !fileID.isEmpty()) {
                Drawable finalThumbnail = thumbnail;
                new Thread(() -> {
                    BitmapDrawable cachedImage = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(fileID);
                    if (null != cachedImage) {
                        // Load image from cache
                        activity.runOnUiThread(() -> {
                            glide.load(cachedImage)
                                    .transition(DrawableTransitionOptions.withCrossFade(100))
                                    .apply(new RequestOptions()
                                            .placeholder(finalThumbnail)
                                            .centerCrop())
                                    .into(rcivImageBody);
                            rcivImageBody.setOnClickListener(v -> openImageDetailPreview(item));
                        });
                    } else {
                        activity.runOnUiThread(() -> rcivImageBody.setOnClickListener(v -> {
                        }));
                        if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                            // Download image
                            if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
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
                rcivImageBody.setOnClickListener(v -> {});
                Drawable finalThumbnail1 = thumbnail;
                activity.runOnUiThread(() -> {
                    if (isMessageFromMySelf(item)) {
                        flBubble.setForeground(bubbleOverlayRight);
                    } else {
                        flBubble.setForeground(bubbleOverlayLeft);
                    }
                    glide.load(imageUri)
                            .transition(DrawableTransitionOptions.withCrossFade(100))
                            .apply(new RequestOptions()
                                    .placeholder(finalThumbnail1)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(rcivImageBody);
                });
            }
        }

        private void setImageViewButtonProgress(TAPMessageModel item) {
            if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                ivButtonProgress.setImageResource(R.drawable.tap_ic_retry_white);
                flProgress.setOnClickListener(v -> resendMessage(item));
            } else if ((null == TAPFileUploadManager.getInstance().getUploadProgressPercent(item.getLocalID())
                    && null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID()))
                    || null != TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID())) {
                ivButtonProgress.setImageResource(R.drawable.tap_ic_download_white);
                flProgress.setOnClickListener(v -> {
                });
            } else {
                ivButtonProgress.setImageResource(R.drawable.tap_ic_cancel_white);
                flProgress.setOnClickListener(v -> TAPDataManager.getInstance()
                        .cancelUploadImage(itemView.getContext(), item.getLocalID()));
            }
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
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
        private TextView tvMediaInfo, tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
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

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (!item.isAnimating()) {
                checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, null);
            }

            tvMediaInfo.setVisibility(View.VISIBLE);
            tvMessageStatus.setText(item.getMessageStatusText());
            setLinkDetection(itemView.getContext(), tvMessageBody);
            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), rcivVideoThumbnail, item);

            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            setVideoProgress(item);

            markMessageAsRead(item, myUserModel);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));

            // TODO: 6 February 2019 TEMPORARY LISTENER FOR QUOTE
            if (null != item.getData() && item.getData().containsKey(USER_INFO)) {
                clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
            }
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
            String fileID = (String) item.getData().get(FILE_ID);
            String dataUri = (String) item.getData().get(FILE_URI);

            Integer uploadProgressPercent = TAPFileUploadManager.getInstance().getUploadProgressPercent(localID);
            Integer downloadProgressPercent = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(localID);
            videoUri = null != dataUri ? Uri.parse(dataUri) : TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), fileID);

            if ((null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty())) {
                // Fix layout when quote/forward exists
                rcivVideoThumbnail.getLayoutParams().width = 0;
                if (null != widthDimension && null != heightDimension && (widthDimension.floatValue() / heightDimension.floatValue()) > 3) {
                    rcivVideoThumbnail.getLayoutParams().height = TAPUtils.getInstance().dpToPx(78);
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
                    thumbnail = itemView.getContext().getDrawable(R.drawable.tap_bg_grey_e4);
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

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID()) || null != dataUri) {
                rcivVideoThumbnail.setImageDrawable(thumbnail);
            }

            if (null != item.getFailedSend() && item.getFailedSend()) {
                // Message failed to send
                tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
                tvMediaInfo.setText(size == null ? "" : TAPUtils.getInstance().getStringSizeLengthFile(size.longValue()));
                ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_retry_white));
                pbProgress.setVisibility(View.GONE);
                if (isMessageFromMySelf(item)) {
                    flBubble.setForeground(bubbleOverlayRight);
                    rcivVideoThumbnail.setOnClickListener(v -> resendMessage(item));
                } else {
                    flBubble.setForeground(bubbleOverlayLeft);
                    rcivVideoThumbnail.setOnClickListener(v -> downloadVideo(item));
                }
                glide.load(videoUri)
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .apply(new RequestOptions()
                                .placeholder(thumbnail)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(rcivVideoThumbnail);
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent) && null != videoUri) {
                // Video has finished downloading or uploading
                tvMediaInfo.setText(null == duration ? "" : TAPUtils.getInstance().getMediaDurationString(duration.intValue(), duration.intValue()));
                ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_play_white));
                pbProgress.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item));
                Drawable finalThumbnail = thumbnail;
                new Thread(() -> {
                    BitmapDrawable videoThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(fileID);
                    if (null == videoThumbnail) {
                        // Get full-size thumbnail from Uri
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        try {
                            retriever.setDataSource(itemView.getContext(), videoUri);
                            videoThumbnail = new BitmapDrawable(itemView.getContext().getResources(), retriever.getFrameAtTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                            videoThumbnail = (BitmapDrawable) finalThumbnail;
                        }
                        TAPCacheManager.getInstance(itemView.getContext()).addBitmapDrawableToCache(fileID, videoThumbnail);
                    }
                    // Load full-size thumbnail from cache
                    if (null != videoThumbnail) {
                        BitmapDrawable finalVideoThumbnail = videoThumbnail;
                        ((Activity) itemView.getContext()).runOnUiThread(() -> {
                            glide.load(finalVideoThumbnail)
                                    .transition(DrawableTransitionOptions.withCrossFade(100))
                                    .apply(new RequestOptions()
                                            .placeholder(finalThumbnail)
                                            .centerCrop())
                                    .into(rcivVideoThumbnail);
                            rcivVideoThumbnail.setOnClickListener(v -> openVideoPlayer(item));
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
                    ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_retry_white));
                } else {
                    ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_download_white));
                }
                pbProgress.setVisibility(View.GONE);
                rcivVideoThumbnail.setOnClickListener(v -> downloadVideo(item));
            } else {
                // File is downloading or uploading
                ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_cancel_white));
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

        private void openVideoPlayer(TAPMessageModel message) {
            if (null == message.getData()) {
                return;
            }
            Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(message.getRoom().getRoomID(), (String) message.getData().get(FILE_ID));
            if (null == videoUri) {
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
                Intent intent = new Intent(itemView.getContext(), TAPVideoPlayerActivity.class);
                intent.putExtra(URI, videoUri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                itemView.getContext().startActivity(intent);
            }
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
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
        private TextView tvUsername, tvFileName, tvFileInfo, tvFileInfoDummy, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
        private View vQuoteBackground, vQuoteDecoration;
        private ProgressBar pbProgress;

        private Uri fileUri;

        protected FileVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
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

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (item.isAnimating()) {
                return;
            }

            tvMessageStatus.setText(item.getMessageStatusText());

            setFileProgress(item);
            enableLongPress(itemView.getContext(), flBubble, item);

            markMessageAsRead(item, myUserModel);

            checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, tvUsername);
            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);

            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            flBubble.setOnClickListener(v -> flFileIcon.performClick());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));

            // TODO: 6 February 2019 TEMPORARY LISTENER FOR QUOTE
            if (null != item.getData() && item.getData().containsKey(USER_INFO)) {
                clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
            } else {
                clQuote.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            }
        }

        @Override
        protected void receiveReadEvent(TAPMessageModel message) {
            receiveReadEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
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
            fileUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));

            tvFileName.setText(TAPUtils.getInstance().getFileDisplayName(item));
            tvFileInfoDummy.setText(TAPUtils.getInstance().getFileDisplayDummyInfo(item));

            if (null != item.getFailedSend() && item.getFailedSend()) {
                // Message failed to send
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_retry_white));
                pbProgress.setVisibility(View.GONE);
                if (isMessageFromMySelf(item)) {
                    flFileIcon.setOnClickListener(v -> resendMessage(item));
                } else {
                    flFileIcon.setOnClickListener(v -> downloadFile(item));
                }
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent) && null != fileUri) {
                // File has finished downloading or uploading
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                ivFileIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_documents_white));
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> openFile(item));
            } else if (((null == uploadProgressPercent || (null != item.getSending() && !item.getSending()))
                    && null == downloadProgressPercent)) {
                // File is not downloaded
                tvFileInfo.setText(TAPUtils.getInstance().getFileDisplayInfo(item));
                if (TAPFileDownloadManager.getInstance().getFailedDownloads().contains(item.getLocalID())) {
                    ivFileIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_retry_white));
                } else {
                    ivFileIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_download_white));
                }
                pbProgress.setVisibility(View.GONE);
                flFileIcon.setOnClickListener(v -> downloadFile(item));
            } else {
                // File is downloading or uploading
                ivFileIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_cancel_white));
                pbProgress.setVisibility(View.VISIBLE);
                pbProgress.setMax(100);
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
        private TextView tvMessageBody, tvMessageStatus, tvForwardedFrom, tvQuoteTitle, tvQuoteContent;
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
            tvQuoteTitle = itemView.findViewById(R.id.tv_quote_title);
            tvQuoteContent = itemView.findViewById(R.id.tv_quote_content);
            vQuoteBackground = itemView.findViewById(R.id.v_quote_background);
            vQuoteDecoration = itemView.findViewById(R.id.v_quote_decoration);
            vMapBorder = itemView.findViewById(R.id.v_map_border);
            mapView = itemView.findViewById(R.id.map_view);
            mapView.onCreate(new Bundle());

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
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
                checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, null);
            }

            setMapData(mapData);

            if (null == item.getFailedSend() || (null != item.getFailedSend() && !item.getFailedSend())) {
                tvMessageStatus.setText(item.getMessageStatusText());
            }

            enableLongPress(itemView.getContext(), flBubble, item);
            enableLongPress(itemView.getContext(), vMapBorder, item);

            showForwardedFrom(item, clForwarded, tvForwardedFrom);
            showOrHideQuote(item, itemView, clQuote, tvQuoteTitle, tvQuoteContent, rcivQuoteImage, vQuoteBackground, vQuoteDecoration);
            if ((null != item.getQuote() && null != item.getQuote().getTitle() && !item.getQuote().getTitle().isEmpty()) ||
                    (null != item.getForwardFrom() && null != item.getForwardFrom().getFullname() && !item.getForwardFrom().getFullname().isEmpty())) {
                // Fix layout when quote/forward exists
                mapView.setOutlineProvider(null);
                clForwardedQuote.setVisibility(View.VISIBLE);
                vMapBorder.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_stroke_e4e4e4_1dp_insettop_insetbottom_1dp));
            } else {
                if (isMessageFromMySelf(item)) {
                    TAPUtils.getInstance().clipToRoundedRectangle(mapView, TAPUtils.getInstance().dpToPx(12), TAPUtils.ClipType.TOP_LEFT);
                    vMapBorder.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_rounded_8dp_1dp_0dp_0dp_stroke_e4e4e4_1dp_insetbottom_1dp));
                } else {
                    TAPUtils.getInstance().clipToRoundedRectangle(mapView, TAPUtils.getInstance().dpToPx(12), TAPUtils.ClipType.TOP_RIGHT);
                    vMapBorder.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_rounded_1dp_8dp_0dp_0dp_stroke_e4e4e4_1dp_insetbottom_1dp));
                }
                clForwardedQuote.setVisibility(View.GONE);
            }

            markMessageAsRead(item, myUserModel);

            vMapBorder.setOnClickListener(v -> openMapDetail(mapData));
            clContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));

            // TODO: 6 February 2019 TEMPORARY LISTENER FOR QUOTE
            if (item.getData().containsKey(USER_INFO)) {
                clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
            }
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
            receiveReadEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveDeliveredEvent(TAPMessageModel message) {
            receiveDeliveredEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void receiveSentEvent(TAPMessageModel message) {
            receiveSentEmit(message, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
        }

        @Override
        protected void setMessage(TAPMessageModel message) {
            setMessageItem(message, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, ivSending);
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

    public class EmptyVH extends TAPBaseChatViewHolder {

        EmptyVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            markMessageAsRead(item, myUserModel);
        }
    }

    private void setMessageItem(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        // Message failed to send
        if (null != item.getFailedSend() && item.getFailedSend()) {
            tvMessageStatus.setText(itemView.getContext().getString(R.string.tap_message_send_failed));
            if (null != ivMessageStatus) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_retry_circle_purple);
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

    private void receiveSentEmit(TAPMessageModel item, FrameLayout flBubble,
                                 TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                 @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageResource(R.drawable.tap_ic_message_sent_grey);
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

    private void receiveReadEmit(TAPMessageModel item, FrameLayout flBubble,
                                 TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                 @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageResource(R.drawable.tap_ic_message_read_green);
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

    private void receiveDeliveredEmit(TAPMessageModel item, FrameLayout flBubble,
                                      TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                      @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (null != ivMessageStatus) {
            ivMessageStatus.setImageResource(R.drawable.tap_ic_message_delivered_grey);
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
                                             TextView tvMessageStatus,
                                             @Nullable ImageView ivMessageStatus,
                                             @Nullable ImageView ivSending,
                                             @Nullable CircleImageView civAvatar,
                                             @Nullable TextView tvUsername) {
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
            // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            if (null != civAvatar && null != item.getUser().getAvatarURL()) {
                glide.load(item.getUser().getAvatarURL().getThumbnail()).into(civAvatar);
                //civAvatar.setVisibility(View.VISIBLE);
            }
            if (null != tvUsername) {
                tvUsername.setText(item.getUser().getUsername());
                //tvUsername.setVisibility(View.VISIBLE);
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
                    bubbleOverlayRight = itemView.getContext().getDrawable(R.drawable.tap_bg_transparent_black_8dp_1dp_8dp_8dp);
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
                if (null == bubbleOverlayRight) {
                    bubbleOverlayLeft = itemView.getContext().getDrawable(R.drawable.tap_bg_transparent_black_1dp_8dp_8dp_8dp);
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
                    ivMessageStatus.setImageResource(R.drawable.tap_ic_retry_circle_purple);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                } else if (null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        // Message has been read
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_read_green);
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        // Message is delivered
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_delivered_grey);
                    } else if (null != item.getSending() && !item.getSending()) {
                        // Message sent
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_sent_grey);
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
            tvQuoteTitle.setText(quote.getTitle());
            tvQuoteContent.setText(quote.getContent());
            String quoteImageURL = quote.getImageURL();
            String quoteFileID = quote.getFileID();
            if (null != quoteImageURL && !quoteImageURL.isEmpty()) {
                // Get quote image from URL
                glide.load(quoteImageURL).into(rcivQuoteImage);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (isMessageFromMySelf(item)) {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_mediumpurple_rounded_8dp));
                } else {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_f3f3f3_rounded_8dp));
                }
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteContent.setMaxLines(1);
            } else if (null != quoteFileID && !quoteFileID.isEmpty()) {
                // Get quote image from file ID
                if (quote.getFileType().equals((String.valueOf(TYPE_FILE)))) {
                    // Load file icon
                    rcivQuoteImage.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_documents_white));
                    rcivQuoteImage.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_purply_rounded_8dp));
                    rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                } else {
                    // Load image from file ID
                    // TODO: 8 March 2019 IMAGE MIGHT NOT EXIST IN CACHE
                    rcivQuoteImage.setImageDrawable(TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(quoteFileID));
                    rcivQuoteImage.setBackground(null);
                    rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                if (isMessageFromMySelf(item)) {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_mediumpurple_rounded_8dp));
                } else {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_f3f3f3_rounded_8dp));
                }
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show no image
                if (isMessageFromMySelf(item)) {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_mediumpurple_rounded_4dp));
                } else {
                    vQuoteBackground.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_f3f3f3_rounded_4dp));
                }
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);
                tvQuoteContent.setMaxLines(2);
            }
            // TODO: 6 February 2019 ENABLE LISTENER
//            clQuote.setOnClickListener(v -> chatListener.onMessageQuoteClicked(item));
        } else {
            // Hide quote
            clQuote.setVisibility(View.GONE);
            vQuoteBackground.setVisibility(View.GONE);
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

    public void addMessageFirstFromAPI(List<TAPMessageModel> messages) {
        addItem(messages, false);
        notifyDataSetChanged();
    }

    public void addMessage(int position, List<TAPMessageModel> messages) {
        addItem(position, messages, true);
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
        if (null == expandedBubble) return;
        expandedBubble.setExpanded(false);
        notifyItemChanged(getItems().indexOf(expandedBubble));
    }
}
