package com.moselo.HomingPigeon.View.Adapter;

import android.view.ViewGroup;

import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Model.SearchChatModel;
import com.moselo.HomingPigeon.R;

public class SearchChatAdapter extends BaseAdapter<SearchChatModel, BaseViewHolder<SearchChatModel>> {

    @Override
    public BaseViewHolder<SearchChatModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (SearchChatModel.MyReturnType.values()[viewType]){
            case RECENT_TITLE:
                return new RecentTitleVH(parent, R.layout.cell_recent_search_title);
            case RECENT_ITEM:
                return null;
            case SECTION_TITLE:
                return null;
            case CHAT_ITEM:
                return null;
            case CONTACT_ITEM:
                return null;
            case MESSAGE_ITEM:
                return null;
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItems().get(position).getMyReturnType().ordinal();
    }

    public class RecentTitleVH extends BaseViewHolder<SearchChatModel> {

        protected RecentTitleVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(SearchChatModel item, int position) {

        }
    }
}
