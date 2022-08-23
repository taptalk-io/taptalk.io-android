package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
import android.os.PersistableBundle
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import kotlinx.android.synthetic.main.tap_activity_shared_media.*

class TapSharedMediaActivity : TAPBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.tap_activity_shared_media)

        vp_shared_media.adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager)
        tab_layout.setupWithViewPager(vp_shared_media)
        // TODO: 23/08/22 setup view pager MU
        // TODO: 23/08/22 load local data MU
        // TODO: 23/08/22 load api from api MU
    }
}