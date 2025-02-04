package io.taptalk.TapTalk.View.Activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TAPMediaPreviewPagerAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMediaPreviewRecyclerAdapter;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MEDIA_PREVIEWS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_PREVIEW;

public class TAPMediaPreviewActivity extends TAPBaseActivity {

    private static final String TAG = TAPMediaPreviewActivity.class.getSimpleName();

    // View
    private ConstraintLayout clMediaPreviewContainer;
    private ViewPager vpImagePreview;
    private TextView tvCancelBtn, tvMultipleImageIndicator, tvSendBtn;
    private RecyclerView rvImageThumbnail;
    private ImageView ivAddMoreImage;
    private TAPMediaPreviewRecyclerAdapter thumbnailAdapter;
    private TAPMediaPreviewPagerAdapter pagerAdapter;
    private ArrayList<TAPUserModel> roomParticipants;

    // Intent
    private ArrayList<TAPMediaPreviewModel> medias, errorMedias;

    // ImagePreview RecyclerView Data
    private int lastIndex = 0, checkCount = 0;

    public static void start(
            Activity context,
            String instanceKey,
            ArrayList<TAPMediaPreviewModel> mediaPreviews,
            ArrayList<TAPUserModel> roomParticipantsByUsername
    ) {
        Intent intent = new Intent(context, TAPMediaPreviewActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(MEDIA_PREVIEWS, mediaPreviews);
        intent.putExtra(GROUP_MEMBERS, roomParticipantsByUsername);
        context.startActivityForResult(intent, SEND_MEDIA_FROM_PREVIEW);
        context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_media_preview);
        receiveIntent();
        initView();
    }

    @Override
    public void applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapCharcoal));
    }

    private void receiveIntent() {
        medias = getIntent().getParcelableArrayListExtra(MEDIA_PREVIEWS);
        roomParticipants = getIntent().getParcelableArrayListExtra(GROUP_MEMBERS);

        if (null == medias) {
            medias = new ArrayList<>();
        }
    }

    private void initView() {
        clMediaPreviewContainer = findViewById(R.id.cl_media_preview_container);
        vpImagePreview = findViewById(R.id.vp_image_preview);
        tvCancelBtn = findViewById(R.id.tv_cancel_btn);
        tvMultipleImageIndicator = findViewById(R.id.tv_multiple_image_indicator);
        tvSendBtn = findViewById(R.id.tv_send_btn);
        rvImageThumbnail = findViewById(R.id.rv_image_thumbnail);
        ivAddMoreImage = findViewById(R.id.iv_add_more_Image);

        thumbnailAdapter = new TAPMediaPreviewRecyclerAdapter(medias, thumbInterface);
        pagerAdapter = new TAPMediaPreviewPagerAdapter(this, instanceKey, medias, roomParticipants);
        vpImagePreview.setAdapter(pagerAdapter);
        vpImagePreview.addOnPageChangeListener(vpPreviewListener);

        if (1 < medias.size()) {
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            tvMultipleImageIndicator.setText(String.format(getString(R.string.tap_format_dd_media_count), 1, medias.size()));

            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(thumbnailAdapter);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.addItemDecoration(new TAPHorizontalDecoration(TAPUtils.dpToPx(1), 0, TAPUtils.dpToPx(16), 0, medias.size(), 0, 0));
        }
        SimpleItemAnimator thumbnailItemAnimator = (SimpleItemAnimator) rvImageThumbnail.getItemAnimator();
        if (null != thumbnailItemAnimator) thumbnailItemAnimator.setSupportsChangeAnimations(false);

        checkMediasForErrors();

        clMediaPreviewContainer.setOnClickListener(v -> TAPUtils.dismissKeyboard(TAPMediaPreviewActivity.this));
        tvCancelBtn.setOnClickListener(v -> onBackPressed());
        ivAddMoreImage.setOnClickListener(v -> {
            TAPUtils.dismissKeyboard(TAPMediaPreviewActivity.this);
            TAPUtils.pickMediaFromGallery(TAPMediaPreviewActivity.this, SEND_MEDIA_FROM_GALLERY, true);
        });

        clMediaPreviewContainer.post(() -> TAPUtils.dismissKeyboard(TAPMediaPreviewActivity.this));
    }

    public interface ImageThumbnailPreviewInterface {
        void onThumbnailTapped(int position, TAPMediaPreviewModel model);
    }

    private ImageThumbnailPreviewInterface thumbInterface = new ImageThumbnailPreviewInterface() {
        @Override
        public void onThumbnailTapped(int position, TAPMediaPreviewModel model) {
            if (!model.isSelected()) {
                vpImagePreview.setCurrentItem(position, true);
            } else {
                removeMediaFromAdapter(position);
            }
        }
    };

    private ViewPager.OnPageChangeListener vpPreviewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (View.VISIBLE == tvMultipleImageIndicator.getVisibility()) {
                tvMultipleImageIndicator.setText(String.format(getString(R.string.tap_format_dd_media_count), i + 1, medias.size()));
            }
            new Thread(() -> {
                for (TAPMediaPreviewModel recyclerItem : thumbnailAdapter.getItems()) {
                    recyclerItem.setSelected(false);
                }
                thumbnailAdapter.getItemAt(i).setSelected(true);

                runOnUiThread(() -> {
                    //thumbnailAdapter.notifyDataSetChanged();
                    if (lastIndex == i) {
                        thumbnailAdapter.notifyItemRangeChanged(i, 1);
                    }
                    else {
                        thumbnailAdapter.notifyItemChanged(i);
                        thumbnailAdapter.notifyItemChanged(lastIndex);
                    }
                    lastIndex = i;
                    rvImageThumbnail.scrollToPosition(i);
                });
            }).start();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case SEND_MEDIA_FROM_GALLERY:
                        processMediaFromGallery(data);
                        break;
                }
                break;
        }
    }

    private void processMediaFromGallery(Intent data) {
        if (null == data) {
            return;
        }
        ArrayList<TAPMediaPreviewModel> galleryMediaPreviews = new ArrayList<>();

        ClipData clipData = data.getClipData();
        if (null != clipData) {
            // Multiple media selection
            galleryMediaPreviews = TAPUtils.getPreviewsFromClipData(this, clipData, false);
        } else {
            // Single media selection
            Uri uri = data.getData();
            TAPMediaPreviewModel preview = TAPUtils.getPreviewFromUri(this, uri, false);
            if (preview != null) {
                galleryMediaPreviews.add(preview);
            }
        }
        medias.addAll(galleryMediaPreviews);
        checkMediasForErrors();

        pagerAdapter.notifyDataSetChanged();
        if (1 < medias.size()) {
            tvMultipleImageIndicator.setText(String.format(getString(R.string.tap_format_dd_media_count), lastIndex + 1, medias.size()));
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(thumbnailAdapter);
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(TAPMediaPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            thumbnailAdapter.notifyDataSetChanged();
        }
    }

    private void removeMediaFromAdapter(int position) {
        int tempPosition;

        if (0 != position && position == medias.size() - 1) tempPosition = position - 1;
        else tempPosition = position;

        medias.remove(position);
        if (0 < medias.size()) {
            medias.get(tempPosition).setSelected(true);
        }
        pagerAdapter.notifyDataSetChanged();
        thumbnailAdapter.notifyDataSetChanged();
        tvMultipleImageIndicator.setText(String.format(getString(R.string.tap_format_dd_media_count), tempPosition + 1, medias.size()));

        if (1 == medias.size()) {
            rvImageThumbnail.setVisibility(View.GONE);
            tvMultipleImageIndicator.setVisibility(View.GONE);
        }
        checkMediasForErrors();
    }

    private void checkMediasForErrors() {
        tvSendBtn.setAlpha(0.5f);
        tvSendBtn.setOnClickListener(null);
        new Thread(() -> {
            errorMedias = new ArrayList<>();
            checkCount = 0;
            for (TAPMediaPreviewModel media : medias) {
                // Check if video size exceeds limit
                if (null != media.getUri() && null == media.isSizeExceedsLimit() && media.getType() == TYPE_VIDEO) {
                    Uri uri = media.getUri();
                    if (TAPFileUtils.isGoogleDriveUri(uri)) {
                        // Requires download to get file size from Google Drive
                        media.setLoading(true);
                        runOnUiThread(() -> {
                            pagerAdapter.notifyDataSetChanged();
                            thumbnailAdapter.notifyItemChanged(thumbnailAdapter.getItems().indexOf(media));
                        });
                        new Thread(() -> {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                if (null != inputStream && !TAPFileUploadManager.getInstance(instanceKey)
                                        .isSizeAllowedForUpload((long) inputStream.available())) {
                                    media.setSizeExceedsLimit(true);
                                    errorMedias.add(media);
                                } else {
                                    media.setSizeExceedsLimit(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            media.setLoading(false);
                            runOnUiThread(() -> {
                                pagerAdapter.notifyDataSetChanged();
                                thumbnailAdapter.notifyItemChanged(thumbnailAdapter.getItems().indexOf(media));
                            });
                            checkSendButtonAvailability();
                        }).start();
                    } else {
                        // Check file size
                        if (!TAPFileUploadManager.getInstance(instanceKey).isSizeAllowedForUpload(
                                new File(TAPFileUtils.getFilePath(this, uri)).length())) {
                            media.setSizeExceedsLimit(true);
                            errorMedias.add(media);
                        } else {
                            media.setSizeExceedsLimit(false);
                        }
                        checkSendButtonAvailability();
                    }
                } else if (null == media.isSizeExceedsLimit()) {
                    media.setSizeExceedsLimit(false);
                    checkSendButtonAvailability();
                } else if (media.isSizeExceedsLimit()) {
                    errorMedias.add(media);
                    checkSendButtonAvailability();
                } else {
                    checkSendButtonAvailability();
                }
            }
        }).start();
    }

    private void checkSendButtonAvailability() {
        if (++checkCount >= medias.size() && errorMedias.size() < medias.size()) {
            runOnUiThread(() -> {
                tvSendBtn.setAlpha(1f);
                tvSendBtn.setOnClickListener(v -> onSendButtonClicked());
            });
        } else {
            runOnUiThread(() -> {
                tvSendBtn.setAlpha(0.5f);
                tvSendBtn.setOnClickListener(null);
            });
        }
    }

    private void onSendButtonClicked() {
        if (errorMedias.size() > 0) {
            // Some files exceeded upload limit
            new TapTalkDialog.Builder(TAPMediaPreviewActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setTitle(getString(R.string.tap_warning_files_may_not_be_sent))
                    .setMessage(String.format(getString(R.string.tap_format_s_warning_video_size_exceeds_limit_wont_be_sent),
                            TAPUtils.getStringSizeLengthFile(TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize())))
                    .setPrimaryButtonTitle(getString(R.string.tap_continue))
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setPrimaryButtonListener(true, view -> sendMedias())
                    .show();
        } else {
            sendMedias();
        }
    }

    private void sendMedias() {
        ArrayList<TAPMediaPreviewModel> mediasCopy = new ArrayList<>(medias);
        if (errorMedias.size() > 0) {
            mediasCopy.removeAll(errorMedias);
        }
        Intent sendIntent = new Intent();
        sendIntent.putParcelableArrayListExtra(MEDIA_PREVIEWS, mediasCopy);
        setResult(RESULT_OK, sendIntent);
        finish();
    }
}
