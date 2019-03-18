package io.taptalk.TapTalk.View.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;

public class TAPVideoPlayerActivity extends TAPBaseActivity {

    private VideoView videoView;

    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_video_player);

        initView();
        receiveIntent();
        loadVideo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out);
    }

    private void initView() {
        videoView = findViewById(R.id.video_view);
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
        videoView.start();
    }
}
