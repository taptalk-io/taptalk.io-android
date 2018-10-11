package com.moselo.HomingPigeon.View.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.DiffCallback.RoomListDiffCallback;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Interface.RoomListInterface;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpRoomListModel;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpChatActivity;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_COLOR;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_MY_USERNAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpRoomListAdapter extends HpBaseAdapter<HpRoomListModel, HpBaseViewHolder<HpRoomListModel>> {

    private HpRoomListViewModel vm;
    private RoomListInterface roomListInterface;
    private ColorStateList avatarTint;
    private String myUsername;

    public HpRoomListAdapter(HpRoomListViewModel vm, String myUsername, RoomListInterface roomListInterface) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.myUsername = myUsername;
        this.roomListInterface = roomListInterface;
    }

    public void addRoomList(List<HpRoomListModel> roomList) {
        RoomListDiffCallback diffCallback = new RoomListDiffCallback(getItems(), roomList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        setItemsWithoutNotify(roomList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpRoomListModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListVH(parent, R.layout.hp_cell_user_room);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RoomListVH extends HpBaseViewHolder<HpRoomListModel> {

        private final String TAG = RoomListVH.class.getSimpleName();
        private ConstraintLayout clContainer;
        private ImageView ivAvatarIcon, ivMute, ivMessageStatus;
        private CircleImageView ivAvatar;
        private TextView tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread;
        private HpMessageModel item;

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
        protected void onBind(HpRoomListModel item, int position) {
            final HpUserModel userModel = item.getLastMessage().getUser();
            final int randomColor = HpUtils.getInstance().getRandomColor(userModel.getName());

            if (null != item.getLastMessage().getRoom().getRoomImage()) {
                GlideApp.with(itemView.getContext()).load(item.getLastMessage().getRoom().getRoomImage().getThumbnail()).centerCrop().into(ivAvatar);
            } else {
                avatarTint = ColorStateList.valueOf(randomColor);
                ivAvatar.setBackgroundTintList(avatarTint);
            }

            // Change avatar icon and background
            Resources resource = itemView.getContext().getResources();
            if (item.getLastMessage().getRoom().isSelected()) {
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
            tvFullName.setText(item.getLastMessage().getRoom().getRoomName());
            tvLastMessage.setText(item.getLastMessage().getBody());
            tvLastMessageTime.setText(HpTimeFormatter.durationString(item.getLastMessage().getCreated()));

            // Check if room is muted
            if (item.getLastMessage().getRoom().isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_9b9b9b_rounded_10dp));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            //Change Status Message Icon
            if (null != item.getLastMessage().getIsRead() && item.getLastMessage().getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_read_green);
            }
            // Message is delivered
            else if (null != item.getLastMessage().getDelivered() && item.getLastMessage().getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
            }
            // Message failed to send
            else if (null != item.getLastMessage().getFailedSend() && item.getLastMessage().getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_failed_grey);
            }
            // Message sent
            else if (null != item.getLastMessage().getSending() && !item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sent_grey);
            }
            // Message is sending
            else if (null != item.getLastMessage().getSending() && item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sending_grey);
            }
            else {
                Log.e(TAG, "no status: " + item.getLastMessage().getBody());
            }

            // Show unread count
            int unreadCount = item.getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(item.getUnreadCount() + "");
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
                    item.getLastMessage().getRoom().setSelected(!item.getLastMessage().getRoom().isSelected());
                    roomListInterface.onRoomSelected(item, item.getLastMessage().getRoom().isSelected());
                    notifyItemChanged(position);
                } else {
                    // Open chat room on click
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                    HpUserModel myUser = HpUtils.getInstance().fromJSON(new TypeReference<HpUserModel>() {
                    }, prefs.getString(K_USER, ""));

                    String myUserID = myUser.getUserID();
                    String roomID = item.getLastMessage().getRecipientID().equals(myUserID) ? HpChatManager.getInstance().arrangeRoomId(myUserID, userModel.getUserID()) : HpChatManager.getInstance().arrangeRoomId(myUserID, item.getLastMessage().getRecipientID());

                    if (!(myUserID + "-" + myUserID).equals(item.getLastMessage().getRoom().getRoomID())) {
                        HpChatManager.getInstance().saveUnsentMessage();
                        Intent intent = new Intent(itemView.getContext(), HpChatActivity.class);
                        intent.putExtra(K_MY_USERNAME, myUsername);
                        intent.putExtra(ROOM_NAME, item.getLastMessage().getRoom().getRoomName());
                        intent.putExtra(K_COLOR, randomColor);
                        intent.putExtra(K_ROOM, HpRoomModel.Builder(roomID,
                                item.getLastMessage().getRoom().getRoomName(), item.getLastMessage().getRoom().getRoomType()));
                        itemView.getContext().startActivity(intent);

                        HpDataManager.getInstance().saveRecipientID(itemView.getContext(), item.getLastMessage().getRecipientID());
                    } else {
                        Toast.makeText(itemView.getContext(), "Invalid Room", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Select room on long click
            itemView.setOnLongClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                item.getLastMessage().getRoom().setSelected(!item.getLastMessage().getRoom().isSelected());
                roomListInterface.onRoomSelected(item, item.getLastMessage().getRoom().isSelected());
                notifyItemChanged(position);
                return true;
            });
        }
    }
}
