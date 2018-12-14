package io.taptalk.TapTalk.View.Adapter;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.CountDownTimer;
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

import io.taptalk.TapTalk.DiffCallback.TAPRoomListDiffCallback;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;

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
            Resources resource = itemView.getContext().getResources();

            // Set room image
            if (null != item.getLastMessage().getRoom().getRoomImage() && !item.getLastMessage().getRoom().getRoomImage().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext()).load(item.getLastMessage().getRoom().getRoomImage().getThumbnail()).into(civAvatar);
                civAvatar.setBackground(null);
            } else {
                civAvatar.setImageDrawable(null);
                civAvatar.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_circle_9b9b9b));
                civAvatar.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(item.getLastMessage().getUser().getName())));
            }

            // Change avatar icon and background
            if (vm.getSelectedRooms().containsKey(item.getLastMessage().getRoom().getRoomID())) {
                // Item is selected
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_select));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_black_18));
                vSeparator.setVisibility(View.GONE);
            } else {
                // Item not selected
                // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE / CHECK IF ROOM IS GROUP
                ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_verified));
                clContainer.setBackgroundColor(resource.getColor(R.color.transparent_white));
                vSeparator.setVisibility(View.VISIBLE);
            }

            // Set name and timestamp text
            tvFullName.setText(item.getLastMessage().getRoom().getRoomName());
            tvLastMessageTime.setText(item.getLastMessageTimestamp());

            if (item.isTyping()) {
                // Set message to Typing
                tvLastMessage.setText(itemView.getContext().getString(R.string.typing_1));
                typingAnimationTimer.start();
                typingIndicatorTimeOutTimer.cancel();
                typingIndicatorTimeOutTimer.start();
            } else {
                // Set last message as text
                tvLastMessage.setText(item.getLastMessage().getBody());
                typingAnimationTimer.cancel();
                typingIndicatorTimeOutTimer.cancel();
            }

            // Check if room is muted
            if (item.getLastMessage().getRoom().isMuted()) {
                ivMute.setVisibility(View.VISIBLE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_9b9b9b_rounded_10dp));
            } else {
                ivMute.setVisibility(View.GONE);
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            // Change Status Message Icon
            // Message Sender is not the active User
            if (null != item.getLastMessage() && !item.getLastMessage().getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                ivMessageStatus.setImageDrawable(null);
            }
            // Message is read
            else if (null != item.getLastMessage().getIsRead() && item.getLastMessage().getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_read_green);
            }
            // Message is delivered
            else if (null != item.getLastMessage().getDelivered() && item.getLastMessage().getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_delivered_grey);
            }
            // Message failed to send
            else if (null != item.getLastMessage().getFailedSend() && item.getLastMessage().getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.ic_tap_ic_failed_grey);
            }
            // Message sent
            else if (null != item.getLastMessage().getSending() && !item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sent_grey);
            }
            // Message is sending
            else if (null != item.getLastMessage().getSending() && item.getLastMessage().getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sending_grey);
            }

            // Show unread count
            int unreadCount = item.getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(String.valueOf(item.getUnreadCount()));
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

        private CountDownTimer typingIndicatorTimeOutTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                getItem().setTyping(false);
                int position = getItems().indexOf(getItem());
                if (position < 0) {
                    notifyDataSetChanged();
                } else {
                    notifyItemChanged(position);
                }
            }
        };

        private CountDownTimer typingAnimationTimer = new CountDownTimer(300L, 100L) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                switch (tvLastMessage.length()) {
                    case 7:
                        tvLastMessage.setText(itemView.getContext().getString(R.string.typing_2));
                        break;
                    case 8:
                        tvLastMessage.setText(itemView.getContext().getString(R.string.typing_3));
                        break;
                    default:
                        tvLastMessage.setText(itemView.getContext().getString(R.string.typing_1));
                }
                start();
            }
        };
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
                        item.getLastMessage().getRoom().getRoomColor(),
                        item.isTyping());
                TAPDataManager.getInstance().saveRecipientID(item.getLastMessage().getRecipientID());
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
