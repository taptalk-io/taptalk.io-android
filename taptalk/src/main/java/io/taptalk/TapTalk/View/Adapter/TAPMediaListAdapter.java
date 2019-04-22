package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPMediaListAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseViewHolder<TAPMessageModel>> {

    private RequestManager glide;
    private int gridWidth;

    public TAPMediaListAdapter(List<TAPMessageModel> items, RequestManager glide) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
        this.glide = glide;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailGridVH(parent, R.layout.tap_cell_thumbnail_grid);
    }

    class ThumbnailGridVH extends TAPBaseViewHolder<TAPMessageModel> {

        ConstraintLayout clContainer;
        ImageView ivThumbnail;

        ThumbnailGridVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            clContainer.getLayoutParams().width = gridWidth;
            if (null != item.getData()) {
                glide.load(TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable((String) item.getData().get(FILE_ID)))
                        .apply(new RequestOptions().placeholder(TAPUtils.getInstance().getRandomColor(String.valueOf(TAPUtils.getInstance().generateRandomNumber(9999999)))))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Drawable thumbnail = new BitmapDrawable(
                                        itemView.getContext().getResources(),
                                        TAPFileUtils.getInstance().decodeBase64(
                                                (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                                        item.getData().get(THUMBNAIL))));
                                ivThumbnail.setImageDrawable(thumbnail);
                                Log.e("]]]]", "onLoadFailed: " + item.getLocalID() + ", " + thumbnail);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.e("]]]]", "onResourceReady: " + item.getLocalID());
                                return false;
                            }
                        })
                        .into(ivThumbnail);
            }

            clContainer.setOnClickListener(v -> {
                Log.e("]]]]]", "onClick: " + item.getType());
                if (item.getType() == TYPE_IMAGE) {
                    // Preview image detail
                    Intent intent = new Intent(itemView.getContext(), TAPImageDetailPreviewActivity.class);
                    intent.putExtra(MESSAGE, item);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            ((Activity) itemView.getContext()),
                            ivThumbnail,
                            itemView.getContext().getString(R.string.tap_transition_view_image));
                    itemView.getContext().startActivity(intent, options.toBundle());
                } else if (item.getType() == TYPE_VIDEO) {
                    // Open video player
                    if (null == item.getData()) {
                        return;
                    }
                    Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                    Log.e("]]]]", "videoUri: " + videoUri);
                    if (null == videoUri) {
                        return;
                    }
                    Intent intent = new Intent(itemView.getContext(), TAPVideoPlayerActivity.class);
                    intent.putExtra(URI, videoUri.toString());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
