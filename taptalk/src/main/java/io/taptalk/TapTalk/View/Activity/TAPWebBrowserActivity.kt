package io.taptalk.TapTalk.View.Activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_web_browser.*

class TAPWebBrowserActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_URL = "extra.url"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_web_browser)
        var url: String = intent.getStringExtra(EXTRA_URL)

        iv_close_btn.setOnClickListener { v: View? ->
            run {
                finish()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
            }
        }

        tv_title.visibility = View.GONE
        tv_url.text = url
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (100 > newProgress && Build.VERSION.SDK_INT >= 24) {
                    pb_loading_webview.visibility = View.VISIBLE
                    pb_loading_webview.setProgress(newProgress, true)
                } else if (100 > newProgress) {
                    pb_loading_webview.visibility = View.VISIBLE
                    pb_loading_webview.progress = newProgress
                } else if (Build.VERSION.SDK_INT >= 24) {
                    pb_loading_webview.setProgress(100, true)
                    pb_loading_webview.visibility = View.GONE
                } else {
                    pb_loading_webview.progress = 100
                    pb_loading_webview.visibility = View.GONE
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (null != view) {
                    tv_title.visibility = View.VISIBLE
                    tv_title.text = view.title
                }
            }
        }

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        }
    }
}
