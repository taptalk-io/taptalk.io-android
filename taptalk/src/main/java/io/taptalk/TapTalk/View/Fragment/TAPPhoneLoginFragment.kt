package io.taptalk.TapTalk.View.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_phone_login.*

class TAPPhoneLoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_phone_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        et_phone_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textCount = s?.length ?: 0
                when (textCount) {
                    in 7..15 -> {
                        fl_continue_btn.background = resources.getDrawable(R.drawable.tap_bg_gradient_ff9833_ff7e00_rounded_8dp_stroke_ff7e00_1dp)
                        fl_continue_btn.setOnClickListener { attemptLogin() }
                        fl_continue_btn.isClickable = true
                    }
                    else -> {
                        fl_continue_btn.background = resources.getDrawable(R.drawable.tap_bg_gradient_cecece_9b9b9b_rounded_8dp_stroke_cecece_1dp)
                        fl_continue_btn.setOnClickListener(null)
                        fl_continue_btn.isClickable = false
                    }
                }
            }
        })

        et_phone_number.setOnEditorActionListener { v, actionId, event ->
            when (v?.length() ?: 0) {
                in 7..15 -> {
                    attemptLogin()
                }
            }
            false
        }

        et_phone_number.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                fl_phone_number.setBackgroundResource(R.drawable.tap_bg_white_rounded_8dp_stroke_362ad7_1dp)
            } else {
                fl_phone_number.setBackgroundResource(R.drawable.tap_bg_rounded_8dp_stroke_dcdcdc_1dp)
            }
        }
    }

    private fun attemptLogin() {
        TAPUtils.getInstance().dismissKeyboard(activity)
        showProgress()
        checkNumberAndCallAPI()
    }

    private fun checkNumberAndCallAPI() {
        var phoneNumber = et_phone_number.text.toString()
        when {
            '0' == phoneNumber.elementAt(0) -> phoneNumber = phoneNumber.replaceFirst("0", "")
            "+62" == phoneNumber.substring(0, 3) -> phoneNumber = phoneNumber.substring(3)
            "62" == phoneNumber.substring(0, 2) -> phoneNumber = phoneNumber.substring(2)
        }

        TapTalk.loginWithRequestOTP(1, phoneNumber, requestOTPInterface)
    }

    private fun showProgress() {
        tv_btn_continue.visibility = View.GONE
        iv_loading_progress.visibility = View.VISIBLE
        TAPUtils.getInstance().rotateAnimateInfinitely(context, iv_loading_progress)
    }

    private fun stopAndHideProgress() {
        tv_btn_continue.visibility = View.VISIBLE
        iv_loading_progress.visibility = View.GONE
        iv_loading_progress.clearAnimation()
    }

    private val requestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean) {
            stopAndHideProgress()
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            showDialog("ERROR", errorMessage ?: "Oops! something wrong, please try again later")
        }
    }

    private fun showDialog(title: String, message: String) {
        TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener {
                    stopAndHideProgress()
                }.show()
    }
}
