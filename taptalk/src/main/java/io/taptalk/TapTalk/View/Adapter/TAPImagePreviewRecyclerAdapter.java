package io.taptalk.TapTalk.View.Adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.Taptalk.R;

public class TAPImagePreviewRecyclerAdapter extends TAPBaseAdapter<Uri, TAPBaseViewHolder<Uri>> {

    public TAPImagePreviewRecyclerAdapter(ArrayList<Uri> imageUris) {
        setItems(imageUris);
    }

    @Override
    public TAPBaseViewHolder<Uri> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImagePreviewVH(viewGroup, R.layout.tap_image_preview_recycler);
    }

    class ImagePreviewVH extends TAPBaseViewHolder<Uri> {
        ImageView ivImagePreview;

        protected ImagePreviewVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivImagePreview = itemView.findViewById(R.id.iv_image_preview);
        }

        @Override
        protected void onBind(Uri item, int position) {
            Glide.with(itemView.getContext()).load(item).apply(new RequestOptions().centerCrop()).into(ivImagePreview);
        }
    }
}
