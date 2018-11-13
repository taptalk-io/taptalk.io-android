package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moselo.HomingPigeon.DiffCallback.TAPRoomListDiffCallback;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.TAPBaseViewHolder;
import com.moselo.HomingPigeon.Helper.TAPTimeFormatter;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Interface.TapTalkRoomListInterface;
import com.moselo.HomingPigeon.Manager.TAPChatManager;
import com.moselo.HomingPigeon.Manager.TAPDataManager;
import com.moselo.HomingPigeon.Model.TAPRoomListModel;
import com.moselo.HomingPigeon.Model.TAPRoomModel;
import com.moselo.HomingPigeon.Model.TAPUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

import java.util.List;

public class HpRoomListAdapter extends HpBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>> {

    private HpRoomListViewModel vm;
    private TapTalkRoomListInterface tapTalkRoomListInterface;

    public HpRoomListAdapter(HpRoomListViewModel vm, TapTalkRoomListInterface tapTalkRoomListInterface) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.tapTalkRoomListInterface = tapTalkRoomListInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPRoomListModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListVH(parent, R.layout.hp_cell_user_room);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RoomListVH extends TAPBaseViewHolder<TAPRoomListModel> {

        private final String TAG = RoomListVH.class.getSimpleName();
        private ConstraintLayout clContainer;
        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon, ivMute, ivMessageStatus;
        private TextView tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread;
        private View vSeparator;

        RoomListVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMute = itemView.findViewById(R.id.iv_mute);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(TAPRoomListModel item, int position) {
            final int randomColor = TAPUtils.getInstance().getRandomColor(item.getLastMessage().getUser().getName());
            Resources resource = itemView.getContext().getResources();

            if (null != item.getLastMessage().getRoom().getRoomImage()) {
                GlideApp.with(itemView.getContext()).load(item.getLastMessage().getRoom().getRoomImage().getThumbnail()).centerCrop().into(civAvatar);
            } else {
                ColorStateList avatarTint = ColorStateList.valueOf(randomColor);
                civAvatar.setBackgroundTintList(avatarTint);
            }

            // Change avatar icon and background
            if (vm.getSelectedRooms().containsKey(item.getLastMessage().getRoom().getRoomID())) {
                // Item is selected
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.hp_ic_select));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_black_18));
                vSeparator.setVisibility(View.GONE);
            } else {
                // Item not selected
                // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE / CHECK IF ROOM IS GROUP
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.hp_ic_verified));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_white));
                vSeparator.setVisibility(View.VISIBLE);
            }

            // Set name, last message, and timestamp text
            tvFullName.setText(item.getLastMessage().getRoom().getRoomName());
            tvLastMessage.setText(item.getLastMessage().getBody());
            // TODO: 17 October 2018 FORMAT TIMESTAMP OUTSIDE BIND
            tvLastMessageTime.setText(TAPTimeFormatter.getInstance().durationString(item.getLastMessage().getCreated()));

            // Check if room is muted
            if (item.getLastMessage().getRoom().isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_9b9b9b_rounded_10dp));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            // Change Status Message Icon
            // Message is read
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

            itemView.setOnClickListener(v -> onRoomClicked(itemView, item, position));
            itemView.setOnLongClickListener(v -> onRoomLongClicked(v, item, position));
        }
    }

    private void onRoomClicked(View itemView, TAPRoomListModel item, int position) {
        if (vm.isSelecting()) {
            // Select room when other room is already selected
            onRoomSelected(item, position);
        } else {
            // Open chat room on click
            TAPUserModel myUser = TAPDataManager.getInstance().getActiveUser();

            String myUserID = myUser.getUserID();
            String roomID = item.getLastMessage().getRecipientID().equals(myUserID) ?
                    TAPChatManager.getInstance().arrangeRoomId(myUserID, item.getLastMessage().getUser().getUserID()) :
                    TAPChatManager.getInstance().arrangeRoomId(myUserID, item.getLastMessage().getRecipientID());

            if (!(myUserID + "-" + myUserID).equals(item.getLastMessage().getRoom().getRoomID())) {
                TAPUtils.getInstance().startChatActivity(
                        itemView.getContext(),
                        roomID,
                        item.getLastMessage().getRoom().getRoomName(),
                        item.getLastMessage().getRoom().getRoomImage(),
                        item.getLastMessage().getRoom().getRoomType(),
                        item.getLastMessage().getRoom().getRoomColor());
                TAPDataManager.getInstance().saveRecipientID(item.getLastMessage().getRecipientID());
            } else {
                Toast.makeText(itemView.getContext(), "Invalid Room", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean onRoomLongClicked(View v, TAPRoomListModel item, int position) {
        // Select room on long click
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        onRoomSelected(item, position);
        return true;
    }

    private void onRoomSelected(TAPRoomListModel item, int position) {
        TAPRoomModel room = item.getLastMessage().getRoom();

        if (!vm.getSelectedRooms().containsKey(item.getLastMessage().getRoom().getRoomID())) {
            // Room selected
            vm.getSelectedRooms().put(item.getLastMessage().getRoom().getRoomID(), item);
        } else {
            // Room deselected
            vm.getSelectedRooms().remove(item.getLastMessage().getRoom().getRoomID());
        }
        tapTalkRoomListInterface.onRoomSelected();
        notifyItemChanged(position);
    }

    public void addRoomList(List<TAPRoomListModel> roomList) {
        TAPRoomListDiffCallback diffCallback = new TAPRoomListDiffCallback(getItems(), roomList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        setItemsWithoutNotify(roomList);
        diffResult.dispatchUpdatesTo(this);
    }
}
