package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.Taptalk.R;

public class TAPImageListAdapter extends TAPBaseAdapter<TAPImageURL, TAPBaseViewHolder<TAPImageURL>> {

    private int gridWidth;

    public TAPImageListAdapter(List<TAPImageURL> items) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPImageURL> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailGridVH(parent, R.layout.tap_cell_thumbnail_grid);
    }

    class ThumbnailGridVH extends TAPBaseViewHolder<TAPImageURL> {

        ConstraintLayout clContainer;
        ImageView ivThumbnail;

        ThumbnailGridVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        @Override
        protected void onBind(TAPImageURL item, int position) {
            clContainer.getLayoutParams().width = gridWidth;
            Glide.with(itemView.getContext()).load(item.getThumbnail()).into(ivThumbnail);

            clContainer.setOnClickListener(v -> {
                // TODO: 23 October 2018 VIEW FULL IMAGE
            });
        }
    }
}
