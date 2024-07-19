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
import io.taptalk.TapTalkSample.databinding.TapActivityLoginBinding
import java.util.Timer
import java.util.TimerTask

class TAPLoginActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityLoginBinding
    private lateinit var countryListAdapter: TAPCountryListAdapter
    private lateinit var redirectTimer: CountDownTimer
    private var vm: TAPLoginViewModel? = null

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
        vb = TapActivityLoginBinding.inflate(layoutInflater)
        setContentView(vb.root)
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
        if (vb.layoutLoginCountryList.clCountryListContainer.visibility == View.VISIBLE ||
            vb.layoutLoginWhatsappVerification.svWhatsappVerification.visibility == View.VISIBLE ||
            vb.layoutLoginOtp.svOtpVerification.visibility == View.VISIBLE
        ) {
            if (vb.layoutLoginWhatsappVerification.ivQrCode.visibility == View.VISIBLE) {
                hideQR()
                return
            }
            showPhoneNumberInputView()
            return
        }
        if (vb.layoutLoginVerificationStatus.llButtonRetryVerification.visibility == View.VISIBLE) {
            showVerificationView()
            return
        }
        if (vb.layoutLoginVerificationStatus.ivVerificationStatusLoading?.visibility == View.VISIBLE) {
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
        vb.layoutLoginInput.etPhoneNumber.filters = vb.layoutLoginInput.etPhoneNumber.filters + numberFilter
        vb.layoutLoginInput.etPhoneNumber.addTextChangedListener(phoneTextWatcher)

        try {
            countryListAdapter = TAPCountryListAdapter(setupDataForRecycler(""), countryPickInterface)
            vb.layoutLoginCountryList.rvCountryList.adapter = countryListAdapter
            vb.layoutLoginCountryList.rvCountryList.setHasFixedSize(true)
            vb.layoutLoginCountryList.rvCountryList.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        vb.layoutLoginCountryList.ivButtonCloseCountryList.setOnClickListener(backButtonClickListener)
        vb.layoutLoginWhatsappVerification.llButtonChangeNumber.setOnClickListener(backButtonClickListener)
        vb.layoutLoginOtp.llButtonChangeNumberOtp.setOnClickListener(backButtonClickListener)
        vb.layoutLoginInput.llButtonWhatsapp.setOnClickListener(loginViaWhatsAppClickListener)
        vb.layoutLoginInput.llButtonOtp.setOnClickListener(loginViaOTPClickListener)
        vb.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(openWhatsAppClickListener)
        vb.layoutLoginWhatsappVerification.llButtonShowQrCode.setOnClickListener { showQR() }
        vb.layoutLoginOtp.llRequestOtpAgain.setOnClickListener { requestOtp(true) }
        vb.layoutLoginVerificationStatus.llButtonRetryVerification.setOnClickListener { showVerificationView() }
        vb.clLoginContainer.setOnClickListener { TAPUtils.dismissKeyboard(this) }
        vb.layoutLoginInput.clLoginInputContainer.setOnClickListener { TAPUtils.dismissKeyboard(this, vb.layoutLoginInput.etPhoneNumber) }
        vb.layoutLoginOtp.clOtpContainer.setOnClickListener { TAPUtils.dismissKeyboard(this, vb.layoutLoginOtp.etOtpCode) }

        vb.layoutLoginCountryList.etSearchCountryList.addTextChangedListener(searchTextWatcher)
        vb.layoutLoginOtp.etOtpCode.addTextChangedListener(otpTextWatcher)
        vb.layoutLoginOtp.etOtpCode.setOnEditorActionListener(otpEditorListener)

        showPhoneNumberInputView()

        if (BuildConfig.BUILD_TYPE == "dev") {
            vb.layoutLoginInput.llButtonOtp.setOnLongClickListener(devPhoneNumberLongClickListener)
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
            vb.layoutLoginInput.tvCountryCode.text = "+$callingCode"
            vb.layoutLoginInput.tvCountryCode.hint = ""
            vb.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
            vb.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
        }
        else {
            vb.layoutLoginInput.tvCountryCode.text = ""
            vb.layoutLoginInput.tvCountryCode.hint = getString(io.taptalk.TapTalk.R.string.tap_hint_select_country)
            vb.layoutLoginInput.etPhoneNumber.visibility = View.GONE
            vb.layoutLoginInput.tvPhoneNumber.visibility = View.GONE
        }
        vm?.selectedCountryID = countryID
        vm?.countryCallingID = callingCode
        vm?.countryFlagUrl = flagIconUrl ?: ""

        // Re-add max length filter
        val filters = vb.layoutLoginInput.etPhoneNumber.filters.toList().filter { it.javaClass != InputFilter.LengthFilter::class.java }
        vb.layoutLoginInput.etPhoneNumber.filters = filters.toTypedArray() + InputFilter.LengthFilter(15 - callingCode.length)

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(vb.layoutLoginInput.ivCountryFlag)
        }
        else {
            vb.layoutLoginInput.ivCountryFlag.setImageResource(io.taptalk.TapTalk.R.drawable.tap_ic_default_flag)
        }
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = vb.layoutLoginInput.etPhoneNumber.text.toString().replace("-", "").trim()
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
                vb.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.GONE
                vb.layoutLoginInput.pbButtonOtpLoading.visibility = View.VISIBLE
                vb.layoutLoginInput.ivButtonWhatsapp.visibility = View.VISIBLE
                vb.layoutLoginInput.ivButtonOtp.visibility = View.GONE
            }
            else {
                vb.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.VISIBLE
                vb.layoutLoginInput.pbButtonOtpLoading.visibility = View.GONE
                vb.layoutLoginInput.ivButtonWhatsapp.visibility = View.GONE
                vb.layoutLoginInput.ivButtonOtp.visibility = View.VISIBLE
            }
            vb.layoutLoginInput.ivCountryChevron.alpha = 0.4f
            vb.layoutLoginInput.tvCountryCode.alpha = 0.4f
            vb.layoutLoginInput.etPhoneNumber.alpha = 0.4f
            vb.layoutLoginInput.tvPhoneNumber.alpha = 0.4f
            vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
            vb.layoutLoginInput.llButtonWhatsapp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
            vb.layoutLoginInput.llButtonOtp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_border_inactive)
            ImageViewCompat.setImageTintList(vb.layoutLoginInput.ivButtonOtp, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1920)))
            vb.layoutLoginInput.tvButtonWhatsapp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
            vb.layoutLoginInput.tvButtonOtp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
            vb.layoutLoginInput.etPhoneNumber.isEnabled = false
            vb.layoutLoginInput.llCountryPickerButton.setOnClickListener(null)
            vb.layoutLoginInput.llButtonWhatsapp.setOnClickListener(null)
            vb.layoutLoginInput.llButtonOtp.setOnClickListener(null)
        }
    }

    private fun hidePhoneNumberInputLoading() {
        runOnUiThread {
            vb.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.GONE
            vb.layoutLoginInput.pbButtonOtpLoading.visibility = View.GONE
            vb.layoutLoginInput.ivButtonWhatsapp.visibility = View.VISIBLE
            vb.layoutLoginInput.ivButtonOtp.visibility = View.VISIBLE
            vb.layoutLoginInput.ivCountryChevron.alpha = 1f
            vb.layoutLoginInput.tvCountryCode.alpha = 1f
            vb.layoutLoginInput.etPhoneNumber.alpha = 1f
            vb.layoutLoginInput.tvPhoneNumber.alpha = 1f
            vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
            vb.layoutLoginInput.llButtonWhatsapp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_active_ripple)
            vb.layoutLoginInput.llButtonOtp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_border_ripple)
            ImageViewCompat.setImageTintList(vb.layoutLoginInput.ivButtonOtp, null)
            vb.layoutLoginInput.tvButtonWhatsapp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapButtonLabelColor))
            vb.layoutLoginInput.tvButtonOtp.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
            vb.layoutLoginInput.etPhoneNumber.isEnabled = true
            vb.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)
            vb.layoutLoginInput.llButtonWhatsapp.setOnClickListener(loginViaWhatsAppClickListener)
            vb.layoutLoginInput.llButtonOtp.setOnClickListener(loginViaOTPClickListener)
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
            vb.layoutLoginInput.tvPhoneNumber.text = TAPUtils.beautifyPhoneNumber(s.toString(), false)
            if (vb.layoutLoginInput.llInputErrorInfo.visibility == View.VISIBLE) {
                vb.layoutLoginInput.etPhoneNumber.removeTextChangedListener(this)
                validatePhoneNumber()
                vb.layoutLoginInput.etPhoneNumber.addTextChangedListener(this)
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
                    vb.layoutLoginInput.etPhoneNumber.visibility = View.GONE
                    vb.layoutLoginInput.tvPhoneNumber.visibility = View.GONE
                    vb.layoutLoginInput.cvCountryFlag.visibility = View.GONE
                    vb.layoutLoginInput.pbLoadingProgressCountry.visibility = View.VISIBLE
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

                    if ("" == vb.layoutLoginInput.tvCountryCode.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        runOnUiThread {
                            setCountry(defaultCountry?.countryID ?: 0, callingCode, defaultCountry?.flagIconUrl ?: "")
                        }
                    }

                    TAPDataManager.getInstance(instanceKey).saveCountryList(vm?.countryListItems)

                    runOnUiThread {
                        searchCountry("")
                        vb.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
                        vb.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
                        vb.layoutLoginInput.cvCountryFlag.visibility = View.VISIBLE
                        vb.layoutLoginInput.pbLoadingProgressCountry.visibility = View.GONE
                        vb.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)

                        if (openListOnSuccess) {
                            showCountryListView()
                        }
                    }
                }.start()
            }

            override fun onError(error: TAPErrorModel?) {
                onError(error?.message ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
            }

            override fun onError(errorMessage: String?) {
                runOnUiThread {
                    vb.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
                    vb.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
                    vb.layoutLoginInput.cvCountryFlag.visibility = View.VISIBLE
                    vb.layoutLoginInput.pbLoadingProgressCountry.visibility = View.GONE
                    setCountry(0, "", "")
                    vb.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)
                    Toast.makeText(this@TAPLoginActivity, getString(io.taptalk.TapTalk.R.string.tap_no_countries_found), Toast.LENGTH_SHORT).show()
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
            vb.layoutLoginCountryList.clCountryListEmptyState.visibility = View.VISIBLE
            vb.layoutLoginCountryList.rvCountryList.visibility = View.GONE
        }
    }

    private fun hideCountryListEmptyState() {
        runOnUiThread {
            vb.layoutLoginCountryList.clCountryListEmptyState.visibility = View.GONE
            vb.layoutLoginCountryList.rvCountryList.visibility = View.VISIBLE
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
            vb.layoutLoginInput.etPhoneNumber.setText("")
        }
        showPhoneNumberInputView()

        vb.layoutLoginInput.llInputErrorInfo.visibility = View.GONE
        vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
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
                if (view != vb.layoutLoginVerificationStatus.clVerificationStatusContainer) {
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
                if (view == vb.layoutLoginCountryList.clCountryListContainer) {
                    vb.layoutLoginCountryList.etSearchCountryList.setText("")
                }
            }
            ?.start()
    }

    private fun showPhoneNumberInputView() {
        runOnUiThread {
            showViewWithAnimation(vb.layoutLoginInput.svLoginPhoneNumberInput)
            hideViewWithAnimation(vb.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(vb.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(vb.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(vb.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showCountryListView() {
        runOnUiThread {
            showViewWithAnimation(vb.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(vb.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(vb.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(vb.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(vb.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showVerificationView() {
        runOnUiThread {
            showViewWithAnimation(vb.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(vb.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(vb.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(vb.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(vb.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showOtpView() {
        runOnUiThread {
            showViewWithAnimation(vb.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(vb.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(vb.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(vb.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(vb.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showVerificationStatusView() {
        runOnUiThread {
            showViewWithAnimation(vb.layoutLoginVerificationStatus.clVerificationStatusContainer)
            hideViewWithAnimation(vb.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(vb.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(vb.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(vb.layoutLoginWhatsappVerification.svWhatsappVerification)
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
                    vb.layoutLoginWhatsappVerification.ivQrCode.setImageDrawable(qrCode)
                    vb.layoutLoginWhatsappVerification.ivQrCode.visibility = View.VISIBLE
                    vb.layoutLoginWhatsappVerification.llButtonShowQrCode.visibility = View.GONE
                    vb.layoutLoginWhatsappVerification.tvButtonVerify.text = getString(R.string.tap_i_have_sent_the_message)
                    vb.layoutLoginWhatsappVerification.tvVerificationDescription.text = getString(R.string.tap_whatsapp_verification_qr_description)
                    vb.layoutLoginWhatsappVerification.tvButtonChangeNumber.text = getString(io.taptalk.TapTalk.R.string.tap_back)
                    vb.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(checkVerificationClickListener)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun hideQR() {
        runOnUiThread {
            vb.layoutLoginWhatsappVerification.ivQrCode.setImageDrawable(null)
            vb.layoutLoginWhatsappVerification.ivQrCode.visibility = View.GONE
            vb.layoutLoginWhatsappVerification.llButtonShowQrCode.visibility = View.VISIBLE
            vb.layoutLoginWhatsappVerification.tvButtonVerify.text = getString(R.string.tap_open_whatsapp)
            vb.layoutLoginWhatsappVerification.tvVerificationDescription.text = getString(R.string.tap_whatsapp_verification_description)
            vb.layoutLoginWhatsappVerification.tvButtonChangeNumber.text = getString(R.string.tap_change_phone_number)
            vb.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(openWhatsAppClickListener)
        }
    }

    private fun showVerificationLoading() {
        runOnUiThread {
            if (vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.animation == null) {
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutLoginVerificationStatus.ivVerificationStatusLoading)
            }
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.GONE
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.GONE
            vb.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            vb.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(io.taptalk.TapTalk.R.string.tap_loading_dots)
            vb.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_loading_description)
            showVerificationStatusView()
        }
    }

    private fun showVerificationSuccess() {
        runOnUiThread {
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            vb.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(R.string.tap_verification_success)
            vb.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_success_description)
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_rounded_check_green))
            showVerificationStatusView()
            startRedirectTimer()
        }
    }

    private fun showVerificationError() {
        runOnUiThread {
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.VISIBLE
            vb.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(R.string.tap_verification_error)
            vb.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_error_description)
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_cancel_white))
            showVerificationStatusView()
        }
    }

    private fun resetVerificationStatus() {
        runOnUiThread {
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            vb.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            vb.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.GONE
            vb.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.GONE
            vb.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            vb.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            vb.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = ""
            vb.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = ""
        }
    }

    private fun startRedirectTimer() {
        if (this::redirectTimer.isInitialized) {
            redirectTimer.cancel()
        }
        redirectTimer = object : CountDownTimer(3000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                vb.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.text = String.format(getString(R.string.tap_format_redirect_seconds), (millisUntilFinished / 1000L).toInt() + 1)
            }

            override fun onFinish() {
                vb.layoutLoginVerificationStatus.llButtonContinueToHome.callOnClick()
            }
        }
        redirectTimer.start()
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = checkAndEditPhoneNumber()
        val phoneNumberWithCode = String.format("%s%s", vm?.countryCallingID ?: "", phoneNumber)
        var isPhonePatternValid = false
        try {
            isPhonePatternValid = Patterns.PHONE.matcher(phoneNumber).matches()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return if (vb.layoutLoginInput.etPhoneNumber.text.isEmpty()) {
            vb.layoutLoginInput.tvInputErrorInfo.text = getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required)
            vb.layoutLoginInput.llInputErrorInfo.visibility = View.VISIBLE
            vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
            false
        }
        else if (!isPhonePatternValid || phoneNumberWithCode.length !in 7..15) {
            vb.layoutLoginInput.tvInputErrorInfo.text = getString(io.taptalk.TapTalk.R.string.tap_error_invalid_phone_number)
            vb.layoutLoginInput.llInputErrorInfo.visibility = View.VISIBLE
            vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
            false
        }
        else {
            vb.layoutLoginInput.llInputErrorInfo.visibility = View.GONE
            vm?.phoneNumber = phoneNumber
            vb.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
            true
        }
    }

    private fun showErrorSnackbar(errorMessage: String?) {
        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            vb.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
            )
        }
        else {
            vb.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_wifi_off_red,
                io.taptalk.TapTalk.R.string.tap_error_check_your_network
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
                        vb.layoutLoginWhatsappVerification.tvVerificationPhoneNumber.text = TAPUtils.beautifyPhoneNumber(String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber), true)
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
        try {
            val waLink = vm?.verification?.waLink ?: ""
            if (waLink.isNotEmpty() && Patterns.WEB_URL.matcher(waLink).matches()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(waLink)
                startActivity(intent)
                vm?.isCheckWhatsAppVerificationPending = true
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
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
                vb.layoutLoginOtp.etOtpCode.requestFocus()
                TAPUtils.showKeyboard(this, vb.layoutLoginOtp.etOtpCode)
            }, animationDuration)
        }
    }

    private fun clearOtpEditText() {
        runOnUiThread {
            vb.layoutLoginOtp.vPointer1.visibility = View.VISIBLE
            vb.layoutLoginOtp.vPointer2.visibility = View.VISIBLE
            vb.layoutLoginOtp.vPointer3.visibility = View.VISIBLE
            vb.layoutLoginOtp.vPointer4.visibility = View.VISIBLE
            vb.layoutLoginOtp.vPointer5.visibility = View.VISIBLE
            vb.layoutLoginOtp.vPointer6.visibility = View.VISIBLE
            vb.layoutLoginOtp.tvOtpFilled1.text = ""
            vb.layoutLoginOtp.tvOtpFilled2.text = ""
            vb.layoutLoginOtp.tvOtpFilled3.text = ""
            vb.layoutLoginOtp.tvOtpFilled4.text = ""
            vb.layoutLoginOtp.tvOtpFilled5.text = ""
            vb.layoutLoginOtp.tvOtpFilled6.text = ""
            vb.layoutLoginOtp.etOtpCode.setText("")
        }
    }

    private fun showVerifyOtpFailed() {
        runOnUiThread {
            vb.layoutLoginOtp.etOtpCode.setText("")
            vb.layoutLoginOtp.tvDidNotReceiveOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_error_invalid_otp)
            vb.layoutLoginOtp.tvDidNotReceiveOtp.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))

            vb.layoutLoginOtp.etOtpCode.requestFocus()
            TAPUtils.showKeyboard(this, vb.layoutLoginOtp.etOtpCode)
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
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.layoutLoginOtp.tvOtpFilled1.text = String.format("%s", s[0])
                    vb.layoutLoginOtp.tvOtpFilled2.text = ""
                    vb.layoutLoginOtp.tvOtpFilled3.text = ""
                    vb.layoutLoginOtp.tvOtpFilled4.text = ""
                    vb.layoutLoginOtp.tvOtpFilled5.text = ""
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                2 -> {
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.layoutLoginOtp.tvOtpFilled2.text = String.format("%s", s[1])
                    vb.layoutLoginOtp.tvOtpFilled3.text = ""
                    vb.layoutLoginOtp.tvOtpFilled4.text = ""
                    vb.layoutLoginOtp.tvOtpFilled5.text = ""
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                3 -> {
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.layoutLoginOtp.tvOtpFilled3.text = String.format("%s", s[2])
                    vb.layoutLoginOtp.tvOtpFilled4.text = ""
                    vb.layoutLoginOtp.tvOtpFilled5.text = ""
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                4 -> {
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.layoutLoginOtp.tvOtpFilled4.text = String.format("%s", s[3])
                    vb.layoutLoginOtp.tvOtpFilled5.text = ""
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                5 -> {
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

                    vb.layoutLoginOtp.tvOtpFilled5.text = String.format("%s", s[4])
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                6 -> {
                    vb.layoutLoginOtp.tvOtpFilled6.text = String.format("%s", s[5])
                    verifyOtp()
                }
                else -> {
                    vb.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    vb.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    vb.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    vb.layoutLoginOtp.tvOtpFilled1.text = ""
                    vb.layoutLoginOtp.tvOtpFilled2.text = ""
                    vb.layoutLoginOtp.tvOtpFilled3.text = ""
                    vb.layoutLoginOtp.tvOtpFilled4.text = ""
                    vb.layoutLoginOtp.tvOtpFilled5.text = ""
                    vb.layoutLoginOtp.tvOtpFilled6.text = ""
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
            vb.layoutLoginOtp.tvOtpTimer.visibility = View.VISIBLE
            vb.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
            vb.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
            vb.layoutLoginOtp.llOtpSent.visibility = View.GONE
        }
    }

    private fun showRequestOtpAgain() {
        runOnUiThread {
            vb.layoutLoginOtp.llRequestOtpAgain.visibility = View.VISIBLE
            vb.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
            vb.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
            vb.layoutLoginOtp.llOtpSent.visibility = View.GONE
        }
    }

    private fun showResendOtpLoading() {
        runOnUiThread {
            vb.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
            vb.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
            vb.layoutLoginOtp.llLoadingOtp.visibility = View.VISIBLE
            vb.layoutLoginOtp.llOtpSent.visibility = View.GONE
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
        vb.layoutLoginOtp.tvDidNotReceiveOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_didnt_receive_the_6_digit_otp)
        vb.layoutLoginOtp.tvDidNotReceiveOtp.setTextColor(ContextCompat.getColor(this, R.color.tapColorTextDark))

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
                                vb.layoutLoginOtp.tvOtpTimer.text = "Wait 0:0$secondLeft"
                            }
                            else {
                                vb.layoutLoginOtp.tvOtpTimer.text = "Wait 0:$secondLeft"
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
                                vb.layoutLoginOtp.tvOtpTimer.text = "Wait $minuteLeft:0$secondLeft"
                            }
                            else {
                                vb.layoutLoginOtp.tvOtpTimer.text = "Wait $minuteLeft:$secondLeft"
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
                    vb.layoutLoginOtp.etOtpCode.isEnabled = false
                    showPhoneNumberInputLoading(true)
                    showResendOtpLoading()
                }

                override fun endLoading() {
                    vb.layoutLoginOtp.etOtpCode.isEnabled = true
                    hidePhoneNumberInputLoading()
                }

                override fun onSuccess(response: TAPOTPResponse?) {
                    vm?.verification = response?.verification
                    vm?.otpID = response?.otpID ?: 0L
                    vm?.otpKey = response?.otpKey
                    if (response?.isSuccess == true) {
                        if (response.channel == "whatsapp") {
                            vb.layoutLoginOtp.tvOtpDescription.text = getString(R.string.tap_otp_verification_whatsapp_description)
                            vb.layoutLoginOtp.ivOtpIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, io.taptalk.TapTalk.R.drawable.tap_ic_whatsapp))
                        }
                        else {
                            vb.layoutLoginOtp.tvOtpDescription.text = getString(R.string.tap_otp_verification_sms_description)
                            vb.layoutLoginOtp.ivOtpIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, R.drawable.tap_ic_sms_circle))
                        }
                        vb.layoutLoginOtp.tvOtpPhoneNumber.text = TAPUtils.beautifyPhoneNumber(String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber), true)
                        vm?.nextOtpRequestTimestamp = (response.nextRequestSeconds * 1000).toLong() + System.currentTimeMillis()
                        vm?.lastRequestOtpPhoneNumber = vm?.phoneNumber ?: ""
                        showOtpView()
                        setupOtpView()
                        Handler(Looper.getMainLooper()).postDelayed({ showOtpTimer() }, 2000)

                        if (isResend) {
                            clearOtpEditText()
                            vb.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
                            vb.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
                            vb.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
                            vb.layoutLoginOtp.llOtpSent.visibility = View.VISIBLE
                            vb.tapCustomSnackbar.show(
                                TapCustomSnackbarView.Companion.Type.DEFAULT,
                                io.taptalk.TapTalk.R.drawable.tap_ic_rounded_check,
                                io.taptalk.TapTalk.R.string.tap_otp_successfully_sent
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
            vb.layoutLoginOtp.etOtpCode.text.toString(),
            object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                override fun startLoading() {
                    vm?.loadingDialog = TapLoadingDialog.Builder(this@TAPLoginActivity).show()
                    vb.layoutLoginOtp.etOtpCode.isEnabled = false
                }

                override fun endLoading() {
                    vm?.loadingDialog?.dismiss()
                    vm?.loadingDialog = null
                    vb.layoutLoginOtp.etOtpCode.isEnabled = true
                }

                override fun onSuccess(response: TAPLoginOTPVerifyResponse?) {
                    onVerificationSuccess(response)
                }

                override fun onError(error: TAPErrorModel?) {
                    onError(error?.message ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
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
        if (this::redirectTimer.isInitialized) {
            redirectTimer.cancel()
        }
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
        if (this::redirectTimer.isInitialized) {
            redirectTimer.cancel()
        }
        TAPRegisterActivity.start(
            this,
            instanceKey,
            vm?.selectedCountryID ?: 0,
            vm?.countryCallingID ?: "",
            vm?.countryFlagUrl ?: "",
            vm?.phoneNumber ?: ""
        )
        finish()
    }

    private fun onVerificationSuccess(response: TAPLoginOTPVerifyResponse?) {
        if (response?.isRegistered == true && !response.ticket.isNullOrEmpty()) {
            // Login
            authenticateTapTalk(response.ticket)
        }
        else {
            // Register
            showVerificationSuccess()
            vb.layoutLoginVerificationStatus.llButtonContinueToHome.setOnClickListener { continueToRegister() }
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
                    vb.layoutLoginVerificationStatus.llButtonContinueToHome.setOnClickListener { continueToHome() }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    showVerificationError()
                }
            }
        )
    }
}
