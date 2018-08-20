package com.moselo.HomingPigeon.Helper.AESCrypto;

import android.webkit.JavascriptInterface;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.CallJavaResultInterface;

public class JavaScriptInterface {
    private final CallJavaResultInterface mCallJavaResultInterface;

    public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
        this.mCallJavaResultInterface = callJavaResult;
    }

    @JavascriptInterface
    public void returnResultToJava(String value) {
        this.mCallJavaResultInterface.jsCallFinished(value);
    }
}
