package io.moselo.SampleApps.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.TapTalkDialog
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
        btn_delete.setOnClickListener {
            // TODO: 22/06/22 call check state api MU
            // TODO: 22/06/22 for testing purposes only MU
            if (et_note.text.toString().isEmpty()) {
                TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_wording_oops_only_admin))
                    .setMessage(getString(R.string.tap_wording_sign_to_another_member))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener {  }
                    .show()
            } else {
                TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_delete_my_account))
                    .setMessage(getString(R.string.tap_wording_delete_account))
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setPrimaryButtonTitle(getString(R.string.tap_delete))
                    .setPrimaryButtonListener { /* TODO: move to otp verification MU */ }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setSecondaryButtonListener {  }
                    .show()
            }
        }
        iv_button_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_right)
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