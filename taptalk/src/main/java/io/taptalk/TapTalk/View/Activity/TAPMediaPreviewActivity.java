package io.taptalk.TapTalk.View.Activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TAPMediaPreviewPagerAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMediaPreviewRecyclerAdapter;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MEDIA_PREVIEWS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_GALLERY;

public class TAPMediaPreviewActivity extends TAPBaseActivity {

    private static final String TAG = TAPMediaPreviewActivity.class.getSimpleName();
    //View
    private ViewPager vpImagePreview;
    private TextView tvCancelBtn, tvMultipleImageIndicator, tvSendBtn;
    private RecyclerView rvImageThumbnail;
    private ImageView ivAddMoreImage;
    private TAPMediaPreviewRecyclerAdapter adapter;
    private TAPMediaPreviewPagerAdapter pagerAdapter;

    //Intent
    private ArrayList<TAPMediaPreviewModel> medias, errorMedias;

    //ImagePreview RecyclerView Data
    private int lastIndex = 0;

    public interface ImageThumbnailPreviewInterface {
        void onThumbnailTapped(int position, TAPMediaPreviewModel model);
    }

    ImageThumbnailPreviewInterface thumbInterface = new ImageThumbnailPreviewInterface() {
        @Override
        public void onThumbnailTapped(int position, TAPMediaPreviewModel model) {
            if (!model.isSelected()) {
                vpImagePreview.setCurrentItem(position, true);
            } else {
                removeMediaFromAdapter(position);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_image_preview);
        receiveIntent();
        initView();
    }

    private void receiveIntent() {
        medias = getIntent().getParcelableArrayListExtra(MEDIA_PREVIEWS);

        if (null == medias) {
            medias = new ArrayList<>();
        }
    }

    private void initView() {
        vpImagePreview = findViewById(R.id.vp_image_preview);
        tvCancelBtn = findViewById(R.id.tv_cancel_btn);
        tvMultipleImageIndicator = findViewById(R.id.tv_multiple_image_indicator);
        tvSendBtn = findViewById(R.id.tv_send_btn);
        rvImageThumbnail = findViewById(R.id.rv_image_thumbnail);
        ivAddMoreImage = findViewById(R.id.iv_add_more_Image);

        adapter = new TAPMediaPreviewRecyclerAdapter(medias, thumbInterface);
        pagerAdapter = new TAPMediaPreviewPagerAdapter(this, medias);
        vpImagePreview.setAdapter(pagerAdapter);
        vpImagePreview.addOnPageChangeListener(vpPreviewListener);

        if (1 < medias.size()) {
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            tvMultipleImageIndicator.setText(1 + " of " + medias.size());

            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(adapter);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.addItemDecoration(new TAPHorizontalDecoration(TAPUtils.getInstance().dpToPx(1), 0, TAPUtils.getInstance().dpToPx(16), 0, medias.size(), 0, 0));

            SimpleItemAnimator thumbnailItemAnimator = (SimpleItemAnimator) rvImageThumbnail.getItemAnimator();
            if (null != thumbnailItemAnimator)
                thumbnailItemAnimator.setSupportsChangeAnimations(false);
        }

        checkSendButtonAvailability();

        tvCancelBtn.setOnClickListener(v -> onBackPressed());
        ivAddMoreImage.setOnClickListener(v -> TAPUtils.getInstance().pickMediaFromGallery(TAPMediaPreviewActivity.this, SEND_MEDIA_FROM_GALLERY, true));
    }

    private ViewPager.OnPageChangeListener vpPreviewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (View.VISIBLE == tvMultipleImageIndicator.getVisibility())
                tvMultipleImageIndicator.setText((i + 1) + " of " + medias.size());

            new Thread(() -> {
                for (TAPMediaPreviewModel recyclerItem : adapter.getItems()) {
                    recyclerItem.setSelected(false);
                }
                adapter.getItemAt(i).setSelected(true);

                runOnUiThread(() -> {
                    //adapter.notifyDataSetChanged();
                    if (lastIndex == i) adapter.notifyItemRangeChanged(i, 1);
                    else {
                        adapter.notifyItemChanged(i);
                        adapter.notifyItemChanged(lastIndex);
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
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case SEND_MEDIA_FROM_GALLERY:
                        processImagesFromGallery(data);
                        break;
                }
                break;
        }
    }

    private void processImagesFromGallery(Intent data) {
        if (null == data) {
            return;
        }
        ArrayList<TAPMediaPreviewModel> galleryMediaPreviews = new ArrayList<>();

        ClipData clipData = data.getClipData();
        if (null != clipData) {
            // Multiple media selection
            galleryMediaPreviews = TAPUtils.getInstance().getUrisFromClipData(this, clipData, false);
        } else {
            // Single media selection
            Uri uri = data.getData();
            galleryMediaPreviews.add(TAPMediaPreviewModel.Builder(uri, TAPUtils.getInstance().getMessageTypeFromFileUri(this, uri), false));
        }
        medias.addAll(galleryMediaPreviews);
        checkSendButtonAvailability();
            
        pagerAdapter.notifyDataSetChanged();
        if (1 < medias.size()) {
            tvMultipleImageIndicator.setText((lastIndex + 1) + " of " + medias.size());
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(adapter);
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(TAPMediaPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void removeMediaFromAdapter(int position) {
        int tempPosition;

        if (0 != position && position == medias.size() - 1) tempPosition = position - 1;
        else tempPosition = position;

        medias.remove(position);
        if (0 < medias.size())
            medias.get(tempPosition).setSelected(true);
        pagerAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        tvMultipleImageIndicator.setText((tempPosition + 1) + " of " + medias.size());

        if (1 == medias.size()) {
            rvImageThumbnail.setVisibility(View.GONE);
            tvMultipleImageIndicator.setVisibility(View.GONE);
            checkSendButtonAvailability();
        }
    }

    private void checkSendButtonAvailability() {
        errorMedias = new ArrayList<>();
        for (TAPMediaPreviewModel media : medias) {
            if (media.isSizeExceedsLimit()) {
                errorMedias.add(media);
            }
        }
        if (errorMedias.size() >= medias.size()) {
            tvSendBtn.setAlpha(0.5f);
            tvSendBtn.setOnClickListener(null);
        } else {
            tvSendBtn.setAlpha(1f);
            tvSendBtn.setOnClickListener(v -> onSendButtonClicked());
        }
    }

    private void onSendButtonClicked() {
        checkSendButtonAvailability();
        if (errorMedias.size() > 0) {
            // Some files exceeded upload limit
            new TapTalkDialog.Builder(TAPMediaPreviewActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setTitle(getString(R.string.tap_warning_files_may_not_be_sent))
                    .setMessage(String.format(getString(R.string.tap_warning_video_size_exceeds_limit_wont_be_sent),
                            TAPUtils.getInstance().getStringSizeLengthFile(TAPFileUploadManager.getInstance().maxUploadSize)))
                    .setPrimaryButtonTitle(getString(R.string.tap_continue_s))
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
        Log.e(TAG, "sendMedias: " + mediasCopy + " / " + medias);
        Intent sendIntent = new Intent();
        sendIntent.putParcelableArrayListExtra(MEDIA_PREVIEWS, mediasCopy);
        setResult(RESULT_OK, sendIntent);
        finish();
    }
}
