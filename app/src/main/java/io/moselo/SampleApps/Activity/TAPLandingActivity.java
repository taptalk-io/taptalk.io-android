package io.moselo.SampleApps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import io.taptalk.TapTalkSample.R;

public class TAPLandingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_landing);

        Button launchDev = findViewById(R.id.button_taptalk_dev);
        Button launchStaging = findViewById(R.id.button_taptalk_staging);

        launchDev.setOnClickListener(v -> startMainActivity(""));
        launchStaging.setOnClickListener(v -> startMainActivity(""));
    }

    private void startMainActivity(String instanceKey) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("instanceKey", instanceKey);
        startActivity(intent);
    }
}
