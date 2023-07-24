package io.taptalk.TapTalk.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Model.TAPCountryListItem

class TAPLoginViewModel(application: Application) : AndroidViewModel(application) {


    var countryIsoCode = "id"
    var phoneNumber = "0"
    var phoneNumberWithCode = "0"
    var countryCallingID = "62"
    var countryID = 1
    var lastLoginTimestamp = 0L
    var isNeedResetData = true
    var countryHashMap = mutableMapOf<String, TAPCountryListItem>()
    var countryListitems = arrayListOf<TAPCountryListItem>()
    var countryFlagUrl = ""
    var previousPhoneNumber = "0"
}
