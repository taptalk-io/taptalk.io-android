package io.moselo.SampleApps.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalkSample.R;

import static io.moselo.SampleApps.SampleApplication.INSTANCE_KEY;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (TAPDataManager.getInstance(INSTANCE_KEY).checkAccessTokenAvailable()) {
            TapUIRoomListActivity.start(MainActivity.this, INSTANCE_KEY);
        } else {
            TAPLoginActivity.start(MainActivity.this, INSTANCE_KEY);
        }
        AnalyticsManager.getInstance(INSTANCE_KEY).trackActiveUser();
        finish();
    }
}
