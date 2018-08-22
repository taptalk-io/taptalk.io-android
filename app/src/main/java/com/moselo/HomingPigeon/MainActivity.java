package com.moselo.HomingPigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.View.Activity.SampleLoginActivity;
import com.moselo.HomingPigeon.View.Activity.SampleRoomListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
        if (null != DataManager.getInstance().getUserModel(this)){
            intent = new Intent(this, SampleRoomListActivity.class);
        }else {
            intent = new Intent(this, SampleLoginActivity.class);
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
