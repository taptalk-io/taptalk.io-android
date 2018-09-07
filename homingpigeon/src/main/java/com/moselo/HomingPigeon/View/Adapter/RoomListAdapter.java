package com.moselo.HomingPigeon.View.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.SampleChatActivity;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_ROOM_ID;
import static com.moselo.HomingPigeon.View.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.View.Helper.Const.K_MY_USERNAME;
import static com.moselo.HomingPigeon.View.Helper.Const.K_THEIR_USERNAME;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListHolder> {

    private List<MessageModel> roomList;
    private ColorStateList avatarTint;
    private String myUsername;
    private int[] randomColors;

    public RoomListAdapter(List<MessageModel> roomList, String myUsername) {
        this.roomList = roomList;
        this.myUsername = myUsername;
    }

    @NonNull
    @Override
    public RoomListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (null != roomList) return roomList.size();
        return 0;
    }

    public List<MessageModel> getItems() {
        return roomList;
    }

    public MessageModel getItemAt(int position) {
        return roomList.get(position);
    }

    class RoomListHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar, ivAvatarIcon, ivMessageStatus;
        private TextView tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread;
        private MessageModel item;

        RoomListHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
            randomColors = itemView.getContext().getResources().getIntArray(R.array.pastel_colors);
        }

        void onBind(int position) {
            item = getItemAt(position);
//            final UserModel userModel = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {},item.getUser());
            final UserModel userModel = item.getUser();
            final int randomColor = getRandomColor(userModel.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
//            tvAvatar.setText(userModel.getName().substring(0, 1).toUpperCase());
            ivAvatar.setBackgroundTintList(avatarTint);
            // TODO: 7 September 2018 SET AVATAR ICON
            tvFullName.setText(userModel.getName());
            tvLastMessage.setText(item.getMessage());
            tvLastMessageTime.setText(TimeFormatter.formatClock(item.getCreated()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), SampleChatActivity.class);
                    intent.putExtra(K_MY_USERNAME, myUsername);
                    intent.putExtra(K_THEIR_USERNAME, userModel.getName());
                    intent.putExtra(K_COLOR, randomColor);
                    intent.putExtra(K_ROOM_ID, item.getRoom().getRoomID());
                    itemView.getContext().startActivity(intent);
                }
            });
            // TODO: 6 September 2018 CHANGE MESSAGE STATUS IMAGE AND UNREAD BADGE
        }
    }

    public int getRandomColor(String s) {
        int hash = 7;
        for (int i = 0, len = s.length(); i < len; i++) {
            hash = s.codePointAt(i) + (hash << 5) - hash;
        }
        int index = Math.abs(hash % randomColors.length);
        return randomColors[index];
    }
}