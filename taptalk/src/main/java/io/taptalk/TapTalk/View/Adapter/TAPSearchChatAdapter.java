package io.taptalk.TapTalk.View.Adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.Taptalk.R;

public class TAPSearchChatAdapter extends TAPBaseAdapter<TAPSearchChatModel, TAPBaseViewHolder<TAPSearchChatModel>> {

    private String searchKeyword;

    private TapTalkRoomListInterface roomListInterface;

    public TAPSearchChatAdapter(List<TAPSearchChatModel> items) {
        setItems(items, true);
    }

    public TAPSearchChatAdapter(List<TAPSearchChatModel> items, TapTalkRoomListInterface roomListInterface) {
        setItems(items, true);
        this.roomListInterface = roomListInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPSearchChatModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (TAPSearchChatModel.Type.values()[viewType]) {
            case RECENT_TITLE:
                return new RecentTitleVH(parent, R.layout.tap_cell_section_title);
            case SECTION_TITLE:
                return new RecentTitleVH(parent, R.layout.tap_cell_section_title);
            case ROOM_ITEM:
                return new RoomItemVH(parent, R.layout.tap_cell_search_room_item);
            case MESSAGE_ITEM:
                return new MessageItemVH(parent, R.layout.tap_cell_search_message_item);
            default:
                return new EmptyItemVH(parent, R.layout.tap_cell_search_empty);
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

    public class RecentTitleVH extends TAPBaseViewHolder<TAPSearchChatModel> {

        private TextView tvClearHistory;
        private TextView tvRecentTitle;

        RecentTitleVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvClearHistory = itemView.findViewById(R.id.tv_clear_history);
            tvRecentTitle = itemView.findViewById(R.id.tv_section_title);
        }

        @Override
        protected void onBind(TAPSearchChatModel item, int position) {
            if (TAPSearchChatModel.Type.SECTION_TITLE == item.getType()) {
                tvClearHistory.setVisibility(View.GONE);
                tvRecentTitle.setText(item.getSectionTitle());
            } else {
                tvClearHistory.setVisibility(View.VISIBLE);
                //ini ngecek karena VH ini di pake di section title jga biar ga slalu ke set listenernya
                tvClearHistory.setOnClickListener(v -> TAPDataManager.getInstance().deleteAllRecentSearch());
            }
        }
    }

    public class MessageItemVH extends TAPBaseViewHolder<TAPSearchChatModel> {

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
        protected void onBind(TAPSearchChatModel item, int position) {
            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);

            TAPMessageEntity message = item.getMessage();
            if (null == message) return;

            // Set Room Name
            tvUserName.setText(message.getRoomName());

            // Set message body with highlighted text
            String highlightedText;
            String colorCode = Integer.toHexString(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark)).substring(2);
            try {
                highlightedText = message.getBody().replaceAll("(?i)([" + searchKeyword + "])",
                        String.format(itemView.getContext().getString(R.string.tap_highlighted_string),
                                colorCode, "$1"));
            } catch (PatternSyntaxException e) {
                // Replace regex special characters with Pattern.quote()
                highlightedText = message.getBody().replaceAll("(?i)(" + Pattern.quote(searchKeyword) + ")",
                        String.format(itemView.getContext().getString(R.string.tap_highlighted_string),
                                colorCode, "$1"));
            } catch (Exception e) {
                highlightedText = message.getBody().replaceAll(
                        "(?i)(" + searchKeyword.replaceAll("[^A-Za-z0-9 ]", "") + ")",
                        String.format(itemView.getContext().getString(R.string.tap_highlighted_string), colorCode, "$1"));
            }
            tvLastMessage.setText(TAPChatManager.getInstance().getActiveUser().getUserID().equals(message.getUserID()) ?
                    Html.fromHtml(String.format("%s: %s", itemView.getContext().getString(R.string.tap_you), highlightedText)) : Html.fromHtml(highlightedText));

            // Set message timestamp
            // TODO: 17 October 2018 PROCESS DATE OUTSIDE BIND
            tvMessageTime.setText(TAPTimeFormatter.getInstance().durationString(message.getCreated()));

            // Change Status Message Icon
            // Message is read
            if (null != message.getIsRead() && message.getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_read_green);
            }
            // Message is delivered
            else if (null != message.getDelivered() && message.getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_delivered_grey);
            }
            // Message failed to send
            else if (null != message.getFailedSend() && message.getFailedSend()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_retry_grey);
            }
            // Message sent
            else if (null != message.getSending() && !message.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sent_grey);
            }
            // Message is sending
            else if (null != message.getSending() && message.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_sending_grey);
            }

            clContainer.setOnClickListener(v -> {
                // TODO: 17 October 2018 OPEN CHAT ROOM & SCROLL POSITION TO MESSAGE
                TAPUtils.getInstance().startChatActivity(itemView.getContext(),
                        message.getRoomID(),
                        message.getRoomName(),
                        // TODO: 18 October 2018 REMOVE CHECK
                        /* TEMPORARY CHECK FOR NULL IMAGE */null != message.getRoomImage() ?
                                TAPUtils.getInstance().fromJSON(new TypeReference<TAPImageURL>() {
                                }, message.getRoomImage())
                                /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                        message.getRoomType(),
                        message.getRoomColor(),
                        message.getLocalID());
            });
        }
    }

    public class RoomItemVH extends TAPBaseViewHolder<TAPSearchChatModel> {

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
        protected void onBind(TAPSearchChatModel item, int position) {
            Resources resource = itemView.getContext().getResources();

            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);

            TAPRoomModel room = item.getRoom();
            if (null == room) return;

            // Load avatar
            if (null != room.getRoomImage() && !room.getRoomImage().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext()).load(room.getRoomImage().getThumbnail()).into(civAvatar);
            } else {
                civAvatar.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_img_default_avatar));
            }

            // Set room name with highlighted text
            String highlightedText = room.getRoomName().replaceAll(
                    "(?i)(" + searchKeyword + ")",
                    String.format(itemView.getContext().getString(R.string.tap_highlighted_string),
                            Integer.toHexString(ContextCompat.getColor(itemView.getContext(),
                                    R.color.colorPrimaryDark)).substring(2), "$1"));
            tvRoomName.setText(Html.fromHtml(highlightedText));

            // Change avatar icon
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE / CHECK IF ROOM IS GROUP
            ivAvatarIcon.setImageDrawable(resource.getDrawable(R.drawable.tap_ic_verified));

            // TODO: 18 October 2018 UPDATE ONLINE STATUS

            // Show unread count
            int unreadCount = room.getUnreadCount();
            if (0 < unreadCount && unreadCount < 100) {
                tvBadgeUnread.setText(room.getUnreadCount() + "");
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else if (unreadCount >= 100) {
                tvBadgeUnread.setText(R.string.tap_over_99);
                tvBadgeUnread.setVisibility(View.VISIBLE);
            } else {
                tvBadgeUnread.setVisibility(View.GONE);
            }

            // Check if room is muted
            if (room.isMuted()) {
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_9b9b9b_rounded_10dp));
            } else {
                tvBadgeUnread.setBackground(resource.getDrawable(R.drawable.tap_bg_primary_primarydark_stroke_primarydark_1dp_rounded_10dp));
            }

            clContainer.setOnClickListener(v -> {
                if (null == roomListInterface) {
                    // Open chat room
                    TAPUtils.getInstance().startChatActivity(itemView.getContext(),
                            room.getRoomID(),
                            room.getRoomName(),
                            room.getRoomImage(),
                            room.getRoomType(),
                            room.getRoomColor(),
                            room.getUnreadCount(),
                            false);

                    TAPRecentSearchEntity recentItem = TAPRecentSearchEntity.Builder(item);
                    TAPDataManager.getInstance().insertToDatabase(recentItem);
                } else {
                    // Trigger listener
                    roomListInterface.onRoomSelected(item.getRoom());
                }
            });
        }
    }

    public class EmptyItemVH extends TAPBaseViewHolder<TAPSearchChatModel> {

        EmptyItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPSearchChatModel item, int position) {

        }
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
