package io.taptalk.TapTalk.View.Activity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.VIDEO_MP4;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.ViewModel.TAPVideoPlayerViewModel;

public class TAPVideoPlayerActivity extends TAPBaseActivity {

    private final String TAG = TAPVideoPlayerActivity.class.getSimpleName();

    private FrameLayout flLoading;
    private ConstraintLayout clContainer, clFooter;
    private VideoView videoView;
    private TextView tvCurrentTime, tvCurrentTimeDummy, tvDuration, tvDurationDummy, tvLoadingText;
    private ImageView ivButtonClose, ivButtonSave, ivButtonMute, ivButtonPlayPause, ivSaving;
    private ProgressBar pbVideoLoading;
    private SeekBar seekBar; // TODO: 21 November 2019 STYLE SEEK BAR FOR API BELOW 21

    private TAPVideoPlayerViewModel vm;

    public static void start(
        Context context,
        String instanceKey,
        Uri uri
    ) {
        start(context, instanceKey, uri, null, null);
    }

    public static void start(
        Context context,
        String instanceKey,
        String url
    ) {
        start(context, instanceKey, null, url, null);
    }

    public static void start(
        Context context,
        String instanceKey,
        Uri uri,
        @Nullable TAPMessageModel message
    ) {
        start(context, instanceKey, uri, null, message);
    }

    public static void start(
        Context context,
        String instanceKey,
        String url,
        @Nullable TAPMessageModel message
    ) {
        start(context, instanceKey, null, url, message);
    }

    public static void start(
        Context context,
        String instanceKey,
        Uri uri,
        String url,
        @Nullable TAPMessageModel message
    ) {
        Intent intent = new Intent(context, TAPVideoPlayerActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        if (null != uri) {
            intent.putExtra(URI, uri.toString());
        }
        if (null != url && !url.isEmpty()) {
            intent.putExtra(URL_MESSAGE, url);
        }
        if (null != message) {
            intent.putExtra(MESSAGE, message);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        }
    }

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
        TAPDataManager.getInstance(instanceKey).saveMediaVolumePreference(vm.getMediaVolume());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != vm.getDurationTimer()) {
            vm.getDurationTimer().cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (TAPUtils.allPermissionsGranted(grantResults)) {
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
        vm = new ViewModelProvider(this).get(TAPVideoPlayerViewModel.class);
        String uriString = getIntent().getStringExtra(URI);
        String urlPath = getIntent().getStringExtra(URL_MESSAGE);
        vm.setMessage(getIntent().getParcelableExtra(MESSAGE));
        if (null != uriString) {
            vm.setVideoUri(Uri.parse(uriString));
        }
        else if (null != urlPath && !urlPath.isEmpty()) {
            vm.setVideoPath(urlPath);
            Uri savedUri = TAPFileDownloadManager.getInstance(instanceKey).getFileMessageUri(TAPUtils.getUriKeyFromUrl(urlPath));
            if (savedUri != null) {
                vm.setVideoUri(savedUri);
            }
            else {
                loadVideoFromUrl();
            }
        }
        else {
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
        pbVideoLoading = findViewById(R.id.pb_video_loading);
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

        if (null == vm.getVideoUri() && null != vm.getVideoPath() && !vm.getVideoPath().isEmpty()) {
            showVideoLoading();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonClose.setBackground(AppCompatResources.getDrawable(this, R.drawable.tap_bg_video_player_button_ripple));
            ivButtonSave.setBackground(AppCompatResources.getDrawable(this, R.drawable.tap_bg_video_player_button_ripple));
            ivButtonMute.setBackground(AppCompatResources.getDrawable(this, R.drawable.tap_bg_video_player_button_ripple));
        }
    }

    private void updateVideoViewParams() {
        if (vm.getVideoUri() == null) {
            return;
        }
        try {
            // Get video data
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            if (vm.getVideoUri() != null) {
                retriever.setDataSource(TAPVideoPlayerActivity.this, vm.getVideoUri());
            }
            String rotation = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            }
            int width, height;
            if (null != rotation && (rotation.equals("90") || rotation.equals("270"))) {
                // Swap width and height when video is rotated
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            }
            else {
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }

            // Fix videoView layout params
            float videoRatio = (float) width / (float) height;
            float screenRatio = (float) TAPUtils.getScreenWidth() / (float) TAPUtils.getScreenHeight();
            if (screenRatio > videoRatio) {
                videoView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                videoView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            retriever.release();
        }
        catch (Exception e) {
            e.printStackTrace();
            finishActivity();
        }
    }

    private void loadVideo() {
        if (vm.getVideoUri() == null) {
            return;
        }
        runOnUiThread(() -> {
            try {
                if (vm.getVideoUri() != null) {
                    videoView.setVideoURI(vm.getVideoUri());
                }
                videoView.setOnPreparedListener(onPreparedListener);
                videoView.setOnCompletionListener(onCompletionListener);
                videoView.setOnErrorListener(onErrorListener);
            }
            catch (Exception e) {
                e.printStackTrace();
                finishActivity();
            }
        });
    }

    private void loadVideoFromUrl() {
        new Thread(() -> {
            File videoFile = TAPFileUtils.saveFileFromUrl(this, vm.getVideoPath(), VIDEO_MP4);
            if (videoFile != null && videoFile.length() > 0L) {
                Uri uri = Uri.fromFile(videoFile);
                TAPFileDownloadManager.getInstance(instanceKey).saveFileMessageUri(TAPUtils.getUriKeyFromUrl(vm.getVideoPath()), uri);
                vm.setVideoUri(uri);
                hideVideoLoading();
                updateVideoViewParams();
                loadVideo();
            }
        }).start();
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
            if (!vm.isVideoLoading()) {
                showLayout(ivButtonPlayPause, 1f);
            }
            if (null != vm.getMessage()) {
                showLayout(ivButtonSave, 0.7f);
            }
            if (vm.isVideoPlaying()) {
                startHidePauseButtonTimer();
            }
        }
    }

    private void hideLayout(View view) {
        runOnUiThread(() -> {
            view.animate()
                .alpha(0f)
                .setDuration(200L)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
        });
    }

    private void showLayout(View view, float alpha) {
        runOnUiThread(() -> {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate()
                .alpha(alpha)
                .setDuration(200L)
                .start();
        });
    }

    private void onMuteButtonTapped() {
        runOnUiThread(() -> {
            if (vm.getMediaVolume() > 0f) {
                // Mute video
                vm.setMediaVolume(0f);
                ivButtonMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_volume_off));
            }
            else {
                // Turn sound on
                vm.setMediaVolume(1f);
                ivButtonMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_volume_on));
            }
            vm.getMediaPlayer().setVolume(vm.getMediaVolume(), vm.getMediaVolume());
        });
    }

    private void onPlayOrPauseButtonTapped() {
        if (vm.isVideoPlaying()) {
            pauseVideo();
        } else {
            resumeVideo();
        }
    }

    private void pauseVideo() {
        try {
            vm.setVideoPlaying(false);
            vm.setPausedPosition(vm.getMediaPlayer().getCurrentPosition());
            vm.getMediaPlayer().pause();
            ivButtonPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_button_play));
            stopProgressTimer();
        }
        catch (Exception e) {
            e.printStackTrace();
            finishActivity();
        }
    }

    private void resumeVideo() {
        try {
            vm.setVideoPlaying(true);
            vm.getMediaPlayer().seekTo(vm.getPausedPosition());
            ivButtonPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_button_pause));
        }
        catch (Exception e) {
            e.printStackTrace();
            finishActivity();
        }
    }

    private void saveVideo() {
        if (null == vm.getMessage()) {
            return;
        }
        if (!TAPUtils.hasPermissions(this, TAPUtils.getStoragePermissions(false))) {
            // Request storage permission
            ActivityCompat.requestPermissions(this, TAPUtils.getStoragePermissions(false), PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
        }
        else {
            showSaveVideoLoading();
            TAPFileDownloadManager.getInstance(instanceKey).writeFileToDisk(this, vm.getMessage(), saveVideoListener);
        }
    }

    private void showVideoLoading() {
        runOnUiThread(() -> {
            vm.setVideoLoading(true);
            videoView.setVisibility(View.GONE);
            pbVideoLoading.setVisibility(View.VISIBLE);
            hideLayout(ivButtonPlayPause);
        });
    }

    private void hideVideoLoading() {
        runOnUiThread(() -> {
            videoView.setVisibility(View.VISIBLE);
            pbVideoLoading.setVisibility(View.GONE);
            showLayout(ivButtonPlayPause, 1f);
            vm.setVideoLoading(false);
        });
    }

    private void showSaveVideoLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            TAPUtils.rotateAnimateInfinitely(this, ivSaving);
            tvLoadingText.setText(getString(R.string.tap_saving));
            ivButtonSave.setOnClickListener(null);
            flLoading.setVisibility(View.VISIBLE);
        });
    }

    private void endSaveVideoLoading() {
        runOnUiThread(() -> {
            ivSaving.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin));
            ivSaving.clearAnimation();
            tvLoadingText.setText(getString(R.string.tap_video_saved));
            flLoading.setOnClickListener(v -> hideSaveVideoLoading());

            new Handler().postDelayed(this::hideSaveVideoLoading, 1000L);
        });
    }

    private void hideSaveVideoLoading() {
        runOnUiThread(() -> {
            flLoading.setVisibility(View.GONE);
            ivButtonSave.setOnClickListener(saveButtonListener);
            flLoading.setOnClickListener(emptyListener);
        });
    }

    private final View.OnClickListener saveButtonListener = v -> saveVideo();
    private final View.OnClickListener emptyListener = v -> {};

    private final MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (!vm.isFirstLoadFinished()) {
                // Play video on first load
                mediaPlayer.start();
                vm.setDuration(mediaPlayer.getDuration());
                vm.setVideoPlaying(true);
                TAPVideoPlayerActivity.this.startProgressTimer();
                vm.setMediaVolume(TAPDataManager.getInstance(instanceKey).getMediaVolumePreference());
                ivButtonMute.setImageDrawable(vm.getMediaVolume() > 0f ? ContextCompat.getDrawable(TAPVideoPlayerActivity.this, R.drawable.tap_ic_volume_on) : ContextCompat.getDrawable(TAPVideoPlayerActivity.this, R.drawable.tap_ic_volume_off));
                tvDuration.setText(TAPUtils.getMediaDurationString(vm.getDuration(), vm.getDuration()));
                vm.setFirstLoadFinished(true);
            }
            tvCurrentTimeDummy.setText(TAPUtils.getMediaDurationStringDummy(vm.getDuration()));
            tvDurationDummy.setText(TAPUtils.getMediaDurationStringDummy(vm.getDuration()));
            mediaPlayer.seekTo(vm.getPausedPosition());
            mediaPlayer.setVolume(vm.getMediaVolume(), vm.getMediaVolume());
            mediaPlayer.setOnSeekCompleteListener(onSeekListener);
            vm.setMediaPlayer(mediaPlayer);
        }
    };

    private final MediaPlayer.OnSeekCompleteListener onSeekListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            try {
                tvCurrentTime.setText(TAPUtils.getMediaDurationString(mediaPlayer.getCurrentPosition(), vm.getDuration()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (!vm.isSeeking() && vm.isVideoPlaying()) {
                mediaPlayer.start();
                TAPVideoPlayerActivity.this.startProgressTimer();
            }
            else {
                vm.setPausedPosition(mediaPlayer.getCurrentPosition());
                if (vm.getPausedPosition() >= vm.getDuration()) {
                    vm.setPausedPosition(0);
                }
            }
        }
    };

    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            pauseVideo();
            vm.getMediaPlayer().seekTo(vm.getDuration());
            seekBar.setProgress(seekBar.getMax());
        }
    };

    private final MediaPlayer.OnErrorListener onErrorListener = (mp, what, extra) -> {
        Toast.makeText(TapTalk.appContext, getString(R.string.tap_error_unable_to_play_video), Toast.LENGTH_SHORT).show();
        finishActivity();
        return false;
    };

    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            try {
                tvCurrentTime.setText(TAPUtils.getMediaDurationString(vm.getMediaPlayer().getCurrentPosition(), vm.getDuration()));
            }
            catch (Exception e) {
                e.printStackTrace();
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

    private final TapTalkActionInterface saveVideoListener = new TapTalkActionInterface() {
        @Override
        public void onSuccess(String message) {
            endSaveVideoLoading();
        }

        @Override
        public void onError(String errorMessage) {
            runOnUiThread(() -> {
                hideSaveVideoLoading();
                Toast.makeText(TAPVideoPlayerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            });
        }
    };
    
    private void finishActivity() {
        if (!isFinishing()) {
            finish();
        }
    }
}
