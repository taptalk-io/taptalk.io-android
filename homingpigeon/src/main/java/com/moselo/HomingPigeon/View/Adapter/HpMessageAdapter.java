package com.moselo.HomingPigeon.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Interface.HomingPigeonChatInterface;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.BubbleType.TYPE_LOG;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpMessageAdapter extends HpBaseAdapter<MessageModel, HpBaseViewHolder<MessageModel>> {

    private static final String TAG = HpMessageAdapter.class.getSimpleName();
    private HomingPigeonChatInterface listener;
    private MessageModel expandedBubble;
    private UserModel myUserModel;

    public HpMessageAdapter(Context context, HomingPigeonChatInterface listener) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        myUserModel = HpUtils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, "{}"));
        this.listener = listener;
    }

    @NonNull
    @Override
    public HpBaseViewHolder<MessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.hp_cell_chat_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.hp_cell_chat_text_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                return new ProductVH(parent, R.layout.hp_cell_chat_product_list);
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
            MessageModel messageModel = getItemAt(position);
            int messageType = 0;
            if (null != messageModel)
                messageType = messageModel.getType();

            switch (messageType) {
                case HpDefaultConstant.MessageType.TYPE_TEXT :
                    if (isMessageFromMySelf(messageModel))
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    else return TYPE_BUBBLE_TEXT_LEFT;
                case HpDefaultConstant.MessageType.TYPE_PRODUCT:
                    return TYPE_BUBBLE_PRODUCT_LIST;
                default:
                    return TYPE_LOG;
            }
        } catch (Exception e) {
            return TYPE_LOG;
        }
    }

    private boolean isMessageFromMySelf(MessageModel messageModel) {
        return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }

    public class TextVH extends HpBaseViewHolder<MessageModel> {

        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus;

        private Drawable bubbleOverlayLeft, bubbleOverlayRight;
        float initialTranslationX = HpUtils.getInstance().dpToPx(-16);

        protected TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivSending = itemView.findViewById(R.id.iv_sending);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            }
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            tvMessageBody.setText(item.getBody());
            tvMessageStatus.setText(HpTimeFormatter.durationString(item.getCreated()));

            if (isMessageFromMySelf(item)) {
                // Message has been read
                if (null != item.getIsRead() && item.getIsRead()) {
                    Log.e(TAG, "is read: " + item.getBody());
                    tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.delivered_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_message_read_green);

                    flBubble.setTranslationX(0);
                    ivMessageStatus.setTranslationX(0);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    tvMessageStatus.setVisibility(View.GONE);
                    ivSending.setAlpha(0f);
                }
                // Message is delivered
                else if (null != item.getDelivered() && item.getDelivered()) {
                    Log.e(TAG, "delivered: " + item.getBody());
                    tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.delivered_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);

                    flBubble.setTranslationX(0);
                    ivMessageStatus.setTranslationX(0);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    tvMessageStatus.setVisibility(View.GONE);
                    ivSending.setAlpha(0f);
                }
                // Message failed to send
                else if (null != item.getFailedSend() && item.getFailedSend()) {
                    Log.e(TAG, "failed: " + item.getBody());
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_retry_circle_purple);

                    flBubble.setTranslationX(0);
                    ivMessageStatus.setTranslationX(0);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.setAlpha(0f);
                }
                // Message sent
                else if (null != item.getSending() && !item.getSending()) {
                    tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), HpTimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.hp_ic_message_sent_grey);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    animateSend(isMessageFromMySelf(item));
                }
                // Message is sending
                else if (null != item.getSending() && item.getSending()) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

                    flBubble.setTranslationX(initialTranslationX);
                    ivMessageStatus.setTranslationX(initialTranslationX);
                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivSending.setAlpha(1.0f);
                }
                else {
                    // TODO: 28 September 2018 TESTING
                    Log.e(TAG, "no status: " + item.getBody());
                }
                ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
            } else {
                // Message from others
                // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            }

            expandOrShrinkBubble(item);

            flBubble.setOnClickListener(v -> onBubbleClicked(item));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }

        private void onBubbleClicked(MessageModel item) {
            if (null != item.getFailedSend() && item.getFailedSend()) {
                resendMessage(item);
            } else if (null != item.getSending() && !item.getSending()){
                if (item.isExpanded()) {
                    // Shrink bubble
                    item.setExpanded(false);
                } else {
                    // Expand clicked bubble
                    shrinkExpandedBubble();
                    item.setExpanded(true);
                }
                expandOrShrinkBubble(item);
                listener.onMessageClicked(item, item.isExpanded());
            }
        }

        private void onStatusImageClicked(MessageModel item) {
            if (null != item.getFailedSend() && item.getFailedSend()) {
                resendMessage(item);
            }
        }

        private void onReplyButtonClicked(MessageModel item) {
            // TODO: 1 October 2018 REPLY
            shrinkExpandedBubble();
        }

        private void resendMessage(MessageModel item) {
            removeMessage(item);
            listener.onRetrySendMessage(item);
        }

        private void expandOrShrinkBubble(MessageModel item) {
            if (item.isExpanded()) {
                // Bubble is selected/expanded
                expandedBubble = item;
                animateFadeInToBottom(tvMessageStatus);
                if (isMessageFromMySelf(item)) {
                    animateFadeOutToBottom(ivMessageStatus);
                    animateShowToLeft(ivReply);
                    if (null == bubbleOverlayRight) {
                        bubbleOverlayRight = itemView.getContext().getDrawable(R.drawable.hp_bg_transparent_black_8dp_1dp_8dp_8dp);
                    }
                    flBubble.setForeground(bubbleOverlayRight);
                } else {
                    animateShowToRight(ivReply);
                    if (null == bubbleOverlayRight) {
                        bubbleOverlayLeft = itemView.getContext().getDrawable(R.drawable.hp_bg_transparent_black_1dp_8dp_8dp_8dp);
                    }
                    flBubble.setForeground(bubbleOverlayLeft);
                }
            } else {
                // Bubble is deselected/shrunk
                flBubble.setForeground(null);
                if (isMessageFromMySelf(item)) {
                    if (null != item.getFailedSend() && item.getFailedSend()) {
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
                        animateHideToRight(ivReply);
                        animateFadeInToTop(ivMessageStatus);
                        animateFadeOutToTop(tvMessageStatus);
                    }
                } else {
                    animateHideToLeft(ivReply);
                    animateFadeOutToTop(tvMessageStatus);
                }
            }
        }

        private void animateSend(boolean ownMessage) {
            if (ivSending.getAlpha() == 0f) return;

            if (ownMessage) ivMessageStatus.setTranslationX(initialTranslationX);
            flBubble.setTranslationX(initialTranslationX);
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            new Handler().postDelayed(() -> {
                if (ownMessage) ivMessageStatus.animate()
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
    }

    public class ProductVH extends HpBaseViewHolder<MessageModel> {

        RecyclerView rvProductList;
        HpProductListAdapter adapter;

        protected ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            if (null == adapter)
                adapter = new HpProductListAdapter(item, myUserModel);

            rvProductList.setAdapter(adapter);
            rvProductList.setHasFixedSize(false);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    public class LogVH extends HpBaseViewHolder<MessageModel> {

        private TextView tvLogMessage;

        protected LogVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            tvLogMessage = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            tvLogMessage.setText(item.getBody());
        }
    }

    public void setMessages(List<MessageModel> messages) {
        setItems(messages, true);
    }

    public void addMessage(MessageModel message) {
        addItem(0, message);
    }

    public void addMessage(List<MessageModel> messages) {
        addItem(messages, true);
    }

    public void setMessageAt(int position, MessageModel message) {
        setItemAt(position, message);
        notifyItemChanged(position);
    }


    public void removeMessageAt(int position) {
        removeItemAt(position);
    }

    public void removeMessage(MessageModel message) {
        removeItem(message);
    }

    public void shrinkExpandedBubble() {
        if (null == expandedBubble) return;
        expandedBubble.setExpanded(false);
        notifyItemChanged(getItems().indexOf(expandedBubble));
    }
}
