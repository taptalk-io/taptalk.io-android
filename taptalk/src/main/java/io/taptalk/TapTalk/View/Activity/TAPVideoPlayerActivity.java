package io.taptalk.TapTalk.View.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;

public class TAPVideoPlayerActivity extends TAPBaseActivity {

    private final String TAG = TAPVideoPlayerActivity.class.getSimpleName();

    //private FrameLayout flLoading;
    private ConstraintLayout clContainer, clFooter;
    private VideoView videoView;
    private TextView tvCurrentTime, tvDuration/*, tvTitle, tvMessageStatus, tvLoadingText*/;
    private ImageView ivButtonClose, ivButtonMute, ivButtonPlayPause/*, ivButtonBack, ivButtonSave, ivSaved*/;
    private SeekBar seekBar;
    //private ProgressBar pbSaving;

    private Uri videoUri;
    private MediaPlayer mediaPlayer;
    private Timer durationTimer, hidePauseButtonTimer;
    private int duration, pausedPosition;
    private float mediaVolume = -1f;
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
        TAPDataManager.getInstance().saveMediaVolumePreference(mediaVolume);
    }

    private void initView() {
        //flLoading = findViewById(R.id.fl_loading);
        clContainer = findViewById(R.id.cl_container);
        clFooter = findViewById(R.id.cl_footer);
        videoView = findViewById(R.id.video_view);
        //tvTitle = findViewById(R.id.tv_title);
        //tvMessageStatus = findViewById(R.id.tv_message_status);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvDuration = findViewById(R.id.tv_duration);
        //tvLoadingText = findViewById(R.id.tv_loading_text);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonMute = findViewById(R.id.iv_button_mute);
        ivButtonPlayPause = findViewById(R.id.iv_button_play_pause);
        //ivButtonBack = findViewById(R.id.iv_button_back);
        //ivButtonSave = findViewById(R.id.iv_save);
        //ivSaved = findViewById(R.id.iv_saved);
        seekBar = findViewById(R.id.seek_bar);
        //pbSaving = findViewById(R.id.pb_saving);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonMute.setOnClickListener(v -> onMuteButtonTapped());
        ivButtonPlayPause.setOnClickListener(v -> onPlayOrPauseButtonTapped());
        clContainer.setOnClickListener(v -> onVideoTapped());

//        if (null != videoUri) {
//            ivButtonSave.setVisibility(View.GONE);
//            ivButtonSave.setOnClickListener(null);
//        } else {
//            ivButtonSave.setVisibility(View.VISIBLE);
//            ivButtonSave.setOnClickListener(saveButtonListener);
//        }

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
        if (null != uriString) {
            videoUri = Uri.parse(uriString);
        } else {
            onBackPressed();
        }
    }

    private void loadVideo() {
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mediaPlayer -> {
            if (-1f == mediaVolume) {
                // Play video on first load
                videoView.start();
                duration = videoView.getDuration();
                isVideoPlaying = true;
                startProgressTimer();
                mediaVolume = TAPDataManager.getInstance().getMediaVolumePreference();
                ivButtonMute.setImageDrawable(mediaVolume > 0f ? getDrawable(R.drawable.tap_ic_volume_on) : getDrawable(R.drawable.tap_ic_volume_off));
                tvDuration.setText(TAPUtils.getInstance().getMediaDurationString(duration, duration));
            }
            videoView.seekTo(pausedPosition);
            mediaPlayer.setVolume(mediaVolume, mediaVolume);
            mediaPlayer.setOnSeekCompleteListener(mediaPlayer1 -> {
                tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(videoView.getCurrentPosition(), duration));
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
            this.mediaPlayer = mediaPlayer;
        });
        videoView.setOnCompletionListener(mediaPlayer -> {
            pauseVideo();
            tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(duration, duration));
            pausedPosition = 0;
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
        Log.e(TAG, "startHidePauseButtonTimer: ");
        stopHidePauseButtonTimer();
        hidePauseButtonTimer = new Timer();
        hidePauseButtonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "hideLayout: ");
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
        } else {
            // Show UI
            showLayout(clFooter);
            showLayout(ivButtonClose);
            showLayout(ivButtonMute);
            showLayout(ivButtonPlayPause);
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

//    private void saveVideo() {
//        if (null == videoUri) {
//            return;
//        }
//        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            // Request storage permission
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
//        } else {
//            showLoading();
//            // TODO: 18 March 2019 SAVE VIDEO HERE
//        }
//    }

//    private void showLoading() {
//        runOnUiThread(() -> {
//            flLoading.setVisibility(View.VISIBLE);
//            pbSaving.setVisibility(View.VISIBLE);
//            ivSaved.setVisibility(View.GONE);
//            tvLoadingText.setText(getString(R.string.tap_saving));
//            ivButtonSave.setOnClickListener(null);
//        });
//    }

//    private void endLoading() {
//        runOnUiThread(() -> {
//            pbSaving.setVisibility(View.GONE);
//            ivSaved.setVisibility(View.VISIBLE);
//            tvLoadingText.setText(getString(R.string.tap_image_saved));
//            flLoading.setOnClickListener(v -> hideLoading());
//
//            new Handler().postDelayed(this::hideLoading, 100L);
//        });
//    }

//    private void hideLoading() {
//        flLoading.setVisibility(View.GONE);
//        ivButtonSave.setOnClickListener(saveButtonListener);
//        flLoading.setOnClickListener(emptyListener);
//    }

//    private View.OnClickListener saveButtonListener = v -> saveVideo();
//    private View.OnClickListener emptyListener = v -> {};

//    private TapTalkActionInterface saveVideoListener = new TapTalkActionInterface() {
//        @Override
//        public void onSuccess(String message) {
//            endLoading();
//        }
//
//        @Override
//        public void onFailure(String errorMessage) {
//            runOnUiThread(() -> {
//                hideLoading();
//                Toast.makeText(TAPVideoPlayerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//            });
//        }
//    };
}
