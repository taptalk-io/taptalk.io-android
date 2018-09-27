package com.moselo.HomingPigeon.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
                return new TextVH(parent, R.layout.cell_chat_log, viewType);
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

        private ConstraintLayout clBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivSending, ivButtonReply;
        private TextView tvUsername, tvMessageBody, tvMessageStatus;

        protected TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clBubble = itemView.findViewById(R.id.cl_bubble);
            ivSending = itemView.findViewById(R.id.iv_sending);
            ivButtonReply = itemView.findViewById(R.id.iv_button_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);

            if (bubbleType == TYPE_BUBBLE_TEXT_RIGHT) {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            } else if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            }
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            tvMessageBody.setText(item.getMessage());
            tvMessageStatus.setText(TimeFormatter.formatClock(item.getCreated()));

            if (isMessageFromMySelf(item)) {
                // Message has been read
                if (null != item.isRead() && item.isRead()) {
                    Log.e(TAG, "is read: " + item.getMessage());
                    ivMessageStatus.setImageResource(R.drawable.ic_message_read_green);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivSending.setVisibility(View.GONE);
                }
                // Message is delivered
                else if (null != item.isDelivered() && item.isDelivered()) {
                    Log.e(TAG, "delivered: " + item.getMessage());
                    ivMessageStatus.setImageResource(R.drawable.ic_message_sent_grey);

                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.animate()
                            .translationX(Utils.getInstance().dpToPx(40))
                            .setDuration(150L)
                            .setInterpolator(new AccelerateInterpolator(0.1f))
                            .withEndAction(() -> ivSending.setVisibility(View.GONE))
                            .start();
                }
                // Message failed to send
                else if (null != item.isFailedSend() && item.isFailedSend()) {
                    Log.e(TAG, "failed: " + item.getMessage());
                    tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                    ivMessageStatus.setImageResource(R.drawable.ic_retry_circle_purple);

                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivSending.setVisibility(View.GONE);
                }
                // Message is sending
                else if (null != item.isSending() && item.isSending()) {
                    Log.e(TAG, "sending: " + item.getMessage());
                    tvMessageStatus.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.GONE);
                    ivSending.setVisibility(View.VISIBLE);
                }
            } else {
                // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
                //if (item.getRoom().getRoomType() == 0) {}
            }

//            if (item.isExpanded()) {
//                tvMessageStatus.setVisibility(View.VISIBLE);
//            }

            clBubble.setOnClickListener(v -> {
                if (null != item.isFailedSend() && item.isFailedSend()) {
                    removeMessage(item);
                    listener.onRetrySendMessage(item);
                } else {
                    if (tvMessageStatus.getVisibility() == View.GONE) {
                        tvMessageStatus.setVisibility(View.VISIBLE);
                        listener.onMessageClicked(item,true);
                    } else {
                        tvMessageStatus.setVisibility(View.GONE);
                        listener.onMessageClicked(item,false);
                    }
//                    item.setExpanded(!item.isExpanded());
//                    notifyItemChanged(position);
                }
            });
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
}
