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
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.databinding.TapActivityWebBrowserBinding

class TAPWebBrowserActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityWebBrowserBinding
    companion object {
        const val EXTRA_URL = "extra.url"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityWebBrowserBinding.inflate(layoutInflater)
        setContentView(vb.root)
        val url: String = intent.getStringExtra(EXTRA_URL) ?: ""

        vb.ivCloseBtn.setOnClickListener { v: View? ->
            run {
                finish()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
            }
        }

        vb.tvTitle.visibility = View.GONE
        vb.tvUrl.text = url
        vb.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (100 > newProgress && Build.VERSION.SDK_INT >= 24) {
                    vb.pbLoadingWebview.visibility = View.VISIBLE
                    vb.pbLoadingWebview.setProgress(newProgress, true)
                }
                else if (100 > newProgress) {
                    vb.pbLoadingWebview.visibility = View.VISIBLE
                    vb.pbLoadingWebview.progress = newProgress
                }
                else if (Build.VERSION.SDK_INT >= 24) {
                    vb.pbLoadingWebview.setProgress(100, true)
                    vb.pbLoadingWebview.visibility = View.GONE
                }
                else {
                    vb.pbLoadingWebview.progress = 100
                    vb.pbLoadingWebview.visibility = View.GONE
                }
            }
        }

        vb.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (null != view) {
                    vb.tvTitle.visibility = View.VISIBLE
                    vb.tvTitle.text = view.title
                }
            }
        }

        val webSettings: WebSettings = vb.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        vb.webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (vb.webView.canGoBack()) {
            vb.webView.goBack()
        }
        else {
            try {
                super.onBackPressed()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, R.color.tapColorPrimary))
    }
}
