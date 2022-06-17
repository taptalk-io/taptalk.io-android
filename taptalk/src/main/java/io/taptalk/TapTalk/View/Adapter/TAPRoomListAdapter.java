package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TAPRoomListAdapter extends TAPBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>> {

    private String instanceKey;
    private TAPRoomListViewModel vm;
    private TapTalkRoomListInterface tapTalkRoomListInterface;
    private RequestManager glide;

    public TAPRoomListAdapter(String instanceKey, TAPRoomListViewModel vm, RequestManager glide, TapTalkRoomListInterface tapTalkRoomListInterface) {
        setItems(vm.getRoomList(), false);
        this.instanceKey = instanceKey;
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
        private ImageView ivAvatarIcon;
        private ImageView ivMute;
        private ImageView ivMessageStatus;
        private ImageView ivPersonalRoomTypingIndicator;
//        private ImageView ivGroupRoomTypingIndicator;
        private ImageView ivBadgeMention;
        private TextView tvAvatarLabel;
        private TextView tvFullName;
        private TextView tvLastMessage;
        private TextView tvLastMessageTime;
        private TextView tvBadgeUnread;
//        private TextView tvGroupSenderName;
        private View vSeparator;
        private View vSeparatorFull;
        private LinearLayout llMarkRead;
        private TextView tvMarkRead;
        private ImageView ivMarkRead;

        RoomListVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivMute = itemView.findViewById(R.id.iv_mute);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
            ivPersonalRoomTypingIndicator = itemView.findViewById(R.id.iv_personal_room_typing_indicator);
//            ivGroupRoomTypingIndicator = itemView.findViewById(R.id.iv_group_room_typing_indicator);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
//            tvGroupSenderName = itemView.findViewById(R.id.tv_group_sender_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
            ivBadgeMention = itemView.findViewById(R.id.iv_badge_mention);
            vSeparator = itemView.findViewById(R.id.v_separator);
            vSeparatorFull = itemView.findViewById(R.id.v_separator_full);
            llMarkRead = itemView.findViewById(R.id.ll_mark_read);
            tvMarkRead = itemView.findViewById(R.id.tv_mark_read);
            ivMarkRead = itemView.findViewById(R.id.iv_mark_read);
        }

        @Override
        protected void onBind(TAPRoomListModel item, int position) {
            TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
            if (null == activeUser) {
                return;
            }

            TAPRoomModel room = item.getLastMessage().getRoom();
            TAPUserModel user = null;
            TAPRoomModel group = null;

            if (room.getType() == TYPE_PERSONAL) {
                user = TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()));
            } else if (room.getType() == TYPE_GROUP || room.getType() == TYPE_TRANSACTION) {
                group = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(room.getRoomID());
            }

            // Set room image
            if (null != user && (null == user.getDeleted() || user.getDeleted() <= 0L) &&
                    null != user.getImageURL() && !user.getImageURL().getThumbnail().isEmpty()) {
                // Load user avatar
                glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Show initial
                        if (itemView.getContext() instanceof Activity) {
                            ((Activity) itemView.getContext()).runOnUiThread(() -> {
                                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.getDefaultAvatarBackgroundColor()));
                                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                                tvAvatarLabel.setText(TAPUtils.getInitials(room.getName(), room.getType() == TYPE_PERSONAL ? 2 : 1));
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
            } else if (null != group && !group.isDeleted() && null != group.getImageURL() &&
                    !group.getImageURL().getThumbnail().isEmpty()) {
                // Load group image
                glide.load(group.getImageURL().getThumbnail()).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else if (null != room.getImageURL() && !room.getImageURL().getThumbnail().isEmpty()) {
                // Load room image
                glide.load(room.getImageURL().getThumbnail()).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                // Show initial
                glide.clear(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.getDefaultAvatarBackgroundColor()));
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                tvAvatarLabel.setText(TAPUtils.getInitials(room.getName(), room.getType() == TYPE_PERSONAL ? 2 : 1));
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
            if (room.getType() == TYPE_GROUP) {
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

            // Set room name
            String roomName = TAPChatManager.getInstance(instanceKey).getRoomListTitleText(item, getBindingAdapterPosition(), itemView.getContext());
            tvFullName.setText(roomName);

            // Set last message timestamp
            tvLastMessageTime.setText(item.getLastMessageTimestamp());

            String draft = TAPChatManager.getInstance(instanceKey).getMessageFromDraft(item.getLastMessage().getRoom().getRoomID());
            if (null != draft && !draft.isEmpty()) {
                // Show draft
                tvLastMessage.setText(String.format(itemView.getContext().getString(R.string.tap_format_s_draft), draft));
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
            } else if (0 < item.getTypingUsersSize() && TYPE_PERSONAL == item.getLastMessage().getRoom().getType()) {
                // Set message to Typing
                tvLastMessage.setText(itemView.getContext().getString(R.string.tap_typing));
                ivPersonalRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivPersonalRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator);
                }
            } else if (1 == item.getTypingUsersSize() && TYPE_PERSONAL != item.getLastMessage().getRoom().getType()) {
                // Set message to typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_format_s_typing_single), item.getFirstTypingUserName());
                tvLastMessage.setText(typingStatus);
                tvLastMessage.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapRoomListMessageColor));
                ivPersonalRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivPersonalRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator);
                }
            } else if (1 < item.getTypingUsersSize() && TYPE_PERSONAL != item.getLastMessage().getRoom().getType()) {
                // Set message to multiple users typing
                String typingStatus = String.format(itemView.getContext().getString(R.string.tap_format_d_people_typing), item.getTypingUsersSize());
                tvLastMessage.setText(typingStatus);
                tvLastMessage.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapRoomListMessageColor));
                ivPersonalRoomTypingIndicator.setVisibility(View.VISIBLE);
                if (null == ivPersonalRoomTypingIndicator.getDrawable()) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator);
                }
            } else if (null != item.getLastMessage().getUser() &&
                    activeUser.getUserID().equals(item.getLastMessage().getUser().getUserID()) &&
                    null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                // Show last message deleted by active user
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_you_deleted_this_message));
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
            } else if (null != item.getLastMessage().getIsDeleted() && item.getLastMessage().getIsDeleted()) {
                // Show last message deleted by sender
                tvLastMessage.setText(itemView.getResources().getString(R.string.tap_this_deleted_message));
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
            } else {
                // Get text from listener
                String roomMessage = TAPChatManager.getInstance(instanceKey).getRoomListContentText(item, getBindingAdapterPosition(), itemView.getContext());
                tvLastMessage.setText(roomMessage);
                ivPersonalRoomTypingIndicator.setVisibility(View.GONE);
            }

            // Check if room is muted
            if (room.getIsMuted()) {
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
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_block_red));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageDeleted)));
            }
            // Message is read
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsRead() && item.getLastMessage().getIsRead() && !TapUI.getInstance(instanceKey).isReadStatusHidden()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageRead)));
            }
            // Message is delivered
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsDelivered() && item.getLastMessage().getIsDelivered()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageDelivered)));
            }
            // Message failed to send
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsFailedSend() && item.getLastMessage().getIsFailedSend()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageFailed)));
            }
            // Message sent
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsSending() && !item.getLastMessage().getIsSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageSent)));
            }
            // Message is sending
            else if (null != item.getLastMessage() && null != item.getLastMessage().getIsSending() && item.getLastMessage().getIsSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sending_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageSending)));
            }

            // Show unread count
            int unreadCount = item.getNumberOfUnreadMessages();
            if (unreadCount > 0 || item.isMarkedAsUnread()) {
                if (unreadCount == 0) {
                    tvBadgeUnread.setText("");
                } else if (unreadCount >= 100) {
                    tvBadgeUnread.setText(R.string.tap_over_99);
                } else {
                    tvBadgeUnread.setText(String.valueOf(unreadCount));
                }
                ivMessageStatus.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.VISIBLE);
                glide.load(R.drawable.tap_ic_mark_read_white).fitCenter().into(ivMarkRead);
                tvMarkRead.setText(R.string.tap_read);
            } else {
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvBadgeUnread.setVisibility(View.GONE);
                glide.load(R.drawable.tap_ic_mark_unread_white).fitCenter().into(ivMarkRead);
                tvMarkRead.setText(R.string.tap_unread);
            }

            // Show mention badge
            if (!TapUI.getInstance(instanceKey).isMentionUsernameDisabled() && item.getNumberOfUnreadMentions() > 0) {
                ivBadgeMention.setVisibility(View.VISIBLE);
            } else {
                ivBadgeMention.setVisibility(View.GONE);
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
            if (TAPApiManager.getInstance(instanceKey).isLoggedOut()) {
                // Return if logged out (active user is null)
                return;
            }
            String myUserID = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
            if (!(myUserID + "-" + myUserID).equals(item.getLastMessage().getRoom().getRoomID())) {
                TapUIChatActivity.start(itemView.getContext(), instanceKey, item.getLastMessage().getRoom(), item.getTypingUsers());
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
