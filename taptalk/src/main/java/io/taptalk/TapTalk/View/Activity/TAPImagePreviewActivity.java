package io.taptalk.TapTalk.View.Activity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TAPImagePreviewPagerAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPImagePreviewRecyclerAdapter;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_RES_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_URLS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_GALLERY;

public class TAPImagePreviewActivity extends AppCompatActivity {

    private static final String TAG = TAPImagePreviewActivity.class.getSimpleName();
    //View
    private ViewPager vpImagePreview;
    private TextView tvCancelBtn, tvMultipleImageIndicator, tvSendBtn;
    private RecyclerView rvImageThumbnail;
    private ImageView ivAddMoreImage;
    private TAPImagePreviewRecyclerAdapter adapter;
    private TAPImagePreviewPagerAdapter pagerAdapter;

    //Intent
    private ArrayList<TAPImagePreviewModel> images;
    //private ArrayList<String> imageCaptions;

    //ImagePreview RecyclerView Data
    private int lastIndex = 0;

    public interface ImageThumbnailPreviewInterface {
        void onThumbnailTap(int position, TAPImagePreviewModel model);
    }

    ImageThumbnailPreviewInterface thumbInterface = new ImageThumbnailPreviewInterface() {
        @Override
        public void onThumbnailTap(int position, TAPImagePreviewModel model) {
            if (!model.isSelected())
                vpImagePreview.setCurrentItem(position, true);
            else deleteImageProcess(position);
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
        images = getIntent().getParcelableArrayListExtra(K_IMAGE_URLS);

        if (null == images) images = new ArrayList<>();
    }

    private void initView() {
        vpImagePreview = findViewById(R.id.vp_image_preview);
        tvCancelBtn = findViewById(R.id.tv_cancel_btn);
        tvMultipleImageIndicator = findViewById(R.id.tv_multiple_image_indicator);
        tvSendBtn = findViewById(R.id.tv_send_btn);
        rvImageThumbnail = findViewById(R.id.rv_image_thumbnail);
        ivAddMoreImage = findViewById(R.id.iv_add_more_Image);

        adapter = new TAPImagePreviewRecyclerAdapter(images, thumbInterface);
        pagerAdapter = new TAPImagePreviewPagerAdapter(this, images);
        vpImagePreview.setAdapter(pagerAdapter);
        vpImagePreview.addOnPageChangeListener(vpPreviewListener);

        if (1 < images.size()) {
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            tvMultipleImageIndicator.setText(1 + " of " + images.size());

            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(adapter);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.addItemDecoration(new TAPHorizontalDecoration(TAPUtils.getInstance().dpToPx(1), 0, TAPUtils.getInstance().dpToPx(16), 0, images.size(), 0, 0));

            SimpleItemAnimator thumbnailItemAnimator = (SimpleItemAnimator) rvImageThumbnail.getItemAnimator();
            if (null != thumbnailItemAnimator)
                thumbnailItemAnimator.setSupportsChangeAnimations(false);
        }

        tvCancelBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        tvSendBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.putParcelableArrayListExtra(K_IMAGE_RES_CODE, images);
            setResult(RESULT_OK, sendIntent);
            finish();
        });

        ivAddMoreImage.setOnClickListener(v -> TAPUtils.getInstance().pickImageFromGallery(TAPImagePreviewActivity.this, SEND_IMAGE_FROM_GALLERY, true));
    }

    private ViewPager.OnPageChangeListener vpPreviewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (View.VISIBLE == tvMultipleImageIndicator.getVisibility())
                tvMultipleImageIndicator.setText((i + 1) + " of " + images.size());

            new Thread(() -> {
                for (TAPImagePreviewModel recyclerItem : adapter.getItems()) {
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
                    case SEND_IMAGE_FROM_GALLERY:
                        processImagesFromGallery(data);
                        break;
                }
                break;
        }
    }

    private void processImagesFromGallery(Intent data) {
        new Thread(() -> {
            if (null == data) {
                return;
            }

            ArrayList<TAPImagePreviewModel> imageGalleryUris = new ArrayList<>();

            ClipData clipData = data.getClipData();
            if (null != clipData) {
                //ini buat lebih dari 1 image selection
                TAPUtils.getInstance().getUrisFromClipData(clipData, imageGalleryUris, false);
            } else {
                //ini buat 1 image selection
                imageGalleryUris.add(TAPImagePreviewModel.Builder(data.getData(), false));
            }

            images.addAll(imageGalleryUris);
            Log.e(TAG, "processImagesFromGallery: " );

            runOnUiThread(() -> {
                pagerAdapter.notifyDataSetChanged();
                if (1 < images.size()) {
                    tvMultipleImageIndicator.setText((lastIndex + 1) + " of " + images.size());
                    tvMultipleImageIndicator.setVisibility(View.VISIBLE);
                    rvImageThumbnail.setVisibility(View.VISIBLE);
                    rvImageThumbnail.setAdapter(adapter);
                    rvImageThumbnail.setHasFixedSize(false);
                    rvImageThumbnail.setLayoutManager(new LinearLayoutManager(TAPImagePreviewActivity.this, LinearLayoutManager.HORIZONTAL, false));
                } else {
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private void deleteImageProcess(int position) {
        int tempPosition;

        if (0 != position && position == images.size() - 1) tempPosition = position - 1;
        else tempPosition = position;

        images.remove(position);
        if (0 < images.size())
            images.get(tempPosition).setSelected(true);
        pagerAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        tvMultipleImageIndicator.setText((tempPosition + 1) + " of " + images.size());

        if (1 == images.size()) {
            rvImageThumbnail.setVisibility(View.GONE);
            tvMultipleImageIndicator.setVisibility(View.GONE);
        }
    }
}