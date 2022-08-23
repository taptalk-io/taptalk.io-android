package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_SHARED_MEDIA
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import kotlinx.android.synthetic.main.tap_activity_shared_media.*

class TapSharedMediaActivity : TAPBaseActivity() {

    companion object {
        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel
        ) {
            val intent = Intent(context, TapSharedMediaActivity::class.java)
            intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
            intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
            context.startActivityForResult(intent, OPEN_SHARED_MEDIA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_shared_media)

        vp_shared_media.adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager)
        tab_layout.setupWithViewPager(vp_shared_media)
        // TODO: 23/08/22 setup view pager MU
        // TODO: 23/08/22 load local data MU
        // TODO: 23/08/22 load api from api MU
    }
}