package com.moselo.HomingPigeon.Helper.AESCrypto.Interface;

import android.webkit.WebView;

public interface WebViewWrapperInterface {
    void loadJavaScript(String s);

    void destroy();

    WebView getWebView();
}
