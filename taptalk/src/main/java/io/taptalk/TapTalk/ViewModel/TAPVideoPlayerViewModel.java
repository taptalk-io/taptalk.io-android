package io.taptalk.TapTalk.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.Timer;

import io.taptalk.TapTalk.Model.TAPMessageModel;

public class TAPVideoPlayerViewModel extends AndroidViewModel {
    private TAPMessageModel message;
    private Uri videoUri;
    private MediaPlayer mediaPlayer;
    private Timer durationTimer, hidePauseButtonTimer;
    private int duration, pausedPosition;
    private float mediaVolume = 1f;
    private boolean isVideoPlaying, isSeeking, isFirstLoadFinished;

    public TAPVideoPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public TAPMessageModel getMessage() {
        return message;
    }

    public void setMessage(TAPMessageModel message) {
        this.message = message;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public MediaPlayer getMediaPlayer() {
        return null != mediaPlayer ? mediaPlayer : new MediaPlayer();
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Timer getDurationTimer() {
        return durationTimer;
    }

    public void setDurationTimer(Timer durationTimer) {
        this.durationTimer = durationTimer;
    }

    public Timer getHidePauseButtonTimer() {
        return hidePauseButtonTimer;
    }

    public void setHidePauseButtonTimer(Timer hidePauseButtonTimer) {
        this.hidePauseButtonTimer = hidePauseButtonTimer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPausedPosition() {
        return pausedPosition;
    }

    public void setPausedPosition(int pausedPosition) {
        this.pausedPosition = pausedPosition;
    }

    public float getMediaVolume() {
        return mediaVolume;
    }

    public void setMediaVolume(float mediaVolume) {
        this.mediaVolume = mediaVolume;
    }

    public boolean isVideoPlaying() {
        return isVideoPlaying;
    }

    public void setVideoPlaying(boolean videoPlaying) {
        isVideoPlaying = videoPlaying;
    }

    public boolean isSeeking() {
        return isSeeking;
    }

    public void setSeeking(boolean seeking) {
        isSeeking = seeking;
    }

    public boolean isFirstLoadFinished() {
        return isFirstLoadFinished;
    }

    public void setFirstLoadFinished(boolean firstLoadFinished) {
        isFirstLoadFinished = firstLoadFinished;
    }
}
