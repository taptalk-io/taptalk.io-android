package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel

class TapMessageInfoViewModel : ViewModel() {
    var message : TAPMessageModel? = null
    var room :  TAPRoomModel? = null
    var isStarred = false
    var isPinned = false
    var isFirstLoadFinished = false
    var readList = ArrayList<TapMessageRecipientModel>()
    var deliveredList = ArrayList<TapMessageRecipientModel>()
}
