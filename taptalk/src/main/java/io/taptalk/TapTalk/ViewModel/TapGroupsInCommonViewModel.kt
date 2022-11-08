package io.taptalk.TapTalk.ViewModel

import androidx.lifecycle.ViewModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPSearchChatModel

class TapGroupsInCommonViewModel : ViewModel() {
    var groups = ArrayList<TAPSearchChatModel>()
    var room : TAPRoomModel? = null
}