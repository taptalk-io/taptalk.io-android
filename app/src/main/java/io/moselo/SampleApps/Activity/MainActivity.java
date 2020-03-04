package io.moselo.SampleApps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity;
import io.taptalk.TaptalkSample.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
        if (TAPDataManager.getInstance().checkAccessTokenAvailable()) {
            intent = new Intent(MainActivity.this, TapUIRoomListActivity.class);
        } else {
            intent = new Intent(MainActivity.this, TAPLoginActivity.class);
        }
        AnalyticsManager.getInstance().trackActiveUser();
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_hello:
                break;
        }
    }
}
