package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

        private ImageView ivImagePreview, ivDelete;
        private FrameLayout flImagePreview, flDelete;
        private ProgressBar pbLoading;

        ImagePreviewVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivImagePreview = itemView.findViewById(R.id.iv_image_preview);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            flImagePreview = itemView.findViewById(R.id.fl_image_preview);
            flDelete = itemView.findViewById(R.id.fl_delete);
            pbLoading = itemView.findViewById(R.id.pb_loading);
        }

        @Override
        protected void onBind(TAPMediaPreviewModel item, int position) {
            Glide.with(itemView.getContext()).load(item.getUri()).apply(new RequestOptions().centerCrop()).into(ivImagePreview);

            if (item.isSelected() && item.isLoading()) {
                // Selected - Media is loading
                int padding = TAPUtils.getInstance().dpToPx(9);
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_remove_white));
                ivDelete.setBackground(null);
                ivDelete.setPadding(padding, padding, padding, padding);
                flDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
            } else if (item.isLoading()) {
                // Not selected - Media is loading
                flImagePreview.setBackground(null);
                ivDelete.setImageDrawable(null);
                ivDelete.setBackground(null);
                flDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
            } else if (item.isSelected() && (null == item.isSizeExceedsLimit() || !item.isSizeExceedsLimit())) {
                // Selected - Media ready
                int padding = TAPUtils.getInstance().dpToPx(9);
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_remove_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_remove_media_thumbnail_button_default));
                ivDelete.setPadding(padding, padding, padding, padding);
                flDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            } else if (item.isSelected()) {
                // Selected - Media size exceeds limit
                int padding = TAPUtils.getInstance().dpToPx(9);
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_selected_media_preview_thumbnail));
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_remove_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_remove_media_thumbnail_button_warning));
                ivDelete.setPadding(padding, padding, padding, padding);
                flDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            } else if (!item.isSelected() && (null == item.isSizeExceedsLimit() || !item.isSizeExceedsLimit())) {
                // Not selected - Media ready
                flImagePreview.setBackground(null);
                ivDelete.setImageDrawable(null);
                ivDelete.setBackground(null);
                flDelete.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
            } else {
                // Not selected - Media size exceeds limit
                int padding = TAPUtils.getInstance().dpToPx(4);
                flImagePreview.setBackground(null);
                ivDelete.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_exclamation_white));
                ivDelete.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_remove_media_thumbnail_button_warning));
                ivDelete.setPadding(padding, padding, padding, padding);
                flDelete.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> thumbInterface.onThumbnailTapped(position, item));
        }
    }
}
