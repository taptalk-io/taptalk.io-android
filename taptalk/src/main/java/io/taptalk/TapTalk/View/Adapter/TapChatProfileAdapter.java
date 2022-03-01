package io.taptalk.TapTalk.View.Adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_NOTIFICATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ChatProfileMenuType.MENU_VIEW_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DEFAULT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.DURATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_LOADING_LAYOUT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_MEDIA_THUMBNAIL;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_MENU_BUTTON;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_SECTION_TITLE;
import static io.taptalk.TapTalk.Model.ResponseModel.TapChatProfileItemModel.TYPE_USER_GROUP_DETAIL;

public class TapChatProfileAdapter extends TAPBaseAdapter<TapChatProfileItemModel, TAPBaseViewHolder<TapChatProfileItemModel>> {

    private String instanceKey = "";
    private TAPChatProfileActivity.ChatProfileInterface chatProfileInterface;
    private RequestManager glide;
    private int gridWidth;
    private int mediaThumbnailStartIndex;

    public TapChatProfileAdapter(String instanceKey, List<TapChatProfileItemModel> items, TAPChatProfileActivity.ChatProfileInterface chatProfileInterface, RequestManager glide) {
        this.instanceKey = instanceKey;
        setItems(items, true);
        gridWidth = (TAPUtils.getScreenWidth() - TAPUtils.dpToPx(34)) / 3;
        this.chatProfileInterface = chatProfileInterface;
        this.glide = glide;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TapChatProfileItemModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SECTION_TITLE:
                return new SectionTitleViewHolder(parent, R.layout.tap_cell_shared_media_title);
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getType();
    }

    public class UserGroupDetailViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private CircleImageView civProfilePicture;
        private ImageView ivProfileBackground;
        private TextView tvProfilePictureLabel;
        private TextView tvLabelRoomName;
        private TextView tvLabelUsernameMemberCount;

        UserGroupDetailViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            civProfilePicture = itemView.findViewById(R.id.civ_profile_picture);
            ivProfileBackground = itemView.findViewById(R.id.iv_profile_background);
            tvProfilePictureLabel = itemView.findViewById(R.id.tv_profile_picture_label);
            tvLabelRoomName = itemView.findViewById(R.id.tv_label_room_name);
            tvLabelUsernameMemberCount = itemView.findViewById(R.id.tv_label_username_member_count);
        }

        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {
            if (null != item.getImageURL() &&
                    null != item.getImageURL().getThumbnail() &&
                    !item.getImageURL().getThumbnail().isEmpty()) {
                // Load image
                glide.load(item.getImageURL().getThumbnail())
                        .apply(new RequestOptions().placeholder(R.drawable.tap_bg_circle_9b9b9b))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                setInitialToProfilePicture(item);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                    .into(civProfilePicture);
                ImageViewCompat.setImageTintList(civProfilePicture, null);
                tvProfilePictureLabel.setVisibility(View.GONE);
            } else {
                setInitialToProfilePicture(item);
            }

            tvLabelRoomName.setText(item.getItemLabel());
            tvLabelUsernameMemberCount.setText(item.getItemSubLabel());
            tvLabelUsernameMemberCount.setTextAppearance(itemView.getContext(), item.getTextStyleResource());
        }

        private void setInitialToProfilePicture(TapChatProfileItemModel item) {
            ImageViewCompat.setImageTintList(civProfilePicture, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.getContext(), item.getItemLabel())));
            civProfilePicture.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
            tvProfilePictureLabel.setText(TAPUtils.getInitials(item.getItemLabel(), item.getTextStyleResource() == R.style.tapChatProfileMemberCountStyle ? 1 : 2));
            tvProfilePictureLabel.setVisibility(View.VISIBLE);
        }
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
        private View vSeparator;
        private SwitchCompat swMenuSwitch;

        MenuButtonViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
            vSeparator = itemView.findViewById(R.id.v_separator);
            swMenuSwitch = itemView.findViewById(R.id.sw_menu_switch);
        }

        @SuppressLint("PrivateResource")
        @Override
        protected void onBind(TapChatProfileItemModel item, int position) {
            // Set menu icon
            ivMenuIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), item.getIconResource()));
            ImageViewCompat.setImageTintList(ivMenuIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), item.getIconColorResource())));

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

            // Set background and margin
            TapChatProfileItemModel previousItem = getItemAt(position - 1);
            TapChatProfileItemModel nextItem = null;
            if (getItemCount() > (position + 1)) {
                nextItem = getItemAt(position + 1);
            }
            if (null == nextItem ||
                    nextItem.getType() != item.getType() ||
                    nextItem.getTextStyleResource() != item.getTextStyleResource()) {
                // Set single background
                vSeparator.setBackground(null);
                if (clContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) clContainer.getLayoutParams();
                    params.bottomMargin = TAPUtils.dpToPx(24);
                    clContainer.requestLayout();
                }
            } else {
                // Set top background
                vSeparator.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.color.tapGreyDc));
                if (clContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) clContainer.getLayoutParams();
                    params.bottomMargin = 0;
                    clContainer.requestLayout();
                }
            }

            if (item.getMenuId() == MENU_NOTIFICATION) {
                // Setup menu switch and listener
                swMenuSwitch.setVisibility(View.VISIBLE);
                swMenuSwitch.setChecked(item.isChecked());
                int switchColorRes = item.isChecked() ? R.color.tapSwitchActiveBackgroundColor : R.color.tapSwitchInactiveBackgroundColor;
                changeSwitchColor(ContextCompat.getColor(itemView.getContext(), switchColorRes));
                swMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    private ValueAnimator transitionToActive, transitionToInactive;

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        item.setChecked(isChecked);
                        chatProfileInterface.onMenuClicked(item);
                        if (isChecked) {
                            // Turn switch ON
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getTransitionInactive().cancel();
                                getTransitionActive().start();
                            } else {
                                ImageViewCompat.setImageTintList(ivMenuIcon, ContextCompat.getColorStateList(
                                        itemView.getContext(), R.color.tapIconChatProfileMenuNotificationActive));
                            }
                            changeSwitchColor(ContextCompat.getColor(itemView.getContext(), R.color.tapSwitchActiveBackgroundColor));

                        } else {
                            // Turn switch OFF
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getTransitionActive().cancel();
                                getTransitionInactive().start();
                            } else {
                                ImageViewCompat.setImageTintList(ivMenuIcon, ContextCompat.getColorStateList(
                                        itemView.getContext(), R.color.tapIconChatProfileMenuNotificationInactive));
                            }
                            changeSwitchColor(ContextCompat.getColor(itemView.getContext(), R.color.tapSwitchInactiveBackgroundColor));
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    private ValueAnimator getTransitionActive() {
                        if (null == transitionToActive) {
                            transitionToActive = ValueAnimator.ofArgb(
                                    ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatProfileMenuNotificationInactive),
                                    ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatProfileMenuNotificationActive));
                            transitionToActive.setDuration(DEFAULT_ANIMATION_TIME);
                            transitionToActive.addUpdateListener(valueAnimator -> ivMenuIcon.setColorFilter(
                                    (Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                        }
                        return transitionToActive;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    private ValueAnimator getTransitionInactive() {
                        if (null == transitionToInactive) {
                            transitionToInactive = ValueAnimator.ofArgb(
                                    ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatProfileMenuNotificationActive),
                                    ContextCompat.getColor(itemView.getContext(), R.color.tapIconChatProfileMenuNotificationInactive));
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
                    ColorStateList.valueOf(TAPUtils.adjustAlpha(color, 0.3f)));
        }
    }

    class MediaThumbnailViewHolder extends TAPBaseViewHolder<TapChatProfileItemModel> {

        private Activity activity;
        private ConstraintLayout clContainer;
        private FrameLayout flProgress;
        private TAPRoundedCornerImageView rcivThumbnail;
        private ImageView ivButtonProgress, ivVideoIcon;
        private TextView tvMediaInfo;
        private ProgressBar pbProgress;
        private View vThumbnailOverlay;

        private Drawable thumbnail;
        private boolean isMediaReady;

        MediaThumbnailViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivThumbnail = itemView.findViewById(R.id.rciv_thumbnail);
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
            if (null == message || null == message.getData()) {
                return;
            }

            Integer downloadProgressValue = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressPercent(message.getLocalID());
            clContainer.getLayoutParams().width = gridWidth;
            if (clContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                // Update left/right margin per item
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) clContainer.getLayoutParams();
                int indexMod = (getAdapterPosition() - mediaThumbnailStartIndex) % 3;
                switch (indexMod) {
                    case 0:
                        params.leftMargin = TAPUtils.dpToPx(14);
//                        params.rightMargin = 0;
                        break;
                    case 1:
                        params.leftMargin = TAPUtils.dpToPx(5);
//                        params.rightMargin = 0;
                        break;
                    case 2:
                        params.leftMargin = TAPUtils.dpToPx(-4);
//                        params.leftMargin = 0;
//                        params.rightMargin = 0;
//                        params.rightMargin = TAPUtils.dpToPx(18);
                        break;
                }
                clContainer.requestLayout();
            }
            rcivThumbnail.setImageDrawable(null);

//            if (null == thumbnail) {
            thumbnail = new BitmapDrawable(
                    itemView.getContext().getResources(),
                    TAPFileUtils.decodeBase64(
                            (String) (null == message.getData().get(THUMBNAIL) ? "" :
                                    message.getData().get(THUMBNAIL))));
            if (thumbnail.getIntrinsicHeight() <= 0) {
                // Set placeholder image if thumbnail fails to load
                thumbnail = ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_grey_e4);
            }
//            }

            // Load thumbnail when download is not in progress
            if (null == downloadProgressValue) {
                rcivThumbnail.setImageDrawable(thumbnail);
                tvMediaInfo.setVisibility(View.GONE);
                flProgress.setVisibility(View.GONE);
                vThumbnailOverlay.setVisibility(View.GONE);
            } else {
                tvMediaInfo.setVisibility(View.VISIBLE);
                flProgress.setVisibility(View.VISIBLE);
                vThumbnailOverlay.setVisibility(View.VISIBLE);
            }

            // Show/hide video icon
            if (message.getType() == TYPE_VIDEO) {
                ivVideoIcon.setVisibility(View.VISIBLE);
            } else {
                ivVideoIcon.setVisibility(View.GONE);
            }

            String fileID = (String) message.getData().get(FILE_ID);

            if (TAPCacheManager.getInstance(itemView.getContext()).containsCache(fileID) || TAPFileDownloadManager.getInstance(instanceKey).checkPhysicalFileExists(message)) {
                // Image exists in cache / file exists in storage
                if (message.getType() == TYPE_VIDEO && null != message.getData()) {
                    Number duration = (Number) message.getData().get(DURATION);
                    if (null != duration) {
                        tvMediaInfo.setText(TAPUtils.getMediaDurationString(duration.intValue(), duration.intValue()));
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
                pbProgress.setProgress(0);
                flProgress.setVisibility(View.GONE);
                new Thread(() -> {
                    BitmapDrawable mediaThumbnail = TAPCacheManager.getInstance(itemView.getContext()).getBitmapDrawable(fileID);
                    if (position == getAdapterPosition()) {
                        // Load image only if view has not been recycled
                        if (message.getType() == TYPE_VIDEO && null == mediaThumbnail) {
                            // Get full-size thumbnail from Uri
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            String dataUri = (String) message.getData().get(FILE_URI);
                            Uri videoUri = null != dataUri ? Uri.parse(dataUri) : TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(message);
                            try {
//                                Uri parsedUri = TAPFileUtils.parseFileUri(videoUri);
//                                retriever.setDataSource(itemView.getContext(), parsedUri);
                                retriever.setDataSource(itemView.getContext(), videoUri);
                                mediaThumbnail = new BitmapDrawable(itemView.getContext().getResources(), retriever.getFrameAtTime());
                                TAPCacheManager.getInstance(itemView.getContext()).addBitmapDrawableToCache(fileID, mediaThumbnail);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mediaThumbnail = (BitmapDrawable) thumbnail;
                            }
                        }
                        if (null != mediaThumbnail) {
                            BitmapDrawable finalMediaThumbnail = mediaThumbnail;
                            activity.runOnUiThread(() -> {
                                // Load media thumbnail
                                glide.load(finalMediaThumbnail)
                                        .apply(new RequestOptions().placeholder(thumbnail))
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                if (position == getAdapterPosition()) {
                                                    // Load image only if view has not been recycled
                                                    isMediaReady = true;
                                                    clContainer.setOnClickListener(v -> chatProfileInterface.onMediaClicked(message, rcivThumbnail, isMediaReady));
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            }
                                        }).into(rcivThumbnail);
                            });
                        }
                    }
                }).start();
            } else {
                // Show small thumbnail
                Number size = (Number) message.getData().get(SIZE);
                String videoSize = null == size ? "" : TAPUtils.getStringSizeLengthFile(size.longValue());
                rcivThumbnail.setImageDrawable(thumbnail);
                if (null == downloadProgressValue) {
                    // Show media requires download
                    isMediaReady = false;
                    pbProgress.setProgress(0);
                    clContainer.setOnClickListener(v -> chatProfileInterface.onMediaClicked(message, rcivThumbnail, isMediaReady));
                    tvMediaInfo.setText(videoSize);
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_download_orange));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileUploadDownloadWhite)));
                } else {
                    // Media is downloading
                    isMediaReady = false;
                    pbProgress.setMax(100);
                    pbProgress.setProgress(downloadProgressValue);
                    clContainer.setOnClickListener(v -> chatProfileInterface.onCancelDownloadClicked(message));
                    //Long downloadProgressBytes = TAPFileDownloadManager.getInstance(instanceKey).getDownloadProgressBytes(item.getLocalID());
                    //if (null != downloadProgressBytes) {
                    //    tvMediaInfo.setText(TAPUtils.getFileDisplayProgress(item, downloadProgressBytes));
                    //}
                    tvMediaInfo.setText(videoSize);
                    ivButtonProgress.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_ic_cancel_white));
                    ImageViewCompat.setImageTintList(ivButtonProgress, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tapIconFileCancelUploadDownloadWhite)));
                    clContainer.setOnClickListener(v -> chatProfileInterface.onCancelDownloadClicked(message));
                }
                tvMediaInfo.setVisibility(View.VISIBLE);
                flProgress.setVisibility(View.VISIBLE);
                vThumbnailOverlay.setVisibility(View.VISIBLE);
            }
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
                TAPUtils.rotateAnimateInfinitely(itemView.getContext(), ivLoading);
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

    public void setMediaThumbnailStartIndex(int mediaThumbnailStartIndex) {
        this.mediaThumbnailStartIndex = mediaThumbnailStartIndex;
    }
}
