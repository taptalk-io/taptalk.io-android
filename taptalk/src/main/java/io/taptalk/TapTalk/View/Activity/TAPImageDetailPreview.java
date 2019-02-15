package io.taptalk.TapTalk.View.Activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Helper.TAPTouchImageView;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.Taptalk.R;

public class TAPImageDetailPreview extends AppCompatActivity {

    private TAPTouchImageView tivImageDetail;
    private String localID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_image_detail_preview);
        initView();
        getIntentFromOtherPage();

        supportPostponeEnterTransition();
        new Thread(() -> {
            BitmapDrawable image = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable(localID);
            if (null != image) {
                runOnUiThread(() -> {
                    tivImageDetail.setImageDrawable(image);
                    supportStartPostponedEnterTransition();
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        tivImageDetail.setZoom(1f);
        supportFinishAfterTransition();
    }

    private void initView() {
        tivImageDetail = findViewById(R.id.tiv_image_detail);
    }

    private void getIntentFromOtherPage() {
        localID = getIntent().getStringExtra(TAPDefaultConstant.ImageDetail.IMAGE_FILE_ID);
        if (null == localID) localID = "";
    }
}
