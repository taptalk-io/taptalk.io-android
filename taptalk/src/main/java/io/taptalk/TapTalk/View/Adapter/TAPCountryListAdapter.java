package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.recyclerview_fastscroll.views.FastScrollRecyclerView;
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType.COUNTRY_INITIAL;

public class TAPCountryListAdapter extends TAPBaseAdapter<TAPCountryRecycleItem, TAPBaseViewHolder<TAPCountryRecycleItem>>
        implements FastScrollRecyclerView.SectionedAdapter, FastScrollRecyclerView.MeasurableAdapter<TAPBaseViewHolder<TAPCountryRecycleItem>> {

    private ArrayList<Integer> mSectionPositions;

    public TAPCountryListAdapter(List<TAPCountryRecycleItem> items) {
        setItems(items);
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPCountryRecycleItem> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (TAPCountryRecycleItem.RecyclerItemType.values()[i]) {
            case COUNTRY_ITEM:
                return new CountryItemViewHolder(false, viewGroup, R.layout.tap_country_recycle_item);
            case COUNTRY_INITIAL:
                return new CountryInitialViewHolder(viewGroup, R.layout.tap_country_initial_recycle_item);
            case COUNTRY_ITEM_BOTTOM:
                return new CountryItemViewHolder(true, viewGroup, R.layout.tap_country_recycle_item);
            default:
                return new CountryItemViewHolder(false, viewGroup, R.layout.tap_country_recycle_item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItems().get(position).getRecyclerItemType().ordinal();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return getItemAt(position).getCountryInitial() + "";
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable TAPBaseViewHolder<TAPCountryRecycleItem> viewHolder, int viewType) {
        if (COUNTRY_INITIAL == TAPCountryRecycleItem.RecyclerItemType.values()[viewType]) {
            return TAPUtils.getInstance().dpToPx(52);
        } else {
            return TAPUtils.getInstance().dpToPx(44);
        }
    }

    public class CountryItemViewHolder extends TAPBaseViewHolder<TAPCountryRecycleItem> {
        boolean isBottom;
        private View vListBottomLine, vLine;
        private TextView tvCountryName;

        protected CountryItemViewHolder(boolean isBottom, ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            this.isBottom = isBottom;
            vListBottomLine = itemView.findViewById(R.id.v_list_bottom_line);
            vLine = itemView.findViewById(R.id.v_line);
            tvCountryName = itemView.findViewById(R.id.tv_country_name);
        }

        @Override
        protected void onBind(TAPCountryRecycleItem item, int position) {
            if (isBottom) {
                vListBottomLine.setVisibility(View.VISIBLE);
                vLine.setVisibility(View.GONE);
            } else {
                vListBottomLine.setVisibility(View.GONE);
                vLine.setVisibility(View.VISIBLE);
            }
            tvCountryName.setText(item.getCountryListItem().getCommonName());
        }
    }

    public class CountryInitialViewHolder extends TAPBaseViewHolder<TAPCountryRecycleItem> {
        private TextView tvCountryInitial;

        protected CountryInitialViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvCountryInitial = itemView.findViewById(R.id.tv_country_initial);
        }

        @Override
        protected void onBind(TAPCountryRecycleItem item, int position) {
            tvCountryInitial.setText(item.getCountryInitial() + "");
        }
    }
}
