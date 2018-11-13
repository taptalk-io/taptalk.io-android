package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.TAPBaseViewHolder;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpImageListAdapter extends HpBaseAdapter<HpImageURL, TAPBaseViewHolder<HpImageURL>> {

    private int gridWidth;

    public HpImageListAdapter(List<HpImageURL> items) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<HpImageURL> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailGridVH(parent, R.layout.cell_thumbnail_grid);
    }

    class ThumbnailGridVH extends TAPBaseViewHolder<HpImageURL> {

        ConstraintLayout clContainer;
        ImageView ivThumbnail;

        ThumbnailGridVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        @Override
        protected void onBind(HpImageURL item, int position) {
            clContainer.getLayoutParams().width = gridWidth;
            GlideApp.with(itemView.getContext()).load(item.getThumbnail()).into(ivThumbnail);

            clContainer.setOnClickListener(v -> {
                // TODO: 23 October 2018 VIEW FULL IMAGE
            });
        }
    }
}
