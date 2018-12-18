package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ImagePreview.K_IMAGE_URLS;

public class TAPImagePreviewActivity extends AppCompatActivity {

    //View
    private ViewPager vpImagePreview;
    private TextView tvCancelBtn, tvMultipleImageIndicator, tvSendBtn;
    private RecyclerView rvImageThumbnail;
    private EditText etCaption;
    private ImageView ivAddMoreImage;

    //Intent
    private int requestCode;
    private ArrayList<TAPImagePreviewModel> images;

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

        TAPImagePreviewRecyclerAdapter adapter = new TAPImagePreviewRecyclerAdapter(images);

        vpImagePreview.setAdapter(new TAPImagePreviewPagerAdapter(this, images));
        vpImagePreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

                    runOnUiThread(adapter::notifyDataSetChanged);
                }).start();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (1 < images.size()) {
            tvMultipleImageIndicator.setVisibility(View.VISIBLE);
            tvMultipleImageIndicator.setText(1 + " of " + images.size());

            rvImageThumbnail.setVisibility(View.VISIBLE);
            rvImageThumbnail.setAdapter(adapter);
            rvImageThumbnail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvImageThumbnail.setHasFixedSize(false);
            rvImageThumbnail.addItemDecoration(new TAPHorizontalDecoration(TAPUtils.getInstance().dpToPx(1), 0, TAPUtils.getInstance().dpToPx(16), 0, images.size(), 0, 0));
        }
    }
}
