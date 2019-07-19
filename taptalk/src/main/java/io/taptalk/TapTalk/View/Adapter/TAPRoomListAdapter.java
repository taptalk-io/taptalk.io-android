package io.taptalk.TapTalk.View.Adapter;

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

import com.bumptech.glide.Glide;

import java.util.List;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.DiffCallback.TAPRoomListDiffCallback;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TAPRoomListAdapter extends TAPBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>> {

    private TAPRoomListViewModel vm;
    private TapTalkRoomListInterface tapTalkRoomListInterface;

    public TAPRoomListAdapter(TAPRoomListViewModel vm, TapTalkRoomListInterface tapTalkRoomListInterface) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.tapTalkRoomListInterface = tapTalkRoomListInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPRoomListModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListVH(parent, R.layout.tap_cell_user_room);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RoomListVH extends TAPBaseViewHolder<TAPRoomListModel> {

        private final String TAG = RoomListVH.class.getSimpleName();
        private ConstraintLayout clContainer;
        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon, ivMute, ivMessageStatus, ivRoomTypingIndicator;
        private TextView tvAvatarLabel, tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread, tvGroupSenderName;
        private View vSeparator, vSeparatorFull;

        RoomListVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMute = itemView.findViewById(R.id.iv_mute);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            ivRoomTypingIndicator = itemView.findViewById(R.id.iv_room_typing_indicator);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvGroupSenderName = itemView.findViewById(R.id.tv_group_sender_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vSeparatorFull = itemView.findViewById(R.id.v_separator_full);
        }

        @Override
        protected void onBind(TAPRoomListModel item, int position) {
            Resources resource = itemView.getContext().getResources();
            TAPRoomModel room = item.getLastMessage().getRoom();

            // Set room image
            if (null != room.getRoomImage() && !room.getRoomImage().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext()).load(room.getRoomImage().getThumbnail()).into(civAvatar);
                civAvatar.setImageTintList(null);
                tvAvatarLabel.setVisibility(View.GONE);
//            } else if (null != item.getLastMessage() && null != room && TYPE_GROUP == room.getRoomType()) {
//                civAvatar.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_group_avatar_blank));
//            } else {
//                civAvatar.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_img_default_avatar));
            } else {
                civAvatar.setImageTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(room.getRoomName())));
                civAvatar.setImageResource(R.drawable.tap_bg_circle_9b9b9b);
                tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(room.getRoomName(), room.getRoomType() == TYPE_PERSONAL ? 2 : 1));
                tvAvatarLabel.setVisibility(View.VISIBLE);
            }

            // Change avatar icon and background
            //if (vm.getSelectedRooms().containsKey(room.getRoomID())) {
            // Item is selected
            //    clContainer.setBackgroundColor(resource.getColor(R.color.tap_transparent_black_18));
            //    ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_select));
            //    ivAvatarIcon.setVisibility(View.VISIBLE);
            //    vSeparator.setVisibility(View.GONE);
            //    vSeparatorFull.setVisibility(View.GONE);
            //} else {
            // Item not selected
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE / CHECK IF ROOM IS GROUP
            clContainer.setBackgroundColor(resource.getColor(R.color.tapWhite));
            //ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_verified));
            if (room.getRoomType() == TYPE_GROUP) {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(R.drawable.tap_ic_group_icon).into(ivAvatarIcon);
                tvGroupSenderName.setVisibility(View.VISIBLE);
                tvGroupSenderName.setText(TAPChatManager.getInstance().getActiveUser().getUserID().equals(item.getLastMessage().getUser().getUserID()) ? itemView.getContext().getString(R.string.tap_you) : item.getLastMessage().getUser().getName());
            } else {
                tvGroupSenderName.setVisibility(View.GONE);
                ivAvatarIcon.setVisibility(View.GONE);
            }
            if (position == getItemCount() - 1) {
                vSeparator.setVisibility(View.GONE);
                vSeparatorFull.setVisibility(View.VISIBLE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
                vSeparatorFull.setVisibility(View.GONE);
            }
            //}

            // Set name and timestamp text
            tvFullName.setText(room.getRoomName());
            tvLastMessageTime.setText(item.getLastMessageTimestamp());

            if (0 < item.getTypingUsersSize() && TYPE_PERSONAL == item.getLastMessage().getRoom().getRoomType()) {
                // Set message to Typing
                tvLastMessage.setText(itemView.getContext().getString(R.string.tap_typing));
                ivRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivRoomTypingIndicator.getDrawable()) {
                    Glide.with(itemView.getContext()).load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                }
                //typingAnimationTimer.start();
                //typingIndicatorTimeOutTimer.cancel();
                //typingIndicatorTimeOutTimer.start();
            } else if (1 == item.getTypingUsersSize() && TYPE_GROUP == item.getLastMessage().getRoom().getRoomType()) {
                // Set message to Typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_typing_single), item.getFirstTypingUserName());
                tvLastMessage.setText(typingStatus);
                ivRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivRoomTypingIndicator.getDrawable()) {
                    Glide.with(itemView.getContext()).load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                }
            } else if (1 < item.getTypingUsersSize() && TYPE_GROUP == item.getLastMessage().getRoom().getRoomType()) {
                // Set message to Typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_people_typing), item.getTypingUsersSize());
                tvLastMessage.setText(typingStatus);
                ivRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivRoomTypingIndicator.getDrawable()) {
                    Glide.with(itemView.getContext()).load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                }
            } else if (null != TAPChatManager.getInstance().getActiveUser() && null != item.getLastMessage().getUser() &&
                    TAPChatManager.getInstance().getActiveUser().getUserID().equals(item.getLastMessage().getUser().getUserID()) &&
                    null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_you_deleted_this_message));
                ivRoomTypingIndicator.setVisibility(View.GONE);
            } else if (null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_this_deleted_message));
                ivRoomTypingIndicator.setVisibility(View.GONE);
            } else if (TYPE_SYSTEM_MESSAGE == item.getLastMessage().getType()) {
                tvLastMessage.setText(TAPChatManager.getInstance().formattingSystemMessage(item.getLastMessage()));
                ivRoomTypingIndicator.setVisibility(View.GONE);
            } else {
                // Set last message as text
                tvLastMessage.setText(item.getLastMessage().getBody());
                ivRoomTypingIndicator.setVisibility(View.GONE);
                //typingAnimationTimer.cancel();
                //typingIndicatorTimeOutTimer.cancel();
            }

            // Check if room is muted
            if (room.isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_room_list_unread_badge_inactive));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_room_list_unread_badge));
            }

            // Change Status Message Icon
            // Message Sender is not the active User
            if (null != item.getLastMessage() && (!item.getLastMessage().getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID()) ||
                    TYPE_SYSTEM_MESSAGE == item.getLastMessage().getType())) {
                ivMessageStatus.setImageDrawable(null);
            }
            // Message is deleted
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_block_grey);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageDeleted)));
            }
            // Message is read
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsRead() && item.getLastMessage().getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_read_orange);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageRead)));
            }
            // Message is delivered
            else if (null != item.getLastMessage() && null != item.getLastMessage().getDelivered() && item.getLastMessage().getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_delivered_grey);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageDelivered)));
            }
            // Message failed to send
            else if (null != item.getLastMessage() && null != item.getLastMessage().getFailedSend() && item.getLastMessage().getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_warning_red_circle_background);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageFailed)));
            }
            // Message sent
            else if (null != item.getLastMessage() && null != item.getLastMessage().getSending() && !item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sent_grey);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageSent)));
            }
            // Message is sending
            else if (null != item.getLastMessage() && null != item.getLastMessage().getSending() && item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sending_grey);
                ivMessageStatus.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconMessageSending)));
            }

            // Show unread count
            int unreadCount = item.getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(String.valueOf(item.getUnreadCount()));
                ivMessageStatus.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else if (unreadCount >= 100) {
                tvBadgeUnread.setText(R.string.tap_over_99);
                ivMessageStatus.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else {
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvBadgeUnread.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> onRoomClicked(itemView, item, position));
            // TODO: 21 December 2018 TEMPORARILY DISABLED FEATURE
            //itemView.setOnLongClickListener(v -> onRoomLongClicked(v, item, position));
        }
    }

    private void onRoomClicked(View itemView, TAPRoomListModel item, int position) {
        if (vm.isSelecting()) {
            // Select room when other room is already selected
            onRoomSelected(item, position);
        } else {
            // Open chat room on click
            if (TAPApiManager.getInstance().isLogout()) {
                // Return if logged out (active user is null)
                return;
            }
            TAPUserModel myUser = TAPChatManager.getInstance().getActiveUser();

            String myUserID = myUser.getUserID();

            if (TYPE_PERSONAL == item.getLastMessage().getRoom().getRoomType() && !(myUserID + "-" + myUserID).equals(item.getLastMessage().getRoom().getRoomID())) {
                String roomID = item.getLastMessage().getRecipientID().equals(myUserID) ?
                        TAPChatManager.getInstance().arrangeRoomId(myUserID, item.getLastMessage().getUser().getUserID()) :
                        TAPChatManager.getInstance().arrangeRoomId(myUserID, item.getLastMessage().getRecipientID());

                TAPUtils.getInstance().startChatActivity(
                        itemView.getContext(),
                        roomID,
                        item.getLastMessage().getRoom().getRoomName(),
                        item.getLastMessage().getRoom().getRoomImage(),
                        item.getLastMessage().getRoom().getRoomType(),
                        item.getLastMessage().getRoom().getRoomColor(),
                        item.getLastMessage().getRoom().getUnreadCount(),
                        false);
                TAPDataManager.getInstance().saveRecipientID(item.getLastMessage().getRecipientID());
            } else if (TYPE_GROUP == item.getLastMessage().getRoom().getRoomType()) {
                TAPUtils.getInstance().startChatActivity(itemView.getContext(), item.getLastMessage().getRoom());
            } else {
                Toast.makeText(itemView.getContext(), "Invalid Room.", Toast.LENGTH_SHORT).show();
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
        String roomID = item.getLastMessage().getRoom().getRoomID();
        if (!vm.getSelectedRooms().containsKey(roomID)) {
            // Room selected
            vm.getSelectedRooms().put(roomID, item);
        } else {
            // Room deselected
            vm.getSelectedRooms().remove(roomID);
        }
        tapTalkRoomListInterface.onRoomSelected(item.getLastMessage().getRoom());
        notifyItemChanged(position);
    }

    public void addRoomList(List<TAPRoomListModel> roomList) {
        TAPRoomListDiffCallback diffCallback = new TAPRoomListDiffCallback(getItems(), roomList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        setItemsWithoutNotify(roomList);
        diffResult.dispatchUpdatesTo(this);
    }
}
