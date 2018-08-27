package com.moselo.HomingPigeon.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;
import static com.moselo.HomingPigeon.View.Helper.Const.*;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<MessageModel> chatMessages;
    private int[] randomColors;
    private UserModel myUserModel;
    private Context context;

    public MessageAdapter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        myUserModel = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {},prefs.getString(K_USER,"{}"));
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
            if (myUserModel.getUserID().equals(chatMessages.get(position).getUser().getUserID()))
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
        private LinearLayout llMessageStatus;
        private TextView tvUsername, tvMessage, tvTimestamp, tvStatus, tvDash;
        private MessageModel item;

        MessageHolder(View itemView) {
            super(itemView);

            clBubble = itemView.findViewById(R.id.cl_bubble);
            llMessageStatus = itemView.findViewById(R.id.ll_message_status);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDash = itemView.findViewById(R.id.tv_label_dash);
        }

        void onBind(int position) {
            item = getItemAt(position);
            randomColors = itemView.getContext().getResources().getIntArray(R.array.random_colors);
            if (getItemViewType() == TYPE_BUBBLE_LEFT) {
                tvUsername.setText(item.getUser().getName());
//                tvUsername.setTextColor(getUsernameColor(item.getUser().getName()));
            }
            else {
                tvUsername.setVisibility(View.GONE);
            }
            tvMessage.setText(item.getMessage());
            if (1 == item.getIsSending()) {
                tvStatus.setText("Sending...");
                tvDash.setText("");
                tvTimestamp.setText("");
                clBubble.setAlpha(0.5f);
                clBubble.setElevation(0f);
            }
            else {
                tvStatus.setText("S");
                tvDash.setText(" - ");
                tvTimestamp.setText(TimeFormatter.formatClock(item.getCreated()));
                clBubble.setAlpha(1f);
                clBubble.setElevation((float) Utils.getInstance().dpToPx(2));
            }

            llMessageStatus.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llMessageStatus.getVisibility() == View.GONE) {
                        llMessageStatus.setVisibility(View.VISIBLE);
                    }
                    else {
                        llMessageStatus.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void setMessages(List<MessageModel> messages) {
        if (null == chatMessages){
            chatMessages = new ArrayList<>();
        }

        chatMessages = messages;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void addMessage(MessageModel message) {
        chatMessages.add(0, message);
        notifyItemInserted(0);
    }

    public void addMessage(List<MessageModel> messageModels) {
        chatMessages.addAll(messageModels);
        notifyDataSetChanged();
    }

    public void setMessageAt(int position, MessageModel message) {
        chatMessages.set(position, message);
        notifyItemChanged(position);
    }

    public List<MessageModel> getItems() {
        return chatMessages;
    }

    public MessageModel getItemAt(int position) {
        return chatMessages.get(position);
    }

//    private int getUsernameColor(String username) {
//        int hash = 7;
//        for (int i = 0, len = username.length(); i < len; i++) {
//            hash = username.codePointAt(i) + (hash << 5) - hash;
//        }
//        int index = Math.abs(hash % randomColors.length);
//        return randomColors[index];
//    }
}
