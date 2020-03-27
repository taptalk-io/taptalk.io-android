package io.moselo.SampleApps.Activity;

import android.content.Intent;
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
        if (TAPDataManager.getInstance("").checkAccessTokenAvailable()) {
            TapUIRoomListActivity.start(MainActivity.this, "");
        } else {
            TAPLoginActivity.start(MainActivity.this, "");
        }
        AnalyticsManager.getInstance("").trackActiveUser();
        finish();
    }
}
