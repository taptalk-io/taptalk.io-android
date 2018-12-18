package io.taptalk.TapTalk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TAPImagePreviewPagerAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPImagePreviewRecyclerAdapter;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_REQ_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_RES_CODE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_URLS;

public class TAPImagePreviewActivity extends AppCompatActivity {

    //View
    private ViewPager vpImagePreview;
    private TextView tvCancelBtn, tvMultipleImageIndicator, tvSendBtn;
    private RecyclerView rvImageThumbnail;
    private EditText etCaption;
    private ImageView ivAddMoreImage;
    TAPImagePreviewRecyclerAdapter adapter;

    //Intent
    private int requestCode;
    private ArrayList<TAPImagePreviewModel> images;

    public interface ImageThumbnailPreviewInterface {
        void onThumbnailTap(int position);
    }

    ImageThumbnailPreviewInterface thumbInterface = new ImageThumbnailPreviewInterface() {
        @Override
        public void onThumbnailTap(int position) {
            vpImagePreview.setCurrentItem(position, true);
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
        requestCode = getIntent().getIntExtra(K_IMAGE_REQ_CODE, 0);
        images = getIntent().getParcelableArrayListExtra(K_IMAGE_URLS);

        if (null == images) images = new ArrayList<>();
    }

    private void initView() {
        vpImagePreview = findViewById(R.id.vp_image_preview);
        tvCancelBtn = findViewById(R.id.tv_cancel_btn);
        tvMultipleImageIndicator = findViewById(R.id.tv_multiple_image_indicator);
        tvSendBtn = findViewById(R.id.tv_send_btn);
        rvImageThumbnail = findViewById(R.id.rv_image_thumbnail);
        etCaption = findViewById(R.id.et_caption);
        ivAddMoreImage = findViewById(R.id.iv_add_more_Image);

        adapter = new TAPImagePreviewRecyclerAdapter(images, thumbInterface);
        vpImagePreview.setAdapter(new TAPImagePreviewPagerAdapter(this, images));
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
    }

    private ViewPager.OnPageChangeListener vpPreviewListener = new ViewPager.OnPageChangeListener() {
        private int lastIndex = 0;

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
}
