package io.taptalk.TapTalk.View.Adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_NOTIFICATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_VIEW_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_LOADING_LAYOUT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_MEDIA_THUMBNAIL;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_MENU_BUTTON;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_SECTION_TITLE;

public class TapChatProfileAdapter extends TAPBaseAdapter<TapChatProfileItemModel, TAPBaseViewHolder<TapChatProfileItemModel>> {

    private TAPChatProfileActivity.ChatProfileInterface chatProfileInterface;
    private RequestManager glide;
    private int gridWidth;

    public TapChatProfileAdapter(List<TapChatProfileItemModel> items, TAPChatProfileActivity.ChatProfileInterface chatProfileInterface, RequestManager glide) {
        setItems(items, true);
        gridWidth = TAPUtils.getInstance().getScreenWidth() / 3;
        this.chatProfileInterface = chatProfileInterface;
        this.glide = glide;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TapChatProfileItemModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SECTION_TITLE:
                return new SectionTitleViewHolder(parent, R.layout.tap_cell_section_title);
            case TYPE_MENU_BUTTON:
                return new MenuButtonViewHolder(parent, R.layout.tap_cell_profile_menu_button);
            case TYPE_MEDIA_THUMBNAIL:
                return new MediaThumbnailViewHolder(parent, R.layout.tap_cell_media_thumbnail_grid);
            case TYPE_LOADING_LAYOUT:
                return new LoadingLayoutViewHolder(parent, R.layout.tap_cell_shared_media_loading);
            default:
                return new EmptyViewHolder(parent, R.layout.tap_cell_empty);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getType();
    }

    public class SectionTitleViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private TextView tvRecentTitle;

        SectionTitleViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvRecentTitle = itemView.findViewById(R.id.tv_section_title);
        }

        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {
            tvRecentTitle.setText(item.getItemLabel());
        }
    }

    private class MenuButtonViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private ConstraintLayout clContainer;
        private ImageView ivMenuIcon, ivRightArrow;
        private TextView tvMenuLabel;
        private SwitchCompat swMenuSwitch;

        MenuButtonViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
            swMenuSwitch = itemView.findViewById(R.id.sw_menu_switch);
        }

        @SuppressLint("PrivateResource")
        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {
            // Set menu icon
            ivMenuIcon.setImageResource(item.getIconResource());
            ivMenuIcon.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(item.getIconColorResource())));

            // Set menu label text
            tvMenuLabel.setText(item.getItemLabel());
            TypedArray typedArray = itemView.getContext().obtainStyledAttributes(item.getTextStyleResource(), R.styleable.TextAppearance);
            tvMenuLabel.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
            typedArray.recycle();

            // Show menu chevron
            if (item.getMenuId() == MENU_VIEW_MEMBERS) {
                ivRightArrow.setVisibility(View.VISIBLE);
            } else {
                ivRightArrow.setVisibility(View.GONE);
            }

            if (item.getMenuId() == MENU_NOTIFICATION) {
                // Setup menu switch and listener
                swMenuSwitch.setVisibility(View.VISIBLE);
                swMenuSwitch.setChecked(item.isChecked());
                int switchColorRes = item.isChecked() ? R.color.tapSwitchActiveBackgroundColor : R.color.tapSwitchInactiveBackgroundColor;
                changeSwitchColor(itemView.getResources().getColor(switchColorRes));
                swMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    private ValueAnimator transitionToActive, transitionToInactive;

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        item.setChecked(isChecked);
                        chatProfileInterface.onMenuClicked(item);
                        if (isChecked) {
                            // Turn switch ON
                            getTransitionInactive().cancel();
                            getTransitionActive().start();
                            changeSwitchColor(itemView.getResources().getColor(R.color.tapSwitchActiveBackgroundColor));

                        } else {
                            // Turn switch OFF
                            getTransitionActive().cancel();
                            getTransitionInactive().start();
                            changeSwitchColor(itemView.getResources().getColor(R.color.tapSwitchInactiveBackgroundColor));
                        }
                    }

                    private ValueAnimator getTransitionActive() {
                        if (null == transitionToActive) {
                            transitionToActive = ValueAnimator.ofArgb(
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationInactive),
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationActive));
                            transitionToActive.setDuration(DEFAULT_ANIMATION_TIME);
                            transitionToActive.addUpdateListener(valueAnimator -> ivMenuIcon.setColorFilter(
                                    (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                        }
                        return transitionToActive;
                    }

                    private ValueAnimator getTransitionInactive() {
                        if (null == transitionToInactive) {
                            transitionToInactive = ValueAnimator.ofArgb(
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationActive),
                                    itemView.getContext().getResources().getColor(R.color.tapIconChatProfileMenuNotificationInactive));
                            transitionToInactive.setDuration(DEFAULT_ANIMATION_TIME);
                            transitionToInactive.addUpdateListener(valueAnimator -> ivMenuIcon.setColorFilter(
                                    (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                        }
                        return transitionToInactive;
                    }
                });
                clContainer.setOnClickListener(v -> swMenuSwitch.setChecked(!swMenuSwitch.isChecked()));
            } else {
                // Setup default listener
                swMenuSwitch.setVisibility(View.GONE);
                clContainer.setOnClickListener(v -> chatProfileInterface.onMenuClicked(item));
            }
        }

        private void changeSwitchColor(int color) {
            DrawableCompat.setTintList(DrawableCompat.wrap(swMenuSwitch.getThumbDrawable()),
                    ColorStateList.valueOf(color));
            DrawableCompat.setTintList(DrawableCompat.wrap(swMenuSwitch.getTrackDrawable()),
                    ColorStateList.valueOf(TAPUtils.getInstance().adjustAlpha(color, 0.3f)));
        }
    }

    class MediaThumbnailViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private Activity activity;
        private ConstraintLayout clContainer;
        private FrameLayout flProgress;
        private ImageView ivThumbnail, ivButtonProgress, ivVideoIcon;
        private TextView tvMediaInfo;
        private ProgressBar pbProgress;
        private View vThumbnailOverlay;

        private Drawable thumbnail;
        private boolean isMediaReady;

        MediaThumbnailViewHolder(ViewGroup parent, int itemLayoutId) {
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
        protected void onBind(TapChatProfileItemModel item, int position) {
            TAPMessageModel message = item.getMediaMessage();
            if (null == message) {
                return;
            }

            clContainer.getLayoutParams().width = gridWidth;

            Integer downloadProgressValue = TAPFileDownloadManager.getInstance().getDownloadProgressPercent(message.getLocalID());

            if (message.getType() == TYPE_VIDEO) {
                ivVideoIcon.setVisibility(View.VISIBLE);
            } else {
                ivVideoIcon.setVisibility(View.GONE);
            }

            if (null == message.getData()) {
                return;
            }

            // Load thumbnail when download is not in progress
            if (null == TAPFileDownloadManager.getInstance().getDownloadProgressPercent(message.getLocalID())) {
                loadSmallThumbnail(message);
            }

            new Thread(() -> {
                BitmapDrawable mediaImage = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable((String) message.getData().get(FILE_ID));
                if (null != mediaImage) {
                    // Load image from cache
                    activity.runOnUiThread(() -> glide.load(mediaImage)
                            .apply(new RequestOptions().placeholder(thumbnail))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    showMediaRequiresDownload(message, downloadProgressValue);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    showMediaReady(message);
                                    return false;
                                }
                            })
                            .into(ivThumbnail));
                } else {
                    // Load video thumbnail
                    Uri videoUri = TAPFileDownloadManager.getInstance().getFileMessageUri(message.getRoom().getRoomID(), (String) message.getData().get(FILE_ID));
                    if (null != videoUri) {
                        activity.runOnUiThread(() -> glide.load(videoUri)
                                .apply(new RequestOptions().placeholder(thumbnail))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        showMediaRequiresDownload(message, downloadProgressValue);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        showMediaReady(message);
                                        return false;
                                    }
                                })
                                .into(ivThumbnail));
                    } else {
                        showMediaRequiresDownload(message, downloadProgressValue);
                    }
                }
            }).start();
        }

        private void loadSmallThumbnail(TAPMessageModel message) {
            if (null == message.getData()) {
                return;
            }
            thumbnail = null;
//            if (null == thumbnail) {
                new Thread(() -> {
                    thumbnail = new BitmapDrawable(
                            itemView.getContext().getResources(),
                            TAPFileUtils.getInstance().decodeBase64(
                                    (String) (null == message.getData().get(THUMBNAIL) ? "" :
                                            message.getData().get(THUMBNAIL))));
                    if (null == thumbnail || thumbnail.getIntrinsicHeight() <= 0) {
                        // Set placeholder image if thumbnail fails to load
                        thumbnail = itemView.getContext().getDrawable(R.drawable.tap_bg_grey_e4);
                    }
                    activity.runOnUiThread(() -> ivThumbnail.setImageDrawable(thumbnail));
                }).start();
//            } else {
//                ivThumbnail.setImageDrawable(thumbnail);
//            }
        }

        private void showMediaReady(TAPMessageModel message) {
            isMediaReady = true;
            clContainer.setOnClickListener(v -> chatProfileInterface.onMediaClicked(message, ivThumbnail, isMediaReady));
            if (message.getType() == TYPE_VIDEO && null != message.getData()) {
                Number duration = (Number) message.getData().get(DURATION);
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

        private void showMediaRequiresDownload(TAPMessageModel message, Integer downloadProgressValue) {
            isMediaReady = false;
            if (null == message.getData()) {
                return;
            }
            loadSmallThumbnail(message);
            new Thread(() -> {
                Number size = (Number) message.getData().get(SIZE);
                String videoSize = null == size ? "" : TAPUtils.getInstance().getStringSizeLengthFile(size.longValue());
                activity.runOnUiThread(() -> {
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
                        ivButtonProgress.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconFileCancelUploadDownload)));
                        clContainer.setOnClickListener(v -> chatProfileInterface.onCancelDownloadClicked(message));
                    } else {
                        // Show download button
                        tvMediaInfo.setText(videoSize);
                        pbProgress.setProgress(0);
                        ivButtonProgress.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_ic_download_white));
                        ivButtonProgress.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.tapIconFileUploadDownload)));
                        clContainer.setOnClickListener(v -> chatProfileInterface.onMediaClicked(message, ivThumbnail, isMediaReady));
                    }
                    tvMediaInfo.setVisibility(View.VISIBLE);
                    flProgress.setVisibility(View.VISIBLE);
                    vThumbnailOverlay.setVisibility(View.VISIBLE);
                });
            }).start();
        }
    }

    class LoadingLayoutViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private LinearLayout llReloadSharedMedia;
        private ImageView ivLoading;

        LoadingLayoutViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            llReloadSharedMedia = itemView.findViewById(R.id.ll_reload_shared_media);
            ivLoading = itemView.findViewById(R.id.iv_shared_media_loading);
        }

        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {
            if (null == ivLoading.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(itemView.getContext(), ivLoading);
            }

            // TODO: 15 October 2019 SHOW RELOAD SHARED MEDIA ON API FAILURE
            llReloadSharedMedia.setVisibility(View.GONE);
            llReloadSharedMedia.setOnClickListener(v -> chatProfileInterface.onReloadSharedMedia());
        }
    }

    class EmptyViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        EmptyViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {

        }
    }
}
