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
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Listener.RoomListListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpChatActivity;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_COLOR;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_MY_USERNAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpRoomListAdapter extends HpBaseAdapter<MessageModel, HpBaseViewHolder<MessageModel>> {

    private HpRoomListViewModel vm;
    private RoomListListener roomListListener;
    private ColorStateList avatarTint;
    private String myUsername;

    public HpRoomListAdapter(HpRoomListViewModel vm, String myUsername, RoomListListener roomListListener) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.myUsername = myUsername;
        this.roomListListener = roomListListener;
    }

    @NonNull
    @Override
    public HpBaseViewHolder<MessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListVH(parent, R.layout.hp_cell_user_room);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RoomListVH extends HpBaseViewHolder<MessageModel> {

        private final String TAG = RoomListVH.class.getSimpleName();
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
            final int randomColor = HpUtils.getInstance().getRandomColor(userModel.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
//            tvAvatar.setText(userModel.getName().substring(0, 1).toUpperCase());
            ivAvatar.setBackgroundTintList(avatarTint);

            // Change avatar icon and background
            Resources resource = itemView.getContext().getResources();
            if (item.getRoom().isSelected()) {
                // Item is selected
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.hp_ic_select));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_black_18));
            } else {
                // Item not selected
                // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.hp_ic_verified));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent));
            }

            // Set name, last message, and timestamp text
            tvFullName.setText(item.getRoom().getRoomName());
            tvLastMessage.setText(item.getMessage());
            tvLastMessageTime.setText(HpTimeFormatter.durationString(item.getCreated()));

            // Check if room is muted
            if (item.getRoom().isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_9b9b9b_rounded_10dp));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            //Change Status Message Icon
            if (null != item.getIsRead() && item.getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_read_green);
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
            }
            // Message failed to send
            else if (null != item.getFailedSend() && item.getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_failed_grey);
            }
            // Message sent
            else if (null != item.getSending() && !item.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sent_grey);
            }
            // Message is sending
            else if (null != item.getSending() && item.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sending_grey);
            }
            else {
                Log.e(TAG, "no status: " + item.getMessage());
            }

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
                    UserModel myUser = HpUtils.getInstance().fromJSON(new TypeReference<UserModel>() {
                    }, prefs.getString(K_USER, ""));

                    String myUserID = myUser.getUserID();
                    String roomID = item.getRecipientID().equals(myUserID) ? ChatManager.getInstance().arrangeRoomId(myUserID, userModel.getUserID()) : ChatManager.getInstance().arrangeRoomId(myUserID, item.getRecipientID());

                    if (!(myUserID + "-" + myUserID).equals(item.getRoom().getRoomID())) {
                        ChatManager.getInstance().saveUnsentMessage();
                        Intent intent = new Intent(itemView.getContext(), HpChatActivity.class);
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
