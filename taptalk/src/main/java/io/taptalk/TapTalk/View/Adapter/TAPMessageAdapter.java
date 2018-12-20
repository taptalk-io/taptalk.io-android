package io.taptalk.TapTalk.View.Adapter;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;

public class TAPMessageAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseChatViewHolder> {

    private static final String TAG = TAPMessageAdapter.class.getSimpleName();
    private TAPChatListener listener;
    private TAPMessageModel expandedBubble;
    private TAPUserModel myUserModel;
    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private float initialTranslationX = TAPUtils.getInstance().dpToPx(-16);
    private long defaultAnimationTime = 200L;

    public TAPMessageAdapter(TAPChatListener listener) {
        myUserModel = TAPDataManager.getInstance().getActiveUser();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.tap_cell_chat_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.tap_cell_chat_text_left, viewType);
            case TYPE_BUBBLE_IMAGE_RIGHT:
                return new ImageVH(parent, R.layout.tap_cell_chat_image_right, viewType);
            case TYPE_BUBBLE_IMAGE_LEFT:
                return new ImageVH(parent, R.layout.tap_cell_chat_image_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                return new ProductVH(parent, R.layout.tap_cell_chat_product_list);
            case TYPE_BUBBLE_ORDER_CARD:
                //return new OrderVH(parent, R.layout.tap_cell_chat_order_card);
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

        private ConstraintLayout clContainer, clReply;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus, tvReplySenderName, tvReplyBody;
        private View vReplyBackground;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clReply = itemView.findViewById(R.id.cl_quote);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvReplySenderName = itemView.findViewById(R.id.tv_quote_title);
            tvReplyBody = itemView.findViewById(R.id.tv_quote_body);
            vReplyBackground = itemView.findViewById(R.id.v_reply_background);

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

            if ((null == item.getIsRead() || !item.getIsRead()) && !isMessageFromMySelf(item)
                    && (null != item.getSending() && !item.getSending())) {
                item.updateReadMessage();
                new Thread(() -> {
                    TAPMessageStatusManager.getInstance().addUnreadListByOne(item.getRoom().getRoomID());
                    TAPMessageStatusManager.getInstance().addReadMessageQueue(item.copyMessageModel());
                }).start();
            }

            // TODO: 1 November 2018 TESTING REPLY LAYOUT
            if (null != item.getReplyTo() && !item.getReplyTo().getBody().isEmpty()) {
                clReply.setVisibility(View.VISIBLE);
                vReplyBackground.setVisibility(View.VISIBLE);
                tvReplySenderName.setText(item.getReplyTo().getUser().getName());
                tvReplyBody.setText(item.getReplyTo().getBody());
            } else {
                clReply.setVisibility(View.GONE);
                vReplyBackground.setVisibility(View.GONE);
            }

            checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, tvUsername);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
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

        private ConstraintLayout clContainer;
        private FrameLayout flBubble, flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivImageBody;
        private ImageView ivMessageStatus, ivReply, ivSending, ivProgress;
        private TextView tvMessageBody, tvMessageStatus;
        private ProgressBar pbProgress;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivImageBody = itemView.findViewById(R.id.rciv_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            ivProgress = itemView.findViewById(R.id.iv_progress);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
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
            if (item.isAnimating()) {
                return;
            }

            // TODO: 20 December 2018 CHECK IF MESSAGE CONTAINS CAPTION
//            if (HAS_CAPTIONS) {
                rcivImageBody.setBottomLeftRadius(0);
                rcivImageBody.setBottomRightRadius(0);
                tvMessageBody.setVisibility(View.VISIBLE);
                tvMessageBody.setText(item.getBody());
//            } else {
//                rcivImageBody.setBottomLeftRadius(TAPUtils.getInstance().dpToPx(9));
//                rcivImageBody.setBottomRightRadius(TAPUtils.getInstance().dpToPx(9));
//                tvMessageBody.setVisibility(View.GONE);
//            }

            tvMessageStatus.setText(item.getMessageStatusText());

            checkAndUpdateMessageStatus(this, item, tvMessageStatus, ivMessageStatus, ivSending, civAvatar, null);

            if (item.isFirstLoadFinished()) {
                flProgress.setVisibility(View.GONE);
            }

            if (!item.getBody().isEmpty()) {
                rcivImageBody.setImageDimensions(item.getImageWidth(), item.getImageHeight());
                int placeholder = isMessageFromMySelf(item) ? R.drawable.tap_bg_amethyst_mediumpurple_270_rounded_8dp_1dp_8dp_8dp : R.drawable.tap_bg_white_rounded_1dp_8dp_8dp_8dp_stroke_eaeaea_1dp;
                Glide.with(itemView.getContext()).load(item.getBody()).apply(new RequestOptions().placeholder(placeholder)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (item.isFirstLoadFinished()) {
                            // Image has been loaded once
                            return false;
                        }

                        // Image is loading for the first time
                        item.setFirstLoadFinished(true);
                        listener.onLayoutLoaded(item);
                        // TODO: 31 October 2018 TESTING DUMMY IMAGE PROGRESS BAR
                        if (isMessageFromMySelf(item)) {
                            flBubble.setForeground(bubbleOverlayRight);
                        } else {
                            flBubble.setForeground(bubbleOverlayLeft);
                        }
                        flProgress.setVisibility(View.VISIBLE);
                        new CountDownTimer(1000, 10) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                pbProgress.setProgress((int) (1000 - millisUntilFinished) / 10);
                            }

                            @Override
                            public void onFinish() {
                                flProgress.setVisibility(View.GONE);
                                flBubble.setForeground(null);
                            }
                        }.start();
                        return false;
                    }
                }).into(rcivImageBody);
            }

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> {
                // TODO: 5 November 2018 VIEW IMAGE
            });
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
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

    public class ProductVH extends TAPBaseChatViewHolder {

        RecyclerView rvProductList;
        TAPProductListAdapter adapter;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == adapter) {
                adapter = new TAPProductListAdapter(item, myUserModel, listener);
            }

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
            tvLogMessage.setText(item.getBody());
            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
        }
    }

    public class EmptyVH extends TAPBaseChatViewHolder {

        EmptyVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {

        }
    }

    private void setMessageItem(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        // Message failed to send
        if (null != item.getFailedSend() && item.getFailedSend()) {
            tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
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
            tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

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
        tvMessageStatus.setVisibility(View.GONE);
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
            // Set timestamp text on non-text or expanded bubble
            if (item.getType() != TYPE_TEXT || item.isExpanded()) {
                tvMessageStatus.setText(item.getMessageStatusText());
            }
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead()) {
                vh.receiveReadEvent(item);
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                vh.receiveDeliveredEvent(item);
            }
            // Message sent
            else if (null != item.getSending() && !item.getSending()) {
                vh.receiveSentEvent(item);
            } else {
                vh.setMessage(item);
            }
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
        } else {
            // Message from others
            // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            if (null != civAvatar && null != item.getUser().getAvatarURL()) {
                Glide.with(vh.itemView.getContext()).load(item.getUser().getAvatarURL().getThumbnail()).into(civAvatar);
                //civAvatar.setVisibility(View.VISIBLE);
            }
            if (null != tvUsername) {
                tvUsername.setText(item.getUser().getUsername());
                //tvUsername.setVisibility(View.VISIBLE);
            }
            listener.onMessageRead(item);
        }
    }

    private void expandOrShrinkBubble(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                      TextView tvMessageStatus, @Nullable ImageView ivMessageStatus,
                                      ImageView ivReply, boolean animate) {
        if (item.isExpanded()) {
            // Expand bubble
            expandedBubble = item;
            animateFadeInToBottom(tvMessageStatus);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                // Right Bubble
                if (animate) {
                    // Animate expand
                    animateFadeOutToBottom(ivMessageStatus);
                    animateShowToLeft(ivReply);
                } else {
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
                    animateShowToRight(ivReply);
                } else {
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

    private void onBubbleClicked(TAPMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, ImageView ivMessageStatus, ImageView ivReply) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        } else if ((null != item.getSending() && !item.getSending()) ||
                (null != item.getDelivered() && item.getDelivered()) ||
                (null != item.getIsRead() && item.getIsRead())) {
            if (item.isExpanded()) {
                // Shrink bubble
                item.setExpanded(false);
            } else {
                // Expand clicked bubble
                tvMessageStatus.setText(item.getMessageStatusText());
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
        listener.onReplyMessage(item);
    }

    private void resendMessage(TAPMessageModel item) {
        removeMessage(item);
        listener.onRetrySendMessage(item);
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
        new Handler().postDelayed(() -> listener.onBubbleExpanded(), 50L);
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
