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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import io.taptalk.TapTalk.View.Activity.TAPProfileActivity;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPMediaListAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseViewHolder<TAPMessageModel>> {

    private TAPProfileActivity.MediaInterface mediaInterface;
    private RequestManager glide;
    private int gridWidth;

    public TAPMediaListAdapter(List<TAPMessageModel> items, TAPProfileActivity.MediaInterface mediaInterface, RequestManager glide) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
        this.mediaInterface = mediaInterface;
        this.glide = glide;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailGridVH(parent, R.layout.tap_cell_thumbnail_grid);
    }

    class ThumbnailGridVH extends TAPBaseViewHolder<TAPMessageModel> {

        private Activity activity;
        private ConstraintLayout clContainer;
        private FrameLayout flProgress;
        private ImageView ivThumbnail, ivButtonProgress;
        private ProgressBar pbProgress;

        private boolean isMediaReady;

        ThumbnailGridVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            flProgress = itemView.findViewById(R.id.fl_progress);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            activity = (Activity) itemView.getContext();
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            clContainer.getLayoutParams().width = gridWidth;
            Integer downloadProgressValue = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID());
            if (null != item.getData()) {
                new Thread(() -> {
                    BitmapDrawable mediaThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable((String) item.getData().get(FILE_ID));
                    Log.e("]]]]", "getBitmapDrawable: " + mediaThumbnail);
                    if (null != mediaThumbnail) {
                        // Load image from cache
                        activity.runOnUiThread(() -> glide.load(mediaThumbnail)
                                .apply(new RequestOptions().placeholder(ivThumbnail.getDrawable()))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        loadSmallThumbnail(item, downloadProgressValue);
                                        Log.e("]]]]", "onLoadFailed: " + item.getLocalID());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        setMediaReady(item);
                                        Log.e("]]]]", "onResourceReady: " + item.getLocalID());
                                        return false;
                                    }
                                })
                                .into(ivThumbnail));
                    } else {
                        // Load video thumbnail
                        Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                        Log.e("]]]]", "getFileMessageUri: " + videoUri);
                        if (null != videoUri) {
                            activity.runOnUiThread(() -> glide.load(videoUri)
                                    .apply(new RequestOptions().placeholder(ivThumbnail.getDrawable()))
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            loadSmallThumbnail(item, downloadProgressValue);
                                            Log.e("]]]]", "onLoadFailed: " + item.getLocalID());
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            setMediaReady(item);
                                            Log.e("]]]]", "onResourceReady: " + item.getLocalID());
                                            return false;
                                        }
                                    })
                                    .into(ivThumbnail));
                        } else/* if (null == downloadProgressValue)*/ {
                            loadSmallThumbnail(item, downloadProgressValue);
                        }
                    }
                }).start();
            }
        }

        private void setMediaReady(TAPMessageModel item) {
            isMediaReady = true;
            flProgress.setVisibility(View.GONE);
            clContainer.setOnClickListener(v -> mediaInterface.onMediaClicked(item, ivThumbnail, isMediaReady));
        }

        private void loadSmallThumbnail(TAPMessageModel item, Integer downloadProgressValue) {
            isMediaReady = false;
            if (null == item.getData()/* || null != ivThumbnail.getDrawable()*/) {
                return;
            }
            Drawable thumbnail = new BitmapDrawable(
                    itemView.getContext().getResources(),
                    TAPFileUtils.getInstance().decodeBase64(
                            (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                    item.getData().get(THUMBNAIL))));
            activity.runOnUiThread(() -> {
                ivThumbnail.setImageDrawable(thumbnail);
                if (null != downloadProgressValue) {
                    // Show download progress
                    pbProgress.setMax(100);
                    pbProgress.setProgress(downloadProgressValue);
                    ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_cancel_white));
                    clContainer.setOnClickListener(v -> mediaInterface.onCancelDownloadClicked(item));
                } else {
                    // Show download button
                    ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_download_white));
                    clContainer.setOnClickListener(v -> mediaInterface.onMediaClicked(item, ivThumbnail, isMediaReady));
                }
                flProgress.setVisibility(View.VISIBLE);
            });
            Log.e("]]]]", "loadSmallThumbnail: " + thumbnail);
        }
    }
}
