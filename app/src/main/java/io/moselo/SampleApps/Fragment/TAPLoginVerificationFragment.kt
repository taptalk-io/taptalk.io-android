package io.moselo.SampleApps.Fragment

import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.moselo.SampleApps.Activity.TAPLoginActivity
import io.moselo.SampleApps.Activity.TAPRegisterActivity
import io.moselo.SampleApps.Activity.TapDeleteAccountActivity
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.AnalyticsManager
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*

class TAPLoginVerificationFragment : androidx.fragment.app.Fragment() {
    val generalErrorMessage = context?.resources?.getString(R.string.tap_error_message_general)
            ?: ""
    var otpTimer: CountDownTimer? = null

    var waitTime = 30L * 1000
    var phoneNumber = "0"
    var phoneNumberWithCode = "0"
    var otpID = 0L
    var otpKey = ""
    var countryID = 0
    var countryCallingCode = ""
    var countryFlagUrl = ""
    var isOtpInvalid = false //to check state UI OTP invalid
    var isFromBtnSendViaSMS = false //to check for update UI if call otp request from button send via sms
    var channel = "sms" //to check channel otp type sended
    var isTimerFinished = false; //to check if timer already finished
    var note = ""
    var otpType = LOGIN

    companion object {
        //Arguments Data
        val kPhoneNumberWithCode = "PhoneNumberWithCode"
        val kPhoneNumber = "PhoneNumber"
        val kOTPID = "OTPID"
        val kOTPKey = "OTPKey"
        val kCountryID = "CountryID"
        val kCountryCallingCode = "CountryCallingCode"
        val kCountryFlagUrl = "CountryFlagUrl"
        val kChannel = "Channel"
        val kWaitTime = "kWaitTime"
        val kType = "kType"
        const val LOGIN = "loginType"
        const val VERIFICATION = "verificationType"
        const val kNote = "kNote"

        fun getInstance(otpID: Long, otpKey: String, phoneNumber: String, phoneNumberWithCode: String, countryID: Int, countryCallingCode: String, countryFlagUrl: String, channel: String, waitTime: Int): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString(kPhoneNumberWithCode, phoneNumberWithCode)
            args.putString(kPhoneNumber, phoneNumber)
            args.putLong(kOTPID, otpID)
            args.putString(kOTPKey, otpKey)
            args.putInt(kCountryID, countryID)
            args.putString(kCountryCallingCode, countryCallingCode)
            args.putString(kCountryFlagUrl, countryFlagUrl)
            args.putString(kChannel, channel)
            args.putLong(kWaitTime, waitTime * 1000L)
            instance.arguments = args
            return instance
        }

        fun getInstance(type: String, phoneNumberWithCode: String?, otpID: Long, otpKey: String?, waitTime: Int, channel: String?, note: String?): TAPLoginVerificationFragment {
            val instance = TAPLoginVerificationFragment()
            val args = Bundle()
            args.putString(kPhoneNumberWithCode, phoneNumberWithCode)
            args.putLong(kOTPID, otpID)
            args.putString(kOTPKey, otpKey)
            args.putString(kType, type)
            args.putString(kChannel, channel)
            args.putLong(kWaitTime, waitTime * 1000L)
            args.putString(kNote, note)
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
        channel = arguments?.getString(kChannel, "sms") ?: "sms"
        phoneNumberWithCode = arguments?.getString(kPhoneNumberWithCode, "") ?: ""
        phoneNumber = arguments?.getString(kPhoneNumber, "0") ?: "0"
        otpID = arguments?.getLong(kOTPID, 0L) ?: 0L
        otpKey = arguments?.getString(kOTPKey, "") ?: ""
        countryID = arguments?.getInt(kCountryID) ?: 0
        countryCallingCode = arguments?.getString(kCountryCallingCode, "") ?: ""
        countryFlagUrl = arguments?.getString(kCountryFlagUrl, "") ?: ""
        waitTime = arguments?.getLong(kWaitTime, 30L * 1000) ?: 30L * 1000
        otpType = arguments?.getString(kType, LOGIN) ?: LOGIN
        note = arguments?.getString(kNote, "") ?: ""
        if (otpType == VERIFICATION) {
            tv_toolbar_title.visibility = View.VISIBLE
        } else {
            tv_toolbar_title.visibility = View.GONE
        }
        setTextandImageBasedOnOTPMethod(channel)

        TAPUtils.animateClickButton(iv_back_button, 0.95f)
        iv_back_button.setOnClickListener { activity?.onBackPressed() }
        et_otp_code.addTextChangedListener(otpTextWatcher)
        et_otp_code.requestFocus()
        TAPUtils.showKeyboard(activity, et_otp_code)
        clearOTPEditText()

        setupTimer()

        ll_request_otp_again.setOnClickListener {
            showRequestingOTPLoading()
            hideBtnSendViaSMS()
            if (otpType == LOGIN) {
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey)
                    .requestOTPLogin(
                        countryID,
                        phoneNumber,
                        channel,
                        object : TAPDefaultDataView<TAPOTPResponse>() {
                            override fun onSuccess(response: TAPOTPResponse) {
                                super.onSuccess(response)
                                et_otp_code.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumber)
                                additional.put("countryCode", countryID.toString())
                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                    .trackEvent("Resend OTP Success", additional)
                                requestOTPInterface.onRequestSuccess(
                                    response.otpID,
                                    response.otpKey,
                                    response.phoneWithCode,
                                    response.isSuccess,
                                    response.channel,
                                    response.message,
                                    response.nextRequestSeconds,
                                    response.whatsAppFailureReason
                                )
                            }

                            override fun onError(error: TAPErrorModel) {
                                super.onError(error)
                                et_otp_code.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumber)
                                additional.put("countryCode", countryID.toString())
                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                    .trackEvent("Resend OTP Failed", additional)
                                requestOTPInterface.onRequestFailed(error.message, error.code)
                            }

                            override fun onError(errorMessage: String) {
                                et_otp_code.isEnabled = true
                                requestOTPInterface.onRequestFailed(errorMessage, "400")
                            }
                        })
            } else {
                requestDeleteAccountOtp(channel)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ll_btn_send_via_sms.background = ContextCompat.getDrawable(requireContext(), R.drawable.tap_bg_button_border_ripple)
        }

        ll_btn_send_via_sms.setOnClickListener {
            isFromBtnSendViaSMS = true
            showPopupLoading(getString(R.string.tap_loading))
            showRequestingOTPLoading()
            if (otpType == LOGIN) {
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey)
                    .requestOTPLogin(
                        countryID,
                        phoneNumber,
                        "sms",
                        object : TAPDefaultDataView<TAPOTPResponse>() {
                            override fun onSuccess(response: TAPOTPResponse) {
                                et_otp_code.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumberWithCode)
                                additional.put("countryCode", countryID.toString())
                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                    .trackEvent("Request OTP Success", additional)
                                super.onSuccess(response)
                                requestOTPInterface.onRequestSuccess(
                                    response.otpID,
                                    response.otpKey,
                                    response.phoneWithCode,
                                    response.isSuccess,
                                    response.channel,
                                    response.message,
                                    response.nextRequestSeconds,
                                    response.whatsAppFailureReason
                                )
                            }

                            override fun onError(error: TAPErrorModel) {
                                et_otp_code.isEnabled = true
                                super.onError(error)
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumberWithCode)
                                additional.put("countryCode", countryID.toString())
                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                    .trackErrorEvent(
                                        "Request OTP Failed",
                                        error.code,
                                        error.message,
                                        additional
                                    )
                                requestOTPInterface.onRequestFailed(error.message, error.code)
                            }

                            override fun onError(errorMessage: String?) {
                                et_otp_code.isEnabled = true
                                super.onError(errorMessage)
                                requestOTPInterface.onRequestFailed(errorMessage, "400")
                            }
                        })
            } else {
                requestDeleteAccountOtp("sms")
            }
        }
    }

    private val requestOTPInterface: TAPRequestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean, channel: String, message: String, nextRequestSeconds: Int, whatsAppFailureReason: String) {
            if (succeess) {
                if (otpType == LOGIN) {
                    val loginActivity = activity as TAPLoginActivity
                    loginActivity.vm.otpID = otpID
                    loginActivity.vm.otpKey = otpKey
                }
                this@TAPLoginVerificationFragment.otpID = otpID
                this@TAPLoginVerificationFragment.otpKey = otpKey ?: ""
                resendOtpSuccessMessage()

                //update UI based on channel
                if (isFromBtnSendViaSMS) {
                    hidePopupLoading()
                    this@TAPLoginVerificationFragment.channel = channel
                    setTextandImageBasedOnOTPMethod(channel)
                    isFromBtnSendViaSMS = false
                }

                waitTime = nextRequestSeconds * 1000L
                setAndStartTimer(waitTime)
                et_otp_code.requestFocus()
                TAPUtils.showKeyboard(activity, et_otp_code)
                Handler().postDelayed({ showTimer() }, 2000)
            } else {
                showRequestAgain()
                if (isFromBtnSendViaSMS) {
                    hidePopupLoading()
                    isFromBtnSendViaSMS = false
                } else {
                    showBtnSendViaSMS()
                }
                if (whatsAppFailureReason == "") {
                    showDialog(getString(R.string.tap_error), message)
                } else {
                    showDialog(getString(R.string.tap_currently_unavailable), getString(R.string.tap_error_we_are_experiencing_some_issues))
                }
            }
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            if (isFromBtnSendViaSMS) {
                hidePopupLoading()
                isFromBtnSendViaSMS = false
            } else {
                showBtnSendViaSMS()
            }
            showRequestAgain()
            if (TAPNetworkStateManager.getInstance("").hasNetworkConnection(context)) {
                showDialog(getString(R.string.tap_error), errorMessage
                        ?: getString(R.string.tap_error_we_are_experiencing_some_issues))
            }else {
                TAPUtils.showNoInternetErrorDialog(context)
            }
        }
    }

    private fun requestDeleteAccountOtp(channel: String) {
        TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).requestDeleteAccountOtp(channel,
            object : TAPDefaultDataView<TAPOTPResponse>() {
                override fun onSuccess(response: TAPOTPResponse) {
                    super.onSuccess(response)
                    et_otp_code.isEnabled = true
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                        .trackEvent("Resend OTP Success", additional)
                    requestOTPInterface.onRequestSuccess(
                        response.otpID,
                        response.otpKey,
                        response.phoneWithCode,
                        response.isSuccess,
                        response.channel,
                        response.message,
                        response.nextRequestSeconds,
                        response.whatsAppFailureReason
                    )
                }

                override fun onError(error: TAPErrorModel?) {
                    super.onError(error)
                    et_otp_code.isEnabled = true
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                        .trackEvent("Resend OTP Failed", additional)
                    requestOTPInterface.onRequestFailed(error?.message, error?.code)
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    et_otp_code.isEnabled = true
                    requestOTPInterface.onRequestFailed(errorMessage, "400")
                }
            })
    }

    private fun resendOtpSuccessMessage() {
        clearOTPEditText()
        ll_request_otp_again.visibility = View.GONE
        ll_loading_otp.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        ll_otp_sent.visibility = View.VISIBLE
        iv_progress_otp.clearAnimation()
    }

    private fun setupTimer() {
        val lastLoginTimestamp =
            when (otpType) {
                LOGIN -> (activity as TAPLoginActivity).vm.lastLoginTimestamp
                else -> (activity as TapDeleteAccountActivity).vm.lastLoginTimestamp
            }
        if (0L != lastLoginTimestamp && (System.currentTimeMillis() - lastLoginTimestamp) < waitTime) {
            // Resume timer with remaining time
            showTimer()
            setAndStartTimer(waitTime - (System.currentTimeMillis() - lastLoginTimestamp))
        } else if (null != otpTimer) {
            // Finish timer
            otpTimer!!.onFinish()
        } else {
            // Start timer from beginning
            showTimer()
            setAndStartTimer(waitTime)
        }
    }

    private fun setAndStartTimer(waitTime: Long) {

        if (null != tv_didnt_receive_and_invalid) {
            tv_didnt_receive_and_invalid.text = resources.getText(R.string.tap_didnt_receive_the_6_digit_otp)
            tv_didnt_receive_and_invalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
        }

        otpTimer?.cancel()

        otpTimer = object : CountDownTimer(waitTime, 1000) {
            override fun onFinish() {
                showRequestAgain()
                showBtnSendViaSMS()
                isTimerFinished = true;
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
            if (otpType == LOGIN) {
                (activity as TAPLoginActivity).vm.lastLoginTimestamp = System.currentTimeMillis()
                (activity as TAPLoginActivity).vm.waitTimeRequestOtp = (waitTime / 1000).toInt()
            } else {
                (activity as TapDeleteAccountActivity).vm.lastLoginTimestamp = System.currentTimeMillis()
                (activity as TapDeleteAccountActivity).vm.waitTimeRequestOtp = (waitTime / 1000).toInt()
            }
        }

        if (isTimerFinished) {
            isTimerFinished = false
        }
    }

    private fun cancelTimer() {
        otpTimer?.cancel()
    }

    private fun verifyOTP() {
        showVerifyingOTPLoading()
        if (otpType == LOGIN) {
            TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).verifyOTPLogin(
                otpID,
                otpKey,
                et_otp_code.text.toString(),
                object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                    override fun onSuccess(response: TAPLoginOTPVerifyResponse) {
                        et_otp_code.isEnabled = true
                        if (response.isRegistered) {
                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                .identifyUser()
                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                .trackEvent("Login Success")
                            TapTalk.authenticateWithAuthTicket(
                                (activity as TAPBaseActivity).instanceKey,
                                response.ticket,
                                true,
                                object : TapCommonListener() {
                                    override fun onSuccess(successMessage: String) {
                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                            .identifyUser()
                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                            .trackEvent("Authenticate TapTalk.io Success")
                                        verifyOTPInterface.verifyOTPSuccessToLogin()
                                    }

                                    override fun onError(errorCode: String, errorMessage: String) {
                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                            .trackErrorEvent(
                                                "Authenticate TapTalk.io Failed",
                                                errorCode,
                                                errorMessage
                                            )
                                        verifyOTPInterface.verifyOTPFailed(errorCode, errorMessage)
                                    }
                                })
                        } else {
                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                                .trackEvent("Login Success and Continue Register")
                            verifyOTPInterface.verifyOTPSuccessToRegister()
                        }

                    }

                    override fun onError(error: TAPErrorModel) {
                        et_otp_code.isEnabled = true
                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                            .trackErrorEvent("Login Failed", error.code, error.message)
                        verifyOTPInterface.verifyOTPFailed(error.message, error.code)
                    }

                    override fun onError(errorMessage: String) {
                        et_otp_code.isEnabled = true
                        verifyOTPInterface.verifyOTPFailed(errorMessage, "400")
                    }
                })
        } else {
            (activity as TapDeleteAccountActivity).vm.isLoading = true
            TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).verifyOtpDeleteAccount(
                otpID,
                otpKey,
                et_otp_code.text.toString(),
                note,
                object : TAPDefaultDataView<TAPCommonResponse>() {
                    override fun onSuccess(response: TAPCommonResponse) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        (activity as TapDeleteAccountActivity).vm.isDeleted = true
                        fl_otp.visibility = View.GONE
                        ll_loading_otp.visibility = View.GONE
                        tv_didnt_receive_and_invalid.text = getString(R.string.tap_verification_succeed)
                        tv_didnt_receive_and_invalid.setTextColor(ContextCompat.getColor(requireContext(), R.color.tapTransparentBlack1980))
                        iv_check.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            TapTalk.logoutAndClearAllTapTalkData((activity as TAPBaseActivity).instanceKey)
                        }, 3000L)
                    }

                    override fun onError(error: TAPErrorModel) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        et_otp_code.isEnabled = true
                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey)
                            .trackErrorEvent("Login Failed", error.code, error.message)
                        verifyOTPInterface.verifyOTPFailed(error.message, error.code)
                    }

                    override fun onError(errorMessage: String) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        et_otp_code.isEnabled = true
                        verifyOTPInterface.verifyOTPFailed(errorMessage, "400")
                    }
                })
        }
    }

    private val verifyOTPInterface = object : TAPVerifyOTPInterface {
        override fun verifyOTPSuccessToLogin() {
            TAPApiManager.getInstance((activity as TAPBaseActivity).instanceKey).isLoggedOut = false
            activity?.runOnUiThread {
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveMyCountryCode(countryCallingCode)
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveMyCountryFlagUrl(countryFlagUrl)
                TapUIRoomListActivity.start(context, (activity as TAPBaseActivity).instanceKey)
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
                //set phonenumber and countryID in viewmodel to default state
                (activity as TAPLoginActivity).vm.phoneNumber = "0"
                (activity as TAPLoginActivity).vm.countryID = 0
                activity?.onBackPressed()
            }
        }

        override fun verifyOTPFailed(errorCode: String?, errorMessage: String?) {
            tv_didnt_receive_and_invalid.text = resources.getText(R.string.tap_error_invalid_otp)
            tv_didnt_receive_and_invalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_1.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_2.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_3.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_4.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_5.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            tv_otp_filled_6.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))

            if (isTimerFinished) {
                showRequestAgain()
            } else {
                showTimer()
            }
            isOtpInvalid = true
            et_otp_code.requestFocus()
            TAPUtils.showKeyboard(activity, et_otp_code)


        }
    }

    private fun clearOTPEditText() {
        v_pointer_1.visibility = View.VISIBLE
        v_pointer_2.visibility = View.VISIBLE
        v_pointer_3.visibility = View.VISIBLE
        v_pointer_4.visibility = View.VISIBLE
        v_pointer_5.visibility = View.VISIBLE
        v_pointer_6.visibility = View.VISIBLE

        tv_otp_filled_1.text = ""
        tv_otp_filled_2.text = ""
        tv_otp_filled_3.text = ""
        tv_otp_filled_4.text = ""
        tv_otp_filled_5.text = ""
        tv_otp_filled_6.text = ""
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
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))

                    tv_otp_filled_1.text = String.format("%s", s[0])
                    tv_otp_filled_2.text = ""
                    tv_otp_filled_3.text = ""
                    tv_otp_filled_4.text = ""
                    tv_otp_filled_5.text = ""
                    tv_otp_filled_6.text = ""
                }
                2 -> {
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))

                    tv_otp_filled_2.text = String.format("%s", s[1])
                    tv_otp_filled_3.text = ""
                    tv_otp_filled_4.text = ""
                    tv_otp_filled_5.text = ""
                    tv_otp_filled_6.text = ""
                }
                3 -> {
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))

                    tv_otp_filled_3.text = String.format("%s", s[2])
                    tv_otp_filled_4.text = ""
                    tv_otp_filled_5.text = ""
                    tv_otp_filled_6.text = ""
                }
                4 -> {
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))

                    tv_otp_filled_4.text = String.format("%s", s[3])
                    tv_otp_filled_5.text = ""
                    tv_otp_filled_6.text = ""
                }
                5 -> {
                    if (isOtpInvalid) {
                        tv_otp_filled_1.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        tv_otp_filled_2.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        tv_otp_filled_3.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        tv_otp_filled_4.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        tv_otp_filled_5.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        tv_otp_filled_6.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        et_otp_code.setText("")
                        isOtpInvalid = false
                    } else {
                        v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                        v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                        v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                        v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                        v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                        v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

                        tv_otp_filled_5.text = String.format("%s", s[4])
                        tv_otp_filled_6.text = ""
                    }
                }
                6 -> {

                    tv_otp_filled_6.text = String.format("%s", s[5])

                    verifyOTP()
                }
                else -> {
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))

                    tv_otp_filled_1.text = ""
                    tv_otp_filled_2.text = ""
                    tv_otp_filled_3.text = ""
                    tv_otp_filled_4.text = ""
                    tv_otp_filled_5.text = ""
                    tv_otp_filled_6.text = ""
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
        ll_otp_sent.visibility = View.GONE
        ll_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.tap_requesting_otp)
        TAPUtils.rotateAnimateInfinitely(context, iv_progress_otp)
        et_otp_code.isEnabled = false
    }

    private fun showVerifyingOTPLoading() {
        ll_otp_sent.visibility = View.GONE
        ll_request_otp_again.visibility = View.GONE
        tv_otp_timer.visibility = View.GONE
        iv_progress_otp.clearAnimation()
        ll_loading_otp.visibility = View.VISIBLE
        tv_loading_otp.text = resources.getText(R.string.tap_verifying_otp)
        TAPUtils.rotateAnimateInfinitely(context, iv_progress_otp)
        et_otp_code.isEnabled = false
    }

    private fun setTextandImageBasedOnOTPMethod(channel: String) {
        if (channel == "whatsapp") {
            iv_otp_method.setImageResource(R.drawable.tap_ic_whatsapp)
            tv_method_and_phonenumber.text = String.format(getString(R.string.tap_format_ss_to), getString(R.string.tap_whatsapp), phoneNumberWithCode)
        } else {
            iv_otp_method.setImageResource(R.drawable.tap_ic_sms_orange)
            iv_otp_method.setColorFilter(ContextCompat.getColor(requireContext(), R.color.tapBlack19))
            tv_method_and_phonenumber.text = String.format(getString(R.string.tap_format_ss_to), getString(R.string.tap_sms), phoneNumberWithCode)
            hideBtnSendViaSMS()
        }
    }

    private fun showPopupLoading(message: String) {
        activity?.runOnUiThread {
            popup_loading.findViewById<ImageView>(R.id.iv_loading_image).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tap_ic_loading_progress_circle_white))
            if (null == popup_loading.findViewById<ImageView>(R.id.iv_loading_image).animation)
                TAPUtils.rotateAnimateInfinitely(context, popup_loading.findViewById<ImageView>(R.id.iv_loading_image))
            popup_loading.findViewById<TextView>(R.id.tv_loading_text)?.text = message
            popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.VISIBLE
        }
    }

    private fun hidePopupLoading() {
        popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.GONE
    }

    private fun showTimer() {
        if (null != tv_otp_timer && null != ll_request_otp_again
                && null != ll_loading_otp && null != ll_otp_sent && null != iv_progress_otp) {
            tv_otp_timer.visibility = View.VISIBLE
            ll_request_otp_again.visibility = View.GONE
            ll_loading_otp.visibility = View.GONE
            ll_otp_sent.visibility = View.GONE
            iv_progress_otp.clearAnimation()
        }
    }

    private fun showRequestAgain() {
        ll_request_otp_again.visibility = View.VISIBLE
        tv_otp_timer.visibility = View.GONE
        ll_loading_otp.visibility = View.GONE
        ll_otp_sent.visibility = View.GONE
    }

    private fun showBtnSendViaSMS() {
        if (channel == "whatsapp") {
            ll_btn_send_via_sms.visibility = View.VISIBLE
            tv_not_working.visibility = View.VISIBLE
        }
    }

    private fun hideBtnSendViaSMS() {
        tv_not_working.visibility = View.GONE
        ll_btn_send_via_sms.visibility = View.GONE
    }
}
