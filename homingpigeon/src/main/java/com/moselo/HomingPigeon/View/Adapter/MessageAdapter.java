package com.moselo.HomingPigeon.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_BUBBLE_LEFT;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_BUBBLE_RIGHT;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BubbleType.TYPE_LOG;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class MessageAdapter extends BaseAdapter<MessageModel, BaseViewHolder<MessageModel>> {

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
            case TYPE_BUBBLE_RIGHT:
                return new MessageHolder(parent, R.layout.cell_chat_right);
            case TYPE_BUBBLE_LEFT:
                return new MessageHolder(parent, R.layout.cell_chat_left);
            default:
                return new MessageHolder(parent, R.layout.cell_chat_log);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (null != getItems()) {
            if (myUserModel.getUserID().equals(getItems().get(position).getUser().getUserID()))
                return TYPE_BUBBLE_RIGHT;
            else
                return TYPE_BUBBLE_LEFT;
        } else return TYPE_LOG;
    }

    public class MessageHolder extends BaseViewHolder<MessageModel> {

        private ConstraintLayout clBubble;
        private LinearLayout llMessageStatus;
        private TextView tvUsername, tvMessage, tvTimestamp, tvStatus, tvDash;
        private MessageModel item;

        protected MessageHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clBubble = itemView.findViewById(R.id.cl_bubble);
            llMessageStatus = itemView.findViewById(R.id.ll_message_status);
            tvUsername = itemView.findViewById(R.id.tv_full_name);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDash = itemView.findViewById(R.id.tv_label_dash);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
//            item = getItemAt(position);

            if (getItemViewType() == TYPE_BUBBLE_LEFT) {
                tvUsername.setText(item.getUser().getName());
//                tvUsername.setTextColor(getUsernameColor(item.getUser().getName()));
            } else {
                tvUsername.setVisibility(View.GONE);
            }

            tvMessage.setText(item.getMessage());

            // Message is sending
            if (null != item.getIsSending() && item.getIsSending()) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorTextContent));
                tvStatus.setText("Sending...");
                tvDash.setText("");
                tvTimestamp.setText("");
                clBubble.setAlpha(0.5f);
                clBubble.setElevation(0f);
                llMessageStatus.setVisibility(View.GONE);
            }
            // Message failed to send
            else if (null != item.getIsFailedSend() && item.getIsFailedSend()) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                tvStatus.setText("Failed, tap to retry.");
                tvDash.setText("");
                tvTimestamp.setText("");
                clBubble.setAlpha(0.5f);
                clBubble.setElevation(0f);
                llMessageStatus.setVisibility(View.VISIBLE);
            }
            // Message is delivered
            else {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorTextContent));
                tvStatus.setText("S");
                tvDash.setText(" - ");
                tvTimestamp.setText(TimeFormatter.formatClock(item.getCreated()));
                clBubble.setAlpha(1f);
                clBubble.setElevation((float) Utils.getInstance().dpToPx(2));
                llMessageStatus.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (null != item.getIsFailedSend() && null != item.getIsSending() &&
                        item.getIsFailedSend() && !item.getIsSending()) {
                    removeMessage(item);
                    listener.onRetrySendMessage(item);
                } else {
                    if (llMessageStatus.getVisibility() == View.GONE) {
                        llMessageStatus.setVisibility(View.VISIBLE);
                    } else {
                        llMessageStatus.setVisibility(View.GONE);
                    }
                }
            });
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
