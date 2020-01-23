package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.View.Activity.TAPMediaPreviewActivity;
import io.taptalk.Taptalk.R;

public class TAPMediaPreviewRecyclerAdapter extends TAPBaseAdapter<TAPMediaPreviewModel, TAPBaseViewHolder<TAPMediaPreviewModel>> {

    TAPMediaPreviewActivity.ImageThumbnailPreviewInterface thumbInterface;

    public TAPMediaPreviewRecyclerAdapter(ArrayList<TAPMediaPreviewModel> images, TAPMediaPreviewActivity.ImageThumbnailPreviewInterface thumbInterface) {
        setItems(images);
        this.thumbInterface = thumbInterface;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMediaPreviewModel> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImagePreviewVH(viewGroup, R.layout.tap_cell_media_preview_thumbnail);
    }

    class ImagePreviewVH extends TAPBaseViewHolder<TAPMediaPreviewModel> {

        private ImageView ivImagePreview, ivRemove, ivWarning, ivLoading;
        private FrameLayout flImagePreview, flRemove;

        ImagePreviewVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivImagePreview = itemView.findViewById(R.id.iv_image_preview);
            ivRemove = itemView.findViewById(R.id.iv_remove);
            ivWarning = itemView.findViewById(R.id.iv_warning);
            ivLoading = itemView.findViewById(R.id.iv_loading);
            flImagePreview = itemView.findViewById(R.id.fl_image_preview);
            flRemove = itemView.findViewById(R.id.fl_remove);
        }

        @Override
        protected void onBind(TAPMediaPreviewModel item, int position) {
            Glide.with(itemView.getContext()).load(item.getUri()).apply(new RequestOptions().centerCrop()).into(ivImagePreview);

            if (item.isSelected() && item.isLoading()) {
                // Selected - Media is loading
                flImagePreview.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivLoading.setBackground(null);
                ivRemove.setVisibility(View.VISIBLE);
                ivWarning.setVisibility(View.GONE);
                ivLoading.setVisibility(View.VISIBLE);
                flRemove.setVisibility(View.VISIBLE);
                if (null == ivLoading.getAnimation()) {
                    TAPUtils.rotateAnimateInfinitely(itemView.getContext(), ivLoading);
                }
            } else if (item.isLoading()) {
                // Not selected - Media is loading
                flImagePreview.setBackground(null);
                ivLoading.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_remove_media_thumbnail_button_default));
                ivRemove.setVisibility(View.GONE);
                ivWarning.setVisibility(View.GONE);
                ivLoading.setVisibility(View.VISIBLE);
                flRemove.setVisibility(View.VISIBLE);
                if (null == ivLoading.getAnimation()) {
                    TAPUtils.rotateAnimateInfinitely(itemView.getContext(), ivLoading);
                }
            } else if (item.isSelected() && (null == item.isSizeExceedsLimit() || !item.isSizeExceedsLimit())) {
                // Selected - Media ready
                flImagePreview.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivLoading.clearAnimation();
                ivRemove.setVisibility(View.VISIBLE);
                ivWarning.setVisibility(View.GONE);
                ivLoading.setVisibility(View.GONE);
                flRemove.setVisibility(View.VISIBLE);
            } else if (item.isSelected()) {
                // Selected - Media size exceeds limit
                flImagePreview.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivWarning.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_remove_red_circle_background));
                ivLoading.clearAnimation();
                ivRemove.setVisibility(View.GONE);
                ivWarning.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.GONE);
                flRemove.setVisibility(View.VISIBLE);
            } else if (!item.isSelected() && (null == item.isSizeExceedsLimit() || !item.isSizeExceedsLimit())) {
                // Not selected - Media ready
                flImagePreview.setBackground(null);
                flRemove.setVisibility(View.GONE);
            } else {
                // Not selected - Media size exceeds limit
                flImagePreview.setBackground(null);
                ivWarning.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_warning_red_circle_background));
                ivLoading.clearAnimation();
                ivRemove.setVisibility(View.GONE);
                ivWarning.setVisibility(View.VISIBLE);
                ivLoading.setVisibility(View.GONE);
                flRemove.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(v -> thumbInterface.onThumbnailTapped(position, item));
        }
    }
}
