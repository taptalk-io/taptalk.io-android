package io.moselo.SampleApps.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TapTalkSample.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String instanceKey = getIntent().getStringExtra("instanceKey");
        String instanceKey = "";

        if (TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            TapUIRoomListActivity.start(MainActivity.this, instanceKey);
        } else {
            TAPLoginActivity.start(MainActivity.this, instanceKey, false);
        }
        AnalyticsManager.getInstance(instanceKey).trackActiveUser();
        finish();
    }
}
