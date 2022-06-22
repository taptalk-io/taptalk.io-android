package io.moselo.SampleApps.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_activity_delete_account.*

class TapDeleteAccountActivity : TAPBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_delete_account)
        cb_agree.setOnCheckedChangeListener { _, isChecked ->
            btn_delete.isEnabled = isChecked
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TapDeleteAccountActivity::class.java)
            context.startActivity(intent)
        }
        fun start(context: Context, instanceKey: String) {
            val intent = Intent(context, TapDeleteAccountActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            context.startActivity(intent)
        }
    }
}