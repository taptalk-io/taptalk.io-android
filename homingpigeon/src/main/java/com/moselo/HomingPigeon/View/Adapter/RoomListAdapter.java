package com.moselo.HomingPigeon.View.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.SampleChatActivity;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_ROOM_ID;
import static com.moselo.HomingPigeon.View.Helper.Const.K_COLOR;
import static com.moselo.HomingPigeon.View.Helper.Const.K_MY_USERNAME;
import static com.moselo.HomingPigeon.View.Helper.Const.K_THEIR_USERNAME;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListHolder> {

    private List<MessageEntity> roomList;
    private ColorStateList avatarTint;
    private String myUsername;
    private int[] randomColors;

    public RoomListAdapter(List<MessageEntity> roomList, String myUsername) {
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

    public List<MessageEntity> getItems() {
        return roomList;
    }

    public MessageEntity getItemAt(int position) {
        return roomList.get(position);
    }

    class RoomListHolder extends RecyclerView.ViewHolder {

        private TextView tvAvatar, tvUsername, tvLastMessage, tvLastMessageTime;
        private MessageEntity item;

        RoomListHolder(View itemView) {
            super(itemView);

            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            randomColors = itemView.getContext().getResources().getIntArray(R.array.random_colors);
        }

        void onBind(int position) {
            item = getItemAt(position);
            final UserModel userModel = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {},item.getUser());
            final String userID = userModel.getUserID();

            final int randomColor = getRandomColor(userModel.getName());
            avatarTint = ColorStateList.valueOf(randomColor);
            tvAvatar.setText(userModel.getName().substring(0, 1).toUpperCase());
            tvAvatar.setBackgroundTintList(avatarTint);
            tvUsername.setText(userModel.getName());
            tvLastMessage.setText(item.getMessage());
            tvLastMessageTime.setText(TimeFormatter.formatClock(item.getCreated()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

                    Intent intent = new Intent(itemView.getContext(), SampleChatActivity.class);
                    intent.putExtra(K_MY_USERNAME, myUsername);
                    intent.putExtra(K_THEIR_USERNAME, userModel.getName());
                    intent.putExtra(K_COLOR, randomColor);
                    intent.putExtra(K_ROOM_ID, Utils.getInstance()
                            .arrangeRoomId(userID,"2"));
                    itemView.getContext().startActivity(intent);
                }
            });
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