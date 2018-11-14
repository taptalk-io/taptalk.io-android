package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.taptalk.TapTalk.Helper.TAPUtils;

public abstract class TAPBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.getInstance().dismissKeyboard(this);
    }

    protected abstract void initView();
}
