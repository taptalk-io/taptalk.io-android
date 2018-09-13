package com.moselo.SampleApps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.View.Activity.LoginActivity;
import com.moselo.HomingPigeon.View.Activity.RoomListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
        if (null != DataManager.getInstance().getActiveUser(this)){
            intent = new Intent(this, RoomListActivity.class);
        }else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hello:
                break;
        }
    }
}
