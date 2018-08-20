package com.moselo.HomingPigeon.Helper.AESCrypto;

import android.os.Handler;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.HandlerWrapperInterface;

public class HandlerWrapper implements HandlerWrapperInterface {
    private final Handler mHandler = new Handler();

    public HandlerWrapper() {
    }

    public void post(Runnable r) {
        this.mHandler.post(r);
    }
}
