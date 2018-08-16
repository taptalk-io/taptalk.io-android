package com.moselo.HomingPigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moselo.HomingPigeon.SampleApp.Activity.SampleLoginActivity;
import com.moselo.HomingPigeon.Testing.LibraryActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SampleLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
