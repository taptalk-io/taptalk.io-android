package com.moselo.HomingPigeon.Helper.AESCrypto;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.CallJavaResultInterface;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.HandlerWrapperInterface;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsCallback;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.JsEvaluatorInterface;
import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.WebViewWrapperInterface;

import java.util.concurrent.atomic.AtomicReference;

public class JsEvaluator implements CallJavaResultInterface, JsEvaluatorInterface {
    public static final String JS_NAMESPACE = "evgeniiJsEvaluator";
    private static final String JS_ERROR_PREFIX = "evgeniiJsEvaluatorException";
    protected WebViewWrapperInterface mWebViewWrapper;
    private final Context mContext;
    private AtomicReference callback = new AtomicReference((Object) null);
    private HandlerWrapperInterface mHandler = new HandlerWrapper();

    public static String escapeCarriageReturn(String str) {
        return str.replace("\r", "\\r");
    }

    public static String escapeClosingScript(String str) {
        return str.replace("</", "<\\/");
    }

    public static String escapeNewLines(String str) {
        return str.replace("\n", "\\n");
    }

    public static String escapeSingleQuotes(String str) {
        return str.replace("'", "\\'");
    }

    public static String escapeSlash(String str) {
        return str.replace("\\", "\\\\");
    }

    public static String getJsForEval(String jsCode) {
        jsCode = escapeSlash(jsCode);
        jsCode = escapeSingleQuotes(jsCode);
        jsCode = escapeClosingScript(jsCode);
        jsCode = escapeNewLines(jsCode);
        jsCode = escapeCarriageReturn(jsCode);
        return String.format("%s.returnResultToJava(eval('try{%s}catch(e){\"%s\"+e}'));", "evgeniiJsEvaluator", jsCode, "evgeniiJsEvaluatorException");
    }

    public JsEvaluator(Context context) {
        this.mContext = context;
    }

    public void callFunction(String jsCode, JsCallback resultCallback, String name, Object... args) {
        jsCode = jsCode + "; " + JsFunctionCallFormatter.toString(name, args);
        this.evaluate(jsCode, resultCallback);
        Log.e("]]]]", "callFunction jsCode: " + jsCode);
    }

    public void evaluate(String jsCode) {
        this.evaluate(jsCode, (JsCallback)null);
    }

    public void evaluate(String jsCode, JsCallback resultCallback) {
        String js = getJsForEval(jsCode);
        this.callback.set(resultCallback);
        this.getWebViewWrapper().loadJavaScript(js);
        Log.e("]]]]", "evaluate js: " + js);
    }

    public void destroy() {
        this.getWebViewWrapper().destroy();
    }

    public WebView getWebView() {
        return this.getWebViewWrapper().getWebView();
    }

    public JsCallback getCallback() {
        return (JsCallback)this.callback.get();
    }

    public WebViewWrapperInterface getWebViewWrapper() {
        if (this.mWebViewWrapper == null) {
            this.mWebViewWrapper = new WebViewWrapper(this.mContext, this);
        }

        return this.mWebViewWrapper;
    }

    public void jsCallFinished(final String value) {
        final JsCallback callbackLocal = (JsCallback)this.callback.getAndSet((Object)null);
        if (callbackLocal != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    Log.e("]]]]", "jsCallFinished value: " + value);
                    if (value != null && value.startsWith("evgeniiJsEvaluatorException")) {
                        callbackLocal.onError(value.substring("evgeniiJsEvaluatorException".length()));
                    } else {
                        callbackLocal.onResult(value);
                    }

                }
            });
        }
    }

    public void setHandler(HandlerWrapperInterface handlerWrapperInterface) {
        this.mHandler = handlerWrapperInterface;
    }

    public void setWebViewWrapper(WebViewWrapperInterface webViewWrapper) {
        this.mWebViewWrapper = webViewWrapper;
    }
}

