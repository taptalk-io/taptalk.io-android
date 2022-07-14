package io.taptalk.TapTalk.View.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_TRANSACTION;

public class TAPSearchChatAdapter extends TAPBaseAdapter<TAPSearchChatModel, TAPBaseViewHolder<TAPSearchChatModel>> {

    private String instanceKey;
    private String searchKeyword;
    private RequestManager glide;
    private TapTalkRoomListInterface roomListInterface;

    // Search chat constructor
    public TAPSearchChatAdapter(String instanceKey, List<TAPSearchChatModel> items, RequestManager glide) {
        this.instanceKey = instanceKey;
        setItems(items, true);
        this.glide = glide;
    }

    // Forward picker constructor
    public TAPSearchChatAdapter(String instanceKey, List<TAPSearchChatModel> items, RequestManager glide, TapTalkRoomListInterface roomListInterface) {
        this.instanceKey = instanceKey;
        setItems(items, true);
        this.glide = glide;
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
            tvRecentTitle.setText(item.getSectionTitle());
            if (TAPSearchChatModel.Type.SECTION_TITLE == item.getType()) {
                tvClearHistory.setVisibility(View.GONE);
            } else {
                tvClearHistory.setVisibility(View.VISIBLE);
                tvClearHistory.setOnClickListener(v -> TAPDataManager.getInstance(instanceKey).deleteAllRecentSearch());
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

        @SuppressLint("PrivateResource")
        @Override
        protected void onBind(TAPSearchChatModel item, int position) {
            if (item.isLastInSection()) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            TAPMessageEntity message = item.getMessage();
            if (null == message) {
                return;
            }

            // Set Room Name
            tvUserName.setText(message.getRoomName());

            // Get highlighted color code
            TypedArray typedArrayMessage = itemView.getContext().obtainStyledAttributes(R.style.tapRoomListMessageHighlightedStyle, R.styleable.TextAppearance);
            String colorCode = Integer.toHexString(typedArrayMessage.getColor(R.styleable.TextAppearance_android_textColor, -1)).substring(2);
            typedArrayMessage.recycle();

            TypedArray typedArraySender = itemView.getContext().obtainStyledAttributes(R.style.tapGroupRoomListSenderNameStyle, R.styleable.TextAppearance);
            String colorSender = Integer.toHexString(typedArraySender.getColor(R.styleable.TextAppearance_android_textColor, -1)).substring(2);
            typedArraySender.recycle();

            // Set message body with highlighted text
            String highlightedText;
            try {
                highlightedText = message.getBody().replaceAll("(?i)([" + searchKeyword + "])",
                        String.format(itemView.getContext().getString(R.string.tap_format_ss_highlighted_string),
                                colorCode, "$1"));
            } catch (PatternSyntaxException e) {
                // Replace regex special characters with Pattern.quote()
                highlightedText = message.getBody().replaceAll("(?i)(" + Pattern.quote(searchKeyword) + ")",
                        String.format(itemView.getContext().getString(R.string.tap_format_ss_highlighted_string),
                                colorCode, "$1"));
            } catch (Exception e) {
                highlightedText = message.getBody().replaceAll(
                        "(?i)(" + searchKeyword.replaceAll("[^A-Za-z0-9 ]", "") + ")",
                        String.format(itemView.getContext().getString(R.string.tap_format_ss_highlighted_string), colorCode, "$1"));
            }

            if (null != item.getMessage() && null != item.getMessage().getRoomType() && item.getMessage().getRoomType() == TAPDefaultConstant.RoomType.TYPE_GROUP) {
                tvLastMessage.setText(Html.fromHtml(String.format("<font color='#%s'>%s: </font> %s", colorSender.length() > 6 ? colorSender.substring(colorSender.length() - 6) : colorSender, TAPUtils.getFirstWordOfString(item.getMessage().getUserFullName()), highlightedText)));
            } else {
                tvLastMessage.setText(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getUserID()) ?
                        Html.fromHtml(String.format("%s: %s", itemView.getContext().getString(R.string.tap_you), highlightedText)) : Html.fromHtml(highlightedText));
            }

            // Set message timestamp
            // TODO: 17 October 2018 PROCESS DATE OUTSIDE BIND
            tvMessageTime.setText(TAPTimeFormatter.durationString(message.getCreated()));

            // Change Status Message Icon
            // Message is read
            if (null != message.getIsRead() && message.getIsRead() && !TapUI.getInstance(instanceKey).isReadStatusHidden()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_read_orange));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageRead)));
            }
            // Message is delivered
            else if (null != message.getDelivered() && message.getDelivered()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_delivered_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageDelivered)));
            }
            // Message failed to send
            else if (null != message.getFailedSend() && message.getFailedSend()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageFailed)));
            }
            // Message sent
            else if (null != message.getSending() && !message.getSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sent_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageSent)));
            }
            // Message is sending
            else if (null != message.getSending() && message.getSending()) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_sending_grey));
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconRoomListMessageSending)));
            }

            clContainer.setOnClickListener(v -> TapUIChatActivity.start(
                    itemView.getContext(),
                    instanceKey,
                    message.getRoomID(),
                    message.getRoomName(),
                    // TODO: 18 October 2018 REMOVE CHECK
                    /* TEMPORARY CHECK FOR NULL IMAGE */null != message.getRoomImage() ?
                            TAPUtils.fromJSON(new TypeReference<TAPImageURL>() {
                            }, message.getRoomImage())
                            /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                    message.getRoomType(),
                    message.getRoomColor(),
                    message.getLocalID()));
        }
    }

    public class RoomItemVH extends TAPBaseViewHolder<TAPSearchChatModel> {

        private View vSeparator;
        private ConstraintLayout clContainer;
        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon, ivBadgeMention;
        private TextView tvAvatarLabel, tvRoomName, tvBadgeUnread;

        RoomItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            vSeparator = itemView.findViewById(R.id.v_separator);
            clContainer = itemView.findViewById(R.id.cl_container);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivBadgeMention = itemView.findViewById(R.id.iv_badge_mention);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvBadgeUnread = itemView.findViewById(R.id.tv_badge_unread);
        }

        @SuppressLint("PrivateResource")
        @Override
        protected void onBind(TAPSearchChatModel item, int position) {
            if (item.isLastInSection()) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            TAPRoomModel room = item.getRoom();
            if (null == room) {
                return;
            }

            TAPUserModel user = null;
            TAPRoomModel group = null;
            if (room.getType() == TYPE_PERSONAL) {
                user = TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.getRoomID()));
            } else if (room.getType() == TYPE_GROUP || room.getType() == TYPE_TRANSACTION) {
                group = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(room.getRoomID());
            }

            // Load avatar
            if (null != user && (null != user.getDeleted() && user.getDeleted() > 0L)) {
                // Deleted user
                glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else if (TAPUtils.isSavedMessagesRoom(room.getRoomID(), instanceKey)) {
                // Saved messages
                glide.load(R.drawable.tap_ic_bookmark_round).fitCenter().into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else if (null != user  && null != user.getImageURL() && !user.getImageURL().getThumbnail().isEmpty()) {
                // Load user avatar
                glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Show initial
                        if (itemView.getContext() instanceof Activity) {
                            ((Activity) itemView.getContext()).runOnUiThread(() -> {
                                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.getContext(), room.getName())));
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
                glide.load(room.getImageURL().getThumbnail()).into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                // Show initial
                glide.clear(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.getContext(), room.getName())));
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                tvAvatarLabel.setText(TAPUtils.getInitials(room.getName(), room.getType() == TYPE_PERSONAL ? 2 : 1));
                tvAvatarLabel.setVisibility(View.VISIBLE);
            }

            // Get highlighted color code
            TypedArray typedArray = itemView.getContext().obtainStyledAttributes(R.style.tapRoomListNameHighlightedStyle, R.styleable.TextAppearance);
            String colorCode = Integer.toHexString(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1)).substring(2);
            typedArray.recycle();

            // Set room name with highlighted text
            String roomName;
            if (null != user && (null == user.getDeleted() || user.getDeleted() <= 0L) &&
                    null != user.getFullname() && !user.getFullname().isEmpty()) {
                roomName = user.getFullname();
            } else if (null != group && !group.isDeleted() && null != group.getName() && !group.getName().isEmpty()) {
                roomName = group.getName();
            } else {
                roomName = room.getName();
            }
            String highlightedText = roomName.replaceAll(
                    "(?i)(" + searchKeyword + ")",
                    String.format(itemView.getContext().getString(R.string.tap_format_ss_highlighted_string), colorCode, "$1"));
            tvRoomName.setText(Html.fromHtml(highlightedText));

            // Set avatar icon
            if (room.getType() == TYPE_GROUP) {
                ivAvatarIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_group_icon));
                ivAvatarIcon.setVisibility(View.VISIBLE);
            } else {
                ivAvatarIcon.setVisibility(View.GONE);
            }

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

            // Show mention badge
            if (!TapUI.getInstance(instanceKey).isMentionUsernameDisabled() && item.getRoomMentionCount() > 0) {
                ivBadgeMention.setVisibility(View.VISIBLE);
            } else {
                ivBadgeMention.setVisibility(View.GONE);
            }

            // Check if room is muted
            if (room.getIsMuted()) {
                tvBadgeUnread.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_room_list_unread_badge_inactive));
            } else {
                tvBadgeUnread.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_room_list_unread_badge));
            }

            clContainer.setOnClickListener(v -> {
                if (null == roomListInterface) {
                    // Open chat room
                    TapUIChatActivity.start(itemView.getContext(), instanceKey, room);
                    TAPRecentSearchEntity recentItem = TAPRecentSearchEntity.Builder(item);
                    TAPDataManager.getInstance(instanceKey).insertToDatabase(recentItem);
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
