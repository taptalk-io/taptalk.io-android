package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;

public class TAPVideoPlayerActivity extends TAPBaseActivity {

    private final String TAG = TAPVideoPlayerActivity.class.getSimpleName();

    private FrameLayout flLoading;
    private ConstraintLayout clContainer, clFooter;
    private VideoView videoView;
    private TextView tvCurrentTime, tvDuration, tvLoadingText;
    private ImageView ivButtonClose, ivButtonSave, ivButtonMute, ivButtonPlayPause, ivSaving;
    private SeekBar seekBar;

    private TAPMessageModel message;
    private Uri videoUri;
    private MediaPlayer mediaPlayer;
    private Timer durationTimer, hidePauseButtonTimer;
    private int duration, pausedPosition;
    private float mediaVolume = 1f;
    private boolean isVideoPlaying, isSeeking, isFirstLoadFinished;

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
        TAPDataManager.getInstance().saveMediaVolumePreference(mediaVolume);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO) {
                saveVideo();
            }
        }
    }

    private void initView() {
        flLoading = findViewById(R.id.fl_loading);
        clContainer = findViewById(R.id.cl_container);
        clFooter = findViewById(R.id.cl_footer);
        videoView = findViewById(R.id.video_view);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSave = findViewById(R.id.iv_button_save);
        ivButtonMute = findViewById(R.id.iv_button_mute);
        ivButtonPlayPause = findViewById(R.id.iv_button_play_pause);
        ivSaving = findViewById(R.id.iv_saving);
        seekBar = findViewById(R.id.seek_bar);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonMute.setOnClickListener(v -> onMuteButtonTapped());
        ivButtonPlayPause.setOnClickListener(v -> onPlayOrPauseButtonTapped());
        clContainer.setOnClickListener(v -> onVideoTapped());

        if (null == message) {
            ivButtonSave.setVisibility(View.GONE);
        } else {
            ivButtonSave.setOnClickListener(saveButtonListener);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(videoView.getCurrentPosition(), duration));
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
        message = getIntent().getParcelableExtra(MESSAGE);
        Log.e(TAG, "open video: " + uriString);
        if (null != uriString) {
            videoUri = Uri.parse(uriString);
        } else {
            onBackPressed();
        }
    }

    private void loadVideo() {
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnCompletionListener(onCompletionListener);
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
        }, 0, 1000L);
        startHidePauseButtonTimer();
    }

    private void stopProgressTimer() {
        if (null == durationTimer) {
            return;
        }
        durationTimer.cancel();
        durationTimer = null;
        stopHidePauseButtonTimer();
    }

    private void startHidePauseButtonTimer() {
        stopHidePauseButtonTimer();
        hidePauseButtonTimer = new Timer();
        hidePauseButtonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> hideLayout(ivButtonPlayPause));
            }
        }, 2000L);
    }

    private void stopHidePauseButtonTimer() {
        if (null == hidePauseButtonTimer) {
            return;
        }
        hidePauseButtonTimer.cancel();
        hidePauseButtonTimer = null;
    }

    private void onVideoTapped() {
        if (clFooter.getVisibility() == View.VISIBLE) {
            // Hide UI
            hideLayout(clFooter);
            hideLayout(ivButtonClose);
            hideLayout(ivButtonMute);
            hideLayout(ivButtonPlayPause);
            if (null != message) {
                hideLayout(ivButtonSave);
            }
        } else {
            // Show UI
            showLayout(clFooter);
            showLayout(ivButtonClose);
            showLayout(ivButtonMute);
            showLayout(ivButtonPlayPause);
            if (null != message) {
                showLayout(ivButtonSave);
            }
            if (isVideoPlaying) {
                startHidePauseButtonTimer();
            }
        }
    }

    private void hideLayout(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(200L)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    private void showLayout(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(200L)
                .start();
    }

    private void onMuteButtonTapped() {
        if (mediaVolume > 0f) {
            // Mute video
            mediaVolume = 0f;
            ivButtonMute.setImageDrawable(getDrawable(R.drawable.tap_ic_volume_off));
        } else {
            // Turn sound on
            mediaVolume = 1f;
            ivButtonMute.setImageDrawable(getDrawable(R.drawable.tap_ic_volume_on));
        }
        mediaPlayer.setVolume(mediaVolume, mediaVolume);
    }

    private void onPlayOrPauseButtonTapped() {
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
        ivButtonPlayPause.setImageDrawable(getDrawable(R.drawable.tap_ic_button_play));
        stopProgressTimer();
    }

    private void resumeVideo() {
        isVideoPlaying = true;
        videoView.seekTo(pausedPosition);
        ivButtonPlayPause.setImageDrawable(getDrawable(R.drawable.tap_ic_button_pause));
    }

    private void saveVideo() {
        if (null == message) {
            return;
        }
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
        } else {
            showLoading();
            TAPFileDownloadManager.getInstance().writeFileToDisk(this, message, saveVideoListener);
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(getDrawable(R.drawable.tap_ic_loading_progress_circle_white));
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
            ivSaving.setImageDrawable(getDrawable(R.drawable.tap_ic_checklist_pumpkin));
            ivSaving.clearAnimation();
            tvLoadingText.setText(getString(R.string.tap_video_saved));
            flLoading.setOnClickListener(v -> hideLoading());

            new Handler().postDelayed(this::hideLoading, 1000L);
        });
    }

    private void hideLoading() {
        flLoading.setVisibility(View.GONE);
        ivButtonSave.setOnClickListener(saveButtonListener);
        flLoading.setOnClickListener(emptyListener);
    }

    private View.OnClickListener saveButtonListener = v -> saveVideo();
    private View.OnClickListener emptyListener = v -> {};

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (!isFirstLoadFinished) {
                // Play video on first load
                videoView.start();
                duration = videoView.getDuration();
                isVideoPlaying = true;
                TAPVideoPlayerActivity.this.startProgressTimer();
                mediaVolume = TAPDataManager.getInstance().getMediaVolumePreference();
                ivButtonMute.setImageDrawable(mediaVolume > 0f ? TAPVideoPlayerActivity.this.getDrawable(R.drawable.tap_ic_volume_on) : TAPVideoPlayerActivity.this.getDrawable(R.drawable.tap_ic_volume_off));
                tvDuration.setText(TAPUtils.getInstance().getMediaDurationString(duration, duration));
                isFirstLoadFinished = true;
            }
            videoView.seekTo(pausedPosition);
            mediaPlayer.setVolume(mediaVolume, mediaVolume);
            mediaPlayer.setOnSeekCompleteListener(mediaPlayer1 -> {
                tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(videoView.getCurrentPosition(), duration));
                if (!isSeeking && isVideoPlaying) {
                    videoView.start();
                    TAPVideoPlayerActivity.this.startProgressTimer();
                    pausedPosition = 0;
                } else {
                    pausedPosition = videoView.getCurrentPosition();
                    if (pausedPosition >= duration) {
                        pausedPosition = 0;
                    }
                }
            });
            TAPVideoPlayerActivity.this.mediaPlayer = mediaPlayer;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            TAPVideoPlayerActivity.this.pauseVideo();
            tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(duration, duration));
            pausedPosition = 0;
        }
    };

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
