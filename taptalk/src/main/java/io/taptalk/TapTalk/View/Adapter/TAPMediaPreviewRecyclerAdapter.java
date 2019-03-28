package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.View.Activity.TAPMediaPreviewActivity;
import io.taptalk.Taptalk.R;

public class TAPMediaPreviewRecyclerAdapter extends TAPBaseAdapter<TAPMediaPreviewModel, TAPBaseViewHolder<TAPMediaPreviewModel>> {

    TAPMediaPreviewActivity.ImageThumbnailPreviewInterface thumbInterface;

    public TAPMediaPreviewRecyclerAdapter(ArrayList<TAPMediaPreviewModel> images, TAPMediaPreviewActivity.ImageThumbnailPreviewInterface thumbInterface) {
        setItems(images);
        this.thumbInterface = thumbInterface;
    }

    @Override
    public TAPBaseViewHolder<TAPMediaPreviewModel> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImagePreviewVH(viewGroup, R.layout.tap_cell_media_preview_thumbnail);
    }

    class ImagePreviewVH extends TAPBaseViewHolder<TAPMediaPreviewModel> {
        ImageView ivImagePreview, ivDelete;
        FrameLayout flImagePreview, flDelete;

        protected ImagePreviewVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivImagePreview = itemView.findViewById(R.id.iv_image_preview);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            flImagePreview = itemView.findViewById(R.id.fl_image_preview);
            flDelete = itemView.findViewById(R.id.fl_delete);
        }

        @Override
        protected void onBind(TAPMediaPreviewModel item, int position) {
            Glide.with(itemView.getContext()).load(item.getUri()).apply(new RequestOptions().centerCrop()).into(ivImagePreview);

            if (item.isSelected() && !item.isSizeExceedsLimit()) {
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_transparent_stroke_greenblue_2dp));
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_remove_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_circle_charcoal_50));
                flDelete.setVisibility(View.VISIBLE);
            } else if (item.isSelected()) {
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_transparent_stroke_greenblue_2dp));
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_remove_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_circle_coralpink));
                flDelete.setVisibility(View.VISIBLE);
            } else if (!item.isSelected() &&  !item.isSizeExceedsLimit()) {
                flImagePreview.setBackground(null);
                ivDelete.setImageDrawable(null);
                ivDelete.setBackground(null);
                flDelete.setVisibility(View.GONE);
            } else {
                flImagePreview.setBackground(null);
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_exclamation_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_circle_coralpink));
                flDelete.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(v -> thumbInterface.onThumbnailTapped(position, item));
        }
    }
}
