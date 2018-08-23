package com.moselo.HomingPigeon.View.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moselo.HomingPigeon.Helper.BroadcastManager;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Manager.ConnectionManager;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsConnectionError;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionBroadcast.kIsDisconnected;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BroadcastManager.register(this,receiver,kIsConnectionError, kIsDisconnected);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case kIsConnectionError :
                    ConnectionManager.getInstance().reconnect();
                    break;
                case kIsDisconnected:
                    if (HomingPigeon.isForeground)
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
