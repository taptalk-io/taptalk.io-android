package com.moselo.HomingPigeon.View.Adapter;

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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.TAPBaseViewHolder;
import com.moselo.HomingPigeon.Helper.TAPHorizontalDecoration;
import com.moselo.HomingPigeon.Helper.TAPRoundedCornerImageView;
import com.moselo.HomingPigeon.Helper.TAPTimeFormatter;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.TAPChatListener;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;

public class HpMessageAdapter extends HpBaseAdapter<HpMessageModel, TAPBaseViewHolder<HpMessageModel>> {

    private static final String TAG = HpMessageAdapter.class.getSimpleName();
    private TAPChatListener listener;
    private HpMessageModel expandedBubble;
    private HpUserModel myUserModel;

    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private float initialTranslationX = TAPUtils.getInstance().dpToPx(-16);
    private long defaultAnimationTime = 200L;

    public HpMessageAdapter(TAPChatListener listener) {
        myUserModel = HpDataManager.getInstance().getActiveUser();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<HpMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.hp_cell_chat_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.hp_cell_chat_text_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                return new ProductVH(parent, R.layout.hp_cell_chat_product_list);
            case TYPE_BUBBLE_IMAGE_RIGHT:
                return new ImageVH(parent, R.layout.hp_cell_chat_image_right, viewType);
            case TYPE_BUBBLE_IMAGE_LEFT:
                return new ImageVH(parent, R.layout.hp_cell_chat_image_left, viewType);
            default:
                return new LogVH(parent, R.layout.hp_cell_chat_log);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            HpMessageModel messageModel = getItemAt(position);
            int messageType = 0;
            if (null != messageModel)
                messageType = messageModel.getType();

            switch (messageType) {
                case TYPE_TEXT:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    } else {
                        return TYPE_BUBBLE_TEXT_LEFT;
                    }
                case TYPE_PRODUCT:
                    return TYPE_BUBBLE_PRODUCT_LIST;
                case TYPE_IMAGE:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_IMAGE_RIGHT;
                    } else {
                        return TYPE_BUBBLE_IMAGE_LEFT;
                    }
                default:
                    return TYPE_LOG;
            }
        } catch (Exception e) {
            return TYPE_LOG;
        }
    }

    private boolean isMessageFromMySelf(HpMessageModel messageModel) {
        return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }

    public class TextVH extends TAPBaseViewHolder<HpMessageModel> {

        private ConstraintLayout clContainer, clReply;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus, tvReplySenderName, tvReplyBody;
        private View vReplyBackground;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clReply = itemView.findViewById(R.id.cl_reply);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvReplySenderName = itemView.findViewById(R.id.tv_reply_sender);
            tvReplyBody = itemView.findViewById(R.id.tv_reply_body);
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
        protected void onBind(HpMessageModel item, int position) {
            tvMessageBody.setText(item.getBody());

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

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, tvUsername, civAvatar, ivMessageStatus, ivReply, ivSending);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }
    }

    public class ImageVH extends TAPBaseViewHolder<HpMessageModel> {

        private ConstraintLayout clContainer;
        private FrameLayout flBubble, flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivImageBody;
        private ImageView ivMessageStatus, ivReply, ivSending, ivProgress;
        private TextView tvMessageStatus;
        private ProgressBar pbProgress;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivImageBody = itemView.findViewById(R.id.rciv_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            ivProgress = itemView.findViewById(R.id.iv_progress);
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
        protected void onBind(HpMessageModel item, int position) {
            tvMessageStatus.setText(TAPTimeFormatter.getInstance().durationString(item.getCreated()));

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, null, civAvatar, ivMessageStatus, ivReply, ivSending);

            if (item.isFirstLoadFinished()) {
                flProgress.setVisibility(View.GONE);
            }

            if (!item.getBody().isEmpty()) {
                rcivImageBody.setImageDimensions(item.getImageWidth(), item.getImageHeight());
                int placeholder = isMessageFromMySelf(item) ? R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_8dp_1dp_8dp_8dp : R.drawable.hp_bg_white_rounded_1dp_8dp_8dp_8dp_stroke_eaeaea_1dp;
                GlideApp.with(itemView.getContext()).load(item.getBody()).placeholder(placeholder).listener(new RequestListener<Drawable>() {
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
    }

    public class ProductVH extends TAPBaseViewHolder<HpMessageModel> {

        RecyclerView rvProductList;
        HpProductListAdapter adapter;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(HpMessageModel item, int position) {
            if (null == adapter) {
                adapter = new HpProductListAdapter(item, myUserModel);
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

    public class LogVH extends TAPBaseViewHolder<HpMessageModel> {

        private ConstraintLayout clContainer;
        private TextView tvLogMessage;

        LogVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            tvLogMessage = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(HpMessageModel item, int position) {
            tvLogMessage.setText(item.getBody());
            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
        }
    }

    private void checkAndUpdateMessageStatus(HpMessageModel item, View itemView, FrameLayout flBubble,
                                             TextView tvMessageStatus, @Nullable TextView tvUsername,
                                             @Nullable CircleImageView civAvatar, @Nullable ImageView ivMessageStatus,
                                             @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (isMessageFromMySelf(item) && null != ivMessageStatus && null != ivSending) {
            // Set timestamp text on non-text or expanded bubble
            if (item.getType() != TYPE_TEXT || item.isExpanded()) {
                tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), TAPTimeFormatter.getInstance().formatDate(item.getCreated())));
            }
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);
                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                ivSending.setAlpha(0f);
                // Show status text and reply button for non-text bubbles
                if (item.getType() == TYPE_TEXT) {
                    tvMessageStatus.setVisibility(View.GONE);
                } else if (null != ivReply) {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivReply.setVisibility(View.VISIBLE);
                }
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
                // Show status text and reply button for non-text bubbles
                if (item.getType() == TYPE_TEXT) {
                    tvMessageStatus.setVisibility(View.GONE);
                } else if (null != ivReply) {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivReply.setVisibility(View.VISIBLE);
                }
            }
            // Message failed to send
            else if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                ivMessageStatus.setImageResource(R.drawable.hp_ic_retry_circle_purple);

                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.VISIBLE);
                ivSending.setAlpha(0f);
                if (null != ivReply) {
                    ivReply.setVisibility(View.GONE);
                }
            }
            // Message sent
            else if (null != item.getSending() && !item.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.VISIBLE);
                animateSend(item, flBubble, ivSending, ivMessageStatus, ivReply);
            }
            // Message is sending
            else if (null != item.getSending() && item.getSending()) {
                item.setNeedAnimateSend(true);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

                flBubble.setTranslationX(initialTranslationX);
                ivSending.setTranslationX(0);
                ivSending.setTranslationY(0);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(1f);
                if (null != ivReply) {
                    ivReply.setVisibility(View.GONE);
                }
            }
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
        } else {
            // Message from others
            // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            if (null != civAvatar && null != item.getUser().getAvatarURL()) {
                GlideApp.with(itemView.getContext()).load(item.getUser().getAvatarURL().getThumbnail()).into(civAvatar);
                //civAvatar.setVisibility(View.VISIBLE);
            }
            if (null != tvUsername) {
                tvUsername.setText(item.getUser().getUsername());
                //tvUsername.setVisibility(View.VISIBLE);
            }
            listener.onMessageRead(item);
        }
    }

    private void expandOrShrinkBubble(HpMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, @Nullable ImageView ivMessageStatus, ImageView ivReply, boolean animate) {
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
                    bubbleOverlayRight = itemView.getContext().getDrawable(R.drawable.hp_bg_transparent_black_8dp_1dp_8dp_8dp);
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
                    bubbleOverlayLeft = itemView.getContext().getDrawable(R.drawable.hp_bg_transparent_black_1dp_8dp_8dp_8dp);
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
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_retry_circle_purple);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                } else if (null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        // Message has been read
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        // Message is delivered
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
                    } else if (null != item.getSending() && !item.getSending()) {
                        // Message sent
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);
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

    private void onBubbleClicked(HpMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, ImageView ivMessageStatus, ImageView ivReply) {
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
                tvMessageStatus.setText(TAPTimeFormatter.getInstance().durationChatString(itemView.getContext(), item.getCreated()));
                shrinkExpandedBubble();
                item.setExpanded(true);
            }
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, true);
        }
    }

    private void onStatusImageClicked(HpMessageModel item) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(HpMessageModel item) {
        listener.onReplyMessage(item);
    }

    private void resendMessage(HpMessageModel item) {
        removeMessage(item);
        listener.onRetrySendMessage(item);
    }

    private void animateSend(HpMessageModel item, FrameLayout flBubble, ImageView ivSending,
                             ImageView ivMessageStatus, @Nullable ImageView ivReply) {
        if (!item.isNeedAnimateSend()) {
            // Set bubble state to post-animation
            flBubble.setTranslationX(0);
            ivMessageStatus.setTranslationX(0);
            ivSending.setAlpha(0f);
        } else {
            // Animate bubble
            item.setNeedAnimateSend(false);
            //ivMessageStatus.setTranslationX(initialTranslationX);
            flBubble.setTranslationX(initialTranslationX);
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            new Handler().postDelayed(() -> {
//                ivMessageStatus.animate()
//                        .translationX(0)
//                        .setDuration(160L)
//                        .start();
                flBubble.animate()
                        .translationX(0)
                        .setDuration(160L)
                        .start();
                ivSending.animate()
                        .translationX(TAPUtils.getInstance().dpToPx(36))
                        .translationY(TAPUtils.getInstance().dpToPx(-23))
                        .setDuration(360L)
                        .setInterpolator(new AccelerateInterpolator(0.5f))
                        .withEndAction(() -> ivSending.setAlpha(0f))
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

    public void setMessages(List<HpMessageModel> messages) {
        setItems(messages, false);
    }

    public void addMessage(HpMessageModel message) {
        addItem(0, message);
    }

    public void addMessage(HpMessageModel message, int position, boolean isNotify) {
        getItems().add(position, message);
        if (isNotify) notifyItemInserted(position);
    }

    public void addMessage(List<HpMessageModel> messages) {
        addItem(messages, true);
    }

    public void addMessage(int position, List<HpMessageModel> messages) {
        addItem(position, messages, true);
    }

    public void setMessageAt(int position, HpMessageModel message) {
        setItemAt(position, message);
        notifyItemChanged(position);
    }

    public void removeMessageAt(int position) {
        removeItemAt(position);
    }

    public void removeMessage(HpMessageModel message) {
        removeItem(message);
    }

    public void shrinkExpandedBubble() {
        if (null == expandedBubble) return;
        expandedBubble.setExpanded(false);
        notifyItemChanged(getItems().indexOf(expandedBubble));
    }
}
