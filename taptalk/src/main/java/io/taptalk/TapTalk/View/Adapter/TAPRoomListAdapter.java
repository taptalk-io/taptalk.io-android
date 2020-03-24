package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.API.Api.TAPApiManager;
import io.taptalk.TapTalk.DiffCallback.TAPRoomListDiffCallback;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TAPRoomListAdapter extends TAPBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>> {

    private TAPRoomListViewModel vm;
    private TapTalkRoomListInterface tapTalkRoomListInterface;
    private RequestManager glide;

    public TAPRoomListAdapter(TAPRoomListViewModel vm, RequestManager glide, TapTalkRoomListInterface tapTalkRoomListInterface) {
        setItems(vm.getRoomList(), false);
        this.vm = vm;
        this.glide = glide;
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
        private ImageView ivAvatarIcon, ivMute, ivMessageStatus, ivPersonalRoomTypingIndicator, ivGroupRoomTypingIndicator;
        private TextView tvAvatarLabel, tvFullName, tvLastMessage, tvLastMessageTime, tvBadgeUnread, tvGroupSenderName;
        private View vSeparator, vSeparatorFull;

        RoomListVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMute = itemView.findViewById(R.id.iv_mute);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            ivPersonalRoomTypingIndicator = itemView.findViewById(R.id.iv_personal_room_typing_indicator);
            ivGroupRoomTypingIndicator = itemView.findViewById(R.id.iv_group_room_typing_indicator);
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
            TAPUserModel activeUser = TAPChatManager.getInstance().getActiveUser();
            if (null == activeUser) {
                return;
            }

            TAPRoomModel room = item.getLastMessage().getRoom();
            TAPUserModel user = null;
            TAPRoomModel group = null;

            if (room.getRoomType() == TYPE_PERSONAL) {
                user = TAPContactManager.getInstance().getUserData(TAPChatManager.getInstance().getOtherUserIdFromRoom(room.getRoomID()));
            } else if (room.getRoomType() == TYPE_GROUP || room.getRoomType() == TYPE_TRANSACTION) {
                group = TAPGroupManager.Companion.getGetInstance().getGroupData(room.getRoomID());
            }

            // Set room image
            if (null != user && (null == user.getDeleted() || user.getDeleted() <= 0L) &&
                    null != user.getAvatarURL() && !user.getAvatarURL().getThumbnail().isEmpty()) {
                // Load user avatar
                glide.load(user.getAvatarURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Show initial
                        if (itemView.getContext() instanceof Activity) {
                            ((Activity) itemView.getContext()).runOnUiThread(() -> {
                                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.getDefaultAvatarBackgroundColor()));
                                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                                tvAvatarLabel.setText(TAPUtils.getInitials(room.getRoomName(), room.getRoomType() == TYPE_PERSONAL ? 2 : 1));
                                tvAvatarLabel.setVisibility(View.VISIBLE);
                            });
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else if (null != group && !group.isRoomDeleted() && null != group.getRoomImage() &&
                    !group.getRoomImage().getThumbnail().isEmpty()) {
                // Load group image
                glide.load(group.getRoomImage().getThumbnail()).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else if (null != room.getRoomImage() && !room.getRoomImage().getThumbnail().isEmpty()) {
                // Load room image
                glide.load(room.getRoomImage().getThumbnail()).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                // Show initial
                glide.clear(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.getDefaultAvatarBackgroundColor()));
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                tvAvatarLabel.setText(TAPUtils.getInitials(room.getRoomName(), room.getRoomType() == TYPE_PERSONAL ? 2 : 1));
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
            clContainer.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.tapWhite));
            //ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_verified));
            if (room.getRoomType() == TYPE_GROUP) {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                glide.load(R.drawable.tap_ic_group_icon).into(ivAvatarIcon);
            } else {
                ivAvatarIcon.setVisibility(View.GONE);
            }

            // Show/hide separator
            if (position == getItemCount() - 1) {
                vSeparator.setVisibility(View.GONE);
                vSeparatorFull.setVisibility(View.VISIBLE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
                vSeparatorFull.setVisibility(View.GONE);
            }
            //}

            // Set room name
            if (null != user && (null == user.getDeleted() || user.getDeleted() <= 0L) &&
                    null != user.getName() && !user.getName().isEmpty()) {
                tvFullName.setText(user.getName());
            } else if (null != group && !group.isRoomDeleted() && null != group.getRoomName() &&
                    !group.getRoomName().isEmpty()) {
                tvFullName.setText(group.getRoomName());
            } else {
                tvFullName.setText(room.getRoomName());
            }

            // Set last message timestamp
            tvLastMessageTime.setText(item.getLastMessageTimestamp());

            String draft = TAPChatManager.getInstance().getMessageFromDraft(item.getLastMessage().getRoom().getRoomID());
            if (null != draft && !draft.isEmpty()) {
                // Show draft
                tvLastMessage.setText(String.format(itemView.getContext().getString(R.string.tap_format_s_draft), draft));
                tvGroupSenderName.setVisibility(View.GONE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
            } else if (0 < item.getTypingUsersSize() && TYPE_PERSONAL == item.getLastMessage().getRoom().getRoomType()) {
                // Set message to Typing
                tvLastMessage.setText(itemView.getContext().getString(R.string.tap_typing));
                tvGroupSenderName.setVisibility(View.GONE);
                ivPersonalRoomTypingIndicator.setVisibility(View.VISIBLE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
                if (null == ivPersonalRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator);
                }
                //typingAnimationTimer.start();
                //typingIndicatorTimeOutTimer.cancel();
                //typingIndicatorTimeOutTimer.start();
            } else if (1 == item.getTypingUsersSize() && TYPE_PERSONAL != item.getLastMessage().getRoom().getRoomType()) {
                // Set message to typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_format_s_typing_single), item.getFirstTypingUserName());
                tvGroupSenderName.setText(typingStatus);
                tvGroupSenderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapRoomListMessageColor));
                tvLastMessage.setText("");
                tvGroupSenderName.setVisibility(View.VISIBLE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivGroupRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivGroupRoomTypingIndicator);
                }
            } else if (1 < item.getTypingUsersSize() && TYPE_PERSONAL != item.getLastMessage().getRoom().getRoomType()) {
                // Set message to multiple users typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_format_d_people_typing), item.getTypingUsersSize());
                tvGroupSenderName.setText(typingStatus);
                tvGroupSenderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapRoomListMessageColor));
                tvLastMessage.setText("");
                tvGroupSenderName.setVisibility(View.VISIBLE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivGroupRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivGroupRoomTypingIndicator);
                }
            } else if (null != item.getLastMessage().getUser() &&
                    activeUser.getUserID().equals(item.getLastMessage().getUser().getUserID()) &&
                    null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                // Show last message deleted by active user
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_you_deleted_this_message));
                if (room.getRoomType() == TYPE_PERSONAL) {
                    tvGroupSenderName.setVisibility(View.GONE);
                } else {
                    tvGroupSenderName.setText(itemView.getContext().getString(R.string.tap_you));
                    tvGroupSenderName.setVisibility(View.VISIBLE);
                }
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
            } else if (null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                // Show last message deleted by sender
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_this_deleted_message));
                if (room.getRoomType() == TYPE_PERSONAL) {
                    tvGroupSenderName.setVisibility(View.GONE);
                } else {
                    tvGroupSenderName.setText(item.getLastMessage().getUser().getName());
                    tvGroupSenderName.setVisibility(View.VISIBLE);
                }
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
            } else if (TYPE_SYSTEM_MESSAGE == item.getLastMessage().getType()) {
                // Show system message
                tvLastMessage.setText(TAPChatManager.getInstance().formattingSystemMessage(item.getLastMessage()));
                tvGroupSenderName.setVisibility(View.GONE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
            } else if (item.getLastMessage().getRoom().getRoomType() != TYPE_PERSONAL) {
                // Show group/channel room with last message
                tvLastMessage.setText(item.getLastMessage().getBody());
                tvGroupSenderName.setText(activeUser.getUserID().equals(item.getLastMessage().getUser().getUserID()) ? itemView.getContext().getString(R.string.tap_you) : item.getLastMessage().getUser().getName());
                tvGroupSenderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapGroupRoomListSenderNameColor));
                tvGroupSenderName.setVisibility(View.VISIBLE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
                //typingAnimationTimer.cancel();
                //typingIndicatorTimeOutTimer.cancel();
            } else {
                // Show personal room with last message
                tvLastMessage.setText(item.getLastMessage().getBody());
                tvGroupSenderName.setVisibility(View.GONE);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
                ivGroupRoomTypingIndicator.setVisibility(View.GONE);
                //typingAnimationTimer.cancel();
                //typingIndicatorTimeOutTimer.cancel();
            }

            // Check if room is muted
            if (room.isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_room_list_unread_badge_inactive));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_room_list_unread_badge));
            }

            // Change Status Message Icon
            // Message sender is not the active user / last message is system message / room draft exists
            if (null != item.getLastMessage() && (!item.getLastMessage().getUser().getUserID().equals(activeUser.getUserID()) ||
                    TYPE_SYSTEM_MESSAGE == item.getLastMessage().getType()) || (null != draft && !draft.isEmpty())) {
                ivMessageStatus.setImageDrawable(null);
            }
            // Message is deleted
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_block_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageDeleted)));
            }
            // Message is read
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsRead() && item.getLastMessage().getIsRead() && !TapUI.getInstance().isReadStatusHidden()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageRead)));
            }
            // Message is delivered
            else if (null != item.getLastMessage() && null != item.getLastMessage().getDelivered() && item.getLastMessage().getDelivered()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageDelivered)));
            }
            // Message failed to send
            else if (null != item.getLastMessage() && null != item.getLastMessage().getFailedSend() && item.getLastMessage().getFailedSend()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageFailed)));
            }
            // Message sent
            else if (null != item.getLastMessage() && null != item.getLastMessage().getSending() && !item.getLastMessage().getSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageSent)));
            }
            // Message is sending
            else if (null != item.getLastMessage() && null != item.getLastMessage().getSending() && item.getLastMessage().getSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sending_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconMessageSending)));
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
            String myUserID = TAPChatManager.getInstance().getActiveUser().getUserID();
            if (!(myUserID + "-" + myUserID).equals(item.getLastMessage().getRoom().getRoomID())) {
                TAPUtils.startChatActivity(itemView.getContext(), item.getLastMessage().getRoom(), item.getTypingUsers());
            } else {
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.tap_error_invalid_room), Toast.LENGTH_SHORT).show();
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
