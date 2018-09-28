package com.moselo.HomingPigeon.View.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.TimeFormatter;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.RoomListListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.ChatActivity;
import com.moselo.HomingPigeon.ViewModel.RoomListViewModel;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_COLOR;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_MY_USERNAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_ROOM_ID;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_THEIR_USERNAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class RoomListAdapter extends BaseAdapter<MessageModel, BaseViewHolder<MessageModel>> {

    private RoomListViewModel vm;
    private RoomListListener roomListListener;
    private ColorStateList avatarTint;
    private String myUsername;

    public RoomListAdapter(RoomListViewModel vm, String myUsername, RoomListListener roomListListener) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.myUsername = myUsername;
        this.roomListListener = roomListListener;
    }

    @NonNull
    @Override
    public BaseViewHolder<MessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListVH(parent, R.layout.cell_user_room);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RoomListVH extends BaseViewHolder<MessageModel> {

        private ConstraintLayout clContainer;
        private ImageView ivAvatar, ivAvatarIcon, ivMute, ivMessageStatus;
        private TextView tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread;
        private MessageModel item;

        protected RoomListVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMute = itemView.findViewById(R.id.iv_mute);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
        }

        @Override
        protected void onBind(MessageModel item, int position) {
            final UserModel userModel = item.getUser();
            final int randomColor = Utils.getInstance().getRandomColor(userModel.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
//            tvAvatar.setText(userModel.getName().substring(0, 1).toUpperCase());
            ivAvatar.setBackgroundTintList(avatarTint);

            // Change avatar icon and background
            Resources resource = itemView.getContext().getResources();
            if (item.getRoom().isSelected()) {
                // Item is selected
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_select));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_grey));
            } else {
                // Item not selected
                // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_verified));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent));
            }

            // Set name, last message, and timestamp text
            tvFullName.setText(item.getRoom().getRoomName());
            tvLastMessage.setText(item.getMessage());
            tvLastMessageTime.setText(TimeFormatter.durationString(item.getCreated()));

            // Check if room is muted
            if (item.getRoom().isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.bg_9b9b9b_rounded_10dp));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            // TODO: 6 September 2018 CHANGE MESSAGE STATUS IMAGE

            // Show unread count
            int unreadCount = item.getRoom().getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(item.getRoom().getUnreadCount() + "");
                ivMessageStatus.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else if (unreadCount >= 100) {
                tvBadgeUnread.setText(R.string.over_99);
                ivMessageStatus.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else {
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvBadgeUnread.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (vm.isSelecting()) {
                    // Select room when other room is already selected
                    item.getRoom().setSelected(!item.getRoom().isSelected());
                    roomListListener.onRoomSelected(item, item.getRoom().isSelected());
                    notifyItemChanged(position);
                } else {
                    // Open chat room on click
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                    UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
                    }, prefs.getString(K_USER, ""));

                    String myUserID = myUser.getUserID();
                    String roomID = item.getRecipientID().equals(myUserID) ? ChatManager.getInstance().arrangeRoomId(myUserID, userModel.getUserID()) : ChatManager.getInstance().arrangeRoomId(myUserID, item.getRecipientID());

                    if (!(myUserID + "-" + myUserID).equals(item.getRoom().getRoomID())) {
                        ChatManager.getInstance().saveUnsentMessage();
                        Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                        intent.putExtra(K_MY_USERNAME, myUsername);
                        intent.putExtra(ROOM_NAME, item.getRoom().getRoomName());
                        intent.putExtra(K_COLOR, randomColor);
                        intent.putExtra(K_ROOM, RoomModel.Builder(roomID,
                                item.getRoom().getRoomName(), item.getRoom().getRoomType()));
                        itemView.getContext().startActivity(intent);

                        DataManager.getInstance().saveRecipientID(itemView.getContext(), item.getRecipientID());
                    } else {
                        Toast.makeText(itemView.getContext(), "Invalid Room", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Select room on long click
            itemView.setOnLongClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                item.getRoom().setSelected(!item.getRoom().isSelected());
                roomListListener.onRoomSelected(item, item.getRoom().isSelected());
                notifyItemChanged(position);
                return true;
            });
        }
    }
}
