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
        ImageView ivImagePreview;
        FrameLayout flImagePreview;
        FrameLayout flDelete;

        protected ImagePreviewVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivImagePreview = itemView.findViewById(R.id.iv_image_preview);
            flImagePreview = itemView.findViewById(R.id.fl_image_preview);
            flDelete = itemView.findViewById(R.id.fl_delete);
        }

        @Override
        protected void onBind(TAPMediaPreviewModel item, int position) {
            Glide.with(itemView.getContext()).load(item.getUri()).apply(new RequestOptions().centerCrop()).into(ivImagePreview);

            if (item.isSelected()) {
                flImagePreview.setBackground(itemView.getResources().getDrawable(R.drawable.tap_bg_transparent_stroke_greenblue_2dp));
                flDelete.setVisibility(View.VISIBLE);
            } else {
                flImagePreview.setBackground(null);
                flDelete.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> thumbInterface.onThumbnailTap(position, item));
        }
    }
}
