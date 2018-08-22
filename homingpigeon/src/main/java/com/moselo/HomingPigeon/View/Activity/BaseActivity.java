package com.moselo.HomingPigeon.View.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsConnectionError;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BroadcastManager.register(this,receiver,kIsConnectionError);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case kIsConnectionError :
                    ConnectionManager.getInstance().reconnect();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastManager.unregister(this,receiver);
    }
}
