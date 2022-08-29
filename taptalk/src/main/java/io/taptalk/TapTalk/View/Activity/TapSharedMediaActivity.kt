package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_SHARED_MEDIA
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import kotlinx.android.synthetic.main.tap_activity_shared_media.*

class TapSharedMediaActivity : TAPBaseActivity() {

    private lateinit var vm : TapSharedMediaViewModel

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
        vm = ViewModelProvider(this, TapSharedMediaViewModel.TapSharedMediaViewModelFactory(application))[TapSharedMediaViewModel::class.java]
        vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        vp_shared_media.adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager, vm.room)
        tab_layout.setupWithViewPager(vp_shared_media)
        iv_button_back.setOnClickListener { onBackPressed() }
        // TODO: 23/08/22 setup view pager MU
        // TODO: 23/08/22 load local data MU
        // TODO: 23/08/22 load api from api MU
    }
}