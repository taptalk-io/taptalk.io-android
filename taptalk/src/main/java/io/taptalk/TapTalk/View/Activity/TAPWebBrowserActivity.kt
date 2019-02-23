package io.taptalk.TapTalk.View.Activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebViewClient
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
        var url : String = intent.getStringExtra(EXTRA_URL)
        wv_browser.webViewClient = WebViewClient()
        val webSettings : WebSettings = wv_browser.settings
        webSettings.javaScriptEnabled = true
        title = url
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        wv_browser.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
