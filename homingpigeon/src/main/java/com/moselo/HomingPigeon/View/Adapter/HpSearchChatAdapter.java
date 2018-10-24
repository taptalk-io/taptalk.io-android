package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpSearchChatAdapter extends HpBaseAdapter<HpSearchChatModel, HpBaseViewHolder<HpSearchChatModel>> {

    private String searchKeyword;

    public HpSearchChatAdapter(List<HpSearchChatModel> items) {
        setItems(items, true);
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpSearchChatModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (HpSearchChatModel.Type.values()[viewType]) {
            case RECENT_TITLE:
                return new RecentTitleVH(parent, R.layout.hp_cell_section_title);
            case SECTION_TITLE:
                return new RecentTitleVH(parent, R.layout.hp_cell_section_title);
            case ROOM_ITEM:
                return new RoomItemVH(parent, R.layout.hp_cell_search_room_item);
            case MESSAGE_ITEM:
                return new MessageItemVH(parent, R.layout.hp_cell_search_message_item);
            default:
                return new EmptyItemVH(parent, R.layout.hp_cell_search_empty);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItems().get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class RecentTitleVH extends HpBaseViewHolder<HpSearchChatModel> {

        private TextView tvClearHistory;
        private TextView tvRecentTitle;

        RecentTitleVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvClearHistory = itemView.findViewById(R.id.tv_clear_history);
            tvRecentTitle = itemView.findViewById(R.id.tv_section_title);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            if (HpSearchChatModel.Type.SECTION_TITLE == item.getType()) {
                tvClearHistory.setVisibility(View.GONE);
                tvRecentTitle.setText(item.getSectionTitle());
            } else {
                tvClearHistory.setVisibility(View.VISIBLE);
                //ini ngecek karena VH ini di pake di section title jga biar ga slalu ke set listenernya
                tvClearHistory.setOnClickListener(v -> HpDataManager.getInstance().deleteAllRecentSearch());
            }
        }
    }

    public class MessageItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        private ConstraintLayout clContainer;
        private View vSeparator;
        private TextView tvUserName;
        private TextView tvLastMessage;
        private TextView tvMessageTime;
        private ImageView ivMessageStatus;

        MessageItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            vSeparator = itemView.findViewById(R.id.v_separator);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
            ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);

            HpMessageEntity message = item.getMessage();
            if (null == message) return;

            // Set Room Name
            tvUserName.setText(message.getRoomName());

            // Set message body with highlighted text
            String highlightedText = message.getBody().replaceAll("(?i)(" + searchKeyword + ")", String.format(itemView.getContext().getString(R.string.highlighted_string), "$1"));
            tvLastMessage.setText(HpDataManager.getInstance().getActiveUser().getUserID().equals(message.getUserID()) ?
                    Html.fromHtml(String.format("%s: %s", itemView.getContext().getString(R.string.you), highlightedText)) : Html.fromHtml(highlightedText));

            // Set message timestamp
            // TODO: 17 October 2018 PROCESS DATE OUTSIDE BIND
            tvMessageTime.setText(HpTimeFormatter.durationString(message.getCreated()));

            // Change Status Message Icon
            // Message is read
            if (null != message.getIsRead() && message.getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_read_green);
            }
            // Message is delivered
            else if (null != message.getDelivered() && message.getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_delivered_grey);
            }
            // Message failed to send
            else if (null != message.getFailedSend() && message.getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_failed_grey);
            }
            // Message sent
            else if (null != message.getSending() && !message.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sent_grey);
            }
            // Message is sending
            else if (null != message.getSending() && message.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.hp_ic_sending_grey);
            }

            clContainer.setOnClickListener(v -> {
                // TODO: 17 October 2018 OPEN CHAT ROOM & SCROLL POSITION TO MESSAGE
                HpUtils.getInstance().startChatActivity(itemView.getContext(),
                        message.getRoomID(),
                        message.getRoomName(),
                        // TODO: 18 October 2018 REMOVE CHECK
                        /* TEMPORARY CHECK FOR NULL IMAGE */null != message.getRoomImage() ?
                                HpUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {
                                }, message.getRoomImage())
                                /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                        message.getRoomType(),
                        message.getRoomColor());
            });
        }
    }

    public class RoomItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        private View vSeparator;
        private ConstraintLayout clContainer;
        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon;
        private TextView tvRoomName, tvBadgeUnread;

        RoomItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            vSeparator = itemView.findViewById(R.id.v_separator);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            Resources resource = itemView.getContext().getResources();

            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);

            HpRoomModel room = item.getRoom();
            if (null == room) return;

            // Load avatar
            if (null != room.getRoomImage() && null != room.getRoomImage().getThumbnail()) {
                GlideApp.with(itemView.getContext()).load(room.getRoomImage().getThumbnail()).into(civAvatar);
            }

            // Set room name with highlighted text
            String highlightedText = room.getRoomName().replaceAll("(?i)(" + searchKeyword + ")", String.format(itemView.getContext().getString(R.string.highlighted_string), "$1"));
            tvRoomName.setText(Html.fromHtml(highlightedText));

            // Change avatar icon
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE / CHECK IF ROOM IS GROUP
            ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.hp_ic_verified));

            // TODO: 18 October 2018 UPDATE ONLINE STATUS

            // Show unread count
            int unreadCount = room.getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(room.getUnreadCount() + "");
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else if (unreadCount >= 100) {
                tvBadgeUnread.setText(R.string.over_99);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else {
                tvBadgeUnread.setVisibility(View.GONE);
            }

            // Check if room is muted
            if (room.isMuted()) {
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_9b9b9b_rounded_10dp));
            } else {
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.hp_bg_amethyst_mediumpurple_270_rounded_10dp));
            }

            clContainer.setOnClickListener(v -> {
                HpUtils.getInstance().startChatActivity(itemView.getContext(),
                        room.getRoomID(),
                        room.getRoomName(),
                        room.getRoomImage(),
                        room.getRoomType(),
                        room.getRoomColor());

                HpRecentSearchEntity recentItem = HpRecentSearchEntity.Builder(item);
                HpDataManager.getInstance().insertToDatabase(recentItem);
            });
        }
    }

    public class EmptyItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        EmptyItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {

        }
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
