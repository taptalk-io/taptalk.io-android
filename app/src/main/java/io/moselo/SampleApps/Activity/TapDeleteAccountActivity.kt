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
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapCheckDeleteAccountStateResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.ViewModel.TapDeleteAccountViewModel
import io.taptalk.TapTalkSample.R
import io.taptalk.TapTalkSample.databinding.TapActivityDeleteAccountBinding

class TapDeleteAccountActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityDeleteAccountBinding

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
        vb = TapActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(vb.root)

        val spannable = SpannableString(vb.tvSubtitle.text)
        spannable.setSpan(
            TypefaceSpan("sans-serif"),
            35,
            vb.tvSubtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapActionSheetDestructiveLabelColor)),
            35,
            vb.tvSubtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            35,
            vb.tvSubtitle.text.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        vb.tvSubtitle.text = spannable

        vb.etNote.setOnTouchListener { v: View, event: MotionEvent ->
            if (vb.etNote.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_SCROLL) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }

        vb.cbAgree.setOnCheckedChangeListener { _, isChecked ->
            vb.btnDelete.isEnabled = isChecked
        }

        vb.btnDelete.setOnClickListener {
            TAPDataManager.getInstance(instanceKey).checkDeleteAccountState(checkDeleteAccountStateView)
        }

        vb.ivButtonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isEmpty()) {
            try {
                super.onBackPressed()
                overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_right)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else {
            if (!vm.isLoading) {
                if (vm.isDeleted) {
                    TAPLoginActivity.start(this, instanceKey)
                }
                else {
                    hideOTPVerification()
                }
            }
        }
    }

    private fun hideOTPVerification() {
        vb.clActionBar.visibility = View.VISIBLE
        vb.svDeleteAccount.visibility = View.VISIBLE
        vb.flContainer.visibility = View.GONE
        supportFragmentManager.popBackStack(VERIFICATION_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showLoading() {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_loading_progress_circle_white))
            if (null == vb.layoutPopupLoadingScreen.ivLoadingImage.animation)
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutPopupLoadingScreen.ivLoadingImage)
            vb.layoutPopupLoadingScreen.tvLoadingText.text = getString(io.taptalk.TapTalk.R.string.tap_loading)
            vb.layoutPopupLoadingScreen.flLoading.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        vb.layoutPopupLoadingScreen.flLoading.visibility = View.GONE
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
                    .setTitle(getString(io.taptalk.TapTalk.R.string.tap_delete_my_account))
                    .setMessage(getString(io.taptalk.TapTalk.R.string.tap_wording_delete_account))
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_delete))
                    .setPrimaryButtonListener {
                        TAPDataManager.getInstance(instanceKey).requestDeleteAccountOtp("", requestOtpView)
                    }
                    .setSecondaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_cancel))
                    .setSecondaryButtonListener {  }
                    .show()
            } else {
                TapTalkDialog.Builder(this@TapDeleteAccountActivity)
                    .setTitle(getString(io.taptalk.TapTalk.R.string.tap_wording_oops_only_admin))
                    .setMessage(getString(io.taptalk.TapTalk.R.string.tap_wording_sign_to_another_member))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok))
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

    private val requestOtpView = object : TAPDefaultDataView<TAPOTPResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoading()

        }

        override fun onSuccess(response: TAPOTPResponse?) {
            super.onSuccess(response)
            // show verify otp
            vb.clActionBar.visibility = View.GONE
            vb.svDeleteAccount.visibility = View.GONE
            vb.flContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    io.taptalk.TapTalk.R.animator.tap_slide_left_fragment,
                    io.taptalk.TapTalk.R.animator.tap_fade_out_fragment,
                    io.taptalk.TapTalk.R.animator.tap_fade_in_fragment,
                    io.taptalk.TapTalk.R.animator.tap_slide_right_fragment
                )
                .replace(
                    R.id.fl_container,
                    TAPLoginVerificationFragment.getInstance(TAPLoginVerificationFragment.VERIFICATION, TAPChatManager.getInstance(instanceKey).activeUser.phoneWithCode, response?.otpID ?: 0L, response?.otpKey, response?.nextRequestSeconds ?: 30, response?.channel, vb.etNote.text.toString())
                )
                .addToBackStack(VERIFICATION_TAG)
                .commit()
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
