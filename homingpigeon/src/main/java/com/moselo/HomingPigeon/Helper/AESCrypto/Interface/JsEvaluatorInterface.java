package com.moselo.HomingPigeon.Helper.AESCrypto.Interface;

import android.webkit.WebView;

public interface JsEvaluatorInterface {
    void callFunction(String s, JsCallback jsCallback, String s1, Object... objects);

    void evaluate(String s);

    void evaluate(String s, JsCallback jsCallback);

    void destroy();

    WebView getWebView();
}
