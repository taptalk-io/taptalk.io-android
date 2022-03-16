package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
import io.taptalk.TapTalk.R

class TapStarredMessagesActivity : TAPBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_starred_messages)
    }
}