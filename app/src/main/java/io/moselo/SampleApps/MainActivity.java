package io.moselo.SampleApps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TaptalkSample.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TapTalk.checkActiveUserToShowPage(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hello:
                break;
        }
    }
}
