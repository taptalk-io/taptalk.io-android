package io.moselo.SampleApps.Fragment

import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.moselo.SampleApps.Activity.TAPLoginActivity
import io.moselo.SampleApps.Activity.TAPLoginActivityOld
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
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalkSample.R
import io.taptalk.TapTalkSample.databinding.TapFragmentLoginVerificationBinding

class TAPLoginVerificationFragment : androidx.fragment.app.Fragment() {

    private lateinit var vb: TapFragmentLoginVerificationBinding
    val generalErrorMessage = context?.resources?.getString(io.taptalk.TapTalk.R.string.tap_error_message_general) ?: ""
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        vb = TapFragmentLoginVerificationBinding.inflate(inflater, container, false)
        return vb.root
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
        waitTime = (arguments?.getLong(kWaitTime, 30L * 1000) ?: 30L) * 1000
        otpType = arguments?.getString(kType, LOGIN) ?: LOGIN
        note = arguments?.getString(kNote, "") ?: ""
        if (otpType == VERIFICATION) {
            vb.tvToolbarTitle.visibility = View.VISIBLE
        }
        else {
            vb.tvToolbarTitle.visibility = View.GONE
        }
        setTextandImageBasedOnOTPMethod(channel)

        TAPUtils.animateClickButton(vb.ivBackButton, 0.95f)
        vb.ivBackButton.setOnClickListener { activity?.onBackPressed() }
        vb.etOtpCode.addTextChangedListener(otpTextWatcher)
        vb.etOtpCode.requestFocus()
        TAPUtils.showKeyboard(activity, vb.etOtpCode)
        clearOTPEditText()

        setupTimer()

        vb.llRequestOtpAgain.setOnClickListener {
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
                                vb.etOtpCode.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumber)
                                additional.put("countryCode", countryID.toString())
//                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Resend OTP Success", additional)
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
                                vb.etOtpCode.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumber)
                                additional.put("countryCode", countryID.toString())
//                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Resend OTP Failed", additional)
                                requestOTPInterface.onRequestFailed(error.message, error.code)
                            }

                            override fun onError(errorMessage: String) {
                                vb.etOtpCode.isEnabled = true
                                requestOTPInterface.onRequestFailed(errorMessage, "400")
                            }
                        })
            } else {
                requestDeleteAccountOtp(channel)
            }
        }

        vb.llBtnSendViaSms.background = ContextCompat.getDrawable(requireContext(), io.taptalk.TapTalk.R.drawable.tap_bg_button_border_ripple)

        vb.llBtnSendViaSms.setOnClickListener {
            isFromBtnSendViaSMS = true
            showPopupLoading(getString(io.taptalk.TapTalk.R.string.tap_loading))
            showRequestingOTPLoading()
            if (otpType == LOGIN) {
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey)
                    .requestOTPLogin(
                        countryID,
                        phoneNumber,
                        "sms",
                        object : TAPDefaultDataView<TAPOTPResponse>() {
                            override fun onSuccess(response: TAPOTPResponse) {
                                vb.etOtpCode.isEnabled = true
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumberWithCode)
                                additional.put("countryCode", countryID.toString())
//                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Request OTP Success", additional)
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
                                vb.etOtpCode.isEnabled = true
                                super.onError(error)
                                val additional = HashMap<String, String>()
                                additional.put("phoneNumber", phoneNumberWithCode)
                                additional.put("countryCode", countryID.toString())
//                                AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackErrorEvent("Request OTP Failed", error.code, error.message, additional)
                                requestOTPInterface.onRequestFailed(error.message, error.code)
                            }

                            override fun onError(errorMessage: String?) {
                                vb.etOtpCode.isEnabled = true
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
                    val loginActivity = activity as TAPLoginActivityOld
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
                vb.etOtpCode.requestFocus()
                TAPUtils.showKeyboard(activity, vb.etOtpCode)
                Handler().postDelayed({ showTimer() }, 2000)
            }
            else {
                showRequestAgain()
                if (isFromBtnSendViaSMS) {
                    hidePopupLoading()
                    isFromBtnSendViaSMS = false
                }
                else {
                    showBtnSendViaSMS()
                }
                if (whatsAppFailureReason == "") {
                    showDialog(getString(io.taptalk.TapTalk.R.string.tap_error), message)
                }
                else {
                    showDialog(getString(io.taptalk.TapTalk.R.string.tap_currently_unavailable), getString(io.taptalk.TapTalk.R.string.tap_error_we_are_experiencing_some_issues))
                }
            }
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            if (isFromBtnSendViaSMS) {
                hidePopupLoading()
                isFromBtnSendViaSMS = false
            }
            else {
                showBtnSendViaSMS()
            }
            showRequestAgain()
            if (TAPNetworkStateManager.getInstance("").hasNetworkConnection(context)) {
                showDialog(
                    getString(io.taptalk.TapTalk.R.string.tap_error), errorMessage ?:
                    getString(io.taptalk.TapTalk.R.string.tap_error_we_are_experiencing_some_issues)
                )
            }
            else {
                TAPUtils.showNoInternetErrorDialog(context)
            }
        }
    }

    private fun requestDeleteAccountOtp(channel: String) {
        TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).requestDeleteAccountOtp(channel,
            object : TAPDefaultDataView<TAPOTPResponse>() {
                override fun onSuccess(response: TAPOTPResponse) {
                    super.onSuccess(response)
                    vb.etOtpCode.isEnabled = true
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
//                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Resend OTP Success", additional)
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
                    vb.etOtpCode.isEnabled = true
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", phoneNumber)
                    additional.put("countryCode", countryID.toString())
//                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Resend OTP Failed", additional)
                    requestOTPInterface.onRequestFailed(error?.message, error?.code)
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    vb.etOtpCode.isEnabled = true
                    requestOTPInterface.onRequestFailed(errorMessage, "400")
                }
            })
    }

    private fun resendOtpSuccessMessage() {
        clearOTPEditText()
        vb.llRequestOtpAgain.visibility = View.GONE
        vb.llLoadingOtp.visibility = View.GONE
        vb.tvOtpTimer.visibility = View.GONE
        vb.llOtpSent.visibility = View.VISIBLE
        vb.ivProgressOtp.clearAnimation()
    }

    private fun setupTimer() {
        val lastLoginTimestamp =
            when (otpType) {
                LOGIN -> (activity as TAPLoginActivityOld).vm.lastLoginTimestamp
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
        vb.tvDidntReceiveAndInvalid.text = resources.getText(io.taptalk.TapTalk.R.string.tap_didnt_receive_the_6_digit_otp)
        vb.tvDidntReceiveAndInvalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))

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
                            if (10 > secondLeft) vb.tvOtpTimer.text = "Wait 0:0$secondLeft"
                            else vb.tvOtpTimer.text = "Wait 0:$secondLeft"
                        } catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                    else -> {
                        try {
                            vb.tvOtpTimer.text = "Wait $minuteLeft:$secondLeft"
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
                (activity as TAPLoginActivityOld).vm.lastLoginTimestamp = System.currentTimeMillis()
                (activity as TAPLoginActivityOld).vm.waitTimeRequestOtp = (waitTime / 1000).toInt()
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
                vb.etOtpCode.text.toString(),
                object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                    override fun onSuccess(response: TAPLoginOTPVerifyResponse) {
                        vb.etOtpCode.isEnabled = true
                        if (response.isRegistered) {
//                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).identifyUser()
//                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Login Success")
                            TapTalk.authenticateWithAuthTicket(
                                (activity as TAPBaseActivity).instanceKey,
                                response.ticket,
                                true,
                                object : TapCommonListener() {
                                    override fun onSuccess(successMessage: String) {
//                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).identifyUser()
//                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Authenticate TapTalk.io Success")
                                        verifyOTPInterface.verifyOTPSuccessToLogin()
                                    }

                                    override fun onError(errorCode: String, errorMessage: String) {
//                                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackErrorEvent("Authenticate TapTalk.io Failed", errorCode, errorMessage)
                                        verifyOTPInterface.verifyOTPFailed(errorCode, errorMessage)
                                    }
                                })
                        } else {
//                            AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Login Success and Continue Register")
                            verifyOTPInterface.verifyOTPSuccessToRegister()
                        }

                    }

                    override fun onError(error: TAPErrorModel) {
                        vb.etOtpCode.isEnabled = true
//                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackErrorEvent("Login Failed", error.code, error.message)
                        verifyOTPInterface.verifyOTPFailed(error.message, error.code)
                    }

                    override fun onError(errorMessage: String) {
                        vb.etOtpCode.isEnabled = true
                        verifyOTPInterface.verifyOTPFailed(errorMessage, "400")
                    }
                })
        } else {
            (activity as TapDeleteAccountActivity).vm.isLoading = true
            TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).verifyOtpDeleteAccount(
                otpID,
                otpKey,
                vb.etOtpCode.text.toString(),
                note,
                object : TAPDefaultDataView<TAPCommonResponse>() {
                    override fun onSuccess(response: TAPCommonResponse) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        (activity as TapDeleteAccountActivity).vm.isDeleted = true
                        vb.flOtp.visibility = View.GONE
                        vb.llLoadingOtp.visibility = View.GONE
                        vb.tvDidntReceiveAndInvalid.text = getString(io.taptalk.TapTalk.R.string.tap_verification_succeed)
                        vb.tvDidntReceiveAndInvalid.setTextColor(ContextCompat.getColor(requireContext(), io.taptalk.TapTalk.R.color.tapTransparentBlack1980))
                        vb.ivCheck.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            TapTalk.logoutAndClearAllTapTalkData((activity as TAPBaseActivity).instanceKey)
                        }, 3000L)
                    }

                    override fun onError(error: TAPErrorModel) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        vb.etOtpCode.isEnabled = true
//                        AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackErrorEvent("Login Failed", error.code, error.message)
                        verifyOTPInterface.verifyOTPFailed(error.message, error.code)
                    }

                    override fun onError(errorMessage: String) {
                        (activity as TapDeleteAccountActivity).vm.isLoading = false
                        vb.etOtpCode.isEnabled = true
                        verifyOTPInterface.verifyOTPFailed(errorMessage, "400")
                    }
                })
        }
    }

    private val verifyOTPInterface = object : TAPVerifyOTPInterface {
        override fun verifyOTPSuccessToLogin() {
            activity?.runOnUiThread {
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveMyCountryCode(countryCallingCode)
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveMyCountryFlagUrl(countryFlagUrl)
                TapUIRoomListActivity.start(context, (activity as TAPBaseActivity).instanceKey)
                TAPDataManager.getInstance("").checkAndRequestAutoStartPermission(activity)
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
                (activity as TAPLoginActivityOld).vm.phoneNumber = "0"
                (activity as TAPLoginActivityOld).vm.countryID = 0
                activity?.onBackPressed()
            }
        }

        override fun verifyOTPFailed(errorCode: String?, errorMessage: String?) {
            vb.tvDidntReceiveAndInvalid.text = resources.getText(io.taptalk.TapTalk.R.string.tap_error_invalid_otp)
            vb.tvDidntReceiveAndInvalid.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled1.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled2.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled3.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled4.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled5.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.tvOtpFilled6.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))

            if (isTimerFinished) {
                showRequestAgain()
            } else {
                showTimer()
            }
            isOtpInvalid = true
            vb.etOtpCode.requestFocus()
            TAPUtils.showKeyboard(activity, vb.etOtpCode)


        }
    }

    private fun clearOTPEditText() {
        vb.vPointer1.visibility = View.VISIBLE
        vb.vPointer2.visibility = View.VISIBLE
        vb.vPointer3.visibility = View.VISIBLE
        vb.vPointer4.visibility = View.VISIBLE
        vb.vPointer5.visibility = View.VISIBLE
        vb.vPointer6.visibility = View.VISIBLE

        vb.tvOtpFilled1.text = ""
        vb.tvOtpFilled2.text = ""
        vb.tvOtpFilled3.text = ""
        vb.tvOtpFilled4.text = ""
        vb.tvOtpFilled5.text = ""
        vb.tvOtpFilled6.text = ""
        vb.etOtpCode.setText("")
    }

    val otpTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s?.length) {
                1 -> {
                    vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.tvOtpFilled1.text = String.format("%s", s[0])
                    vb.tvOtpFilled2.text = ""
                    vb.tvOtpFilled3.text = ""
                    vb.tvOtpFilled4.text = ""
                    vb.tvOtpFilled5.text = ""
                    vb.tvOtpFilled6.text = ""
                }
                2 -> {
                    vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.tvOtpFilled2.text = String.format("%s", s[1])
                    vb.tvOtpFilled3.text = ""
                    vb.tvOtpFilled4.text = ""
                    vb.tvOtpFilled5.text = ""
                    vb.tvOtpFilled6.text = ""
                }
                3 -> {
                    vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.tvOtpFilled3.text = String.format("%s", s[2])
                    vb.tvOtpFilled4.text = ""
                    vb.tvOtpFilled5.text = ""
                    vb.tvOtpFilled6.text = ""
                }
                4 -> {
                    vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.tvOtpFilled4.text = String.format("%s", s[3])
                    vb.tvOtpFilled5.text = ""
                    vb.tvOtpFilled6.text = ""
                }
                5 -> {
                    if (isOtpInvalid) {
                        vb.tvOtpFilled1.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.tvOtpFilled2.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.tvOtpFilled3.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.tvOtpFilled4.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.tvOtpFilled5.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.tvOtpFilled6.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorTextDark))
                        vb.etOtpCode.setText("")
                        isOtpInvalid = false
                    } else {
                        vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                        vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                        vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                        vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                        vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                        vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

                        vb.tvOtpFilled5.text = String.format("%s", s[4])
                        vb.tvOtpFilled6.text = ""
                    }
                }
                6 -> {

                    vb.tvOtpFilled6.text = String.format("%s", s[5])

                    verifyOTP()
                }
                else -> {
                    vb.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.tvOtpFilled1.text = ""
                    vb.tvOtpFilled2.text = ""
                    vb.tvOtpFilled3.text = ""
                    vb.tvOtpFilled4.text = ""
                    vb.tvOtpFilled5.text = ""
                    vb.tvOtpFilled6.text = ""
                }
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok))
                .setPrimaryButtonListener {}
                .show()
    }

    private fun showRequestingOTPLoading() {
        vb.llOtpSent.visibility = View.GONE
        vb.llRequestOtpAgain.visibility = View.GONE
        vb.tvOtpTimer.visibility = View.GONE
        vb.ivProgressOtp.clearAnimation()
        vb.llLoadingOtp.visibility = View.VISIBLE
        vb.tvLoadingOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_requesting_otp)
        TAPUtils.rotateAnimateInfinitely(context, vb.ivProgressOtp)
        vb.etOtpCode.isEnabled = false
    }

    private fun showVerifyingOTPLoading() {
        vb.llOtpSent.visibility = View.GONE
        vb.llRequestOtpAgain.visibility = View.GONE
        vb.tvOtpTimer.visibility = View.GONE
        vb.ivProgressOtp.clearAnimation()
        vb.llLoadingOtp.visibility = View.VISIBLE
        vb.tvLoadingOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_verifying_otp)
        TAPUtils.rotateAnimateInfinitely(context, vb.ivProgressOtp)
        vb.etOtpCode.isEnabled = false
    }

    private fun setTextandImageBasedOnOTPMethod(channel: String) {
        if (channel == "whatsapp") {
            vb.ivOtpMethod.setImageResource(io.taptalk.TapTalk.R.drawable.tap_ic_whatsapp)
            vb.tvMethodAndPhonenumber.text = String.format(getString(io.taptalk.TapTalk.R.string.tap_format_ss_to), getString(io.taptalk.TapTalk.R.string.tap_whatsapp), phoneNumberWithCode)
        }
        else {
            vb.ivOtpMethod.setImageResource(io.taptalk.TapTalk.R.drawable.tap_ic_sms_orange)
            vb.ivOtpMethod.setColorFilter(ContextCompat.getColor(requireContext(), io.taptalk.TapTalk.R.color.tapBlack19))
            vb.tvMethodAndPhonenumber.text = String.format(getString(io.taptalk.TapTalk.R.string.tap_format_ss_to), getString(io.taptalk.TapTalk.R.string.tap_sms), phoneNumberWithCode)
            hideBtnSendViaSMS()
        }
    }

    private fun showPopupLoading(message: String) {
        activity?.runOnUiThread {
            vb.popupLoading.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), io.taptalk.TapTalk.R.drawable.tap_ic_loading_progress_circle_white))
            if (null == vb.popupLoading.ivLoadingImage.animation) {
                TAPUtils.rotateAnimateInfinitely(context, vb.popupLoading.ivLoadingImage)
            }
            vb.popupLoading.tvLoadingText.text = message
            vb.popupLoading.flLoading.visibility = View.VISIBLE
        }
    }

    private fun hidePopupLoading() {
        vb.popupLoading.flLoading.visibility = View.GONE
    }

    private fun showTimer() {
        vb.tvOtpTimer.visibility = View.VISIBLE
        vb.llRequestOtpAgain.visibility = View.GONE
        vb.llLoadingOtp.visibility = View.GONE
        vb.llOtpSent.visibility = View.GONE
        vb.ivProgressOtp.clearAnimation()
    }

    private fun showRequestAgain() {
        vb.llRequestOtpAgain.visibility = View.VISIBLE
        vb.tvOtpTimer.visibility = View.GONE
        vb.llLoadingOtp.visibility = View.GONE
        vb.llOtpSent.visibility = View.GONE
    }

    private fun showBtnSendViaSMS() {
        if (channel == "whatsapp") {
            vb.llBtnSendViaSms.visibility = View.VISIBLE
            vb.tvNotWorking.visibility = View.VISIBLE
        }
    }

    private fun hideBtnSendViaSMS() {
        vb.tvNotWorking.visibility = View.GONE
        vb.llBtnSendViaSms.visibility = View.GONE
    }
}
