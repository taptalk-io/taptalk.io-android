package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel

class TapMessageInfoViewModel : ViewModel() {
    var message : TAPMessageModel? = null
    var readList = ArrayList<TapMessageRecipientModel>()
    var deliveredList = ArrayList<TapMessageRecipientModel>()
}