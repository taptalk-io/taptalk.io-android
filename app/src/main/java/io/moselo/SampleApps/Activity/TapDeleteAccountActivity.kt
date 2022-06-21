package io.moselo.SampleApps.Activity

import android.os.Bundle
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalkSample.R

class TapDeleteAccountActivity : TAPBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_delete_account)
    }
}