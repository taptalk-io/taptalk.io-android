package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;

public class TAPVideoPlayerActivity extends TAPBaseActivity {

    private final String TAG = TAPVideoPlayerActivity.class.getSimpleName();

    private FrameLayout flLoading;
    private VideoView videoView;
    private TextView tvTitle, tvMessageStatus, tvDuration, tvLoadingText;
    private ImageView ivButtonBack, ivButtonSave, ivSaved;
    private SeekBar seekBar;
    private ProgressBar pbSaving;

    private Uri videoUri;
    private Timer durationTimer;
    private int duration, pausedPosition;
    private boolean isVideoPlaying, isSeeking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_video_player);

        receiveIntent();
        initView();
        loadVideo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseVideo();
    }

    private void initView() {
        flLoading = findViewById(R.id.fl_loading);
        videoView = findViewById(R.id.video_view);
        tvTitle = findViewById(R.id.tv_title);
        tvMessageStatus = findViewById(R.id.tv_message_status);
        tvDuration = findViewById(R.id.tv_duration);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonSave = findViewById(R.id.iv_save);
        ivSaved = findViewById(R.id.iv_saved);
        seekBar = findViewById(R.id.seek_bar);
        pbSaving = findViewById(R.id.pb_saving);

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        videoView.setOnClickListener(v -> onVideoTapped());

        // TODO: 18 March 2019 CHECK IF VIDEO IS FROM MESSAGE BUBBLE
        if (null != videoUri) {
            ivButtonSave.setVisibility(View.GONE);
            ivButtonSave.setOnClickListener(null);
        } else {
            ivButtonSave.setVisibility(View.VISIBLE);
            ivButtonSave.setOnClickListener(saveButtonListener);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvDuration.setText(TAPUtils.getInstance().getMediaDurationString(videoView.getCurrentPosition(), duration));
                if (isSeeking) {
                    videoView.seekTo(duration * seekBar.getProgress() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
                if (isVideoPlaying) {
                    videoView.pause();
                    stopProgressTimer();
                }
                videoView.seekTo(duration * seekBar.getProgress() / 100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                videoView.seekTo(duration * seekBar.getProgress() / 100);
            }
        });
    }

    private void receiveIntent() {
        String uriString = getIntent().getStringExtra(URI);
        if (null != uriString) {
            videoUri = Uri.parse(uriString);
        } else {
            finish();
        }
    }

    private void loadVideo() {
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mediaPlayer -> {
            videoView.start();
            duration = videoView.getDuration();
            isVideoPlaying = true;
            startProgressTimer();

            mediaPlayer.setOnSeekCompleteListener(mediaPlayer1 -> {
                tvDuration.setText(TAPUtils.getInstance().getMediaDurationString(videoView.getCurrentPosition(), duration));
                if (!isSeeking && isVideoPlaying) {
                    videoView.start();
                    startProgressTimer();
                    pausedPosition = 0;
                } else {
                    pausedPosition = videoView.getCurrentPosition();
                    if (pausedPosition >= duration) {
                        pausedPosition = 0;
                    }
                }
            });
        });
        videoView.setOnCompletionListener(mediaPlayer -> {
            pauseVideo();
            videoView.seekTo(duration);
        });
    }

    private void startProgressTimer() {
        if (null != durationTimer) {
            return;
        }
        durationTimer = new Timer();
        durationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> seekBar.setProgress(videoView.getCurrentPosition() * 100 / duration));
            }
        }, 0,1000L);
    }

    private void stopProgressTimer() {
        if (null == durationTimer) {
            return;
        }
        durationTimer.cancel();
        durationTimer = null;
    }

    private void onVideoTapped() {
        if (isVideoPlaying) {
            pauseVideo();
        } else {
            resumeVideo();
        }
    }

    private void pauseVideo() {
        isVideoPlaying = false;
        pausedPosition = videoView.getCurrentPosition();
        videoView.pause();
        stopProgressTimer();
    }

    private void resumeVideo() {
        isVideoPlaying = true;
        videoView.seekTo(pausedPosition);
    }

    private void saveVideo() {
        if (null == videoUri) {
            return;
        }
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
        } else {
            showLoading();
            // TODO: 18 March 2019 SAVE VIDEO HERE
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            flLoading.setVisibility(View.VISIBLE);
            pbSaving.setVisibility(View.VISIBLE);
            ivSaved.setVisibility(View.GONE);
            tvLoadingText.setText(getString(R.string.tap_saving));
            ivButtonSave.setOnClickListener(null);
        });
    }

    private void endLoading() {
        runOnUiThread(() -> {
            pbSaving.setVisibility(View.GONE);
            ivSaved.setVisibility(View.VISIBLE);
            tvLoadingText.setText(getString(R.string.tap_image_saved));
            flLoading.setOnClickListener(v -> hideLoading());

            new Handler().postDelayed(this::hideLoading, 100L);
        });
    }

    private void hideLoading() {
        flLoading.setVisibility(View.GONE);
        ivButtonSave.setOnClickListener(saveButtonListener);
        flLoading.setOnClickListener(emptyListener);
    }

    private View.OnClickListener saveButtonListener = v -> saveVideo();
    private View.OnClickListener emptyListener = v -> {};

    private TapTalkActionInterface saveVideoListener = new TapTalkActionInterface() {
        @Override
        public void onSuccess(String message) {
            endLoading();
        }

        @Override
        public void onFailure(String errorMessage) {
            runOnUiThread(() -> {
                hideLoading();
                Toast.makeText(TAPVideoPlayerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            });
        }
    };
}
