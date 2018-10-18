package com.moselo.HomingPigeon.View.Adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Listener.HpChatListener;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_LOG;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.MessageType.TYPE_IMAGE;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.MessageType.TYPE_PRODUCT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.MessageType.TYPE_TEXT;

public class HpMessageAdapter extends HpBaseAdapter<HpMessageModel, HpBaseViewHolder<HpMessageModel>> {

    private static final String TAG = HpMessageAdapter.class.getSimpleName();
    private HpChatListener listener;
    private HpMessageModel expandedBubble;
    private HpUserModel myUserModel;

    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private float initialTranslationX = HpUtils.getInstance().dpToPx(-16);

    public HpMessageAdapter(HpChatListener listener) {
        myUserModel = HpDataManager.getInstance().getActiveUser();
        this.listener = listener;
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    public class TextVH extends HpBaseViewHolder<HpMessageModel> {

        private ConstraintLayout clContainer;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);

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

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, tvUsername, civAvatar, ivMessageStatus, ivSending);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }
    }

    public class ImageVH extends HpBaseViewHolder<HpMessageModel> {

        private ConstraintLayout clContainer;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvMessageStatus;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(HpMessageModel item, int position) {
            tvMessageStatus.setText(HpTimeFormatter.durationString(item.getCreated()));

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, null, civAvatar, ivMessageStatus, ivSending);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }
    }

    public class ProductVH extends HpBaseViewHolder<HpMessageModel> {

        RecyclerView rvProductList;
        HpProductListAdapter adapter;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(HpMessageModel item, int position) {
            if (null == adapter)
                adapter = new HpProductListAdapter(item, myUserModel);

            rvProductList.setAdapter(adapter);
            rvProductList.setHasFixedSize(false);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    public class LogVH extends HpBaseViewHolder<HpMessageModel> {

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
                                             @Nullable ImageView ivSending) {
        if (isMessageFromMySelf(item) && null != ivMessageStatus && null != ivSending) {
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead() && item.isExpanded()) {
                tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);

                flBubble.setTranslationX(0);
                ivMessageStatus.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
            } else if (null != item.getIsRead() && item.getIsRead() && !item.isExpanded()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);

                flBubble.setTranslationX(0);
                ivMessageStatus.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered() && item.isExpanded()) {
                tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);

                flBubble.setTranslationX(0);
                ivMessageStatus.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
            } else if (null != item.getDelivered() && item.getDelivered() && !item.isExpanded()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);

                flBubble.setTranslationX(0);
                ivMessageStatus.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
            }
            // Message failed to send
            else if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                ivMessageStatus.setImageResource(R.drawable.hp_ic_retry_circle_purple);

                flBubble.setTranslationX(0);
                ivMessageStatus.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.VISIBLE);
                ivSending.setAlpha(0f);
            }
            // Message sent
            else if (null != item.getSending() && !item.getSending() && item.isExpanded()) {
                tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);

                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.VISIBLE);
                if (!item.isNeedAnimateSend()){
                    flBubble.setTranslationX(0);
                    ivMessageStatus.setTranslationX(0);
                    ivSending.setAlpha(0f);
                } else animateSend(item, flBubble, ivSending, ivMessageStatus);

            } else if (null != item.getSending() && !item.getSending() && !item.isExpanded()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.VISIBLE);
                if (!item.isNeedAnimateSend()){
                    flBubble.setTranslationX(0);
                    ivMessageStatus.setTranslationX(0);
                    ivSending.setAlpha(0f);
                } else animateSend(item, flBubble, ivSending, ivMessageStatus);
            }
            // Message is sending
            else if (null != item.getSending() && item.getSending()) {
                item.setNeedAnimateSend(true);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

                flBubble.setTranslationX(initialTranslationX);
                ivMessageStatus.setTranslationX(initialTranslationX);
                ivSending.setTranslationX(0);
                ivSending.setTranslationY(0);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(1f);
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
        }
    }

    private void expandOrShrinkBubble(HpMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, @Nullable ImageView ivMessageStatus, ImageView ivReply, boolean animate) {
        if (item.isExpanded()) {
            // Bubble is selected/expanded
            expandedBubble = item;
            animateFadeInToBottom(tvMessageStatus);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                if (animate) {
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
                if (animate) {
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
            // Bubble is deselected/shrunk
            flBubble.setForeground(null);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                if ((null != item.getFailedSend() && item.getFailedSend())) {
                    ivReply.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_retry_circle_purple);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                } else if (null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
                    } else if (null != item.getSending() && !item.getSending()) {
                        ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);
                    }
                    if (animate) {
                        animateHideToRight(ivReply);
                        animateFadeInToTop(ivMessageStatus);
                        animateFadeOutToTop(tvMessageStatus);
                    } else {
                        ivReply.setVisibility(View.GONE);
                        ivMessageStatus.setVisibility(View.VISIBLE);
                        tvMessageStatus.setVisibility(View.GONE);
                    }
                } else if (null != item.getSending() && item.getSending()) {
                    ivReply.setVisibility(View.GONE);
                }
            }
            // Message from others
            else if (animate) {
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
                tvMessageStatus.setText(String.format("%s, %s", itemView.getContext().getString(R.string.sent_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                shrinkExpandedBubble();
                item.setExpanded(true);
            }
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, true);
            listener.onMessageClicked(item, item.isExpanded());
        }
    }

    private void onStatusImageClicked(HpMessageModel item) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(HpMessageModel item) {
        // TODO: 1 October 2018 REPLY
        shrinkExpandedBubble();
    }

    private void resendMessage(HpMessageModel item) {
        removeMessage(item);
        listener.onRetrySendMessage(item);
    }

    private void animateSend(HpMessageModel item, FrameLayout flBubble, ImageView ivSending, ImageView ivMessageStatus) {
        Log.e(TAG, "animateSend: " + ivSending.getAlpha());
        if (!item.isNeedAnimateSend()) return;

        item.setNeedAnimateSend(true);
        ivMessageStatus.setTranslationX(initialTranslationX);
        flBubble.setTranslationX(initialTranslationX);
        ivSending.setTranslationX(0);
        ivSending.setTranslationY(0);
        new Handler().postDelayed(() -> {
            ivMessageStatus.animate()
                    .translationX(0)
                    .setDuration(160L)
                    .start();
            flBubble.animate()
                    .translationX(0)
                    .setDuration(160L)
                    .start();
            ivSending.animate()
                    .translationX(HpUtils.getInstance().dpToPx(36))
                    .translationY(HpUtils.getInstance().dpToPx(-23))
                    .setDuration(360L)
                    .setInterpolator(new AccelerateInterpolator(0.5f))
                    .withEndAction(() -> ivSending.setAlpha(0f))
                    .start();
        }, 200L);
    }

    private void animateFadeInToTop(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(HpUtils.getInstance().dpToPx(24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(150L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateFadeInToBottom(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(HpUtils.getInstance().dpToPx(-24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(150L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateFadeOutToTop(View view) {
        view.animate()
                .translationY(HpUtils.getInstance().dpToPx(-24))
                .alpha(0)
                .setDuration(150L)
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
                .translationY(HpUtils.getInstance().dpToPx(24))
                .alpha(0)
                .setDuration(150L)
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
        view.setTranslationX(HpUtils.getInstance().dpToPx(32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150L)
                .start();
    }

    private void animateShowToRight(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(HpUtils.getInstance().dpToPx(-32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150L)
                .start();
    }

    private void animateHideToLeft(View view) {
        view.animate()
                .translationX(HpUtils.getInstance().dpToPx(-32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150L)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationX(0);
                })
                .start();
    }

    private void animateHideToRight(View view) {
        view.animate()
                .translationX(HpUtils.getInstance().dpToPx(32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150L)
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
