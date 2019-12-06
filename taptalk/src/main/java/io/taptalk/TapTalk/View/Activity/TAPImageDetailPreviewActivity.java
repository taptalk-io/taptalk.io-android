package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.taptalk.TapTalk.Helper.TAPTouchImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE;

public class TAPImageDetailPreviewActivity extends AppCompatActivity {

    private final String TAG = TAPImageDetailPreviewActivity.class.getSimpleName();

    private ConstraintLayout clActionBar;
    private LinearLayout llCaption;
    private FrameLayout flLoading;
    private TextView tvTitle, tvMessageStatus, tvCaption, tvLoadingText;
    private ImageView ivButtonBack, ivButtonSave, ivSaving;
    private TAPTouchImageView tivImageDetail;

    private String fileUrl, fileID, title, messageStatus, caption, mimeType;

    private GestureDetector gestureDetector;

    private BitmapDrawable image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_image_detail_preview);

        getIntentFromOtherPage();
        initView();

        supportPostponeEnterTransition();
        if (null != fileUrl) {
            // Load image from URL
            tivImageDetail.post(() -> Glide.with(TAPImageDetailPreviewActivity.this)
                    .load(fileUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            finish();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            image = (BitmapDrawable) resource;
                            supportStartPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(tivImageDetail));
        } else if (null != fileID) {
            new Thread(() -> {
                // Load image from cache
                image = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable(fileID);
                if (null != image) {
                    runOnUiThread(() -> {
                        tivImageDetail.setImageDrawable(image);
                        supportStartPostponedEnterTransition();
                    });
                } else {
                    finish();
                }
            }).start();
        }
    }

    @Override
    public void onBackPressed() {
        tivImageDetail.setZoom(1f);
        supportFinishAfterTransition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE:
                    saveImage();
                    break;
            }
        }
    }

    private void getIntentFromOtherPage() {
        TAPMessageModel message = getIntent().getParcelableExtra(MESSAGE);
        if (null == message || null == message.getData()) {
            Log.e(TAG, getString(R.string.tap_error_load_image_detail));
            return;
        }
        fileUrl = (String) message.getData().get(FILE_URL);
        fileID = (String) message.getData().get(FILE_ID);
        title = message.getUser().getName();
        messageStatus = message.getMessageStatusText();
        caption = (String) message.getData().get(CAPTION);
        mimeType = (String) message.getData().get(MEDIA_TYPE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        llCaption = findViewById(R.id.ll_caption);
        flLoading = findViewById(R.id.fl_loading);
        tvTitle = findViewById(R.id.tv_title);
        tvMessageStatus = findViewById(R.id.tv_message_status);
        tvCaption = findViewById(R.id.tv_caption);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSave = findViewById(R.id.iv_save);
        ivSaving = findViewById(R.id.iv_loading_image);
        tivImageDetail = findViewById(R.id.tiv_image_detail);

        tvTitle.setText(title);
        tvMessageStatus.setText(messageStatus);

        if (!caption.isEmpty()) {
            llCaption.setVisibility(View.VISIBLE);
            tvCaption.setText(caption);
            tvCaption.setMovementMethod(new ScrollingMovementMethod());
        }

        gestureDetector = new GestureDetector(this, new TapGestureListener());
        tivImageDetail.setOnTouchListener(touchListener);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        ivButtonSave.setOnClickListener(saveButtonListener);
        clActionBar.setOnClickListener(emptyListener);
        llCaption.setOnClickListener(emptyListener);
        flLoading.setOnClickListener(emptyListener);
    }

    private void onImageSingleTapped() {
        if (clActionBar.getVisibility() == View.VISIBLE) {
            // Hide UI
            clActionBar.setVisibility(View.GONE);
            llCaption.setVisibility(View.GONE);
        } else {
            // Show UI
            clActionBar.setVisibility(View.VISIBLE);
            llCaption.setVisibility(caption.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void saveImage() {
        if (null == image) {
            return;
        }
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE);
        } else {
            showLoading();
            TAPFileDownloadManager.getInstance().writeImageFileToDisk(this, System.currentTimeMillis(), image.getBitmap(), mimeType, saveImageListener);
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivSaving.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(this, ivSaving);
            }
            tvLoadingText.setText(getString(R.string.tap_saving));
            ivButtonSave.setOnClickListener(null);
            flLoading.setVisibility(View.VISIBLE);
        });
    }

    private void endLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin));
            ivSaving.clearAnimation();
            tvLoadingText.setText(getString(R.string.tap_image_saved));
            flLoading.setOnClickListener(v -> hideLoading());

            new Handler().postDelayed(this::hideLoading, 1000L);
        });
    }

    private void hideLoading() {
        flLoading.setVisibility(View.GONE);
        ivButtonSave.setOnClickListener(saveButtonListener);
        flLoading.setOnClickListener(emptyListener);
    }

    private View.OnClickListener saveButtonListener = v -> saveImage();
    private View.OnClickListener emptyListener = v -> {
    };

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    };

    private TapTalkActionInterface saveImageListener = new TapTalkActionInterface() {
        @Override
        public void onSuccess(String message) {
            endLoading();
        }

        @Override
        public void onError(String errorMessage) {
            runOnUiThread(() -> {
                hideLoading();
                Toast.makeText(TAPImageDetailPreviewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onImageSingleTapped();
            return true;
        }
    }
}
