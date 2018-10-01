package com.moselo.HomingPigeon.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_LOG;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class MessageAdapter extends BaseAdapter<MessageModel, BaseViewHolder<MessageModel>> {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private HomingPigeonChatListener listener;
    private UserModel myUserModel;

    public MessageAdapter(Context context, HomingPigeonChatListener listener) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        myUserModel = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
        }, prefs.getString(K_USER, "{}"));
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder<MessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.cell_chat_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.cell_chat_text_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                return new ProductVH(parent, R.layout.cell_chat_product_list);
            default:
                return new LogVH(parent, R.layout.cell_chat_log);
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
                case DefaultConstant.MessageType.TYPE_TEXT :
                    if (isMessageFromMySelf(messageModel))
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    else return TYPE_BUBBLE_TEXT_LEFT;
                case DefaultConstant.MessageType.TYPE_PRODUCT:
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

    public class TextVH extends BaseViewHolder<MessageModel> {

        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus;

        private Drawable bubbleOverlayLeft, bubbleOverlayRight;

        protected TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivSending = itemView.findViewById(R.id.iv_sending);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            }
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            tvMessageBody.setText(item.getMessage());
            tvMessageStatus.setText(TimeFormatter.durationString(item.getCreated()));

            if (isMessageFromMySelf(item)) {
                // Message has been read
                if (null != item.getIsRead() && item.getIsRead()) {
                    Log.e(TAG, "is read: " + item.getMessage());
                    tvMessageStatus.setText(String.format("%s%s", itemView.getContext().getString(R.string.delivered_at), TimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.ic_message_read_green);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivSending.setVisibility(View.GONE);
                }
                // Message is delivered
                else if (null != item.getDelivered() && item.getDelivered()) {
                    Log.e(TAG, "delivered: " + item.getMessage());
                    tvMessageStatus.setText(String.format("%s%s", itemView.getContext().getString(R.string.delivered_at), TimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.ic_delivered_grey);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.setVisibility(View.GONE);
                }
                // Message failed to send
                else if (null != item.getFailedSend() && item.getFailedSend()) {
                    Log.e(TAG, "failed: " + item.getMessage());
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                    ivMessageStatus.setImageResource(R.drawable.ic_retry_circle_purple);

                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.setVisibility(View.GONE);
                }
                // Message sent
                else if (null != item.getSending() && !item.getSending()) {
                    tvMessageStatus.setText(String.format("%s%s", itemView.getContext().getString(R.string.sent_at), TimeFormatter.formatTimeAndDate(item.getCreated())));
                    ivMessageStatus.setImageResource(R.drawable.ic_message_sent_grey);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.setVisibility(View.GONE);
//                    Animation sendAnimation = new TranslateAnimation(0, Utils.getInstance().dpToPx(32), 0, 0);
//                    sendAnimation.setDuration(150L);
//                    sendAnimation.setFillAfter(false);
//                    sendAnimation.setInterpolator(new AccelerateInterpolator(0.5f));
//                    sendAnimation.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            ivSending.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                    ivSending.startAnimation(sendAnimation);
                }
                // Message is sending
                else if (null != item.getSending() && item.getSending()) {
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivSending.setVisibility(View.VISIBLE);
                }
                else {
                    // TODO: 28 September 2018 TESTING
                    Log.e(TAG, "no status: " + item.getMessage());
                }
            } else {
                // Message from others
                // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            }

            expandOrShrinkBubble(item);

            flBubble.setOnClickListener(v -> onBubbleClicked(item));
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
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
            } else if (item.isExpanded()) {
                // TODO: 1 October 2018 REPLY
            }
        }

        private void resendMessage(MessageModel item) {
            removeMessage(item);
            listener.onRetrySendMessage(item);
        }

        private void expandOrShrinkBubble(MessageModel item) {
            if (item.isExpanded()) {
                // Bubble is selected/expanded
                ivMessageStatus.setImageResource(R.drawable.ic_reply_circle_white);
                tvMessageStatus.setVisibility(View.VISIBLE);
                ivMessageStatus.setVisibility(View.VISIBLE);
                if (isMessageFromMySelf(item)) {
                    if (null == bubbleOverlayRight) {
                        bubbleOverlayRight = itemView.getContext().getDrawable(R.drawable.bg_transparent_black_8dp_1dp_8dp_8dp);
                    }
                    flBubble.setForeground(bubbleOverlayRight);
                } else {
                    if (null == bubbleOverlayRight) {
                        bubbleOverlayLeft = itemView.getContext().getDrawable(R.drawable.bg_transparent_black_1dp_8dp_8dp_8dp);
                    }
                    flBubble.setForeground(bubbleOverlayLeft);
                }
//                listener.onMessageClicked(item,true);
            } else {
                // Bubble is deselected/shrunk
                flBubble.setForeground(null);
                tvMessageStatus.setVisibility(View.GONE);
//                        llButtonReply.setVisibility(View.GONE);

                if (isMessageFromMySelf(item) && null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        ivMessageStatus.setImageResource(R.drawable.ic_message_read_green);
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        ivMessageStatus.setImageResource(R.drawable.ic_delivered_grey);
                    } else if (null != item.getFailedSend() && item.getFailedSend()) {
                        tvMessageStatus.setVisibility(View.VISIBLE);
                        ivMessageStatus.setImageResource(R.drawable.ic_retry_circle_purple);
                    } else if (null != item.getSending() && !item.getSending()) {
                        ivMessageStatus.setImageResource(R.drawable.ic_message_sent_grey);
                    }
                    ivMessageStatus.setVisibility(View.VISIBLE);
                } else {
                    ivMessageStatus.setVisibility(View.GONE);
                }
//                listener.onMessageClicked(item,false);
            }
        }
    }

    public class ProductVH extends BaseViewHolder<MessageModel> {

        RecyclerView rvProductList;
        ProductListAdapter adapter;

        protected ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            if (null == adapter)
                adapter = new ProductListAdapter(item, myUserModel);

            rvProductList.setAdapter(adapter);
            rvProductList.setHasFixedSize(false);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    public class LogVH extends BaseViewHolder<MessageModel> {

        private TextView tvLogMessage;

        protected LogVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            tvLogMessage = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            tvLogMessage.setText(item.getMessage());
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
        for (MessageModel message : getItems()) {
            if (message.isExpanded()) {
                message.setExpanded(false);
                notifyItemChanged(getItems().indexOf(message));
                return;
            }
        }
    }
}
