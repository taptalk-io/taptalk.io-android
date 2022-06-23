package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import io.moselo.SampleApps.Fragment.TAPLoginVerificationFragment
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.ViewModel.TapDeleteAccountViewModel
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_activity_delete_account.*

class TapDeleteAccountActivity : TAPBaseActivity() {

    private val viewModel : TapDeleteAccountViewModel by lazy {
        ViewModelProvider(this)[TapDeleteAccountViewModel::class.java]
    }

    companion object {

        private const val VERIFICATION_TAG = "verificationTag"
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_delete_account)

        val spannable = SpannableString(tv_subtitle.text)
        spannable.setSpan(
            TypefaceSpan("sans-serif"),
            35,
            tv_subtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.tapActionSheetDestructiveLabelColor)),
            35,
            tv_subtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            35,
            tv_subtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        tv_subtitle.text = spannable

        et_note.setOnTouchListener { v: View, event: MotionEvent ->
            if (et_note.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_SCROLL) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }
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

    private fun showOTPVerification() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.tap_slide_left_fragment,
                R.animator.tap_fade_out_fragment,
                R.animator.tap_fade_in_fragment,
                R.animator.tap_slide_right_fragment
            )
            .replace(
                R.id.fl_container,
                TAPLoginVerificationFragment.getInstance()
            )
            .addToBackStack(VERIFICATION_TAG)
            .commit()
    }

    private fun hideOTPVerification() {
        supportFragmentManager.popBackStack(VERIFICATION_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}