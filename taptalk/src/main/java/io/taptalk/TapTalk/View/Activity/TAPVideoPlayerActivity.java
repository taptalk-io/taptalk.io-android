package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import io.taptalk.TapTalk.ViewModel.TAPVideoPlayerViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;

public class TAPVideoPlayerActivity extends AppCompatActivity {

    private final String TAG = TAPVideoPlayerActivity.class.getSimpleName();

    private FrameLayout flLoading;
    private ConstraintLayout clContainer, clFooter;
    private VideoView videoView;
    private TextView tvCurrentTime, tvCurrentTimeDummy, tvDuration, tvDurationDummy, tvLoadingText;
    private ImageView ivButtonClose, ivButtonSave, ivButtonMute, ivButtonPlayPause, ivSaving;
    private SeekBar seekBar;

    private TAPVideoPlayerViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_video_player);

        initViewModel();
        initView();
        updateVideoViewParams();
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
        TAPDataManager.getInstance().saveMediaVolumePreference(vm.getMediaVolume());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO) {
                saveVideo();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVideoViewParams();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPVideoPlayerViewModel.class);
        String uriString = getIntent().getStringExtra(URI);
        vm.setMessage(getIntent().getParcelableExtra(MESSAGE));
        if (null != uriString) {
            vm.setVideoUri(Uri.parse(uriString));
        } else {
            onBackPressed();
        }
    }

    private void initView() {
        flLoading = findViewById(R.id.fl_loading);
        clContainer = findViewById(R.id.cl_container);
        clFooter = findViewById(R.id.cl_footer);
        videoView = findViewById(R.id.video_view);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvCurrentTimeDummy = findViewById(R.id.tv_current_time_dummy);
        tvDuration = findViewById(R.id.tv_duration);
        tvDurationDummy = findViewById(R.id.tv_duration_dummy);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSave = findViewById(R.id.iv_button_save);
        ivButtonMute = findViewById(R.id.iv_button_mute);
        ivButtonPlayPause = findViewById(R.id.iv_button_play_pause);
        ivSaving = findViewById(R.id.iv_loading_image);
        seekBar = findViewById(R.id.seek_bar);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonMute.setOnClickListener(v -> onMuteButtonTapped());
        ivButtonPlayPause.setOnClickListener(v -> onPlayOrPauseButtonTapped());
        clContainer.setOnClickListener(v -> onVideoTapped());

        if (null == vm.getMessage()) {
            ivButtonSave.setVisibility(View.GONE);
        } else {
            ivButtonSave.setOnClickListener(saveButtonListener);
        }

        seekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    private void updateVideoViewParams() {
        // Get video data
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(TAPVideoPlayerActivity.this, vm.getVideoUri());
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int width, height;
        if (rotation.equals("90") || rotation.equals("270")) {
            // Swap width and height when video is rotated
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        } else {
            width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        }

        // Fix videoView layout params
        float videoRatio = (float) width / (float) height;
        float screenRatio = (float) TAPUtils.getInstance().getScreenWidth() / (float) TAPUtils.getInstance().getScreenHeight();
        if (screenRatio > videoRatio) {
            videoView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            videoView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            videoView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        retriever.release();
    }

    private void loadVideo() {
        videoView.setVideoURI(vm.getVideoUri());
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnCompletionListener(onCompletionListener);
    }

    private void startProgressTimer() {
        if (null != vm.getDurationTimer()) {
            return;
        }
        vm.setDurationTimer(new Timer());
        vm.getDurationTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> seekBar.setProgress(vm.getMediaPlayer().getCurrentPosition() * seekBar.getMax() / vm.getDuration()));
            }
        }, 0, 10L);
        startHidePauseButtonTimer();
    }

    private void stopProgressTimer() {
        if (null == vm.getDurationTimer()) {
            return;
        }
        vm.getDurationTimer().cancel();
        vm.setDurationTimer(null);
        stopHidePauseButtonTimer();
    }

    private void startHidePauseButtonTimer() {
        stopHidePauseButtonTimer();
        vm.setHidePauseButtonTimer(new Timer());
        vm.getHidePauseButtonTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> hideLayout(ivButtonPlayPause));
            }
        }, 2000L);
    }

    private void stopHidePauseButtonTimer() {
        if (null == vm.getHidePauseButtonTimer()) {
            return;
        }
        vm.getHidePauseButtonTimer().cancel();
        vm.setHidePauseButtonTimer(null);
    }

    private void onVideoTapped() {
        if (clFooter.getVisibility() == View.VISIBLE) {
            // Hide UI
            hideLayout(clFooter);
            hideLayout(ivButtonClose);
            hideLayout(ivButtonMute);
            hideLayout(ivButtonPlayPause);
            if (null != vm.getMessage()) {
                hideLayout(ivButtonSave);
            }
        } else {
            // Show UI
            showLayout(clFooter, 1f);
            showLayout(ivButtonClose, 0.7f);
            showLayout(ivButtonMute, 0.7f);
            showLayout(ivButtonPlayPause, 1f);
            if (null != vm.getMessage()) {
                showLayout(ivButtonSave, 0.7f);
            }
            if (vm.isVideoPlaying()) {
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

    private void showLayout(View view, float alpha) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(alpha)
                .setDuration(200L)
                .start();
    }

    private void onMuteButtonTapped() {
        if (vm.getMediaVolume() > 0f) {
            // Mute video
            vm.setMediaVolume(0f);
            ivButtonMute.setImageDrawable(getDrawable(R.drawable.tap_ic_volume_off));
        } else {
            // Turn sound on
            vm.setMediaVolume(1f);
            ivButtonMute.setImageDrawable(getDrawable(R.drawable.tap_ic_volume_on));
        }
        vm.getMediaPlayer().setVolume(vm.getMediaVolume(), vm.getMediaVolume());
    }

    private void onPlayOrPauseButtonTapped() {
        if (vm.isVideoPlaying()) {
            pauseVideo();
        } else {
            resumeVideo();
        }
    }

    private void pauseVideo() {
        vm.setVideoPlaying(false);
        vm.setPausedPosition(vm.getMediaPlayer().getCurrentPosition());
        vm.getMediaPlayer().pause();
        ivButtonPlayPause.setImageDrawable(getDrawable(R.drawable.tap_ic_button_play));
        stopProgressTimer();
    }

    private void resumeVideo() {
        vm.setVideoPlaying(true);
        vm.getMediaPlayer().seekTo(vm.getPausedPosition());
        ivButtonPlayPause.setImageDrawable(getDrawable(R.drawable.tap_ic_button_pause));
    }

    private void saveVideo() {
        if (null == vm.getMessage()) {
            return;
        }
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
        } else {
            showLoading();
            TAPFileDownloadManager.getInstance().writeFileToDisk(this, vm.getMessage(), saveVideoListener);
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
            if (!vm.isFirstLoadFinished()) {
                // Play video on first load
                mediaPlayer.start();
                vm.setDuration(mediaPlayer.getDuration());
                vm.setVideoPlaying(true);
                TAPVideoPlayerActivity.this.startProgressTimer();
                vm.setMediaVolume(TAPDataManager.getInstance().getMediaVolumePreference());
                ivButtonMute.setImageDrawable(vm.getMediaVolume() > 0f ? TAPVideoPlayerActivity.this.getDrawable(R.drawable.tap_ic_volume_on) : TAPVideoPlayerActivity.this.getDrawable(R.drawable.tap_ic_volume_off));
                tvDuration.setText(TAPUtils.getInstance().getMediaDurationString(vm.getDuration(), vm.getDuration()));
                vm.setFirstLoadFinished(true);
            }
            tvCurrentTimeDummy.setText(TAPUtils.getInstance().getMediaDurationStringDummy(vm.getDuration()));
            tvDurationDummy.setText(TAPUtils.getInstance().getMediaDurationStringDummy(vm.getDuration()));
            mediaPlayer.seekTo(vm.getPausedPosition());
            mediaPlayer.setVolume(vm.getMediaVolume(), vm.getMediaVolume());
            mediaPlayer.setOnSeekCompleteListener(onSeekListener);
            vm.setMediaPlayer(mediaPlayer);
        }
    };

    private MediaPlayer.OnSeekCompleteListener onSeekListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            try {
                tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(mediaPlayer.getCurrentPosition(), vm.getDuration()));
            } catch (Exception e) {
                Log.e(TAG, "onProgressChanged: " + e.getMessage());
            }
            if (!vm.isSeeking() && vm.isVideoPlaying()) {
                mediaPlayer.start();
                TAPVideoPlayerActivity.this.startProgressTimer();
            } else {
                vm.setPausedPosition(mediaPlayer.getCurrentPosition());
                if (vm.getPausedPosition() >= vm.getDuration()) {
                    vm.setPausedPosition(0);
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            pauseVideo();
            vm.getMediaPlayer().seekTo(vm.getDuration());
            seekBar.setProgress(seekBar.getMax());
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            try {
                tvCurrentTime.setText(TAPUtils.getInstance().getMediaDurationString(vm.getMediaPlayer().getCurrentPosition(), vm.getDuration()));
            } catch (Exception e) {
                Log.e(TAG, "onProgressChanged: " + e.getMessage());
            }
            if (vm.isSeeking()) {
                //videoView.seekTo(vm.getDuration() * seekBar.getProgress() / seekBar.getMax());
                vm.getMediaPlayer().seekTo(vm.getDuration() * seekBar.getProgress() / seekBar.getMax());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            vm.setSeeking(true);
            if (vm.isVideoPlaying()) {
                vm.getMediaPlayer().pause();
                stopProgressTimer();
            }
            //videoView.seekTo(vm.getDuration() * seekBar.getProgress() / seekBar.getMax());
            vm.getMediaPlayer().seekTo(vm.getDuration() * seekBar.getProgress() / seekBar.getMax());
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            vm.setSeeking(false);
            vm.getMediaPlayer().seekTo(vm.getDuration() * seekBar.getProgress() / seekBar.getMax());
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
