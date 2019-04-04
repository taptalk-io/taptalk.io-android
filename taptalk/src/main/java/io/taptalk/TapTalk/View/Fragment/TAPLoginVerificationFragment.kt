package io.taptalk.TapTalk.View.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*

class TAPLoginVerificationFragment : Fragment() {
    val generalErrorMessage = context?.resources?.getString(R.string.tap_error_message_general)
            ?: ""
    var otpTimer: CountDownTimer? = null
    val waitTime = 30L
    var phoneNumber = "0"
    var otpID = 0L
    var otpKey = ""

    companion object {
        //Arguments Data
        val kPhoneNumberWithCode = "PhoneNumberWithCode"
        val kPhoneNumber = "PhoneNumber"
        val kOTPID = "OTPID"
        val kOTPKey = "OTPKey"

        fun getInstance(otpID: Long, otpKey: String, phoneNumber: String, phoneNumberWithCode: String): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString(kPhoneNumberWithCode, phoneNumberWithCode)
            args.putString(kPhoneNumber, phoneNumber)
            args.putLong(kOTPID, otpID)
            args.putString(kOTPKey, otpKey)
            instance.arguments = args
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_login_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewListener()
    }

    private fun initViewListener() {
        tv_phone_number.text = arguments?.getString(kPhoneNumberWithCode, "") ?: ""
        phoneNumber = arguments?.getString(kPhoneNumber, "0") ?: "0"
        otpID = arguments?.getLong(kOTPID, 0L) ?: 0L
        otpKey = arguments?.getString(kOTPKey, "") ?: ""
        iv_back_button.setOnClickListener {
            activity?.onBackPressed()
        }
        et_otp_code.addTextChangedListener(otpTextWatcher)
        et_otp_code.requestFocus()
        TAPUtils.getInstance().showKeyboard(activity, et_otp_code)
        setAndStartTimer()
        tv_request_otp_again.setOnClickListener {
            showRequestingOTPLoading()
            TapTalk.loginWithRequestOTP(1, phoneNumber, requestOTPInterface)
        }
    }

    private val requestOTPInterface: TAPRequestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean) {
            this@TAPLoginVerificationFragment.otpID = otpID
            this@TAPLoginVerificationFragment.otpKey = otpKey ?: ""
            setAndStartTimer()
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            showDialog("ERROR", errorMessage ?: generalErrorMessage)
        }

    }

    private fun setAndStartTimer() {
        clearOTPEditText()
        tv_didnt_receive_and_invalid.text = resources.getText(R.string.didnt_receive_the_6_digit_otp)
        tv_didnt_receive_and_invalid.setTextColor(resources.getColor(R.color.tap_black_19))
        tv_otp_timer.visibility = View.VISIBLE
        tv_request_otp_again.visibility = View.GONE
        ll_loading_otp.visibility = View.GONE
        iv_progress_otp.clearAnimation()

        otpTimer = object : CountDownTimer(waitTime * 1000, 1000) {
            override fun onFinish() {
                tv_otp_timer.visibility = View.GONE
                ll_loading_otp.visibility = View.GONE
                tv_request_otp_again.visibility = View.VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val minuteLeft = timeLeft / 60
                val secondLeft = timeLeft - (60 * (timeLeft / 60))
                when (minuteLeft) {
                    0L -> {
                        try {
                            if (10 > secondLeft) tv_otp_timer.text = "Wait 0:0$secondLeft"
                            else tv_otp_timer.text = "Wait 0:$secondLeft"
                        } catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                    else -> {
                        try {
                            tv_otp_timer.text = "Wait $minuteLeft:$secondLeft"
                        } catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }.start()
    }

    private fun cancelTimer() {
        otpTimer?.cancel()
    }

    private fun verifyOTP() {
        showVerifyingOTPLoading()
        cancelTimer()
        TapTalk.verifyOTP(otpID, otpKey, et_otp_code.text.toString(), verifyOTPInterface)
    }

    private val verifyOTPInterface = object : TAPVerifyOTPInterface {
        override fun verifyOTPSuccessToLogin() {
            TAPApiManager.getInstance().isLogout = false
            activity?.runOnUiThread {
                val intent = Intent(context, TAPRoomListActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        override fun verifyOTPSuccessToRegister() {
            activity?.runOnUiThread {
                Toast.makeText(context, "Register", Toast.LENGTH_SHORT).show()
            }
        }

        override fun verifyOTPFailed(errorCode: String?, errorMessage: String?) {
            tv_didnt_receive_and_invalid.text = resources.getText(R.string.invalid_otp)
            tv_didnt_receive_and_invalid.setTextColor(resources.getColor(R.color.tap_watermelon_red))
            tv_request_otp_again.visibility = View.VISIBLE
            ll_loading_otp.visibility = View.GONE
            tv_otp_timer.visibility = View.GONE
        }

    }

    private fun clearOTPEditText() {
        v_pointer_1.visibility = View.VISIBLE
        v_pointer_2.visibility = View.VISIBLE
        v_pointer_3.visibility = View.VISIBLE
        v_pointer_4.visibility = View.VISIBLE
        v_pointer_5.visibility = View.VISIBLE
        v_pointer_6.visibility = View.VISIBLE

        iv_otp_filled_1.visibility = View.INVISIBLE
        iv_otp_filled_2.visibility = View.INVISIBLE
        iv_otp_filled_3.visibility = View.INVISIBLE
        iv_otp_filled_4.visibility = View.INVISIBLE
        iv_otp_filled_5.visibility = View.INVISIBLE
        iv_otp_filled_6.visibility = View.INVISIBLE
        et_otp_code.setText("")
    }

    val otpTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s?.length) {
                1 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.VISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.INVISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                2 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                3 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                4 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                5 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.INVISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.VISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
                6 -> {
                    v_pointer_1.visibility = View.INVISIBLE
                    v_pointer_2.visibility = View.INVISIBLE
                    v_pointer_3.visibility = View.INVISIBLE
                    v_pointer_4.visibility = View.INVISIBLE
                    v_pointer_5.visibility = View.INVISIBLE
                    v_pointer_6.visibility = View.INVISIBLE

                    iv_otp_filled_1.visibility = View.VISIBLE
                    iv_otp_filled_2.visibility = View.VISIBLE
                    iv_otp_filled_3.visibility = View.VISIBLE
                    iv_otp_filled_4.visibility = View.VISIBLE
                    iv_otp_filled_5.visibility = View.VISIBLE
                    iv_otp_filled_6.visibility = View.VISIBLE

                    verifyOTP()
                }
                else -> {
                    v_pointer_1.visibility = View.VISIBLE
                    v_pointer_2.visibility = View.VISIBLE
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_6.visibility = View.VISIBLE

                    iv_otp_filled_1.visibility = View.INVISIBLE
                    iv_otp_filled_2.visibility = View.INVISIBLE
                    iv_otp_filled_3.visibility = View.INVISIBLE
                    iv_otp_filled_4.visibility = View.INVISIBLE
                    iv_otp_filled_5.visibility = View.INVISIBLE
                    iv_otp_filled_6.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener {

                }.show()
    }

    private fun showRequestingOTPLoading() {
        tv_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.requesting_otp)
        TAPUtils.getInstance().rotateAnimateInfinitely(context, iv_progress_otp)
    }

    private fun showVerifyingOTPLoading() {
        tv_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.verifying_otp)
        TAPUtils.getInstance().rotateAnimateInfinitely(context, iv_progress_otp)
    }
}
