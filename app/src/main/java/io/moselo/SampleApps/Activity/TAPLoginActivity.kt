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

    private lateinit var binding: TapActivityLoginBinding
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
        binding = TapActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        if (binding.layoutLoginCountryList.clCountryListContainer.visibility == View.VISIBLE ||
            binding.layoutLoginWhatsappVerification.svWhatsappVerification.visibility == View.VISIBLE ||
            binding.layoutLoginOtp.svOtpVerification.visibility == View.VISIBLE
        ) {
            if (binding.layoutLoginWhatsappVerification.ivQrCode.visibility == View.VISIBLE) {
                hideQR()
                return
            }
            showPhoneNumberInputView()
            return
        }
        if (binding.layoutLoginVerificationStatus.llButtonRetryVerification.visibility == View.VISIBLE) {
            showVerificationView()
            return
        }
        if (binding.layoutLoginVerificationStatus.ivVerificationStatusLoading?.visibility == View.VISIBLE) {
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
        binding.layoutLoginInput.etPhoneNumber.filters = binding.layoutLoginInput.etPhoneNumber.filters + numberFilter
        binding.layoutLoginInput.etPhoneNumber.addTextChangedListener(phoneTextWatcher)

        try {
            countryListAdapter = TAPCountryListAdapter(setupDataForRecycler(""), countryPickInterface)
            binding.layoutLoginCountryList.rvCountryList.adapter = countryListAdapter
            binding.layoutLoginCountryList.rvCountryList.setHasFixedSize(true)
            binding.layoutLoginCountryList.rvCountryList.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        binding.layoutLoginCountryList.ivButtonCloseCountryList.setOnClickListener(backButtonClickListener)
        binding.layoutLoginWhatsappVerification.llButtonChangeNumber.setOnClickListener(backButtonClickListener)
        binding.layoutLoginOtp.llButtonChangeNumberOtp.setOnClickListener(backButtonClickListener)
        binding.layoutLoginInput.llButtonWhatsapp.setOnClickListener(loginViaWhatsAppClickListener)
        binding.layoutLoginInput.llButtonOtp.setOnClickListener(loginViaOTPClickListener)
        binding.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(openWhatsAppClickListener)
        binding.layoutLoginWhatsappVerification.llButtonShowQrCode.setOnClickListener { showQR() }
        binding.layoutLoginOtp.llRequestOtpAgain.setOnClickListener { requestOtp(true) }
        binding.layoutLoginVerificationStatus.llButtonRetryVerification.setOnClickListener { showVerificationView() }
        binding.clLoginContainer.setOnClickListener { TAPUtils.dismissKeyboard(this) }
        binding.layoutLoginInput.clLoginInputContainer.setOnClickListener { TAPUtils.dismissKeyboard(this, binding.layoutLoginInput.etPhoneNumber) }
        binding.layoutLoginOtp.clOtpContainer.setOnClickListener { TAPUtils.dismissKeyboard(this, binding.layoutLoginOtp.etOtpCode) }

        binding.layoutLoginCountryList.etSearchCountryList.addTextChangedListener(searchTextWatcher)
        binding.layoutLoginOtp.etOtpCode.addTextChangedListener(otpTextWatcher)
        binding.layoutLoginOtp.etOtpCode.setOnEditorActionListener(otpEditorListener)

        showPhoneNumberInputView()

        if (BuildConfig.BUILD_TYPE == "dev") {
            binding.layoutLoginInput.llButtonOtp.setOnLongClickListener(devPhoneNumberLongClickListener)
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
            binding.layoutLoginInput.tvCountryCode.text = "+$callingCode"
            binding.layoutLoginInput.tvCountryCode.hint = ""
            binding.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
            binding.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
        }
        else {
            binding.layoutLoginInput.tvCountryCode.text = ""
            binding.layoutLoginInput.tvCountryCode.hint = getString(io.taptalk.TapTalk.R.string.tap_hint_select_country)
            binding.layoutLoginInput.etPhoneNumber.visibility = View.GONE
            binding.layoutLoginInput.tvPhoneNumber.visibility = View.GONE
        }
        vm?.selectedCountryID = countryID
        vm?.countryCallingID = callingCode
        vm?.countryFlagUrl = flagIconUrl ?: ""

        // Re-add max length filter
        val filters = binding.layoutLoginInput.etPhoneNumber.filters.toList().filter { it.javaClass != InputFilter.LengthFilter::class.java }
        binding.layoutLoginInput.etPhoneNumber.filters = filters.toTypedArray() + InputFilter.LengthFilter(15 - callingCode.length)

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(binding.layoutLoginInput.ivCountryFlag)
        }
        else {
            binding.layoutLoginInput.ivCountryFlag.setImageResource(io.taptalk.TapTalk.R.drawable.tap_ic_default_flag)
        }
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = binding.layoutLoginInput.etPhoneNumber.text.toString().replace("-", "").trim()
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
                binding.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.GONE
                binding.layoutLoginInput.pbButtonOtpLoading.visibility = View.VISIBLE
                binding.layoutLoginInput.ivButtonWhatsapp.visibility = View.VISIBLE
                binding.layoutLoginInput.ivButtonOtp.visibility = View.GONE
            }
            else {
                binding.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.VISIBLE
                binding.layoutLoginInput.pbButtonOtpLoading.visibility = View.GONE
                binding.layoutLoginInput.ivButtonWhatsapp.visibility = View.GONE
                binding.layoutLoginInput.ivButtonOtp.visibility = View.VISIBLE
            }
            binding.layoutLoginInput.ivCountryChevron.alpha = 0.4f
            binding.layoutLoginInput.tvCountryCode.alpha = 0.4f
            binding.layoutLoginInput.etPhoneNumber.alpha = 0.4f
            binding.layoutLoginInput.tvPhoneNumber.alpha = 0.4f
            binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
            binding.layoutLoginInput.llButtonWhatsapp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
            binding.layoutLoginInput.llButtonOtp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_border_inactive)
            ImageViewCompat.setImageTintList(binding.layoutLoginInput.ivButtonOtp, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1920)))
            binding.layoutLoginInput.tvButtonWhatsapp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
            binding.layoutLoginInput.tvButtonOtp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
            binding.layoutLoginInput.etPhoneNumber.isEnabled = false
            binding.layoutLoginInput.llCountryPickerButton.setOnClickListener(null)
            binding.layoutLoginInput.llButtonWhatsapp.setOnClickListener(null)
            binding.layoutLoginInput.llButtonOtp.setOnClickListener(null)
        }
    }

    private fun hidePhoneNumberInputLoading() {
        runOnUiThread {
            binding.layoutLoginInput.pbButtonWhatsappLoading.visibility = View.GONE
            binding.layoutLoginInput.pbButtonOtpLoading.visibility = View.GONE
            binding.layoutLoginInput.ivButtonWhatsapp.visibility = View.VISIBLE
            binding.layoutLoginInput.ivButtonOtp.visibility = View.VISIBLE
            binding.layoutLoginInput.ivCountryChevron.alpha = 1f
            binding.layoutLoginInput.tvCountryCode.alpha = 1f
            binding.layoutLoginInput.etPhoneNumber.alpha = 1f
            binding.layoutLoginInput.tvPhoneNumber.alpha = 1f
            binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
            binding.layoutLoginInput.llButtonWhatsapp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_active_ripple)
            binding.layoutLoginInput.llButtonOtp.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_border_ripple)
            ImageViewCompat.setImageTintList(binding.layoutLoginInput.ivButtonOtp, null)
            binding.layoutLoginInput.tvButtonWhatsapp.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapButtonLabelColor))
            binding.layoutLoginInput.tvButtonOtp.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
            binding.layoutLoginInput.etPhoneNumber.isEnabled = true
            binding.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)
            binding.layoutLoginInput.llButtonWhatsapp.setOnClickListener(loginViaWhatsAppClickListener)
            binding.layoutLoginInput.llButtonOtp.setOnClickListener(loginViaOTPClickListener)
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
            binding.layoutLoginInput.tvPhoneNumber.text = TAPUtils.beautifyPhoneNumber(s.toString(), false)
            if (binding.layoutLoginInput.llInputErrorInfo.visibility == View.VISIBLE) {
                binding.layoutLoginInput.etPhoneNumber.removeTextChangedListener(this)
                validatePhoneNumber()
                binding.layoutLoginInput.etPhoneNumber.addTextChangedListener(this)
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
                    binding.layoutLoginInput.etPhoneNumber.visibility = View.GONE
                    binding.layoutLoginInput.tvPhoneNumber.visibility = View.GONE
                    binding.layoutLoginInput.cvCountryFlag.visibility = View.GONE
                    binding.layoutLoginInput.pbLoadingProgressCountry.visibility = View.VISIBLE
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

                    if ("" == binding.layoutLoginInput.tvCountryCode.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        runOnUiThread {
                            setCountry(defaultCountry?.countryID ?: 0, callingCode, defaultCountry?.flagIconUrl ?: "")
                        }
                    }

                    TAPDataManager.getInstance(instanceKey).saveCountryList(vm?.countryListItems)

                    runOnUiThread {
                        searchCountry("")
                        binding.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
                        binding.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
                        binding.layoutLoginInput.cvCountryFlag.visibility = View.VISIBLE
                        binding.layoutLoginInput.pbLoadingProgressCountry.visibility = View.GONE
                        binding.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)

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
                    binding.layoutLoginInput.etPhoneNumber.visibility = View.VISIBLE
                    binding.layoutLoginInput.tvPhoneNumber.visibility = View.VISIBLE
                    binding.layoutLoginInput.cvCountryFlag.visibility = View.VISIBLE
                    binding.layoutLoginInput.pbLoadingProgressCountry.visibility = View.GONE
                    setCountry(0, "", "")
                    binding.layoutLoginInput.llCountryPickerButton.setOnClickListener(countryPickerClickListener)
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
            binding.layoutLoginCountryList.clCountryListEmptyState.visibility = View.VISIBLE
            binding.layoutLoginCountryList.rvCountryList.visibility = View.GONE
        }
    }

    private fun hideCountryListEmptyState() {
        runOnUiThread {
            binding.layoutLoginCountryList.clCountryListEmptyState.visibility = View.GONE
            binding.layoutLoginCountryList.rvCountryList.visibility = View.VISIBLE
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
            binding.layoutLoginInput.etPhoneNumber.setText("")
        }
        showPhoneNumberInputView()

        binding.layoutLoginInput.llInputErrorInfo.visibility = View.GONE
        binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
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
                if (view != binding.layoutLoginVerificationStatus.clVerificationStatusContainer) {
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
                if (view == binding.layoutLoginCountryList.clCountryListContainer) {
                    binding.layoutLoginCountryList.etSearchCountryList.setText("")
                }
            }
            ?.start()
    }

    private fun showPhoneNumberInputView() {
        runOnUiThread {
            showViewWithAnimation(binding.layoutLoginInput.svLoginPhoneNumberInput)
            hideViewWithAnimation(binding.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(binding.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(binding.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(binding.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showCountryListView() {
        runOnUiThread {
            showViewWithAnimation(binding.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(binding.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(binding.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(binding.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(binding.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showVerificationView() {
        runOnUiThread {
            showViewWithAnimation(binding.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(binding.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(binding.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(binding.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(binding.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showOtpView() {
        runOnUiThread {
            showViewWithAnimation(binding.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(binding.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(binding.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(binding.layoutLoginWhatsappVerification.svWhatsappVerification)
            hideViewWithAnimation(binding.layoutLoginVerificationStatus.clVerificationStatusContainer)
        }
    }

    private fun showVerificationStatusView() {
        runOnUiThread {
            showViewWithAnimation(binding.layoutLoginVerificationStatus.clVerificationStatusContainer)
            hideViewWithAnimation(binding.layoutLoginInput.svLoginPhoneNumberInput, phoneInputHiddenTranslation)
            hideViewWithAnimation(binding.layoutLoginCountryList.clCountryListContainer)
            hideViewWithAnimation(binding.layoutLoginOtp.svOtpVerification)
            hideViewWithAnimation(binding.layoutLoginWhatsappVerification.svWhatsappVerification)
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
                    binding.layoutLoginWhatsappVerification.ivQrCode.setImageDrawable(qrCode)
                    binding.layoutLoginWhatsappVerification.ivQrCode.visibility = View.VISIBLE
                    binding.layoutLoginWhatsappVerification.llButtonShowQrCode.visibility = View.GONE
                    binding.layoutLoginWhatsappVerification.tvButtonVerify.text = getString(R.string.tap_i_have_sent_the_message)
                    binding.layoutLoginWhatsappVerification.tvVerificationDescription.text = getString(R.string.tap_whatsapp_verification_qr_description)
                    binding.layoutLoginWhatsappVerification.tvButtonChangeNumber.text = getString(io.taptalk.TapTalk.R.string.tap_back)
                    binding.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(checkVerificationClickListener)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun hideQR() {
        runOnUiThread {
            binding.layoutLoginWhatsappVerification.ivQrCode.setImageDrawable(null)
            binding.layoutLoginWhatsappVerification.ivQrCode.visibility = View.GONE
            binding.layoutLoginWhatsappVerification.llButtonShowQrCode.visibility = View.VISIBLE
            binding.layoutLoginWhatsappVerification.tvButtonVerify.text = getString(R.string.tap_open_whatsapp)
            binding.layoutLoginWhatsappVerification.tvVerificationDescription.text = getString(R.string.tap_whatsapp_verification_description)
            binding.layoutLoginWhatsappVerification.tvButtonChangeNumber.text = getString(R.string.tap_change_phone_number)
            binding.layoutLoginWhatsappVerification.llButtonVerify.setOnClickListener(openWhatsAppClickListener)
        }
    }

    private fun showVerificationLoading() {
        runOnUiThread {
            if (binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.animation == null) {
                TAPUtils.rotateAnimateInfinitely(this, binding.layoutLoginVerificationStatus.ivVerificationStatusLoading)
            }
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.GONE
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.GONE
            binding.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            binding.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(io.taptalk.TapTalk.R.string.tap_loading_dots)
            binding.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_loading_description)
            showVerificationStatusView()
        }
    }

    private fun showVerificationSuccess() {
        runOnUiThread {
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            binding.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(R.string.tap_verification_success)
            binding.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_success_description)
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_success)
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_rounded_check_green))
            showVerificationStatusView()
            startRedirectTimer()
        }
    }

    private fun showVerificationError() {
        runOnUiThread {
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.VISIBLE
            binding.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = getString(R.string.tap_verification_error)
            binding.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = getString(R.string.tap_verification_error_description)
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_verification_error)
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_cancel_white))
            showVerificationStatusView()
        }
    }

    private fun resetVerificationStatus() {
        runOnUiThread {
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.clearAnimation()
            binding.layoutLoginVerificationStatus.ivVerificationStatusLoading.visibility = View.GONE
            binding.layoutLoginVerificationStatus.ivVerificationStatusImage.visibility = View.GONE
            binding.layoutLoginVerificationStatus.vVerificationStatusBackground.visibility = View.GONE
            binding.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonContinueToHome.visibility = View.GONE
            binding.layoutLoginVerificationStatus.llButtonRetryVerification.visibility = View.GONE
            binding.layoutLoginVerificationStatus.tvVerificationStatusTitle.text = ""
            binding.layoutLoginVerificationStatus.tvVerificationStatusDescription.text = ""
        }
    }

    private fun startRedirectTimer() {
        if (this::redirectTimer.isInitialized) {
            redirectTimer.cancel()
        }
        redirectTimer = object : CountDownTimer(3000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                binding.layoutLoginVerificationStatus.tvVerificationStatusRedirectTimer.text = String.format(getString(R.string.tap_format_redirect_seconds), (millisUntilFinished / 1000L).toInt() + 1)
            }

            override fun onFinish() {
                binding.layoutLoginVerificationStatus.llButtonContinueToHome.callOnClick()
            }
        }
        redirectTimer.start()
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = checkAndEditPhoneNumber()
        val phoneNumberWithCode = String.format("%s%s", vm?.countryCallingID ?: "", phoneNumber)
        return if (binding.layoutLoginInput.etPhoneNumber.text.isEmpty()) {
            binding.layoutLoginInput.tvInputErrorInfo.text = getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required)
            binding.layoutLoginInput.llInputErrorInfo.visibility = View.VISIBLE
            binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
            false
        }
        else if (!Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumberWithCode.length !in 7..15) {
            binding.layoutLoginInput.tvInputErrorInfo.text = getString(io.taptalk.TapTalk.R.string.tap_error_invalid_phone_number)
            binding.layoutLoginInput.llInputErrorInfo.visibility = View.VISIBLE
            binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
            false
        }
        else {
            binding.layoutLoginInput.llInputErrorInfo.visibility = View.GONE
            vm?.phoneNumber = phoneNumber
            binding.layoutLoginInput.clInputPhoneNumber.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
            true
        }
    }

    private fun showErrorSnackbar(errorMessage: String?) {
        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            binding.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
            )
        }
        else {
            binding.tapCustomSnackbar.show(
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
                        binding.layoutLoginWhatsappVerification.tvVerificationPhoneNumber.text = TAPUtils.beautifyPhoneNumber(String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber), true)
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
                binding.layoutLoginOtp.etOtpCode.requestFocus()
                TAPUtils.showKeyboard(this, binding.layoutLoginOtp.etOtpCode)
            }, animationDuration)
        }
    }

    private fun clearOtpEditText() {
        runOnUiThread {
            binding.layoutLoginOtp.vPointer1.visibility = View.VISIBLE
            binding.layoutLoginOtp.vPointer2.visibility = View.VISIBLE
            binding.layoutLoginOtp.vPointer3.visibility = View.VISIBLE
            binding.layoutLoginOtp.vPointer4.visibility = View.VISIBLE
            binding.layoutLoginOtp.vPointer5.visibility = View.VISIBLE
            binding.layoutLoginOtp.vPointer6.visibility = View.VISIBLE
            binding.layoutLoginOtp.tvOtpFilled1.text = ""
            binding.layoutLoginOtp.tvOtpFilled2.text = ""
            binding.layoutLoginOtp.tvOtpFilled3.text = ""
            binding.layoutLoginOtp.tvOtpFilled4.text = ""
            binding.layoutLoginOtp.tvOtpFilled5.text = ""
            binding.layoutLoginOtp.tvOtpFilled6.text = ""
            binding.layoutLoginOtp.etOtpCode.setText("")
        }
    }

    private fun showVerifyOtpFailed() {
        runOnUiThread {
            binding.layoutLoginOtp.etOtpCode.setText("")
            binding.layoutLoginOtp.tvDidNotReceiveOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_error_invalid_otp)
            binding.layoutLoginOtp.tvDidNotReceiveOtp.setTextColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))
            binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorError))

            binding.layoutLoginOtp.etOtpCode.requestFocus()
            TAPUtils.showKeyboard(this, binding.layoutLoginOtp.etOtpCode)
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
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    binding.layoutLoginOtp.tvOtpFilled1.text = String.format("%s", s[0])
                    binding.layoutLoginOtp.tvOtpFilled2.text = ""
                    binding.layoutLoginOtp.tvOtpFilled3.text = ""
                    binding.layoutLoginOtp.tvOtpFilled4.text = ""
                    binding.layoutLoginOtp.tvOtpFilled5.text = ""
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                2 -> {
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    binding.layoutLoginOtp.tvOtpFilled2.text = String.format("%s", s[1])
                    binding.layoutLoginOtp.tvOtpFilled3.text = ""
                    binding.layoutLoginOtp.tvOtpFilled4.text = ""
                    binding.layoutLoginOtp.tvOtpFilled5.text = ""
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                3 -> {
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    binding.layoutLoginOtp.tvOtpFilled3.text = String.format("%s", s[2])
                    binding.layoutLoginOtp.tvOtpFilled4.text = ""
                    binding.layoutLoginOtp.tvOtpFilled5.text = ""
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                4 -> {
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    binding.layoutLoginOtp.tvOtpFilled4.text = String.format("%s", s[3])
                    binding.layoutLoginOtp.tvOtpFilled5.text = ""
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                5 -> {
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))

                    binding.layoutLoginOtp.tvOtpFilled5.text = String.format("%s", s[4])
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
                }
                6 -> {
                    binding.layoutLoginOtp.tvOtpFilled6.text = String.format("%s", s[5])
                    verifyOtp()
                }
                else -> {
                    binding.layoutLoginOtp.vPointer1.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimary))
                    binding.layoutLoginOtp.vPointer2.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer3.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer4.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer5.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))
                    binding.layoutLoginOtp.vPointer6.setBackgroundColor(ContextCompat.getColor(TapTalk.appContext, io.taptalk.TapTalk.R.color.tapTransparentBlack1940))

                    binding.layoutLoginOtp.tvOtpFilled1.text = ""
                    binding.layoutLoginOtp.tvOtpFilled2.text = ""
                    binding.layoutLoginOtp.tvOtpFilled3.text = ""
                    binding.layoutLoginOtp.tvOtpFilled4.text = ""
                    binding.layoutLoginOtp.tvOtpFilled5.text = ""
                    binding.layoutLoginOtp.tvOtpFilled6.text = ""
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
            binding.layoutLoginOtp.tvOtpTimer.visibility = View.VISIBLE
            binding.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
            binding.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
            binding.layoutLoginOtp.llOtpSent.visibility = View.GONE
        }
    }

    private fun showRequestOtpAgain() {
        runOnUiThread {
            binding.layoutLoginOtp.llRequestOtpAgain.visibility = View.VISIBLE
            binding.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
            binding.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
            binding.layoutLoginOtp.llOtpSent.visibility = View.GONE
        }
    }

    private fun showResendOtpLoading() {
        runOnUiThread {
            binding.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
            binding.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
            binding.layoutLoginOtp.llLoadingOtp.visibility = View.VISIBLE
            binding.layoutLoginOtp.llOtpSent.visibility = View.GONE
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
        binding.layoutLoginOtp.tvDidNotReceiveOtp.text = resources.getText(io.taptalk.TapTalk.R.string.tap_didnt_receive_the_6_digit_otp)
        binding.layoutLoginOtp.tvDidNotReceiveOtp.setTextColor(ContextCompat.getColor(this, R.color.tapColorTextDark))

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
                                binding.layoutLoginOtp.tvOtpTimer.text = "Wait 0:0$secondLeft"
                            }
                            else {
                                binding.layoutLoginOtp.tvOtpTimer.text = "Wait 0:$secondLeft"
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
                                binding.layoutLoginOtp.tvOtpTimer.text = "Wait $minuteLeft:0$secondLeft"
                            }
                            else {
                                binding.layoutLoginOtp.tvOtpTimer.text = "Wait $minuteLeft:$secondLeft"
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
                    binding.layoutLoginOtp.etOtpCode.isEnabled = false
                    showPhoneNumberInputLoading(true)
                    showResendOtpLoading()
                }

                override fun endLoading() {
                    binding.layoutLoginOtp.etOtpCode.isEnabled = true
                    hidePhoneNumberInputLoading()
                }

                override fun onSuccess(response: TAPOTPResponse?) {
                    vm?.verification = response?.verification
                    vm?.otpID = response?.otpID ?: 0L
                    vm?.otpKey = response?.otpKey
                    if (response?.isSuccess == true) {
                        if (response.channel == "whatsapp") {
                            binding.layoutLoginOtp.tvOtpDescription.text = getString(R.string.tap_otp_verification_whatsapp_description)
                            binding.layoutLoginOtp.ivOtpIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, io.taptalk.TapTalk.R.drawable.tap_ic_whatsapp))
                        }
                        else {
                            binding.layoutLoginOtp.tvOtpDescription.text = getString(R.string.tap_otp_verification_sms_description)
                            binding.layoutLoginOtp.ivOtpIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPLoginActivity, R.drawable.tap_ic_sms_circle))
                        }
                        binding.layoutLoginOtp.tvOtpPhoneNumber.text = TAPUtils.beautifyPhoneNumber(String.format("+%s %s", vm?.countryCallingID, vm?.phoneNumber), true)
                        vm?.nextOtpRequestTimestamp = (response.nextRequestSeconds * 1000).toLong() + System.currentTimeMillis()
                        vm?.lastRequestOtpPhoneNumber = vm?.phoneNumber ?: ""
                        showOtpView()
                        setupOtpView()
                        Handler(Looper.getMainLooper()).postDelayed({ showOtpTimer() }, 2000)

                        if (isResend) {
                            clearOtpEditText()
                            binding.layoutLoginOtp.llRequestOtpAgain.visibility = View.GONE
                            binding.layoutLoginOtp.llLoadingOtp.visibility = View.GONE
                            binding.layoutLoginOtp.tvOtpTimer.visibility = View.GONE
                            binding.layoutLoginOtp.llOtpSent.visibility = View.VISIBLE
                            binding.tapCustomSnackbar.show(
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
            binding.layoutLoginOtp.etOtpCode.text.toString(),
            object : TAPDefaultDataView<TAPLoginOTPVerifyResponse>() {
                override fun startLoading() {
                    vm?.loadingDialog = TapLoadingDialog.Builder(this@TAPLoginActivity).show()
                    binding.layoutLoginOtp.etOtpCode.isEnabled = false
                }

                override fun endLoading() {
                    vm?.loadingDialog?.dismiss()
                    vm?.loadingDialog = null
                    binding.layoutLoginOtp.etOtpCode.isEnabled = true
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
            binding.layoutLoginVerificationStatus.llButtonContinueToHome.setOnClickListener { continueToRegister() }
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
                    binding.layoutLoginVerificationStatus.llButtonContinueToHome.setOnClickListener { continueToHome() }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    showVerificationError()
                }
            }
        )
    }
}
