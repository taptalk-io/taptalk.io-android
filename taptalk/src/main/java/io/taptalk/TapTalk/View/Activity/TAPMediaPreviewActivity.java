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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
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
    private ArrayList<TAPMediaPreviewModel> medias;
    //private ArrayList<String> imageCaptions;

    //ImagePreview RecyclerView Data
    private int lastIndex = 0;

    public interface ImageThumbnailPreviewInterface {
        void onThumbnailTap(int position, TAPMediaPreviewModel model);
    }

    ImageThumbnailPreviewInterface thumbInterface = new ImageThumbnailPreviewInterface() {
        @Override
        public void onThumbnailTap(int position, TAPMediaPreviewModel model) {
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

        tvCancelBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        tvSendBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.putParcelableArrayListExtra(MEDIA_PREVIEWS, medias);
            setResult(RESULT_OK, sendIntent);
            finish();
        });

        ivAddMoreImage.setOnClickListener(v -> TAPUtils.getInstance().pickImageFromGallery(TAPMediaPreviewActivity.this, SEND_MEDIA_FROM_GALLERY, true));
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

        ArrayList<TAPMediaPreviewModel> imageGalleryUris = new ArrayList<>();

        ClipData clipData = data.getClipData();
        if (null != clipData) {
            //ini buat lebih dari 1 image selection
            imageGalleryUris = TAPUtils.getInstance().getUrisFromClipData(this, clipData, false);
        } else {
            //ini buat 1 image selection
            Uri uri = data.getData();
            imageGalleryUris.add(TAPMediaPreviewModel.Builder(uri, TAPUtils.getInstance().getMessageTypeFromFileUri(this, uri), false));
        }

        medias.addAll(imageGalleryUris);
            
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

    private void deleteImageProcess(int position) {
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
        }
    }
}
