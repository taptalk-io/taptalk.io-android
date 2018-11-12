package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.moselo.HomingPigeon.Helper.HpUtils;

public abstract class HpBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HpUtils.getInstance().dismissKeyboard(this);
    }

    protected abstract void initView();
}
