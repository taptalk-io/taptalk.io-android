package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TapMessageInfoModel

class TapMessageInfoViewModel : ViewModel() {
    var message : TAPMessageModel? = null
    var readList = ArrayList<TapMessageInfoModel>()
    var deliveredList = ArrayList<TapMessageInfoModel>()
}