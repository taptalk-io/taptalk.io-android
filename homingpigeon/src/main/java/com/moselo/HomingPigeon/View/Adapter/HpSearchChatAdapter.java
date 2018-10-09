package com.moselo.HomingPigeon.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpSearchChatAdapter extends HpBaseAdapter<HpSearchChatModel, HpBaseViewHolder<HpSearchChatModel>> {

    public HpSearchChatAdapter(List<HpSearchChatModel> items) {
        setItems(items, true);
    }

    @Override
    public HpBaseViewHolder<HpSearchChatModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (HpSearchChatModel.MyReturnType.values()[viewType]){
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
        return getItems().get(position).getMyReturnType().ordinal();
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
            if (HpSearchChatModel.MyReturnType.SECTION_TITLE == item.getMyReturnType()) {
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

        private View vSeparator;

        protected MessageItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(HpSearchChatModel item, int position) {
            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);
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
}
