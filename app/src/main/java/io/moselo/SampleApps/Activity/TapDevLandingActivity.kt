package io.moselo.SampleApps.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.activity_dev_landing.*

class TapDevLandingActivity : TAPBaseActivity() {

    companion object {
        fun start(
                context: Context?,
                instanceKey: String?
        ) {
            val intent = Intent(context, TapDevLandingActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_landing)

        ll_button_open_chat.setOnClickListener {
            TapUIRoomListActivity.start(this, instanceKey)
        }
    }
}
