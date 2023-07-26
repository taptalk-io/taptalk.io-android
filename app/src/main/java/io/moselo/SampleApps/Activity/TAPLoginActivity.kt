package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.moselo.SampleApps.Adapter.TAPCountryListAdapter
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem
import io.taptalk.TapTalk.Model.TAPCountryRecycleItem.RecyclerItemType
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModel
import io.taptalk.TapTalkSample.BuildConfig
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_layout_login_country_list.*
import kotlinx.android.synthetic.main.tap_layout_login_input.*
import kotlinx.android.synthetic.main.tap_layout_login_verification.*

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
    }

    override fun onBackPressed() {
        if (cl_country_list_container.visibility == View.VISIBLE ||
            cl_verification_container.visibility == View.VISIBLE
        ) {
            showPhoneNumberInputView()
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.REGISTER -> {
                    TAPApiManager.getInstance(instanceKey).isLoggedOut = false
                    if (BuildConfig.DEBUG) {
                        TapDevLandingActivity.start(this@TAPLoginActivity, instanceKey)
                    } else {
                        TapUIRoomListActivity.start(this@TAPLoginActivity, instanceKey)
                    }
                    finish()
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
        try {
            countryListAdapter = TAPCountryListAdapter(setupDataForRecycler(""), countryPickInterface)
            rv_country_list?.adapter = countryListAdapter
            rv_country_list?.setHasFixedSize(true)
            rv_country_list?.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        iv_button_close_country_list.setOnClickListener(backButtonClickListener)
        ll_button_change_number.setOnClickListener(backButtonClickListener)
        ll_button_continue.setOnClickListener(submitPhoneNumberClickListener)

        et_search_country_list.addTextChangedListener(searchTextWatcher)
    }

    private val backButtonClickListener = OnClickListener {
        showPhoneNumberInputView()
    }

    /**=============================================================================================
     * Phone Number Input
     =============================================================================================*/

    @SuppressLint("SetTextI18n")
    private fun setCountry(countryID: Int, callingCode: String, flagIconUrl: String?) {
        if (callingCode.isNotEmpty()) {
            tv_country_code.text = "+$callingCode"
            tv_country_code.hint = ""
            et_phone_number.visibility = View.VISIBLE
        }
        else {
            tv_country_code.text = ""
            tv_country_code.hint = getString(R.string.tap_hint_select_country)
            et_phone_number.visibility = View.GONE
        }
        vm?.selectedCountryID = countryID
        vm?.countryCallingID = callingCode
        vm?.countryFlagUrl = flagIconUrl ?: ""

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(iv_country_flag)
        }
        else {
            iv_country_flag.setImageResource(R.drawable.tap_ic_default_flag)
        }
    }

    private fun checkAndEditPhoneNumber(): String {
        var phoneNumber = et_phone_number.text.toString().replace("-", "").trim()
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

    private fun showPhoneNumberInputLoading() {
        iv_button_continue_loading.visibility = View.VISIBLE
        iv_country_chevron.alpha = 0.4f
        tv_country_code.alpha = 0.4f
        et_phone_number.alpha = 0.4f
        cl_input_phone_number.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        ll_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        tv_button_continue.setTextColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1940))
        et_phone_number.isEnabled = false
        ll_country_picker_button.setOnClickListener(null)
        ll_button_continue.setOnClickListener(null)
    }

    private fun hidePhoneNumberInputLoading() {
        iv_button_continue_loading.visibility = View.GONE
        iv_country_chevron.alpha = 1f
        tv_country_code.alpha = 1f
        et_phone_number.alpha = 1f
        cl_input_phone_number.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_light)
        ll_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        tv_button_continue.setTextColor(ContextCompat.getColor(this, R.color.tapButtonLabelColor))
        et_phone_number.isEnabled = true
        ll_country_picker_button.setOnClickListener(countryPickerClickListener)
        ll_button_continue.setOnClickListener(submitPhoneNumberClickListener)
    }

    private val countryPickerClickListener = OnClickListener {
        showCountryListView()
    }

    private val submitPhoneNumberClickListener = OnClickListener {
        // TODO:
        showVerificationView()
    }

    /**=============================================================================================
     * Country List
    ==============================================================================================*/

    private fun initCountryList() {
        val lastCallCountryTimestamp = TAPDataManager.getInstance(instanceKey).lastCallCountryTimestamp
        val oneDayAgoTimestamp: Long = 24 * 60 * 60 * 1000

        if (0L == lastCallCountryTimestamp || System.currentTimeMillis() - oneDayAgoTimestamp >= lastCallCountryTimestamp) {
            Log.e(">>>>", "initCountryList: call API")
            callCountryListFromAPI()
        }
        else if (vm?.isNeedResetData == true) {
            Log.e(">>>>", "initCountryList: reset data")
            callCountryListFromAPI()
            vm?.countryIsoCode = TAPUtils.getDeviceCountryCode(this)
            //vm?.countryHashMap = TAPDataManager.getInstance(instanceKey).countryList
            vm?.countryListItems = TAPDataManager.getInstance(instanceKey).countryList
            vm?.countryHashMap = vm?.countryListItems?.associateBy({ it.iso2Code }, { it })?.toMutableMap() ?: HashMap()
            vm?.isNeedResetData = false

            if (vm?.countryHashMap?.containsKey(vm?.countryIsoCode) == false ||
                "" == vm?.countryHashMap?.get(vm?.countryIsoCode)?.callingCode
            ) {
                setCountry(defaultCountryID, defaultCallingCode, "")
            }
            else {
                setCountry(vm?.countryHashMap?.get(vm?.countryIsoCode)?.countryID ?: 0,
                    vm?.countryHashMap?.get(vm?.countryIsoCode)?.callingCode ?: "",
                    vm?.countryHashMap?.get(vm?.countryIsoCode)?.flagIconUrl ?: "")
            }
        }
        else {
            Log.e(">>>>", "initCountryList: set country")
            setCountry(defaultCountryID, defaultCallingCode, vm?.countryFlagUrl)
            searchCountry("")
            ll_country_picker_button.setOnClickListener(countryPickerClickListener)
        }
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

    private fun callCountryListFromAPI() {
        TAPDataManager.getInstance(instanceKey).getCountryList(object : TAPDefaultDataView<TAPCountryListResponse>() {
            override fun startLoading() {
                et_phone_number?.isEnabled = false
                tv_country_code?.visibility = View.GONE
                iv_loading_progress_country?.let {
                    it.visibility = View.VISIBLE
                    TAPUtils.rotateAnimateInfinitely(this@TAPLoginActivity, it)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: TAPCountryListResponse?) {
                et_phone_number?.isEnabled = true
                vm?.countryListItems?.clear()
                TAPDataManager.getInstance(instanceKey).saveLastCallCountryTimestamp(System.currentTimeMillis())
                setCountry(0, "", "")
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

                    if ("" == tv_country_code.text) {
                        val callingCode: String = defaultCountry?.callingCode ?: ""
                        runOnUiThread {
                            setCountry(defaultCountry?.countryID ?: 0, callingCode, defaultCountry?.flagIconUrl ?: "")
                        }
                    }

                    Log.e(">>>>>", "onSuccess: ${vm?.countryListItems?.size}")

                    TAPDataManager.getInstance(instanceKey).saveCountryList(vm?.countryListItems)

                    runOnUiThread {
                        searchCountry("")
                        tv_country_code.visibility = View.VISIBLE
                        iv_loading_progress_country.visibility = View.GONE
                        iv_loading_progress_country.clearAnimation()
                        ll_country_picker_button.setOnClickListener(countryPickerClickListener)
                    }
                }.start()
            }

            override fun onError(error: TAPErrorModel?) {
                onError(error?.message ?: getString(R.string.tap_error_message_general))
            }

            override fun onError(errorMessage: String?) {
                iv_loading_progress_country.visibility = View.GONE
                iv_loading_progress_country.clearAnimation()
                tv_country_code.visibility = View.VISIBLE
                setCountry(0, "", "")
                // TODO: SHOW ERROR
            }
        })
    }

    private fun searchCountry(countryKeyword: String?) {
        countryListAdapter.items = setupDataForRecycler(countryKeyword ?: "")
//        countryListAdapter.notifyDataSetChanged()
        if (countryListAdapter.items.size == 0) {
            showCountryListEmptyState()
        }
        else {
            hideCountryListEmptyState()
        }
    }

    private fun showCountryListEmptyState() {
        cl_country_list_empty_state.visibility = View.VISIBLE
        rv_country_list.visibility = View.GONE
    }

    private fun hideCountryListEmptyState() {
        cl_country_list_empty_state.visibility = View.GONE
        rv_country_list.visibility = View.VISIBLE
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
            et_phone_number.setText("")
        }
        showPhoneNumberInputView()
    }

    /**=============================================================================================
     * Layout Changes
    ==============================================================================================*/

    private val animationDuration = 250L
    private val phoneInputHiddenTranslation = TAPUtils.dpToPx(16).toFloat()
    private val hiddenTranslation = TAPUtils.dpToPx(960).toFloat()

    private fun showViewWithAnimation(view: View) {
        TAPUtils.dismissKeyboard(this)
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setDuration(animationDuration)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun hideViewWithAnimation(view: View) {
        hideViewWithAnimation(view, hiddenTranslation)
    }

    private fun hideViewWithAnimation(view: View, translationY: Float) {
        if (view.visibility == View.GONE) {
            return
        }
        view.animate()
            .translationY(translationY)
            .setDuration(animationDuration)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.visibility = View.GONE
                if (view == cl_country_list_container) {
                    et_search_country_list.setText("")
                }
            }
            .start()
    }

    private fun showPhoneNumberInputView() {
        showViewWithAnimation(cl_login_input_container)
        hideViewWithAnimation(cl_country_list_container)
        hideViewWithAnimation(cl_verification_container)
    }

    private fun showCountryListView() {
        showViewWithAnimation(cl_country_list_container)
        hideViewWithAnimation(cl_login_input_container, phoneInputHiddenTranslation)
        hideViewWithAnimation(cl_verification_container)
    }

    private fun showVerificationView() {
        showViewWithAnimation(cl_verification_container)
        hideViewWithAnimation(cl_login_input_container, phoneInputHiddenTranslation)
        hideViewWithAnimation(cl_country_list_container)
    }

//    fun showOTPVerification(
//        otpID: Long?,
//        otpKey: String?,
//        phoneNumber: String,
//        phoneNumberWithCode: String?,
//        countryID: Int,
//        countryCallingID: String?,
//        countryFlagUrl: String?,
//        channel: String?,
//        nextRequestSeconds: Int
//    ) {
//        if (BuildConfig.BUILD_TYPE == "dev") {
//            val otpCode = phoneNumber.substring(phoneNumber.length - 6)
//            TAPDataManager.getInstance(instanceKey).verifyOTPLogin(
//                otpID!!,
//                otpKey,
//                otpCode,
//                object : TAPDefaultDataView<TAPLoginOTPVerifyResponse?>() {
//                    override fun onSuccess(response: TAPLoginOTPVerifyResponse) {
//                        if (response.isRegistered) {
//                            TapTalk.authenticateWithAuthTicket(
//                                instanceKey,
//                                response.ticket,
//                                true,
//                                object : TapCommonListener() {
//                                    override fun onSuccess(successMessage: String) {
//                                        TapDevLandingActivity.start(
//                                            this@TAPLoginActivity,
//                                            instanceKey
//                                        )
//                                    }
//
//                                    override fun onError(errorCode: String, errorMessage: String) {
//                                        TapTalkDialog.Builder(this@TAPLoginActivity)
//                                            .setTitle("Error Verifying OTP")
//                                            .setMessage(errorMessage)
//                                            .setPrimaryButtonTitle("OK")
//                                            .show()
//                                    }
//                                })
//                        } else {
//                            start(
//                                this@TAPLoginActivity,
//                                instanceKey,
//                                countryID,
//                                countryCallingID!!,
//                                countryFlagUrl!!,
//                                phoneNumber
//                            )
//                            vm!!.phoneNumber = "0"
//                            vm!!.countryID = 0
//                        }
//                    }
//
//                    override fun onError(error: TAPErrorModel) {
//                        onError(error.message)
//                    }
//
//                    override fun onError(errorMessage: String) {
//                        TapTalkDialog.Builder(this@TAPLoginActivity)
//                            .setTitle("Error Verifying OTP")
//                            .setMessage(errorMessage)
//                            .setPrimaryButtonTitle("OK")
//                            .show()
//                    }
//                })
//        } else {
//            supportFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.animator.tap_slide_left_fragment,
//                    R.animator.tap_fade_out_fragment,
//                    R.animator.tap_fade_in_fragment,
//                    R.animator.tap_slide_right_fragment
//                )
//                .replace(
//                    R.id.fl_container,
//                    getInstance(
//                        otpID!!,
//                        otpKey!!,
//                        phoneNumber,
//                        phoneNumberWithCode!!,
//                        countryID,
//                        countryCallingID!!,
//                        countryFlagUrl!!,
//                        channel!!,
//                        nextRequestSeconds
//                    )
//                )
//                .addToBackStack(null)
//                .commit()
//        }
//    }

//    fun setLastLoginData(
//        otpID: Long?,
//        otpKey: String?,
//        phoneNumber: String?,
//        phoneNumberWithCode: String?,
//        countryID: Int,
//        countryCallingID: String?,
//        channel: String?
//    ) {
//        vm!!.setLastLoginData(
//            otpID,
//            otpKey,
//            phoneNumber,
//            phoneNumberWithCode,
//            countryID,
//            countryCallingID,
//            channel
//        )
//    }
}
