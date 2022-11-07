package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
import android.os.PersistableBundle
import io.taptalk.TapTalk.R

class TapGroupsInCommonActivity :  TAPBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.tap_activity_groups_in_common)
    }
}