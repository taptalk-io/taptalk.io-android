package com.moselo.HomingPigeon.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.SearchChatModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class SearchChatAdapter extends BaseAdapter<SearchChatModel, BaseViewHolder<SearchChatModel>> {

    public SearchChatAdapter(List<SearchChatModel> items) {
        setItems(items, true);
    }

    @Override
    public BaseViewHolder<SearchChatModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (SearchChatModel.MyReturnType.values()[viewType]){
            case RECENT_TITLE:
                return new RecentTitleVH(parent, R.layout.cell_recent_search_title);
            case RECENT_ITEM:
                return new RecentItemVH(parent, R.layout.cell_recent_search_item);
            case SECTION_TITLE:
                return new RecentTitleVH(parent, R.layout.cell_recent_search_title);
            case CHAT_ITEM:
                return new ChatItemVH(parent, R.layout.cell_search_chat_item);
            case CONTACT_ITEM:
                return new ContactItemVH(parent, R.layout.cell_search_contact_item);
            case MESSAGE_ITEM:
                return new MessageItemVH(parent, R.layout.cell_search_message_item);
            default:
                return new RecentTitleVH(parent, R.layout.cell_recent_search_title);
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

    public class RecentTitleVH extends BaseViewHolder<SearchChatModel> {
        private TextView tvClearHistory;
        private TextView tvRecentTitle;

        protected RecentTitleVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvClearHistory = itemView.findViewById(R.id.tv_clear_history);
            tvRecentTitle = itemView.findViewById(R.id.tv_recent_title);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {
            if (SearchChatModel.MyReturnType.SECTION_TITLE == item.getMyReturnType()) {
                tvClearHistory.setVisibility(View.GONE);
                tvRecentTitle.setText(item.getSectionTitle());
            }
            else tvClearHistory.setVisibility(View.VISIBLE);
        }
    }

    public class RecentItemVH extends BaseViewHolder<SearchChatModel> {

        private TextView tvSearchText;

        protected RecentItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvSearchText = itemView.findViewById(R.id.tv_search_text);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {
            tvSearchText.setText(item.getRecentSearch().getSearchText());
        }
    }

    public class ChatItemVH extends BaseViewHolder<SearchChatModel> {

        private CircleImageView civAvatar;

        protected ChatItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            civAvatar = itemView.findViewById(R.id.civ_avatar);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {
            GlideApp.with(itemView.getContext()).load("https://images.performgroup.com/di/library/GOAL/bd/53/mohamed-salah-liverpool-2018-19_e6x5i309jkl11i2uzq838u0ve.jpg?t=-423079251")
                    .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civAvatar);
        }
    }

    public class MessageItemVH extends BaseViewHolder<SearchChatModel> {

        private View vSeparator;

        protected MessageItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {
            if (item.isLastInSection())
                vSeparator.setVisibility(View.GONE);
            else vSeparator.setVisibility(View.VISIBLE);
        }
    }

    public class ContactItemVH extends BaseViewHolder<SearchChatModel> {

        private CircleImageView civContactAvatar;

        protected ContactItemVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            civContactAvatar = itemView.findViewById(R.id.civ_contact_avatar);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {
            GlideApp.with(itemView.getContext()).load("https://images.performgroup.com/di/library/GOAL/bd/53/mohamed-salah-liverpool-2018-19_e6x5i309jkl11i2uzq838u0ve.jpg?t=-423079251")
                    .centerCrop().override(Utils.getInstance().getScreenWidth(), Utils.getInstance().getScreeHeight()).into(civContactAvatar);
        }
    }
}
