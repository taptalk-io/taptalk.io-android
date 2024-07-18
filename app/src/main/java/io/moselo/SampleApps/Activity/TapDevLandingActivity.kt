package io.moselo.SampleApps.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalkSample.databinding.ActivityDevLandingBinding

class TapDevLandingActivity : TAPBaseActivity() {

    private lateinit var binding: ActivityDevLandingBinding

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
        binding = ActivityDevLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llButtonOpenChat.setOnClickListener {
            TapUIRoomListActivity.start(this, instanceKey)
        }
    }
}
