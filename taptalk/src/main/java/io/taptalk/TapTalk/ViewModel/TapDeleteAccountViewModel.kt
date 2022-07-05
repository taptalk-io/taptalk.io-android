package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel

class TapDeleteAccountViewModel: ViewModel() {

    var otpID = 0L
    var lastLoginTimestamp = 0L
    var otpKey = ""
    var phoneNumber = "0"
    var channel: String? = "sms"
    var countryID = 0
    var waitTimeRequestOtp = 0
    var isDeleted = false
    var isLoading = false

}