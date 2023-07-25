package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.moselo.SampleApps.Activity.TAPRegisterActivity.Companion.start
import io.moselo.SampleApps.Fragment.TAPLoginVerificationFragment.Companion.getInstance
import io.moselo.SampleApps.Fragment.TAPPhoneLoginFragment.Companion.getInstance
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCountryListResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModel
import io.taptalk.TapTalkSample.BuildConfig
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_layout_login_country_list.*
import kotlinx.android.synthetic.main.tap_layout_login_input.*
import java.util.Locale

class TAPLoginActivity : TAPBaseActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_login)
        initViewModel()
        initView()
        initCountryList()
        TAPUtils.checkAndRequestNotificationPermission(this)
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

    private fun initView() {

    }

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
            vm?.countryListitems = TAPDataManager.getInstance(instanceKey).countryList
            vm?.countryHashMap = vm?.countryListitems?.associateBy({ it.iso2Code }, { it })?.toMutableMap() ?: HashMap()
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
            ll_country_picker_button.setOnClickListener {
                showCountryList()
            }
        }
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
                vm?.countryListitems?.clear()
                TAPDataManager.getInstance(instanceKey).saveLastCallCountryTimestamp(System.currentTimeMillis())
                setCountry(0, "", "")
                Thread {
                    var defaultCountry: TAPCountryListItem? = null
                    response?.countries?.forEach {
                        vm?.countryListitems?.add(it)
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

                    TAPDataManager.getInstance(instanceKey).saveCountryList(vm?.countryListitems)

                    runOnUiThread {
                        tv_country_code.visibility = View.VISIBLE
                        iv_loading_progress_country.visibility = View.GONE
                        iv_loading_progress_country.clearAnimation()
                        ll_country_picker_button.setOnClickListener {
                            showCountryList()
                        }
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
        vm?.countryID = countryID
        vm?.countryCallingID = callingCode
        vm?.countryFlagUrl = flagIconUrl ?: ""

        if ("" != flagIconUrl) {
            Glide.with(this).load(flagIconUrl).into(iv_country_flag)
        }
        else {
            iv_country_flag.setImageResource(R.drawable.tap_ic_default_flag)
        }
    }

    private fun showCountryList() {
        cl_login_input_container.animate()
            .translationY(TAPUtils.dpToPx(resources, 16f).toFloat())
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                cl_login_input_container.visibility = View.GONE
            }
            .start()
        cl_country_list_container.visibility = View.VISIBLE
        cl_country_list_container.animate()
            .translationY(0f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
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

    private fun initViewModel() {
        vm = ViewModelProvider(this).get(TAPLoginViewModel::class.java)
    }
}
