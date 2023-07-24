package io.moselo.SampleApps.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
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
import io.taptalk.TapTalk.Model.ResponseModel.TAPLoginOTPVerifyResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.ViewModel.TAPLoginViewModel
import io.taptalk.TapTalkSample.BuildConfig
import io.taptalk.TapTalkSample.R

class TAPLoginActivity : TAPBaseActivity() {
    private var flContainer: FrameLayout? = null
    private var vm: TAPLoginViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_login)
        initViewModel()
        initView()
        initFirstPage()
        TAPUtils.checkAndRequestNotificationPermission(this)
    }

    override fun onDestroy() {
        super.onDestroy()
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
        flContainer = findViewById(R.id.fl_container)
    }

    fun initFirstPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container, getInstance())
            .commit()
    }

    fun showPhoneLogin() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.tap_slide_left_fragment,
                R.animator.tap_fade_out_fragment,
                R.animator.tap_fade_in_fragment,
                R.animator.tap_slide_right_fragment
            )
            .replace(R.id.fl_container, getInstance())
            .addToBackStack(null)
            .commit()
    }

    fun showOTPVerification(
        otpID: Long?,
        otpKey: String?,
        phoneNumber: String,
        phoneNumberWithCode: String?,
        countryID: Int,
        countryCallingID: String?,
        countryFlagUrl: String?,
        channel: String?,
        nextRequestSeconds: Int
    ) {
        if (BuildConfig.BUILD_TYPE == "dev") {
            val otpCode = phoneNumber.substring(phoneNumber.length - 6)
            TAPDataManager.getInstance(instanceKey).verifyOTPLogin(
                otpID!!,
                otpKey,
                otpCode,
                object : TAPDefaultDataView<TAPLoginOTPVerifyResponse?>() {
                    override fun onSuccess(response: TAPLoginOTPVerifyResponse) {
                        if (response.isRegistered) {
                            TapTalk.authenticateWithAuthTicket(
                                instanceKey,
                                response.ticket,
                                true,
                                object : TapCommonListener() {
                                    override fun onSuccess(successMessage: String) {
                                        TapDevLandingActivity.start(
                                            this@TAPLoginActivity,
                                            instanceKey
                                        )
                                    }

                                    override fun onError(errorCode: String, errorMessage: String) {
                                        TapTalkDialog.Builder(this@TAPLoginActivity)
                                            .setTitle("Error Verifying OTP")
                                            .setMessage(errorMessage)
                                            .setPrimaryButtonTitle("OK")
                                            .show()
                                    }
                                })
                        } else {
                            start(
                                this@TAPLoginActivity,
                                instanceKey,
                                countryID,
                                countryCallingID!!,
                                countryFlagUrl!!,
                                phoneNumber
                            )
                            vm!!.phoneNumber = "0"
                            vm!!.countryID = 0
                        }
                    }

                    override fun onError(error: TAPErrorModel) {
                        onError(error.message)
                    }

                    override fun onError(errorMessage: String) {
                        TapTalkDialog.Builder(this@TAPLoginActivity)
                            .setTitle("Error Verifying OTP")
                            .setMessage(errorMessage)
                            .setPrimaryButtonTitle("OK")
                            .show()
                    }
                })
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.tap_slide_left_fragment,
                    R.animator.tap_fade_out_fragment,
                    R.animator.tap_fade_in_fragment,
                    R.animator.tap_slide_right_fragment
                )
                .replace(
                    R.id.fl_container,
                    getInstance(
                        otpID!!,
                        otpKey!!,
                        phoneNumber,
                        phoneNumberWithCode!!,
                        countryID,
                        countryCallingID!!,
                        countryFlagUrl!!,
                        channel!!,
                        nextRequestSeconds
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }

    fun setLastLoginData(
        otpID: Long?,
        otpKey: String?,
        phoneNumber: String?,
        phoneNumberWithCode: String?,
        countryID: Int,
        countryCallingID: String?,
        channel: String?
    ) {
        vm!!.setLastLoginData(
            otpID,
            otpKey,
            phoneNumber,
            phoneNumberWithCode,
            countryID,
            countryCallingID,
            channel
        )
    }

    private fun initViewModel() {
        vm = ViewModelProvider(this).get(TAPLoginViewModel::class.java)
    }

    fun getVm(): TAPLoginViewModel {
        return if (null == vm) ViewModelProvider(this).get(TAPLoginViewModel::class.java)
            .also { vm = it } else vm!!
    }

    companion object {
        private val TAG = TAPLoginActivity::class.java.simpleName
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
}