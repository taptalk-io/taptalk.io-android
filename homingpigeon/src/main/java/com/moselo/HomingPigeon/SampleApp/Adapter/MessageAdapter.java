package com.moselo.HomingPigeon.SampleApp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.SampleApp.Helper.Const;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.moselo.HomingPigeon.SampleApp.Helper.Const.*;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<MessageEntity> chatMessages;
    private Context context;
    private String myUsername;
    private int[] randomColors;

    public MessageAdapter(Context context, String myUsername) {
        this.context = context;
        this.myUsername = myUsername;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case TYPE_LOG:
                layout = R.layout.layout_chat_log;
                break;
            case TYPE_BUBBLE_LEFT:
                layout = R.layout.cell_chat_left;
                break;
            case TYPE_BUBBLE_RIGHT:
                layout = R.layout.cell_chat_right;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int position) {
        messageHolder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (null != chatMessages) {
            return chatMessages.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null != chatMessages) {
            if (chatMessages.get(position).getUserName().equals(myUsername))
                return TYPE_BUBBLE_RIGHT;
            else
                return TYPE_BUBBLE_LEFT;
        }
        else {
            return TYPE_LOG;
        }
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout clBubble;
        private TextView tvUsername, tvMessage, tvTimestamp, tvStatus;
        private MessageEntity item;

        public MessageHolder(View itemView) {
            super(itemView);

            clBubble = itemView.findViewById(R.id.cl_bubble);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void onBind(int position) {
            item = getItemAt(position);
            randomColors = itemView.getContext().getResources().getIntArray(R.array.random_colors);
            if (getItemViewType() == TYPE_BUBBLE_LEFT) {
                tvUsername.setText(item.getUserName());
                tvUsername.setTextColor(getUsernameColor(item.getUserName()));
            }
            else {
                tvUsername.setVisibility(View.GONE);
            }
            tvMessage.setText(item.getMessage());
            tvTimestamp.setText("00:00");
            tvStatus.setText("S");
//            SimpleDateFormat timeSdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
//            tvTimestamp.setText(timeSdf.format(item.getTimestamp()).getTime());
//            if (item.getStatus().equals("Sending")) clBubble.setAlpha(0.5f);
//            else clBubble.setAlpha(1f);
        }
    }

    public void setMessages(List<MessageEntity> messages) {
        chatMessages = messages;
        notifyItemRangeChanged(0,getItemCount());
    }

    public void addMessage(MessageEntity message) {
        chatMessages.add(0, message);
        notifyItemInserted(0);
    }

    public void setMessageAt(int position, MessageEntity message) {
        chatMessages.set(position, message);
        notifyItemChanged(position);
    }

    public List<MessageEntity> getItems() {
        return chatMessages;
    }

    public MessageEntity getItemAt(int position) {
        return chatMessages.get(position);
    }

    private int getUsernameColor(String username) {
        int hash = 7;
        for (int i = 0, len = username.length(); i < len; i++) {
            hash = username.codePointAt(i) + (hash << 5) - hash;
        }
        int index = Math.abs(hash % randomColors.length);
        return randomColors[index];
    }
}
