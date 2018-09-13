package com.moselo.SampleApps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.View.Activity.SampleLoginActivity;
import com.moselo.HomingPigeon.View.Activity.SampleRoomListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HomingPigeon.checkActiveUserToShowPage(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hello:
                break;
        }
    }
}
