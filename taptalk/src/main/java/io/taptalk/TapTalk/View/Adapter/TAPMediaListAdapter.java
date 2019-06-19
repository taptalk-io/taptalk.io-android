package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPMediaListAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseViewHolder<TAPMessageModel>> {

    private TAPChatProfileActivity.MediaInterface mediaInterface;
    private RequestManager glide;
    private int gridWidth;

    public TAPMediaListAdapter(List<TAPMessageModel> items, TAPChatProfileActivity.MediaInterface mediaInterface, RequestManager glide) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
        this.mediaInterface = mediaInterface;
        this.glide = glide;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailGridVH(parent, R.layout.tap_cell_media_thumbnail_grid);
    }

    class ThumbnailGridVH extends TAPBaseViewHolder<TAPMessageModel> {

        private Activity activity;
        private ConstraintLayout clContainer;
        private FrameLayout flProgress;
        private ImageView ivThumbnail, ivButtonProgress, ivVideoIcon;
        private TextView tvMediaInfo;
        private ProgressBar pbProgress;
        private View vThumbnailOverlay;

        private Drawable thumbnail;
        private boolean isMediaReady;

        ThumbnailGridVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            flProgress = itemView.findViewById(R.id.fl_progress);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            ivButtonProgress = itemView.findViewById(R.id.iv_button_progress);
            ivVideoIcon = itemView.findViewById(R.id.iv_video_icon);
            tvMediaInfo = itemView.findViewById(R.id.tv_media_info);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            vThumbnailOverlay = itemView.findViewById(R.id.v_thumbnail_overlay);
            activity = (Activity) itemView.getContext();
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            clContainer.getLayoutParams().width = gridWidth;
            Integer downloadProgressValue = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(item.getLocalID());
            if (null != item.getData()) {
                new Thread(() -> {
                    BitmapDrawable mediaThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable((String) item.getData().get(FILE_ID));
                    if (null != mediaThumbnail) {
                        // Load image from cache
                        activity.runOnUiThread(() -> glide.load(mediaThumbnail)
                                //.apply(new RequestOptions().placeholder(ivThumbnail.getDrawable()))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        loadSmallThumbnail(item, downloadProgressValue);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        setMediaReady(item);
                                        return false;
                                    }
                                })
                                .into(ivThumbnail));
                    } else {
                        // Load video thumbnail
                        Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(item.getRoom().getRoomID(), (String) item.getData().get(FILE_ID));
                        if (null != videoUri) {
                            activity.runOnUiThread(() -> glide.load(videoUri)
                                    //.apply(new RequestOptions().placeholder(ivThumbnail.getDrawable()))
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            loadSmallThumbnail(item, downloadProgressValue);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            setMediaReady(item);
                                            return false;
                                        }
                                    })
                                    .into(ivThumbnail));
                        } else {
                            loadSmallThumbnail(item, downloadProgressValue);
                        }
                    }
                }).start();
            }
            if (item.getType() == TYPE_VIDEO) {
                ivVideoIcon.setVisibility(View.VISIBLE);
            } else {
                ivVideoIcon.setVisibility(View.GONE);
            }
        }

        private void setMediaReady(TAPMessageModel item) {
            isMediaReady = true;
            clContainer.setOnClickListener(v -> mediaInterface.onMediaClicked(item, ivThumbnail, isMediaReady));
            if (item.getType() == TYPE_VIDEO && null != item.getData()) {
                Number duration = (Number) item.getData().get(DURATION);
                if (null != duration) {
                    tvMediaInfo.setText(TAPUtils.getInstance().getMediaDurationString(duration.intValue(), duration.intValue()));
                    tvMediaInfo.setVisibility(View.VISIBLE);
                    vThumbnailOverlay.setVisibility(View.VISIBLE);
                } else {
                    tvMediaInfo.setVisibility(View.GONE);
                    vThumbnailOverlay.setVisibility(View.VISIBLE);
                }
            } else {
                tvMediaInfo.setVisibility(View.GONE);
                vThumbnailOverlay.setVisibility(View.GONE);
            }
            flProgress.setVisibility(View.GONE);
        }

        private void loadSmallThumbnail(TAPMessageModel item, Integer downloadProgressValue) {
            isMediaReady = false;
            if (null == item.getData()) {
                return;
            }
            new Thread(() -> {
                if (null == thumbnail) {
                    thumbnail = new BitmapDrawable(
                            itemView.getContext().getResources(),
                            TAPFileUtils.getInstance().decodeBase64(
                                    (String) (null == item.getData().get(THUMBNAIL) ? "" :
                                            item.getData().get(THUMBNAIL))));
                    if (thumbnail.getIntrinsicHeight() <= 0) {
                        // Set placeholder image if thumbnail fails to load
                        thumbnail = itemView.getContext().getDrawable(R.drawable.tap_bg_grey_e4);
                    }
                }
                Number size = (Number) item.getData().get(SIZE);
                String videoSize = null == size ? "" : TAPUtils.getInstance().getStringSizeLengthFile(size.longValue());
                activity.runOnUiThread(() -> {
                    ivThumbnail.setImageDrawable(thumbnail);
                    if (null != downloadProgressValue) {
                        // Show download progress
                        //Long downloadProgressBytes = TAPFileDownloadManager.getInstance().getDownloadProgressBytes(item.getLocalID());
                        //if (null != downloadProgressBytes) {
                        //    tvMediaInfo.setText(TAPUtils.getInstance().getFileDisplayProgress(item, downloadProgressBytes));
                        //}
                        tvMediaInfo.setText(videoSize);
                        pbProgress.setMax(100);
                        pbProgress.setProgress(downloadProgressValue);
                        ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_cancel_white));
                        clContainer.setOnClickListener(v -> mediaInterface.onCancelDownloadClicked(item));
                    } else {
                        // Show download button
                        tvMediaInfo.setText(videoSize);
                        pbProgress.setProgress(0);
                        ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_download_white));
                        clContainer.setOnClickListener(v -> mediaInterface.onMediaClicked(item, ivThumbnail, isMediaReady));
                    }
                    tvMediaInfo.setVisibility(View.VISIBLE);
                    flProgress.setVisibility(View.VISIBLE);
                    vThumbnailOverlay.setVisibility(View.VISIBLE);
                });
            }).start();
        }
    }
}
