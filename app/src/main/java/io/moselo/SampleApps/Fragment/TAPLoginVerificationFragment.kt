package io.moselo.SampleApps.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.moselo.SampleApps.Activity.TAPLoginActivity
import io.moselo.SampleApps.Activity.TAPRegisterActivity
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.AnalyticsManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*

class TAPLoginVerificationFragment : androidx.fragment.app.Fragment() {
    val generalErrorMessage = context?.resources?.getString(R.string.tap_error_message_general)
            ?: ""
    var otpTimer: CountDownTimer? = null
    var waitTime = 120L * 1000
    var phoneNumber = "0"
    var otpID = 0L
    var otpKey = ""
    var countryID = 0
    var countryCallingCode = ""
    var countryFlagUrl = ""

    companion object {
        //Arguments Data
        val kPhoneNumberWithCode = "PhoneNumberWithCode"
        val kPhoneNumber = "PhoneNumber"
        val kOTPID = "OTPID"
        val kOTPKey = "OTPKey"
        val kCountryID = "CountryID"
        val kCountryCallingCode = "CountryCallingCode"
        val kCountryFlagUrl = "CountryFlagUrl"

        fun getInstance(otpID: Long, otpKey: String, phoneNumber: String, phoneNumberWithCode: String, countryID: Int, countryCallingCode: String, countryFlagUrl: String): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString(kPhoneNumberWithCode, phoneNumberWithCode)
            args.putString(kPhoneNumber, phoneNumber)
            args.putLong(kOTPID, otpID)
            args.putString(kOTPKey, otpKey)
            args.putInt(kCountryID, countryID)
            args.putString(kCountryCallingCode, countryCallingCode)
            args.putString(kCountryFlagUrl, countryFlagUrl)
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

    override fun onResume() {
        super.onResume()
        setupTimer()
    }

    override fun onStop() {
        super.onStop()
        cancelTimer()
    }

    private fun initViewListener() {
        tv_phone_number.text = arguments?.getString(kPhoneNumberWithCode, "") ?: ""
        phoneNumber = arguments?.getString(kPhoneNumber, "0") ?: "0"
        otpID = arguments?.getLong(kOTPID, 0L) ?: 0L
        otpKey = arguments?.getString(kOTPKey, "") ?: ""
        countryID = arguments?.getInt(kCountryID) ?: 0
        countryCallingCode = arguments?.getString(kCountryCallingCode, "") ?: ""
        countryFlagUrl = arguments?.getString(kCountryFlagUrl, "") ?: ""
        TAPUtils.animateClickButton(iv_back_button, 0.95f)
        iv_back_button.setOnClickListener { activity?.onBackPressed() }
        et_otp_code.addTextChangedListener(otpTextWatcher)
        et_otp_code.requestFocus()
        TAPUtils.showKeyboard(activity, et_otp_code)
        clearOTPEditText()

        setupTimer()

        tv_request_otp_again.setOnClickListener {
            showRequestingOTPLoading()
            TAPDataManager.getInstance().requestOTPLogin(countryID, phoneNumber, object : TAPDefaultDataView<TAPLoginOTPResponse>() {
                override fun onSuccess(response: TAPLoginOTPResponse) {
                    super.onSuccess(response)
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
                    AnalyticsManager.getInstance().trackEvent("Resend OTP Success", additional)
                    requestOTPInterface.onRequestSuccess(response.otpID, response.otpKey, response.phoneWithCode, response.isSuccess)
                }

                override fun onError(error: TAPErrorModel) {
                    super.onError(error)
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
                    AnalyticsManager.getInstance().trackEvent("Resend OTP Failed", additional)
                    requestOTPInterface.onRequestFailed(error.message, error.code)
                }

                override fun onError(errorMessage: String) {
                    requestOTPInterface.onRequestFailed(errorMessage, "400")
                }
            })
        }
    }

    private val requestOTPInterface: TAPRequestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean) {
            val loginActivity = activity as TAPLoginActivity
            this@TAPLoginVerificationFragment.otpID = otpID
            loginActivity.vm.otpID = otpID
            this@TAPLoginVerificationFragment.otpKey = otpKey ?: ""
            loginActivity.vm.otpKey = otpKey
            resendOtpSuccessMessage()

            Handler().postDelayed({ setAndStartTimer(waitTime) }, 2000)
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            showDialog(getString(R.string.tap_error), errorMessage ?: generalErrorMessage)
        }
    }

    private fun resendOtpSuccessMessage() {
        clearOTPEditText()
        tv_request_otp_again.visibility = View.GONE
        ll_loading_otp.visibility = View.GONE
        ll_otp_sent.visibility = View.VISIBLE
        iv_progress_otp.clearAnimation()
    }

    private fun setupTimer() {
        val lastLoginTimestamp = (activity as TAPLoginActivity).vm.lastLoginTimestamp
        if (0L != lastLoginTimestamp && (System.currentTimeMillis() - lastLoginTimestamp) < waitTime) {
            // Resume timer with remaining time
            setAndStartTimer(waitTime - (System.currentTimeMillis() - lastLoginTimestamp))
        } else if (null != otpTimer) {
            // Finish timer
            otpTimer!!.onFinish()
        } else {
            // Start timer from beginning
            setAndStartTimer(waitTime)
        }
    }

    private fun setAndStartTimer(waitTime: Long) {
        if (null != tv_didnt_receive_and_invalid && null != tv_otp_timer && null != tv_request_otp_again
                && null != ll_loading_otp && null != ll_otp_sent && null != iv_progress_otp) {
            tv_didnt_receive_and_invalid.text = resources.getText(R.string.tap_didnt_receive_the_6_digit_otp)
            tv_didnt_receive_and_invalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
            tv_otp_timer.visibility = View.VISIBLE
            tv_request_otp_again.visibility = View.GONE
            ll_loading_otp.visibility = View.GONE
            ll_otp_sent.visibility = View.GONE
            iv_progress_otp.clearAnimation()
            otpTimer?.cancel()

            otpTimer = object : CountDownTimer(waitTime, 1000) {
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

            if (this.waitTime == waitTime) {
                (activity as TAPLoginActivity).vm.lastLoginTimestamp = System.currentTimeMillis()
            }
        }
    }

    private fun cancelTimer() {
        otpTimer?.cancel()
    }

    private fun verifyOTP() {
        showVerifyingOTPLoading()
        cancelTimer()
        TAPDataManager.getInstance().verifyOTPLogin(otpID, otpKey, et_otp_code.text.toString(), object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
            override fun onSuccess(response: TAPLoginOTPVerifyResponse) {
                if (response.isRegistered) {
                    AnalyticsManager.getInstance().identifyUser()
                    AnalyticsManager.getInstance().trackEvent("Login Success")
                    TapTalk.authenticateWithAuthTicket(response.ticket, true, object : TapCommonListener() {
                        override fun onSuccess(successMessage: String) {
                            AnalyticsManager.getInstance().identifyUser()
                            AnalyticsManager.getInstance().trackEvent("Authenticate TapTalk.io Success")
                            verifyOTPInterface.verifyOTPSuccessToLogin()
                        }

                        override fun onError(errorCode: String, errorMessage: String) {
                            AnalyticsManager.getInstance().trackErrorEvent("Authenticate TapTalk.io Failed", errorCode, errorMessage)
                            verifyOTPInterface.verifyOTPFailed(errorCode, errorMessage)
                        }
                    })
                } else {
                    AnalyticsManager.getInstance().trackEvent("Login Success and Continue Register")
                    verifyOTPInterface.verifyOTPSuccessToRegister()
                }
            }

            override fun onError(error: TAPErrorModel) {
                AnalyticsManager.getInstance().trackErrorEvent("Login Failed", error.code, error.message)
                verifyOTPInterface.verifyOTPFailed(error.message, error.code)
            }

            override fun onError(errorMessage: String) {
                verifyOTPInterface.verifyOTPFailed(errorMessage, "400")
            }
        })
    }

    private val verifyOTPInterface = object : TAPVerifyOTPInterface {
        override fun verifyOTPSuccessToLogin() {
            TAPApiManager.getInstance().isLoggedOut = false
            activity?.runOnUiThread {
                TAPDataManager.getInstance().saveMyCountryCode(countryCallingCode)
                TAPDataManager.getInstance().saveMyCountryFlagUrl(countryFlagUrl)
                val intent = Intent(context, TapUIRoomListActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        override fun verifyOTPSuccessToRegister() {
            activity?.runOnUiThread {
                TAPRegisterActivity.start(
                        activity!!,
                        (activity as TAPLoginActivity).instanceKey,
                        countryID,
                        countryCallingCode,
                        countryFlagUrl,
                        phoneNumber)
                (activity as TAPLoginActivity).initFirstPage()
            }
        }

        override fun verifyOTPFailed(errorCode: String?, errorMessage: String?) {
            tv_didnt_receive_and_invalid.text = resources.getText(R.string.tap_error_invalid_otp)
            tv_didnt_receive_and_invalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
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
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_6.visibility = View.VISIBLE
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_6.visibility = View.VISIBLE
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_6.visibility = View.VISIBLE
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_6.visibility = View.VISIBLE
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

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
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_2.visibility = View.VISIBLE
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_3.visibility = View.VISIBLE
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_4.visibility = View.VISIBLE
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_5.visibility = View.VISIBLE
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                    v_pointer_6.visibility = View.VISIBLE
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                .setPrimaryButtonListener {}
                .show()
    }

    private fun showRequestingOTPLoading() {
        tv_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.tap_requesting_otp)
        TAPUtils.rotateAnimateInfinitely(context, iv_progress_otp)
    }

    private fun showVerifyingOTPLoading() {
        tv_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.tap_verifying_otp)
        TAPUtils.rotateAnimateInfinitely(context, iv_progress_otp)
    }
}
