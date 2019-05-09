package io.taptalk.TapTalk.View.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import io.taptalk.TapTalk.API.View.TapDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COUNTRY_LIST
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.COUNTRY_PICK
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPRequestOTPInterface
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPCountryListActivity
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_fragment_phone_login.*

class TAPPhoneLoginFragment : Fragment() {

    val generalErrorMessage = context?.resources?.getString(R.string.tap_error_message_general)
            ?: ""
    var countryIsoCode = "id" //Indonesia Default
    var defaultCallingCode = "62" //Indonesia Default
    var defaultCountryID = 1 //Indonesia Default
    var isNeedResetData = true //ini biar dy ga ngambil data hp setiap kali muncul halaman login

    //val oneWeekAgoTimestamp = 604800000L // 7 * 24 * 60 * 60 * 1000
    private val oneWeekAgoTimestamp: Long = 7 * 24 * 60 * 60 * 1000
    private var countryHashMap = mutableMapOf<String, TAPCountryListItem>()
    private var countryListitems = arrayListOf<TAPCountryListItem>()
    private val maxTime = 30L

    companion object {
        fun getInstance(): TAPPhoneLoginFragment {
            return TAPPhoneLoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_phone_login, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lastCallCountryTimestamp = TAPDataManager.getInstance().lastCallCountryTimestamp
        if (0L == lastCallCountryTimestamp || System.currentTimeMillis() - oneWeekAgoTimestamp == lastCallCountryTimestamp)
            callCountryListFromAPI()
        else if (isNeedResetData) {
            countryIsoCode = TAPUtils.getInstance().getDeviceCountryCode(context)
            //countryHashMap = TAPDataManager.getInstance().countryList
            countryListitems = TAPDataManager.getInstance().countryList
            countryHashMap = countryListitems.associateBy({ it.iso2Code }, { it }).toMutableMap()
            isNeedResetData = false
            if (!countryHashMap.containsKey(countryIsoCode) || "" == countryHashMap.get(countryIsoCode)?.callingCode) {
                setCountry(defaultCountryID, defaultCallingCode)
            } else {
                setCountry(countryHashMap.get(countryIsoCode)?.countryID ?: 0,
                        countryHashMap.get(countryIsoCode)?.callingCode ?: "")
            }
        } else {
            setCountry(defaultCountryID, defaultCallingCode)
        }
        initView()
    }

    private fun initView() {
        et_phone_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textCount = /*s?.length ?: 0*/ checkAndEditPhoneNumber().length + defaultCallingCode.length
                when (textCount) {
                    in 7..15 -> {
                        fl_continue_btn.background = resources.getDrawable(R.drawable.tap_bg_orange_button_ripple)
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
                fl_phone_number.setBackgroundResource(R.drawable.tap_bg_white_rounded_8dp_stroke_dcdcdc_1dp)
            }
        }

        ll_country_code.setOnClickListener {
            val intent = Intent(context, TAPCountryListActivity::class.java)
            intent.putExtra(COUNTRY_LIST, countryListitems)
            intent.putExtra(COUNTRY_ID, defaultCountryID)
            startActivityForResult(intent, COUNTRY_PICK)
        }
    }

    private fun attemptLogin() {
        TAPUtils.getInstance().dismissKeyboard(activity)
        showProgress()
        checkNumberAndCallAPI()
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = et_phone_number.text.toString().replace("-", "").trim()
        val callingCodeLength: Int = defaultCallingCode.length
        when {
            phoneNumber.isEmpty() || callingCodeLength > phoneNumber.length -> {
            }
            '0' == phoneNumber.elementAt(0) -> phoneNumber = phoneNumber.replaceFirst("0", "")
            //"+$defaultCallingCode" == phoneNumber.substring(0, (callingCodeLength + 1)) -> phoneNumber = phoneNumber.substring(3)
            defaultCallingCode == phoneNumber.substring(0, callingCodeLength) -> phoneNumber = phoneNumber.substring(2)
        }
        return phoneNumber
    }

    private fun checkNumberAndCallAPI() {
        val loginActivity = activity as TAPLoginActivity
        val loginViewModel = loginActivity.vm
        val currentOTPTimestampLength = System.currentTimeMillis() - loginViewModel.lastLoginTimestamp
        if (defaultCountryID == loginViewModel.countryID
                && checkAndEditPhoneNumber() == loginViewModel.phoneNumber
                && currentOTPTimestampLength <= maxTime * 1000) {
            requestOTPInterface.onRequestSuccess(loginViewModel.otpID, loginViewModel.otpKey, loginViewModel.phoneNumberWithCode.replaceFirst("+", ""), true)
        } else {
            TapTalk.loginWithRequestOTP(defaultCountryID, checkAndEditPhoneNumber(), requestOTPInterface)
            loginActivity.vm.lastLoginTimestamp = 0L
        }
    }

    private fun showProgress() {
        tv_btn_continue.visibility = View.GONE
        iv_loading_progress_request_otp.visibility = View.VISIBLE
        TAPUtils.getInstance().rotateAnimateInfinitely(context, iv_loading_progress_request_otp)
    }

    private fun stopAndHideProgress() {
        tv_btn_continue.visibility = View.VISIBLE
        iv_loading_progress_request_otp.visibility = View.GONE
        iv_loading_progress_request_otp.clearAnimation()
    }

    private val requestOTPInterface = object : TAPRequestOTPInterface {
        override fun onRequestSuccess(otpID: Long, otpKey: String?, phone: String?, succeess: Boolean) {
            stopAndHideProgress()
            if (activity is TAPLoginActivity) {
                try {
                    val phoneNumber = "+$phone"
                    val loginActivity = activity as TAPLoginActivity
                    loginActivity.setLastLoginData(otpID, otpKey, checkAndEditPhoneNumber(), phoneNumber, defaultCountryID, defaultCallingCode)
                    loginActivity.showOTPVerification(otpID, otpKey, checkAndEditPhoneNumber(), phoneNumber, defaultCountryID, defaultCallingCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("><><><", "Masuk ", e)
                }
            }
        }

        override fun onRequestFailed(errorMessage: String?, errorCode: String?) {
            showDialog("ERROR", errorMessage ?: generalErrorMessage)
        }
    }

    private fun callCountryListFromAPI() {
        TAPDataManager.getInstance().getCountryList(object : TapDefaultDataView<TAPCountryListResponse>() {
            override fun startLoading() {
                tv_country_code.visibility = View.GONE
                iv_loading_progress_country.visibility = View.VISIBLE
                TAPUtils.getInstance().rotateAnimateInfinitely(context, iv_loading_progress_country)
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: TAPCountryListResponse?) {
                countryListitems.clear()
                TAPDataManager.getInstance().saveLastCallCountryTimestamp(System.currentTimeMillis())
                setCountry(0, "")
                Thread {
                    var defaultCountry: TAPCountryListItem? = null
                    response?.countries?.forEach {
                        countryListitems.add(it)
                        countryHashMap.put(it.iso2Code, it)
                        if (countryIsoCode.toLowerCase() == it.iso2Code.toLowerCase() && it.iso2Code.toLowerCase() == "id") {
                            defaultCountry = it
                            activity?.runOnUiThread {
                                setCountry(it.countryID, it.callingCode)
                            }
                        } else if (countryIsoCode.toLowerCase() == it.iso2Code.toLowerCase()) {
                            activity?.runOnUiThread {
                                setCountry(it.countryID, it.callingCode)
                            }
                        } else if (it.iso2Code.toLowerCase() == "id") {
                            defaultCountry = it
                        }
                    }

                    if ("" == tv_country_code.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        activity?.runOnUiThread {
                            setCountry(defaultCountry?.countryID ?: 0, callingCode)
                        }
                    }

                    TAPDataManager.getInstance().saveCountryList(countryListitems)

                    activity?.runOnUiThread {
                        iv_loading_progress_country.visibility = View.GONE
                        iv_loading_progress_country.clearAnimation()
                        tv_country_code.visibility = View.VISIBLE
                    }
                }.start()

            }

            override fun onError(error: TAPErrorModel?) {
                super.onError(error)
                iv_loading_progress_country.visibility = View.GONE
                iv_loading_progress_country.clearAnimation()
                tv_country_code.visibility = View.VISIBLE
                setCountry(0, "")
                showDialog("ERROR", error?.message ?: generalErrorMessage)
            }

            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                iv_loading_progress_country.visibility = View.GONE
                iv_loading_progress_country.clearAnimation()
                tv_country_code.visibility = View.VISIBLE
                setCountry(0, "")
                showDialog("ERROR", errorMessage ?: generalErrorMessage)
            }
        })
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

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            COUNTRY_PICK -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val item = data?.getParcelableExtra<TAPCountryListItem>(TAPDefaultConstant.K_COUNTRY_PICK)
                        val callingCode: String = item?.callingCode ?: ""
                        setCountry(item?.countryID ?: 0, callingCode)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCountry(countryID: Int, callingCode: String) {
        tv_country_code.text = "+$callingCode"
        defaultCountryID = countryID
        defaultCallingCode = callingCode
    }
}
