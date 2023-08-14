package io.taptalk.TapTalk.ViewModel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Helper.TapLoadingDialog
import io.taptalk.TapTalk.Model.TAPCountryListItem
import io.taptalk.TapTalk.Model.TapVerificationModel
import java.util.Timer

class TAPLoginViewModel(application: Application) : AndroidViewModel(application) {


    var countryIsoCode = "id"
    var phoneNumber = "0"
    var phoneNumberWithCode = "0"
    var countryCallingID = "62"
    var selectedCountryID = 1
    var isNeedResetData = true
    var countryHashMap = mutableMapOf<String, TAPCountryListItem>()
    var countryListItems = arrayListOf<TAPCountryListItem>()
    var countryFlagUrl = ""
    var isCheckWhatsAppVerificationPending = false
    var verification: TapVerificationModel? = null
    var checkVerificationTimer: Timer? = null
    var checkVerificationAttempts = 0
    var otpTimer: CountDownTimer? = null
    var nextOtpRequestTimestamp = 0L
    var lastRequestOtpPhoneNumber = ""
    var otpID = 0L
    var otpKey: String? = ""
    var loadingDialog: TapLoadingDialog? = null
}
