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
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TapCheckDeleteAccountStateResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.ViewModel.TapDeleteAccountViewModel
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_activity_delete_account.*
import kotlinx.android.synthetic.main.tap_activity_delete_account.cl_action_bar
import kotlinx.android.synthetic.main.tap_activity_delete_account.iv_button_back
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*

class TapDeleteAccountActivity : TAPBaseActivity() {

    val vm : TapDeleteAccountViewModel by lazy {
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
            TAPDataManager.getInstance(instanceKey).checkDeleteAccountState(checkDeleteAccountStateView)
        }

        iv_button_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isEmpty()) {
            super.onBackPressed()
            overridePendingTransition(
                io.taptalk.TapTalk.R.anim.tap_stay,
                io.taptalk.TapTalk.R.anim.tap_slide_right
            )
        } else {
            hideOTPVerification()
        }
    }

    private fun showOTPVerification() {
        cl_action_bar.visibility = View.GONE
        sv_delete_account.visibility = View.GONE
        fl_container.visibility = View.VISIBLE
//        supportFragmentManager.beginTransaction()
//            .setCustomAnimations(
//                R.animator.tap_slide_left_fragment,
//                R.animator.tap_fade_out_fragment,
//                R.animator.tap_fade_in_fragment,
//                R.animator.tap_slide_right_fragment
//            )
//            .replace(
//                R.id.fl_container,
//                TAPLoginVerificationFragment.getInstance(TAPLoginVerificationFragment.VERIFICATION, TAPChatManager.getInstance(instanceKey).activeUser.phoneWithCode, et_note.text.toString())
//            )
//            .addToBackStack(VERIFICATION_TAG)
//            .commit()
    }

    private fun hideOTPVerification() {
        cl_action_bar.visibility = View.VISIBLE
        sv_delete_account.visibility = View.VISIBLE
        fl_container.visibility = View.GONE
        supportFragmentManager.popBackStack(VERIFICATION_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showLoading() {
        runOnUiThread {
            iv_loading_image.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_loading_image.animation)
                TAPUtils.rotateAnimateInfinitely(this, iv_loading_image)
            tv_loading_text.text = getString(R.string.tap_loading)
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }

    private fun showErrorDialog(message: String?) {
        TapTalkDialog.Builder(this)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(getString(io.taptalk.TapTalk.R.string.tap_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok))
            .setPrimaryButtonListener(true) { }
            .setCancelable(true)
            .show()
    }

    private val checkDeleteAccountStateView = object : TAPDefaultDataView<TapCheckDeleteAccountStateResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoading()
        }

        override fun onSuccess(response: TapCheckDeleteAccountStateResponse?) {
            super.onSuccess(response)
            if (response?.isCanDelete == true) {
                TapTalkDialog.Builder(this@TapDeleteAccountActivity)
                    .setTitle(getString(R.string.tap_delete_my_account))
                    .setMessage(getString(R.string.tap_wording_delete_account))
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setPrimaryButtonTitle(getString(R.string.tap_delete))
                    .setPrimaryButtonListener {
                        showOTPVerification()
                    }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setSecondaryButtonListener {  }
                    .show()
            } else {
                TapTalkDialog.Builder(this@TapDeleteAccountActivity)
                    .setTitle(getString(R.string.tap_wording_oops_only_admin))
                    .setMessage(getString(R.string.tap_wording_sign_to_another_member))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener {  }
                    .show()
            }
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            endLoading()
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            endLoading()
            showErrorDialog(errorMessage)
        }
    }
}