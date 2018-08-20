package com.moselo.HomingPigeon.Helper.AESCrypto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.CallJavaResultInterface;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.WebViewWrapperInterface;

import java.io.UnsupportedEncodingException;

@SuppressLint({"SetJavaScriptEnabled"})
public class WebViewWrapper implements WebViewWrapperInterface {
    protected WebView mWebView;

    public WebViewWrapper(Context context, CallJavaResultInterface callJavaResult) {
        this.mWebView = new WebView(context);
        this.mWebView.setWillNotDraw(true);
        WebSettings webSettings = this.mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        JavaScriptInterface jsInterface = new JavaScriptInterface(callJavaResult);
        this.mWebView.addJavascriptInterface(jsInterface, "evgeniiJsEvaluator");
    }

    public void loadJavaScript(String javascript) {
        try {
            javascript = "<script>" + javascript + "</script>";
            byte[] data = javascript.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, 0);
            this.mWebView.loadUrl("data:text/html;charset=utf-8;base64," + base64);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

    }

    public void destroy() {
        if (this.mWebView != null) {
            this.mWebView.removeJavascriptInterface("evgeniiJsEvaluator");
            this.mWebView.loadUrl("about:blank");
            this.mWebView.stopLoading();
            if (Build.VERSION.SDK_INT < 19) {
                this.mWebView.freeMemory();
            }

            this.mWebView.clearHistory();
            this.mWebView.removeAllViews();
            this.mWebView.destroyDrawingCache();
            this.mWebView.destroy();
            this.mWebView = null;
        }

    }

    public WebView getWebView() {
        return this.mWebView;
    }
}
