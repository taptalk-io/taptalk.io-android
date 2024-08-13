package io.moselo.SampleApps.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import io.moselo.SampleApps.Activity.TAPCountryListActivity
import io.moselo.SampleApps.Activity.TAPLoginActivityOld
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_LIST
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.COUNTRY_PICK
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalkSample.databinding.TapFragmentPhoneLoginBinding

class TAPPhoneLoginFragment : androidx.fragment.app.Fragment() {

    private lateinit var vb: TapFragmentPhoneLoginBinding
    val generalErrorMessage = context?.resources?.getString(io.taptalk.TapTalk.R.string.tap_error_message_general) ?: ""
    var countryIsoCode = "id" //Indonesia Default
    var defaultCallingCode = "62" //Indonesia Default
    var defaultCountryID = 1 //Indonesia Default
    var isNeedResetData = true //ini biar dy ga ngambil data hp setiap kali muncul halaman login

    //val oneDayAgoTimestamp = 604800000L // 7 * 24 * 60 * 60 * 1000
    private val oneDayAgoTimestamp: Long = 24 * 60 * 60 * 1000
    private var countryHashMap = mutableMapOf<String, TAPCountryListItem>()
    private var countryListitems = arrayListOf<TAPCountryListItem>()
    private var maxTime = 120L * 1000
    private var countryFlagUrl = ""
    private var previousPhoneNumber = "0"

    companion object {
        fun getInstance(): TAPPhoneLoginFragment {
            return TAPPhoneLoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        vb = TapFragmentPhoneLoginBinding.inflate(inflater, container, false)
        return vb.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lastCallCountryTimestamp = TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).lastCallCountryTimestamp

        if (0L == lastCallCountryTimestamp || System.currentTimeMillis() - oneDayAgoTimestamp == lastCallCountryTimestamp) {
            callCountryListFromAPI()
        }
        else if (isNeedResetData) {
            callCountryListFromAPI()
            countryIsoCode = TAPUtils.getDeviceCountryCode(context)
            //countryHashMap = TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).countryList
            countryListitems = TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).countryList
            countryHashMap = countryListitems.associateBy({ it.iso2Code }, { it }).toMutableMap()
            isNeedResetData = false
            if (!countryHashMap.containsKey(countryIsoCode) || "" == countryHashMap.get(countryIsoCode)?.callingCode) {
                setCountry(defaultCountryID, defaultCallingCode, "")
            }
            else {
                setCountry(countryHashMap.get(countryIsoCode)?.countryID ?: 0,
                        countryHashMap.get(countryIsoCode)?.callingCode ?: "",
                        countryHashMap.get(countryIsoCode)?.flagIconUrl ?: "")
            }
        }
        else {
            setCountry(defaultCountryID, defaultCallingCode, countryFlagUrl)
        }
        initView()
    }

    private fun initView() {
        vb.etPhoneNumber.addTextChangedListener(phoneNumberTextWatcher)

        vb.etPhoneNumber.setOnEditorActionListener { v, actionId, event ->
            when (v?.length() ?: 0) {
                in 7..15 -> {
                    attemptLogin()
                }
            }
            false
        }

        vb.etPhoneNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                vb.flPhoneNumber.setBackgroundResource(io.taptalk.TapTalk.R.drawable.tap_bg_text_field_active)
            }
            else {
                vb.flPhoneNumber.setBackgroundResource(io.taptalk.TapTalk.R.drawable.tap_bg_text_field_inactive)
            }
        }

        enableCountryPicker()
    }

    private fun enableContinueButton() {
        enableCountryPicker()
        vb.flContinueBtn.setOnClickListener { attemptLogin() }
        vb.flContinueBtn.isClickable = true
    }

    private fun disableContinueButton() {
        disableCountryPicker()
        vb.flContinueBtn.setOnClickListener(null)
        vb.flContinueBtn.isClickable = false
    }

    private fun attemptLogin() {
        disableContinueButton()
        if (isVisible) {
            TAPUtils.dismissKeyboard(activity)
            showProgress()
            checkNumberAndCallAPI()
        }
    }

    private fun enableCountryPicker() {
        vb.llCountryCode.setOnClickListener {
//            val activity = context as TAPBaseActivity
//            TAPCountryListActivity.start(activity, activity.instanceKey, countryListitems, defaultCountryID)
            val intent = Intent(context, TAPCountryListActivity::class.java)
            intent.putExtra(COUNTRY_LIST, countryListitems)
            intent.putExtra(COUNTRY_ID, defaultCountryID)
            startActivityForResult(intent, COUNTRY_PICK)
        }
    }

    private fun disableCountryPicker() {
        vb.llCountryCode.setOnClickListener(null)
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = vb.etPhoneNumber.text.toString().replace("-", "").trim()
        val callingCodeLength: Int = defaultCallingCode.length
        when {
            phoneNumber.isEmpty() || callingCodeLength > phoneNumber.length -> {
            }
            '0' == phoneNumber.elementAt(0) -> phoneNumber = phoneNumber.replaceFirst("0", "")
            //"+$defaultCallingCode" == phoneNumber.substring(0, (callingCodeLength + 1)) -> phoneNumber = phoneNumber.substring(3)
            defaultCallingCode == phoneNumber.substring(0, callingCodeLength) -> phoneNumber = phoneNumber.substring(callingCodeLength-1)
        }
        return phoneNumber
    }

    private fun checkNumberAndCallAPI() {
        val loginActivity = activity as TAPLoginActivityOld
        val loginViewModel = loginActivity.vm
        val currentOTPTimestampLength = System.currentTimeMillis() - loginViewModel.lastLoginTimestamp
        if (defaultCountryID == loginViewModel.countryID
                && checkAndEditPhoneNumber() == loginViewModel.phoneNumber
                && currentOTPTimestampLength <= maxTime) {
            requestOTPInterface.onRequestSuccess(loginViewModel.otpID, loginViewModel.otpKey, loginViewModel.phoneNumberWithCode.replaceFirst("+", ""), true, loginViewModel.channel, "", loginViewModel.waitTimeRequestOtp, "")
        } else {
            TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).requestOTPLogin(defaultCountryID, checkAndEditPhoneNumber(), "", object : TAPDefaultDataView<TAPOTPResponse>() {
                override fun onSuccess(response: TAPOTPResponse) {
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", defaultCallingCode + checkAndEditPhoneNumber())
                    additional.put("countryCode", defaultCountryID.toString())
//                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackEvent("Request OTP Success", additional)
                    super.onSuccess(response)
                    requestOTPInterface.onRequestSuccess(response.otpID, response.otpKey, response.phoneWithCode, response.isSuccess, response.channel, response.message, response.nextRequestSeconds, response.whatsAppFailureReason)
                }

                override fun onError(error: TAPErrorModel) {
                    super.onError(error)
                    val additional = HashMap<String, String>()
                    additional.put("phoneNumber", defaultCallingCode + checkAndEditPhoneNumber())
                    additional.put("countryCode", defaultCountryID.toString())
//                    AnalyticsManager.getInstance((activity as TAPBaseActivity).instanceKey).trackErrorEvent("Request OTP Failed", error.code, error.message, additional)
                    requestOTPInterface.onRequestFailed(error.message, error.code)
                }

                override fun onError(errorMessage: String) {
                    requestOTPInterface.onRequestFailed(errorMessage, "400")
                }
            })
            loginActivity.vm.lastLoginTimestamp = 0L
        }
    }

    private fun showProgress() {
        if (isVisible) {
            vb.tvBtnContinue.visibility = View.GONE
            vb.ivLoadingProgressRequestOtp.visibility = View.VISIBLE
            TAPUtils.rotateAnimateInfinitely(context, vb.ivLoadingProgressRequestOtp)
        }
    }

    private fun stopAndHideProgress() {
        if (isVisible) {
            vb.tvBtnContinue.visibility = View.VISIBLE
            vb.ivLoadingProgressRequestOtp.visibility = View.GONE
            vb.ivLoadingProgressRequestOtp.clearAnimation()
        }
    }

    private val requestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean, channel: String, message: String, nextRequestSeconds: Int, whatsAppFailureReason: String) {
            maxTime = nextRequestSeconds * 1000L
            if (isVisible) {
                stopAndHideProgress()
                if (succeess) {
                    if (activity is TAPLoginActivityOld) {
                        try {
                            val phoneNumber = "+$phone"
                            val loginActivity = activity as TAPLoginActivityOld
                            loginActivity.setLastLoginData(otpID, otpKey, checkAndEditPhoneNumber(), phoneNumber, defaultCountryID, defaultCallingCode, channel)
                            loginActivity.showOTPVerification(otpID, otpKey, checkAndEditPhoneNumber(), phoneNumber, defaultCountryID, defaultCallingCode, countryFlagUrl, channel, nextRequestSeconds)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else {
                    enableContinueButton()
                    if (whatsAppFailureReason == "") {
                        showDialog(getString(io.taptalk.TapTalk.R.string.tap_error), message)
                    } else {
                        showDialog(getString(io.taptalk.TapTalk.R.string.tap_currently_unavailable), getString(io.taptalk.TapTalk.R.string.tap_error_we_are_experiencing_some_issues))
                    }
                }
            }
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            enableContinueButton()
            if (TAPNetworkStateManager.getInstance("").hasNetworkConnection(context)) {
                showDialog(getString(io.taptalk.TapTalk.R.string.tap_error), errorMessage ?: generalErrorMessage)
            }
            else {
                TAPUtils.showNoInternetErrorDialog(context)
                stopAndHideProgress()
            }
        }
    }

    private fun callCountryListFromAPI() {
        TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).getCountryList(object : TAPDefaultDataView<TAPCountryListResponse>() {
            override fun startLoading() {
                vb.etPhoneNumber.isEnabled = false
                vb.tvCountryCode.visibility = View.GONE
                vb.ivLoadingProgressCountry.visibility = View.VISIBLE
                TAPUtils.rotateAnimateInfinitely(context, vb.ivLoadingProgressCountry)
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: TAPCountryListResponse?) {
                vb.etPhoneNumber.isEnabled = true
                countryListitems.clear()
                TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveLastCallCountryTimestamp(System.currentTimeMillis())
                setCountry(0, "", "")
                Thread {
                    var defaultCountry: TAPCountryListItem? = null
                    response?.countries?.forEach {
                        countryListitems.add(it)
                        countryHashMap.put(it.iso2Code, it)
                        if (countryIsoCode.equals(it.iso2Code, true) && it.iso2Code.lowercase() == "id") {
                            defaultCountry = it
                            activity?.runOnUiThread {
                                setCountry(it.countryID, it.callingCode, it.flagIconUrl)
                            }
                        }
                        else if (countryIsoCode.equals(it.iso2Code, true)) {
                            activity?.runOnUiThread {
                                setCountry(it.countryID, it.callingCode, it.flagIconUrl)
                            }
                        }
                        else if (it.iso2Code.lowercase() == "id") {
                            defaultCountry = it
                        }
                    }

                    if ("" == vb.tvCountryCode.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        activity?.runOnUiThread {
                            setCountry(defaultCountry?.countryID
                                    ?: 0, callingCode, defaultCountry?.flagIconUrl ?: "")
                        }
                    }

                    TAPDataManager.getInstance((activity as TAPBaseActivity).instanceKey).saveCountryList(countryListitems)

                    activity?.runOnUiThread {
                        vb.ivLoadingProgressCountry.visibility = View.GONE
                        vb.ivLoadingProgressCountry.clearAnimation()
                        vb.tvCountryCode.visibility = View.VISIBLE
                    }
                }.start()
            }

            override fun onError(error: TAPErrorModel?) {
                super.onError(error)
                vb.ivLoadingProgressCountry.visibility = View.GONE
                vb.ivLoadingProgressCountry.clearAnimation()
                vb.tvCountryCode.visibility = View.VISIBLE
                setCountry(0, "", "")
                showDialog("ERROR", error?.message ?: generalErrorMessage)
            }

            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                vb.ivLoadingProgressCountry.visibility = View.GONE
                vb.ivLoadingProgressCountry.clearAnimation()
                vb.tvCountryCode.visibility = View.VISIBLE
                setCountry(0, "", "")
                showDialog("ERROR", errorMessage ?: generalErrorMessage)
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        if (isVisible)
            TapTalkDialog.Builder(context)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok))
                .setPrimaryButtonListener {
                    stopAndHideProgress()
                }.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            COUNTRY_PICK -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val item = data?.getParcelableExtra<TAPCountryListItem>(TAPDefaultConstant.K_COUNTRY_PICK)
                        val callingCode: String = item?.callingCode ?: ""
                        setCountry(item?.countryID ?: 0, callingCode, item?.flagIconUrl ?: "")
                        val textCount = callingCode.length + checkAndEditPhoneNumber().length
                        when {
                            textCount > 15 -> {
                                vb.etPhoneNumber.setText("")
                            }
                            textCount in 7..15 -> {
                                changeButtonContinueStateEnabled()
                            }
                            else -> {
                                changeButtonContinueStateDisabled()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCountry(countryID: Int, callingCode: String, flagIconUrl: String) {
        vb.tvCountryCode.text = "+$callingCode"
        defaultCountryID = countryID
        defaultCallingCode = callingCode
        countryFlagUrl = flagIconUrl

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(vb.ivCountryFlag)
        }
        else {
            vb.ivCountryFlag.setImageResource(io.taptalk.TapTalk.R.drawable.tap_ic_default_flag)
        }
    }

    private fun changeButtonContinueStateEnabled() {
        if (context != null) {
            vb.flContinueBtn.background = ContextCompat.getDrawable(requireContext(), io.taptalk.TapTalk.R.drawable.tap_bg_button_active_ripple)
        }
        vb.flContinueBtn.setOnClickListener { attemptLogin() }
        vb.flContinueBtn.isClickable = true
    }

    private fun changeButtonContinueStateDisabled() {
        if (context != null) {
            vb.flContinueBtn.background = ContextCompat.getDrawable(requireContext(), io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive_ripple)
        }
        vb.flContinueBtn.setOnClickListener(null)
        vb.flContinueBtn.isClickable = false
    }

    private val phoneNumberTextWatcher = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            previousPhoneNumber = p0.toString()
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val textCount = /*s?.length ?: 0*/ checkAndEditPhoneNumber().length + defaultCallingCode.length
            if (textCount >= 7) {
                changeButtonContinueStateEnabled()
            } else if (textCount < 7) {
                changeButtonContinueStateDisabled()
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            val textCount = /*s?.length ?: 0*/ checkAndEditPhoneNumber().length + defaultCallingCode.length
            if (textCount > 15) {
                vb.etPhoneNumber.removeTextChangedListener(this)
                vb.etPhoneNumber.setText(previousPhoneNumber)
                vb.etPhoneNumber.addTextChangedListener(this)
                vb.etPhoneNumber.setSelection(vb.etPhoneNumber.length())
            }
        }
    }
}
