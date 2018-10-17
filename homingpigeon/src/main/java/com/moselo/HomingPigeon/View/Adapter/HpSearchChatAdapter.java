package com.moselo.HomingPigeon.View.Adapter;

import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpTimeFormatter;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpSearchChatAdapter extends HpBaseAdapter<HpSearchChatModel, HpBaseViewHolder<HpSearchChatModel>> {

    private String searchKeyword;

    public HpSearchChatAdapter(List<HpSearchChatModel> items) {
        setItems(items, true);
    }

    @Override
    public HpBaseViewHolder<HpSearchChatModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (HpSearchChatModel.Type.values()[viewType]){
            case RECENT_TITLE:
                return new RecentTitleVH(parent, R.layout.hp_cell_recent_search_title);
            case RECENT_ITEM:
                return new RecentItemVH(parent, R.layout.hp_cell_recent_search_item);
            case SECTION_TITLE:
                return new RecentTitleVH(parent, R.layout.hp_cell_recent_search_title);
            case CHAT_ITEM:
                return new ChatItemVH(parent, R.layout.hp_cell_search_chat_item);
            case CONTACT_ITEM:
                return new ContactItemVH(parent, R.layout.hp_cell_search_contact_item);
            case MESSAGE_ITEM:
                return new MessageItemVH(parent, R.layout.hp_cell_search_message_item);
            default:
                return new EmptyItemVH(parent, R.layout.hp_cell_search_recent_empty);
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

        protected RecentTitleVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvClearHistory = itemView.findViewById(R.id.tv_clear_history);
            tvRecentTitle = itemView.findViewById(R.id.tv_recent_title);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            if (HpSearchChatModel.Type.SECTION_TITLE == item.getType()) {
                tvClearHistory.setVisibility(View.GONE);
                tvRecentTitle.setText(item.getSectionTitle());
            }
            else tvClearHistory.setVisibility(View.VISIBLE);
        }
    }

    public class RecentItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        private TextView tvSearchText;

        protected RecentItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvSearchText = itemView.findViewById(R.id.tv_search_text);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            tvSearchText.setText(item.getRecentSearch().getSearchText());
        }
    }

    public class ChatItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        private CircleImageView civAvatar;

        protected ChatItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            GlideApp.with(itemView.getContext()).load("https://images.performgroup.com/di/library/GOAL/bd/53/mohamed-salah-liverpool-2018-19_e6x5i309jkl11i2uzq838u0ve.jpg?t=-423079251")
                    .centerCrop().override(HpUtils.getInstance().getScreenWidth(), HpUtils.getInstance().getScreeHeight()).into(civAvatar);
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
                        /* TEMPORARY CHECK FOR NULL IMAGE */null != message.getRoomImage() ?
                        HpUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {}, message.getRoomImage())
                        /* TEMPORARY CHECK FOR NULL IMAGE */: null,
                        message.getRoomType(),
                        message.getRoomColor());
            });
        }
    }

    public class ContactItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        private CircleImageView civContactAvatar;

        protected ContactItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            civContactAvatar = itemView.findViewById(R.id.civ_contact_avatar);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            GlideApp.with(itemView.getContext()).load("https://images.performgroup.com/di/library/GOAL/bd/53/mohamed-salah-liverpool-2018-19_e6x5i309jkl11i2uzq838u0ve.jpg?t=-423079251")
                    .centerCrop().override(HpUtils.getInstance().getScreenWidth(), HpUtils.getInstance().getScreeHeight()).into(civContactAvatar);
        }
    }

    public class EmptyItemVH extends HpBaseViewHolder<HpSearchChatModel> {

        protected EmptyItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {

        }
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
