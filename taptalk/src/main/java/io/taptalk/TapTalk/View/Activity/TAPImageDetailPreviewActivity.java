package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPTouchImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE;

public class TAPImageDetailPreviewActivity extends TAPBaseActivity {

    private final String TAG = TAPImageDetailPreviewActivity.class.getSimpleName();

    private ConstraintLayout clActionBar;
    private LinearLayout llCaption;
    private FrameLayout flLoading;
    private TextView tvTitle, tvMessageStatus, tvCaption, tvLoadingText;
    private ImageView ivButtonBack, ivButtonSave, ivSaving;
    private TAPTouchImageView tivImageDetail;

    private String fileUrl, fileID, title, messageStatus, caption, mimeType;
    private GestureDetector gestureDetector;
//    private BitmapDrawable image;
    private Drawable image;

    public static void start(
            Context context,
            String instanceKey,
            TAPMessageModel message,
            ImageView imageView
    ) {
        Intent intent = new Intent(context, TAPImageDetailPreviewActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(MESSAGE, message);
        if (context instanceof Activity) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context,
                    imageView,
                    context.getString(R.string.tap_transition_view_image));
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_image_detail_preview);

        getIntentFromOtherPage();
        initView();

        supportPostponeEnterTransition();
        if ((null != fileUrl && !fileUrl.isEmpty()) || (null != fileID && !fileID.isEmpty())) {
            // Load image from cache
            image = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable(fileUrl, fileID);
            if (null != image) {
                runOnUiThread(() -> {
                    tivImageDetail.setImageDrawable(image);
                    tivImageDetail.setZoom(1f);
                    supportStartPostponedEnterTransition();
                });
            } else {
                // Load image from URL
                runOnUiThread(() -> {
                    tivImageDetail.post(() -> {
                        RequestManager glide = Glide.with(TAPImageDetailPreviewActivity.this);
                        glide.clear(tivImageDetail);
                        glide.load(fileUrl)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    finish();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    image = resource;
                                    tivImageDetail.setZoom(1f);
                                    supportStartPostponedEnterTransition();
                                    return false;
                                }
                            })
                            .into(tivImageDetail);
                    });
                });
            }
        }
//        if (null != fileUrl && !fileUrl.isEmpty()) {
//            // Load image from URL
//            tivImageDetail.post(() -> Glide.with(TAPImageDetailPreviewActivity.this)
//                    .load(fileUrl)
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            finish();
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            image = (BitmapDrawable) resource;
//                            supportStartPostponedEnterTransition();
//                            return false;
//                        }
//                    })
//                    .into(tivImageDetail));
//        } else if (null != fileID && !fileID.isEmpty()) {
//            new Thread(() -> {
//                // Load image from cache
//                image = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable(fileID);
//                if (null != image) {
//                    runOnUiThread(() -> {
//                        tivImageDetail.setImageDrawable(image);
//                        supportStartPostponedEnterTransition();
//                    });
//                } else {
//                    finish();
//                }
//            }).start();
//        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            tivImageDetail.setZoom(1f);
            supportFinishAfterTransition();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (TAPUtils.allPermissionsGranted(grantResults)) {
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
        fileUrl = (String) message.getData().get(URL);
        fileID = (String) message.getData().get(FILE_ID);
        caption = (String) message.getData().get(CAPTION);
        mimeType = TAPUtils.getMimeTypeFromMessage(message);
        title = message.getUser().getFullname();
        messageStatus = TAPTimeFormatter.durationChatString(TapTalk.appContext, message.getCreated());
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

        if (caption != null && !caption.isEmpty()) {
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
            llCaption.setVisibility((caption == null || caption.isEmpty()) ? View.GONE : View.VISIBLE);
        }
    }

    private void saveImage() {
        if (null == image) {
            return;
        }
        Bitmap bitmap = TAPUtils.drawableToBitmap(image);
        if (null == bitmap) {
            return;
        }
        if (!TAPUtils.hasPermissions(this, TAPUtils.getStoragePermissions(false))) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, TAPUtils.getStoragePermissions(false), PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE);
        } else {
            showLoading();
            TAPFileDownloadManager.getInstance(instanceKey).writeImageFileToDisk(this, System.currentTimeMillis(), bitmap, mimeType, saveImageListener);
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivSaving.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivSaving);
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
