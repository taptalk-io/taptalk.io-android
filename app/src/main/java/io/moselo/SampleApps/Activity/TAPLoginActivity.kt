package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.moselo.SampleApps.Adapter.TAPCountryListAdapter
import io.moselo.SampleApps.SampleApplication
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapCustomSnackbarView
import io.taptalk.TapTalk.Helper.TapLoadingDialog
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPOTPResponse
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModel
import io.taptalk.TapTalkSample.BuildConfig
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_activity_login.*
import kotlinx.android.synthetic.main.tap_layout_login_country_list.*
import kotlinx.android.synthetic.main.tap_layout_login_input.*
import kotlinx.android.synthetic.main.tap_layout_login_otp.*
import kotlinx.android.synthetic.main.tap_layout_login_verification_status.*
import kotlinx.android.synthetic.main.tap_layout_login_whatsapp_verification.*
import java.util.Timer
import java.util.TimerTask

class TAPLoginActivity : TAPBaseActivity() {

    private var vm: TAPLoginViewModel? = null
    private lateinit var countryListAdapter: TAPCountryListAdapter

    private val defaultCallingCode = "62"
    private val defaultCountryID = 1

    companion object {
        @JvmStatic
        @JvmOverloads
        fun start(
            context: Context,
            instanceKey: String?,
            newTask: Boolean = true
        ) {
            val intent = Intent(context, TAPLoginActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            if (newTask) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    /**=============================================================================================
     * Override Methods
    ==============================================================================================*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_login)
        initViewModel()
        initView()
        initCountryList()
        TAPUtils.checkAndRequestNotificationPermission(this)

        if (application != null && application is SampleApplication) {
            (application as SampleApplication).loginActivityExists = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (vm?.isCheckWhatsAppVerificationPending == true) {
            vm?.isCheckWhatsAppVerificationPending = false
            checkWhatsAppVerification(true)
        }
    }

//    override fun onPause() {
//        super.onPause()
//        vm?.checkVerificationTimer?.cancel()
//    }

    override fun onBackPressed() {
        if (cl_country_list_container.visibility == View.VISIBLE ||
            sv_whatsapp_verification.visibility == View.VISIBLE ||
            sv_otp_verification.visibility == View.VISIBLE
        ) {
            if (iv_qr_code.visibility == View.VISIBLE) {
                hideQR()
                return
            }
            showPhoneNumberInputView()
            return
        }
        if (ll_button_retry_verification.visibility == View.VISIBLE) {
            showVerificationView()
            return
        }
        if (iv_verification_status_loading?.visibility == View.VISIBLE) {
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (application != null && application is SampleApplication) {
            (application as SampleApplication).loginActivityExists = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.REGISTER -> {
                    continueToHome()
                }
            }
        }
    }

    /**=============================================================================================
     * Initialization
    ==============================================================================================*/

    private fun initViewModel() {
        vm = ViewModelProvider(this).get(TAPLoginViewModel::class.java)
    }

    private fun initView() {
        val numberFilter = InputFilter { source, _, _, _, _, _ ->
            val numberRegex = Regex("[^0-9]")
            return@InputFilter numberRegex.replace(source, "")
        }
        et_phone_number?.filters = et_phone_number.filters + numberFilter
        et_phone_number?.addTextChangedListener(phoneTextWatcher)

        try {
            countryListAdapter = TAPCountryListAdapter(setupDataForRecycler(""), countryPickInterface)
            rv_country_list?.adapter = countryListAdapter
            rv_country_list?.setHasFixedSize(true)
            rv_country_list?.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        iv_button_close_country_list?.setOnClickListener(backButtonClickListener)
        ll_button_change_number?.setOnClickListener(backButtonClickListener)
        ll_button_change_number_otp?.setOnClickListener(backButtonClickListener)
        ll_button_whatsapp?.setOnClickListener(loginViaWhatsAppClickListener)
        ll_button_otp?.setOnClickListener(loginViaOTPClickListener)
        ll_button_verify?.setOnClickListener(openWhatsAppClickListener)
        ll_button_show_qr_code?.setOnClickListener { showQR() }
        ll_request_otp_again.setOnClickListener { requestOtp(true) }
        ll_button_retry_verification?.setOnClickListener { showVerificationView() }
        cl_login_container?.setOnClickListener { TAPUtils.dismissKeyboard(this) }
        cl_login_input_container?.setOnClickListener { TAPUtils.dismissKeyboard(this, et_phone_number) }
        cl_otp_container?.setOnClickListener { TAPUtils.dismissKeyboard(this, et_otp_code) }

        et_search_country_list?.addTextChangedListener(searchTextWatcher)
        et_otp_code?.addTextChangedListener(otpTextWatcher)
        et_otp_code?.setOnEditorActionListener(otpEditorListener)

        showPhoneNumberInputView()

        if (BuildConfig.BUILD_TYPE == "dev") {
            ll_button_otp?.setOnLongClickListener(devPhoneNumberLongClickListener)
        }
    }

    private val backButtonClickListener = OnClickListener {
        onBackPressed()
    }

    /**=============================================================================================
     * Phone Number Input
     =============================================================================================*/

    @SuppressLint("SetTextI18n")
    private fun setCountry(countryID: Int, callingCode: String, flagIconUrl: String?) {
        if (callingCode.isNotEmpty()) {
            tv_country_code?.text = "+$callingCode"
            tv_country_code?.hint = ""
            et_phone_number?.visibility = View.VISIBLE
            tv_phone_number?.visibility = View.VISIBLE
        }
        else {
            tv_country_code?.text = ""
            tv_country_code?.hint = getString(R.string.tap_hint_select_country)
            et_phone_number?.visibility = View.GONE
            tv_phone_number?.visibility = View.GONE
        }
        vm?.selectedCountryID = countryID
        vm?.countryCallingID = callingCode
        vm?.countryFlagUrl = flagIconUrl ?: ""

        et_phone_number?.filters = et_phone_number.filters + InputFilter.LengthFilter(15 - callingCode.length)

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(iv_country_flag)
        }
        else {
            iv_country_flag?.setImageResource(R.drawable.tap_ic_default_flag)
        }
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = et_phone_number.text.toString().replace("-", "").trim()
        val callingCodeLength: Int = defaultCallingCode.length
        if (phoneNumber.isNotEmpty() && callingCodeLength < phoneNumber.length) {
            when {
                '0' == phoneNumber.elementAt(0) -> {
                    phoneNumber = phoneNumber.replaceFirst("0", "")
                }
                defaultCallingCode == phoneNumber.substring(0, callingCodeLength) -> {
                    phoneNumber = phoneNumber.substring(callingCodeLength - 1)
                }
            }
        }
        return phoneNumber
    }

    private fun showPhoneNumberInputLoading(isLoadingOTP: Boolean = false) {
        runOnUiThread {
            if (isLoadingOTP) {
                pb_button_whatsapp_loading?.visibility = View.GONE
                pb_button_otp_loading?.visibility = View.VISIBLE
                iv_button_whatsapp?.visibility = View.VISIBLE
                iv_button_otp?.visibility = View.GONE
            }
            else {
                pb_button_whatsapp_loading?.visibility = View.VISIBLE
                pb_button_otp_loading?.visibility = View.GONE
                iv_button_whatsapp?.visibility = View.GONE
                iv_button_otp?.visibility = View.VISIBLE
            }
            iv_country_chevron?.alpha = 0.4f
            tv_country_code?.alpha = 0.4f
            et_phone_number?.alpha = 0.4f
            tv_phone_number?.alpha = 0.4f
            cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
            ll_button_whatsapp?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
            ll_button_otp?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_border_inactive)
            ImageViewCompat.setImageTintList(iv_button_otp, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapTransparentBlack1920)))
            tv_button_whatsapp?.setTextColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1940))
            tv_button_otp?.setTextColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1940))
            et_phone_number?.isEnabled = false
            ll_country_picker_button?.setOnClickListener(null)
            ll_button_whatsapp?.setOnClickListener(null)
            ll_button_otp?.setOnClickListener(null)
        }
    }

    private fun hidePhoneNumberInputLoading() {
        runOnUiThread {
            pb_button_whatsapp_loading?.visibility = View.GONE
            pb_button_otp_loading?.visibility = View.GONE
            iv_button_whatsapp?.visibility = View.VISIBLE
            iv_button_otp?.visibility = View.VISIBLE
            iv_country_chevron?.alpha = 1f
            tv_country_code?.alpha = 1f
            et_phone_number?.alpha = 1f
            tv_phone_number?.alpha = 1f
            cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_light)
            ll_button_whatsapp?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
            ll_button_otp?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_border_ripple)
            ImageViewCompat.setImageTintList(iv_button_otp, null)
            tv_button_whatsapp?.setTextColor(ContextCompat.getColor(this, R.color.tapButtonLabelColor))
            tv_button_otp?.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
            et_phone_number?.isEnabled = true
            ll_country_picker_button?.setOnClickListener(countryPickerClickListener)
            ll_button_whatsapp?.setOnClickListener(loginViaWhatsAppClickListener)
            ll_button_otp?.setOnClickListener(loginViaOTPClickListener)
        }
    }

    private val countryPickerClickListener = OnClickListener {
        if (!vm?.countryListItems.isNullOrEmpty()) {
            showCountryListView()
        }
        else {
            callCountryListFromAPI(true)
        }
    }

    private val loginViaWhatsAppClickListener = OnClickListener {
        if (validatePhoneNumber()) {
            requestWhatsAppVerification()
        }
    }

    private val loginViaOTPClickListener = OnClickListener {
        if (validatePhoneNumber()) {
            requestOtp()
        }
    }

    private val devPhoneNumberLongClickListener = OnLongClickListener {
        if (validatePhoneNumber()) {
            val phoneNumber = checkAndEditPhoneNumber()
            showPhoneNumberInputLoading(true)
            TAPDataManager.getInstance(instanceKey).requestOTPLogin(
                vm?.selectedCountryID ?: defaultCountryID,
                phoneNumber,
                "",
                object : TAPDefaultDataView<TAPOTPResponse>() {
                    override fun onSuccess(response: TAPOTPResponse) {
                        val otpCode: String = phoneNumber.substring(phoneNumber.length - 6)
                        TAPDataManager.getInstance(instanceKey).verifyOTPLogin(
                            response.otpID,
                            response.otpKey,
                            otpCode,
                            object : TAPDefaultDataView<TAPLoginOTPVerifyResponse?>() {
                                override fun onSuccess(response: TAPLoginOTPVerifyResponse?) {
                                    if (response?.isRegistered == true) {
                                        TapTalk.authenticateWithAuthTicket(
                                            instanceKey,
                                            response.ticket,
                                            true,
                                            object : TapCommonListener() {
                                                override fun onSuccess(successMessage: String) {
                                                    continueToHome()
                                                }

                                                override fun onError(
                                                    errorCode: String,
                                                    errorMessage: String
                                                ) {
                                                    hidePhoneNumberInputLoading()
                                                    showErrorSnackbar(errorMessage)
                                                }
                                            })
                                    } else {
                                        continueToRegister()
                                    }
                                }

                                override fun onError(error: TAPErrorModel) {
                                    hidePhoneNumberInputLoading()
                                    showErrorSnackbar(error.message)
                                }

                                override fun onError(errorMessage: String) {
                                    onError(TAPErrorModel(ERROR_CODE_OTHERS, errorMessage, ""))
                                }
                            })
                    }

                    override fun onError(error: TAPErrorModel) {
                        hidePhoneNumberInputLoading()
                        showErrorSnackbar(error.message)
                    }

                    override fun onError(errorMessage: String) {
                        onError(TAPErrorModel(ERROR_CODE_OTHERS, errorMessage, ""))
                    }
                }
            )
        }
        false
    }

    private val phoneTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            tv_phone_number.text = TAPUtils.beautifyPhoneNumber(s.toString(), false)
            if (ll_input_error_info?.visibility == View.VISIBLE) {
                et_phone_number?.removeTextChangedListener(this)
                validatePhoneNumber()
                et_phone_number?.addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    /**=============================================================================================
     * Country List
    ==============================================================================================*/

    private fun initCountryList() {
        vm?.countryIsoCode = TAPUtils.getDeviceCountryCode(this)
        vm?.countryListItems = TAPDataManager.getInstance(instanceKey).countryList
        vm?.countryHashMap = vm?.countryListItems?.associateBy({ it.iso2Code }, { it })?.toMutableMap() ?: HashMap()
        vm?.isNeedResetData = false

        if (vm?.countryHashMap?.containsKey(vm?.countryIsoCode) == false ||
            "" == vm?.countryHashMap?.get(vm?.countryIsoCode)?.callingCode
        ) {
            setCountry(defaultCountryID, defaultCallingCode, "")
        }
        else {
            setCountry(
                vm?.countryHashMap?.get(vm?.countryIsoCode)?.countryID ?: 0,
                vm?.countryHashMap?.get(vm?.countryIsoCode)?.callingCode ?: "",
                vm?.countryHashMap?.get(vm?.countryIsoCode)?.flagIconUrl ?: ""
            )
        }
        callCountryListFromAPI()
    }

    private fun setupDataForRecycler(searchKeyword: String): List<TAPCountryRecycleItem> {
        val filteredCountries: MutableList<TAPCountryRecycleItem> = ArrayList()
        val countryListSize: Int = vm?.countryListItems?.size ?: 0
        for (counter in 0 until countryListSize) {
            val country: TAPCountryListItem? =  vm?.countryListItems?.get(counter)
            if (country?.commonName?.contains(searchKeyword, true) == true) {
                val countryInitial = country.commonName[0]
                var previousItemName = ""
                if (counter > 0) {
                    previousItemName = vm?.countryListItems?.get(counter - 1)?.commonName ?: ""
                }

                if (searchKeyword.isEmpty() &&
                    (counter == 0 || (previousItemName.isNotEmpty() && previousItemName[0] != countryInitial))
                ) {
                    val countryRecycleFirstInitial = TAPCountryRecycleItem()
                    countryRecycleFirstInitial.recyclerItemType = RecyclerItemType.COUNTRY_INITIAL
                    countryRecycleFirstInitial.countryInitial = countryInitial
                    filteredCountries.add(countryRecycleFirstInitial)
                }
                val countryRecycleItem = TAPCountryRecycleItem()
                countryRecycleItem.recyclerItemType = RecyclerItemType.COUNTRY_ITEM
                countryRecycleItem.countryListItem = country
                countryRecycleItem.countryInitial = countryInitial
                if (vm?.selectedCountryID == country.countryID) {
                    countryRecycleItem.isSelected = true
                    countryListAdapter.selectedItem = countryRecycleItem
                }
                else {
                    countryRecycleItem.isSelected = false
                }
                filteredCountries.add(countryRecycleItem)
            }
        }
        if (searchKeyword.isNotEmpty()) {
            val filteredCountriesCopy: List<TAPCountryRecycleItem> = ArrayList(filteredCountries)
            var initialCount = 0
            for (counter in filteredCountriesCopy.indices) {
                val country = filteredCountriesCopy[counter]
                val countryInitial = country.countryInitial
                if (counter == 0 ||
                    filteredCountriesCopy[counter - 1].countryListItem.commonName.isNotEmpty() &&
                    filteredCountriesCopy[counter - 1].countryListItem.commonName[0] != countryInitial
                ) {
                    val countryRecycleFirstInitial = TAPCountryRecycleItem()
                    countryRecycleFirstInitial.recyclerItemType = RecyclerItemType.COUNTRY_INITIAL
                    countryRecycleFirstInitial.countryInitial = countryInitial
                    filteredCountries.add(counter + initialCount, countryRecycleFirstInitial)
                    initialCount++
                }
            }
        }
        return filteredCountries
    }

    private fun callCountryListFromAPI(openListOnSuccess: Boolean = false) {
        TAPDataManager.getInstance(instanceKey).getCountryList(object : TAPDefaultDataView<TAPCountryListResponse>() {
            override fun startLoading() {
                runOnUiThread {
                    et_phone_number?.visibility = View.GONE
                    tv_phone_number?.visibility = View.GONE
                    cv_country_flag?.visibility = View.GONE
                    pb_loading_progress_country?.visibility = View.VISIBLE
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: TAPCountryListResponse?) {
                vm?.countryListItems?.clear()
                TAPDataManager.getInstance(instanceKey).saveLastCallCountryTimestamp(System.currentTimeMillis())
                Thread {
                    var defaultCountry: TAPCountryListItem? = null
                    response?.countries?.forEach {
                        vm?.countryListItems?.add(it)
                        vm?.countryHashMap?.put(it.iso2Code, it)

                        if (vm?.countryIsoCode.equals(it.iso2Code, true)) {
                            runOnUiThread {
                                setCountry(it.countryID, it.callingCode, it.flagIconUrl)
                            }
                        }

                        if (it.iso2Code.lowercase() == "id") {
                            defaultCountry = it
                        }
                    }

                    if ("" == tv_country_code?.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        runOnUiThread {
                            setCountry(defaultCountry?.countryID ?: 0, callingCode, defaultCountry?.flagIconUrl ?: "")
                        }
                    }

                    TAPDataManager.getInstance(instanceKey).saveCountryList(vm?.countryListItems)

                    runOnUiThread {
                        searchCountry("")
                        et_phone_number?.visibility = View.VISIBLE
                        tv_phone_number?.visibility = View.VISIBLE
                        cv_country_flag?.visibility = View.VISIBLE
                        pb_loading_progress_country?.visibility = View.GONE
                        ll_country_picker_button?.setOnClickListener(countryPickerClickListener)

                        if (openListOnSuccess) {
                            showCountryListView()
                        }
                    }
                }.start()
            }

            override fun onError(error: TAPErrorModel?) {
                onError(error?.message ?: getString(R.string.tap_error_message_general))
            }

            override fun onError(errorMessage: String?) {
                runOnUiThread {
                    et_phone_number?.visibility = View.VISIBLE
                    tv_phone_number?.visibility = View.VISIBLE
                    cv_country_flag?.visibility = View.VISIBLE
                    pb_loading_progress_country?.visibility = View.GONE
                    setCountry(0, "", "")
                    ll_country_picker_button?.setOnClickListener(countryPickerClickListener)
                    Toast.makeText(this@TAPLoginActivity, getString(R.string.tap_no_countries_found), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun searchCountry(countryKeyword: String?) {
        countryListAdapter.items = setupDataForRecycler(countryKeyword ?: "")
        if (countryListAdapter.items.size == 0) {
            showCountryListEmptyState()
        }
        else {
            hideCountryListEmptyState()
        }
    }

    private fun showCountryListEmptyState() {
        runOnUiThread {
            cl_country_list_empty_state?.visibility = View.VISIBLE
            rv_country_list?.visibility = View.GONE
        }
    }

    private fun hideCountryListEmptyState() {
        runOnUiThread {
            cl_country_list_empty_state?.visibility = View.GONE
            rv_country_list?.visibility = View.VISIBLE
        }
    }

    private val searchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            searchCountry(s.toString())
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val countryPickInterface = TAPCountryListActivity.TAPCountryPickInterface {
        val callingCode: String = it?.callingCode ?: ""
        setCountry(it?.countryID ?: 0, callingCode, it?.flagIconUrl ?: "")
        val textCount = callingCode.length + checkAndEditPhoneNumber().length
        if (textCount > 15) {
            et_phone_number?.setText("")
        }
        showPhoneNumberInputView()

        ll_input_error_info?.visibility = View.GONE
        cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_light)
    }

    /**=============================================================================================
     * Layout Changes
    ==============================================================================================*/

    private val animationDuration = 250L
    private val phoneInputHiddenTranslation = TAPUtils.dpToPx(16).toFloat()
    private val hiddenTranslation = TAPUtils.dpToPx(960).toFloat()

    private fun showViewWithAnimation(view: View?) {
        TAPUtils.dismissKeyboard(this)
        view?.visibility = View.VISIBLE
        view?.animate()
            ?.translationY(0f)
            ?.setDuration(animationDuration)
            ?.setInterpolator(DecelerateInterpolator())
            ?.withEndAction {
                if (view != cl_verification_status_container) {
                    resetVerificationStatus()
                }
            }
            ?.start()
    }

    private fun hideViewWithAnimation(view: View?) {
        hideViewWithAnimation(view, hiddenTranslation)
    }

    private fun hideViewWithAnimation(view: View?, translationY: Float) {
        if (view?.visibility == View.GONE) {
            return
        }
        view?.animate()
            ?.translationY(translationY)
            ?.setDuration(animationDuration)
            ?.setInterpolator(AccelerateInterpolator())
            ?.withEndAction {
                view.visibility = View.GONE
                if (view == cl_country_list_container) {
                    et_search_country_list.setText("")
                }
            }
            ?.start()
    }

    private fun showPhoneNumberInputView() {
        runOnUiThread {
            showViewWithAnimation(sv_login_phone_number_input)
            hideViewWithAnimation(cl_country_list_container)
            hideViewWithAnimation(sv_whatsapp_verification)
            hideViewWithAnimation(sv_otp_verification)
            hideViewWithAnimation(cl_verification_status_container)
        }
    }

    private fun showCountryListView() {
        runOnUiThread {
            showViewWithAnimation(cl_country_list_container)
            hideViewWithAnimation(sv_login_phone_number_input, phoneInputHiddenTranslation)
            hideViewWithAnimation(sv_whatsapp_verification)
            hideViewWithAnimation(sv_otp_verification)
            hideViewWithAnimation(cl_verification_status_container)
        }
    }

    private fun showVerificationView() {
        runOnUiThread {
            showViewWithAnimation(sv_whatsapp_verification)
            hideViewWithAnimation(sv_login_phone_number_input, phoneInputHiddenTranslation)
            hideViewWithAnimation(cl_country_list_container)
            hideViewWithAnimation(sv_otp_verification)
            hideViewWithAnimation(cl_verification_status_container)
        }
    }

    private fun showOtpView() {
        runOnUiThread {
            showViewWithAnimation(sv_otp_verification)
            hideViewWithAnimation(sv_login_phone_number_input, phoneInputHiddenTranslation)
            hideViewWithAnimation(cl_country_list_container)
            hideViewWithAnimation(sv_whatsapp_verification)
            hideViewWithAnimation(cl_verification_status_container)
        }
    }

    private fun showVerificationStatusView() {
        runOnUiThread {
            showViewWithAnimation(cl_verification_status_container)
            hideViewWithAnimation(sv_login_phone_number_input, phoneInputHiddenTranslation)
            hideViewWithAnimation(cl_country_list_container)
            hideViewWithAnimation(sv_otp_verification)
            hideViewWithAnimation(sv_whatsapp_verification)
        }
    }

    private fun showQR() {
        if (!vm?.verification?.qrCode.isNullOrEmpty()) {
            try {
                val identifier = ";base64,"
                var base64 = vm?.verification?.qrCode
                if (base64 != null && base64.contains(identifier)) {
                    base64 = base64.substring(base64.indexOf(identifier) + identifier.length)
                }
                val qrCode = BitmapDrawable(resources, TAPFileUtils.decodeBase64(base64))
                runOnUiThread {
                    iv_qr_code.setImageDrawable(qrCode)
                    iv_qr_code.visibility = View.VISIBLE
                    ll_button_show_qr_code.visibility = View.GONE
                    tv_button_verify?.text = getString(R.string.tap_i_have_sent_the_message)
                    tv_verification_description?.text = getString(R.string.tap_whatsapp_verification_qr_description)
                    tv_button_change_number?.text = getString(R.string.tap_back)
                    ll_button_verify?.setOnClickListener(checkVerificationClickListener)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun hideQR() {
        runOnUiThread {
            iv_qr_code.setImageDrawable(null)
            iv_qr_code.visibility = View.GONE
            ll_button_show_qr_code.visibility = View.VISIBLE
            tv_button_verify?.text = getString(R.string.tap_open_whatsapp)
            tv_verification_description?.text = getString(R.string.tap_whatsapp_verification_description)
            tv_button_change_number?.text = getString(R.string.tap_change_phone_number)
            ll_button_verify?.setOnClickListener(openWhatsAppClickListener)
        }
    }

    private fun showVerificationLoading() {
        runOnUiThread {
            if (iv_verification_status_loading?.animation == null) {
                TAPUtils.rotateAnimateInfinitely(this, iv_verification_status_loading)
            }
            iv_verification_status_loading?.visibility = View.VISIBLE
            iv_verification_status_image?.visibility = View.GONE
            v_verification_status_background?.visibility = View.GONE
            tv_verification_status_redirect_timer?.visibility = View.GONE
            ll_button_continue_to_home?.visibility = View.GONE
            ll_button_retry_verification?.visibility = View.GONE
            tv_verification_status_title?.text = getString(R.string.tap_loading_dots)
            tv_verification_status_description?.text = getString(R.string.tap_verification_loading_description)
            showVerificationStatusView()
        }
    }

    private fun showVerificationSuccess() {
        runOnUiThread {
            iv_verification_status_loading?.clearAnimation()
            iv_verification_status_loading?.visibility = View.GONE
            iv_verification_status_image?.visibility = View.VISIBLE
            v_verification_status_background?.visibility = View.VISIBLE
            tv_verification_status_redirect_timer?.visibility = View.VISIBLE
            ll_button_continue_to_home?.visibility = View.VISIBLE
            ll_button_retry_verification?.visibility = View.GONE
            tv_verification_status_title?.text = getString(R.string.tap_verification_success)
            tv_verification_status_description?.text = getString(R.string.tap_verification_success_description)
            v_verification_status_background?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            iv_verification_status_image?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            iv_verification_status_image?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_rounded_check_green))
            showVerificationStatusView()
            startRedirectTimer()
        }
    }

    private fun showVerificationError() {
        runOnUiThread {
            iv_verification_status_loading?.clearAnimation()
            iv_verification_status_loading?.visibility = View.GONE
            iv_verification_status_image?.visibility = View.VISIBLE
            v_verification_status_background?.visibility = View.VISIBLE
            tv_verification_status_redirect_timer?.visibility = View.GONE
            ll_button_continue_to_home?.visibility = View.GONE
            ll_button_retry_verification?.visibility = View.VISIBLE
            tv_verification_status_title?.text = getString(R.string.tap_verification_error)
            tv_verification_status_description?.text = getString(R.string.tap_verification_error_description)
            v_verification_status_background?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            iv_verification_status_image?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            iv_verification_status_image?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_cancel_white))
            showVerificationStatusView()
        }
    }

    private fun resetVerificationStatus() {
        runOnUiThread {
            iv_verification_status_loading?.clearAnimation()
            iv_verification_status_loading?.visibility = View.GONE
            iv_verification_status_image?.visibility = View.GONE
            v_verification_status_background?.visibility = View.GONE
            tv_verification_status_redirect_timer?.visibility = View.GONE
            ll_button_continue_to_home?.visibility = View.GONE
            ll_button_retry_verification?.visibility = View.GONE
            tv_verification_status_title?.text = ""
            tv_verification_status_description?.text = ""
        }
    }

    private fun startRedirectTimer() {
        val redirectTimer = object : CountDownTimer(3000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tv_verification_status_redirect_timer.text = String.format(getString(R.string.tap_format_redirect_seconds), (millisUntilFinished / 1000L).toInt() + 1)
            }

            override fun onFinish() {
                ll_button_continue_to_home.callOnClick()
            }
        }
        redirectTimer.start()
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = checkAndEditPhoneNumber()
        val phoneNumberWithCode = String.format("%s%s", vm?.countryCallingID ?: "", phoneNumber)
        return if (et_phone_number.text.isEmpty()) {
            tv_input_error_info?.text = getString(R.string.tap_this_field_is_required)
            ll_input_error_info?.visibility = View.VISIBLE
            cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
            false
        }
        else if (!Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumberWithCode.length !in 7..15) {
            tv_input_error_info?.text = getString(R.string.tap_error_invalid_phone_number)
            ll_input_error_info?.visibility = View.VISIBLE
            cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
            false
        }
        else {
            ll_input_error_info?.visibility = View.GONE
            vm?.phoneNumber = phoneNumber
            cl_input_phone_number?.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_light)
            true
        }
    }

    private fun showErrorSnackbar(errorMessage: String?) {
        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            tap_custom_snackbar?.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(R.string.tap_error_message_general)
            )
        }
        else {
            tap_custom_snackbar?.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                R.drawable.tap_ic_wifi_off_red,
                R.string.tap_error_check_your_network
            )
        }
    }

    /**=============================================================================================
     * WhatsApp Verification
    ==============================================================================================*/

    private fun requestWhatsAppVerification() {
        if (vm != null &&
            vm!!.nextWhatsAppRequestTimestamp > System.currentTimeMillis() &&
            vm?.lastRequestWhatsAppPhoneNumber == vm?.phoneNumber
        ) {
            val waitSeconds = ((vm!!.nextWhatsAppRequestTimestamp - System.currentTimeMillis()) / 1000).toInt()
            showErrorSnackbar(String.format(getString(R.string.tap_d_format_error_verification_wait), waitSeconds))
            return
        }
        TAPDataManager.getInstance(instanceKey).requestWhatsAppVerification(
            vm?.selectedCountryID ?: 0,
            vm?.phoneNumber ?: "",
            object : TAPDefaultDataView<TAPOTPResponse>() {
                override fun startLoading() {
                    showPhoneNumberInputLoading()
                }

                override fun endLoading() {
                    hidePhoneNumberInputLoading()
                }

                override fun onSuccess(response: TAPOTPResponse?) {
                    vm?.verification = response?.verification
                    if (response?.isSuccess == true) {
                        vm?.nextWhatsAppRequestTimestamp = (response.nextRequestSeconds * 1000).toLong() + System.currentTimeMillis()
                        vm?.lastRequestWhatsAppPhoneNumber = vm?.phoneNumber ?: ""
                        tv_verification_phone_number?.text = TAPUtils.beautifyPhoneNumber(String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber), true)
                        showVerificationView()
                    }
                    else {
                        onError(response?.message ?: response?.whatsAppFailureReason)
                    }
                }

                override fun onError(error: TAPErrorModel?) {
                    onError(error?.message)
                }

                override fun onError(errorMessage: String?) {
                    endLoading()
                    showErrorSnackbar(errorMessage)
                }
            }
        )
    }

    private val openWhatsAppClickListener = OnClickListener {
        openWhatsAppLink()
    }

    private val checkVerificationClickListener = OnClickListener {
        checkWhatsAppVerification(true)
    }

    private fun openWhatsAppLink() {
        val waLink = vm?.verification?.waLink ?: ""
        if (waLink.isNotEmpty() && Patterns.WEB_URL.matcher(waLink).matches()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(waLink)
                startActivity(intent)
                vm?.isCheckWhatsAppVerificationPending = true
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkWhatsAppVerification(resetAttempt: Boolean) {
        //vm?.isCheckWhatsAppVerificationPending = false
        if (resetAttempt) {
            vm?.checkVerificationAttempts = 0
        }
        showVerificationLoading()
        val phoneWithCode = String.format("%s%s", vm?.countryCallingID, vm?.phoneNumber)
        TAPDataManager.getInstance(instanceKey).checkWhatsAppVerification(
            phoneWithCode,
            vm?.verification?.id,
            object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                override fun onSuccess(response: TAPLoginOTPVerifyResponse?) {
                    onVerificationSuccess(response)
                }

                override fun onError(error: TAPErrorModel?) {
                    //vm?.isCheckWhatsAppVerificationPending = true
                    vm?.checkVerificationAttempts = (vm?.checkVerificationAttempts ?: 0) + 1

                    if ((vm?.checkVerificationAttempts ?: 0) >= 5) {
                        showVerificationError()
                    }
                    else {
                        vm?.checkVerificationTimer?.cancel()
                        vm?.checkVerificationTimer = Timer()
                        vm?.checkVerificationTimer?.schedule(object : TimerTask() {
                            override fun run() {
                                checkWhatsAppVerification(false)
                            }
                        }, 3000L)
                    }
                }

                override fun onError(errorMessage: String?) {
                    onError(TAPErrorModel(ERROR_CODE_OTHERS, errorMessage, ""))
                }
            }
        )
    }

    /**=============================================================================================
     * OTP Verification
    ==============================================================================================*/

    private fun setupOtpView() {
        runOnUiThread {
            clearOtpEditText()
            setupTimer()
            Handler(Looper.getMainLooper()).postDelayed({
                et_otp_code?.requestFocus()
                TAPUtils.showKeyboard(this, et_otp_code)
            }, animationDuration)
        }
    }

    private fun clearOtpEditText() {
        runOnUiThread {
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
    }

    private fun showVerifyOtpFailed() {
        runOnUiThread {
            et_otp_code.setText("")
            tv_did_not_receive_otp.text = resources.getText(R.string.tap_error_invalid_otp)
            tv_did_not_receive_otp.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))

            et_otp_code?.requestFocus()
            TAPUtils.showKeyboard(this, et_otp_code)
        }
    }

    private val otpTextWatcher = object : TextWatcher {
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
                    v_pointer_1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapTransparentBlack1940))
                    v_pointer_6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

                    tv_otp_filled_5.text = String.format("%s", s[4])
                    tv_otp_filled_6.text = ""
                }
                6 -> {
                    tv_otp_filled_6.text = String.format("%s", s[5])
                    verifyOtp()
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

    private val otpEditorListener = OnEditorActionListener { textView, i, keyEvent ->
        if (textView.text.length >= 6) {
            verifyOtp()
        }
        true
    }

    private fun showOtpTimer() {
        runOnUiThread {
            tv_otp_timer?.visibility = View.VISIBLE
            ll_request_otp_again?.visibility = View.GONE
            ll_loading_otp?.visibility = View.GONE
            ll_otp_sent?.visibility = View.GONE
        }
    }

    private fun showRequestOtpAgain() {
        runOnUiThread {
            ll_request_otp_again?.visibility = View.VISIBLE
            tv_otp_timer?.visibility = View.GONE
            ll_loading_otp?.visibility = View.GONE
            ll_otp_sent?.visibility = View.GONE
        }
    }

    private fun showResendOtpLoading() {
        runOnUiThread {
            ll_request_otp_again?.visibility = View.GONE
            tv_otp_timer?.visibility = View.GONE
            ll_loading_otp?.visibility = View.VISIBLE
            ll_otp_sent?.visibility = View.GONE
        }
    }

    private fun setupTimer() {
        showOtpTimer()
        setAndStartTimer()
    }

    private fun setAndStartTimer() {
        if (vm == null || vm!!.nextOtpRequestTimestamp < System.currentTimeMillis()) {
            return
        }
        tv_did_not_receive_otp?.text = resources.getText(R.string.tap_didnt_receive_the_6_digit_otp)
        tv_did_not_receive_otp?.setTextColor(ContextCompat.getColor(this, R.color.tapColorTextDark))

        cancelTimer()
        vm?.otpTimer = object : CountDownTimer(vm!!.nextOtpRequestTimestamp - System.currentTimeMillis(), 1000) {
            override fun onFinish() {
                showRequestOtpAgain()
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val minuteLeft = timeLeft / 60
                val secondLeft = timeLeft - (60 * (timeLeft / 60))
                when (minuteLeft) {
                    0L -> {
                        try {
                            if (10 > secondLeft) {
                                tv_otp_timer.text = "Wait 0:0$secondLeft"
                            }
                            else {
                                tv_otp_timer.text = "Wait 0:$secondLeft"
                            }
                        }
                        catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                    else -> {
                        try {
                            if (10 > secondLeft) {
                                tv_otp_timer.text = "Wait $minuteLeft:0$secondLeft"
                            }
                            else {
                                tv_otp_timer.text = "Wait $minuteLeft:$secondLeft"
                            }
                        }
                        catch (e: Exception) {
                            cancelTimer()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }.start()
    }

    private fun cancelTimer() {
        vm?.otpTimer?.cancel()
    }

    private fun requestOtp(isResend: Boolean = false) {
        if (vm != null &&
            vm!!.nextOtpRequestTimestamp > System.currentTimeMillis() &&
            vm?.lastRequestOtpPhoneNumber == vm?.phoneNumber
        ) {
            val waitSeconds = ((vm!!.nextOtpRequestTimestamp - System.currentTimeMillis()) / 1000).toInt()
            showErrorSnackbar(String.format(getString(R.string.tap_d_format_error_otp_wait), waitSeconds))
            return
        }
        TAPDataManager.getInstance(instanceKey).requestOTPLogin(
            vm?.selectedCountryID ?: 0,
            vm?.phoneNumber ?: "",
            "",
            object : TAPDefaultDataView<TAPOTPResponse>() {
                override fun startLoading() {
                    et_otp_code.isEnabled = false
                    showPhoneNumberInputLoading(true)
                    showResendOtpLoading()
                }

                override fun endLoading() {
                    et_otp_code.isEnabled = true
                    hidePhoneNumberInputLoading()
                }

                override fun onSuccess(response: TAPOTPResponse?) {
                    vm?.verification = response?.verification
                    vm?.otpID = response?.otpID ?: 0L
                    vm?.otpKey = response?.otpKey
                    if (response?.isSuccess == true) {
                        if (response.channel == "whatsapp") {
                            tv_otp_description.text = getString(R.string.tap_otp_verification_whatsapp_description)
                            iv_otp_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, R.drawable.tap_ic_whatsapp))
                        }
                        else {
                            tv_otp_description.text = getString(R.string.tap_otp_verification_sms_description)
                            iv_otp_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, R.drawable.tap_ic_sms_circle))
                        }
                        tv_otp_phone_number?.text = String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber)
                        vm?.nextOtpRequestTimestamp = (response.nextRequestSeconds * 1000).toLong() + System.currentTimeMillis()
                        vm?.lastRequestOtpPhoneNumber = vm?.phoneNumber ?: ""
                        showOtpView()
                        setupOtpView()
                        Handler(Looper.getMainLooper()).postDelayed({ showOtpTimer() }, 2000)

                        if (isResend) {
                            clearOtpEditText()
                            ll_request_otp_again.visibility = View.GONE
                            ll_loading_otp.visibility = View.GONE
                            tv_otp_timer.visibility = View.GONE
                            ll_otp_sent.visibility = View.VISIBLE
                            tap_custom_snackbar?.show(
                                TapCustomSnackbarView.Companion.Type.DEFAULT,
                                R.drawable.tap_ic_rounded_check,
                                R.string.tap_otp_successfully_sent
                            )
                        }
                    }
                    else {
                        onError(response?.message ?: response?.whatsAppFailureReason)
                    }
                }

                override fun onError(error: TAPErrorModel?) {
                    onError(error?.message)
                }

                override fun onError(errorMessage: String?) {
                    endLoading()
                    showErrorSnackbar(errorMessage)
                    showRequestOtpAgain()
                }
            }
        )
    }

    private fun verifyOtp() {
        if (vm == null || vm!!.otpID <= 0L || vm?.otpKey.isNullOrEmpty()) {
            return
        }
        TAPDataManager.getInstance(instanceKey).verifyOTPLogin(
            vm!!.otpID,
            vm!!.otpKey,
            et_otp_code.text.toString(),
            object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                override fun startLoading() {
                    vm?.loadingDialog = TapLoadingDialog.Builder(this@TAPLoginActivity).show()
                    et_otp_code.isEnabled = false
                }

                override fun endLoading() {
                    vm?.loadingDialog?.dismiss()
                    vm?.loadingDialog = null
                    et_otp_code.isEnabled = true
                }

                override fun onSuccess(response: TAPLoginOTPVerifyResponse?) {
                    onVerificationSuccess(response)
                }

                override fun onError(error: TAPErrorModel?) {
                    onError(error?.message ?: getString(R.string.tap_error_message_general))
                }

                override fun onError(errorMessage: String) {
                    endLoading()
                    if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this@TAPLoginActivity)) {
                        showVerifyOtpFailed()
                    }
                    else {
                        showErrorSnackbar(errorMessage)
                    }
                }
            })

    }

    /**=============================================================================================
     * Authentication
    ==============================================================================================*/

    private fun continueToHome() {
        TAPApiManager.getInstance(instanceKey).isLoggedOut = false
        if (BuildConfig.DEBUG) {
            TapDevLandingActivity.start(this, instanceKey)
        }
        else {
            TapUIRoomListActivity.start(this, instanceKey)
        }
        finish()
    }

    private fun continueToRegister() {
        TAPRegisterActivity.start(
            this,
            instanceKey,
            vm?.selectedCountryID ?: 0,
            vm?.countryCallingID ?: "",
            vm?.countryFlagUrl ?: "",
            vm?.phoneNumber ?: ""
        )
    }

    private fun onVerificationSuccess(response: TAPLoginOTPVerifyResponse?) {
        if (response?.isRegistered == true && !response.ticket.isNullOrEmpty()) {
            // Login
            authenticateTapTalk(response.ticket)
        }
        else {
            // Register
            showVerificationSuccess()
            ll_button_continue_to_home?.setOnClickListener { continueToRegister() }
        }
    }

    private fun authenticateTapTalk(ticket: String?) {
        if (ticket.isNullOrEmpty()) {
            showVerificationError()
            return
        }
        TapTalk.authenticateWithAuthTicket(
            instanceKey,
            ticket,
            true,
            object : TapCommonListener() {
                override fun onSuccess(successMessage: String?) {
                    showVerificationSuccess()
                    ll_button_continue_to_home?.setOnClickListener { continueToHome() }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    showVerificationError()
                }
            }
        )
    }
}
